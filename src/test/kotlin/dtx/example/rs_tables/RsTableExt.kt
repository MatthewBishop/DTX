package dtx.example.rs_tables

import dtx.core.ShouldRoll
import dtx.core.defaultShouldRoll
import dtx.rs_tables.*

public fun <T, R> rsDropTable(
    identifier: String,
    guaranteed: RSGuaranteedTable<T, R> = RSGuaranteedTable.Empty(),
    preRoll: RSPreRollTable<T, R> = RSPreRollTable.Empty(),
    mainTable: RSWeightedTable<T, R> = RSWeightedTable.Empty(),
    tertiaries: RSPreRollTable<T, R> = RSPreRollTable.Empty(),
    shouldRollFunc: ShouldRoll<T> = ::defaultShouldRoll,
): RSDropTable<T, R> {
    return RSDropTable(
        identifier = identifier,
        guaranteed = guaranteed,
        preRoll = preRoll,
        mainTable = mainTable,
        tertiaries = tertiaries,
        shouldRollFunc = shouldRollFunc
    )
}

public fun <T, R> rsGuaranteedTable(block: RSGuaranteedTableBuilder<T, R>.() -> Unit): RSGuaranteedTable<T, R> {

    val builder = RSGuaranteedTableBuilder<T, R>()
    builder.apply(block)

    return builder.build()
}

public inline fun <T, R> rsPrerollTable(block: RSPrerollTableBuilder<T, R>.() -> Unit): RSPreRollTable<T, R> {

    val builder = RSPrerollTableBuilder<T, R>()
    builder.apply(block)

    return builder.build()
}

public inline fun <T, R> rsTertiaryTable(block: RSPrerollTableBuilder<T, R>.() -> Unit): RSPreRollTable<T, R> {
    return rsPrerollTable(block)
}

public fun <T, R> rsWeightedTable(block: RSWeightedTableBuilder<T, R>.() -> Unit): RSWeightedTable<T, R> {

    val builder = RSWeightedTableBuilder<T, R>()
    builder.apply(block)

    return builder.build()
}

