package com.android.volley;

public class VolleyError extends Exception {
  public final NetworkResponse networkResponse = null;
  
  private long networkTimeMs;
  
  public VolleyError() {}
  
  public VolleyError(NetworkResponse paramNetworkResponse) {}
  
  public VolleyError(String paramString) { super(paramString); }
  
  public VolleyError(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public VolleyError(Throwable paramThrowable) { super(paramThrowable); }
  
  public long getNetworkTimeMs() { return this.networkTimeMs; }
  
  void setNetworkTimeMs(long paramLong) { this.networkTimeMs = paramLong; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/VolleyError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */