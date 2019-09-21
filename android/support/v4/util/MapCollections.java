package android.support.v4.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

abstract class MapCollections<K, V> extends Object {
  EntrySet mEntrySet;
  
  KeySet mKeySet;
  
  ValuesCollection mValues;
  
  public static <K, V> boolean containsAllHelper(Map<K, V> paramMap, Collection<?> paramCollection) {
    Iterator iterator = paramCollection.iterator();
    while (iterator.hasNext()) {
      if (!paramMap.containsKey(iterator.next()))
        return false; 
    } 
    return true;
  }
  
  public static <T> boolean equalsSetHelper(Set<T> paramSet, Object paramObject) {
    if (paramSet == paramObject)
      return true; 
    if (paramObject instanceof Set) {
      paramObject = (Set)paramObject;
      try {
        if (paramSet.size() == paramObject.size()) {
          boolean bool = paramSet.containsAll(paramObject);
          if (bool)
            return true; 
        } 
        return false;
      } catch (NullPointerException paramSet) {
        return false;
      } catch (ClassCastException paramSet) {
        return false;
      } 
    } 
    return false;
  }
  
  public static <K, V> boolean removeAllHelper(Map<K, V> paramMap, Collection<?> paramCollection) {
    int i = paramMap.size();
    Iterator iterator = paramCollection.iterator();
    while (iterator.hasNext())
      paramMap.remove(iterator.next()); 
    return (i != paramMap.size());
  }
  
  public static <K, V> boolean retainAllHelper(Map<K, V> paramMap, Collection<?> paramCollection) {
    int i = paramMap.size();
    Iterator iterator = paramMap.keySet().iterator();
    while (iterator.hasNext()) {
      if (!paramCollection.contains(iterator.next()))
        iterator.remove(); 
    } 
    return (i != paramMap.size());
  }
  
  protected abstract void colClear();
  
  protected abstract Object colGetEntry(int paramInt1, int paramInt2);
  
  protected abstract Map<K, V> colGetMap();
  
  protected abstract int colGetSize();
  
  protected abstract int colIndexOfKey(Object paramObject);
  
  protected abstract int colIndexOfValue(Object paramObject);
  
  protected abstract void colPut(K paramK, V paramV);
  
  protected abstract void colRemoveAt(int paramInt);
  
  protected abstract V colSetValue(int paramInt, V paramV);
  
  public Set<Map.Entry<K, V>> getEntrySet() {
    if (this.mEntrySet == null)
      this.mEntrySet = new EntrySet(); 
    return this.mEntrySet;
  }
  
  public Set<K> getKeySet() {
    if (this.mKeySet == null)
      this.mKeySet = new KeySet(); 
    return this.mKeySet;
  }
  
  public Collection<V> getValues() {
    if (this.mValues == null)
      this.mValues = new ValuesCollection(); 
    return this.mValues;
  }
  
  public Object[] toArrayHelper(int paramInt) {
    int i = colGetSize();
    Object[] arrayOfObject = new Object[i];
    for (byte b = 0; b < i; b++)
      arrayOfObject[b] = colGetEntry(b, paramInt); 
    return arrayOfObject;
  }
  
  public <T> T[] toArrayHelper(T[] paramArrayOfT, int paramInt) {
    int i = colGetSize();
    T[] arrayOfT = paramArrayOfT;
    if (paramArrayOfT.length < i)
      arrayOfT = (T[])(Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i); 
    for (byte b = 0; b < i; b++)
      arrayOfT[b] = colGetEntry(b, paramInt); 
    if (arrayOfT.length > i)
      arrayOfT[i] = null; 
    return arrayOfT;
  }
  
  final class ArrayIterator<T> extends Object implements Iterator<T> {
    boolean mCanRemove = false;
    
    int mIndex;
    
    final int mOffset;
    
    int mSize;
    
    ArrayIterator(int param1Int) {
      this.mOffset = param1Int;
      this.mSize = this$0.colGetSize();
    }
    
    public boolean hasNext() { return (this.mIndex < this.mSize); }
    
    public T next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      Object object = MapCollections.this.colGetEntry(this.mIndex, this.mOffset);
      this.mIndex++;
      this.mCanRemove = true;
      return (T)object;
    }
    
    public void remove() {
      if (!this.mCanRemove)
        throw new IllegalStateException(); 
      this.mIndex--;
      this.mSize--;
      this.mCanRemove = false;
      MapCollections.this.colRemoveAt(this.mIndex);
    }
  }
  
  final class EntrySet extends Object implements Set<Map.Entry<K, V>> {
    public boolean add(Map.Entry<K, V> param1Entry) { throw new UnsupportedOperationException(); }
    
    public boolean addAll(Collection<? extends Map.Entry<K, V>> param1Collection) {
      int i = MapCollections.this.colGetSize();
      for (Map.Entry entry : param1Collection)
        MapCollections.this.colPut(entry.getKey(), entry.getValue()); 
      return (i != MapCollections.this.colGetSize());
    }
    
    public void clear() { MapCollections.this.colClear(); }
    
    public boolean contains(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      param1Object = (Map.Entry)param1Object;
      int i = MapCollections.this.colIndexOfKey(param1Object.getKey());
      return (i < 0) ? false : ContainerHelpers.equal(MapCollections.this.colGetEntry(i, 1), param1Object.getValue());
    }
    
    public boolean containsAll(Collection<?> param1Collection) {
      Iterator iterator = param1Collection.iterator();
      while (iterator.hasNext()) {
        if (!contains(iterator.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean equals(Object param1Object) { return MapCollections.equalsSetHelper(this, param1Object); }
    
    public int hashCode() {
      int i = MapCollections.this.colGetSize() - 1;
      int j = 0;
      while (i >= 0) {
        int m;
        int k;
        Object object1 = MapCollections.this.colGetEntry(i, 0);
        Object object2 = MapCollections.this.colGetEntry(i, 1);
        if (object1 == null) {
          k = 0;
        } else {
          k = object1.hashCode();
        } 
        if (object2 == null) {
          m = 0;
        } else {
          m = object2.hashCode();
        } 
        j += (k ^ m);
        i--;
      } 
      return j;
    }
    
    public boolean isEmpty() { return (MapCollections.this.colGetSize() == 0); }
    
    public Iterator<Map.Entry<K, V>> iterator() { return new MapCollections.MapIterator(MapCollections.this); }
    
    public boolean remove(Object param1Object) { throw new UnsupportedOperationException(); }
    
    public boolean removeAll(Collection<?> param1Collection) { throw new UnsupportedOperationException(); }
    
    public boolean retainAll(Collection<?> param1Collection) { throw new UnsupportedOperationException(); }
    
    public int size() { return MapCollections.this.colGetSize(); }
    
    public Object[] toArray() { throw new UnsupportedOperationException(); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) { throw new UnsupportedOperationException(); }
  }
  
  final class KeySet extends Object implements Set<K> {
    public boolean add(K param1K) { throw new UnsupportedOperationException(); }
    
    public boolean addAll(Collection<? extends K> param1Collection) { throw new UnsupportedOperationException(); }
    
    public void clear() { MapCollections.this.colClear(); }
    
    public boolean contains(Object param1Object) { return (MapCollections.this.colIndexOfKey(param1Object) >= 0); }
    
    public boolean containsAll(Collection<?> param1Collection) { return MapCollections.containsAllHelper(MapCollections.this.colGetMap(), param1Collection); }
    
    public boolean equals(Object param1Object) { return MapCollections.equalsSetHelper(this, param1Object); }
    
    public int hashCode() {
      int i = MapCollections.this.colGetSize() - 1;
      int j = 0;
      while (i >= 0) {
        int k;
        Object object = MapCollections.this.colGetEntry(i, 0);
        if (object == null) {
          k = 0;
        } else {
          k = object.hashCode();
        } 
        j += k;
        i--;
      } 
      return j;
    }
    
    public boolean isEmpty() { return (MapCollections.this.colGetSize() == 0); }
    
    public Iterator<K> iterator() { return new MapCollections.ArrayIterator(MapCollections.this, 0); }
    
    public boolean remove(Object param1Object) {
      int i = MapCollections.this.colIndexOfKey(param1Object);
      if (i >= 0) {
        MapCollections.this.colRemoveAt(i);
        return true;
      } 
      return false;
    }
    
    public boolean removeAll(Collection<?> param1Collection) { return MapCollections.removeAllHelper(MapCollections.this.colGetMap(), param1Collection); }
    
    public boolean retainAll(Collection<?> param1Collection) { return MapCollections.retainAllHelper(MapCollections.this.colGetMap(), param1Collection); }
    
    public int size() { return MapCollections.this.colGetSize(); }
    
    public Object[] toArray() { return MapCollections.this.toArrayHelper(0); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) { return (T[])MapCollections.this.toArrayHelper(param1ArrayOfT, 0); }
  }
  
  final class MapIterator extends Object implements Iterator<Map.Entry<K, V>>, Map.Entry<K, V> {
    int mEnd;
    
    boolean mEntryValid = false;
    
    int mIndex;
    
    MapIterator() {
      this.mEnd = this$0.colGetSize() - 1;
      this.mIndex = -1;
    }
    
    public boolean equals(Object param1Object) {
      if (!this.mEntryValid)
        throw new IllegalStateException("This container does not support retaining Map.Entry objects"); 
      boolean bool = param1Object instanceof Map.Entry;
      boolean bool1 = false;
      if (!bool)
        return false; 
      param1Object = (Map.Entry)param1Object;
      bool = bool1;
      if (ContainerHelpers.equal(param1Object.getKey(), MapCollections.this.colGetEntry(this.mIndex, 0))) {
        bool = bool1;
        if (ContainerHelpers.equal(param1Object.getValue(), MapCollections.this.colGetEntry(this.mIndex, 1)))
          bool = true; 
      } 
      return bool;
    }
    
    public K getKey() {
      if (!this.mEntryValid)
        throw new IllegalStateException("This container does not support retaining Map.Entry objects"); 
      return (K)MapCollections.this.colGetEntry(this.mIndex, 0);
    }
    
    public V getValue() {
      if (!this.mEntryValid)
        throw new IllegalStateException("This container does not support retaining Map.Entry objects"); 
      return (V)MapCollections.this.colGetEntry(this.mIndex, 1);
    }
    
    public boolean hasNext() { return (this.mIndex < this.mEnd); }
    
    public int hashCode() {
      if (!this.mEntryValid)
        throw new IllegalStateException("This container does not support retaining Map.Entry objects"); 
      MapCollections mapCollections = MapCollections.this;
      int i = this.mIndex;
      int j = 0;
      Object object1 = mapCollections.colGetEntry(i, 0);
      Object object2 = MapCollections.this.colGetEntry(this.mIndex, 1);
      if (object1 == null) {
        i = 0;
      } else {
        i = object1.hashCode();
      } 
      if (object2 != null)
        j = object2.hashCode(); 
      return i ^ j;
    }
    
    public Map.Entry<K, V> next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.mIndex++;
      this.mEntryValid = true;
      return this;
    }
    
    public void remove() {
      if (!this.mEntryValid)
        throw new IllegalStateException(); 
      MapCollections.this.colRemoveAt(this.mIndex);
      this.mIndex--;
      this.mEnd--;
      this.mEntryValid = false;
    }
    
    public V setValue(V param1V) {
      if (!this.mEntryValid)
        throw new IllegalStateException("This container does not support retaining Map.Entry objects"); 
      return (V)MapCollections.this.colSetValue(this.mIndex, param1V);
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(getKey());
      stringBuilder.append("=");
      stringBuilder.append(getValue());
      return stringBuilder.toString();
    }
  }
  
  final class ValuesCollection extends Object implements Collection<V> {
    public boolean add(V param1V) { throw new UnsupportedOperationException(); }
    
    public boolean addAll(Collection<? extends V> param1Collection) { throw new UnsupportedOperationException(); }
    
    public void clear() { MapCollections.this.colClear(); }
    
    public boolean contains(Object param1Object) { return (MapCollections.this.colIndexOfValue(param1Object) >= 0); }
    
    public boolean containsAll(Collection<?> param1Collection) {
      Iterator iterator = param1Collection.iterator();
      while (iterator.hasNext()) {
        if (!contains(iterator.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean isEmpty() { return (MapCollections.this.colGetSize() == 0); }
    
    public Iterator<V> iterator() { return new MapCollections.ArrayIterator(MapCollections.this, 1); }
    
    public boolean remove(Object param1Object) {
      int i = MapCollections.this.colIndexOfValue(param1Object);
      if (i >= 0) {
        MapCollections.this.colRemoveAt(i);
        return true;
      } 
      return false;
    }
    
    public boolean removeAll(Collection<?> param1Collection) {
      int i = MapCollections.this.colGetSize();
      byte b = 0;
      boolean bool = false;
      while (b < i) {
        int j = i;
        byte b1 = b;
        if (param1Collection.contains(MapCollections.this.colGetEntry(b, 1))) {
          MapCollections.this.colRemoveAt(b);
          b1 = b - 1;
          j = i - 1;
          bool = true;
        } 
        b = b1 + 1;
        i = j;
      } 
      return bool;
    }
    
    public boolean retainAll(Collection<?> param1Collection) {
      int i = MapCollections.this.colGetSize();
      byte b = 0;
      boolean bool = false;
      while (b < i) {
        int j = i;
        byte b1 = b;
        if (!param1Collection.contains(MapCollections.this.colGetEntry(b, 1))) {
          MapCollections.this.colRemoveAt(b);
          b1 = b - 1;
          j = i - 1;
          bool = true;
        } 
        b = b1 + 1;
        i = j;
      } 
      return bool;
    }
    
    public int size() { return MapCollections.this.colGetSize(); }
    
    public Object[] toArray() { return MapCollections.this.toArrayHelper(1); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) { return (T[])MapCollections.this.toArrayHelper(param1ArrayOfT, 1); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/util/MapCollections.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */