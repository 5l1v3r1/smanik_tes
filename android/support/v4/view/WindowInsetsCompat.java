package android.support.v4.view;

import android.graphics.Rect;
import android.os.Build;
import android.view.WindowInsets;

public class WindowInsetsCompat {
  private final Object mInsets;
  
  public WindowInsetsCompat(WindowInsetsCompat paramWindowInsetsCompat) {
    int i = Build.VERSION.SDK_INT;
    WindowInsetsCompat windowInsetsCompat = null;
    if (i >= 20) {
      WindowInsets windowInsets;
      if (paramWindowInsetsCompat == null) {
        paramWindowInsetsCompat = windowInsetsCompat;
      } else {
        windowInsets = new WindowInsets((WindowInsets)paramWindowInsetsCompat.mInsets);
      } 
      this.mInsets = windowInsets;
      return;
    } 
    this.mInsets = null;
  }
  
  private WindowInsetsCompat(Object paramObject) { this.mInsets = paramObject; }
  
  static Object unwrap(WindowInsetsCompat paramWindowInsetsCompat) { return (paramWindowInsetsCompat == null) ? null : paramWindowInsetsCompat.mInsets; }
  
  static WindowInsetsCompat wrap(Object paramObject) { return (paramObject == null) ? null : new WindowInsetsCompat(paramObject); }
  
  public WindowInsetsCompat consumeStableInsets() { return (Build.VERSION.SDK_INT >= 21) ? new WindowInsetsCompat(((WindowInsets)this.mInsets).consumeStableInsets()) : null; }
  
  public WindowInsetsCompat consumeSystemWindowInsets() { return (Build.VERSION.SDK_INT >= 20) ? new WindowInsetsCompat(((WindowInsets)this.mInsets).consumeSystemWindowInsets()) : null; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject != null) {
      if (getClass() != paramObject.getClass())
        return false; 
      paramObject = (WindowInsetsCompat)paramObject;
      return (this.mInsets == null) ? ((paramObject.mInsets == null)) : this.mInsets.equals(paramObject.mInsets);
    } 
    return false;
  }
  
  public int getStableInsetBottom() { return (Build.VERSION.SDK_INT >= 21) ? ((WindowInsets)this.mInsets).getStableInsetBottom() : 0; }
  
  public int getStableInsetLeft() { return (Build.VERSION.SDK_INT >= 21) ? ((WindowInsets)this.mInsets).getStableInsetLeft() : 0; }
  
  public int getStableInsetRight() { return (Build.VERSION.SDK_INT >= 21) ? ((WindowInsets)this.mInsets).getStableInsetRight() : 0; }
  
  public int getStableInsetTop() { return (Build.VERSION.SDK_INT >= 21) ? ((WindowInsets)this.mInsets).getStableInsetTop() : 0; }
  
  public int getSystemWindowInsetBottom() { return (Build.VERSION.SDK_INT >= 20) ? ((WindowInsets)this.mInsets).getSystemWindowInsetBottom() : 0; }
  
  public int getSystemWindowInsetLeft() { return (Build.VERSION.SDK_INT >= 20) ? ((WindowInsets)this.mInsets).getSystemWindowInsetLeft() : 0; }
  
  public int getSystemWindowInsetRight() { return (Build.VERSION.SDK_INT >= 20) ? ((WindowInsets)this.mInsets).getSystemWindowInsetRight() : 0; }
  
  public int getSystemWindowInsetTop() { return (Build.VERSION.SDK_INT >= 20) ? ((WindowInsets)this.mInsets).getSystemWindowInsetTop() : 0; }
  
  public boolean hasInsets() { return (Build.VERSION.SDK_INT >= 20) ? ((WindowInsets)this.mInsets).hasInsets() : 0; }
  
  public boolean hasStableInsets() { return (Build.VERSION.SDK_INT >= 21) ? ((WindowInsets)this.mInsets).hasStableInsets() : 0; }
  
  public boolean hasSystemWindowInsets() { return (Build.VERSION.SDK_INT >= 20) ? ((WindowInsets)this.mInsets).hasSystemWindowInsets() : 0; }
  
  public int hashCode() { return (this.mInsets == null) ? 0 : this.mInsets.hashCode(); }
  
  public boolean isConsumed() { return (Build.VERSION.SDK_INT >= 21) ? ((WindowInsets)this.mInsets).isConsumed() : 0; }
  
  public boolean isRound() { return (Build.VERSION.SDK_INT >= 20) ? ((WindowInsets)this.mInsets).isRound() : 0; }
  
  public WindowInsetsCompat replaceSystemWindowInsets(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return (Build.VERSION.SDK_INT >= 20) ? new WindowInsetsCompat(((WindowInsets)this.mInsets).replaceSystemWindowInsets(paramInt1, paramInt2, paramInt3, paramInt4)) : null; }
  
  public WindowInsetsCompat replaceSystemWindowInsets(Rect paramRect) { return (Build.VERSION.SDK_INT >= 21) ? new WindowInsetsCompat(((WindowInsets)this.mInsets).replaceSystemWindowInsets(paramRect)) : null; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/WindowInsetsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */