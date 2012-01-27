package snake.util;

// ClipsLoader.java
// Andrew Davison, March 2004, dandrew@ratree.psu.ac.th

/* ClipsLoader  stores a collection of ClipInfo objects
   in a HashMap whose keys are their names.

   The name and filename for a clip is obtained from a sounds
   information file which is loaded when ClipsLoader is created.
   The information file is assumed to be in Sounds/.

   ClipsLoader allows a specified clip to be played, stopped,
   resumed, looped. A SoundsWatcher can be attached to a clip.
   All of this functionality is handled in the ClipInfo object;
   ClipsLoader simply redirects the method call to the right ClipInfo.

   It is possible for many clips to play at the same time, since
   each ClipInfo object is responsible for playing its clip.
*/

import java.util.*;

public class ClipsLoader
{
  private HashMap clipsMap;
    /* The key is the clip 'name', the object (value)
       is a ClipInfo object */

  public ClipsLoader()
  {
    clipsMap = new HashMap();
  }

  // ----------- manipulate a particular clip --------

  public void load(String name, String fnm)
  // create a ClipInfo object for name and store it
  {
    if (clipsMap.containsKey(name))
      System.out.println( "Error: " + name + "already stored");
    else {
      clipsMap.put(name, new ClipInfo(name, fnm) );
      System.out.println("-- " + name + "/" + fnm);
    }
  }

  public void close(String name)
  // close the specified clip
  {  ClipInfo ci = (ClipInfo) clipsMap.get(name);
     if (ci == null)
       System.out.println( "Error: " + name + "not stored");
     else
      ci.close();
  }

  public void play(String name, boolean toLoop)
  // play (perhaps loop) the specified clip
  {  ClipInfo ci = (ClipInfo) clipsMap.get(name);
     if (ci == null)
       System.out.println( "Error: " + name + "not stored");
     else
      ci.play(toLoop);
  }

  public void stop(String name)
  // stop the clip, resetting it to the beginning
  { ClipInfo ci = (ClipInfo) clipsMap.get(name);
    if (ci == null)
      System.out.println( "Error: " + name + "not stored");
    else
      ci.stop();
  }

  public void pause(String name)
  { ClipInfo ci = (ClipInfo) clipsMap.get(name);
    if (ci == null)
      System.out.println( "Error: " + name + "not stored");
    else
      ci.pause();
  }

  public void resume(String name)
  { ClipInfo ci = (ClipInfo) clipsMap.get(name);
    if (ci == null)
      System.out.println( "Error: " + name + "not stored");
    else
      ci.resume();
  }

  //methods for special sounds
  public void playEat()
  {
    play("eat",false);
  }

  public void playCrash()
  {
    play("crash",false);
  }

  public void playDie()
  {
    play("die",false);
  }
}
