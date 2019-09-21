package android.support.v7.widget;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class TooltipCompat {
  public static void setTooltipText(@NonNull View paramView, @Nullable CharSequence paramCharSequence) {
    if (Build.VERSION.SDK_INT >= 26) {
      paramView.setTooltipText(paramCharSequence);
      return;
    } 
    TooltipCompatHandler.setTooltipText(paramView, paramCharSequence);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/TooltipCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */