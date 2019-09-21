package android.support.transition;

import android.animation.TypeEvaluator;
import android.graphics.Rect;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class RectEvaluator extends Object implements TypeEvaluator<Rect> {
  private Rect mRect;
  
  RectEvaluator() {}
  
  RectEvaluator(Rect paramRect) { this.mRect = paramRect; }
  
  public Rect evaluate(float paramFloat, Rect paramRect1, Rect paramRect2) {
    int i = paramRect1.left + (int)((paramRect2.left - paramRect1.left) * paramFloat);
    int j = paramRect1.top + (int)((paramRect2.top - paramRect1.top) * paramFloat);
    int k = paramRect1.right + (int)((paramRect2.right - paramRect1.right) * paramFloat);
    int m = paramRect1.bottom + (int)((paramRect2.bottom - paramRect1.bottom) * paramFloat);
    if (this.mRect == null)
      return new Rect(i, j, k, m); 
    this.mRect.set(i, j, k, m);
    return this.mRect;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/RectEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */