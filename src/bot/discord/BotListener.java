package bot.discord;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.RestAction;

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
        StringBuilder out = new StringBuilder("\nThis bot is running on following servers :\n");
        int i = 1;
        for(Guild g :  event.getJDA().getGuilds()){
            out.append(i++).append(")\t").append(g.getName()).append("(").append(g.getId()).append(") \n");
            g.getTextChannels().get(0).sendMessage("Hello").queue();
        }
        System.out.println(out);
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
