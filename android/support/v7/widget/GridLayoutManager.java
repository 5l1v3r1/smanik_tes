package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.Arrays;

public class GridLayoutManager extends LinearLayoutManager {
  private static final boolean DEBUG = false;
  
  public static final int DEFAULT_SPAN_COUNT = -1;
  
  private static final String TAG = "GridLayoutManager";
  
  int[] mCachedBorders;
  
  final Rect mDecorInsets = new Rect();
  
  boolean mPendingSpanCountChange = false;
  
  final SparseIntArray mPreLayoutSpanIndexCache = new SparseIntArray();
  
  final SparseIntArray mPreLayoutSpanSizeCache = new SparseIntArray();
  
  View[] mSet;
  
  int mSpanCount = -1;
  
  SpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();
  
  public GridLayoutManager(Context paramContext, int paramInt) {
    super(paramContext);
    setSpanCount(paramInt);
  }
  
  public GridLayoutManager(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean) {
    super(paramContext, paramInt2, paramBoolean);
    setSpanCount(paramInt1);
  }
  
  public GridLayoutManager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setSpanCount((getProperties(paramContext, paramAttributeSet, paramInt1, paramInt2)).spanCount);
  }
  
  private void assignSpans(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i;
    paramInt2 = -1;
    int j = 0;
    if (paramBoolean) {
      paramInt2 = paramInt1;
      paramInt1 = 0;
      i = 1;
    } else {
      paramInt1--;
      i = -1;
    } 
    while (paramInt1 != paramInt2) {
      View view = this.mSet[paramInt1];
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      layoutParams.mSpanSize = getSpanSize(paramRecycler, paramState, getPosition(view));
      layoutParams.mSpanIndex = j;
      j += layoutParams.mSpanSize;
      paramInt1 += i;
    } 
  }
  
  private void cachePreLayoutSpanMapping() {
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      LayoutParams layoutParams = (LayoutParams)getChildAt(b).getLayoutParams();
      int j = layoutParams.getViewLayoutPosition();
      this.mPreLayoutSpanSizeCache.put(j, layoutParams.getSpanSize());
      this.mPreLayoutSpanIndexCache.put(j, layoutParams.getSpanIndex());
    } 
  }
  
  private void calculateItemBorders(int paramInt) { this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, paramInt); }
  
  static int[] calculateItemBorders(int[] paramArrayOfInt, int paramInt1, int paramInt2) { // Byte code:
    //   0: iconst_1
    //   1: istore #4
    //   3: aload_0
    //   4: ifnull -> 28
    //   7: aload_0
    //   8: arraylength
    //   9: iload_1
    //   10: iconst_1
    //   11: iadd
    //   12: if_icmpne -> 28
    //   15: aload_0
    //   16: astore #8
    //   18: aload_0
    //   19: aload_0
    //   20: arraylength
    //   21: iconst_1
    //   22: isub
    //   23: iaload
    //   24: iload_2
    //   25: if_icmpeq -> 35
    //   28: iload_1
    //   29: iconst_1
    //   30: iadd
    //   31: newarray int
    //   33: astore #8
    //   35: iconst_0
    //   36: istore #5
    //   38: aload #8
    //   40: iconst_0
    //   41: iconst_0
    //   42: iastore
    //   43: iload_2
    //   44: iload_1
    //   45: idiv
    //   46: istore #6
    //   48: iload_2
    //   49: iload_1
    //   50: irem
    //   51: istore #7
    //   53: iconst_0
    //   54: istore_3
    //   55: iload #5
    //   57: istore_2
    //   58: iload #4
    //   60: iload_1
    //   61: if_icmpgt -> 118
    //   64: iload_2
    //   65: iload #7
    //   67: iadd
    //   68: istore_2
    //   69: iload_2
    //   70: ifle -> 94
    //   73: iload_1
    //   74: iload_2
    //   75: isub
    //   76: iload #7
    //   78: if_icmpge -> 94
    //   81: iload #6
    //   83: iconst_1
    //   84: iadd
    //   85: istore #5
    //   87: iload_2
    //   88: iload_1
    //   89: isub
    //   90: istore_2
    //   91: goto -> 98
    //   94: iload #6
    //   96: istore #5
    //   98: iload_3
    //   99: iload #5
    //   101: iadd
    //   102: istore_3
    //   103: aload #8
    //   105: iload #4
    //   107: iload_3
    //   108: iastore
    //   109: iload #4
    //   111: iconst_1
    //   112: iadd
    //   113: istore #4
    //   115: goto -> 58
    //   118: aload #8
    //   120: areturn }
  
  private void clearPreLayoutSpanMappingCache() {
    this.mPreLayoutSpanSizeCache.clear();
    this.mPreLayoutSpanIndexCache.clear();
  }
  
  private void ensureAnchorIsInCorrectSpan(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.AnchorInfo paramAnchorInfo, int paramInt) {
    if (paramInt == 1) {
      paramInt = 1;
    } else {
      paramInt = 0;
    } 
    int i = getSpanIndex(paramRecycler, paramState, paramAnchorInfo.mPosition);
    if (paramInt != 0) {
      while (i > 0 && paramAnchorInfo.mPosition > 0) {
        paramAnchorInfo.mPosition--;
        i = getSpanIndex(paramRecycler, paramState, paramAnchorInfo.mPosition);
      } 
    } else {
      int j = paramState.getItemCount();
      paramInt = paramAnchorInfo.mPosition;
      while (paramInt < j - 1) {
        int m = paramInt + 1;
        int k = getSpanIndex(paramRecycler, paramState, m);
        if (k > i) {
          paramInt = m;
          i = k;
        } 
      } 
      paramAnchorInfo.mPosition = paramInt;
    } 
  }
  
  private void ensureViewSet() {
    if (this.mSet == null || this.mSet.length != this.mSpanCount)
      this.mSet = new View[this.mSpanCount]; 
  }
  
  private int getSpanGroupIndex(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt) {
    if (!paramState.isPreLayout())
      return this.mSpanSizeLookup.getSpanGroupIndex(paramInt, this.mSpanCount); 
    int i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (i == -1) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Cannot find span size for pre layout position. ");
      stringBuilder.append(paramInt);
      Log.w("GridLayoutManager", stringBuilder.toString());
      return 0;
    } 
    return this.mSpanSizeLookup.getSpanGroupIndex(i, this.mSpanCount);
  }
  
  private int getSpanIndex(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt) {
    if (!paramState.isPreLayout())
      return this.mSpanSizeLookup.getCachedSpanIndex(paramInt, this.mSpanCount); 
    int i = this.mPreLayoutSpanIndexCache.get(paramInt, -1);
    if (i != -1)
      return i; 
    i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (i == -1) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
      stringBuilder.append(paramInt);
      Log.w("GridLayoutManager", stringBuilder.toString());
      return 0;
    } 
    return this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
  }
  
  private int getSpanSize(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt) {
    if (!paramState.isPreLayout())
      return this.mSpanSizeLookup.getSpanSize(paramInt); 
    int i = this.mPreLayoutSpanSizeCache.get(paramInt, -1);
    if (i != -1)
      return i; 
    i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (i == -1) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
      stringBuilder.append(paramInt);
      Log.w("GridLayoutManager", stringBuilder.toString());
      return 1;
    } 
    return this.mSpanSizeLookup.getSpanSize(i);
  }
  
  private void guessMeasurement(float paramFloat, int paramInt) { calculateItemBorders(Math.max(Math.round(paramFloat * this.mSpanCount), paramInt)); }
  
  private void measureChild(View paramView, int paramInt, boolean paramBoolean) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect rect = layoutParams.mDecorInsets;
    int j = rect.top + rect.bottom + layoutParams.topMargin + layoutParams.bottomMargin;
    int i = rect.left + rect.right + layoutParams.leftMargin + layoutParams.rightMargin;
    int k = getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
    if (this.mOrientation == 1) {
      i = getChildMeasureSpec(k, paramInt, i, layoutParams.width, false);
      paramInt = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), j, layoutParams.height, true);
    } else {
      paramInt = getChildMeasureSpec(k, paramInt, j, layoutParams.height, false);
      i = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getWidthMode(), i, layoutParams.width, true);
    } 
    measureChildWithDecorationsAndMargin(paramView, i, paramInt, paramBoolean);
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, int paramInt1, int paramInt2, boolean paramBoolean) {
    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
    if (paramBoolean) {
      paramBoolean = shouldReMeasureChild(paramView, paramInt1, paramInt2, layoutParams);
    } else {
      paramBoolean = shouldMeasureChild(paramView, paramInt1, paramInt2, layoutParams);
    } 
    if (paramBoolean)
      paramView.measure(paramInt1, paramInt2); 
  }
  
  private void updateMeasurements() {
    int i;
    if (getOrientation() == 1) {
      i = getWidth() - getPaddingRight() - getPaddingLeft();
    } else {
      i = getHeight() - getPaddingBottom() - getPaddingTop();
    } 
    calculateItemBorders(i);
  }
  
  public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams) { return paramLayoutParams instanceof LayoutParams; }
  
  void collectPrefetchPositionsForLayoutState(RecyclerView.State paramState, LinearLayoutManager.LayoutState paramLayoutState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry) {
    int i = this.mSpanCount;
    byte b;
    for (b = 0; b < this.mSpanCount && paramLayoutState.hasMore(paramState) && i > 0; b++) {
      int j = paramLayoutState.mCurrentPosition;
      paramLayoutPrefetchRegistry.addPosition(j, Math.max(0, paramLayoutState.mScrollingOffset));
      i -= this.mSpanSizeLookup.getSpanSize(j);
      paramLayoutState.mCurrentPosition += paramLayoutState.mItemDirection;
    } 
  }
  
  View findReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2, int paramInt3) {
    int i;
    ensureLayoutState();
    int j = this.mOrientationHelper.getStartAfterPadding();
    int k = this.mOrientationHelper.getEndAfterPadding();
    if (paramInt2 > paramInt1) {
      i = 1;
    } else {
      i = -1;
    } 
    View view2 = null;
    View view1;
    for (view1 = null; paramInt1 != paramInt2; view1 = view4) {
      View view5 = getChildAt(paramInt1);
      int m = getPosition(view5);
      View view3 = view2;
      View view4 = view1;
      if (m >= 0) {
        view3 = view2;
        view4 = view1;
        if (m < paramInt3)
          if (getSpanIndex(paramRecycler, paramState, m) != 0) {
            view3 = view2;
            view4 = view1;
          } else if (((RecyclerView.LayoutParams)view5.getLayoutParams()).isItemRemoved()) {
            view3 = view2;
            view4 = view1;
            if (view1 == null) {
              view4 = view5;
              view3 = view2;
            } 
          } else if (this.mOrientationHelper.getDecoratedStart(view5) >= k || this.mOrientationHelper.getDecoratedEnd(view5) < j) {
            view3 = view2;
            view4 = view1;
            if (view2 == null) {
              view3 = view5;
              view4 = view1;
            } 
          } else {
            return view5;
          }  
      } 
      paramInt1 += i;
      view2 = view3;
    } 
    return (view2 != null) ? view2 : view1;
  }
  
  public RecyclerView.LayoutParams generateDefaultLayoutParams() { return (this.mOrientation == 0) ? new LayoutParams(-2, -1) : new LayoutParams(-1, -2); }
  
  public RecyclerView.LayoutParams generateLayoutParams(Context paramContext, AttributeSet paramAttributeSet) { return new LayoutParams(paramContext, paramAttributeSet); }
  
  public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams instanceof ViewGroup.MarginLayoutParams) ? new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams) : new LayoutParams(paramLayoutParams); }
  
  public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return (this.mOrientation == 1) ? this.mSpanCount : ((paramState.getItemCount() < 1) ? 0 : (getSpanGroupIndex(paramRecycler, paramState, paramState.getItemCount() - 1) + 1)); }
  
  public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return (this.mOrientation == 0) ? this.mSpanCount : ((paramState.getItemCount() < 1) ? 0 : (getSpanGroupIndex(paramRecycler, paramState, paramState.getItemCount() - 1) + 1)); }
  
  int getSpaceForSpanRange(int paramInt1, int paramInt2) { return (this.mOrientation == 1 && isLayoutRTL()) ? (this.mCachedBorders[this.mSpanCount - paramInt1] - this.mCachedBorders[this.mSpanCount - paramInt1 - paramInt2]) : (this.mCachedBorders[paramInt2 + paramInt1] - this.mCachedBorders[paramInt1]); }
  
  public int getSpanCount() { return this.mSpanCount; }
  
  public SpanSizeLookup getSpanSizeLookup() { return this.mSpanSizeLookup; }
  
  void layoutChunk(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.LayoutState paramLayoutState, LinearLayoutManager.LayoutChunkResult paramLayoutChunkResult) {
    boolean bool;
    int m;
    int k;
    StringBuilder stringBuilder;
    int i1 = this.mOrientationHelper.getModeInOther();
    if (i1 != 1073741824) {
      k = 1;
    } else {
      k = 0;
    } 
    if (getChildCount() > 0) {
      m = this.mCachedBorders[this.mSpanCount];
    } else {
      m = 0;
    } 
    if (k)
      updateMeasurements(); 
    if (paramLayoutState.mItemDirection == 1) {
      bool = true;
    } else {
      bool = false;
    } 
    int i = this.mSpanCount;
    if (!bool)
      i = getSpanIndex(paramRecycler, paramState, paramLayoutState.mCurrentPosition) + getSpanSize(paramRecycler, paramState, paramLayoutState.mCurrentPosition); 
    int j = 0;
    byte b;
    for (b = 0; b < this.mSpanCount && paramLayoutState.hasMore(paramState) && i > 0; b++) {
      int i3 = paramLayoutState.mCurrentPosition;
      int i2 = getSpanSize(paramRecycler, paramState, i3);
      if (i2 > this.mSpanCount) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("Item at position ");
        stringBuilder.append(i3);
        stringBuilder.append(" requires ");
        stringBuilder.append(i2);
        stringBuilder.append(" spans but GridLayoutManager has only ");
        stringBuilder.append(this.mSpanCount);
        stringBuilder.append(" spans.");
        throw new IllegalArgumentException(stringBuilder.toString());
      } 
      i -= i2;
      if (i < 0)
        break; 
      View view = paramLayoutState.next(stringBuilder);
      if (view == null)
        break; 
      j += i2;
      this.mSet[b] = view;
    } 
    if (b == 0) {
      paramLayoutChunkResult.mFinished = true;
      return;
    } 
    float f = 0.0F;
    assignSpans(stringBuilder, paramState, b, j, bool);
    j = 0;
    i = 0;
    while (j < b) {
      View view = this.mSet[j];
      if (paramLayoutState.mScrapList == null) {
        if (bool) {
          addView(view);
        } else {
          addView(view, 0);
        } 
      } else if (bool) {
        addDisappearingView(view);
      } else {
        addDisappearingView(view, 0);
      } 
      calculateItemDecorationsForChild(view, this.mDecorInsets);
      measureChild(view, i1, false);
      int i3 = this.mOrientationHelper.getDecoratedMeasurement(view);
      int i2 = i;
      if (i3 > i)
        i2 = i3; 
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      float f2 = this.mOrientationHelper.getDecoratedMeasurementInOther(view) * 1.0F / layoutParams.mSpanSize;
      float f1 = f;
      if (f2 > f)
        f1 = f2; 
      j++;
      i = i2;
      f = f1;
    } 
    j = i;
    if (k) {
      guessMeasurement(f, m);
      k = 0;
      i = 0;
      while (true) {
        j = i;
        if (k < b) {
          View view = this.mSet[k];
          measureChild(view, 1073741824, true);
          m = this.mOrientationHelper.getDecoratedMeasurement(view);
          j = i;
          if (m > i)
            j = m; 
          k++;
          i = j;
          continue;
        } 
        break;
      } 
    } 
    for (i = 0; i < b; i++) {
      View view = this.mSet[i];
      if (this.mOrientationHelper.getDecoratedMeasurement(view) != j) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        Rect rect = layoutParams.mDecorInsets;
        m = rect.top + rect.bottom + layoutParams.topMargin + layoutParams.bottomMargin;
        k = rect.left + rect.right + layoutParams.leftMargin + layoutParams.rightMargin;
        int i2 = getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
        if (this.mOrientation == 1) {
          k = getChildMeasureSpec(i2, 1073741824, k, layoutParams.width, false);
          m = View.MeasureSpec.makeMeasureSpec(j - m, 1073741824);
        } else {
          k = View.MeasureSpec.makeMeasureSpec(j - k, 1073741824);
          m = getChildMeasureSpec(i2, 1073741824, m, layoutParams.height, false);
        } 
        measureChildWithDecorationsAndMargin(view, k, m, true);
      } 
    } 
    int n = 0;
    paramLayoutChunkResult.mConsumed = j;
    if (this.mOrientation == 1) {
      if (paramLayoutState.mLayoutDirection == -1) {
        k = paramLayoutState.mOffset;
        i = k;
        k -= j;
        j = i;
        i = k;
      } else {
        k = paramLayoutState.mOffset;
        i = k;
        j += k;
      } 
      k = 0;
      m = 0;
    } else if (paramLayoutState.mLayoutDirection == -1) {
      k = paramLayoutState.mOffset;
      i = 0;
      int i2 = 0;
      m = k;
      k -= j;
      j = i2;
    } else {
      k = paramLayoutState.mOffset;
      m = j + k;
      i = 0;
      j = 0;
    } 
    while (n < b) {
      View view = this.mSet[n];
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      if (this.mOrientation == 1) {
        if (isLayoutRTL()) {
          m = getPaddingLeft() + this.mCachedBorders[this.mSpanCount - layoutParams.mSpanIndex];
          int i3 = this.mOrientationHelper.getDecoratedMeasurementInOther(view);
          k = m;
          m -= i3;
        } else {
          k = getPaddingLeft() + this.mCachedBorders[layoutParams.mSpanIndex];
          m = this.mOrientationHelper.getDecoratedMeasurementInOther(view) + k;
          int i3 = k;
          k = m;
          m = i3;
        } 
      } else {
        i = getPaddingTop() + this.mCachedBorders[layoutParams.mSpanIndex];
        j = this.mOrientationHelper.getDecoratedMeasurementInOther(view) + i;
        int i3 = k;
        k = m;
        m = i3;
      } 
      layoutDecoratedWithMargins(view, m, i, k, j);
      if (layoutParams.isItemRemoved() || layoutParams.isItemChanged())
        paramLayoutChunkResult.mIgnoreConsumed = true; 
      paramLayoutChunkResult.mFocusable |= view.hasFocusable();
      int i2 = n + true;
      n = k;
      k = m;
      m = n;
      n = i2;
    } 
    Arrays.fill(this.mSet, null);
  }
  
  void onAnchorReady(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.AnchorInfo paramAnchorInfo, int paramInt) {
    super.onAnchorReady(paramRecycler, paramState, paramAnchorInfo, paramInt);
    updateMeasurements();
    if (paramState.getItemCount() > 0 && !paramState.isPreLayout())
      ensureAnchorIsInCorrectSpan(paramRecycler, paramState, paramAnchorInfo, paramInt); 
    ensureViewSet();
  }
  
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokevirtual findContainingItemView : (Landroid/view/View;)Landroid/view/View;
    //   5: astore #22
    //   7: aconst_null
    //   8: astore #21
    //   10: aload #22
    //   12: ifnonnull -> 17
    //   15: aconst_null
    //   16: areturn
    //   17: aload #22
    //   19: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   22: checkcast android/support/v7/widget/GridLayoutManager$LayoutParams
    //   25: astore #23
    //   27: aload #23
    //   29: getfield mSpanIndex : I
    //   32: istore #14
    //   34: aload #23
    //   36: getfield mSpanIndex : I
    //   39: aload #23
    //   41: getfield mSpanSize : I
    //   44: iadd
    //   45: istore #15
    //   47: aload_0
    //   48: aload_1
    //   49: iload_2
    //   50: aload_3
    //   51: aload #4
    //   53: invokespecial onFocusSearchFailed : (Landroid/view/View;ILandroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/RecyclerView$State;)Landroid/view/View;
    //   56: ifnonnull -> 61
    //   59: aconst_null
    //   60: areturn
    //   61: aload_0
    //   62: iload_2
    //   63: invokevirtual convertFocusDirectionToLayoutDirection : (I)I
    //   66: iconst_1
    //   67: if_icmpne -> 76
    //   70: iconst_1
    //   71: istore #20
    //   73: goto -> 79
    //   76: iconst_0
    //   77: istore #20
    //   79: iload #20
    //   81: aload_0
    //   82: getfield mShouldReverseLayout : Z
    //   85: if_icmpeq -> 93
    //   88: iconst_1
    //   89: istore_2
    //   90: goto -> 95
    //   93: iconst_0
    //   94: istore_2
    //   95: iload_2
    //   96: ifeq -> 116
    //   99: aload_0
    //   100: invokevirtual getChildCount : ()I
    //   103: iconst_1
    //   104: isub
    //   105: istore #7
    //   107: iconst_m1
    //   108: istore #5
    //   110: iconst_m1
    //   111: istore #8
    //   113: goto -> 128
    //   116: aload_0
    //   117: invokevirtual getChildCount : ()I
    //   120: istore #5
    //   122: iconst_0
    //   123: istore #7
    //   125: iconst_1
    //   126: istore #8
    //   128: aload_0
    //   129: getfield mOrientation : I
    //   132: iconst_1
    //   133: if_icmpne -> 149
    //   136: aload_0
    //   137: invokevirtual isLayoutRTL : ()Z
    //   140: ifeq -> 149
    //   143: iconst_1
    //   144: istore #9
    //   146: goto -> 152
    //   149: iconst_0
    //   150: istore #9
    //   152: aload_0
    //   153: aload_3
    //   154: aload #4
    //   156: iload #7
    //   158: invokespecial getSpanGroupIndex : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/RecyclerView$State;I)I
    //   161: istore #16
    //   163: aconst_null
    //   164: astore_1
    //   165: iconst_m1
    //   166: istore #12
    //   168: iconst_0
    //   169: istore #11
    //   171: iconst_0
    //   172: istore_2
    //   173: iconst_m1
    //   174: istore #6
    //   176: iload #5
    //   178: istore #10
    //   180: iload #12
    //   182: istore #5
    //   184: iload #7
    //   186: iload #10
    //   188: if_icmpeq -> 555
    //   191: aload_0
    //   192: aload_3
    //   193: aload #4
    //   195: iload #7
    //   197: invokespecial getSpanGroupIndex : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/RecyclerView$State;I)I
    //   200: istore #12
    //   202: aload_0
    //   203: iload #7
    //   205: invokevirtual getChildAt : (I)Landroid/view/View;
    //   208: astore #23
    //   210: aload #23
    //   212: aload #22
    //   214: if_acmpne -> 220
    //   217: goto -> 555
    //   220: aload #23
    //   222: invokevirtual hasFocusable : ()Z
    //   225: ifeq -> 246
    //   228: iload #12
    //   230: iload #16
    //   232: if_icmpeq -> 246
    //   235: aload #21
    //   237: ifnull -> 243
    //   240: goto -> 555
    //   243: goto -> 545
    //   246: aload #23
    //   248: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   251: checkcast android/support/v7/widget/GridLayoutManager$LayoutParams
    //   254: astore #24
    //   256: aload #24
    //   258: getfield mSpanIndex : I
    //   261: istore #17
    //   263: aload #24
    //   265: getfield mSpanIndex : I
    //   268: aload #24
    //   270: getfield mSpanSize : I
    //   273: iadd
    //   274: istore #18
    //   276: aload #23
    //   278: invokevirtual hasFocusable : ()Z
    //   281: ifeq -> 301
    //   284: iload #17
    //   286: iload #14
    //   288: if_icmpne -> 301
    //   291: iload #18
    //   293: iload #15
    //   295: if_icmpne -> 301
    //   298: aload #23
    //   300: areturn
    //   301: aload #23
    //   303: invokevirtual hasFocusable : ()Z
    //   306: ifeq -> 314
    //   309: aload #21
    //   311: ifnull -> 326
    //   314: aload #23
    //   316: invokevirtual hasFocusable : ()Z
    //   319: ifne -> 332
    //   322: aload_1
    //   323: ifnonnull -> 332
    //   326: iconst_1
    //   327: istore #12
    //   329: goto -> 466
    //   332: iload #17
    //   334: iload #14
    //   336: invokestatic max : (II)I
    //   339: istore #12
    //   341: iload #18
    //   343: iload #15
    //   345: invokestatic min : (II)I
    //   348: iload #12
    //   350: isub
    //   351: istore #19
    //   353: aload #23
    //   355: invokevirtual hasFocusable : ()Z
    //   358: ifeq -> 404
    //   361: iload #19
    //   363: iload #11
    //   365: if_icmple -> 371
    //   368: goto -> 326
    //   371: iload #19
    //   373: iload #11
    //   375: if_icmpne -> 463
    //   378: iload #17
    //   380: iload #5
    //   382: if_icmple -> 391
    //   385: iconst_1
    //   386: istore #12
    //   388: goto -> 394
    //   391: iconst_0
    //   392: istore #12
    //   394: iload #9
    //   396: iload #12
    //   398: if_icmpne -> 463
    //   401: goto -> 326
    //   404: aload #21
    //   406: ifnonnull -> 463
    //   409: iconst_0
    //   410: istore #12
    //   412: aload_0
    //   413: aload #23
    //   415: iconst_0
    //   416: iconst_1
    //   417: invokevirtual isViewPartiallyVisible : (Landroid/view/View;ZZ)Z
    //   420: ifeq -> 463
    //   423: iload_2
    //   424: istore #13
    //   426: iload #19
    //   428: iload #13
    //   430: if_icmple -> 436
    //   433: goto -> 326
    //   436: iload #19
    //   438: iload #13
    //   440: if_icmpne -> 463
    //   443: iload #17
    //   445: iload #6
    //   447: if_icmple -> 453
    //   450: iconst_1
    //   451: istore #12
    //   453: iload #9
    //   455: iload #12
    //   457: if_icmpne -> 463
    //   460: goto -> 326
    //   463: iconst_0
    //   464: istore #12
    //   466: iload #12
    //   468: ifeq -> 545
    //   471: aload #23
    //   473: invokevirtual hasFocusable : ()Z
    //   476: ifeq -> 510
    //   479: aload #24
    //   481: getfield mSpanIndex : I
    //   484: istore #5
    //   486: iload #18
    //   488: iload #15
    //   490: invokestatic min : (II)I
    //   493: iload #17
    //   495: iload #14
    //   497: invokestatic max : (II)I
    //   500: isub
    //   501: istore #11
    //   503: aload #23
    //   505: astore #21
    //   507: goto -> 545
    //   510: aload #24
    //   512: getfield mSpanIndex : I
    //   515: istore #6
    //   517: iload #18
    //   519: iload #15
    //   521: invokestatic min : (II)I
    //   524: istore_2
    //   525: iload #17
    //   527: iload #14
    //   529: invokestatic max : (II)I
    //   532: istore #12
    //   534: aload #23
    //   536: astore_1
    //   537: iload_2
    //   538: iload #12
    //   540: isub
    //   541: istore_2
    //   542: goto -> 545
    //   545: iload #7
    //   547: iload #8
    //   549: iadd
    //   550: istore #7
    //   552: goto -> 184
    //   555: aload #21
    //   557: ifnull -> 563
    //   560: aload #21
    //   562: astore_1
    //   563: aload_1
    //   564: areturn }
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat) {
    boolean bool;
    ViewGroup.LayoutParams layoutParams1 = paramView.getLayoutParams();
    if (!(layoutParams1 instanceof LayoutParams)) {
      onInitializeAccessibilityNodeInfoForItem(paramView, paramAccessibilityNodeInfoCompat);
      return;
    } 
    LayoutParams layoutParams = (LayoutParams)layoutParams1;
    int i = getSpanGroupIndex(paramRecycler, paramState, layoutParams.getViewLayoutPosition());
    if (this.mOrientation == 0) {
      int m = layoutParams.getSpanIndex();
      int n = layoutParams.getSpanSize();
      if (this.mSpanCount > 1 && layoutParams.getSpanSize() == this.mSpanCount) {
        bool = true;
      } else {
        bool = false;
      } 
      paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(m, n, i, 1, bool, false));
      return;
    } 
    int j = layoutParams.getSpanIndex();
    int k = layoutParams.getSpanSize();
    if (this.mSpanCount > 1 && layoutParams.getSpanSize() == this.mSpanCount) {
      bool = true;
    } else {
      bool = false;
    } 
    paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(i, 1, j, k, bool, false));
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) { this.mSpanSizeLookup.invalidateSpanIndexCache(); }
  
  public void onItemsChanged(RecyclerView paramRecyclerView) { this.mSpanSizeLookup.invalidateSpanIndexCache(); }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3) { this.mSpanSizeLookup.invalidateSpanIndexCache(); }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) { this.mSpanSizeLookup.invalidateSpanIndexCache(); }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject) { this.mSpanSizeLookup.invalidateSpanIndexCache(); }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) {
    if (paramState.isPreLayout())
      cachePreLayoutSpanMapping(); 
    super.onLayoutChildren(paramRecycler, paramState);
    clearPreLayoutSpanMappingCache();
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState) {
    super.onLayoutCompleted(paramState);
    this.mPendingSpanCountChange = false;
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) {
    updateMeasurements();
    ensureViewSet();
    return super.scrollHorizontallyBy(paramInt, paramRecycler, paramState);
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) {
    updateMeasurements();
    ensureViewSet();
    return super.scrollVerticallyBy(paramInt, paramRecycler, paramState);
  }
  
  public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2) {
    if (this.mCachedBorders == null)
      super.setMeasuredDimension(paramRect, paramInt1, paramInt2); 
    int i = getPaddingLeft() + getPaddingRight();
    int j = getPaddingTop() + getPaddingBottom();
    if (this.mOrientation == 1) {
      paramInt2 = chooseSize(paramInt2, paramRect.height() + j, getMinimumHeight());
      i = chooseSize(paramInt1, this.mCachedBorders[this.mCachedBorders.length - 1] + i, getMinimumWidth());
      paramInt1 = paramInt2;
      paramInt2 = i;
    } else {
      paramInt1 = chooseSize(paramInt1, paramRect.width() + i, getMinimumWidth());
      i = chooseSize(paramInt2, this.mCachedBorders[this.mCachedBorders.length - 1] + j, getMinimumHeight());
      paramInt2 = paramInt1;
      paramInt1 = i;
    } 
    setMeasuredDimension(paramInt2, paramInt1);
  }
  
  public void setSpanCount(int paramInt) {
    if (paramInt == this.mSpanCount)
      return; 
    this.mPendingSpanCountChange = true;
    if (paramInt < 1) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Span count should be at least 1. Provided ");
      stringBuilder.append(paramInt);
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    this.mSpanCount = paramInt;
    this.mSpanSizeLookup.invalidateSpanIndexCache();
    requestLayout();
  }
  
  public void setSpanSizeLookup(SpanSizeLookup paramSpanSizeLookup) { this.mSpanSizeLookup = paramSpanSizeLookup; }
  
  public void setStackFromEnd(boolean paramBoolean) {
    if (paramBoolean)
      throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout"); 
    super.setStackFromEnd(false);
  }
  
  public boolean supportsPredictiveItemAnimations() { return (this.mPendingSavedState == null && !this.mPendingSpanCountChange); }
  
  public static final class DefaultSpanSizeLookup extends SpanSizeLookup {
    public int getSpanIndex(int param1Int1, int param1Int2) { return param1Int1 % param1Int2; }
    
    public int getSpanSize(int param1Int) { return 1; }
  }
  
  public static class LayoutParams extends RecyclerView.LayoutParams {
    public static final int INVALID_SPAN_ID = -1;
    
    int mSpanIndex = -1;
    
    int mSpanSize = 0;
    
    public LayoutParams(int param1Int1, int param1Int2) { super(param1Int1, param1Int2); }
    
    public LayoutParams(Context param1Context, AttributeSet param1AttributeSet) { super(param1Context, param1AttributeSet); }
    
    public LayoutParams(RecyclerView.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.MarginLayoutParams param1MarginLayoutParams) { super(param1MarginLayoutParams); }
    
    public int getSpanIndex() { return this.mSpanIndex; }
    
    public int getSpanSize() { return this.mSpanSize; }
  }
  
  public static abstract class SpanSizeLookup {
    private boolean mCacheSpanIndices = false;
    
    final SparseIntArray mSpanIndexCache = new SparseIntArray();
    
    int findReferenceIndexFromCache(int param1Int) {
      int j = this.mSpanIndexCache.size() - 1;
      int i = 0;
      while (i <= j) {
        int k = i + j >>> 1;
        if (this.mSpanIndexCache.keyAt(k) < param1Int) {
          i = k + 1;
          continue;
        } 
        j = k - 1;
      } 
      param1Int = i - 1;
      return (param1Int >= 0 && param1Int < this.mSpanIndexCache.size()) ? this.mSpanIndexCache.keyAt(param1Int) : -1;
    }
    
    int getCachedSpanIndex(int param1Int1, int param1Int2) {
      if (!this.mCacheSpanIndices)
        return getSpanIndex(param1Int1, param1Int2); 
      int i = this.mSpanIndexCache.get(param1Int1, -1);
      if (i != -1)
        return i; 
      param1Int2 = getSpanIndex(param1Int1, param1Int2);
      this.mSpanIndexCache.put(param1Int1, param1Int2);
      return param1Int2;
    }
    
    public int getSpanGroupIndex(int param1Int1, int param1Int2) {
      int k = getSpanSize(param1Int1);
      byte b = 0;
      int i = 0;
      int j;
      for (j = 0; b < param1Int1; j = b1) {
        byte b1;
        int m = getSpanSize(b);
        int n = i + m;
        if (n == param1Int2) {
          b1 = j + true;
          i = 0;
        } else {
          i = n;
          b1 = j;
          if (n > param1Int2) {
            b1 = j + true;
            i = m;
          } 
        } 
        b++;
      } 
      param1Int1 = j;
      if (i + k > param1Int2)
        param1Int1 = j + 1; 
      return param1Int1;
    }
    
    public int getSpanIndex(int param1Int1, int param1Int2) { // Byte code:
      //   0: aload_0
      //   1: iload_1
      //   2: invokevirtual getSpanSize : (I)I
      //   5: istore #7
      //   7: iload #7
      //   9: iload_2
      //   10: if_icmpne -> 15
      //   13: iconst_0
      //   14: ireturn
      //   15: aload_0
      //   16: getfield mCacheSpanIndices : Z
      //   19: ifeq -> 70
      //   22: aload_0
      //   23: getfield mSpanIndexCache : Landroid/util/SparseIntArray;
      //   26: invokevirtual size : ()I
      //   29: ifle -> 70
      //   32: aload_0
      //   33: iload_1
      //   34: invokevirtual findReferenceIndexFromCache : (I)I
      //   37: istore #4
      //   39: iload #4
      //   41: iflt -> 70
      //   44: aload_0
      //   45: getfield mSpanIndexCache : Landroid/util/SparseIntArray;
      //   48: iload #4
      //   50: invokevirtual get : (I)I
      //   53: aload_0
      //   54: iload #4
      //   56: invokevirtual getSpanSize : (I)I
      //   59: iadd
      //   60: istore_3
      //   61: iload #4
      //   63: iconst_1
      //   64: iadd
      //   65: istore #4
      //   67: goto -> 75
      //   70: iconst_0
      //   71: istore #4
      //   73: iconst_0
      //   74: istore_3
      //   75: iload #4
      //   77: iload_1
      //   78: if_icmpge -> 127
      //   81: aload_0
      //   82: iload #4
      //   84: invokevirtual getSpanSize : (I)I
      //   87: istore #5
      //   89: iload_3
      //   90: iload #5
      //   92: iadd
      //   93: istore #6
      //   95: iload #6
      //   97: iload_2
      //   98: if_icmpne -> 106
      //   101: iconst_0
      //   102: istore_3
      //   103: goto -> 118
      //   106: iload #6
      //   108: istore_3
      //   109: iload #6
      //   111: iload_2
      //   112: if_icmple -> 118
      //   115: iload #5
      //   117: istore_3
      //   118: iload #4
      //   120: iconst_1
      //   121: iadd
      //   122: istore #4
      //   124: goto -> 75
      //   127: iload #7
      //   129: iload_3
      //   130: iadd
      //   131: iload_2
      //   132: if_icmpgt -> 137
      //   135: iload_3
      //   136: ireturn
      //   137: iconst_0
      //   138: ireturn }
    
    public abstract int getSpanSize(int param1Int);
    
    public void invalidateSpanIndexCache() { this.mSpanIndexCache.clear(); }
    
    public boolean isSpanIndexCacheEnabled() { return this.mCacheSpanIndices; }
    
    public void setSpanIndexCacheEnabled(boolean param1Boolean) { this.mCacheSpanIndices = param1Boolean; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/GridLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */