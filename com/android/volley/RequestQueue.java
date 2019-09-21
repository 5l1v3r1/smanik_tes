package com.android.volley;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestQueue {
  private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;
  
  private final Cache mCache;
  
  private CacheDispatcher mCacheDispatcher;
  
  private final PriorityBlockingQueue<Request<?>> mCacheQueue = new PriorityBlockingQueue();
  
  private final Set<Request<?>> mCurrentRequests = new HashSet();
  
  private final ResponseDelivery mDelivery;
  
  private NetworkDispatcher[] mDispatchers;
  
  private List<RequestFinishedListener> mFinishedListeners = new ArrayList();
  
  private final Network mNetwork;
  
  private final PriorityBlockingQueue<Request<?>> mNetworkQueue = new PriorityBlockingQueue();
  
  private AtomicInteger mSequenceGenerator = new AtomicInteger();
  
  private final Map<String, Queue<Request<?>>> mWaitingRequests = new HashMap();
  
  public RequestQueue(Cache paramCache, Network paramNetwork) { this(paramCache, paramNetwork, 4); }
  
  public RequestQueue(Cache paramCache, Network paramNetwork, int paramInt) { this(paramCache, paramNetwork, paramInt, new ExecutorDelivery(new Handler(Looper.getMainLooper()))); }
  
  public RequestQueue(Cache paramCache, Network paramNetwork, int paramInt, ResponseDelivery paramResponseDelivery) {
    this.mCache = paramCache;
    this.mNetwork = paramNetwork;
    this.mDispatchers = new NetworkDispatcher[paramInt];
    this.mDelivery = paramResponseDelivery;
  }
  
  public <T> Request<T> add(Request<T> paramRequest) {
    paramRequest.setRequestQueue(this);
    synchronized (this.mCurrentRequests) {
      this.mCurrentRequests.add(paramRequest);
      paramRequest.setSequence(getSequenceNumber());
      paramRequest.addMarker("add-to-queue");
      if (!paramRequest.shouldCache()) {
        this.mNetworkQueue.add(paramRequest);
        return paramRequest;
      } 
      synchronized (this.mWaitingRequests) {
        String str = paramRequest.getCacheKey();
        if (this.mWaitingRequests.containsKey(str)) {
          Queue queue = (Queue)this.mWaitingRequests.get(str);
          null = queue;
          if (queue == null)
            null = new LinkedList(); 
          null.add(paramRequest);
          this.mWaitingRequests.put(str, null);
          if (VolleyLog.DEBUG)
            VolleyLog.v("Request for cacheKey=%s is in flight, putting on hold.", new Object[] { str }); 
        } else {
          this.mWaitingRequests.put(str, null);
          this.mCacheQueue.add(paramRequest);
        } 
        return paramRequest;
      } 
    } 
  }
  
  public <T> void addRequestFinishedListener(RequestFinishedListener<T> paramRequestFinishedListener) {
    synchronized (this.mFinishedListeners) {
      this.mFinishedListeners.add(paramRequestFinishedListener);
      return;
    } 
  }
  
  public void cancelAll(RequestFilter paramRequestFilter) {
    synchronized (this.mCurrentRequests) {
      for (Request request : this.mCurrentRequests) {
        if (paramRequestFilter.apply(request))
          request.cancel(); 
      } 
      return;
    } 
  }
  
  public void cancelAll(final Object tag) {
    if (paramObject == null)
      throw new IllegalArgumentException("Cannot cancelAll with a null tag"); 
    cancelAll(new RequestFilter() {
          public boolean apply(Request<?> param1Request) { return (param1Request.getTag() == tag); }
        });
  }
  
  <T> void finish(Request<T> paramRequest) {
    synchronized (this.mCurrentRequests) {
      this.mCurrentRequests.remove(paramRequest);
      synchronized (this.mFinishedListeners) {
        Iterator iterator = this.mFinishedListeners.iterator();
        while (iterator.hasNext())
          ((RequestFinishedListener)iterator.next()).onRequestFinished(paramRequest); 
        if (paramRequest.shouldCache())
          synchronized (this.mWaitingRequests) {
            String str = paramRequest.getCacheKey();
            Queue queue = (Queue)this.mWaitingRequests.remove(str);
            if (queue != null) {
              if (VolleyLog.DEBUG)
                VolleyLog.v("Releasing %d waiting requests for cacheKey=%s.", new Object[] { Integer.valueOf(queue.size()), str }); 
              this.mCacheQueue.addAll(queue);
            } 
            return;
          }  
        return;
      } 
    } 
  }
  
  public Cache getCache() { return this.mCache; }
  
  public int getSequenceNumber() { return this.mSequenceGenerator.incrementAndGet(); }
  
  public <T> void removeRequestFinishedListener(RequestFinishedListener<T> paramRequestFinishedListener) {
    synchronized (this.mFinishedListeners) {
      this.mFinishedListeners.remove(paramRequestFinishedListener);
      return;
    } 
  }
  
  public void start() {
    stop();
    this.mCacheDispatcher = new CacheDispatcher(this.mCacheQueue, this.mNetworkQueue, this.mCache, this.mDelivery);
    this.mCacheDispatcher.start();
    for (byte b = 0; b < this.mDispatchers.length; b++) {
      NetworkDispatcher networkDispatcher = new NetworkDispatcher(this.mNetworkQueue, this.mNetwork, this.mCache, this.mDelivery);
      this.mDispatchers[b] = networkDispatcher;
      networkDispatcher.start();
    } 
  }
  
  public void stop() {
    if (this.mCacheDispatcher != null)
      this.mCacheDispatcher.quit(); 
    for (byte b = 0; b < this.mDispatchers.length; b++) {
      if (this.mDispatchers[b] != null)
        this.mDispatchers[b].quit(); 
    } 
  }
  
  public static interface RequestFilter {
    boolean apply(Request<?> param1Request);
  }
  
  public static interface RequestFinishedListener<T> {
    void onRequestFinished(Request<T> param1Request);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/RequestQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */