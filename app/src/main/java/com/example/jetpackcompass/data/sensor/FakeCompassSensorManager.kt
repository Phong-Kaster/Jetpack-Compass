package com.example.jetpackcompass.data.sensor

class FakeCompassSensorManager : CompassSensorDataSource {

    private var running = false
    private var worker: Thread? = null

    override fun start(onReading: (Float) -> Unit) {
        running = true

        worker = Thread {
            var angle = 0f
            while (running) {
                onReading(angle)
                angle = (angle + 3f) % 360f
                Thread.sleep(50)
            }
        }.also { it.start() }
    }

    override fun stop() {
        running = false
        worker = null
    }
}
