package dtx.rs_tables

import dtx.core.ArgMap
import dtx.core.RollResult
import dtx.core.Rollable
import dtx.core.SingleRollableBuilder
import dtx.core.flattenToList
import dtx.core.singleRollable
import dtx.impl.WeightedRollable
import dtx.impl.WeightedRollableImpl
import dtx.table.Table
import kotlin.collections.forEach
import kotlin.random.Random

public class RSPreRollTable<T, R>(
    public val tableIdentifier: String,
    public override val tableEntries: List<WeightedRollable<T, R>>,
): Table<T, R> {

    override val ignoreModifier: Boolean = true

    override fun roll(target: T, otherArgs: ArgMap): RollResult<R> {
        if (tableEntries.isEmpty()) {
            return RollResult.Nothing()
        }

        val results = mutableListOf<RollResult<R>>()
        tableEntries.forEach { tableEntry ->
            val roll = Random.nextDouble(0.0, 1.0 + Double.MIN_VALUE)
            if (roll <= tableEntry.weight) {
                results.add(tableEntry.roll(target, otherArgs))
            }
        }
        return results.flattenToList()
    }
}

public class RSPrerollTableBuilder<T, R> {
    public var identifier: String = ""
    private val prerollEntries = mutableListOf<WeightedRollable<T, R>>()
    public var ignoreModifier: Boolean = false

    public fun identifier(identifier: String): RSPrerollTableBuilder<T, R> {
        this.identifier = identifier
        return this
    }

    public fun ignoreModifier(ignore: Boolean): RSPrerollTableBuilder<T, R> {
        this.ignoreModifier = ignore
        return this
    }

    public fun addEntry(entry: WeightedRollable<T, R>): RSPrerollTableBuilder<T, R> {
        prerollEntries.add(entry)
        return this
    }

    public infix fun Double.chance(entry: Rollable<T, R>): RSPrerollTableBuilder<T, R> = addEntry(entry = WeightedRollableImpl(this, entry))

    public infix fun Double.chance(builder: SingleRollableBuilder<T, R>.() -> Unit): RSPrerollTableBuilder<T, R> = chance(singleRollable(builder))

    public infix fun Double.chance(item: R): RSPrerollTableBuilder<T, R> = chance(Rollable.Single(item))

    public infix fun Int.weight(entry: Rollable<T, R>): RSPrerollTableBuilder<T, R> = (1 outOf this).chance(entry)

    public infix fun Int.weight(builder: SingleRollableBuilder<T, R>.() -> Unit): RSPrerollTableBuilder<T, R> = weight(singleRollable(builder))

    public infix fun Int.weight(item: R): RSPrerollTableBuilder<T, R> = weight(Rollable.Single(item))

    public fun build(): RSPreRollTable<T, R> = RSPreRollTable(
        identifier,
        prerollEntries
    )
}
