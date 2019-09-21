package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import java.io.UnsupportedEncodingException;
import org.json.JSONArray;
import org.json.JSONException;

public class JsonArrayRequest extends JsonRequest<JSONArray> {
  public JsonArrayRequest(int paramInt, String paramString, JSONArray paramJSONArray, Response.Listener<JSONArray> paramListener, Response.ErrorListener paramErrorListener) { super(paramInt, paramString, str, paramListener, paramErrorListener); }
  
  public JsonArrayRequest(String paramString, Response.Listener<JSONArray> paramListener, Response.ErrorListener paramErrorListener) { super(0, paramString, null, paramListener, paramErrorListener); }
  
  protected Response<JSONArray> parseNetworkResponse(NetworkResponse paramNetworkResponse) {
    try {
      return Response.success(new JSONArray(new String(paramNetworkResponse.data, HttpHeaderParser.parseCharset(paramNetworkResponse.headers, "utf-8"))), HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));
    } catch (UnsupportedEncodingException paramNetworkResponse) {
      return Response.error(new ParseError(paramNetworkResponse));
    } catch (JSONException paramNetworkResponse) {
      return Response.error(new ParseError(paramNetworkResponse));
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/JsonArrayRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */