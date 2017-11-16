package bot.discord.audioCore;

public class AudioList {
    private AudioList head, next;

    public AudioList(AudioList head) {
        this.head = head;
        setNext(head);
    }

    public AudioList getNext() {
        return next;
    }

    public void setNext(AudioList next) {
        this.next = next;
        getNext().setNext(head);
    }
}
