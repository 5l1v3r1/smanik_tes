package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import java.io.UnsupportedEncodingException;

public class StringRequest extends Request<String> {
  private final Response.Listener<String> mListener;
  
  public StringRequest(int paramInt, String paramString, Response.Listener<String> paramListener, Response.ErrorListener paramErrorListener) {
    super(paramInt, paramString, paramErrorListener);
    this.mListener = paramListener;
  }
  
  public StringRequest(String paramString, Response.Listener<String> paramListener, Response.ErrorListener paramErrorListener) { this(0, paramString, paramListener, paramErrorListener); }
  
  protected void deliverResponse(String paramString) { this.mListener.onResponse(paramString); }
  
  protected Response<String> parseNetworkResponse(NetworkResponse paramNetworkResponse) {
    String str;
    try {
      str = new String(paramNetworkResponse.data, HttpHeaderParser.parseCharset(paramNetworkResponse.headers));
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      str = new String(paramNetworkResponse.data);
    } 
    return Response.success(str, HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/StringRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */