package com.android.volley;

import android.annotation.TargetApi;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import java.util.concurrent.BlockingQueue;

public class NetworkDispatcher extends Thread {
  private final Cache mCache;
  
  private final ResponseDelivery mDelivery;
  
  private final Network mNetwork;
  
  private final BlockingQueue<Request<?>> mQueue;
  
  public NetworkDispatcher(BlockingQueue<Request<?>> paramBlockingQueue, Network paramNetwork, Cache paramCache, ResponseDelivery paramResponseDelivery) {
    this.mQueue = paramBlockingQueue;
    this.mNetwork = paramNetwork;
    this.mCache = paramCache;
    this.mDelivery = paramResponseDelivery;
  }
  
  @TargetApi(14)
  private void addTrafficStatsTag(Request<?> paramRequest) {
    if (Build.VERSION.SDK_INT >= 14)
      TrafficStats.setThreadStatsTag(paramRequest.getTrafficStatsTag()); 
  }
  
  private void parseAndDeliverNetworkError(Request<?> paramRequest, VolleyError paramVolleyError) {
    paramVolleyError = paramRequest.parseNetworkError(paramVolleyError);
    this.mDelivery.postError(paramRequest, paramVolleyError);
  }
  
  public void quit() {
    this.mQuit = true;
    interrupt();
  }
  
  public void run() {
    Process.setThreadPriority(10);
    while (true) {
      long l = SystemClock.elapsedRealtime();
      try {
        Request request = (Request)this.mQueue.take();
        try {
          request.addMarker("network-queue-take");
          if (request.isCanceled()) {
            request.finish("network-discard-cancelled");
            continue;
          } 
          addTrafficStatsTag(request);
          NetworkResponse networkResponse = this.mNetwork.performRequest(request);
          request.addMarker("network-http-complete");
          if (networkResponse.notModified && request.hasHadResponseDelivered()) {
            request.finish("not-modified");
            continue;
          } 
          Response response = request.parseNetworkResponse(networkResponse);
          request.addMarker("network-parse-complete");
          if (request.shouldCache() && response.cacheEntry != null) {
            this.mCache.put(request.getCacheKey(), response.cacheEntry);
            request.addMarker("network-cache-written");
          } 
          request.markDelivered();
          this.mDelivery.postResponse(request, response);
        } catch (VolleyError volleyError) {
          volleyError.setNetworkTimeMs(SystemClock.elapsedRealtime() - l);
          parseAndDeliverNetworkError(request, volleyError);
        } catch (Exception exception) {
          VolleyLog.e(exception, "Unhandled exception %s", new Object[] { exception.toString() });
          exception = new VolleyError(exception);
          exception.setNetworkTimeMs(SystemClock.elapsedRealtime() - l);
          this.mDelivery.postError(request, exception);
        } 
      } catch (InterruptedException interruptedException) {
        if (this.mQuit)
          return; 
      } 
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/NetworkDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */