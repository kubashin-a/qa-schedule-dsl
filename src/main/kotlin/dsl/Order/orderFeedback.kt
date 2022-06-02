package dsl.Order

fun Order.orderFeedback() {
    println("Status of order[${this.getScenarioName()}] is '${this.status}'")
}