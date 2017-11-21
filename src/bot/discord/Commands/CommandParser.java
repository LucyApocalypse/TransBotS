package bot.discord.Commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandParser {
    public CommandContainer parse(String rw, MessageReceivedEvent event){
            ArrayList<String> split  = new ArrayList<>();
            String beheaded = rw.replaceFirst("!", "");
            String[] splitbeheaded  = beheaded.split(" ");
            String[] splitbeheaded1 = new String[splitbeheaded.length];
            String m;
            for(int i = 0; i < splitbeheaded.length; i++){
                m = splitbeheaded[i];
                if(m.startsWith("https://")){
                    m = m.substring(8);
                    if(!m.startsWith("www."))
                        m = "www." + m;
                }else if(m.startsWith("http://")){
                    m = m.substring(7);
                    if(!m.startsWith("www."))
                        m = "www." + m;
                }
                splitbeheaded1[i]  = m;
            }
            splitbeheaded = splitbeheaded1;
            split.addAll(Arrays.asList(splitbeheaded));
            String invoke = split.get(0);
            String[] args = new String[split.size() - 1];
            split.subList(1, split.size()).toArray(args);
            return new CommandContainer(rw, beheaded, splitbeheaded, invoke, args, event);
    }

    public class CommandContainer {
        public final String raw, beheaded, invoke;
        public final String[] splitbeheaded, args;
        public final MessageReceivedEvent event;

        CommandContainer(String raw, String beheaded, String[] splitbeheaded, String invoke, String[] args, MessageReceivedEvent event) {
            this.raw = raw;
            this.beheaded = beheaded;
            this.invoke = invoke;
            this.splitbeheaded = splitbeheaded;
            this.args = args;
            this.event = event;
        }
    }
}
