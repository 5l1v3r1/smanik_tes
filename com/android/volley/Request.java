package com.android.volley;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

public abstract class Request<T> extends Object implements Comparable<Request<T>> {
  private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
  
  private Cache.Entry mCacheEntry;
  
  private boolean mCanceled;
  
  private final int mDefaultTrafficStatsTag;
  
  private final Response.ErrorListener mErrorListener;
  
  private final VolleyLog.MarkerLog mEventLog;
  
  private final int mMethod;
  
  private RequestQueue mRequestQueue;
  
  private boolean mResponseDelivered;
  
  private RetryPolicy mRetryPolicy;
  
  private Integer mSequence;
  
  private boolean mShouldCache;
  
  private Object mTag;
  
  private final String mUrl;
  
  public Request(int paramInt, String paramString, Response.ErrorListener paramErrorListener) {
    if (VolleyLog.MarkerLog.ENABLED) {
      object = new VolleyLog.MarkerLog();
    } else {
      object = null;
    } 
    this.mEventLog = object;
    this.mShouldCache = true;
    this.mCanceled = false;
    this.mResponseDelivered = false;
    this.mCacheEntry = null;
    this.mMethod = paramInt;
    this.mUrl = paramString;
    this.mErrorListener = paramErrorListener;
    this.mDefaultTrafficStatsTag = setRetryPolicy(new DefaultRetryPolicy()).findDefaultTrafficStatsTag(paramString);
  }
  
  @Deprecated
  public Request(String paramString, Response.ErrorListener paramErrorListener) { this(-1, paramString, paramErrorListener); }
  
  private byte[] encodeParameters(Map<String, String> paramMap, String paramString) {
    stringBuilder = new StringBuilder();
    try {
      for (Map.Entry entry : paramMap.entrySet()) {
        stringBuilder.append(URLEncoder.encode((String)entry.getKey(), paramString));
        stringBuilder.append('=');
        stringBuilder.append(URLEncoder.encode((String)entry.getValue(), paramString));
        stringBuilder.append('&');
      } 
      return stringBuilder.toString().getBytes(paramString);
    } catch (UnsupportedEncodingException stringBuilder) {
      String str = String.valueOf(paramString);
      if (str.length() != 0) {
        str = "Encoding not supported: ".concat(str);
      } else {
        str = new String("Encoding not supported: ");
      } 
      throw new RuntimeException(str, stringBuilder);
    } 
  }
  
  private static int findDefaultTrafficStatsTag(String paramString) {
    if (!TextUtils.isEmpty(paramString)) {
      Uri uri = Uri.parse(paramString);
      if (uri != null) {
        String str = uri.getHost();
        if (str != null)
          return str.hashCode(); 
      } 
    } 
    return 0;
  }
  
  public void addMarker(String paramString) {
    if (VolleyLog.MarkerLog.ENABLED)
      this.mEventLog.add(paramString, Thread.currentThread().getId()); 
  }
  
  public void cancel() { this.mCanceled = true; }
  
  public int compareTo(Request<T> paramRequest) {
    Priority priority1 = getPriority();
    Priority priority2 = paramRequest.getPriority();
    return (priority1 == priority2) ? (this.mSequence.intValue() - paramRequest.mSequence.intValue()) : (priority2.ordinal() - priority1.ordinal());
  }
  
  public void deliverError(VolleyError paramVolleyError) {
    if (this.mErrorListener != null)
      this.mErrorListener.onErrorResponse(paramVolleyError); 
  }
  
  protected abstract void deliverResponse(T paramT);
  
  void finish(final String tag) {
    if (this.mRequestQueue != null)
      this.mRequestQueue.finish(this); 
    if (VolleyLog.MarkerLog.ENABLED) {
      final long threadId = Thread.currentThread().getId();
      if (Looper.myLooper() != Looper.getMainLooper()) {
        (new Handler(Looper.getMainLooper())).post(new Runnable() {
              public void run() {
                Request.this.mEventLog.add(tag, threadId);
                Request.this.mEventLog.finish(toString());
              }
            });
        return;
      } 
      this.mEventLog.add(paramString, l);
      this.mEventLog.finish(toString());
    } 
  }
  
  public byte[] getBody() throws AuthFailureError {
    Map map = getParams();
    return (map != null && map.size() > 0) ? encodeParameters(map, getParamsEncoding()) : null;
  }
  
  public String getBodyContentType() {
    String str = String.valueOf(getParamsEncoding());
    return (str.length() != 0) ? "application/x-www-form-urlencoded; charset=".concat(str) : new String("application/x-www-form-urlencoded; charset=");
  }
  
  public Cache.Entry getCacheEntry() { return this.mCacheEntry; }
  
  public String getCacheKey() { return getUrl(); }
  
