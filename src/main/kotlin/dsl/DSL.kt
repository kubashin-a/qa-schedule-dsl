package dsl

import dsl.Client.Client
import dsl.Client.checkClients
import dsl.Order.Order
import dsl.Order.checkOrders
import java.util.*
import kotlin.collections.LinkedHashSet
import testscenarios.*

fun runSchedule() {
    val scheduleContext = ScheduleContext()
    val scenarios: Iterable<TestScenario> = ServiceLoader.load(TestScenario::class.java)
    scenarios.forEach { testScenario ->
        testScenario.scenario(scheduleContext)
    }

    val stageList = scheduleContext.schedule.getStageNames()

    // schedule execution
    stageList.forEach { stageName ->
        println(">> Stage[$stageName]")
        val event = scheduleContext.schedule.getStage(stageName)

        var hasFeedbacks   = false
        var hasStateChecks = false
        event.scenarioStages.forEach { (_, stage) -> // run all scenarios action parts of one Stage in parallel
            if (stage.actions.isNotEmpty()) {
                println(" * Scenario[${stage.scenario.name}]/action")
                for (action in stage.actions) {
                    try {
                        stage.run(action)
                    } catch (e: Exception) {
                        // inform about Exception, but don't stop schedule execution
                        println("  !! Kotlin Error: $e")
                        println(e.stackTraceToString())
                    }
                }
                if (stage.feedbacks.size != 0) { hasFeedbacks = true }
                if (stage.clients.isUpdated()) { hasStateChecks = true }
                if (stage.orders.isUpdated()) { hasStateChecks = true }
            }
        }

        // innerSteps processing
        event.scenarioStages.forEach { (_, stage) ->
            if (stage.innerSteps.isNotEmpty()) {
                println(" * Scenario[${stage.scenario.name}]/innerStep")
                for (action in stage.innerSteps) {
                    try {
                        stage.run(action)
                    } catch (e: Exception) {
                        println("  !! Kotlin Error: $e")
                        println(e.stackTraceToString())
                    }
                }
            }
        }

        if (hasFeedbacks || hasStateChecks) {
            println("  <wait[${event.pause}s]>")
            Thread.sleep(event.pause * 1000)
        }
        if (hasStateChecks) {
            event.scenarioStages.forEach { (_, stage) ->
                val objectList = listOf(
                    stage.clients,
                    stage.orders,
                )
                val hasScenarioChecks = objectList.any{ it.isUpdated() } // at list one object isUpdated
                if (hasScenarioChecks) {
                    objectList.filter{ it.isUpdated() }.forEach { obj ->
                        println(" * Scenario[${stage.scenario.name}]/stateCheck[${obj.objectName}]")
                        if (obj.isUpdated()) {
                            try {
                                obj.checkAll(stage)
                            } catch (e: Exception) {
                                println("  !! Kotlin Error: $e")
                                println(e.stackTraceToString())
                            }
                        }
                    }
                }
            }
        }
        if (hasFeedbacks) {
            event.scenarioStages.forEach { (_, stage) ->
                if (stage.feedbacks.size > 0) {
                    println(" * Scenario[${stage.scenario.name}]/feedback")
                    stage.feedbacks.forEach { feedback ->
                        try {
                            feedback(stage)
                        } catch (e: Exception) {
                            println("  !! Kotlin Error: $e")
                            println(e.stackTraceToString())
                        }
                    }
                }
            }
        }
    }

}

@DslMarker
annotation class Dsl

@Dsl
class ScheduleContext () {
    private val scenarios = LinkedHashSet<Scenario>()

    val schedule = Schedule()
    val global   = GlobalContext()

    fun scenario(
        name: String,
        description: String,
        setup: Scenario.() -> Unit
    ) {
        if (scenarios.any{ it.name == name }) {
            throw Exception("Duplicated scenario name '$name'")
        }
        scenarios += Scenario(
            name = name,
            description = description,
            schedule = this
        ).also(setup)
    }
}

@Dsl
class Scenario(
    val name: String,
    val description: String,
    val schedule: ScheduleContext
) {
    val global  = schedule.global

    val clients = GenericScenarioObjects<Client>("client", checkFun = Stage::checkClients)
    val orders  = GenericScenarioObjects<Order>("order", checkFun = Stage::checkOrders)

    fun stage(name: String, function: Stage.() -> Unit) {
        schedule.schedule.addScenarioStage(name, this, function)
    }
}

@Dsl
data class Stage(
    val scenario: Scenario
) {
    val actions = mutableListOf<Stage.() -> Unit>()
    val innerSteps = mutableListOf<Stage.() -> Unit>()

    val clients = scenario.clients
    val orders  = scenario.orders

    val global = scenario.schedule.global
    val feedbacks = arrayListOf<Stage.() -> Unit>()

    fun feedback(func: Stage.() -> Unit) {
        feedbacks.add(func)
    }
}
