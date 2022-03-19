package kim.bifrost.rain.arathoth.api

import org.bukkit.entity.Entity

/**
 * kim.bifrost.rain.arathoth.api.ArathothAPI
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 16:43
 **/
interface ArathothAPI {
    /**
     * 读取实体属性，根据不同类型实体有不同的读取策略
     *
     * @param T
     * @param params 读取策略
     * @param entity 实体
     */
    fun <T: Entity> loadStatus(params: LoadStatusParams<T>, entity: T)
}