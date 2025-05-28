package dtx.rs_tables

import dtx.core.*
import dtx.table.Table

public class RSGuaranteedTable<T, R>(
    public val tableIdentifier: String,
    public override val tableEntries: Collection<Rollable<T, R>>,
    public override val ignoreModifier: Boolean = true,
): Table<T, R> {
    override fun roll(target: T, otherArgs: ArgMap): RollResult<R> {
        if (tableEntries.isEmpty()) {
            return RollResult.Nothing()
        }

        val rollResults = mutableListOf<RollResult<R>>()
        tableEntries.forEach { tableEntry ->
            rollResults.add(tableEntry.roll(target, otherArgs))
        }
        return rollResults.flattenToList()
    }
}

public class RSGuaranteedTableBuilder<T, R> {
    public var tableIdentifier: String = ""
    private val tableEntries = mutableListOf<Rollable<T, R>>()
    public var ignoreModifier: Boolean = false

    public fun identifier(identifier: String): RSGuaranteedTableBuilder<T, R> {
        tableIdentifier = identifier
        return this
    }

    public fun add(rollable: Rollable<T, R>): RSGuaranteedTableBuilder<T, R> {
        tableEntries.add(rollable)
        return this
    }

    public fun add(block: SingleRollableBuilder<T, R>.() -> Unit): RSGuaranteedTableBuilder<T, R> {
        val rollable = singleRollable(block)
        tableEntries.add(rollable)
        return this
    }

    public fun add(item: R): RSGuaranteedTableBuilder<T, R> {
        add(Rollable.Single(item))
        return this
    }

    public fun build(): RSGuaranteedTable<T, R> = RSGuaranteedTable(
        tableIdentifier = tableIdentifier,
        tableEntries = tableEntries,
        ignoreModifier = ignoreModifier
    )
}