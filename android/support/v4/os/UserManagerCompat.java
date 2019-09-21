package android.support.v4.os;

import android.content.Context;
import android.os.Build;
import android.os.UserManager;

public class UserManagerCompat {
  public static boolean isUserUnlocked(Context paramContext) { return (Build.VERSION.SDK_INT >= 24) ? ((UserManager)paramContext.getSystemService(UserManager.class)).isUserUnlocked() : 1; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/os/UserManagerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */