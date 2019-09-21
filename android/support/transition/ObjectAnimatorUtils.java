package android.support.transition;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.Property;

class ObjectAnimatorUtils {
  private static final ObjectAnimatorUtilsImpl IMPL;
  
  static  {
    if (Build.VERSION.SDK_INT >= 21) {
      IMPL = new ObjectAnimatorUtilsApi21();
      return;
    } 
    IMPL = new ObjectAnimatorUtilsApi14();
  }
  
  static <T> ObjectAnimator ofPointF(T paramT, Property<T, PointF> paramProperty, Path paramPath) { return IMPL.ofPointF(paramT, paramProperty, paramPath); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ObjectAnimatorUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */