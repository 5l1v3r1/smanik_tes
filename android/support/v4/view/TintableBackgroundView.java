package android.support.v4.view;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;

public interface TintableBackgroundView {
  @Nullable
  ColorStateList getSupportBackgroundTintList();
  
  @Nullable
  PorterDuff.Mode getSupportBackgroundTintMode();
  
  void setSupportBackgroundTintList(@Nullable ColorStateList paramColorStateList);
  
  void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode paramMode);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/TintableBackgroundView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */