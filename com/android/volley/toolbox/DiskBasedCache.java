package com.android.volley.toolbox;

import android.os.SystemClock;
import com.android.volley.Cache;
import com.android.volley.VolleyLog;
import java.io.EOFException;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiskBasedCache implements Cache {
  private static final int CACHE_MAGIC = 538247942;
  
  private static final int DEFAULT_DISK_USAGE_BYTES = 5242880;
  
  private static final float HYSTERESIS_FACTOR = 0.9F;
  
  private final Map<String, CacheHeader> mEntries = new LinkedHashMap(16, 0.75F, true);
  
  private final int mMaxCacheSizeInBytes;
  
  private final File mRootDirectory;
  
  private long mTotalSize = 0L;
  
  public DiskBasedCache(File paramFile) { this(paramFile, 5242880); }
  
  public DiskBasedCache(File paramFile, int paramInt) {
    this.mRootDirectory = paramFile;
    this.mMaxCacheSizeInBytes = paramInt;
  }
  
  private String getFilenameForKey(String paramString) {
    int i = paramString.length() / 2;
    String str;
    paramString = String.valueOf((str = String.valueOf(String.valueOf(paramString.substring(0, i).hashCode()))).valueOf(paramString.substring(i).hashCode()));
    return (paramString.length() != 0) ? str.concat(paramString) : new String(str);
  }
  
  private void pruneIfNeeded(int paramInt) {
    long l2 = this.mTotalSize;
    long l1 = paramInt;
    if (l2 + l1 < this.mMaxCacheSizeInBytes)
      return; 
    if (VolleyLog.DEBUG)
      VolleyLog.v("Pruning old cache entries.", new Object[0]); 
    long l3 = this.mTotalSize;
    l2 = SystemClock.elapsedRealtime();
    Iterator iterator = this.mEntries.entrySet().iterator();
    paramInt = 0;
    while (iterator.hasNext()) {
      CacheHeader cacheHeader = (CacheHeader)((Map.Entry)iterator.next()).getValue();
      if (getFileForKey(cacheHeader.key).delete()) {
        this.mTotalSize -= cacheHeader.size;
      } else {
        VolleyLog.d("Could not delete cache entry for key=%s, filename=%s", new Object[] { cacheHeader.key, getFilenameForKey(cacheHeader.key) });
      } 
      iterator.remove();
      paramInt++;
      if ((float)(this.mTotalSize + l1) < this.mMaxCacheSizeInBytes * 0.9F)
        break; 
    } 
    if (VolleyLog.DEBUG)
      VolleyLog.v("pruned %d files, %d bytes, %d ms", new Object[] { Integer.valueOf(paramInt), Long.valueOf(this.mTotalSize - l3), Long.valueOf(SystemClock.elapsedRealtime() - l2) }); 
  }
  
  private void putEntry(String paramString, CacheHeader paramCacheHeader) {
    if (!this.mEntries.containsKey(paramString)) {
      this.mTotalSize += paramCacheHeader.size;
    } else {
      CacheHeader cacheHeader = (CacheHeader)this.mEntries.get(paramString);
      this.mTotalSize += paramCacheHeader.size - cacheHeader.size;
    } 
    this.mEntries.put(paramString, paramCacheHeader);
  }
  
  private static int read(InputStream paramInputStream) throws IOException {
    int i = paramInputStream.read();
    if (i == -1)
      throw new EOFException(); 
    return i;
  }
  
  static int readInt(InputStream paramInputStream) throws IOException {
    int i = read(paramInputStream);
    int j = read(paramInputStream);
    int k = read(paramInputStream);
    return read(paramInputStream) << 24 | i << 0 | false | j << 8 | k << 16;
  }
  
  static long readLong(InputStream paramInputStream) throws IOException { return (read(paramInputStream) & 0xFFL) << false | 0x0L | (read(paramInputStream) & 0xFFL) << 8 | (read(paramInputStream) & 0xFFL) << 16 | (read(paramInputStream) & 0xFFL) << 24 | (read(paramInputStream) & 0xFFL) << 32 | (read(paramInputStream) & 0xFFL) << 40 | (read(paramInputStream) & 0xFFL) << 48 | (read(paramInputStream) & 0xFFL) << 56; }
  
  static String readString(InputStream paramInputStream) throws IOException { return new String(streamToBytes(paramInputStream, (int)readLong(paramInputStream)), "UTF-8"); }
  
  static Map<String, String> readStringStringMap(InputStream paramInputStream) throws IOException {
    HashMap hashMap;
    int i = readInt(paramInputStream);
    if (i == 0) {
      hashMap = Collections.emptyMap();
    } else {
      hashMap = new HashMap(i);
    } 
    for (byte b = 0; b < i; b++)
      hashMap.put(readString(paramInputStream).intern(), readString(paramInputStream).intern()); 
    return hashMap;
  }
  
  private void removeEntry(String paramString) {
    CacheHeader cacheHeader = (CacheHeader)this.mEntries.get(paramString);
    if (cacheHeader != null) {
      this.mTotalSize -= cacheHeader.size;
      this.mEntries.remove(paramString);
    } 
  }
  
  private static byte[] streamToBytes(InputStream paramInputStream, int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[paramInt];
    int i = 0;
    while (i < paramInt) {
      int j = paramInputStream.read(arrayOfByte, i, paramInt - i);
      if (j != -1)
        i += j; 
    } 
    if (i != paramInt) {
      StringBuilder stringBuilder = new StringBuilder(50);
      stringBuilder.append("Expected ");
      stringBuilder.append(paramInt);
      stringBuilder.append(" bytes, read ");
      stringBuilder.append(i);
      stringBuilder.append(" bytes");
      throw new IOException(stringBuilder.toString());
    } 
    return arrayOfByte;
  }
  
  static void writeInt(OutputStream paramOutputStream, int paramInt) throws IOException {
    paramOutputStream.write(paramInt >> 0 & 0xFF);
    paramOutputStream.write(paramInt >> 8 & 0xFF);
    paramOutputStream.write(paramInt >> 16 & 0xFF);
    paramOutputStream.write(paramInt >> 24 & 0xFF);
  }
  
  static void writeLong(OutputStream paramOutputStream, long paramLong) throws IOException {
    paramOutputStream.write((byte)(int)(paramLong >>> false));
    paramOutputStream.write((byte)(int)(paramLong >>> 8));
    paramOutputStream.write((byte)(int)(paramLong >>> 16));
    paramOutputStream.write((byte)(int)(paramLong >>> 24));
    paramOutputStream.write((byte)(int)(paramLong >>> 32));
    paramOutputStream.write((byte)(int)(paramLong >>> 40));
    paramOutputStream.write((byte)(int)(paramLong >>> 48));
    paramOutputStream.write((byte)(int)(paramLong >>> 56));
  }
  
  static void writeString(OutputStream paramOutputStream, String paramString) throws IOException {
    byte[] arrayOfByte = paramString.getBytes("UTF-8");
    writeLong(paramOutputStream, arrayOfByte.length);
    paramOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
  }
  
  static void writeStringStringMap(Map<String, String> paramMap, OutputStream paramOutputStream) throws IOException {
    if (paramMap != null) {
      writeInt(paramOutputStream, paramMap.size());
      for (Map.Entry entry : paramMap.entrySet()) {
        writeString(paramOutputStream, (String)entry.getKey());
        writeString(paramOutputStream, (String)entry.getValue());
      } 
    } else {
      writeInt(paramOutputStream, 0);
    } 
  }
  
  public void clear() { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mRootDirectory : Ljava/io/File;
    //   6: invokevirtual listFiles : ()[Ljava/io/File;
    //   9: astore_3
    //   10: aload_3
    //   11: ifnull -> 38
    //   14: aload_3
    //   15: arraylength
    //   16: istore_2
    //   17: iconst_0
    //   18: istore_1
    //   19: iload_1
    //   20: iload_2
    //   21: if_icmpge -> 38
    //   24: aload_3
    //   25: iload_1
    //   26: aaload
    //   27: invokevirtual delete : ()Z
    //   30: pop
    //   31: iload_1
    //   32: iconst_1
    //   33: iadd
    //   34: istore_1
    //   35: goto -> 19
    //   38: aload_0
    //   39: getfield mEntries : Ljava/util/Map;
    //   42: invokeinterface clear : ()V
    //   47: aload_0
    //   48: lconst_0
    //   49: putfield mTotalSize : J
    //   52: ldc_w 'Cache cleared.'
    //   55: iconst_0
    //   56: anewarray java/lang/Object
    //   59: invokestatic d : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   62: aload_0
    //   63: monitorexit
    //   64: return
    //   65: astore_3
    //   66: aload_0
    //   67: monitorexit
    //   68: aload_3
    //   69: athrow
    // Exception table:
    //   from	to	target	type
    //   2	10	65	finally
    //   14	17	65	finally
    //   24	31	65	finally
    //   38	62	65	finally }
  
  public Cache.Entry get(String paramString) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mEntries : Ljava/util/Map;
    //   6: aload_1
    //   7: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   12: checkcast com/android/volley/toolbox/DiskBasedCache$CacheHeader
    //   15: astore #4
    //   17: aload #4
    //   19: ifnonnull -> 26
    //   22: aload_0
    //   23: monitorexit
    //   24: aconst_null
    //   25: areturn
    //   26: aload_0
    //   27: aload_1
    //   28: invokevirtual getFileForKey : (Ljava/lang/String;)Ljava/io/File;
    //   31: astore #5
    //   33: new com/android/volley/toolbox/DiskBasedCache$CountingInputStream
    //   36: dup
    //   37: new java/io/BufferedInputStream
    //   40: dup
    //   41: new java/io/FileInputStream
    //   44: dup
    //   45: aload #5
    //   47: invokespecial <init> : (Ljava/io/File;)V
    //   50: invokespecial <init> : (Ljava/io/InputStream;)V
    //   53: aconst_null
    //   54: invokespecial <init> : (Ljava/io/InputStream;Lcom/android/volley/toolbox/DiskBasedCache$1;)V
    //   57: astore_3
    //   58: aload_3
    //   59: astore_2
    //   60: aload_3
    //   61: invokestatic readHeader : (Ljava/io/InputStream;)Lcom/android/volley/toolbox/DiskBasedCache$CacheHeader;
    //   64: pop
    //   65: aload_3
    //   66: astore_2
    //   67: aload #4
    //   69: aload_3
    //   70: aload #5
    //   72: invokevirtual length : ()J
    //   75: aload_3
    //   76: invokestatic access$100 : (Lcom/android/volley/toolbox/DiskBasedCache$CountingInputStream;)I
    //   79: i2l
    //   80: lsub
    //   81: l2i
    //   82: invokestatic streamToBytes : (Ljava/io/InputStream;I)[B
    //   85: invokevirtual toCacheEntry : ([B)Lcom/android/volley/Cache$Entry;
    //   88: astore #4
    //   90: aload_3
    //   91: ifnull -> 105
    //   94: aload_3
    //   95: invokevirtual close : ()V
    //   98: goto -> 105
    //   101: aload_0
    //   102: monitorexit
    //   103: aconst_null
    //   104: areturn
    //   105: aload_0
    //   106: monitorexit
    //   107: aload #4
    //   109: areturn
    //   110: astore #4
    //   112: goto -> 125
    //   115: astore_1
    //   116: aconst_null
    //   117: astore_2
    //   118: goto -> 180
    //   121: astore #4
    //   123: aconst_null
    //   124: astore_3
    //   125: aload_3
    //   126: astore_2
    //   127: ldc_w '%s: %s'
    //   130: iconst_2
    //   131: anewarray java/lang/Object
    //   134: dup
    //   135: iconst_0
    //   136: aload #5
    //   138: invokevirtual getAbsolutePath : ()Ljava/lang/String;
    //   141: aastore
    //   142: dup
    //   143: iconst_1
    //   144: aload #4
    //   146: invokevirtual toString : ()Ljava/lang/String;
    //   149: aastore
    //   150: invokestatic d : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   153: aload_3
    //   154: astore_2
    //   155: aload_0
    //   156: aload_1
    //   157: invokevirtual remove : (Ljava/lang/String;)V
    //   160: aload_3
    //   161: ifnull -> 175
    //   164: aload_3
    //   165: invokevirtual close : ()V
    //   168: goto -> 175
    //   171: aload_0
    //   172: monitorexit
    //   173: aconst_null
    //   174: areturn
    //   175: aload_0
    //   176: monitorexit
    //   177: aconst_null
    //   178: areturn
    //   179: astore_1
    //   180: aload_2
    //   181: ifnull -> 195
    //   184: aload_2
    //   185: invokevirtual close : ()V
    //   188: goto -> 195
    //   191: aload_0
    //   192: monitorexit
    //   193: aconst_null
    //   194: areturn
    //   195: aload_1
    //   196: athrow
    //   197: astore_1
    //   198: aload_0
    //   199: monitorexit
    //   200: aload_1
    //   201: athrow
    //   202: astore_1
    //   203: goto -> 101
    //   206: astore_1
    //   207: goto -> 171
    //   210: astore_1
    //   211: goto -> 191
    // Exception table:
    //   from	to	target	type
    //   2	17	197	finally
    //   26	33	197	finally
    //   33	58	121	java/io/IOException
    //   33	58	115	finally
    //   60	65	110	java/io/IOException
    //   60	65	179	finally
    //   67	90	110	java/io/IOException
    //   67	90	179	finally
    //   94	98	202	java/io/IOException
    //   94	98	197	finally
    //   127	153	179	finally
    //   155	160	179	finally
    //   164	168	206	java/io/IOException
    //   164	168	197	finally
    //   184	188	210	java/io/IOException
    //   184	188	197	finally
    //   195	197	197	finally }
  
  public File getFileForKey(String paramString) { return new File(this.mRootDirectory, getFilenameForKey(paramString)); }
  
  public void initialize() { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mRootDirectory : Ljava/io/File;
    //   6: invokevirtual exists : ()Z
    //   9: istore_3
    //   10: iconst_0
    //   11: istore_1
    //   12: iload_3
    //   13: ifne -> 49
    //   16: aload_0
    //   17: getfield mRootDirectory : Ljava/io/File;
    //   20: invokevirtual mkdirs : ()Z
    //   23: ifne -> 46
    //   26: ldc_w 'Unable to create cache dir %s'
    //   29: iconst_1
    //   30: anewarray java/lang/Object
    //   33: dup
    //   34: iconst_0
    //   35: aload_0
    //   36: getfield mRootDirectory : Ljava/io/File;
    //   39: invokevirtual getAbsolutePath : ()Ljava/lang/String;
    //   42: aastore
    //   43: invokestatic e : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   46: aload_0
    //   47: monitorexit
    //   48: return
    //   49: aload_0
    //   50: getfield mRootDirectory : Ljava/io/File;
    //   53: invokevirtual listFiles : ()[Ljava/io/File;
    //   56: astore #7
    //   58: aload #7
    //   60: ifnonnull -> 66
    //   63: aload_0
    //   64: monitorexit
    //   65: return
    //   66: aload #7
    //   68: arraylength
    //   69: istore_2
    //   70: iload_1
    //   71: iload_2
    //   72: if_icmpge -> 215
    //   75: aload #7
    //   77: iload_1
    //   78: aaload
    //   79: astore #8
    //   81: aconst_null
    //   82: astore #6
    //   84: aconst_null
    //   85: astore #4
    //   87: new java/io/BufferedInputStream
    //   90: dup
    //   91: new java/io/FileInputStream
    //   94: dup
    //   95: aload #8
    //   97: invokespecial <init> : (Ljava/io/File;)V
    //   100: invokespecial <init> : (Ljava/io/InputStream;)V
    //   103: astore #5
    //   105: aload #5
    //   107: invokestatic readHeader : (Ljava/io/InputStream;)Lcom/android/volley/toolbox/DiskBasedCache$CacheHeader;
    //   110: astore #4
    //   112: aload #4
    //   114: aload #8
    //   116: invokevirtual length : ()J
    //   119: putfield size : J
    //   122: aload_0
    //   123: aload #4
    //   125: getfield key : Ljava/lang/String;
    //   128: aload #4
    //   130: invokespecial putEntry : (Ljava/lang/String;Lcom/android/volley/toolbox/DiskBasedCache$CacheHeader;)V
    //   133: aload #5
    //   135: ifnull -> 208
    //   138: aload #5
    //   140: invokevirtual close : ()V
    //   143: goto -> 208
    //   146: astore #6
    //   148: aload #5
    //   150: astore #4
    //   152: aload #6
    //   154: astore #5
    //   156: goto -> 185
    //   159: goto -> 167
    //   162: astore #5
    //   164: goto -> 185
    //   167: aload #8
    //   169: ifnull -> 198
    //   172: aload #5
    //   174: astore #4
    //   176: aload #8
    //   178: invokevirtual delete : ()Z
    //   181: pop
    //   182: goto -> 198
    //   185: aload #4
    //   187: ifnull -> 195
    //   190: aload #4
    //   192: invokevirtual close : ()V
    //   195: aload #5
    //   197: athrow
    //   198: aload #5
    //   200: ifnull -> 208
    //   203: aload #5
    //   205: invokevirtual close : ()V
    //   208: iload_1
    //   209: iconst_1
    //   210: iadd
    //   211: istore_1
    //   212: goto -> 70
    //   215: aload_0
    //   216: monitorexit
    //   217: return
    //   218: astore #4
    //   220: aload_0
    //   221: monitorexit
    //   222: aload #4
    //   224: athrow
    //   225: astore #4
    //   227: aload #6
    //   229: astore #5
    //   231: goto -> 167
    //   234: astore #4
    //   236: goto -> 159
    //   239: astore #4
    //   241: goto -> 208
    //   244: astore #4
    //   246: goto -> 195
    // Exception table:
    //   from	to	target	type
    //   2	10	218	finally
    //   16	46	218	finally
    //   49	58	218	finally
    //   66	70	218	finally
    //   87	105	225	java/io/IOException
    //   87	105	162	finally
    //   105	133	234	java/io/IOException
    //   105	133	146	finally
    //   138	143	239	java/io/IOException
    //   138	143	218	finally
    //   176	182	162	finally
    //   190	195	244	java/io/IOException
    //   190	195	218	finally
    //   195	198	218	finally
    //   203	208	239	java/io/IOException
    //   203	208	218	finally }
  
  public void invalidate(String paramString, boolean paramBoolean) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokevirtual get : (Ljava/lang/String;)Lcom/android/volley/Cache$Entry;
    //   7: astore_3
    //   8: aload_3
    //   9: ifnull -> 32
    //   12: aload_3
    //   13: lconst_0
    //   14: putfield softTtl : J
    //   17: iload_2
    //   18: ifeq -> 26
    //   21: aload_3
    //   22: lconst_0
    //   23: putfield ttl : J
    //   26: aload_0
    //   27: aload_1
    //   28: aload_3
    //   29: invokevirtual put : (Ljava/lang/String;Lcom/android/volley/Cache$Entry;)V
    //   32: aload_0
    //   33: monitorexit
    //   34: return
    //   35: astore_1
    //   36: aload_0
    //   37: monitorexit
    //   38: aload_1
    //   39: athrow
    // Exception table:
    //   from	to	target	type
    //   2	8	35	finally
    //   12	17	35	finally
    //   21	26	35	finally
    //   26	32	35	finally }
  
  public void put(String paramString, Cache.Entry paramEntry) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_2
    //   4: getfield data : [B
    //   7: arraylength
    //   8: invokespecial pruneIfNeeded : (I)V
    //   11: aload_0
    //   12: aload_1
    //   13: invokevirtual getFileForKey : (Ljava/lang/String;)Ljava/io/File;
    //   16: astore_3
    //   17: new java/io/BufferedOutputStream
    //   20: dup
    //   21: new java/io/FileOutputStream
    //   24: dup
    //   25: aload_3
    //   26: invokespecial <init> : (Ljava/io/File;)V
    //   29: invokespecial <init> : (Ljava/io/OutputStream;)V
    //   32: astore #4
    //   34: new com/android/volley/toolbox/DiskBasedCache$CacheHeader
    //   37: dup
    //   38: aload_1
    //   39: aload_2
    //   40: invokespecial <init> : (Ljava/lang/String;Lcom/android/volley/Cache$Entry;)V
    //   43: astore #5
    //   45: aload #5
    //   47: aload #4
    //   49: invokevirtual writeHeader : (Ljava/io/OutputStream;)Z
    //   52: ifne -> 85
    //   55: aload #4
    //   57: invokevirtual close : ()V
    //   60: ldc_w 'Failed to write header for %s'
    //   63: iconst_1
    //   64: anewarray java/lang/Object
    //   67: dup
    //   68: iconst_0
    //   69: aload_3
    //   70: invokevirtual getAbsolutePath : ()Ljava/lang/String;
    //   73: aastore
    //   74: invokestatic d : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   77: new java/io/IOException
    //   80: dup
    //   81: invokespecial <init> : ()V
    //   84: athrow
    //   85: aload #4
    //   87: aload_2
    //   88: getfield data : [B
    //   91: invokevirtual write : ([B)V
    //   94: aload #4
    //   96: invokevirtual close : ()V
    //   99: aload_0
    //   100: aload_1
    //   101: aload #5
    //   103: invokespecial putEntry : (Ljava/lang/String;Lcom/android/volley/toolbox/DiskBasedCache$CacheHeader;)V
    //   106: aload_0
    //   107: monitorexit
    //   108: return
    //   109: aload_3
    //   110: invokevirtual delete : ()Z
    //   113: ifne -> 133
    //   116: ldc_w 'Could not clean up file %s'
    //   119: iconst_1
    //   120: anewarray java/lang/Object
    //   123: dup
    //   124: iconst_0
    //   125: aload_3
    //   126: invokevirtual getAbsolutePath : ()Ljava/lang/String;
    //   129: aastore
    //   130: invokestatic d : (Ljava/lang/String;[Ljava/lang/Object;)V
    //   133: aload_0
    //   134: monitorexit
    //   135: return
    //   136: astore_1
    //   137: aload_0
    //   138: monitorexit
    //   139: aload_1
    //   140: athrow
    //   141: astore_1
    //   142: goto -> 109
    // Exception table:
    //   from	to	target	type
    //   2	17	136	finally
    //   17	85	141	java/io/IOException
    //   17	85	136	finally
    //   85	106	141	java/io/IOException
    //   85	106	136	finally
    //   109	133	136	finally }
  
  public void remove(String paramString) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokevirtual getFileForKey : (Ljava/lang/String;)Ljava/io/File;
    //   7: invokevirtual delete : ()Z
    //   10: istore_2
    //   11: aload_0
    //   12: aload_1
    //   13: invokespecial removeEntry : (Ljava/lang/String;)V
    //   16: iload_2
    //   17: ifne -> 41
    //   20: ldc 'Could not delete cache entry for key=%s, filename=%s'
    //   22: iconst_2
    //   23: anewarray java/lang/Object
    //   26: dup
    //   27: iconst_0
    //   28: aload_1
    //   29: aastore
    //   30: dup
    //   31: iconst_1
    //   32: aload_0
    //   33: aload_1
    //   34: invokespecial getFilenameForKey : (Ljava/lang/String;)Ljava/lang/String;
    //   37: aastore
    //   38: invokestatic d : (Ljava/lang/String;[Ljava/lang/Object;)V
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
    //   2	16	44	finally
    //   20	41	44	finally }
  
  static class CacheHeader {
    public String etag;
    
    public String key;
    
    public long lastModified;
    
    public Map<String, String> responseHeaders;
    
    public long serverDate;
    
    public long size;
    
    public long softTtl;
    
    public long ttl;
    
    private CacheHeader() {}
    
    public CacheHeader(String param1String, Cache.Entry param1Entry) {
      this.key = param1String;
      this.size = param1Entry.data.length;
      this.etag = param1Entry.etag;
      this.serverDate = param1Entry.serverDate;
      this.lastModified = param1Entry.lastModified;
      this.ttl = param1Entry.ttl;
      this.softTtl = param1Entry.softTtl;
      this.responseHeaders = param1Entry.responseHeaders;
    }
    
    public static CacheHeader readHeader(InputStream param1InputStream) throws IOException {
      CacheHeader cacheHeader = new CacheHeader();
      if (DiskBasedCache.readInt(param1InputStream) != 538247942)
        throw new IOException(); 
      cacheHeader.key = DiskBasedCache.readString(param1InputStream);
      cacheHeader.etag = DiskBasedCache.readString(param1InputStream);
      if (cacheHeader.etag.equals(""))
        cacheHeader.etag = null; 
      cacheHeader.serverDate = DiskBasedCache.readLong(param1InputStream);
      cacheHeader.lastModified = DiskBasedCache.readLong(param1InputStream);
      cacheHeader.ttl = DiskBasedCache.readLong(param1InputStream);
      cacheHeader.softTtl = DiskBasedCache.readLong(param1InputStream);
      cacheHeader.responseHeaders = DiskBasedCache.readStringStringMap(param1InputStream);
      return cacheHeader;
    }
    
    public Cache.Entry toCacheEntry(byte[] param1ArrayOfByte) {
      Cache.Entry entry = new Cache.Entry();
      entry.data = param1ArrayOfByte;
      entry.etag = this.etag;
      entry.serverDate = this.serverDate;
      entry.lastModified = this.lastModified;
      entry.ttl = this.ttl;
      entry.softTtl = this.softTtl;
      entry.responseHeaders = this.responseHeaders;
      return entry;
    }
    
    public boolean writeHeader(OutputStream param1OutputStream) {
      try {
        String str;
        DiskBasedCache.writeInt(param1OutputStream, 538247942);
        DiskBasedCache.writeString(param1OutputStream, this.key);
        if (this.etag == null) {
          str = "";
        } else {
          str = this.etag;
        } 
        DiskBasedCache.writeString(param1OutputStream, str);
        DiskBasedCache.writeLong(param1OutputStream, this.serverDate);
        DiskBasedCache.writeLong(param1OutputStream, this.lastModified);
        DiskBasedCache.writeLong(param1OutputStream, this.ttl);
        DiskBasedCache.writeLong(param1OutputStream, this.softTtl);
        DiskBasedCache.writeStringStringMap(this.responseHeaders, param1OutputStream);
        param1OutputStream.flush();
        return true;
      } catch (IOException param1OutputStream) {
        VolleyLog.d("%s", new Object[] { param1OutputStream.toString() });
        return false;
      } 
    }
  }
  
  private static class CountingInputStream extends FilterInputStream {
    private int bytesRead = 0;
    
    private CountingInputStream(InputStream param1InputStream) { super(param1InputStream); }
    
    public int read() throws IOException {
      int i = super.read();
      if (i != -1)
        this.bytesRead++; 
      return i;
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      param1Int1 = super.read(param1ArrayOfByte, param1Int1, param1Int2);
      if (param1Int1 != -1)
        this.bytesRead += param1Int1; 
      return param1Int1;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/DiskBasedCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */