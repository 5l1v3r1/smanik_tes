package android.support.v7.recyclerview.extensions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import java.util.Collections;
import java.util.List;

public class AsyncListDiffer<T> extends Object {
  private final AsyncDifferConfig<T> mConfig;
  
  @Nullable
  private List<T> mList;
  
  private int mMaxScheduledGeneration;
  
  @NonNull
  private List<T> mReadOnlyList = Collections.emptyList();
  
  private final ListUpdateCallback mUpdateCallback;
  
  public AsyncListDiffer(@NonNull ListUpdateCallback paramListUpdateCallback, @NonNull AsyncDifferConfig<T> paramAsyncDifferConfig) {
    this.mUpdateCallback = paramListUpdateCallback;
    this.mConfig = paramAsyncDifferConfig;
  }
  
  public AsyncListDiffer(@NonNull RecyclerView.Adapter paramAdapter, @NonNull DiffUtil.ItemCallback<T> paramItemCallback) {
    this.mUpdateCallback = new AdapterListUpdateCallback(paramAdapter);
    this.mConfig = (new AsyncDifferConfig.Builder(paramItemCallback)).build();
  }
  
  private void latchList(@NonNull List<T> paramList, @NonNull DiffUtil.DiffResult paramDiffResult) {
    this.mList = paramList;
    this.mReadOnlyList = Collections.unmodifiableList(paramList);
    paramDiffResult.dispatchUpdatesTo(this.mUpdateCallback);
  }
  
  @NonNull
  public List<T> getCurrentList() { return this.mReadOnlyList; }
  
  public void submitList(final List<T> newList) {
    if (paramList == this.mList)
      return; 
    final int runGeneration = this.mMaxScheduledGeneration + 1;
    this.mMaxScheduledGeneration = i;
    if (paramList == null) {
      i = this.mList.size();
      this.mList = null;
      this.mReadOnlyList = Collections.emptyList();
      this.mUpdateCallback.onRemoved(0, i);
      return;
    } 
    if (this.mList == null) {
      this.mList = paramList;
      this.mReadOnlyList = Collections.unmodifiableList(paramList);
      this.mUpdateCallback.onInserted(0, paramList.size());
      return;
    } 
    final List oldList = this.mList;
    this.mConfig.getBackgroundThreadExecutor().execute(new Runnable() {
          public void run() {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                  public boolean areContentsTheSame(int param2Int1, int param2Int2) { return AsyncListDiffer.null.this.this$0.mConfig.getDiffCallback().areContentsTheSame(oldList.get(param2Int1), newList.get(param2Int2)); }
                  
                  public boolean areItemsTheSame(int param2Int1, int param2Int2) { return AsyncListDiffer.null.this.this$0.mConfig.getDiffCallback().areItemsTheSame(oldList.get(param2Int1), newList.get(param2Int2)); }
                  
                  @Nullable
                  public Object getChangePayload(int param2Int1, int param2Int2) { return AsyncListDiffer.null.this.this$0.mConfig.getDiffCallback().getChangePayload(oldList.get(param2Int1), newList.get(param2Int2)); }
                  
                  public int getNewListSize() { return newList.size(); }
                  
                  public int getOldListSize() { return oldList.size(); }
                });
            AsyncListDiffer.this.mConfig.getMainThreadExecutor().execute(new Runnable() {
                  public void run() {
                    if (AsyncListDiffer.null.this.this$0.mMaxScheduledGeneration == runGeneration)
                      AsyncListDiffer.null.this.this$0.latchList(newList, result); 
                  }
                });
          }
        });
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/recyclerview/extensions/AsyncListDiffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */