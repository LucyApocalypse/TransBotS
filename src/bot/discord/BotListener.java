package bot.discord;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class BotListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if(event.getMessage().getEmbeds().size() != 0){
            List<MessageEmbed> messageEmbed = event.getMessage().getEmbeds();

            if (messageEmbed.get(0).getTitle() == null || !event.getMessage().getAuthor().equals(event.getJDA().getSelfUser())){
                return;
            }

            if(messageEmbed.get(0).getTitle().contains("VOTE") && event.getMessage().getAuthor().equals(event.getJDA().getSelfUser())){
                //Guild guild = event.getGuild();
                // List<Emote> emotes =

                /*for(Emote e : emotes){
                    System.out.println(e.getName() + "   " + e.getId());
                }*/
            }
            return;
        }

        String e = event.getMessage().getContent();
        StringBuilder builder = new StringBuilder();
        String[] es = e.split(" ");
        for (String s : es){
            if(s.toLowerCase().startsWith("http://")){
                s = s.substring(7);
            } else if(s.toLowerCase().startsWith("https://")){
                s = s.substring(8);
            }
            builder.append(s);
            builder.append(" ");
        }
        e = builder.toString().trim();

        if (event.getMessage().getAuthor().isBot()){
            return;
        }
        if(e.startsWith("!") && event.getAuthor() != event.getJDA().getSelfUser()){
            try {
                Main.handleCommand(Main.parser.parse(e, event));
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

    }
}
