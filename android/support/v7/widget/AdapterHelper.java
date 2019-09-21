package android.support.v7.widget;

import android.support.v4.util.Pools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AdapterHelper implements OpReorderer.Callback {
  private static final boolean DEBUG = false;
  
  static final int POSITION_TYPE_INVISIBLE = 0;
  
  static final int POSITION_TYPE_NEW_OR_LAID_OUT = 1;
  
  private static final String TAG = "AHT";
  
  final Callback mCallback;
  
  final boolean mDisableRecycler;
  
  private int mExistingUpdateTypes = 0;
  
  Runnable mOnItemProcessedCallback;
  
  final OpReorderer mOpReorderer;
  
  final ArrayList<UpdateOp> mPendingUpdates = new ArrayList();
  
  final ArrayList<UpdateOp> mPostponedList = new ArrayList();
  
  private Pools.Pool<UpdateOp> mUpdateOpPool = new Pools.SimplePool(30);
  
  AdapterHelper(Callback paramCallback) { this(paramCallback, false); }
  
  AdapterHelper(Callback paramCallback, boolean paramBoolean) {
    this.mCallback = paramCallback;
    this.mDisableRecycler = paramBoolean;
    this.mOpReorderer = new OpReorderer(this);
  }
  
  private void applyAdd(UpdateOp paramUpdateOp) { postponeAndUpdateViewHolders(paramUpdateOp); }
  
  private void applyMove(UpdateOp paramUpdateOp) { postponeAndUpdateViewHolders(paramUpdateOp); }
  
  private void applyRemove(UpdateOp paramUpdateOp) {
    int m = paramUpdateOp.positionStart;
    int j = paramUpdateOp.positionStart + paramUpdateOp.itemCount;
    int i = paramUpdateOp.positionStart;
    int k = 0;
    byte b = -1;
    while (i < j) {
      int n;
      if (this.mCallback.findViewHolder(i) != null || canFindInPreLayout(i)) {
        if (b == 0) {
          dispatchAndUpdateViewHolders(obtainUpdateOp(2, m, k, null));
          n = 1;
        } else {
          n = 0;
        } 
        b = 1;
      } else {
        if (b == 1) {
          postponeAndUpdateViewHolders(obtainUpdateOp(2, m, k, null));
          b = 1;
        } else {
          b = 0;
        } 
        byte b1 = 0;
        n = b;
        b = b1;
      } 
      if (n != 0) {
        i -= k;
        j -= k;
        n = 1;
      } else {
        n = k + 1;
      } 
      i++;
      k = n;
    } 
    UpdateOp updateOp = paramUpdateOp;
    if (k != paramUpdateOp.itemCount) {
      recycleUpdateOp(paramUpdateOp);
      updateOp = obtainUpdateOp(2, m, k, null);
    } 
    if (b == 0) {
      dispatchAndUpdateViewHolders(updateOp);
      return;
    } 
    postponeAndUpdateViewHolders(updateOp);
  }
  
  private void applyUpdate(UpdateOp paramUpdateOp) {
    int k = paramUpdateOp.positionStart;
    int n = paramUpdateOp.positionStart;
    int i1 = paramUpdateOp.itemCount;
    int i = paramUpdateOp.positionStart;
    int m = -1;
    int j = 0;
    while (i < n + i1) {
      int i2;
      if (this.mCallback.findViewHolder(i) != null || canFindInPreLayout(i)) {
        int i3 = j;
        i2 = k;
        if (m == 0) {
          dispatchAndUpdateViewHolders(obtainUpdateOp(4, k, j, paramUpdateOp.payload));
          i2 = i;
          i3 = 0;
        } 
        j = 1;
        k = i2;
        i2 = j;
        j = i3;
      } else {
        i2 = j;
        int i3 = k;
        if (m == 1) {
          postponeAndUpdateViewHolders(obtainUpdateOp(4, k, j, paramUpdateOp.payload));
          i3 = i;
          i2 = 0;
        } 
        k = 0;
        j = i2;
        i2 = k;
        k = i3;
      } 
      j++;
      i++;
      m = i2;
    } 
    Object object = paramUpdateOp;
    if (j != paramUpdateOp.itemCount) {
      object = paramUpdateOp.payload;
      recycleUpdateOp(paramUpdateOp);
      object = obtainUpdateOp(4, k, j, object);
    } 
    if (m == 0) {
      dispatchAndUpdateViewHolders(object);
      return;
    } 
    postponeAndUpdateViewHolders(object);
  }
  
  private boolean canFindInPreLayout(int paramInt) {
    int i = this.mPostponedList.size();
    for (byte b = 0; b < i; b++) {
      UpdateOp updateOp = (UpdateOp)this.mPostponedList.get(b);
      if (updateOp.cmd == 8) {
        if (findPositionOffset(updateOp.itemCount, b + 1) == paramInt)
          return true; 
      } else if (updateOp.cmd == 1) {
        int k = updateOp.positionStart;
        int m = updateOp.itemCount;
        for (int j = updateOp.positionStart; j < k + m; j++) {
          if (findPositionOffset(j, b + 1) == paramInt)
            return true; 
        } 
      } 
    } 
    return false;
  }
  
  private void dispatchAndUpdateViewHolders(UpdateOp paramUpdateOp) {
    byte b1;
    if (paramUpdateOp.cmd == 1 || paramUpdateOp.cmd == 8)
      throw new IllegalArgumentException("should not dispatch add or move for pre layout"); 
    int k = updatePositionWithPostponed(paramUpdateOp.positionStart, paramUpdateOp.cmd);
    int i = paramUpdateOp.positionStart;
    int j = paramUpdateOp.cmd;
    if (j != 2) {
      if (j != 4) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("op should be remove or update.");
        stringBuilder.append(paramUpdateOp);
        throw new IllegalArgumentException(stringBuilder.toString());
      } 
      b1 = 1;
    } else {
      b1 = 0;
    } 
    byte b2 = 1;
    int m;
    for (m = 1; b2 < paramUpdateOp.itemCount; m = j) {
      int n = updatePositionWithPostponed(paramUpdateOp.positionStart + b1 * b2, paramUpdateOp.cmd);
      j = paramUpdateOp.cmd;
      if ((j != 2) ? (j == 4 && n == k + 1) : (n == k)) {
        j = 1;
      } else {
        j = 0;
      } 
      if (j != 0) {
        j = m + true;
      } else {
        UpdateOp updateOp = obtainUpdateOp(paramUpdateOp.cmd, k, m, paramUpdateOp.payload);
        dispatchFirstPassAndUpdateViewHolders(updateOp, i);
        recycleUpdateOp(updateOp);
        j = i;
        if (paramUpdateOp.cmd == 4)
          j = i + m; 
        k = n;
        m = 1;
        i = j;
        j = m;
      } 
      b2++;
    } 
    Object object = paramUpdateOp.payload;
    recycleUpdateOp(paramUpdateOp);
    if (m > 0) {
      paramUpdateOp = obtainUpdateOp(paramUpdateOp.cmd, k, m, object);
      dispatchFirstPassAndUpdateViewHolders(paramUpdateOp, i);
      recycleUpdateOp(paramUpdateOp);
    } 
  }
  
  private void postponeAndUpdateViewHolders(UpdateOp paramUpdateOp) {
    this.mPostponedList.add(paramUpdateOp);
    int i = paramUpdateOp.cmd;
    if (i != 4) {
      if (i != 8) {
        StringBuilder stringBuilder;
        switch (i) {
          default:
            stringBuilder = new StringBuilder();
            stringBuilder.append("Unknown update op type for ");
            stringBuilder.append(paramUpdateOp);
            throw new IllegalArgumentException(stringBuilder.toString());
          case 2:
            this.mCallback.offsetPositionsForRemovingLaidOutOrNewView(paramUpdateOp.positionStart, paramUpdateOp.itemCount);
            return;
          case 1:
            break;
        } 
        this.mCallback.offsetPositionsForAdd(paramUpdateOp.positionStart, paramUpdateOp.itemCount);
        return;
      } 
      this.mCallback.offsetPositionsForMove(paramUpdateOp.positionStart, paramUpdateOp.itemCount);
      return;
    } 
    this.mCallback.markViewHoldersUpdated(paramUpdateOp.positionStart, paramUpdateOp.itemCount, paramUpdateOp.payload);
  }
  
  private int updatePositionWithPostponed(int paramInt1, int paramInt2) {
    int i = this.mPostponedList.size() - 1;
    int j;
    for (j = paramInt1; i >= 0; j = paramInt1) {
      UpdateOp updateOp = (UpdateOp)this.mPostponedList.get(i);
      if (updateOp.cmd == 8) {
        int k;
        if (updateOp.positionStart < updateOp.itemCount) {
          paramInt1 = updateOp.positionStart;
          k = updateOp.itemCount;
        } else {
          paramInt1 = updateOp.itemCount;
          k = updateOp.positionStart;
        } 
        if (j >= paramInt1 && j <= k) {
          if (paramInt1 == updateOp.positionStart) {
            if (paramInt2 == 1) {
              updateOp.itemCount++;
            } else if (paramInt2 == 2) {
              updateOp.itemCount--;
            } 
            paramInt1 = j + 1;
          } else {
            if (paramInt2 == 1) {
              updateOp.positionStart++;
            } else if (paramInt2 == 2) {
              updateOp.positionStart--;
            } 
            paramInt1 = j - 1;
          } 
        } else {
          paramInt1 = j;
          if (j < updateOp.positionStart)
            if (paramInt2 == 1) {
              updateOp.positionStart++;
              updateOp.itemCount++;
              paramInt1 = j;
            } else {
              paramInt1 = j;
              if (paramInt2 == 2) {
                updateOp.positionStart--;
                updateOp.itemCount--;
                paramInt1 = j;
              } 
            }  
        } 
      } else if (updateOp.positionStart <= j) {
        if (updateOp.cmd == 1) {
          paramInt1 = j - updateOp.itemCount;
        } else {
          paramInt1 = j;
          if (updateOp.cmd == 2)
            paramInt1 = j + updateOp.itemCount; 
        } 
      } else if (paramInt2 == 1) {
        updateOp.positionStart++;
        paramInt1 = j;
      } else {
        paramInt1 = j;
        if (paramInt2 == 2) {
          updateOp.positionStart--;
          paramInt1 = j;
        } 
      } 
      i--;
    } 
    for (paramInt1 = this.mPostponedList.size() - 1; paramInt1 >= 0; paramInt1--) {
      UpdateOp updateOp = (UpdateOp)this.mPostponedList.get(paramInt1);
      if (updateOp.cmd == 8) {
        if (updateOp.itemCount == updateOp.positionStart || updateOp.itemCount < 0) {
          this.mPostponedList.remove(paramInt1);
          recycleUpdateOp(updateOp);
        } 
      } else if (updateOp.itemCount <= 0) {
        this.mPostponedList.remove(paramInt1);
        recycleUpdateOp(updateOp);
      } 
    } 
    return j;
  }
  
  AdapterHelper addUpdateOp(UpdateOp... paramVarArgs) {
    Collections.addAll(this.mPendingUpdates, paramVarArgs);
    return this;
  }
  
  public int applyPendingUpdatesToPosition(int paramInt) {
    int j = this.mPendingUpdates.size();
    byte b = 0;
    int i;
    for (i = paramInt; b < j; i = paramInt) {
      UpdateOp updateOp = (UpdateOp)this.mPendingUpdates.get(b);
      paramInt = updateOp.cmd;
      if (paramInt != 8) {
        switch (paramInt) {
          default:
            paramInt = i;
            break;
          case 2:
            paramInt = i;
            if (updateOp.positionStart <= i) {
              if (updateOp.positionStart + updateOp.itemCount > i)
                return -1; 
              paramInt = i - updateOp.itemCount;
            } 
            break;
          case 1:
            paramInt = i;
            if (updateOp.positionStart <= i)
              paramInt = i + updateOp.itemCount; 
            break;
        } 
      } else if (updateOp.positionStart == i) {
        paramInt = updateOp.itemCount;
      } else {
        int k = i;
        if (updateOp.positionStart < i)
          k = i - 1; 
        paramInt = k;
        if (updateOp.itemCount <= k)
          paramInt = k + 1; 
      } 
      b++;
    } 
    return i;
  }
  
  void consumePostponedUpdates() {
    int i = this.mPostponedList.size();
    for (byte b = 0; b < i; b++)
      this.mCallback.onDispatchSecondPass((UpdateOp)this.mPostponedList.get(b)); 
    recycleUpdateOpsAndClearList(this.mPostponedList);
    this.mExistingUpdateTypes = 0;
  }
  
  void consumeUpdatesInOnePass() {
    consumePostponedUpdates();
    int i = this.mPendingUpdates.size();
    for (byte b = 0; b < i; b++) {
      UpdateOp updateOp = (UpdateOp)this.mPendingUpdates.get(b);
      int j = updateOp.cmd;
      if (j != 4) {
        if (j != 8) {
          switch (j) {
            case 2:
              this.mCallback.onDispatchSecondPass(updateOp);
              this.mCallback.offsetPositionsForRemovingInvisible(updateOp.positionStart, updateOp.itemCount);
              break;
            case 1:
              this.mCallback.onDispatchSecondPass(updateOp);
              this.mCallback.offsetPositionsForAdd(updateOp.positionStart, updateOp.itemCount);
              break;
          } 
        } else {
          this.mCallback.onDispatchSecondPass(updateOp);
          this.mCallback.offsetPositionsForMove(updateOp.positionStart, updateOp.itemCount);
        } 
      } else {
        this.mCallback.onDispatchSecondPass(updateOp);
        this.mCallback.markViewHoldersUpdated(updateOp.positionStart, updateOp.itemCount, updateOp.payload);
      } 
      if (this.mOnItemProcessedCallback != null)
        this.mOnItemProcessedCallback.run(); 
    } 
    recycleUpdateOpsAndClearList(this.mPendingUpdates);
    this.mExistingUpdateTypes = 0;
  }
  
  void dispatchFirstPassAndUpdateViewHolders(UpdateOp paramUpdateOp, int paramInt) {
    this.mCallback.onDispatchFirstPass(paramUpdateOp);
    int i = paramUpdateOp.cmd;
    if (i != 2) {
      if (i != 4)
        throw new IllegalArgumentException("only remove and update ops can be dispatched in first pass"); 
      this.mCallback.markViewHoldersUpdated(paramInt, paramUpdateOp.itemCount, paramUpdateOp.payload);
      return;
    } 
    this.mCallback.offsetPositionsForRemovingInvisible(paramInt, paramUpdateOp.itemCount);
  }
  
  int findPositionOffset(int paramInt) { return findPositionOffset(paramInt, 0); }
  
  int findPositionOffset(int paramInt1, int paramInt2) {
    int j = this.mPostponedList.size();
    int i = paramInt2;
    for (paramInt2 = paramInt1; i < j; paramInt2 = paramInt1) {
      UpdateOp updateOp = (UpdateOp)this.mPostponedList.get(i);
      if (updateOp.cmd == 8) {
        if (updateOp.positionStart == paramInt2) {
          paramInt1 = updateOp.itemCount;
        } else {
          int k = paramInt2;
          if (updateOp.positionStart < paramInt2)
            k = paramInt2 - 1; 
          paramInt1 = k;
          if (updateOp.itemCount <= k)
            paramInt1 = k + 1; 
        } 
      } else {
        paramInt1 = paramInt2;
        if (updateOp.positionStart <= paramInt2)
          if (updateOp.cmd == 2) {
            if (paramInt2 < updateOp.positionStart + updateOp.itemCount)
              return -1; 
            paramInt1 = paramInt2 - updateOp.itemCount;
          } else {
            paramInt1 = paramInt2;
            if (updateOp.cmd == 1)
              paramInt1 = paramInt2 + updateOp.itemCount; 
          }  
      } 
      i++;
    } 
    return paramInt2;
  }
  
  boolean hasAnyUpdateTypes(int paramInt) { return ((paramInt & this.mExistingUpdateTypes) != 0); }
  
  boolean hasPendingUpdates() { return (this.mPendingUpdates.size() > 0); }
  
  boolean hasUpdates() { return (!this.mPostponedList.isEmpty() && !this.mPendingUpdates.isEmpty()); }
  
  public UpdateOp obtainUpdateOp(int paramInt1, int paramInt2, int paramInt3, Object paramObject) {
    UpdateOp updateOp = (UpdateOp)this.mUpdateOpPool.acquire();
    if (updateOp == null)
      return new UpdateOp(paramInt1, paramInt2, paramInt3, paramObject); 
    updateOp.cmd = paramInt1;
    updateOp.positionStart = paramInt2;
    updateOp.itemCount = paramInt3;
    updateOp.payload = paramObject;
    return updateOp;
  }
  
  boolean onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject) {
    boolean bool = false;
    if (paramInt2 < 1)
      return false; 
    this.mPendingUpdates.add(obtainUpdateOp(4, paramInt1, paramInt2, paramObject));
    this.mExistingUpdateTypes |= 0x4;
    if (this.mPendingUpdates.size() == 1)
      bool = true; 
    return bool;
  }
  
  boolean onItemRangeInserted(int paramInt1, int paramInt2) {
    boolean bool = false;
    if (paramInt2 < 1)
      return false; 
    this.mPendingUpdates.add(obtainUpdateOp(1, paramInt1, paramInt2, null));
    this.mExistingUpdateTypes |= 0x1;
    if (this.mPendingUpdates.size() == 1)
      bool = true; 
    return bool;
  }
  
  boolean onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3) {
    boolean bool = false;
    if (paramInt1 == paramInt2)
      return false; 
    if (paramInt3 != 1)
      throw new IllegalArgumentException("Moving more than 1 item is not supported yet"); 
    this.mPendingUpdates.add(obtainUpdateOp(8, paramInt1, paramInt2, null));
    this.mExistingUpdateTypes |= 0x8;
    if (this.mPendingUpdates.size() == 1)
      bool = true; 
    return bool;
  }
  
  boolean onItemRangeRemoved(int paramInt1, int paramInt2) {
    boolean bool = false;
    if (paramInt2 < 1)
      return false; 
    this.mPendingUpdates.add(obtainUpdateOp(2, paramInt1, paramInt2, null));
    this.mExistingUpdateTypes |= 0x2;
    if (this.mPendingUpdates.size() == 1)
      bool = true; 
    return bool;
  }
  
  void preProcess() {
    this.mOpReorderer.reorderOps(this.mPendingUpdates);
    int i = this.mPendingUpdates.size();
    for (byte b = 0; b < i; b++) {
      UpdateOp updateOp = (UpdateOp)this.mPendingUpdates.get(b);
      int j = updateOp.cmd;
      if (j != 4) {
        if (j != 8) {
          switch (j) {
            case 2:
              applyRemove(updateOp);
              break;
            case 1:
              applyAdd(updateOp);
              break;
          } 
        } else {
          applyMove(updateOp);
        } 
      } else {
        applyUpdate(updateOp);
      } 
      if (this.mOnItemProcessedCallback != null)
        this.mOnItemProcessedCallback.run(); 
    } 
    this.mPendingUpdates.clear();
  }
  
  public void recycleUpdateOp(UpdateOp paramUpdateOp) {
    if (!this.mDisableRecycler) {
      paramUpdateOp.payload = null;
      this.mUpdateOpPool.release(paramUpdateOp);
    } 
  }
  
  void recycleUpdateOpsAndClearList(List<UpdateOp> paramList) {
    int i = paramList.size();
    for (byte b = 0; b < i; b++)
      recycleUpdateOp((UpdateOp)paramList.get(b)); 
    paramList.clear();
  }
  
  void reset() {
    recycleUpdateOpsAndClearList(this.mPendingUpdates);
    recycleUpdateOpsAndClearList(this.mPostponedList);
    this.mExistingUpdateTypes = 0;
  }
  
  static interface Callback {
    RecyclerView.ViewHolder findViewHolder(int param1Int);
    
    void markViewHoldersUpdated(int param1Int1, int param1Int2, Object param1Object);
    
    void offsetPositionsForAdd(int param1Int1, int param1Int2);
    
    void offsetPositionsForMove(int param1Int1, int param1Int2);
    
    void offsetPositionsForRemovingInvisible(int param1Int1, int param1Int2);
    
    void offsetPositionsForRemovingLaidOutOrNewView(int param1Int1, int param1Int2);
    
    void onDispatchFirstPass(AdapterHelper.UpdateOp param1UpdateOp);
    
    void onDispatchSecondPass(AdapterHelper.UpdateOp param1UpdateOp);
  }
  
  static class UpdateOp {
    static final int ADD = 1;
    
    static final int MOVE = 8;
    
    static final int POOL_SIZE = 30;
    
    static final int REMOVE = 2;
    
    static final int UPDATE = 4;
    
    int cmd;
    
    int itemCount;
    
    Object payload;
    
    int positionStart;
    
    UpdateOp(int param1Int1, int param1Int2, int param1Int3, Object param1Object) {
      this.cmd = param1Int1;
      this.positionStart = param1Int2;
      this.itemCount = param1Int3;
      this.payload = param1Object;
    }
    
    String cmdToString() {
      int i = this.cmd;
      if (i != 4) {
        if (i != 8) {
          switch (i) {
            default:
              return "??";
            case 2:
              return "rm";
            case 1:
              break;
          } 
          return "add";
        } 
        return "mv";
      } 
      return "up";
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object != null) {
        if (getClass() != param1Object.getClass())
          return false; 
        param1Object = (UpdateOp)param1Object;
        if (this.cmd != param1Object.cmd)
          return false; 
        if (this.cmd == 8 && Math.abs(this.itemCount - this.positionStart) == 1 && this.itemCount == param1Object.positionStart && this.positionStart == param1Object.itemCount)
          return true; 
        if (this.itemCount != param1Object.itemCount)
          return false; 
        if (this.positionStart != param1Object.positionStart)
          return false; 
        if (this.payload != null) {
          if (!this.payload.equals(param1Object.payload))
            return false; 
        } else if (param1Object.payload != null) {
          return false;
        } 
        return true;
      } 
      return false;
    }
    
    public int hashCode() { return (this.cmd * 31 + this.positionStart) * 31 + this.itemCount; }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      stringBuilder.append("[");
      stringBuilder.append(cmdToString());
      stringBuilder.append(",s:");
      stringBuilder.append(this.positionStart);
      stringBuilder.append("c:");
      stringBuilder.append(this.itemCount);
      stringBuilder.append(",p:");
      stringBuilder.append(this.payload);
      stringBuilder.append("]");
      return stringBuilder.toString();
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/AdapterHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */