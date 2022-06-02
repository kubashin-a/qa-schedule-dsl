package testscenarios

import dsl.*

abstract class TestScenario {
    abstract val name: String
    abstract val description: String

    abstract fun scenario(ctx: ScheduleContext)
}
