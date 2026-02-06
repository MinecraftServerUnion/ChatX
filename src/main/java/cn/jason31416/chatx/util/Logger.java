package cn.jason31416.chatx.util;

import cn.jason31416.chatx.ChatX;

public class Logger {
    public static void info(String message){
        ChatX.getLogger().info(message);
    }
    public static void warn(String message){
        ChatX.getLogger().warn(message);
    }
    public static void error(String message) {
        ChatX.getLogger().error(message);
    }
    public static void debug(String message) {
        if(Config.getBoolean("debug")){
            ChatX.getLogger().info("[DEBUG] "+message);
        }
    }
}