  public Response.ErrorListener getErrorListener() { return this.mErrorListener; }
  
  public Map<String, String> getHeaders() throws AuthFailureError { return Collections.emptyMap(); }
  
  public int getMethod() { return this.mMethod; }
  
  protected Map<String, String> getParams() throws AuthFailureError { return null; }
  
  protected String getParamsEncoding() { return "UTF-8"; }
  
  @Deprecated
  public byte[] getPostBody() throws AuthFailureError {
    Map map = getPostParams();
    return (map != null && map.size() > 0) ? encodeParameters(map, getPostParamsEncoding()) : null;
  }
  
  @Deprecated
  public String getPostBodyContentType() { return getBodyContentType(); }
  
  @Deprecated
  protected Map<String, String> getPostParams() throws AuthFailureError { return getParams(); }
  
  @Deprecated
  protected String getPostParamsEncoding() { return getParamsEncoding(); }
  
  public Priority getPriority() { return Priority.NORMAL; }
  
  public RetryPolicy getRetryPolicy() { return this.mRetryPolicy; }
  
  public final int getSequence() {
    if (this.mSequence == null)
      throw new IllegalStateException("getSequence called before setSequence"); 
    return this.mSequence.intValue();
  }
  
  public Object getTag() { return this.mTag; }
  
  public final int getTimeoutMs() { return this.mRetryPolicy.getCurrentTimeout(); }
  
  public int getTrafficStatsTag() { return this.mDefaultTrafficStatsTag; }
  
  public String getUrl() { return this.mUrl; }
  
  public boolean hasHadResponseDelivered() { return this.mResponseDelivered; }
  
  public boolean isCanceled() { return this.mCanceled; }
  
  public void markDelivered() { this.mResponseDelivered = true; }
  
  protected VolleyError parseNetworkError(VolleyError paramVolleyError) { return paramVolleyError; }
  
  protected abstract Response<T> parseNetworkResponse(NetworkResponse paramNetworkResponse);
  
  public Request<?> setCacheEntry(Cache.Entry paramEntry) {
    this.mCacheEntry = paramEntry;
    return this;
  }
  
  public Request<?> setRequestQueue(RequestQueue paramRequestQueue) {
    this.mRequestQueue = paramRequestQueue;
    return this;
  }
  
  public Request<?> setRetryPolicy(RetryPolicy paramRetryPolicy) {
    this.mRetryPolicy = paramRetryPolicy;
    return this;
  }
  
  public final Request<?> setSequence(int paramInt) {
    this.mSequence = Integer.valueOf(paramInt);
    return this;
  }
  
  public final Request<?> setShouldCache(boolean paramBoolean) {
    this.mShouldCache = paramBoolean;
    return this;
  }
  
  public Request<?> setTag(Object paramObject) {
    this.mTag = paramObject;
    return this;
  }
  
  public final boolean shouldCache() { return this.mShouldCache; }
  
  public String toString() {
    String str1 = String.valueOf(Integer.toHexString(getTrafficStatsTag()));
    if (str1.length() != 0) {
      str1 = "0x".concat(str1);
    } else {
      str1 = new String("0x");
    } 
    if (this.mCanceled) {
      str2 = "[X] ";
    } else {
      str2 = "[ ] ";
    } 
    String str2;
    String str3;
    String str4;
    String str5 = String.valueOf((str4 = String.valueOf((str1 = String.valueOf((str3 = String.valueOf((str2 = String.valueOf(String.valueOf(str2))).valueOf(getUrl()))).valueOf(str1))).valueOf(getPriority()))).valueOf(this.mSequence));
    StringBuilder stringBuilder = new StringBuilder(str2.length() + 3 + str3.length() + str1.length() + str4.length() + str5.length());
    stringBuilder.append(str2);
    stringBuilder.append(str3);
    stringBuilder.append(" ");
    stringBuilder.append(str1);
    stringBuilder.append(" ");
    stringBuilder.append(str4);
    stringBuilder.append(" ");
    stringBuilder.append(str5);
    return stringBuilder.toString();
  }
  
  public static interface Method {
    public static final int DELETE = 3;
    
    public static final int DEPRECATED_GET_OR_POST = -1;
    
    public static final int GET = 0;
    
    public static final int HEAD = 4;
    
    public static final int OPTIONS = 5;
    
    public static final int PATCH = 7;
    
    public static final int POST = 1;
    
    public static final int PUT = 2;
    
    public static final int TRACE = 6;
  }
  
  public enum Priority {
    HIGH, IMMEDIATE, LOW, NORMAL;
    
    static  {
      HIGH = new Priority("HIGH", 2);
      IMMEDIATE = new Priority("IMMEDIATE", 3);
      $VALUES = new Priority[] { LOW, NORMAL, HIGH, IMMEDIATE };
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/Request.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */