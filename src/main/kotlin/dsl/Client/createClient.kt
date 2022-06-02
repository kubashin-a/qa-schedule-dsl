package dsl.Client

import dsl.Stage

fun Stage.createClient(name: String, isValid: Boolean = true, error_message: String? = null): Client? {
    if (!isValid) { // negative path
        println("Error creating client '$name': $error_message")
        return null
    } else { // positive path
        val newClient = Client(name)
        println("Create client '$name'")
        this.clients.setUpdated()
        this.feedback {
            println("Status of client[${newClient.getScenarioName()}] is 'active'")
        }
        return newClient
    }
}