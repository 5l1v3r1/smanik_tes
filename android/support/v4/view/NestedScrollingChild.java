package android.support.v4.view;

import android.support.annotation.Nullable;

public interface NestedScrollingChild {
  boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean);
  
  boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2);
  
  boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, @Nullable int[] paramArrayOfInt1, @Nullable int[] paramArrayOfInt2);
  
  boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, @Nullable int[] paramArrayOfInt);
  
  boolean hasNestedScrollingParent();
  
  boolean isNestedScrollingEnabled();
  
  void setNestedScrollingEnabled(boolean paramBoolean);
  
  boolean startNestedScroll(int paramInt);
  
  void stopNestedScroll();
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/NestedScrollingChild.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */