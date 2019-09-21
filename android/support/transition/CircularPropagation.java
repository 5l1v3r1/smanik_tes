package android.support.transition;

import android.graphics.Rect;
import android.view.ViewGroup;

public class CircularPropagation extends VisibilityPropagation {
  private float mPropagationSpeed = 3.0F;
  
  private static float distance(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    paramFloat1 = paramFloat3 - paramFloat1;
    paramFloat2 = paramFloat4 - paramFloat2;
    return (float)Math.sqrt((paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2));
  }
  
  public long getStartDelay(ViewGroup paramViewGroup, Transition paramTransition, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2) {
    int j;
    int i;
    boolean bool;
    if (paramTransitionValues1 == null && paramTransitionValues2 == null)
      return 0L; 
    if (paramTransitionValues2 == null || getViewVisibility(paramTransitionValues1) == 0) {
      bool = true;
    } else {
      bool = true;
      paramTransitionValues1 = paramTransitionValues2;
    } 
    int k = getViewX(paramTransitionValues1);
    int m = getViewY(paramTransitionValues1);
    Rect rect = paramTransition.getEpicenter();
    if (rect != null) {
      i = rect.centerX();
      j = rect.centerY();
    } else {
      int[] arrayOfInt = new int[2];
      paramViewGroup.getLocationOnScreen(arrayOfInt);
      i = Math.round((arrayOfInt[0] + paramViewGroup.getWidth() / 2) + paramViewGroup.getTranslationX());
      j = Math.round((arrayOfInt[1] + paramViewGroup.getHeight() / 2) + paramViewGroup.getTranslationY());
    } 
    float f = distance(k, m, i, j) / distance(0.0F, 0.0F, paramViewGroup.getWidth(), paramViewGroup.getHeight());
    long l2 = paramTransition.getDuration();
    long l1 = l2;
    if (l2 < 0L)
      l1 = 300L; 
    return Math.round((float)(l1 * bool) / this.mPropagationSpeed * f);
  }
  
  public void setPropagationSpeed(float paramFloat) {
    if (paramFloat == 0.0F)
      throw new IllegalArgumentException("propagationSpeed may not be 0"); 
    this.mPropagationSpeed = paramFloat;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/CircularPropagation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */