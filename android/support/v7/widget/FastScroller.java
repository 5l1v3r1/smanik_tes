package android.support.v7.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@VisibleForTesting
class FastScroller extends RecyclerView.ItemDecoration implements RecyclerView.OnItemTouchListener {
  private static final int ANIMATION_STATE_FADING_IN = 1;
  
  private static final int ANIMATION_STATE_FADING_OUT = 3;
  
  private static final int ANIMATION_STATE_IN = 2;
  
  private static final int ANIMATION_STATE_OUT = 0;
  
  private static final int DRAG_NONE = 0;
  
  private static final int DRAG_X = 1;
  
  private static final int DRAG_Y = 2;
  
  private static final int[] EMPTY_STATE_SET;
  
  private static final int HIDE_DELAY_AFTER_DRAGGING_MS = 1200;
  
  private static final int HIDE_DELAY_AFTER_VISIBLE_MS = 1500;
  
  private static final int HIDE_DURATION_MS = 500;
  
  private static final int[] PRESSED_STATE_SET = { 16842919 };
  
  private static final int SCROLLBAR_FULL_OPAQUE = 255;
  
  private static final int SHOW_DURATION_MS = 500;
  
  private static final int STATE_DRAGGING = 2;
  
  private static final int STATE_HIDDEN = 0;
  
  private static final int STATE_VISIBLE = 1;
  
  private int mAnimationState = 0;
  
  private int mDragState = 0;
  
  private final Runnable mHideRunnable = new Runnable() {
      public void run() { FastScroller.this.hide(500); }
    };
  
  @VisibleForTesting
  float mHorizontalDragX;
  
  private final int[] mHorizontalRange = new int[2];
  
  @VisibleForTesting
  int mHorizontalThumbCenterX;
  
  private final StateListDrawable mHorizontalThumbDrawable;
  
  private final int mHorizontalThumbHeight;
  
  @VisibleForTesting
  int mHorizontalThumbWidth;
  
  private final Drawable mHorizontalTrackDrawable;
  
  private final int mHorizontalTrackHeight;
  
  private final int mMargin;
  
  private boolean mNeedHorizontalScrollbar = false;
  
  private boolean mNeedVerticalScrollbar = false;
  
  private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
      public void onScrolled(RecyclerView param1RecyclerView, int param1Int1, int param1Int2) { FastScroller.this.updateScrollPosition(param1RecyclerView.computeHorizontalScrollOffset(), param1RecyclerView.computeVerticalScrollOffset()); }
    };
  
  private RecyclerView mRecyclerView;
  
  private int mRecyclerViewHeight = 0;
  
  private int mRecyclerViewWidth = 0;
  
  private final int mScrollbarMinimumRange;
  
  private final ValueAnimator mShowHideAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
  
  private int mState = 0;
  
  @VisibleForTesting
  float mVerticalDragY;
  
  private final int[] mVerticalRange = new int[2];
  
  @VisibleForTesting
  int mVerticalThumbCenterY;
  
  private final StateListDrawable mVerticalThumbDrawable;
  
  @VisibleForTesting
  int mVerticalThumbHeight;
  
  private final int mVerticalThumbWidth;
  
  private final Drawable mVerticalTrackDrawable;
  
  private final int mVerticalTrackWidth;
  
  static  {
    EMPTY_STATE_SET = new int[0];
  }
  
  FastScroller(RecyclerView paramRecyclerView, StateListDrawable paramStateListDrawable1, Drawable paramDrawable1, StateListDrawable paramStateListDrawable2, Drawable paramDrawable2, int paramInt1, int paramInt2, int paramInt3) {
    this.mVerticalThumbDrawable = paramStateListDrawable1;
    this.mVerticalTrackDrawable = paramDrawable1;
    this.mHorizontalThumbDrawable = paramStateListDrawable2;
    this.mHorizontalTrackDrawable = paramDrawable2;
    this.mVerticalThumbWidth = Math.max(paramInt1, paramStateListDrawable1.getIntrinsicWidth());
    this.mVerticalTrackWidth = Math.max(paramInt1, paramDrawable1.getIntrinsicWidth());
    this.mHorizontalThumbHeight = Math.max(paramInt1, paramStateListDrawable2.getIntrinsicWidth());
    this.mHorizontalTrackHeight = Math.max(paramInt1, paramDrawable2.getIntrinsicWidth());
    this.mScrollbarMinimumRange = paramInt2;
    this.mMargin = paramInt3;
    this.mVerticalThumbDrawable.setAlpha(255);
    this.mVerticalTrackDrawable.setAlpha(255);
    this.mShowHideAnimator.addListener(new AnimatorListener(null));
    this.mShowHideAnimator.addUpdateListener(new AnimatorUpdater(null));
    attachToRecyclerView(paramRecyclerView);
  }
  
  private void cancelHide() { this.mRecyclerView.removeCallbacks(this.mHideRunnable); }
  
  private void destroyCallbacks() {
    this.mRecyclerView.removeItemDecoration(this);
    this.mRecyclerView.removeOnItemTouchListener(this);
    this.mRecyclerView.removeOnScrollListener(this.mOnScrollListener);
    cancelHide();
  }
  
  private void drawHorizontalScrollbar(Canvas paramCanvas) {
    int i = this.mRecyclerViewHeight - this.mHorizontalThumbHeight;
    int j = this.mHorizontalThumbCenterX - this.mHorizontalThumbWidth / 2;
    this.mHorizontalThumbDrawable.setBounds(0, 0, this.mHorizontalThumbWidth, this.mHorizontalThumbHeight);
    this.mHorizontalTrackDrawable.setBounds(0, 0, this.mRecyclerViewWidth, this.mHorizontalTrackHeight);
    paramCanvas.translate(0.0F, i);
    this.mHorizontalTrackDrawable.draw(paramCanvas);
    paramCanvas.translate(j, 0.0F);
    this.mHorizontalThumbDrawable.draw(paramCanvas);
    paramCanvas.translate(-j, -i);
  }
  
  private void drawVerticalScrollbar(Canvas paramCanvas) {
    int i = this.mRecyclerViewWidth - this.mVerticalThumbWidth;
    int j = this.mVerticalThumbCenterY - this.mVerticalThumbHeight / 2;
    this.mVerticalThumbDrawable.setBounds(0, 0, this.mVerticalThumbWidth, this.mVerticalThumbHeight);
    this.mVerticalTrackDrawable.setBounds(0, 0, this.mVerticalTrackWidth, this.mRecyclerViewHeight);
    if (isLayoutRTL()) {
      this.mVerticalTrackDrawable.draw(paramCanvas);
      paramCanvas.translate(this.mVerticalThumbWidth, j);
      paramCanvas.scale(-1.0F, 1.0F);
      this.mVerticalThumbDrawable.draw(paramCanvas);
      paramCanvas.scale(1.0F, 1.0F);
      paramCanvas.translate(-this.mVerticalThumbWidth, -j);
      return;
    } 
    paramCanvas.translate(i, 0.0F);
    this.mVerticalTrackDrawable.draw(paramCanvas);
    paramCanvas.translate(0.0F, j);
    this.mVerticalThumbDrawable.draw(paramCanvas);
    paramCanvas.translate(-i, -j);
  }
  
  private int[] getHorizontalRange() {
    this.mHorizontalRange[0] = this.mMargin;
    this.mHorizontalRange[1] = this.mRecyclerViewWidth - this.mMargin;
    return this.mHorizontalRange;
  }
  
  private int[] getVerticalRange() {
    this.mVerticalRange[0] = this.mMargin;
    this.mVerticalRange[1] = this.mRecyclerViewHeight - this.mMargin;
    return this.mVerticalRange;
  }
  
  private void horizontalScrollTo(float paramFloat) {
    int[] arrayOfInt = getHorizontalRange();
    paramFloat = Math.max(arrayOfInt[0], Math.min(arrayOfInt[1], paramFloat));
    if (Math.abs(this.mHorizontalThumbCenterX - paramFloat) < 2.0F)
      return; 
    int i = scrollTo(this.mHorizontalDragX, paramFloat, arrayOfInt, this.mRecyclerView.computeHorizontalScrollRange(), this.mRecyclerView.computeHorizontalScrollOffset(), this.mRecyclerViewWidth);
    if (i != 0)
      this.mRecyclerView.scrollBy(i, 0); 
    this.mHorizontalDragX = paramFloat;
  }
  
  private boolean isLayoutRTL() { return (ViewCompat.getLayoutDirection(this.mRecyclerView) == 1); }
  
  private void requestRedraw() { this.mRecyclerView.invalidate(); }
  
  private void resetHideDelay(int paramInt) {
    cancelHide();
    this.mRecyclerView.postDelayed(this.mHideRunnable, paramInt);
  }
  
  private int scrollTo(float paramFloat1, float paramFloat2, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
    int i = paramArrayOfInt[1] - paramArrayOfInt[0];
    if (i == 0)
      return 0; 
    paramFloat1 = (paramFloat2 - paramFloat1) / i;
    paramInt1 -= paramInt3;
    paramInt3 = (int)(paramFloat1 * paramInt1);
    paramInt2 += paramInt3;
    return (paramInt2 < paramInt1 && paramInt2 >= 0) ? paramInt3 : 0;
  }
  
  private void setState(int paramInt) {
    if (paramInt == 2 && this.mState != 2) {
      this.mVerticalThumbDrawable.setState(PRESSED_STATE_SET);
      cancelHide();
    } 
    if (paramInt == 0) {
      requestRedraw();
    } else {
      show();
    } 
    if (this.mState == 2 && paramInt != 2) {
      this.mVerticalThumbDrawable.setState(EMPTY_STATE_SET);
      resetHideDelay(1200);
    } else if (paramInt == 1) {
      resetHideDelay(1500);
    } 
    this.mState = paramInt;
  }
  
  private void setupCallbacks() {
    this.mRecyclerView.addItemDecoration(this);
    this.mRecyclerView.addOnItemTouchListener(this);
    this.mRecyclerView.addOnScrollListener(this.mOnScrollListener);
  }
  
  private void verticalScrollTo(float paramFloat) {
    int[] arrayOfInt = getVerticalRange();
    paramFloat = Math.max(arrayOfInt[0], Math.min(arrayOfInt[1], paramFloat));
    if (Math.abs(this.mVerticalThumbCenterY - paramFloat) < 2.0F)
      return; 
    int i = scrollTo(this.mVerticalDragY, paramFloat, arrayOfInt, this.mRecyclerView.computeVerticalScrollRange(), this.mRecyclerView.computeVerticalScrollOffset(), this.mRecyclerViewHeight);
    if (i != 0)
      this.mRecyclerView.scrollBy(0, i); 
    this.mVerticalDragY = paramFloat;
  }
  
  public void attachToRecyclerView(@Nullable RecyclerView paramRecyclerView) {
    if (this.mRecyclerView == paramRecyclerView)
      return; 
    if (this.mRecyclerView != null)
      destroyCallbacks(); 
    this.mRecyclerView = paramRecyclerView;
    if (this.mRecyclerView != null)
      setupCallbacks(); 
  }
  
  @VisibleForTesting
  Drawable getHorizontalThumbDrawable() { return this.mHorizontalThumbDrawable; }
  
  @VisibleForTesting
  Drawable getHorizontalTrackDrawable() { return this.mHorizontalTrackDrawable; }
  
  @VisibleForTesting
  Drawable getVerticalThumbDrawable() { return this.mVerticalThumbDrawable; }
  
  @VisibleForTesting
  Drawable getVerticalTrackDrawable() { return this.mVerticalTrackDrawable; }
  
  public void hide() { hide(0); }
  
  @VisibleForTesting
  void hide(int paramInt) {
    switch (this.mAnimationState) {
      default:
        return;
      case 1:
        this.mShowHideAnimator.cancel();
        break;
      case 2:
        break;
    } 
    this.mAnimationState = 3;
    this.mShowHideAnimator.setFloatValues(new float[] { ((Float)this.mShowHideAnimator.getAnimatedValue()).floatValue(), 0.0F });
    this.mShowHideAnimator.setDuration(paramInt);
    this.mShowHideAnimator.start();
  }
  
  public boolean isDragging() { return (this.mState == 2); }
  
  @VisibleForTesting
  boolean isHidden() { return (this.mState == 0); }
  
  @VisibleForTesting
  boolean isPointInsideHorizontalThumb(float paramFloat1, float paramFloat2) { return (paramFloat2 >= (this.mRecyclerViewHeight - this.mHorizontalThumbHeight) && paramFloat1 >= (this.mHorizontalThumbCenterX - this.mHorizontalThumbWidth / 2) && paramFloat1 <= (this.mHorizontalThumbCenterX + this.mHorizontalThumbWidth / 2)); }
  
  @VisibleForTesting
  boolean isPointInsideVerticalThumb(float paramFloat1, float paramFloat2) { return ((isLayoutRTL() ? (paramFloat1 <= (this.mVerticalThumbWidth / 2)) : (paramFloat1 >= (this.mRecyclerViewWidth - this.mVerticalThumbWidth))) && paramFloat2 >= (this.mVerticalThumbCenterY - this.mVerticalThumbHeight / 2) && paramFloat2 <= (this.mVerticalThumbCenterY + this.mVerticalThumbHeight / 2)); }
  
  @VisibleForTesting
  boolean isVisible() { return (this.mState == 1); }
  
  public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState) {
    if (this.mRecyclerViewWidth != this.mRecyclerView.getWidth() || this.mRecyclerViewHeight != this.mRecyclerView.getHeight()) {
      this.mRecyclerViewWidth = this.mRecyclerView.getWidth();
      this.mRecyclerViewHeight = this.mRecyclerView.getHeight();
      setState(0);
      return;
    } 
    if (this.mAnimationState != 0) {
      if (this.mNeedVerticalScrollbar)
        drawVerticalScrollbar(paramCanvas); 
      if (this.mNeedHorizontalScrollbar)
        drawHorizontalScrollbar(paramCanvas); 
    } 
  }
  
  public boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent) { // Byte code:
    //   0: aload_0
    //   1: getfield mState : I
    //   4: istore_3
    //   5: iconst_0
    //   6: istore #5
    //   8: iload_3
    //   9: iconst_1
    //   10: if_icmpne -> 117
    //   13: aload_0
    //   14: aload_2
    //   15: invokevirtual getX : ()F
    //   18: aload_2
    //   19: invokevirtual getY : ()F
    //   22: invokevirtual isPointInsideVerticalThumb : (FF)Z
    //   25: istore #6
    //   27: aload_0
    //   28: aload_2
    //   29: invokevirtual getX : ()F
    //   32: aload_2
    //   33: invokevirtual getY : ()F
    //   36: invokevirtual isPointInsideHorizontalThumb : (FF)Z
    //   39: istore #7
    //   41: iload #5
    //   43: istore #4
    //   45: aload_2
    //   46: invokevirtual getAction : ()I
    //   49: ifne -> 132
    //   52: iload #6
    //   54: ifne -> 66
    //   57: iload #5
    //   59: istore #4
    //   61: iload #7
    //   63: ifeq -> 132
    //   66: iload #7
    //   68: ifeq -> 89
    //   71: aload_0
    //   72: iconst_1
    //   73: putfield mDragState : I
    //   76: aload_0
    //   77: aload_2
    //   78: invokevirtual getX : ()F
    //   81: f2i
    //   82: i2f
    //   83: putfield mHorizontalDragX : F
    //   86: goto -> 109
    //   89: iload #6
    //   91: ifeq -> 109
    //   94: aload_0
    //   95: iconst_2
    //   96: putfield mDragState : I
    //   99: aload_0
    //   100: aload_2
    //   101: invokevirtual getY : ()F
    //   104: f2i
    //   105: i2f
    //   106: putfield mVerticalDragY : F
    //   109: aload_0
    //   110: iconst_2
    //   111: invokespecial setState : (I)V
    //   114: goto -> 129
    //   117: iload #5
    //   119: istore #4
    //   121: aload_0
    //   122: getfield mState : I
    //   125: iconst_2
    //   126: if_icmpne -> 132
    //   129: iconst_1
    //   130: istore #4
    //   132: iload #4
    //   134: ireturn }
  
  public void onRequestDisallowInterceptTouchEvent(boolean paramBoolean) {}
  
  public void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent) {
    if (this.mState == 0)
      return; 
    if (paramMotionEvent.getAction() == 0) {
      boolean bool1 = isPointInsideVerticalThumb(paramMotionEvent.getX(), paramMotionEvent.getY());
      boolean bool2 = isPointInsideHorizontalThumb(paramMotionEvent.getX(), paramMotionEvent.getY());
      if (bool1 || bool2) {
        if (bool2) {
          this.mDragState = 1;
          this.mHorizontalDragX = (int)paramMotionEvent.getX();
        } else if (bool1) {
          this.mDragState = 2;
          this.mVerticalDragY = (int)paramMotionEvent.getY();
        } 
        setState(2);
        return;
      } 
    } else {
      if (paramMotionEvent.getAction() == 1 && this.mState == 2) {
        this.mVerticalDragY = 0.0F;
        this.mHorizontalDragX = 0.0F;
        setState(1);
        this.mDragState = 0;
        return;
      } 
      if (paramMotionEvent.getAction() == 2 && this.mState == 2) {
        show();
        if (this.mDragState == 1)
          horizontalScrollTo(paramMotionEvent.getX()); 
        if (this.mDragState == 2)
          verticalScrollTo(paramMotionEvent.getY()); 
      } 
    } 
  }
  
  public void show() {
    int i = this.mAnimationState;
    if (i != 0) {
      if (i != 3)
        return; 
      this.mShowHideAnimator.cancel();
    } 
    this.mAnimationState = 1;
    this.mShowHideAnimator.setFloatValues(new float[] { ((Float)this.mShowHideAnimator.getAnimatedValue()).floatValue(), 1.0F });
    this.mShowHideAnimator.setDuration(500L);
    this.mShowHideAnimator.setStartDelay(0L);
    this.mShowHideAnimator.start();
  }
  
  void updateScrollPosition(int paramInt1, int paramInt2) {
    boolean bool;
    int i = this.mRecyclerView.computeVerticalScrollRange();
    int j = this.mRecyclerViewHeight;
    if (i - j > 0 && this.mRecyclerViewHeight >= this.mScrollbarMinimumRange) {
      bool = true;
    } else {
      bool = false;
    } 
    this.mNeedVerticalScrollbar = bool;
    int k = this.mRecyclerView.computeHorizontalScrollRange();
    int m = this.mRecyclerViewWidth;
    if (k - m > 0 && this.mRecyclerViewWidth >= this.mScrollbarMinimumRange) {
      bool = true;
    } else {
      bool = false;
    } 
    this.mNeedHorizontalScrollbar = bool;
    if (!this.mNeedVerticalScrollbar && !this.mNeedHorizontalScrollbar) {
      if (this.mState != 0)
        setState(0); 
      return;
    } 
    if (this.mNeedVerticalScrollbar) {
      float f1 = paramInt2;
      float f2 = j;
      this.mVerticalThumbCenterY = (int)(f2 * (f1 + f2 / 2.0F) / i);
      this.mVerticalThumbHeight = Math.min(j, j * j / i);
    } 
    if (this.mNeedHorizontalScrollbar) {
      float f1 = paramInt1;
      float f2 = m;
      this.mHorizontalThumbCenterX = (int)(f2 * (f1 + f2 / 2.0F) / k);
      this.mHorizontalThumbWidth = Math.min(m, m * m / k);
    } 
    if (this.mState == 0 || this.mState == 1)
      setState(1); 
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface AnimationState {}
  
  private class AnimatorListener extends AnimatorListenerAdapter {
    private boolean mCanceled = false;
    
    private AnimatorListener() {}
    
    public void onAnimationCancel(Animator param1Animator) { this.mCanceled = true; }
    
    public void onAnimationEnd(Animator param1Animator) {
      if (this.mCanceled) {
        this.mCanceled = false;
        return;
      } 
      if (((Float)FastScroller.this.mShowHideAnimator.getAnimatedValue()).floatValue() == 0.0F) {
        FastScroller.access$302(FastScroller.this, 0);
        FastScroller.this.setState(0);
        return;
      } 
      FastScroller.access$302(FastScroller.this, 2);
      FastScroller.this.requestRedraw();
    }
  }
  
  private class AnimatorUpdater implements ValueAnimator.AnimatorUpdateListener {
    private AnimatorUpdater() {}
    
    public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
      int i = (int)(((Float)param1ValueAnimator.getAnimatedValue()).floatValue() * 255.0F);
      FastScroller.this.mVerticalThumbDrawable.setAlpha(i);
      FastScroller.this.mVerticalTrackDrawable.setAlpha(i);
      FastScroller.this.requestRedraw();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface DragState {}
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface State {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/FastScroller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */