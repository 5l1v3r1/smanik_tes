package com.android.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import com.android.volley.RequestQueue;
import java.io.File;

public class Volley {
  private static final String DEFAULT_CACHE_DIR = "volley";
  
  public static RequestQueue newRequestQueue(Context paramContext) { return newRequestQueue(paramContext, null); }
  
  public static RequestQueue newRequestQueue(Context paramContext, HttpStack paramHttpStack) {
    String str;
    File file = new File(paramContext.getCacheDir(), "volley");
    try {
      str = paramContext.getPackageName();
      PackageInfo packageInfo = paramContext.getPackageManager().getPackageInfo(str, 0);
      str = String.valueOf(String.valueOf(str));
      int i = packageInfo.versionCode;
      StringBuilder stringBuilder = new StringBuilder(str.length() + 12);
      stringBuilder.append(str);
      stringBuilder.append("/");
      stringBuilder.append(i);
      str = stringBuilder.toString();
    } catch (android.content.pm.PackageManager.NameNotFoundException paramContext) {
      str = "volley/0";
    } 
    HttpStack httpStack = paramHttpStack;
    if (paramHttpStack == null)
      if (Build.VERSION.SDK_INT >= 9) {
        httpStack = new HurlStack();
      } else {
        httpStack = new HttpClientStack(AndroidHttpClient.newInstance(str));
      }  
    BasicNetwork basicNetwork = new BasicNetwork(httpStack);
    RequestQueue requestQueue = new RequestQueue(new DiskBasedCache(file), basicNetwork);
    requestQueue.start();
    return requestQueue;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/Volley.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */