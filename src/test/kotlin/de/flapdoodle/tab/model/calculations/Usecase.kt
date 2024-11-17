package de.flapdoodle.tab.model.calculations

import org.junit.jupiter.api.Test

class Usecase {

    @Test
    fun ex30range() {
        val ex30 = Properties(
            battery = 66.0,
            firstSOC = 0.9,
            secondSOC = 0.7,
            loadingTime = 0.25,
        )

        val ex30consumptions = listOf<Consumption>(
            Consumption(15.5, 100.0),
            Consumption(18.6, 110.0),
            Consumption(25.1, 130.0),
            Consumption(27.1, 140.0),
        )


    }

    fun waypoint(consumption: Consumption, properties: Properties, endKm: Double): List<Waypoint> {
        var ret = emptyList<Waypoint>()

        val first = waypoint(properties.battery*properties.firstSOC, consumption)
        return ret
    }

    fun waypoint(battery: Double, consumption: Consumption): Waypoint {
        val distance = battery*consumption.kw/100.0
        val time = distance/consumption.speed
        return Waypoint(distance, time)
    }

    data class Consumption(val kw: Double, val speed: Double)
    data class Properties(val battery: Double, val firstSOC: Double, val secondSOC: Double, val loadingTime: Double)
    data class Waypoint(val distance: Double, val time: Double)
}