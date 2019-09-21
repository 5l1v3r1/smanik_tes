package android.support.v7.widget;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class StaggeredGridLayoutManager extends RecyclerView.LayoutManager implements RecyclerView.SmoothScroller.ScrollVectorProvider {
  static final boolean DEBUG = false;
  
  @Deprecated
  public static final int GAP_HANDLING_LAZY = 1;
  
  public static final int GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS = 2;
  
  public static final int GAP_HANDLING_NONE = 0;
  
  public static final int HORIZONTAL = 0;
  
  static final int INVALID_OFFSET = -2147483648;
  
  private static final float MAX_SCROLL_FACTOR = 0.33333334F;
  
  private static final String TAG = "StaggeredGridLManager";
  
  public static final int VERTICAL = 1;
  
  private final AnchorInfo mAnchorInfo = new AnchorInfo();
  
  private final Runnable mCheckForGapsRunnable = new Runnable() {
      public void run() { StaggeredGridLayoutManager.this.checkForGaps(); }
    };
  
  private int mFullSizeSpec;
  
  private int mGapStrategy = 2;
  
  private boolean mLaidOutInvalidFullSpan = false;
  
  private boolean mLastLayoutFromEnd;
  
  private boolean mLastLayoutRTL;
  
  @NonNull
  private final LayoutState mLayoutState;
  
  LazySpanLookup mLazySpanLookup = new LazySpanLookup();
  
  private int mOrientation;
  
  private SavedState mPendingSavedState;
  
  int mPendingScrollPosition = -1;
  
  int mPendingScrollPositionOffset = Integer.MIN_VALUE;
  
  private int[] mPrefetchDistances;
  
  @NonNull
  OrientationHelper mPrimaryOrientation;
  
  private BitSet mRemainingSpans;
  
  boolean mReverseLayout = false;
  
  @NonNull
  OrientationHelper mSecondaryOrientation;
  
  boolean mShouldReverseLayout = false;
  
  private int mSizePerSpan;
  
  private boolean mSmoothScrollbarEnabled = true;
  
  private int mSpanCount = -1;
  
  Span[] mSpans;
  
  private final Rect mTmpRect = new Rect();
  
  public StaggeredGridLayoutManager(int paramInt1, int paramInt2) {
    this.mOrientation = paramInt2;
    setSpanCount(paramInt1);
    this.mLayoutState = new LayoutState();
    createOrientationHelpers();
  }
  
  public StaggeredGridLayoutManager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
    RecyclerView.LayoutManager.Properties properties = getProperties(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setOrientation(properties.orientation);
    setSpanCount(properties.spanCount);
    setReverseLayout(properties.reverseLayout);
    this.mLayoutState = new LayoutState();
    createOrientationHelpers();
  }
  
  private void appendViewToAllSpans(View paramView) {
    for (int i = this.mSpanCount - 1; i >= 0; i--)
      this.mSpans[i].appendToSpan(paramView); 
  }
  
  private void applyPendingSavedState(AnchorInfo paramAnchorInfo) {
    if (this.mPendingSavedState.mSpanOffsetsSize > 0)
      if (this.mPendingSavedState.mSpanOffsetsSize == this.mSpanCount) {
        for (byte b = 0; b < this.mSpanCount; b++) {
          this.mSpans[b].clear();
          int j = this.mPendingSavedState.mSpanOffsets[b];
          int i = j;
          if (j != Integer.MIN_VALUE)
            if (this.mPendingSavedState.mAnchorLayoutFromEnd) {
              i = j + this.mPrimaryOrientation.getEndAfterPadding();
            } else {
              i = j + this.mPrimaryOrientation.getStartAfterPadding();
            }  
          this.mSpans[b].setLine(i);
        } 
      } else {
        this.mPendingSavedState.invalidateSpanInfo();
        this.mPendingSavedState.mAnchorPosition = this.mPendingSavedState.mVisibleAnchorPosition;
      }  
    this.mLastLayoutRTL = this.mPendingSavedState.mLastLayoutRTL;
    setReverseLayout(this.mPendingSavedState.mReverseLayout);
    resolveShouldLayoutReverse();
    if (this.mPendingSavedState.mAnchorPosition != -1) {
      this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
      paramAnchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
    } else {
      paramAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
    } 
    if (this.mPendingSavedState.mSpanLookupSize > 1) {
      this.mLazySpanLookup.mData = this.mPendingSavedState.mSpanLookup;
      this.mLazySpanLookup.mFullSpanItems = this.mPendingSavedState.mFullSpanItems;
    } 
  }
  
  private void attachViewToSpans(View paramView, LayoutParams paramLayoutParams, LayoutState paramLayoutState) {
    if (paramLayoutState.mLayoutDirection == 1) {
      if (paramLayoutParams.mFullSpan) {
        appendViewToAllSpans(paramView);
        return;
      } 
      paramLayoutParams.mSpan.appendToSpan(paramView);
      return;
    } 
    if (paramLayoutParams.mFullSpan) {
      prependViewToAllSpans(paramView);
      return;
    } 
    paramLayoutParams.mSpan.prependToSpan(paramView);
  }
  
  private int calculateScrollDirectionForPosition(int paramInt) {
    boolean bool;
    int j = getChildCount();
    int i = -1;
    if (j == 0) {
      paramInt = i;
      if (this.mShouldReverseLayout)
        paramInt = 1; 
      return paramInt;
    } 
    if (paramInt < getFirstChildPosition()) {
      bool = true;
    } else {
      bool = false;
    } 
    return (bool != this.mShouldReverseLayout) ? -1 : 1;
  }
  
  private boolean checkSpanForGap(Span paramSpan) {
    if (this.mShouldReverseLayout) {
      if (paramSpan.getEndLine() < this.mPrimaryOrientation.getEndAfterPadding())
        return (paramSpan.getLayoutParams((View)paramSpan.mViews.get(paramSpan.mViews.size() - 1))).mFullSpan ^ true; 
    } else if (paramSpan.getStartLine() > this.mPrimaryOrientation.getStartAfterPadding()) {
      return (paramSpan.getLayoutParams((View)paramSpan.mViews.get(0))).mFullSpan ^ true;
    } 
    return false;
  }
  
  private int computeScrollExtent(RecyclerView.State paramState) { return (getChildCount() == 0) ? 0 : ScrollbarHelper.computeScrollExtent(paramState, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart(this.mSmoothScrollbarEnabled ^ true), findFirstVisibleItemClosestToEnd(this.mSmoothScrollbarEnabled ^ true), this, this.mSmoothScrollbarEnabled); }
  
  private int computeScrollOffset(RecyclerView.State paramState) { return (getChildCount() == 0) ? 0 : ScrollbarHelper.computeScrollOffset(paramState, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart(this.mSmoothScrollbarEnabled ^ true), findFirstVisibleItemClosestToEnd(this.mSmoothScrollbarEnabled ^ true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout); }
  
  private int computeScrollRange(RecyclerView.State paramState) { return (getChildCount() == 0) ? 0 : ScrollbarHelper.computeScrollRange(paramState, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart(this.mSmoothScrollbarEnabled ^ true), findFirstVisibleItemClosestToEnd(this.mSmoothScrollbarEnabled ^ true), this, this.mSmoothScrollbarEnabled); }
  
  private int convertFocusDirectionToLayoutDirection(int paramInt) {
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
  
  private LazySpanLookup.FullSpanItem createFullSpanItemFromEnd(int paramInt) {
    LazySpanLookup.FullSpanItem fullSpanItem = new LazySpanLookup.FullSpanItem();
    fullSpanItem.mGapPerSpan = new int[this.mSpanCount];
    for (byte b = 0; b < this.mSpanCount; b++)
      fullSpanItem.mGapPerSpan[b] = paramInt - this.mSpans[b].getEndLine(paramInt); 
    return fullSpanItem;
  }
  
  private LazySpanLookup.FullSpanItem createFullSpanItemFromStart(int paramInt) {
    LazySpanLookup.FullSpanItem fullSpanItem = new LazySpanLookup.FullSpanItem();
    fullSpanItem.mGapPerSpan = new int[this.mSpanCount];
    for (byte b = 0; b < this.mSpanCount; b++)
      fullSpanItem.mGapPerSpan[b] = this.mSpans[b].getStartLine(paramInt) - paramInt; 
    return fullSpanItem;
  }
  
  private void createOrientationHelpers() { this.mSecondaryOrientation = (this.mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, this.mOrientation)).createOrientationHelper(this, 1 - this.mOrientation); }
  
  private int fill(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState, RecyclerView.State paramState) {
    int i;
    this.mRemainingSpans.set(0, this.mSpanCount, true);
    if (this.mLayoutState.mInfinite) {
      if (paramLayoutState.mLayoutDirection == 1) {
        i = Integer.MAX_VALUE;
      } else {
        i = Integer.MIN_VALUE;
      } 
    } else if (paramLayoutState.mLayoutDirection == 1) {
      i = paramLayoutState.mEndLine + paramLayoutState.mAvailable;
    } else {
      i = paramLayoutState.mStartLine - paramLayoutState.mAvailable;
    } 
    updateAllRemainingSpans(paramLayoutState.mLayoutDirection, i);
    if (this.mShouldReverseLayout) {
      k = this.mPrimaryOrientation.getEndAfterPadding();
    } else {
      k = this.mPrimaryOrientation.getStartAfterPadding();
    } 
    int j;
    for (j = 0; paramLayoutState.hasMore(paramState) && (this.mLayoutState.mInfinite || !this.mRemainingSpans.isEmpty()); j = 1) {
      Span span;
      int i1;
      int n;
      int m;
      View view = paramLayoutState.next(paramRecycler);
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      int i2 = layoutParams.getViewLayoutPosition();
      j = this.mLazySpanLookup.getSpan(i2);
      if (j == -1) {
        i1 = 1;
      } else {
        i1 = 0;
      } 
      if (i1) {
        if (layoutParams.mFullSpan) {
          span = this.mSpans[0];
        } else {
          span = getNextSpan(paramLayoutState);
        } 
        this.mLazySpanLookup.setSpan(i2, span);
      } else {
        span = this.mSpans[j];
      } 
      layoutParams.mSpan = span;
      if (paramLayoutState.mLayoutDirection == 1) {
        addView(view);
      } else {
        addView(view, 0);
      } 
      measureChildWithDecorationsAndMargin(view, layoutParams, false);
      if (paramLayoutState.mLayoutDirection == 1) {
        if (layoutParams.mFullSpan) {
          j = getMaxEnd(k);
        } else {
          j = span.getEndLine(k);
        } 
        m = this.mPrimaryOrientation.getDecoratedMeasurement(view);
        if (i1 && layoutParams.mFullSpan) {
          LazySpanLookup.FullSpanItem fullSpanItem = createFullSpanItemFromEnd(j);
          fullSpanItem.mGapDir = -1;
          fullSpanItem.mPosition = i2;
          this.mLazySpanLookup.addFullSpanItem(fullSpanItem);
        } 
        n = m + j;
        m = j;
      } else {
        if (layoutParams.mFullSpan) {
          j = getMinStart(k);
        } else {
          j = span.getStartLine(k);
        } 
        m = j - this.mPrimaryOrientation.getDecoratedMeasurement(view);
        if (i1 && layoutParams.mFullSpan) {
          LazySpanLookup.FullSpanItem fullSpanItem = createFullSpanItemFromStart(j);
          fullSpanItem.mGapDir = 1;
          fullSpanItem.mPosition = i2;
          this.mLazySpanLookup.addFullSpanItem(fullSpanItem);
        } 
        n = j;
      } 
      if (layoutParams.mFullSpan && paramLayoutState.mItemDirection == -1)
        if (i1) {
          this.mLaidOutInvalidFullSpan = true;
        } else {
          boolean bool;
          if (paramLayoutState.mLayoutDirection == 1) {
            bool = areAllEndsEqual();
          } else {
            bool = areAllStartsEqual();
          } 
          if (bool ^ true) {
            LazySpanLookup.FullSpanItem fullSpanItem = this.mLazySpanLookup.getFullSpanItem(i2);
            if (fullSpanItem != null)
              fullSpanItem.mHasUnwantedGapAfter = true; 
            this.mLaidOutInvalidFullSpan = true;
          } 
        }  
      attachViewToSpans(view, layoutParams, paramLayoutState);
      if (isLayoutRTL() && this.mOrientation == 1) {
        if (layoutParams.mFullSpan) {
          j = this.mSecondaryOrientation.getEndAfterPadding();
        } else {
          j = this.mSecondaryOrientation.getEndAfterPadding() - (this.mSpanCount - 1 - span.mIndex) * this.mSizePerSpan;
        } 
        i2 = this.mSecondaryOrientation.getDecoratedMeasurement(view);
        i1 = j;
        j -= i2;
        i2 = i1;
      } else {
        if (layoutParams.mFullSpan) {
          j = this.mSecondaryOrientation.getStartAfterPadding();
        } else {
          j = span.mIndex * this.mSizePerSpan + this.mSecondaryOrientation.getStartAfterPadding();
        } 
        i2 = this.mSecondaryOrientation.getDecoratedMeasurement(view);
        i1 = j;
        i2 += j;
        j = i1;
      } 
      if (this.mOrientation == 1) {
        layoutDecoratedWithMargins(view, j, m, i2, n);
      } else {
        layoutDecoratedWithMargins(view, m, j, n, i2);
      } 
      if (layoutParams.mFullSpan) {
        updateAllRemainingSpans(this.mLayoutState.mLayoutDirection, i);
      } else {
        updateRemainingSpans(span, this.mLayoutState.mLayoutDirection, i);
      } 
      recycle(paramRecycler, this.mLayoutState);
      if (this.mLayoutState.mStopInFocusable && view.hasFocusable())
        if (layoutParams.mFullSpan) {
          this.mRemainingSpans.clear();
        } else {
          this.mRemainingSpans.set(span.mIndex, false);
        }  
    } 
    int k = 0;
    if (j == 0)
      recycle(paramRecycler, this.mLayoutState); 
    if (this.mLayoutState.mLayoutDirection == -1) {
      i = getMinStart(this.mPrimaryOrientation.getStartAfterPadding());
      i = this.mPrimaryOrientation.getStartAfterPadding() - i;
    } else {
      i = getMaxEnd(this.mPrimaryOrientation.getEndAfterPadding()) - this.mPrimaryOrientation.getEndAfterPadding();
    } 
    j = k;
    if (i > 0)
      j = Math.min(paramLayoutState.mAvailable, i); 
    return j;
  }
  
  private int findFirstReferenceChildPosition(int paramInt) {
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      int j = getPosition(getChildAt(b));
      if (j >= 0 && j < paramInt)
        return j; 
    } 
    return 0;
  }
  
  private int findLastReferenceChildPosition(int paramInt) {
    for (int i = getChildCount() - 1; i >= 0; i--) {
      int j = getPosition(getChildAt(i));
      if (j >= 0 && j < paramInt)
        return j; 
    } 
    return 0;
  }
  
  private void fixEndGap(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean) {
    int i = getMaxEnd(-2147483648);
    if (i == Integer.MIN_VALUE)
      return; 
    i = this.mPrimaryOrientation.getEndAfterPadding() - i;
    if (i > 0) {
      i -= -scrollBy(-i, paramRecycler, paramState);
      if (paramBoolean && i > 0)
        this.mPrimaryOrientation.offsetChildren(i); 
      return;
    } 
  }
  
  private void fixStartGap(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean) {
    int i = getMinStart(2147483647);
    if (i == Integer.MAX_VALUE)
      return; 
    i -= this.mPrimaryOrientation.getStartAfterPadding();
    if (i > 0) {
      i -= scrollBy(i, paramRecycler, paramState);
      if (paramBoolean && i > 0)
        this.mPrimaryOrientation.offsetChildren(-i); 
      return;
    } 
  }
  
  private int getMaxEnd(int paramInt) {
    int i = this.mSpans[0].getEndLine(paramInt);
    byte b = 1;
    while (b < this.mSpanCount) {
      int k = this.mSpans[b].getEndLine(paramInt);
      int j = i;
      if (k > i)
        j = k; 
      b++;
      i = j;
    } 
    return i;
  }
  
  private int getMaxStart(int paramInt) {
    int i = this.mSpans[0].getStartLine(paramInt);
    byte b = 1;
    while (b < this.mSpanCount) {
      int k = this.mSpans[b].getStartLine(paramInt);
      int j = i;
      if (k > i)
        j = k; 
      b++;
      i = j;
    } 
    return i;
  }
  
  private int getMinEnd(int paramInt) {
    int i = this.mSpans[0].getEndLine(paramInt);
    byte b = 1;
    while (b < this.mSpanCount) {
      int k = this.mSpans[b].getEndLine(paramInt);
      int j = i;
      if (k < i)
        j = k; 
      b++;
      i = j;
    } 
    return i;
  }
  
  private int getMinStart(int paramInt) {
    int i = this.mSpans[0].getStartLine(paramInt);
    byte b = 1;
    while (b < this.mSpanCount) {
      int k = this.mSpans[b].getStartLine(paramInt);
      int j = i;
      if (k < i)
        j = k; 
      b++;
      i = j;
    } 
    return i;
  }
  
  private Span getNextSpan(LayoutState paramLayoutState) {
    byte b2;
    byte b1;
    boolean bool = preferLastSpan(paramLayoutState.mLayoutDirection);
    int i = -1;
    if (bool) {
      b1 = this.mSpanCount - 1;
      b2 = -1;
    } else {
      b1 = 0;
      i = this.mSpanCount;
      b2 = 1;
    } 
    int j = paramLayoutState.mLayoutDirection;
    Span span2 = null;
    paramLayoutState = null;
    if (j == 1) {
      Span span;
      j = Integer.MAX_VALUE;
      int m = this.mPrimaryOrientation.getStartAfterPadding();
      while (b1 != i) {
        span2 = this.mSpans[b1];
        int i1 = span2.getEndLine(m);
        int n = j;
        if (i1 < j) {
          span = span2;
          n = i1;
        } 
        b1 += b2;
        j = n;
      } 
      return span;
    } 
    j = Integer.MIN_VALUE;
    int k = this.mPrimaryOrientation.getEndAfterPadding();
    Span span1 = span2;
    while (b1 != i) {
      span2 = this.mSpans[b1];
      int n = span2.getStartLine(k);
      int m = j;
      if (n > j) {
        span1 = span2;
        m = n;
      } 
      b1 += b2;
      j = m;
    } 
    return span1;
  }
  
  private void handleUpdate(int paramInt1, int paramInt2, int paramInt3) {
    if (this.mShouldReverseLayout) {
      int k = getLastChildPosition();
    } else {
      int k = getFirstChildPosition();
    } 
    if (paramInt3 == 8) {
      if (paramInt1 < paramInt2) {
        i = paramInt2 + 1;
      } else {
        int k = paramInt1 + 1;
        i = paramInt2;
        this.mLazySpanLookup.invalidateAfter(i);
      } 
    } else {
      i = paramInt1 + paramInt2;
    } 
    int j = i;
    int i = paramInt1;
    this.mLazySpanLookup.invalidateAfter(i);
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, int paramInt1, int paramInt2, boolean paramBoolean) {
    calculateItemDecorationsForChild(paramView, this.mTmpRect);
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    paramInt1 = updateSpecWithExtra(paramInt1, layoutParams.leftMargin + this.mTmpRect.left, layoutParams.rightMargin + this.mTmpRect.right);
    paramInt2 = updateSpecWithExtra(paramInt2, layoutParams.topMargin + this.mTmpRect.top, layoutParams.bottomMargin + this.mTmpRect.bottom);
    if (paramBoolean) {
      paramBoolean = shouldReMeasureChild(paramView, paramInt1, paramInt2, layoutParams);
    } else {
      paramBoolean = shouldMeasureChild(paramView, paramInt1, paramInt2, layoutParams);
    } 
    if (paramBoolean)
      paramView.measure(paramInt1, paramInt2); 
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, LayoutParams paramLayoutParams, boolean paramBoolean) {
    if (paramLayoutParams.mFullSpan) {
      if (this.mOrientation == 1) {
        measureChildWithDecorationsAndMargin(paramView, this.mFullSizeSpec, getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom(), paramLayoutParams.height, true), paramBoolean);
        return;
      } 
      measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight(), paramLayoutParams.width, true), this.mFullSizeSpec, paramBoolean);
      return;
    } 
    if (this.mOrientation == 1) {
      measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(this.mSizePerSpan, getWidthMode(), 0, paramLayoutParams.width, false), getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom(), paramLayoutParams.height, true), paramBoolean);
      return;
    } 
    measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight(), paramLayoutParams.width, true), getChildMeasureSpec(this.mSizePerSpan, getHeightMode(), 0, paramLayoutParams.height, false), paramBoolean);
  }
  
  private void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean) { // Byte code:
    //   0: aload_0
    //   1: getfield mAnchorInfo : Landroid/support/v7/widget/StaggeredGridLayoutManager$AnchorInfo;
    //   4: astore #7
    //   6: aload_0
    //   7: getfield mPendingSavedState : Landroid/support/v7/widget/StaggeredGridLayoutManager$SavedState;
    //   10: ifnonnull -> 21
    //   13: aload_0
    //   14: getfield mPendingScrollPosition : I
    //   17: iconst_m1
    //   18: if_icmpeq -> 39
    //   21: aload_2
    //   22: invokevirtual getItemCount : ()I
    //   25: ifne -> 39
    //   28: aload_0
    //   29: aload_1
    //   30: invokevirtual removeAndRecycleAllViews : (Landroid/support/v7/widget/RecyclerView$Recycler;)V
    //   33: aload #7
    //   35: invokevirtual reset : ()V
    //   38: return
    //   39: aload #7
    //   41: getfield mValid : Z
    //   44: istore #6
    //   46: iconst_1
    //   47: istore #5
    //   49: iload #6
    //   51: ifeq -> 78
    //   54: aload_0
    //   55: getfield mPendingScrollPosition : I
    //   58: iconst_m1
    //   59: if_icmpne -> 78
    //   62: aload_0
    //   63: getfield mPendingSavedState : Landroid/support/v7/widget/StaggeredGridLayoutManager$SavedState;
    //   66: ifnull -> 72
    //   69: goto -> 78
    //   72: iconst_0
    //   73: istore #4
    //   75: goto -> 81
    //   78: iconst_1
    //   79: istore #4
    //   81: iload #4
    //   83: ifeq -> 133
    //   86: aload #7
    //   88: invokevirtual reset : ()V
    //   91: aload_0
    //   92: getfield mPendingSavedState : Landroid/support/v7/widget/StaggeredGridLayoutManager$SavedState;
    //   95: ifnull -> 107
    //   98: aload_0
    //   99: aload #7
    //   101: invokespecial applyPendingSavedState : (Landroid/support/v7/widget/StaggeredGridLayoutManager$AnchorInfo;)V
    //   104: goto -> 120
    //   107: aload_0
    //   108: invokespecial resolveShouldLayoutReverse : ()V
    //   111: aload #7
    //   113: aload_0
    //   114: getfield mShouldReverseLayout : Z
    //   117: putfield mLayoutFromEnd : Z
    //   120: aload_0
    //   121: aload_2
    //   122: aload #7
    //   124: invokevirtual updateAnchorInfoForLayout : (Landroid/support/v7/widget/RecyclerView$State;Landroid/support/v7/widget/StaggeredGridLayoutManager$AnchorInfo;)V
    //   127: aload #7
    //   129: iconst_1
    //   130: putfield mValid : Z
    //   133: aload_0
    //   134: getfield mPendingSavedState : Landroid/support/v7/widget/StaggeredGridLayoutManager$SavedState;
    //   137: ifnonnull -> 184
    //   140: aload_0
    //   141: getfield mPendingScrollPosition : I
    //   144: iconst_m1
    //   145: if_icmpne -> 184
    //   148: aload #7
    //   150: getfield mLayoutFromEnd : Z
    //   153: aload_0
    //   154: getfield mLastLayoutFromEnd : Z
    //   157: if_icmpne -> 171
    //   160: aload_0
    //   161: invokevirtual isLayoutRTL : ()Z
    //   164: aload_0
    //   165: getfield mLastLayoutRTL : Z
    //   168: if_icmpeq -> 184
    //   171: aload_0
    //   172: getfield mLazySpanLookup : Landroid/support/v7/widget/StaggeredGridLayoutManager$LazySpanLookup;
    //   175: invokevirtual clear : ()V
    //   178: aload #7
    //   180: iconst_1
    //   181: putfield mInvalidateOffsets : Z
    //   184: aload_0
    //   185: invokevirtual getChildCount : ()I
    //   188: ifle -> 392
    //   191: aload_0
    //   192: getfield mPendingSavedState : Landroid/support/v7/widget/StaggeredGridLayoutManager$SavedState;
    //   195: ifnull -> 209
    //   198: aload_0
    //   199: getfield mPendingSavedState : Landroid/support/v7/widget/StaggeredGridLayoutManager$SavedState;
    //   202: getfield mSpanOffsetsSize : I
    //   205: iconst_1
    //   206: if_icmpge -> 392
    //   209: aload #7
    //   211: getfield mInvalidateOffsets : Z
    //   214: ifeq -> 273
    //   217: iconst_0
    //   218: istore #4
    //   220: iload #4
    //   222: aload_0
    //   223: getfield mSpanCount : I
    //   226: if_icmpge -> 392
    //   229: aload_0
    //   230: getfield mSpans : [Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   233: iload #4
    //   235: aaload
    //   236: invokevirtual clear : ()V
    //   239: aload #7
    //   241: getfield mOffset : I
    //   244: ldc -2147483648
    //   246: if_icmpeq -> 264
    //   249: aload_0
    //   250: getfield mSpans : [Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   253: iload #4
    //   255: aaload
    //   256: aload #7
    //   258: getfield mOffset : I
    //   261: invokevirtual setLine : (I)V
    //   264: iload #4
    //   266: iconst_1
    //   267: iadd
    //   268: istore #4
    //   270: goto -> 220
    //   273: iload #4
    //   275: ifne -> 341
    //   278: aload_0
    //   279: getfield mAnchorInfo : Landroid/support/v7/widget/StaggeredGridLayoutManager$AnchorInfo;
    //   282: getfield mSpanReferenceLines : [I
    //   285: ifnonnull -> 291
    //   288: goto -> 341
    //   291: iconst_0
    //   292: istore #4
    //   294: iload #4
    //   296: aload_0
    //   297: getfield mSpanCount : I
    //   300: if_icmpge -> 392
    //   303: aload_0
    //   304: getfield mSpans : [Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   307: iload #4
    //   309: aaload
    //   310: astore #8
    //   312: aload #8
    //   314: invokevirtual clear : ()V
    //   317: aload #8
    //   319: aload_0
    //   320: getfield mAnchorInfo : Landroid/support/v7/widget/StaggeredGridLayoutManager$AnchorInfo;
    //   323: getfield mSpanReferenceLines : [I
    //   326: iload #4
    //   328: iaload
    //   329: invokevirtual setLine : (I)V
    //   332: iload #4
    //   334: iconst_1
    //   335: iadd
    //   336: istore #4
    //   338: goto -> 294
    //   341: iconst_0
    //   342: istore #4
    //   344: iload #4
    //   346: aload_0
    //   347: getfield mSpanCount : I
    //   350: if_icmpge -> 381
    //   353: aload_0
    //   354: getfield mSpans : [Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   357: iload #4
    //   359: aaload
    //   360: aload_0
    //   361: getfield mShouldReverseLayout : Z
    //   364: aload #7
    //   366: getfield mOffset : I
    //   369: invokevirtual cacheReferenceLineAndClear : (ZI)V
    //   372: iload #4
    //   374: iconst_1
    //   375: iadd
    //   376: istore #4
    //   378: goto -> 344
    //   381: aload_0
    //   382: getfield mAnchorInfo : Landroid/support/v7/widget/StaggeredGridLayoutManager$AnchorInfo;
    //   385: aload_0
    //   386: getfield mSpans : [Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   389: invokevirtual saveSpanReferenceLines : ([Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;)V
    //   392: aload_0
    //   393: aload_1
    //   394: invokevirtual detachAndScrapAttachedViews : (Landroid/support/v7/widget/RecyclerView$Recycler;)V
    //   397: aload_0
    //   398: getfield mLayoutState : Landroid/support/v7/widget/LayoutState;
    //   401: iconst_0
    //   402: putfield mRecycle : Z
    //   405: aload_0
    //   406: iconst_0
    //   407: putfield mLaidOutInvalidFullSpan : Z
    //   410: aload_0
    //   411: aload_0
    //   412: getfield mSecondaryOrientation : Landroid/support/v7/widget/OrientationHelper;
    //   415: invokevirtual getTotalSpace : ()I
    //   418: invokevirtual updateMeasureSpecs : (I)V
    //   421: aload_0
    //   422: aload #7
    //   424: getfield mPosition : I
    //   427: aload_2
    //   428: invokespecial updateLayoutState : (ILandroid/support/v7/widget/RecyclerView$State;)V
    //   431: aload #7
    //   433: getfield mLayoutFromEnd : Z
    //   436: ifeq -> 494
    //   439: aload_0
    //   440: iconst_m1
    //   441: invokespecial setLayoutStateDirection : (I)V
    //   444: aload_0
    //   445: aload_1
    //   446: aload_0
    //   447: getfield mLayoutState : Landroid/support/v7/widget/LayoutState;
    //   450: aload_2
    //   451: invokespecial fill : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/LayoutState;Landroid/support/v7/widget/RecyclerView$State;)I
    //   454: pop
    //   455: aload_0
    //   456: iconst_1
    //   457: invokespecial setLayoutStateDirection : (I)V
    //   460: aload_0
    //   461: getfield mLayoutState : Landroid/support/v7/widget/LayoutState;
    //   464: aload #7
    //   466: getfield mPosition : I
    //   469: aload_0
    //   470: getfield mLayoutState : Landroid/support/v7/widget/LayoutState;
    //   473: getfield mItemDirection : I
    //   476: iadd
    //   477: putfield mCurrentPosition : I
    //   480: aload_0
    //   481: aload_1
    //   482: aload_0
    //   483: getfield mLayoutState : Landroid/support/v7/widget/LayoutState;
    //   486: aload_2
    //   487: invokespecial fill : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/LayoutState;Landroid/support/v7/widget/RecyclerView$State;)I
    //   490: pop
    //   491: goto -> 546
    //   494: aload_0
    //   495: iconst_1
    //   496: invokespecial setLayoutStateDirection : (I)V
    //   499: aload_0
    //   500: aload_1
    //   501: aload_0
    //   502: getfield mLayoutState : Landroid/support/v7/widget/LayoutState;
    //   505: aload_2
    //   506: invokespecial fill : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/LayoutState;Landroid/support/v7/widget/RecyclerView$State;)I
    //   509: pop
    //   510: aload_0
    //   511: iconst_m1
    //   512: invokespecial setLayoutStateDirection : (I)V
    //   515: aload_0
    //   516: getfield mLayoutState : Landroid/support/v7/widget/LayoutState;
    //   519: aload #7
    //   521: getfield mPosition : I
    //   524: aload_0
    //   525: getfield mLayoutState : Landroid/support/v7/widget/LayoutState;
    //   528: getfield mItemDirection : I
    //   531: iadd
    //   532: putfield mCurrentPosition : I
    //   535: aload_0
    //   536: aload_1
    //   537: aload_0
    //   538: getfield mLayoutState : Landroid/support/v7/widget/LayoutState;
    //   541: aload_2
    //   542: invokespecial fill : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/LayoutState;Landroid/support/v7/widget/RecyclerView$State;)I
    //   545: pop
    //   546: aload_0
    //   547: invokespecial repositionToWrapContentIfNecessary : ()V
    //   550: aload_0
    //   551: invokevirtual getChildCount : ()I
    //   554: ifle -> 595
    //   557: aload_0
    //   558: getfield mShouldReverseLayout : Z
    //   561: ifeq -> 581
    //   564: aload_0
    //   565: aload_1
    //   566: aload_2
    //   567: iconst_1
    //   568: invokespecial fixEndGap : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/RecyclerView$State;Z)V
    //   571: aload_0
    //   572: aload_1
    //   573: aload_2
    //   574: iconst_0
    //   575: invokespecial fixStartGap : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/RecyclerView$State;Z)V
    //   578: goto -> 595
    //   581: aload_0
    //   582: aload_1
    //   583: aload_2
    //   584: iconst_1
    //   585: invokespecial fixStartGap : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/RecyclerView$State;Z)V
    //   588: aload_0
    //   589: aload_1
    //   590: aload_2
    //   591: iconst_0
    //   592: invokespecial fixEndGap : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/RecyclerView$State;Z)V
    //   595: iload_3
    //   596: ifeq -> 671
    //   599: aload_2
    //   600: invokevirtual isPreLayout : ()Z
    //   603: ifne -> 671
    //   606: aload_0
    //   607: getfield mGapStrategy : I
    //   610: ifeq -> 640
    //   613: aload_0
    //   614: invokevirtual getChildCount : ()I
    //   617: ifle -> 640
    //   620: aload_0
    //   621: getfield mLaidOutInvalidFullSpan : Z
    //   624: ifne -> 634
    //   627: aload_0
    //   628: invokevirtual hasGapsToFix : ()Landroid/view/View;
    //   631: ifnull -> 640
    //   634: iconst_1
    //   635: istore #4
    //   637: goto -> 643
    //   640: iconst_0
    //   641: istore #4
    //   643: iload #4
    //   645: ifeq -> 671
    //   648: aload_0
    //   649: aload_0
    //   650: getfield mCheckForGapsRunnable : Ljava/lang/Runnable;
    //   653: invokevirtual removeCallbacks : (Ljava/lang/Runnable;)Z
    //   656: pop
    //   657: aload_0
    //   658: invokevirtual checkForGaps : ()Z
    //   661: ifeq -> 671
    //   664: iload #5
    //   666: istore #4
    //   668: goto -> 674
    //   671: iconst_0
    //   672: istore #4
    //   674: aload_2
    //   675: invokevirtual isPreLayout : ()Z
    //   678: ifeq -> 688
    //   681: aload_0
    //   682: getfield mAnchorInfo : Landroid/support/v7/widget/StaggeredGridLayoutManager$AnchorInfo;
    //   685: invokevirtual reset : ()V
    //   688: aload_0
    //   689: aload #7
    //   691: getfield mLayoutFromEnd : Z
    //   694: putfield mLastLayoutFromEnd : Z
    //   697: aload_0
    //   698: aload_0
    //   699: invokevirtual isLayoutRTL : ()Z
    //   702: putfield mLastLayoutRTL : Z
    //   705: iload #4
    //   707: ifeq -> 724
    //   710: aload_0
    //   711: getfield mAnchorInfo : Landroid/support/v7/widget/StaggeredGridLayoutManager$AnchorInfo;
    //   714: invokevirtual reset : ()V
    //   717: aload_0
    //   718: aload_1
    //   719: aload_2
    //   720: iconst_0
    //   721: invokespecial onLayoutChildren : (Landroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/RecyclerView$State;Z)V
    //   724: return }
  
  private boolean preferLastSpan(int paramInt) {
    boolean bool;
    int i = this.mOrientation;
    boolean bool1 = false;
    byte b = 0;
    if (i == 0) {
      if (paramInt == -1) {
        bool = true;
      } else {
        bool = false;
      } 
      bool1 = b;
      if (bool != this.mShouldReverseLayout)
        bool1 = true; 
      return bool1;
    } 
    if (paramInt == -1) {
      bool = true;
    } else {
      bool = false;
    } 
    if (bool == this.mShouldReverseLayout) {
      bool = true;
    } else {
      bool = false;
    } 
    if (bool == isLayoutRTL())
      bool1 = true; 
    return bool1;
  }
  
  private void prependViewToAllSpans(View paramView) {
    for (int i = this.mSpanCount - 1; i >= 0; i--)
      this.mSpans[i].prependToSpan(paramView); 
  }
  
  private void recycle(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState) {
    if (paramLayoutState.mRecycle) {
      if (paramLayoutState.mInfinite)
        return; 
      if (paramLayoutState.mAvailable == 0) {
        if (paramLayoutState.mLayoutDirection == -1) {
          recycleFromEnd(paramRecycler, paramLayoutState.mEndLine);
          return;
        } 
        recycleFromStart(paramRecycler, paramLayoutState.mStartLine);
        return;
      } 
      if (paramLayoutState.mLayoutDirection == -1) {
        int j = paramLayoutState.mStartLine - getMaxStart(paramLayoutState.mStartLine);
        if (j < 0) {
          j = paramLayoutState.mEndLine;
        } else {
          j = paramLayoutState.mEndLine - Math.min(j, paramLayoutState.mAvailable);
        } 
        recycleFromEnd(paramRecycler, j);
        return;
      } 
      int i = getMinEnd(paramLayoutState.mEndLine) - paramLayoutState.mEndLine;
      if (i < 0) {
        i = paramLayoutState.mStartLine;
      } else {
        int j = paramLayoutState.mStartLine;
        i = Math.min(i, paramLayoutState.mAvailable) + j;
      } 
      recycleFromStart(paramRecycler, i);
      return;
    } 
  }
  
  private void recycleFromEnd(RecyclerView.Recycler paramRecycler, int paramInt) {
    int i = getChildCount() - 1;
    while (i >= 0) {
      View view = getChildAt(i);
      if (this.mPrimaryOrientation.getDecoratedStart(view) >= paramInt && this.mPrimaryOrientation.getTransformedStartWithDecoration(view) >= paramInt) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.mFullSpan) {
          byte b2;
          boolean bool = false;
          byte b1 = 0;
          while (true) {
            b2 = bool;
            if (b1 < this.mSpanCount) {
              if ((this.mSpans[b1]).mViews.size() == 1)
                return; 
              b1++;
              continue;
            } 
            break;
          } 
          while (b2 < this.mSpanCount) {
            this.mSpans[b2].popEnd();
            b2++;
          } 
        } else {
          if (layoutParams.mSpan.mViews.size() == 1)
            return; 
          layoutParams.mSpan.popEnd();
        } 
        removeAndRecycleView(view, paramRecycler);
        i--;
        continue;
      } 
      return;
    } 
  }
  
  private void recycleFromStart(RecyclerView.Recycler paramRecycler, int paramInt) {
    while (getChildCount() > 0) {
      boolean bool = false;
      View view = getChildAt(0);
      if (this.mPrimaryOrientation.getDecoratedEnd(view) <= paramInt && this.mPrimaryOrientation.getTransformedEndWithDecoration(view) <= paramInt) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.mFullSpan) {
          byte b2;
          byte b1 = 0;
          while (true) {
            b2 = bool;
            if (b1 < this.mSpanCount) {
              if ((this.mSpans[b1]).mViews.size() == 1)
                return; 
              b1++;
              continue;
            } 
            break;
          } 
          while (b2 < this.mSpanCount) {
            this.mSpans[b2].popStart();
            b2++;
          } 
        } else {
          if (layoutParams.mSpan.mViews.size() == 1)
            return; 
          layoutParams.mSpan.popStart();
        } 
        removeAndRecycleView(view, paramRecycler);
        continue;
      } 
      return;
    } 
  }
  
  private void repositionToWrapContentIfNecessary() {
    if (this.mSecondaryOrientation.getMode() == 1073741824)
      return; 
    int m = getChildCount();
    int j = 0;
    int i = 0;
    float f = 0.0F;
    while (i < m) {
      View view = getChildAt(i);
      float f1 = this.mSecondaryOrientation.getDecoratedMeasurement(view);
      if (f1 >= f) {
        float f2 = f1;
        if (((LayoutParams)view.getLayoutParams()).isFullSpan())
          f2 = f1 * 1.0F / this.mSpanCount; 
        f = Math.max(f, f2);
      } 
      i++;
    } 
    int n = this.mSizePerSpan;
    int k = Math.round(f * this.mSpanCount);
    i = k;
    if (this.mSecondaryOrientation.getMode() == Integer.MIN_VALUE)
      i = Math.min(k, this.mSecondaryOrientation.getTotalSpace()); 
    updateMeasureSpecs(i);
    i = j;
    if (this.mSizePerSpan == n)
      return; 
    while (i < m) {
      View view = getChildAt(i);
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      if (!layoutParams.mFullSpan)
        if (isLayoutRTL() && this.mOrientation == 1) {
          view.offsetLeftAndRight(-(this.mSpanCount - 1 - layoutParams.mSpan.mIndex) * this.mSizePerSpan - -(this.mSpanCount - 1 - layoutParams.mSpan.mIndex) * n);
        } else {
          j = layoutParams.mSpan.mIndex * this.mSizePerSpan;
          k = layoutParams.mSpan.mIndex * n;
          if (this.mOrientation == 1) {
            view.offsetLeftAndRight(j - k);
          } else {
            view.offsetTopAndBottom(j - k);
          } 
        }  
      i++;
    } 
  }
  
  private void resolveShouldLayoutReverse() {
    if (this.mOrientation == 1 || !isLayoutRTL()) {
      this.mShouldReverseLayout = this.mReverseLayout;
      return;
    } 
    this.mShouldReverseLayout = this.mReverseLayout ^ true;
  }
  
  private void setLayoutStateDirection(int paramInt) {
    boolean bool;
    this.mLayoutState.mLayoutDirection = paramInt;
    LayoutState layoutState = this.mLayoutState;
    boolean bool1 = this.mShouldReverseLayout;
    int i = 1;
    if (paramInt == -1) {
      bool = true;
    } else {
      bool = false;
    } 
    if (bool1 == bool) {
      paramInt = i;
    } else {
      paramInt = -1;
    } 
    layoutState.mItemDirection = paramInt;
  }
  
  private void updateAllRemainingSpans(int paramInt1, int paramInt2) {
    for (byte b = 0; b < this.mSpanCount; b++) {
      if (!(this.mSpans[b]).mViews.isEmpty())
        updateRemainingSpans(this.mSpans[b], paramInt1, paramInt2); 
    } 
  }
  
  private boolean updateAnchorFromChildren(RecyclerView.State paramState, AnchorInfo paramAnchorInfo) {
    int i;
    if (this.mLastLayoutFromEnd) {
      i = findLastReferenceChildPosition(paramState.getItemCount());
    } else {
      i = findFirstReferenceChildPosition(paramState.getItemCount());
    } 
    paramAnchorInfo.mPosition = i;
    paramAnchorInfo.mOffset = Integer.MIN_VALUE;
    return true;
  }
  
  private void updateLayoutState(int paramInt, RecyclerView.State paramState) {
    LayoutState layoutState = this.mLayoutState;
    byte b = 0;
    layoutState.mAvailable = 0;
    this.mLayoutState.mCurrentPosition = paramInt;
    if (isSmoothScrolling()) {
      int i = paramState.getTargetScrollPosition();
      if (i != -1) {
        boolean bool1 = this.mShouldReverseLayout;
        if (i < paramInt) {
          b1 = 1;
        } else {
          b1 = 0;
        } 
        if (bool1 == b1) {
          i = this.mPrimaryOrientation.getTotalSpace();
          paramInt = 0;
        } else {
          paramInt = this.mPrimaryOrientation.getTotalSpace();
          i = 0;
        } 
        if (getClipToPadding()) {
          this.mLayoutState.mStartLine = this.mPrimaryOrientation.getStartAfterPadding() - paramInt;
          this.mLayoutState.mEndLine = this.mPrimaryOrientation.getEndAfterPadding() + i;
        } else {
          this.mLayoutState.mEndLine = this.mPrimaryOrientation.getEnd() + i;
          this.mLayoutState.mStartLine = -paramInt;
        } 
        this.mLayoutState.mStopInFocusable = false;
        this.mLayoutState.mRecycle = true;
        LayoutState layoutState1 = this.mLayoutState;
        byte b1 = b;
        if (this.mPrimaryOrientation.getMode() == 0) {
          b1 = b;
          if (this.mPrimaryOrientation.getEnd() == 0)
            b1 = 1; 
        } 
        layoutState1.mInfinite = b1;
        return;
      } 
    } 
    paramInt = 0;
    boolean bool = false;
  }
  
  private void updateRemainingSpans(Span paramSpan, int paramInt1, int paramInt2) {
    int i = paramSpan.getDeletedSize();
    if (paramInt1 == -1) {
      if (paramSpan.getStartLine() + i <= paramInt2) {
        this.mRemainingSpans.set(paramSpan.mIndex, false);
        return;
      } 
    } else if (paramSpan.getEndLine() - i >= paramInt2) {
      this.mRemainingSpans.set(paramSpan.mIndex, false);
    } 
  }
  
  private int updateSpecWithExtra(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 == 0 && paramInt3 == 0)
      return paramInt1; 
    int i = View.MeasureSpec.getMode(paramInt1);
    return (i == Integer.MIN_VALUE || i == 1073741824) ? View.MeasureSpec.makeMeasureSpec(Math.max(0, View.MeasureSpec.getSize(paramInt1) - paramInt2 - paramInt3), i) : paramInt1;
  }
  
  boolean areAllEndsEqual() {
    int i = this.mSpans[0].getEndLine(-2147483648);
    for (byte b = 1; b < this.mSpanCount; b++) {
      if (this.mSpans[b].getEndLine(-2147483648) != i)
        return false; 
    } 
    return true;
  }
  
  boolean areAllStartsEqual() {
    int i = this.mSpans[0].getStartLine(-2147483648);
    for (byte b = 1; b < this.mSpanCount; b++) {
      if (this.mSpans[b].getStartLine(-2147483648) != i)
        return false; 
    } 
    return true;
  }
  
  public void assertNotInLayoutOrScroll(String paramString) {
    if (this.mPendingSavedState == null)
      super.assertNotInLayoutOrScroll(paramString); 
  }
  
  public boolean canScrollHorizontally() { return (this.mOrientation == 0); }
  
  public boolean canScrollVertically() { return (this.mOrientation == 1); }
  
  boolean checkForGaps() {
    if (getChildCount() != 0 && this.mGapStrategy != 0) {
      byte b;
      int j;
      int i;
      if (!isAttachedToWindow())
        return false; 
      if (this.mShouldReverseLayout) {
        i = getLastChildPosition();
        j = getFirstChildPosition();
      } else {
        i = getFirstChildPosition();
        j = getLastChildPosition();
      } 
      if (i == 0 && hasGapsToFix() != null) {
        this.mLazySpanLookup.clear();
        requestSimpleAnimationsInNextLayout();
        requestLayout();
        return true;
      } 
      if (!this.mLaidOutInvalidFullSpan)
        return false; 
      if (this.mShouldReverseLayout) {
        b = -1;
      } else {
        b = 1;
      } 
      LazySpanLookup lazySpanLookup = this.mLazySpanLookup;
      LazySpanLookup.FullSpanItem fullSpanItem1 = lazySpanLookup.getFirstFullSpanItemInRange(i, ++j, b, true);
      if (fullSpanItem1 == null) {
        this.mLaidOutInvalidFullSpan = false;
        this.mLazySpanLookup.forceInvalidateAfter(j);
        return false;
      } 
      LazySpanLookup.FullSpanItem fullSpanItem2 = this.mLazySpanLookup.getFirstFullSpanItemInRange(i, fullSpanItem1.mPosition, b * -1, true);
      if (fullSpanItem2 == null) {
        this.mLazySpanLookup.forceInvalidateAfter(fullSpanItem1.mPosition);
      } else {
        this.mLazySpanLookup.forceInvalidateAfter(fullSpanItem2.mPosition + 1);
      } 
      requestSimpleAnimationsInNextLayout();
      requestLayout();
      return true;
    } 
    return false;
  }
  
  public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams) { return paramLayoutParams instanceof LayoutParams; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY})
  public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, RecyclerView.State paramState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry) {
    if (this.mOrientation != 0)
      paramInt1 = paramInt2; 
    if (getChildCount() != 0) {
      if (paramInt1 == 0)
        return; 
      prepareLayoutStateForDelta(paramInt1, paramState);
      if (this.mPrefetchDistances == null || this.mPrefetchDistances.length < this.mSpanCount)
        this.mPrefetchDistances = new int[this.mSpanCount]; 
      int i = 0;
      paramInt2 = 0;
      for (paramInt1 = 0; paramInt2 < this.mSpanCount; paramInt1 = j) {
        int k;
        if (this.mLayoutState.mItemDirection == -1) {
          k = this.mLayoutState.mStartLine - this.mSpans[paramInt2].getStartLine(this.mLayoutState.mStartLine);
        } else {
          k = this.mSpans[paramInt2].getEndLine(this.mLayoutState.mEndLine) - this.mLayoutState.mEndLine;
        } 
        int j = paramInt1;
        if (k >= 0) {
          this.mPrefetchDistances[paramInt1] = k;
          j = paramInt1 + 1;
        } 
        paramInt2++;
      } 
      Arrays.sort(this.mPrefetchDistances, 0, paramInt1);
      for (paramInt2 = i; paramInt2 < paramInt1 && this.mLayoutState.hasMore(paramState); paramInt2++) {
        paramLayoutPrefetchRegistry.addPosition(this.mLayoutState.mCurrentPosition, this.mPrefetchDistances[paramInt2]);
        LayoutState layoutState = this.mLayoutState;
        layoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
      } 
      return;
    } 
  }
  
  public int computeHorizontalScrollExtent(RecyclerView.State paramState) { return computeScrollExtent(paramState); }
  
  public int computeHorizontalScrollOffset(RecyclerView.State paramState) { return computeScrollOffset(paramState); }
  
  public int computeHorizontalScrollRange(RecyclerView.State paramState) { return computeScrollRange(paramState); }
  
  public PointF computeScrollVectorForPosition(int paramInt) {
    paramInt = calculateScrollDirectionForPosition(paramInt);
    PointF pointF = new PointF();
    if (paramInt == 0)
      return null; 
    if (this.mOrientation == 0) {
      pointF.x = paramInt;
      pointF.y = 0.0F;
      return pointF;
    } 
    pointF.x = 0.0F;
    pointF.y = paramInt;
    return pointF;
  }
  
  public int computeVerticalScrollExtent(RecyclerView.State paramState) { return computeScrollExtent(paramState); }
  
  public int computeVerticalScrollOffset(RecyclerView.State paramState) { return computeScrollOffset(paramState); }
  
  public int computeVerticalScrollRange(RecyclerView.State paramState) { return computeScrollRange(paramState); }
  
  public int[] findFirstCompletelyVisibleItemPositions(int[] paramArrayOfInt) { // Byte code:
    //   0: aload_1
    //   1: ifnonnull -> 14
    //   4: aload_0
    //   5: getfield mSpanCount : I
    //   8: newarray int
    //   10: astore_3
    //   11: goto -> 77
    //   14: aload_1
    //   15: astore_3
    //   16: aload_1
    //   17: arraylength
    //   18: aload_0
    //   19: getfield mSpanCount : I
    //   22: if_icmpge -> 77
    //   25: new java/lang/StringBuilder
    //   28: dup
    //   29: invokespecial <init> : ()V
    //   32: astore_3
    //   33: aload_3
    //   34: ldc_w 'Provided int[]'s size must be more than or equal to span count. Expected:'
    //   37: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: pop
    //   41: aload_3
    //   42: aload_0
    //   43: getfield mSpanCount : I
    //   46: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   49: pop
    //   50: aload_3
    //   51: ldc_w ', array size:'
    //   54: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: pop
    //   58: aload_3
    //   59: aload_1
    //   60: arraylength
    //   61: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   64: pop
    //   65: new java/lang/IllegalArgumentException
    //   68: dup
    //   69: aload_3
    //   70: invokevirtual toString : ()Ljava/lang/String;
    //   73: invokespecial <init> : (Ljava/lang/String;)V
    //   76: athrow
    //   77: iconst_0
    //   78: istore_2
    //   79: iload_2
    //   80: aload_0
    //   81: getfield mSpanCount : I
    //   84: if_icmpge -> 106
    //   87: aload_3
    //   88: iload_2
    //   89: aload_0
    //   90: getfield mSpans : [Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   93: iload_2
    //   94: aaload
    //   95: invokevirtual findFirstCompletelyVisibleItemPosition : ()I
    //   98: iastore
    //   99: iload_2
    //   100: iconst_1
    //   101: iadd
    //   102: istore_2
    //   103: goto -> 79
    //   106: aload_3
    //   107: areturn }
  
  View findFirstVisibleItemClosestToEnd(boolean paramBoolean) {
    int j = this.mPrimaryOrientation.getStartAfterPadding();
    int k = this.mPrimaryOrientation.getEndAfterPadding();
    int i = getChildCount() - 1;
    View view;
    for (view = null; i >= 0; view = view1) {
      View view2 = getChildAt(i);
      int m = this.mPrimaryOrientation.getDecoratedStart(view2);
      int n = this.mPrimaryOrientation.getDecoratedEnd(view2);
      View view1 = view;
      if (n > j)
        if (m >= k) {
          view1 = view;
        } else if (n > k) {
          if (!paramBoolean)
            return view2; 
          view1 = view;
          if (view == null)
            view1 = view2; 
        } else {
          return view2;
        }  
      i--;
    } 
    return view;
  }
  
  View findFirstVisibleItemClosestToStart(boolean paramBoolean) {
    int i = this.mPrimaryOrientation.getStartAfterPadding();
    int j = this.mPrimaryOrientation.getEndAfterPadding();
    int k = getChildCount();
    View view = null;
    byte b = 0;
    while (b < k) {
      View view2 = getChildAt(b);
      int m = this.mPrimaryOrientation.getDecoratedStart(view2);
      View view1 = view;
      if (this.mPrimaryOrientation.getDecoratedEnd(view2) > i)
        if (m >= j) {
          view1 = view;
        } else if (m < i) {
          if (!paramBoolean)
            return view2; 
          view1 = view;
          if (view == null)
            view1 = view2; 
        } else {
          return view2;
        }  
      b++;
      view = view1;
    } 
    return view;
  }
  
  int findFirstVisibleItemPositionInt() {
    View view;
    if (this.mShouldReverseLayout) {
      view = findFirstVisibleItemClosestToEnd(true);
    } else {
      view = findFirstVisibleItemClosestToStart(true);
    } 
    return (view == null) ? -1 : getPosition(view);
  }
  
  public int[] findFirstVisibleItemPositions(int[] paramArrayOfInt) { // Byte code:
    //   0: aload_1
    //   1: ifnonnull -> 14
    //   4: aload_0
    //   5: getfield mSpanCount : I
    //   8: newarray int
    //   10: astore_3
    //   11: goto -> 77
    //   14: aload_1
    //   15: astore_3
    //   16: aload_1
    //   17: arraylength
    //   18: aload_0
    //   19: getfield mSpanCount : I
    //   22: if_icmpge -> 77
    //   25: new java/lang/StringBuilder
    //   28: dup
    //   29: invokespecial <init> : ()V
    //   32: astore_3
    //   33: aload_3
    //   34: ldc_w 'Provided int[]'s size must be more than or equal to span count. Expected:'
    //   37: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: pop
    //   41: aload_3
    //   42: aload_0
    //   43: getfield mSpanCount : I
    //   46: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   49: pop
    //   50: aload_3
    //   51: ldc_w ', array size:'
    //   54: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: pop
    //   58: aload_3
    //   59: aload_1
    //   60: arraylength
    //   61: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   64: pop
    //   65: new java/lang/IllegalArgumentException
    //   68: dup
    //   69: aload_3
    //   70: invokevirtual toString : ()Ljava/lang/String;
    //   73: invokespecial <init> : (Ljava/lang/String;)V
    //   76: athrow
    //   77: iconst_0
    //   78: istore_2
    //   79: iload_2
    //   80: aload_0
    //   81: getfield mSpanCount : I
    //   84: if_icmpge -> 106
    //   87: aload_3
    //   88: iload_2
    //   89: aload_0
    //   90: getfield mSpans : [Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   93: iload_2
    //   94: aaload
    //   95: invokevirtual findFirstVisibleItemPosition : ()I
    //   98: iastore
    //   99: iload_2
    //   100: iconst_1
    //   101: iadd
    //   102: istore_2
    //   103: goto -> 79
    //   106: aload_3
    //   107: areturn }
  
  public int[] findLastCompletelyVisibleItemPositions(int[] paramArrayOfInt) { // Byte code:
    //   0: aload_1
    //   1: ifnonnull -> 14
    //   4: aload_0
    //   5: getfield mSpanCount : I
    //   8: newarray int
    //   10: astore_3
    //   11: goto -> 77
    //   14: aload_1
    //   15: astore_3
    //   16: aload_1
    //   17: arraylength
    //   18: aload_0
    //   19: getfield mSpanCount : I
    //   22: if_icmpge -> 77
    //   25: new java/lang/StringBuilder
    //   28: dup
    //   29: invokespecial <init> : ()V
    //   32: astore_3
    //   33: aload_3
    //   34: ldc_w 'Provided int[]'s size must be more than or equal to span count. Expected:'
    //   37: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: pop
    //   41: aload_3
    //   42: aload_0
    //   43: getfield mSpanCount : I
    //   46: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   49: pop
    //   50: aload_3
    //   51: ldc_w ', array size:'
    //   54: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: pop
    //   58: aload_3
    //   59: aload_1
    //   60: arraylength
    //   61: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   64: pop
    //   65: new java/lang/IllegalArgumentException
    //   68: dup
    //   69: aload_3
    //   70: invokevirtual toString : ()Ljava/lang/String;
    //   73: invokespecial <init> : (Ljava/lang/String;)V
    //   76: athrow
    //   77: iconst_0
    //   78: istore_2
    //   79: iload_2
    //   80: aload_0
    //   81: getfield mSpanCount : I
    //   84: if_icmpge -> 106
    //   87: aload_3
    //   88: iload_2
    //   89: aload_0
    //   90: getfield mSpans : [Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   93: iload_2
    //   94: aaload
    //   95: invokevirtual findLastCompletelyVisibleItemPosition : ()I
    //   98: iastore
    //   99: iload_2
    //   100: iconst_1
    //   101: iadd
    //   102: istore_2
    //   103: goto -> 79
    //   106: aload_3
    //   107: areturn }
  
  public int[] findLastVisibleItemPositions(int[] paramArrayOfInt) { // Byte code:
    //   0: aload_1
    //   1: ifnonnull -> 14
    //   4: aload_0
    //   5: getfield mSpanCount : I
    //   8: newarray int
    //   10: astore_3
    //   11: goto -> 77
    //   14: aload_1
    //   15: astore_3
    //   16: aload_1
    //   17: arraylength
    //   18: aload_0
    //   19: getfield mSpanCount : I
    //   22: if_icmpge -> 77
    //   25: new java/lang/StringBuilder
    //   28: dup
    //   29: invokespecial <init> : ()V
    //   32: astore_3
    //   33: aload_3
    //   34: ldc_w 'Provided int[]'s size must be more than or equal to span count. Expected:'
    //   37: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: pop
    //   41: aload_3
    //   42: aload_0
    //   43: getfield mSpanCount : I
    //   46: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   49: pop
    //   50: aload_3
    //   51: ldc_w ', array size:'
    //   54: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: pop
    //   58: aload_3
    //   59: aload_1
    //   60: arraylength
    //   61: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   64: pop
    //   65: new java/lang/IllegalArgumentException
    //   68: dup
    //   69: aload_3
    //   70: invokevirtual toString : ()Ljava/lang/String;
    //   73: invokespecial <init> : (Ljava/lang/String;)V
    //   76: athrow
    //   77: iconst_0
    //   78: istore_2
    //   79: iload_2
    //   80: aload_0
    //   81: getfield mSpanCount : I
    //   84: if_icmpge -> 106
    //   87: aload_3
    //   88: iload_2
    //   89: aload_0
    //   90: getfield mSpans : [Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   93: iload_2
    //   94: aaload
    //   95: invokevirtual findLastVisibleItemPosition : ()I
    //   98: iastore
    //   99: iload_2
    //   100: iconst_1
    //   101: iadd
    //   102: istore_2
    //   103: goto -> 79
    //   106: aload_3
    //   107: areturn }
  
  public RecyclerView.LayoutParams generateDefaultLayoutParams() { return (this.mOrientation == 0) ? new LayoutParams(-2, -1) : new LayoutParams(-1, -2); }
  
  public RecyclerView.LayoutParams generateLayoutParams(Context paramContext, AttributeSet paramAttributeSet) { return new LayoutParams(paramContext, paramAttributeSet); }
  
  public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams instanceof ViewGroup.MarginLayoutParams) ? new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams) : new LayoutParams(paramLayoutParams); }
  
  public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return (this.mOrientation == 1) ? this.mSpanCount : super.getColumnCountForAccessibility(paramRecycler, paramState); }
  
  int getFirstChildPosition() { return (getChildCount() == 0) ? 0 : getPosition(getChildAt(0)); }
  
  public int getGapStrategy() { return this.mGapStrategy; }
  
  int getLastChildPosition() {
    int i = getChildCount();
    return (i == 0) ? 0 : getPosition(getChildAt(i - 1));
  }
  
  public int getOrientation() { return this.mOrientation; }
  
  public boolean getReverseLayout() { return this.mReverseLayout; }
  
  public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return (this.mOrientation == 0) ? this.mSpanCount : super.getRowCountForAccessibility(paramRecycler, paramState); }
  
  public int getSpanCount() { return this.mSpanCount; }
  
  View hasGapsToFix() { // Byte code:
    //   0: aload_0
    //   1: invokevirtual getChildCount : ()I
    //   4: iconst_1
    //   5: isub
    //   6: istore_1
    //   7: new java/util/BitSet
    //   10: dup
    //   11: aload_0
    //   12: getfield mSpanCount : I
    //   15: invokespecial <init> : (I)V
    //   18: astore #7
    //   20: aload #7
    //   22: iconst_0
    //   23: aload_0
    //   24: getfield mSpanCount : I
    //   27: iconst_1
    //   28: invokevirtual set : (IIZ)V
    //   31: aload_0
    //   32: getfield mOrientation : I
    //   35: istore_2
    //   36: iconst_m1
    //   37: istore #5
    //   39: iload_2
    //   40: iconst_1
    //   41: if_icmpne -> 56
    //   44: aload_0
    //   45: invokevirtual isLayoutRTL : ()Z
    //   48: ifeq -> 56
    //   51: iconst_1
    //   52: istore_2
    //   53: goto -> 58
    //   56: iconst_m1
    //   57: istore_2
    //   58: aload_0
    //   59: getfield mShouldReverseLayout : Z
    //   62: ifeq -> 70
    //   65: iconst_m1
    //   66: istore_3
    //   67: goto -> 76
    //   70: iload_1
    //   71: iconst_1
    //   72: iadd
    //   73: istore_3
    //   74: iconst_0
    //   75: istore_1
    //   76: iload_1
    //   77: istore #4
    //   79: iload_1
    //   80: iload_3
    //   81: if_icmpge -> 90
    //   84: iconst_1
    //   85: istore #5
    //   87: iload_1
    //   88: istore #4
    //   90: iload #4
    //   92: iload_3
    //   93: if_icmpeq -> 349
    //   96: aload_0
    //   97: iload #4
    //   99: invokevirtual getChildAt : (I)Landroid/view/View;
    //   102: astore #8
    //   104: aload #8
    //   106: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   109: checkcast android/support/v7/widget/StaggeredGridLayoutManager$LayoutParams
    //   112: astore #9
    //   114: aload #7
    //   116: aload #9
    //   118: getfield mSpan : Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   121: getfield mIndex : I
    //   124: invokevirtual get : (I)Z
    //   127: ifeq -> 158
    //   130: aload_0
    //   131: aload #9
    //   133: getfield mSpan : Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   136: invokespecial checkSpanForGap : (Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;)Z
    //   139: ifeq -> 145
    //   142: aload #8
    //   144: areturn
    //   145: aload #7
    //   147: aload #9
    //   149: getfield mSpan : Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   152: getfield mIndex : I
    //   155: invokevirtual clear : (I)V
    //   158: aload #9
    //   160: getfield mFullSpan : Z
    //   163: ifeq -> 169
    //   166: goto -> 339
    //   169: iload #4
    //   171: iload #5
    //   173: iadd
    //   174: istore_1
    //   175: iload_1
    //   176: iload_3
    //   177: if_icmpeq -> 339
    //   180: aload_0
    //   181: iload_1
    //   182: invokevirtual getChildAt : (I)Landroid/view/View;
    //   185: astore #10
    //   187: aload_0
    //   188: getfield mShouldReverseLayout : Z
    //   191: ifeq -> 233
    //   194: aload_0
    //   195: getfield mPrimaryOrientation : Landroid/support/v7/widget/OrientationHelper;
    //   198: aload #8
    //   200: invokevirtual getDecoratedEnd : (Landroid/view/View;)I
    //   203: istore_1
    //   204: aload_0
    //   205: getfield mPrimaryOrientation : Landroid/support/v7/widget/OrientationHelper;
    //   208: aload #10
    //   210: invokevirtual getDecoratedEnd : (Landroid/view/View;)I
    //   213: istore #6
    //   215: iload_1
    //   216: iload #6
    //   218: if_icmpge -> 224
    //   221: aload #8
    //   223: areturn
    //   224: iload_1
    //   225: iload #6
    //   227: if_icmpne -> 274
    //   230: goto -> 269
    //   233: aload_0
    //   234: getfield mPrimaryOrientation : Landroid/support/v7/widget/OrientationHelper;
    //   237: aload #8
    //   239: invokevirtual getDecoratedStart : (Landroid/view/View;)I
    //   242: istore_1
    //   243: aload_0
    //   244: getfield mPrimaryOrientation : Landroid/support/v7/widget/OrientationHelper;
    //   247: aload #10
    //   249: invokevirtual getDecoratedStart : (Landroid/view/View;)I
    //   252: istore #6
    //   254: iload_1
    //   255: iload #6
    //   257: if_icmple -> 263
    //   260: aload #8
    //   262: areturn
    //   263: iload_1
    //   264: iload #6
    //   266: if_icmpne -> 274
    //   269: iconst_1
    //   270: istore_1
    //   271: goto -> 276
    //   274: iconst_0
    //   275: istore_1
    //   276: iload_1
    //   277: ifeq -> 339
    //   280: aload #10
    //   282: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   285: checkcast android/support/v7/widget/StaggeredGridLayoutManager$LayoutParams
    //   288: astore #10
    //   290: aload #9
    //   292: getfield mSpan : Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   295: getfield mIndex : I
    //   298: aload #10
    //   300: getfield mSpan : Landroid/support/v7/widget/StaggeredGridLayoutManager$Span;
    //   303: getfield mIndex : I
    //   306: isub
    //   307: ifge -> 315
    //   310: iconst_1
    //   311: istore_1
    //   312: goto -> 317
    //   315: iconst_0
    //   316: istore_1
    //   317: iload_2
    //   318: ifge -> 327
    //   321: iconst_1
    //   322: istore #6
    //   324: goto -> 330
    //   327: iconst_0
    //   328: istore #6
    //   330: iload_1
    //   331: iload #6
    //   333: if_icmpeq -> 339
    //   336: aload #8
    //   338: areturn
    //   339: iload #4
    //   341: iload #5
    //   343: iadd
    //   344: istore #4
    //   346: goto -> 90
    //   349: aconst_null
    //   350: areturn }
  
  public void invalidateSpanAssignments() {
    this.mLazySpanLookup.clear();
    requestLayout();
  }
  
  public boolean isAutoMeasureEnabled() { return (this.mGapStrategy != 0); }
  
  boolean isLayoutRTL() { return (getLayoutDirection() == 1); }
  
  public void offsetChildrenHorizontal(int paramInt) {
    super.offsetChildrenHorizontal(paramInt);
    for (byte b = 0; b < this.mSpanCount; b++)
      this.mSpans[b].onOffset(paramInt); 
  }
  
  public void offsetChildrenVertical(int paramInt) {
    super.offsetChildrenVertical(paramInt);
    for (byte b = 0; b < this.mSpanCount; b++)
      this.mSpans[b].onOffset(paramInt); 
  }
  
  public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler) {
    super.onDetachedFromWindow(paramRecyclerView, paramRecycler);
    removeCallbacks(this.mCheckForGapsRunnable);
    for (byte b = 0; b < this.mSpanCount; b++)
      this.mSpans[b].clear(); 
    paramRecyclerView.requestLayout();
  }
  
  @Nullable
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) {
    if (getChildCount() == 0)
      return null; 
    paramView = findContainingItemView(paramView);
    if (paramView == null)
      return null; 
    resolveShouldLayoutReverse();
    int k = convertFocusDirectionToLayoutDirection(paramInt);
    if (k == Integer.MIN_VALUE)
      return null; 
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    boolean bool1 = layoutParams.mFullSpan;
    Span span = layoutParams.mSpan;
    if (k == 1) {
      paramInt = getLastChildPosition();
    } else {
      paramInt = getFirstChildPosition();
    } 
    updateLayoutState(paramInt, paramState);
    setLayoutStateDirection(k);
    this.mLayoutState.mCurrentPosition = this.mLayoutState.mItemDirection + paramInt;
    this.mLayoutState.mAvailable = (int)(this.mPrimaryOrientation.getTotalSpace() * 0.33333334F);
    this.mLayoutState.mStopInFocusable = true;
    LayoutState layoutState = this.mLayoutState;
    int j = 0;
    layoutState.mRecycle = false;
    fill(paramRecycler, this.mLayoutState, paramState);
    this.mLastLayoutFromEnd = this.mShouldReverseLayout;
    if (!bool1) {
      View view = span.getFocusableViewAfter(paramInt, k);
      if (view != null && view != paramView)
        return view; 
    } 
    if (preferLastSpan(k)) {
      int m;
      for (m = this.mSpanCount - 1; m >= 0; m--) {
        View view = this.mSpans[m].getFocusableViewAfter(paramInt, k);
        if (view != null && view != paramView)
          return view; 
      } 
    } else {
      byte b;
      for (b = 0; b < this.mSpanCount; b++) {
        View view = this.mSpans[b].getFocusableViewAfter(paramInt, k);
        if (view != null && view != paramView)
          return view; 
      } 
    } 
    boolean bool2 = this.mReverseLayout;
    if (k == -1) {
      paramInt = 1;
    } else {
      paramInt = 0;
    } 
    if ((bool2 ^ true) == paramInt) {
      paramInt = 1;
    } else {
      paramInt = 0;
    } 
    if (!bool1) {
      int m;
      if (paramInt != 0) {
        m = span.findFirstPartiallyVisibleItemPosition();
      } else {
        m = span.findLastPartiallyVisibleItemPosition();
      } 
      View view = findViewByPosition(m);
      if (view != null && view != paramView)
        return view; 
    } 
    int i = j;
    if (preferLastSpan(k)) {
      for (i = this.mSpanCount - 1; i >= 0; i--) {
        if (i != span.mIndex) {
          if (paramInt != 0) {
            j = this.mSpans[i].findFirstPartiallyVisibleItemPosition();
          } else {
            j = this.mSpans[i].findLastPartiallyVisibleItemPosition();
          } 
          View view = findViewByPosition(j);
          if (view != null && view != paramView)
            return view; 
        } 
      } 
    } else {
      while (i < this.mSpanCount) {
        if (paramInt != 0) {
          j = this.mSpans[i].findFirstPartiallyVisibleItemPosition();
        } else {
          j = this.mSpans[i].findLastPartiallyVisibleItemPosition();
        } 
        View view = findViewByPosition(j);
        if (view != null && view != paramView)
          return view; 
        i++;
      } 
    } 
    return null;
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent) {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    if (getChildCount() > 0) {
      View view1 = findFirstVisibleItemClosestToStart(false);
      View view2 = findFirstVisibleItemClosestToEnd(false);
      if (view1 != null) {
        if (view2 == null)
          return; 
        int i = getPosition(view1);
        int j = getPosition(view2);
        if (i < j) {
          paramAccessibilityEvent.setFromIndex(i);
          paramAccessibilityEvent.setToIndex(j);
          return;
        } 
        paramAccessibilityEvent.setFromIndex(j);
        paramAccessibilityEvent.setToIndex(i);
        return;
      } 
      return;
    } 
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat) {
    byte b;
    ViewGroup.LayoutParams layoutParams1 = paramView.getLayoutParams();
    if (!(layoutParams1 instanceof LayoutParams)) {
      onInitializeAccessibilityNodeInfoForItem(paramView, paramAccessibilityNodeInfoCompat);
      return;
    } 
    LayoutParams layoutParams = (LayoutParams)layoutParams1;
    if (this.mOrientation == 0) {
      int j = layoutParams.getSpanIndex();
      if (layoutParams.mFullSpan) {
        b = this.mSpanCount;
      } else {
        b = 1;
      } 
      paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(j, b, -1, -1, layoutParams.mFullSpan, false));
      return;
    } 
    int i = layoutParams.getSpanIndex();
    if (layoutParams.mFullSpan) {
      b = this.mSpanCount;
    } else {
      b = 1;
    } 
    paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(-1, -1, i, b, layoutParams.mFullSpan, false));
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) { handleUpdate(paramInt1, paramInt2, 1); }
  
  public void onItemsChanged(RecyclerView paramRecyclerView) {
    this.mLazySpanLookup.clear();
    requestLayout();
  }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3) { handleUpdate(paramInt1, paramInt2, 8); }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) { handleUpdate(paramInt1, paramInt2, 2); }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject) { handleUpdate(paramInt1, paramInt2, 4); }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { onLayoutChildren(paramRecycler, paramState, true); }
  
  public void onLayoutCompleted(RecyclerView.State paramState) {
    super.onLayoutCompleted(paramState);
    this.mPendingScrollPosition = -1;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    this.mPendingSavedState = null;
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
    savedState.mReverseLayout = this.mReverseLayout;
    savedState.mAnchorLayoutFromEnd = this.mLastLayoutFromEnd;
    savedState.mLastLayoutRTL = this.mLastLayoutRTL;
    LazySpanLookup lazySpanLookup = this.mLazySpanLookup;
    byte b = 0;
    if (lazySpanLookup != null && this.mLazySpanLookup.mData != null) {
      savedState.mSpanLookup = this.mLazySpanLookup.mData;
      savedState.mSpanLookupSize = savedState.mSpanLookup.length;
      savedState.mFullSpanItems = this.mLazySpanLookup.mFullSpanItems;
    } else {
      savedState.mSpanLookupSize = 0;
    } 
    if (getChildCount() > 0) {
      int i;
      if (this.mLastLayoutFromEnd) {
        i = getLastChildPosition();
      } else {
        i = getFirstChildPosition();
      } 
      savedState.mAnchorPosition = i;
      savedState.mVisibleAnchorPosition = findFirstVisibleItemPositionInt();
      savedState.mSpanOffsetsSize = this.mSpanCount;
      savedState.mSpanOffsets = new int[this.mSpanCount];
      while (b < this.mSpanCount) {
        if (this.mLastLayoutFromEnd) {
          int j = this.mSpans[b].getEndLine(-2147483648);
          i = j;
          if (j != Integer.MIN_VALUE)
            i = j - this.mPrimaryOrientation.getEndAfterPadding(); 
        } else {
          int j = this.mSpans[b].getStartLine(-2147483648);
          i = j;
          if (j != Integer.MIN_VALUE)
            i = j - this.mPrimaryOrientation.getStartAfterPadding(); 
        } 
        savedState.mSpanOffsets[b] = i;
        b++;
      } 
    } else {
      savedState.mAnchorPosition = -1;
      savedState.mVisibleAnchorPosition = -1;
      savedState.mSpanOffsetsSize = 0;
    } 
    return savedState;
  }
  
  public void onScrollStateChanged(int paramInt) {
    if (paramInt == 0)
      checkForGaps(); 
  }
  
  void prepareLayoutStateForDelta(int paramInt, RecyclerView.State paramState) {
    byte b;
    int i;
    if (paramInt > 0) {
      i = getLastChildPosition();
      b = 1;
    } else {
      i = getFirstChildPosition();
      b = -1;
    } 
    this.mLayoutState.mRecycle = true;
    updateLayoutState(i, paramState);
    setLayoutStateDirection(b);
    this.mLayoutState.mCurrentPosition = i + this.mLayoutState.mItemDirection;
    this.mLayoutState.mAvailable = Math.abs(paramInt);
  }
  
  int scrollBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) {
    if (getChildCount() != 0) {
      if (paramInt == 0)
        return 0; 
      prepareLayoutStateForDelta(paramInt, paramState);
      int i = fill(paramRecycler, this.mLayoutState, paramState);
      if (this.mLayoutState.mAvailable >= i)
        if (paramInt < 0) {
          paramInt = -i;
        } else {
          paramInt = i;
        }  
      this.mPrimaryOrientation.offsetChildren(-paramInt);
      this.mLastLayoutFromEnd = this.mShouldReverseLayout;
      this.mLayoutState.mAvailable = 0;
      recycle(paramRecycler, this.mLayoutState);
      return paramInt;
    } 
    return 0;
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return scrollBy(paramInt, paramRecycler, paramState); }
  
  public void scrollToPosition(int paramInt) {
    if (this.mPendingSavedState != null && this.mPendingSavedState.mAnchorPosition != paramInt)
      this.mPendingSavedState.invalidateAnchorPositionInfo(); 
    this.mPendingScrollPosition = paramInt;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    requestLayout();
  }
  
  public void scrollToPositionWithOffset(int paramInt1, int paramInt2) {
    if (this.mPendingSavedState != null)
      this.mPendingSavedState.invalidateAnchorPositionInfo(); 
    this.mPendingScrollPosition = paramInt1;
    this.mPendingScrollPositionOffset = paramInt2;
    requestLayout();
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState) { return scrollBy(paramInt, paramRecycler, paramState); }
  
  public void setGapStrategy(int paramInt) {
    assertNotInLayoutOrScroll(null);
    if (paramInt == this.mGapStrategy)
      return; 
    if (paramInt != 0 && paramInt != 2)
      throw new IllegalArgumentException("invalid gap strategy. Must be GAP_HANDLING_NONE or GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS"); 
    this.mGapStrategy = paramInt;
    requestLayout();
  }
  
  public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2) {
    int i = getPaddingLeft() + getPaddingRight();
    int j = getPaddingTop() + getPaddingBottom();
    if (this.mOrientation == 1) {
      paramInt2 = chooseSize(paramInt2, paramRect.height() + j, getMinimumHeight());
      i = chooseSize(paramInt1, this.mSizePerSpan * this.mSpanCount + i, getMinimumWidth());
      paramInt1 = paramInt2;
      paramInt2 = i;
    } else {
      paramInt1 = chooseSize(paramInt1, paramRect.width() + i, getMinimumWidth());
      i = chooseSize(paramInt2, this.mSizePerSpan * this.mSpanCount + j, getMinimumHeight());
      paramInt2 = paramInt1;
      paramInt1 = i;
    } 
    setMeasuredDimension(paramInt2, paramInt1);
  }
  
  public void setOrientation(int paramInt) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("invalid orientation."); 
    assertNotInLayoutOrScroll(null);
    if (paramInt == this.mOrientation)
      return; 
    this.mOrientation = paramInt;
    OrientationHelper orientationHelper = this.mPrimaryOrientation;
    this.mPrimaryOrientation = this.mSecondaryOrientation;
    this.mSecondaryOrientation = orientationHelper;
    requestLayout();
  }
  
  public void setReverseLayout(boolean paramBoolean) {
    assertNotInLayoutOrScroll(null);
    if (this.mPendingSavedState != null && this.mPendingSavedState.mReverseLayout != paramBoolean)
      this.mPendingSavedState.mReverseLayout = paramBoolean; 
    this.mReverseLayout = paramBoolean;
    requestLayout();
  }
  
  public void setSpanCount(int paramInt) {
    assertNotInLayoutOrScroll(null);
    if (paramInt != this.mSpanCount) {
      invalidateSpanAssignments();
      this.mSpanCount = paramInt;
      this.mRemainingSpans = new BitSet(this.mSpanCount);
      this.mSpans = new Span[this.mSpanCount];
      for (paramInt = 0; paramInt < this.mSpanCount; paramInt++)
        this.mSpans[paramInt] = new Span(paramInt); 
      requestLayout();
    } 
  }
  
  public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt) {
    LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(paramRecyclerView.getContext());
    linearSmoothScroller.setTargetPosition(paramInt);
    startSmoothScroll(linearSmoothScroller);
  }
  
  public boolean supportsPredictiveItemAnimations() { return (this.mPendingSavedState == null); }
  
  boolean updateAnchorFromPendingData(RecyclerView.State paramState, AnchorInfo paramAnchorInfo) {
    boolean bool1 = paramState.isPreLayout();
    boolean bool = false;
    if (!bool1) {
      if (this.mPendingScrollPosition == -1)
        return false; 
      if (this.mPendingScrollPosition < 0 || this.mPendingScrollPosition >= paramState.getItemCount()) {
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        return false;
      } 
      if (this.mPendingSavedState == null || this.mPendingSavedState.mAnchorPosition == -1 || this.mPendingSavedState.mSpanOffsetsSize < 1) {
        View view = findViewByPosition(this.mPendingScrollPosition);
        if (view != null) {
          if (this.mShouldReverseLayout) {
            i = getLastChildPosition();
          } else {
            i = getFirstChildPosition();
          } 
          paramAnchorInfo.mPosition = i;
          if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
            if (paramAnchorInfo.mLayoutFromEnd) {
              paramAnchorInfo.mOffset = this.mPrimaryOrientation.getEndAfterPadding() - this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedEnd(view);
              return true;
            } 
            paramAnchorInfo.mOffset = this.mPrimaryOrientation.getStartAfterPadding() + this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedStart(view);
            return true;
          } 
          if (this.mPrimaryOrientation.getDecoratedMeasurement(view) > this.mPrimaryOrientation.getTotalSpace()) {
            if (paramAnchorInfo.mLayoutFromEnd) {
              i = this.mPrimaryOrientation.getEndAfterPadding();
            } else {
              i = this.mPrimaryOrientation.getStartAfterPadding();
            } 
            paramAnchorInfo.mOffset = i;
            return true;
          } 
          int i = this.mPrimaryOrientation.getDecoratedStart(view) - this.mPrimaryOrientation.getStartAfterPadding();
          if (i < 0) {
            paramAnchorInfo.mOffset = -i;
            return true;
          } 
          i = this.mPrimaryOrientation.getEndAfterPadding() - this.mPrimaryOrientation.getDecoratedEnd(view);
          if (i < 0) {
            paramAnchorInfo.mOffset = i;
            return true;
          } 
          paramAnchorInfo.mOffset = Integer.MIN_VALUE;
          return true;
        } 
        paramAnchorInfo.mPosition = this.mPendingScrollPosition;
        if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE) {
          if (calculateScrollDirectionForPosition(paramAnchorInfo.mPosition) == 1)
            bool = true; 
          paramAnchorInfo.mLayoutFromEnd = bool;
          paramAnchorInfo.assignCoordinateFromPadding();
        } else {
          paramAnchorInfo.assignCoordinateFromPadding(this.mPendingScrollPositionOffset);
        } 
        paramAnchorInfo.mInvalidateOffsets = true;
        return true;
      } 
      paramAnchorInfo.mOffset = Integer.MIN_VALUE;
      paramAnchorInfo.mPosition = this.mPendingScrollPosition;
      return true;
    } 
    return false;
  }
  
  void updateAnchorInfoForLayout(RecyclerView.State paramState, AnchorInfo paramAnchorInfo) {
    if (updateAnchorFromPendingData(paramState, paramAnchorInfo))
      return; 
    if (updateAnchorFromChildren(paramState, paramAnchorInfo))
      return; 
    paramAnchorInfo.assignCoordinateFromPadding();
    paramAnchorInfo.mPosition = 0;
  }
  
  void updateMeasureSpecs(int paramInt) {
    this.mSizePerSpan = paramInt / this.mSpanCount;
    this.mFullSizeSpec = View.MeasureSpec.makeMeasureSpec(paramInt, this.mSecondaryOrientation.getMode());
  }
  
  class AnchorInfo {
    boolean mInvalidateOffsets;
    
    boolean mLayoutFromEnd;
    
    int mOffset;
    
    int mPosition;
    
    int[] mSpanReferenceLines;
    
    boolean mValid;
    
    AnchorInfo() { reset(); }
    
    void assignCoordinateFromPadding() {
      int i;
      if (this.mLayoutFromEnd) {
        i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
      } else {
        i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
      } 
      this.mOffset = i;
    }
    
    void assignCoordinateFromPadding(int param1Int) {
      if (this.mLayoutFromEnd) {
        this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() - param1Int;
        return;
      } 
      this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding() + param1Int;
    }
    
    void reset() {
      this.mPosition = -1;
      this.mOffset = Integer.MIN_VALUE;
      this.mLayoutFromEnd = false;
      this.mInvalidateOffsets = false;
      this.mValid = false;
      if (this.mSpanReferenceLines != null)
        Arrays.fill(this.mSpanReferenceLines, -1); 
    }
    
    void saveSpanReferenceLines(StaggeredGridLayoutManager.Span[] param1ArrayOfSpan) {
      int i = param1ArrayOfSpan.length;
      if (this.mSpanReferenceLines == null || this.mSpanReferenceLines.length < i)
        this.mSpanReferenceLines = new int[StaggeredGridLayoutManager.this.mSpans.length]; 
      for (byte b = 0; b < i; b++)
        this.mSpanReferenceLines[b] = param1ArrayOfSpan[b].getStartLine(-2147483648); 
    }
  }
  
  public static class LayoutParams extends RecyclerView.LayoutParams {
    public static final int INVALID_SPAN_ID = -1;
    
    boolean mFullSpan;
    
    StaggeredGridLayoutManager.Span mSpan;
    
    public LayoutParams(int param1Int1, int param1Int2) { super(param1Int1, param1Int2); }
    
    public LayoutParams(Context param1Context, AttributeSet param1AttributeSet) { super(param1Context, param1AttributeSet); }
    
    public LayoutParams(RecyclerView.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.MarginLayoutParams param1MarginLayoutParams) { super(param1MarginLayoutParams); }
    
    public final int getSpanIndex() { return (this.mSpan == null) ? -1 : this.mSpan.mIndex; }
    
    public boolean isFullSpan() { return this.mFullSpan; }
    
    public void setFullSpan(boolean param1Boolean) { this.mFullSpan = param1Boolean; }
  }
  
  static class LazySpanLookup {
    private static final int MIN_SIZE = 10;
    
    int[] mData;
    
    List<FullSpanItem> mFullSpanItems;
    
    private int invalidateFullSpansAfter(int param1Int) { // Byte code:
      //   0: aload_0
      //   1: getfield mFullSpanItems : Ljava/util/List;
      //   4: ifnonnull -> 9
      //   7: iconst_m1
      //   8: ireturn
      //   9: aload_0
      //   10: iload_1
      //   11: invokevirtual getFullSpanItem : (I)Landroid/support/v7/widget/StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem;
      //   14: astore #4
      //   16: aload #4
      //   18: ifnull -> 33
      //   21: aload_0
      //   22: getfield mFullSpanItems : Ljava/util/List;
      //   25: aload #4
      //   27: invokeinterface remove : (Ljava/lang/Object;)Z
      //   32: pop
      //   33: aload_0
      //   34: getfield mFullSpanItems : Ljava/util/List;
      //   37: invokeinterface size : ()I
      //   42: istore_3
      //   43: iconst_0
      //   44: istore_2
      //   45: iload_2
      //   46: iload_3
      //   47: if_icmpge -> 80
      //   50: aload_0
      //   51: getfield mFullSpanItems : Ljava/util/List;
      //   54: iload_2
      //   55: invokeinterface get : (I)Ljava/lang/Object;
      //   60: checkcast android/support/v7/widget/StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem
      //   63: getfield mPosition : I
      //   66: iload_1
      //   67: if_icmplt -> 73
      //   70: goto -> 82
      //   73: iload_2
      //   74: iconst_1
      //   75: iadd
      //   76: istore_2
      //   77: goto -> 45
      //   80: iconst_m1
      //   81: istore_2
      //   82: iload_2
      //   83: iconst_m1
      //   84: if_icmpeq -> 119
      //   87: aload_0
      //   88: getfield mFullSpanItems : Ljava/util/List;
      //   91: iload_2
      //   92: invokeinterface get : (I)Ljava/lang/Object;
      //   97: checkcast android/support/v7/widget/StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem
      //   100: astore #4
      //   102: aload_0
      //   103: getfield mFullSpanItems : Ljava/util/List;
      //   106: iload_2
      //   107: invokeinterface remove : (I)Ljava/lang/Object;
      //   112: pop
      //   113: aload #4
      //   115: getfield mPosition : I
      //   118: ireturn
      //   119: iconst_m1
      //   120: ireturn }
    
    private void offsetFullSpansForAddition(int param1Int1, int param1Int2) {
      if (this.mFullSpanItems == null)
        return; 
      for (int i = this.mFullSpanItems.size() - 1; i >= 0; i--) {
        FullSpanItem fullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (fullSpanItem.mPosition >= param1Int1)
          fullSpanItem.mPosition += param1Int2; 
      } 
    }
    
    private void offsetFullSpansForRemoval(int param1Int1, int param1Int2) {
      if (this.mFullSpanItems == null)
        return; 
      for (int i = this.mFullSpanItems.size() - 1; i >= 0; i--) {
        FullSpanItem fullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (fullSpanItem.mPosition >= param1Int1)
          if (fullSpanItem.mPosition < param1Int1 + param1Int2) {
            this.mFullSpanItems.remove(i);
          } else {
            fullSpanItem.mPosition -= param1Int2;
          }  
      } 
    }
    
    public void addFullSpanItem(FullSpanItem param1FullSpanItem) {
      if (this.mFullSpanItems == null)
        this.mFullSpanItems = new ArrayList(); 
      int i = this.mFullSpanItems.size();
      for (byte b = 0; b < i; b++) {
        FullSpanItem fullSpanItem = (FullSpanItem)this.mFullSpanItems.get(b);
        if (fullSpanItem.mPosition == param1FullSpanItem.mPosition)
          this.mFullSpanItems.remove(b); 
        if (fullSpanItem.mPosition >= param1FullSpanItem.mPosition) {
          this.mFullSpanItems.add(b, param1FullSpanItem);
          return;
        } 
      } 
      this.mFullSpanItems.add(param1FullSpanItem);
    }
    
    void clear() {
      if (this.mData != null)
        Arrays.fill(this.mData, -1); 
      this.mFullSpanItems = null;
    }
    
    void ensureSize(int param1Int) {
      if (this.mData == null) {
        this.mData = new int[Math.max(param1Int, 10) + 1];
        Arrays.fill(this.mData, -1);
        return;
      } 
      if (param1Int >= this.mData.length) {
        int[] arrayOfInt = this.mData;
        this.mData = new int[sizeForPosition(param1Int)];
        System.arraycopy(arrayOfInt, 0, this.mData, 0, arrayOfInt.length);
        Arrays.fill(this.mData, arrayOfInt.length, this.mData.length, -1);
      } 
    }
    
    int forceInvalidateAfter(int param1Int) {
      if (this.mFullSpanItems != null)
        for (int i = this.mFullSpanItems.size() - 1; i >= 0; i--) {
          if (((FullSpanItem)this.mFullSpanItems.get(i)).mPosition >= param1Int)
            this.mFullSpanItems.remove(i); 
        }  
      return invalidateAfter(param1Int);
    }
    
    public FullSpanItem getFirstFullSpanItemInRange(int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean) {
      if (this.mFullSpanItems == null)
        return null; 
      int i = this.mFullSpanItems.size();
      byte b;
      for (b = 0; b < i; b++) {
        FullSpanItem fullSpanItem = (FullSpanItem)this.mFullSpanItems.get(b);
        if (fullSpanItem.mPosition >= param1Int2)
          return null; 
        if (fullSpanItem.mPosition >= param1Int1 && (param1Int3 == 0 || fullSpanItem.mGapDir == param1Int3 || (param1Boolean && fullSpanItem.mHasUnwantedGapAfter)))
          return fullSpanItem; 
      } 
      return null;
    }
    
    public FullSpanItem getFullSpanItem(int param1Int) {
      if (this.mFullSpanItems == null)
        return null; 
      for (int i = this.mFullSpanItems.size() - 1; i >= 0; i--) {
        FullSpanItem fullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (fullSpanItem.mPosition == param1Int)
          return fullSpanItem; 
      } 
      return null;
    }
    
    int getSpan(int param1Int) { return (this.mData == null || param1Int >= this.mData.length) ? -1 : this.mData[param1Int]; }
    
    int invalidateAfter(int param1Int) {
      if (this.mData == null)
        return -1; 
      if (param1Int >= this.mData.length)
        return -1; 
      int i = invalidateFullSpansAfter(param1Int);
      if (i == -1) {
        Arrays.fill(this.mData, param1Int, this.mData.length, -1);
        return this.mData.length;
      } 
      int[] arrayOfInt = this.mData;
      Arrays.fill(arrayOfInt, param1Int, ++i, -1);
      return i;
    }
    
    void offsetForAddition(int param1Int1, int param1Int2) {
      if (this.mData != null) {
        if (param1Int1 >= this.mData.length)
          return; 
        int i = param1Int1 + param1Int2;
        ensureSize(i);
        System.arraycopy(this.mData, param1Int1, this.mData, i, this.mData.length - param1Int1 - param1Int2);
        Arrays.fill(this.mData, param1Int1, i, -1);
        offsetFullSpansForAddition(param1Int1, param1Int2);
        return;
      } 
    }
    
    void offsetForRemoval(int param1Int1, int param1Int2) {
      if (this.mData != null) {
        if (param1Int1 >= this.mData.length)
          return; 
        int i = param1Int1 + param1Int2;
        ensureSize(i);
        System.arraycopy(this.mData, i, this.mData, param1Int1, this.mData.length - param1Int1 - param1Int2);
        Arrays.fill(this.mData, this.mData.length - param1Int2, this.mData.length, -1);
        offsetFullSpansForRemoval(param1Int1, param1Int2);
        return;
      } 
    }
    
    void setSpan(int param1Int, StaggeredGridLayoutManager.Span param1Span) {
      ensureSize(param1Int);
      this.mData[param1Int] = param1Span.mIndex;
    }
    
    int sizeForPosition(int param1Int) {
      int i;
      for (i = this.mData.length; i <= param1Int; i *= 2);
      return i;
    }
    
    static class FullSpanItem implements Parcelable {
      public static final Parcelable.Creator<FullSpanItem> CREATOR = new Parcelable.Creator<FullSpanItem>() {
          public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFromParcel(Parcel param3Parcel) { return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem(param3Parcel); }
          
          public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[] newArray(int param3Int) { return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[param3Int]; }
        };
      
      int mGapDir;
      
      int[] mGapPerSpan;
      
      boolean mHasUnwantedGapAfter;
      
      int mPosition;
      
      FullSpanItem() {}
      
      FullSpanItem(Parcel param2Parcel) {
        this.mPosition = param2Parcel.readInt();
        this.mGapDir = param2Parcel.readInt();
        int i = param2Parcel.readInt();
        boolean bool = true;
        if (i != 1)
          bool = false; 
        this.mHasUnwantedGapAfter = bool;
        i = param2Parcel.readInt();
        if (i > 0) {
          this.mGapPerSpan = new int[i];
          param2Parcel.readIntArray(this.mGapPerSpan);
        } 
      }
      
      public int describeContents() { return 0; }
      
      int getGapForSpan(int param2Int) { return (this.mGapPerSpan == null) ? 0 : this.mGapPerSpan[param2Int]; }
      
      public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FullSpanItem{mPosition=");
        stringBuilder.append(this.mPosition);
        stringBuilder.append(", mGapDir=");
        stringBuilder.append(this.mGapDir);
        stringBuilder.append(", mHasUnwantedGapAfter=");
        stringBuilder.append(this.mHasUnwantedGapAfter);
        stringBuilder.append(", mGapPerSpan=");
        stringBuilder.append(Arrays.toString(this.mGapPerSpan));
        stringBuilder.append('}');
        return stringBuilder.toString();
      }
      
      public void writeToParcel(Parcel param2Parcel, int param2Int) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
    }
    
    static final class null extends Object implements Parcelable.Creator<FullSpanItem> {
      public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFromParcel(Parcel param2Parcel) { return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem(param2Parcel); }
      
      public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[] newArray(int param2Int) { return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[param2Int]; }
    }
  }
  
  static class FullSpanItem implements Parcelable {
    public static final Parcelable.Creator<FullSpanItem> CREATOR = new Parcelable.Creator<FullSpanItem>() {
        public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFromParcel(Parcel param3Parcel) { return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem(param3Parcel); }
        
        public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[] newArray(int param3Int) { return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[param3Int]; }
      };
    
    int mGapDir;
    
    int[] mGapPerSpan;
    
    boolean mHasUnwantedGapAfter;
    
    int mPosition;
    
    FullSpanItem() {}
    
    FullSpanItem(Parcel param1Parcel) {
      this.mPosition = param1Parcel.readInt();
      this.mGapDir = param1Parcel.readInt();
      int i = param1Parcel.readInt();
      boolean bool = true;
      if (i != 1)
        bool = false; 
      this.mHasUnwantedGapAfter = bool;
      i = param1Parcel.readInt();
      if (i > 0) {
        this.mGapPerSpan = new int[i];
        param1Parcel.readIntArray(this.mGapPerSpan);
      } 
    }
    
    public int describeContents() { return 0; }
    
    int getGapForSpan(int param1Int) { return (this.mGapPerSpan == null) ? 0 : this.mGapPerSpan[param1Int]; }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("FullSpanItem{mPosition=");
      stringBuilder.append(this.mPosition);
      stringBuilder.append(", mGapDir=");
      stringBuilder.append(this.mGapDir);
      stringBuilder.append(", mHasUnwantedGapAfter=");
      stringBuilder.append(this.mHasUnwantedGapAfter);
      stringBuilder.append(", mGapPerSpan=");
      stringBuilder.append(Arrays.toString(this.mGapPerSpan));
      stringBuilder.append('}');
      return stringBuilder.toString();
    }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  }
  
  static final class null extends Object implements Parcelable.Creator<LazySpanLookup.FullSpanItem> {
    public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFromParcel(Parcel param1Parcel) { return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem(param1Parcel); }
    
    public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[] newArray(int param1Int) { return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[param1Int]; }
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static class SavedState implements Parcelable {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
        public StaggeredGridLayoutManager.SavedState createFromParcel(Parcel param2Parcel) { return new StaggeredGridLayoutManager.SavedState(param2Parcel); }
        
        public StaggeredGridLayoutManager.SavedState[] newArray(int param2Int) { return new StaggeredGridLayoutManager.SavedState[param2Int]; }
      };
    
    boolean mAnchorLayoutFromEnd;
    
    int mAnchorPosition;
    
    List<StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem> mFullSpanItems;
    
    boolean mLastLayoutRTL;
    
    boolean mReverseLayout;
    
    int[] mSpanLookup;
    
    int mSpanLookupSize;
    
    int[] mSpanOffsets;
    
    int mSpanOffsetsSize;
    
    int mVisibleAnchorPosition;
    
    public SavedState() {}
    
    SavedState(Parcel param1Parcel) {
      this.mAnchorPosition = param1Parcel.readInt();
      this.mVisibleAnchorPosition = param1Parcel.readInt();
      this.mSpanOffsetsSize = param1Parcel.readInt();
      if (this.mSpanOffsetsSize > 0) {
        this.mSpanOffsets = new int[this.mSpanOffsetsSize];
        param1Parcel.readIntArray(this.mSpanOffsets);
      } 
      this.mSpanLookupSize = param1Parcel.readInt();
      if (this.mSpanLookupSize > 0) {
        this.mSpanLookup = new int[this.mSpanLookupSize];
        param1Parcel.readIntArray(this.mSpanLookup);
      } 
      int i = param1Parcel.readInt();
      byte b2 = 0;
      if (i == 1) {
        b1 = 1;
      } else {
        b1 = 0;
      } 
      this.mReverseLayout = b1;
      if (param1Parcel.readInt() == 1) {
        b1 = 1;
      } else {
        b1 = 0;
      } 
      this.mAnchorLayoutFromEnd = b1;
      byte b1 = b2;
      if (param1Parcel.readInt() == 1)
        b1 = 1; 
      this.mLastLayoutRTL = b1;
      this.mFullSpanItems = param1Parcel.readArrayList(StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem.class.getClassLoader());
    }
    
    public SavedState(SavedState param1SavedState) {
      this.mSpanOffsetsSize = param1SavedState.mSpanOffsetsSize;
      this.mAnchorPosition = param1SavedState.mAnchorPosition;
      this.mVisibleAnchorPosition = param1SavedState.mVisibleAnchorPosition;
      this.mSpanOffsets = param1SavedState.mSpanOffsets;
      this.mSpanLookupSize = param1SavedState.mSpanLookupSize;
      this.mSpanLookup = param1SavedState.mSpanLookup;
      this.mReverseLayout = param1SavedState.mReverseLayout;
      this.mAnchorLayoutFromEnd = param1SavedState.mAnchorLayoutFromEnd;
      this.mLastLayoutRTL = param1SavedState.mLastLayoutRTL;
      this.mFullSpanItems = param1SavedState.mFullSpanItems;
    }
    
    public int describeContents() { return 0; }
    
    void invalidateAnchorPositionInfo() {
      this.mSpanOffsets = null;
      this.mSpanOffsetsSize = 0;
      this.mAnchorPosition = -1;
      this.mVisibleAnchorPosition = -1;
    }
    
    void invalidateSpanInfo() {
      this.mSpanOffsets = null;
      this.mSpanOffsetsSize = 0;
      this.mSpanLookupSize = 0;
      this.mSpanLookup = null;
      this.mFullSpanItems = null;
    }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  }
  
  static final class null extends Object implements Parcelable.Creator<SavedState> {
    public StaggeredGridLayoutManager.SavedState createFromParcel(Parcel param1Parcel) { return new StaggeredGridLayoutManager.SavedState(param1Parcel); }
    
    public StaggeredGridLayoutManager.SavedState[] newArray(int param1Int) { return new StaggeredGridLayoutManager.SavedState[param1Int]; }
  }
  
  class Span {
    static final int INVALID_LINE = -2147483648;
    
    int mCachedEnd = Integer.MIN_VALUE;
    
    int mCachedStart = Integer.MIN_VALUE;
    
    int mDeletedSize = 0;
    
    final int mIndex;
    
    ArrayList<View> mViews = new ArrayList();
    
    Span(int param1Int) { this.mIndex = param1Int; }
    
    void appendToSpan(View param1View) {
      StaggeredGridLayoutManager.LayoutParams layoutParams = getLayoutParams(param1View);
      layoutParams.mSpan = this;
      this.mViews.add(param1View);
      this.mCachedEnd = Integer.MIN_VALUE;
      if (this.mViews.size() == 1)
        this.mCachedStart = Integer.MIN_VALUE; 
      if (layoutParams.isItemRemoved() || layoutParams.isItemChanged())
        this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(param1View); 
    }
    
    void cacheReferenceLineAndClear(boolean param1Boolean, int param1Int) {
      int i;
      if (param1Boolean) {
        i = getEndLine(-2147483648);
      } else {
        i = getStartLine(-2147483648);
      } 
      clear();
      if (i == Integer.MIN_VALUE)
        return; 
      if ((param1Boolean && i < StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding()) || (!param1Boolean && i > StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding()))
        return; 
      int j = i;
      if (param1Int != Integer.MIN_VALUE)
        j = i + param1Int; 
      this.mCachedEnd = j;
      this.mCachedStart = j;
    }
    
    void calculateCachedEnd() {
      View view = (View)this.mViews.get(this.mViews.size() - 1);
      StaggeredGridLayoutManager.LayoutParams layoutParams = getLayoutParams(view);
      this.mCachedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(view);
      if (layoutParams.mFullSpan) {
        StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem fullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(layoutParams.getViewLayoutPosition());
        if (fullSpanItem != null && fullSpanItem.mGapDir == 1)
          this.mCachedEnd += fullSpanItem.getGapForSpan(this.mIndex); 
      } 
    }
    
    void calculateCachedStart() {
      View view = (View)this.mViews.get(0);
      StaggeredGridLayoutManager.LayoutParams layoutParams = getLayoutParams(view);
      this.mCachedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(view);
      if (layoutParams.mFullSpan) {
        StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem fullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(layoutParams.getViewLayoutPosition());
        if (fullSpanItem != null && fullSpanItem.mGapDir == -1)
          this.mCachedStart -= fullSpanItem.getGapForSpan(this.mIndex); 
      } 
    }
    
    void clear() {
      this.mViews.clear();
      invalidateCache();
      this.mDeletedSize = 0;
    }
    
    public int findFirstCompletelyVisibleItemPosition() { return StaggeredGridLayoutManager.this.mReverseLayout ? findOneVisibleChild(this.mViews.size() - 1, -1, true) : findOneVisibleChild(0, this.mViews.size(), true); }
    
    public int findFirstPartiallyVisibleItemPosition() { return StaggeredGridLayoutManager.this.mReverseLayout ? findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true) : findOnePartiallyVisibleChild(0, this.mViews.size(), true); }
    
    public int findFirstVisibleItemPosition() { return StaggeredGridLayoutManager.this.mReverseLayout ? findOneVisibleChild(this.mViews.size() - 1, -1, false) : findOneVisibleChild(0, this.mViews.size(), false); }
    
    public int findLastCompletelyVisibleItemPosition() { return StaggeredGridLayoutManager.this.mReverseLayout ? findOneVisibleChild(0, this.mViews.size(), true) : findOneVisibleChild(this.mViews.size() - 1, -1, true); }
    
    public int findLastPartiallyVisibleItemPosition() { return StaggeredGridLayoutManager.this.mReverseLayout ? findOnePartiallyVisibleChild(0, this.mViews.size(), true) : findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true); }
    
    public int findLastVisibleItemPosition() { return StaggeredGridLayoutManager.this.mReverseLayout ? findOneVisibleChild(0, this.mViews.size(), false) : findOneVisibleChild(this.mViews.size() - 1, -1, false); }
    
    int findOnePartiallyOrCompletelyVisibleChild(int param1Int1, int param1Int2, boolean param1Boolean1, boolean param1Boolean2, boolean param1Boolean3) {
      int i;
      int j = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
      int k = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
      if (param1Int2 > param1Int1) {
        i = 1;
      } else {
        i = -1;
      } 
      while (param1Int1 != param1Int2) {
        boolean bool1;
        View view = (View)this.mViews.get(param1Int1);
        int m = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(view);
        int n = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(view);
        boolean bool2 = false;
        if (param1Boolean3 ? (m <= k) : (m < k)) {
          bool1 = true;
        } else {
          bool1 = false;
        } 
        if (param1Boolean3 ? (n >= j) : (n > j))
          bool2 = true; 
        if (bool1 && bool2)
          if (param1Boolean1 && param1Boolean2) {
            if (m >= j && n <= k)
              return StaggeredGridLayoutManager.this.getPosition(view); 
          } else {
            if (param1Boolean2)
              return StaggeredGridLayoutManager.this.getPosition(view); 
            if (m < j || n > k)
              return StaggeredGridLayoutManager.this.getPosition(view); 
          }  
        param1Int1 += i;
      } 
      return -1;
    }
    
    int findOnePartiallyVisibleChild(int param1Int1, int param1Int2, boolean param1Boolean) { return findOnePartiallyOrCompletelyVisibleChild(param1Int1, param1Int2, false, false, param1Boolean); }
    
    int findOneVisibleChild(int param1Int1, int param1Int2, boolean param1Boolean) { return findOnePartiallyOrCompletelyVisibleChild(param1Int1, param1Int2, param1Boolean, true, false); }
    
    public int getDeletedSize() { return this.mDeletedSize; }
    
    int getEndLine() {
      if (this.mCachedEnd != Integer.MIN_VALUE)
        return this.mCachedEnd; 
      calculateCachedEnd();
      return this.mCachedEnd;
    }
    
    int getEndLine(int param1Int) {
      if (this.mCachedEnd != Integer.MIN_VALUE)
        return this.mCachedEnd; 
      if (this.mViews.size() == 0)
        return param1Int; 
      calculateCachedEnd();
      return this.mCachedEnd;
    }
    
    public View getFocusableViewAfter(int param1Int1, int param1Int2) { // Byte code:
      //   0: aconst_null
      //   1: astore #5
      //   3: aconst_null
      //   4: astore #4
      //   6: iload_2
      //   7: iconst_m1
      //   8: if_icmpne -> 119
      //   11: aload_0
      //   12: getfield mViews : Ljava/util/ArrayList;
      //   15: invokevirtual size : ()I
      //   18: istore_3
      //   19: iconst_0
      //   20: istore_2
      //   21: aload #4
      //   23: astore #5
      //   25: iload_2
      //   26: iload_3
      //   27: if_icmpge -> 230
      //   30: aload_0
      //   31: getfield mViews : Ljava/util/ArrayList;
      //   34: iload_2
      //   35: invokevirtual get : (I)Ljava/lang/Object;
      //   38: checkcast android/view/View
      //   41: astore #6
      //   43: aload_0
      //   44: getfield this$0 : Landroid/support/v7/widget/StaggeredGridLayoutManager;
      //   47: getfield mReverseLayout : Z
      //   50: ifeq -> 70
      //   53: aload #4
      //   55: astore #5
      //   57: aload_0
      //   58: getfield this$0 : Landroid/support/v7/widget/StaggeredGridLayoutManager;
      //   61: aload #6
      //   63: invokevirtual getPosition : (Landroid/view/View;)I
      //   66: iload_1
      //   67: if_icmple -> 230
      //   70: aload_0
      //   71: getfield this$0 : Landroid/support/v7/widget/StaggeredGridLayoutManager;
      //   74: getfield mReverseLayout : Z
      //   77: ifne -> 96
      //   80: aload_0
      //   81: getfield this$0 : Landroid/support/v7/widget/StaggeredGridLayoutManager;
      //   84: aload #6
      //   86: invokevirtual getPosition : (Landroid/view/View;)I
      //   89: iload_1
      //   90: if_icmplt -> 96
      //   93: aload #4
      //   95: areturn
      //   96: aload #4
      //   98: astore #5
      //   100: aload #6
      //   102: invokevirtual hasFocusable : ()Z
      //   105: ifeq -> 230
      //   108: iload_2
      //   109: iconst_1
      //   110: iadd
      //   111: istore_2
      //   112: aload #6
      //   114: astore #4
      //   116: goto -> 21
      //   119: aload_0
      //   120: getfield mViews : Ljava/util/ArrayList;
      //   123: invokevirtual size : ()I
      //   126: iconst_1
      //   127: isub
      //   128: istore_2
      //   129: aload #5
      //   131: astore #4
      //   133: aload #4
      //   135: astore #5
      //   137: iload_2
      //   138: iflt -> 230
      //   141: aload_0
      //   142: getfield mViews : Ljava/util/ArrayList;
      //   145: iload_2
      //   146: invokevirtual get : (I)Ljava/lang/Object;
      //   149: checkcast android/view/View
      //   152: astore #6
      //   154: aload_0
      //   155: getfield this$0 : Landroid/support/v7/widget/StaggeredGridLayoutManager;
      //   158: getfield mReverseLayout : Z
      //   161: ifeq -> 181
      //   164: aload #4
      //   166: astore #5
      //   168: aload_0
      //   169: getfield this$0 : Landroid/support/v7/widget/StaggeredGridLayoutManager;
      //   172: aload #6
      //   174: invokevirtual getPosition : (Landroid/view/View;)I
      //   177: iload_1
      //   178: if_icmpge -> 230
      //   181: aload_0
      //   182: getfield this$0 : Landroid/support/v7/widget/StaggeredGridLayoutManager;
      //   185: getfield mReverseLayout : Z
      //   188: ifne -> 207
      //   191: aload_0
      //   192: getfield this$0 : Landroid/support/v7/widget/StaggeredGridLayoutManager;
      //   195: aload #6
      //   197: invokevirtual getPosition : (Landroid/view/View;)I
      //   200: iload_1
      //   201: if_icmpgt -> 207
      //   204: aload #4
      //   206: areturn
      //   207: aload #4
      //   209: astore #5
      //   211: aload #6
      //   213: invokevirtual hasFocusable : ()Z
      //   216: ifeq -> 230
      //   219: iload_2
      //   220: iconst_1
      //   221: isub
      //   222: istore_2
      //   223: aload #6
      //   225: astore #4
      //   227: goto -> 133
      //   230: aload #5
      //   232: areturn }
    
    StaggeredGridLayoutManager.LayoutParams getLayoutParams(View param1View) { return (StaggeredGridLayoutManager.LayoutParams)param1View.getLayoutParams(); }
    
    int getStartLine() {
      if (this.mCachedStart != Integer.MIN_VALUE)
        return this.mCachedStart; 
      calculateCachedStart();
      return this.mCachedStart;
    }
    
    int getStartLine(int param1Int) {
      if (this.mCachedStart != Integer.MIN_VALUE)
        return this.mCachedStart; 
      if (this.mViews.size() == 0)
        return param1Int; 
      calculateCachedStart();
      return this.mCachedStart;
    }
    
    void invalidateCache() {
      this.mCachedStart = Integer.MIN_VALUE;
      this.mCachedEnd = Integer.MIN_VALUE;
    }
    
    void onOffset(int param1Int) {
      if (this.mCachedStart != Integer.MIN_VALUE)
        this.mCachedStart += param1Int; 
      if (this.mCachedEnd != Integer.MIN_VALUE)
        this.mCachedEnd += param1Int; 
    }
    
    void popEnd() {
      int i = this.mViews.size();
      View view = (View)this.mViews.remove(i - 1);
      StaggeredGridLayoutManager.LayoutParams layoutParams = getLayoutParams(view);
      layoutParams.mSpan = null;
      if (layoutParams.isItemRemoved() || layoutParams.isItemChanged())
        this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view); 
      if (i == 1)
        this.mCachedStart = Integer.MIN_VALUE; 
      this.mCachedEnd = Integer.MIN_VALUE;
    }
    
    void popStart() {
      View view = (View)this.mViews.remove(0);
      StaggeredGridLayoutManager.LayoutParams layoutParams = getLayoutParams(view);
      layoutParams.mSpan = null;
      if (this.mViews.size() == 0)
        this.mCachedEnd = Integer.MIN_VALUE; 
      if (layoutParams.isItemRemoved() || layoutParams.isItemChanged())
        this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view); 
      this.mCachedStart = Integer.MIN_VALUE;
    }
    
    void prependToSpan(View param1View) {
      StaggeredGridLayoutManager.LayoutParams layoutParams = getLayoutParams(param1View);
      layoutParams.mSpan = this;
      this.mViews.add(0, param1View);
      this.mCachedStart = Integer.MIN_VALUE;
      if (this.mViews.size() == 1)
        this.mCachedEnd = Integer.MIN_VALUE; 
      if (layoutParams.isItemRemoved() || layoutParams.isItemChanged())
        this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(param1View); 
    }
    
    void setLine(int param1Int) {
      this.mCachedStart = param1Int;
      this.mCachedEnd = param1Int;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/StaggeredGridLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */