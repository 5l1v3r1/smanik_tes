package com.wang.avi.indicators;

import android.animation.ValueAnimator;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.animation.LinearInterpolator;
import com.wang.avi.Indicator;
import java.util.ArrayList;

public class BallPulseRiseIndicator extends Indicator {
  private float degress;
  
  private Camera mCamera = new Camera();
  
  private Matrix mMatrix = new Matrix();
  
  public void draw(Canvas paramCanvas, Paint paramPaint) {
    this.mMatrix.reset();
    this.mCamera.save();
    this.mCamera.rotateX(this.degress);
    this.mCamera.getMatrix(this.mMatrix);
    this.mCamera.restore();
    this.mMatrix.preTranslate(-centerX(), -centerY());
    this.mMatrix.postTranslate(centerX(), centerY());
    paramCanvas.concat(this.mMatrix);
    float f1 = (getWidth() / 10);
    float f2 = (getWidth() / 4);
    float f3 = 2.0F * f1;
    paramCanvas.drawCircle(f2, f3, f1, paramPaint);
    paramCanvas.drawCircle((getWidth() * 3 / 4), f3, f1, paramPaint);
    paramCanvas.drawCircle(f1, getHeight() - f3, f1, paramPaint);
    paramCanvas.drawCircle((getWidth() / 2), getHeight() - f3, f1, paramPaint);
    paramCanvas.drawCircle(getWidth() - f1, getHeight() - f3, f1, paramPaint);
  }
  
  public ArrayList<ValueAnimator> onCreateAnimators() {
    ArrayList arrayList = new ArrayList();
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 360.0F });
    addUpdateListener(valueAnimator, new ValueAnimator.AnimatorUpdateListener() {
          public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
            BallPulseRiseIndicator.access$002(BallPulseRiseIndicator.this, ((Float)param1ValueAnimator.getAnimatedValue()).floatValue());
            BallPulseRiseIndicator.this.postInvalidate();
          }
        });
    valueAnimator.setInterpolator(new LinearInterpolator());
    valueAnimator.setRepeatCount(-1);
    valueAnimator.setDuration(1500L);
    arrayList.add(valueAnimator);
    return arrayList;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/indicators/BallPulseRiseIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */