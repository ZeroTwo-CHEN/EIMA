package top.remake.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * 用来存放controller的全局类
 * 用于在其他地方获取controller中的组件
 *
 * @author ZeroTwo_CHEN
 */
public class ControllerMap {
    private static final Map<String, Object> CONTROLLER_MAP = new HashMap<>();

    private ControllerMap() {
    }

    public static void addController(Object controller) {
        CONTROLLER_MAP.put(controller.getClass().getName(), controller);
    }

    public static Object getController(Class<?> controllerName) {
        return CONTROLLER_MAP.get(controllerName.getName());
    }
}
