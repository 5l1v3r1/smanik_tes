package android.support.v7.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class SortedList<T> extends Object {
  private static final int CAPACITY_GROWTH = 10;
  
  private static final int DELETION = 2;
  
  private static final int INSERTION = 1;
  
  public static final int INVALID_POSITION = -1;
  
  private static final int LOOKUP = 4;
  
  private static final int MIN_CAPACITY = 10;
  
  private BatchedCallback mBatchedCallback;
  
  private Callback mCallback;
  
  T[] mData;
  
  private int mNewDataStart;
  
  private T[] mOldData;
  
  private int mOldDataSize;
  
  private int mOldDataStart;
  
  private int mSize;
  
  private final Class<T> mTClass;
  
  public SortedList(Class<T> paramClass, Callback<T> paramCallback) { this(paramClass, paramCallback, 10); }
  
  public SortedList(Class<T> paramClass, Callback<T> paramCallback, int paramInt) {
    this.mTClass = paramClass;
    this.mData = (Object[])Array.newInstance(paramClass, paramInt);
    this.mCallback = paramCallback;
    this.mSize = 0;
  }
  
  private int add(T paramT, boolean paramBoolean) {
    int i;
    int j = findIndexOf(paramT, this.mData, 0, this.mSize, 1);
    if (j == -1) {
      i = 0;
    } else {
      i = j;
      if (j < this.mSize) {
        Object object = this.mData[j];
        i = j;
        if (this.mCallback.areItemsTheSame(object, paramT)) {
          if (this.mCallback.areContentsTheSame(object, paramT)) {
            this.mData[j] = paramT;
            return j;
          } 
          this.mData[j] = paramT;
          this.mCallback.onChanged(j, 1, this.mCallback.getChangePayload(object, paramT));
          return j;
        } 
      } 
    } 
    addToData(i, paramT);
    if (paramBoolean)
      this.mCallback.onInserted(i, 1); 
    return i;
  }
  
  private void addAllInternal(T[] paramArrayOfT) {
    if (paramArrayOfT.length < 1)
      return; 
    int i = sortAndDedup(paramArrayOfT);
    if (this.mSize == 0) {
      this.mData = paramArrayOfT;
      this.mSize = i;
      this.mCallback.onInserted(0, i);
      return;
    } 
    merge(paramArrayOfT, i);
  }
  
  private void addToData(int paramInt, T paramT) { // Byte code:
    //   0: iload_1
    //   1: aload_0
    //   2: getfield mSize : I
    //   5: if_icmple -> 57
    //   8: new java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial <init> : ()V
    //   15: astore_2
    //   16: aload_2
    //   17: ldc 'cannot add item to '
    //   19: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   22: pop
    //   23: aload_2
    //   24: iload_1
    //   25: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   28: pop
    //   29: aload_2
    //   30: ldc ' because size is '
    //   32: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   35: pop
    //   36: aload_2
    //   37: aload_0
    //   38: getfield mSize : I
    //   41: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   44: pop
    //   45: new java/lang/IndexOutOfBoundsException
    //   48: dup
    //   49: aload_2
    //   50: invokevirtual toString : ()Ljava/lang/String;
    //   53: invokespecial <init> : (Ljava/lang/String;)V
    //   56: athrow
    //   57: aload_0
    //   58: getfield mSize : I
    //   61: aload_0
    //   62: getfield mData : [Ljava/lang/Object;
    //   65: arraylength
    //   66: if_icmpne -> 129
    //   69: aload_0
    //   70: getfield mTClass : Ljava/lang/Class;
    //   73: aload_0
    //   74: getfield mData : [Ljava/lang/Object;
    //   77: arraylength
    //   78: bipush #10
    //   80: iadd
    //   81: invokestatic newInstance : (Ljava/lang/Class;I)Ljava/lang/Object;
    //   84: checkcast [Ljava/lang/Object;
    //   87: astore_3
    //   88: aload_0
    //   89: getfield mData : [Ljava/lang/Object;
    //   92: iconst_0
    //   93: aload_3
    //   94: iconst_0
    //   95: iload_1
    //   96: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   99: aload_3
    //   100: iload_1
    //   101: aload_2
    //   102: aastore
    //   103: aload_0
    //   104: getfield mData : [Ljava/lang/Object;
    //   107: iload_1
    //   108: aload_3
    //   109: iload_1
    //   110: iconst_1
    //   111: iadd
    //   112: aload_0
    //   113: getfield mSize : I
    //   116: iload_1
    //   117: isub
    //   118: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   121: aload_0
    //   122: aload_3
    //   123: putfield mData : [Ljava/lang/Object;
    //   126: goto -> 157
    //   129: aload_0
    //   130: getfield mData : [Ljava/lang/Object;
    //   133: iload_1
    //   134: aload_0
    //   135: getfield mData : [Ljava/lang/Object;
    //   138: iload_1
    //   139: iconst_1
    //   140: iadd
    //   141: aload_0
    //   142: getfield mSize : I
    //   145: iload_1
    //   146: isub
    //   147: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   150: aload_0
    //   151: getfield mData : [Ljava/lang/Object;
    //   154: iload_1
    //   155: aload_2
    //   156: aastore
    //   157: aload_0
    //   158: aload_0
    //   159: getfield mSize : I
    //   162: iconst_1
    //   163: iadd
    //   164: putfield mSize : I
    //   167: return }
  
  private T[] copyArray(T[] paramArrayOfT) {
    Object[] arrayOfObject = (Object[])Array.newInstance(this.mTClass, paramArrayOfT.length);
    System.arraycopy(paramArrayOfT, 0, arrayOfObject, 0, paramArrayOfT.length);
    return (T[])arrayOfObject;
  }
  
  private int findIndexOf(T paramT, T[] paramArrayOfT, int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt1;
    while (i < paramInt2) {
      paramInt1 = (i + paramInt2) / 2;
      T t = paramArrayOfT[paramInt1];
      int j = this.mCallback.compare(t, paramT);
      if (j < 0) {
        i = paramInt1 + 1;
        continue;
      } 
      if (j == 0) {
        if (this.mCallback.areItemsTheSame(t, paramT))
          return paramInt1; 
        i = linearEqualitySearch(paramT, paramInt1, i, paramInt2);
        if (paramInt3 == 1) {
          paramInt2 = i;
          if (i == -1)
            paramInt2 = paramInt1; 
          return paramInt2;
        } 
        return i;
      } 
      paramInt2 = paramInt1;
    } 
    return (paramInt3 == 1) ? i : -1;
  }
  
  private int findSameItem(T paramT, T[] paramArrayOfT, int paramInt1, int paramInt2) {
    while (paramInt1 < paramInt2) {
      if (this.mCallback.areItemsTheSame(paramArrayOfT[paramInt1], paramT))
        return paramInt1; 
      paramInt1++;
    } 
    return -1;
  }
  
  private int linearEqualitySearch(T paramT, int paramInt1, int paramInt2, int paramInt3) {
    int i;
    int j = paramInt1 - 1;
    while (true) {
      i = paramInt1;
      if (j >= paramInt2) {
        Object object = this.mData[j];
        if (this.mCallback.compare(object, paramT) != 0) {
          i = paramInt1;
          break;
        } 
        if (this.mCallback.areItemsTheSame(object, paramT))
          return j; 
        j--;
        continue;
      } 
      break;
    } 
    while (true) {
      paramInt1 = i + 1;
      if (paramInt1 < paramInt3) {
        Object object = this.mData[paramInt1];
        if (this.mCallback.compare(object, paramT) != 0)
          break; 
        i = paramInt1;
        if (this.mCallback.areItemsTheSame(object, paramT))
          return paramInt1; 
        continue;
      } 
      break;
    } 
    return -1;
  }
  
  private void merge(T[] paramArrayOfT, int paramInt) {
    boolean bool;
    boolean bool1 = this.mCallback instanceof BatchedCallback;
    int i = 0;
    if (!bool1) {
      bool = true;
    } else {
      bool = false;
    } 
    if (bool)
      beginBatchedUpdates(); 
    this.mOldData = this.mData;
    this.mOldDataStart = 0;
    this.mOldDataSize = this.mSize;
    int j = this.mSize;
    this.mData = (Object[])Array.newInstance(this.mTClass, j + paramInt + 10);
    this.mNewDataStart = 0;
    while (true) {
      if (this.mOldDataStart < this.mOldDataSize || i < paramInt)
        if (this.mOldDataStart == this.mOldDataSize) {
          paramInt -= i;
          System.arraycopy(paramArrayOfT, i, this.mData, this.mNewDataStart, paramInt);
          this.mNewDataStart += paramInt;
          this.mSize += paramInt;
          this.mCallback.onInserted(this.mNewDataStart - paramInt, paramInt);
        } else if (i == paramInt) {
          paramInt = this.mOldDataSize - this.mOldDataStart;
          System.arraycopy(this.mOldData, this.mOldDataStart, this.mData, this.mNewDataStart, paramInt);
          this.mNewDataStart += paramInt;
        } else {
          Object[] arrayOfObject = this.mOldData[this.mOldDataStart];
          T t = paramArrayOfT[i];
          j = this.mCallback.compare(arrayOfObject, t);
          if (j > 0) {
            arrayOfObject = this.mData;
            j = this.mNewDataStart;
            this.mNewDataStart = j + 1;
            arrayOfObject[j] = t;
            this.mSize++;
            i++;
            this.mCallback.onInserted(this.mNewDataStart - 1, 1);
            continue;
          } 
          if (j == 0 && this.mCallback.areItemsTheSame(arrayOfObject, t)) {
            Object[] arrayOfObject1 = this.mData;
            j = this.mNewDataStart;
            this.mNewDataStart = j + 1;
            arrayOfObject1[j] = t;
            j = i + 1;
            this.mOldDataStart++;
            i = j;
            if (!this.mCallback.areContentsTheSame(arrayOfObject, t)) {
              this.mCallback.onChanged(this.mNewDataStart - 1, 1, this.mCallback.getChangePayload(arrayOfObject, t));
              i = j;
            } 
            continue;
          } 
          t = (T)this.mData;
          j = this.mNewDataStart;
          this.mNewDataStart = j + 1;
          t[j] = arrayOfObject;
          this.mOldDataStart++;
          continue;
        }  
      this.mOldData = null;
      if (bool)
        endBatchedUpdates(); 
      return;
    } 
  }
  
  private boolean remove(T paramT, boolean paramBoolean) {
    int i = findIndexOf(paramT, this.mData, 0, this.mSize, 2);
    if (i == -1)
      return false; 
    removeItemAtIndex(i, paramBoolean);
    return true;
  }
  
  private void removeItemAtIndex(int paramInt, boolean paramBoolean) {
    System.arraycopy(this.mData, paramInt + 1, this.mData, paramInt, this.mSize - paramInt - 1);
    this.mSize--;
    this.mData[this.mSize] = null;
    if (paramBoolean)
      this.mCallback.onRemoved(paramInt, 1); 
  }
  
  private void replaceAllInsert(T paramT) {
    this.mData[this.mNewDataStart] = paramT;
    this.mNewDataStart++;
    this.mSize++;
    this.mCallback.onInserted(this.mNewDataStart - 1, 1);
  }
  
  private void replaceAllInternal(@NonNull T[] paramArrayOfT) {
    boolean bool;
    if (!(this.mCallback instanceof BatchedCallback)) {
      bool = true;
    } else {
      bool = false;
    } 
    if (bool)
      beginBatchedUpdates(); 
    this.mOldDataStart = 0;
    this.mOldDataSize = this.mSize;
    this.mOldData = this.mData;
    this.mNewDataStart = 0;
    int i = sortAndDedup(paramArrayOfT);
    this.mData = (Object[])Array.newInstance(this.mTClass, i);
    while (true) {
      if (this.mNewDataStart < i || this.mOldDataStart < this.mOldDataSize)
        if (this.mOldDataStart >= this.mOldDataSize) {
          int j = this.mNewDataStart;
          i -= this.mNewDataStart;
          System.arraycopy(paramArrayOfT, j, this.mData, j, i);
          this.mNewDataStart += i;
          this.mSize += i;
          this.mCallback.onInserted(j, i);
        } else if (this.mNewDataStart >= i) {
          i = this.mOldDataSize - this.mOldDataStart;
          this.mSize -= i;
          this.mCallback.onRemoved(this.mNewDataStart, i);
        } else {
          Object object = this.mOldData[this.mOldDataStart];
          T t = paramArrayOfT[this.mNewDataStart];
          int j = this.mCallback.compare(object, t);
          if (j < 0) {
            replaceAllRemove();
            continue;
          } 
          if (j > 0) {
            replaceAllInsert(t);
            continue;
          } 
          if (!this.mCallback.areItemsTheSame(object, t)) {
            replaceAllRemove();
            replaceAllInsert(t);
            continue;
          } 
          this.mData[this.mNewDataStart] = t;
          this.mOldDataStart++;
          this.mNewDataStart++;
          if (!this.mCallback.areContentsTheSame(object, t))
            this.mCallback.onChanged(this.mNewDataStart - 1, 1, this.mCallback.getChangePayload(object, t)); 
          continue;
        }  
      this.mOldData = null;
      if (bool)
        endBatchedUpdates(); 
      return;
    } 
  }
  
  private void replaceAllRemove() {
    this.mSize--;
    this.mOldDataStart++;
    this.mCallback.onRemoved(this.mNewDataStart, 1);
  }
  
  private int sortAndDedup(@NonNull T[] paramArrayOfT) {
    int i = paramArrayOfT.length;
    int j = 0;
    if (i == 0)
      return 0; 
    Arrays.sort(paramArrayOfT, this.mCallback);
    byte b = 1;
    i = 1;
    while (b < paramArrayOfT.length) {
      T t = paramArrayOfT[b];
      if (this.mCallback.compare(paramArrayOfT[j], t) == 0) {
        int k = findSameItem(t, paramArrayOfT, j, i);
        if (k != -1) {
          paramArrayOfT[k] = t;
        } else {
          if (i != b)
            paramArrayOfT[i] = t; 
          i++;
        } 
      } else {
        if (i != b)
          paramArrayOfT[i] = t; 
        int k = i + 1;
        j = i;
        i = k;
      } 
      b++;
    } 
    return i;
  }
  
  private void throwIfInMutationOperation() {
    if (this.mOldData != null)
      throw new IllegalStateException("Data cannot be mutated in the middle of a batch update operation such as addAll or replaceAll."); 
  }
  
  public int add(T paramT) {
    throwIfInMutationOperation();
    return add(paramT, true);
  }
  
  public void addAll(Collection<T> paramCollection) { addAll(paramCollection.toArray((Object[])Array.newInstance(this.mTClass, paramCollection.size())), true); }
  
  public void addAll(T... paramVarArgs) { addAll(paramVarArgs, false); }
  
  public void addAll(T[] paramArrayOfT, boolean paramBoolean) {
    throwIfInMutationOperation();
    if (paramArrayOfT.length == 0)
      return; 
    if (paramBoolean) {
      addAllInternal(paramArrayOfT);
      return;
    } 
    addAllInternal(copyArray(paramArrayOfT));
  }
  
  public void beginBatchedUpdates() {
    throwIfInMutationOperation();
    if (this.mCallback instanceof BatchedCallback)
      return; 
    if (this.mBatchedCallback == null)
      this.mBatchedCallback = new BatchedCallback(this.mCallback); 
    this.mCallback = this.mBatchedCallback;
  }
  
  public void clear() {
    throwIfInMutationOperation();
    if (this.mSize == 0)
      return; 
    int i = this.mSize;
    Arrays.fill(this.mData, 0, i, null);
    this.mSize = 0;
    this.mCallback.onRemoved(0, i);
  }
  
  public void endBatchedUpdates() {
    throwIfInMutationOperation();
    if (this.mCallback instanceof BatchedCallback)
      ((BatchedCallback)this.mCallback).dispatchLastEvent(); 
    if (this.mCallback == this.mBatchedCallback)
      this.mCallback = this.mBatchedCallback.mWrappedCallback; 
  }
  
  public T get(int paramInt) throws IndexOutOfBoundsException {
    if (paramInt >= this.mSize || paramInt < 0) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Asked to get item at ");
      stringBuilder.append(paramInt);
      stringBuilder.append(" but size is ");
      stringBuilder.append(this.mSize);
      throw new IndexOutOfBoundsException(stringBuilder.toString());
    } 
    return (this.mOldData != null && paramInt >= this.mNewDataStart) ? (T)this.mOldData[paramInt - this.mNewDataStart + this.mOldDataStart] : (T)this.mData[paramInt];
  }
  
  public int indexOf(T paramT) {
    if (this.mOldData != null) {
      int i = findIndexOf(paramT, this.mData, 0, this.mNewDataStart, 4);
      if (i != -1)
        return i; 
      i = findIndexOf(paramT, this.mOldData, this.mOldDataStart, this.mOldDataSize, 4);
      return (i != -1) ? (i - this.mOldDataStart + this.mNewDataStart) : -1;
    } 
    return findIndexOf(paramT, this.mData, 0, this.mSize, 4);
  }
  
  public void recalculatePositionOfItemAt(int paramInt) {
    throwIfInMutationOperation();
    Object object = get(paramInt);
    removeItemAtIndex(paramInt, false);
    int i = add(object, false);
    if (paramInt != i)
      this.mCallback.onMoved(paramInt, i); 
  }
  
  public boolean remove(T paramT) {
    throwIfInMutationOperation();
    return remove(paramT, true);
  }
  
  public T removeItemAt(int paramInt) throws IndexOutOfBoundsException {
    throwIfInMutationOperation();
    Object object = get(paramInt);
    removeItemAtIndex(paramInt, true);
    return (T)object;
  }
  
  public void replaceAll(@NonNull Collection<T> paramCollection) { replaceAll(paramCollection.toArray((Object[])Array.newInstance(this.mTClass, paramCollection.size())), true); }
  
  public void replaceAll(@NonNull T... paramVarArgs) { replaceAll(paramVarArgs, false); }
  
  public void replaceAll(@NonNull T[] paramArrayOfT, boolean paramBoolean) {
    throwIfInMutationOperation();
    if (paramBoolean) {
      replaceAllInternal(paramArrayOfT);
      return;
    } 
    replaceAllInternal(copyArray(paramArrayOfT));
  }
  
  public int size() { return this.mSize; }
  
  public void updateItemAt(int paramInt, T paramT) {
    throwIfInMutationOperation();
    Object object = get(paramInt);
    if (object == paramT || !this.mCallback.areContentsTheSame(object, paramT)) {
      i = 1;
    } else {
      i = 0;
    } 
    if (object != paramT && this.mCallback.compare(object, paramT) == 0) {
      this.mData[paramInt] = paramT;
      if (i)
        this.mCallback.onChanged(paramInt, 1, this.mCallback.getChangePayload(object, paramT)); 
      return;
    } 
    if (i)
      this.mCallback.onChanged(paramInt, 1, this.mCallback.getChangePayload(object, paramT)); 
    removeItemAtIndex(paramInt, false);
    int i = add(paramT, false);
    if (paramInt != i)
      this.mCallback.onMoved(paramInt, i); 
  }
  
  public static class BatchedCallback<T2> extends Callback<T2> {
    private final BatchingListUpdateCallback mBatchingListUpdateCallback;
    
    final SortedList.Callback<T2> mWrappedCallback;
    
    public BatchedCallback(SortedList.Callback<T2> param1Callback) {
      this.mWrappedCallback = param1Callback;
      this.mBatchingListUpdateCallback = new BatchingListUpdateCallback(this.mWrappedCallback);
    }
    
    public boolean areContentsTheSame(T2 param1T21, T2 param1T22) { return this.mWrappedCallback.areContentsTheSame(param1T21, param1T22); }
    
    public boolean areItemsTheSame(T2 param1T21, T2 param1T22) { return this.mWrappedCallback.areItemsTheSame(param1T21, param1T22); }
    
    public int compare(T2 param1T21, T2 param1T22) { return this.mWrappedCallback.compare(param1T21, param1T22); }
    
    public void dispatchLastEvent() { this.mBatchingListUpdateCallback.dispatchLastEvent(); }
    
    @Nullable
    public Object getChangePayload(T2 param1T21, T2 param1T22) { return this.mWrappedCallback.getChangePayload(param1T21, param1T22); }
    
    public void onChanged(int param1Int1, int param1Int2) { this.mBatchingListUpdateCallback.onChanged(param1Int1, param1Int2, null); }
    
    public void onChanged(int param1Int1, int param1Int2, Object param1Object) { this.mBatchingListUpdateCallback.onChanged(param1Int1, param1Int2, param1Object); }
    
    public void onInserted(int param1Int1, int param1Int2) { this.mBatchingListUpdateCallback.onInserted(param1Int1, param1Int2); }
    
    public void onMoved(int param1Int1, int param1Int2) { this.mBatchingListUpdateCallback.onMoved(param1Int1, param1Int2); }
    
    public void onRemoved(int param1Int1, int param1Int2) { this.mBatchingListUpdateCallback.onRemoved(param1Int1, param1Int2); }
  }
  
  public static abstract class Callback<T2> extends Object implements Comparator<T2>, ListUpdateCallback {
    public abstract boolean areContentsTheSame(T2 param1T21, T2 param1T22);
    
    public abstract boolean areItemsTheSame(T2 param1T21, T2 param1T22);
    
    public abstract int compare(T2 param1T21, T2 param1T22);
    
    @Nullable
    public Object getChangePayload(T2 param1T21, T2 param1T22) { return null; }
    
    public abstract void onChanged(int param1Int1, int param1Int2);
    
    public void onChanged(int param1Int1, int param1Int2, Object param1Object) { onChanged(param1Int1, param1Int2); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/util/SortedList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */