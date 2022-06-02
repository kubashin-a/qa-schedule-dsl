package testscenarios.impl

import dsl.*
import dsl.Client.createClient
import dsl.Order.OrderPosition
import dsl.Order.confirmDelivery
import dsl.Order.createOrder
import dsl.Order.rejectDelivery
import testscenarios.TestScenario

class Scenario01: TestScenario() {
    override val name = "Scenario01"
    override val description = "Simple scenario"
    override fun scenario(ctx: ScheduleContext) {
        with(ctx) {
            scenario(name, description) {
                stage("D1_CreateClient") {
                    clients["cl1"] = createClient("SuperClient")
                    clients["cl2"] = createClient("RejectClient")
                }
                stage("D1_CreateOrder") {
                    orders["ord1"] = clients["cl1"].createOrder(this,
                        content = listOf(
                            OrderPosition("socks", 3),
                            OrderPosition("apple", 1)
                        )
                    )
                    orders["ord2"] = clients["cl2"].createOrder(this,
                        content = listOf(
                            OrderPosition("socks", 3),
                            OrderPosition("book", 2)
                        )
                    )
                }
                stage("D1_Delivery") {
                    orders["ord1"].confirmDelivery(this)
                    orders["ord2"].rejectDelivery(this)
                }
            }
        }
    }
}
