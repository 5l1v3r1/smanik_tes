package com.wang.avi.indicators;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class LineSpinFadeLoaderIndicator extends BallSpinFadeLoaderIndicator {
  public void draw(Canvas paramCanvas, Paint paramPaint) {
    float f = (getWidth() / 10);
    byte b;
    for (b = 0; b < 8; b++) {
      paramCanvas.save();
      BallSpinFadeLoaderIndicator.Point point = circleAt(getWidth(), getHeight(), getWidth() / 2.5F - f, 0.7853981633974483D * b);
      paramCanvas.translate(point.x, point.y);
      paramCanvas.scale(this.scaleFloats[b], this.scaleFloats[b]);
      paramCanvas.rotate((b * 45));
      paramPaint.setAlpha(this.alphas[b]);
      float f1 = -f;
      paramCanvas.drawRoundRect(new RectF(f1, f1 / 1.5F, f * 1.5F, f / 1.5F), 5.0F, 5.0F, paramPaint);
      paramCanvas.restore();
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/indicators/LineSpinFadeLoaderIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */