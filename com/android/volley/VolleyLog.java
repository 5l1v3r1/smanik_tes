package com.android.volley;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VolleyLog {
  public static boolean DEBUG = Log.isLoggable(TAG, 2);
  
  public static String TAG = "Volley";
  
  private static String buildMessage(String paramString, Object... paramVarArgs) {
    String str1;
    if (paramVarArgs != null)
      paramString = String.format(Locale.US, paramString, paramVarArgs); 
    StackTraceElement[] arrayOfStackTraceElement = (new Throwable()).fillInStackTrace().getStackTrace();
    String str2 = "<unknown>";
    byte b = 2;
    while (true) {
      str1 = str2;
      if (b < arrayOfStackTraceElement.length) {
        if (!arrayOfStackTraceElement[b].getClass().equals(VolleyLog.class)) {
          str2 = String.valueOf((str1 = (str1 = arrayOfStackTraceElement[b].getClassName()).valueOf((str1 = str1.substring(str1.lastIndexOf('.') + 1)).valueOf(str1.substring(str1.lastIndexOf('$') + 1)))).valueOf(arrayOfStackTraceElement[b].getMethodName()));
          StringBuilder stringBuilder = new StringBuilder(str1.length() + 1 + str2.length());
          stringBuilder.append(str1);
          stringBuilder.append(".");
          stringBuilder.append(str2);
          str1 = stringBuilder.toString();
          break;
        } 
        b++;
        continue;
      } 
      break;
    } 
    return String.format(Locale.US, "[%d] %s: %s", new Object[] { Long.valueOf(Thread.currentThread().getId()), str1, paramString });
  }
  
  public static void d(String paramString, Object... paramVarArgs) { Log.d(TAG, buildMessage(paramString, paramVarArgs)); }
  
  public static void e(String paramString, Object... paramVarArgs) { Log.e(TAG, buildMessage(paramString, paramVarArgs)); }
  
  public static void e(Throwable paramThrowable, String paramString, Object... paramVarArgs) { Log.e(TAG, buildMessage(paramString, paramVarArgs), paramThrowable); }
  
  public static void setTag(String paramString) {
    d("Changing log tag to %s", new Object[] { paramString });
    TAG = paramString;
    DEBUG = Log.isLoggable(TAG, 2);
  }
  
  public static void v(String paramString, Object... paramVarArgs) {
    if (DEBUG)
      Log.v(TAG, buildMessage(paramString, paramVarArgs)); 
  }
  
  public static void wtf(String paramString, Object... paramVarArgs) { Log.wtf(TAG, buildMessage(paramString, paramVarArgs)); }
  
  public static void wtf(Throwable paramThrowable, String paramString, Object... paramVarArgs) { Log.wtf(TAG, buildMessage(paramString, paramVarArgs), paramThrowable); }
  
  static class MarkerLog {
    public static final boolean ENABLED = VolleyLog.DEBUG;
    
    private static final long MIN_DURATION_FOR_LOGGING_MS = 0L;
    
    private boolean mFinished = false;
    
    private final List<Marker> mMarkers = new ArrayList();
    
    private long getTotalDuration() {
      if (this.mMarkers.size() == 0)
        return 0L; 
      long l = ((Marker)this.mMarkers.get(0)).time;
      return ((Marker)this.mMarkers.get(this.mMarkers.size() - 1)).time - l;
    }
    
    public void add(String param1String, long param1Long) { // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield mFinished : Z
      //   6: ifeq -> 19
      //   9: new java/lang/IllegalStateException
      //   12: dup
      //   13: ldc 'Marker added to finished log'
      //   15: invokespecial <init> : (Ljava/lang/String;)V
      //   18: athrow
      //   19: aload_0
      //   20: getfield mMarkers : Ljava/util/List;
      //   23: new com/android/volley/VolleyLog$MarkerLog$Marker
      //   26: dup
      //   27: aload_1
      //   28: lload_2
      //   29: invokestatic elapsedRealtime : ()J
      //   32: invokespecial <init> : (Ljava/lang/String;JJ)V
      //   35: invokeinterface add : (Ljava/lang/Object;)Z
      //   40: pop
      //   41: aload_0
      //   42: monitorexit
      //   43: return
      //   44: astore_1
      //   45: aload_0
      //   46: monitorexit
      //   47: aload_1
      //   48: athrow
      // Exception table:
      //   from	to	target	type
      //   2	19	44	finally
      //   19	41	44	finally }
    
    protected void finalize() {
      if (!this.mFinished) {
        finish("Request on the loose");
        VolleyLog.e("Marker log finalized without finish() - uncaught exit point for request", new Object[0]);
      } 
    }
    
    public void finish(String param1String) { // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: iconst_1
      //   4: putfield mFinished : Z
      //   7: aload_0
      //   8: invokespecial getTotalDuration : ()J
      //   11: lstore #4
      //   13: lload #4
      //   15: lconst_0
      //   16: lcmp
      //   17: ifgt -> 23
      //   20: aload_0
      //   21: monitorexit
      //   22: return
      //   23: aload_0
      //   24: getfield mMarkers : Ljava/util/List;
      //   27: iconst_0
      //   28: invokeinterface get : (I)Ljava/lang/Object;
      //   33: checkcast com/android/volley/VolleyLog$MarkerLog$Marker
      //   36: getfield time : J
      //   39: lstore_2
      //   40: ldc '(%-4d ms) %s'
      //   42: iconst_2
      //   43: anewarray java/lang/Object
      //   46: dup
      //   47: iconst_0
      //   48: lload #4
      //   50: invokestatic valueOf : (J)Ljava/lang/Long;
      //   53: aastore
      //   54: dup
      //   55: iconst_1
      //   56: aload_1
      //   57: aastore
      //   58: invokestatic d : (Ljava/lang/String;[Ljava/lang/Object;)V
      //   61: aload_0
      //   62: getfield mMarkers : Ljava/util/List;
      //   65: invokeinterface iterator : ()Ljava/util/Iterator;
      //   70: astore_1
      //   71: aload_1
      //   72: invokeinterface hasNext : ()Z
      //   77: ifeq -> 142
      //   80: aload_1
      //   81: invokeinterface next : ()Ljava/lang/Object;
      //   86: checkcast com/android/volley/VolleyLog$MarkerLog$Marker
      //   89: astore #6
      //   91: aload #6
      //   93: getfield time : J
      //   96: lstore #4
      //   98: ldc '(+%-4d) [%2d] %s'
      //   100: iconst_3
      //   101: anewarray java/lang/Object
      //   104: dup
      //   105: iconst_0
      //   106: lload #4
      //   108: lload_2
      //   109: lsub
      //   110: invokestatic valueOf : (J)Ljava/lang/Long;
      //   113: aastore
      //   114: dup
      //   115: iconst_1
      //   116: aload #6
      //   118: getfield thread : J
      //   121: invokestatic valueOf : (J)Ljava/lang/Long;
      //   124: aastore
      //   125: dup
      //   126: iconst_2
      //   127: aload #6
      //   129: getfield name : Ljava/lang/String;
      //   132: aastore
      //   133: invokestatic d : (Ljava/lang/String;[Ljava/lang/Object;)V
      //   136: lload #4
      //   138: lstore_2
      //   139: goto -> 71
      //   142: aload_0
      //   143: monitorexit
      //   144: return
      //   145: astore_1
      //   146: aload_0
      //   147: monitorexit
      //   148: aload_1
      //   149: athrow
      // Exception table:
      //   from	to	target	type
      //   2	13	145	finally
      //   23	71	145	finally
      //   71	136	145	finally }
    
    private static class Marker {
      public final String name;
      
      public final long thread;
      
      public final long time;
      
      public Marker(String param2String, long param2Long1, long param2Long2) {
        this.name = param2String;
        this.thread = param2Long1;
        this.time = param2Long2;
      }
    }
  }
  
  private static class Marker {
    public final String name;
    
    public final long thread;
    
    public final long time;
    
    public Marker(String param1String, long param1Long1, long param1Long2) {
      this.name = param1String;
      this.thread = param1Long1;
      this.time = param1Long2;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/VolleyLog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */