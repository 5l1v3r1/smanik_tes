package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public class HurlStack implements HttpStack {
  private static final String HEADER_CONTENT_TYPE = "Content-Type";
  
  private final SSLSocketFactory mSslSocketFactory;
  
  private final UrlRewriter mUrlRewriter;
  
  public HurlStack() { this(null); }
  
  public HurlStack(UrlRewriter paramUrlRewriter) { this(paramUrlRewriter, null); }
  
  public HurlStack(UrlRewriter paramUrlRewriter, SSLSocketFactory paramSSLSocketFactory) {
    this.mUrlRewriter = paramUrlRewriter;
    this.mSslSocketFactory = paramSSLSocketFactory;
  }
  
  private static void addBodyIfExists(HttpURLConnection paramHttpURLConnection, Request<?> paramRequest) throws IOException, AuthFailureError {
    byte[] arrayOfByte = paramRequest.getBody();
    if (arrayOfByte != null) {
      paramHttpURLConnection.setDoOutput(true);
      paramHttpURLConnection.addRequestProperty("Content-Type", paramRequest.getBodyContentType());
      DataOutputStream dataOutputStream = new DataOutputStream(paramHttpURLConnection.getOutputStream());
      dataOutputStream.write(arrayOfByte);
      dataOutputStream.close();
    } 
  }
  
  private static HttpEntity entityFromConnection(HttpURLConnection paramHttpURLConnection) {
    InputStream inputStream;
    BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
    try {
      inputStream = paramHttpURLConnection.getInputStream();
    } catch (IOException iOException) {
      inputStream = paramHttpURLConnection.getErrorStream();
    } 
    basicHttpEntity.setContent(inputStream);
    basicHttpEntity.setContentLength(paramHttpURLConnection.getContentLength());
    basicHttpEntity.setContentEncoding(paramHttpURLConnection.getContentEncoding());
    basicHttpEntity.setContentType(paramHttpURLConnection.getContentType());
    return basicHttpEntity;
  }
  
  private static boolean hasResponseBody(int paramInt1, int paramInt2) { return (paramInt1 != 4 && (100 > paramInt2 || paramInt2 >= 200) && paramInt2 != 204 && paramInt2 != 304); }
  
  private HttpURLConnection openConnection(URL paramURL, Request<?> paramRequest) throws IOException {
    HttpURLConnection httpURLConnection = createConnection(paramURL);
    int i = paramRequest.getTimeoutMs();
    httpURLConnection.setConnectTimeout(i);
    httpURLConnection.setReadTimeout(i);
    httpURLConnection.setUseCaches(false);
    httpURLConnection.setDoInput(true);
    if ("https".equals(paramURL.getProtocol()) && this.mSslSocketFactory != null)
      ((HttpsURLConnection)httpURLConnection).setSSLSocketFactory(this.mSslSocketFactory); 
    return httpURLConnection;
  }
  
  static void setConnectionParametersForRequest(HttpURLConnection paramHttpURLConnection, Request<?> paramRequest) throws IOException, AuthFailureError {
    switch (paramRequest.getMethod()) {
      default:
        throw new IllegalStateException("Unknown method type.");
      case 7:
        paramHttpURLConnection.setRequestMethod("PATCH");
        addBodyIfExists(paramHttpURLConnection, paramRequest);
        return;
      case 6:
        paramHttpURLConnection.setRequestMethod("TRACE");
        return;
      case 5:
        paramHttpURLConnection.setRequestMethod("OPTIONS");
        return;
      case 4:
        paramHttpURLConnection.setRequestMethod("HEAD");
        return;
      case 3:
        paramHttpURLConnection.setRequestMethod("DELETE");
        return;
      case 2:
        paramHttpURLConnection.setRequestMethod("PUT");
        addBodyIfExists(paramHttpURLConnection, paramRequest);
        return;
      case 1:
        paramHttpURLConnection.setRequestMethod("POST");
        addBodyIfExists(paramHttpURLConnection, paramRequest);
        return;
      case 0:
        paramHttpURLConnection.setRequestMethod("GET");
        return;
      case -1:
        break;
    } 
    byte[] arrayOfByte = paramRequest.getPostBody();
    if (arrayOfByte != null) {
      paramHttpURLConnection.setDoOutput(true);
      paramHttpURLConnection.setRequestMethod("POST");
      paramHttpURLConnection.addRequestProperty("Content-Type", paramRequest.getPostBodyContentType());
      DataOutputStream dataOutputStream = new DataOutputStream(paramHttpURLConnection.getOutputStream());
      dataOutputStream.write(arrayOfByte);
      dataOutputStream.close();
    } 
  }
  
  protected HttpURLConnection createConnection(URL paramURL) throws IOException {
    HttpURLConnection httpURLConnection;
    httpURLConnection.setInstanceFollowRedirects((httpURLConnection = (HttpURLConnection)paramURL.openConnection()).getFollowRedirects());
    return httpURLConnection;
  }
  
  public HttpResponse performRequest(Request<?> paramRequest, Map<String, String> paramMap) throws IOException, AuthFailureError {
    String str2;
    String str1;
    String str3 = paramRequest.getUrl();
    HashMap hashMap = new HashMap();
    hashMap.putAll(paramRequest.getHeaders());
    hashMap.putAll(paramMap);
    if (this.mUrlRewriter != null) {
      String str = this.mUrlRewriter.rewriteUrl(str3);
      str2 = str;
      if (str == null) {
        str1 = String.valueOf(str3);
        if (str1.length() != 0) {
          str1 = "URL blocked by rewriter: ".concat(str1);
        } else {
          str1 = new String("URL blocked by rewriter: ");
        } 
        throw new IOException(str1);
      } 
    } else {
      str2 = str3;
    } 
    HttpURLConnection httpURLConnection = openConnection(new URL(str2), str1);
    for (String str : hashMap.keySet())
      httpURLConnection.addRequestProperty(str, (String)hashMap.get(str)); 
    setConnectionParametersForRequest(httpURLConnection, str1);
    ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
    if (httpURLConnection.getResponseCode() == -1)
      throw new IOException("Could not retrieve response code from HttpUrlConnection."); 
    BasicStatusLine basicStatusLine = new BasicStatusLine(protocolVersion, httpURLConnection.getResponseCode(), httpURLConnection.getResponseMessage());
    BasicHttpResponse basicHttpResponse = new BasicHttpResponse(basicStatusLine);
    if (hasResponseBody(str1.getMethod(), basicStatusLine.getStatusCode()))
      basicHttpResponse.setEntity(entityFromConnection(httpURLConnection)); 
    for (Map.Entry entry : httpURLConnection.getHeaderFields().entrySet()) {
      if (entry.getKey() != null)
        basicHttpResponse.addHeader(new BasicHeader((String)entry.getKey(), (String)((List)entry.getValue()).get(0))); 
    } 
    return basicHttpResponse;
  }
  
  public static interface UrlRewriter {
    String rewriteUrl(String param1String);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/HurlStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */