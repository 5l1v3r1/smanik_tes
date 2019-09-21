package android.support.v4.widget;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.PopupMenu;

public final class PopupMenuCompat {
  @Nullable
  public static View.OnTouchListener getDragToOpenListener(@NonNull Object paramObject) { return (Build.VERSION.SDK_INT >= 19) ? ((PopupMenu)paramObject).getDragToOpenListener() : null; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/widget/PopupMenuCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */