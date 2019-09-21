package android.support.v7.widget;

import android.os.SystemClock;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.ShowableListMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public abstract class ForwardingListener implements View.OnTouchListener, View.OnAttachStateChangeListener {
  private int mActivePointerId;
  
  private Runnable mDisallowIntercept;
  
  private boolean mForwarding;
  
  private final int mLongPressTimeout;
  
  private final float mScaledTouchSlop;
  
  final View mSrc;
  
  private final int mTapTimeout;
  
  private final int[] mTmpLocation = new int[2];
  
  private Runnable mTriggerLongPress;
  
  public ForwardingListener(View paramView) {
    this.mSrc = paramView;
    paramView.setLongClickable(true);
    paramView.addOnAttachStateChangeListener(this);
    this.mScaledTouchSlop = ViewConfiguration.get(paramView.getContext()).getScaledTouchSlop();
    this.mTapTimeout = ViewConfiguration.getTapTimeout();
    this.mLongPressTimeout = (this.mTapTimeout + ViewConfiguration.getLongPressTimeout()) / 2;
  }
  
  private void clearCallbacks() {
    if (this.mTriggerLongPress != null)
      this.mSrc.removeCallbacks(this.mTriggerLongPress); 
    if (this.mDisallowIntercept != null)
      this.mSrc.removeCallbacks(this.mDisallowIntercept); 
  }
  
  private boolean onTouchForwarded(MotionEvent paramMotionEvent) {
    View view = this.mSrc;
    ShowableListMenu showableListMenu = getPopup();
    if (showableListMenu != null) {
      if (!showableListMenu.isShowing())
        return false; 
      DropDownListView dropDownListView = (DropDownListView)showableListMenu.getListView();
      if (dropDownListView != null) {
        if (!dropDownListView.isShown())
          return false; 
        MotionEvent motionEvent = MotionEvent.obtainNoHistory(paramMotionEvent);
        toGlobalMotionEvent(view, motionEvent);
        toLocalMotionEvent(dropDownListView, motionEvent);
        boolean bool = dropDownListView.onForwardedEvent(motionEvent, this.mActivePointerId);
        motionEvent.recycle();
        int i = paramMotionEvent.getActionMasked();
        if (i != 1 && i != 3) {
          i = 1;
        } else {
          i = 0;
        } 
        return (bool && i != 0);
      } 
      return false;
    } 
    return false;
  }
  
  private boolean onTouchObserved(MotionEvent paramMotionEvent) {
    int i;
    View view = this.mSrc;
    if (!view.isEnabled())
      return false; 
    switch (paramMotionEvent.getActionMasked()) {
      default:
        return false;
      case 2:
        i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
        if (i >= 0 && !pointInView(view, paramMotionEvent.getX(i), paramMotionEvent.getY(i), this.mScaledTouchSlop)) {
          clearCallbacks();
          view.getParent().requestDisallowInterceptTouchEvent(true);
          return true;
        } 
        return false;
      case 1:
      case 3:
        clearCallbacks();
        return false;
      case 0:
        break;
    } 
    this.mActivePointerId = paramMotionEvent.getPointerId(0);
    if (this.mDisallowIntercept == null)
      this.mDisallowIntercept = new DisallowIntercept(); 
    view.postDelayed(this.mDisallowIntercept, this.mTapTimeout);
    if (this.mTriggerLongPress == null)
      this.mTriggerLongPress = new TriggerLongPress(); 
    view.postDelayed(this.mTriggerLongPress, this.mLongPressTimeout);
    return false;
  }
  
  private static boolean pointInView(View paramView, float paramFloat1, float paramFloat2, float paramFloat3) {
    float f = -paramFloat3;
    return (paramFloat1 >= f && paramFloat2 >= f && paramFloat1 < (paramView.getRight() - paramView.getLeft()) + paramFloat3 && paramFloat2 < (paramView.getBottom() - paramView.getTop()) + paramFloat3);
  }
  
  private boolean toGlobalMotionEvent(View paramView, MotionEvent paramMotionEvent) {
    int[] arrayOfInt = this.mTmpLocation;
    paramView.getLocationOnScreen(arrayOfInt);
    paramMotionEvent.offsetLocation(arrayOfInt[0], arrayOfInt[1]);
    return true;
  }
  
  private boolean toLocalMotionEvent(View paramView, MotionEvent paramMotionEvent) {
    int[] arrayOfInt = this.mTmpLocation;
    paramView.getLocationOnScreen(arrayOfInt);
    paramMotionEvent.offsetLocation(-arrayOfInt[0], -arrayOfInt[1]);
    return true;
  }
  
  public abstract ShowableListMenu getPopup();
  
  protected boolean onForwardingStarted() {
    ShowableListMenu showableListMenu = getPopup();
    if (showableListMenu != null && !showableListMenu.isShowing())
      showableListMenu.show(); 
    return true;
  }
  
  protected boolean onForwardingStopped() {
    ShowableListMenu showableListMenu = getPopup();
    if (showableListMenu != null && showableListMenu.isShowing())
      showableListMenu.dismiss(); 
    return true;
  }
  
  void onLongPress() {
    clearCallbacks();
    View view = this.mSrc;
    if (view.isEnabled()) {
      if (view.isLongClickable())
        return; 
      if (!onForwardingStarted())
        return; 
      view.getParent().requestDisallowInterceptTouchEvent(true);
      long l = SystemClock.uptimeMillis();
      MotionEvent motionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
      view.onTouchEvent(motionEvent);
      motionEvent.recycle();
      this.mForwarding = true;
      return;
    } 
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
    byte b;
    boolean bool2 = this.mForwarding;
    boolean bool = true;
    if (bool2) {
      if (onTouchForwarded(paramMotionEvent) || !onForwardingStopped()) {
        b = 1;
      } else {
        b = 0;
      } 
    } else {
      byte b1;
      if (onTouchObserved(paramMotionEvent) && onForwardingStarted()) {
        b1 = 1;
      } else {
        b1 = 0;
      } 
      b = b1;
      if (b1) {
        long l = SystemClock.uptimeMillis();
        MotionEvent motionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
        this.mSrc.onTouchEvent(motionEvent);
        motionEvent.recycle();
        b = b1;
      } 
    } 
    this.mForwarding = b;
    boolean bool1 = bool;
    if (b == 0) {
      if (bool2)
        return true; 
      bool1 = false;
    } 
    return bool1;
  }
  
  public void onViewAttachedToWindow(View paramView) {}
  
  public void onViewDetachedFromWindow(View paramView) {
    this.mForwarding = false;
    this.mActivePointerId = -1;
    if (this.mDisallowIntercept != null)
      this.mSrc.removeCallbacks(this.mDisallowIntercept); 
  }
  
  private class DisallowIntercept implements Runnable {
    public void run() {
      ViewParent viewParent = ForwardingListener.this.mSrc.getParent();
      if (viewParent != null)
        viewParent.requestDisallowInterceptTouchEvent(true); 
    }
  }
  
  private class TriggerLongPress implements Runnable {
    public void run() { ForwardingListener.this.onLongPress(); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/ForwardingListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */