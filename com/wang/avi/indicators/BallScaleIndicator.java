package com.wang.avi.indicators;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.animation.LinearInterpolator;
import com.wang.avi.Indicator;
import java.util.ArrayList;

public class BallScaleIndicator extends Indicator {
  int alpha = 255;
  
  float scale = 1.0F;
  
  public void draw(Canvas paramCanvas, Paint paramPaint) {
    paramPaint.setAlpha(this.alpha);
    paramCanvas.scale(this.scale, this.scale, (getWidth() / 2), (getHeight() / 2));
    paramPaint.setAlpha(this.alpha);
    paramCanvas.drawCircle((getWidth() / 2), (getHeight() / 2), (getWidth() / 2) - 4.0F, paramPaint);
  }
  
  public ArrayList<ValueAnimator> onCreateAnimators() {
    ArrayList arrayList = new ArrayList();
    ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
    valueAnimator1.setInterpolator(new LinearInterpolator());
    valueAnimator1.setDuration(1000L);
    valueAnimator1.setRepeatCount(-1);
    addUpdateListener(valueAnimator1, new ValueAnimator.AnimatorUpdateListener() {
          public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
            BallScaleIndicator.this.scale = ((Float)param1ValueAnimator.getAnimatedValue()).floatValue();
            BallScaleIndicator.this.postInvalidate();
          }
        });
    ValueAnimator valueAnimator2 = ValueAnimator.ofInt(new int[] { 255, 0 });
    valueAnimator2.setInterpolator(new LinearInterpolator());
    valueAnimator2.setDuration(1000L);
    valueAnimator2.setRepeatCount(-1);
    addUpdateListener(valueAnimator2, new ValueAnimator.AnimatorUpdateListener() {
          public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
            BallScaleIndicator.this.alpha = ((Integer)param1ValueAnimator.getAnimatedValue()).intValue();
            BallScaleIndicator.this.postInvalidate();
          }
        });
    arrayList.add(valueAnimator1);
    arrayList.add(valueAnimator2);
    return arrayList;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/indicators/BallScaleIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */