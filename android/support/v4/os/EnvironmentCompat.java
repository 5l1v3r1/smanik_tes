package android.support.v4.os;

import android.os.Build;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;

public final class EnvironmentCompat {
  public static final String MEDIA_UNKNOWN = "unknown";
  
  private static final String TAG = "EnvironmentCompat";
  
  public static String getStorageState(File paramFile) {
    if (Build.VERSION.SDK_INT >= 19)
      return Environment.getStorageState(paramFile); 
    try {
      if (paramFile.getCanonicalPath().startsWith(Environment.getExternalStorageDirectory().getCanonicalPath()))
        return Environment.getExternalStorageState(); 
    } catch (IOException paramFile) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Failed to resolve canonical path: ");
      stringBuilder.append(paramFile);
      Log.w("EnvironmentCompat", stringBuilder.toString());
    } 
    return "unknown";
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/os/EnvironmentCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */