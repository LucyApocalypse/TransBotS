package bot.discord.audioCore;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer PLAYER;
    private final Deque<AudioInfo> queue;
    private  AudioInfo lastTrack;
    private boolean isRepeatable = false;
    private VoiceChannel n;
    private boolean lockVchan = false;

    public VoiceChannel getChannel() {
        return n;
    }

    public boolean isVCHannLocked() {
        return lockVchan;
    }

    public void setVChanLock(boolean lock) {
        this.lockVchan = lock;
    }

    public AudioPlayer getPLAYER() {
        return PLAYER;
    }

    public boolean isRepeatable() {
        return isRepeatable;
    }

    public void setRepeatable() {
        if(queue.size() >= 2) {
            isRepeatable = !isRepeatable;
        } else {
            isRepeatable = false;
        }
    }

    public TrackManager(AudioPlayer player) {
        this.PLAYER = player;
        this.queue = new LinkedList<>();
    }

    public void queue(AudioTrack track, Member author) {
        AudioInfo info = new AudioInfo(track, author);
        queue.add(info);
        if (PLAYER.getPlayingTrack() == null) {
            PLAYER.playTrack(track);
        }
    }

    public List<AudioInfo> getQueue() {
        return new LinkedList<>(queue);
    }

    public AudioInfo getInfo(AudioTrack track) {

        return queue.stream()
                .filter(info -> info.getTrack().equals(track))
                .findFirst().orElse(null);
    }

    public void purgeQueue() {
        queue.clear();
    }

    public void shuffleQueue() {
        List<AudioInfo> cQueue = new ArrayList<>(getQueue());
        AudioInfo current = cQueue.get(0);
        cQueue.remove(0);
        Collections.shuffle(cQueue);
        cQueue.add(0, current);
        purgeQueue();
        queue.addAll(cQueue);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        AudioInfo info = queue.element();
        VoiceChannel vChan;
        if(!(info.getAuthor().getVoiceState().getChannel() == null) && !lockVchan) {
            vChan  = info.getAuthor().getVoiceState().getChannel();
            n = vChan;
        } else {
            vChan = n;
        }

        if (vChan == null) {
            player.stopTrack();
            n = null;
            lockVchan = false;
        } else
            info.getAuthor().getGuild().getAudioManager().openAudioConnection(vChan);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        AudioInfo a = queue.poll();
        Guild g;

        try {
            g = a.getAuthor().getGuild();
        }catch (Exception e){
            return;
        }


        if (queue.isEmpty())
            g.getAudioManager().closeAudioConnection();
        else
            player.playTrack(queue.element().getTrack());

        if(!queue.isEmpty()  && !endReason.equals(AudioTrackEndReason.STOPPED) && isRepeatable){
            queue(a.getTrack().makeClone(), a.getAuthor());
        }
    }
}


