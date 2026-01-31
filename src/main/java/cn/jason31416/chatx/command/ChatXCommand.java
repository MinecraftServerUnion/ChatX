package cn.jason31416.chatx.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import cn.jason31416.chatx.ChatX;
import cn.jason31416.chatx.gui.GUIContainer;
import cn.jason31416.chatx.gui.GUIData;
import cn.jason31416.chatx.message.Message;
import cn.jason31416.chatx.module.ShowItemModule;

import java.util.List;
import java.util.UUID;

public class ChatXCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        if(invocation.arguments().length == 0 || invocation.arguments()[0].equals("version")){
            invocation.source().sendMessage(new Message("<#47BFFB>ChatX v"+ ChatX.getProxy().getPluginManager().getPlugin("chatx").get().getDescription().getVersion().get()
                + " by onelili & Jason31416").toComponent());
            return;
        }
        switch (invocation.arguments()[0]){
            case "reload" -> {
                if(invocation.source().hasPermission("chatx.admin")){
                    ChatX.reload();
                    invocation.source().sendMessage(new Message("<#47BFFB>ChatX has been reloaded.").toComponent());
                }
            }
            case "item" -> {
                if(invocation.arguments().length == 2) {
                    UUID uuid = UUID.fromString(invocation.arguments()[1]);
                    if(ShowItemModule.getGuiMap().containsKey(uuid) && invocation.source() instanceof Player player) {
                        GUIData data = ShowItemModule.getGuiMap().get(uuid);
                        GUIContainer container = GUIContainer.of(data);
                        container.open(player);
                    }
                }
            }
            default -> {
                invocation.source().sendMessage(new Message("&cUnknown chatx command.").toComponent());
            }
        }
    }
    @Override
    public List<String> suggest(Invocation invocation) {
        if(invocation.arguments().length <= 1){
            return List.of("version", "reload");
        }
        return List.of();
    }
}
