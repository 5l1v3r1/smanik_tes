package android.support.v4.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.text.TextUtils;
import java.util.Collection;
import java.util.Locale;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class Preconditions {
  public static void checkArgument(boolean paramBoolean) {
    if (!paramBoolean)
      throw new IllegalArgumentException(); 
  }
  
  public static void checkArgument(boolean paramBoolean, Object paramObject) {
    if (!paramBoolean)
      throw new IllegalArgumentException(String.valueOf(paramObject)); 
  }
  
  public static float checkArgumentFinite(float paramFloat, String paramString) {
    if (Float.isNaN(paramFloat)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString);
      stringBuilder.append(" must not be NaN");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    if (Float.isInfinite(paramFloat)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString);
      stringBuilder.append(" must not be infinite");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    return paramFloat;
  }
  
  public static float checkArgumentInRange(float paramFloat1, float paramFloat2, float paramFloat3, String paramString) {
    if (Float.isNaN(paramFloat1)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString);
      stringBuilder.append(" must not be NaN");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    if (paramFloat1 < paramFloat2)
      throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%f, %f] (too low)", new Object[] { paramString, Float.valueOf(paramFloat2), Float.valueOf(paramFloat3) })); 
    if (paramFloat1 > paramFloat3)
      throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%f, %f] (too high)", new Object[] { paramString, Float.valueOf(paramFloat2), Float.valueOf(paramFloat3) })); 
    return paramFloat1;
  }
  
  public static int checkArgumentInRange(int paramInt1, int paramInt2, int paramInt3, String paramString) {
    if (paramInt1 < paramInt2)
      throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too low)", new Object[] { paramString, Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) })); 
    if (paramInt1 > paramInt3)
      throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too high)", new Object[] { paramString, Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) })); 
    return paramInt1;
  }
  
  public static long checkArgumentInRange(long paramLong1, long paramLong2, long paramLong3, String paramString) {
    if (paramLong1 < paramLong2)
      throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too low)", new Object[] { paramString, Long.valueOf(paramLong2), Long.valueOf(paramLong3) })); 
    if (paramLong1 > paramLong3)
      throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too high)", new Object[] { paramString, Long.valueOf(paramLong2), Long.valueOf(paramLong3) })); 
    return paramLong1;
  }
  
  @IntRange(from = 0L)
  public static int checkArgumentNonnegative(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    return paramInt;
  }
  
  @IntRange(from = 0L)
  public static int checkArgumentNonnegative(int paramInt, String paramString) {
    if (paramInt < 0)
      throw new IllegalArgumentException(paramString); 
    return paramInt;
  }
  
  public static long checkArgumentNonnegative(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(); 
    return paramLong;
  }
  
  public static long checkArgumentNonnegative(long paramLong, String paramString) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(paramString); 
    return paramLong;
  }
  
  public static int checkArgumentPositive(int paramInt, String paramString) {
    if (paramInt <= 0)
      throw new IllegalArgumentException(paramString); 
    return paramInt;
  }
  
  public static float[] checkArrayElementsInRange(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2, String paramString) {
    StringBuilder stringBuilder1;
    StringBuilder stringBuilder2 = new StringBuilder();
    stringBuilder2.append(paramString);
    stringBuilder2.append(" must not be null");
    checkNotNull(paramArrayOfFloat, stringBuilder2.toString());
    byte b;
    for (b = 0; b < paramArrayOfFloat.length; b++) {
      float f = paramArrayOfFloat[b];
      if (Float.isNaN(f)) {
        stringBuilder1 = new StringBuilder();
        stringBuilder1.append(paramString);
        stringBuilder1.append("[");
        stringBuilder1.append(b);
        stringBuilder1.append("] must not be NaN");
        throw new IllegalArgumentException(stringBuilder1.toString());
      } 
      if (f < paramFloat1)
        throw new IllegalArgumentException(String.format(Locale.US, "%s[%d] is out of range of [%f, %f] (too low)", new Object[] { paramString, Integer.valueOf(b), Float.valueOf(paramFloat1), Float.valueOf(paramFloat2) })); 
      if (f > paramFloat2)
        throw new IllegalArgumentException(String.format(Locale.US, "%s[%d] is out of range of [%f, %f] (too high)", new Object[] { paramString, Integer.valueOf(b), Float.valueOf(paramFloat1), Float.valueOf(paramFloat2) })); 
    } 
    return stringBuilder1;
  }
  
  public static <T> T[] checkArrayElementsNotNull(T[] paramArrayOfT, String paramString) { // Byte code:
    //   0: aload_0
    //   1: ifnonnull -> 37
    //   4: new java/lang/StringBuilder
    //   7: dup
    //   8: invokespecial <init> : ()V
    //   11: astore_0
    //   12: aload_0
    //   13: aload_1
    //   14: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: pop
    //   18: aload_0
    //   19: ldc ' must not be null'
    //   21: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: pop
    //   25: new java/lang/NullPointerException
    //   28: dup
    //   29: aload_0
    //   30: invokevirtual toString : ()Ljava/lang/String;
    //   33: invokespecial <init> : (Ljava/lang/String;)V
    //   36: athrow
    //   37: iconst_0
    //   38: istore_2
    //   39: iload_2
    //   40: aload_0
    //   41: arraylength
    //   42: if_icmpge -> 89
    //   45: aload_0
    //   46: iload_2
    //   47: aaload
    //   48: ifnonnull -> 82
    //   51: new java/lang/NullPointerException
    //   54: dup
    //   55: getstatic java/util/Locale.US : Ljava/util/Locale;
    //   58: ldc '%s[%d] must not be null'
    //   60: iconst_2
    //   61: anewarray java/lang/Object
    //   64: dup
    //   65: iconst_0
    //   66: aload_1
    //   67: aastore
    //   68: dup
    //   69: iconst_1
    //   70: iload_2
    //   71: invokestatic valueOf : (I)Ljava/lang/Integer;
    //   74: aastore
    //   75: invokestatic format : (Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   78: invokespecial <init> : (Ljava/lang/String;)V
    //   81: athrow
    //   82: iload_2
    //   83: iconst_1
    //   84: iadd
    //   85: istore_2
    //   86: goto -> 39
    //   89: aload_0
    //   90: areturn }
  
  @NonNull
  public static <C extends Collection<T>, T> C checkCollectionElementsNotNull(C paramC, String paramString) { // Byte code:
    //   0: aload_0
    //   1: ifnonnull -> 37
    //   4: new java/lang/StringBuilder
    //   7: dup
    //   8: invokespecial <init> : ()V
    //   11: astore_0
    //   12: aload_0
    //   13: aload_1
    //   14: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: pop
    //   18: aload_0
    //   19: ldc ' must not be null'
    //   21: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: pop
    //   25: new java/lang/NullPointerException
    //   28: dup
    //   29: aload_0
    //   30: invokevirtual toString : ()Ljava/lang/String;
    //   33: invokespecial <init> : (Ljava/lang/String;)V
    //   36: athrow
    //   37: lconst_0
    //   38: lstore_2
    //   39: aload_0
    //   40: invokeinterface iterator : ()Ljava/util/Iterator;
    //   45: astore #4
    //   47: aload #4
    //   49: invokeinterface hasNext : ()Z
    //   54: ifeq -> 105
    //   57: aload #4
    //   59: invokeinterface next : ()Ljava/lang/Object;
    //   64: ifnonnull -> 98
    //   67: new java/lang/NullPointerException
    //   70: dup
    //   71: getstatic java/util/Locale.US : Ljava/util/Locale;
    //   74: ldc '%s[%d] must not be null'
    //   76: iconst_2
    //   77: anewarray java/lang/Object
    //   80: dup
    //   81: iconst_0
    //   82: aload_1
    //   83: aastore
    //   84: dup
    //   85: iconst_1
    //   86: lload_2
    //   87: invokestatic valueOf : (J)Ljava/lang/Long;
    //   90: aastore
    //   91: invokestatic format : (Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   94: invokespecial <init> : (Ljava/lang/String;)V
    //   97: athrow
    //   98: lload_2
    //   99: lconst_1
    //   100: ladd
    //   101: lstore_2
    //   102: goto -> 47
    //   105: aload_0
    //   106: areturn }
  
  public static <T> Collection<T> checkCollectionNotEmpty(Collection<T> paramCollection, String paramString) {
    StringBuilder stringBuilder;
    if (paramCollection == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append(paramString);
      stringBuilder.append(" must not be null");
      throw new NullPointerException(stringBuilder.toString());
    } 
    if (stringBuilder.isEmpty()) {
      stringBuilder = new StringBuilder();
      stringBuilder.append(paramString);
      stringBuilder.append(" is empty");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    return stringBuilder;
  }
  
  public static int checkFlagsArgument(int paramInt1, int paramInt2) {
    if ((paramInt1 & paramInt2) != paramInt1) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Requested flags 0x");
      stringBuilder.append(Integer.toHexString(paramInt1));
      stringBuilder.append(", but only 0x");
      stringBuilder.append(Integer.toHexString(paramInt2));
      stringBuilder.append(" are allowed");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    return paramInt1;
  }
  
  @NonNull
  public static <T> T checkNotNull(T paramT) {
    if (paramT == null)
      throw new NullPointerException(); 
    return paramT;
  }
  
  @NonNull
  public static <T> T checkNotNull(T paramT, Object paramObject) {
    if (paramT == null)
      throw new NullPointerException(String.valueOf(paramObject)); 
    return paramT;
  }
  
  public static void checkState(boolean paramBoolean) { checkState(paramBoolean, null); }
  
  public static void checkState(boolean paramBoolean, String paramString) {
    if (!paramBoolean)
      throw new IllegalStateException(paramString); 
  }
  
  @NonNull
  public static <T extends CharSequence> T checkStringNotEmpty(T paramT) {
    if (TextUtils.isEmpty(paramT))
      throw new IllegalArgumentException(); 
    return paramT;
  }
  
  @NonNull
  public static <T extends CharSequence> T checkStringNotEmpty(T paramT, Object paramObject) {
    if (TextUtils.isEmpty(paramT))
      throw new IllegalArgumentException(String.valueOf(paramObject)); 
    return paramT;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/util/Preconditions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */