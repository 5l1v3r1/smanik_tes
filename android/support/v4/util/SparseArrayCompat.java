package android.support.v4.util;

public class SparseArrayCompat<E> extends Object implements Cloneable {
  private static final Object DELETED = new Object();
  
  private boolean mGarbage = false;
  
  private int[] mKeys;
  
  private int mSize;
  
  private Object[] mValues;
  
  public SparseArrayCompat() { this(10); }
  
  public SparseArrayCompat(int paramInt) {
    if (paramInt == 0) {
      this.mKeys = ContainerHelpers.EMPTY_INTS;
      this.mValues = ContainerHelpers.EMPTY_OBJECTS;
    } else {
      paramInt = ContainerHelpers.idealIntArraySize(paramInt);
      this.mKeys = new int[paramInt];
      this.mValues = new Object[paramInt];
    } 
    this.mSize = 0;
  }
  
  private void gc() {
    int i = this.mSize;
    int[] arrayOfInt = this.mKeys;
    Object[] arrayOfObject = this.mValues;
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b1 < i; b2 = b) {
      Object object = arrayOfObject[b1];
      byte b = b2;
      if (object != DELETED) {
        if (b1 != b2) {
          arrayOfInt[b2] = arrayOfInt[b1];
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
  
  public void append(int paramInt, E paramE) {
    if (this.mSize != 0 && paramInt <= this.mKeys[this.mSize - 1]) {
      put(paramInt, paramE);
      return;
    } 
    if (this.mGarbage && this.mSize >= this.mKeys.length)
      gc(); 
    int i = this.mSize;
    if (i >= this.mKeys.length) {
      int j = ContainerHelpers.idealIntArraySize(i + 1);
      int[] arrayOfInt = new int[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(this.mKeys, 0, arrayOfInt, 0, this.mKeys.length);
      System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
      this.mKeys = arrayOfInt;
      this.mValues = arrayOfObject;
    } 
    this.mKeys[i] = paramInt;
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
  
  public SparseArrayCompat<E> clone() {
    try {
      SparseArrayCompat sparseArrayCompat = (SparseArrayCompat)super.clone();
      try {
        sparseArrayCompat.mKeys = (int[])this.mKeys.clone();
        sparseArrayCompat.mValues = (Object[])this.mValues.clone();
        return sparseArrayCompat;
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        return sparseArrayCompat;
      } 
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public void delete(int paramInt) {
    paramInt = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if (paramInt >= 0 && this.mValues[paramInt] != DELETED) {
      this.mValues[paramInt] = DELETED;
      this.mGarbage = true;
    } 
  }
  
  public E get(int paramInt) { return (E)get(paramInt, null); }
  
  public E get(int paramInt, E paramE) {
    paramInt = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    return (paramInt >= 0) ? ((this.mValues[paramInt] == DELETED) ? paramE : (E)this.mValues[paramInt]) : paramE;
  }
  
  public int indexOfKey(int paramInt) {
    if (this.mGarbage)
      gc(); 
    return ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
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
  
  public int keyAt(int paramInt) {
    if (this.mGarbage)
      gc(); 
    return this.mKeys[paramInt];
  }
  
  public void put(int paramInt, E paramE) {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if (i >= 0) {
      this.mValues[i] = paramE;
      return;
    } 
    int j = i ^ 0xFFFFFFFF;
    if (j < this.mSize && this.mValues[j] == DELETED) {
      this.mKeys[j] = paramInt;
      this.mValues[j] = paramE;
      return;
    } 
    i = j;
    if (this.mGarbage) {
      i = j;
      if (this.mSize >= this.mKeys.length) {
        gc();
        i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt) ^ 0xFFFFFFFF;
      } 
    } 
    if (this.mSize >= this.mKeys.length) {
      j = ContainerHelpers.idealIntArraySize(this.mSize + 1);
      int[] arrayOfInt = new int[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(this.mKeys, 0, arrayOfInt, 0, this.mKeys.length);
      System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
      this.mKeys = arrayOfInt;
      this.mValues = arrayOfObject;
    } 
    if (this.mSize - i != 0) {
      int[] arrayOfInt1 = this.mKeys;
      int[] arrayOfInt2 = this.mKeys;
      j = i + 1;
      System.arraycopy(arrayOfInt1, i, arrayOfInt2, j, this.mSize - i);
      System.arraycopy(this.mValues, i, this.mValues, j, this.mSize - i);
    } 
    this.mKeys[i] = paramInt;
    this.mValues[i] = paramE;
    this.mSize++;
  }
  
  public void remove(int paramInt) { delete(paramInt); }
  
  public void removeAt(int paramInt) {
    if (this.mValues[paramInt] != DELETED) {
      this.mValues[paramInt] = DELETED;
      this.mGarbage = true;
    } 
  }
  
  public void removeAtRange(int paramInt1, int paramInt2) {
    paramInt2 = Math.min(this.mSize, paramInt2 + paramInt1);
    while (paramInt1 < paramInt2) {
      removeAt(paramInt1);
      paramInt1++;
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


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/util/SparseArrayCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */