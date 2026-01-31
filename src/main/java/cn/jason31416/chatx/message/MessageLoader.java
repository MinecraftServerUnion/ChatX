package cn.jason31416.chatx.message;

import cn.jason31416.chatx.ChatX;
import cn.jason31416.chatx.util.Config;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageLoader {
    public static MessageLoader instance;
    public Map<String, Object> messageConfig;

    public static void initialize(){
        if(!new File(ChatX.getDataDirectory(), "lang").exists()){
            String[] langs = {"zh-CN", "en-US"};
            new File(ChatX.getDataDirectory(), "lang").mkdir();
            for(String i: langs) {
                try (InputStream inputStream = ChatX.class.getClassLoader().getResourceAsStream("lang/" + i + ".yml"); OutputStream outputStream = new FileOutputStream(new File(ChatX.getDataDirectory(), "lang/" + i + ".yml"))) {
                    outputStream.write(Objects.requireNonNull(inputStream).readAllBytes());
                } catch (Exception e) {
                    throw new RuntimeException("Cannot save language file: " + e);
                }
            }
        }

        File lang = new File(ChatX.getDataDirectory(), "lang/"+ Config.getString("lang")+".yml");

        if(!lang.exists()){
            throw new RuntimeException("Language file for "+Config.getString("lang")+" not found!");
        }
        new MessageLoader(lang);
    }

    public MessageLoader(File filePath) {
        try (FileInputStream is = new FileInputStream(filePath)){
            this.messageConfig = new Yaml().load(is);
        }catch (Exception ignored){
            throw new RuntimeException("Failed to load message config file!");
        }
        instance = this;
    }
    public static List<String> getList(String key) {
        try {
            if (key.contains(".")) {
                String[] keys = key.split("\\.");
                Object value = instance.messageConfig.get(keys[0]);
                for (int i = 1; i < keys.length; i++) {
                    value = ((Map<String, Object>) value).get(keys[i]);
                }
                return (List<String>) value;
            } else {
                return (List<String>) instance.messageConfig.get(key);
            }
        } catch (Exception e) {
            return List.of();
        }
    }
    public static String get(String key, String def) {
        try {
            if (key.contains(".")) {
                String[] keys = key.split("\\.");
                Object value = instance.messageConfig.get(keys[0]);
                for (int i = 1; i < keys.length; i++) {
                    value = ((Map<String, Object>) value).get(keys[i]);
                }
                return value.toString();
            } else {
                return instance.messageConfig.get(key).toString();
            }
        } catch (Exception e) {
            return def;
        }
    }
    public static Message getMessage(String key) {
        return new Message(get(key, "<red>Error: message "+key+" not found, please contact admin!"));
    }
    public static Message getMessage(String key, String defaultMessage) {
        return new Message(get(key, defaultMessage));
    }
}
