package com.wang.avi.indicators;

import android.animation.ValueAnimator;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.animation.LinearInterpolator;
import com.wang.avi.Indicator;
import java.util.ArrayList;

public class TriangleSkewSpinIndicator extends Indicator {
  private Camera mCamera = new Camera();
  
  private Matrix mMatrix = new Matrix();
  
  private float rotateX;
  
  private float rotateY;
  
  public void draw(Canvas paramCanvas, Paint paramPaint) {
    this.mMatrix.reset();
    this.mCamera.save();
    this.mCamera.rotateX(this.rotateX);
    this.mCamera.rotateY(this.rotateY);
    this.mCamera.getMatrix(this.mMatrix);
    this.mCamera.restore();
    this.mMatrix.preTranslate(-centerX(), -centerY());
    this.mMatrix.postTranslate(centerX(), centerY());
    paramCanvas.concat(this.mMatrix);
    Path path = new Path();
    path.moveTo((getWidth() / 5), (getHeight() * 4 / 5));
    path.lineTo((getWidth() * 4 / 5), (getHeight() * 4 / 5));
    path.lineTo((getWidth() / 2), (getHeight() / 5));
    path.close();
    paramCanvas.drawPath(path, paramPaint);
  }
  
  public ArrayList<ValueAnimator> onCreateAnimators() {
    ArrayList arrayList = new ArrayList();
    ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(new float[] { 0.0F, 180.0F, 180.0F, 0.0F, 0.0F });
    addUpdateListener(valueAnimator1, new ValueAnimator.AnimatorUpdateListener() {
          public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
            TriangleSkewSpinIndicator.access$002(TriangleSkewSpinIndicator.this, ((Float)param1ValueAnimator.getAnimatedValue()).floatValue());
            TriangleSkewSpinIndicator.this.postInvalidate();
          }
        });
    valueAnimator1.setInterpolator(new LinearInterpolator());
    valueAnimator1.setRepeatCount(-1);
    ValueAnimator valueAnimator2 = valueAnimator1.setDuration(2500L).ofFloat(new float[] { 0.0F, 0.0F, 180.0F, 180.0F, 0.0F });
    addUpdateListener(valueAnimator2, new ValueAnimator.AnimatorUpdateListener() {
          public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
            TriangleSkewSpinIndicator.access$102(TriangleSkewSpinIndicator.this, ((Float)param1ValueAnimator.getAnimatedValue()).floatValue());
            TriangleSkewSpinIndicator.this.postInvalidate();
          }
        });
    valueAnimator2.setInterpolator(new LinearInterpolator());
    valueAnimator2.setRepeatCount(-1);
    valueAnimator2.setDuration(2500L);
    arrayList.add(valueAnimator1);
    arrayList.add(valueAnimator2);
    return arrayList;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/indicators/TriangleSkewSpinIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */