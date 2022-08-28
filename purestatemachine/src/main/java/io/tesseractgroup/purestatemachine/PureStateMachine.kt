package io.tesseractgroup.purestatemachine

/**
 * PureStateMachineApp
 * Created by matt on 2/22/18.
 */

class PureStateMachine<State: Any, Event: Any, Command: Any>(initialState: State, private val handler: (State, Event) -> StateUpdate<State, Command>) {

    private val state: Agent<State> = Agent(initialState)

    val currentState: State
        get() {
            return state.fetch { it }
        }

    fun handleEvent(event: Event) : List<Command> {
        val commands: List<Command> = state.fetchAndUpdate { currentState ->
            val stateUpdate = handler(currentState, event)
            FetchAndUpdateResult(stateUpdate.commands(), (stateUpdate.state() ?: currentState))
        }
        return commands
    }
}

sealed class StateUpdate<State, Command> {
    class NoUpdate<State, Command>: StateUpdate<State, Command>()
    class State<State, Command>(val state: State): StateUpdate<State, Command>()
    class Commands<State, Command>(val commands: List<Command>): StateUpdate<State, Command>()
    class StateAndCommands<State, Command>(val state: State, val commands: List<Command>): StateUpdate<State, Command>()

    fun state(): State? {
        return when(this){
            is NoUpdate -> null
            is StateUpdate.State -> this.state
            is Commands -> null
            is StateAndCommands -> this.state
        }
    }

    fun commands(): List<Command> {
        return when(this){
            is NoUpdate -> listOf()
            is StateUpdate.State -> listOf()
            is Commands -> commands
            is StateAndCommands -> commands
        }
    }

    fun <T>mapState(lambda: (State) -> T): StateUpdate<T, Command> {
        return when(this){
            is NoUpdate -> NoUpdate()
            is StateUpdate.State -> State(lambda(state))
            is Commands -> Commands(commands)
            is StateAndCommands -> StateAndCommands(lambda(state), commands)
        }
    }

    fun <T>mapCommand(lambda: (Command) -> T): StateUpdate<State, T> {
        return when(this){
            is NoUpdate -> NoUpdate()
            is StateUpdate.State -> State(state)
            is Commands -> Commands(commands.map(lambda))
            is StateAndCommands -> StateAndCommands(state, commands.map(lambda))
        }
    }
}