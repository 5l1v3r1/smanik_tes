package android.support.transition;

import android.animation.Animator;
import android.graphics.Matrix;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ImageView;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(21)
class ImageViewUtilsApi21 implements ImageViewUtilsImpl {
  private static final String TAG = "ImageViewUtilsApi21";
  
  private static Method sAnimateTransformMethod;
  
  private static boolean sAnimateTransformMethodFetched;
  
  private void fetchAnimateTransformMethod() {
    if (!sAnimateTransformMethodFetched) {
      try {
        sAnimateTransformMethod = ImageView.class.getDeclaredMethod("animateTransform", new Class[] { Matrix.class });
        sAnimateTransformMethod.setAccessible(true);
      } catch (NoSuchMethodException noSuchMethodException) {
        Log.i("ImageViewUtilsApi21", "Failed to retrieve animateTransform method", noSuchMethodException);
      } 
      sAnimateTransformMethodFetched = true;
    } 
  }
  
  public void animateTransform(ImageView paramImageView, Matrix paramMatrix) {
    fetchAnimateTransformMethod();
    if (sAnimateTransformMethod != null)
      try {
        sAnimateTransformMethod.invoke(paramImageView, new Object[] { paramMatrix });
        return;
      } catch (IllegalAccessException paramImageView) {
        return;
      } catch (InvocationTargetException paramImageView) {
        throw new RuntimeException(paramImageView.getCause());
      }  
  }
  
  public void reserveEndAnimateTransform(ImageView paramImageView, Animator paramAnimator) {}
  
  public void startAnimateTransform(ImageView paramImageView) {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ImageViewUtilsApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */