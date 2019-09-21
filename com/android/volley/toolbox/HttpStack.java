package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import java.io.IOException;
import java.util.Map;
import org.apache.http.HttpResponse;

public interface HttpStack {
  HttpResponse performRequest(Request<?> paramRequest, Map<String, String> paramMap) throws IOException, AuthFailureError;
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/HttpStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */