package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;

public interface Authenticator {
  String getAuthToken() throws AuthFailureError;
  
  void invalidateAuthToken(String paramString);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/Authenticator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */