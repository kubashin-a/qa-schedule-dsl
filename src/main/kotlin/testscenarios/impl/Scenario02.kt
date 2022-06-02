package testscenarios.impl

import dsl.*
import dsl.Client.createClient
import dsl.Order.*
import testscenarios.TestScenario

class Scenario02: TestScenario() {
    override val name = "Scenario02"
    override val description = "Demonstration of negative/partially negative cases and multiple output of function"
    override fun scenario(ctx: ScheduleContext) {
        with(ctx) {
            scenario(name, description) {
                stage("D1_CreateClient") {
                    clients["cl1"] = createClient("MyClient02")
                    clients["_"] = createClient(
                        name = clients["cl1"].name,
                        isValid = false,
                        error_message = "Client with name '${clients["cl1"].name}' already exists"
                    )
                }
                stage("D1_CreateOrder") {
                    orders["ord1", "_", "ord3"] = clients["cl1"].multiOrder(this,
                        instructions = listOf(
                            OrderInstruction("socks", 2),
                            OrderInstruction(
                                product = "apple",
                                quantity = 2,
                                isValid = false,
                                error_message = "Apples not in stock"
                            ),
                            OrderInstruction("lamp", 1),
                        )
                    )
                }
                stage("D1_Delivery") {
                    orders["ord1"].confirmDelivery(this)
                    orders["ord3"].confirmDelivery(this)
                }
            }
        }
    }
}
