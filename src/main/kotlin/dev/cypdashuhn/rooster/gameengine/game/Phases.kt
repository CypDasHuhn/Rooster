package dev.cypdashuhn.rooster.gameengine.game

import kotlinx.datetime.Instant

enum class PhaseStatus {
    COMPLETED,
    ON_GOING,
    NOT_STARTED
}

sealed class PhaseState(
    val status: PhaseStatus
) {
    data class Completed(
        val startedAt: Instant,
        val completedAt: Instant
    ) : PhaseState(PhaseStatus.COMPLETED)

    data class Ongoing(
        val startedAt: Instant
    ) : PhaseState(PhaseStatus.ON_GOING)

    object NotStarted : PhaseState(PhaseStatus.NOT_STARTED)
}

enum class RoosterPhase(
    val phaseName: String,
    val processing: Boolean,
    val preMain: Boolean,
    val playersAllowed: Boolean
) {
    // Pre-Queue
    CREATING("Creating", true, true, false),
    PREPARED("Prepared", false, true, false),

    // Queue
    QUEUE("Queue", false, true, true),
    STARTING("Starting", true, true, true),

    // Post Main
    ENDING("Ending", true, false, true),
    END("End", false, false, true),

    // Closing
    CLOSING("Closing", true, false, false),
    CLOSED("Closed", false, false, false);

    fun toPhase() = Phase(phaseName, roosterPhase = true)
    infix fun isPhase(phase: Phase) = phase.name == phaseName
}

class Phase {
    val name: String
    val roosterPhase: Boolean

    internal constructor(
        name: String,
        roosterPhase: Boolean
    ) {
        this.name = name
        this.roosterPhase = roosterPhase
    }

    constructor(name: String) {
        this.name = name
        this.roosterPhase = false
    }

    companion object {
        private const val MAIN_PHASE_NAME = "Main"

        fun default() = custom(listOf(MAIN_PHASE_NAME))
        fun custom(phases: List<String>): PhaseList {
            val preMain = RoosterPhase.entries.filter { it.preMain }.map { it.toPhase() }
            val postMain = RoosterPhase.entries.filter { !it.preMain }.map { it.toPhase() }

            return PhaseList(preMain + (phases.map { Phase(it) }) + postMain)
        }
    }

    infix fun isRoosterPhase(roosterPhase: RoosterPhase) = roosterPhase isPhase this
}

class PhaseList internal constructor(
    val phases: List<Phase>
)