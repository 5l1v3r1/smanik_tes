package android.support.transition;

import android.animation.Animator;
import android.graphics.Matrix;
import android.os.Build;
import android.widget.ImageView;

class ImageViewUtils {
  private static final ImageViewUtilsImpl IMPL;
  
  static  {
    if (Build.VERSION.SDK_INT >= 21) {
      IMPL = new ImageViewUtilsApi21();
      return;
    } 
    IMPL = new ImageViewUtilsApi14();
  }
  
  static void animateTransform(ImageView paramImageView, Matrix paramMatrix) { IMPL.animateTransform(paramImageView, paramMatrix); }
  
  static void reserveEndAnimateTransform(ImageView paramImageView, Animator paramAnimator) { IMPL.reserveEndAnimateTransform(paramImageView, paramAnimator); }
  
  static void startAnimateTransform(ImageView paramImageView) { IMPL.startAnimateTransform(paramImageView); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ImageViewUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */