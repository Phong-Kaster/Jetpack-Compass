package com.example.jetpackcompass.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class CompassSensorManager(context: Context) : CompassSensorDataSource {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val magnetometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private var gravity = FloatArray(3)
    private var geomagnetic = FloatArray(3)

    private var onReading: ((Float) -> Unit)? = null

    private val listener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER ->
                    gravity = event.values.clone()

                Sensor.TYPE_MAGNETIC_FIELD ->
                    geomagnetic = event.values.clone()
            }

            val rotationMatrix = FloatArray(9)
            val inclinationMatrix = FloatArray(9)

            if (SensorManager.getRotationMatrix(
                    rotationMatrix,
                    inclinationMatrix,
                    gravity,
                    geomagnetic
                )
            ) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)

                val azimuth =
                    Math.toDegrees(orientation[0].toDouble()).toFloat()

                onReading?.invoke(azimuth)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    override fun start(onReading: (Float) -> Unit) {
        this.onReading = onReading
        sensorManager.registerListener(
            listener,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
        sensorManager.registerListener(
            listener,
            magnetometer,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun stop() {
        sensorManager.unregisterListener(listener)
        onReading = null
    }
}
