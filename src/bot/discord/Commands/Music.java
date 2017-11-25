package bot.discord.Commands;

import bot.discord.audioCore.*;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;


public class Music implements Commands {


    private static Guild guild;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> PLAYERS = new HashMap<>();

    public Music() {
        YoutubeAudioSourceManager audioSourceManager = new YoutubeAudioSourceManager(true);
        MANAGER.registerSourceManager(audioSourceManager);
        MANAGER.registerSourceManager(new SoundCloudAudioSourceManager(true));
        MANAGER.registerSourceManager(new BandcampAudioSourceManager());
        MANAGER.registerSourceManager(new VimeoAudioSourceManager());
        MANAGER.registerSourceManager(new TwitchStreamAudioSourceManager());
        MANAGER.registerSourceManager(new BeamAudioSourceManager());
        MANAGER.registerSourceManager(new LocalAudioSourceManager());
        MANAGER.registerSourceManager(new HttpAudioSourceManager());

        AudioSourceManagers.registerRemoteSources(MANAGER);
    }

    private AudioPlayer createPlayer(Guild g) {
        AudioPlayer p = MANAGER.createPlayer();
        TrackManager m = new TrackManager(p);
        p.addListener(m);
        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(p));
        PLAYERS.put(g, new AbstractMap.SimpleEntry<>(p, m));
        return p;
    }

    private boolean hasPlayer(Guild g) {
        return PLAYERS.containsKey(g);
    }
    private AudioPlayer getPlayer(Guild g) {
        if (hasPlayer(g))
            return PLAYERS.get(g).getKey();
        else
            return createPlayer(g);
    }

    private TrackManager getManager(Guild g) {
        return PLAYERS.get(g).getValue();
    }
    private boolean isIdle(Guild g) {
        return !hasPlayer(g) || getPlayer(g).getPlayingTrack() == null;
    }
    private void loadTrack(String identifier, final Member author) {
        final Guild guild = author.getGuild();
        getPlayer(guild);
        MANAGER.setFrameBufferDuration(5000);
        MANAGER.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                getManager(guild).queue(track, author);
            }
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                    getManager(guild).queue(playlist.getTracks().get(0), author);
            }
            @Override
            public void noMatches() {
            }
            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
            }
        });

    }
    private void skip(Guild g) {
        try {
            getPlayer(g).stopTrack();
        }catch (Exception e){
            e.printStackTrace();
            getPlayer(g).destroy();
        }

    }
    private String getTimestamp(long milis) {
        long seconds = milis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

    private String buildQueueMessage(AudioInfo info) {
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String title = trackInfo.title;
        long length = trackInfo.length;
        return "`[ " + getTimestamp(length) + " ]` " + title + "\n";
    }

    private void sendErrorMsg(MessageReceivedEvent event, EmbedBuilder content) {
        event.getTextChannel().sendMessage(
                content.build()
        ).queue();
    }


    @Override
    public boolean validate(MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        guild = event.getGuild();
        if (args.length < 1) {
            sendErrorMsg(event, help());
            return;
        }
        switch (args[0].toLowerCase()) {

            case "volume":
                int volume;
                try {
                    volume = Integer.parseInt(args[1]);
                }catch (Exception e){
                    event.getTextChannel().sendMessage(

                            new EmbedBuilder().setTitle("**Info**")
                                    .addField("Volume", "*" + String.valueOf(getPlayer(guild).getVolume()) + "*", false)
                                    .setColor(Color.YELLOW)
                                    .build()

                    ).queue();
                    volume = getPlayer(guild).getVolume();
                }
                getPlayer(guild).setVolume(volume <= 100 ? volume : 100);
                break;

            case "play":
            case "p":
                List<VoiceChannel> channels = event.getJDA().getVoiceChannels();
                Set<Member> members = new HashSet<>();
                for (VoiceChannel channel : channels){
                    members.addAll(channel.getMembers());
                }
                if(!members.contains(event.getMember())){
                    event.getTextChannel().sendMessage("I'm sorry, " + event.getMember().getAsMention()
                            + ", but you need enter to voice chanel!").queue();
                    return;
                }
                if (args.length < 2) {
                    sendErrorMsg(event, new EmbedBuilder().setColor(Color.RED).setDescription("Invalid source"));
                    return;
                }

                if(args[1].toLowerCase().contains("http://") || args[1].toLowerCase().contains("https://") || args[1].toLowerCase().contains("www.")){
                    event.getTextChannel().sendMessage(

                            new EmbedBuilder()
                                    .setTitle("**SORRY**")
                                    .setDescription("Try to search track by name\n" + "Usage: `-!m p(lay) [track name]`")
                                    .setColor(Color.RED)
                                    .build()

                    ).queue();
                    return;
                }
                String identifier = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                identifier = "ytsearch: " + identifier;

                loadTrack(identifier, event.getMember());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                AudioInfo audioInfo1 = getManager(guild).getQueue().get(getManager(guild).getQueue().size() - 1);
                AudioTrackInfo info1 = audioInfo1.getTrack().getInfo();

                event.getTextChannel().sendMessage(

                        new EmbedBuilder()
                            .setColor(Color.YELLOW)
                            .setTitle("**NEW TRACK**")
                            .setDescription("New track added from **YouTube**")
                            .addField("Track", info1.title, false)
                            .addField("Author", info1.author, false)
                            .addField("Duration", getTimestamp(info1.length), false)
                            .build()

                ).queue();

                break;
            case "splay":
            case "sp":
                List<VoiceChannel> schannels = event.getJDA().getVoiceChannels();
                Set<Member> smembers = new HashSet<>();
                for (VoiceChannel channel : schannels){
                    smembers.addAll(channel.getMembers());
                }
                if(!smembers.contains(event.getMember())){
                    event.getTextChannel().sendMessage("I'm sorry, " + event.getMember().getAsMention()
                            + ", but you need enter to voice chanel!").queue();
                    return;
                }
                if (args.length < 2) {
                    sendErrorMsg(event, new EmbedBuilder().setColor(Color.RED).setDescription("Invalid source"));
                    return;
                }

                if(args[1].toLowerCase().contains("http://") || args[1].toLowerCase().contains("https://") || args[1].toLowerCase().contains("www.")){
                    event.getTextChannel().sendMessage(

                            new EmbedBuilder()
                                    .setTitle("**SORRY**")
                                    .setDescription("Try to search track by name\n" + "Usage: `-!m sp(lay) [track name]`")
                                    .setColor(Color.RED)
                                    .build()

                    ).queue();
                    return;
                }
                String sidentifier = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                identifier = "scsearch: " + sidentifier;

                loadTrack(identifier, event.getMember());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                AudioInfo saudioInfo1 = getManager(guild).getQueue().get(getManager(guild).getQueue().size() - 1);
                AudioTrackInfo sinfo1 = saudioInfo1.getTrack().getInfo();

                event.getTextChannel().sendMessage(

                        new EmbedBuilder()
                                .setColor(Color.YELLOW)
                                .setTitle("**NEW TRACK**")
                                .setDescription("New track added from **SoundCloud**")
                                .addField("Track", sinfo1.title, false)
                                .addField("Author", sinfo1.author, false)
                                .addField("Duration", getTimestamp(sinfo1.length), false)
                                .build()

                ).queue();

                break;
            case "pause":
                getPlayer(guild).setPaused(true);
                break;
            case "resume":
                getPlayer(guild).setPaused(false);
                break;
            case "skip":
                if (isIdle(guild)) return;
                skip(guild);
                break;
            case "stop":
                if (isIdle(guild)) return;
                skip(guild);
                getManager(guild).purgeQueue();
                guild.getAudioManager().closeAudioConnection();
                break;
            case "shuffle":
                if (isIdle(guild)) return;
                getManager(guild).shuffleQueue();
                break;
            case "now":
            case "info":
            case "np":
                if (isIdle(guild)) return;
                AudioTrack track = getPlayer(guild).getPlayingTrack();
                AudioTrackInfo info = track.getInfo();
                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setDescription("**CURRENT TRACK INFO:**")
                                .addField("Title", info.title, false)
                                .addField("Duration",
                                        "`[ " + getTimestamp(track.getPosition()) + "/ " + getTimestamp(track.getDuration()) + " ]`", false)
                                .addField("Author", info.author, false)
                                .setColor(Color.YELLOW)
                                .build()
                ).queue();
                break;
            case "queue":
            case "list":
                if (isIdle(guild)) return;
                int sideNumb = args.length > 1 ? Integer.parseInt(args[1]) : 1;
                List<String> tracks = new ArrayList<>();
                List<String> trackSublist;
                getManager(guild).getQueue().forEach(audioInfo -> tracks.add(buildQueueMessage(audioInfo)));
                if (tracks.size() > 100)
                    trackSublist = tracks.subList((sideNumb-1)*100, (sideNumb-1)*100+20);
                else
                    trackSublist = tracks;
                String out = trackSublist.stream().collect(Collectors.joining("\n"));
                int sideNumbAll = tracks.size() >= 100 ? tracks.size() / 100 : 1;
                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setDescription(
                                        "**CURRENT QUEUE:**\n" +
                                                "[ Tracks | Side *" + sideNumb + "* */* *" + sideNumbAll + "*]" +
                                                "\t(repeat: *" + getManager(guild).isRepeatable() + "*)\n" + out)
                                .setColor(Color.YELLOW)
                                .build()
                ).queue();
                break;
            case "next":
                AudioInfo audioInfo = getManager(guild).getInfo(getPlayer(guild).getPlayingTrack());
                if(getManager(guild).isRepeatable())
                    getManager(guild).queue(audioInfo.getTrack().makeClone(), audioInfo.getAuthor());
                getPlayer(guild).stopTrack();
                break;
            case "repeat":
            case "r":
                getPlayer(guild);
                getManager(guild).setRepeatable();
                StringBuilder builder = new StringBuilder().append("Now I'll ");
                if (getManager(guild).isRepeatable()) {
                    builder.append("**REPEAT** tracks");
                } else {
                    builder.append("**NOT REPEAT** tracks");
                }
                builder.append("\n");
                builder.append("*I can repeat only 2 or more tracks!*");
                event.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setDescription(builder.toString())
                                .build()
                ).queue();
                break;
            case "help":
                event.getTextChannel().sendMessage(
                        help().build()
                ).queue();
                break;

            case "lock":

                getPlayer(guild);

                if(getManager(guild).getChannel() == null){
                    event.getTextChannel().sendMessage(
                            new EmbedBuilder().setTitle("Error!")
                                    .setColor(Color.RED)
                                    .setDescription("I **can't lock** voice channel!")
                                    .build()
                    ).queue();
                    return;
                }

                getManager(guild).setVChanLock(true);
                event.getTextChannel().sendMessage(
                        new EmbedBuilder().setTitle("**VOICE CHANNEL LOCK**")
                                .setColor(Color.GREEN)
                                .setDescription("Now voice channel `" + getManager(guild).getChannel().getName() + "` **IS LOCKED**!")
                                .build()
                ).queue();

                break;

            case "unlock":

                getPlayer(guild);

                if(getManager(guild).getChannel() == null){
                    event.getTextChannel().sendMessage(
                            new EmbedBuilder().setTitle("Error!")
                                    .setColor(Color.RED)
                                    .setDescription("I **can't unlock** voice channel!")
                                    .build()
                    ).queue();
                    return;
                }

                getManager(guild).setVChanLock(false);
                event.getTextChannel().sendMessage(
                        new EmbedBuilder().setTitle("**VOICE CHANNEL LOCK**")
                                .setColor(Color.GREEN)
                                .setDescription("Now voice channel `" + getManager(guild).getChannel().getName() + "` **IS UNLOCKED**!")
                                .build()
                ).queue();

                break;

            case "islocked":

                if(getManager(guild).getChannel() == null){
                    event.getTextChannel().sendMessage("❌ I am NOT CONNECTED to voice channel").queue();
                    return;
                }

                getPlayer(guild);

                event.getTextChannel().sendMessage(
                        new EmbedBuilder().setTitle("**VOICE CHANNEL LOCK**")
                                .setColor(Color.YELLOW)
                                .setDescription("Voice channel `" + getManager(guild).getChannel().getName() + "` **IS**"
                                        + (!getManager(guild).isVCHannLocked() ? " ** NOT **" : " ") + "**LOCKED**")
                                .build()
                ).queue();

                break;

                default:
                    event.getTextChannel().sendMessage(
                            new EmbedBuilder().setTitle("Error!")
                            .setColor(Color.RED)
                            .setDescription("Try tu use ``-!m help``")
                            .build()
                    ).queue();
        }
    }

    @Override
    public EmbedBuilder help() {
        EmbedBuilder builder = new EmbedBuilder().setTitle("**MUSIC HELP**");
        builder.addField("Play", "Play track from YouTube or SoundCloud\nUsage **YT**: `-!m p(lay) [track name]`\nUsage **SC**: `-!m sp(lay) [track name]`", true);
        builder.addField("Stop", "Stop playing tracks \nUsage: `-!m stop`", true);
        builder.addBlankField(false);
        builder.addField("Pause", "Make pause \nUsage: `-!m pause`", true);
        builder.addField("Resume", "Continue playing \nUsage: `-!m resume`", true);
        builder.addBlankField(false);
        builder.addField("Skip", "Skip this track \nUsage: `-!m skip`", true);
        builder.addField("Next", "Skip this track\nand add to the queue and\n if `repeat` is `true`\nUsage: `-!m next`", true);
        builder.addBlankField(false);
        builder.addField("NP (NOW / INFO)", "Info about now playing track \nUsage: `-!m np (now, info)`", true);
        builder.addField("Queue (List)", "Tracks queue \nUsage: `-!m queue (list)`", true);
        builder.addBlankField(false);
        builder.addField("Repeat", "Turn on / off track repeat \nUsage: `-!m r(epeat)`", true);
        builder.addField("Shuffle", "Shuffle tracks queue \nUsage: `-!m shuffle`", true);
        builder.addBlankField(false);
        builder.addField("Volume", "Set Bot volume\nUsage: `-!m volume [0-100]`", true);
        builder.addField("Volume", "Get Bot volume\nUsage: `-!m volume`", true);
        builder.addBlankField(false);
        builder.addField("Lock", "Set Voice Channel Locked\nUsage: `-!m lock`", true);
        builder.addField("Unlock", "Set Voice Channel Unlocked\nUsage: `-!m unlock`", true);
        builder.addBlankField(false);
        builder.addField("Is locked?", "Is voice channel locked?\nUsage: `-!m islocked`", true);
        builder.addField("Help", "Get help (about music command [**not ready yet**])\nUsage: `-!m help (command)`", true);
        builder.setColor(Color.YELLOW);
        return builder;
    }

    @Override
    public void execute(boolean success, MessageReceivedEvent event) {
    }
}
