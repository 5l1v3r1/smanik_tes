package android.support.transition;

import android.animation.Animator;
import android.graphics.Matrix;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;

@RequiresApi(14)
interface ImageViewUtilsImpl {
  void animateTransform(ImageView paramImageView, Matrix paramMatrix);
  
  void reserveEndAnimateTransform(ImageView paramImageView, Animator paramAnimator);
  
  void startAnimateTransform(ImageView paramImageView);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ImageViewUtilsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */