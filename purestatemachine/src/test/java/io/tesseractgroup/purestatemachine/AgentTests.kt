package io.tesseractgroup.purestatemachine

import kotlinx.coroutines.*
import org.amshove.kluent.*
import org.junit.Test

/**
 * PureStateMachineApp
 * Created by matt on 4/26/18.
 */

data class State(val numbers: List<Int>)

class AgentTests {

    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100)

    @Test
    fun testSyncAdd(): Unit = runBlocking {
        val agent = Agent(State(listOf()))

        for (number in numbers) {
            add(agent, number, AgentConcurrencyType.SYNC)
        }

        agent.fetch { state ->
            state.numbers shouldEqual numbers
        }
    }

    @Test
    fun testAsyncAdd(): Unit = runBlocking {
        val agent = Agent(State(listOf()))

        for (number in numbers) {
            add(agent, number, AgentConcurrencyType.ASYNC)
        }

        agent.fetch { state ->
            state.numbers shouldEqual numbers
        }
    }

    @Test
    fun testAsyncAddAndFetch(): Unit = runBlocking {
        val agent = Agent(State(listOf()))

        for (number in numbers) {
                add(agent, number, AgentConcurrencyType.ASYNC)

                val lastNumber = agent.fetch { state ->
                    state.numbers.lastOrNull() ?: 0
                }
                lastNumber shouldEqual number
        }

        agent.fetch { state ->
            state.numbers shouldEqual numbers
        }
    }

    fun add(agent: Agent<State>, number: Int, concurrencyType: AgentConcurrencyType) {
//        System.out.println("Added number Thread: ${Thread.currentThread().name}")
        agent.update(concurrencyType) { state ->
            val updatedList = state.numbers + number
//            System.out.println("Added to queue: ${updatedList} Thread: ${Thread.currentThread().name}")
            return@update state.copy(numbers = updatedList)
        }
    }
}