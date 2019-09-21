package com.wang.avi.indicators;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.wang.avi.Indicator;
import java.util.ArrayList;

public class BallSpinFadeLoaderIndicator extends Indicator {
  public static final int ALPHA = 255;
  
  public static final float SCALE = 1.0F;
  
  int[] alphas = { 255, 255, 255, 255, 255, 255, 255, 255 };
  
  float[] scaleFloats = { 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F };
  
  Point circleAt(int paramInt1, int paramInt2, float paramFloat, double paramDouble) {
    double d1 = (paramInt1 / 2);
    double d2 = paramFloat;
    return new Point((float)(d1 + Math.cos(paramDouble) * d2), (float)((paramInt2 / 2) + d2 * Math.sin(paramDouble)));
  }
  
  public void draw(Canvas paramCanvas, Paint paramPaint) {
    float f = (getWidth() / 10);
    byte b;
    for (b = 0; b < 8; b++) {
      paramCanvas.save();
      Point point = circleAt(getWidth(), getHeight(), (getWidth() / 2) - f, 0.7853981633974483D * b);
      paramCanvas.translate(point.x, point.y);
      paramCanvas.scale(this.scaleFloats[b], this.scaleFloats[b]);
      paramPaint.setAlpha(this.alphas[b]);
      paramCanvas.drawCircle(0.0F, 0.0F, f, paramPaint);
      paramCanvas.restore();
    } 
  }
  
  public ArrayList<ValueAnimator> onCreateAnimators() {
    ArrayList arrayList = new ArrayList();
    int[] arrayOfInt = new int[9];
    arrayOfInt[0] = 0;
    arrayOfInt[1] = 120;
    arrayOfInt[2] = 240;
    arrayOfInt[3] = 360;
    arrayOfInt[4] = 480;
    arrayOfInt[5] = 600;
    arrayOfInt[6] = 720;
    arrayOfInt[7] = 780;
    arrayOfInt[8] = 840;
    arrayOfInt;
    for (final byte index = 0; b < 8; b++) {
      ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(new float[] { 1.0F, 0.4F, 1.0F });
      valueAnimator1.setDuration(1000L);
      valueAnimator1.setRepeatCount(-1);
      valueAnimator1.setStartDelay(arrayOfInt[b]);
      addUpdateListener(valueAnimator1, new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
              BallSpinFadeLoaderIndicator.this.scaleFloats[index] = ((Float)param1ValueAnimator.getAnimatedValue()).floatValue();
              BallSpinFadeLoaderIndicator.this.postInvalidate();
            }
          });
      ValueAnimator valueAnimator2 = ValueAnimator.ofInt(new int[] { 255, 77, 255 });
      valueAnimator2.setDuration(1000L);
      valueAnimator2.setRepeatCount(-1);
      valueAnimator2.setStartDelay(arrayOfInt[b]);
      addUpdateListener(valueAnimator2, new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
              BallSpinFadeLoaderIndicator.this.alphas[index] = ((Integer)param1ValueAnimator.getAnimatedValue()).intValue();
              BallSpinFadeLoaderIndicator.this.postInvalidate();
            }
          });
      arrayList.add(valueAnimator1);
      arrayList.add(valueAnimator2);
    } 
    return arrayList;
  }
  
  final class Point {
    public float x;
    
    public float y;
    
    public Point(float param1Float1, float param1Float2) {
      this.x = param1Float1;
      this.y = param1Float2;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/indicators/BallSpinFadeLoaderIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */