package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.appcompat.R;

class ThemeUtils {
  private static final int[] APPCOMPAT_CHECK_ATTRS = { R.attr.colorPrimary };
  
  static void checkAppCompatTheme(Context paramContext) {
    TypedArray typedArray = paramContext.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
    boolean bool = typedArray.hasValue(0);
    typedArray.recycle();
    if (bool ^ true)
      throw new IllegalArgumentException("You need to use a Theme.AppCompat theme (or descendant) with the design library."); 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/ThemeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */