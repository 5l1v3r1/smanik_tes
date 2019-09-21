package com.blogspot.scqq.b0x.Util;

import android.app.Application;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {
  private static final String TAG = "AppController";
  
  private static AppController instance;
  
  RequestQueue mRequestQueue;
  
  static  {
  
  }
  
  public static AppController getInstance() { // Byte code:
    //   0: ldc com/blogspot/scqq/b0x/Util/AppController
    //   2: monitorenter
    //   3: getstatic com/blogspot/scqq/b0x/Util/AppController.instance : Lcom/blogspot/scqq/b0x/Util/AppController;
    //   6: astore_0
    //   7: ldc com/blogspot/scqq/b0x/Util/AppController
    //   9: monitorexit
    //   10: aload_0
    //   11: areturn
    //   12: astore_0
    //   13: ldc com/blogspot/scqq/b0x/Util/AppController
    //   15: monitorexit
    //   16: aload_0
    //   17: athrow
    // Exception table:
    //   from	to	target	type
    //   3	7	12	finally }
  
  private RequestQueue getRequestQueue() {
    if (this.mRequestQueue == null)
      this.mRequestQueue = Volley.newRequestQueue(getApplicationContext()); 
    return this.mRequestQueue;
  }
  
  public <T> void addToRequestQueue(Request<T> paramRequest) {
    paramRequest.setTag(TAG);
    getRequestQueue().add(paramRequest);
  }
  
  public <T> void addToRequestQueue(Request<T> paramRequest, String paramString) {
    String str = paramString;
    if (TextUtils.isEmpty(paramString))
      str = TAG; 
    paramRequest.setTag(str);
    getRequestQueue().add(paramRequest);
  }
  
  public void cancelAllRequest(Object paramObject) {
    if (this.mRequestQueue != null)
      this.mRequestQueue.cancelAll(paramObject); 
  }
  
  public void onCreate() {
    super.onCreate();
    instance = this;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/blogspot/scqq/b0x/Util/AppController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */