package android.support.v7.recyclerview.extensions;

import android.support.annotation.NonNull;
import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import java.util.List;

public abstract class ListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
  private final AsyncListDiffer<T> mHelper;
  
  protected ListAdapter(@NonNull AsyncDifferConfig<T> paramAsyncDifferConfig) { this.mHelper = new AsyncListDiffer(new AdapterListUpdateCallback(this), paramAsyncDifferConfig); }
  
  protected ListAdapter(@NonNull DiffUtil.ItemCallback<T> paramItemCallback) { this.mHelper = new AsyncListDiffer(new AdapterListUpdateCallback(this), (new AsyncDifferConfig.Builder(paramItemCallback)).build()); }
  
  protected T getItem(int paramInt) { return (T)this.mHelper.getCurrentList().get(paramInt); }
  
  public int getItemCount() { return this.mHelper.getCurrentList().size(); }
  
  public void submitList(List<T> paramList) { this.mHelper.submitList(paramList); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/recyclerview/extensions/ListAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */