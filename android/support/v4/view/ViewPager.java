package android.support.v4.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.Scroller;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewPager extends ViewGroup {
  private static final int CLOSE_ENOUGH = 2;
  
  private static final Comparator<ItemInfo> COMPARATOR;
  
  private static final boolean DEBUG = false;
  
  private static final int DEFAULT_GUTTER_SIZE = 16;
  
  private static final int DEFAULT_OFFSCREEN_PAGES = 1;
  
  private static final int DRAW_ORDER_DEFAULT = 0;
  
  private static final int DRAW_ORDER_FORWARD = 1;
  
  private static final int DRAW_ORDER_REVERSE = 2;
  
  private static final int INVALID_POINTER = -1;
  
  static final int[] LAYOUT_ATTRS = { 16842931 };
  
  private static final int MAX_SETTLE_DURATION = 600;
  
  private static final int MIN_DISTANCE_FOR_FLING = 25;
  
  private static final int MIN_FLING_VELOCITY = 400;
  
  public static final int SCROLL_STATE_DRAGGING = 1;
  
  public static final int SCROLL_STATE_IDLE = 0;
  
  public static final int SCROLL_STATE_SETTLING = 2;
  
  private static final String TAG = "ViewPager";
  
  private static final boolean USE_CACHE = false;
  
  private static final Interpolator sInterpolator;
  
  private static final ViewPositionComparator sPositionComparator;
  
  private int mActivePointerId = -1;
  
  PagerAdapter mAdapter;
  
  private List<OnAdapterChangeListener> mAdapterChangeListeners;
  
  private int mBottomPageBounds;
  
  private boolean mCalledSuper;
  
  private int mChildHeightMeasureSpec;
  
  private int mChildWidthMeasureSpec;
  
  private int mCloseEnough;
  
  int mCurItem;
  
  private int mDecorChildCount;
  
  private int mDefaultGutterSize;
  
  private int mDrawingOrder;
  
  private ArrayList<View> mDrawingOrderedChildren;
  
  private final Runnable mEndScrollRunnable = new Runnable() {
      public void run() {
        ViewPager.this.setScrollState(0);
        ViewPager.this.populate();
      }
    };
  
  private int mExpectedAdapterCount;
  
  private long mFakeDragBeginTime;
  
  private boolean mFakeDragging;
  
  private boolean mFirstLayout = true;
  
  private float mFirstOffset = -3.4028235E38F;
  
  private int mFlingDistance;
  
  private int mGutterSize;
  
  private boolean mInLayout;
  
  private float mInitialMotionX;
  
  private float mInitialMotionY;
  
  private OnPageChangeListener mInternalPageChangeListener;
  
  private boolean mIsBeingDragged;
  
  private boolean mIsScrollStarted;
  
  private boolean mIsUnableToDrag;
  
  private final ArrayList<ItemInfo> mItems = new ArrayList();
  
  private float mLastMotionX;
  
  private float mLastMotionY;
  
  private float mLastOffset = Float.MAX_VALUE;
  
  private EdgeEffect mLeftEdge;
  
  private Drawable mMarginDrawable;
  
  private int mMaximumVelocity;
  
  private int mMinimumVelocity;
  
  private boolean mNeedCalculatePageOffsets = false;
  
  private PagerObserver mObserver;
  
  private int mOffscreenPageLimit = 1;
  
  private OnPageChangeListener mOnPageChangeListener;
  
  private List<OnPageChangeListener> mOnPageChangeListeners;
  
  private int mPageMargin;
  
  private PageTransformer mPageTransformer;
  
  private int mPageTransformerLayerType;
  
  private boolean mPopulatePending;
  
  private Parcelable mRestoredAdapterState = null;
  
  private ClassLoader mRestoredClassLoader = null;
  
  private int mRestoredCurItem = -1;
  
  private EdgeEffect mRightEdge;
  
  private int mScrollState = 0;
  
  private Scroller mScroller;
  
  private boolean mScrollingCacheEnabled;
  
  private final ItemInfo mTempItem = new ItemInfo();
  
  private final Rect mTempRect = new Rect();
  
  private int mTopPageBounds;
  
  private int mTouchSlop;
  
  private VelocityTracker mVelocityTracker;
  
  static  {
    COMPARATOR = new Comparator<ItemInfo>() {
        public int compare(ViewPager.ItemInfo param1ItemInfo1, ViewPager.ItemInfo param1ItemInfo2) { return param1ItemInfo1.position - param1ItemInfo2.position; }
      };
    sInterpolator = new Interpolator() {
        public float getInterpolation(float param1Float) {
          param1Float--;
          return param1Float * param1Float * param1Float * param1Float * param1Float + 1.0F;
        }
      };
    sPositionComparator = new ViewPositionComparator();
  }
  
  public ViewPager(@NonNull Context paramContext) {
    super(paramContext);
    initViewPager();
  }
  
  public ViewPager(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    initViewPager();
  }
  
  private void calculatePageOffsets(ItemInfo paramItemInfo1, int paramInt, ItemInfo paramItemInfo2) {
    float f2;
    int m = this.mAdapter.getCount();
    int i = getClientWidth();
    if (i > 0) {
      f2 = this.mPageMargin / i;
    } else {
      f2 = 0.0F;
    } 
    if (paramItemInfo2 != null) {
      i = paramItemInfo2.position;
      if (i < paramItemInfo1.position) {
        f1 = paramItemInfo2.offset + paramItemInfo2.widthFactor + f2;
        i++;
        byte b = 0;
        while (i <= paramItemInfo1.position && b < this.mItems.size()) {
          int n;
          float f;
          paramItemInfo2 = (ItemInfo)this.mItems.get(b);
          while (true) {
            n = i;
            f = f1;
            if (i > paramItemInfo2.position) {
              n = i;
              f = f1;
              if (b < this.mItems.size() - 1) {
                paramItemInfo2 = (ItemInfo)this.mItems.get(++b);
                continue;
              } 
            } 
            break;
          } 
          while (n < paramItemInfo2.position) {
            f += this.mAdapter.getPageWidth(n) + f2;
            n++;
          } 
          paramItemInfo2.offset = f;
          f1 = f + paramItemInfo2.widthFactor + f2;
          i = n + 1;
        } 
      } else if (i > paramItemInfo1.position) {
        int n = this.mItems.size() - 1;
        f1 = paramItemInfo2.offset;
        while (--i >= paramItemInfo1.position && n >= 0) {
          int i1;
          float f;
          paramItemInfo2 = (ItemInfo)this.mItems.get(n);
          while (true) {
            i1 = i;
            f = f1;
            if (i < paramItemInfo2.position) {
              i1 = i;
              f = f1;
              if (n > 0) {
                paramItemInfo2 = (ItemInfo)this.mItems.get(--n);
                continue;
              } 
            } 
            break;
          } 
          while (i1 > paramItemInfo2.position) {
            f -= this.mAdapter.getPageWidth(i1) + f2;
            i1--;
          } 
          f1 = f - paramItemInfo2.widthFactor + f2;
          paramItemInfo2.offset = f1;
          i = i1 - 1;
        } 
      } 
    } 
    int k = this.mItems.size();
    float f3 = paramItemInfo1.offset;
    i = paramItemInfo1.position - 1;
    if (paramItemInfo1.position == 0) {
      f1 = paramItemInfo1.offset;
    } else {
      f1 = -3.4028235E38F;
    } 
    this.mFirstOffset = f1;
    int j = paramItemInfo1.position;
    if (j == --m) {
      f1 = paramItemInfo1.offset + paramItemInfo1.widthFactor - 1.0F;
    } else {
      f1 = Float.MAX_VALUE;
    } 
    this.mLastOffset = f1;
    j = paramInt - 1;
    float f1 = f3;
    while (j >= 0) {
      paramItemInfo2 = (ItemInfo)this.mItems.get(j);
      while (i > paramItemInfo2.position) {
        f1 -= this.mAdapter.getPageWidth(i) + f2;
        i--;
      } 
      f1 -= paramItemInfo2.widthFactor + f2;
      paramItemInfo2.offset = f1;
      if (paramItemInfo2.position == 0)
        this.mFirstOffset = f1; 
      j--;
      i--;
    } 
    f1 = paramItemInfo1.offset + paramItemInfo1.widthFactor + f2;
    j = paramItemInfo1.position + 1;
    i = paramInt + 1;
    for (paramInt = j; i < k; paramInt++) {
      paramItemInfo1 = (ItemInfo)this.mItems.get(i);
      while (paramInt < paramItemInfo1.position) {
        f1 += this.mAdapter.getPageWidth(paramInt) + f2;
        paramInt++;
      } 
      if (paramItemInfo1.position == m)
        this.mLastOffset = paramItemInfo1.widthFactor + f1 - 1.0F; 
      paramItemInfo1.offset = f1;
      f1 += paramItemInfo1.widthFactor + f2;
      i++;
    } 
    this.mNeedCalculatePageOffsets = false;
  }
  
  private void completeScroll(boolean paramBoolean) {
    if (this.mScrollState == 2) {
      b1 = 1;
    } else {
      b1 = 0;
    } 
    if (b1) {
      setScrollingCacheEnabled(false);
      if (this.mScroller.isFinished() ^ true) {
        this.mScroller.abortAnimation();
        int i = getScrollX();
        int j = getScrollY();
        int k = this.mScroller.getCurrX();
        int m = this.mScroller.getCurrY();
        if (i != k || j != m) {
          scrollTo(k, m);
          if (k != i)
            pageScrolled(k); 
        } 
      } 
    } 
    this.mPopulatePending = false;
    byte b2 = 0;
    boolean bool = b1;
    for (byte b1 = b2; b1 < this.mItems.size(); b1++) {
      ItemInfo itemInfo = (ItemInfo)this.mItems.get(b1);
      if (itemInfo.scrolling) {
        itemInfo.scrolling = false;
        bool = true;
      } 
    } 
    if (bool) {
      if (paramBoolean) {
        ViewCompat.postOnAnimation(this, this.mEndScrollRunnable);
        return;
      } 
      this.mEndScrollRunnable.run();
    } 
  }
  
  private int determineTargetPage(int paramInt1, float paramFloat, int paramInt2, int paramInt3) {
    if (Math.abs(paramInt3) > this.mFlingDistance && Math.abs(paramInt2) > this.mMinimumVelocity) {
      if (paramInt2 <= 0)
        paramInt1++; 
    } else {
      float f;
      if (paramInt1 >= this.mCurItem) {
        f = 0.4F;
      } else {
        f = 0.6F;
      } 
      paramInt1 += (int)(paramFloat + f);
    } 
    paramInt2 = paramInt1;
    if (this.mItems.size() > 0) {
      ItemInfo itemInfo1 = (ItemInfo)this.mItems.get(0);
      ItemInfo itemInfo2 = (ItemInfo)this.mItems.get(this.mItems.size() - 1);
      paramInt2 = Math.max(itemInfo1.position, Math.min(paramInt1, itemInfo2.position));
    } 
    return paramInt2;
  }
  
  private void dispatchOnPageScrolled(int paramInt1, float paramFloat, int paramInt2) {
    if (this.mOnPageChangeListener != null)
      this.mOnPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2); 
    if (this.mOnPageChangeListeners != null) {
      byte b = 0;
      int i = this.mOnPageChangeListeners.size();
      while (b < i) {
        OnPageChangeListener onPageChangeListener = (OnPageChangeListener)this.mOnPageChangeListeners.get(b);
        if (onPageChangeListener != null)
          onPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2); 
        b++;
      } 
    } 
    if (this.mInternalPageChangeListener != null)
      this.mInternalPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2); 
  }
  
  private void dispatchOnPageSelected(int paramInt) {
    if (this.mOnPageChangeListener != null)
      this.mOnPageChangeListener.onPageSelected(paramInt); 
    if (this.mOnPageChangeListeners != null) {
      byte b = 0;
      int i = this.mOnPageChangeListeners.size();
      while (b < i) {
        OnPageChangeListener onPageChangeListener = (OnPageChangeListener)this.mOnPageChangeListeners.get(b);
        if (onPageChangeListener != null)
          onPageChangeListener.onPageSelected(paramInt); 
        b++;
      } 
    } 
    if (this.mInternalPageChangeListener != null)
      this.mInternalPageChangeListener.onPageSelected(paramInt); 
  }
  
  private void dispatchOnScrollStateChanged(int paramInt) {
    if (this.mOnPageChangeListener != null)
      this.mOnPageChangeListener.onPageScrollStateChanged(paramInt); 
    if (this.mOnPageChangeListeners != null) {
      byte b = 0;
      int i = this.mOnPageChangeListeners.size();
      while (b < i) {
        OnPageChangeListener onPageChangeListener = (OnPageChangeListener)this.mOnPageChangeListeners.get(b);
        if (onPageChangeListener != null)
          onPageChangeListener.onPageScrollStateChanged(paramInt); 
        b++;
      } 
    } 
    if (this.mInternalPageChangeListener != null)
      this.mInternalPageChangeListener.onPageScrollStateChanged(paramInt); 
  }
  
  private void enableLayers(boolean paramBoolean) {
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      byte b1;
      if (paramBoolean) {
        b1 = this.mPageTransformerLayerType;
      } else {
        b1 = 0;
      } 
      getChildAt(b).setLayerType(b1, null);
    } 
  }
  
  private void endDrag() {
    this.mIsBeingDragged = false;
    this.mIsUnableToDrag = false;
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    } 
  }
  
  private Rect getChildRectInPagerCoordinates(Rect paramRect, View paramView) {
    Rect rect = paramRect;
    if (paramRect == null)
      rect = new Rect(); 
    if (paramView == null) {
      rect.set(0, 0, 0, 0);
      return rect;
    } 
    rect.left = paramView.getLeft();
    rect.right = paramView.getRight();
    rect.top = paramView.getTop();
    rect.bottom = paramView.getBottom();
    ViewParent viewParent = paramView.getParent();
    while (viewParent instanceof ViewGroup && viewParent != this) {
      ViewGroup viewGroup = (ViewGroup)viewParent;
      rect.left += viewGroup.getLeft();
      rect.right += viewGroup.getRight();
      rect.top += viewGroup.getTop();
      rect.bottom += viewGroup.getBottom();
      ViewParent viewParent1 = viewGroup.getParent();
    } 
    return rect;
  }
  
  private int getClientWidth() { return getMeasuredWidth() - getPaddingLeft() - getPaddingRight(); }
  
  private ItemInfo infoForCurrentScrollPosition() {
    float f2;
    float f1;
    int i = getClientWidth();
    if (i > 0) {
      f1 = getScrollX() / i;
    } else {
      f1 = 0.0F;
    } 
    if (i > 0) {
      f2 = this.mPageMargin / i;
    } else {
      f2 = 0.0F;
    } 
    ItemInfo itemInfo = null;
    i = 0;
    boolean bool = true;
    int j = -1;
    float f3 = 0.0F;
    float f4 = 0.0F;
    while (i < this.mItems.size()) {
      ItemInfo itemInfo2 = (ItemInfo)this.mItems.get(i);
      int k = i;
      ItemInfo itemInfo1 = itemInfo2;
      if (!bool) {
        int m = itemInfo2.position;
        j++;
        k = i;
        itemInfo1 = itemInfo2;
        if (m != j) {
          itemInfo1 = this.mTempItem;
          itemInfo1.offset = f3 + f4 + f2;
          itemInfo1.position = j;
          itemInfo1.widthFactor = this.mAdapter.getPageWidth(itemInfo1.position);
          k = i - 1;
        } 
      } 
      f3 = itemInfo1.offset;
      f4 = itemInfo1.widthFactor;
      if (bool || f1 >= f3) {
        if (f1 >= f4 + f3 + f2) {
          if (k == this.mItems.size() - 1)
            return itemInfo1; 
          j = itemInfo1.position;
          f4 = itemInfo1.widthFactor;
          i = k + 1;
          bool = false;
          itemInfo = itemInfo1;
          continue;
        } 
        return itemInfo1;
      } 
      return itemInfo;
    } 
    return itemInfo;
  }
  
  private static boolean isDecorView(@NonNull View paramView) { return (paramView.getClass().getAnnotation(DecorView.class) != null); }
  
  private boolean isGutterDrag(float paramFloat1, float paramFloat2) { return ((paramFloat1 < this.mGutterSize && paramFloat2 > 0.0F) || (paramFloat1 > (getWidth() - this.mGutterSize) && paramFloat2 < 0.0F)); }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent) {
    int i = paramMotionEvent.getActionIndex();
    if (paramMotionEvent.getPointerId(i) == this.mActivePointerId) {
      if (i == 0) {
        i = 1;
      } else {
        i = 0;
      } 
      this.mLastMotionX = paramMotionEvent.getX(i);
      this.mActivePointerId = paramMotionEvent.getPointerId(i);
      if (this.mVelocityTracker != null)
        this.mVelocityTracker.clear(); 
    } 
  }
  
  private boolean pageScrolled(int paramInt) {
    if (this.mItems.size() == 0) {
      if (this.mFirstLayout)
        return false; 
      this.mCalledSuper = false;
      onPageScrolled(0, 0.0F, 0);
      if (!this.mCalledSuper)
        throw new IllegalStateException("onPageScrolled did not call superclass implementation"); 
      return false;
    } 
    ItemInfo itemInfo = infoForCurrentScrollPosition();
    int j = getClientWidth();
    int k = this.mPageMargin;
    float f2 = this.mPageMargin;
    float f1 = j;
    f2 /= f1;
    int i = itemInfo.position;
    f1 = (paramInt / f1 - itemInfo.offset) / (itemInfo.widthFactor + f2);
    paramInt = (int)((k + j) * f1);
    this.mCalledSuper = false;
    onPageScrolled(i, f1, paramInt);
    if (!this.mCalledSuper)
      throw new IllegalStateException("onPageScrolled did not call superclass implementation"); 
    return true;
  }
  
  private boolean performDrag(float paramFloat) {
    boolean bool;
    float f1 = this.mLastMotionX;
    this.mLastMotionX = paramFloat;
    float f2 = getScrollX() + f1 - paramFloat;
    float f3 = getClientWidth();
    paramFloat = this.mFirstOffset * f3;
    f1 = this.mLastOffset * f3;
    ArrayList arrayList = this.mItems;
    byte b1 = 0;
    byte b2 = 0;
    int j = 0;
    ItemInfo itemInfo1 = (ItemInfo)arrayList.get(0);
    ItemInfo itemInfo2 = (ItemInfo)this.mItems.get(this.mItems.size() - 1);
    if (itemInfo1.position != 0) {
      paramFloat = itemInfo1.offset * f3;
      i = 0;
    } else {
      i = 1;
    } 
    if (itemInfo2.position != this.mAdapter.getCount() - 1) {
      f1 = itemInfo2.offset * f3;
      bool = false;
    } else {
      bool = true;
    } 
    if (f2 < paramFloat) {
      if (i) {
        this.mLeftEdge.onPull(Math.abs(paramFloat - f2) / f3);
        j = 1;
      } 
    } else {
      j = b2;
      paramFloat = f2;
      if (f2 > f1) {
        j = b1;
        if (bool) {
          this.mRightEdge.onPull(Math.abs(f2 - f1) / f3);
          j = 1;
        } 
        paramFloat = f1;
      } 
    } 
    f1 = this.mLastMotionX;
    int i = (int)paramFloat;
    this.mLastMotionX = f1 + paramFloat - i;
    scrollTo(i, getScrollY());
    pageScrolled(i);
    return j;
  }
  
  private void recomputeScrollPosition(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    float f;
    if (paramInt2 > 0 && !this.mItems.isEmpty()) {
      if (!this.mScroller.isFinished()) {
        this.mScroller.setFinalX(getCurrentItem() * getClientWidth());
        return;
      } 
      int i = getPaddingLeft();
      int j = getPaddingRight();
      int k = getPaddingLeft();
      int m = getPaddingRight();
      scrollTo((int)(getScrollX() / (paramInt2 - k - m + paramInt4) * (paramInt1 - i - j + paramInt3)), getScrollY());
      return;
    } 
    ItemInfo itemInfo = infoForPosition(this.mCurItem);
    if (itemInfo != null) {
      f = Math.min(itemInfo.offset, this.mLastOffset);
    } else {
      f = 0.0F;
    } 
    paramInt1 = (int)(f * (paramInt1 - getPaddingLeft() - getPaddingRight()));
    if (paramInt1 != getScrollX()) {
      completeScroll(false);
      scrollTo(paramInt1, getScrollY());
    } 
  }
  
  private void removeNonDecorViews() {
    for (byte b = 0; b < getChildCount(); b = b1 + 1) {
      byte b1 = b;
      if (!((LayoutParams)getChildAt(b).getLayoutParams()).isDecor) {
        removeViewAt(b);
        b1 = b - 1;
      } 
    } 
  }
  
  private void requestParentDisallowInterceptTouchEvent(boolean paramBoolean) {
    ViewParent viewParent = getParent();
    if (viewParent != null)
      viewParent.requestDisallowInterceptTouchEvent(paramBoolean); 
  }
  
  private boolean resetTouch() {
    this.mActivePointerId = -1;
    endDrag();
    this.mLeftEdge.onRelease();
    this.mRightEdge.onRelease();
    return (this.mLeftEdge.isFinished() || this.mRightEdge.isFinished());
  }
  
  private void scrollToItem(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2) {
    byte b;
    ItemInfo itemInfo = infoForPosition(paramInt1);
    if (itemInfo != null) {
      b = (int)(getClientWidth() * Math.max(this.mFirstOffset, Math.min(itemInfo.offset, this.mLastOffset)));
    } else {
      b = 0;
    } 
    if (paramBoolean1) {
      smoothScrollTo(b, 0, paramInt2);
      if (paramBoolean2) {
        dispatchOnPageSelected(paramInt1);
        return;
      } 
    } else {
      if (paramBoolean2)
        dispatchOnPageSelected(paramInt1); 
      completeScroll(false);
      scrollTo(b, 0);
      pageScrolled(b);
    } 
  }
  
  private void setScrollingCacheEnabled(boolean paramBoolean) {
    if (this.mScrollingCacheEnabled != paramBoolean)
      this.mScrollingCacheEnabled = paramBoolean; 
  }
  
  private void sortChildDrawingOrder() {
    if (this.mDrawingOrder != 0) {
      if (this.mDrawingOrderedChildren == null) {
        this.mDrawingOrderedChildren = new ArrayList();
      } else {
        this.mDrawingOrderedChildren.clear();
      } 
      int i = getChildCount();
      for (byte b = 0; b < i; b++) {
        View view = getChildAt(b);
        this.mDrawingOrderedChildren.add(view);
      } 
      Collections.sort(this.mDrawingOrderedChildren, sPositionComparator);
    } 
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2) {
    int i = paramArrayList.size();
    int j = getDescendantFocusability();
    if (j != 393216) {
      byte b;
      for (b = 0; b < getChildCount(); b++) {
        View view = getChildAt(b);
        if (view.getVisibility() == 0) {
          ItemInfo itemInfo = infoForChild(view);
          if (itemInfo != null && itemInfo.position == this.mCurItem)
            view.addFocusables(paramArrayList, paramInt1, paramInt2); 
        } 
      } 
    } 
    if (j != 262144 || i == paramArrayList.size()) {
      if (!isFocusable())
        return; 
      if ((paramInt2 & true) == 1 && isInTouchMode() && !isFocusableInTouchMode())
        return; 
      if (paramArrayList != null)
        paramArrayList.add(this); 
    } 
  }
  
  ItemInfo addNewItem(int paramInt1, int paramInt2) {
    ItemInfo itemInfo = new ItemInfo();
    itemInfo.position = paramInt1;
    itemInfo.object = this.mAdapter.instantiateItem(this, paramInt1);
    itemInfo.widthFactor = this.mAdapter.getPageWidth(paramInt1);
    if (paramInt2 < 0 || paramInt2 >= this.mItems.size()) {
      this.mItems.add(itemInfo);
      return itemInfo;
    } 
    this.mItems.add(paramInt2, itemInfo);
    return itemInfo;
  }
  
  public void addOnAdapterChangeListener(@NonNull OnAdapterChangeListener paramOnAdapterChangeListener) {
    if (this.mAdapterChangeListeners == null)
      this.mAdapterChangeListeners = new ArrayList(); 
    this.mAdapterChangeListeners.add(paramOnAdapterChangeListener);
  }
  
  public void addOnPageChangeListener(@NonNull OnPageChangeListener paramOnPageChangeListener) {
    if (this.mOnPageChangeListeners == null)
      this.mOnPageChangeListeners = new ArrayList(); 
    this.mOnPageChangeListeners.add(paramOnPageChangeListener);
  }
  
  public void addTouchables(ArrayList<View> paramArrayList) {
    for (byte b = 0; b < getChildCount(); b++) {
      View view = getChildAt(b);
      if (view.getVisibility() == 0) {
        ItemInfo itemInfo = infoForChild(view);
        if (itemInfo != null && itemInfo.position == this.mCurItem)
          view.addTouchables(paramArrayList); 
      } 
    } 
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams) {
    ViewGroup.LayoutParams layoutParams = paramLayoutParams;
    if (!checkLayoutParams(paramLayoutParams))
      layoutParams = generateLayoutParams(paramLayoutParams); 
    paramLayoutParams = (LayoutParams)layoutParams;
    paramLayoutParams.isDecor |= isDecorView(paramView);
    if (this.mInLayout) {
      if (paramLayoutParams != null && paramLayoutParams.isDecor)
        throw new IllegalStateException("Cannot add pager decor view during layout"); 
      paramLayoutParams.needsMeasure = true;
      addViewInLayout(paramView, paramInt, layoutParams);
      return;
    } 
    super.addView(paramView, paramInt, layoutParams);
  }
  
  public boolean arrowScroll(int paramInt) { // Byte code:
    //   0: aload_0
    //   1: invokevirtual findFocus : ()Landroid/view/View;
    //   4: astore #7
    //   6: iconst_0
    //   7: istore #4
    //   9: aconst_null
    //   10: astore #6
    //   12: aload #7
    //   14: aload_0
    //   15: if_acmpne -> 25
    //   18: aload #6
    //   20: astore #5
    //   22: goto -> 198
    //   25: aload #7
    //   27: ifnull -> 194
    //   30: aload #7
    //   32: invokevirtual getParent : ()Landroid/view/ViewParent;
    //   35: astore #5
    //   37: aload #5
    //   39: instanceof android/view/ViewGroup
    //   42: ifeq -> 68
    //   45: aload #5
    //   47: aload_0
    //   48: if_acmpne -> 56
    //   51: iconst_1
    //   52: istore_2
    //   53: goto -> 70
    //   56: aload #5
    //   58: invokeinterface getParent : ()Landroid/view/ViewParent;
    //   63: astore #5
    //   65: goto -> 37
    //   68: iconst_0
    //   69: istore_2
    //   70: iload_2
    //   71: ifne -> 194
    //   74: new java/lang/StringBuilder
    //   77: dup
    //   78: invokespecial <init> : ()V
    //   81: astore #8
    //   83: aload #8
    //   85: aload #7
    //   87: invokevirtual getClass : ()Ljava/lang/Class;
    //   90: invokevirtual getSimpleName : ()Ljava/lang/String;
    //   93: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   96: pop
    //   97: aload #7
    //   99: invokevirtual getParent : ()Landroid/view/ViewParent;
    //   102: astore #5
    //   104: aload #5
    //   106: instanceof android/view/ViewGroup
    //   109: ifeq -> 147
    //   112: aload #8
    //   114: ldc_w ' => '
    //   117: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: pop
    //   121: aload #8
    //   123: aload #5
    //   125: invokevirtual getClass : ()Ljava/lang/Class;
    //   128: invokevirtual getSimpleName : ()Ljava/lang/String;
    //   131: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   134: pop
    //   135: aload #5
    //   137: invokeinterface getParent : ()Landroid/view/ViewParent;
    //   142: astore #5
    //   144: goto -> 104
    //   147: new java/lang/StringBuilder
    //   150: dup
    //   151: invokespecial <init> : ()V
    //   154: astore #5
    //   156: aload #5
    //   158: ldc_w 'arrowScroll tried to find focus based on non-child current focused view '
    //   161: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: pop
    //   165: aload #5
    //   167: aload #8
    //   169: invokevirtual toString : ()Ljava/lang/String;
    //   172: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: pop
    //   176: ldc 'ViewPager'
    //   178: aload #5
    //   180: invokevirtual toString : ()Ljava/lang/String;
    //   183: invokestatic e : (Ljava/lang/String;Ljava/lang/String;)I
    //   186: pop
    //   187: aload #6
    //   189: astore #5
    //   191: goto -> 198
    //   194: aload #7
    //   196: astore #5
    //   198: invokestatic getInstance : ()Landroid/view/FocusFinder;
    //   201: aload_0
    //   202: aload #5
    //   204: iload_1
    //   205: invokevirtual findNextFocus : (Landroid/view/ViewGroup;Landroid/view/View;I)Landroid/view/View;
    //   208: astore #6
    //   210: aload #6
    //   212: ifnull -> 348
    //   215: aload #6
    //   217: aload #5
    //   219: if_acmpeq -> 348
    //   222: iload_1
    //   223: bipush #17
    //   225: if_icmpne -> 285
    //   228: aload_0
    //   229: aload_0
    //   230: getfield mTempRect : Landroid/graphics/Rect;
    //   233: aload #6
    //   235: invokespecial getChildRectInPagerCoordinates : (Landroid/graphics/Rect;Landroid/view/View;)Landroid/graphics/Rect;
    //   238: getfield left : I
    //   241: istore_2
    //   242: aload_0
    //   243: aload_0
    //   244: getfield mTempRect : Landroid/graphics/Rect;
    //   247: aload #5
    //   249: invokespecial getChildRectInPagerCoordinates : (Landroid/graphics/Rect;Landroid/view/View;)Landroid/graphics/Rect;
    //   252: getfield left : I
    //   255: istore_3
    //   256: aload #5
    //   258: ifnull -> 275
    //   261: iload_2
    //   262: iload_3
    //   263: if_icmplt -> 275
    //   266: aload_0
    //   267: invokevirtual pageLeft : ()Z
    //   270: istore #4
    //   272: goto -> 282
    //   275: aload #6
    //   277: invokevirtual requestFocus : ()Z
    //   280: istore #4
    //   282: goto -> 388
    //   285: iload_1
    //   286: bipush #66
    //   288: if_icmpne -> 388
    //   291: aload_0
    //   292: aload_0
    //   293: getfield mTempRect : Landroid/graphics/Rect;
    //   296: aload #6
    //   298: invokespecial getChildRectInPagerCoordinates : (Landroid/graphics/Rect;Landroid/view/View;)Landroid/graphics/Rect;
    //   301: getfield left : I
    //   304: istore_2
    //   305: aload_0
    //   306: aload_0
    //   307: getfield mTempRect : Landroid/graphics/Rect;
    //   310: aload #5
    //   312: invokespecial getChildRectInPagerCoordinates : (Landroid/graphics/Rect;Landroid/view/View;)Landroid/graphics/Rect;
    //   315: getfield left : I
    //   318: istore_3
    //   319: aload #5
    //   321: ifnull -> 338
    //   324: iload_2
    //   325: iload_3
    //   326: if_icmpgt -> 338
    //   329: aload_0
    //   330: invokevirtual pageRight : ()Z
    //   333: istore #4
    //   335: goto -> 282
    //   338: aload #6
    //   340: invokevirtual requestFocus : ()Z
    //   343: istore #4
    //   345: goto -> 282
    //   348: iload_1
    //   349: bipush #17
    //   351: if_icmpeq -> 382
    //   354: iload_1
    //   355: iconst_1
    //   356: if_icmpne -> 362
    //   359: goto -> 382
    //   362: iload_1
    //   363: bipush #66
    //   365: if_icmpeq -> 373
    //   368: iload_1
    //   369: iconst_2
    //   370: if_icmpne -> 388
    //   373: aload_0
    //   374: invokevirtual pageRight : ()Z
    //   377: istore #4
    //   379: goto -> 388
    //   382: aload_0
    //   383: invokevirtual pageLeft : ()Z
    //   386: istore #4
    //   388: iload #4
    //   390: ifeq -> 401
    //   393: aload_0
    //   394: iload_1
    //   395: invokestatic getContantForFocusDirection : (I)I
    //   398: invokevirtual playSoundEffect : (I)V
    //   401: iload #4
    //   403: ireturn }
  
  public boolean beginFakeDrag() {
    if (this.mIsBeingDragged)
      return false; 
    this.mFakeDragging = true;
    setScrollState(1);
    this.mLastMotionX = 0.0F;
    this.mInitialMotionX = 0.0F;
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    } else {
      this.mVelocityTracker.clear();
    } 
    long l = SystemClock.uptimeMillis();
    MotionEvent motionEvent = MotionEvent.obtain(l, l, 0, 0.0F, 0.0F, 0);
    this.mVelocityTracker.addMovement(motionEvent);
    motionEvent.recycle();
    this.mFakeDragBeginTime = l;
    return true;
  }
  
  protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3) {
    if (paramView instanceof ViewGroup) {
      ViewGroup viewGroup = (ViewGroup)paramView;
      int j = paramView.getScrollX();
      int k = paramView.getScrollY();
      int i;
      for (i = viewGroup.getChildCount() - 1; i >= 0; i--) {
        View view = viewGroup.getChildAt(i);
        int m = paramInt2 + j;
        if (m >= view.getLeft() && m < view.getRight()) {
          int n = paramInt3 + k;
          if (n >= view.getTop() && n < view.getBottom() && canScroll(view, true, paramInt1, m - view.getLeft(), n - view.getTop()))
            return true; 
        } 
      } 
    } 
    return (paramBoolean && paramView.canScrollHorizontally(-paramInt1));
  }
  
  public boolean canScrollHorizontally(int paramInt) {
    PagerAdapter pagerAdapter = this.mAdapter;
    boolean bool1 = false;
    boolean bool = false;
    if (pagerAdapter == null)
      return false; 
    int i = getClientWidth();
    int j = getScrollX();
    if (paramInt < 0) {
      if (j > (int)(i * this.mFirstOffset))
        bool = true; 
      return bool;
    } 
    if (paramInt > 0) {
      bool = bool1;
      if (j < (int)(i * this.mLastOffset))
        bool = true; 
      return bool;
    } 
    return false;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams instanceof LayoutParams && super.checkLayoutParams(paramLayoutParams)); }
  
  public void clearOnPageChangeListeners() {
    if (this.mOnPageChangeListeners != null)
      this.mOnPageChangeListeners.clear(); 
  }
  
  public void computeScroll() {
    this.mIsScrollStarted = true;
    if (!this.mScroller.isFinished() && this.mScroller.computeScrollOffset()) {
      int i = getScrollX();
      int j = getScrollY();
      int k = this.mScroller.getCurrX();
      int m = this.mScroller.getCurrY();
      if (i != k || j != m) {
        scrollTo(k, m);
        if (!pageScrolled(k)) {
          this.mScroller.abortAnimation();
          scrollTo(0, m);
        } 
      } 
      ViewCompat.postInvalidateOnAnimation(this);
      return;
    } 
    completeScroll(true);
  }
  
  void dataSetChanged() { // Byte code:
    //   0: aload_0
    //   1: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   4: invokevirtual getCount : ()I
    //   7: istore #9
    //   9: aload_0
    //   10: iload #9
    //   12: putfield mExpectedAdapterCount : I
    //   15: aload_0
    //   16: getfield mItems : Ljava/util/ArrayList;
    //   19: invokevirtual size : ()I
    //   22: aload_0
    //   23: getfield mOffscreenPageLimit : I
    //   26: iconst_2
    //   27: imul
    //   28: iconst_1
    //   29: iadd
    //   30: if_icmpge -> 50
    //   33: aload_0
    //   34: getfield mItems : Ljava/util/ArrayList;
    //   37: invokevirtual size : ()I
    //   40: iload #9
    //   42: if_icmpge -> 50
    //   45: iconst_1
    //   46: istore_1
    //   47: goto -> 52
    //   50: iconst_0
    //   51: istore_1
    //   52: aload_0
    //   53: getfield mCurItem : I
    //   56: istore_2
    //   57: iload_1
    //   58: istore #6
    //   60: iload_2
    //   61: istore_1
    //   62: iconst_0
    //   63: istore_3
    //   64: iconst_0
    //   65: istore_2
    //   66: iload_3
    //   67: aload_0
    //   68: getfield mItems : Ljava/util/ArrayList;
    //   71: invokevirtual size : ()I
    //   74: if_icmpge -> 292
    //   77: aload_0
    //   78: getfield mItems : Ljava/util/ArrayList;
    //   81: iload_3
    //   82: invokevirtual get : (I)Ljava/lang/Object;
    //   85: checkcast android/support/v4/view/ViewPager$ItemInfo
    //   88: astore #10
    //   90: aload_0
    //   91: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   94: aload #10
    //   96: getfield object : Ljava/lang/Object;
    //   99: invokevirtual getItemPosition : (Ljava/lang/Object;)I
    //   102: istore #8
    //   104: iload #8
    //   106: iconst_m1
    //   107: if_icmpne -> 122
    //   110: iload_3
    //   111: istore #4
    //   113: iload_2
    //   114: istore #5
    //   116: iload_1
    //   117: istore #7
    //   119: goto -> 278
    //   122: iload #8
    //   124: bipush #-2
    //   126: if_icmpne -> 234
    //   129: aload_0
    //   130: getfield mItems : Ljava/util/ArrayList;
    //   133: iload_3
    //   134: invokevirtual remove : (I)Ljava/lang/Object;
    //   137: pop
    //   138: iload_3
    //   139: iconst_1
    //   140: isub
    //   141: istore #5
    //   143: iload_2
    //   144: istore #4
    //   146: iload_2
    //   147: ifne -> 161
    //   150: aload_0
    //   151: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   154: aload_0
    //   155: invokevirtual startUpdate : (Landroid/view/ViewGroup;)V
    //   158: iconst_1
    //   159: istore #4
    //   161: aload_0
    //   162: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   165: aload_0
    //   166: aload #10
    //   168: getfield position : I
    //   171: aload #10
    //   173: getfield object : Ljava/lang/Object;
    //   176: invokevirtual destroyItem : (Landroid/view/ViewGroup;ILjava/lang/Object;)V
    //   179: iload #5
    //   181: istore_3
    //   182: iload #4
    //   184: istore_2
    //   185: aload_0
    //   186: getfield mCurItem : I
    //   189: aload #10
    //   191: getfield position : I
    //   194: if_icmpne -> 219
    //   197: iconst_0
    //   198: aload_0
    //   199: getfield mCurItem : I
    //   202: iload #9
    //   204: iconst_1
    //   205: isub
    //   206: invokestatic min : (II)I
    //   209: invokestatic max : (II)I
    //   212: istore_1
    //   213: iload #4
    //   215: istore_2
    //   216: iload #5
    //   218: istore_3
    //   219: iconst_1
    //   220: istore #6
    //   222: iload_3
    //   223: istore #4
    //   225: iload_2
    //   226: istore #5
    //   228: iload_1
    //   229: istore #7
    //   231: goto -> 278
    //   234: iload_3
    //   235: istore #4
    //   237: iload_2
    //   238: istore #5
    //   240: iload_1
    //   241: istore #7
    //   243: aload #10
    //   245: getfield position : I
    //   248: iload #8
    //   250: if_icmpeq -> 278
    //   253: aload #10
    //   255: getfield position : I
    //   258: aload_0
    //   259: getfield mCurItem : I
    //   262: if_icmpne -> 268
    //   265: iload #8
    //   267: istore_1
    //   268: aload #10
    //   270: iload #8
    //   272: putfield position : I
    //   275: goto -> 219
    //   278: iload #4
    //   280: iconst_1
    //   281: iadd
    //   282: istore_3
    //   283: iload #5
    //   285: istore_2
    //   286: iload #7
    //   288: istore_1
    //   289: goto -> 66
    //   292: iload_2
    //   293: ifeq -> 304
    //   296: aload_0
    //   297: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   300: aload_0
    //   301: invokevirtual finishUpdate : (Landroid/view/ViewGroup;)V
    //   304: aload_0
    //   305: getfield mItems : Ljava/util/ArrayList;
    //   308: getstatic android/support/v4/view/ViewPager.COMPARATOR : Ljava/util/Comparator;
    //   311: invokestatic sort : (Ljava/util/List;Ljava/util/Comparator;)V
    //   314: iload #6
    //   316: ifeq -> 376
    //   319: aload_0
    //   320: invokevirtual getChildCount : ()I
    //   323: istore_3
    //   324: iconst_0
    //   325: istore_2
    //   326: iload_2
    //   327: iload_3
    //   328: if_icmpge -> 365
    //   331: aload_0
    //   332: iload_2
    //   333: invokevirtual getChildAt : (I)Landroid/view/View;
    //   336: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   339: checkcast android/support/v4/view/ViewPager$LayoutParams
    //   342: astore #10
    //   344: aload #10
    //   346: getfield isDecor : Z
    //   349: ifne -> 358
    //   352: aload #10
    //   354: fconst_0
    //   355: putfield widthFactor : F
    //   358: iload_2
    //   359: iconst_1
    //   360: iadd
    //   361: istore_2
    //   362: goto -> 326
    //   365: aload_0
    //   366: iload_1
    //   367: iconst_0
    //   368: iconst_1
    //   369: invokevirtual setCurrentItemInternal : (IZZ)V
    //   372: aload_0
    //   373: invokevirtual requestLayout : ()V
    //   376: return }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) { return (super.dispatchKeyEvent(paramKeyEvent) || executeKeyEvent(paramKeyEvent)); }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent) {
    if (paramAccessibilityEvent.getEventType() == 4096)
      return super.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent); 
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = getChildAt(b);
      if (view.getVisibility() == 0) {
        ItemInfo itemInfo = infoForChild(view);
        if (itemInfo != null && itemInfo.position == this.mCurItem && view.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent))
          return true; 
      } 
    } 
    return false;
  }
  
  float distanceInfluenceForSnapDuration(float paramFloat) { return (float)Math.sin(((paramFloat - 0.5F) * 0.47123894F)); }
  
  public void draw(Canvas paramCanvas) {
    super.draw(paramCanvas);
    int j = getOverScrollMode();
    boolean bool = false;
    int i = 0;
    if (j == 0 || (j == 1 && this.mAdapter != null && this.mAdapter.getCount() > 1)) {
      boolean bool1;
      if (!this.mLeftEdge.isFinished()) {
        bool = paramCanvas.save();
        i = getHeight() - getPaddingTop() - getPaddingBottom();
        j = getWidth();
        paramCanvas.rotate(270.0F);
        paramCanvas.translate((-i + getPaddingTop()), this.mFirstOffset * j);
        this.mLeftEdge.setSize(i, j);
        bool1 = false | this.mLeftEdge.draw(paramCanvas);
        paramCanvas.restoreToCount(bool);
      } 
      boolean bool2 = bool1;
      if (!this.mRightEdge.isFinished()) {
        j = paramCanvas.save();
        int k = getWidth();
        int m = getHeight();
        int n = getPaddingTop();
        int i1 = getPaddingBottom();
        paramCanvas.rotate(90.0F);
        paramCanvas.translate(-getPaddingTop(), -(this.mLastOffset + 1.0F) * k);
        this.mRightEdge.setSize(m - n - i1, k);
        bool = bool1 | this.mRightEdge.draw(paramCanvas);
        paramCanvas.restoreToCount(j);
      } 
    } else {
      this.mLeftEdge.finish();
      this.mRightEdge.finish();
    } 
    if (bool)
      ViewCompat.postInvalidateOnAnimation(this); 
  }
  
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    Drawable drawable = this.mMarginDrawable;
    if (drawable != null && drawable.isStateful())
      drawable.setState(getDrawableState()); 
  }
  
  public void endFakeDrag() {
    if (!this.mFakeDragging)
      throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first."); 
    if (this.mAdapter != null) {
      VelocityTracker velocityTracker = this.mVelocityTracker;
      velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
      int i = (int)velocityTracker.getXVelocity(this.mActivePointerId);
      this.mPopulatePending = true;
      int j = getClientWidth();
      int k = getScrollX();
      ItemInfo itemInfo = infoForCurrentScrollPosition();
      setCurrentItemInternal(determineTargetPage(itemInfo.position, (k / j - itemInfo.offset) / itemInfo.widthFactor, i, (int)(this.mLastMotionX - this.mInitialMotionX)), true, true, i);
    } 
    endDrag();
    this.mFakeDragging = false;
  }
  
  public boolean executeKeyEvent(@NonNull KeyEvent paramKeyEvent) {
    if (paramKeyEvent.getAction() == 0) {
      int i = paramKeyEvent.getKeyCode();
      if (i != 61) {
        switch (i) {
          default:
            return false;
          case 22:
            return paramKeyEvent.hasModifiers(2) ? pageRight() : arrowScroll(66);
          case 21:
            break;
        } 
        return paramKeyEvent.hasModifiers(2) ? pageLeft() : arrowScroll(17);
      } 
      if (paramKeyEvent.hasNoModifiers())
        return arrowScroll(2); 
      if (paramKeyEvent.hasModifiers(1))
        return arrowScroll(1); 
    } 
  }
  
  public void fakeDragBy(float paramFloat) {
    if (!this.mFakeDragging)
      throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first."); 
    if (this.mAdapter == null)
      return; 
    this.mLastMotionX += paramFloat;
    float f2 = getScrollX() - paramFloat;
    float f3 = getClientWidth();
    paramFloat = this.mFirstOffset * f3;
    float f1 = this.mLastOffset * f3;
    ItemInfo itemInfo1 = (ItemInfo)this.mItems.get(0);
    ItemInfo itemInfo2 = (ItemInfo)this.mItems.get(this.mItems.size() - 1);
    if (itemInfo1.position != 0)
      paramFloat = itemInfo1.offset * f3; 
    if (itemInfo2.position != this.mAdapter.getCount() - 1)
      f1 = itemInfo2.offset * f3; 
    if (f2 >= paramFloat) {
      paramFloat = f2;
      if (f2 > f1)
        paramFloat = f1; 
    } 
    f1 = this.mLastMotionX;
    int i = (int)paramFloat;
    this.mLastMotionX = f1 + paramFloat - i;
    scrollTo(i, getScrollY());
    pageScrolled(i);
    long l = SystemClock.uptimeMillis();
    MotionEvent motionEvent = MotionEvent.obtain(this.mFakeDragBeginTime, l, 2, this.mLastMotionX, 0.0F, 0);
    this.mVelocityTracker.addMovement(motionEvent);
    motionEvent.recycle();
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams() { return new LayoutParams(); }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return new LayoutParams(getContext(), paramAttributeSet); }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return generateDefaultLayoutParams(); }
  
  @Nullable
  public PagerAdapter getAdapter() { return this.mAdapter; }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2) {
    int i = paramInt2;
    if (this.mDrawingOrder == 2)
      i = paramInt1 - 1 - paramInt2; 
    return ((LayoutParams)((View)this.mDrawingOrderedChildren.get(i)).getLayoutParams()).childIndex;
  }
  
  public int getCurrentItem() { return this.mCurItem; }
  
  public int getOffscreenPageLimit() { return this.mOffscreenPageLimit; }
  
  public int getPageMargin() { return this.mPageMargin; }
  
  ItemInfo infoForAnyChild(View paramView) {
    while (true) {
      ViewParent viewParent = paramView.getParent();
      if (viewParent != this) {
        if (viewParent == null || !(viewParent instanceof View))
          return null; 
        paramView = (View)viewParent;
        continue;
      } 
      break;
    } 
    return infoForChild(paramView);
  }
  
  ItemInfo infoForChild(View paramView) {
    for (byte b = 0; b < this.mItems.size(); b++) {
      ItemInfo itemInfo = (ItemInfo)this.mItems.get(b);
      if (this.mAdapter.isViewFromObject(paramView, itemInfo.object))
        return itemInfo; 
    } 
    return null;
  }
  
  ItemInfo infoForPosition(int paramInt) {
    for (byte b = 0; b < this.mItems.size(); b++) {
      ItemInfo itemInfo = (ItemInfo)this.mItems.get(b);
      if (itemInfo.position == paramInt)
        return itemInfo; 
    } 
    return null;
  }
  
  void initViewPager() {
    setWillNotDraw(false);
    setDescendantFocusability(262144);
    setFocusable(true);
    Context context = getContext();
    this.mScroller = new Scroller(context, sInterpolator);
    ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
    float f = (context.getResources().getDisplayMetrics()).density;
    this.mTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
    this.mMinimumVelocity = (int)(400.0F * f);
    this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    this.mLeftEdge = new EdgeEffect(context);
    this.mRightEdge = new EdgeEffect(context);
    this.mFlingDistance = (int)(25.0F * f);
    this.mCloseEnough = (int)(2.0F * f);
    this.mDefaultGutterSize = (int)(f * 16.0F);
    ViewCompat.setAccessibilityDelegate(this, new MyAccessibilityDelegate());
    if (ViewCompat.getImportantForAccessibility(this) == 0)
      ViewCompat.setImportantForAccessibility(this, 1); 
    ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener() {
          private final Rect mTempRect = new Rect();
          
          public WindowInsetsCompat onApplyWindowInsets(View param1View, WindowInsetsCompat param1WindowInsetsCompat) {
            WindowInsetsCompat windowInsetsCompat = ViewCompat.onApplyWindowInsets(param1View, param1WindowInsetsCompat);
            if (windowInsetsCompat.isConsumed())
              return windowInsetsCompat; 
            Rect rect = this.mTempRect;
            rect.left = windowInsetsCompat.getSystemWindowInsetLeft();
            rect.top = windowInsetsCompat.getSystemWindowInsetTop();
            rect.right = windowInsetsCompat.getSystemWindowInsetRight();
            rect.bottom = windowInsetsCompat.getSystemWindowInsetBottom();
            byte b = 0;
            int i = ViewPager.this.getChildCount();
            while (b < i) {
              WindowInsetsCompat windowInsetsCompat1 = ViewCompat.dispatchApplyWindowInsets(ViewPager.this.getChildAt(b), windowInsetsCompat);
              rect.left = Math.min(windowInsetsCompat1.getSystemWindowInsetLeft(), rect.left);
              rect.top = Math.min(windowInsetsCompat1.getSystemWindowInsetTop(), rect.top);
              rect.right = Math.min(windowInsetsCompat1.getSystemWindowInsetRight(), rect.right);
              rect.bottom = Math.min(windowInsetsCompat1.getSystemWindowInsetBottom(), rect.bottom);
              b++;
            } 
            return windowInsetsCompat.replaceSystemWindowInsets(rect.left, rect.top, rect.right, rect.bottom);
          }
        });
  }
  
  public boolean isFakeDragging() { return this.mFakeDragging; }
  
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    this.mFirstLayout = true;
  }
  
  protected void onDetachedFromWindow() {
    removeCallbacks(this.mEndScrollRunnable);
    if (this.mScroller != null && !this.mScroller.isFinished())
      this.mScroller.abortAnimation(); 
    super.onDetachedFromWindow();
  }
  
  protected void onDraw(Canvas paramCanvas) {
    super.onDraw(paramCanvas);
    if (this.mPageMargin > 0 && this.mMarginDrawable != null && this.mItems.size() > 0 && this.mAdapter != null) {
      int j = getScrollX();
      int k = getWidth();
      float f1 = this.mPageMargin;
      float f3 = k;
      float f2 = f1 / f3;
      ArrayList arrayList = this.mItems;
      byte b = 0;
      ItemInfo itemInfo = (ItemInfo)arrayList.get(0);
      f1 = itemInfo.offset;
      int m = this.mItems.size();
      int i = itemInfo.position;
      int n = ((ItemInfo)this.mItems.get(m - 1)).position;
      while (i < n) {
        ItemInfo itemInfo1;
        float f;
        while (i > itemInfo.position && b < m) {
          ArrayList arrayList1 = this.mItems;
          itemInfo1 = (ItemInfo)arrayList1.get(++b);
        } 
        if (i == itemInfo1.position) {
          f = itemInfo1.offset;
          float f5 = itemInfo1.widthFactor;
          f1 = itemInfo1.offset;
          float f4 = itemInfo1.widthFactor;
          f = (f + f5) * f3;
          f1 = f1 + f4 + f2;
        } else {
          float f4 = this.mAdapter.getPageWidth(i);
          f = (f1 + f4) * f3;
          f1 += f4 + f2;
        } 
        if (this.mPageMargin + f > j) {
          this.mMarginDrawable.setBounds(Math.round(f), this.mTopPageBounds, Math.round(this.mPageMargin + f), this.mBottomPageBounds);
          this.mMarginDrawable.draw(paramCanvas);
        } 
        if (f > (j + k))
          return; 
        i++;
      } 
    } 
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
    int i = paramMotionEvent.getAction() & 0xFF;
    if (i == 3 || i == 1) {
      resetTouch();
      return false;
    } 
    if (i != 0) {
      if (this.mIsBeingDragged)
        return true; 
      if (this.mIsUnableToDrag)
        return false; 
    } 
    if (i != 0) {
      if (i != 2) {
        if (i == 6)
          onSecondaryPointerUp(paramMotionEvent); 
      } else {
        i = this.mActivePointerId;
        if (i != -1) {
          i = paramMotionEvent.findPointerIndex(i);
          float f2 = paramMotionEvent.getX(i);
          float f1 = f2 - this.mLastMotionX;
          float f4 = Math.abs(f1);
          float f3 = paramMotionEvent.getY(i);
          float f5 = Math.abs(f3 - this.mInitialMotionY);
          if (f1 != 0.0F && !isGutterDrag(this.mLastMotionX, f1) && canScroll(this, false, (int)f1, (int)f2, (int)f3)) {
            this.mLastMotionX = f2;
            this.mLastMotionY = f3;
            this.mIsUnableToDrag = true;
            return false;
          } 
          if (f4 > this.mTouchSlop && f4 * 0.5F > f5) {
            this.mIsBeingDragged = true;
            requestParentDisallowInterceptTouchEvent(true);
            setScrollState(1);
            if (f1 > 0.0F) {
              f1 = this.mInitialMotionX + this.mTouchSlop;
            } else {
              f1 = this.mInitialMotionX - this.mTouchSlop;
            } 
            this.mLastMotionX = f1;
            this.mLastMotionY = f3;
            setScrollingCacheEnabled(true);
          } else if (f5 > this.mTouchSlop) {
            this.mIsUnableToDrag = true;
          } 
          if (this.mIsBeingDragged && performDrag(f2))
            ViewCompat.postInvalidateOnAnimation(this); 
        } 
      } 
    } else {
      float f = paramMotionEvent.getX();
      this.mInitialMotionX = f;
      this.mLastMotionX = f;
      f = paramMotionEvent.getY();
      this.mInitialMotionY = f;
      this.mLastMotionY = f;
      this.mActivePointerId = paramMotionEvent.getPointerId(0);
      this.mIsUnableToDrag = false;
      this.mIsScrollStarted = true;
      this.mScroller.computeScrollOffset();
      if (this.mScrollState == 2 && Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) > this.mCloseEnough) {
        this.mScroller.abortAnimation();
        this.mPopulatePending = false;
        populate();
        this.mIsBeingDragged = true;
        requestParentDisallowInterceptTouchEvent(true);
        setScrollState(1);
      } else {
        completeScroll(false);
        this.mIsBeingDragged = false;
      } 
    } 
    if (this.mVelocityTracker == null)
      this.mVelocityTracker = VelocityTracker.obtain(); 
    this.mVelocityTracker.addMovement(paramMotionEvent);
    return this.mIsBeingDragged;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int k = getChildCount();
    int m = paramInt3 - paramInt1;
    int n = paramInt4 - paramInt2;
    paramInt2 = getPaddingLeft();
    paramInt1 = getPaddingTop();
    int i = getPaddingRight();
    paramInt4 = getPaddingBottom();
    int i1 = getScrollX();
    int j = 0;
    byte b = 0;
    while (b < k) {
      View view = getChildAt(b);
      int i2 = paramInt2;
      int i5 = i;
      int i4 = paramInt1;
      int i3 = paramInt4;
      paramInt3 = j;
      if (view.getVisibility() != 8) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        i2 = paramInt2;
        i5 = i;
        i4 = paramInt1;
        i3 = paramInt4;
        paramInt3 = j;
        if (layoutParams.isDecor) {
          paramInt3 = layoutParams.gravity & 0x7;
          i3 = layoutParams.gravity & 0x70;
          if (paramInt3 != 1) {
            if (paramInt3 != 3) {
              if (paramInt3 != 5) {
                paramInt3 = paramInt2;
                i2 = paramInt2;
              } else {
                paramInt3 = m - i - view.getMeasuredWidth();
                i += view.getMeasuredWidth();
                i2 = paramInt2;
              } 
            } else {
              i2 = view.getMeasuredWidth();
              paramInt3 = paramInt2;
              i2 += paramInt2;
            } 
          } else {
            paramInt3 = Math.max((m - view.getMeasuredWidth()) / 2, paramInt2);
            i2 = paramInt2;
          } 
          if (i3 != 16) {
            if (i3 != 48) {
              if (i3 != 80) {
                paramInt2 = paramInt1;
              } else {
                paramInt2 = n - paramInt4 - view.getMeasuredHeight();
                paramInt4 += view.getMeasuredHeight();
              } 
            } else {
              i3 = view.getMeasuredHeight();
              paramInt2 = paramInt1;
              paramInt1 = i3 + paramInt1;
            } 
          } else {
            paramInt2 = Math.max((n - view.getMeasuredHeight()) / 2, paramInt1);
          } 
          paramInt3 += i1;
          view.layout(paramInt3, paramInt2, view.getMeasuredWidth() + paramInt3, paramInt2 + view.getMeasuredHeight());
          paramInt3 = j + 1;
          i3 = paramInt4;
          i4 = paramInt1;
          i5 = i;
        } 
      } 
      b++;
      paramInt2 = i2;
      i = i5;
      paramInt1 = i4;
      paramInt4 = i3;
      j = paramInt3;
    } 
    for (paramInt3 = 0; paramInt3 < k; paramInt3++) {
      View view = getChildAt(paramInt3);
      if (view.getVisibility() != 8) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (!layoutParams.isDecor) {
          ItemInfo itemInfo = infoForChild(view);
          if (itemInfo != null) {
            float f = (m - paramInt2 - i);
            int i2 = (int)(itemInfo.offset * f) + paramInt2;
            if (layoutParams.needsMeasure) {
              layoutParams.needsMeasure = false;
              view.measure(View.MeasureSpec.makeMeasureSpec((int)(f * layoutParams.widthFactor), 1073741824), View.MeasureSpec.makeMeasureSpec(n - paramInt1 - paramInt4, 1073741824));
            } 
            view.layout(i2, paramInt1, view.getMeasuredWidth() + i2, view.getMeasuredHeight() + paramInt1);
          } 
        } 
      } 
    } 
    this.mTopPageBounds = paramInt1;
    this.mBottomPageBounds = n - paramInt4;
    this.mDecorChildCount = j;
    if (this.mFirstLayout)
      scrollToItem(this.mCurItem, false, 0, false); 
    this.mFirstLayout = false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: iconst_0
    //   2: iload_1
    //   3: invokestatic getDefaultSize : (II)I
    //   6: iconst_0
    //   7: iload_2
    //   8: invokestatic getDefaultSize : (II)I
    //   11: invokevirtual setMeasuredDimension : (II)V
    //   14: aload_0
    //   15: invokevirtual getMeasuredWidth : ()I
    //   18: istore_1
    //   19: aload_0
    //   20: iload_1
    //   21: bipush #10
    //   23: idiv
    //   24: aload_0
    //   25: getfield mDefaultGutterSize : I
    //   28: invokestatic min : (II)I
    //   31: putfield mGutterSize : I
    //   34: aload_0
    //   35: invokevirtual getPaddingLeft : ()I
    //   38: istore_3
    //   39: aload_0
    //   40: invokevirtual getPaddingRight : ()I
    //   43: istore #4
    //   45: aload_0
    //   46: invokevirtual getMeasuredHeight : ()I
    //   49: istore_2
    //   50: aload_0
    //   51: invokevirtual getPaddingTop : ()I
    //   54: istore #5
    //   56: aload_0
    //   57: invokevirtual getPaddingBottom : ()I
    //   60: istore #6
    //   62: aload_0
    //   63: invokevirtual getChildCount : ()I
    //   66: istore #11
    //   68: iload_2
    //   69: iload #5
    //   71: isub
    //   72: iload #6
    //   74: isub
    //   75: istore_2
    //   76: iload_1
    //   77: iload_3
    //   78: isub
    //   79: iload #4
    //   81: isub
    //   82: istore_1
    //   83: iconst_0
    //   84: istore #5
    //   86: iconst_1
    //   87: istore #8
    //   89: ldc_w 1073741824
    //   92: istore #10
    //   94: iload #5
    //   96: iload #11
    //   98: if_icmpge -> 430
    //   101: aload_0
    //   102: iload #5
    //   104: invokevirtual getChildAt : (I)Landroid/view/View;
    //   107: astore #12
    //   109: iload_1
    //   110: istore_3
    //   111: iload_2
    //   112: istore #4
    //   114: aload #12
    //   116: invokevirtual getVisibility : ()I
    //   119: bipush #8
    //   121: if_icmpeq -> 416
    //   124: aload #12
    //   126: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   129: checkcast android/support/v4/view/ViewPager$LayoutParams
    //   132: astore #13
    //   134: iload_1
    //   135: istore_3
    //   136: iload_2
    //   137: istore #4
    //   139: aload #13
    //   141: ifnull -> 416
    //   144: iload_1
    //   145: istore_3
    //   146: iload_2
    //   147: istore #4
    //   149: aload #13
    //   151: getfield isDecor : Z
    //   154: ifeq -> 416
    //   157: aload #13
    //   159: getfield gravity : I
    //   162: bipush #7
    //   164: iand
    //   165: istore_3
    //   166: aload #13
    //   168: getfield gravity : I
    //   171: bipush #112
    //   173: iand
    //   174: istore #4
    //   176: iload #4
    //   178: bipush #48
    //   180: if_icmpeq -> 199
    //   183: iload #4
    //   185: bipush #80
    //   187: if_icmpne -> 193
    //   190: goto -> 199
    //   193: iconst_0
    //   194: istore #7
    //   196: goto -> 202
    //   199: iconst_1
    //   200: istore #7
    //   202: iload #8
    //   204: istore #6
    //   206: iload_3
    //   207: iconst_3
    //   208: if_icmpeq -> 226
    //   211: iload_3
    //   212: iconst_5
    //   213: if_icmpne -> 223
    //   216: iload #8
    //   218: istore #6
    //   220: goto -> 226
    //   223: iconst_0
    //   224: istore #6
    //   226: ldc_w -2147483648
    //   229: istore #8
    //   231: iload #7
    //   233: ifeq -> 248
    //   236: ldc_w 1073741824
    //   239: istore #4
    //   241: ldc_w -2147483648
    //   244: istore_3
    //   245: goto -> 265
    //   248: iload #8
    //   250: istore #4
    //   252: iload #6
    //   254: ifeq -> 241
    //   257: ldc_w 1073741824
    //   260: istore_3
    //   261: iload #8
    //   263: istore #4
    //   265: aload #13
    //   267: getfield width : I
    //   270: bipush #-2
    //   272: if_icmpeq -> 309
    //   275: aload #13
    //   277: getfield width : I
    //   280: iconst_m1
    //   281: if_icmpeq -> 294
    //   284: aload #13
    //   286: getfield width : I
    //   289: istore #4
    //   291: goto -> 297
    //   294: iload_1
    //   295: istore #4
    //   297: ldc_w 1073741824
    //   300: istore #8
    //   302: iload #4
    //   304: istore #9
    //   306: goto -> 316
    //   309: iload_1
    //   310: istore #9
    //   312: iload #4
    //   314: istore #8
    //   316: aload #13
    //   318: getfield height : I
    //   321: bipush #-2
    //   323: if_icmpeq -> 349
    //   326: aload #13
    //   328: getfield height : I
    //   331: iconst_m1
    //   332: if_icmpeq -> 344
    //   335: aload #13
    //   337: getfield height : I
    //   340: istore_3
    //   341: goto -> 358
    //   344: iload_2
    //   345: istore_3
    //   346: goto -> 358
    //   349: iload_2
    //   350: istore #4
    //   352: iload_3
    //   353: istore #10
    //   355: iload #4
    //   357: istore_3
    //   358: aload #12
    //   360: iload #9
    //   362: iload #8
    //   364: invokestatic makeMeasureSpec : (II)I
    //   367: iload_3
    //   368: iload #10
    //   370: invokestatic makeMeasureSpec : (II)I
    //   373: invokevirtual measure : (II)V
    //   376: iload #7
    //   378: ifeq -> 395
    //   381: iload_2
    //   382: aload #12
    //   384: invokevirtual getMeasuredHeight : ()I
    //   387: isub
    //   388: istore #4
    //   390: iload_1
    //   391: istore_3
    //   392: goto -> 416
    //   395: iload_1
    //   396: istore_3
    //   397: iload_2
    //   398: istore #4
    //   400: iload #6
    //   402: ifeq -> 416
    //   405: iload_1
    //   406: aload #12
    //   408: invokevirtual getMeasuredWidth : ()I
    //   411: isub
    //   412: istore_3
    //   413: iload_2
    //   414: istore #4
    //   416: iload #5
    //   418: iconst_1
    //   419: iadd
    //   420: istore #5
    //   422: iload_3
    //   423: istore_1
    //   424: iload #4
    //   426: istore_2
    //   427: goto -> 86
    //   430: aload_0
    //   431: iload_1
    //   432: ldc_w 1073741824
    //   435: invokestatic makeMeasureSpec : (II)I
    //   438: putfield mChildWidthMeasureSpec : I
    //   441: aload_0
    //   442: iload_2
    //   443: ldc_w 1073741824
    //   446: invokestatic makeMeasureSpec : (II)I
    //   449: putfield mChildHeightMeasureSpec : I
    //   452: aload_0
    //   453: iconst_1
    //   454: putfield mInLayout : Z
    //   457: aload_0
    //   458: invokevirtual populate : ()V
    //   461: iconst_0
    //   462: istore_2
    //   463: aload_0
    //   464: iconst_0
    //   465: putfield mInLayout : Z
    //   468: aload_0
    //   469: invokevirtual getChildCount : ()I
    //   472: istore_3
    //   473: iload_2
    //   474: iload_3
    //   475: if_icmpge -> 549
    //   478: aload_0
    //   479: iload_2
    //   480: invokevirtual getChildAt : (I)Landroid/view/View;
    //   483: astore #12
    //   485: aload #12
    //   487: invokevirtual getVisibility : ()I
    //   490: bipush #8
    //   492: if_icmpeq -> 542
    //   495: aload #12
    //   497: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   500: checkcast android/support/v4/view/ViewPager$LayoutParams
    //   503: astore #13
    //   505: aload #13
    //   507: ifnull -> 518
    //   510: aload #13
    //   512: getfield isDecor : Z
    //   515: ifne -> 542
    //   518: aload #12
    //   520: iload_1
    //   521: i2f
    //   522: aload #13
    //   524: getfield widthFactor : F
    //   527: fmul
    //   528: f2i
    //   529: ldc_w 1073741824
    //   532: invokestatic makeMeasureSpec : (II)I
    //   535: aload_0
    //   536: getfield mChildHeightMeasureSpec : I
    //   539: invokevirtual measure : (II)V
    //   542: iload_2
    //   543: iconst_1
    //   544: iadd
    //   545: istore_2
    //   546: goto -> 473
    //   549: return }
  
  @CallSuper
  protected void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {
    int i = this.mDecorChildCount;
    int j = 0;
    if (i > 0) {
      int m = getScrollX();
      i = getPaddingLeft();
      int k = getPaddingRight();
      int n = getWidth();
      int i1 = getChildCount();
      byte b;
      for (b = 0; b < i1; b++) {
        View view = getChildAt(b);
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.isDecor) {
          int i2 = layoutParams.gravity & 0x7;
          if (i2 != 1) {
            if (i2 != 3) {
              if (i2 != 5) {
                int i3 = i;
                i2 = i;
                i = i3;
              } else {
                i2 = n - k - view.getMeasuredWidth();
                k += view.getMeasuredWidth();
              } 
            } else {
              int i3 = view.getWidth() + i;
              i2 = i;
              i = i3;
            } 
          } else {
            i2 = Math.max((n - view.getMeasuredWidth()) / 2, i);
          } 
          i2 = i2 + m - view.getLeft();
          if (i2 != 0)
            view.offsetLeftAndRight(i2); 
        } 
      } 
    } 
    dispatchOnPageScrolled(paramInt1, paramFloat, paramInt2);
    if (this.mPageTransformer != null) {
      paramInt2 = getScrollX();
      i = getChildCount();
      for (paramInt1 = j; paramInt1 < i; paramInt1++) {
        View view = getChildAt(paramInt1);
        if (!((LayoutParams)view.getLayoutParams()).isDecor) {
          paramFloat = (view.getLeft() - paramInt2) / getClientWidth();
          this.mPageTransformer.transformPage(view, paramFloat);
        } 
      } 
    } 
    this.mCalledSuper = true;
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect) {
    int j;
    int i = getChildCount();
    int k = -1;
    if ((paramInt & 0x2) != 0) {
      k = i;
      i = 0;
      j = 1;
    } else {
      i--;
      j = -1;
    } 
    while (i != k) {
      View view = getChildAt(i);
      if (view.getVisibility() == 0) {
        ItemInfo itemInfo = infoForChild(view);
        if (itemInfo != null && itemInfo.position == this.mCurItem && view.requestFocus(paramInt, paramRect))
          return true; 
      } 
      i += j;
    } 
    return false;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable) {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
      return;
    } 
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    if (this.mAdapter != null) {
      this.mAdapter.restoreState(paramParcelable.adapterState, paramParcelable.loader);
      setCurrentItemInternal(paramParcelable.position, false, true);
      return;
    } 
    this.mRestoredCurItem = paramParcelable.position;
    this.mRestoredAdapterState = paramParcelable.adapterState;
    this.mRestoredClassLoader = paramParcelable.loader;
  }
  
  public Parcelable onSaveInstanceState() {
    SavedState savedState = new SavedState(super.onSaveInstanceState());
    savedState.position = this.mCurItem;
    if (this.mAdapter != null)
      savedState.adapterState = this.mAdapter.saveState(); 
    return savedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 != paramInt3)
      recomputeScrollPosition(paramInt1, paramInt3, this.mPageMargin, this.mPageMargin); 
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) {
    if (this.mFakeDragging)
      return true; 
    int i = paramMotionEvent.getAction();
    boolean bool = false;
    if (i == 0 && paramMotionEvent.getEdgeFlags() != 0)
      return false; 
    if (this.mAdapter != null) {
      float f;
      if (this.mAdapter.getCount() == 0)
        return false; 
      if (this.mVelocityTracker == null)
        this.mVelocityTracker = VelocityTracker.obtain(); 
      this.mVelocityTracker.addMovement(paramMotionEvent);
      switch (paramMotionEvent.getAction() & 0xFF) {
        case 6:
          onSecondaryPointerUp(paramMotionEvent);
          this.mLastMotionX = paramMotionEvent.getX(paramMotionEvent.findPointerIndex(this.mActivePointerId));
          break;
        case 5:
          i = paramMotionEvent.getActionIndex();
          this.mLastMotionX = paramMotionEvent.getX(i);
          this.mActivePointerId = paramMotionEvent.getPointerId(i);
          break;
        case 3:
          if (this.mIsBeingDragged) {
            scrollToItem(this.mCurItem, true, 0, false);
            bool = resetTouch();
          } 
          break;
        case 2:
          if (!this.mIsBeingDragged) {
            i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
            if (i == -1) {
              bool = resetTouch();
              break;
            } 
            float f1 = paramMotionEvent.getX(i);
            float f3 = Math.abs(f1 - this.mLastMotionX);
            float f2 = paramMotionEvent.getY(i);
            float f4 = Math.abs(f2 - this.mLastMotionY);
            if (f3 > this.mTouchSlop && f3 > f4) {
              this.mIsBeingDragged = true;
              requestParentDisallowInterceptTouchEvent(true);
              if (f1 - this.mInitialMotionX > 0.0F) {
                f1 = this.mInitialMotionX + this.mTouchSlop;
              } else {
                f1 = this.mInitialMotionX - this.mTouchSlop;
              } 
              this.mLastMotionX = f1;
              this.mLastMotionY = f2;
              setScrollState(1);
              setScrollingCacheEnabled(true);
              ViewParent viewParent = getParent();
              if (viewParent != null)
                viewParent.requestDisallowInterceptTouchEvent(true); 
            } 
          } 
          if (this.mIsBeingDragged)
            bool = false | performDrag(paramMotionEvent.getX(paramMotionEvent.findPointerIndex(this.mActivePointerId))); 
          break;
        case 1:
          if (this.mIsBeingDragged) {
            VelocityTracker velocityTracker = this.mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
            i = (int)velocityTracker.getXVelocity(this.mActivePointerId);
            this.mPopulatePending = true;
            int j = getClientWidth();
            int k = getScrollX();
            ItemInfo itemInfo = infoForCurrentScrollPosition();
            float f2 = this.mPageMargin;
            float f1 = j;
            f2 /= f1;
            setCurrentItemInternal(determineTargetPage(itemInfo.position, (k / f1 - itemInfo.offset) / (itemInfo.widthFactor + f2), i, (int)(paramMotionEvent.getX(paramMotionEvent.findPointerIndex(this.mActivePointerId)) - this.mInitialMotionX)), true, true, i);
            bool = resetTouch();
          } 
          break;
        case 0:
          this.mScroller.abortAnimation();
          this.mPopulatePending = false;
          populate();
          f = paramMotionEvent.getX();
          this.mInitialMotionX = f;
          this.mLastMotionX = f;
          f = paramMotionEvent.getY();
          this.mInitialMotionY = f;
          this.mLastMotionY = f;
          this.mActivePointerId = paramMotionEvent.getPointerId(0);
          break;
      } 
      if (bool)
        ViewCompat.postInvalidateOnAnimation(this); 
      return true;
    } 
    return false;
  }
  
  boolean pageLeft() {
    if (this.mCurItem > 0) {
      setCurrentItem(this.mCurItem - 1, true);
      return true;
    } 
    return false;
  }
  
  boolean pageRight() {
    if (this.mAdapter != null && this.mCurItem < this.mAdapter.getCount() - 1) {
      setCurrentItem(this.mCurItem + 1, true);
      return true;
    } 
    return false;
  }
  
  void populate() { populate(this.mCurItem); }
  
  void populate(int paramInt) { // Byte code:
    //   0: aload_0
    //   1: getfield mCurItem : I
    //   4: iload_1
    //   5: if_icmpeq -> 26
    //   8: aload_0
    //   9: aload_0
    //   10: getfield mCurItem : I
    //   13: invokevirtual infoForPosition : (I)Landroid/support/v4/view/ViewPager$ItemInfo;
    //   16: astore #14
    //   18: aload_0
    //   19: iload_1
    //   20: putfield mCurItem : I
    //   23: goto -> 29
    //   26: aconst_null
    //   27: astore #14
    //   29: aload_0
    //   30: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   33: ifnonnull -> 41
    //   36: aload_0
    //   37: invokespecial sortChildDrawingOrder : ()V
    //   40: return
    //   41: aload_0
    //   42: getfield mPopulatePending : Z
    //   45: ifeq -> 53
    //   48: aload_0
    //   49: invokespecial sortChildDrawingOrder : ()V
    //   52: return
    //   53: aload_0
    //   54: invokevirtual getWindowToken : ()Landroid/os/IBinder;
    //   57: ifnonnull -> 61
    //   60: return
    //   61: aload_0
    //   62: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   65: aload_0
    //   66: invokevirtual startUpdate : (Landroid/view/ViewGroup;)V
    //   69: aload_0
    //   70: getfield mOffscreenPageLimit : I
    //   73: istore_1
    //   74: iconst_0
    //   75: aload_0
    //   76: getfield mCurItem : I
    //   79: iload_1
    //   80: isub
    //   81: invokestatic max : (II)I
    //   84: istore #11
    //   86: aload_0
    //   87: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   90: invokevirtual getCount : ()I
    //   93: istore #9
    //   95: iload #9
    //   97: iconst_1
    //   98: isub
    //   99: aload_0
    //   100: getfield mCurItem : I
    //   103: iload_1
    //   104: iadd
    //   105: invokestatic min : (II)I
    //   108: istore #10
    //   110: iload #9
    //   112: aload_0
    //   113: getfield mExpectedAdapterCount : I
    //   116: if_icmpeq -> 260
    //   119: aload_0
    //   120: invokevirtual getResources : ()Landroid/content/res/Resources;
    //   123: aload_0
    //   124: invokevirtual getId : ()I
    //   127: invokevirtual getResourceName : (I)Ljava/lang/String;
    //   130: astore #13
    //   132: goto -> 144
    //   135: aload_0
    //   136: invokevirtual getId : ()I
    //   139: invokestatic toHexString : (I)Ljava/lang/String;
    //   142: astore #13
    //   144: new java/lang/StringBuilder
    //   147: dup
    //   148: invokespecial <init> : ()V
    //   151: astore #14
    //   153: aload #14
    //   155: ldc_w 'The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: '
    //   158: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   161: pop
    //   162: aload #14
    //   164: aload_0
    //   165: getfield mExpectedAdapterCount : I
    //   168: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   171: pop
    //   172: aload #14
    //   174: ldc_w ', found: '
    //   177: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: pop
    //   181: aload #14
    //   183: iload #9
    //   185: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   188: pop
    //   189: aload #14
    //   191: ldc_w ' Pager id: '
    //   194: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   197: pop
    //   198: aload #14
    //   200: aload #13
    //   202: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   205: pop
    //   206: aload #14
    //   208: ldc_w ' Pager class: '
    //   211: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   214: pop
    //   215: aload #14
    //   217: aload_0
    //   218: invokevirtual getClass : ()Ljava/lang/Class;
    //   221: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   224: pop
    //   225: aload #14
    //   227: ldc_w ' Problematic adapter: '
    //   230: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   233: pop
    //   234: aload #14
    //   236: aload_0
    //   237: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   240: invokevirtual getClass : ()Ljava/lang/Class;
    //   243: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   246: pop
    //   247: new java/lang/IllegalStateException
    //   250: dup
    //   251: aload #14
    //   253: invokevirtual toString : ()Ljava/lang/String;
    //   256: invokespecial <init> : (Ljava/lang/String;)V
    //   259: athrow
    //   260: iconst_0
    //   261: istore_1
    //   262: iload_1
    //   263: aload_0
    //   264: getfield mItems : Ljava/util/ArrayList;
    //   267: invokevirtual size : ()I
    //   270: if_icmpge -> 320
    //   273: aload_0
    //   274: getfield mItems : Ljava/util/ArrayList;
    //   277: iload_1
    //   278: invokevirtual get : (I)Ljava/lang/Object;
    //   281: checkcast android/support/v4/view/ViewPager$ItemInfo
    //   284: astore #13
    //   286: aload #13
    //   288: getfield position : I
    //   291: aload_0
    //   292: getfield mCurItem : I
    //   295: if_icmplt -> 313
    //   298: aload #13
    //   300: getfield position : I
    //   303: aload_0
    //   304: getfield mCurItem : I
    //   307: if_icmpne -> 320
    //   310: goto -> 323
    //   313: iload_1
    //   314: iconst_1
    //   315: iadd
    //   316: istore_1
    //   317: goto -> 262
    //   320: aconst_null
    //   321: astore #13
    //   323: aload #13
    //   325: astore #15
    //   327: aload #13
    //   329: ifnonnull -> 352
    //   332: aload #13
    //   334: astore #15
    //   336: iload #9
    //   338: ifle -> 352
    //   341: aload_0
    //   342: aload_0
    //   343: getfield mCurItem : I
    //   346: iload_1
    //   347: invokevirtual addNewItem : (II)Landroid/support/v4/view/ViewPager$ItemInfo;
    //   350: astore #15
    //   352: aload #15
    //   354: ifnull -> 1133
    //   357: iload_1
    //   358: iconst_1
    //   359: isub
    //   360: istore #6
    //   362: iload #6
    //   364: iflt -> 384
    //   367: aload_0
    //   368: getfield mItems : Ljava/util/ArrayList;
    //   371: iload #6
    //   373: invokevirtual get : (I)Ljava/lang/Object;
    //   376: checkcast android/support/v4/view/ViewPager$ItemInfo
    //   379: astore #13
    //   381: goto -> 387
    //   384: aconst_null
    //   385: astore #13
    //   387: aload_0
    //   388: invokespecial getClientWidth : ()I
    //   391: istore #12
    //   393: iload #12
    //   395: ifgt -> 404
    //   398: fconst_0
    //   399: fstore #4
    //   401: goto -> 423
    //   404: fconst_2
    //   405: aload #15
    //   407: getfield widthFactor : F
    //   410: fsub
    //   411: aload_0
    //   412: invokevirtual getPaddingLeft : ()I
    //   415: i2f
    //   416: iload #12
    //   418: i2f
    //   419: fdiv
    //   420: fadd
    //   421: fstore #4
    //   423: aload_0
    //   424: getfield mCurItem : I
    //   427: iconst_1
    //   428: isub
    //   429: istore #8
    //   431: fconst_0
    //   432: fstore_3
    //   433: iload_1
    //   434: istore #5
    //   436: iload #8
    //   438: iflt -> 740
    //   441: fload_3
    //   442: fload #4
    //   444: fcmpl
    //   445: iflt -> 580
    //   448: iload #8
    //   450: iload #11
    //   452: if_icmpge -> 580
    //   455: aload #13
    //   457: ifnonnull -> 463
    //   460: goto -> 740
    //   463: fload_3
    //   464: fstore_2
    //   465: iload #6
    //   467: istore_1
    //   468: aload #13
    //   470: astore #16
    //   472: iload #5
    //   474: istore #7
    //   476: iload #8
    //   478: aload #13
    //   480: getfield position : I
    //   483: if_icmpne -> 718
    //   486: fload_3
    //   487: fstore_2
    //   488: iload #6
    //   490: istore_1
    //   491: aload #13
    //   493: astore #16
    //   495: iload #5
    //   497: istore #7
    //   499: aload #13
    //   501: getfield scrolling : Z
    //   504: ifne -> 718
    //   507: aload_0
    //   508: getfield mItems : Ljava/util/ArrayList;
    //   511: iload #6
    //   513: invokevirtual remove : (I)Ljava/lang/Object;
    //   516: pop
    //   517: aload_0
    //   518: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   521: aload_0
    //   522: iload #8
    //   524: aload #13
    //   526: getfield object : Ljava/lang/Object;
    //   529: invokevirtual destroyItem : (Landroid/view/ViewGroup;ILjava/lang/Object;)V
    //   532: iload #6
    //   534: iconst_1
    //   535: isub
    //   536: istore #6
    //   538: iload #5
    //   540: iconst_1
    //   541: isub
    //   542: istore #5
    //   544: fload_3
    //   545: fstore_2
    //   546: iload #6
    //   548: istore_1
    //   549: iload #5
    //   551: istore #7
    //   553: iload #6
    //   555: iflt -> 703
    //   558: aload_0
    //   559: getfield mItems : Ljava/util/ArrayList;
    //   562: iload #6
    //   564: invokevirtual get : (I)Ljava/lang/Object;
    //   567: checkcast android/support/v4/view/ViewPager$ItemInfo
    //   570: astore #13
    //   572: fload_3
    //   573: fstore_2
    //   574: iload #6
    //   576: istore_1
    //   577: goto -> 710
    //   580: aload #13
    //   582: ifnull -> 645
    //   585: iload #8
    //   587: aload #13
    //   589: getfield position : I
    //   592: if_icmpne -> 645
    //   595: fload_3
    //   596: aload #13
    //   598: getfield widthFactor : F
    //   601: fadd
    //   602: fstore_3
    //   603: iload #6
    //   605: iconst_1
    //   606: isub
    //   607: istore #6
    //   609: fload_3
    //   610: fstore_2
    //   611: iload #6
    //   613: istore_1
    //   614: iload #5
    //   616: istore #7
    //   618: iload #6
    //   620: iflt -> 703
    //   623: aload_0
    //   624: getfield mItems : Ljava/util/ArrayList;
    //   627: iload #6
    //   629: invokevirtual get : (I)Ljava/lang/Object;
    //   632: checkcast android/support/v4/view/ViewPager$ItemInfo
    //   635: astore #13
    //   637: fload_3
    //   638: fstore_2
    //   639: iload #6
    //   641: istore_1
    //   642: goto -> 710
    //   645: fload_3
    //   646: aload_0
    //   647: iload #8
    //   649: iload #6
    //   651: iconst_1
    //   652: iadd
    //   653: invokevirtual addNewItem : (II)Landroid/support/v4/view/ViewPager$ItemInfo;
    //   656: getfield widthFactor : F
    //   659: fadd
    //   660: fstore_3
    //   661: iload #5
    //   663: iconst_1
    //   664: iadd
    //   665: istore #5
    //   667: fload_3
    //   668: fstore_2
    //   669: iload #6
    //   671: istore_1
    //   672: iload #5
    //   674: istore #7
    //   676: iload #6
    //   678: iflt -> 703
    //   681: aload_0
    //   682: getfield mItems : Ljava/util/ArrayList;
    //   685: iload #6
    //   687: invokevirtual get : (I)Ljava/lang/Object;
    //   690: checkcast android/support/v4/view/ViewPager$ItemInfo
    //   693: astore #13
    //   695: fload_3
    //   696: fstore_2
    //   697: iload #6
    //   699: istore_1
    //   700: goto -> 710
    //   703: aconst_null
    //   704: astore #13
    //   706: iload #7
    //   708: istore #5
    //   710: iload #5
    //   712: istore #7
    //   714: aload #13
    //   716: astore #16
    //   718: iload #8
    //   720: iconst_1
    //   721: isub
    //   722: istore #8
    //   724: fload_2
    //   725: fstore_3
    //   726: iload_1
    //   727: istore #6
    //   729: aload #16
    //   731: astore #13
    //   733: iload #7
    //   735: istore #5
    //   737: goto -> 436
    //   740: aload #15
    //   742: getfield widthFactor : F
    //   745: fstore_3
    //   746: iload #5
    //   748: iconst_1
    //   749: iadd
    //   750: istore #6
    //   752: fload_3
    //   753: fconst_2
    //   754: fcmpg
    //   755: ifge -> 1106
    //   758: iload #6
    //   760: aload_0
    //   761: getfield mItems : Ljava/util/ArrayList;
    //   764: invokevirtual size : ()I
    //   767: if_icmpge -> 787
    //   770: aload_0
    //   771: getfield mItems : Ljava/util/ArrayList;
    //   774: iload #6
    //   776: invokevirtual get : (I)Ljava/lang/Object;
    //   779: checkcast android/support/v4/view/ViewPager$ItemInfo
    //   782: astore #13
    //   784: goto -> 790
    //   787: aconst_null
    //   788: astore #13
    //   790: iload #12
    //   792: ifgt -> 801
    //   795: fconst_0
    //   796: fstore #4
    //   798: goto -> 814
    //   801: aload_0
    //   802: invokevirtual getPaddingRight : ()I
    //   805: i2f
    //   806: iload #12
    //   808: i2f
    //   809: fdiv
    //   810: fconst_2
    //   811: fadd
    //   812: fstore #4
    //   814: aload_0
    //   815: getfield mCurItem : I
    //   818: istore_1
    //   819: aload #13
    //   821: astore #16
    //   823: iload_1
    //   824: iconst_1
    //   825: iadd
    //   826: istore #7
    //   828: iload #7
    //   830: iload #9
    //   832: if_icmpge -> 1106
    //   835: fload_3
    //   836: fload #4
    //   838: fcmpl
    //   839: iflt -> 963
    //   842: iload #7
    //   844: iload #10
    //   846: if_icmple -> 963
    //   849: aload #16
    //   851: ifnonnull -> 857
    //   854: goto -> 1106
    //   857: fload_3
    //   858: fstore_2
    //   859: iload #6
    //   861: istore_1
    //   862: aload #16
    //   864: astore #13
    //   866: iload #7
    //   868: aload #16
    //   870: getfield position : I
    //   873: if_icmpne -> 1091
    //   876: fload_3
    //   877: fstore_2
    //   878: iload #6
    //   880: istore_1
    //   881: aload #16
    //   883: astore #13
    //   885: aload #16
    //   887: getfield scrolling : Z
    //   890: ifne -> 1091
    //   893: aload_0
    //   894: getfield mItems : Ljava/util/ArrayList;
    //   897: iload #6
    //   899: invokevirtual remove : (I)Ljava/lang/Object;
    //   902: pop
    //   903: aload_0
    //   904: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   907: aload_0
    //   908: iload #7
    //   910: aload #16
    //   912: getfield object : Ljava/lang/Object;
    //   915: invokevirtual destroyItem : (Landroid/view/ViewGroup;ILjava/lang/Object;)V
    //   918: fload_3
    //   919: fstore_2
    //   920: iload #6
    //   922: istore_1
    //   923: iload #6
    //   925: aload_0
    //   926: getfield mItems : Ljava/util/ArrayList;
    //   929: invokevirtual size : ()I
    //   932: if_icmpge -> 957
    //   935: aload_0
    //   936: getfield mItems : Ljava/util/ArrayList;
    //   939: iload #6
    //   941: invokevirtual get : (I)Ljava/lang/Object;
    //   944: checkcast android/support/v4/view/ViewPager$ItemInfo
    //   947: astore #13
    //   949: fload_3
    //   950: fstore_2
    //   951: iload #6
    //   953: istore_1
    //   954: goto -> 1091
    //   957: aconst_null
    //   958: astore #13
    //   960: goto -> 1091
    //   963: aload #16
    //   965: ifnull -> 1031
    //   968: iload #7
    //   970: aload #16
    //   972: getfield position : I
    //   975: if_icmpne -> 1031
    //   978: fload_3
    //   979: aload #16
    //   981: getfield widthFactor : F
    //   984: fadd
    //   985: fstore_3
    //   986: iload #6
    //   988: iconst_1
    //   989: iadd
    //   990: istore #6
    //   992: fload_3
    //   993: fstore_2
    //   994: iload #6
    //   996: istore_1
    //   997: iload #6
    //   999: aload_0
    //   1000: getfield mItems : Ljava/util/ArrayList;
    //   1003: invokevirtual size : ()I
    //   1006: if_icmpge -> 957
    //   1009: aload_0
    //   1010: getfield mItems : Ljava/util/ArrayList;
    //   1013: iload #6
    //   1015: invokevirtual get : (I)Ljava/lang/Object;
    //   1018: checkcast android/support/v4/view/ViewPager$ItemInfo
    //   1021: astore #13
    //   1023: fload_3
    //   1024: fstore_2
    //   1025: iload #6
    //   1027: istore_1
    //   1028: goto -> 1091
    //   1031: aload_0
    //   1032: iload #7
    //   1034: iload #6
    //   1036: invokevirtual addNewItem : (II)Landroid/support/v4/view/ViewPager$ItemInfo;
    //   1039: astore #13
    //   1041: iload #6
    //   1043: iconst_1
    //   1044: iadd
    //   1045: istore #6
    //   1047: fload_3
    //   1048: aload #13
    //   1050: getfield widthFactor : F
    //   1053: fadd
    //   1054: fstore_3
    //   1055: fload_3
    //   1056: fstore_2
    //   1057: iload #6
    //   1059: istore_1
    //   1060: iload #6
    //   1062: aload_0
    //   1063: getfield mItems : Ljava/util/ArrayList;
    //   1066: invokevirtual size : ()I
    //   1069: if_icmpge -> 957
    //   1072: aload_0
    //   1073: getfield mItems : Ljava/util/ArrayList;
    //   1076: iload #6
    //   1078: invokevirtual get : (I)Ljava/lang/Object;
    //   1081: checkcast android/support/v4/view/ViewPager$ItemInfo
    //   1084: astore #13
    //   1086: iload #6
    //   1088: istore_1
    //   1089: fload_3
    //   1090: fstore_2
    //   1091: fload_2
    //   1092: fstore_3
    //   1093: iload_1
    //   1094: istore #6
    //   1096: aload #13
    //   1098: astore #16
    //   1100: iload #7
    //   1102: istore_1
    //   1103: goto -> 823
    //   1106: aload_0
    //   1107: aload #15
    //   1109: iload #5
    //   1111: aload #14
    //   1113: invokespecial calculatePageOffsets : (Landroid/support/v4/view/ViewPager$ItemInfo;ILandroid/support/v4/view/ViewPager$ItemInfo;)V
    //   1116: aload_0
    //   1117: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   1120: aload_0
    //   1121: aload_0
    //   1122: getfield mCurItem : I
    //   1125: aload #15
    //   1127: getfield object : Ljava/lang/Object;
    //   1130: invokevirtual setPrimaryItem : (Landroid/view/ViewGroup;ILjava/lang/Object;)V
    //   1133: aload_0
    //   1134: getfield mAdapter : Landroid/support/v4/view/PagerAdapter;
    //   1137: aload_0
    //   1138: invokevirtual finishUpdate : (Landroid/view/ViewGroup;)V
    //   1141: aload_0
    //   1142: invokevirtual getChildCount : ()I
    //   1145: istore #5
    //   1147: iconst_0
    //   1148: istore_1
    //   1149: iload_1
    //   1150: iload #5
    //   1152: if_icmpge -> 1236
    //   1155: aload_0
    //   1156: iload_1
    //   1157: invokevirtual getChildAt : (I)Landroid/view/View;
    //   1160: astore #14
    //   1162: aload #14
    //   1164: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   1167: checkcast android/support/v4/view/ViewPager$LayoutParams
    //   1170: astore #13
    //   1172: aload #13
    //   1174: iload_1
    //   1175: putfield childIndex : I
    //   1178: aload #13
    //   1180: getfield isDecor : Z
    //   1183: ifne -> 1229
    //   1186: aload #13
    //   1188: getfield widthFactor : F
    //   1191: fconst_0
    //   1192: fcmpl
    //   1193: ifne -> 1229
    //   1196: aload_0
    //   1197: aload #14
    //   1199: invokevirtual infoForChild : (Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;
    //   1202: astore #14
    //   1204: aload #14
    //   1206: ifnull -> 1229
    //   1209: aload #13
    //   1211: aload #14
    //   1213: getfield widthFactor : F
    //   1216: putfield widthFactor : F
    //   1219: aload #13
    //   1221: aload #14
    //   1223: getfield position : I
    //   1226: putfield position : I
    //   1229: iload_1
    //   1230: iconst_1
    //   1231: iadd
    //   1232: istore_1
    //   1233: goto -> 1149
    //   1236: aload_0
    //   1237: invokespecial sortChildDrawingOrder : ()V
    //   1240: aload_0
    //   1241: invokevirtual hasFocus : ()Z
    //   1244: ifeq -> 1348
    //   1247: aload_0
    //   1248: invokevirtual findFocus : ()Landroid/view/View;
    //   1251: astore #13
    //   1253: aload #13
    //   1255: ifnull -> 1269
    //   1258: aload_0
    //   1259: aload #13
    //   1261: invokevirtual infoForAnyChild : (Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;
    //   1264: astore #13
    //   1266: goto -> 1272
    //   1269: aconst_null
    //   1270: astore #13
    //   1272: aload #13
    //   1274: ifnull -> 1289
    //   1277: aload #13
    //   1279: getfield position : I
    //   1282: aload_0
    //   1283: getfield mCurItem : I
    //   1286: if_icmpeq -> 1348
    //   1289: iconst_0
    //   1290: istore_1
    //   1291: iload_1
    //   1292: aload_0
    //   1293: invokevirtual getChildCount : ()I
    //   1296: if_icmpge -> 1348
    //   1299: aload_0
    //   1300: iload_1
    //   1301: invokevirtual getChildAt : (I)Landroid/view/View;
    //   1304: astore #13
    //   1306: aload_0
    //   1307: aload #13
    //   1309: invokevirtual infoForChild : (Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;
    //   1312: astore #14
    //   1314: aload #14
    //   1316: ifnull -> 1341
    //   1319: aload #14
    //   1321: getfield position : I
    //   1324: aload_0
    //   1325: getfield mCurItem : I
    //   1328: if_icmpne -> 1341
    //   1331: aload #13
    //   1333: iconst_2
    //   1334: invokevirtual requestFocus : (I)Z
    //   1337: ifeq -> 1341
    //   1340: return
    //   1341: iload_1
    //   1342: iconst_1
    //   1343: iadd
    //   1344: istore_1
    //   1345: goto -> 1291
    //   1348: return
    //   1349: astore #13
    //   1351: goto -> 135
    // Exception table:
    //   from	to	target	type
    //   119	132	1349	android/content/res/Resources$NotFoundException }
  
  public void removeOnAdapterChangeListener(@NonNull OnAdapterChangeListener paramOnAdapterChangeListener) {
    if (this.mAdapterChangeListeners != null)
      this.mAdapterChangeListeners.remove(paramOnAdapterChangeListener); 
  }
  
  public void removeOnPageChangeListener(@NonNull OnPageChangeListener paramOnPageChangeListener) {
    if (this.mOnPageChangeListeners != null)
      this.mOnPageChangeListeners.remove(paramOnPageChangeListener); 
  }
  
  public void removeView(View paramView) {
    if (this.mInLayout) {
      removeViewInLayout(paramView);
      return;
    } 
    super.removeView(paramView);
  }
  
  public void setAdapter(@Nullable PagerAdapter paramPagerAdapter) {
    PagerAdapter pagerAdapter = this.mAdapter;
    boolean bool = false;
    if (pagerAdapter != null) {
      this.mAdapter.setViewPagerObserver(null);
      this.mAdapter.startUpdate(this);
      for (byte b = 0; b < this.mItems.size(); b++) {
        ItemInfo itemInfo = (ItemInfo)this.mItems.get(b);
        this.mAdapter.destroyItem(this, itemInfo.position, itemInfo.object);
      } 
      this.mAdapter.finishUpdate(this);
      this.mItems.clear();
      removeNonDecorViews();
      this.mCurItem = 0;
      scrollTo(0, 0);
    } 
    pagerAdapter = this.mAdapter;
    this.mAdapter = paramPagerAdapter;
    this.mExpectedAdapterCount = 0;
    if (this.mAdapter != null) {
      if (this.mObserver == null)
        this.mObserver = new PagerObserver(); 
      this.mAdapter.setViewPagerObserver(this.mObserver);
      this.mPopulatePending = false;
      boolean bool1 = this.mFirstLayout;
      this.mFirstLayout = true;
      this.mExpectedAdapterCount = this.mAdapter.getCount();
      if (this.mRestoredCurItem >= 0) {
        this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
        setCurrentItemInternal(this.mRestoredCurItem, false, true);
        this.mRestoredCurItem = -1;
        this.mRestoredAdapterState = null;
        this.mRestoredClassLoader = null;
      } else if (!bool1) {
        populate();
      } else {
        requestLayout();
      } 
    } 
    if (this.mAdapterChangeListeners != null && !this.mAdapterChangeListeners.isEmpty()) {
      int i = this.mAdapterChangeListeners.size();
      for (byte b = bool; b < i; b++)
        ((OnAdapterChangeListener)this.mAdapterChangeListeners.get(b)).onAdapterChanged(this, pagerAdapter, paramPagerAdapter); 
    } 
  }
  
  public void setCurrentItem(int paramInt) {
    this.mPopulatePending = false;
    setCurrentItemInternal(paramInt, this.mFirstLayout ^ true, false);
  }
  
  public void setCurrentItem(int paramInt, boolean paramBoolean) {
    this.mPopulatePending = false;
    setCurrentItemInternal(paramInt, paramBoolean, false);
  }
  
  void setCurrentItemInternal(int paramInt, boolean paramBoolean1, boolean paramBoolean2) { setCurrentItemInternal(paramInt, paramBoolean1, paramBoolean2, 0); }
  
  void setCurrentItemInternal(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2) {
    int i;
    if (this.mAdapter == null || this.mAdapter.getCount() <= 0) {
      setScrollingCacheEnabled(false);
      return;
    } 
    if (!paramBoolean2 && this.mCurItem == paramInt1 && this.mItems.size() != 0) {
      setScrollingCacheEnabled(false);
      return;
    } 
    paramBoolean2 = true;
    if (paramInt1 < 0) {
      i = 0;
    } else {
      i = paramInt1;
      if (paramInt1 >= this.mAdapter.getCount())
        i = this.mAdapter.getCount() - 1; 
    } 
    paramInt1 = this.mOffscreenPageLimit;
    if (i > this.mCurItem + paramInt1 || i < this.mCurItem - paramInt1)
      for (paramInt1 = 0; paramInt1 < this.mItems.size(); paramInt1++)
        ((ItemInfo)this.mItems.get(paramInt1)).scrolling = true;  
    if (this.mCurItem == i)
      paramBoolean2 = false; 
    if (this.mFirstLayout) {
      this.mCurItem = i;
      if (paramBoolean2)
        dispatchOnPageSelected(i); 
      requestLayout();
      return;
    } 
    populate(i);
    scrollToItem(i, paramBoolean1, paramInt2, paramBoolean2);
  }
  
  OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener paramOnPageChangeListener) {
    OnPageChangeListener onPageChangeListener = this.mInternalPageChangeListener;
    this.mInternalPageChangeListener = paramOnPageChangeListener;
    return onPageChangeListener;
  }
  
  public void setOffscreenPageLimit(int paramInt) {
    int i = paramInt;
    if (paramInt < 1) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Requested offscreen page limit ");
      stringBuilder.append(paramInt);
      stringBuilder.append(" too small; defaulting to ");
      stringBuilder.append(1);
      Log.w("ViewPager", stringBuilder.toString());
      i = 1;
    } 
    if (i != this.mOffscreenPageLimit) {
      this.mOffscreenPageLimit = i;
      populate();
    } 
  }
  
  @Deprecated
  public void setOnPageChangeListener(OnPageChangeListener paramOnPageChangeListener) { this.mOnPageChangeListener = paramOnPageChangeListener; }
  
  public void setPageMargin(int paramInt) {
    int i = this.mPageMargin;
    this.mPageMargin = paramInt;
    int j = getWidth();
    recomputeScrollPosition(j, j, paramInt, i);
    requestLayout();
  }
  
  public void setPageMarginDrawable(@DrawableRes int paramInt) { setPageMarginDrawable(ContextCompat.getDrawable(getContext(), paramInt)); }
  
  public void setPageMarginDrawable(@Nullable Drawable paramDrawable) {
    boolean bool;
    this.mMarginDrawable = paramDrawable;
    if (paramDrawable != null)
      refreshDrawableState(); 
    if (paramDrawable == null) {
      bool = true;
    } else {
      bool = false;
    } 
    setWillNotDraw(bool);
    invalidate();
  }
  
  public void setPageTransformer(boolean paramBoolean, @Nullable PageTransformer paramPageTransformer) { setPageTransformer(paramBoolean, paramPageTransformer, 2); }
  
  public void setPageTransformer(boolean paramBoolean, @Nullable PageTransformer paramPageTransformer, int paramInt) {
    boolean bool2;
    boolean bool;
    boolean bool1;
    byte b = 1;
    if (paramPageTransformer != null) {
      bool = true;
    } else {
      bool = false;
    } 
    if (this.mPageTransformer != null) {
      bool2 = true;
    } else {
      bool2 = false;
    } 
    if (bool != bool2) {
      bool1 = true;
    } else {
      bool1 = false;
    } 
    this.mPageTransformer = paramPageTransformer;
    setChildrenDrawingOrderEnabled(bool);
    if (bool) {
      if (paramBoolean)
        b = 2; 
      this.mDrawingOrder = b;
      this.mPageTransformerLayerType = paramInt;
    } else {
      this.mDrawingOrder = 0;
    } 
    if (bool1)
      populate(); 
  }
  
  void setScrollState(int paramInt) {
    if (this.mScrollState == paramInt)
      return; 
    this.mScrollState = paramInt;
    if (this.mPageTransformer != null) {
      boolean bool;
      if (paramInt != 0) {
        bool = true;
      } else {
        bool = false;
      } 
      enableLayers(bool);
    } 
    dispatchOnScrollStateChanged(paramInt);
  }
  
  void smoothScrollTo(int paramInt1, int paramInt2) { smoothScrollTo(paramInt1, paramInt2, 0); }
  
  void smoothScrollTo(int paramInt1, int paramInt2, int paramInt3) {
    int i;
    if (getChildCount() == 0) {
      setScrollingCacheEnabled(false);
      return;
    } 
    if (this.mScroller != null && !this.mScroller.isFinished()) {
      i = 1;
    } else {
      i = 0;
    } 
    if (i) {
      if (this.mIsScrollStarted) {
        i = this.mScroller.getCurrX();
      } else {
        i = this.mScroller.getStartX();
      } 
      this.mScroller.abortAnimation();
      setScrollingCacheEnabled(false);
    } else {
      i = getScrollX();
    } 
    int j = getScrollY();
    int k = paramInt1 - i;
    paramInt2 -= j;
    if (k == 0 && paramInt2 == 0) {
      completeScroll(false);
      populate();
      setScrollState(0);
      return;
    } 
    setScrollingCacheEnabled(true);
    setScrollState(2);
    paramInt1 = getClientWidth();
    int m = paramInt1 / 2;
    float f2 = Math.abs(k);
    float f1 = paramInt1;
    float f3 = Math.min(1.0F, f2 * 1.0F / f1);
    f2 = m;
    f3 = distanceInfluenceForSnapDuration(f3);
    paramInt1 = Math.abs(paramInt3);
    if (paramInt1 > 0) {
      paramInt1 = Math.round(Math.abs((f2 + f3 * f2) / paramInt1) * 1000.0F) * 4;
    } else {
      f2 = this.mAdapter.getPageWidth(this.mCurItem);
      paramInt1 = (int)((Math.abs(k) / (f1 * f2 + this.mPageMargin) + 1.0F) * 100.0F);
    } 
    paramInt1 = Math.min(paramInt1, 600);
    this.mIsScrollStarted = false;
    this.mScroller.startScroll(i, j, k, paramInt2, paramInt1);
    ViewCompat.postInvalidateOnAnimation(this);
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable) { return (super.verifyDrawable(paramDrawable) || paramDrawable == this.mMarginDrawable); }
  
  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE})
  public static @interface DecorView {}
  
  static class ItemInfo {
    Object object;
    
    float offset;
    
    int position;
    
    boolean scrolling;
    
    float widthFactor;
  }
  
  public static class LayoutParams extends ViewGroup.LayoutParams {
    int childIndex;
    
    public int gravity;
    
    public boolean isDecor;
    
    boolean needsMeasure;
    
    int position;
    
    float widthFactor = 0.0F;
    
    public LayoutParams() { super(-1, -1); }
    
    public LayoutParams(Context param1Context, AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, ViewPager.LAYOUT_ATTRS);
      this.gravity = typedArray.getInteger(0, 48);
      typedArray.recycle();
    }
  }
  
  class MyAccessibilityDelegate extends AccessibilityDelegateCompat {
    private boolean canScroll() { return (ViewPager.this.mAdapter != null && ViewPager.this.mAdapter.getCount() > 1); }
    
    public void onInitializeAccessibilityEvent(View param1View, AccessibilityEvent param1AccessibilityEvent) {
      super.onInitializeAccessibilityEvent(param1View, param1AccessibilityEvent);
      param1AccessibilityEvent.setClassName(ViewPager.class.getName());
      param1AccessibilityEvent.setScrollable(canScroll());
      if (param1AccessibilityEvent.getEventType() == 4096 && ViewPager.this.mAdapter != null) {
        param1AccessibilityEvent.setItemCount(ViewPager.this.mAdapter.getCount());
        param1AccessibilityEvent.setFromIndex(ViewPager.this.mCurItem);
        param1AccessibilityEvent.setToIndex(ViewPager.this.mCurItem);
      } 
    }
    
    public void onInitializeAccessibilityNodeInfo(View param1View, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) {
      super.onInitializeAccessibilityNodeInfo(param1View, param1AccessibilityNodeInfoCompat);
      param1AccessibilityNodeInfoCompat.setClassName(ViewPager.class.getName());
      param1AccessibilityNodeInfoCompat.setScrollable(canScroll());
      if (ViewPager.this.canScrollHorizontally(1))
        param1AccessibilityNodeInfoCompat.addAction(4096); 
      if (ViewPager.this.canScrollHorizontally(-1))
        param1AccessibilityNodeInfoCompat.addAction(8192); 
    }
    
    public boolean performAccessibilityAction(View param1View, int param1Int, Bundle param1Bundle) {
      if (super.performAccessibilityAction(param1View, param1Int, param1Bundle))
        return true; 
      if (param1Int != 4096) {
        if (param1Int != 8192)
          return false; 
        if (ViewPager.this.canScrollHorizontally(-1)) {
          ViewPager.this.setCurrentItem(ViewPager.this.mCurItem - 1);
          return true;
        } 
        return false;
      } 
      if (ViewPager.this.canScrollHorizontally(1)) {
        ViewPager.this.setCurrentItem(ViewPager.this.mCurItem + 1);
        return true;
      } 
      return false;
    }
  }
  
  public static interface OnAdapterChangeListener {
    void onAdapterChanged(@NonNull ViewPager param1ViewPager, @Nullable PagerAdapter param1PagerAdapter1, @Nullable PagerAdapter param1PagerAdapter2);
  }
  
  public static interface OnPageChangeListener {
    void onPageScrollStateChanged(int param1Int);
    
    void onPageScrolled(int param1Int1, float param1Float, int param1Int2);
    
    void onPageSelected(int param1Int);
  }
  
  public static interface PageTransformer {
    void transformPage(@NonNull View param1View, float param1Float);
  }
  
  private class PagerObserver extends DataSetObserver {
    public void onChanged() { ViewPager.this.dataSetChanged(); }
    
    public void onInvalidated() { ViewPager.this.dataSetChanged(); }
  }
  
  public static class SavedState extends AbsSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
        public ViewPager.SavedState createFromParcel(Parcel param2Parcel) { return new ViewPager.SavedState(param2Parcel, null); }
        
        public ViewPager.SavedState createFromParcel(Parcel param2Parcel, ClassLoader param2ClassLoader) { return new ViewPager.SavedState(param2Parcel, param2ClassLoader); }
        
        public ViewPager.SavedState[] newArray(int param2Int) { return new ViewPager.SavedState[param2Int]; }
      };
    
    Parcelable adapterState;
    
    ClassLoader loader;
    
    int position;
    
    SavedState(Parcel param1Parcel, ClassLoader param1ClassLoader) {
      super(param1Parcel, param1ClassLoader);
      ClassLoader classLoader = param1ClassLoader;
      if (param1ClassLoader == null)
        classLoader = getClass().getClassLoader(); 
      this.position = param1Parcel.readInt();
      this.adapterState = param1Parcel.readParcelable(classLoader);
      this.loader = classLoader;
    }
    
    public SavedState(@NonNull Parcelable param1Parcelable) { super(param1Parcelable); }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("FragmentPager.SavedState{");
      stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      stringBuilder.append(" position=");
      stringBuilder.append(this.position);
      stringBuilder.append("}");
      return stringBuilder.toString();
    }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) {
      super.writeToParcel(param1Parcel, param1Int);
      param1Parcel.writeInt(this.position);
      param1Parcel.writeParcelable(this.adapterState, param1Int);
    }
  }
  
  static final class null extends Object implements Parcelable.ClassLoaderCreator<SavedState> {
    public ViewPager.SavedState createFromParcel(Parcel param1Parcel) { return new ViewPager.SavedState(param1Parcel, null); }
    
    public ViewPager.SavedState createFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) { return new ViewPager.SavedState(param1Parcel, param1ClassLoader); }
    
    public ViewPager.SavedState[] newArray(int param1Int) { return new ViewPager.SavedState[param1Int]; }
  }
  
  public static class SimpleOnPageChangeListener implements OnPageChangeListener {
    public void onPageScrollStateChanged(int param1Int) {}
    
    public void onPageScrolled(int param1Int1, float param1Float, int param1Int2) {}
    
    public void onPageSelected(int param1Int) {}
  }
  
  static class ViewPositionComparator extends Object implements Comparator<View> {
    public int compare(View param1View1, View param1View2) {
      ViewPager.LayoutParams layoutParams1 = (ViewPager.LayoutParams)param1View1.getLayoutParams();
      ViewPager.LayoutParams layoutParams2 = (ViewPager.LayoutParams)param1View2.getLayoutParams();
      return (layoutParams1.isDecor != layoutParams2.isDecor) ? (layoutParams1.isDecor ? 1 : -1) : (layoutParams1.position - layoutParams2.position);
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/ViewPager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */