package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import java.io.UnsupportedEncodingException;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonObjectRequest extends JsonRequest<JSONObject> {
  public JsonObjectRequest(int paramInt, String paramString, JSONObject paramJSONObject, Response.Listener<JSONObject> paramListener, Response.ErrorListener paramErrorListener) { super(paramInt, paramString, str, paramListener, paramErrorListener); }
  
  public JsonObjectRequest(String paramString, JSONObject paramJSONObject, Response.Listener<JSONObject> paramListener, Response.ErrorListener paramErrorListener) { this(b, paramString, paramJSONObject, paramListener, paramErrorListener); }
  
  protected Response<JSONObject> parseNetworkResponse(NetworkResponse paramNetworkResponse) {
    try {
      return Response.success(new JSONObject(new String(paramNetworkResponse.data, HttpHeaderParser.parseCharset(paramNetworkResponse.headers, "utf-8"))), HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));
    } catch (UnsupportedEncodingException paramNetworkResponse) {
      return Response.error(new ParseError(paramNetworkResponse));
    } catch (JSONException paramNetworkResponse) {
      return Response.error(new ParseError(paramNetworkResponse));
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/JsonObjectRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */