package android.support.v4.graphics.drawable;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public interface TintAwareDrawable {
  void setTint(@ColorInt int paramInt);
  
  void setTintList(ColorStateList paramColorStateList);
  
  void setTintMode(PorterDuff.Mode paramMode);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/graphics/drawable/TintAwareDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */