package android.support.design.widget;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SwipeDismissBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
  private static final float DEFAULT_ALPHA_END_DISTANCE = 0.5F;
  
  private static final float DEFAULT_ALPHA_START_DISTANCE = 0.0F;
  
  private static final float DEFAULT_DRAG_DISMISS_THRESHOLD = 0.5F;
  
  public static final int STATE_DRAGGING = 1;
  
  public static final int STATE_IDLE = 0;
  
  public static final int STATE_SETTLING = 2;
  
  public static final int SWIPE_DIRECTION_ANY = 2;
  
  public static final int SWIPE_DIRECTION_END_TO_START = 1;
  
  public static final int SWIPE_DIRECTION_START_TO_END = 0;
  
  float mAlphaEndSwipeDistance = 0.5F;
  
  float mAlphaStartSwipeDistance = 0.0F;
  
  private final ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
      private static final int INVALID_POINTER_ID = -1;
      
      private int mActivePointerId = -1;
      
      private int mOriginalCapturedViewLeft;
      
      private boolean shouldDismiss(View param1View, float param1Float) {
        boolean bool = false;
        if (param1Float != 0.0F) {
          boolean bool1;
          if (ViewCompat.getLayoutDirection(param1View) == 1) {
            bool1 = true;
          } else {
            bool1 = false;
          } 
          return (SwipeDismissBehavior.this.mSwipeDirection == 2) ? true : ((SwipeDismissBehavior.this.mSwipeDirection == 0) ? ((bool1 ? (param1Float < 0.0F) : (param1Float > 0.0F))) : ((SwipeDismissBehavior.this.mSwipeDirection == 1) ? ((bool1 ? (param1Float > 0.0F) : (param1Float < 0.0F))) : false));
        } 
        int i = param1View.getLeft();
        int j = this.mOriginalCapturedViewLeft;
        int k = Math.round(param1View.getWidth() * SwipeDismissBehavior.this.mDragDismissThreshold);
        if (Math.abs(i - j) >= k)
          bool = true; 
        return bool;
      }
      
      public int clampViewPositionHorizontal(View param1View, int param1Int1, int param1Int2) {
        int i;
        if (ViewCompat.getLayoutDirection(param1View) == 1) {
          param1Int2 = 1;
        } else {
          param1Int2 = 0;
        } 
        if (SwipeDismissBehavior.this.mSwipeDirection == 0) {
          if (param1Int2 != 0) {
            i = this.mOriginalCapturedViewLeft - param1View.getWidth();
            param1Int2 = this.mOriginalCapturedViewLeft;
          } else {
            i = this.mOriginalCapturedViewLeft;
            param1Int2 = this.mOriginalCapturedViewLeft;
            param1Int2 = param1View.getWidth() + param1Int2;
          } 
        } else if (SwipeDismissBehavior.this.mSwipeDirection == 1) {
          if (param1Int2 != 0) {
            i = this.mOriginalCapturedViewLeft;
            param1Int2 = this.mOriginalCapturedViewLeft;
            param1Int2 = param1View.getWidth() + param1Int2;
          } else {
            i = this.mOriginalCapturedViewLeft - param1View.getWidth();
            param1Int2 = this.mOriginalCapturedViewLeft;
          } 
        } else {
          i = this.mOriginalCapturedViewLeft - param1View.getWidth();
          param1Int2 = this.mOriginalCapturedViewLeft;
          param1Int2 = param1View.getWidth() + param1Int2;
        } 
        return SwipeDismissBehavior.clamp(i, param1Int1, param1Int2);
      }
      
      public int clampViewPositionVertical(View param1View, int param1Int1, int param1Int2) { return param1View.getTop(); }
      
      public int getViewHorizontalDragRange(View param1View) { return param1View.getWidth(); }
      
      public void onViewCaptured(View param1View, int param1Int) {
        this.mActivePointerId = param1Int;
        this.mOriginalCapturedViewLeft = param1View.getLeft();
        ViewParent viewParent = param1View.getParent();
        if (viewParent != null)
          viewParent.requestDisallowInterceptTouchEvent(true); 
      }
      
      public void onViewDragStateChanged(int param1Int) {
        if (SwipeDismissBehavior.this.mListener != null)
          SwipeDismissBehavior.this.mListener.onDragStateChanged(param1Int); 
      }
      
      public void onViewPositionChanged(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
        float f1 = this.mOriginalCapturedViewLeft + param1View.getWidth() * SwipeDismissBehavior.this.mAlphaStartSwipeDistance;
        float f2 = this.mOriginalCapturedViewLeft + param1View.getWidth() * SwipeDismissBehavior.this.mAlphaEndSwipeDistance;
        float f3 = param1Int1;
        if (f3 <= f1) {
          param1View.setAlpha(1.0F);
          return;
        } 
        if (f3 >= f2) {
          param1View.setAlpha(0.0F);
          return;
        } 
        param1View.setAlpha(SwipeDismissBehavior.clamp(0.0F, 1.0F - SwipeDismissBehavior.fraction(f1, f2, f3), 1.0F));
      }
      
      public void onViewReleased(View param1View, float param1Float1, float param1Float2) {
        boolean bool;
        this.mActivePointerId = -1;
        int i = param1View.getWidth();
        if (shouldDismiss(param1View, param1Float1)) {
          if (param1View.getLeft() < this.mOriginalCapturedViewLeft) {
            i = this.mOriginalCapturedViewLeft - i;
          } else {
            i = this.mOriginalCapturedViewLeft + i;
          } 
          bool = true;
        } else {
          i = this.mOriginalCapturedViewLeft;
          bool = false;
        } 
        if (SwipeDismissBehavior.this.mViewDragHelper.settleCapturedViewAt(i, param1View.getTop())) {
          ViewCompat.postOnAnimation(param1View, new SwipeDismissBehavior.SettleRunnable(SwipeDismissBehavior.this, param1View, bool));
          return;
        } 
        if (bool && SwipeDismissBehavior.this.mListener != null)
          SwipeDismissBehavior.this.mListener.onDismiss(param1View); 
      }
      
      public boolean tryCaptureView(View param1View, int param1Int) { return (this.mActivePointerId == -1 && SwipeDismissBehavior.this.canSwipeDismissView(param1View)); }
    };
  
  float mDragDismissThreshold = 0.5F;
  
  private boolean mInterceptingEvents;
  
  OnDismissListener mListener;
  
  private float mSensitivity = 0.0F;
  
  private boolean mSensitivitySet;
  
  int mSwipeDirection = 2;
  
  ViewDragHelper mViewDragHelper;
  
  static float clamp(float paramFloat1, float paramFloat2, float paramFloat3) { return Math.min(Math.max(paramFloat1, paramFloat2), paramFloat3); }
  
  static int clamp(int paramInt1, int paramInt2, int paramInt3) { return Math.min(Math.max(paramInt1, paramInt2), paramInt3); }
  
  private void ensureViewDragHelper(ViewGroup paramViewGroup) {
    if (this.mViewDragHelper == null) {
      ViewDragHelper viewDragHelper;
      if (this.mSensitivitySet) {
        viewDragHelper = ViewDragHelper.create(paramViewGroup, this.mSensitivity, this.mDragCallback);
      } else {
        viewDragHelper = ViewDragHelper.create(viewDragHelper, this.mDragCallback);
      } 
      this.mViewDragHelper = viewDragHelper;
    } 
  }
  
  static float fraction(float paramFloat1, float paramFloat2, float paramFloat3) { return (paramFloat3 - paramFloat1) / (paramFloat2 - paramFloat1); }
  
  public boolean canSwipeDismissView(@NonNull View paramView) { return true; }
  
  public int getDragState() { return (this.mViewDragHelper != null) ? this.mViewDragHelper.getViewDragState() : 0; }
  
  public boolean onInterceptTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent) {
    boolean bool = this.mInterceptingEvents;
    int i = paramMotionEvent.getActionMasked();
    if (i != 3) {
      switch (i) {
        case 0:
          this.mInterceptingEvents = paramCoordinatorLayout.isPointInChildBounds(paramV, (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
          bool = this.mInterceptingEvents;
          break;
        case 1:
          this.mInterceptingEvents = false;
          break;
      } 
      if (bool) {
        ensureViewDragHelper(paramCoordinatorLayout);
        return this.mViewDragHelper.shouldInterceptTouchEvent(paramMotionEvent);
      } 
      return false;
    } 
  }
  
  public boolean onTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent) {
    if (this.mViewDragHelper != null) {
      this.mViewDragHelper.processTouchEvent(paramMotionEvent);
      return true;
    } 
    return false;
  }
  
  public void setDragDismissDistance(float paramFloat) { this.mDragDismissThreshold = clamp(0.0F, paramFloat, 1.0F); }
  
  public void setEndAlphaSwipeDistance(float paramFloat) { this.mAlphaEndSwipeDistance = clamp(0.0F, paramFloat, 1.0F); }
  
  public void setListener(OnDismissListener paramOnDismissListener) { this.mListener = paramOnDismissListener; }
  
  public void setSensitivity(float paramFloat) {
    this.mSensitivity = paramFloat;
    this.mSensitivitySet = true;
  }
  
  public void setStartAlphaSwipeDistance(float paramFloat) { this.mAlphaStartSwipeDistance = clamp(0.0F, paramFloat, 1.0F); }
  
  public void setSwipeDirection(int paramInt) { this.mSwipeDirection = paramInt; }
  
  public static interface OnDismissListener {
    void onDismiss(View param1View);
    
    void onDragStateChanged(int param1Int);
  }
  
  private class SettleRunnable implements Runnable {
    private final boolean mDismiss;
    
    private final View mView;
    
    SettleRunnable(View param1View, boolean param1Boolean) {
      this.mView = param1View;
      this.mDismiss = param1Boolean;
    }
    
    public void run() {
      if (SwipeDismissBehavior.this.mViewDragHelper != null && SwipeDismissBehavior.this.mViewDragHelper.continueSettling(true)) {
        ViewCompat.postOnAnimation(this.mView, this);
        return;
      } 
      if (this.mDismiss && SwipeDismissBehavior.this.mListener != null)
        SwipeDismissBehavior.this.mListener.onDismiss(this.mView); 
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  private static @interface SwipeDirection {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/SwipeDismissBehavior.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */