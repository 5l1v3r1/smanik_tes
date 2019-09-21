package android.support.v7.widget;

import android.content.Context;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import java.util.List;

public class LinearLayoutManager extends RecyclerView.LayoutManager implements ItemTouchHelper.ViewDropHandler, RecyclerView.SmoothScroller.ScrollVectorProvider {
  static final boolean DEBUG = false;
  
  public static final int HORIZONTAL = 0;
  
  public static final int INVALID_OFFSET = -2147483648;
  
  private static final float MAX_SCROLL_FACTOR = 0.33333334F;
  
  private static final String TAG = "LinearLayoutManager";
  
  public static final int VERTICAL = 1;
  
  final AnchorInfo mAnchorInfo = new AnchorInfo();
  
  private int mInitialPrefetchItemCount = 2;
  
  private boolean mLastStackFromEnd;
  
  private final LayoutChunkResult mLayoutChunkResult = new LayoutChunkResult();
  
  private LayoutState mLayoutState;
  
  int mOrientation = 1;
  
  OrientationHelper mOrientationHelper;
  
  SavedState mPendingSavedState = null;
  
  int mPendingScrollPosition = -1;
  
  int mPendingScrollPositionOffset = Integer.MIN_VALUE;
  
  private boolean mRecycleChildrenOnDetach;
  
  private boolean mReverseLayout = false;
  
  boolean mShouldReverseLayout = false;
  
  private boolean mSmoothScrollbarEnabled = true;
  
  private boolean mStackFromEnd = false;
  
  public LinearLayoutManager(Context paramContext) { this(paramContext, 1, false); }
  
  public LinearLayoutManager(Context paramContext, int paramInt, boolean paramBoolean) {
    setOrientation(paramInt);
    setReverseLayout(paramBoolean);
  }
  
  public LinearLayoutManager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
    RecyclerView.LayoutManager.Properties properties = getProperties(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setOrientation(properties.orientation);
    setReverseLayout(properties.reverseLayout);
    setStackFromEnd(properties.stackFromEnd);
  }
  
  private int computeScrollExtent(RecyclerView.State paramState) {
    if (getChildCount() == 0)
      return 0; 
    ensureLayoutState();
    return ScrollbarHelper.computeScrollExtent(paramState, this.mOrientationHelper, findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ true, true), findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ true, true), this, this.mSmoothScrollbarEnabled);
  }
  
  private int computeScrollOffset(RecyclerView.State paramState) {
    if (getChildCount() == 0)
      return 0; 
    ensureLayoutState();
    return ScrollbarHelper.computeScrollOffset(paramState, this.mOrientationHelper, findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ true, true), findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ true, true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
  }
  
  private int computeScrollRange(RecyclerView.State paramState) {
    if (getChildCount() == 0)
      return 0; 
    ensureLayoutState();
    return ScrollbarHelper.computeScrollRange(paramState, this.mOrientationHelper, findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ true, true), findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ true, true), this, this.mSmoothScrollbarEnabled);
  }
  
  private View findFirstPartiallyOrCompletelyInvisibleChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return findOnePartiallyOrCompletelyInvisibleChild(0, getChildCount()); }
  
  private View findFirstReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return findReferenceChild(paramRecycler, paramState, 0, getChildCount(), paramState.getItemCount()); }
  
  private View findFirstVisibleChildClosestToEnd(boolean paramBoolean1, boolean paramBoolean2) { return this.mShouldReverseLayout ? findOneVisibleChild(0, getChildCount(), paramBoolean1, paramBoolean2) : findOneVisibleChild(getChildCount() - 1, -1, paramBoolean1, paramBoolean2); }
  
  private View findFirstVisibleChildClosestToStart(boolean paramBoolean1, boolean paramBoolean2) { return this.mShouldReverseLayout ? findOneVisibleChild(getChildCount() - 1, -1, paramBoolean1, paramBoolean2) : findOneVisibleChild(0, getChildCount(), paramBoolean1, paramBoolean2); }
  
  private View findLastPartiallyOrCompletelyInvisibleChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return findOnePartiallyOrCompletelyInvisibleChild(getChildCount() - 1, -1); }
  
  private View findLastReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return findReferenceChild(paramRecycler, paramState, getChildCount() - 1, -1, paramState.getItemCount()); }
  
  private View findPartiallyOrCompletelyInvisibleChildClosestToEnd(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return this.mShouldReverseLayout ? findFirstPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState) : findLastPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState); }
  
  private View findPartiallyOrCompletelyInvisibleChildClosestToStart(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return this.mShouldReverseLayout ? findLastPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState) : findFirstPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState); }
  
  private View findReferenceChildClosestToEnd(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return this.mShouldReverseLayout ? findFirstReferenceChild(paramRecycler, paramState) : findLastReferenceChild(paramRecycler, paramState); }
  
  private View findReferenceChildClosestToStart(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return this.mShouldReverseLayout ? findLastReferenceChild(paramRecycler, paramState) : findFirstReferenceChild(paramRecycler, paramState); }
  
  private int fixLayoutEndGap(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean) {
    int i = this.mOrientationHelper.getEndAfterPadding() - paramInt;
    if (i > 0) {
      i = -scrollBy(-i, paramRecycler, paramState);
      if (paramBoolean) {
        paramInt = this.mOrientationHelper.getEndAfterPadding() - paramInt + i;
        if (paramInt > 0) {
          this.mOrientationHelper.offsetChildren(paramInt);
          return paramInt + i;
        } 
      } 
      return i;
    } 
    return 0;
  }
  
  private int fixLayoutStartGap(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean) {
    int i = paramInt - this.mOrientationHelper.getStartAfterPadding();
    if (i > 0) {
      i = -scrollBy(i, paramRecycler, paramState);
      if (paramBoolean) {
        paramInt = paramInt + i - this.mOrientationHelper.getStartAfterPadding();
        if (paramInt > 0) {
          this.mOrientationHelper.offsetChildren(-paramInt);
          return i - paramInt;
        } 
      } 
      return i;
    } 
    return 0;
  }
  
  private View getChildClosestToEnd() {
    int i;
    if (this.mShouldReverseLayout) {
      i = 0;
    } else {
      i = getChildCount() - 1;
    } 
    return getChildAt(i);
  }
  
  private View getChildClosestToStart() {
    byte b;
    if (this.mShouldReverseLayout) {
      b = getChildCount() - 1;
    } else {
      b = 0;
    } 
    return getChildAt(b);
  }
  
  private void layoutForPredictiveAnimations(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2) {
    if (paramState.willRunPredictiveAnimations() && getChildCount() != 0 && !paramState.isPreLayout()) {
      if (!supportsPredictiveItemAnimations())
        return; 
      List list = paramRecycler.getScrapList();
      int k = list.size();
      int m = getPosition(getChildAt(0));
      byte b = 0;
      int j = 0;
      int i = 0;
      while (b < k) {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)list.get(b);
        if (!viewHolder.isRemoved()) {
          boolean bool;
          int n = viewHolder.getLayoutPosition();
          byte b1 = 1;
          if (n < m) {
            bool = true;
          } else {
            bool = false;
          } 
          if (bool != this.mShouldReverseLayout)
            b1 = -1; 
          if (b1 == -1) {
            j += this.mOrientationHelper.getDecoratedMeasurement(viewHolder.itemView);
          } else {
            i += this.mOrientationHelper.getDecoratedMeasurement(viewHolder.itemView);
          } 
        } 
        b++;
      } 
      this.mLayoutState.mScrapList = list;
      if (j > 0) {
        updateLayoutStateToFillStart(getPosition(getChildClosestToStart()), paramInt1);
        this.mLayoutState.mExtra = j;
        this.mLayoutState.mAvailable = 0;
        this.mLayoutState.assignPositionFromScrapList();
        fill(paramRecycler, this.mLayoutState, paramState, false);
      } 
      if (i > 0) {
        updateLayoutStateToFillEnd(getPosition(getChildClosestToEnd()), paramInt2);
        this.mLayoutState.mExtra = i;
        this.mLayoutState.mAvailable = 0;
        this.mLayoutState.assignPositionFromScrapList();
        fill(paramRecycler, this.mLayoutState, paramState, false);
      } 
      this.mLayoutState.mScrapList = null;
      return;
    } 
  }
  
  private void logChildren() {
    Log.d("LinearLayoutManager", "internal representation of views on the screen");
    for (byte b = 0; b < getChildCount(); b++) {
      View view = getChildAt(b);
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("item ");
      stringBuilder.append(getPosition(view));
      stringBuilder.append(", coord:");
      stringBuilder.append(this.mOrientationHelper.getDecoratedStart(view));
      Log.d("LinearLayoutManager", stringBuilder.toString());
    } 
    Log.d("LinearLayoutManager", "==============");
  }
  
  private void recycleByLayoutState(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState) {
    if (paramLayoutState.mRecycle) {
      if (paramLayoutState.mInfinite)
        return; 
      if (paramLayoutState.mLayoutDirection == -1) {
        recycleViewsFromEnd(paramRecycler, paramLayoutState.mScrollingOffset);
        return;
      } 
      recycleViewsFromStart(paramRecycler, paramLayoutState.mScrollingOffset);
      return;
    } 
  }
  
  private void recycleChildren(RecyclerView.Recycler paramRecycler, int paramInt1, int paramInt2) {
    if (paramInt1 == paramInt2)
      return; 
    int i = paramInt1;
    if (paramInt2 > paramInt1) {
      while (--paramInt2 >= paramInt1) {
        removeAndRecycleViewAt(paramInt2, paramRecycler);
        paramInt2--;
      } 
    } else {
      while (i > paramInt2) {
        removeAndRecycleViewAt(i, paramRecycler);
        i--;
      } 
    } 
  }
  
  private void recycleViewsFromEnd(RecyclerView.Recycler paramRecycler, int paramInt) {
    int i = getChildCount();
    if (paramInt < 0)
      return; 
    int j = this.mOrientationHelper.getEnd() - paramInt;
    if (this.mShouldReverseLayout) {
      for (paramInt = 0; paramInt < i; paramInt++) {
        View view = getChildAt(paramInt);
        if (this.mOrientationHelper.getDecoratedStart(view) < j || this.mOrientationHelper.getTransformedStartWithDecoration(view) < j) {
          recycleChildren(paramRecycler, 0, paramInt);
          return;
        } 
      } 
    } else {
      for (paramInt = --i; paramInt >= 0; paramInt--) {
        View view = getChildAt(paramInt);
        if (this.mOrientationHelper.getDecoratedStart(view) < j || this.mOrientationHelper.getTransformedStartWithDecoration(view) < j) {
          recycleChildren(paramRecycler, i, paramInt);
          return;
        } 
      } 
    } 
  }
  
  private void recycleViewsFromStart(RecyclerView.Recycler paramRecycler, int paramInt) {
    if (paramInt < 0)
      return; 
    int i = getChildCount();
    if (this.mShouldReverseLayout) {
      for (int j = --i; j >= 0; j--) {
        View view = getChildAt(j);
        if (this.mOrientationHelper.getDecoratedEnd(view) > paramInt || this.mOrientationHelper.getTransformedEndWithDecoration(view) > paramInt) {
          recycleChildren(paramRecycler, i, j);
          return;
        } 
      } 
    } else {
      for (byte b = 0; b < i; b++) {
        View view = getChildAt(b);
        if (this.mOrientationHelper.getDecoratedEnd(view) > paramInt || this.mOrientationHelper.getTransformedEndWithDecoration(view) > paramInt) {
          recycleChildren(paramRecycler, 0, b);
          return;
        } 
      } 
    } 
  }
  
  private void resolveShouldLayoutReverse() {
    if (this.mOrientation == 1 || !isLayoutRTL()) {
      this.mShouldReverseLayout = this.mReverseLayout;
      return;
    } 
    this.mShouldReverseLayout = this.mReverseLayout ^ true;
  }
  
  private boolean updateAnchorFromChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AnchorInfo paramAnchorInfo) {
    View view1;
    int j = getChildCount();
    int i = 0;
    if (j == 0)
      return false; 
    View view2 = getFocusedChild();
    if (view2 != null && paramAnchorInfo.isViewValidAsAnchor(view2, paramState)) {
      paramAnchorInfo.assignFromViewAndKeepVisibleRect(view2, getPosition(view2));
      return true;
    } 
    if (this.mLastStackFromEnd != this.mStackFromEnd)
      return false; 
    if (paramAnchorInfo.mLayoutFromEnd) {
      view1 = findReferenceChildClosestToEnd(paramRecycler, paramState);
    } else {
      view1 = findReferenceChildClosestToStart(view1, paramState);
    } 
    if (view1 != null) {
      paramAnchorInfo.assignFromView(view1, getPosition(view1));
      if (!paramState.isPreLayout() && supportsPredictiveItemAnimations()) {
        if (this.mOrientationHelper.getDecoratedStart(view1) >= this.mOrientationHelper.getEndAfterPadding() || this.mOrientationHelper.getDecoratedEnd(view1) < this.mOrientationHelper.getStartAfterPadding())
          i = 1; 
        if (i) {
          if (paramAnchorInfo.mLayoutFromEnd) {
            i = this.mOrientationHelper.getEndAfterPadding();
          } else {
            i = this.mOrientationHelper.getStartAfterPadding();
          } 
          paramAnchorInfo.mCoordinate = i;
        } 
      } 
      return true;
    } 
    return false;
  }
  
  private boolean updateAnchorFromPendingData(RecyclerView.State paramState, AnchorInfo paramAnchorInfo) {
    boolean bool = paramState.isPreLayout();
    boolean bool1 = false;
    if (!bool) {
      if (this.mPendingScrollPosition == -1)
        return false; 
      if (this.mPendingScrollPosition < 0 || this.mPendingScrollPosition >= paramState.getItemCount()) {
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        return false;
      } 
      paramAnchorInfo.mPosition = this.mPendingScrollPosition;
      if (this.mPendingSavedState != null && this.mPendingSavedState.hasValidAnchor()) {
        paramAnchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
        if (paramAnchorInfo.mLayoutFromEnd) {
          paramAnchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingSavedState.mAnchorOffset;
          return true;
        } 
        paramAnchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingSavedState.mAnchorOffset;
        return true;
      } 
      if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE) {
        View view = findViewByPosition(this.mPendingScrollPosition);
        if (view != null) {
          int i;
          if (this.mOrientationHelper.getDecoratedMeasurement(view) > this.mOrientationHelper.getTotalSpace()) {
            paramAnchorInfo.assignCoordinateFromPadding();
            return true;
          } 
          if (this.mOrientationHelper.getDecoratedStart(view) - this.mOrientationHelper.getStartAfterPadding() < 0) {
            paramAnchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding();
            paramAnchorInfo.mLayoutFromEnd = false;
            return true;
          } 
          if (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(view) < 0) {
            paramAnchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding();
            paramAnchorInfo.mLayoutFromEnd = true;
            return true;
          } 
          if (paramAnchorInfo.mLayoutFromEnd) {
            i = this.mOrientationHelper.getDecoratedEnd(view) + this.mOrientationHelper.getTotalSpaceChange();
          } else {
            i = this.mOrientationHelper.getDecoratedStart(view);
          } 
          paramAnchorInfo.mCoordinate = i;
          return true;
        } 
        if (getChildCount() > 0) {
          int i = getPosition(getChildAt(0));
          if (this.mPendingScrollPosition < i) {
            bool = true;
          } else {
            bool = false;
          } 
          if (bool == this.mShouldReverseLayout)
            bool1 = true; 
          paramAnchorInfo.mLayoutFromEnd = bool1;
        } 
        paramAnchorInfo.assignCoordinateFromPadding();
        return true;
      } 
      paramAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
      if (this.mShouldReverseLayout) {
        paramAnchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingScrollPositionOffset;
        return true;
      } 
      paramAnchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingScrollPositionOffset;
      return true;
    } 
    return false;
  }
  
  private void updateAnchorInfoForLayout(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AnchorInfo paramAnchorInfo) {
    boolean bool;
    if (updateAnchorFromPendingData(paramState, paramAnchorInfo))
      return; 
    if (updateAnchorFromChildren(paramRecycler, paramState, paramAnchorInfo))
      return; 
    paramAnchorInfo.assignCoordinateFromPadding();
    if (this.mStackFromEnd) {
      bool = paramState.getItemCount() - 1;
    } else {
      bool = false;
    } 
    paramAnchorInfo.mPosition = bool;
  }
  
  private void updateLayoutState(int paramInt1, int paramInt2, boolean paramBoolean, RecyclerView.State paramState) {
    this.mLayoutState.mInfinite = resolveIsInfinite();
    this.mLayoutState.mExtra = getExtraLayoutSpace(paramState);
    this.mLayoutState.mLayoutDirection = paramInt1;
    byte b = -1;
    if (paramInt1 == 1) {
      LayoutState layoutState1 = this.mLayoutState;
      layoutState1.mExtra += this.mOrientationHelper.getEndPadding();
      View view = getChildClosestToEnd();
      LayoutState layoutState2 = this.mLayoutState;
      if (!this.mShouldReverseLayout)
        b = 1; 
      layoutState2.mItemDirection = b;
      this.mLayoutState.mCurrentPosition = getPosition(view) + this.mLayoutState.mItemDirection;
      this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedEnd(view);
      paramInt1 = this.mOrientationHelper.getDecoratedEnd(view) - this.mOrientationHelper.getEndAfterPadding();
    } else {
      View view = getChildClosestToStart();
      LayoutState layoutState = this.mLayoutState;
      layoutState.mExtra += this.mOrientationHelper.getStartAfterPadding();
      layoutState = this.mLayoutState;
      if (this.mShouldReverseLayout)
        b = 1; 
      layoutState.mItemDirection = b;
      this.mLayoutState.mCurrentPosition = getPosition(view) + this.mLayoutState.mItemDirection;
      this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedStart(view);
      paramInt1 = -this.mOrientationHelper.getDecoratedStart(view) + this.mOrientationHelper.getStartAfterPadding();
    } 
    this.mLayoutState.mAvailable = paramInt2;
    if (paramBoolean) {
      LayoutState layoutState = this.mLayoutState;
      layoutState.mAvailable -= paramInt1;
    } 
    this.mLayoutState.mScrollingOffset = paramInt1;
  }
  
  private void updateLayoutStateToFillEnd(int paramInt1, int paramInt2) {
    boolean bool;
    this.mLayoutState.mAvailable = this.mOrientationHelper.getEndAfterPadding() - paramInt2;
    LayoutState layoutState = this.mLayoutState;
    if (this.mShouldReverseLayout) {
      bool = true;
    } else {
      bool = true;
    } 
    layoutState.mItemDirection = bool;
    this.mLayoutState.mCurrentPosition = paramInt1;
    this.mLayoutState.mLayoutDirection = 1;
    this.mLayoutState.mOffset = paramInt2;
    this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
  }
  
  private void updateLayoutStateToFillEnd(AnchorInfo paramAnchorInfo) { updateLayoutStateToFillEnd(paramAnchorInfo.mPosition, paramAnchorInfo.mCoordinate); }
  
  private void updateLayoutStateToFillStart(int paramInt1, int paramInt2) {
    this.mLayoutState.mAvailable = paramInt2 - this.mOrientationHelper.getStartAfterPadding();
    this.mLayoutState.mCurrentPosition = paramInt1;
    LayoutState layoutState = this.mLayoutState;
    if (this.mShouldReverseLayout) {
      paramInt1 = 1;
    } else {
      paramInt1 = -1;
    } 
    layoutState.mItemDirection = paramInt1;
    this.mLayoutState.mLayoutDirection = -1;
    this.mLayoutState.mOffset = paramInt2;
    this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
  }
  
  private void updateLayoutStateToFillStart(AnchorInfo paramAnchorInfo) { updateLayoutStateToFillStart(paramAnchorInfo.mPosition, paramAnchorInfo.mCoordinate); }
  
  public void assertNotInLayoutOrScroll(String paramString) {
    if (this.mPendingSavedState == null)
      super.assertNotInLayoutOrScroll(paramString); 
  }
  
  public boolean canScrollHorizontally() { return (this.mOrientation == 0); }
  
  public boolean canScrollVertically() { return (this.mOrientation == 1); }
  
  public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, RecyclerView.State paramState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry) {
    if (this.mOrientation != 0)
      paramInt1 = paramInt2; 
    if (getChildCount() != 0) {
      if (paramInt1 == 0)
        return; 
      ensureLayoutState();
      if (paramInt1 > 0) {
        paramInt2 = 1;
      } else {
        paramInt2 = -1;
      } 
      updateLayoutState(paramInt2, Math.abs(paramInt1), true, paramState);
      collectPrefetchPositionsForLayoutState(paramState, this.mLayoutState, paramLayoutPrefetchRegistry);
      return;
    } 
  }
  
  public void collectInitialPrefetchPositions(int paramInt, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry) {
    boolean bool;
    SavedState savedState = this.mPendingSavedState;
    int j = -1;
    if (savedState != null && this.mPendingSavedState.hasValidAnchor()) {
      bool = this.mPendingSavedState.mAnchorLayoutFromEnd;
      i = this.mPendingSavedState.mAnchorPosition;
    } else {
      resolveShouldLayoutReverse();
      bool = this.mShouldReverseLayout;
      if (this.mPendingScrollPosition == -1) {
        if (bool) {
          i = paramInt - 1;
        } else {
          i = 0;
        } 
      } else {
        i = this.mPendingScrollPosition;
      } 
    } 
    if (!bool)
      j = 1; 
    int m = 0;
    int k = i;
    for (int i = m; i < this.mInitialPrefetchItemCount && k >= 0 && k < paramInt; i++) {
      paramLayoutPrefetchRegistry.addPosition(k, 0);
      k += j;
    } 
  }
  
  void collectPrefetchPositionsForLayoutState(RecyclerView.State paramState, LayoutState paramLayoutState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry) {
    int i = paramLayoutState.mCurrentPosition;
    if (i >= 0 && i < paramState.getItemCount())
      paramLayoutPrefetchRegistry.addPosition(i, Math.max(0, paramLayoutState.mScrollingOffset)); 
  }
  
  public int computeHorizontalScrollExtent(RecyclerView.State paramState) { return computeScrollExtent(paramState); }
  
  public int computeHorizontalScrollOffset(RecyclerView.State paramState) { return computeScrollOffset(paramState); }
  
  public int computeHorizontalScrollRange(RecyclerView.State paramState) { return computeScrollRange(paramState); }
  
  public PointF computeScrollVectorForPosition(int paramInt) {
    if (getChildCount() == 0)
      return null; 
    boolean bool = false;
    int j = getPosition(getChildAt(0));
    int i = 1;
    if (paramInt < j)
      bool = true; 
    paramInt = i;
    if (bool != this.mShouldReverseLayout)
      paramInt = -1; 
    return (this.mOrientation == 0) ? new PointF(paramInt, 0.0F) : new PointF(0.0F, paramInt);
  }
  
  public int computeVerticalScrollExtent(RecyclerView.State paramState) { return computeScrollExtent(paramState); }
  
  public int computeVerticalScrollOffset(RecyclerView.State paramState) { return computeScrollOffset(paramState); }
  
  public int computeVerticalScrollRange(RecyclerView.State paramState) { return computeScrollRange(paramState); }
  
  int convertFocusDirectionToLayoutDirection(int paramInt) {
    int i = Integer.MIN_VALUE;
    if (paramInt != 17) {
      if (paramInt != 33) {
        if (paramInt != 66) {
          if (paramInt != 130) {
            switch (paramInt) {
              default:
                return Integer.MIN_VALUE;
              case 2:
                return (this.mOrientation == 1) ? 1 : (isLayoutRTL() ? -1 : 1);
              case 1:
                break;
            } 
            return (this.mOrientation == 1) ? -1 : (isLayoutRTL() ? 1 : -1);
          } 
          if (this.mOrientation == 1)
            i = 1; 
          return i;
        } 
        if (this.mOrientation == 0)
          i = 1; 
        return i;
      } 
      return (this.mOrientation == 1) ? -1 : Integer.MIN_VALUE;
    } 
    return (this.mOrientation == 0) ? -1 : Integer.MIN_VALUE;
  }
  
  LayoutState createLayoutState() { return new LayoutState(); }
  
  void ensureLayoutState() {
    if (this.mLayoutState == null)
      this.mLayoutState = createLayoutState(); 
  }
  
  int fill(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState, RecyclerView.State paramState, boolean paramBoolean) { // Byte code:
    //   0: aload_2
    //   1: getfield mAvailable : I
    //   4: istore #7
    //   6: aload_2
    //   7: getfield mScrollingOffset : I
    //   10: ldc -2147483648
    //   12: if_icmpeq -> 41
    //   15: aload_2
    //   16: getfield mAvailable : I
    //   19: ifge -> 35
    //   22: aload_2
    //   23: aload_2
    //   24: getfield mScrollingOffset : I
    //   27: aload_2
    //   28: getfield mAvailable : I
    //   31: iadd
    //   32: putfield mScrollingOffset : I
    //   35: aload_0
    //   36: aload_1
    //   37: aload_2
    //   38: invokespecial recycleByLayoutState : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/LinearLayoutManager$LayoutState;)V
    //   41: aload_2
    //   42: getfield mAvailable : I
    //   45: aload_2
    //   46: getfield mExtra : I
    //   49: iadd
    //   50: istore #5
    //   52: aload_0
    //   53: getfield mLayoutChunkResult : Landroid/support/v7/widget/LinearLayoutManager$LayoutChunkResult;
    //   56: astore #8
    //   58: aload_2
    //   59: getfield mInfinite : Z
    //   62: ifne -> 70
    //   65: iload #5
    //   67: ifle -> 245
    //   70: aload_2
    //   71: aload_3
    //   72: invokevirtual hasMore : (Landroid/support/v7/widget/RecyclerView$State;)Z
    //   75: ifeq -> 245
    //   78: aload #8
    //   80: invokevirtual resetInternal : ()V
    //   83: aload_0
    //   84: aload_1
    //   85: aload_3
    //   86: aload_2
    //   87: aload #8
    //   89: invokevirtual layoutChunk : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/RecyclerView$State;Landroid/support/v7/widget/LinearLayoutManager$LayoutState;Landroid/support/v7/widget/LinearLayoutManager$LayoutChunkResult;)V
    //   92: aload #8
    //   94: getfield mFinished : Z
    //   97: ifeq -> 103
    //   100: goto -> 245
    //   103: aload_2
    //   104: aload_2
    //   105: getfield mOffset : I
    //   108: aload #8
    //   110: getfield mConsumed : I
    //   113: aload_2
    //   114: getfield mLayoutDirection : I
    //   117: imul
    //   118: iadd
    //   119: putfield mOffset : I
    //   122: aload #8
    //   124: getfield mIgnoreConsumed : Z
    //   127: ifeq -> 151
    //   130: aload_0
    //   131: getfield mLayoutState : Landroid/support/v7/widget/LinearLayoutManager$LayoutState;
    //   134: getfield mScrapList : Ljava/util/List;
    //   137: ifnonnull -> 151
    //   140: iload #5
    //   142: istore #6
    //   144: aload_3
    //   145: invokevirtual isPreLayout : ()Z
    //   148: ifne -> 175
    //   151: aload_2
    //   152: aload_2
    //   153: getfield mAvailable : I
    //   156: aload #8
    //   158: getfield mConsumed : I
    //   161: isub
    //   162: putfield mAvailable : I
    //   165: iload #5
    //   167: aload #8
    //   169: getfield mConsumed : I
    //   172: isub
    //   173: istore #6
    //   175: aload_2
    //   176: getfield mScrollingOffset : I
    //   179: ldc -2147483648
    //   181: if_icmpeq -> 224
    //   184: aload_2
    //   185: aload_2
    //   186: getfield mScrollingOffset : I
    //   189: aload #8
    //   191: getfield mConsumed : I
    //   194: iadd
    //   195: putfield mScrollingOffset : I
    //   198: aload_2
    //   199: getfield mAvailable : I
    //   202: ifge -> 218
    //   205: aload_2
    //   206: aload_2
    //   207: getfield mScrollingOffset : I
    //   210: aload_2
    //   211: getfield mAvailable : I
    //   214: iadd
    //   215: putfield mScrollingOffset : I
    //   218: aload_0
    //   219: aload_1
    //   220: aload_2
    //   221: invokespecial recycleByLayoutState : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/LinearLayoutManager$LayoutState;)V
    //   224: iload #6
    //   226: istore #5
    //   228: iload #4
    //   230: ifeq -> 58
    //   233: iload #6
    //   235: istore #5
    //   237: aload #8
    //   239: getfield mFocusable : Z
    //   242: ifeq -> 58
    //   245: iload #7
    //   247: aload_2
    //   248: getfield mAvailable : I
    //   251: isub
    //   252: ireturn }
  
  public int findFirstCompletelyVisibleItemPosition() {
    View view = findOneVisibleChild(0, getChildCount(), true, false);
    return (view == null) ? -1 : getPosition(view);
  }
  
  public int findFirstVisibleItemPosition() {
    View view = findOneVisibleChild(0, getChildCount(), false, true);
    return (view == null) ? -1 : getPosition(view);
  }
  
  public int findLastCompletelyVisibleItemPosition() {
    View view = findOneVisibleChild(getChildCount() - 1, -1, true, false);
    return (view == null) ? -1 : getPosition(view);
  }
  
  public int findLastVisibleItemPosition() {
    View view = findOneVisibleChild(getChildCount() - 1, -1, false, true);
    return (view == null) ? -1 : getPosition(view);
  }
  
  View findOnePartiallyOrCompletelyInvisibleChild(int paramInt1, int paramInt2) {
    char c2;
    char c1;
    ensureLayoutState();
    if (paramInt2 > paramInt1) {
      c1 = '\001';
    } else if (paramInt2 < paramInt1) {
      c1 = '￿';
    } else {
      c1 = Character.MIN_VALUE;
    } 
    if (!c1)
      return getChildAt(paramInt1); 
    if (this.mOrientationHelper.getDecoratedStart(getChildAt(paramInt1)) < this.mOrientationHelper.getStartAfterPadding()) {
      c1 = '䄄';
      c2 = '䀄';
    } else {
      c1 = '၁';
      c2 = 'ခ';
    } 
    return (this.mOrientation == 0) ? this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, c1, c2) : this.mVerticalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, c1, c2);
  }
  
  View findOneVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) {
    char c1;
    ensureLayoutState();
    char c2 = 'ŀ';
    if (paramBoolean1) {
      c1 = '怃';
    } else {
      c1 = 'ŀ';
    } 
    if (!paramBoolean2)
      c2 = Character.MIN_VALUE; 
    return (this.mOrientation == 0) ? this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, c1, c2) : this.mVerticalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, c1, c2);
  }
  
  View findReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2, int paramInt3) {
    int i;
    View view;
    ensureLayoutState();
    int j = this.mOrientationHelper.getStartAfterPadding();
    int k = this.mOrientationHelper.getEndAfterPadding();
    if (paramInt2 > paramInt1) {
      i = 1;
    } else {
      i = -1;
    } 
    paramState = null;
    for (paramRecycler = null; paramInt1 != paramInt2; paramRecycler = recycler) {
      View view2 = getChildAt(paramInt1);
      int m = getPosition(view2);
      View view1 = paramState;
      RecyclerView.Recycler recycler = paramRecycler;
      if (m >= 0) {
        view1 = paramState;
        recycler = paramRecycler;
        if (m < paramInt3)
          if (((RecyclerView.LayoutParams)view2.getLayoutParams()).isItemRemoved()) {
            view1 = paramState;
            recycler = paramRecycler;
            if (paramRecycler == null) {
              View view3 = view2;
              view1 = paramState;
            } 
          } else if (this.mOrientationHelper.getDecoratedStart(view2) >= k || this.mOrientationHelper.getDecoratedEnd(view2) < j) {
            view1 = paramState;
            recycler = paramRecycler;
            if (paramState == null) {
              view1 = view2;
              recycler = paramRecycler;
            } 
          } else {
            return view2;
          }  
      } 
      paramInt1 += i;
      view = view1;
    } 
    return (view != null) ? view : paramRecycler;
  }
  
  public View findViewByPosition(int paramInt) {
    int i = getChildCount();
    if (i == 0)
      return null; 
    int j = paramInt - getPosition(getChildAt(0));
    if (j >= 0 && j < i) {
      View view = getChildAt(j);
      if (getPosition(view) == paramInt)
        return view; 
    } 
    return super.findViewByPosition(paramInt);
  }
  
  public RecyclerView.LayoutParams generateDefaultLayoutParams() { return new RecyclerView.LayoutParams(-2, -2); }
  
  protected int getExtraLayoutSpace(RecyclerView.State paramState) { return paramState.hasTargetScrollPosition() ? this.mOrientationHelper.getTotalSpace() : 0; }
  
  public int getInitialPrefetchItemCount() { return this.mInitialPrefetchItemCount; }
  
  public int getOrientation() { return this.mOrientation; }
  
  public boolean getRecycleChildrenOnDetach() { return this.mRecycleChildrenOnDetach; }
  
  public boolean getReverseLayout() { return this.mReverseLayout; }
  
  public boolean getStackFromEnd() { return this.mStackFromEnd; }
  
  public boolean isAutoMeasureEnabled() { return true; }
  
  protected boolean isLayoutRTL() { return (getLayoutDirection() == 1); }
  
  public boolean isSmoothScrollbarEnabled() { return this.mSmoothScrollbarEnabled; }
  
  void layoutChunk(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LayoutState paramLayoutState, LayoutChunkResult paramLayoutChunkResult) {
    int m;
    int k;
    int j;
    int i;
    View view = paramLayoutState.next(paramRecycler);
    if (view == null) {
      paramLayoutChunkResult.mFinished = true;
      return;
    } 
    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)view.getLayoutParams();
    if (paramLayoutState.mScrapList == null) {
      boolean bool;
      boolean bool1 = this.mShouldReverseLayout;
      if (paramLayoutState.mLayoutDirection == -1) {
        bool = true;
      } else {
        bool = false;
      } 
      if (bool1 == bool) {
        addView(view);
      } else {
        addView(view, 0);
      } 
    } else {
      boolean bool;
      boolean bool1 = this.mShouldReverseLayout;
      if (paramLayoutState.mLayoutDirection == -1) {
        bool = true;
      } else {
        bool = false;
      } 
      if (bool1 == bool) {
        addDisappearingView(view);
      } else {
        addDisappearingView(view, 0);
      } 
    } 
    measureChildWithMargins(view, 0, 0);
    paramLayoutChunkResult.mConsumed = this.mOrientationHelper.getDecoratedMeasurement(view);
    if (this.mOrientation == 1) {
      if (isLayoutRTL()) {
        i = getWidth() - getPaddingRight();
        m = i - this.mOrientationHelper.getDecoratedMeasurementInOther(view);
      } else {
        m = getPaddingLeft();
        i = this.mOrientationHelper.getDecoratedMeasurementInOther(view) + m;
      } 
      if (paramLayoutState.mLayoutDirection == -1) {
        k = paramLayoutState.mOffset;
        int n = paramLayoutState.mOffset - paramLayoutChunkResult.mConsumed;
        j = i;
        i = n;
      } else {
        int n = paramLayoutState.mOffset;
        k = paramLayoutState.mOffset + paramLayoutChunkResult.mConsumed;
        j = i;
        i = n;
      } 
    } else {
      j = getPaddingTop();
      i = this.mOrientationHelper.getDecoratedMeasurementInOther(view) + j;
      if (paramLayoutState.mLayoutDirection == -1) {
        k = paramLayoutState.mOffset;
        m = paramLayoutState.mOffset;
        int i1 = paramLayoutChunkResult.mConsumed;
        int n = i;
        m -= i1;
        i = j;
        j = k;
        k = n;
      } else {
        int n = paramLayoutState.mOffset;
        int i1 = paramLayoutState.mOffset + paramLayoutChunkResult.mConsumed;
        m = j;
        k = i;
        j = i1;
        i = m;
        m = n;
      } 
    } 
    layoutDecoratedWithMargins(view, m, i, j, k);
    if (layoutParams.isItemRemoved() || layoutParams.isItemChanged())
      paramLayoutChunkResult.mIgnoreConsumed = true; 
    paramLayoutChunkResult.mFocusable = view.hasFocusable();
  }
  
  void onAnchorReady(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AnchorInfo paramAnchorInfo, int paramInt) {}
  
  public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler) {
    super.onDetachedFromWindow(paramRecyclerView, paramRecycler);
    if (this.mRecycleChildrenOnDetach) {
      removeAndRecycleAllViews(paramRecycler);
      paramRecycler.clear();
    } 
  }
  
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) {
    View view;
    resolveShouldLayoutReverse();
    if (getChildCount() == 0)
      return null; 
    paramInt = convertFocusDirectionToLayoutDirection(paramInt);
    if (paramInt == Integer.MIN_VALUE)
      return null; 
    ensureLayoutState();
    ensureLayoutState();
    updateLayoutState(paramInt, (int)(this.mOrientationHelper.getTotalSpace() * 0.33333334F), false, paramState);
    this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
    this.mLayoutState.mRecycle = false;
    fill(paramRecycler, this.mLayoutState, paramState, true);
    if (paramInt == -1) {
      paramView = findPartiallyOrCompletelyInvisibleChildClosestToStart(paramRecycler, paramState);
    } else {
      paramView = findPartiallyOrCompletelyInvisibleChildClosestToEnd(paramRecycler, paramState);
    } 
    if (paramInt == -1) {
      view = getChildClosestToStart();
    } else {
      view = getChildClosestToEnd();
    } 
    return view.hasFocusable() ? ((paramView == null) ? null : view) : paramView;
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent) {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    if (getChildCount() > 0) {
      paramAccessibilityEvent.setFromIndex(findFirstVisibleItemPosition());
      paramAccessibilityEvent.setToIndex(findLastVisibleItemPosition());
    } 
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) {
    SavedState savedState = this.mPendingSavedState;
    int k = -1;
    if ((savedState != null || this.mPendingScrollPosition != -1) && paramState.getItemCount() == 0) {
      removeAndRecycleAllViews(paramRecycler);
      return;
    } 
    if (this.mPendingSavedState != null && this.mPendingSavedState.hasValidAnchor())
      this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition; 
    ensureLayoutState();
    this.mLayoutState.mRecycle = false;
    resolveShouldLayoutReverse();
    View view = getFocusedChild();
    if (!this.mAnchorInfo.mValid || this.mPendingScrollPosition != -1 || this.mPendingSavedState != null) {
      this.mAnchorInfo.reset();
      this.mAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout ^ this.mStackFromEnd;
      updateAnchorInfoForLayout(paramRecycler, paramState, this.mAnchorInfo);
      this.mAnchorInfo.mValid = true;
    } else if (view != null && (this.mOrientationHelper.getDecoratedStart(view) >= this.mOrientationHelper.getEndAfterPadding() || this.mOrientationHelper.getDecoratedEnd(view) <= this.mOrientationHelper.getStartAfterPadding())) {
      this.mAnchorInfo.assignFromViewAndKeepVisibleRect(view, getPosition(view));
    } 
    int j = getExtraLayoutSpace(paramState);
    if (this.mLayoutState.mLastScrollDelta >= 0) {
      i = j;
      j = 0;
    } else {
      i = 0;
    } 
    int m = j + this.mOrientationHelper.getStartAfterPadding();
    int n = i + this.mOrientationHelper.getEndPadding();
    int i = m;
    j = n;
    if (paramState.isPreLayout()) {
      i = m;
      j = n;
      if (this.mPendingScrollPosition != -1) {
        i = m;
        j = n;
        if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
          view = findViewByPosition(this.mPendingScrollPosition);
          i = m;
          j = n;
          if (view != null) {
            if (this.mShouldReverseLayout) {
              i = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(view) - this.mPendingScrollPositionOffset;
            } else {
              i = this.mOrientationHelper.getDecoratedStart(view);
              j = this.mOrientationHelper.getStartAfterPadding();
              i = this.mPendingScrollPositionOffset - i - j;
            } 
            if (i > 0) {
              i = m + i;
              j = n;
            } else {
              j = n - i;
              i = m;
            } 
          } 
        } 
      } 
    } 
    if (this.mAnchorInfo.mLayoutFromEnd ? this.mShouldReverseLayout : !this.mShouldReverseLayout)
      k = 1; 
    onAnchorReady(paramRecycler, paramState, this.mAnchorInfo, k);
    detachAndScrapAttachedViews(paramRecycler);
    this.mLayoutState.mInfinite = resolveIsInfinite();
    this.mLayoutState.mIsPreLayout = paramState.isPreLayout();
    if (this.mAnchorInfo.mLayoutFromEnd) {
      updateLayoutStateToFillStart(this.mAnchorInfo);
      this.mLayoutState.mExtra = i;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      k = this.mLayoutState.mOffset;
      n = this.mLayoutState.mCurrentPosition;
      i = j;
      if (this.mLayoutState.mAvailable > 0)
        i = j + this.mLayoutState.mAvailable; 
      updateLayoutStateToFillEnd(this.mAnchorInfo);
      this.mLayoutState.mExtra = i;
      LayoutState layoutState = this.mLayoutState;
      layoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      m = this.mLayoutState.mOffset;
      j = k;
      i = m;
      if (this.mLayoutState.mAvailable > 0) {
        i = this.mLayoutState.mAvailable;
        updateLayoutStateToFillStart(n, k);
        this.mLayoutState.mExtra = i;
        fill(paramRecycler, this.mLayoutState, paramState, false);
        j = this.mLayoutState.mOffset;
        i = m;
      } 
    } else {
      updateLayoutStateToFillEnd(this.mAnchorInfo);
      this.mLayoutState.mExtra = j;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      k = this.mLayoutState.mOffset;
      n = this.mLayoutState.mCurrentPosition;
      j = i;
      if (this.mLayoutState.mAvailable > 0)
        j = i + this.mLayoutState.mAvailable; 
      updateLayoutStateToFillStart(this.mAnchorInfo);
      this.mLayoutState.mExtra = j;
      LayoutState layoutState = this.mLayoutState;
      layoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      m = this.mLayoutState.mOffset;
      j = m;
      i = k;
      if (this.mLayoutState.mAvailable > 0) {
        i = this.mLayoutState.mAvailable;
        updateLayoutStateToFillEnd(n, k);
        this.mLayoutState.mExtra = i;
        fill(paramRecycler, this.mLayoutState, paramState, false);
        i = this.mLayoutState.mOffset;
        j = m;
      } 
    } 
    k = j;
    m = i;
    if (getChildCount() > 0)
      if (this.mShouldReverseLayout ^ this.mStackFromEnd) {
        m = fixLayoutEndGap(i, paramRecycler, paramState, true);
        k = j + m;
        j = fixLayoutStartGap(k, paramRecycler, paramState, false);
        k += j;
        m = i + m + j;
      } else {
        k = fixLayoutStartGap(j, paramRecycler, paramState, true);
        i += k;
        m = fixLayoutEndGap(i, paramRecycler, paramState, false);
        k = j + k + m;
        m = i + m;
      }  
    layoutForPredictiveAnimations(paramRecycler, paramState, k, m);
    if (!paramState.isPreLayout()) {
      this.mOrientationHelper.onLayoutComplete();
    } else {
      this.mAnchorInfo.reset();
    } 
    this.mLastStackFromEnd = this.mStackFromEnd;
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState) {
    super.onLayoutCompleted(paramState);
    this.mPendingSavedState = null;
    this.mPendingScrollPosition = -1;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    this.mAnchorInfo.reset();
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable) {
    if (paramParcelable instanceof SavedState) {
      this.mPendingSavedState = (SavedState)paramParcelable;
      requestLayout();
    } 
  }
  
  public Parcelable onSaveInstanceState() {
    if (this.mPendingSavedState != null)
      return new SavedState(this.mPendingSavedState); 
    SavedState savedState = new SavedState();
    if (getChildCount() > 0) {
      ensureLayoutState();
      boolean bool = this.mLastStackFromEnd ^ this.mShouldReverseLayout;
      savedState.mAnchorLayoutFromEnd = bool;
      if (bool) {
        View view1 = getChildClosestToEnd();
        savedState.mAnchorOffset = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(view1);
        savedState.mAnchorPosition = getPosition(view1);
        return savedState;
      } 
      View view = getChildClosestToStart();
      savedState.mAnchorPosition = getPosition(view);
      savedState.mAnchorOffset = this.mOrientationHelper.getDecoratedStart(view) - this.mOrientationHelper.getStartAfterPadding();
      return savedState;
    } 
    savedState.invalidateAnchor();
    return savedState;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void prepareForDrop(View paramView1, View paramView2, int paramInt1, int paramInt2) {
    assertNotInLayoutOrScroll("Cannot drop a view during a scroll or layout calculation");
    ensureLayoutState();
    resolveShouldLayoutReverse();
    paramInt1 = getPosition(paramView1);
    paramInt2 = getPosition(paramView2);
    if (paramInt1 < paramInt2) {
      paramInt1 = 1;
    } else {
      paramInt1 = -1;
    } 
    if (this.mShouldReverseLayout) {
      if (paramInt1 == 1) {
        scrollToPositionWithOffset(paramInt2, this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedStart(paramView2) + this.mOrientationHelper.getDecoratedMeasurement(paramView1));
        return;
      } 
      scrollToPositionWithOffset(paramInt2, this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(paramView2));
      return;
    } 
    if (paramInt1 == -1) {
      scrollToPositionWithOffset(paramInt2, this.mOrientationHelper.getDecoratedStart(paramView2));
      return;
    } 
    scrollToPositionWithOffset(paramInt2, this.mOrientationHelper.getDecoratedEnd(paramView2) - this.mOrientationHelper.getDecoratedMeasurement(paramView1));
  }
  
  boolean resolveIsInfinite() { return (this.mOrientationHelper.getMode() == 0 && this.mOrientationHelper.getEnd() == 0); }
  
  int scrollBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) {
    if (getChildCount() != 0) {
      int i;
      if (paramInt == 0)
        return 0; 
      this.mLayoutState.mRecycle = true;
      ensureLayoutState();
      if (paramInt > 0) {
        i = 1;
      } else {
        i = -1;
      } 
      int j = Math.abs(paramInt);
      updateLayoutState(i, j, true, paramState);
      int k = this.mLayoutState.mScrollingOffset + fill(paramRecycler, this.mLayoutState, paramState, false);
      if (k < 0)
        return 0; 
      if (j > k)
        paramInt = i * k; 
      this.mOrientationHelper.offsetChildren(-paramInt);
      this.mLayoutState.mLastScrollDelta = paramInt;
      return paramInt;
    } 
    return 0;
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return (this.mOrientation == 1) ? 0 : scrollBy(paramInt, paramRecycler, paramState); }
  
  public void scrollToPosition(int paramInt) {
    this.mPendingScrollPosition = paramInt;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    if (this.mPendingSavedState != null)
      this.mPendingSavedState.invalidateAnchor(); 
    requestLayout();
  }
  
  public void scrollToPositionWithOffset(int paramInt1, int paramInt2) {
    this.mPendingScrollPosition = paramInt1;
    this.mPendingScrollPositionOffset = paramInt2;
    if (this.mPendingSavedState != null)
      this.mPendingSavedState.invalidateAnchor(); 
    requestLayout();
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return (this.mOrientation == 0) ? 0 : scrollBy(paramInt, paramRecycler, paramState); }
  
  public void setInitialPrefetchItemCount(int paramInt) { this.mInitialPrefetchItemCount = paramInt; }
  
  public void setOrientation(int paramInt) {
    if (paramInt != 0 && paramInt != 1) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("invalid orientation:");
      stringBuilder.append(paramInt);
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    assertNotInLayoutOrScroll(null);
    if (paramInt != this.mOrientation || this.mOrientationHelper == null) {
      this.mOrientationHelper = OrientationHelper.createOrientationHelper(this, paramInt);
      this.mAnchorInfo.mOrientationHelper = this.mOrientationHelper;
      this.mOrientation = paramInt;
      requestLayout();
    } 
  }
  
  public void setRecycleChildrenOnDetach(boolean paramBoolean) { this.mRecycleChildrenOnDetach = paramBoolean; }
  
  public void setReverseLayout(boolean paramBoolean) {
    assertNotInLayoutOrScroll(null);
    if (paramBoolean == this.mReverseLayout)
      return; 
    this.mReverseLayout = paramBoolean;
    requestLayout();
  }
  
  public void setSmoothScrollbarEnabled(boolean paramBoolean) { this.mSmoothScrollbarEnabled = paramBoolean; }
  
  public void setStackFromEnd(boolean paramBoolean) {
    assertNotInLayoutOrScroll(null);
    if (this.mStackFromEnd == paramBoolean)
      return; 
    this.mStackFromEnd = paramBoolean;
    requestLayout();
  }
  
  boolean shouldMeasureTwice() { return (getHeightMode() != 1073741824 && getWidthMode() != 1073741824 && hasFlexibleChildInBothOrientations()); }
  
  public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt) {
    LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(paramRecyclerView.getContext());
    linearSmoothScroller.setTargetPosition(paramInt);
    startSmoothScroll(linearSmoothScroller);
  }
  
  public boolean supportsPredictiveItemAnimations() { return (this.mPendingSavedState == null && this.mLastStackFromEnd == this.mStackFromEnd); }
  
  void validateChildOrder() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("validating child count ");
    stringBuilder.append(getChildCount());
    Log.d("LinearLayoutManager", stringBuilder.toString());
    if (getChildCount() < 1)
      return; 
    boolean bool1 = false;
    boolean bool = false;
    int i = getPosition(getChildAt(0));
    int j = this.mOrientationHelper.getDecoratedStart(getChildAt(0));
    if (this.mShouldReverseLayout) {
      for (byte b = 1; b < getChildCount(); b++) {
        View view = getChildAt(b);
        int k = getPosition(view);
        int m = this.mOrientationHelper.getDecoratedStart(view);
        if (k < i) {
          logChildren();
          StringBuilder stringBuilder1 = new StringBuilder();
          stringBuilder1.append("detected invalid position. loc invalid? ");
          if (m < j)
            bool = true; 
          stringBuilder1.append(bool);
          throw new RuntimeException(stringBuilder1.toString());
        } 
        if (m > j) {
          logChildren();
          throw new RuntimeException("detected invalid location");
        } 
      } 
    } else {
      for (byte b = 1; b < getChildCount(); b++) {
        View view = getChildAt(b);
        int k = getPosition(view);
        int m = this.mOrientationHelper.getDecoratedStart(view);
        if (k < i) {
          logChildren();
          StringBuilder stringBuilder1 = new StringBuilder();
          stringBuilder1.append("detected invalid position. loc invalid? ");
          bool = bool1;
          if (m < j)
            bool = true; 
          stringBuilder1.append(bool);
          throw new RuntimeException(stringBuilder1.toString());
        } 
        if (m < j) {
          logChildren();
          throw new RuntimeException("detected invalid location");
        } 
      } 
    } 
  }
  
  static class AnchorInfo {
    int mCoordinate;
    
    boolean mLayoutFromEnd;
    
    OrientationHelper mOrientationHelper;
    
    int mPosition;
    
    boolean mValid;
    
    AnchorInfo() { reset(); }
    
    void assignCoordinateFromPadding() {
      int i;
      if (this.mLayoutFromEnd) {
        i = this.mOrientationHelper.getEndAfterPadding();
      } else {
        i = this.mOrientationHelper.getStartAfterPadding();
      } 
      this.mCoordinate = i;
    }
    
    public void assignFromView(View param1View, int param1Int) {
      if (this.mLayoutFromEnd) {
        this.mCoordinate = this.mOrientationHelper.getDecoratedEnd(param1View) + this.mOrientationHelper.getTotalSpaceChange();
      } else {
        this.mCoordinate = this.mOrientationHelper.getDecoratedStart(param1View);
      } 
      this.mPosition = param1Int;
    }
    
    public void assignFromViewAndKeepVisibleRect(View param1View, int param1Int) {
      int i = this.mOrientationHelper.getTotalSpaceChange();
      if (i >= 0) {
        assignFromView(param1View, param1Int);
        return;
      } 
      this.mPosition = param1Int;
      if (this.mLayoutFromEnd) {
        param1Int = this.mOrientationHelper.getEndAfterPadding() - i - this.mOrientationHelper.getDecoratedEnd(param1View);
        this.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - param1Int;
        if (param1Int > 0) {
          i = this.mOrientationHelper.getDecoratedMeasurement(param1View);
          int j = this.mCoordinate;
          int k = this.mOrientationHelper.getStartAfterPadding();
          i = j - i - k + Math.min(this.mOrientationHelper.getDecoratedStart(param1View) - k, 0);
          if (i < 0) {
            this.mCoordinate += Math.min(param1Int, -i);
            return;
          } 
        } 
      } else {
        int j = this.mOrientationHelper.getDecoratedStart(param1View);
        param1Int = j - this.mOrientationHelper.getStartAfterPadding();
        this.mCoordinate = j;
        if (param1Int > 0) {
          int k = this.mOrientationHelper.getDecoratedMeasurement(param1View);
          int m = this.mOrientationHelper.getEndAfterPadding();
          int n = this.mOrientationHelper.getDecoratedEnd(param1View);
          i = this.mOrientationHelper.getEndAfterPadding() - Math.min(0, m - i - n) - j + k;
          if (i < 0)
            this.mCoordinate -= Math.min(param1Int, -i); 
        } 
      } 
    }
    
    boolean isViewValidAsAnchor(View param1View, RecyclerView.State param1State) {
      RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param1View.getLayoutParams();
      return (!layoutParams.isItemRemoved() && layoutParams.getViewLayoutPosition() >= 0 && layoutParams.getViewLayoutPosition() < param1State.getItemCount());
    }
    
    void reset() {
      this.mPosition = -1;
      this.mCoordinate = Integer.MIN_VALUE;
      this.mLayoutFromEnd = false;
      this.mValid = false;
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("AnchorInfo{mPosition=");
      stringBuilder.append(this.mPosition);
      stringBuilder.append(", mCoordinate=");
      stringBuilder.append(this.mCoordinate);
      stringBuilder.append(", mLayoutFromEnd=");
      stringBuilder.append(this.mLayoutFromEnd);
      stringBuilder.append(", mValid=");
      stringBuilder.append(this.mValid);
      stringBuilder.append('}');
      return stringBuilder.toString();
    }
  }
  
  protected static class LayoutChunkResult {
    public int mConsumed;
    
    public boolean mFinished;
    
    public boolean mFocusable;
    
    public boolean mIgnoreConsumed;
    
    void resetInternal() {
      this.mConsumed = 0;
      this.mFinished = false;
      this.mIgnoreConsumed = false;
      this.mFocusable = false;
    }
  }
  
  static class LayoutState {
    static final int INVALID_LAYOUT = -2147483648;
    
    static final int ITEM_DIRECTION_HEAD = -1;
    
    static final int ITEM_DIRECTION_TAIL = 1;
    
    static final int LAYOUT_END = 1;
    
    static final int LAYOUT_START = -1;
    
    static final int SCROLLING_OFFSET_NaN = -2147483648;
    
    static final String TAG = "LLM#LayoutState";
    
    int mAvailable;
    
    int mCurrentPosition;
    
    int mExtra = 0;
    
    boolean mInfinite;
    
    boolean mIsPreLayout = false;
    
    int mItemDirection;
    
    int mLastScrollDelta;
    
    int mLayoutDirection;
    
    int mOffset;
    
    boolean mRecycle = true;
    
    List<RecyclerView.ViewHolder> mScrapList = null;
    
    int mScrollingOffset;
    
    private View nextViewFromScrapList() {
      int i = this.mScrapList.size();
      for (byte b = 0; b < i; b++) {
        View view = ((RecyclerView.ViewHolder)this.mScrapList.get(b)).itemView;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)view.getLayoutParams();
        if (!layoutParams.isItemRemoved() && this.mCurrentPosition == layoutParams.getViewLayoutPosition()) {
          assignPositionFromScrapList(view);
          return view;
        } 
      } 
      return null;
    }
    
    public void assignPositionFromScrapList() { assignPositionFromScrapList(null); }
    
    public void assignPositionFromScrapList(View param1View) {
      param1View = nextViewInLimitedList(param1View);
      if (param1View == null) {
        this.mCurrentPosition = -1;
        return;
      } 
      this.mCurrentPosition = ((RecyclerView.LayoutParams)param1View.getLayoutParams()).getViewLayoutPosition();
    }
    
    boolean hasMore(RecyclerView.State param1State) { return (this.mCurrentPosition >= 0 && this.mCurrentPosition < param1State.getItemCount()); }
    
    void log() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("avail:");
      stringBuilder.append(this.mAvailable);
      stringBuilder.append(", ind:");
      stringBuilder.append(this.mCurrentPosition);
      stringBuilder.append(", dir:");
      stringBuilder.append(this.mItemDirection);
      stringBuilder.append(", offset:");
      stringBuilder.append(this.mOffset);
      stringBuilder.append(", layoutDir:");
      stringBuilder.append(this.mLayoutDirection);
      Log.d("LLM#LayoutState", stringBuilder.toString());
    }
    
    View next(RecyclerView.Recycler param1Recycler) {
      if (this.mScrapList != null)
        return nextViewFromScrapList(); 
      View view = param1Recycler.getViewForPosition(this.mCurrentPosition);
      this.mCurrentPosition += this.mItemDirection;
      return view;
    }
    
    public View nextViewInLimitedList(View param1View) {
      int j = this.mScrapList.size();
      View view = null;
      int i = Integer.MAX_VALUE;
      byte b = 0;
      while (b < j) {
        View view2 = ((RecyclerView.ViewHolder)this.mScrapList.get(b)).itemView;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)view2.getLayoutParams();
        View view1 = view;
        int k = i;
        if (view2 != param1View)
          if (layoutParams.isItemRemoved()) {
            view1 = view;
            k = i;
          } else {
            int m = (layoutParams.getViewLayoutPosition() - this.mCurrentPosition) * this.mItemDirection;
            if (m < 0) {
              view1 = view;
              k = i;
            } else {
              view1 = view;
              k = i;
              if (m < i) {
                if (m == 0)
                  return view2; 
                view1 = view2;
                k = m;
              } 
            } 
          }  
        b++;
        view = view1;
        i = k;
      } 
      return view;
    }
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static class SavedState implements Parcelable {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
        public LinearLayoutManager.SavedState createFromParcel(Parcel param2Parcel) { return new LinearLayoutManager.SavedState(param2Parcel); }
        
        public LinearLayoutManager.SavedState[] newArray(int param2Int) { return new LinearLayoutManager.SavedState[param2Int]; }
      };
    
    boolean mAnchorLayoutFromEnd;
    
    int mAnchorOffset;
    
    int mAnchorPosition;
    
    public SavedState() {}
    
    SavedState(Parcel param1Parcel) {
      this.mAnchorPosition = param1Parcel.readInt();
      this.mAnchorOffset = param1Parcel.readInt();
      int i = param1Parcel.readInt();
      boolean bool = true;
      if (i != 1)
        bool = false; 
      this.mAnchorLayoutFromEnd = bool;
    }
    
    public SavedState(SavedState param1SavedState) {
      this.mAnchorPosition = param1SavedState.mAnchorPosition;
      this.mAnchorOffset = param1SavedState.mAnchorOffset;
      this.mAnchorLayoutFromEnd = param1SavedState.mAnchorLayoutFromEnd;
    }
    
    public int describeContents() { return 0; }
    
    boolean hasValidAnchor() { return (this.mAnchorPosition >= 0); }
    
    void invalidateAnchor() { this.mAnchorPosition = -1; }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  }
  
  static final class null extends Object implements Parcelable.Creator<SavedState> {
    public LinearLayoutManager.SavedState createFromParcel(Parcel param1Parcel) { return new LinearLayoutManager.SavedState(param1Parcel); }
    
    public LinearLayoutManager.SavedState[] newArray(int param1Int) { return new LinearLayoutManager.SavedState[param1Int]; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/LinearLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */