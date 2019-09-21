package android.support.v4.util;

import android.support.annotation.RestrictTo;
import java.io.PrintWriter;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class TimeUtils {
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static final int HUNDRED_DAY_FIELD_LEN = 19;
  
  private static final int SECONDS_PER_DAY = 86400;
  
  private static final int SECONDS_PER_HOUR = 3600;
  
  private static final int SECONDS_PER_MINUTE = 60;
  
  private static char[] sFormatStr;
  
  private static final Object sFormatSync = new Object();
  
  static  {
    sFormatStr = new char[24];
  }
  
  private static int accumField(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3) { return (paramInt1 > 99 || (paramBoolean && paramInt3 >= 3)) ? (paramInt2 + 3) : ((paramInt1 > 9 || (paramBoolean && paramInt3 >= 2)) ? (paramInt2 + 2) : ((paramBoolean || paramInt1 > 0) ? (paramInt2 + 1) : 0)); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static void formatDuration(long paramLong1, long paramLong2, PrintWriter paramPrintWriter) {
    if (paramLong1 == 0L) {
      paramPrintWriter.print("--");
      return;
    } 
    formatDuration(paramLong1 - paramLong2, paramPrintWriter, 0);
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static void formatDuration(long paramLong, PrintWriter paramPrintWriter) { formatDuration(paramLong, paramPrintWriter, 0); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static void formatDuration(long paramLong, PrintWriter paramPrintWriter, int paramInt) {
    synchronized (sFormatSync) {
      paramInt = formatDurationLocked(paramLong, paramInt);
      paramPrintWriter.print(new String(sFormatStr, 0, paramInt));
      return;
    } 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static void formatDuration(long paramLong, StringBuilder paramStringBuilder) {
    synchronized (sFormatSync) {
      int i = formatDurationLocked(paramLong, 0);
      paramStringBuilder.append(sFormatStr, 0, i);
      return;
    } 
  }
  
  private static int formatDurationLocked(long paramLong, int paramInt) {
    boolean bool1;
    boolean bool;
    byte b3;
    byte b2;
    int j;
    byte b1;
    if (sFormatStr.length < paramInt)
      sFormatStr = new char[paramInt]; 
    char[] arrayOfChar = sFormatStr;
    if (paramLong == 0L) {
      while (paramInt - 1 > 0)
        arrayOfChar[0] = ' '; 
      arrayOfChar[0] = '0';
      return 1;
    } 
    if (paramLong > 0L) {
      b1 = 43;
    } else {
      b1 = 45;
      paramLong = -paramLong;
    } 
    int m = (int)(paramLong % 1000L);
    int i = (int)Math.floor((paramLong / 1000L));
    if (i > 86400) {
      k = i / 86400;
      i -= 86400 * k;
    } else {
      k = 0;
    } 
    if (i > 3600) {
      b2 = i / 3600;
      i -= b2 * 3600;
    } else {
      b2 = 0;
    } 
    if (i > 60) {
      b3 = i / 60;
      j = i - b3 * 60;
    } else {
      b3 = 0;
      j = i;
    } 
    if (paramInt != 0) {
      i = accumField(k, 1, false, 0);
      if (i > 0) {
        bool1 = true;
      } else {
        bool1 = false;
      } 
      i += accumField(b2, 1, bool1, 2);
      if (i > 0) {
        bool1 = true;
      } else {
        bool1 = false;
      } 
      i += accumField(b3, 1, bool1, 2);
      if (i > 0) {
        bool1 = true;
      } else {
        bool1 = false;
      } 
      int n = i + accumField(j, 1, bool1, 2);
      if (n > 0) {
        i = 3;
      } else {
        i = 0;
      } 
      n += accumField(m, 2, true, i) + 1;
      i = 0;
      while (true) {
        bool = i;
        if (n < paramInt) {
          arrayOfChar[i] = ' ';
          i++;
          n++;
          continue;
        } 
        break;
      } 
    } else {
      bool = false;
    } 
    arrayOfChar[bool] = b1;
    byte b4 = bool + true;
    if (paramInt != 0) {
      paramInt = 1;
    } else {
      paramInt = 0;
    } 
    int k = printField(arrayOfChar, k, 'd', b4, false, 0);
    if (k != b4) {
      bool1 = true;
    } else {
      bool1 = false;
    } 
    if (paramInt != 0) {
      i = 2;
    } else {
      i = 0;
    } 
    k = printField(arrayOfChar, b2, 'h', k, bool1, i);
    if (k != b4) {
      bool1 = true;
    } else {
      bool1 = false;
    } 
    if (paramInt != 0) {
      i = 2;
    } else {
      i = 0;
    } 
    k = printField(arrayOfChar, b3, 'm', k, bool1, i);
    if (k != b4) {
      bool1 = true;
    } else {
      bool1 = false;
    } 
    if (paramInt != 0) {
      i = 2;
    } else {
      i = 0;
    } 
    i = printField(arrayOfChar, j, 's', k, bool1, i);
    if (paramInt != 0 && i != b4) {
      paramInt = 3;
    } else {
      paramInt = 0;
    } 
    paramInt = printField(arrayOfChar, m, 'm', i, true, paramInt);
    arrayOfChar[paramInt] = 's';
    return paramInt + 1;
  }
  
  private static int printField(char[] paramArrayOfChar, int paramInt1, char paramChar, int paramInt2, boolean paramBoolean, int paramInt3) { // Byte code:
    //   0: iload #4
    //   2: ifne -> 12
    //   5: iload_3
    //   6: istore #6
    //   8: iload_1
    //   9: ifle -> 145
    //   12: iload #4
    //   14: ifeq -> 23
    //   17: iload #5
    //   19: iconst_3
    //   20: if_icmpge -> 29
    //   23: iload_1
    //   24: bipush #99
    //   26: if_icmple -> 60
    //   29: iload_1
    //   30: bipush #100
    //   32: idiv
    //   33: istore #7
    //   35: aload_0
    //   36: iload_3
    //   37: iload #7
    //   39: bipush #48
    //   41: iadd
    //   42: i2c
    //   43: castore
    //   44: iload_3
    //   45: iconst_1
    //   46: iadd
    //   47: istore #6
    //   49: iload_1
    //   50: iload #7
    //   52: bipush #100
    //   54: imul
    //   55: isub
    //   56: istore_1
    //   57: goto -> 63
    //   60: iload_3
    //   61: istore #6
    //   63: iload #4
    //   65: ifeq -> 74
    //   68: iload #5
    //   70: iconst_2
    //   71: if_icmpge -> 93
    //   74: iload_1
    //   75: bipush #9
    //   77: if_icmpgt -> 93
    //   80: iload #6
    //   82: istore #7
    //   84: iload_1
    //   85: istore #5
    //   87: iload_3
    //   88: iload #6
    //   90: if_icmpeq -> 121
    //   93: iload_1
    //   94: bipush #10
    //   96: idiv
    //   97: istore_3
    //   98: aload_0
    //   99: iload #6
    //   101: iload_3
    //   102: bipush #48
    //   104: iadd
    //   105: i2c
    //   106: castore
    //   107: iload #6
    //   109: iconst_1
    //   110: iadd
    //   111: istore #7
    //   113: iload_1
    //   114: iload_3
    //   115: bipush #10
    //   117: imul
    //   118: isub
    //   119: istore #5
    //   121: aload_0
    //   122: iload #7
    //   124: iload #5
    //   126: bipush #48
    //   128: iadd
    //   129: i2c
    //   130: castore
    //   131: iload #7
    //   133: iconst_1
    //   134: iadd
    //   135: istore_1
    //   136: aload_0
    //   137: iload_1
    //   138: iload_2
    //   139: castore
    //   140: iload_1
    //   141: iconst_1
    //   142: iadd
    //   143: istore #6
    //   145: iload #6
    //   147: ireturn }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/util/TimeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */