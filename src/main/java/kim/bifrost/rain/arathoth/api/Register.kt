package kim.bifrost.rain.arathoth.api

/**
 * kim.bifrost.rain.arathoth.api.Register
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/18 22:59
 **/
@Target(AnnotationTarget.FIELD)
annotation class Register(val name: String = "")
