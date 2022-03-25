package kim.bifrost.rain.arathoth.internal.database.impl

import com.google.gson.GsonBuilder
import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.internal.database.Correction
import kim.bifrost.rain.arathoth.internal.database.Database
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import taboolib.module.database.getHost
import javax.sql.DataSource

/**
 * kim.bifrost.rain.arathoth.internal.database.impl.DatabaseMySQL
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/21 20:30
 **/
object DatabaseMySQL : Database {

    private val host by lazy { Arathoth.conf.getHost("database") }

    private val gson = GsonBuilder().create()

    private val table by lazy {
        Table(Arathoth.conf.getString("database.table.correction", "arathoth_correction")!!, host) {
            add("id") {
                type(ColumnTypeSQL.INT) {
                    options(
                        ColumnOptionSQL.PRIMARY_KEY,
                        ColumnOptionSQL.AUTO_INCREMENT,
                        ColumnOptionSQL.NOTNULL,
                        ColumnOptionSQL.UNIQUE_KEY
                    )
                }
            }
            add("player") {
                type(ColumnTypeSQL.TEXT) {
                    options(ColumnOptionSQL.NOTNULL)
                }
            }
            add("attrNode") {
                type(ColumnTypeSQL.TEXT) {
                    options(ColumnOptionSQL.NOTNULL)
                }
            }
            add("data") {
                type(ColumnTypeSQL.TEXT) {
                    options(ColumnOptionSQL.NOTNULL)
                }
            }
            add("expire") {
                type(ColumnTypeSQL.INT) {
                    options(ColumnOptionSQL.NOTNULL)
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