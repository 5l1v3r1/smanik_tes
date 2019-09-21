package android.support.v4.util;

import java.util.ConcurrentModificationException;
import java.util.Map;

public class SimpleArrayMap<K, V> extends Object {
  private static final int BASE_SIZE = 4;
  
  private static final int CACHE_SIZE = 10;
  
  private static final boolean CONCURRENT_MODIFICATION_EXCEPTIONS = true;
  
  private static final boolean DEBUG = false;
  
  private static final String TAG = "ArrayMap";
  
  static Object[] mBaseCache;
  
  static int mBaseCacheSize;
  
  static Object[] mTwiceBaseCache;
  
  static int mTwiceBaseCacheSize;
  
  Object[] mArray;
  
  int[] mHashes;
  
  int mSize;
  
  public SimpleArrayMap() {
    this.mHashes = ContainerHelpers.EMPTY_INTS;
    this.mArray = ContainerHelpers.EMPTY_OBJECTS;
    this.mSize = 0;
  }
  
  public SimpleArrayMap(int paramInt) {
    if (paramInt == 0) {
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
    } else {
      allocArrays(paramInt);
    } 
    this.mSize = 0;
  }
  
  public SimpleArrayMap(SimpleArrayMap<K, V> paramSimpleArrayMap) {
    this();
    if (paramSimpleArrayMap != null)
      putAll(paramSimpleArrayMap); 
  }
  
  private void allocArrays(int paramInt) { // Byte code:
    //   0: iload_1
    //   1: bipush #8
    //   3: if_icmpne -> 75
    //   6: ldc android/support/v4/util/ArrayMap
    //   8: monitorenter
    //   9: getstatic android/support/v4/util/SimpleArrayMap.mTwiceBaseCache : [Ljava/lang/Object;
    //   12: ifnull -> 63
    //   15: getstatic android/support/v4/util/SimpleArrayMap.mTwiceBaseCache : [Ljava/lang/Object;
    //   18: astore_2
    //   19: aload_0
    //   20: aload_2
    //   21: putfield mArray : [Ljava/lang/Object;
    //   24: aload_2
    //   25: iconst_0
    //   26: aaload
    //   27: checkcast [Ljava/lang/Object;
    //   30: putstatic android/support/v4/util/SimpleArrayMap.mTwiceBaseCache : [Ljava/lang/Object;
    //   33: aload_0
    //   34: aload_2
    //   35: iconst_1
    //   36: aaload
    //   37: checkcast [I
    //   40: putfield mHashes : [I
    //   43: aload_2
    //   44: iconst_1
    //   45: aconst_null
    //   46: aastore
    //   47: aload_2
    //   48: iconst_0
    //   49: aconst_null
    //   50: aastore
    //   51: getstatic android/support/v4/util/SimpleArrayMap.mTwiceBaseCacheSize : I
    //   54: iconst_1
    //   55: isub
    //   56: putstatic android/support/v4/util/SimpleArrayMap.mTwiceBaseCacheSize : I
    //   59: ldc android/support/v4/util/ArrayMap
    //   61: monitorexit
    //   62: return
    //   63: ldc android/support/v4/util/ArrayMap
    //   65: monitorexit
    //   66: goto -> 149
    //   69: astore_2
    //   70: ldc android/support/v4/util/ArrayMap
    //   72: monitorexit
    //   73: aload_2
    //   74: athrow
    //   75: iload_1
    //   76: iconst_4
    //   77: if_icmpne -> 149
    //   80: ldc android/support/v4/util/ArrayMap
    //   82: monitorenter
    //   83: getstatic android/support/v4/util/SimpleArrayMap.mBaseCache : [Ljava/lang/Object;
    //   86: ifnull -> 137
    //   89: getstatic android/support/v4/util/SimpleArrayMap.mBaseCache : [Ljava/lang/Object;
    //   92: astore_2
    //   93: aload_0
    //   94: aload_2
    //   95: putfield mArray : [Ljava/lang/Object;
    //   98: aload_2
    //   99: iconst_0
    //   100: aaload
    //   101: checkcast [Ljava/lang/Object;
    //   104: putstatic android/support/v4/util/SimpleArrayMap.mBaseCache : [Ljava/lang/Object;
    //   107: aload_0
    //   108: aload_2
    //   109: iconst_1
    //   110: aaload
    //   111: checkcast [I
    //   114: putfield mHashes : [I
    //   117: aload_2
    //   118: iconst_1
    //   119: aconst_null
    //   120: aastore
    //   121: aload_2
    //   122: iconst_0
    //   123: aconst_null
    //   124: aastore
    //   125: getstatic android/support/v4/util/SimpleArrayMap.mBaseCacheSize : I
    //   128: iconst_1
    //   129: isub
    //   130: putstatic android/support/v4/util/SimpleArrayMap.mBaseCacheSize : I
    //   133: ldc android/support/v4/util/ArrayMap
    //   135: monitorexit
    //   136: return
    //   137: ldc android/support/v4/util/ArrayMap
    //   139: monitorexit
    //   140: goto -> 149
    //   143: astore_2
    //   144: ldc android/support/v4/util/ArrayMap
    //   146: monitorexit
    //   147: aload_2
    //   148: athrow
    //   149: aload_0
    //   150: iload_1
    //   151: newarray int
    //   153: putfield mHashes : [I
    //   156: aload_0
    //   157: iload_1
    //   158: iconst_1
    //   159: ishl
    //   160: anewarray java/lang/Object
    //   163: putfield mArray : [Ljava/lang/Object;
    //   166: return
    // Exception table:
    //   from	to	target	type
    //   9	43	69	finally
    //   51	62	69	finally
    //   63	66	69	finally
    //   70	73	69	finally
    //   83	117	143	finally
    //   125	136	143	finally
    //   137	140	143	finally
    //   144	147	143	finally }
  
  private static int binarySearchHashes(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    try {
      return ContainerHelpers.binarySearch(paramArrayOfInt, paramInt1, paramInt2);
    } catch (ArrayIndexOutOfBoundsException paramArrayOfInt) {
      throw new ConcurrentModificationException();
    } 
  }
  
  private static void freeArrays(int[] paramArrayOfInt, Object[] paramArrayOfObject, int paramInt) { // Byte code:
    //   0: aload_0
    //   1: arraylength
    //   2: bipush #8
    //   4: if_icmpne -> 59
    //   7: ldc android/support/v4/util/ArrayMap
    //   9: monitorenter
    //   10: getstatic android/support/v4/util/SimpleArrayMap.mTwiceBaseCacheSize : I
    //   13: bipush #10
    //   15: if_icmpge -> 49
    //   18: aload_1
    //   19: iconst_0
    //   20: getstatic android/support/v4/util/SimpleArrayMap.mTwiceBaseCache : [Ljava/lang/Object;
    //   23: aastore
    //   24: aload_1
    //   25: iconst_1
    //   26: aload_0
    //   27: aastore
    //   28: iload_2
    //   29: iconst_1
    //   30: ishl
    //   31: iconst_1
    //   32: isub
    //   33: istore_2
    //   34: goto -> 118
    //   37: aload_1
    //   38: putstatic android/support/v4/util/SimpleArrayMap.mTwiceBaseCache : [Ljava/lang/Object;
    //   41: getstatic android/support/v4/util/SimpleArrayMap.mTwiceBaseCacheSize : I
    //   44: iconst_1
    //   45: iadd
    //   46: putstatic android/support/v4/util/SimpleArrayMap.mTwiceBaseCacheSize : I
    //   49: ldc android/support/v4/util/ArrayMap
    //   51: monitorexit
    //   52: return
    //   53: astore_0
    //   54: ldc android/support/v4/util/ArrayMap
    //   56: monitorexit
    //   57: aload_0
    //   58: athrow
    //   59: aload_0
    //   60: arraylength
    //   61: iconst_4
    //   62: if_icmpne -> 117
    //   65: ldc android/support/v4/util/ArrayMap
    //   67: monitorenter
    //   68: getstatic android/support/v4/util/SimpleArrayMap.mBaseCacheSize : I
    //   71: bipush #10
    //   73: if_icmpge -> 107
    //   76: aload_1
    //   77: iconst_0
    //   78: getstatic android/support/v4/util/SimpleArrayMap.mBaseCache : [Ljava/lang/Object;
    //   81: aastore
    //   82: aload_1
    //   83: iconst_1
    //   84: aload_0
    //   85: aastore
    //   86: iload_2
    //   87: iconst_1
    //   88: ishl
    //   89: iconst_1
    //   90: isub
    //   91: istore_2
    //   92: goto -> 134
    //   95: aload_1
    //   96: putstatic android/support/v4/util/SimpleArrayMap.mBaseCache : [Ljava/lang/Object;
    //   99: getstatic android/support/v4/util/SimpleArrayMap.mBaseCacheSize : I
    //   102: iconst_1
    //   103: iadd
    //   104: putstatic android/support/v4/util/SimpleArrayMap.mBaseCacheSize : I
    //   107: ldc android/support/v4/util/ArrayMap
    //   109: monitorexit
    //   110: return
    //   111: astore_0
    //   112: ldc android/support/v4/util/ArrayMap
    //   114: monitorexit
    //   115: aload_0
    //   116: athrow
    //   117: return
    //   118: iload_2
    //   119: iconst_2
    //   120: if_icmplt -> 37
    //   123: aload_1
    //   124: iload_2
    //   125: aconst_null
    //   126: aastore
    //   127: iload_2
    //   128: iconst_1
    //   129: isub
    //   130: istore_2
    //   131: goto -> 118
    //   134: iload_2
    //   135: iconst_2
    //   136: if_icmplt -> 95
    //   139: aload_1
    //   140: iload_2
    //   141: aconst_null
    //   142: aastore
    //   143: iload_2
    //   144: iconst_1
    //   145: isub
    //   146: istore_2
    //   147: goto -> 134
    // Exception table:
    //   from	to	target	type
    //   10	24	53	finally
    //   37	49	53	finally
    //   49	52	53	finally
    //   54	57	53	finally
    //   68	82	111	finally
    //   95	107	111	finally
    //   107	110	111	finally
    //   112	115	111	finally }
  
  public void clear() {
    if (this.mSize > 0) {
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      int i = this.mSize;
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
      this.mSize = 0;
      freeArrays(arrayOfInt, arrayOfObject, i);
    } 
    if (this.mSize > 0)
      throw new ConcurrentModificationException(); 
  }
  
  public boolean containsKey(Object paramObject) { return (indexOfKey(paramObject) >= 0); }
  
  public boolean containsValue(Object paramObject) { return (indexOfValue(paramObject) >= 0); }
  
  public void ensureCapacity(int paramInt) {
    int i = this.mSize;
    if (this.mHashes.length < paramInt) {
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      allocArrays(paramInt);
      if (this.mSize > 0) {
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, i);
        System.arraycopy(arrayOfObject, 0, this.mArray, 0, i << 1);
      } 
      freeArrays(arrayOfInt, arrayOfObject, i);
    } 
    if (this.mSize != i)
      throw new ConcurrentModificationException(); 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof SimpleArrayMap) {
      paramObject = (SimpleArrayMap)paramObject;
      if (size() != paramObject.size())
        return false; 
      byte b = 0;
      try {
        while (b < this.mSize) {
          Object object1 = keyAt(b);
          Object object2 = valueAt(b);
          Object object3 = paramObject.get(object1);
          if (object2 == null) {
            if (object3 == null) {
              if (!paramObject.containsKey(object1))
                return false; 
            } else {
              return false;
            } 
          } else {
            boolean bool = object2.equals(object3);
            if (!bool)
              return false; 
          } 
          b++;
        } 
        return true;
      } catch (NullPointerException paramObject) {
        return false;
      } catch (ClassCastException paramObject) {
        return false;
      } 
    } 
    if (paramObject instanceof Map) {
      paramObject = (Map)paramObject;
      if (size() != paramObject.size())
        return false; 
      byte b = 0;
      try {
        while (b < this.mSize) {
          Object object1 = keyAt(b);
          Object object2 = valueAt(b);
          Object object3 = paramObject.get(object1);
          if (object2 == null) {
            if (object3 == null) {
              if (!paramObject.containsKey(object1))
                return false; 
            } else {
              return false;
            } 
          } else {
            boolean bool = object2.equals(object3);
            if (!bool)
              return false; 
          } 
          b++;
        } 
        return true;
      } catch (NullPointerException paramObject) {
        return false;
      } catch (ClassCastException paramObject) {
        return false;
      } 
    } 
    return false;
  }
  
