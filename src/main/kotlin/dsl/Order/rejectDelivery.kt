package dsl.Order

import dsl.Stage

fun Order.rejectDelivery(stageContext: Stage) {
    val order = this
    order.status = "rejected"
    val orderValue = order.content.sumOf { ord -> ord.quantity * stageContext.global.prices[ord.product]!! }
    order.client.balance += orderValue
    stageContext.global.deliveryConfirmation.add(order.id, order.status)
    // trigger stateCheck
    stageContext.orders.setUpdated()
    stageContext.clients.setUpdated()
    stageContext.feedback {
        order.orderFeedback()
    }
}