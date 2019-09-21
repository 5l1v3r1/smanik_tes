package com.wang.avi;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public abstract class Indicator extends Drawable implements Animatable {
  private static final Rect ZERO_BOUNDS_RECT = new Rect();
  
  private int alpha = 255;
  
  protected Rect drawBounds = ZERO_BOUNDS_RECT;
  
  private ArrayList<ValueAnimator> mAnimators;
  
  private boolean mHasAnimators;
  
  private Paint mPaint = new Paint();
  
  private HashMap<ValueAnimator, ValueAnimator.AnimatorUpdateListener> mUpdateListeners = new HashMap();
  
  public Indicator() {
    this.mPaint.setColor(-1);
    this.mPaint.setStyle(Paint.Style.FILL);
    this.mPaint.setAntiAlias(true);
  }
  
  private void ensureAnimators() {
    if (!this.mHasAnimators) {
      this.mAnimators = onCreateAnimators();
      this.mHasAnimators = true;
    } 
  }
  
  private boolean isStarted() {
    Iterator iterator = this.mAnimators.iterator();
    return iterator.hasNext() ? ((ValueAnimator)iterator.next()).isStarted() : 0;
  }
  
  private void startAnimators() {
    for (byte b = 0; b < this.mAnimators.size(); b++) {
      ValueAnimator valueAnimator = (ValueAnimator)this.mAnimators.get(b);
      ValueAnimator.AnimatorUpdateListener animatorUpdateListener = (ValueAnimator.AnimatorUpdateListener)this.mUpdateListeners.get(valueAnimator);
      if (animatorUpdateListener != null)
        valueAnimator.addUpdateListener(animatorUpdateListener); 
      valueAnimator.start();
    } 
  }
  
  private void stopAnimators() {
    if (this.mAnimators != null)
      for (ValueAnimator valueAnimator : this.mAnimators) {
        if (valueAnimator != null && valueAnimator.isStarted()) {
          valueAnimator.removeAllUpdateListeners();
          valueAnimator.end();
        } 
      }  
  }
  
  public void addUpdateListener(ValueAnimator paramValueAnimator, ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener) { this.mUpdateListeners.put(paramValueAnimator, paramAnimatorUpdateListener); }
  
  public int centerX() { return this.drawBounds.centerX(); }
  
  public int centerY() { return this.drawBounds.centerY(); }
  
  public void draw(Canvas paramCanvas) { draw(paramCanvas, this.mPaint); }
  
  public abstract void draw(Canvas paramCanvas, Paint paramPaint);
  
  public float exactCenterX() { return this.drawBounds.exactCenterX(); }
  
  public float exactCenterY() { return this.drawBounds.exactCenterY(); }
  
  public int getAlpha() { return this.alpha; }
  
  public int getColor() { return this.mPaint.getColor(); }
  
  public Rect getDrawBounds() { return this.drawBounds; }
  
  public int getHeight() { return this.drawBounds.height(); }
  
  public int getOpacity() { return -1; }
  
  public int getWidth() { return this.drawBounds.width(); }
  
  public boolean isRunning() {
    Iterator iterator = this.mAnimators.iterator();
    return iterator.hasNext() ? ((ValueAnimator)iterator.next()).isRunning() : 0;
  }
  
  protected void onBoundsChange(Rect paramRect) {
    super.onBoundsChange(paramRect);
    setDrawBounds(paramRect);
  }
  
  public abstract ArrayList<ValueAnimator> onCreateAnimators();
  
  public void postInvalidate() { invalidateSelf(); }
  
  public void setAlpha(int paramInt) { this.alpha = paramInt; }
  
  public void setColor(int paramInt) { this.mPaint.setColor(paramInt); }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setDrawBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.drawBounds = new Rect(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void setDrawBounds(Rect paramRect) { setDrawBounds(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom); }
  
  public void start() {
    ensureAnimators();
    if (this.mAnimators == null)
      return; 
    if (isStarted())
      return; 
    startAnimators();
    invalidateSelf();
  }
  
  public void stop() { stopAnimators(); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/Indicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */