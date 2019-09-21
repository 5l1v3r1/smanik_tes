package android.support.v4.view.animation;

import android.graphics.Path;
import android.os.Build;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

public final class PathInterpolatorCompat {
  public static Interpolator create(float paramFloat1, float paramFloat2) { return (Build.VERSION.SDK_INT >= 21) ? new PathInterpolator(paramFloat1, paramFloat2) : new PathInterpolatorApi14(paramFloat1, paramFloat2); }
  
  public static Interpolator create(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) { return (Build.VERSION.SDK_INT >= 21) ? new PathInterpolator(paramFloat1, paramFloat2, paramFloat3, paramFloat4) : new PathInterpolatorApi14(paramFloat1, paramFloat2, paramFloat3, paramFloat4); }
  
  public static Interpolator create(Path paramPath) { return (Build.VERSION.SDK_INT >= 21) ? new PathInterpolator(paramPath) : new PathInterpolatorApi14(paramPath); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/animation/PathInterpolatorCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */