package android.support.v4.widget;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;

public final class ListViewCompat {
  public static boolean canScrollList(@NonNull ListView paramListView, int paramInt) {
    if (Build.VERSION.SDK_INT >= 19)
      return paramListView.canScrollList(paramInt); 
    int j = paramListView.getChildCount();
    boolean bool = false;
    null = false;
    if (j == 0)
      return false; 
    int i = paramListView.getFirstVisiblePosition();
    if (paramInt > 0) {
      paramInt = paramListView.getChildAt(j - 1).getBottom();
      if (i + j < paramListView.getCount() || paramInt > paramListView.getHeight() - paramListView.getListPaddingBottom())
        null = true; 
      return null;
    } 
    paramInt = paramListView.getChildAt(0).getTop();
    if (i <= 0) {
      null = bool;
      return (paramInt < paramListView.getListPaddingTop()) ? true : null;
    } 
    return true;
  }
  
  public static void scrollListBy(@NonNull ListView paramListView, int paramInt) {
    if (Build.VERSION.SDK_INT >= 19) {
      paramListView.scrollListBy(paramInt);
      return;
    } 
    int i = paramListView.getFirstVisiblePosition();
    if (i == -1)
      return; 
    View view = paramListView.getChildAt(0);
    if (view == null)
      return; 
    paramListView.setSelectionFromTop(i, view.getTop() - paramInt);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/widget/ListViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */