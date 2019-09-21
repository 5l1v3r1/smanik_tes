package android.support.v4.os;

import android.os.Build;

public class BuildCompat {
  @Deprecated
  public static boolean isAtLeastN() { return (Build.VERSION.SDK_INT >= 24); }
  
  @Deprecated
  public static boolean isAtLeastNMR1() { return (Build.VERSION.SDK_INT >= 25); }
  
  @Deprecated
  public static boolean isAtLeastO() { return (Build.VERSION.SDK_INT >= 26); }
  
  @Deprecated
  public static boolean isAtLeastOMR1() { return (Build.VERSION.SDK_INT >= 27); }
  
  public static boolean isAtLeastP() { return Build.VERSION.CODENAME.equals("P"); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/os/BuildCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */