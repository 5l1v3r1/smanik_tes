package android.support.v7.widget;

import android.support.annotation.Nullable;
import android.support.v4.os.TraceCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

final class GapWorker implements Runnable {
  static final ThreadLocal<GapWorker> sGapWorker = new ThreadLocal();
  
  static Comparator<Task> sTaskComparator = new Comparator<Task>() {
      public int compare(GapWorker.Task param1Task1, GapWorker.Task param1Task2) {
        boolean bool;
        RecyclerView recyclerView = param1Task1.view;
        byte b = 1;
        if (recyclerView == null) {
          i = 1;
        } else {
          i = 0;
        } 
        if (param1Task2.view == null) {
          bool = true;
        } else {
          bool = false;
        } 
        if (i != bool)
          return (param1Task1.view == null) ? 1 : -1; 
        if (param1Task1.immediate != param1Task2.immediate) {
          i = b;
          if (param1Task1.immediate)
            i = -1; 
          return i;
        } 
        int i = param1Task2.viewVelocity - param1Task1.viewVelocity;
        if (i != 0)
          return i; 
        i = param1Task1.distanceToItem - param1Task2.distanceToItem;
        return (i != 0) ? i : 0;
      }
    };
  
  long mFrameIntervalNs;
  
  long mPostTimeNs;
  
  ArrayList<RecyclerView> mRecyclerViews = new ArrayList();
  
  private ArrayList<Task> mTasks = new ArrayList();
  
  private void buildTaskList() {
    int j = this.mRecyclerViews.size();
    byte b = 0;
    int i;
    for (i = 0; b < j; i = k) {
      RecyclerView recyclerView = (RecyclerView)this.mRecyclerViews.get(b);
      int k = i;
      if (recyclerView.getWindowVisibility() == 0) {
        recyclerView.mPrefetchRegistry.collectPrefetchPositionsFromView(recyclerView, false);
        k = i + recyclerView.mPrefetchRegistry.mCount;
      } 
      b++;
    } 
    this.mTasks.ensureCapacity(i);
    i = 0;
    b = 0;
    while (i < j) {
      RecyclerView recyclerView = (RecyclerView)this.mRecyclerViews.get(i);
      if (recyclerView.getWindowVisibility() == 0) {
        LayoutPrefetchRegistryImpl layoutPrefetchRegistryImpl = recyclerView.mPrefetchRegistry;
        int k = Math.abs(layoutPrefetchRegistryImpl.mPrefetchDx) + Math.abs(layoutPrefetchRegistryImpl.mPrefetchDy);
        for (byte b1 = 0; b1 < layoutPrefetchRegistryImpl.mCount * 2; b1 += 2) {
          Task task;
          boolean bool;
          if (b >= this.mTasks.size()) {
            task = new Task();
            this.mTasks.add(task);
          } else {
            task = (Task)this.mTasks.get(b);
          } 
          int m = layoutPrefetchRegistryImpl.mPrefetchArray[b1 + true];
          if (m <= k) {
            bool = true;
          } else {
            bool = false;
          } 
          task.immediate = bool;
          task.viewVelocity = k;
          task.distanceToItem = m;
          task.view = recyclerView;
          task.position = layoutPrefetchRegistryImpl.mPrefetchArray[b1];
          b++;
        } 
      } 
      i++;
    } 
    Collections.sort(this.mTasks, sTaskComparator);
  }
  
  private void flushTaskWithDeadline(Task paramTask, long paramLong) {
    long l;
    if (paramTask.immediate) {
      l = Float.MAX_VALUE;
    } else {
      l = paramLong;
    } 
    RecyclerView.ViewHolder viewHolder = prefetchPositionWithDeadline(paramTask.view, paramTask.position, l);
    if (viewHolder != null && viewHolder.mNestedRecyclerView != null && viewHolder.isBound() && !viewHolder.isInvalid())
      prefetchInnerRecyclerViewWithDeadline((RecyclerView)viewHolder.mNestedRecyclerView.get(), paramLong); 
  }
  
  private void flushTasksWithDeadline(long paramLong) {
    for (byte b = 0; b < this.mTasks.size(); b++) {
      Task task = (Task)this.mTasks.get(b);
      if (task.view == null)
        return; 
      flushTaskWithDeadline(task, paramLong);
      task.clear();
    } 
  }
  
  static boolean isPrefetchPositionAttached(RecyclerView paramRecyclerView, int paramInt) {
    int i = paramRecyclerView.mChildHelper.getUnfilteredChildCount();
    for (byte b = 0; b < i; b++) {
      RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(paramRecyclerView.mChildHelper.getUnfilteredChildAt(b));
      if (viewHolder.mPosition == paramInt && !viewHolder.isInvalid())
        return true; 
    } 
    return false;
  }
  
  private void prefetchInnerRecyclerViewWithDeadline(@Nullable RecyclerView paramRecyclerView, long paramLong) {
    if (paramRecyclerView == null)
      return; 
    if (paramRecyclerView.mDataSetHasChangedAfterLayout && paramRecyclerView.mChildHelper.getUnfilteredChildCount() != 0)
      paramRecyclerView.removeAndRecycleViews(); 
    LayoutPrefetchRegistryImpl layoutPrefetchRegistryImpl = paramRecyclerView.mPrefetchRegistry;
    layoutPrefetchRegistryImpl.collectPrefetchPositionsFromView(paramRecyclerView, true);
    if (layoutPrefetchRegistryImpl.mCount != 0)
      try {
        TraceCompat.beginSection("RV Nested Prefetch");
        paramRecyclerView.mState.prepareForNestedPrefetch(paramRecyclerView.mAdapter);
        byte b;
        for (b = 0; b < layoutPrefetchRegistryImpl.mCount * 2; b += 2)
          prefetchPositionWithDeadline(paramRecyclerView, layoutPrefetchRegistryImpl.mPrefetchArray[b], paramLong); 
        return;
      } finally {
        TraceCompat.endSection();
      }  
  }
  
  private RecyclerView.ViewHolder prefetchPositionWithDeadline(RecyclerView paramRecyclerView, int paramInt, long paramLong) {
    if (isPrefetchPositionAttached(paramRecyclerView, paramInt))
      return null; 
    null = paramRecyclerView.mRecycler;
    try {
      paramRecyclerView.onEnterLayoutOrScroll();
      viewHolder = null.tryGetViewHolderForPositionByDeadline(paramInt, false, paramLong);
      if (viewHolder != null)
        if (viewHolder.isBound() && !viewHolder.isInvalid()) {
          null.recycleView(viewHolder.itemView);
        } else {
          null.addViewHolderToRecycledViewPool(viewHolder, false);
        }  
      return viewHolder;
    } finally {
      paramRecyclerView.onExitLayoutOrScroll(false);
    } 
  }
  
  public void add(RecyclerView paramRecyclerView) { this.mRecyclerViews.add(paramRecyclerView); }
  
  void postFromTraversal(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {
    if (paramRecyclerView.isAttachedToWindow() && this.mPostTimeNs == 0L) {
      this.mPostTimeNs = paramRecyclerView.getNanoTime();
      paramRecyclerView.post(this);
    } 
    paramRecyclerView.mPrefetchRegistry.setPrefetchVector(paramInt1, paramInt2);
  }
  
  void prefetch(long paramLong) {
    buildTaskList();
    flushTasksWithDeadline(paramLong);
  }
  
  public void remove(RecyclerView paramRecyclerView) { this.mRecyclerViews.remove(paramRecyclerView); }
  
  public void run() {
    try {
      TraceCompat.beginSection("RV Prefetch");
      boolean bool = this.mRecyclerViews.isEmpty();
      if (bool)
        return; 
      int i = this.mRecyclerViews.size();
      byte b = 0;
      long l;
      for (l = 0L; b < i; l = l1) {
        RecyclerView recyclerView = (RecyclerView)this.mRecyclerViews.get(b);
        long l1 = l;
        if (recyclerView.getWindowVisibility() == 0)
          l1 = Math.max(recyclerView.getDrawingTime(), l); 
        b++;
      } 
      if (l == 0L)
        return; 
      prefetch(TimeUnit.MILLISECONDS.toNanos(l) + this.mFrameIntervalNs);
      return;
    } finally {
      this.mPostTimeNs = 0L;
      TraceCompat.endSection();
    } 
  }
  
  static class LayoutPrefetchRegistryImpl implements RecyclerView.LayoutManager.LayoutPrefetchRegistry {
    int mCount;
    
    int[] mPrefetchArray;
    
    int mPrefetchDx;
    
    int mPrefetchDy;
    
    public void addPosition(int param1Int1, int param1Int2) {
      if (param1Int1 < 0)
        throw new IllegalArgumentException("Layout positions must be non-negative"); 
      if (param1Int2 < 0)
        throw new IllegalArgumentException("Pixel distance must be non-negative"); 
      int i = this.mCount * 2;
      if (this.mPrefetchArray == null) {
        this.mPrefetchArray = new int[4];
        Arrays.fill(this.mPrefetchArray, -1);
      } else if (i >= this.mPrefetchArray.length) {
        int[] arrayOfInt = this.mPrefetchArray;
        this.mPrefetchArray = new int[i * 2];
        System.arraycopy(arrayOfInt, 0, this.mPrefetchArray, 0, arrayOfInt.length);
      } 
      this.mPrefetchArray[i] = param1Int1;
      this.mPrefetchArray[i + 1] = param1Int2;
      this.mCount++;
    }
    
    void clearPrefetchPositions() {
      if (this.mPrefetchArray != null)
        Arrays.fill(this.mPrefetchArray, -1); 
      this.mCount = 0;
    }
    
    void collectPrefetchPositionsFromView(RecyclerView param1RecyclerView, boolean param1Boolean) {
      this.mCount = 0;
      if (this.mPrefetchArray != null)
        Arrays.fill(this.mPrefetchArray, -1); 
      RecyclerView.LayoutManager layoutManager = param1RecyclerView.mLayout;
      if (param1RecyclerView.mAdapter != null && layoutManager != null && layoutManager.isItemPrefetchEnabled()) {
        if (param1Boolean) {
          if (!param1RecyclerView.mAdapterHelper.hasPendingUpdates())
            layoutManager.collectInitialPrefetchPositions(param1RecyclerView.mAdapter.getItemCount(), this); 
        } else if (!param1RecyclerView.hasPendingAdapterUpdates()) {
          layoutManager.collectAdjacentPrefetchPositions(this.mPrefetchDx, this.mPrefetchDy, param1RecyclerView.mState, this);
        } 
        if (this.mCount > layoutManager.mPrefetchMaxCountObserved) {
          layoutManager.mPrefetchMaxCountObserved = this.mCount;
          layoutManager.mPrefetchMaxObservedInInitialPrefetch = param1Boolean;
          param1RecyclerView.mRecycler.updateViewCacheSize();
        } 
      } 
    }
    
    boolean lastPrefetchIncludedPosition(int param1Int) {
      if (this.mPrefetchArray != null) {
        int i = this.mCount;
        for (byte b = 0; b < i * 2; b += 2) {
          if (this.mPrefetchArray[b] == param1Int)
            return true; 
        } 
      } 
      return false;
    }
    
    void setPrefetchVector(int param1Int1, int param1Int2) {
      this.mPrefetchDx = param1Int1;
      this.mPrefetchDy = param1Int2;
    }
  }
  
  static class Task {
    public int distanceToItem;
    
    public boolean immediate;
    
    public int position;
    
    public RecyclerView view;
    
    public int viewVelocity;
    
    public void clear() {
      this.immediate = false;
      this.viewVelocity = 0;
      this.distanceToItem = 0;
      this.view = null;
      this.position = 0;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/GapWorker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */