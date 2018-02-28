package io.tesseractgroup.purestatemachine

import io.tesseractgroup.purestatemachine.AgentConcurrencyType.*
import org.jetbrains.anko.doAsync

/**
 * PureStateMachineApp
 * Created by matt on 2/22/18.
 */
enum class AgentConcurrencyType {
    SYNC, ASYNC
}

class Agent<State>(private var state: State) {

    fun <Result> fetch(closure: ((State) -> Result)) : Result {
        var result: Result? = null
        sync {
            result = closure(state)
        }
        return result!!
    }

    fun update(type: AgentConcurrencyType = ASYNC, closure: (State) -> State ) {
        when(type){
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

    fun <Result> fetchAndUpdate(closure: (State) -> Pair<State, Result>) : Result {
        var result: Result? = null
        sync {
            val resultPair = closure(state)
            state = resultPair.first
            result = resultPair.second
        }
        return result!!
    }

    fun cast(closure: (State) -> Unit) {
        async(closure)
    }

    private fun sync(closure: (State) -> Unit){
        synchronized(this){
            closure(state)
        }
    }

    private fun async(closure: (State) -> Unit){
        doAsync {
            synchronized(this){
                closure(state)
            }
        }
    }

}