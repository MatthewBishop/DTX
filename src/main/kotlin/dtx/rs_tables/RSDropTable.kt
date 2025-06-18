package dtx.rs_tables

import dtx.core.ArgMap
import dtx.core.RollResult
import dtx.core.Rollable
import dtx.core.ShouldRoll
import dtx.core.defaultShouldRoll
import dtx.core.flattenToList
import dtx.table.Table

public data class RSDropTable<T, R>(
    val identifier: String,
    val guaranteed: RSGuaranteedTable<T, R>,
    val preRoll: RSPreRollTable<T, R>,
    val mainTable: RSWeightedTable<T, R>,
    val tertiaries: RSPreRollTable<T, R>,
    private val shouldRollFunc: ShouldRoll<T> = ::defaultShouldRoll,
): Table<T, R> {

    override val tableEntries: Collection<Rollable<T, R>> = emptyList()

    override fun shouldRoll(target: T): Boolean {
        return shouldRollFunc(target)
    }

    override fun roll(target: T, otherArgs: ArgMap): RollResult<R> {

        val results = mutableListOf<RollResult<R>>()
        results.add(guaranteed.roll(target, otherArgs))
        results.add(preRoll.roll(target, otherArgs))
        results.add(mainTable.roll(target, otherArgs))
        results.add(tertiaries.roll(target, otherArgs))

        return results.flattenToList()
    }
}
