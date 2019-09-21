package com.android.volley.toolbox;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

public class ImageRequest extends Request<Bitmap> {
  public static final float DEFAULT_IMAGE_BACKOFF_MULT = 2.0F;
  
  public static final int DEFAULT_IMAGE_MAX_RETRIES = 2;
  
  public static final int DEFAULT_IMAGE_TIMEOUT_MS = 1000;
  
  private static final Object sDecodeLock = new Object();
  
  private final Bitmap.Config mDecodeConfig;
  
  private final Response.Listener<Bitmap> mListener;
  
  private final int mMaxHeight;
  
  private final int mMaxWidth;
  
  private ImageView.ScaleType mScaleType;
  
  @Deprecated
  public ImageRequest(String paramString, Response.Listener<Bitmap> paramListener, int paramInt1, int paramInt2, Bitmap.Config paramConfig, Response.ErrorListener paramErrorListener) { this(paramString, paramListener, paramInt1, paramInt2, ImageView.ScaleType.CENTER_INSIDE, paramConfig, paramErrorListener); }
  
  public ImageRequest(String paramString, Response.Listener<Bitmap> paramListener, int paramInt1, int paramInt2, ImageView.ScaleType paramScaleType, Bitmap.Config paramConfig, Response.ErrorListener paramErrorListener) {
    super(0, paramString, paramErrorListener);
    setRetryPolicy(new DefaultRetryPolicy(1000, 2, 2.0F));
    this.mListener = paramListener;
    this.mDecodeConfig = paramConfig;
    this.mMaxWidth = paramInt1;
    this.mMaxHeight = paramInt2;
    this.mScaleType = paramScaleType;
  }
  
  private Response<Bitmap> doParse(NetworkResponse paramNetworkResponse) { // Byte code:
    //   0: aload_1
    //   1: getfield data : [B
    //   4: astore #6
    //   6: new android/graphics/BitmapFactory$Options
    //   9: dup
    //   10: invokespecial <init> : ()V
    //   13: astore #7
    //   15: aload_0
    //   16: getfield mMaxWidth : I
    //   19: ifne -> 54
    //   22: aload_0
    //   23: getfield mMaxHeight : I
    //   26: ifne -> 54
    //   29: aload #7
    //   31: aload_0
    //   32: getfield mDecodeConfig : Landroid/graphics/Bitmap$Config;
    //   35: putfield inPreferredConfig : Landroid/graphics/Bitmap$Config;
    //   38: aload #6
    //   40: iconst_0
    //   41: aload #6
    //   43: arraylength
    //   44: aload #7
    //   46: invokestatic decodeByteArray : ([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   49: astore #6
    //   51: goto -> 205
    //   54: aload #7
    //   56: iconst_1
    //   57: putfield inJustDecodeBounds : Z
    //   60: aload #6
    //   62: iconst_0
    //   63: aload #6
    //   65: arraylength
    //   66: aload #7
    //   68: invokestatic decodeByteArray : ([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   71: pop
    //   72: aload #7
    //   74: getfield outWidth : I
    //   77: istore_2
    //   78: aload #7
    //   80: getfield outHeight : I
    //   83: istore_3
    //   84: aload_0
    //   85: getfield mMaxWidth : I
    //   88: aload_0
    //   89: getfield mMaxHeight : I
    //   92: iload_2
    //   93: iload_3
    //   94: aload_0
    //   95: getfield mScaleType : Landroid/widget/ImageView$ScaleType;
    //   98: invokestatic getResizedDimension : (IIIILandroid/widget/ImageView$ScaleType;)I
    //   101: istore #4
    //   103: aload_0
    //   104: getfield mMaxHeight : I
    //   107: aload_0
    //   108: getfield mMaxWidth : I
    //   111: iload_3
    //   112: iload_2
    //   113: aload_0
    //   114: getfield mScaleType : Landroid/widget/ImageView$ScaleType;
    //   117: invokestatic getResizedDimension : (IIIILandroid/widget/ImageView$ScaleType;)I
    //   120: istore #5
    //   122: aload #7
    //   124: iconst_0
    //   125: putfield inJustDecodeBounds : Z
    //   128: aload #7
    //   130: iload_2
    //   131: iload_3
    //   132: iload #4
    //   134: iload #5
    //   136: invokestatic findBestSampleSize : (IIII)I
    //   139: putfield inSampleSize : I
    //   142: aload #6
    //   144: iconst_0
    //   145: aload #6
    //   147: arraylength
    //   148: aload #7
    //   150: invokestatic decodeByteArray : ([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   153: astore #7
    //   155: aload #7
    //   157: astore #6
    //   159: aload #7
    //   161: ifnull -> 205
    //   164: aload #7
    //   166: invokevirtual getWidth : ()I
    //   169: iload #4
    //   171: if_icmpgt -> 188
    //   174: aload #7
    //   176: astore #6
    //   178: aload #7
    //   180: invokevirtual getHeight : ()I
    //   183: iload #5
    //   185: if_icmple -> 205
    //   188: aload #7
    //   190: iload #4
    //   192: iload #5
    //   194: iconst_1
    //   195: invokestatic createScaledBitmap : (Landroid/graphics/Bitmap;IIZ)Landroid/graphics/Bitmap;
    //   198: astore #6
    //   200: aload #7
    //   202: invokevirtual recycle : ()V
    //   205: aload #6
    //   207: ifnonnull -> 222
    //   210: new com/android/volley/ParseError
    //   213: dup
    //   214: aload_1
    //   215: invokespecial <init> : (Lcom/android/volley/NetworkResponse;)V
    //   218: invokestatic error : (Lcom/android/volley/VolleyError;)Lcom/android/volley/Response;
    //   221: areturn
    //   222: aload #6
    //   224: aload_1
    //   225: invokestatic parseCacheHeaders : (Lcom/android/volley/NetworkResponse;)Lcom/android/volley/Cache$Entry;
    //   228: invokestatic success : (Ljava/lang/Object;Lcom/android/volley/Cache$Entry;)Lcom/android/volley/Response;
    //   231: areturn }
  
  static int findBestSampleSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    double d = Math.min(paramInt1 / paramInt3, paramInt2 / paramInt4);
    float f = 1.0F;
    while (true) {
      float f1 = 2.0F * f;
      if (f1 <= d) {
        f = f1;
        continue;
      } 
      break;
    } 
    return (int)f;
  }
  
  private static int getResizedDimension(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageView.ScaleType paramScaleType) {
    if (paramInt1 == 0 && paramInt2 == 0)
      return paramInt3; 
    if (paramScaleType == ImageView.ScaleType.FIT_XY)
      return (paramInt1 == 0) ? paramInt3 : paramInt1; 
    if (paramInt1 == 0) {
      double d = paramInt2 / paramInt4;
      return (int)(paramInt3 * d);
    } 
    if (paramInt2 == 0)
      return paramInt1; 
    double d1 = paramInt4 / paramInt3;
    if (paramScaleType == ImageView.ScaleType.CENTER_CROP) {
      double d4 = paramInt1;
      double d5 = paramInt2;
      if (d4 * d1 < d5)
        paramInt1 = (int)(d5 / d1); 
      return paramInt1;
    } 
    double d2 = paramInt1;
    double d3 = paramInt2;
    if (d2 * d1 > d3)
      paramInt1 = (int)(d3 / d1); 
    return paramInt1;
  }
  
  protected void deliverResponse(Bitmap paramBitmap) { this.mListener.onResponse(paramBitmap); }
  
  public Request.Priority getPriority() { return Request.Priority.LOW; }
  
  protected Response<Bitmap> parseNetworkResponse(NetworkResponse paramNetworkResponse) {
    synchronized (sDecodeLock) {
      return doParse(paramNetworkResponse);
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/ImageRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */