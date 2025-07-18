package dtx.impl

import dtx.core.ArgMap
import dtx.core.ResultSelector
import dtx.core.Rollable
import dtx.core.SingleRollableBuilder
import dtx.core.RollResult
import dtx.core.Rollable.Companion.defaultOnSelect
import dtx.core.ShouldRoll
import dtx.core.defaultShouldRoll
import dtx.core.singleRollable
import dtx.table.AbstractTableBuilder
import dtx.table.Table

internal class WrappingInt(
    var currentValue: Int,
    val wrapFloor: Int,
    val wrapCeil: Int,
    val onWrapFunc: () -> Unit,
) {


    init {
        currentValue = currentValue.coerceIn(wrapFloor, wrapCeil)
    }


    fun onWrap() {
        onWrapFunc()
    }


    fun inc() {

        currentValue += 1

        if (currentValue > wrapCeil) {
            onWrapFunc()
            currentValue = wrapFloor
        }
    }
}


public class SequentialTable<T, R>(
    public val tableName: String,
    public override val tableEntries: List<Rollable<T, R>>,
    private val shouldRollFunc: ShouldRoll<T> = ::defaultShouldRoll,
    private val onSelectFunc: (T, RollResult<R>) -> Unit = ::defaultOnSelect,
    private val onTableReset: SequentialTable<T, R>.() -> Unit = { }
): Table<T, R> {

    private var isActive: Boolean = true

    private val pointer = WrappingInt(0, 0, tableEntries.size) {
        this.onTableReset()
    }

    public override fun shouldRoll(target: T): Boolean {
        return shouldRollFunc(target)
    }

    public override fun onSelect(target: T, result: RollResult<R>) {
        this.onSelectFunc(target, result)
    }

    public override fun roll(target: T, otherArgs: ArgMap): RollResult<R> {

        if (!shouldRoll(target)) {
            return RollResult.Nothing()
        }

        if (this.isActive) {

            val selected = this.tableEntries[this.pointer.currentValue]
            this.pointer.currentValue.inc()
            val result = selected.roll(target, otherArgs)
            this.onSelect(target, result)

            return result
        }

        return RollResult.Nothing()
    }
}


public class SequentialTableBuilder<T, R>: AbstractTableBuilder<T, R, SequentialTable<T, R>, Rollable<T, R>, SequentialTableBuilder<T, R>>() {

    override val entries: MutableList<Rollable<T, R>> = mutableListOf()

    public var onResetFunc: SequentialTable<T, R>.() -> Unit = { }

    public fun add(vararg rollables: Rollable<T, R>): SequentialTableBuilder<T, R>  {

        rollables.forEach { addEntry(it) }

        return this
    }

    public fun add(vararg values: R): SequentialTableBuilder<T, R> {

        values.forEach { singleValue ->
            add(Rollable.Single(singleValue))
        }

        return this
    }

    public fun addBy(resultSelector: ResultSelector<T, R>): SequentialTableBuilder<T, R> {
        return add(Rollable.SingleByFun(resultSelector))
    }

    public fun add(block: SingleRollableBuilder<T, R>.() -> Unit): SequentialTableBuilder<T, R> {
        return add(singleRollable(block))
    }


    public override fun build(): SequentialTable<T, R> {
        return SequentialTable(tableName, entries, shouldRollFunc, onSelectFunc, onResetFunc)
    }
}

public fun <T, R> sequentialTable(
    tableName: String = "Unnamed Sequential Table",
    block: SequentialTableBuilder<T, R>.() -> Unit
): SequentialTable<T, R> {

    val builder = SequentialTableBuilder<T, R>()
    builder.apply { name(tableName) }
    builder.block()

    return builder.build()

}