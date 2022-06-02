package dsl.Order

import dsl.Stage

fun Order.confirmDelivery(stageContext: Stage) {
    val order = this
    order.status = "delivered"
    stageContext.global.deliveryConfirmation.add(order.id, order.status)
    // trigger statecheck
    stageContext.orders.setUpdated()
    stageContext.feedback {
        order.orderFeedback()
    }
}