package dsl

import java.util.concurrent.ConcurrentHashMap


class GlobalContext () {
    private val generateCounterMap = ConcurrentHashMap<String, Long>()
    private val startTime = System.currentTimeMillis()

    class DeliveryConfirmation() {
        private val data = mutableListOf<Pair<String, String>>()
        fun add(order: String, status: String) = data.add(Pair(order, status))
        fun generateFile() {
            println("Generating file 'confirmed_delivery':")
            for (line in data) {
                println("> ${line.first}: ${line.second}")
            }
            data.clear()
        }
    }

    val deliveryConfirmation = DeliveryConfirmation()

    val prices = mapOf(
        "socks" to 10,
        "apple" to 3,
        "book" to 15,
        "lamp" to 4
    )

    // only for generate fun
    enum class GenerateSource {
        Input,
        Percent,
        TimeDigit,
        TimeLetter,
        CounterDigit,
        CounterLetter
    }
    /**
     * Generate string by pattern based on global counter and start timestamp
     * @param pattern the generated symbols must start from %: %c/%C - generate a digit/letter based on global counter,
     * %t/%T - generate a digit/letter based on start script time, example: "%c%C-sometext-%t%T" generate
     * "1W-sometext-2X". Pattern can be combined with %{}, i.e. "%c%C%t%T" can be converted to "%{cCtT}". The '%%'
     * sequence produce a '%' literal.
     * @param key the function works with named counters. Every counter starts from '0' or 'A' and increasing only for
     * function call with the same key, i.e. generate("%c", "inv") increment only the 'inv' counter and don't touch
     * others.
     */
    fun generate(pattern: String, key: String = "default"): String {
        val source = mutableListOf<GenerateSource>()
        var filteredInput = String()
        var idx = 0
        while (idx < pattern.length) {
            if (pattern[idx] == '%') {
                idx += 1
                when (pattern[idx]) {
                    'c' -> source.add(GenerateSource.CounterDigit)
                    'C' -> source.add(GenerateSource.CounterLetter)
                    't' -> source.add(GenerateSource.TimeDigit)
                    'T' -> source.add(GenerateSource.TimeLetter)
                    '%' -> source.add(GenerateSource.Percent)
                    '{' -> {
                        idx += 1
                        while (true) {
                            when (pattern[idx]) {
                                'c' -> source.add(GenerateSource.CounterDigit)
                                'C' -> source.add(GenerateSource.CounterLetter)
                                't' -> source.add(GenerateSource.TimeDigit)
                                'T' -> source.add(GenerateSource.TimeLetter)
                                else -> throw Exception("generate: Only 'c','C','t','T' symbols are allowed inside '%{...}'")
                            }
                            idx += 1
                            if (idx == pattern.length) {
                                throw Exception("generate: Unclosed generate pattern '%{...}', the '}' is required")
                            }
                            if (pattern[idx] == '}') {
                                break
                            }
                        }
                    }
                    else -> throw Exception("generate: Only '%','c','C','t','T','{' symbols are allowed after '%'")
                }
            } else {
                source.add(GenerateSource.Input)
                filteredInput += pattern[idx]
            }
            idx += 1
        }
        val result = CharArray(source.size)

        var counterValue = generateCounterMap.merge(key, 1) { prev, _ -> prev + 1 }!!
        var timeValue = (startTime / 100)
        var inputIdx = filteredInput.length
        idx = source.size
        while (idx > 0) {
            idx -= 1
            when (source[idx]) {
                GenerateSource.Input -> {
                    inputIdx -= 1
                    result[idx] = filteredInput[inputIdx]
                }
                GenerateSource.Percent -> result[idx] = '%'
                GenerateSource.TimeDigit -> {
                    val reminder = timeValue % 10
                    timeValue /= 10
                    result[idx] = '0' + reminder.toInt()
                }
                GenerateSource.TimeLetter -> {
                    val reminder = timeValue % 26
                    timeValue /= 26
                    result[idx] = 'A' + reminder.toInt()
                }
                GenerateSource.CounterDigit -> {
                    val reminder = counterValue % 10
                    counterValue /= 10
                    result[idx] = '0' + reminder.toInt()
                }
                GenerateSource.CounterLetter -> {
                    val reminder = counterValue % 26
                    counterValue /= 26
                    result[idx] = 'A' + reminder.toInt()
                }
            }
        }
        return result.joinToString("")
    }
}
