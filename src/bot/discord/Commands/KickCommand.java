package bot.discord.Commands;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class KickCommand implements Commands {
    @Override
    public boolean validate(MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws InterruptedException {
        if(args.length == 0 || event.getMessage().getMentionedUsers().size() == 0){
            return;
        }

        List<User> users = event.getMessage().getMentionedUsers();
        int days = 0;

        try {
            days = Integer.parseInt(args[0]);
        } catch (Exception e){
            return;
        }

        for (User u : users){
            event.getGuild().getController().kick(u.getId(), "Kicked by admin");
        }

    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public void execute(boolean success, MessageReceivedEvent event) {

    }
}
