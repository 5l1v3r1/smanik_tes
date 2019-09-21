package android.support.v4.view;

import android.os.Build;
import android.view.ScaleGestureDetector;

public final class ScaleGestureDetectorCompat {
  public static boolean isQuickScaleEnabled(ScaleGestureDetector paramScaleGestureDetector) { return (Build.VERSION.SDK_INT >= 19) ? paramScaleGestureDetector.isQuickScaleEnabled() : 0; }
  
  @Deprecated
  public static boolean isQuickScaleEnabled(Object paramObject) { return isQuickScaleEnabled((ScaleGestureDetector)paramObject); }
  
  public static void setQuickScaleEnabled(ScaleGestureDetector paramScaleGestureDetector, boolean paramBoolean) {
    if (Build.VERSION.SDK_INT >= 19)
      paramScaleGestureDetector.setQuickScaleEnabled(paramBoolean); 
  }
  
  @Deprecated
  public static void setQuickScaleEnabled(Object paramObject, boolean paramBoolean) { setQuickScaleEnabled((ScaleGestureDetector)paramObject, paramBoolean); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/ScaleGestureDetectorCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */