package kim.bifrost.rain.arathoth.internal.set

import kim.bifrost.rain.arathoth.api.data.AttributeData

/**
 * kim.bifrost.rain.arathoth.internal.set.AttributeSet
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 1:13
 **/
class AttributeSet(
    val data: Map<String, AttributeData<*>>
) {

    companion object {
        // path - set
        val registry = mutableMapOf<String, AttributeSet>()
    }
}