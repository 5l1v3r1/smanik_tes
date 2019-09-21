package android.support.v4.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewParent;

public class NestedScrollingChildHelper {
  private boolean mIsNestedScrollingEnabled;
  
  private ViewParent mNestedScrollingParentNonTouch;
  
  private ViewParent mNestedScrollingParentTouch;
  
  private int[] mTempNestedScrollConsumed;
  
  private final View mView;
  
  public NestedScrollingChildHelper(@NonNull View paramView) { this.mView = paramView; }
  
  private ViewParent getNestedScrollingParentForType(int paramInt) {
    switch (paramInt) {
      default:
        return null;
      case 1:
        return this.mNestedScrollingParentNonTouch;
      case 0:
        break;
    } 
    return this.mNestedScrollingParentTouch;
  }
  
  private void setNestedScrollingParentForType(int paramInt, ViewParent paramViewParent) {
    switch (paramInt) {
      default:
        return;
      case 1:
        this.mNestedScrollingParentNonTouch = paramViewParent;
        return;
      case 0:
        break;
    } 
    this.mNestedScrollingParentTouch = paramViewParent;
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean) {
    if (isNestedScrollingEnabled()) {
      ViewParent viewParent = getNestedScrollingParentForType(0);
      if (viewParent != null)
        return ViewParentCompat.onNestedFling(viewParent, this.mView, paramFloat1, paramFloat2, paramBoolean); 
    } 
    return false;
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2) {
    if (isNestedScrollingEnabled()) {
      ViewParent viewParent = getNestedScrollingParentForType(0);
      if (viewParent != null)
        return ViewParentCompat.onNestedPreFling(viewParent, this.mView, paramFloat1, paramFloat2); 
    } 
    return false;
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, @Nullable int[] paramArrayOfInt1, @Nullable int[] paramArrayOfInt2) { return dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2, 0); }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, @Nullable int[] paramArrayOfInt1, @Nullable int[] paramArrayOfInt2, int paramInt3) {
    if (isNestedScrollingEnabled()) {
      ViewParent viewParent = getNestedScrollingParentForType(paramInt3);
      if (viewParent == null)
        return false; 
      boolean bool = true;
      if (paramInt1 != 0 || paramInt2 != 0) {
        int j;
        int i;
        if (paramArrayOfInt2 != null) {
          this.mView.getLocationInWindow(paramArrayOfInt2);
          i = paramArrayOfInt2[0];
          j = paramArrayOfInt2[1];
        } else {
          i = 0;
          j = 0;
        } 
        int[] arrayOfInt = paramArrayOfInt1;
        if (paramArrayOfInt1 == null) {
          if (this.mTempNestedScrollConsumed == null)
            this.mTempNestedScrollConsumed = new int[2]; 
          arrayOfInt = this.mTempNestedScrollConsumed;
        } 
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 0;
        ViewParentCompat.onNestedPreScroll(viewParent, this.mView, paramInt1, paramInt2, arrayOfInt, paramInt3);
        if (paramArrayOfInt2 != null) {
          this.mView.getLocationInWindow(paramArrayOfInt2);
          paramArrayOfInt2[0] = paramArrayOfInt2[0] - i;
          paramArrayOfInt2[1] = paramArrayOfInt2[1] - j;
        } 
        if (arrayOfInt[0] == 0) {
          if (arrayOfInt[1] != 0)
            return true; 
          bool = false;
        } 
        return bool;
      } 
      if (paramArrayOfInt2 != null) {
        paramArrayOfInt2[0] = 0;
        paramArrayOfInt2[1] = 0;
        return false;
      } 
    } 
    return false;
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, @Nullable int[] paramArrayOfInt) { return dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, 0); }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, @Nullable int[] paramArrayOfInt, int paramInt5) {
    if (isNestedScrollingEnabled()) {
      ViewParent viewParent = getNestedScrollingParentForType(paramInt5);
      if (viewParent == null)
        return false; 
      if (paramInt1 != 0 || paramInt2 != 0 || paramInt3 != 0 || paramInt4 != 0) {
        int j;
        int i;
        if (paramArrayOfInt != null) {
          this.mView.getLocationInWindow(paramArrayOfInt);
          i = paramArrayOfInt[0];
          j = paramArrayOfInt[1];
        } else {
          i = 0;
          j = 0;
        } 
        ViewParentCompat.onNestedScroll(viewParent, this.mView, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
        if (paramArrayOfInt != null) {
          this.mView.getLocationInWindow(paramArrayOfInt);
          paramArrayOfInt[0] = paramArrayOfInt[0] - i;
          paramArrayOfInt[1] = paramArrayOfInt[1] - j;
        } 
        return true;
      } 
      if (paramArrayOfInt != null) {
        paramArrayOfInt[0] = 0;
        paramArrayOfInt[1] = 0;
        return false;
      } 
    } 
    return false;
  }
  
  public boolean hasNestedScrollingParent() { return hasNestedScrollingParent(0); }
  
  public boolean hasNestedScrollingParent(int paramInt) { return (getNestedScrollingParentForType(paramInt) != null); }
  
  public boolean isNestedScrollingEnabled() { return this.mIsNestedScrollingEnabled; }
  
  public void onDetachedFromWindow() { ViewCompat.stopNestedScroll(this.mView); }
  
  public void onStopNestedScroll(@NonNull View paramView) { ViewCompat.stopNestedScroll(this.mView); }
  
  public void setNestedScrollingEnabled(boolean paramBoolean) {
    if (this.mIsNestedScrollingEnabled)
      ViewCompat.stopNestedScroll(this.mView); 
    this.mIsNestedScrollingEnabled = paramBoolean;
  }
  
  public boolean startNestedScroll(int paramInt) { return startNestedScroll(paramInt, 0); }
  
  public boolean startNestedScroll(int paramInt1, int paramInt2) {
    if (hasNestedScrollingParent(paramInt2))
      return true; 
    if (isNestedScrollingEnabled()) {
      ViewParent viewParent = this.mView.getParent();
      View view = this.mView;
      while (viewParent != null) {
        if (ViewParentCompat.onStartNestedScroll(viewParent, view, this.mView, paramInt1, paramInt2)) {
          setNestedScrollingParentForType(paramInt2, viewParent);
          ViewParentCompat.onNestedScrollAccepted(viewParent, view, this.mView, paramInt1, paramInt2);
          return true;
        } 
        if (viewParent instanceof View)
          view = (View)viewParent; 
        viewParent = viewParent.getParent();
      } 
    } 
    return false;
  }
  
  public void stopNestedScroll() { stopNestedScroll(0); }
  
  public void stopNestedScroll(int paramInt) {
    ViewParent viewParent = getNestedScrollingParentForType(paramInt);
    if (viewParent != null) {
      ViewParentCompat.onStopNestedScroll(viewParent, this.mView, paramInt);
      setNestedScrollingParentForType(paramInt, null);
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/NestedScrollingChildHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */