package com.android.volley.toolbox;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import java.util.Map;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

public class HttpHeaderParser {
  public static Cache.Entry parseCacheHeaders(NetworkResponse paramNetworkResponse) { // Byte code:
    //   0: invokestatic currentTimeMillis : ()J
    //   3: lstore #13
    //   5: aload_0
    //   6: getfield headers : Ljava/util/Map;
    //   9: astore #15
    //   11: aload #15
    //   13: ldc 'Date'
    //   15: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   20: checkcast java/lang/String
    //   23: astore #16
    //   25: aload #16
    //   27: ifnull -> 40
    //   30: aload #16
    //   32: invokestatic parseDateAsEpoch : (Ljava/lang/String;)J
    //   35: lstore #7
    //   37: goto -> 43
    //   40: lconst_0
    //   41: lstore #7
    //   43: aload #15
    //   45: ldc 'Cache-Control'
    //   47: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   52: checkcast java/lang/String
    //   55: astore #16
    //   57: iconst_0
    //   58: istore_2
    //   59: aload #16
    //   61: ifnull -> 233
    //   64: aload #16
    //   66: ldc ','
    //   68: invokevirtual split : (Ljava/lang/String;)[Ljava/lang/String;
    //   71: astore #16
    //   73: lconst_0
    //   74: lstore #5
    //   76: iconst_0
    //   77: istore_1
    //   78: lconst_0
    //   79: lstore_3
    //   80: iload_2
    //   81: aload #16
    //   83: arraylength
    //   84: if_icmpge -> 228
    //   87: aload #16
    //   89: iload_2
    //   90: aaload
    //   91: invokevirtual trim : ()Ljava/lang/String;
    //   94: astore #17
    //   96: aload #17
    //   98: ldc 'no-cache'
    //   100: invokevirtual equals : (Ljava/lang/Object;)Z
    //   103: ifne -> 226
    //   106: aload #17
    //   108: ldc 'no-store'
    //   110: invokevirtual equals : (Ljava/lang/Object;)Z
    //   113: ifeq -> 119
    //   116: goto -> 226
    //   119: aload #17
    //   121: ldc 'max-age='
    //   123: invokevirtual startsWith : (Ljava/lang/String;)Z
    //   126: ifeq -> 147
    //   129: aload #17
    //   131: bipush #8
    //   133: invokevirtual substring : (I)Ljava/lang/String;
    //   136: invokestatic parseLong : (Ljava/lang/String;)J
    //   139: lstore #9
    //   141: lload_3
    //   142: lstore #11
    //   144: goto -> 212
    //   147: aload #17
    //   149: ldc 'stale-while-revalidate='
    //   151: invokevirtual startsWith : (Ljava/lang/String;)Z
    //   154: ifeq -> 176
    //   157: aload #17
    //   159: bipush #23
    //   161: invokevirtual substring : (I)Ljava/lang/String;
    //   164: invokestatic parseLong : (Ljava/lang/String;)J
    //   167: lstore #11
    //   169: lload #5
    //   171: lstore #9
    //   173: goto -> 212
    //   176: aload #17
    //   178: ldc 'must-revalidate'
    //   180: invokevirtual equals : (Ljava/lang/Object;)Z
    //   183: ifne -> 203
    //   186: lload #5
    //   188: lstore #9
    //   190: lload_3
    //   191: lstore #11
    //   193: aload #17
    //   195: ldc 'proxy-revalidate'
    //   197: invokevirtual equals : (Ljava/lang/Object;)Z
    //   200: ifeq -> 212
    //   203: iconst_1
    //   204: istore_1
    //   205: lload_3
    //   206: lstore #11
    //   208: lload #5
    //   210: lstore #9
    //   212: iload_2
    //   213: iconst_1
    //   214: iadd
    //   215: istore_2
    //   216: lload #9
    //   218: lstore #5
    //   220: lload #11
    //   222: lstore_3
    //   223: goto -> 80
    //   226: aconst_null
    //   227: areturn
    //   228: iconst_1
    //   229: istore_2
    //   230: goto -> 242
    //   233: lconst_0
    //   234: lstore #5
    //   236: iconst_0
    //   237: istore_1
    //   238: lconst_0
    //   239: lstore_3
    //   240: iconst_0
    //   241: istore_2
    //   242: aload #15
    //   244: ldc 'Expires'
    //   246: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   251: checkcast java/lang/String
    //   254: astore #16
    //   256: aload #16
    //   258: ifnull -> 271
    //   261: aload #16
    //   263: invokestatic parseDateAsEpoch : (Ljava/lang/String;)J
    //   266: lstore #11
    //   268: goto -> 274
    //   271: lconst_0
    //   272: lstore #11
    //   274: aload #15
    //   276: ldc 'Last-Modified'
    //   278: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   283: checkcast java/lang/String
    //   286: astore #16
    //   288: aload #16
    //   290: ifnull -> 303
    //   293: aload #16
    //   295: invokestatic parseDateAsEpoch : (Ljava/lang/String;)J
    //   298: lstore #9
    //   300: goto -> 306
    //   303: lconst_0
    //   304: lstore #9
    //   306: aload #15
    //   308: ldc 'ETag'
    //   310: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   315: checkcast java/lang/String
    //   318: astore #16
    //   320: iload_2
    //   321: ifeq -> 357
    //   324: lload #13
    //   326: lload #5
    //   328: ldc2_w 1000
    //   331: lmul
    //   332: ladd
    //   333: lstore #5
    //   335: iload_1
    //   336: ifeq -> 345
    //   339: lload #5
    //   341: lstore_3
    //   342: goto -> 354
    //   345: lload #5
    //   347: lload_3
    //   348: ldc2_w 1000
    //   351: lmul
    //   352: ladd
    //   353: lstore_3
    //   354: goto -> 393
    //   357: lload #7
    //   359: lconst_0
    //   360: lcmp
    //   361: ifle -> 388
    //   364: lload #11
    //   366: lload #7
    //   368: lcmp
    //   369: iflt -> 388
    //   372: lload #13
    //   374: lload #11
    //   376: lload #7
    //   378: lsub
    //   379: ladd
    //   380: lstore #5
    //   382: lload #5
    //   384: lstore_3
    //   385: goto -> 393
    //   388: lconst_0
    //   389: lstore_3
    //   390: lload_3
    //   391: lstore #5
    //   393: new com/android/volley/Cache$Entry
    //   396: dup
    //   397: invokespecial <init> : ()V
    //   400: astore #17
    //   402: aload #17
    //   404: aload_0
    //   405: getfield data : [B
    //   408: putfield data : [B
    //   411: aload #17
    //   413: aload #16
    //   415: putfield etag : Ljava/lang/String;
    //   418: aload #17
    //   420: lload #5
    //   422: putfield softTtl : J
    //   425: aload #17
    //   427: lload_3
    //   428: putfield ttl : J
    //   431: aload #17
    //   433: lload #7
    //   435: putfield serverDate : J
    //   438: aload #17
    //   440: lload #9
    //   442: putfield lastModified : J
    //   445: aload #17
    //   447: aload #15
    //   449: putfield responseHeaders : Ljava/util/Map;
    //   452: aload #17
    //   454: areturn
    //   455: astore #17
    //   457: lload #5
    //   459: lstore #9
    //   461: lload_3
    //   462: lstore #11
    //   464: goto -> 212
    // Exception table:
    //   from	to	target	type
    //   129	141	455	java/lang/Exception
    //   157	169	455	java/lang/Exception }
  
  public static String parseCharset(Map<String, String> paramMap) { return parseCharset(paramMap, "ISO-8859-1"); }
  
  public static String parseCharset(Map<String, String> paramMap, String paramString) {
    String str = (String)paramMap.get("Content-Type");
    if (str != null) {
      String[] arrayOfString = str.split(";");
      for (byte b = 1; b < arrayOfString.length; b++) {
        String[] arrayOfString1 = arrayOfString[b].trim().split("=");
        if (arrayOfString1.length == 2 && arrayOfString1[0].equals("charset"))
          return arrayOfString1[1]; 
      } 
    } 
    return paramString;
  }
  
  public static long parseDateAsEpoch(String paramString) {
    try {
      return DateUtils.parseDate(paramString).getTime();
    } catch (DateParseException paramString) {
      return 0L;
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/HttpHeaderParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */