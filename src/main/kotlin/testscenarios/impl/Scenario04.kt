package testscenarios.impl

import dsl.*
import dsl.Client.createClient
import dsl.Order.OrderPosition
import dsl.Order.confirmDelivery
import dsl.Order.createOrder
import testscenarios.TestScenario

class Scenario04: TestScenario() {
    override val name = "Scenario04"
    override val description = "Generate parts of scenario"
    override fun scenario(ctx: ScheduleContext) {
        with(ctx) {
            val inputData = listOf<Pair<String, Long>>(
                // namespace, quantity of lamps
                Pair("v1", 2),
                Pair("v2", 100),
                Pair("v3", 1)
            )
            scenario(name, description) {
                stage("D2_CreateClient") {
                    clients["cl1"] = createClient("MyClient04")
                }
                inputData.forEach {
                    val namespace = it.first
                    val quantity = it.second
                    stage("D2_CreateOrder") {
                        orders["${namespace}:ord1"] = clients["cl1"].createOrder(
                            this,
                            content = listOf(
                                OrderPosition("lamp", quantity)
                            )
                        )
                    }
                    stage("D2_Delivery") {
                        orders["${namespace}:ord1"].confirmDelivery(this)
                    }
                }
            }
        }
    }
}
