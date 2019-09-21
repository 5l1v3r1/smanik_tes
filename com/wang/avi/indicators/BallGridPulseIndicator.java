package com.wang.avi.indicators;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.wang.avi.Indicator;
import java.util.ArrayList;

public class BallGridPulseIndicator extends Indicator {
  public static final int ALPHA = 255;
  
  public static final float SCALE = 1.0F;
  
  int[] alphas = { 255, 255, 255, 255, 255, 255, 255, 255, 255 };
  
  float[] scaleFloats = { 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F };
  
  public void draw(Canvas paramCanvas, Paint paramPaint) {
    float f1 = (getWidth() - 16.0F) / 6.0F;
    float f2 = (getWidth() / 2);
    float f3 = 2.0F * f1;
    float f4 = f3 + 4.0F;
    float f5 = (getWidth() / 2);
    byte b;
    for (b = 0; b < 3; b++) {
      byte b1;
      for (b1 = 0; b1 < 3; b1++) {
        paramCanvas.save();
        float f6 = b1;
        float f7 = b;
        paramCanvas.translate(f3 * f6 + f2 - f4 + f6 * 4.0F, f3 * f7 + f5 - f4 + f7 * 4.0F);
        float[] arrayOfFloat = this.scaleFloats;
        byte b2 = b * 3 + b1;
        paramCanvas.scale(arrayOfFloat[b2], this.scaleFloats[b2]);
        paramPaint.setAlpha(this.alphas[b2]);
        paramCanvas.drawCircle(0.0F, 0.0F, f1, paramPaint);
        paramCanvas.restore();
      } 
    } 
  }
  
  public ArrayList<ValueAnimator> onCreateAnimators() {
    ArrayList arrayList = new ArrayList();
    int[] arrayOfInt1 = new int[9];
    arrayOfInt1[0] = 720;
    arrayOfInt1[1] = 1020;
    arrayOfInt1[2] = 1280;
    arrayOfInt1[3] = 1420;
    arrayOfInt1[4] = 1450;
    arrayOfInt1[5] = 1180;
    arrayOfInt1[6] = 870;
    arrayOfInt1[7] = 1450;
    arrayOfInt1[8] = 1060;
    arrayOfInt1;
    int[] arrayOfInt2 = new int[9];
    arrayOfInt2[0] = -60;
    arrayOfInt2[1] = 250;
    arrayOfInt2[2] = -170;
    arrayOfInt2[3] = 480;
    arrayOfInt2[4] = 310;
    arrayOfInt2[5] = 30;
    arrayOfInt2[6] = 460;
    arrayOfInt2[7] = 780;
    arrayOfInt2[8] = 450;
    arrayOfInt2;
    for (final byte index = 0; b < 9; b++) {
      ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(new float[] { 1.0F, 0.5F, 1.0F });
      valueAnimator1.setDuration(arrayOfInt1[b]);
      valueAnimator1.setRepeatCount(-1);
      valueAnimator1.setStartDelay(arrayOfInt2[b]);
      addUpdateListener(valueAnimator1, new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
              BallGridPulseIndicator.this.scaleFloats[index] = ((Float)param1ValueAnimator.getAnimatedValue()).floatValue();
              BallGridPulseIndicator.this.postInvalidate();
            }
          });
      ValueAnimator valueAnimator2 = ValueAnimator.ofInt(new int[] { 255, 210, 122, 255 });
      valueAnimator2.setDuration(arrayOfInt1[b]);
      valueAnimator2.setRepeatCount(-1);
      valueAnimator2.setStartDelay(arrayOfInt2[b]);
      addUpdateListener(valueAnimator2, new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
              BallGridPulseIndicator.this.alphas[index] = ((Integer)param1ValueAnimator.getAnimatedValue()).intValue();
              BallGridPulseIndicator.this.postInvalidate();
            }
          });
      arrayList.add(valueAnimator1);
      arrayList.add(valueAnimator2);
    } 
    return arrayList;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/indicators/BallGridPulseIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */