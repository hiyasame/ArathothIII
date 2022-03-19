package kim.bifrost.rain.arathoth.internal

import kim.bifrost.rain.arathoth.api.ArathothAPI
import kim.bifrost.rain.arathoth.api.LoadStatusParams
import org.bukkit.entity.Entity

/**
 * kim.bifrost.rain.arathoth.internal.ArathothAPI
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 21:25
 **/
internal object ArathothAPIImpl : ArathothAPI {
    override fun <T : Entity> loadStatus(params: LoadStatusParams<T>, entity: T) {
        params.load(entity)
    }
}