  public V get(Object paramObject) {
    int i = indexOfKey(paramObject);
    return (i >= 0) ? (V)this.mArray[(i << 1) + 1] : null;
  }
  
  public int hashCode() {
    int[] arrayOfInt = this.mHashes;
    Object[] arrayOfObject = this.mArray;
    int j = this.mSize;
    byte b2 = 0;
    byte b1 = 1;
    int i = 0;
    while (b2 < j) {
      int k;
      Object object = arrayOfObject[b1];
      int m = arrayOfInt[b2];
      if (object == null) {
        k = 0;
      } else {
        k = object.hashCode();
      } 
      i += (k ^ m);
      b2++;
      b1 += 2;
    } 
    return i;
  }
  
  int indexOf(Object paramObject, int paramInt) {
    int j = this.mSize;
    if (j == 0)
      return -1; 
    int k = binarySearchHashes(this.mHashes, j, paramInt);
    if (k < 0)
      return k; 
    if (paramObject.equals(this.mArray[k << 1]))
      return k; 
    int i;
    for (i = k + 1; i < j && this.mHashes[i] == paramInt; i++) {
      if (paramObject.equals(this.mArray[i << 1]))
        return i; 
    } 
    for (j = k - 1; j >= 0 && this.mHashes[j] == paramInt; j--) {
      if (paramObject.equals(this.mArray[j << 1]))
        return j; 
    } 
    return i ^ 0xFFFFFFFF;
  }
  
  public int indexOfKey(Object paramObject) { return (paramObject == null) ? indexOfNull() : indexOf(paramObject, paramObject.hashCode()); }
  
  int indexOfNull() {
    int j = this.mSize;
    if (j == 0)
      return -1; 
    int k = binarySearchHashes(this.mHashes, j, 0);
    if (k < 0)
      return k; 
    if (this.mArray[k << true] == null)
      return k; 
    int i;
    for (i = k + 1; i < j && this.mHashes[i] == 0; i++) {
      if (this.mArray[i << true] == null)
        return i; 
    } 
    for (j = k - 1; j >= 0 && this.mHashes[j] == 0; j--) {
      if (this.mArray[j << true] == null)
        return j; 
    } 
    return i ^ 0xFFFFFFFF;
  }
  
  int indexOfValue(Object paramObject) {
    int i = this.mSize * 2;
    Object[] arrayOfObject = this.mArray;
    if (paramObject == null) {
      for (byte b = 1; b < i; b += 2) {
        if (arrayOfObject[b] == null)
          return b >> true; 
      } 
    } else {
      for (byte b = 1; b < i; b += 2) {
        if (paramObject.equals(arrayOfObject[b]))
          return b >> true; 
      } 
    } 
    return -1;
  }
  
  public boolean isEmpty() { return (this.mSize <= 0); }
  
  public K keyAt(int paramInt) { return (K)this.mArray[paramInt << 1]; }
  
  public V put(K paramK, V paramV) {
    int j;
    int k = this.mSize;
    if (paramK == null) {
      i = indexOfNull();
      j = 0;
    } else {
      j = paramK.hashCode();
      i = indexOf(paramK, j);
    } 
    if (i >= 0) {
      i = (i << 1) + 1;
      paramK = (K)this.mArray[i];
      this.mArray[i] = paramV;
      return (V)paramK;
    } 
    int m = i ^ 0xFFFFFFFF;
    if (k >= this.mHashes.length) {
      i = 4;
      if (k >= 8) {
        i = (k >> 1) + k;
      } else if (k >= 4) {
        i = 8;
      } 
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject1 = this.mArray;
      allocArrays(i);
      if (k != this.mSize)
        throw new ConcurrentModificationException(); 
      if (this.mHashes.length > 0) {
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, arrayOfInt.length);
        System.arraycopy(arrayOfObject1, 0, this.mArray, 0, arrayOfObject1.length);
      } 
      freeArrays(arrayOfInt, arrayOfObject1, k);
    } 
    if (m < k) {
      int[] arrayOfInt1 = this.mHashes;
      int[] arrayOfInt2 = this.mHashes;
      i = m + 1;
      System.arraycopy(arrayOfInt1, m, arrayOfInt2, i, k - m);
      System.arraycopy(this.mArray, m << 1, this.mArray, i << 1, this.mSize - m << 1);
    } 
    if (k != this.mSize || m >= this.mHashes.length)
      throw new ConcurrentModificationException(); 
    this.mHashes[m] = j;
    Object[] arrayOfObject = this.mArray;
    int i = m << 1;
    arrayOfObject[i] = paramK;
    this.mArray[i + 1] = paramV;
    this.mSize++;
    return null;
  }
  
  public void putAll(SimpleArrayMap<? extends K, ? extends V> paramSimpleArrayMap) {
    int i = paramSimpleArrayMap.mSize;
    ensureCapacity(this.mSize + i);
    int j = this.mSize;
    byte b = 0;
    if (j == 0) {
      if (i > 0) {
        System.arraycopy(paramSimpleArrayMap.mHashes, 0, this.mHashes, 0, i);
        System.arraycopy(paramSimpleArrayMap.mArray, 0, this.mArray, 0, i << 1);
        this.mSize = i;
        return;
      } 
    } else {
      while (b < i) {
        put(paramSimpleArrayMap.keyAt(b), paramSimpleArrayMap.valueAt(b));
        b++;
      } 
    } 
  }
  
  public V remove(Object paramObject) {
    int i = indexOfKey(paramObject);
    return (i >= 0) ? (V)removeAt(i) : null;
  }
  
  public V removeAt(int paramInt) {
    Object[] arrayOfObject = this.mArray;
    int k = paramInt << 1;
    Object object = arrayOfObject[k + 1];
    int j = this.mSize;
    int i = 0;
    if (j <= 1) {
      freeArrays(this.mHashes, this.mArray, j);
      this.mHashes = ContainerHelpers.EMPTY_INTS;
      this.mArray = ContainerHelpers.EMPTY_OBJECTS;
      paramInt = i;
    } else {
      int m = j - 1;
      int n = this.mHashes.length;
      i = 8;
      if (n > 8 && this.mSize < this.mHashes.length / 3) {
        if (j > 8)
          i = j + (j >> 1); 
        int[] arrayOfInt = this.mHashes;
        Object[] arrayOfObject1 = this.mArray;
        allocArrays(i);
        if (j != this.mSize)
          throw new ConcurrentModificationException(); 
        if (paramInt > 0) {
          System.arraycopy(arrayOfInt, 0, this.mHashes, 0, paramInt);
          System.arraycopy(arrayOfObject1, 0, this.mArray, 0, k);
        } 
        if (paramInt < m) {
          i = paramInt + 1;
          int[] arrayOfInt1 = this.mHashes;
          n = m - paramInt;
          System.arraycopy(arrayOfInt, i, arrayOfInt1, paramInt, n);
          System.arraycopy(arrayOfObject1, i << 1, this.mArray, k, n << 1);
        } 
      } else {
        if (paramInt < m) {
          int[] arrayOfInt1 = this.mHashes;
          i = paramInt + 1;
          int[] arrayOfInt2 = this.mHashes;
          n = m - paramInt;
          System.arraycopy(arrayOfInt1, i, arrayOfInt2, paramInt, n);
          System.arraycopy(this.mArray, i << 1, this.mArray, k, n << 1);
        } 
        Object[] arrayOfObject1 = this.mArray;
        paramInt = m << 1;
        arrayOfObject1[paramInt] = null;
        this.mArray[paramInt + 1] = null;
      } 
      paramInt = m;
    } 
    if (j != this.mSize)
      throw new ConcurrentModificationException(); 
    this.mSize = paramInt;
    return (V)object;
  }
  
  public V setValueAt(int paramInt, V paramV) {
    paramInt = (paramInt << 1) + 1;
    Object object = this.mArray[paramInt];
    this.mArray[paramInt] = paramV;
    return (V)object;
  }
  
  public int size() { return this.mSize; }
  
  public String toString() {
    if (isEmpty())
      return "{}"; 
    StringBuilder stringBuilder = new StringBuilder(this.mSize * 28);
    stringBuilder.append('{');
    for (byte b = 0; b < this.mSize; b++) {
      if (b)
        stringBuilder.append(", "); 
      Object object = keyAt(b);
      if (object != this) {
        stringBuilder.append(object);
      } else {
        stringBuilder.append("(this Map)");
      } 
      stringBuilder.append('=');
      object = valueAt(b);
      if (object != this) {
        stringBuilder.append(object);
      } else {
        stringBuilder.append("(this Map)");
      } 
    } 
    stringBuilder.append('}');
    return stringBuilder.toString();
  }
  
  public V valueAt(int paramInt) { return (V)this.mArray[(paramInt << 1) + 1]; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/util/SimpleArrayMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */