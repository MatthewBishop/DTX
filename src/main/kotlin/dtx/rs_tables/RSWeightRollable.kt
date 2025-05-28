package dtx.rs_tables

import dtx.core.RollResult
import dtx.core.Rollable
import kotlin.random.Random

public class RSWeightRollable<T, R>(
    public val weight: Int,
    public val rollable: Rollable<T, R>
): Rollable<T, R> by rollable {

    public fun rsRoll(target: T): RollResult<R> {
        val rolled = Random.nextInt(0, weight)
        if (rolled == 0) {
            return rollable.roll(target)
        }
        return RollResult.Nothing()
    }
}
