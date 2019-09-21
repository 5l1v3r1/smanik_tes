package com.android.volley;

import android.os.Handler;
import java.util.concurrent.Executor;

public class ExecutorDelivery implements ResponseDelivery {
  private final Executor mResponsePoster = new Executor() {
      public void execute(Runnable param1Runnable) { handler.post(param1Runnable); }
    };
  
  public ExecutorDelivery(final Handler handler) {}
  
  public ExecutorDelivery(Executor paramExecutor) {}
  
  public void postError(Request<?> paramRequest, VolleyError paramVolleyError) {
    paramRequest.addMarker("post-error");
    Response response = Response.error(paramVolleyError);
    this.mResponsePoster.execute(new ResponseDeliveryRunnable(paramRequest, response, null));
  }
  
  public void postResponse(Request<?> paramRequest, Response<?> paramResponse) { postResponse(paramRequest, paramResponse, null); }
  
  public void postResponse(Request<?> paramRequest, Response<?> paramResponse, Runnable paramRunnable) {
    paramRequest.markDelivered();
    paramRequest.addMarker("post-response");
    this.mResponsePoster.execute(new ResponseDeliveryRunnable(paramRequest, paramResponse, paramRunnable));
  }
  
  private class ResponseDeliveryRunnable implements Runnable {
    private final Request mRequest;
    
    private final Response mResponse;
    
    private final Runnable mRunnable;
    
    public ResponseDeliveryRunnable(Request param1Request, Response param1Response, Runnable param1Runnable) {
      this.mRequest = param1Request;
      this.mResponse = param1Response;
      this.mRunnable = param1Runnable;
    }
    
    public void run() {
      if (this.mRequest.isCanceled()) {
        this.mRequest.finish("canceled-at-delivery");
        return;
      } 
      if (this.mResponse.isSuccess()) {
        this.mRequest.deliverResponse(this.mResponse.result);
      } else {
        this.mRequest.deliverError(this.mResponse.error);
      } 
      if (this.mResponse.intermediate) {
        this.mRequest.addMarker("intermediate-response");
      } else {
        this.mRequest.finish("done");
      } 
      if (this.mRunnable != null)
        this.mRunnable.run(); 
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/ExecutorDelivery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */