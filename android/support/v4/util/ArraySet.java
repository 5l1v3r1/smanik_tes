package android.support.v4.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class ArraySet<E> extends Object implements Collection<E>, Set<E> {
  private static final int BASE_SIZE = 4;
  
  private static final int CACHE_SIZE = 10;
  
  private static final boolean DEBUG = false;
  
  private static final int[] INT = new int[0];
  
  private static final Object[] OBJECT = new Object[0];
  
  private static final String TAG = "ArraySet";
  
  private static Object[] sBaseCache;
  
  private static int sBaseCacheSize;
  
  private static Object[] sTwiceBaseCache;
  
  private static int sTwiceBaseCacheSize;
  
  private Object[] mArray;
  
  private MapCollections<E, E> mCollections;
  
  private int[] mHashes;
  
  private int mSize;
  
  public ArraySet() { this(0); }
  
  public ArraySet(int paramInt) {
    if (paramInt == 0) {
      this.mHashes = INT;
      this.mArray = OBJECT;
    } else {
      allocArrays(paramInt);
    } 
    this.mSize = 0;
  }
  
  public ArraySet(@Nullable ArraySet<E> paramArraySet) {
    this();
    if (paramArraySet != null)
      addAll(paramArraySet); 
  }
  
  public ArraySet(@Nullable Collection<E> paramCollection) {
    this();
    if (paramCollection != null)
      addAll(paramCollection); 
  }
  
  private void allocArrays(int paramInt) { // Byte code:
    //   0: iload_1
    //   1: bipush #8
    //   3: if_icmpne -> 75
    //   6: ldc android/support/v4/util/ArraySet
    //   8: monitorenter
    //   9: getstatic android/support/v4/util/ArraySet.sTwiceBaseCache : [Ljava/lang/Object;
    //   12: ifnull -> 63
    //   15: getstatic android/support/v4/util/ArraySet.sTwiceBaseCache : [Ljava/lang/Object;
    //   18: astore_2
    //   19: aload_0
    //   20: aload_2
    //   21: putfield mArray : [Ljava/lang/Object;
    //   24: aload_2
    //   25: iconst_0
    //   26: aaload
    //   27: checkcast [Ljava/lang/Object;
    //   30: putstatic android/support/v4/util/ArraySet.sTwiceBaseCache : [Ljava/lang/Object;
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
    //   51: getstatic android/support/v4/util/ArraySet.sTwiceBaseCacheSize : I
    //   54: iconst_1
    //   55: isub
    //   56: putstatic android/support/v4/util/ArraySet.sTwiceBaseCacheSize : I
    //   59: ldc android/support/v4/util/ArraySet
    //   61: monitorexit
    //   62: return
    //   63: ldc android/support/v4/util/ArraySet
    //   65: monitorexit
    //   66: goto -> 149
    //   69: astore_2
    //   70: ldc android/support/v4/util/ArraySet
    //   72: monitorexit
    //   73: aload_2
    //   74: athrow
    //   75: iload_1
    //   76: iconst_4
    //   77: if_icmpne -> 149
    //   80: ldc android/support/v4/util/ArraySet
    //   82: monitorenter
    //   83: getstatic android/support/v4/util/ArraySet.sBaseCache : [Ljava/lang/Object;
    //   86: ifnull -> 137
    //   89: getstatic android/support/v4/util/ArraySet.sBaseCache : [Ljava/lang/Object;
    //   92: astore_2
    //   93: aload_0
    //   94: aload_2
    //   95: putfield mArray : [Ljava/lang/Object;
    //   98: aload_2
    //   99: iconst_0
    //   100: aaload
    //   101: checkcast [Ljava/lang/Object;
    //   104: putstatic android/support/v4/util/ArraySet.sBaseCache : [Ljava/lang/Object;
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
    //   125: getstatic android/support/v4/util/ArraySet.sBaseCacheSize : I
    //   128: iconst_1
    //   129: isub
    //   130: putstatic android/support/v4/util/ArraySet.sBaseCacheSize : I
    //   133: ldc android/support/v4/util/ArraySet
    //   135: monitorexit
    //   136: return
    //   137: ldc android/support/v4/util/ArraySet
    //   139: monitorexit
    //   140: goto -> 149
    //   143: astore_2
    //   144: ldc android/support/v4/util/ArraySet
    //   146: monitorexit
    //   147: aload_2
    //   148: athrow
    //   149: aload_0
    //   150: iload_1
    //   151: newarray int
    //   153: putfield mHashes : [I
    //   156: aload_0
    //   157: iload_1
    //   158: anewarray java/lang/Object
    //   161: putfield mArray : [Ljava/lang/Object;
    //   164: return
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
  
  private static void freeArrays(int[] paramArrayOfInt, Object[] paramArrayOfObject, int paramInt) { // Byte code:
    //   0: aload_0
    //   1: arraylength
    //   2: bipush #8
    //   4: if_icmpne -> 57
    //   7: ldc android/support/v4/util/ArraySet
    //   9: monitorenter
    //   10: getstatic android/support/v4/util/ArraySet.sTwiceBaseCacheSize : I
    //   13: bipush #10
    //   15: if_icmpge -> 47
    //   18: aload_1
    //   19: iconst_0
    //   20: getstatic android/support/v4/util/ArraySet.sTwiceBaseCache : [Ljava/lang/Object;
    //   23: aastore
    //   24: aload_1
    //   25: iconst_1
    //   26: aload_0
    //   27: aastore
    //   28: iload_2
    //   29: iconst_1
    //   30: isub
    //   31: istore_2
    //   32: goto -> 114
    //   35: aload_1
    //   36: putstatic android/support/v4/util/ArraySet.sTwiceBaseCache : [Ljava/lang/Object;
    //   39: getstatic android/support/v4/util/ArraySet.sTwiceBaseCacheSize : I
    //   42: iconst_1
    //   43: iadd
    //   44: putstatic android/support/v4/util/ArraySet.sTwiceBaseCacheSize : I
    //   47: ldc android/support/v4/util/ArraySet
    //   49: monitorexit
    //   50: return
    //   51: astore_0
    //   52: ldc android/support/v4/util/ArraySet
    //   54: monitorexit
    //   55: aload_0
    //   56: athrow
    //   57: aload_0
    //   58: arraylength
    //   59: iconst_4
    //   60: if_icmpne -> 113
    //   63: ldc android/support/v4/util/ArraySet
    //   65: monitorenter
    //   66: getstatic android/support/v4/util/ArraySet.sBaseCacheSize : I
    //   69: bipush #10
    //   71: if_icmpge -> 103
    //   74: aload_1
    //   75: iconst_0
    //   76: getstatic android/support/v4/util/ArraySet.sBaseCache : [Ljava/lang/Object;
    //   79: aastore
    //   80: aload_1
    //   81: iconst_1
    //   82: aload_0
    //   83: aastore
    //   84: iload_2
    //   85: iconst_1
    //   86: isub
    //   87: istore_2
    //   88: goto -> 130
    //   91: aload_1
    //   92: putstatic android/support/v4/util/ArraySet.sBaseCache : [Ljava/lang/Object;
    //   95: getstatic android/support/v4/util/ArraySet.sBaseCacheSize : I
    //   98: iconst_1
    //   99: iadd
    //   100: putstatic android/support/v4/util/ArraySet.sBaseCacheSize : I
    //   103: ldc android/support/v4/util/ArraySet
    //   105: monitorexit
    //   106: return
    //   107: astore_0
    //   108: ldc android/support/v4/util/ArraySet
    //   110: monitorexit
    //   111: aload_0
    //   112: athrow
    //   113: return
    //   114: iload_2
    //   115: iconst_2
    //   116: if_icmplt -> 35
    //   119: aload_1
    //   120: iload_2
    //   121: aconst_null
    //   122: aastore
    //   123: iload_2
    //   124: iconst_1
    //   125: isub
    //   126: istore_2
    //   127: goto -> 114
    //   130: iload_2
    //   131: iconst_2
    //   132: if_icmplt -> 91
    //   135: aload_1
    //   136: iload_2
    //   137: aconst_null
    //   138: aastore
    //   139: iload_2
    //   140: iconst_1
    //   141: isub
    //   142: istore_2
    //   143: goto -> 130
    // Exception table:
    //   from	to	target	type
    //   10	24	51	finally
    //   35	47	51	finally
    //   47	50	51	finally
    //   52	55	51	finally
    //   66	80	107	finally
    //   91	103	107	finally
    //   103	106	107	finally
    //   108	111	107	finally }
  
  private MapCollections<E, E> getCollection() {
    if (this.mCollections == null)
      this.mCollections = new MapCollections<E, E>() {
          protected void colClear() { ArraySet.this.clear(); }
          
          protected Object colGetEntry(int param1Int1, int param1Int2) { return ArraySet.this.mArray[param1Int1]; }
          
          protected Map<E, E> colGetMap() { throw new UnsupportedOperationException("not a map"); }
          
          protected int colGetSize() { return ArraySet.this.mSize; }
          
          protected int colIndexOfKey(Object param1Object) { return ArraySet.this.indexOf(param1Object); }
          
          protected int colIndexOfValue(Object param1Object) { return ArraySet.this.indexOf(param1Object); }
          
          protected void colPut(E param1E1, E param1E2) { ArraySet.this.add(param1E1); }
          
          protected void colRemoveAt(int param1Int) { ArraySet.this.removeAt(param1Int); }
          
          protected E colSetValue(int param1Int, E param1E) { throw new UnsupportedOperationException("not a map"); }
        }; 
    return this.mCollections;
  }
  
  private int indexOf(Object paramObject, int paramInt) {
    int j = this.mSize;
    if (j == 0)
      return -1; 
    int k = ContainerHelpers.binarySearch(this.mHashes, j, paramInt);
    if (k < 0)
      return k; 
    if (paramObject.equals(this.mArray[k]))
      return k; 
    int i;
    for (i = k + 1; i < j && this.mHashes[i] == paramInt; i++) {
      if (paramObject.equals(this.mArray[i]))
        return i; 
    } 
    for (j = k - 1; j >= 0 && this.mHashes[j] == paramInt; j--) {
      if (paramObject.equals(this.mArray[j]))
        return j; 
    } 
    return i ^ 0xFFFFFFFF;
  }
  
  private int indexOfNull() {
    int j = this.mSize;
    if (j == 0)
      return -1; 
    int k = ContainerHelpers.binarySearch(this.mHashes, j, 0);
    if (k < 0)
      return k; 
    if (this.mArray[k] == null)
      return k; 
    int i;
    for (i = k + 1; i < j && this.mHashes[i] == 0; i++) {
      if (this.mArray[i] == null)
        return i; 
    } 
    for (j = k - 1; j >= 0 && this.mHashes[j] == 0; j--) {
      if (this.mArray[j] == null)
        return j; 
    } 
    return i ^ 0xFFFFFFFF;
  }
  
  public boolean add(@Nullable E paramE) {
    int j;
    int i;
    if (paramE == null) {
      i = indexOfNull();
      j = 0;
    } else {
      j = paramE.hashCode();
      i = indexOf(paramE, j);
    } 
    if (i >= 0)
      return false; 
    int k = i ^ 0xFFFFFFFF;
    if (this.mSize >= this.mHashes.length) {
      int m = this.mSize;
      i = 4;
      if (m >= 8) {
        i = this.mSize;
        i = (this.mSize >> 1) + i;
      } else if (this.mSize >= 4) {
        i = 8;
      } 
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      allocArrays(i);
      if (this.mHashes.length > 0) {
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, arrayOfInt.length);
        System.arraycopy(arrayOfObject, 0, this.mArray, 0, arrayOfObject.length);
      } 
      freeArrays(arrayOfInt, arrayOfObject, this.mSize);
    } 
    if (k < this.mSize) {
      int[] arrayOfInt1 = this.mHashes;
      int[] arrayOfInt2 = this.mHashes;
      i = k + 1;
      System.arraycopy(arrayOfInt1, k, arrayOfInt2, i, this.mSize - k);
      System.arraycopy(this.mArray, k, this.mArray, i, this.mSize - k);
    } 
    this.mHashes[k] = j;
    this.mArray[k] = paramE;
    this.mSize++;
    return true;
  }
  
  public void addAll(@NonNull ArraySet<? extends E> paramArraySet) {
    int i = paramArraySet.mSize;
    ensureCapacity(this.mSize + i);
    int j = this.mSize;
    byte b = 0;
    if (j == 0) {
      if (i > 0) {
        System.arraycopy(paramArraySet.mHashes, 0, this.mHashes, 0, i);
        System.arraycopy(paramArraySet.mArray, 0, this.mArray, 0, i);
        this.mSize = i;
        return;
      } 
    } else {
      while (b < i) {
        add(paramArraySet.valueAt(b));
        b++;
      } 
    } 
  }
  
  public boolean addAll(@NonNull Collection<? extends E> paramCollection) {
    ensureCapacity(this.mSize + paramCollection.size());
    Iterator iterator = paramCollection.iterator();
    boolean bool;
    for (bool = false; iterator.hasNext(); bool |= add(iterator.next()));
    return bool;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void append(E paramE) {
    int i;
    int j = this.mSize;
    if (paramE == null) {
      i = 0;
    } else {
      i = paramE.hashCode();
    } 
    if (j >= this.mHashes.length)
      throw new IllegalStateException("Array is full"); 
    if (j > 0 && this.mHashes[j - 1] > i) {
      add(paramE);
      return;
    } 
    this.mSize = j + 1;
    this.mHashes[j] = i;
    this.mArray[j] = paramE;
  }
  
  public void clear() {
    if (this.mSize != 0) {
      freeArrays(this.mHashes, this.mArray, this.mSize);
      this.mHashes = INT;
      this.mArray = OBJECT;
      this.mSize = 0;
    } 
  }
  
  public boolean contains(Object paramObject) { return (indexOf(paramObject) >= 0); }
  
  public boolean containsAll(@NonNull Collection<?> paramCollection) {
    Iterator iterator = paramCollection.iterator();
    while (iterator.hasNext()) {
      if (!contains(iterator.next()))
        return false; 
    } 
    return true;
  }
  
  public void ensureCapacity(int paramInt) {
    if (this.mHashes.length < paramInt) {
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      allocArrays(paramInt);
      if (this.mSize > 0) {
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, this.mSize);
        System.arraycopy(arrayOfObject, 0, this.mArray, 0, this.mSize);
      } 
      freeArrays(arrayOfInt, arrayOfObject, this.mSize);
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof Set) {
      paramObject = (Set)paramObject;
      if (size() != paramObject.size())
        return false; 
      byte b = 0;
      try {
        while (b < this.mSize) {
          boolean bool = paramObject.contains(valueAt(b));
          if (!bool)
            return false; 
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
  
  public int hashCode() {
    int[] arrayOfInt = this.mHashes;
    int j = this.mSize;
    byte b = 0;
    int i = 0;
    while (b < j) {
      i += arrayOfInt[b];
      b++;
    } 
    return i;
  }
  
  public int indexOf(Object paramObject) { return (paramObject == null) ? indexOfNull() : indexOf(paramObject, paramObject.hashCode()); }
  
  public boolean isEmpty() { return (this.mSize <= 0); }
  
  public Iterator<E> iterator() { return getCollection().getKeySet().iterator(); }
  
  public boolean remove(Object paramObject) {
    int i = indexOf(paramObject);
    if (i >= 0) {
      removeAt(i);
      return true;
    } 
    return false;
  }
  
  public boolean removeAll(ArraySet<? extends E> paramArraySet) {
    int i = paramArraySet.mSize;
    int j = this.mSize;
    boolean bool = false;
    for (byte b = 0; b < i; b++)
      remove(paramArraySet.valueAt(b)); 
    if (j != this.mSize)
      bool = true; 
    return bool;
  }
  
  public boolean removeAll(@NonNull Collection<?> paramCollection) {
    Iterator iterator = paramCollection.iterator();
    boolean bool;
    for (bool = false; iterator.hasNext(); bool |= remove(iterator.next()));
    return bool;
  }
  
  public E removeAt(int paramInt) {
    Object object = this.mArray[paramInt];
    if (this.mSize <= 1) {
      freeArrays(this.mHashes, this.mArray, this.mSize);
      this.mHashes = INT;
      this.mArray = OBJECT;
      this.mSize = 0;
      return (E)object;
    } 
    int j = this.mHashes.length;
    int i = 8;
    if (j > 8 && this.mSize < this.mHashes.length / 3) {
      if (this.mSize > 8) {
        i = this.mSize;
        i = (this.mSize >> 1) + i;
      } 
      int[] arrayOfInt = this.mHashes;
      Object[] arrayOfObject = this.mArray;
      allocArrays(i);
      this.mSize--;
      if (paramInt > 0) {
        System.arraycopy(arrayOfInt, 0, this.mHashes, 0, paramInt);
        System.arraycopy(arrayOfObject, 0, this.mArray, 0, paramInt);
      } 
      if (paramInt < this.mSize) {
        i = paramInt + 1;
        System.arraycopy(arrayOfInt, i, this.mHashes, paramInt, this.mSize - paramInt);
        System.arraycopy(arrayOfObject, i, this.mArray, paramInt, this.mSize - paramInt);
        return (E)object;
      } 
    } else {
      this.mSize--;
      if (paramInt < this.mSize) {
        int[] arrayOfInt = this.mHashes;
        i = paramInt + 1;
        System.arraycopy(arrayOfInt, i, this.mHashes, paramInt, this.mSize - paramInt);
        System.arraycopy(this.mArray, i, this.mArray, paramInt, this.mSize - paramInt);
      } 
      this.mArray[this.mSize] = null;
    } 
    return (E)object;
  }
  
  public boolean retainAll(@NonNull Collection<?> paramCollection) {
    int i = this.mSize - 1;
    boolean bool = false;
    while (i >= 0) {
      if (!paramCollection.contains(this.mArray[i])) {
        removeAt(i);
        bool = true;
      } 
      i--;
    } 
    return bool;
  }
  
  public int size() { return this.mSize; }
  
  @NonNull
  public Object[] toArray() {
    Object[] arrayOfObject = new Object[this.mSize];
    System.arraycopy(this.mArray, 0, arrayOfObject, 0, this.mSize);
    return arrayOfObject;
  }
  
  @NonNull
  public <T> T[] toArray(@NonNull T[] paramArrayOfT) {
    T[] arrayOfT = paramArrayOfT;
    if (paramArrayOfT.length < this.mSize)
      arrayOfT = (T[])(Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), this.mSize); 
    System.arraycopy(this.mArray, 0, arrayOfT, 0, this.mSize);
    if (arrayOfT.length > this.mSize)
      arrayOfT[this.mSize] = null; 
    return arrayOfT;
  }
  
  public String toString() {
    if (isEmpty())
      return "{}"; 
    StringBuilder stringBuilder = new StringBuilder(this.mSize * 14);
    stringBuilder.append('{');
    for (byte b = 0; b < this.mSize; b++) {
      if (b)
        stringBuilder.append(", "); 
      Object object = valueAt(b);
      if (object != this) {
        stringBuilder.append(object);
      } else {
        stringBuilder.append("(this Set)");
      } 
    } 
    stringBuilder.append('}');
    return stringBuilder.toString();
  }
  
  @Nullable
  public E valueAt(int paramInt) { return (E)this.mArray[paramInt]; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/util/ArraySet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */