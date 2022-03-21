package kim.bifrost.rain.arathoth.internal.database

import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.internal.database.impl.DatabaseMySQL
import kim.bifrost.rain.arathoth.internal.database.impl.DatabaseSQLite

/**
 * kim.bifrost.rain.arathoth.internal.database.Database
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/21 19:36
 **/
interface Database {
    /**
     * 插入一条数据
     *
     * @param data
     */
    fun insert(data: Correction)

    /**
     * 查询某玩家的补查数据
     *
     * @param player 玩家
     * @return
     */
    fun query(player: String): List<Correction>

    /**
     * 删除表中所有过期补查数据
     */
    fun removeExpired()

    /**
     * 根据id删除一条数据
     *
     * @param id
     */
    fun remove(id: Int)

    companion object : Database by Arathoth.database {

    }
}