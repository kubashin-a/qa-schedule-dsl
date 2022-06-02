package dsl.Order

import dsl.Client.Client
import dsl.Stage

fun Client.createOrder(stageContext: Stage, content: List<OrderPosition>): Order {
    val newOrder = Order(global = stageContext.global, this, content)
    println("Create order '${newOrder.id}' for client[${getScenarioName()}]")

    // trigger stateCheck
    stageContext.orders.setUpdated()
    stageContext.feedback{
        newOrder.orderFeedback()
    }
    return newOrder
}
