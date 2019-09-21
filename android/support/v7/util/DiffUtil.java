package android.support.v7.util;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DiffUtil {
  private static final Comparator<Snake> SNAKE_COMPARATOR = new Comparator<Snake>() {
      public int compare(DiffUtil.Snake param1Snake1, DiffUtil.Snake param1Snake2) {
        int j = param1Snake1.x - param1Snake2.x;
        int i = j;
        if (j == 0)
          i = param1Snake1.y - param1Snake2.y; 
        return i;
      }
    };
  
  public static DiffResult calculateDiff(Callback paramCallback) { return calculateDiff(paramCallback, true); }
  
  public static DiffResult calculateDiff(Callback paramCallback, boolean paramBoolean) {
    int i = paramCallback.getOldListSize();
    int j = paramCallback.getNewListSize();
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    arrayList2.add(new Range(0, i, 0, j));
    i = Math.abs(i - j) + i + j;
    j = i * 2;
    int[] arrayOfInt1 = new int[j];
    int[] arrayOfInt2 = new int[j];
    ArrayList arrayList3 = new ArrayList();
    while (!arrayList2.isEmpty()) {
      Range range = (Range)arrayList2.remove(arrayList2.size() - 1);
      Snake snake = diffPartial(paramCallback, range.oldListStart, range.oldListEnd, range.newListStart, range.newListEnd, arrayOfInt1, arrayOfInt2, i);
      if (snake != null) {
        Range range1;
        if (snake.size > 0)
          arrayList1.add(snake); 
        snake.x += range.oldListStart;
        snake.y += range.newListStart;
        if (arrayList3.isEmpty()) {
          range1 = new Range();
        } else {
          range1 = (Range)arrayList3.remove(arrayList3.size() - 1);
        } 
        range1.oldListStart = range.oldListStart;
        range1.newListStart = range.newListStart;
        if (snake.reverse) {
          range1.oldListEnd = snake.x;
          range1.newListEnd = snake.y;
        } else if (snake.removal) {
          range1.oldListEnd = snake.x - 1;
          range1.newListEnd = snake.y;
        } else {
          range1.oldListEnd = snake.x;
          range1.newListEnd = snake.y - 1;
        } 
        arrayList2.add(range1);
        if (snake.reverse) {
          if (snake.removal) {
            range.oldListStart = snake.x + snake.size + 1;
            range.newListStart = snake.y + snake.size;
          } else {
            range.oldListStart = snake.x + snake.size;
            range.newListStart = snake.y + snake.size + 1;
          } 
        } else {
          range.oldListStart = snake.x + snake.size;
          range.newListStart = snake.y + snake.size;
        } 
        arrayList2.add(range);
        continue;
      } 
      arrayList3.add(range);
    } 
    Collections.sort(arrayList1, SNAKE_COMPARATOR);
    return new DiffResult(paramCallback, arrayList1, arrayOfInt1, arrayOfInt2, paramBoolean);
  }
  
  private static Snake diffPartial(Callback paramCallback, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt5) {
    paramInt2 -= paramInt1;
    paramInt4 -= paramInt3;
    if (paramInt2 < 1 || paramInt4 < 1)
      return null; 
    int m = paramInt2 - paramInt4;
    int i = (paramInt2 + paramInt4 + 1) / 2;
    int j = paramInt5 - i - 1;
    int k = paramInt5 + i + 1;
    Arrays.fill(paramArrayOfInt1, j, k, 0);
    Arrays.fill(paramArrayOfInt2, j + m, k + m, paramInt2);
    if (m % 2 != 0) {
      k = 1;
    } else {
      k = 0;
    } 
    byte b;
    for (b = 0; b <= i; b++) {
      byte b1 = -b;
      byte b2 = b1;
      while (true)
        b2 += 2; 
      b2 = b1;
      while (true)
        b2 += 2; 
      continue;
    } 
    throw new IllegalStateException("DiffUtil hit an unexpected case while trying to calculate the optimal path. Please make sure your data is not changing during the diff calculation.");
  }
  
  public static abstract class Callback {
    public abstract boolean areContentsTheSame(int param1Int1, int param1Int2);
    
    public abstract boolean areItemsTheSame(int param1Int1, int param1Int2);
    
    @Nullable
    public Object getChangePayload(int param1Int1, int param1Int2) { return null; }
    
    public abstract int getNewListSize();
    
    public abstract int getOldListSize();
  }
  
  public static class DiffResult {
    private static final int FLAG_CHANGED = 2;
    
    private static final int FLAG_IGNORE = 16;
    
    private static final int FLAG_MASK = 31;
    
    private static final int FLAG_MOVED_CHANGED = 4;
    
    private static final int FLAG_MOVED_NOT_CHANGED = 8;
    
    private static final int FLAG_NOT_CHANGED = 1;
    
    private static final int FLAG_OFFSET = 5;
    
    private final DiffUtil.Callback mCallback;
    
    private final boolean mDetectMoves;
    
    private final int[] mNewItemStatuses;
    
    private final int mNewListSize;
    
    private final int[] mOldItemStatuses;
    
    private final int mOldListSize;
    
    private final List<DiffUtil.Snake> mSnakes;
    
    DiffResult(DiffUtil.Callback param1Callback, List<DiffUtil.Snake> param1List, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2, boolean param1Boolean) {
      this.mSnakes = param1List;
      this.mOldItemStatuses = param1ArrayOfInt1;
      this.mNewItemStatuses = param1ArrayOfInt2;
      Arrays.fill(this.mOldItemStatuses, 0);
      Arrays.fill(this.mNewItemStatuses, 0);
      this.mCallback = param1Callback;
      this.mOldListSize = param1Callback.getOldListSize();
      this.mNewListSize = param1Callback.getNewListSize();
      this.mDetectMoves = param1Boolean;
      addRootSnake();
      findMatchingItems();
    }
    
    private void addRootSnake() {
      DiffUtil.Snake snake;
      if (this.mSnakes.isEmpty()) {
        snake = null;
      } else {
        snake = (DiffUtil.Snake)this.mSnakes.get(0);
      } 
      if (snake == null || snake.x != 0 || snake.y != 0) {
        snake = new DiffUtil.Snake();
        snake.x = 0;
        snake.y = 0;
        snake.removal = false;
        snake.size = 0;
        snake.reverse = false;
        this.mSnakes.add(0, snake);
      } 
    }
    
    private void dispatchAdditions(List<DiffUtil.PostponedUpdate> param1List, ListUpdateCallback param1ListUpdateCallback, int param1Int1, int param1Int2, int param1Int3) {
      if (!this.mDetectMoves) {
        param1ListUpdateCallback.onInserted(param1Int1, param1Int2);
        return;
      } 
      while (--param1Int2 >= 0) {
        StringBuilder stringBuilder;
        int[] arrayOfInt = this.mNewItemStatuses;
        int i = param1Int3 + param1Int2;
        int j = arrayOfInt[i] & 0x1F;
        if (j != 0) {
          if (j != 4 && j != 8) {
            if (j != 16) {
              stringBuilder = new StringBuilder();
              stringBuilder.append("unknown flag for pos ");
              stringBuilder.append(i);
              stringBuilder.append(" ");
              stringBuilder.append(Long.toBinaryString(j));
              throw new IllegalStateException(stringBuilder.toString());
            } 
            stringBuilder.add(new DiffUtil.PostponedUpdate(i, param1Int1, false));
          } else {
            int k = this.mNewItemStatuses[i] >> 5;
            param1ListUpdateCallback.onMoved((removePostponedUpdate(stringBuilder, k, true)).currentPos, param1Int1);
            if (j == 4)
              param1ListUpdateCallback.onChanged(param1Int1, 1, this.mCallback.getChangePayload(k, i)); 
          } 
        } else {
          param1ListUpdateCallback.onInserted(param1Int1, 1);
          for (DiffUtil.PostponedUpdate postponedUpdate : stringBuilder)
            postponedUpdate.currentPos++; 
        } 
        param1Int2--;
      } 
    }
    
    private void dispatchRemovals(List<DiffUtil.PostponedUpdate> param1List, ListUpdateCallback param1ListUpdateCallback, int param1Int1, int param1Int2, int param1Int3) {
      if (!this.mDetectMoves) {
        param1ListUpdateCallback.onRemoved(param1Int1, param1Int2);
        return;
      } 
      while (--param1Int2 >= 0) {
        StringBuilder stringBuilder;
        int[] arrayOfInt = this.mOldItemStatuses;
        int i = param1Int3 + param1Int2;
        int j = arrayOfInt[i] & 0x1F;
        if (j != 0) {
          if (j != 4 && j != 8) {
            if (j != 16) {
              stringBuilder = new StringBuilder();
              stringBuilder.append("unknown flag for pos ");
              stringBuilder.append(i);
              stringBuilder.append(" ");
              stringBuilder.append(Long.toBinaryString(j));
              throw new IllegalStateException(stringBuilder.toString());
            } 
            stringBuilder.add(new DiffUtil.PostponedUpdate(i, param1Int1 + param1Int2, true));
          } else {
            int k = this.mOldItemStatuses[i] >> 5;
            DiffUtil.PostponedUpdate postponedUpdate = removePostponedUpdate(stringBuilder, k, false);
            param1ListUpdateCallback.onMoved(param1Int1 + param1Int2, postponedUpdate.currentPos - 1);
            if (j == 4)
              param1ListUpdateCallback.onChanged(postponedUpdate.currentPos - 1, 1, this.mCallback.getChangePayload(i, k)); 
          } 
        } else {
          param1ListUpdateCallback.onRemoved(param1Int1 + param1Int2, 1);
          for (DiffUtil.PostponedUpdate postponedUpdate : stringBuilder)
            postponedUpdate.currentPos--; 
        } 
        param1Int2--;
      } 
    }
    
    private void findAddition(int param1Int1, int param1Int2, int param1Int3) {
      if (this.mOldItemStatuses[param1Int1 - 1] != 0)
        return; 
      findMatchingItem(param1Int1, param1Int2, param1Int3, false);
    }
    
    private boolean findMatchingItem(int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean) {
      int j;
      int i;
      if (param1Boolean) {
        i = param1Int2 - 1;
        param1Int2 = param1Int1;
        j = i;
      } else {
        int k = param1Int1 - 1;
        j = k;
        i = param1Int2;
        param1Int2 = k;
      } 
      while (param1Int3 >= 0) {
        int[] arrayOfInt = (DiffUtil.Snake)this.mSnakes.get(param1Int3);
        int m = arrayOfInt.x;
        int n = arrayOfInt.size;
        int i1 = arrayOfInt.y;
        int i2 = arrayOfInt.size;
        int k = 4;
        if (param1Boolean) {
          while (--param1Int2 >= m + n) {
            if (this.mCallback.areItemsTheSame(param1Int2, j)) {
              if (this.mCallback.areContentsTheSame(param1Int2, j))
                k = 8; 
              this.mNewItemStatuses[j] = param1Int2 << 5 | 0x10;
              this.mOldItemStatuses[param1Int2] = j << 5 | k;
              return true;
            } 
            param1Int2--;
          } 
        } else {
          for (param1Int2 = i - 1; param1Int2 >= i1 + i2; param1Int2--) {
            if (this.mCallback.areItemsTheSame(j, param1Int2)) {
              if (this.mCallback.areContentsTheSame(j, param1Int2))
                k = 8; 
              arrayOfInt = this.mOldItemStatuses;
              arrayOfInt[--param1Int1] = param1Int2 << 5 | 0x10;
              this.mNewItemStatuses[param1Int2] = param1Int1 << 5 | k;
              return true;
            } 
          } 
        } 
        param1Int2 = arrayOfInt.x;
        i = arrayOfInt.y;
        param1Int3--;
      } 
      return false;
    }
    
    private void findMatchingItems() {
      int j = this.mOldListSize;
      int i = this.mNewListSize;
      for (int k = this.mSnakes.size() - 1; k >= 0; k--) {
        DiffUtil.Snake snake = (DiffUtil.Snake)this.mSnakes.get(k);
        int i1 = snake.x;
        int i2 = snake.size;
        int m = snake.y;
        int n = snake.size;
        if (this.mDetectMoves) {
          int i3;
          while (true) {
            i3 = i;
            if (j > i1 + i2) {
              findAddition(j, i, k);
              j--;
              continue;
            } 
            break;
          } 
          while (i3 > m + n) {
            findRemoval(j, i3, k);
            i3--;
          } 
        } 
        for (i = 0; i < snake.size; i++) {
          int i3 = snake.x + i;
          m = snake.y + i;
          if (this.mCallback.areContentsTheSame(i3, m)) {
            j = 1;
          } else {
            j = 2;
          } 
          this.mOldItemStatuses[i3] = m << 5 | j;
          this.mNewItemStatuses[m] = i3 << 5 | j;
        } 
        j = snake.x;
        i = snake.y;
      } 
    }
    
    private void findRemoval(int param1Int1, int param1Int2, int param1Int3) {
      if (this.mNewItemStatuses[param1Int2 - 1] != 0)
        return; 
      findMatchingItem(param1Int1, param1Int2, param1Int3, true);
    }
    
    private static DiffUtil.PostponedUpdate removePostponedUpdate(List<DiffUtil.PostponedUpdate> param1List, int param1Int, boolean param1Boolean) {
      for (int i = param1List.size() - 1; i >= 0; i--) {
        DiffUtil.PostponedUpdate postponedUpdate = (DiffUtil.PostponedUpdate)param1List.get(i);
        if (postponedUpdate.posInOwnerList == param1Int && postponedUpdate.removal == param1Boolean) {
          param1List.remove(i);
          while (i < param1List.size()) {
            DiffUtil.PostponedUpdate postponedUpdate1 = (DiffUtil.PostponedUpdate)param1List.get(i);
            int j = postponedUpdate1.currentPos;
            if (param1Boolean) {
              param1Int = 1;
            } else {
              param1Int = -1;
            } 
            postponedUpdate1.currentPos = j + param1Int;
            i++;
          } 
          return postponedUpdate;
        } 
      } 
      return null;
    }
    
    public void dispatchUpdatesTo(ListUpdateCallback param1ListUpdateCallback) {
      if (param1ListUpdateCallback instanceof BatchingListUpdateCallback) {
        param1ListUpdateCallback = (BatchingListUpdateCallback)param1ListUpdateCallback;
      } else {
        param1ListUpdateCallback = new BatchingListUpdateCallback(param1ListUpdateCallback);
      } 
      ArrayList arrayList = new ArrayList();
      int j = this.mOldListSize;
      int k = this.mNewListSize;
      for (int i = this.mSnakes.size(); --i >= 0; i--) {
        DiffUtil.Snake snake = (DiffUtil.Snake)this.mSnakes.get(i);
        int m = snake.size;
        int n = snake.x + m;
        int i1 = snake.y + m;
        if (n < j)
          dispatchRemovals(arrayList, param1ListUpdateCallback, n, j - n, n); 
        if (i1 < k)
          dispatchAdditions(arrayList, param1ListUpdateCallback, n, k - i1, i1); 
        for (j = m - 1; j >= 0; j--) {
          if ((this.mOldItemStatuses[snake.x + j] & 0x1F) == 2)
            param1ListUpdateCallback.onChanged(snake.x + j, 1, this.mCallback.getChangePayload(snake.x + j, snake.y + j)); 
        } 
        j = snake.x;
        k = snake.y;
      } 
      param1ListUpdateCallback.dispatchLastEvent();
    }
    
    public void dispatchUpdatesTo(RecyclerView.Adapter param1Adapter) { dispatchUpdatesTo(new AdapterListUpdateCallback(param1Adapter)); }
    
    @VisibleForTesting
    List<DiffUtil.Snake> getSnakes() { return this.mSnakes; }
  }
  
  public static abstract class ItemCallback<T> extends Object {
    public abstract boolean areContentsTheSame(T param1T1, T param1T2);
    
    public abstract boolean areItemsTheSame(T param1T1, T param1T2);
    
    public Object getChangePayload(T param1T1, T param1T2) { return null; }
  }
  
  private static class PostponedUpdate {
    int currentPos;
    
    int posInOwnerList;
    
    boolean removal;
    
    public PostponedUpdate(int param1Int1, int param1Int2, boolean param1Boolean) {
      this.posInOwnerList = param1Int1;
      this.currentPos = param1Int2;
      this.removal = param1Boolean;
    }
  }
  
  static class Range {
    int newListEnd;
    
    int newListStart;
    
    int oldListEnd;
    
    int oldListStart;
    
    public Range() {}
    
    public Range(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.oldListStart = param1Int1;
      this.oldListEnd = param1Int2;
      this.newListStart = param1Int3;
      this.newListEnd = param1Int4;
    }
  }
  
  static class Snake {
    boolean removal;
    
    boolean reverse;
    
    int size;
    
    int x;
    
    int y;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/util/DiffUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */