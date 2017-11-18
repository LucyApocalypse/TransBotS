package bot.discord.Commands;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.List;

public class VoteCommand implements Commands{
    @Override
    public boolean validate(MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) throws InterruptedException {
        if(args.length == 0){
            event.getTextChannel().sendMessage(help().build()).queue();
        }

        StringBuilder builder = new StringBuilder();
        for (String a : args){
            builder.append(a);
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setColor(Color.ORANGE)
                .setTitle("VOTE")
                .addField("Author", event.getMessage().getAuthor().getName(), false)
                .addField("Content", builder.toString(), false);

        Message message = new MessageBuilder().setEmbed(embedBuilder.build()).build();
        event.getTextChannel().sendMessage(message).queue();

        List<Message> m = event.getTextChannel().getHistory().retrievePast(5).complete();



    }

    @Override
    public EmbedBuilder help() {
        return null;
    }

    @Override
    public void execute(boolean success, MessageReceivedEvent event) {

    }
}
