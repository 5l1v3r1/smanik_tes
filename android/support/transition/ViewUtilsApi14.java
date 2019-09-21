package android.support.transition;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewParent;

@RequiresApi(14)
class ViewUtilsApi14 implements ViewUtilsImpl {
  private float[] mMatrixValues;
  
  public void clearNonTransitionAlpha(@NonNull View paramView) {
    if (paramView.getVisibility() == 0)
      paramView.setTag(R.id.save_non_transition_alpha, null); 
  }
  
  public ViewOverlayImpl getOverlay(@NonNull View paramView) { return ViewOverlayApi14.createFrom(paramView); }
  
  public float getTransitionAlpha(@NonNull View paramView) {
    Float float = (Float)paramView.getTag(R.id.save_non_transition_alpha);
    return (float != null) ? (paramView.getAlpha() / float.floatValue()) : paramView.getAlpha();
  }
  
  public WindowIdImpl getWindowId(@NonNull View paramView) { return new WindowIdApi14(paramView.getWindowToken()); }
  
  public void saveNonTransitionAlpha(@NonNull View paramView) {
    if (paramView.getTag(R.id.save_non_transition_alpha) == null)
      paramView.setTag(R.id.save_non_transition_alpha, Float.valueOf(paramView.getAlpha())); 
  }
  
  public void setAnimationMatrix(@NonNull View paramView, Matrix paramMatrix) {
    boolean bool;
    if (paramMatrix == null || paramMatrix.isIdentity()) {
      paramView.setPivotX((paramView.getWidth() / 2));
      paramView.setPivotY((paramView.getHeight() / 2));
      paramView.setTranslationX(0.0F);
      paramView.setTranslationY(0.0F);
      paramView.setScaleX(1.0F);
      paramView.setScaleY(1.0F);
      paramView.setRotation(0.0F);
      return;
    } 
    float[] arrayOfFloat2 = this.mMatrixValues;
    float[] arrayOfFloat1 = arrayOfFloat2;
    if (arrayOfFloat2 == null) {
      arrayOfFloat1 = new float[9];
      this.mMatrixValues = arrayOfFloat1;
    } 
    paramMatrix.getValues(arrayOfFloat1);
    float f1 = arrayOfFloat1[3];
    float f2 = (float)Math.sqrt((1.0F - f1 * f1));
    if (arrayOfFloat1[0] < 0.0F) {
      bool = true;
    } else {
      bool = true;
    } 
    float f3 = f2 * bool;
    f1 = (float)Math.toDegrees(Math.atan2(f1, f3));
    f2 = arrayOfFloat1[0] / f3;
    f3 = arrayOfFloat1[4] / f3;
    float f4 = arrayOfFloat1[2];
    float f5 = arrayOfFloat1[5];
    paramView.setPivotX(0.0F);
    paramView.setPivotY(0.0F);
    paramView.setTranslationX(f4);
    paramView.setTranslationY(f5);
    paramView.setRotation(f1);
    paramView.setScaleX(f2);
    paramView.setScaleY(f3);
  }
  
  public void setLeftTopRightBottom(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramView.setLeft(paramInt1);
    paramView.setTop(paramInt2);
    paramView.setRight(paramInt3);
    paramView.setBottom(paramInt4);
  }
  
  public void setTransitionAlpha(@NonNull View paramView, float paramFloat) {
    Float float = (Float)paramView.getTag(R.id.save_non_transition_alpha);
    if (float != null) {
      paramView.setAlpha(float.floatValue() * paramFloat);
      return;
    } 
    paramView.setAlpha(paramFloat);
  }
  
  public void transformMatrixToGlobal(@NonNull View paramView, @NonNull Matrix paramMatrix) {
    ViewParent viewParent = paramView.getParent();
    if (viewParent instanceof View) {
      View view = (View)viewParent;
      transformMatrixToGlobal(view, paramMatrix);
      paramMatrix.preTranslate(-view.getScrollX(), -view.getScrollY());
    } 
    paramMatrix.preTranslate(paramView.getLeft(), paramView.getTop());
    Matrix matrix = paramView.getMatrix();
    if (!matrix.isIdentity())
      paramMatrix.preConcat(matrix); 
  }
  
  public void transformMatrixToLocal(@NonNull View paramView, @NonNull Matrix paramMatrix) {
    ViewParent viewParent = paramView.getParent();
    if (viewParent instanceof View) {
      View view = (View)viewParent;
      transformMatrixToLocal(view, paramMatrix);
      paramMatrix.postTranslate(view.getScrollX(), view.getScrollY());
    } 
    paramMatrix.postTranslate(paramView.getLeft(), paramView.getTop());
    Matrix matrix = paramView.getMatrix();
    if (!matrix.isIdentity()) {
      Matrix matrix1 = new Matrix();
      if (matrix.invert(matrix1))
        paramMatrix.postConcat(matrix1); 
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ViewUtilsApi14.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */