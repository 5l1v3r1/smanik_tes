package android.support.transition;

import android.animation.PropertyValuesHolder;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.Property;

class PropertyValuesHolderUtils {
  private static final PropertyValuesHolderUtilsImpl IMPL;
  
  static  {
    if (Build.VERSION.SDK_INT >= 21) {
      IMPL = new PropertyValuesHolderUtilsApi21();
      return;
    } 
    IMPL = new PropertyValuesHolderUtilsApi14();
  }
  
  static PropertyValuesHolder ofPointF(Property<?, PointF> paramProperty, Path paramPath) { return IMPL.ofPointF(paramProperty, paramPath); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/PropertyValuesHolderUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */