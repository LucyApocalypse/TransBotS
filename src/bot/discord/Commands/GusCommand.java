package bot.discord.Commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class GusCommand implements Commands {
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
        event.getMessage().delete().queue();
        String s = "ЗАПУСКАЕМ\n" +
                "░ГУСЯ░▄▀▀▀▄░РАБОТЯГИ░░\n" +
                "▄███▀░◐░░░▌░░░░░░░\n" +
                "░░░░▌░░░░░▐░░░░░░░\n" +
                "░░░░▐░░░░░▐░░░░░░░\n" +
                "░░░░▌░░░░░▐▄▄░░░░░\n" +
                "░░░░▌░░░░▄▀▒▒▀▀▀▀▄\n" +
                "░░░▐░░░░▐▒▒▒▒▒▒▒▒▀▀▄\n" +
                "░░░▐░░░░▐▄▒▒▒▒▒▒▒▒▒▒▀▄\n" +
                "░░░░▀▄░░░░▀▄▒▒▒▒▒▒▒▒▒▒▀▄\n" +
                "░░░░░░▀▄▄▄▄▄█▄▄▄▄▄▄▄▄▄▄▄▀▄\n" +
                "░░░░░░░░░░░▌▌░▌▌░░░░░\n" +
                "░░░░░░░░░░░▌▌░▌▌░░░░░\n" +
                "░░░░░░░░░▄▄▌▌▄▌▌░░░░░";

        EmbedBuilder builder = new EmbedBuilder().setColor(Color.YELLOW);
        builder.setDescription(s);
        builder.setTitle("Just Gus", "https://vk.com/antich4t");
        MessageBuilder messageBuilder = new MessageBuilder();
        Message message = messageBuilder.setEmbed(builder.build()).build();
        event.getTextChannel().sendMessage(message).queue();
    }

    @Override
    public EmbedBuilder help() {
        return null;
    }

    @Override
    public void execute(boolean success, MessageReceivedEvent event) {

    }
}
