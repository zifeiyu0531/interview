package DesignPattern.AdapterPattern;

public class AdapterTest {
    public static void main(String[] args) {
        AudioPlayer audioPlayer = new AudioPlayer();

        audioPlayer.play("mp3", "1.mp3");
        audioPlayer.play("mp4", "2.mp4");
        audioPlayer.play("vlc", "3.vlc");
    }
}
