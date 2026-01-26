package com.example.jetpackcompass

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.jetpackcompass.data.location.LocationDataSource
import com.example.jetpackcompass.data.location.LocationSensorManager
import com.example.jetpackcompass.data.sensor.CompassSensorManager
import com.example.jetpackcompass.data.sensor.FakeCompassSensorManager
import com.example.jetpackcompass.domain.usecase.CalculateQiblaBearingUseCase
import com.example.jetpackcompass.ui.compass.CompassUiState
import com.example.jetpackcompass.ui.compass.CompassScreen
import com.example.jetpackcompass.ui.compass.CompassViewModel
import com.example.jetpackcompass.ui.component.CoreLayout
import com.example.jetpackcompass.ui.component.GpsDisabledContent
import com.example.jetpackcompass.ui.component.PermissionRequiredContent
import com.example.jetpackcompass.ui.theme.JetpackCompassTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private var uiState by mutableStateOf(CompassUiState())
    private lateinit var compassViewModel: CompassViewModel

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            updatePermissionState(isGranted)
            if (!isGranted) {
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
        collectUiState()
    }

    override fun onPause() {
        super.onPause()
        stopCompass()
    }


    private fun createViewModel() {
        compassViewModel = CompassViewModel(
            sensorDataSource = CompassSensorManager(this),
            locationDataSource = LocationSensorManager(this),
            calculateQiblaBearingUseCase = CalculateQiblaBearingUseCase()
        )
    }

    private fun checkPermissionAndGps() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                locationPermission
            ) == PackageManager.PERMISSION_GRANTED -> updatePermissionState(true)

            else -> permissionLauncher.launch(locationPermission)
        }
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

    private fun openLocationSettings() {
        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createViewModel()
        enableEdgeToEdge()
        setContent {
            JetpackCompassTheme {
                JetpackComposeLayout(
                    uiState = uiState,
                    onOpenLocationSettings = { openLocationSettings() },
                    onOpenNextVersion = {
                        val intent = Intent(this, Compass2Activity::class.java)
                        startActivity(intent)

                    }
                )
            }
        }
    }
}


@Composable
fun JetpackComposeLayout(
    uiState: CompassUiState,
    onOpenLocationSettings: () -> Unit = {},
    onOpenNextVersion: () -> Unit = {},
) {
    CoreLayout(
        modifier = Modifier.background(Color.Black),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 32.dp
                    )
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Go to next version compass",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Blue
                        )
                        .clickable {
                            onOpenNextVersion()
                        }
                        .padding(12.dp)
                )
            }
        },
        content = {
            CompassScreen(
                enable = uiState.hasLocationPermission && uiState.isGpsEnabled,
                uiState = uiState,
            )

            GpsDisabledContent(
                enable = uiState.hasLocationPermission && !uiState.isGpsEnabled,
                onOpenLocationSettings = onOpenLocationSettings,
            )

            PermissionRequiredContent(
                enable = !uiState.hasLocationPermission || !uiState.isGpsEnabled,
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun JetpackComposeLayoutPreview() {
    JetpackComposeLayout(
        uiState = CompassUiState(
            hasLocationPermission = false,
            isGpsEnabled = true
        ),
        onOpenLocationSettings = {}
    )
}