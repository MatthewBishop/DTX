package dtx.rs_tables

import dtx.core.*
import dtx.table.Table
import kotlin.random.Random

public data class RSWeightEntry<T, R>(
    val rangeStart: Int,
    val rangeEnd: Int,
    val rollable: Rollable<T, R>
): Rollable<T, R> by rollable {
    public infix fun checkWeight(value: Int): Boolean = value in rangeStart .. rangeEnd
}

public class RSWeightedTable<T, R>(
    public val tableIdentifier: String,
    entries: List<RSWeightRollable<T, R>>,
): Table<T, R> {

    override val ignoreModifier: Boolean = true

    public override val tableEntries: List<RSWeightEntry<T, R>> = buildList {
        var total = 0
        entries.map {
            val upper = total + it.weight
            val entry = RSWeightEntry(total, upper, it)
            total = upper
            add(entry)
        }
    }

    private val maxRoll = (tableEntries.maxOfOrNull { it.rangeEnd } ?: 0) + 1

    override fun roll(target: T, otherArgs: ArgMap): RollResult<R> {
        if (tableEntries.isEmpty()) {
            return RollResult.Nothing()
        }
        if (tableEntries.size == 1) {
            return tableEntries.first().roll(target, otherArgs)
        }
        val roll = Random.nextInt(0, maxRoll)
        tableEntries.forEach {
            if (it checkWeight roll) {
                return it.roll(target, otherArgs)
            }
        }
        return RollResult.Nothing()
    }
}

public class RSWeightedTableBuilder<T, R> {
    public var tableIdentifier: String = ""
    private val tableEntries = mutableListOf<RSWeightRollable<T, R>>()
    public var ignoreModifier: Boolean = true

    public fun identifier(newIdentifier: String): RSWeightedTableBuilder<T, R> {
        tableIdentifier = newIdentifier
        return this
    }

    public fun ignoreModifier(newIgnore: Boolean): RSWeightedTableBuilder<T, R> {
        ignoreModifier = newIgnore
        return this
    }

    public fun addEntry(entry: RSWeightRollable<T, R>): RSWeightedTableBuilder<T, R> {
        tableEntries.add(entry)
        return this
    }

    public infix fun Int.weight(entry: Rollable<T, R>): RSWeightedTableBuilder<T, R> = addEntry(RSWeightRollable(this, entry))

    public infix fun Int.build(builder: SingleRollableBuilder<T, R>.() -> Unit): RSWeightedTableBuilder<T, R> = weight(singleRollable(builder))

    public infix fun Int.weight(item: R): RSWeightedTableBuilder<T, R> = weight(Rollable.Single(item))

    public fun build(): RSWeightedTable<T, R> = RSWeightedTable(tableIdentifier, tableEntries)
}