package com.android.volley.toolbox;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class ImageLoader {
  private int mBatchResponseDelayMs = 100;
  
  private final HashMap<String, BatchedImageRequest> mBatchedResponses = new HashMap();
  
  private final ImageCache mCache;
  
  private final Handler mHandler = new Handler(Looper.getMainLooper());
  
  private final HashMap<String, BatchedImageRequest> mInFlightRequests = new HashMap();
  
  private final RequestQueue mRequestQueue;
  
  private Runnable mRunnable;
  
  public ImageLoader(RequestQueue paramRequestQueue, ImageCache paramImageCache) {
    this.mRequestQueue = paramRequestQueue;
    this.mCache = paramImageCache;
  }
  
  private void batchResponse(String paramString, BatchedImageRequest paramBatchedImageRequest) {
    this.mBatchedResponses.put(paramString, paramBatchedImageRequest);
    if (this.mRunnable == null) {
      this.mRunnable = new Runnable() {
          public void run() {
            Iterator iterator = ImageLoader.this.mBatchedResponses.values().iterator();
            while (iterator.hasNext()) {
              ImageLoader.BatchedImageRequest batchedImageRequest;
              Iterator iterator1 = batchedImageRequest.mContainers.iterator();
              while (iterator1.hasNext()) {
                ImageLoader.ImageContainer imageContainer;
                if (imageContainer.mListener == null)
                  continue; 
                if (batchedImageRequest.getError() == null) {
                  ImageLoader.ImageContainer.access$502(imageContainer, batchedImageRequest.mResponseBitmap);
                  imageContainer.mListener.onResponse(imageContainer, false);
                  continue;
                } 
                imageContainer.mListener.onErrorResponse(batchedImageRequest.getError());
              } 
            } 
            ImageLoader.this.mBatchedResponses.clear();
            ImageLoader.access$602(ImageLoader.this, null);
          }
        };
      this.mHandler.postDelayed(this.mRunnable, this.mBatchResponseDelayMs);
    } 
  }
  
  private static String getCacheKey(String paramString, int paramInt1, int paramInt2, ImageView.ScaleType paramScaleType) {
    StringBuilder stringBuilder = new StringBuilder(paramString.length() + 12);
    stringBuilder.append("#W");
    stringBuilder.append(paramInt1);
    stringBuilder.append("#H");
    stringBuilder.append(paramInt2);
    stringBuilder.append("#S");
    stringBuilder.append(paramScaleType.ordinal());
    stringBuilder.append(paramString);
    return stringBuilder.toString();
  }
  
  public static ImageListener getImageListener(final ImageView view, final int defaultImageResId, final int errorImageResId) { return new ImageListener() {
        public void onErrorResponse(VolleyError param1VolleyError) {
          if (errorImageResId != 0)
            view.setImageResource(errorImageResId); 
        }
        
        public void onResponse(ImageLoader.ImageContainer param1ImageContainer, boolean param1Boolean) {
          if (param1ImageContainer.getBitmap() != null) {
            view.setImageBitmap(param1ImageContainer.getBitmap());
            return;
          } 
          if (defaultImageResId != 0)
            view.setImageResource(defaultImageResId); 
        }
      }; }
  
  private void throwIfNotOnMainThread() {
    if (Looper.myLooper() != Looper.getMainLooper())
      throw new IllegalStateException("ImageLoader must be invoked from the main thread."); 
  }
  
  public ImageContainer get(String paramString, ImageListener paramImageListener) { return get(paramString, paramImageListener, 0, 0); }
  
  public ImageContainer get(String paramString, ImageListener paramImageListener, int paramInt1, int paramInt2) { return get(paramString, paramImageListener, paramInt1, paramInt2, ImageView.ScaleType.CENTER_INSIDE); }
  
  public ImageContainer get(String paramString, ImageListener paramImageListener, int paramInt1, int paramInt2, ImageView.ScaleType paramScaleType) {
    ImageContainer imageContainer1;
    throwIfNotOnMainThread();
    String str = getCacheKey(paramString, paramInt1, paramInt2, paramScaleType);
    Bitmap bitmap = this.mCache.getBitmap(str);
    if (bitmap != null) {
      imageContainer1 = new ImageContainer(bitmap, paramString, null, null);
      paramImageListener.onResponse(imageContainer1, true);
      return imageContainer1;
    } 
    ImageContainer imageContainer2 = new ImageContainer(null, imageContainer1, str, paramImageListener);
    paramImageListener.onResponse(imageContainer2, true);
    BatchedImageRequest batchedImageRequest = (BatchedImageRequest)this.mInFlightRequests.get(str);
    if (batchedImageRequest != null) {
      batchedImageRequest.addContainer(imageContainer2);
      return imageContainer2;
    } 
    Request request = makeImageRequest(imageContainer1, paramInt1, paramInt2, paramScaleType, str);
    this.mRequestQueue.add(request);
    this.mInFlightRequests.put(str, new BatchedImageRequest(request, imageContainer2));
    return imageContainer2;
  }
  
  public boolean isCached(String paramString, int paramInt1, int paramInt2) { return isCached(paramString, paramInt1, paramInt2, ImageView.ScaleType.CENTER_INSIDE); }
  
  public boolean isCached(String paramString, int paramInt1, int paramInt2, ImageView.ScaleType paramScaleType) {
    throwIfNotOnMainThread();
    paramString = getCacheKey(paramString, paramInt1, paramInt2, paramScaleType);
    return (this.mCache.getBitmap(paramString) != null);
  }
  
  protected Request<Bitmap> makeImageRequest(String paramString1, int paramInt1, int paramInt2, ImageView.ScaleType paramScaleType, String paramString2) { return new ImageRequest(paramString1, new Response.Listener<Bitmap>(this, paramString2) {
          public void onResponse(Bitmap param1Bitmap) { ImageLoader.this.onGetImageSuccess(cacheKey, param1Bitmap); }
        }paramInt1, paramInt2, paramScaleType, Bitmap.Config.RGB_565, new Response.ErrorListener(this, paramString2) {
          public void onErrorResponse(VolleyError param1VolleyError) { ImageLoader.this.onGetImageError(cacheKey, param1VolleyError); }
        }); }
  
  protected void onGetImageError(String paramString, VolleyError paramVolleyError) {
    BatchedImageRequest batchedImageRequest = (BatchedImageRequest)this.mInFlightRequests.remove(paramString);
    if (batchedImageRequest != null) {
      batchedImageRequest.setError(paramVolleyError);
      batchResponse(paramString, batchedImageRequest);
    } 
  }
  
  protected void onGetImageSuccess(String paramString, Bitmap paramBitmap) {
    this.mCache.putBitmap(paramString, paramBitmap);
    BatchedImageRequest batchedImageRequest = (BatchedImageRequest)this.mInFlightRequests.remove(paramString);
    if (batchedImageRequest != null) {
      BatchedImageRequest.access$002(batchedImageRequest, paramBitmap);
      batchResponse(paramString, batchedImageRequest);
    } 
  }
  
  public void setBatchedResponseDelay(int paramInt) { this.mBatchResponseDelayMs = paramInt; }
  
  private class BatchedImageRequest {
    private final LinkedList<ImageLoader.ImageContainer> mContainers = new LinkedList();
    
    private VolleyError mError;
    
    private final Request<?> mRequest;
    
    private Bitmap mResponseBitmap;
    
    public BatchedImageRequest(Request<?> param1Request, ImageLoader.ImageContainer param1ImageContainer) {
      this.mRequest = param1Request;
      this.mContainers.add(param1ImageContainer);
    }
    
    public void addContainer(ImageLoader.ImageContainer param1ImageContainer) { this.mContainers.add(param1ImageContainer); }
    
    public VolleyError getError() { return this.mError; }
    
    public boolean removeContainerAndCancelIfNecessary(ImageLoader.ImageContainer param1ImageContainer) {
      this.mContainers.remove(param1ImageContainer);
      if (this.mContainers.size() == 0) {
        this.mRequest.cancel();
        return true;
      } 
      return false;
    }
    
    public void setError(VolleyError param1VolleyError) { this.mError = param1VolleyError; }
  }
  
  public static interface ImageCache {
    Bitmap getBitmap(String param1String);
    
    void putBitmap(String param1String, Bitmap param1Bitmap);
  }
  
  public class ImageContainer {
    private Bitmap mBitmap;
    
    private final String mCacheKey;
    
    private final ImageLoader.ImageListener mListener;
    
    private final String mRequestUrl;
    
    public ImageContainer(Bitmap param1Bitmap, String param1String1, String param1String2, ImageLoader.ImageListener param1ImageListener) {
      this.mBitmap = param1Bitmap;
      this.mRequestUrl = param1String1;
      this.mCacheKey = param1String2;
      this.mListener = param1ImageListener;
    }
    
    public void cancelRequest() {
      if (this.mListener == null)
        return; 
      ImageLoader.BatchedImageRequest batchedImageRequest = (ImageLoader.BatchedImageRequest)ImageLoader.this.mInFlightRequests.get(this.mCacheKey);
      if (batchedImageRequest != null) {
        if (batchedImageRequest.removeContainerAndCancelIfNecessary(this)) {
          ImageLoader.this.mInFlightRequests.remove(this.mCacheKey);
          return;
        } 
      } else {
        batchedImageRequest = (ImageLoader.BatchedImageRequest)ImageLoader.this.mBatchedResponses.get(this.mCacheKey);
        if (batchedImageRequest != null) {
          batchedImageRequest.removeContainerAndCancelIfNecessary(this);
          if (batchedImageRequest.mContainers.size() == 0)
            ImageLoader.this.mBatchedResponses.remove(this.mCacheKey); 
        } 
      } 
    }
    
    public Bitmap getBitmap() { return this.mBitmap; }
    
    public String getRequestUrl() { return this.mRequestUrl; }
  }
  
  public static interface ImageListener extends Response.ErrorListener {
    void onResponse(ImageLoader.ImageContainer param1ImageContainer, boolean param1Boolean);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/ImageLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */