package io.tesseractgroup.purestatemachine

import kotlinx.coroutines.*
import org.amshove.kluent.shouldEqual
import org.junit.Test

/**
 * PureStateMachineApp
 * Created by matt on 4/26/18.
 */

class PureStateMachineTests {

    @Test
    fun testStateMachine() {
        val stateMachine = PureStateMachine(1, ::eventHandler)
        stateMachine.currentState shouldEqual 1
        stateMachine.handleEvent(Event.Add(1))
        stateMachine.currentState shouldEqual 2
        stateMachine.handleEvent(Event.Subtract(1))
        stateMachine.currentState shouldEqual 1
    }

    private fun eventHandler(state: Int, event: Event): StateUpdate<Int, Command> {
        return when(event){
            is Event.Add -> {
                StateUpdate.StateAndCommands(
                        state + event.value, listOf(Command.Print)
                )
            }
            is Event.Subtract -> {
                StateUpdate.StateAndCommands(
                        state - event.value, listOf(Command.Print)
                )
            }
        }
    }


    sealed class Event {
        data class Add(val value: Int): Event()
        data class Subtract(val value: Int): Event()
    }

    sealed class Command {
        object Print: Command()
    }

}