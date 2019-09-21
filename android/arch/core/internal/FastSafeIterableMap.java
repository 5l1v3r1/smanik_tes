package android.arch.core.internal;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import java.util.HashMap;
import java.util.Map;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class FastSafeIterableMap<K, V> extends SafeIterableMap<K, V> {
  private HashMap<K, SafeIterableMap.Entry<K, V>> mHashMap = new HashMap();
  
  public Map.Entry<K, V> ceil(K paramK) { return contains(paramK) ? ((SafeIterableMap.Entry)this.mHashMap.get(paramK)).mPrevious : null; }
  
  public boolean contains(K paramK) { return this.mHashMap.containsKey(paramK); }
  
  protected SafeIterableMap.Entry<K, V> get(K paramK) { return (SafeIterableMap.Entry)this.mHashMap.get(paramK); }
  
  public V putIfAbsent(@NonNull K paramK, @NonNull V paramV) {
    SafeIterableMap.Entry entry = get(paramK);
    if (entry != null)
      return (V)entry.mValue; 
    this.mHashMap.put(paramK, put(paramK, paramV));
    return null;
  }
  
  public V remove(@NonNull K paramK) {
    Object object = super.remove(paramK);
    this.mHashMap.remove(paramK);
    return (V)object;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/core/internal/FastSafeIterableMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */