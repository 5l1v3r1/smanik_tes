package android.support.v4.provider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.GuardedBy;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.TypefaceCompat;
import android.support.v4.graphics.TypefaceCompatUtil;
import android.support.v4.util.LruCache;
import android.support.v4.util.Preconditions;
import android.support.v4.util.SimpleArrayMap;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class FontsContractCompat {
  private static final int BACKGROUND_THREAD_KEEP_ALIVE_DURATION_MS = 10000;
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static final String PARCEL_FONT_RESULTS = "font_results";
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static final int RESULT_CODE_PROVIDER_NOT_FOUND = -1;
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static final int RESULT_CODE_WRONG_CERTIFICATES = -2;
  
  private static final String TAG = "FontsContractCompat";
  
  private static final SelfDestructiveThread sBackgroundThread;
  
  private static final Comparator<byte[]> sByteArrayComparator;
  
  private static final Object sLock;
  
  @GuardedBy("sLock")
  private static final SimpleArrayMap<String, ArrayList<SelfDestructiveThread.ReplyCallback<TypefaceResult>>> sPendingReplies;
  
  private static final LruCache<String, Typeface> sTypefaceCache = new LruCache(16);
  
  static  {
    sBackgroundThread = new SelfDestructiveThread("fonts", 10, 10000);
    sLock = new Object();
    sPendingReplies = new SimpleArrayMap();
    sByteArrayComparator = new Comparator<byte[]>() {
        public int compare(byte[] param1ArrayOfByte1, byte[] param1ArrayOfByte2) {
          if (param1ArrayOfByte1.length != param1ArrayOfByte2.length)
            return param1ArrayOfByte1.length - param1ArrayOfByte2.length; 
          for (byte b = 0; b < param1ArrayOfByte1.length; b++) {
            if (param1ArrayOfByte1[b] != param1ArrayOfByte2[b])
              return param1ArrayOfByte1[b] - param1ArrayOfByte2[b]; 
          } 
          return 0;
        }
      };
  }
  
  @Nullable
  public static Typeface buildTypeface(@NonNull Context paramContext, @Nullable CancellationSignal paramCancellationSignal, @NonNull FontInfo[] paramArrayOfFontInfo) { return TypefaceCompat.createFromFontInfo(paramContext, paramCancellationSignal, paramArrayOfFontInfo, 0); }
  
  private static List<byte[]> convertToByteArrayList(Signature[] paramArrayOfSignature) {
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < paramArrayOfSignature.length; b++)
      arrayList.add(paramArrayOfSignature[b].toByteArray()); 
    return arrayList;
  }
  
  private static boolean equalsByteArrayList(List<byte[]> paramList1, List<byte[]> paramList2) {
    if (paramList1.size() != paramList2.size())
      return false; 
    for (byte b = 0; b < paramList1.size(); b++) {
      if (!Arrays.equals((byte[])paramList1.get(b), (byte[])paramList2.get(b)))
        return false; 
    } 
    return true;
  }
  
  @NonNull
  public static FontFamilyResult fetchFonts(@NonNull Context paramContext, @Nullable CancellationSignal paramCancellationSignal, @NonNull FontRequest paramFontRequest) throws PackageManager.NameNotFoundException {
    ProviderInfo providerInfo = getProvider(paramContext.getPackageManager(), paramFontRequest, paramContext.getResources());
    return (providerInfo == null) ? new FontFamilyResult(1, null) : new FontFamilyResult(0, getFontFromProvider(paramContext, paramFontRequest, providerInfo.authority, paramCancellationSignal));
  }
  
  private static List<List<byte[]>> getCertificates(FontRequest paramFontRequest, Resources paramResources) { return (paramFontRequest.getCertificates() != null) ? paramFontRequest.getCertificates() : FontResourcesParserCompat.readCerts(paramResources, paramFontRequest.getCertificatesArrayResId()); }
  
  @NonNull
  @VisibleForTesting
  static FontInfo[] getFontFromProvider(Context paramContext, FontRequest paramFontRequest, String paramString, CancellationSignal paramCancellationSignal) { // Byte code:
    //   0: new java/util/ArrayList
    //   3: dup
    //   4: invokespecial <init> : ()V
    //   7: astore #14
    //   9: new android/net/Uri$Builder
    //   12: dup
    //   13: invokespecial <init> : ()V
    //   16: ldc 'content'
    //   18: invokevirtual scheme : (Ljava/lang/String;)Landroid/net/Uri$Builder;
    //   21: aload_2
    //   22: invokevirtual authority : (Ljava/lang/String;)Landroid/net/Uri$Builder;
    //   25: invokevirtual build : ()Landroid/net/Uri;
    //   28: astore #16
    //   30: new android/net/Uri$Builder
    //   33: dup
    //   34: invokespecial <init> : ()V
    //   37: ldc 'content'
    //   39: invokevirtual scheme : (Ljava/lang/String;)Landroid/net/Uri$Builder;
    //   42: aload_2
    //   43: invokevirtual authority : (Ljava/lang/String;)Landroid/net/Uri$Builder;
    //   46: ldc 'file'
    //   48: invokevirtual appendPath : (Ljava/lang/String;)Landroid/net/Uri$Builder;
    //   51: invokevirtual build : ()Landroid/net/Uri;
    //   54: astore #17
    //   56: aconst_null
    //   57: astore #15
    //   59: aload #15
    //   61: astore_2
    //   62: getstatic android/os/Build$VERSION.SDK_INT : I
    //   65: bipush #16
    //   67: if_icmple -> 160
    //   70: aload #15
    //   72: astore_2
    //   73: aload_0
    //   74: invokevirtual getContentResolver : ()Landroid/content/ContentResolver;
    //   77: astore_0
    //   78: aload #15
    //   80: astore_2
    //   81: aload_1
    //   82: invokevirtual getQuery : ()Ljava/lang/String;
    //   85: astore_1
    //   86: aload #15
    //   88: astore_2
    //   89: aload_0
    //   90: aload #16
    //   92: bipush #7
    //   94: anewarray java/lang/String
    //   97: dup
    //   98: iconst_0
    //   99: ldc_w '_id'
    //   102: aastore
    //   103: dup
    //   104: iconst_1
    //   105: ldc_w 'file_id'
    //   108: aastore
    //   109: dup
    //   110: iconst_2
    //   111: ldc_w 'font_ttc_index'
    //   114: aastore
    //   115: dup
    //   116: iconst_3
    //   117: ldc_w 'font_variation_settings'
    //   120: aastore
    //   121: dup
    //   122: iconst_4
    //   123: ldc_w 'font_weight'
    //   126: aastore
    //   127: dup
    //   128: iconst_5
    //   129: ldc_w 'font_italic'
    //   132: aastore
    //   133: dup
    //   134: bipush #6
    //   136: ldc_w 'result_code'
    //   139: aastore
    //   140: ldc_w 'query = ?'
    //   143: iconst_1
    //   144: anewarray java/lang/String
    //   147: dup
    //   148: iconst_0
    //   149: aload_1
    //   150: aastore
    //   151: aconst_null
    //   152: aload_3
    //   153: invokevirtual query : (Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/database/Cursor;
    //   156: astore_0
    //   157: goto -> 565
    //   160: aload #15
    //   162: astore_2
    //   163: aload_0
    //   164: invokevirtual getContentResolver : ()Landroid/content/ContentResolver;
    //   167: astore_0
    //   168: aload #15
    //   170: astore_2
    //   171: aload_1
    //   172: invokevirtual getQuery : ()Ljava/lang/String;
    //   175: astore_1
    //   176: aload #15
    //   178: astore_2
    //   179: aload_0
    //   180: aload #16
    //   182: bipush #7
    //   184: anewarray java/lang/String
    //   187: dup
    //   188: iconst_0
    //   189: ldc_w '_id'
    //   192: aastore
    //   193: dup
    //   194: iconst_1
    //   195: ldc_w 'file_id'
    //   198: aastore
    //   199: dup
    //   200: iconst_2
    //   201: ldc_w 'font_ttc_index'
    //   204: aastore
    //   205: dup
    //   206: iconst_3
    //   207: ldc_w 'font_variation_settings'
    //   210: aastore
    //   211: dup
    //   212: iconst_4
    //   213: ldc_w 'font_weight'
    //   216: aastore
    //   217: dup
    //   218: iconst_5
    //   219: ldc_w 'font_italic'
    //   222: aastore
    //   223: dup
    //   224: bipush #6
    //   226: ldc_w 'result_code'
    //   229: aastore
    //   230: ldc_w 'query = ?'
    //   233: iconst_1
    //   234: anewarray java/lang/String
    //   237: dup
    //   238: iconst_0
    //   239: aload_1
    //   240: aastore
    //   241: aconst_null
    //   242: invokevirtual query : (Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   245: astore_0
    //   246: goto -> 565
    //   249: aload #14
    //   251: astore_1
    //   252: aload_0
    //   253: ifnull -> 530
    //   256: aload #14
    //   258: astore_1
    //   259: aload_0
    //   260: astore_2
    //   261: aload_0
    //   262: invokeinterface getCount : ()I
    //   267: ifle -> 530
    //   270: aload_0
    //   271: astore_2
    //   272: aload_0
    //   273: ldc_w 'result_code'
    //   276: invokeinterface getColumnIndex : (Ljava/lang/String;)I
    //   281: istore #7
    //   283: aload_0
    //   284: astore_2
    //   285: new java/util/ArrayList
    //   288: dup
    //   289: invokespecial <init> : ()V
    //   292: astore_3
    //   293: aload_0
    //   294: astore_2
    //   295: aload_0
    //   296: ldc_w '_id'
    //   299: invokeinterface getColumnIndex : (Ljava/lang/String;)I
    //   304: istore #8
    //   306: aload_0
    //   307: astore_2
    //   308: aload_0
    //   309: ldc_w 'file_id'
    //   312: invokeinterface getColumnIndex : (Ljava/lang/String;)I
    //   317: istore #9
    //   319: aload_0
    //   320: astore_2
    //   321: aload_0
    //   322: ldc_w 'font_ttc_index'
    //   325: invokeinterface getColumnIndex : (Ljava/lang/String;)I
    //   330: istore #10
    //   332: aload_0
    //   333: astore_2
    //   334: aload_0
    //   335: ldc_w 'font_weight'
    //   338: invokeinterface getColumnIndex : (Ljava/lang/String;)I
    //   343: istore #11
    //   345: aload_0
    //   346: astore_2
    //   347: aload_0
    //   348: ldc_w 'font_italic'
    //   351: invokeinterface getColumnIndex : (Ljava/lang/String;)I
    //   356: istore #12
    //   358: aload_0
    //   359: astore_2
    //   360: aload_0
    //   361: invokeinterface moveToNext : ()Z
    //   366: ifeq -> 528
    //   369: iload #7
    //   371: iconst_m1
    //   372: if_icmpeq -> 568
    //   375: aload_0
    //   376: astore_2
    //   377: aload_0
    //   378: iload #7
    //   380: invokeinterface getInt : (I)I
    //   385: istore #4
    //   387: goto -> 390
    //   390: iload #10
    //   392: iconst_m1
    //   393: if_icmpeq -> 574
    //   396: aload_0
    //   397: astore_2
    //   398: aload_0
    //   399: iload #10
    //   401: invokeinterface getInt : (I)I
    //   406: istore #5
    //   408: goto -> 411
    //   411: iload #9
    //   413: iconst_m1
    //   414: if_icmpne -> 436
    //   417: aload_0
    //   418: astore_2
    //   419: aload #16
    //   421: aload_0
    //   422: iload #8
    //   424: invokeinterface getLong : (I)J
    //   429: invokestatic withAppendedId : (Landroid/net/Uri;J)Landroid/net/Uri;
    //   432: astore_1
    //   433: goto -> 580
    //   436: aload_0
    //   437: astore_2
    //   438: aload #17
    //   440: aload_0
    //   441: iload #9
    //   443: invokeinterface getLong : (I)J
    //   448: invokestatic withAppendedId : (Landroid/net/Uri;J)Landroid/net/Uri;
    //   451: astore_1
    //   452: goto -> 580
    //   455: iload #11
    //   457: iconst_m1
    //   458: if_icmpeq -> 583
    //   461: aload_0
    //   462: astore_2
    //   463: aload_0
    //   464: iload #11
    //   466: invokeinterface getInt : (I)I
    //   471: istore #6
    //   473: goto -> 476
    //   476: iload #12
    //   478: iconst_m1
    //   479: if_icmpeq -> 591
    //   482: aload_0
    //   483: astore_2
    //   484: aload_0
    //   485: iload #12
    //   487: invokeinterface getInt : (I)I
    //   492: iconst_1
    //   493: if_icmpne -> 591
    //   496: iconst_1
    //   497: istore #13
    //   499: goto -> 502
    //   502: aload_0
    //   503: astore_2
    //   504: aload_3
    //   505: new android/support/v4/provider/FontsContractCompat$FontInfo
    //   508: dup
    //   509: aload_1
    //   510: iload #5
    //   512: iload #6
    //   514: iload #13
    //   516: iload #4
    //   518: invokespecial <init> : (Landroid/net/Uri;IIZI)V
    //   521: invokevirtual add : (Ljava/lang/Object;)Z
    //   524: pop
    //   525: goto -> 358
    //   528: aload_3
    //   529: astore_1
    //   530: aload_0
    //   531: ifnull -> 540
    //   534: aload_0
    //   535: invokeinterface close : ()V
    //   540: aload_1
    //   541: iconst_0
    //   542: anewarray android/support/v4/provider/FontsContractCompat$FontInfo
    //   545: invokevirtual toArray : ([Ljava/lang/Object;)[Ljava/lang/Object;
    //   548: checkcast [Landroid/support/v4/provider/FontsContractCompat$FontInfo;
    //   551: areturn
    //   552: astore_0
    //   553: aload_2
    //   554: ifnull -> 563
    //   557: aload_2
    //   558: invokeinterface close : ()V
    //   563: aload_0
    //   564: athrow
    //   565: goto -> 249
    //   568: iconst_0
    //   569: istore #4
    //   571: goto -> 390
    //   574: iconst_0
    //   575: istore #5
    //   577: goto -> 411
    //   580: goto -> 455
    //   583: sipush #400
    //   586: istore #6
    //   588: goto -> 476
    //   591: iconst_0
    //   592: istore #13
    //   594: goto -> 502
    // Exception table:
    //   from	to	target	type
    //   62	70	552	finally
    //   73	78	552	finally
    //   81	86	552	finally
    //   89	157	552	finally
    //   163	168	552	finally
    //   171	176	552	finally
    //   179	246	552	finally
    //   261	270	552	finally
    //   272	283	552	finally
    //   285	293	552	finally
    //   295	306	552	finally
    //   308	319	552	finally
    //   321	332	552	finally
    //   334	345	552	finally
    //   347	358	552	finally
    //   360	369	552	finally
    //   377	387	552	finally
    //   398	408	552	finally
    //   419	433	552	finally
    //   438	452	552	finally
    //   463	473	552	finally
    //   484	496	552	finally
    //   504	525	552	finally }
  
  @NonNull
  private static TypefaceResult getFontInternal(Context paramContext, FontRequest paramFontRequest, int paramInt) {
    try {
      FontFamilyResult fontFamilyResult = fetchFonts(paramContext, null, paramFontRequest);
      int i = fontFamilyResult.getStatusCode();
      byte b = -3;
      if (i == 0) {
        Typeface typeface = TypefaceCompat.createFromFontInfo(paramContext, null, fontFamilyResult.getFonts(), paramInt);
        if (typeface != null)
          b = 0; 
        return new TypefaceResult(typeface, b);
      } 
      if (fontFamilyResult.getStatusCode() == 1)
        b = -2; 
      return new TypefaceResult(null, b);
    } catch (android.content.pm.PackageManager.NameNotFoundException paramContext) {
      return new TypefaceResult(null, -1);
    } 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static Typeface getFontSync(Context paramContext, final FontRequest request, @Nullable final ResourcesCompat.FontCallback fontCallback, @Nullable final Handler handler, boolean paramBoolean, int paramInt1, final int style) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(paramFontRequest.getIdentifier());
    stringBuilder.append("-");
    stringBuilder.append(paramInt2);
    final String id = stringBuilder.toString();
    Typeface typeface = (Typeface)sTypefaceCache.get(str);
    if (typeface != null) {
      if (paramFontCallback != null)
        paramFontCallback.onFontRetrieved(typeface); 
      return typeface;
    } 
    if (paramBoolean && paramInt1 == -1) {
      null = getFontInternal(paramContext, paramFontRequest, paramInt2);
      if (paramFontCallback != null)
        if (null.mResult == 0) {
          paramFontCallback.callbackSuccessAsync(null.mTypeface, paramHandler);
        } else {
          paramFontCallback.callbackFailAsync(null.mResult, paramHandler);
        }  
      return null.mTypeface;
    } 
    Callable<TypefaceResult> callable = new Callable<TypefaceResult>() {
        public FontsContractCompat.TypefaceResult call() throws Exception {
          FontsContractCompat.TypefaceResult typefaceResult = FontsContractCompat.getFontInternal(context, request, style);
          if (typefaceResult.mTypeface != null)
            sTypefaceCache.put(id, typefaceResult.mTypeface); 
          return typefaceResult;
        }
      };
    if (paramBoolean)
      try {
        return ((TypefaceResult)sBackgroundThread.postAndWait(callable, paramInt1)).mTypeface;
      } catch (InterruptedException null) {
        return null;
      }  
    if (paramFontCallback == null) {
      null = null;
    } else {
      null = new SelfDestructiveThread.ReplyCallback<TypefaceResult>() {
          public void onReply(FontsContractCompat.TypefaceResult param1TypefaceResult) {
            if (param1TypefaceResult == null) {
              fontCallback.callbackFailAsync(1, handler);
              return;
            } 
            if (param1TypefaceResult.mResult == 0) {
              fontCallback.callbackSuccessAsync(param1TypefaceResult.mTypeface, handler);
              return;
            } 
            fontCallback.callbackFailAsync(param1TypefaceResult.mResult, handler);
          }
        };
    } 
    synchronized (sLock) {
      if (sPendingReplies.containsKey(str)) {
        if (null != null)
          ((ArrayList)sPendingReplies.get(str)).add(null); 
        return null;
      } 
      if (null != null) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(null);
        sPendingReplies.put(str, arrayList);
      } 
      sBackgroundThread.postAndReply(callable, new SelfDestructiveThread.ReplyCallback<TypefaceResult>() {
            public void onReply(FontsContractCompat.TypefaceResult param1TypefaceResult) {
              synchronized (sLock) {
                ArrayList arrayList = (ArrayList)sPendingReplies.get(id);
                if (arrayList == null)
                  return; 
                sPendingReplies.remove(id);
                for (byte b = 0; b < arrayList.size(); b++)
                  ((ReplyCallback)arrayList.get(b)).onReply(param1TypefaceResult); 
                return;
              } 
            }
          });
      return null;
    } 
  }
  
  @Nullable
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  @VisibleForTesting
  public static ProviderInfo getProvider(@NonNull PackageManager paramPackageManager, @NonNull FontRequest paramFontRequest, @Nullable Resources paramResources) throws PackageManager.NameNotFoundException {
    StringBuilder stringBuilder;
    String str = paramFontRequest.getProviderAuthority();
    byte b = 0;
    ProviderInfo providerInfo = paramPackageManager.resolveContentProvider(str, 0);
    if (providerInfo == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("No package found for authority: ");
      stringBuilder.append(str);
      throw new PackageManager.NameNotFoundException(stringBuilder.toString());
    } 
    if (!providerInfo.packageName.equals(paramFontRequest.getProviderPackage())) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Found content provider ");
      stringBuilder.append(str);
      stringBuilder.append(", but package was not ");
      stringBuilder.append(paramFontRequest.getProviderPackage());
      throw new PackageManager.NameNotFoundException(stringBuilder.toString());
    } 
    List list1 = convertToByteArrayList((stringBuilder.getPackageInfo(providerInfo.packageName, 64)).signatures);
    Collections.sort(list1, sByteArrayComparator);
    List list2 = getCertificates(paramFontRequest, paramResources);
    while (b < list2.size()) {
      ArrayList arrayList = new ArrayList((Collection)list2.get(b));
      Collections.sort(arrayList, sByteArrayComparator);
      if (equalsByteArrayList(list1, arrayList))
        return providerInfo; 
      b++;
    } 
    return null;
  }
  
  @RequiresApi(19)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static Map<Uri, ByteBuffer> prepareFontData(Context paramContext, FontInfo[] paramArrayOfFontInfo, CancellationSignal paramCancellationSignal) {
    HashMap hashMap = new HashMap();
    int i = paramArrayOfFontInfo.length;
    for (byte b = 0; b < i; b++) {
      FontInfo fontInfo = paramArrayOfFontInfo[b];
      if (fontInfo.getResultCode() == 0) {
        Uri uri = fontInfo.getUri();
        if (!hashMap.containsKey(uri))
          hashMap.put(uri, TypefaceCompatUtil.mmap(paramContext, paramCancellationSignal, uri)); 
      } 
    } 
    return Collections.unmodifiableMap(hashMap);
  }
  
  public static void requestFont(@NonNull final Context context, @NonNull final FontRequest request, @NonNull final FontRequestCallback callback, @NonNull Handler paramHandler) { paramHandler.post(new Runnable() {
          public void run() {
            try {
              FontsContractCompat.FontFamilyResult fontFamilyResult = FontsContractCompat.fetchFonts(context, null, request);
              if (fontFamilyResult.getStatusCode() != 0) {
                switch (fontFamilyResult.getStatusCode()) {
                  default:
                    callerThreadHandler.post(new Runnable() {
                          public void run() { callback.onTypefaceRequestFailed(-3); }
                        });
                    return;
                  case 2:
                    callerThreadHandler.post(new Runnable() {
                          public void run() { callback.onTypefaceRequestFailed(-3); }
                        });
                    return;
                  case 1:
                    break;
                } 
                callerThreadHandler.post(new Runnable() {
                      public void run() { callback.onTypefaceRequestFailed(-2); }
                    });
                return;
              } 
              FontsContractCompat.FontInfo[] arrayOfFontInfo = fontFamilyResult.getFonts();
              if (arrayOfFontInfo == null || arrayOfFontInfo.length == 0) {
                callerThreadHandler.post(new Runnable() {
                      public void run() { callback.onTypefaceRequestFailed(1); }
                    });
                return;
              } 
              int j = arrayOfFontInfo.length;
              for (final int resultCode = 0; i < j; i++) {
                FontsContractCompat.FontInfo fontInfo = arrayOfFontInfo[i];
                if (fontInfo.getResultCode() != 0) {
                  i = fontInfo.getResultCode();
                  if (i < 0) {
                    callerThreadHandler.post(new Runnable() {
                          public void run() { callback.onTypefaceRequestFailed(-3); }
                        });
                    return;
                  } 
                  callerThreadHandler.post(new Runnable() {
                        public void run() { callback.onTypefaceRequestFailed(resultCode); }
                      });
                  return;
                } 
              } 
              final Typeface typeface = FontsContractCompat.buildTypeface(context, null, arrayOfFontInfo);
              if (typeface == null) {
                callerThreadHandler.post(new Runnable() {
                      public void run() { callback.onTypefaceRequestFailed(-3); }
                    });
                return;
              } 
              callerThreadHandler.post(new Runnable() {
                    public void run() { callback.onTypefaceRetrieved(typeface); }
                  });
              return;
            } catch (android.content.pm.PackageManager.NameNotFoundException nameNotFoundException) {
              callerThreadHandler.post(new Runnable() {
                    public void run() { callback.onTypefaceRequestFailed(-1); }
                  });
              return;
            } 
          }
        }); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static void resetCache() { sTypefaceCache.evictAll(); }
  
  public static final class Columns implements BaseColumns {
    public static final String FILE_ID = "file_id";
    
    public static final String ITALIC = "font_italic";
    
    public static final String RESULT_CODE = "result_code";
    
    public static final int RESULT_CODE_FONT_NOT_FOUND = 1;
    
    public static final int RESULT_CODE_FONT_UNAVAILABLE = 2;
    
    public static final int RESULT_CODE_MALFORMED_QUERY = 3;
    
    public static final int RESULT_CODE_OK = 0;
    
    public static final String TTC_INDEX = "font_ttc_index";
    
    public static final String VARIATION_SETTINGS = "font_variation_settings";
    
    public static final String WEIGHT = "font_weight";
  }
  
  public static class FontFamilyResult {
    public static final int STATUS_OK = 0;
    
    public static final int STATUS_UNEXPECTED_DATA_PROVIDED = 2;
    
    public static final int STATUS_WRONG_CERTIFICATES = 1;
    
    private final FontsContractCompat.FontInfo[] mFonts;
    
    private final int mStatusCode;
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public FontFamilyResult(int param1Int, @Nullable FontsContractCompat.FontInfo[] param1ArrayOfFontInfo) {
      this.mStatusCode = param1Int;
      this.mFonts = param1ArrayOfFontInfo;
    }
    
    public FontsContractCompat.FontInfo[] getFonts() { return this.mFonts; }
    
    public int getStatusCode() { return this.mStatusCode; }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    static @interface FontResultStatus {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static @interface FontResultStatus {}
  
  public static class FontInfo {
    private final boolean mItalic;
    
    private final int mResultCode;
    
    private final int mTtcIndex;
    
    private final Uri mUri;
    
    private final int mWeight;
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public FontInfo(@NonNull Uri param1Uri, @IntRange(from = 0L) int param1Int1, @IntRange(from = 1L, to = 1000L) int param1Int2, boolean param1Boolean, int param1Int3) {
      this.mUri = (Uri)Preconditions.checkNotNull(param1Uri);
      this.mTtcIndex = param1Int1;
      this.mWeight = param1Int2;
      this.mItalic = param1Boolean;
      this.mResultCode = param1Int3;
    }
    
    public int getResultCode() { return this.mResultCode; }
    
    @IntRange(from = 0L)
    public int getTtcIndex() { return this.mTtcIndex; }
    
    @NonNull
    public Uri getUri() { return this.mUri; }
    
    @IntRange(from = 1L, to = 1000L)
    public int getWeight() { return this.mWeight; }
    
    public boolean isItalic() { return this.mItalic; }
  }
  
  public static class FontRequestCallback {
    public static final int FAIL_REASON_FONT_LOAD_ERROR = -3;
    
    public static final int FAIL_REASON_FONT_NOT_FOUND = 1;
    
    public static final int FAIL_REASON_FONT_UNAVAILABLE = 2;
    
    public static final int FAIL_REASON_MALFORMED_QUERY = 3;
    
    public static final int FAIL_REASON_PROVIDER_NOT_FOUND = -1;
    
    public static final int FAIL_REASON_SECURITY_VIOLATION = -4;
    
    public static final int FAIL_REASON_WRONG_CERTIFICATES = -2;
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static final int RESULT_OK = 0;
    
    public void onTypefaceRequestFailed(int param1Int) {}
    
    public void onTypefaceRetrieved(Typeface param1Typeface) {}
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static @interface FontRequestFailReason {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface FontRequestFailReason {}
  
  private static final class TypefaceResult {
    final int mResult;
    
    final Typeface mTypeface;
    
    TypefaceResult(@Nullable Typeface param1Typeface, int param1Int) {
      this.mTypeface = param1Typeface;
      this.mResult = param1Int;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/provider/FontsContractCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */