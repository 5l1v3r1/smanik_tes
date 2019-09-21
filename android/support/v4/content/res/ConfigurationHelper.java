package android.support.v4.content.res;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;

public final class ConfigurationHelper {
  public static int getDensityDpi(@NonNull Resources paramResources) { return (Build.VERSION.SDK_INT >= 17) ? (paramResources.getConfiguration()).densityDpi : (paramResources.getDisplayMetrics()).densityDpi; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/content/res/ConfigurationHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */