package snake;

/**
 * Class that manages the effects of a snake, including duration, step, minimum
 * and maximum values for effects. Supported effects are brightness and transparency
 * (fading), and a special effect that makes the snake immovable.
 * @author Thomas Scheller, Markus Karolus
 */
public class SnakeEffect
{
  //values for brightness effect
  private boolean brightenEffect = false;
  private int brightenDuration = 0;
  private float brightenValue = 1.0f;
  private float brightenMin = 0.8f;
  private float brightenMax = 2.6f;
  private float brightenStep = 0.2f;
  private boolean brightenIncrease = false;

  //values for fading effect (transparency)
  private boolean fadeEffect = false;
  private int fadeDuration = 0;
  private float fadeValue = 1.0f;
  private float fadeMin = 0.2f;
  private float fadeMax = 1.0f;
  private float fadeStep = 0.1f;
  private boolean fadeIncrease = false;

  //values for immovable effect
  private boolean noMoveEffect = false;
  private int noMoveDuration = 0;

  /**
   * Create a new SnakeEffect.
   */
  public SnakeEffect()
  {
  }

  /**
   * Activate the brighten effect, with the given duration. If the duration
   * is 0, any running brighten effect is stopped.
   * @param frameDuration duration of the brighten effect in frames
   */
  public void showBrightenEffect(int frameDuration)
  {
    if (frameDuration > 0)
    {
      brightenDuration = frameDuration;
      brightenEffect = true;
    }
    else
    {
      brightenDuration = 0;
      brightenEffect = false;
    }
  }

  /**
   * Activate the brighten effect, with the given duration. If the duration
   * is 0, any running brighten effect is stopped.
   * @param frameDuration duration of the fade effect in frames
   */
  public void showFadeEffect(int frameDuration)
  {
    if (frameDuration > 0)
    {
      fadeDuration = frameDuration;
      fadeEffect = true;
    }
    else
    {
      fadeDuration = 0;
      fadeEffect = false;
    }
  }

  /**
   * Activate the no move effect, with the given duration. If the duration
   * is 0, any running no move effect is stopped.
   * @param frameDuration duration of the no move effect in frames
   */
  public void showNoMoveEffect(int frameDuration)
  {
    if (frameDuration > 0)
    {
      noMoveDuration = frameDuration;
      noMoveEffect = true;
    }
    else
    {
      noMoveDuration = 0;
      noMoveEffect = false;
    }
  }

  /**
   * Updates effect values of all active effects. Must be called at every update
   * of the snake. The durations of running effects are decreased by 1, if they
   * reach 0, the effect is turned off.
   */
  public void update()
  {
    //brightness effect
    if (brightenEffect)
    {
      if (brightenIncrease)
      {
        brightenValue += brightenStep;
        if (brightenValue >= brightenMax)
          brightenIncrease = false;
      }
      else
      {
        brightenValue -= brightenStep;
        if (brightenValue <= brightenMin)
          brightenIncrease = true;
      }

      brightenDuration--;
      if (brightenDuration == 0)
        brightenEffect = false;
    }
    //fade effect
    if (fadeEffect)
    {
      if (fadeIncrease)
      {
        fadeValue += fadeStep;
        if (fadeValue >= fadeMax)
          fadeIncrease = false;
      }
      else
      {
        fadeValue -= fadeStep;
        if (fadeValue <= fadeMin)
          fadeIncrease = true;
      }

      fadeDuration--;
      if (fadeDuration == 0)
        fadeEffect = false;
    }
    //immovable effect
    if (noMoveEffect)
    {
      noMoveDuration--;
      if (noMoveDuration == 0)
        noMoveEffect = false;
    }
  }

  public boolean hasBrightenEffect()
  {
    return brightenEffect;
  }

  public float getBrightenValue()
  {
    return brightenValue;
  }

  public boolean hasFadeEffect()
  {
    return fadeEffect;
  }

  public float getFadeValue()
  {
    return fadeValue;
  }

  public boolean hasNoMoveEffect()
  {
    return noMoveEffect;
  }

  /**
   * Activate standard restart effect for the snake (after death), having a no move
   * effect and a brighten effect for 40 frames.
   */
  public void showStandardRestartEffect()
  {
    showNoMoveEffect(40);
    showBrightenEffect(40);
  }

  /**
   * Set standard effect after a crash with itself or another snake, which is a
   * brighten effect for 20 frames.
   */
  public void showStandardCrashEffect()
  {
    showBrightenEffect(20);
  }
}
