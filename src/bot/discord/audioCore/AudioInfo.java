package bot.discord.audioCore;

import bot.discord.Commands.Music;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Member;


public class AudioInfo {

    private final AudioTrack TRACK;
    private final Member AUTHOR;
    private final Long DURATION;

    public AudioInfo(AudioTrack track, Member author) {
        this.TRACK = track;
        this.AUTHOR = author;
        DURATION = track.getDuration();
    }

    public AudioTrack getTrack() {
        return TRACK;
    }

    public Member getAuthor() {
        return AUTHOR;
    }

    public Long getDURATION() {
        return DURATION;
    }
}