package kim.bifrost.rain.arathoth.api.data

/**
 * kim.bifrost.rain.arathoth.api.data.NumberAttributeData
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/18 17:31
 **/
data class NumberAttributeData(
    val range: List<Double>,
    val percent: Double
) {
    fun append(data: NumberAttributeData): NumberAttributeData {
        return NumberAttributeData(
            range.mapIndexed { index, d -> d + data.range[index] },
            percent + data.percent
        )
    }
}
