package testscenarios.impl

import dsl.*
import testscenarios.TestScenario

class Auxiliary: TestScenario() {
    override val name = "Auxiliary"
    override val description = "Run workflow of 'real' system"
    override fun scenario(ctx: ScheduleContext) {
        with(ctx) {
            scenario(name, description) {
                stage("D1_StartDay") {
                    println("===> Start 1st virtual day <===")
                }
                stage("D1_Delivery@innerStep") {
                    global.deliveryConfirmation.generateFile()
                }
                stage("D2_StartDay") {
                    println("===> Start 2nd virtual day <===")
                }
                stage("D2_Delivery@innerStep") {
                    global.deliveryConfirmation.generateFile()
                }
            }
        }
    }
}