package android.support.v4.util;

public class LongSparseArray<E> extends Object implements Cloneable {
  private static final Object DELETED = new Object();
  
  private boolean mGarbage = false;
  
  private long[] mKeys;
  
  private int mSize;
  
  private Object[] mValues;
  
  public LongSparseArray() { this(10); }
  
  public LongSparseArray(int paramInt) {
    if (paramInt == 0) {
      this.mKeys = ContainerHelpers.EMPTY_LONGS;
      this.mValues = ContainerHelpers.EMPTY_OBJECTS;
    } else {
      paramInt = ContainerHelpers.idealLongArraySize(paramInt);
      this.mKeys = new long[paramInt];
      this.mValues = new Object[paramInt];
    } 
    this.mSize = 0;
  }
  
  private void gc() {
    int i = this.mSize;
    long[] arrayOfLong = this.mKeys;
    Object[] arrayOfObject = this.mValues;
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b1 < i; b2 = b) {
      Object object = arrayOfObject[b1];
      byte b = b2;
      if (object != DELETED) {
        if (b1 != b2) {
          arrayOfLong[b2] = arrayOfLong[b1];
          arrayOfObject[b2] = object;
          arrayOfObject[b1] = null;
        } 
        b = b2 + true;
      } 
      b1++;
    } 
    this.mGarbage = false;
    this.mSize = b2;
  }
  
  public void append(long paramLong, E paramE) {
    if (this.mSize != 0 && paramLong <= this.mKeys[this.mSize - 1]) {
      put(paramLong, paramE);
      return;
    } 
    if (this.mGarbage && this.mSize >= this.mKeys.length)
      gc(); 
    int i = this.mSize;
    if (i >= this.mKeys.length) {
      int j = ContainerHelpers.idealLongArraySize(i + 1);
      long[] arrayOfLong = new long[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(this.mKeys, 0, arrayOfLong, 0, this.mKeys.length);
      System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
      this.mKeys = arrayOfLong;
      this.mValues = arrayOfObject;
    } 
    this.mKeys[i] = paramLong;
    this.mValues[i] = paramE;
    this.mSize = i + 1;
  }
  
  public void clear() {
    int i = this.mSize;
    Object[] arrayOfObject = this.mValues;
    for (byte b = 0; b < i; b++)
      arrayOfObject[b] = null; 
    this.mSize = 0;
    this.mGarbage = false;
  }
  
  public LongSparseArray<E> clone() {
    try {
      LongSparseArray longSparseArray = (LongSparseArray)super.clone();
      try {
        longSparseArray.mKeys = (long[])this.mKeys.clone();
        longSparseArray.mValues = (Object[])this.mValues.clone();
        return longSparseArray;
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        return longSparseArray;
      } 
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public void delete(long paramLong) {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong);
    if (i >= 0 && this.mValues[i] != DELETED) {
      this.mValues[i] = DELETED;
      this.mGarbage = true;
    } 
  }
  
  public E get(long paramLong) { return (E)get(paramLong, null); }
  
  public E get(long paramLong, E paramE) {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong);
    return (i >= 0) ? ((this.mValues[i] == DELETED) ? paramE : (E)this.mValues[i]) : paramE;
  }
  
  public int indexOfKey(long paramLong) {
    if (this.mGarbage)
      gc(); 
    return ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong);
  }
  
  public int indexOfValue(E paramE) {
    if (this.mGarbage)
      gc(); 
    for (byte b = 0; b < this.mSize; b++) {
      if (this.mValues[b] == paramE)
        return b; 
    } 
    return -1;
  }
  
  public long keyAt(int paramInt) {
    if (this.mGarbage)
      gc(); 
    return this.mKeys[paramInt];
  }
  
  public void put(long paramLong, E paramE) {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong);
    if (i >= 0) {
      this.mValues[i] = paramE;
      return;
    } 
    int j = i ^ 0xFFFFFFFF;
    if (j < this.mSize && this.mValues[j] == DELETED) {
      this.mKeys[j] = paramLong;
      this.mValues[j] = paramE;
      return;
    } 
    i = j;
    if (this.mGarbage) {
      i = j;
      if (this.mSize >= this.mKeys.length) {
        gc();
        i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramLong) ^ 0xFFFFFFFF;
      } 
    } 
    if (this.mSize >= this.mKeys.length) {
      j = ContainerHelpers.idealLongArraySize(this.mSize + 1);
      long[] arrayOfLong = new long[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(this.mKeys, 0, arrayOfLong, 0, this.mKeys.length);
      System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
      this.mKeys = arrayOfLong;
      this.mValues = arrayOfObject;
    } 
    if (this.mSize - i != 0) {
      long[] arrayOfLong1 = this.mKeys;
      long[] arrayOfLong2 = this.mKeys;
      j = i + 1;
      System.arraycopy(arrayOfLong1, i, arrayOfLong2, j, this.mSize - i);
      System.arraycopy(this.mValues, i, this.mValues, j, this.mSize - i);
    } 
    this.mKeys[i] = paramLong;
    this.mValues[i] = paramE;
    this.mSize++;
  }
  
  public void remove(long paramLong) { delete(paramLong); }
  
  public void removeAt(int paramInt) {
    if (this.mValues[paramInt] != DELETED) {
      this.mValues[paramInt] = DELETED;
      this.mGarbage = true;
    } 
  }
  
  public void setValueAt(int paramInt, E paramE) {
    if (this.mGarbage)
      gc(); 
    this.mValues[paramInt] = paramE;
  }
  
  public int size() {
    if (this.mGarbage)
      gc(); 
    return this.mSize;
  }
  
  public String toString() {
    if (size() <= 0)
      return "{}"; 
    StringBuilder stringBuilder = new StringBuilder(this.mSize * 28);
    stringBuilder.append('{');
    for (byte b = 0; b < this.mSize; b++) {
      if (b)
        stringBuilder.append(", "); 
      stringBuilder.append(keyAt(b));
      stringBuilder.append('=');
      Object object = valueAt(b);
      if (object != this) {
        stringBuilder.append(object);
      } else {
        stringBuilder.append("(this Map)");
      } 
    } 
    stringBuilder.append('}');
    return stringBuilder.toString();
  }
  
  public E valueAt(int paramInt) {
    if (this.mGarbage)
      gc(); 
    return (E)this.mValues[paramInt];
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/util/LongSparseArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */