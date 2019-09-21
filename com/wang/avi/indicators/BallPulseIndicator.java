package com.wang.avi.indicators;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.wang.avi.Indicator;
import java.util.ArrayList;

public class BallPulseIndicator extends Indicator {
  public static final float SCALE = 1.0F;
  
  private float[] scaleFloats = { 1.0F, 1.0F, 1.0F };
  
  public void draw(Canvas paramCanvas, Paint paramPaint) {
    float f1 = (Math.min(getWidth(), getHeight()) - 8.0F) / 6.0F;
    float f2 = (getWidth() / 2);
    float f3 = 2.0F * f1;
    float f4 = (getHeight() / 2);
    byte b;
    for (b = 0; b < 3; b++) {
      paramCanvas.save();
      float f = b;
      paramCanvas.translate(f3 * f + f2 - f3 + 4.0F + f * 4.0F, f4);
      paramCanvas.scale(this.scaleFloats[b], this.scaleFloats[b]);
      paramCanvas.drawCircle(0.0F, 0.0F, f1, paramPaint);
      paramCanvas.restore();
    } 
  }
  
  public ArrayList<ValueAnimator> onCreateAnimators() {
    ArrayList arrayList = new ArrayList();
    for (final byte index = 0; b < 3; b++) {
      ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.3F, 1.0F });
      valueAnimator.setDuration(750L);
      valueAnimator.setRepeatCount(-1);
      new int[3][0] = 120;
      new int[3][1] = 240;
      new int[3][2] = 360;
      valueAnimator.setStartDelay(new int[3][b]);
      addUpdateListener(valueAnimator, new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
              BallPulseIndicator.this.scaleFloats[index] = ((Float)param1ValueAnimator.getAnimatedValue()).floatValue();
              BallPulseIndicator.this.postInvalidate();
            }
          });
      arrayList.add(valueAnimator);
    } 
    return arrayList;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/indicators/BallPulseIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */