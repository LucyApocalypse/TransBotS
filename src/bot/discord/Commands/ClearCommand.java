package bot.discord.Commands;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class ClearCommand implements Commands {

    private EmbedBuilder error = new EmbedBuilder().setColor(Color.RED).setTitle("Error");

    private int getInt(String n, MessageReceivedEvent event){
        try {
            return Integer.parseInt(n);
        }catch (Exception e){
            event.getTextChannel().sendMessage(error.setDescription("Invalid argument").build()).queue();
            return 0;
        }
    }

    @Override
    public boolean validate(MessageReceivedEvent event) {
        return true;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        Set<Permission> authorPermissions = new HashSet<>();
        authorPermissions.addAll(event.getMember().getPermissions());

        if (    !authorPermissions.contains(Permission.ADMINISTRATOR) ||
                !authorPermissions.contains(Permission.MESSAGE_MANAGE) ||
                !authorPermissions.contains(Permission.MANAGE_CHANNEL)){

            event.getTextChannel().sendMessage("I'm sorry, " + event.getAuthor().getAsMention()
                                    + ", but you don't have the right permission ").queue();
            return;
        }

        List<Role> persinalRoles = event.getJDA().getSelfUser().getJDA().getRoles();
        List<Permission> personalPermissions = new LinkedList<>();
        for(Role role : persinalRoles){
            personalPermissions.addAll(role.getPermissions());
        }

        if (    !personalPermissions.contains(Permission.ADMINISTRATOR) ||
                !personalPermissions.contains(Permission.MESSAGE_MANAGE) ||
                !personalPermissions.contains(Permission.MANAGE_CHANNEL)){

            event.getTextChannel().sendMessage("I'm sorry, " + event.getAuthor().getAsMention()
                    + ", but I don't have the right permission ").queue();
            return;
        }

        if(args.length == 0){
            event.getTextChannel().sendMessage(error.setDescription("Invalid Argument").build()).queue();
            return;
        }
        event.getMessage().delete().queue();
        int n = getInt(args[0], event);
        n = ++n < 100 ? n : 99;
        List<Message> msg = new MessageHistory(event.getTextChannel()).retrievePast(n).complete();
        try {
            event.getTextChannel().deleteMessages(msg).queue();
        }catch (Exception e){
            e.printStackTrace();
            event.getTextChannel().sendMessage("I'm sorry, " + event.getAuthor().getAsMention()
                    + ", but I don't have the right permission ").queue();
        }


    }

    @Override
    public EmbedBuilder help() {
        return null;
    }

    @Override
    public void execute(boolean success, MessageReceivedEvent event) {

    }
}
