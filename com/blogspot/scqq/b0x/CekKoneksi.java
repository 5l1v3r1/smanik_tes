package com.blogspot.scqq.b0x;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CekKoneksi {
  public static boolean isNetworkStatusAvialable(Context paramContext) {
    ConnectivityManager connectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
    if (connectivityManager != null) {
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      if (networkInfo != null && networkInfo.isConnected())
        return true; 
    } 
    return false;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/blogspot/scqq/b0x/CekKoneksi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */