package bot.discord;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getContent().startsWith("-!") && event.getAuthor() != event.getJDA().getSelfUser()){
            try {
                Main.handleCommand(Main.parser.parse(event.getMessage().getContent(), event));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        Guild[] guilds = event.getJDA().getGuilds().toArray(new Guild[0]);
        for(Guild g : guilds) {
            System.out.println("Server " + g.getName() + " ( " + g.getRegion() + " ) ");
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        for (int i = 0; i < ("Author: " + event.getAuthor().getName() + " (" + event.getAuthor().getId() + ")").length(); i++){
            System.out.print("*");
        }
        System.out.println();
        System.out.println("Private Message Received:");
        System.out.println("Author: " + event.getAuthor().getName() + " (" + event.getAuthor().getId() + ")");
        System.out.println("Message: " + event.getMessage().getContent());
        for (int i = 0; i < ("Author: " + event.getAuthor().getName() + " (" + event.getAuthor().getId() + ")").length(); i++){
            System.out.print("*");
        }
        System.out.println();
        event.getMessage().getAuthor().openPrivateChannel().complete().sendMessage("Sorry! I can't answer you!");
    }


}
