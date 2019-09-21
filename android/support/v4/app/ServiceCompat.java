package android.support.v4.app;

import android.app.Service;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ServiceCompat {
  public static final int START_STICKY = 1;
  
  public static final int STOP_FOREGROUND_DETACH = 2;
  
  public static final int STOP_FOREGROUND_REMOVE = 1;
  
  public static void stopForeground(@NonNull Service paramService, int paramInt) {
    if (Build.VERSION.SDK_INT >= 24) {
      paramService.stopForeground(paramInt);
      return;
    } 
    boolean bool = true;
    if ((paramInt & true) == 0)
      bool = false; 
    paramService.stopForeground(bool);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface StopForegroundFlags {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/ServiceCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */