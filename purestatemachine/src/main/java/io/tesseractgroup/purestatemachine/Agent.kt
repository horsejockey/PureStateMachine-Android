package io.tesseractgroup.purestatemachine

import io.tesseractgroup.purestatemachine.AgentConcurrencyType.ASYNC
import io.tesseractgroup.purestatemachine.AgentConcurrencyType.SYNC
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * PureStateMachineApp
 * Created by matt on 2/22/18.
 */
enum class AgentConcurrencyType {
    SYNC, ASYNC
}

class Agent<State>(private var state: State) {

    private val privateThread = newSingleThreadContext("Agent-${UUID.randomUUID()}")

    fun <Result: Any> fetch(closure: ((State) -> Result)): Result {
        var result: Result? = null
        sync {
            result = closure(state)
        }
        return result!!
    }

    fun update(type: AgentConcurrencyType = ASYNC, closure: (State) -> State) {
        when (type) {
            ASYNC -> {
                async {
                    state = closure(state)
                }
            }
            SYNC -> {
                sync {
                    state = closure(state)
                }
            }
        }
    }

    fun <Result: Any> fetchAndUpdate(closure: (State) -> Pair<State, Result>): Result {
        var result: Result? = null
        sync {
            val resultPair = closure(state)
            state = resultPair.first
            result = resultPair.second
        }
        return result!!
    }

    private fun sync(closure: (State) -> Unit) {
        runBlocking(privateThread) {
            closure(state)
        }
    }

    private fun async(closure: (State) -> Unit) {
        GlobalScope.launch(privateThread) {
            closure(state)
        }
    }

}