package com.android.volley.toolbox;

import android.os.SystemClock;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.cookie.DateUtils;

public class BasicNetwork implements Network {
  protected static final boolean DEBUG = VolleyLog.DEBUG;
  
  private static int DEFAULT_POOL_SIZE = 4096;
  
  private static int SLOW_REQUEST_THRESHOLD_MS = 3000;
  
  protected final HttpStack mHttpStack;
  
  protected final ByteArrayPool mPool;
  
  public BasicNetwork(HttpStack paramHttpStack) { this(paramHttpStack, new ByteArrayPool(DEFAULT_POOL_SIZE)); }
  
  public BasicNetwork(HttpStack paramHttpStack, ByteArrayPool paramByteArrayPool) {
    this.mHttpStack = paramHttpStack;
    this.mPool = paramByteArrayPool;
  }
  
  private void addCacheHeaders(Map<String, String> paramMap, Cache.Entry paramEntry) {
    if (paramEntry == null)
      return; 
    if (paramEntry.etag != null)
      paramMap.put("If-None-Match", paramEntry.etag); 
    if (paramEntry.lastModified > 0L)
      paramMap.put("If-Modified-Since", DateUtils.formatDate(new Date(paramEntry.lastModified))); 
  }
  
  private static void attemptRetryOnException(String paramString, Request<?> paramRequest, VolleyError paramVolleyError) throws VolleyError {
    RetryPolicy retryPolicy = paramRequest.getRetryPolicy();
    int i = paramRequest.getTimeoutMs();
    try {
      retryPolicy.retry(paramVolleyError);
      paramRequest.addMarker(String.format("%s-retry [timeout=%s]", new Object[] { paramString, Integer.valueOf(i) }));
      return;
    } catch (VolleyError paramVolleyError) {
      paramRequest.addMarker(String.format("%s-timeout-giveup [timeout=%s]", new Object[] { paramString, Integer.valueOf(i) }));
      throw paramVolleyError;
    } 
  }
  
  protected static Map<String, String> convertHeaders(Header[] paramArrayOfHeader) {
    TreeMap treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    for (byte b = 0; b < paramArrayOfHeader.length; b++)
      treeMap.put(paramArrayOfHeader[b].getName(), paramArrayOfHeader[b].getValue()); 
    return treeMap;
  }
  
  private byte[] entityToBytes(HttpEntity paramHttpEntity) throws IOException, ServerError {
    poolingByteArrayOutputStream = new PoolingByteArrayOutputStream(this.mPool, (int)paramHttpEntity.getContentLength());
    Object object = null;
    try {
      inputStream = paramHttpEntity.getContent();
      if (inputStream == null)
        throw new ServerError(); 
      arrayOfByte = this.mPool.getBuf(1024);
    } finally {
      inputStream = null;
    } 
    try {
      paramHttpEntity.consumeContent();
    } catch (IOException paramHttpEntity) {
      VolleyLog.v("Error occured when calling consumingContent", new Object[0]);
    } 
    this.mPool.returnBuf(arrayOfByte);
    poolingByteArrayOutputStream.close();
    throw inputStream;
  }
  
