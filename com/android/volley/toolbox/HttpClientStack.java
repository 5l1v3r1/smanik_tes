package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpClientStack implements HttpStack {
  private static final String HEADER_CONTENT_TYPE = "Content-Type";
  
  protected final HttpClient mClient;
  
  public HttpClientStack(HttpClient paramHttpClient) { this.mClient = paramHttpClient; }
  
  private static void addHeaders(HttpUriRequest paramHttpUriRequest, Map<String, String> paramMap) {
    for (String str : paramMap.keySet())
      paramHttpUriRequest.setHeader(str, (String)paramMap.get(str)); 
  }
  
  static HttpUriRequest createHttpRequest(Request<?> paramRequest, Map<String, String> paramMap) throws AuthFailureError {
    HttpPatch httpPatch;
    HttpPost httpPost;
    HttpPut httpPut;
    switch (paramRequest.getMethod()) {
      default:
        throw new IllegalStateException("Unknown request method.");
      case 7:
        httpPatch = new HttpPatch(paramRequest.getUrl());
        httpPatch.addHeader("Content-Type", paramRequest.getBodyContentType());
        setEntityIfNonEmptyBody(httpPatch, paramRequest);
        return httpPatch;
      case 6:
        return new HttpTrace(paramRequest.getUrl());
      case 5:
        return new HttpOptions(paramRequest.getUrl());
      case 4:
        return new HttpHead(paramRequest.getUrl());
      case 3:
        return new HttpDelete(paramRequest.getUrl());
      case 2:
        httpPut = new HttpPut(paramRequest.getUrl());
        httpPut.addHeader("Content-Type", paramRequest.getBodyContentType());
        setEntityIfNonEmptyBody(httpPut, paramRequest);
        return httpPut;
      case 1:
        httpPost = new HttpPost(paramRequest.getUrl());
        httpPost.addHeader("Content-Type", paramRequest.getBodyContentType());
        setEntityIfNonEmptyBody(httpPost, paramRequest);
        return httpPost;
      case 0:
        return new HttpGet(paramRequest.getUrl());
      case -1:
        break;
    } 
    byte[] arrayOfByte = paramRequest.getPostBody();
    if (arrayOfByte != null) {
      HttpPost httpPost1 = new HttpPost(paramRequest.getUrl());
      httpPost1.addHeader("Content-Type", paramRequest.getPostBodyContentType());
      httpPost1.setEntity(new ByteArrayEntity(arrayOfByte));
      return httpPost1;
    } 
    return new HttpGet(paramRequest.getUrl());
  }
  
  private static List<NameValuePair> getPostParameterPairs(Map<String, String> paramMap) {
    ArrayList arrayList = new ArrayList(paramMap.size());
    for (String str : paramMap.keySet())
      arrayList.add(new BasicNameValuePair(str, (String)paramMap.get(str))); 
    return arrayList;
  }
  
  private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase paramHttpEntityEnclosingRequestBase, Request<?> paramRequest) throws AuthFailureError {
    byte[] arrayOfByte = paramRequest.getBody();
    if (arrayOfByte != null)
      paramHttpEntityEnclosingRequestBase.setEntity(new ByteArrayEntity(arrayOfByte)); 
  }
  
  protected void onPrepareRequest(HttpUriRequest paramHttpUriRequest) throws IOException {}
  
  public HttpResponse performRequest(Request<?> paramRequest, Map<String, String> paramMap) throws IOException, AuthFailureError {
    HttpUriRequest httpUriRequest = createHttpRequest(paramRequest, paramMap);
    addHeaders(httpUriRequest, paramMap);
    addHeaders(httpUriRequest, paramRequest.getHeaders());
    onPrepareRequest(httpUriRequest);
    HttpParams httpParams = httpUriRequest.getParams();
    int i = paramRequest.getTimeoutMs();
    HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
    HttpConnectionParams.setSoTimeout(httpParams, i);
    return this.mClient.execute(httpUriRequest);
  }
  
  public static final class HttpPatch extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "PATCH";
    
    public HttpPatch() {}
    
    public HttpPatch(String param1String) { setURI(URI.create(param1String)); }
    
    public HttpPatch(URI param1URI) { setURI(param1URI); }
    
    public String getMethod() { return "PATCH"; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/HttpClientStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */