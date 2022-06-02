package dsl

class ScheduleEvent(
    val pause: Long,
    val scenarioStages: MutableMap<String, Stage> = mutableMapOf()
)

class Schedule(
    private var stageNames: MutableList<String> = arrayListOf(),
    private var stageMap: MutableMap<String, ScheduleEvent> = mutableMapOf()
) {
    init {
        // ScheduleEvent( name, pause )
        addScheduleStage("D1_StartDay", 0)
        addScheduleStage("D1_CreateClient", 3)
        addScheduleStage("D1_CreateOrder", 1)
        addScheduleStage("D1_Delivery", 2)
        addScheduleStage("D2_StartDay", 0)
        addScheduleStage("D2_CreateClient", 3)
        addScheduleStage("D2_CreateOrder", 1)
        addScheduleStage("D2_Delivery", 2)

    }
    private fun addScheduleStage(name: String, pause: Long) {
        if (name in stageNames) {
            throw Exception("Duplicate declaration of stage '${name}'")
        }
        stageNames.add(name)
        stageMap[name] = ScheduleEvent(pause)
    }
    fun addScenarioStage(name: String, scenario: Scenario, action: Stage.() -> Unit) {
        val nameParts = name.split('@')
        val shortName = nameParts[0]
        val scenarioName = scenario.name

        val isInnerStep: Boolean = if (nameParts.size == 1) {
            false
        } else if (nameParts.size == 2 && nameParts[1] == "innerStep") {
            true
        } else {
            throw Exception("Error in stage name '$name': only @innerStep suffix is allowed")
        }

        if (!stageNames.contains(shortName)) {
            throw Exception("Stage '${shortName}' not exist in schedule")
        }

        if (!stageMap[shortName]!!.scenarioStages.containsKey(scenarioName)) {
            stageMap[shortName]!!.scenarioStages[scenarioName] = Stage(scenario)
        }
        if (isInnerStep) {
            stageMap[shortName]!!.scenarioStages[scenarioName]!!.innerSteps.add(action)
        } else {
            stageMap[shortName]!!.scenarioStages[scenarioName]!!.actions.add(action)
        }
    }
    fun getStageNames(): List<String> {
        return stageNames
    }
    fun getStage(name: String): ScheduleEvent {
        if (!stageMap.containsKey(name)) {
            throw Exception("Stage '${name}' not exist in schedule")
        }
        return stageMap[name]!!
    }
}