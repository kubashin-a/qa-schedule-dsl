package testscenarios.impl

import dsl.*
import dsl.Client.createClient
import dsl.Order.OrderPosition
import dsl.Order.confirmDelivery
import dsl.Order.createOrder
import testscenarios.TestScenario

class Scenario03: TestScenario() {
    override val name = "Scenario03"
    override val description = "Multiple scenario generation"
    override fun scenario(ctx: ScheduleContext) {
        with(ctx) {
            val inputData = listOf<Pair<String, Long>>(
                // additional info, quantity of lamps
                Pair("v1", 2),
                Pair("v2", 100),
                Pair("v3", 1)
            )
            inputData.forEach {
                val info = it.first
                val quantity = it.second
                scenario("$name:$info", description) {
                    stage("D2_CreateClient") {
                        clients["cl1"] = createClient(global.generate("GenClient%{cc}", "client"))
                    }
                    stage("D2_CreateOrder") {
                        orders["ord1"] = clients["cl1"].createOrder(
                            this,
                            content = listOf(
                                OrderPosition("lamp", quantity)
                            )
                        )
                    }
                    stage("D2_Delivery") {
                        orders["ord1"].confirmDelivery(this)
                    }
                }
            }
        }
    }
}
