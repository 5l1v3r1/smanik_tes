package com.android.volley;

import android.os.Process;
import java.util.concurrent.BlockingQueue;

public class CacheDispatcher extends Thread {
  private static final boolean DEBUG = VolleyLog.DEBUG;
  
  private final Cache mCache;
  
  private final BlockingQueue<Request<?>> mCacheQueue;
  
  private final ResponseDelivery mDelivery;
  
  private final BlockingQueue<Request<?>> mNetworkQueue;
  
  public CacheDispatcher(BlockingQueue<Request<?>> paramBlockingQueue1, BlockingQueue<Request<?>> paramBlockingQueue2, Cache paramCache, ResponseDelivery paramResponseDelivery) {
    this.mCacheQueue = paramBlockingQueue1;
    this.mNetworkQueue = paramBlockingQueue2;
    this.mCache = paramCache;
    this.mDelivery = paramResponseDelivery;
  }
  
  public void quit() {
    this.mQuit = true;
    interrupt();
  }
  
  public void run() {
    if (DEBUG)
      VolleyLog.v("start new dispatcher", new Object[0]); 
    Process.setThreadPriority(10);
    this.mCache.initialize();
    while (true) {
      try {
        final Request request = (Request)this.mCacheQueue.take();
        request.addMarker("cache-queue-take");
        if (request.isCanceled()) {
          request.finish("cache-discard-canceled");
          continue;
        } 
        Cache.Entry entry = this.mCache.get(request.getCacheKey());
        if (entry == null) {
          request.addMarker("cache-miss");
          this.mNetworkQueue.put(request);
          continue;
        } 
        if (entry.isExpired()) {
          request.addMarker("cache-hit-expired");
          request.setCacheEntry(entry);
          this.mNetworkQueue.put(request);
          continue;
        } 
        request.addMarker("cache-hit");
        Response response = request.parseNetworkResponse(new NetworkResponse(entry.data, entry.responseHeaders));
        request.addMarker("cache-hit-parsed");
        if (!entry.refreshNeeded()) {
          this.mDelivery.postResponse(request, response);
          continue;
        } 
        request.addMarker("cache-hit-refresh-needed");
        request.setCacheEntry(entry);
        response.intermediate = true;
        this.mDelivery.postResponse(request, response, new Runnable() {
              public void run() {
                try {
                  CacheDispatcher.this.mNetworkQueue.put(request);
                  return;
                } catch (InterruptedException interruptedException) {
                  return;
                } 
              }
            });
      } catch (InterruptedException interruptedException) {
        if (this.mQuit)
          return; 
      } 
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/CacheDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */