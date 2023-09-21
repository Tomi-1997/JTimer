import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.net.URL;

public class Audio
{
    /*
        Jingle from https://freesound.org/people/phantastonia/sounds/270602/#
     */
    public static void play(String filename, float vol)
    {
        try
        {
            /*
                Open file
             */
            URL url = Audio.class.getClassLoader().getResource(filename);
            assert url != null;
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            /*
                Control volume
             */
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(vol);
            clip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
