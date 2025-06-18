package dtx.rs_tables

import dtx.core.Rollable
import dtx.impl.WeightedRollable
import dtx.impl.WeightedRollableImpl

public class RSWeightEntry<T, R>(
    public val rangeStart: Int,
    public val rangeEnd: Int,
    rollable: Rollable<T, R>
): WeightedRollable<T, R> by WeightedRollableImpl(
    weight = (rangeEnd - rangeStart).toDouble(),
    rollable
) {
    public infix fun checkWeight(value: Int): Boolean = value in rangeStart ..< rangeEnd
}