package kim.bifrost.rain.arathoth.internal.database.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.internal.database.Correction
import kim.bifrost.rain.arathoth.internal.database.Database
import taboolib.common.platform.function.getDataFolder
import taboolib.module.database.*
import java.io.File
import javax.sql.DataSource

/**
 * kim.bifrost.rain.arathoth.internal.database.impl.DatabaseSQLite
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/21 20:29
 **/
object DatabaseSQLite : Database {

    private val host = File(getDataFolder(), "data.db").getHost()

    private val gson = GsonBuilder().create()

    private val table by lazy {
        Table(Arathoth.conf.getString("database.table.correction", "arathoth_correction")!!, host) {
            add("id") {
                type(ColumnTypeSQLite.INTEGER) {
                    options(
                        ColumnOptionSQLite.PRIMARY_KEY,
                        ColumnOptionSQLite.AUTOINCREMENT,
                        ColumnOptionSQLite.NOTNULL,
                        ColumnOptionSQLite.UNIQUE
                    )
                }
            }
            add("player") {
                type(ColumnTypeSQLite.TEXT) {
                    options(ColumnOptionSQLite.NOTNULL)
                }
            }
            add("attrNode") {
                type(ColumnTypeSQLite.TEXT) {
                    options(ColumnOptionSQLite.NOTNULL)
                }
            }
            add("data") {
                type(ColumnTypeSQLite.TEXT) {
                    options(ColumnOptionSQLite.NOTNULL)
                }
            }
            add("expire") {
                type(ColumnTypeSQLite.INTEGER) {
                    options(ColumnOptionSQLite.NOTNULL)
                }
            }
        }.also { it.workspace(dataSource) { createTable() }.run() }
    }

    private val dataSource: DataSource by lazy {
        host.createDataSource()
    }

    override fun insert(data: Correction) {
        table.workspace(dataSource) {
            insert("player", "attrNode", "data", "expire") {
                value(data.player, data.attrNode, gson.toJson(data.data), data.expire)
            }
        }.run()
    }

    override fun query(player: String): List<Correction> {
        return table.select(dataSource) {
            where { "player" eq player }
        }.map {
            Correction(
                player = getString("player"),
                attrNode = getString("attrNode"),
                data = gson.fromJson(getString("data"), AttributeData::class.java),
                expire = getLong("expire")
            )
        }
    }

    override fun removeExpired() {
        table.workspace(dataSource) {
            delete {
                where { "expire" lt System.currentTimeMillis() }
            }
        }.run()
    }

    override fun remove(id: Int) {
        table.workspace(dataSource) {
            delete {
                where { "id" eq id }
            }
        }.run()
    }
}