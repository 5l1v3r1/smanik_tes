package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

class TransitionUtils {
  private static final int MAX_IMAGE_SIZE = 1048576;
  
  static View copyViewImage(ViewGroup paramViewGroup, View paramView1, View paramView2) {
    Matrix matrix = new Matrix();
    matrix.setTranslate(-paramView2.getScrollX(), -paramView2.getScrollY());
    ViewUtils.transformMatrixToGlobal(paramView1, matrix);
    ViewUtils.transformMatrixToLocal(paramViewGroup, matrix);
    RectF rectF = new RectF(0.0F, 0.0F, paramView1.getWidth(), paramView1.getHeight());
    matrix.mapRect(rectF);
    int i = Math.round(rectF.left);
    int j = Math.round(rectF.top);
    int k = Math.round(rectF.right);
    int m = Math.round(rectF.bottom);
    ImageView imageView = new ImageView(paramView1.getContext());
    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    Bitmap bitmap = createViewBitmap(paramView1, matrix, rectF);
    if (bitmap != null)
      imageView.setImageBitmap(bitmap); 
    imageView.measure(View.MeasureSpec.makeMeasureSpec(k - i, 1073741824), View.MeasureSpec.makeMeasureSpec(m - j, 1073741824));
    imageView.layout(i, j, k, m);
    return imageView;
  }
  
  private static Bitmap createViewBitmap(View paramView, Matrix paramMatrix, RectF paramRectF) {
    int j = Math.round(paramRectF.width());
    int i = Math.round(paramRectF.height());
    if (j > 0 && i > 0) {
      float f = Math.min(1.0F, 1048576.0F / (j * i));
      j = (int)(j * f);
      i = (int)(i * f);
      paramMatrix.postTranslate(-paramRectF.left, -paramRectF.top);
      paramMatrix.postScale(f, f);
      Bitmap bitmap = Bitmap.createBitmap(j, i, Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      canvas.concat(paramMatrix);
      paramView.draw(canvas);
      return bitmap;
    } 
    return null;
  }
  
  static Animator mergeAnimators(Animator paramAnimator1, Animator paramAnimator2) {
    if (paramAnimator1 == null)
      return paramAnimator2; 
    if (paramAnimator2 == null)
      return paramAnimator1; 
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(new Animator[] { paramAnimator1, paramAnimator2 });
    return animatorSet;
  }
  
  static class MatrixEvaluator extends Object implements TypeEvaluator<Matrix> {
    final float[] mTempEndValues = new float[9];
    
    final Matrix mTempMatrix = new Matrix();
    
    final float[] mTempStartValues = new float[9];
    
    public Matrix evaluate(float param1Float, Matrix param1Matrix1, Matrix param1Matrix2) {
      param1Matrix1.getValues(this.mTempStartValues);
      param1Matrix2.getValues(this.mTempEndValues);
      byte b;
      for (b = 0; b < 9; b++) {
        float f1 = this.mTempEndValues[b];
        float f2 = this.mTempStartValues[b];
        this.mTempEndValues[b] = this.mTempStartValues[b] + (f1 - f2) * param1Float;
      } 
      this.mTempMatrix.setValues(this.mTempEndValues);
      return this.mTempMatrix;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/TransitionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */