package de.flapdoodle.tab.model.calculations

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Usecase {


    @Test
    fun ex30range() {
        val ex30 = Properties(
            battery = 66.0,
            firstSOC = 0.9,
            secondSOC = 0.7,
            loadingTime = 0.45,
        )

        val ex30consumptions = listOf<Consumption>(
            Consumption(15.5, 100.0),
            Consumption(18.6, 110.0),
            Consumption(25.1, 130.0),
            Consumption(27.1, 140.0),
            Consumption(45.1, 160.0),
        )

        ex30consumptions.forEach { consumption ->
            val waypoints = waypoints(consumption, ex30, 800.0)
            waypoints.forEach { waypoint -> print("${waypoint.distance} (${waypoint.time}), ") }
            println()
            val stopps = waypoints.size - 1
            val timeUsed = waypoints.sumOf { it.time } + stopps *ex30.loadingTime
            val distance = waypoints.sumOf { it.distance }

            println(consumption)
            println("stopps: $stopps")
            println("distance: $distance")
            println("time used: $timeUsed")
            println()
        }


    }

    @Test
    fun onlyOneWaypoint() {
        val consumption = Consumption(10.0, 100.0)
        val properties = Properties(15.0, 0.9, 0.7, 0.3)

        assertThat(waypoints(consumption, properties, 10.0))
            .hasSize(1)
            .containsExactly(Waypoint(10.0 , 0.1))

        assertThat(waypoints(consumption, properties, 200.0))
            .hasSize(2)
            .containsExactly(Waypoint(135.0, 1.35), Waypoint(65.0, 0.65))
    }

    fun waypoints(consumption: Consumption, properties: Properties, endKm: Double): List<Waypoint> {
        var ret = emptyList<Waypoint>()

        val first = waypoint(properties.battery*properties.firstSOC, consumption)
        if (first.distance >= endKm) {
            ret = ret + first.at(endKm)
        } else {
            ret = ret + first
            var rest = endKm - first.distance
            do {
                val second = waypoint(properties.battery*properties.secondSOC, consumption)
                if (second.distance >= rest) {
                    ret = ret + second.at(rest)
                    rest = 0.0
                } else {
                    ret = ret + second
                    rest -= second.distance
                }
            } while (rest > 0)
        }
        return ret
    }

    fun waypoint(battery: Double, consumption: Consumption): Waypoint {
        val distance = battery/consumption.kw*100.0
        val time = distance/consumption.speed
        return Waypoint(distance, time)
    }

    data class Consumption(val kw: Double, val speed: Double)
    data class Properties(val battery: Double, val firstSOC: Double, val secondSOC: Double, val loadingTime: Double)
    data class Waypoint(val distance: Double, val time: Double) {
        fun at(partial: Double): Waypoint {
            require(partial <= distance) {"partial > distance"}
            val factor = partial / distance
            return Waypoint(partial, time * factor)
        }
    }
}