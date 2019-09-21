package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class ChangeClipBounds extends Transition {
  private static final String PROPNAME_BOUNDS = "android:clipBounds:bounds";
  
  private static final String PROPNAME_CLIP = "android:clipBounds:clip";
  
  private static final String[] sTransitionProperties = { "android:clipBounds:clip" };
  
  public ChangeClipBounds() {}
  
  public ChangeClipBounds(Context paramContext, AttributeSet paramAttributeSet) { super(paramContext, paramAttributeSet); }
  
  private void captureValues(TransitionValues paramTransitionValues) {
    View view = paramTransitionValues.view;
    if (view.getVisibility() == 8)
      return; 
    Rect rect = ViewCompat.getClipBounds(view);
    paramTransitionValues.values.put("android:clipBounds:clip", rect);
    if (rect == null) {
      Rect rect1 = new Rect(0, 0, view.getWidth(), view.getHeight());
      paramTransitionValues.values.put("android:clipBounds:bounds", rect1);
    } 
  }
  
  public void captureEndValues(@NonNull TransitionValues paramTransitionValues) { captureValues(paramTransitionValues); }
  
  public void captureStartValues(@NonNull TransitionValues paramTransitionValues) { captureValues(paramTransitionValues); }
  
  public Animator createAnimator(@NonNull ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2) {
    if (paramTransitionValues1 != null && paramTransitionValues2 != null && paramTransitionValues1.values.containsKey("android:clipBounds:clip")) {
      boolean bool;
      Rect rect2;
      Rect rect1;
      if (!paramTransitionValues2.values.containsKey("android:clipBounds:clip"))
        return null; 
      Rect rect3 = (Rect)paramTransitionValues1.values.get("android:clipBounds:clip");
      Rect rect4 = (Rect)paramTransitionValues2.values.get("android:clipBounds:clip");
      if (rect4 == null) {
        bool = true;
      } else {
        bool = false;
      } 
      if (rect3 == null && rect4 == null)
        return null; 
      if (rect3 == null) {
        rect1 = (Rect)paramTransitionValues1.values.get("android:clipBounds:bounds");
        rect2 = rect4;
      } else {
        rect1 = rect3;
        rect2 = rect4;
        if (rect4 == null) {
          rect2 = (Rect)paramTransitionValues2.values.get("android:clipBounds:bounds");
          rect1 = rect3;
        } 
      } 
      if (rect1.equals(rect2))
        return null; 
      ViewCompat.setClipBounds(paramTransitionValues2.view, rect1);
      RectEvaluator rectEvaluator = new RectEvaluator(new Rect());
      ObjectAnimator objectAnimator = ObjectAnimator.ofObject(paramTransitionValues2.view, ViewUtils.CLIP_BOUNDS, rectEvaluator, new Rect[] { rect1, rect2 });
      if (bool)
        objectAnimator.addListener(new AnimatorListenerAdapter() {
              public void onAnimationEnd(Animator param1Animator) { ViewCompat.setClipBounds(endView, null); }
            }); 
      return objectAnimator;
    } 
    return null;
  }
  
  public String[] getTransitionProperties() { return sTransitionProperties; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ChangeClipBounds.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */