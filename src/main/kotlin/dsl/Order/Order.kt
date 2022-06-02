package dsl.Order

import dsl.Client.Client
import dsl.GlobalContext
import dsl.ScenarioObject
import dsl.Stage

class OrderPosition(
    val product: String,
    val quantity: Long,
)

class OrderInstruction(
    val product: String,
    val quantity: Long,
    val isValid: Boolean = true,
    val error_message: String? = null
)

class Order (
    global: GlobalContext,
    val client: Client,
    val content: List<OrderPosition>
): ScenarioObject {
    var status = "new"
    val id = global.generate("%{TT}-%{ccccc}")
    private var _scenarioName: String? = null
    override fun setScenarioName(name: String) {
        _scenarioName = name
    }
    override fun getScenarioName(): String {
        return if (_scenarioName == null) id else _scenarioName!!
    }
}

fun Stage.checkOrders() {
    for (client in this.clients.all()) {
        client.checkClientOrders(this)
    }
}