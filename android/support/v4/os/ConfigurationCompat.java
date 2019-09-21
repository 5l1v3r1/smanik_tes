package android.support.v4.os;

import android.content.res.Configuration;
import android.os.Build;
import java.util.Locale;

public final class ConfigurationCompat {
  public static LocaleListCompat getLocales(Configuration paramConfiguration) { return (Build.VERSION.SDK_INT >= 24) ? LocaleListCompat.wrap(paramConfiguration.getLocales()) : LocaleListCompat.create(new Locale[] { paramConfiguration.locale }); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/os/ConfigurationCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */