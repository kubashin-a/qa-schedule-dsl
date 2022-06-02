package dsl.Client

import dsl.ScenarioObject
import dsl.Stage


class Client (
    var name: String
): ScenarioObject {
    var balance: Long = 0

    var _scenarioName: String? = null
    override fun setScenarioName(name: String) {
        _scenarioName = name
    }
    override fun getScenarioName(): String {
        return if (_scenarioName == null) name else _scenarioName!!
    }

}

fun Stage.checkClients() {
    for (client in this.clients.all()) {
        client.checkClient()
    }
    this.clients.resetUpdated()
}