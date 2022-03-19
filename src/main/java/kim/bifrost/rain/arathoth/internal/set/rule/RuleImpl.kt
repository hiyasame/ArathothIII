package kim.bifrost.rain.arathoth.internal.set.rule

import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import java.util.function.Supplier

/**
 * kim.bifrost.rain.arathoth.internal.set.rule.RuleImpl
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 1:28
 **/
annotation class RuleImpl(val key: String)

@Awake
object RuleImplLoader : Injector.Classes {
    override val lifeCycle: LifeCycle
        get() = LifeCycle.ENABLE

    override val priority: Byte
        get() = 0

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {

    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {
        if (clazz.isAnnotationPresent(RuleImpl::class.java) && Rule::class.java.isAssignableFrom(clazz)) {
            val ruleImpl = clazz.getAnnotation(RuleImpl::class.java)
            Rule.registry[ruleImpl.key] = instance.get() as Rule
        }
    }
}
