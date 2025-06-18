package dtx.rs_tables

import dtx.core.ShouldRoll
import dtx.core.defaultShouldRoll
import dtx.impl.ChanceRollable
import dtx.impl.MultiChanceTable
import dtx.impl.MultiChanceTableBuilder
import dtx.impl.MultiChanceTableImpl
import dtx.impl.Percent

public class RSPreRollTable<T, R>(
    tableIdentifier: String,
    tableEntries: List<ChanceRollable<T, R>>,
    shouldRollFunc: ShouldRoll<T> = ::defaultShouldRoll,
): RSTable<T, R>, MultiChanceTable<T, R> by MultiChanceTableImpl<T, R>(
    tableIdentifier, tableEntries, shouldRollFunc
) {
    public companion object {
        public val EmptyTable: RSPreRollTable<Any?, Any?> = RSPreRollTable<Any?, Any?>("", emptyList()) { false }
        public fun <T, R> Empty(): RSPreRollTable<T, R> = EmptyTable as RSPreRollTable<T, R>
    }
}

public class RSPrerollTableBuilder<T, R>: MultiChanceTableBuilder<T, R>() {

    public infix fun Int.outOf(other: Int): Percent = Percent(toDouble() / other.toDouble())

    override fun build(): RSPreRollTable<T, R> = RSPreRollTable(
        tableName,
        entries
    )
}

public inline fun <T, R> rsPrerollTable(block: RSPrerollTableBuilder<T, R>.() -> Unit): RSPreRollTable<T, R> {

    val builder = RSPrerollTableBuilder<T, R>()
    builder.apply(block)

    return builder.build()
}

public inline fun <T, R> rsTertiaryTable(block: RSPrerollTableBuilder<T, R>.() -> Unit): RSPreRollTable<T, R> {
    return rsPrerollTable(block)
}
