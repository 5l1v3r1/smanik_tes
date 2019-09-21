package com.android.volley;

public interface ResponseDelivery {
  void postError(Request<?> paramRequest, VolleyError paramVolleyError);
  
  void postResponse(Request<?> paramRequest, Response<?> paramResponse);
  
  void postResponse(Request<?> paramRequest, Response<?> paramResponse, Runnable paramRunnable);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/ResponseDelivery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */