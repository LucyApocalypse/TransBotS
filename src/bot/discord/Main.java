package bot.discord;

import bot.discord.Commands.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static JDA jda;
    public static HashMap<String, Commands> commandsHashMap = new HashMap<String, Commands>();
    static final CommandParser parser = new CommandParser();

    private static void _init(String token){
        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);
        jdaBuilder.setToken(token);
        jdaBuilder.setAutoReconnect(true);
        jdaBuilder.setStatus(OnlineStatus.ONLINE);
        jdaBuilder.setGame(Game.of("Use -!help for help"));

        try {
            BotListener botListener = new BotListener();
            jda = jdaBuilder.buildBlocking();
            jda.addEventListener(botListener);
            if(!jda.getSelfUser().getName().equalsIgnoreCase("dad bogdan"))
                jda.getSelfUser().getManager().setName("Dad Bogdan").queue();
        } catch (Exception ignored){

        }
    }
    private static void _initCommands(){
        commandsHashMap.put("ping", new PingCommand());
        commandsHashMap.put("clear", new ClearCommand());
        commandsHashMap.put("dad", new DadBogdanCommand());
        commandsHashMap.put("bogdan", commandsHashMap.get("dad"));
        commandsHashMap.put("vote", new VoteCommand());
        commandsHashMap.put("v", commandsHashMap.get("vote"));
        commandsHashMap.put("m", new Music());
        commandsHashMap.put("music", commandsHashMap.get("m"));
        commandsHashMap.put("gus", new GusCommand());
        commandsHashMap.put("help", new HelpCommand());
        commandsHashMap.put("hlp", commandsHashMap.get("help"));
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(new File("Token.txt"));
            _init(scanner.next());
            _initCommands();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

    }

    static void handleCommand(CommandParser.CommandContainer cmd) throws InterruptedException {
        if(commandsHashMap.containsKey(cmd.invoke)){
            boolean safe = commandsHashMap.get(cmd.invoke).called(cmd.args, cmd.event);

            if(safe){
                commandsHashMap.get(cmd.invoke).action(cmd.args, cmd.event);
                commandsHashMap.get(cmd.invoke).execute(safe, cmd.event);
            } else {
                commandsHashMap.get(cmd.invoke).execute(safe, cmd.event);
            }
        }else {
            cmd.event.getTextChannel().sendMessage(
                    new EmbedBuilder()
                            .setTitle("Error")
                            .setDescription("Try to use: `-!help`")
                            .setColor(Color.RED)
                            .build()
            ).queue();
        }
    }
}
