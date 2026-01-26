package com.example.jetpackcompass

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.jetpackcompass.data.location.LocationDataSource
import com.example.jetpackcompass.data.location.LocationSensorManager
import com.example.jetpackcompass.data.sensor.CompassSensorManager
import com.example.jetpackcompass.domain.enums.CompassDesign
import com.example.jetpackcompass.domain.usecase.CalculateQiblaBearingUseCase
import com.example.jetpackcompass.ui.compass.CompassUiState
import com.example.jetpackcompass.ui.compass.CompassViewModel
import com.example.jetpackcompass.ui.component.CoreLayout
import com.example.jetpackcompass.ui.component.CustomizedCompass
import com.example.jetpackcompass.ui.theme.JetpackCompassTheme
import com.example.jetpackcompass.util.CompassUtil.currentDirectionPointToMecca
import kotlinx.coroutines.launch

class Compass2Activity : ComponentActivity() {
    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private var uiState by mutableStateOf(CompassUiState())
    private lateinit var compassViewModel: CompassViewModel

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            updatePermissionState(isGranted)
            if (isGranted) {
                startCompass()
            } else {
                Toast.makeText(
                    this,
                    "Location permission is required to use the compass",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    override fun onResume() {
        super.onResume()
        checkPermissionAndGps()
        startCompass()
    }


    override fun onPause() {
        super.onPause()
        stopCompass()
    }


    private fun updatePermissionState(hasPermission: Boolean) {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGpsEnabled =
            hasPermission && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        uiState = uiState.copy(
            hasLocationPermission = hasPermission,
            isGpsEnabled = isGpsEnabled
        )
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            compassViewModel.uiState.collect { state ->
                uiState = uiState.copy(
                    azimuth = state.azimuth,
                    directionText = state.directionText
                )
            }
        }

    }

    private fun startCompass() {
        if (uiState.hasLocationPermission && uiState.isGpsEnabled) {
            compassViewModel.start()
        }
    }

    private fun stopCompass() {
        compassViewModel.stop()
    }

    private fun checkPermissionAndGps() {
        val hasPermission =
            ContextCompat.checkSelfPermission(
                this,
                locationPermission
            ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            updatePermissionState(true)
        } else {
            permissionLauncher.launch(locationPermission)
        }
    }


    private fun createViewModel() {
        // todo: use other sensor manager
//        compassViewModel = CompassViewModel(
//            sensorDataSource = FakeCompassSensorManager(),
//            getCompassReadingUseCase = GetCompassReadingUseCase()
//        )

        compassViewModel = CompassViewModel(
            sensorDataSource = CompassSensorManager(this),
            locationDataSource = LocationSensorManager(this),
            calculateQiblaBearingUseCase = CalculateQiblaBearingUseCase()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createViewModel()

        collectUiState()

        enableEdgeToEdge()
        setContent {
            JetpackCompassTheme {
                Compass2Layout(
                    uiState = compassViewModel.uiState.collectAsState().value,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
private fun Compass2Layout(
    uiState: CompassUiState,
    onBack: () -> Unit = {},
) {
    // Device direction
    val animatedAzimuth by animateFloatAsState(
        targetValue = -uiState.azimuth,
        animationSpec = tween(durationMillis = 300),
        label = "dialRotation"
    )

    // Qibla direction
    val animatedQibla by animateFloatAsState(
        targetValue = uiState.qiblaBearing - uiState.azimuth,
        animationSpec = tween(durationMillis = 300),
        label = "qiblaRotation"
    )


    CoreLayout(
        modifier = Modifier.background(color = Color.Black),
        topBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .statusBarsPadding()
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        tint = Color.White,
                        contentDescription = "Back",
                        modifier = Modifier
                            .clickable { onBack() }
                    )
                }

                Text(
                    text = "${uiState.azimuth.toInt()}°",
                    color = if (currentDirectionPointToMecca(qiblaDirection = animatedQibla))
                        Color.Green
                    else
                        Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = uiState.directionText,
                    color = if (currentDirectionPointToMecca(qiblaDirection = animatedQibla))
                        Color.Green
                    else
                        Color.White,
                    fontSize = 24.sp
                )
            }

        },
        content = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CustomizedCompass(
                    azimuth = uiState.azimuth,
                    qiblaBearing = uiState.qiblaBearing,
                    compassDesign = CompassDesign.Royalty,
                    modifier = Modifier
                )
            }
        }
    )
}

@Preview
@Composable
private fun PreviewCompass2Layout() {
    Compass2Layout(
        uiState = CompassUiState(
            azimuth = 0f,
            directionText = "North"
        ),
        onBack = {},
    )
}