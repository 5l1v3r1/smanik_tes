package android.support.transition;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.RequiresApi;
import android.util.Property;

@RequiresApi(14)
class ObjectAnimatorUtilsApi14 implements ObjectAnimatorUtilsImpl {
  public <T> ObjectAnimator ofPointF(T paramT, Property<T, PointF> paramProperty, Path paramPath) { return ObjectAnimator.ofFloat(paramT, new PathProperty(paramProperty, paramPath), new float[] { 0.0F, 1.0F }); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ObjectAnimatorUtilsApi14.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */