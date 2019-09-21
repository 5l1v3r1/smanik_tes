package com.android.volley.toolbox;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.volley.VolleyError;

public class NetworkImageView extends ImageView {
  private int mDefaultImageId;
  
  private int mErrorImageId;
  
  private ImageLoader.ImageContainer mImageContainer;
  
  private ImageLoader mImageLoader;
  
  private String mUrl;
  
  public NetworkImageView(Context paramContext) { this(paramContext, null); }
  
  public NetworkImageView(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public NetworkImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) { super(paramContext, paramAttributeSet, paramInt); }
  
  private void setDefaultImageOrNull() {
    if (this.mDefaultImageId != 0) {
      setImageResource(this.mDefaultImageId);
      return;
    } 
    setImageBitmap(null);
  }
  
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    invalidate();
  }
  
  void loadImageIfNecessary(boolean paramBoolean) { // Byte code:
    //   0: aload_0
    //   1: invokevirtual getWidth : ()I
    //   4: istore #6
    //   6: aload_0
    //   7: invokevirtual getHeight : ()I
    //   10: istore #5
    //   12: aload_0
    //   13: invokevirtual getScaleType : ()Landroid/widget/ImageView$ScaleType;
    //   16: astore #8
    //   18: aload_0
    //   19: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   22: astore #9
    //   24: iconst_1
    //   25: istore #4
    //   27: aload #9
    //   29: ifnull -> 76
    //   32: aload_0
    //   33: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   36: getfield width : I
    //   39: bipush #-2
    //   41: if_icmpne -> 49
    //   44: iconst_1
    //   45: istore_2
    //   46: goto -> 51
    //   49: iconst_0
    //   50: istore_2
    //   51: iload_2
    //   52: istore_3
    //   53: aload_0
    //   54: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   57: getfield height : I
    //   60: bipush #-2
    //   62: if_icmpne -> 78
    //   65: iconst_1
    //   66: istore #7
    //   68: iload_2
    //   69: istore_3
    //   70: iload #7
    //   72: istore_2
    //   73: goto -> 80
    //   76: iconst_0
    //   77: istore_3
    //   78: iconst_0
    //   79: istore_2
    //   80: iload_3
    //   81: ifeq -> 91
    //   84: iload_2
    //   85: ifeq -> 91
    //   88: goto -> 94
    //   91: iconst_0
    //   92: istore #4
    //   94: iload #6
    //   96: ifne -> 110
    //   99: iload #5
    //   101: ifne -> 110
    //   104: iload #4
    //   106: ifne -> 110
    //   109: return
    //   110: aload_0
    //   111: getfield mUrl : Ljava/lang/String;
    //   114: invokestatic isEmpty : (Ljava/lang/CharSequence;)Z
    //   117: ifeq -> 144
    //   120: aload_0
    //   121: getfield mImageContainer : Lcom/android/volley/toolbox/ImageLoader$ImageContainer;
    //   124: ifnull -> 139
    //   127: aload_0
    //   128: getfield mImageContainer : Lcom/android/volley/toolbox/ImageLoader$ImageContainer;
    //   131: invokevirtual cancelRequest : ()V
    //   134: aload_0
    //   135: aconst_null
    //   136: putfield mImageContainer : Lcom/android/volley/toolbox/ImageLoader$ImageContainer;
    //   139: aload_0
    //   140: invokespecial setDefaultImageOrNull : ()V
    //   143: return
    //   144: aload_0
    //   145: getfield mImageContainer : Lcom/android/volley/toolbox/ImageLoader$ImageContainer;
    //   148: ifnull -> 190
    //   151: aload_0
    //   152: getfield mImageContainer : Lcom/android/volley/toolbox/ImageLoader$ImageContainer;
    //   155: invokevirtual getRequestUrl : ()Ljava/lang/String;
    //   158: ifnull -> 190
    //   161: aload_0
    //   162: getfield mImageContainer : Lcom/android/volley/toolbox/ImageLoader$ImageContainer;
    //   165: invokevirtual getRequestUrl : ()Ljava/lang/String;
    //   168: aload_0
    //   169: getfield mUrl : Ljava/lang/String;
    //   172: invokevirtual equals : (Ljava/lang/Object;)Z
    //   175: ifeq -> 179
    //   178: return
    //   179: aload_0
    //   180: getfield mImageContainer : Lcom/android/volley/toolbox/ImageLoader$ImageContainer;
    //   183: invokevirtual cancelRequest : ()V
    //   186: aload_0
    //   187: invokespecial setDefaultImageOrNull : ()V
    //   190: iload #6
    //   192: istore #4
    //   194: iload_3
    //   195: ifeq -> 201
    //   198: iconst_0
    //   199: istore #4
    //   201: iload_2
    //   202: ifeq -> 210
    //   205: iconst_0
    //   206: istore_2
    //   207: goto -> 213
    //   210: iload #5
    //   212: istore_2
    //   213: aload_0
    //   214: aload_0
    //   215: getfield mImageLoader : Lcom/android/volley/toolbox/ImageLoader;
    //   218: aload_0
    //   219: getfield mUrl : Ljava/lang/String;
    //   222: new com/android/volley/toolbox/NetworkImageView$1
    //   225: dup
    //   226: aload_0
    //   227: iload_1
    //   228: invokespecial <init> : (Lcom/android/volley/toolbox/NetworkImageView;Z)V
    //   231: iload #4
    //   233: iload_2
    //   234: aload #8
    //   236: invokevirtual get : (Ljava/lang/String;Lcom/android/volley/toolbox/ImageLoader$ImageListener;IILandroid/widget/ImageView$ScaleType;)Lcom/android/volley/toolbox/ImageLoader$ImageContainer;
    //   239: putfield mImageContainer : Lcom/android/volley/toolbox/ImageLoader$ImageContainer;
    //   242: return }
  
  protected void onDetachedFromWindow() {
    if (this.mImageContainer != null) {
      this.mImageContainer.cancelRequest();
      setImageBitmap(null);
      this.mImageContainer = null;
    } 
    super.onDetachedFromWindow();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    loadImageIfNecessary(true);
  }
  
  public void setDefaultImageResId(int paramInt) { this.mDefaultImageId = paramInt; }
  
  public void setErrorImageResId(int paramInt) { this.mErrorImageId = paramInt; }
  
  public void setImageUrl(String paramString, ImageLoader paramImageLoader) {
    this.mUrl = paramString;
    this.mImageLoader = paramImageLoader;
    loadImageIfNecessary(false);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/NetworkImageView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */