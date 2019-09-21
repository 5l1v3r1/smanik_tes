package android.support.v4.view;

import android.support.annotation.NonNull;
import android.view.View;

public interface NestedScrollingParent {
  int getNestedScrollAxes();
  
  boolean onNestedFling(@NonNull View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean);
  
  boolean onNestedPreFling(@NonNull View paramView, float paramFloat1, float paramFloat2);
  
  void onNestedPreScroll(@NonNull View paramView, int paramInt1, int paramInt2, @NonNull int[] paramArrayOfInt);
  
  void onNestedScroll(@NonNull View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void onNestedScrollAccepted(@NonNull View paramView1, @NonNull View paramView2, int paramInt);
  
  boolean onStartNestedScroll(@NonNull View paramView1, @NonNull View paramView2, int paramInt);
  
  void onStopNestedScroll(@NonNull View paramView);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/NestedScrollingParent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */