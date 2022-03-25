package kim.bifrost.rain.arathoth.internal

import kim.bifrost.rain.arathoth.api.AttributeKey
import kim.bifrost.rain.arathoth.api.Register
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import java.lang.reflect.Field
import java.util.function.Supplier

/**
 * kim.bifrost.rain.arathoth.internal.RegisterPoster
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/18 23:05
 **/
@Awake
object RegisterPoster : Injector.Fields {

    override val lifeCycle: LifeCycle
        get() = LifeCycle.ACTIVE

    override val priority: Byte
        get() = 0

    override fun inject(field: Field, clazz: Class<*>, instance: Supplier<*>) {
        if (field.isAnnotationPresent(Register::class.java)) {
            val obj = instance.get()
            val key = field.get(obj) as AttributeKey<*>
            key.register()
        }
    }
}