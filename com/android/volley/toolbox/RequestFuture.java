package com.android.volley.toolbox;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestFuture<T> extends Object implements Future<T>, Response.Listener<T>, Response.ErrorListener {
  private VolleyError mException;
  
  private Request<?> mRequest;
  
  private T mResult;
  
  private boolean mResultReceived = false;
  
  private T doGet(Long paramLong) throws InterruptedException, ExecutionException, TimeoutException { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mException : Lcom/android/volley/VolleyError;
    //   6: ifnull -> 21
    //   9: new java/util/concurrent/ExecutionException
    //   12: dup
    //   13: aload_0
    //   14: getfield mException : Lcom/android/volley/VolleyError;
    //   17: invokespecial <init> : (Ljava/lang/Throwable;)V
    //   20: athrow
    //   21: aload_0
    //   22: getfield mResultReceived : Z
    //   25: ifeq -> 37
    //   28: aload_0
    //   29: getfield mResult : Ljava/lang/Object;
    //   32: astore_1
    //   33: aload_0
    //   34: monitorexit
    //   35: aload_1
    //   36: areturn
    //   37: aload_1
    //   38: ifnonnull -> 49
    //   41: aload_0
    //   42: lconst_0
    //   43: invokevirtual wait : (J)V
    //   46: goto -> 66
    //   49: aload_1
    //   50: invokevirtual longValue : ()J
    //   53: lconst_0
    //   54: lcmp
    //   55: ifle -> 66
    //   58: aload_0
    //   59: aload_1
    //   60: invokevirtual longValue : ()J
    //   63: invokevirtual wait : (J)V
    //   66: aload_0
    //   67: getfield mException : Lcom/android/volley/VolleyError;
    //   70: ifnull -> 85
    //   73: new java/util/concurrent/ExecutionException
    //   76: dup
    //   77: aload_0
    //   78: getfield mException : Lcom/android/volley/VolleyError;
    //   81: invokespecial <init> : (Ljava/lang/Throwable;)V
    //   84: athrow
    //   85: aload_0
    //   86: getfield mResultReceived : Z
    //   89: ifne -> 100
    //   92: new java/util/concurrent/TimeoutException
    //   95: dup
    //   96: invokespecial <init> : ()V
    //   99: athrow
    //   100: aload_0
    //   101: getfield mResult : Ljava/lang/Object;
    //   104: astore_1
    //   105: aload_0
    //   106: monitorexit
    //   107: aload_1
    //   108: areturn
    //   109: astore_1
    //   110: aload_0
    //   111: monitorexit
    //   112: aload_1
    //   113: athrow
    // Exception table:
    //   from	to	target	type
    //   2	21	109	finally
    //   21	33	109	finally
    //   41	46	109	finally
    //   49	66	109	finally
    //   66	85	109	finally
    //   85	100	109	finally
    //   100	105	109	finally }
  
  public static <E> RequestFuture<E> newFuture() { return new RequestFuture(); }
  
  public boolean cancel(boolean paramBoolean) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mRequest : Lcom/android/volley/Request;
    //   6: astore_2
    //   7: aload_2
    //   8: ifnonnull -> 15
    //   11: aload_0
    //   12: monitorexit
    //   13: iconst_0
    //   14: ireturn
    //   15: aload_0
    //   16: invokevirtual isDone : ()Z
    //   19: ifne -> 33
    //   22: aload_0
    //   23: getfield mRequest : Lcom/android/volley/Request;
    //   26: invokevirtual cancel : ()V
    //   29: aload_0
    //   30: monitorexit
    //   31: iconst_1
    //   32: ireturn
    //   33: aload_0
    //   34: monitorexit
    //   35: iconst_0
    //   36: ireturn
    //   37: astore_2
    //   38: aload_0
    //   39: monitorexit
    //   40: aload_2
    //   41: athrow
    // Exception table:
    //   from	to	target	type
    //   2	7	37	finally
    //   15	29	37	finally }
  
  public T get() throws InterruptedException, ExecutionException {
    try {
      return (T)doGet(null);
    } catch (TimeoutException timeoutException) {
      throw new AssertionError(timeoutException);
    } 
  }
  
  public T get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException { return (T)doGet(Long.valueOf(TimeUnit.MILLISECONDS.convert(paramLong, paramTimeUnit))); }
  
  public boolean isCancelled() { return (this.mRequest == null) ? false : this.mRequest.isCanceled(); }
  
  public boolean isDone() { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mResultReceived : Z
    //   6: ifne -> 33
    //   9: aload_0
    //   10: getfield mException : Lcom/android/volley/VolleyError;
    //   13: ifnonnull -> 33
    //   16: aload_0
    //   17: invokevirtual isCancelled : ()Z
    //   20: istore_1
    //   21: iload_1
    //   22: ifeq -> 28
    //   25: goto -> 33
    //   28: iconst_0
    //   29: istore_1
    //   30: goto -> 35
    //   33: iconst_1
    //   34: istore_1
    //   35: aload_0
    //   36: monitorexit
    //   37: iload_1
    //   38: ireturn
    //   39: astore_2
    //   40: aload_0
    //   41: monitorexit
    //   42: aload_2
    //   43: athrow
    // Exception table:
    //   from	to	target	type
    //   2	21	39	finally }
  
  public void onErrorResponse(VolleyError paramVolleyError) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: putfield mException : Lcom/android/volley/VolleyError;
    //   7: aload_0
    //   8: invokevirtual notifyAll : ()V
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: astore_1
    //   15: aload_0
    //   16: monitorexit
    //   17: aload_1
    //   18: athrow
    // Exception table:
    //   from	to	target	type
    //   2	11	14	finally }
  
  public void onResponse(T paramT) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: iconst_1
    //   4: putfield mResultReceived : Z
    //   7: aload_0
    //   8: aload_1
    //   9: putfield mResult : Ljava/lang/Object;
    //   12: aload_0
    //   13: invokevirtual notifyAll : ()V
    //   16: aload_0
    //   17: monitorexit
    //   18: return
    //   19: astore_1
    //   20: aload_0
    //   21: monitorexit
    //   22: aload_1
    //   23: athrow
    // Exception table:
    //   from	to	target	type
    //   2	16	19	finally }
  
  public void setRequest(Request<?> paramRequest) { this.mRequest = paramRequest; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/RequestFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */