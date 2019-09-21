package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Matrix;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;

@RequiresApi(14)
class ImageViewUtilsApi14 implements ImageViewUtilsImpl {
  public void animateTransform(ImageView paramImageView, Matrix paramMatrix) { paramImageView.setImageMatrix(paramMatrix); }
  
  public void reserveEndAnimateTransform(final ImageView view, Animator paramAnimator) { paramAnimator.addListener(new AnimatorListenerAdapter() {
          public void onAnimationEnd(Animator param1Animator) {
            ImageView.ScaleType scaleType = (ImageView.ScaleType)view.getTag(R.id.save_scale_type);
            view.setScaleType(scaleType);
            view.setTag(R.id.save_scale_type, null);
            if (scaleType == ImageView.ScaleType.MATRIX) {
              view.setImageMatrix((Matrix)view.getTag(R.id.save_image_matrix));
              view.setTag(R.id.save_image_matrix, null);
            } 
            param1Animator.removeListener(this);
          }
        }); }
  
  public void startAnimateTransform(ImageView paramImageView) {
    ImageView.ScaleType scaleType = paramImageView.getScaleType();
    paramImageView.setTag(R.id.save_scale_type, scaleType);
    if (scaleType == ImageView.ScaleType.MATRIX) {
      paramImageView.setTag(R.id.save_image_matrix, paramImageView.getImageMatrix());
    } else {
      paramImageView.setScaleType(ImageView.ScaleType.MATRIX);
    } 
    paramImageView.setImageMatrix(MatrixUtils.IDENTITY_MATRIX);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ImageViewUtilsApi14.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */