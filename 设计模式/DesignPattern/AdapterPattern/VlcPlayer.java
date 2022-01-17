package DesignPattern.AdapterPattern;

public class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Play Vlc file: " + fileName);
    }

    @Override
    public void playMp4(String fileName) {

    }
}