  private void logSlowRequests(long paramLong, Request<?> paramRequest, byte[] paramArrayOfByte, StatusLine paramStatusLine) {
    if (DEBUG || paramLong > SLOW_REQUEST_THRESHOLD_MS) {
      String str;
      if (paramArrayOfByte != null) {
        str = Integer.valueOf(paramArrayOfByte.length);
      } else {
        str = "null";
      } 
      VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]", new Object[] { paramRequest, Long.valueOf(paramLong), str, Integer.valueOf(paramStatusLine.getStatusCode()), Integer.valueOf(paramRequest.getRetryPolicy().getCurrentRetryCount()) });
    } 
  }
  
  protected void logError(String paramString1, String paramString2, long paramLong) { VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", new Object[] { paramString1, Long.valueOf(SystemClock.elapsedRealtime() - paramLong), paramString2 }); }
  
  public NetworkResponse performRequest(Request<?> paramRequest) throws VolleyError {
    long l = SystemClock.elapsedRealtime();
    while (true) {
      map = Collections.emptyMap();
      try {
        HttpResponse httpResponse1;
        NetworkResponse networkResponse = new HashMap();
        addCacheHeaders(networkResponse, paramRequest.getCacheEntry());
        HttpResponse httpResponse2 = this.mHttpStack.performRequest(paramRequest, networkResponse);
        try {
          StatusLine statusLine = httpResponse2.getStatusLine();
          int i = statusLine.getStatusCode();
          IOException iOException = convertHeaders(httpResponse2.getAllHeaders());
          if (i == 304) {
            try {
              httpResponse1 = paramRequest.getCacheEntry();
              if (httpResponse1 == null)
                return new NetworkResponse(304, null, iOException, true, SystemClock.elapsedRealtime() - l); 
              httpResponse1.responseHeaders.putAll(iOException);
              return new NetworkResponse(304, httpResponse1.data, httpResponse1.responseHeaders, true, SystemClock.elapsedRealtime() - l);
            } catch (IOException iOException1) {
              statusLine = null;
              map = iOException;
              iOException = iOException1;
              httpResponse1 = statusLine;
            } 
          } else {
            Map map1;
            try {
              httpResponse1 = httpResponse2.getEntity();
              if (httpResponse1 != null) {
                byte[] arrayOfByte = entityToBytes(httpResponse2.getEntity());
              } else {
                httpResponse1 = new byte[0];
              } 
              try {
                logSlowRequests(SystemClock.elapsedRealtime() - l, paramRequest, httpResponse1, statusLine);
                if (i < 200 || i > 299)
                  throw new IOException(); 
                long l1 = SystemClock.elapsedRealtime();
                try {
                  return new NetworkResponse(i, httpResponse1, iOException, false, l1 - l);
                } catch (IOException map) {}
              } catch (IOException map) {}
              Map map2 = map;
              map = iOException;
              map1 = map2;
            } catch (IOException iOException2) {
              map = map1;
              statusLine = null;
              IOException iOException1 = iOException2;
              httpResponse1 = statusLine;
            } 
          } 
        } catch (IOException iOException2) {
          HttpResponse httpResponse = httpResponse2;
          httpResponse2 = null;
          IOException iOException3 = iOException2;
          httpResponse1 = httpResponse2;
          httpResponse2 = httpResponse;
          IOException iOException1 = iOException3;
        } 
        if (httpResponse2 != null) {
          int i = httpResponse2.getStatusLine().getStatusCode();
          VolleyLog.e("Unexpected response code %d for %s", new Object[] { Integer.valueOf(i), paramRequest.getUrl() });
          if (httpResponse1 != null) {
            networkResponse = new NetworkResponse(i, httpResponse1, map, false, SystemClock.elapsedRealtime() - l);
            if (i == 401 || i == 403) {
              attemptRetryOnException("auth", paramRequest, new AuthFailureError(networkResponse));
              continue;
            } 
            throw new ServerError(networkResponse);
          } 
          throw new NetworkError(null);
        } 
        throw new NoConnectionError(networkResponse);
      } catch (SocketTimeoutException socketTimeoutException) {
        attemptRetryOnException("socket", paramRequest, new TimeoutError());
      } catch (ConnectTimeoutException connectTimeoutException) {
        attemptRetryOnException("connection", paramRequest, new TimeoutError());
      } catch (MalformedURLException malformedURLException) {
        String str = String.valueOf(paramRequest.getUrl());
        if (str.length() != 0) {
          str = "Bad URL ".concat(str);
        } else {
          str = new String("Bad URL ");
        } 
        throw new RuntimeException(str, malformedURLException);
      } catch (IOException iOException2) {
        IOException iOException1 = null;
        IOException iOException3 = null;
        IOException iOException4 = iOException2;
        iOException2 = iOException3;
        iOException3 = iOException1;
        iOException1 = iOException4;
      } 
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/BasicNetwork.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */