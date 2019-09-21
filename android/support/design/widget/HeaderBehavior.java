package android.support.design.widget;

import android.content.Context;
import android.support.v4.math.MathUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

abstract class HeaderBehavior<V extends View> extends ViewOffsetBehavior<V> {
  private static final int INVALID_POINTER = -1;
  
  private int mActivePointerId = -1;
  
  private Runnable mFlingRunnable;
  
  private boolean mIsBeingDragged;
  
  private int mLastMotionY;
  
  OverScroller mScroller;
  
  private int mTouchSlop = -1;
  
  private VelocityTracker mVelocityTracker;
  
  public HeaderBehavior() {}
  
  public HeaderBehavior(Context paramContext, AttributeSet paramAttributeSet) { super(paramContext, paramAttributeSet); }
  
  private void ensureVelocityTracker() {
    if (this.mVelocityTracker == null)
      this.mVelocityTracker = VelocityTracker.obtain(); 
  }
  
  boolean canDragView(V paramV) { return false; }
  
  final boolean fling(CoordinatorLayout paramCoordinatorLayout, V paramV, int paramInt1, int paramInt2, float paramFloat) {
    if (this.mFlingRunnable != null) {
      paramV.removeCallbacks(this.mFlingRunnable);
      this.mFlingRunnable = null;
    } 
    if (this.mScroller == null)
      this.mScroller = new OverScroller(paramV.getContext()); 
    this.mScroller.fling(0, getTopAndBottomOffset(), 0, Math.round(paramFloat), 0, 0, paramInt1, paramInt2);
    if (this.mScroller.computeScrollOffset()) {
      this.mFlingRunnable = new FlingRunnable(paramCoordinatorLayout, paramV);
      ViewCompat.postOnAnimation(paramV, this.mFlingRunnable);
      return true;
    } 
    onFlingFinished(paramCoordinatorLayout, paramV);
    return false;
  }
  
  int getMaxDragOffset(V paramV) { return -paramV.getHeight(); }
  
  int getScrollRangeForDragFling(V paramV) { return paramV.getHeight(); }
  
  int getTopBottomOffsetForScrollingSibling() { return getTopAndBottomOffset(); }
  
  void onFlingFinished(CoordinatorLayout paramCoordinatorLayout, V paramV) {}
  
  public boolean onInterceptTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent) {
    int j;
    int i;
    if (this.mTouchSlop < 0)
      this.mTouchSlop = ViewConfiguration.get(paramCoordinatorLayout.getContext()).getScaledTouchSlop(); 
    if (paramMotionEvent.getAction() == 2 && this.mIsBeingDragged)
      return true; 
    switch (paramMotionEvent.getActionMasked()) {
      case 2:
        i = this.mActivePointerId;
        if (i == -1)
          break; 
        i = paramMotionEvent.findPointerIndex(i);
        if (i == -1)
          break; 
        i = (int)paramMotionEvent.getY(i);
        if (Math.abs(i - this.mLastMotionY) > this.mTouchSlop) {
          this.mIsBeingDragged = true;
          this.mLastMotionY = i;
        } 
        break;
      case 1:
      case 3:
        this.mIsBeingDragged = false;
        this.mActivePointerId = -1;
        if (this.mVelocityTracker != null) {
          this.mVelocityTracker.recycle();
          this.mVelocityTracker = null;
        } 
        break;
      case 0:
        this.mIsBeingDragged = false;
        i = (int)paramMotionEvent.getX();
        j = (int)paramMotionEvent.getY();
        if (canDragView(paramV) && paramCoordinatorLayout.isPointInChildBounds(paramV, i, j)) {
          this.mLastMotionY = j;
          this.mActivePointerId = paramMotionEvent.getPointerId(0);
          ensureVelocityTracker();
        } 
        break;
    } 
    if (this.mVelocityTracker != null)
      this.mVelocityTracker.addMovement(paramMotionEvent); 
    return this.mIsBeingDragged;
  }
  
  public boolean onTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent) {
    int k;
    int j;
    int i;
    if (this.mTouchSlop < 0)
      this.mTouchSlop = ViewConfiguration.get(paramCoordinatorLayout.getContext()).getScaledTouchSlop(); 
    switch (paramMotionEvent.getActionMasked()) {
      case 2:
        i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
        if (i == -1)
          return false; 
        k = (int)paramMotionEvent.getY(i);
        j = this.mLastMotionY - k;
        i = j;
        if (!this.mIsBeingDragged) {
          i = j;
          if (Math.abs(j) > this.mTouchSlop) {
            this.mIsBeingDragged = true;
            if (j > 0) {
              i = j - this.mTouchSlop;
            } else {
              i = j + this.mTouchSlop;
            } 
          } 
        } 
        if (this.mIsBeingDragged) {
          this.mLastMotionY = k;
          scroll(paramCoordinatorLayout, paramV, i, getMaxDragOffset(paramV), 0);
        } 
        break;
      case 1:
        if (this.mVelocityTracker != null) {
          this.mVelocityTracker.addMovement(paramMotionEvent);
          this.mVelocityTracker.computeCurrentVelocity(1000);
          float f = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
          fling(paramCoordinatorLayout, paramV, -getScrollRangeForDragFling(paramV), 0, f);
        } 
      case 3:
        this.mIsBeingDragged = false;
        this.mActivePointerId = -1;
        if (this.mVelocityTracker != null) {
          this.mVelocityTracker.recycle();
          this.mVelocityTracker = null;
        } 
        break;
      case 0:
        i = (int)paramMotionEvent.getX();
        j = (int)paramMotionEvent.getY();
        if (paramCoordinatorLayout.isPointInChildBounds(paramV, i, j) && canDragView(paramV)) {
          this.mLastMotionY = j;
          this.mActivePointerId = paramMotionEvent.getPointerId(0);
          ensureVelocityTracker();
          break;
        } 
        return false;
    } 
    if (this.mVelocityTracker != null)
      this.mVelocityTracker.addMovement(paramMotionEvent); 
    return true;
  }
  
  final int scroll(CoordinatorLayout paramCoordinatorLayout, V paramV, int paramInt1, int paramInt2, int paramInt3) { return setHeaderTopBottomOffset(paramCoordinatorLayout, paramV, getTopBottomOffsetForScrollingSibling() - paramInt1, paramInt2, paramInt3); }
  
  int setHeaderTopBottomOffset(CoordinatorLayout paramCoordinatorLayout, V paramV, int paramInt) { return setHeaderTopBottomOffset(paramCoordinatorLayout, paramV, paramInt, -2147483648, 2147483647); }
  
  int setHeaderTopBottomOffset(CoordinatorLayout paramCoordinatorLayout, V paramV, int paramInt1, int paramInt2, int paramInt3) {
    int i = getTopAndBottomOffset();
    if (paramInt2 != 0 && i >= paramInt2 && i <= paramInt3) {
      paramInt1 = MathUtils.clamp(paramInt1, paramInt2, paramInt3);
      if (i != paramInt1) {
        setTopAndBottomOffset(paramInt1);
        return i - paramInt1;
      } 
    } 
    return 0;
  }
  
  private class FlingRunnable implements Runnable {
    private final V mLayout;
    
    private final CoordinatorLayout mParent;
    
    FlingRunnable(CoordinatorLayout param1CoordinatorLayout, V param1V) {
      this.mParent = param1CoordinatorLayout;
      this.mLayout = param1V;
    }
    
    public void run() {
      if (this.mLayout != null && HeaderBehavior.this.mScroller != null) {
        if (HeaderBehavior.this.mScroller.computeScrollOffset()) {
          HeaderBehavior.this.setHeaderTopBottomOffset(this.mParent, this.mLayout, HeaderBehavior.this.mScroller.getCurrY());
          ViewCompat.postOnAnimation(this.mLayout, this);
          return;
        } 
        HeaderBehavior.this.onFlingFinished(this.mParent, this.mLayout);
      } 
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/HeaderBehavior.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */