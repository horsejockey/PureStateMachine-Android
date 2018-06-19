package io.tesseractgroup.purestatemachine

/**
 * PureStateMachineApp
 * Created by matt on 2/22/18.
 */

class PureStateMachine<State: Any, Event: Any, Command: Any>(initialState: State, private val handler: (State, Event) -> Pair<State, Command>) {

    private val state: Agent<State> = Agent(initialState)

    var currentState: State
        get() {
            return state.fetch { it }
        }
        private set(value) {
            //none
        }

    fun handleEvent(event: Event) : Command {
        return state.fetchAndUpdate { s ->
            handler(s, event)
        }
    }

}