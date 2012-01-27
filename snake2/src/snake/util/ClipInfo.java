package snake.util;

// ClipInfo.java
// Andrew Davison, March 2004, dandrew@ratree.psu.ac.th

/* Load a clip, which can be played, stopped, resumed, looped.

   An object implementing the SoundsWatcher interface
   can be notified when the clip loops or stops.
*/

import java.io.*;
import javax.sound.sampled.*;

public class ClipInfo implements LineListener
{
  private String name, filename;
  private Clip clip = null;
  private boolean isLooping = false;

  public ClipInfo(String nm, String fnm)
  {
    name = nm;
    loadClip(fnm);
  }

  private void loadClip(String fnm)
  {
    try {

      java.io.File soundFile = new java.io.File(fnm);
      if (!soundFile.exists())
      {
        System.out.println("Can't find file: " + soundFile);
      }

      // link an audio stream to the sound clip's file
      AudioInputStream stream = AudioSystem.getAudioInputStream(soundFile );

      AudioFormat format = stream.getFormat();

      // convert ULAW/ALAW formats to PCM format
      if ( (format.getEncoding() == AudioFormat.Encoding.ULAW) ||
           (format.getEncoding() == AudioFormat.Encoding.ALAW) ) {
        AudioFormat newFormat =
           new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                format.getSampleRate(),
                                format.getSampleSizeInBits()*2,
                                format.getChannels(),
                                format.getFrameSize()*2,
                                format.getFrameRate(), true);  // big endian
        // update stream and format details
        stream = AudioSystem.getAudioInputStream(newFormat, stream);
        System.out.println("Converted Audio format: " + newFormat);
        format = newFormat;
      }

      DataLine.Info info = new DataLine.Info(Clip.class, format);

      // make sure sound system supports data line
      if (!AudioSystem.isLineSupported(info)) {
        System.out.println("Unsupported Clip File: " + fnm);
        return;
      }

      // get clip line resource
      clip = (Clip) AudioSystem.getLine(info);

      // listen to clip for events
      clip.addLineListener(this);

      clip.open(stream);    // open the sound file as a clip
    }

    catch (UnsupportedAudioFileException audioException) {
      System.out.println("Unsupported audio file: " + fnm);
    }
    catch (LineUnavailableException noLineException) {
      System.out.println("No audio line available for : " + fnm);
    }
    catch (IOException ioException) {
      System.out.println("Could not read: " + fnm);
    }
    catch (Exception e) {
      System.out.println("Problem with " + fnm);
    }
  }

  public void update(LineEvent lineEvent)
  //Called when the clip's line detects open, close, start, or
  //stop events. The watcher (if one exists) is notified.
  {
    // when clip is stopped / reaches its end
    if (lineEvent.getType() == LineEvent.Type.STOP) {
      clip.stop();
      if (!isLooping) {  // it isn't looping
      }
      else {      // else play it again
        clip.start();
      }
    }
  }

  public void close()
  { if (clip != null) {
      clip.stop();
      clip.close();
    }
  }

  public void play(boolean toLoop)
  { if (clip != null) {
      isLooping = toLoop;

   clip.setFramePosition(0);
      clip.start(); // start playing from where stopped
    }
  }

  public void stop()
  // stop and reset clip to its start
  { if (clip != null) {
      isLooping = false;
      clip.stop();
      clip.setFramePosition(0);
    }
  }

  public void pause()
  // stop the clip at its current playing position
  { if (clip != null)
      clip.stop();
  }

  public void resume()
  { if (clip != null)
      clip.start();
  }

  // -------------- other access methods -------------------

  public String getName()
  {  return name;  }

}
