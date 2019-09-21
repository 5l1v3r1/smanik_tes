package android.support.v4.widget;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListPopupWindow;

public final class ListPopupWindowCompat {
  @Nullable
  public static View.OnTouchListener createDragToOpenListener(@NonNull ListPopupWindow paramListPopupWindow, @NonNull View paramView) { return (Build.VERSION.SDK_INT >= 19) ? paramListPopupWindow.createDragToOpenListener(paramView) : null; }
  
  @Deprecated
  public static View.OnTouchListener createDragToOpenListener(Object paramObject, View paramView) { return createDragToOpenListener((ListPopupWindow)paramObject, paramView); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/widget/ListPopupWindowCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */