package android.support.transition;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.RequiresApi;
import android.util.Property;

@RequiresApi(21)
class ObjectAnimatorUtilsApi21 implements ObjectAnimatorUtilsImpl {
  public <T> ObjectAnimator ofPointF(T paramT, Property<T, PointF> paramProperty, Path paramPath) { return ObjectAnimator.ofObject(paramT, paramProperty, null, paramPath); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ObjectAnimatorUtilsApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */