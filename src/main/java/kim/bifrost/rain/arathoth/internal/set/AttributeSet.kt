package kim.bifrost.rain.arathoth.internal.set

/**
 * kim.bifrost.rain.arathoth.internal.set.AttributeSet
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 1:13
 **/
class AttributeSet(
    val data: Map<String, Any>
) {

    companion object {
        // path - set
        val registry = mutableMapOf<String, AttributeSet>()
    }
}