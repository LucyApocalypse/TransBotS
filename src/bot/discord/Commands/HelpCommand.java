package bot.discord.Commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class HelpCommand implements Commands {
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
        event.getTextChannel().sendMessage(help().build()).queue();
    }

    @Override
    public EmbedBuilder help() {
        return new EmbedBuilder().setTitle("**COMMAND HELP**")
                .addField("Music", "Play music\nUsage: `!m(usic) [cmd / help]`", true)
                .addField("Ping", "Just ping-pong\nUsage: `!ping`", true)
                .addBlankField(false)
                .addField("Vote", "Make a vote\nUsage `!vote [content / --help]`", true)
                .addField("Clear", "Delete messages\nUsage: `!clear [num]`", true)
                .setColor(Color.YELLOW);
    }

    @Override
    public void execute(boolean success, MessageReceivedEvent event) {

    }
}
