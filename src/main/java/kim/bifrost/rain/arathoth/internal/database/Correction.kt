package kim.bifrost.rain.arathoth.internal.database

import kim.bifrost.rain.arathoth.api.data.AttributeData

/**
 * kim.bifrost.rain.arathoth.internal.database.Correction
 * Arathoth
 * 补查值
 *
 * @author 寒雨
 * @since 2022/3/21 20:10
 **/
data class Correction(
    val id: Int = 0,
    val player: String,
    val attrNode: String,
    val data: AttributeData,
    val expire: Long
    ) {
    fun expired() = System.currentTimeMillis() > expire
}