package dsl.Order

import dsl.Client.Client
import dsl.Stage

fun Client.checkClientOrders(stageContext: Stage) {
    val clientOrders = stageContext.orders
        .filter { it.client === this }
        .joinToString(",", "[", "]") { it.getScenarioName() }
    println("Orders of client[${getScenarioName()}] is $clientOrders")
}