package org.example;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundPlayer {


    private final String soundPrefix= "sfx/" ;
    private final String onButtonSoundLocation= "onButtonSound.wav" ;
    private final String onButtonClickSoundLocation= "onButtonClickSound.wav" ;
    private final String onfailedConnectionSoundLocation= "failedConnectionSound.wav" ;
    private final String onsuccesConnectionSoundLocation= "succesConnectionSound.mp3" ;

    private final String drawCardSoundLocationb= "drawCardSound.wav" ;
    private final String finishSoundLocationi= "finishSound.mp3" ;



    SoundPlayer()
    {
        this.loadSounds();
    }

    private void loadSounds() {





    }

    public void playOnButton()
    {
        this.playSound(onButtonSoundLocation);

    }
    public void playDrawCard()
    {
        this.playSound(drawCardSoundLocationb);

    }


    public void playOnButtonClick()
    {
        this.playSound(onButtonClickSoundLocation);
    }

    public void playSucces()
    {

        this.playSound(onsuccesConnectionSoundLocation);
    }

    public void playFailed()
    {
        this.playSound(onfailedConnectionSoundLocation);
    }


    public void playfinish()
    {
        this.playSound(this.finishSoundLocationi);
    }
    private void playSound(String fileName)
    {
        Platform.runLater(
                new Runnable() {
                    @Override
                    public void run() {
                        String file=getClass().getClassLoader().getResource(soundPrefix+fileName).toString();
                        Media failedSfx = new Media(file);
                        MediaPlayer mediaPlayer = new MediaPlayer(failedSfx);
                        mediaPlayer.play();
                    }
                }
        );

    }
}




