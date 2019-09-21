package android.support.v4.os;

import android.os.Build;
import android.support.annotation.GuardedBy;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.Size;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

@RequiresApi(14)
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
final class LocaleListHelper {
  private static final Locale EN_LATN;
  
  private static final Locale LOCALE_AR_XB;
  
  private static final Locale LOCALE_EN_XA;
  
  private static final int NUM_PSEUDO_LOCALES = 2;
  
  private static final String STRING_AR_XB = "ar-XB";
  
  private static final String STRING_EN_XA = "en-XA";
  
  @GuardedBy("sLock")
  private static LocaleListHelper sDefaultAdjustedLocaleList;
  
  @GuardedBy("sLock")
  private static LocaleListHelper sDefaultLocaleList;
  
  private static final Locale[] sEmptyList = new Locale[0];
  
  private static final LocaleListHelper sEmptyLocaleList = new LocaleListHelper(new Locale[0]);
  
  @GuardedBy("sLock")
  private static Locale sLastDefaultLocale;
  
  @GuardedBy("sLock")
  private static LocaleListHelper sLastExplicitlySetLocaleList;
  
  private static final Object sLock;
  
  private final Locale[] mList;
  
  @NonNull
  private final String mStringRepresentation;
  
  static  {
    LOCALE_EN_XA = new Locale("en", "XA");
    LOCALE_AR_XB = new Locale("ar", "XB");
    EN_LATN = LocaleHelper.forLanguageTag("en-Latn");
    sLock = new Object();
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  LocaleListHelper(@NonNull Locale paramLocale, LocaleListHelper paramLocaleListHelper) { // Byte code:
    //   0: aload_0
    //   1: invokespecial <init> : ()V
    //   4: aload_1
    //   5: ifnonnull -> 18
    //   8: new java/lang/NullPointerException
    //   11: dup
    //   12: ldc 'topLocale is null'
    //   14: invokespecial <init> : (Ljava/lang/String;)V
    //   17: athrow
    //   18: iconst_0
    //   19: istore #6
    //   21: aload_2
    //   22: ifnonnull -> 31
    //   25: iconst_0
    //   26: istore #4
    //   28: goto -> 38
    //   31: aload_2
    //   32: getfield mList : [Ljava/util/Locale;
    //   35: arraylength
    //   36: istore #4
    //   38: iconst_0
    //   39: istore_3
    //   40: iload_3
    //   41: iload #4
    //   43: if_icmpge -> 69
    //   46: aload_1
    //   47: aload_2
    //   48: getfield mList : [Ljava/util/Locale;
    //   51: iload_3
    //   52: aaload
    //   53: invokevirtual equals : (Ljava/lang/Object;)Z
    //   56: ifeq -> 62
    //   59: goto -> 71
    //   62: iload_3
    //   63: iconst_1
    //   64: iadd
    //   65: istore_3
    //   66: goto -> 40
    //   69: iconst_m1
    //   70: istore_3
    //   71: iload_3
    //   72: iconst_m1
    //   73: if_icmpne -> 82
    //   76: iconst_1
    //   77: istore #5
    //   79: goto -> 85
    //   82: iconst_0
    //   83: istore #5
    //   85: iload #5
    //   87: iload #4
    //   89: iadd
    //   90: istore #8
    //   92: iload #8
    //   94: anewarray java/util/Locale
    //   97: astore #9
    //   99: aload #9
    //   101: iconst_0
    //   102: aload_1
    //   103: invokevirtual clone : ()Ljava/lang/Object;
    //   106: checkcast java/util/Locale
    //   109: aastore
    //   110: iload_3
    //   111: iconst_m1
    //   112: if_icmpne -> 151
    //   115: iconst_0
    //   116: istore_3
    //   117: iload_3
    //   118: iload #4
    //   120: if_icmpge -> 224
    //   123: iload_3
    //   124: iconst_1
    //   125: iadd
    //   126: istore #5
    //   128: aload #9
    //   130: iload #5
    //   132: aload_2
    //   133: getfield mList : [Ljava/util/Locale;
    //   136: iload_3
    //   137: aaload
    //   138: invokevirtual clone : ()Ljava/lang/Object;
    //   141: checkcast java/util/Locale
    //   144: aastore
    //   145: iload #5
    //   147: istore_3
    //   148: goto -> 117
    //   151: iconst_0
    //   152: istore #5
    //   154: iload #5
    //   156: iload_3
    //   157: if_icmpge -> 191
    //   160: iload #5
    //   162: iconst_1
    //   163: iadd
    //   164: istore #7
    //   166: aload #9
    //   168: iload #7
    //   170: aload_2
    //   171: getfield mList : [Ljava/util/Locale;
    //   174: iload #5
    //   176: aaload
    //   177: invokevirtual clone : ()Ljava/lang/Object;
    //   180: checkcast java/util/Locale
    //   183: aastore
    //   184: iload #7
    //   186: istore #5
    //   188: goto -> 154
    //   191: iload_3
    //   192: iconst_1
    //   193: iadd
    //   194: istore_3
    //   195: iload_3
    //   196: iload #4
    //   198: if_icmpge -> 224
    //   201: aload #9
    //   203: iload_3
    //   204: aload_2
    //   205: getfield mList : [Ljava/util/Locale;
    //   208: iload_3
    //   209: aaload
    //   210: invokevirtual clone : ()Ljava/lang/Object;
    //   213: checkcast java/util/Locale
    //   216: aastore
    //   217: iload_3
    //   218: iconst_1
    //   219: iadd
    //   220: istore_3
    //   221: goto -> 195
    //   224: new java/lang/StringBuilder
    //   227: dup
    //   228: invokespecial <init> : ()V
    //   231: astore_1
    //   232: iload #6
    //   234: istore_3
    //   235: iload_3
    //   236: iload #8
    //   238: if_icmpge -> 275
    //   241: aload_1
    //   242: aload #9
    //   244: iload_3
    //   245: aaload
    //   246: invokestatic toLanguageTag : (Ljava/util/Locale;)Ljava/lang/String;
    //   249: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   252: pop
    //   253: iload_3
    //   254: iload #8
    //   256: iconst_1
    //   257: isub
    //   258: if_icmpge -> 268
    //   261: aload_1
    //   262: bipush #44
    //   264: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   267: pop
    //   268: iload_3
    //   269: iconst_1
    //   270: iadd
    //   271: istore_3
    //   272: goto -> 235
    //   275: aload_0
    //   276: aload #9
    //   278: putfield mList : [Ljava/util/Locale;
    //   281: aload_0
    //   282: aload_1
    //   283: invokevirtual toString : ()Ljava/lang/String;
    //   286: putfield mStringRepresentation : Ljava/lang/String;
    //   289: return }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  LocaleListHelper(@NonNull Locale... paramVarArgs) {
    if (paramVarArgs.length == 0) {
      this.mList = sEmptyList;
      this.mStringRepresentation = "";
      return;
    } 
    Locale[] arrayOfLocale = new Locale[paramVarArgs.length];
    HashSet hashSet = new HashSet();
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < paramVarArgs.length; b++) {
      StringBuilder stringBuilder1;
      Locale locale = paramVarArgs[b];
      if (locale == null) {
        stringBuilder1 = new StringBuilder();
        stringBuilder1.append("list[");
        stringBuilder1.append(b);
        stringBuilder1.append("] is null");
        throw new NullPointerException(stringBuilder1.toString());
      } 
      if (hashSet.contains(locale)) {
        stringBuilder1 = new StringBuilder();
        stringBuilder1.append("list[");
        stringBuilder1.append(b);
        stringBuilder1.append("] is a repetition");
        throw new IllegalArgumentException(stringBuilder1.toString());
      } 
      locale = (Locale)locale.clone();
      arrayOfLocale[b] = locale;
      stringBuilder.append(LocaleHelper.toLanguageTag(locale));
      if (b < stringBuilder1.length - 1)
        stringBuilder.append(','); 
      hashSet.add(locale);
    } 
    this.mList = arrayOfLocale;
    this.mStringRepresentation = stringBuilder.toString();
  }
  
  private Locale computeFirstMatch(Collection<String> paramCollection, boolean paramBoolean) {
    int i = computeFirstMatchIndex(paramCollection, paramBoolean);
    return (i == -1) ? null : this.mList[i];
  }
  
  private int computeFirstMatchIndex(Collection<String> paramCollection, boolean paramBoolean) { // Byte code:
    //   0: aload_0
    //   1: getfield mList : [Ljava/util/Locale;
    //   4: arraylength
    //   5: iconst_1
    //   6: if_icmpne -> 11
    //   9: iconst_0
    //   10: ireturn
    //   11: aload_0
    //   12: getfield mList : [Ljava/util/Locale;
    //   15: arraylength
    //   16: ifne -> 21
    //   19: iconst_m1
    //   20: ireturn
    //   21: iload_2
    //   22: ifeq -> 48
    //   25: aload_0
    //   26: getstatic android/support/v4/os/LocaleListHelper.EN_LATN : Ljava/util/Locale;
    //   29: invokespecial findFirstMatchIndex : (Ljava/util/Locale;)I
    //   32: istore_3
    //   33: iload_3
    //   34: ifne -> 39
    //   37: iconst_0
    //   38: ireturn
    //   39: iload_3
    //   40: ldc 2147483647
    //   42: if_icmpge -> 48
    //   45: goto -> 51
    //   48: ldc 2147483647
    //   50: istore_3
    //   51: aload_1
    //   52: invokeinterface iterator : ()Ljava/util/Iterator;
    //   57: astore_1
    //   58: aload_1
    //   59: invokeinterface hasNext : ()Z
    //   64: ifeq -> 104
    //   67: aload_0
    //   68: aload_1
    //   69: invokeinterface next : ()Ljava/lang/Object;
    //   74: checkcast java/lang/String
    //   77: invokestatic forLanguageTag : (Ljava/lang/String;)Ljava/util/Locale;
    //   80: invokespecial findFirstMatchIndex : (Ljava/util/Locale;)I
    //   83: istore #4
    //   85: iload #4
    //   87: ifne -> 92
    //   90: iconst_0
    //   91: ireturn
    //   92: iload #4
    //   94: iload_3
    //   95: if_icmpge -> 58
    //   98: iload #4
    //   100: istore_3
    //   101: goto -> 58
    //   104: iload_3
    //   105: ldc 2147483647
    //   107: if_icmpne -> 112
    //   110: iconst_0
    //   111: ireturn
    //   112: iload_3
    //   113: ireturn }
  
  private int findFirstMatchIndex(Locale paramLocale) {
    for (byte b = 0; b < this.mList.length; b++) {
      if (matchScore(paramLocale, this.mList[b]) > 0)
        return b; 
    } 
    return Integer.MAX_VALUE;
  }
  
  @NonNull
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static LocaleListHelper forLanguageTags(@Nullable String paramString) {
    if (paramString == null || paramString.isEmpty())
      return getEmptyLocaleList(); 
    String[] arrayOfString = paramString.split(",");
    Locale[] arrayOfLocale = new Locale[arrayOfString.length];
    for (byte b = 0; b < arrayOfLocale.length; b++)
      arrayOfLocale[b] = LocaleHelper.forLanguageTag(arrayOfString[b]); 
    return new LocaleListHelper(arrayOfLocale);
  }
  
  @NonNull
  @Size(min = 1L)
  static LocaleListHelper getAdjustedDefault() {
    getDefault();
    synchronized (sLock) {
      return sDefaultAdjustedLocaleList;
    } 
  }
  
  @NonNull
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  @Size(min = 1L)
  static LocaleListHelper getDefault() {
    null = Locale.getDefault();
    synchronized (sLock) {
      if (!null.equals(sLastDefaultLocale)) {
        sLastDefaultLocale = null;
        if (sDefaultLocaleList != null && null.equals(sDefaultLocaleList.get(0)))
          return sDefaultLocaleList; 
        sDefaultLocaleList = new LocaleListHelper(null, sLastExplicitlySetLocaleList);
        sDefaultAdjustedLocaleList = sDefaultLocaleList;
      } 
      return sDefaultLocaleList;
    } 
  }
  
  @NonNull
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static LocaleListHelper getEmptyLocaleList() { return sEmptyLocaleList; }
  
  private static String getLikelyScript(Locale paramLocale) {
    if (Build.VERSION.SDK_INT >= 21) {
      String str = paramLocale.getScript();
      return !str.isEmpty() ? str : "";
    } 
    return "";
  }
  
  private static boolean isPseudoLocale(String paramString) { return ("en-XA".equals(paramString) || "ar-XB".equals(paramString)); }
  
  private static boolean isPseudoLocale(Locale paramLocale) { return (LOCALE_EN_XA.equals(paramLocale) || LOCALE_AR_XB.equals(paramLocale)); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static boolean isPseudoLocalesOnly(@Nullable String[] paramArrayOfString) {
    if (paramArrayOfString == null)
      return true; 
    if (paramArrayOfString.length > 3)
      return false; 
    int i = paramArrayOfString.length;
    for (byte b = 0; b < i; b++) {
      String str = paramArrayOfString[b];
      if (!str.isEmpty() && !isPseudoLocale(str))
        return false; 
    } 
    return true;
  }
  
  @IntRange(from = 0L, to = 1L)
  private static int matchScore(Locale paramLocale1, Locale paramLocale2) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:659)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static void setDefault(@NonNull @Size(min = 1L) LocaleListHelper paramLocaleListHelper) { setDefault(paramLocaleListHelper, 0); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static void setDefault(@NonNull @Size(min = 1L) LocaleListHelper paramLocaleListHelper, int paramInt) {
    if (paramLocaleListHelper == null)
      throw new NullPointerException("locales is null"); 
    if (paramLocaleListHelper.isEmpty())
      throw new IllegalArgumentException("locales is empty"); 
    synchronized (sLock) {
      (sLastDefaultLocale = paramLocaleListHelper.get(paramInt)).setDefault(sLastDefaultLocale);
      sLastExplicitlySetLocaleList = paramLocaleListHelper;
      sDefaultLocaleList = paramLocaleListHelper;
      if (paramInt == 0) {
        sDefaultAdjustedLocaleList = sDefaultLocaleList;
      } else {
        sDefaultAdjustedLocaleList = new LocaleListHelper(sLastDefaultLocale, sDefaultLocaleList);
      } 
      return;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof LocaleListHelper))
      return false; 
    Locale[] arrayOfLocale = ((LocaleListHelper)paramObject).mList;
    if (this.mList.length != arrayOfLocale.length)
      return false; 
    for (byte b = 0; b < this.mList.length; b++) {
      if (!this.mList[b].equals(arrayOfLocale[b]))
        return false; 
    } 
    return true;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  Locale get(int paramInt) { return (paramInt >= 0 && paramInt < this.mList.length) ? this.mList[paramInt] : null; }
  
  @Nullable
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  Locale getFirstMatch(String[] paramArrayOfString) { return computeFirstMatch(Arrays.asList(paramArrayOfString), false); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  int getFirstMatchIndex(String[] paramArrayOfString) { return computeFirstMatchIndex(Arrays.asList(paramArrayOfString), false); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  int getFirstMatchIndexWithEnglishSupported(Collection<String> paramCollection) { return computeFirstMatchIndex(paramCollection, true); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  int getFirstMatchIndexWithEnglishSupported(String[] paramArrayOfString) { return getFirstMatchIndexWithEnglishSupported(Arrays.asList(paramArrayOfString)); }
  
  @Nullable
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  Locale getFirstMatchWithEnglishSupported(String[] paramArrayOfString) { return computeFirstMatch(Arrays.asList(paramArrayOfString), true); }
  
  public int hashCode() {
    int i = 1;
    for (byte b = 0; b < this.mList.length; b++)
      i = i * 31 + this.mList[b].hashCode(); 
    return i;
  }
  
  @IntRange(from = -1L)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  int indexOf(Locale paramLocale) {
    for (byte b = 0; b < this.mList.length; b++) {
      if (this.mList[b].equals(paramLocale))
        return b; 
    } 
    return -1;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  boolean isEmpty() { return (this.mList.length == 0); }
  
  @IntRange(from = 0L)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  int size() { return this.mList.length; }
  
  @NonNull
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  String toLanguageTags() { return this.mStringRepresentation; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("[");
    for (byte b = 0; b < this.mList.length; b++) {
      stringBuilder.append(this.mList[b]);
      if (b < this.mList.length - 1)
        stringBuilder.append(','); 
    } 
    stringBuilder.append("]");
    return stringBuilder.toString();
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/os/LocaleListHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */