package com.wang.avi;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.wang.avi.indicators.BallPulseIndicator;

public class AVLoadingIndicatorView extends View {
  private static final BallPulseIndicator DEFAULT_INDICATOR = new BallPulseIndicator();
  
  private static final int MIN_DELAY = 500;
  
  private static final int MIN_SHOW_TIME = 500;
  
  private static final String TAG = "AVLoadingIndicatorView";
  
  private final Runnable mDelayedHide = new Runnable() {
      public void run() {
        AVLoadingIndicatorView.access$002(AVLoadingIndicatorView.this, false);
        AVLoadingIndicatorView.access$102(AVLoadingIndicatorView.this, -1L);
        AVLoadingIndicatorView.this.setVisibility(8);
      }
    };
  
  private final Runnable mDelayedShow = new Runnable() {
      public void run() {
        AVLoadingIndicatorView.access$202(AVLoadingIndicatorView.this, false);
        if (!AVLoadingIndicatorView.this.mDismissed) {
          AVLoadingIndicatorView.access$102(AVLoadingIndicatorView.this, System.currentTimeMillis());
          AVLoadingIndicatorView.this.setVisibility(0);
        } 
      }
    };
  
  private boolean mDismissed = false;
  
  private Indicator mIndicator;
  
  private int mIndicatorColor;
  
  int mMaxHeight;
  
  int mMaxWidth;
  
  int mMinHeight;
  
  int mMinWidth;
  
  private boolean mPostedHide = false;
  
  private boolean mPostedShow = false;
  
  private boolean mShouldStartAnimationDrawable;
  
  private long mStartTime = -1L;
  
  public AVLoadingIndicatorView(Context paramContext) {
    super(paramContext);
    init(paramContext, null, 0, 0);
  }
  
  public AVLoadingIndicatorView(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    init(paramContext, paramAttributeSet, 0, R.style.AVLoadingIndicatorView);
  }
  
  public AVLoadingIndicatorView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet, paramInt, R.style.AVLoadingIndicatorView);
  }
  
  @TargetApi(21)
  public AVLoadingIndicatorView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    init(paramContext, paramAttributeSet, paramInt1, R.style.AVLoadingIndicatorView);
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
    this.mMinWidth = 24;
    this.mMaxWidth = 48;
    this.mMinHeight = 24;
    this.mMaxHeight = 48;
    TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AVLoadingIndicatorView, paramInt1, paramInt2);
    this.mMinWidth = typedArray.getDimensionPixelSize(R.styleable.AVLoadingIndicatorView_minWidth, this.mMinWidth);
    this.mMaxWidth = typedArray.getDimensionPixelSize(R.styleable.AVLoadingIndicatorView_maxWidth, this.mMaxWidth);
    this.mMinHeight = typedArray.getDimensionPixelSize(R.styleable.AVLoadingIndicatorView_minHeight, this.mMinHeight);
    this.mMaxHeight = typedArray.getDimensionPixelSize(R.styleable.AVLoadingIndicatorView_maxHeight, this.mMaxHeight);
    String str = typedArray.getString(R.styleable.AVLoadingIndicatorView_indicatorName);
    this.mIndicatorColor = typedArray.getColor(R.styleable.AVLoadingIndicatorView_indicatorColor, -1);
    setIndicator(str);
    if (this.mIndicator == null)
      setIndicator(DEFAULT_INDICATOR); 
    typedArray.recycle();
  }
  
  private void removeCallbacks() {
    removeCallbacks(this.mDelayedHide);
    removeCallbacks(this.mDelayedShow);
  }
  
  private void updateDrawableBounds(int paramInt1, int paramInt2) {
    int k;
    int i = paramInt1 - getPaddingRight() + getPaddingLeft();
    int j = paramInt2 - getPaddingTop() + getPaddingBottom();
    if (this.mIndicator != null) {
      paramInt1 = this.mIndicator.getIntrinsicWidth();
      paramInt2 = this.mIndicator.getIntrinsicHeight();
      float f1 = paramInt1 / paramInt2;
      float f2 = i;
      float f3 = j;
      float f4 = f2 / f3;
      k = 0;
      paramInt1 = 0;
      paramInt2 = i;
      if (f1 != f4)
        if (f4 > f1) {
          k = (int)(f3 * f1);
          paramInt2 = (i - k) / 2;
          paramInt1 = paramInt2;
          paramInt2 = k + paramInt2;
        } else {
          paramInt1 = (int)(f2 * 1.0F / f1);
          int m = (j - paramInt1) / 2;
          paramInt1 += m;
          paramInt2 = k;
          j = i;
          k = m;
          this.mIndicator.setBounds(paramInt2, k, j, paramInt1);
        }  
      i = j;
      k = 0;
      j = paramInt2;
      paramInt2 = paramInt1;
      paramInt1 = i;
    } else {
      return;
    } 
    this.mIndicator.setBounds(paramInt2, k, j, paramInt1);
  }
  
  private void updateDrawableState() {
    int[] arrayOfInt = getDrawableState();
    if (this.mIndicator != null && this.mIndicator.isStateful())
      this.mIndicator.setState(arrayOfInt); 
  }
  
  void drawTrack(Canvas paramCanvas) {
    Indicator indicator = this.mIndicator;
    if (indicator != null) {
      int i = paramCanvas.save();
      paramCanvas.translate(getPaddingLeft(), getPaddingTop());
      indicator.draw(paramCanvas);
      paramCanvas.restoreToCount(i);
      if (this.mShouldStartAnimationDrawable && indicator instanceof Animatable) {
        ((Animatable)indicator).start();
        this.mShouldStartAnimationDrawable = false;
      } 
    } 
  }
  
  @TargetApi(21)
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2) {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    if (this.mIndicator != null)
      this.mIndicator.setHotspot(paramFloat1, paramFloat2); 
  }
  
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    updateDrawableState();
  }
  
  public Indicator getIndicator() { return this.mIndicator; }
  
  public void hide() {
    this.mDismissed = true;
    removeCallbacks(this.mDelayedShow);
    long l = System.currentTimeMillis() - this.mStartTime;
    if (l >= 500L || this.mStartTime == -1L) {
      setVisibility(8);
      return;
    } 
    if (!this.mPostedHide) {
      postDelayed(this.mDelayedHide, 500L - l);
      this.mPostedHide = true;
      return;
    } 
  }
  
  public void invalidateDrawable(Drawable paramDrawable) {
    Rect rect;
    if (verifyDrawable(paramDrawable)) {
      rect = paramDrawable.getBounds();
      int i = getScrollX() + getPaddingLeft();
      int j = getScrollY() + getPaddingTop();
      invalidate(rect.left + i, rect.top + j, rect.right + i, rect.bottom + j);
      return;
    } 
    super.invalidateDrawable(rect);
  }
  
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    startAnimation();
    removeCallbacks();
  }
  
  protected void onDetachedFromWindow() {
    stopAnimation();
    super.onDetachedFromWindow();
    removeCallbacks();
  }
  
  protected void onDraw(Canvas paramCanvas) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokespecial onDraw : (Landroid/graphics/Canvas;)V
    //   7: aload_0
    //   8: aload_1
    //   9: invokevirtual drawTrack : (Landroid/graphics/Canvas;)V
    //   12: aload_0
    //   13: monitorexit
    //   14: return
    //   15: astore_1
    //   16: aload_0
    //   17: monitorexit
    //   18: aload_1
    //   19: athrow
    // Exception table:
    //   from	to	target	type
    //   2	12	15	finally }
  
  protected void onMeasure(int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mIndicator : Lcom/wang/avi/Indicator;
    //   6: astore #9
    //   8: aload #9
    //   10: ifnull -> 124
    //   13: aload_0
    //   14: getfield mMinWidth : I
    //   17: aload_0
    //   18: getfield mMaxWidth : I
    //   21: aload #9
    //   23: invokevirtual getIntrinsicWidth : ()I
    //   26: invokestatic min : (II)I
    //   29: invokestatic max : (II)I
    //   32: istore #4
    //   34: aload_0
    //   35: getfield mMinHeight : I
    //   38: aload_0
    //   39: getfield mMaxHeight : I
    //   42: aload #9
    //   44: invokevirtual getIntrinsicHeight : ()I
    //   47: invokestatic min : (II)I
    //   50: invokestatic max : (II)I
    //   53: istore_3
    //   54: goto -> 57
    //   57: aload_0
    //   58: invokespecial updateDrawableState : ()V
    //   61: aload_0
    //   62: invokevirtual getPaddingLeft : ()I
    //   65: istore #5
    //   67: aload_0
    //   68: invokevirtual getPaddingRight : ()I
    //   71: istore #6
    //   73: aload_0
    //   74: invokevirtual getPaddingTop : ()I
    //   77: istore #7
    //   79: aload_0
    //   80: invokevirtual getPaddingBottom : ()I
    //   83: istore #8
    //   85: aload_0
    //   86: iload #4
    //   88: iload #5
    //   90: iload #6
    //   92: iadd
    //   93: iadd
    //   94: iload_1
    //   95: iconst_0
    //   96: invokestatic resolveSizeAndState : (III)I
    //   99: iload_3
    //   100: iload #7
    //   102: iload #8
    //   104: iadd
    //   105: iadd
    //   106: iload_2
    //   107: iconst_0
    //   108: invokestatic resolveSizeAndState : (III)I
    //   111: invokevirtual setMeasuredDimension : (II)V
    //   114: aload_0
    //   115: monitorexit
    //   116: return
    //   117: astore #9
    //   119: aload_0
    //   120: monitorexit
    //   121: aload #9
    //   123: athrow
    //   124: iconst_0
    //   125: istore_3
    //   126: iconst_0
    //   127: istore #4
    //   129: goto -> 57
    // Exception table:
    //   from	to	target	type
    //   2	8	117	finally
    //   13	54	117	finally
    //   57	114	117	finally }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { updateDrawableBounds(paramInt1, paramInt2); }
  
  protected void onVisibilityChanged(View paramView, int paramInt) {
    super.onVisibilityChanged(paramView, paramInt);
    if (paramInt == 8 || paramInt == 4) {
      stopAnimation();
      return;
    } 
    startAnimation();
  }
  
  public void setIndicator(Indicator paramIndicator) {
    if (this.mIndicator != paramIndicator) {
      if (this.mIndicator != null) {
        this.mIndicator.setCallback(null);
        unscheduleDrawable(this.mIndicator);
      } 
      this.mIndicator = paramIndicator;
      setIndicatorColor(this.mIndicatorColor);
      if (paramIndicator != null)
        paramIndicator.setCallback(this); 
      postInvalidate();
    } 
  }
  
  public void setIndicator(String paramString) {
    if (TextUtils.isEmpty(paramString))
      return; 
    StringBuilder stringBuilder = new StringBuilder();
    if (!paramString.contains(".")) {
      stringBuilder.append(getClass().getPackage().getName());
      stringBuilder.append(".indicators");
      stringBuilder.append(".");
    } 
    stringBuilder.append(paramString);
    try {
      setIndicator((Indicator)Class.forName(stringBuilder.toString()).newInstance());
      return;
    } catch (ClassNotFoundException paramString) {
      Log.e("AVLoadingIndicatorView", "Didn't find your class , check the name again !");
      return;
    } catch (InstantiationException paramString) {
      paramString.printStackTrace();
      return;
    } catch (IllegalAccessException paramString) {
      paramString.printStackTrace();
      return;
    } 
  }
  
  public void setIndicatorColor(int paramInt) {
    this.mIndicatorColor = paramInt;
    this.mIndicator.setColor(paramInt);
  }
  
  public void setVisibility(int paramInt) {
    if (getVisibility() != paramInt) {
      super.setVisibility(paramInt);
      if (paramInt == 8 || paramInt == 4) {
        stopAnimation();
        return;
      } 
      startAnimation();
      return;
    } 
  }
  
  public void show() {
    this.mStartTime = -1L;
    this.mDismissed = false;
    removeCallbacks(this.mDelayedHide);
    if (!this.mPostedShow) {
      postDelayed(this.mDelayedShow, 500L);
      this.mPostedShow = true;
    } 
  }
  
  public void smoothToHide() {
    startAnimation(AnimationUtils.loadAnimation(getContext(), 17432577));
    setVisibility(8);
  }
  
  public void smoothToShow() {
    startAnimation(AnimationUtils.loadAnimation(getContext(), 17432576));
    setVisibility(0);
  }
  
  void startAnimation() {
    if (getVisibility() != 0)
      return; 
    if (this.mIndicator instanceof Animatable)
      this.mShouldStartAnimationDrawable = true; 
    postInvalidate();
  }
  
  void stopAnimation() {
    if (this.mIndicator instanceof Animatable) {
      this.mIndicator.stop();
      this.mShouldStartAnimationDrawable = false;
    } 
    postInvalidate();
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable) { return (paramDrawable == this.mIndicator || super.verifyDrawable(paramDrawable)); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/wang/avi/AVLoadingIndicatorView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */