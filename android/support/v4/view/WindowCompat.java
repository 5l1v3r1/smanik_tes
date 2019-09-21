package android.support.v4.view;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

public final class WindowCompat {
  public static final int FEATURE_ACTION_BAR = 8;
  
  public static final int FEATURE_ACTION_BAR_OVERLAY = 9;
  
  public static final int FEATURE_ACTION_MODE_OVERLAY = 10;
  
  @NonNull
  public static <T extends View> T requireViewById(@NonNull Window paramWindow, @IdRes int paramInt) {
    View view = paramWindow.findViewById(paramInt);
    if (view == null)
      throw new IllegalArgumentException("ID does not reference a View inside this Window"); 
    return (T)view;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/WindowCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */