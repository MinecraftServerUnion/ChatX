package dev.onelili.unichat.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;
import dev.onelili.unichat.velocity.UniChat;
import dev.onelili.unichat.velocity.message.Message;

import java.util.List;

public class UniChatCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        if(invocation.arguments().length == 0 || invocation.arguments()[0].equals("version")){
            invocation.source().sendMessage(new Message("<#47BFFB>UniChat v"+ UniChat.getProxy().getPluginManager().getPlugin("unichat").get().getDescription().getVersion()
                + " by Jason31416 & onelili").toComponent());
            return;
        }
        switch (invocation.arguments()[0]){
            case "reload" -> {
                if(invocation.source().hasPermission("unichat.admin")){
                    UniChat.reload();
                    invocation.source().sendMessage(new Message("<#47BFFB>UniChat has been reloaded.").toComponent());
                }
            }
            default -> {
                invocation.source().sendMessage(new Message("&cUnknown unichat command.").toComponent());
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
