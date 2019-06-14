package io.tesseractgroup.purestatemachine

import io.tesseractgroup.purestatemachine.AgentConcurrencyType.ASYNC
import io.tesseractgroup.purestatemachine.AgentConcurrencyType.SYNC
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

/**
 * PureStateMachineApp
 * Created by matt on 2/22/18.
 */
enum class AgentConcurrencyType {
    SYNC, ASYNC
}

data class FetchAndUpdateResult<State, Result>(
        val fetchedValue: Result,
        val updatedState: State
)

class Agent<State: Any>(private var state: State) {

    private val privateThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    fun <Result : Any> fetch(closure: ((State) -> Result)): Result {
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

    fun <Result : Any> fetchAndUpdate(closure: (State) -> FetchAndUpdateResult<State, Result>): Result {
        var result: Result? = null
        sync {
            val resultValues = closure(state)
            state = resultValues.updatedState
            result = resultValues.fetchedValue
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