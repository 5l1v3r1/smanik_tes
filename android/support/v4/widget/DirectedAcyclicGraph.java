package android.support.v4.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.util.Pools;
import android.support.v4.util.SimpleArrayMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestrictTo({RestrictTo.Scope.LIBRARY})
public final class DirectedAcyclicGraph<T> extends Object {
  private final SimpleArrayMap<T, ArrayList<T>> mGraph = new SimpleArrayMap();
  
  private final Pools.Pool<ArrayList<T>> mListPool = new Pools.SimplePool(10);
  
  private final ArrayList<T> mSortResult = new ArrayList();
  
  private final HashSet<T> mSortTmpMarked = new HashSet();
  
  private void dfs(T paramT, ArrayList<T> paramArrayList, HashSet<T> paramHashSet) {
    if (paramArrayList.contains(paramT))
      return; 
    if (paramHashSet.contains(paramT))
      throw new RuntimeException("This graph contains cyclic dependencies"); 
    paramHashSet.add(paramT);
    ArrayList arrayList = (ArrayList)this.mGraph.get(paramT);
    if (arrayList != null) {
      byte b = 0;
      int i = arrayList.size();
      while (b < i) {
        dfs(arrayList.get(b), paramArrayList, paramHashSet);
        b++;
      } 
    } 
    paramHashSet.remove(paramT);
    paramArrayList.add(paramT);
  }
  
  @NonNull
  private ArrayList<T> getEmptyList() {
    ArrayList arrayList2 = (ArrayList)this.mListPool.acquire();
    ArrayList arrayList1 = arrayList2;
    if (arrayList2 == null)
      arrayList1 = new ArrayList(); 
    return arrayList1;
  }
  
  private void poolList(@NonNull ArrayList<T> paramArrayList) {
    paramArrayList.clear();
    this.mListPool.release(paramArrayList);
  }
  
  public void addEdge(@NonNull T paramT1, @NonNull T paramT2) {
    if (!this.mGraph.containsKey(paramT1) || !this.mGraph.containsKey(paramT2))
      throw new IllegalArgumentException("All nodes must be present in the graph before being added as an edge"); 
    ArrayList arrayList2 = (ArrayList)this.mGraph.get(paramT1);
    ArrayList arrayList1 = arrayList2;
    if (arrayList2 == null) {
      arrayList1 = getEmptyList();
      this.mGraph.put(paramT1, arrayList1);
    } 
    arrayList1.add(paramT2);
  }
  
  public void addNode(@NonNull T paramT) {
    if (!this.mGraph.containsKey(paramT))
      this.mGraph.put(paramT, null); 
  }
  
  public void clear() {
    int i = this.mGraph.size();
    for (byte b = 0; b < i; b++) {
      ArrayList arrayList = (ArrayList)this.mGraph.valueAt(b);
      if (arrayList != null)
        poolList(arrayList); 
    } 
    this.mGraph.clear();
  }
  
  public boolean contains(@NonNull T paramT) { return this.mGraph.containsKey(paramT); }
  
  @Nullable
  public List getIncomingEdges(@NonNull T paramT) { return (List)this.mGraph.get(paramT); }
  
  @Nullable
  public List<T> getOutgoingEdges(@NonNull T paramT) {
    int i = this.mGraph.size();
    ArrayList arrayList = null;
    byte b = 0;
    while (b < i) {
      ArrayList arrayList2 = (ArrayList)this.mGraph.valueAt(b);
      ArrayList arrayList1 = arrayList;
      if (arrayList2 != null) {
        arrayList1 = arrayList;
        if (arrayList2.contains(paramT)) {
          arrayList1 = arrayList;
          if (arrayList == null)
            arrayList1 = new ArrayList(); 
          arrayList1.add(this.mGraph.keyAt(b));
        } 
      } 
      b++;
      arrayList = arrayList1;
    } 
    return arrayList;
  }
  
  @NonNull
  public ArrayList<T> getSortedList() {
    this.mSortResult.clear();
    this.mSortTmpMarked.clear();
    int i = this.mGraph.size();
    for (byte b = 0; b < i; b++)
      dfs(this.mGraph.keyAt(b), this.mSortResult, this.mSortTmpMarked); 
    return this.mSortResult;
  }
  
  public boolean hasOutgoingEdges(@NonNull T paramT) {
    int i = this.mGraph.size();
    for (byte b = 0; b < i; b++) {
      ArrayList arrayList = (ArrayList)this.mGraph.valueAt(b);
      if (arrayList != null && arrayList.contains(paramT))
        return true; 
    } 
    return false;
  }
  
  int size() { return this.mGraph.size(); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/widget/DirectedAcyclicGraph.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */