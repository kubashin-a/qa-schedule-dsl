package dsl.Client

fun Client.checkClient() {
    println("Client[${getScenarioName()}] '${this.name}' has balance: $balance")
}