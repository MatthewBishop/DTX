package dtx.example.rs_tables
import dtx.example.Item
import dtx.example.Player
import dtx.rs_tables.*

fun rsDropTable(
    identifier: String,
    guaranteed: RSGuaranteedTable<Player, Item> = RSGuaranteedTable_Empty,
    preRoll: RSPreRollTable<Player, Item> = RSPreRollTable_Empty,
    mainTable: RSWeightedTable<Player, Item> = RSWeightedTable_Empty,
    tertiaries: RSPreRollTable<Player, Item> = RSPreRollTable_Empty
): RSDropTable<Player, Item> {
    return RSDropTable(
        identifier = identifier,
        guaranteed = guaranteed,
        preRoll = preRoll,
        mainTable = mainTable,
        tertiaries = tertiaries
    )
}

val RSWeightedTable_Empty = RSWeightedTable<Player, Item>("", emptyList())
val RSPreRollTable_Empty = RSPreRollTable<Player, Item>("", emptyList())
val RSGuaranteedTable_Empty = RSGuaranteedTable<Player, Item>("", emptyList())

fun rsWeightedTable(builder: RSWeightedTableBuilder<Player, Item>.() -> Unit): RSWeightedTable<Player, Item> = RSWeightedTableBuilder<Player, Item>().apply(builder).build()

fun rsPrerollTable(builder: RSPrerollTableBuilder<Player, Item>.() -> Unit): RSPreRollTable<Player, Item> {
    val builder = RSPrerollTableBuilder<Player, Item>()
    builder.builder()
    return builder.build()
}

fun rsTertiaryTable(builder: RSPrerollTableBuilder<Player, Item>.() -> Unit): RSPreRollTable<Player, Item> = rsPrerollTable(builder)

fun rsGuaranteedTable(builder: RSGuaranteedTableBuilder<Player, Item>.() -> Unit): RSGuaranteedTable<Player, Item> {
    val builder = RSGuaranteedTableBuilder<Player, Item>()
    builder.builder()
    return builder.build()
}
