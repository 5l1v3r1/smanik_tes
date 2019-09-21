package android.support.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.Map;

public class ChangeImageTransform extends Transition {
  private static final Property<ImageView, Matrix> ANIMATED_TRANSFORM_PROPERTY;
  
  private static final TypeEvaluator<Matrix> NULL_MATRIX_EVALUATOR;
  
  private static final String PROPNAME_BOUNDS = "android:changeImageTransform:bounds";
  
  private static final String PROPNAME_MATRIX = "android:changeImageTransform:matrix";
  
  private static final String[] sTransitionProperties = { "android:changeImageTransform:matrix", "android:changeImageTransform:bounds" };
  
  static  {
    NULL_MATRIX_EVALUATOR = new TypeEvaluator<Matrix>() {
        public Matrix evaluate(float param1Float, Matrix param1Matrix1, Matrix param1Matrix2) { return null; }
      };
    ANIMATED_TRANSFORM_PROPERTY = new Property<ImageView, Matrix>(Matrix.class, "animatedTransform") {
        public Matrix get(ImageView param1ImageView) { return null; }
        
        public void set(ImageView param1ImageView, Matrix param1Matrix) { ImageViewUtils.animateTransform(param1ImageView, param1Matrix); }
      };
  }
  
  public ChangeImageTransform() {}
  
  public ChangeImageTransform(Context paramContext, AttributeSet paramAttributeSet) { super(paramContext, paramAttributeSet); }
  
  private void captureValues(TransitionValues paramTransitionValues) {
    View view = paramTransitionValues.view;
    if (view instanceof ImageView) {
      if (view.getVisibility() != 0)
        return; 
      ImageView imageView = (ImageView)view;
      if (imageView.getDrawable() == null)
        return; 
      Map map = paramTransitionValues.values;
      map.put("android:changeImageTransform:bounds", new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
      map.put("android:changeImageTransform:matrix", copyImageMatrix(imageView));
      return;
    } 
  }
  
  private static Matrix centerCropMatrix(ImageView paramImageView) {
    Drawable drawable = paramImageView.getDrawable();
    int i = drawable.getIntrinsicWidth();
    float f1 = paramImageView.getWidth();
    float f2 = i;
    float f5 = f1 / f2;
    i = drawable.getIntrinsicHeight();
    float f3 = paramImageView.getHeight();
    float f4 = i;
    f5 = Math.max(f5, f3 / f4);
    i = Math.round((f1 - f2 * f5) / 2.0F);
    int j = Math.round((f3 - f4 * f5) / 2.0F);
    Matrix matrix = new Matrix();
    matrix.postScale(f5, f5);
    matrix.postTranslate(i, j);
    return matrix;
  }
  
  private static Matrix copyImageMatrix(ImageView paramImageView) {
    switch (paramImageView.getScaleType()) {
      default:
        return new Matrix(paramImageView.getImageMatrix());
      case null:
        return centerCropMatrix(paramImageView);
      case FIT_XY:
        break;
    } 
    return fitXYMatrix(paramImageView);
  }
  
  private ObjectAnimator createMatrixAnimator(ImageView paramImageView, Matrix paramMatrix1, Matrix paramMatrix2) { return ObjectAnimator.ofObject(paramImageView, ANIMATED_TRANSFORM_PROPERTY, new TransitionUtils.MatrixEvaluator(), new Matrix[] { paramMatrix1, paramMatrix2 }); }
  
  private ObjectAnimator createNullAnimator(ImageView paramImageView) { return ObjectAnimator.ofObject(paramImageView, ANIMATED_TRANSFORM_PROPERTY, NULL_MATRIX_EVALUATOR, new Matrix[] { null, null }); }
  
  private static Matrix fitXYMatrix(ImageView paramImageView) {
    Drawable drawable = paramImageView.getDrawable();
    Matrix matrix = new Matrix();
    matrix.postScale(paramImageView.getWidth() / drawable.getIntrinsicWidth(), paramImageView.getHeight() / drawable.getIntrinsicHeight());
    return matrix;
  }
  
  public void captureEndValues(@NonNull TransitionValues paramTransitionValues) { captureValues(paramTransitionValues); }
  
  public void captureStartValues(@NonNull TransitionValues paramTransitionValues) { captureValues(paramTransitionValues); }
  
  public Animator createAnimator(@NonNull ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2) {
    if (paramTransitionValues1 != null) {
      if (paramTransitionValues2 == null)
        return null; 
      Rect rect1 = (Rect)paramTransitionValues1.values.get("android:changeImageTransform:bounds");
      Rect rect2 = (Rect)paramTransitionValues2.values.get("android:changeImageTransform:bounds");
      if (rect1 != null) {
        if (rect2 == null)
          return null; 
        Matrix matrix2 = (Matrix)paramTransitionValues1.values.get("android:changeImageTransform:matrix");
        Matrix matrix3 = (Matrix)paramTransitionValues2.values.get("android:changeImageTransform:matrix");
        if ((matrix2 == null && matrix3 == null) || (matrix2 != null && matrix2.equals(matrix3))) {
          i = 1;
        } else {
          i = 0;
        } 
        if (rect1.equals(rect2) && i)
          return null; 
        ImageView imageView = (ImageView)paramTransitionValues2.view;
        Drawable drawable = imageView.getDrawable();
        int i = drawable.getIntrinsicWidth();
        int j = drawable.getIntrinsicHeight();
        ImageViewUtils.startAnimateTransform(imageView);
        if (i == 0 || j == 0) {
          ObjectAnimator objectAnimator1 = createNullAnimator(imageView);
          ImageViewUtils.reserveEndAnimateTransform(imageView, objectAnimator1);
          return objectAnimator1;
        } 
        Matrix matrix1 = matrix2;
        if (matrix2 == null)
          matrix1 = MatrixUtils.IDENTITY_MATRIX; 
        matrix2 = matrix3;
        if (matrix3 == null)
          matrix2 = MatrixUtils.IDENTITY_MATRIX; 
        ANIMATED_TRANSFORM_PROPERTY.set(imageView, matrix1);
        ObjectAnimator objectAnimator = createMatrixAnimator(imageView, matrix1, matrix2);
        ImageViewUtils.reserveEndAnimateTransform(imageView, objectAnimator);
        return objectAnimator;
      } 
      return null;
    } 
    return null;
  }
  
  public String[] getTransitionProperties() { return sTransitionProperties; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ChangeImageTransform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */