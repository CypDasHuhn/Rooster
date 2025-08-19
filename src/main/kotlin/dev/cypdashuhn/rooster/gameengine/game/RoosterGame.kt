package dev.cypdashuhn.rooster.gameengine.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinInstant
import java.util.*
import kotlin.time.Duration

abstract class RoosterGame<ParticipantStateType> {
    private val participantGroups: Map<ParticipantGroup, MutableList<Participant<ParticipantStateType>>>

    private val phases: MutableMap<Phase, PhaseState>

    constructor(
        phases: PhaseList,
        teams: List<ParticipantGroup>
    ) {
        this.phases = phases.phases.associateWith { PhaseState.NotStarted as PhaseState }.toMutableMap()
        this.participantGroups = teams.associateWith { mutableListOf() }

        startByRoosterPhase(RoosterPhase.CREATING)

        // Creating -> Prepared
        step(::onCreate, phaseChanger = {})
    }

    //region Phases
    private fun byRoosterPhase(roosterPhase: RoosterPhase) = phases.entries.first { it.key isRoosterPhase roosterPhase }

    private fun closePhase(phase: Phase) {
        phases[phase] = PhaseState.Completed(
            (phases[phase]!! as PhaseState.Ongoing).startedAt,
            currentTime()
        )
    }

    private fun startByRoosterPhase(roosterPhase: RoosterPhase) {
        phases[byRoosterPhase(roosterPhase).key] = PhaseState.Ongoing(currentTime())
    }

    open fun duration(): Duration {
        val creatingPhaseState = byRoosterPhase(RoosterPhase.STARTING).value
        if (creatingPhaseState !is PhaseState.Completed) return Duration.ZERO
        return currentTime() - creatingPhaseState.completedAt
    }

    // TODO: Handle Phase Errors better

    fun open() {
        // Prepared -> Queue
        step(::onOpen, followUp = {})
    }

    fun start() {
        if (byRoosterPhase(RoosterPhase.QUEUE).value !is PhaseState.Ongoing) throw IllegalStateException("Queue phase is not ongoing")

        // Queue -> Starting -> [First Post-Starting Phase]
        step(::onStart)
    }

    fun end() {
        if (byRoosterPhase(RoosterPhase.STARTING).value !is PhaseState.Completed) throw IllegalStateException("Starting phase is not completed")

        // [currentPhase] -> [All Main Phases] -> Ending -> End
        step(::onEnd, {
            val currentPhase = phases.entries.withIndex().first { it.value.value is PhaseState.Ongoing }
            val closingPhase = phases.entries.withIndex().first { it.value.key isRoosterPhase RoosterPhase.CLOSING }

            closePhase(currentPhase.value.key)

            phases.entries.withIndex()
                .filter { it.index > currentPhase.index && it.index < closingPhase.index }
                .map { it.value }
                .forEach {
                    phases[it.key] = PhaseState.Completed(currentTime(), currentTime())
                }

            phases[closingPhase.value.key] = PhaseState.Ongoing(currentTime())
        })
    }

    fun close() {
        if (byRoosterPhase(RoosterPhase.END).value !is PhaseState.Ongoing) throw IllegalStateException("Ending phase is not completed")

        // End -> Closing -> Closed
        step(::onClose)
    }

    private fun step(
        block: suspend () -> Unit,
        phaseChanger: () -> Unit = ::nextPhase,
        followUp: suspend () -> Unit = ::nextPhase
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            phaseChanger()
            block()
            followUp()
        }
    }

    fun nextPhase() {
        val (currentPhaseIdx, currentPhase) = phases.entries.withIndex().first { it.value.value is PhaseState.Ongoing }
        val nextPhase = phases.entries.withIndex().first { it.index == currentPhaseIdx + 1 }.value

        phases[currentPhase.key] =
            PhaseState.Completed((currentPhase.value as PhaseState.Ongoing).startedAt, currentTime())
        phases[nextPhase.key] = PhaseState.Ongoing(currentTime())
    }

    abstract suspend fun onCreate()

    abstract suspend fun onOpen()

    abstract suspend fun onStart()

    abstract suspend fun onEnd()

    abstract suspend fun onClose()
    //endregion

    fun addParticipant(participant: Participant<ParticipantStateType>) {
        val participantGroup = participantGroups.keys.firstOrNull { it.name == participant.groupName }
        if (participantGroup == null) {
            throw IllegalArgumentException("Participant is not part of a valid team")
        }

        participantGroups[participantGroup]!!.add(participant)
    }

    // TODO: Add BukkitEventHandler invoking this if a participant leaves the server
    fun removeParticipant(participant: Participant<ParticipantStateType>) {
        val participantGroup = participantGroups.keys.firstOrNull { it.name == participant.groupName }
        if (participantGroup == null) {
            throw IllegalArgumentException("Participant is not part of a valid team")
        }

        participantGroups[participantGroup]!!.remove(participant)
    }
}

fun currentTime() = Calendar.getInstance().time.toInstant().toKotlinInstant()