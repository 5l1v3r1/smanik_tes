package com.android.volley;

public class Response<T> extends Object {
  public final Cache.Entry cacheEntry = null;
  
  public final VolleyError error;
  
  public boolean intermediate = false;
  
  public final T result = null;
  
  private Response(VolleyError paramVolleyError) { this.error = paramVolleyError; }
  
  private Response(T paramT, Cache.Entry paramEntry) { this.error = null; }
  
  public static <T> Response<T> error(VolleyError paramVolleyError) { return new Response(paramVolleyError); }
  
  public static <T> Response<T> success(T paramT, Cache.Entry paramEntry) { return new Response(paramT, paramEntry); }
  
  public boolean isSuccess() { return (this.error == null); }
  
  public static interface ErrorListener {
    void onErrorResponse(VolleyError param1VolleyError);
  }
  
  public static interface Listener<T> {
    void onResponse(T param1T);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/Response.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */