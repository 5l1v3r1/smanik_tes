package com.wang.avi.indicators;

import android.animation.ValueAnimator;
import java.util.ArrayList;

public class LineScalePulseOutIndicator extends LineScaleIndicator {
  public ArrayList<ValueAnimator> onCreateAnimators() {
    ArrayList arrayList = new ArrayList();
    for (final byte index = 0; b < 5; b++) {
      ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.3F, 1.0F });
      valueAnimator.setDuration(900L);
      valueAnimator.setRepeatCount(-1);
      new long[5][0] = 500L;
      new long[5][1] = 250L;
      new long[5][2] = 0L;
      new long[5][3] = 250L;
      new long[5][4] = 500L;
      valueAnimator.setStartDelay(new long[5][b]);
      addUpdateListener(valueAnimator, new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
              LineScalePulseOutIndicator.this.scaleYFloats[index] = ((Float)param1ValueAnimator.getAnimatedValue()).floatValue();
              LineScalePulseOutIndicator.this.postInvalidate();
            }
          });
      arrayList.add(valueAnimator);
    } 
    return arrayList;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/indicators/LineScalePulseOutIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */