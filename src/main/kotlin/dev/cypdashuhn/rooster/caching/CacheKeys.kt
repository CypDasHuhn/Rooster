package dev.cypdashuhn.rooster.caching

import java.util.*


sealed class FieldPlan(
    open val index: Int,
    open var bitWidth: Int
) {
    class Single(override val index: Int, override var bitWidth: Int, val transform: (Any) -> Long) :
        FieldPlan(index, bitWidth)

    class Array(override val index: Int, override var bitWidth: Int, val transform: (Any) -> LongArray) :
        FieldPlan(index, bitWidth)

    companion object {
        var hashOption = HashOption.DEFAULT

        fun fromValue(index: Int, value: Any): FieldPlan {
            return when (value) {
                is Boolean -> Single(index, 1) { if (value) 1L else 0L }
                is Int -> Single(index, 32) { value.toLong() }
                is Char -> Single(index, 16) { value.code.toLong() }
                is Short -> Single(index, 16) { value.toLong() }
                is Byte -> Single(index, 8) { value.toLong() }
                is Long if (hashOption.size >= 64) -> Single(index, 64) { value }
                is Float -> Single(index, 32) { value.toLong() }
                is Double if (hashOption.size >= 64) -> Single(index, 64) { value.toBits() }
                is Enum<*> -> Single(
                    index,
                    value::class.java.enumConstants.size
                ) { (value as Enum<*>).ordinal.toLong() }

                is UUID if (hashOption.size >= 128) -> Array(index, 128) {
                    longArrayOf(value.mostSignificantBits, value.leastSignificantBits)
                }

                else if (hashOption.size <= 64) -> Single(index, hashOption.size) {
                    hashOption.methodSingle!!.invoke(
                        value
                    )
                }

                else -> Array(index, hashOption.size) { hashOption.methodArr!!.invoke(value) }
            }
        }
    }
}

fun getPlan(values: Array<Any>): Array<FieldPlan> {
    val plans = values
        .withIndex()
        .map { FieldPlan.fromValue(it.index, it.value) } // mutable copy if needed

    val totalBits = plans.sumOf { it.bitWidth }
    var overflow = totalBits % 64
    if (overflow == 0) return plans.sortedBy { it.bitWidth }.toTypedArray()

    val maxCuts = plans.associateWith { it.bitWidth / 32 }
    val cuts = mutableMapOf<FieldPlan, Int>().withDefault { 0 }

    while (overflow > 0) {
        var didCut = false
        for (plan in plans) {
            val currentCut = cuts.getValue(plan)
            val maxCut = maxCuts.getValue(plan)

            if (currentCut < maxCut) {
                plan.bitWidth -= 1
                cuts[plan] = currentCut + 1
                overflow -= 1
                didCut = true
                if (overflow == 0) break
            }
        }
        if (!didCut) break
    }

    return plans.sortedBy { it.bitWidth }.toTypedArray()
}


fun pack(values: Array<Any>, plans: Array<FieldPlan>): LongArray {
    val totalBits = plans.sumOf { it.bitWidth }
    val longsNeeded = (totalBits + 63) / 64
    val result = LongArray(longsNeeded)

    var bitIndex = 0

    for (plan in plans) {
        val value = values[plan.index]

        when (plan) {
            is FieldPlan.Single -> {
                val raw = plan.transform(value) and ((1L shl plan.bitWidth) - 1)
                val targetIndex = bitIndex / 64
                val offset = bitIndex % 64
                val bitsLeft = 64 - offset

                if (plan.bitWidth <= bitsLeft) {
                    result[targetIndex] = result[targetIndex] or (raw shl (bitsLeft - plan.bitWidth))
                } else {
                    val upper = raw ushr (plan.bitWidth - bitsLeft)
                    val lower = raw and ((1L shl (plan.bitWidth - bitsLeft)) - 1)

                    result[targetIndex] = result[targetIndex] or upper
                    result[targetIndex + 1] = result[targetIndex + 1] or (lower shl (64 - (plan.bitWidth - bitsLeft)))
                }

                bitIndex += plan.bitWidth
            }

            is FieldPlan.Array -> {
                val array = plan.transform(value)
                var bitsRemaining = plan.bitWidth
                var i = 0

                while (bitsRemaining > 0) {
                    val take = minOf(64, bitsRemaining)
                    val raw = array[i] and ((1L shl take) - 1)

                    val targetIndex = bitIndex / 64
                    val offset = bitIndex % 64
                    val bitsLeft = 64 - offset

                    if (take <= bitsLeft) {
                        result[targetIndex] = result[targetIndex] or (raw shl (bitsLeft - take))
                    } else {
                        val upper = raw ushr (take - bitsLeft)
                        val lower = raw and ((1L shl (take - bitsLeft)) - 1)

                        result[targetIndex] = result[targetIndex] or upper
                        result[targetIndex + 1] = result[targetIndex + 1] or (lower shl (64 - (take - bitsLeft)))
                    }

                    bitIndex += take
                    bitsRemaining -= take
                    i++
                }
            }
        }
    }

    return result
}


enum class HashOption(
    val size: Int,
    val methodArr: ((Any) -> LongArray)? = null,
    val methodSingle: ((Any) -> Long)? = null
) {
    DEFAULT(32, methodSingle = { it.hashCode().toLong() }),
}