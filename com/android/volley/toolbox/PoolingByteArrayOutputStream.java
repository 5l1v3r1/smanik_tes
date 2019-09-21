package com.android.volley.toolbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PoolingByteArrayOutputStream extends ByteArrayOutputStream {
  private static final int DEFAULT_SIZE = 256;
  
  private final ByteArrayPool mPool;
  
  public PoolingByteArrayOutputStream(ByteArrayPool paramByteArrayPool) { this(paramByteArrayPool, 256); }
  
  public PoolingByteArrayOutputStream(ByteArrayPool paramByteArrayPool, int paramInt) {
    this.mPool = paramByteArrayPool;
    this.buf = this.mPool.getBuf(Math.max(paramInt, 256));
  }
  
  private void expand(int paramInt) {
    if (this.count + paramInt <= this.buf.length)
      return; 
    byte[] arrayOfByte = this.mPool.getBuf((this.count + paramInt) * 2);
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);
    this.mPool.returnBuf(this.buf);
    this.buf = arrayOfByte;
  }
  
  public void close() throws IOException {
    this.mPool.returnBuf(this.buf);
    this.buf = null;
    super.close();
  }
  
  public void finalize() throws IOException { this.mPool.returnBuf(this.buf); }
  
  public void write(int paramInt) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: iconst_1
    //   4: invokespecial expand : (I)V
    //   7: aload_0
    //   8: iload_1
    //   9: invokespecial write : (I)V
    //   12: aload_0
    //   13: monitorexit
    //   14: return
    //   15: astore_2
    //   16: aload_0
    //   17: monitorexit
    //   18: aload_2
    //   19: athrow
    // Exception table:
    //   from	to	target	type
    //   2	12	15	finally }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: iload_3
    //   4: invokespecial expand : (I)V
    //   7: aload_0
    //   8: aload_1
    //   9: iload_2
    //   10: iload_3
    //   11: invokespecial write : ([BII)V
    //   14: aload_0
    //   15: monitorexit
    //   16: return
    //   17: astore_1
    //   18: aload_0
    //   19: monitorexit
    //   20: aload_1
    //   21: athrow
    // Exception table:
    //   from	to	target	type
    //   2	14	17	finally }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/PoolingByteArrayOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */