package com.wang.avi.indicators;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.wang.avi.Indicator;
import java.util.ArrayList;

public class LineScaleIndicator extends Indicator {
  public static final float SCALE = 1.0F;
  
  float[] scaleYFloats = { 1.0F, 1.0F, 1.0F, 1.0F, 1.0F };
  
  public void draw(Canvas paramCanvas, Paint paramPaint) {
    float f1 = (getWidth() / 11);
    float f2 = (getHeight() / 2);
    byte b;
    for (b = 0; b < 5; b++) {
      paramCanvas.save();
      float f3 = (b * 2 + 2);
      float f4 = f1 / 2.0F;
      paramCanvas.translate(f3 * f1 - f4, f2);
      paramCanvas.scale(1.0F, this.scaleYFloats[b]);
      paramCanvas.drawRoundRect(new RectF(-f1 / 2.0F, -getHeight() / 2.5F, f4, getHeight() / 2.5F), 5.0F, 5.0F, paramPaint);
      paramCanvas.restore();
    } 
  }
  
  public ArrayList<ValueAnimator> onCreateAnimators() {
    ArrayList arrayList = new ArrayList();
    for (final byte index = 0; b < 5; b++) {
      ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.4F, 1.0F });
      valueAnimator.setDuration(1000L);
      valueAnimator.setRepeatCount(-1);
      new long[5][0] = 100L;
      new long[5][1] = 200L;
      new long[5][2] = 300L;
      new long[5][3] = 400L;
      new long[5][4] = 500L;
      valueAnimator.setStartDelay(new long[5][b]);
      addUpdateListener(valueAnimator, new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
              LineScaleIndicator.this.scaleYFloats[index] = ((Float)param1ValueAnimator.getAnimatedValue()).floatValue();
              LineScaleIndicator.this.postInvalidate();
            }
          });
      arrayList.add(valueAnimator);
    } 
    return arrayList;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/indicators/LineScaleIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */