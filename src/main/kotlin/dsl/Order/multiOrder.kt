package dsl.Order

import dsl.Client.Client
import dsl.Stage

// Just example for multiple and partially negative output.
// This function create multiple orders for one call
fun Client.multiOrder(stageContext: Stage, instructions: List<OrderInstruction>): List<Order?> {
    stageContext.orders.setUpdated()
    val orderList = mutableListOf<Order?>()
    for (instruction in instructions) {
        if (!instruction.isValid) { // negative path
            println("Error while creating order: ${instruction.error_message!!}")
            orderList.add(null)
        } else { // positive path
            val newOrder = Order(global = stageContext.global, this, listOf(
                OrderPosition(instruction.product, instruction.quantity)
            ))
            orderList.add(newOrder)
            println("Create order '${newOrder.id}' for client[${getScenarioName()}]")
            // trigger stateCheck
            stageContext.feedback {
                newOrder.orderFeedback()
            }
        }
    }
    return orderList
}
