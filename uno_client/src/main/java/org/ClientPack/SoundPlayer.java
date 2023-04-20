package org.ClientPack;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundPlayer {

    /**
     * String that hold prefix for folder with sound effects
     */
    private final String soundPrefix= "sfx/" ;
    private final String onButtonClickSoundLocation= "onButtonClickSound.wav" ;
    /**
     * String that hold location for failed connection sound
     */
    private final String onfailedConnectionSoundLocation= "failedConnectionSound.wav" ;
    /**
     * String that hold location for succes sound
     */
    private final String onsuccesConnectionSoundLocation= "succesConnectionSound.mp3" ;

    /**
     * String that hold location for draw card sound
     */
    private final String drawCardSoundLocationb= "drawCardSound.wav" ;
    /**
     * String that hold location for finish sound effect
     */
    private final String finishSoundLocationi= "finishSound.mp3" ;
    /**
     * String that hold location for clock tick sound
     */
    private final String clockTickSoundLocationi= "clockTickSound.wav" ;

    /**
     * String that hold location for meesseage pop sound
     */
    private final String messagePopSoundLocation= "messagePopSound.wav" ;





    /**
     * play draw card sound
     */
    public void playDrawCard()
    {
        this.playSound(drawCardSoundLocationb);

    }


    /**
     * play button click sound
     */
    public void playOnButtonClick()
    {
        this.playSound(onButtonClickSoundLocation);
    }

    /**
     * plays succes sound
     */
    public void playSucces()
    {

        this.playSound(onsuccesConnectionSoundLocation);
    }

    /**
     * plays falied sound effect
     */
    public void playFailed()
    {
        this.playSound(onfailedConnectionSoundLocation);
    }

    /**
     * plays finssh sound effect
     */
    public void playfinish()
    {
        this.playSound(this.finishSoundLocationi);
    }

    /**
     * plays clock sound effecr
     */

    public void playClock()
    {
        this.playSound(this.clockTickSoundLocationi);
    }

    /**
     * plays messeage pop sound
     */
    public void playmesseagePop()
    {
        this.playSound(this.messagePopSoundLocation);
    }


    /**
     * a method to play sound from provided location
     * @param fileName
     */
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




