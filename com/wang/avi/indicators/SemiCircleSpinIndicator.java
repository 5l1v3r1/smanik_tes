package com.wang.avi.indicators;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.wang.avi.Indicator;
import java.util.ArrayList;

public class SemiCircleSpinIndicator extends Indicator {
  private float degress;
  
  public void draw(Canvas paramCanvas, Paint paramPaint) {
    paramCanvas.rotate(this.degress, centerX(), centerY());
    paramCanvas.drawArc(new RectF(0.0F, 0.0F, getWidth(), getHeight()), -60.0F, 120.0F, false, paramPaint);
  }
  
  public ArrayList<ValueAnimator> onCreateAnimators() {
    ArrayList arrayList = new ArrayList();
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 180.0F, 360.0F });
    addUpdateListener(valueAnimator, new ValueAnimator.AnimatorUpdateListener() {
          public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
            SemiCircleSpinIndicator.access$002(SemiCircleSpinIndicator.this, ((Float)param1ValueAnimator.getAnimatedValue()).floatValue());
            SemiCircleSpinIndicator.this.postInvalidate();
          }
        });
    valueAnimator.setDuration(600L);
    valueAnimator.setRepeatCount(-1);
    arrayList.add(valueAnimator);
    return arrayList;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/indicators/SemiCircleSpinIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */