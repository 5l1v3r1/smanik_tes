package com.android.volley;

public interface RetryPolicy {
  int getCurrentRetryCount();
  
  int getCurrentTimeout();
  
  void retry(VolleyError paramVolleyError) throws VolleyError;
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/RetryPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */