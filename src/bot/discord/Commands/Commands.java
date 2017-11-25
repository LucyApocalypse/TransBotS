package bot.discord.Commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Commands {

    boolean validate(MessageReceivedEvent event);
    boolean called(String[] args, MessageReceivedEvent event);
    void action(String[] args, MessageReceivedEvent event) throws InterruptedException;
    EmbedBuilder help();
    void execute(boolean success, MessageReceivedEvent event);

}
