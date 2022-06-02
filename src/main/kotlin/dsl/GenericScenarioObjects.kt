package dsl


class GenericScenarioObjects<T: ScenarioObject>(
    val objectName: String,
    private val checkFun: Stage.() -> Unit
) {
    private val mapData: MutableMap<String, T> = mutableMapOf()
    private var isUpdated: Boolean = false
    fun setUpdated() {
        isUpdated = true
    }
    fun resetUpdated() {
        isUpdated = false
    }
    fun skipAutochecks() {
        isUpdated = false
    }

    fun isUpdated(): Boolean = isUpdated

    fun filter(func: (T) -> Boolean): List<T> {
        return mapData.values.filter(func)
    }
    fun all(): List<T> = mapData.values.toList()
    operator fun get(name: String) = when {
        name in mapData -> mapData[name]!!
        else -> throw Exception("No $objectName '$name' found")
    }
    operator fun set(vararg names: String, data: List<T?>) {
        if (names.size != data.size) {
            val nameList = names.joinToString(prefix = "\"", postfix = "\"", separator = "\", \"")
            throw Exception("Invalid sizes: $objectName[$nameList] wait ${names.size} parameters, but get only ${data.size}")
        }
        for ((idx, name) in names.withIndex()) {
            if (name == "_" && data[idx] == null) {
                continue // don't add null values
            }
            if (name == "_" && data[idx] != null) {
                throw Exception("$objectName[$name] allowed only for empty values")
            }
            if (data[idx] == null && name != "_") {
                throw Exception("$objectName[$name] is empty and must be defined as special name '_'")
            }
            if (name in mapData) {
                throw Exception("$objectName[$name] already defined")
            }
            data[idx]!!.setScenarioName(name)
            mapData[name] = data[idx]!!
        }
    }
    operator fun set(name: String, data: T?) {
        if (name == "_" && data == null) {
            return // don't add null values
        }
        if (name == "_" && data != null) {
            throw Exception("$objectName[$name] allowed only for empty values")
        }
        if (data == null && name != "_") {
            throw Exception("$objectName[$name] is empty and must be defined as special name '_'")
        }
        if (name in mapData) {
            throw Exception("$objectName[$name] already defined")
        }
        data!!.setScenarioName(name)
        mapData[name] = data
    }
    fun checkAll(stageContext: Stage) {
        stageContext.apply(checkFun)
        isUpdated = false
    }
}

interface ScenarioObject {
    fun setScenarioName(name: String)
    fun getScenarioName(): String
}
