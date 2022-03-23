// 全局变量
// config中注册的静态类/单例
// manager - js属性管理器
// manager.getConfig(): FileConfiguration - 获取属性配置
// manager.getStatus(entity, attrNode): AttributeData - 获取属性数据

// 处理器类型: 对应函数名称
var handlers = {
    "event:org.bukkit.event.entity.EntityDamageByEntityEvent": "event",
    // timer:{time},{async}
    "timer:40,true": "timer"
}

function event(event) {
    // var entity = event.getEntity();
    // var data = manager.getStatus(entity, "js:example");
    // event.setDamage(event.getDamage() + data.generateValue());
}

function timer() {
    // 每两秒执行一次
}

// 初始化配置文件
function setUpConfig(config) {
    config.set("patterns", "[VALUE] example")
}

