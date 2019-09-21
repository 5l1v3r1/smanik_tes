package android.support.v4.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.widget.EdgeEffect;
import android.widget.FrameLayout;
import android.widget.OverScroller;

public class NestedScrollView extends FrameLayout implements NestedScrollingParent, NestedScrollingChild2, ScrollingView {
  private static final AccessibilityDelegate ACCESSIBILITY_DELEGATE = new AccessibilityDelegate();
  
  static final int ANIMATED_SCROLL_GAP = 250;
  
  private static final int INVALID_POINTER = -1;
  
  static final float MAX_SCROLL_FACTOR = 0.5F;
  
  private static final int[] SCROLLVIEW_STYLEABLE = { 16843130 };
  
  private static final String TAG = "NestedScrollView";
  
  private int mActivePointerId = -1;
  
  private final NestedScrollingChildHelper mChildHelper;
  
  private View mChildToScrollTo = null;
  
  private EdgeEffect mEdgeGlowBottom;
  
  private EdgeEffect mEdgeGlowTop;
  
  private boolean mFillViewport;
  
  private boolean mIsBeingDragged = false;
  
  private boolean mIsLaidOut = false;
  
  private boolean mIsLayoutDirty = true;
  
  private int mLastMotionY;
  
  private long mLastScroll;
  
  private int mLastScrollerY;
  
  private int mMaximumVelocity;
  
  private int mMinimumVelocity;
  
  private int mNestedYOffset;
  
  private OnScrollChangeListener mOnScrollChangeListener;
  
  private final NestedScrollingParentHelper mParentHelper;
  
  private SavedState mSavedState;
  
  private final int[] mScrollConsumed = new int[2];
  
  private final int[] mScrollOffset = new int[2];
  
  private OverScroller mScroller;
  
  private boolean mSmoothScrollingEnabled = true;
  
  private final Rect mTempRect = new Rect();
  
  private int mTouchSlop;
  
  private VelocityTracker mVelocityTracker;
  
  private float mVerticalScrollFactor;
  
  public NestedScrollView(@NonNull Context paramContext) { this(paramContext, null); }
  
  public NestedScrollView(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public NestedScrollView(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    initScrollView();
    TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, SCROLLVIEW_STYLEABLE, paramInt, 0);
    setFillViewport(typedArray.getBoolean(0, false));
    typedArray.recycle();
    this.mParentHelper = new NestedScrollingParentHelper(this);
    this.mChildHelper = new NestedScrollingChildHelper(this);
    setNestedScrollingEnabled(true);
    ViewCompat.setAccessibilityDelegate(this, ACCESSIBILITY_DELEGATE);
  }
  
  private boolean canScroll() {
    boolean bool = false;
    View view = getChildAt(0);
    if (view != null) {
      int i = view.getHeight();
      if (getHeight() < i + getPaddingTop() + getPaddingBottom())
        bool = true; 
      return bool;
    } 
    return false;
  }
  
  private static int clamp(int paramInt1, int paramInt2, int paramInt3) { return (paramInt2 >= paramInt3 || paramInt1 < 0) ? 0 : ((paramInt2 + paramInt1 > paramInt3) ? (paramInt3 - paramInt2) : paramInt1); }
  
  private void doScrollY(int paramInt) {
    if (paramInt != 0) {
      if (this.mSmoothScrollingEnabled) {
        smoothScrollBy(0, paramInt);
        return;
      } 
      scrollBy(0, paramInt);
    } 
  }
  
  private void endDrag() {
    this.mIsBeingDragged = false;
    recycleVelocityTracker();
    stopNestedScroll(0);
    if (this.mEdgeGlowTop != null) {
      this.mEdgeGlowTop.onRelease();
      this.mEdgeGlowBottom.onRelease();
    } 
  }
  
  private void ensureGlows() {
    if (getOverScrollMode() != 2) {
      if (this.mEdgeGlowTop == null) {
        Context context = getContext();
        this.mEdgeGlowTop = new EdgeEffect(context);
        this.mEdgeGlowBottom = new EdgeEffect(context);
        return;
      } 
    } else {
      this.mEdgeGlowTop = null;
      this.mEdgeGlowBottom = null;
    } 
  }
  
  private View findFocusableViewInBounds(boolean paramBoolean, int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: iconst_2
    //   2: invokevirtual getFocusables : (I)Ljava/util/ArrayList;
    //   5: astore #14
    //   7: aload #14
    //   9: invokeinterface size : ()I
    //   14: istore #9
    //   16: aconst_null
    //   17: astore #13
    //   19: iconst_0
    //   20: istore #6
    //   22: iconst_0
    //   23: istore #7
    //   25: iload #6
    //   27: iload #9
    //   29: if_icmpge -> 249
    //   32: aload #14
    //   34: iload #6
    //   36: invokeinterface get : (I)Ljava/lang/Object;
    //   41: checkcast android/view/View
    //   44: astore #12
    //   46: aload #12
    //   48: invokevirtual getTop : ()I
    //   51: istore #8
    //   53: aload #12
    //   55: invokevirtual getBottom : ()I
    //   58: istore #10
    //   60: aload #13
    //   62: astore #11
    //   64: iload #7
    //   66: istore #5
    //   68: iload_2
    //   69: iload #10
    //   71: if_icmpge -> 232
    //   74: aload #13
    //   76: astore #11
    //   78: iload #7
    //   80: istore #5
    //   82: iload #8
    //   84: iload_3
    //   85: if_icmpge -> 232
    //   88: iload_2
    //   89: iload #8
    //   91: if_icmpge -> 106
    //   94: iload #10
    //   96: iload_3
    //   97: if_icmpge -> 106
    //   100: iconst_1
    //   101: istore #4
    //   103: goto -> 109
    //   106: iconst_0
    //   107: istore #4
    //   109: aload #13
    //   111: ifnonnull -> 125
    //   114: aload #12
    //   116: astore #11
    //   118: iload #4
    //   120: istore #5
    //   122: goto -> 232
    //   125: iload_1
    //   126: ifeq -> 139
    //   129: iload #8
    //   131: aload #13
    //   133: invokevirtual getTop : ()I
    //   136: if_icmplt -> 153
    //   139: iload_1
    //   140: ifne -> 159
    //   143: iload #10
    //   145: aload #13
    //   147: invokevirtual getBottom : ()I
    //   150: if_icmple -> 159
    //   153: iconst_1
    //   154: istore #8
    //   156: goto -> 162
    //   159: iconst_0
    //   160: istore #8
    //   162: iload #7
    //   164: ifeq -> 196
    //   167: aload #13
    //   169: astore #11
    //   171: iload #7
    //   173: istore #5
    //   175: iload #4
    //   177: ifeq -> 232
    //   180: aload #13
    //   182: astore #11
    //   184: iload #7
    //   186: istore #5
    //   188: iload #8
    //   190: ifeq -> 232
    //   193: goto -> 224
    //   196: iload #4
    //   198: ifeq -> 211
    //   201: aload #12
    //   203: astore #11
    //   205: iconst_1
    //   206: istore #5
    //   208: goto -> 232
    //   211: aload #13
    //   213: astore #11
    //   215: iload #7
    //   217: istore #5
    //   219: iload #8
    //   221: ifeq -> 232
    //   224: aload #12
    //   226: astore #11
    //   228: iload #7
    //   230: istore #5
    //   232: iload #6
    //   234: iconst_1
    //   235: iadd
    //   236: istore #6
    //   238: aload #11
    //   240: astore #13
    //   242: iload #5
    //   244: istore #7
    //   246: goto -> 25
    //   249: aload #13
    //   251: areturn }
  
  private void flingWithNestedDispatch(int paramInt) {
    boolean bool;
    int i = getScrollY();
    if ((i > 0 || paramInt > 0) && (i < getScrollRange() || paramInt < 0)) {
      bool = true;
    } else {
      bool = false;
    } 
    float f = paramInt;
    if (!dispatchNestedPreFling(0.0F, f)) {
      dispatchNestedFling(0.0F, f, bool);
      fling(paramInt);
    } 
  }
  
  private float getVerticalScrollFactorCompat() {
    if (this.mVerticalScrollFactor == 0.0F) {
      TypedValue typedValue = new TypedValue();
      Context context = getContext();
      if (!context.getTheme().resolveAttribute(16842829, typedValue, true))
        throw new IllegalStateException("Expected theme to define listPreferredItemHeight."); 
      this.mVerticalScrollFactor = typedValue.getDimension(context.getResources().getDisplayMetrics());
    } 
    return this.mVerticalScrollFactor;
  }
  
  private boolean inChild(int paramInt1, int paramInt2) {
    int i = getChildCount();
    byte b = 0;
    if (i > 0) {
      i = getScrollY();
      View view = getChildAt(0);
      int j = b;
      if (paramInt2 >= view.getTop() - i) {
        j = b;
        if (paramInt2 < view.getBottom() - i) {
          j = b;
          if (paramInt1 >= view.getLeft()) {
            j = b;
            if (paramInt1 < view.getRight())
              j = 1; 
          } 
        } 
      } 
      return j;
    } 
    return false;
  }
  
  private void initOrResetVelocityTracker() {
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
      return;
    } 
    this.mVelocityTracker.clear();
  }
  
  private void initScrollView() {
    this.mScroller = new OverScroller(getContext());
    setFocusable(true);
    setDescendantFocusability(262144);
    setWillNotDraw(false);
    ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
    this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
    this.mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
  }
  
  private void initVelocityTrackerIfNotExists() {
    if (this.mVelocityTracker == null)
      this.mVelocityTracker = VelocityTracker.obtain(); 
  }
  
  private boolean isOffScreen(View paramView) { return isWithinDeltaOfScreen(paramView, 0, getHeight()) ^ true; }
  
  private static boolean isViewDescendantOf(View paramView1, View paramView2) {
    if (paramView1 == paramView2)
      return true; 
    ViewParent viewParent = paramView1.getParent();
    return (viewParent instanceof ViewGroup && isViewDescendantOf((View)viewParent, paramView2));
  }
  
  private boolean isWithinDeltaOfScreen(View paramView, int paramInt1, int paramInt2) {
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    return (this.mTempRect.bottom + paramInt1 >= getScrollY() && this.mTempRect.top - paramInt1 <= getScrollY() + paramInt2);
  }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent) {
    int i = paramMotionEvent.getActionIndex();
    if (paramMotionEvent.getPointerId(i) == this.mActivePointerId) {
      if (i == 0) {
        i = 1;
      } else {
        i = 0;
      } 
      this.mLastMotionY = (int)paramMotionEvent.getY(i);
      this.mActivePointerId = paramMotionEvent.getPointerId(i);
      if (this.mVelocityTracker != null)
        this.mVelocityTracker.clear(); 
    } 
  }
  
  private void recycleVelocityTracker() {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    } 
  }
  
  private boolean scrollAndFocus(int paramInt1, int paramInt2, int paramInt3) {
    boolean bool;
    int j = getHeight();
    int i = getScrollY();
    j += i;
    boolean bool1 = false;
    if (paramInt1 == 33) {
      bool = true;
    } else {
      bool = false;
    } 
    View view = findFocusableViewInBounds(bool, paramInt2, paramInt3);
    NestedScrollView nestedScrollView = view;
    if (view == null)
      nestedScrollView = this; 
    if (paramInt2 >= i && paramInt3 <= j) {
      bool = bool1;
    } else {
      if (bool) {
        paramInt2 -= i;
      } else {
        paramInt2 = paramInt3 - j;
      } 
      doScrollY(paramInt2);
      bool = true;
    } 
    if (nestedScrollView != findFocus())
      nestedScrollView.requestFocus(paramInt1); 
    return bool;
  }
  
  private void scrollToChild(View paramView) {
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    int i = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
    if (i != 0)
      scrollBy(0, i); 
  }
  
  private boolean scrollToChildRect(Rect paramRect, boolean paramBoolean) {
    boolean bool;
    int i = computeScrollDeltaToGetChildRectOnScreen(paramRect);
    if (i != 0) {
      bool = true;
    } else {
      bool = false;
    } 
    if (bool) {
      if (paramBoolean) {
        scrollBy(0, i);
        return bool;
      } 
      smoothScrollBy(0, i);
    } 
    return bool;
  }
  
  public void addView(View paramView) {
    if (getChildCount() > 0)
      throw new IllegalStateException("ScrollView can host only one direct child"); 
    super.addView(paramView);
  }
  
  public void addView(View paramView, int paramInt) {
    if (getChildCount() > 0)
      throw new IllegalStateException("ScrollView can host only one direct child"); 
    super.addView(paramView, paramInt);
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams) {
    if (getChildCount() > 0)
      throw new IllegalStateException("ScrollView can host only one direct child"); 
    super.addView(paramView, paramInt, paramLayoutParams);
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams) {
    if (getChildCount() > 0)
      throw new IllegalStateException("ScrollView can host only one direct child"); 
    super.addView(paramView, paramLayoutParams);
  }
  
  public boolean arrowScroll(int paramInt) {
    View view2 = findFocus();
    View view1 = view2;
    if (view2 == this)
      view1 = null; 
    view2 = FocusFinder.getInstance().findNextFocus(this, view1, paramInt);
    int i = getMaxScrollAmount();
    if (view2 != null && isWithinDeltaOfScreen(view2, i, getHeight())) {
      view2.getDrawingRect(this.mTempRect);
      offsetDescendantRectToMyCoords(view2, this.mTempRect);
      doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
      view2.requestFocus(paramInt);
    } else {
      int j;
      if (paramInt == 33 && getScrollY() < i) {
        j = getScrollY();
      } else {
        j = i;
        if (paramInt == 130) {
          j = i;
          if (getChildCount() > 0) {
            int k = getChildAt(0).getBottom() - getScrollY() + getHeight() - getPaddingBottom();
            j = i;
            if (k < i)
              j = k; 
          } 
        } 
      } 
      if (j == 0)
        return false; 
      if (paramInt != 130)
        j = -j; 
      doScrollY(j);
    } 
    if (view1 != null && view1.isFocused() && isOffScreen(view1)) {
      paramInt = getDescendantFocusability();
      setDescendantFocusability(131072);
      requestFocus();
      setDescendantFocusability(paramInt);
    } 
    return true;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public int computeHorizontalScrollExtent() { return super.computeHorizontalScrollExtent(); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public int computeHorizontalScrollOffset() { return super.computeHorizontalScrollOffset(); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public int computeHorizontalScrollRange() { return super.computeHorizontalScrollRange(); }
  
  public void computeScroll() {
    if (this.mScroller.computeScrollOffset()) {
      this.mScroller.getCurrX();
      int k = this.mScroller.getCurrY();
      int j = k - this.mLastScrollerY;
      int i = j;
      if (dispatchNestedPreScroll(0, j, this.mScrollConsumed, null, 1))
        i = j - this.mScrollConsumed[1]; 
      if (i != 0) {
        j = getScrollRange();
        int m = getScrollY();
        overScrollByCompat(0, i, getScrollX(), m, 0, j, 0, 0, false);
        int n = getScrollY() - m;
        if (!dispatchNestedScroll(0, n, 0, i - n, null, 1)) {
          i = getOverScrollMode();
          if (i == 0 || (i == 1 && j > 0)) {
            i = 1;
          } else {
            i = 0;
          } 
          if (i != 0) {
            ensureGlows();
            if (k <= 0 && m > 0) {
              this.mEdgeGlowTop.onAbsorb((int)this.mScroller.getCurrVelocity());
            } else if (k >= j && m < j) {
              this.mEdgeGlowBottom.onAbsorb((int)this.mScroller.getCurrVelocity());
            } 
          } 
        } 
      } 
      this.mLastScrollerY = k;
      ViewCompat.postInvalidateOnAnimation(this);
      return;
    } 
    if (hasNestedScrollingParent(1))
      stopNestedScroll(1); 
    this.mLastScrollerY = 0;
  }
  
  protected int computeScrollDeltaToGetChildRectOnScreen(Rect paramRect) {
    int i = getChildCount();
    int m = 0;
    if (i == 0)
      return 0; 
    int n = getHeight();
    i = getScrollY();
    int k = i + n;
    int i1 = getVerticalFadingEdgeLength();
    int j = i;
    if (paramRect.top > 0)
      j = i + i1; 
    i = k;
    if (paramRect.bottom < getChildAt(0).getHeight())
      i = k - i1; 
    if (paramRect.bottom > i && paramRect.top > j) {
      if (paramRect.height() > n) {
        j = paramRect.top - j + 0;
      } else {
        j = paramRect.bottom - i + 0;
      } 
      return Math.min(j, getChildAt(0).getBottom() - i);
    } 
    k = m;
    if (paramRect.top < j) {
      k = m;
      if (paramRect.bottom < i) {
        if (paramRect.height() > n) {
          i = 0 - i - paramRect.bottom;
        } else {
          i = 0 - j - paramRect.top;
        } 
        k = Math.max(i, -getScrollY());
      } 
    } 
    return k;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public int computeVerticalScrollExtent() { return super.computeVerticalScrollExtent(); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public int computeVerticalScrollOffset() { return Math.max(0, super.computeVerticalScrollOffset()); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public int computeVerticalScrollRange() {
    int j = getChildCount();
    int i = getHeight() - getPaddingBottom() - getPaddingTop();
    if (j == 0)
      return i; 
    j = getChildAt(0).getBottom();
    int k = getScrollY();
    int m = Math.max(0, j - i);
    if (k < 0)
      return j - k; 
    i = j;
    if (k > m)
      i = j + k - m; 
    return i;
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) { return (super.dispatchKeyEvent(paramKeyEvent) || executeKeyEvent(paramKeyEvent)); }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean) { return this.mChildHelper.dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean); }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2) { return this.mChildHelper.dispatchNestedPreFling(paramFloat1, paramFloat2); }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2) { return this.mChildHelper.dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2); }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3) { return this.mChildHelper.dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2, paramInt3); }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) { return this.mChildHelper.dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt); }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5) { return this.mChildHelper.dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5); }
  
  public void draw(Canvas paramCanvas) { // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial draw : (Landroid/graphics/Canvas;)V
    //   5: aload_0
    //   6: getfield mEdgeGlowTop : Landroid/widget/EdgeEffect;
    //   9: ifnull -> 385
    //   12: aload_0
    //   13: invokevirtual getScrollY : ()I
    //   16: istore #9
    //   18: aload_0
    //   19: getfield mEdgeGlowTop : Landroid/widget/EdgeEffect;
    //   22: invokevirtual isFinished : ()Z
    //   25: istore #11
    //   27: iconst_0
    //   28: istore #6
    //   30: iload #11
    //   32: ifne -> 196
    //   35: aload_1
    //   36: invokevirtual save : ()I
    //   39: istore #10
    //   41: aload_0
    //   42: invokevirtual getWidth : ()I
    //   45: istore_2
    //   46: aload_0
    //   47: invokevirtual getHeight : ()I
    //   50: istore #8
    //   52: iconst_0
    //   53: iload #9
    //   55: invokestatic min : (II)I
    //   58: istore #7
    //   60: getstatic android/os/Build$VERSION.SDK_INT : I
    //   63: bipush #21
    //   65: if_icmplt -> 83
    //   68: aload_0
    //   69: invokevirtual getClipToPadding : ()Z
    //   72: ifeq -> 78
    //   75: goto -> 83
    //   78: iconst_0
    //   79: istore_3
    //   80: goto -> 102
    //   83: iload_2
    //   84: aload_0
    //   85: invokevirtual getPaddingLeft : ()I
    //   88: aload_0
    //   89: invokevirtual getPaddingRight : ()I
    //   92: iadd
    //   93: isub
    //   94: istore_2
    //   95: aload_0
    //   96: invokevirtual getPaddingLeft : ()I
    //   99: iconst_0
    //   100: iadd
    //   101: istore_3
    //   102: iload #8
    //   104: istore #5
    //   106: iload #7
    //   108: istore #4
    //   110: getstatic android/os/Build$VERSION.SDK_INT : I
    //   113: bipush #21
    //   115: if_icmplt -> 156
    //   118: iload #8
    //   120: istore #5
    //   122: iload #7
    //   124: istore #4
    //   126: aload_0
    //   127: invokevirtual getClipToPadding : ()Z
    //   130: ifeq -> 156
    //   133: iload #8
    //   135: aload_0
    //   136: invokevirtual getPaddingTop : ()I
    //   139: aload_0
    //   140: invokevirtual getPaddingBottom : ()I
    //   143: iadd
    //   144: isub
    //   145: istore #5
    //   147: iload #7
    //   149: aload_0
    //   150: invokevirtual getPaddingTop : ()I
    //   153: iadd
    //   154: istore #4
    //   156: aload_1
    //   157: iload_3
    //   158: i2f
    //   159: iload #4
    //   161: i2f
    //   162: invokevirtual translate : (FF)V
    //   165: aload_0
    //   166: getfield mEdgeGlowTop : Landroid/widget/EdgeEffect;
    //   169: iload_2
    //   170: iload #5
    //   172: invokevirtual setSize : (II)V
    //   175: aload_0
    //   176: getfield mEdgeGlowTop : Landroid/widget/EdgeEffect;
    //   179: aload_1
    //   180: invokevirtual draw : (Landroid/graphics/Canvas;)Z
    //   183: ifeq -> 190
    //   186: aload_0
    //   187: invokestatic postInvalidateOnAnimation : (Landroid/view/View;)V
    //   190: aload_1
    //   191: iload #10
    //   193: invokevirtual restoreToCount : (I)V
    //   196: aload_0
    //   197: getfield mEdgeGlowBottom : Landroid/widget/EdgeEffect;
    //   200: invokevirtual isFinished : ()Z
    //   203: ifne -> 385
    //   206: aload_1
    //   207: invokevirtual save : ()I
    //   210: istore #10
    //   212: aload_0
    //   213: invokevirtual getWidth : ()I
    //   216: istore #4
    //   218: aload_0
    //   219: invokevirtual getHeight : ()I
    //   222: istore #7
    //   224: aload_0
    //   225: invokevirtual getScrollRange : ()I
    //   228: iload #9
    //   230: invokestatic max : (II)I
    //   233: iload #7
    //   235: iadd
    //   236: istore #8
    //   238: getstatic android/os/Build$VERSION.SDK_INT : I
    //   241: bipush #21
    //   243: if_icmplt -> 259
    //   246: iload #6
    //   248: istore_3
    //   249: iload #4
    //   251: istore_2
    //   252: aload_0
    //   253: invokevirtual getClipToPadding : ()Z
    //   256: ifeq -> 279
    //   259: iload #4
    //   261: aload_0
    //   262: invokevirtual getPaddingLeft : ()I
    //   265: aload_0
    //   266: invokevirtual getPaddingRight : ()I
    //   269: iadd
    //   270: isub
    //   271: istore_2
    //   272: iconst_0
    //   273: aload_0
    //   274: invokevirtual getPaddingLeft : ()I
    //   277: iadd
    //   278: istore_3
    //   279: iload #8
    //   281: istore #5
    //   283: iload #7
    //   285: istore #4
    //   287: getstatic android/os/Build$VERSION.SDK_INT : I
    //   290: bipush #21
    //   292: if_icmplt -> 333
    //   295: iload #8
    //   297: istore #5
    //   299: iload #7
    //   301: istore #4
    //   303: aload_0
    //   304: invokevirtual getClipToPadding : ()Z
    //   307: ifeq -> 333
    //   310: iload #7
    //   312: aload_0
    //   313: invokevirtual getPaddingTop : ()I
    //   316: aload_0
    //   317: invokevirtual getPaddingBottom : ()I
    //   320: iadd
    //   321: isub
    //   322: istore #4
    //   324: iload #8
    //   326: aload_0
    //   327: invokevirtual getPaddingBottom : ()I
    //   330: isub
    //   331: istore #5
    //   333: aload_1
    //   334: iload_3
    //   335: iload_2
    //   336: isub
    //   337: i2f
    //   338: iload #5
    //   340: i2f
    //   341: invokevirtual translate : (FF)V
    //   344: aload_1
    //   345: ldc_w 180.0
    //   348: iload_2
    //   349: i2f
    //   350: fconst_0
    //   351: invokevirtual rotate : (FFF)V
    //   354: aload_0
    //   355: getfield mEdgeGlowBottom : Landroid/widget/EdgeEffect;
    //   358: iload_2
    //   359: iload #4
    //   361: invokevirtual setSize : (II)V
    //   364: aload_0
    //   365: getfield mEdgeGlowBottom : Landroid/widget/EdgeEffect;
    //   368: aload_1
    //   369: invokevirtual draw : (Landroid/graphics/Canvas;)Z
    //   372: ifeq -> 379
    //   375: aload_0
    //   376: invokestatic postInvalidateOnAnimation : (Landroid/view/View;)V
    //   379: aload_1
    //   380: iload #10
    //   382: invokevirtual restoreToCount : (I)V
    //   385: return }
  
  public boolean executeKeyEvent(@NonNull KeyEvent paramKeyEvent) {
    View view;
    this.mTempRect.setEmpty();
    boolean bool = canScroll();
    boolean bool1 = false;
    char c = '';
    if (!bool) {
      if (isFocused() && paramKeyEvent.getKeyCode() != 4) {
        View view1 = findFocus();
        view = view1;
        if (view1 == this)
          view = null; 
        view = FocusFinder.getInstance().findNextFocus(this, view, 130);
        bool = bool1;
        if (view != null) {
          bool = bool1;
          if (view != this) {
            bool = bool1;
            if (view.requestFocus(130))
              bool = true; 
          } 
        } 
        return bool;
      } 
      return false;
    } 
    if (view.getAction() == 0) {
      int i = view.getKeyCode();
      if (i != 62) {
        switch (i) {
          default:
            return false;
          case 20:
            return !view.isAltPressed() ? arrowScroll(130) : fullScroll(130);
          case 19:
            break;
        } 
        return !view.isAltPressed() ? arrowScroll(33) : fullScroll(33);
      } 
      if (view.isShiftPressed())
        c = '!'; 
      pageScroll(c);
    } 
    return false;
  }
  
  public void fling(int paramInt) {
    if (getChildCount() > 0) {
      startNestedScroll(2, 1);
      this.mScroller.fling(getScrollX(), getScrollY(), 0, paramInt, 0, 0, -2147483648, 2147483647, 0, 0);
      this.mLastScrollerY = getScrollY();
      ViewCompat.postInvalidateOnAnimation(this);
    } 
  }
  
  public boolean fullScroll(int paramInt) {
    int i;
    if (paramInt == 130) {
      i = 1;
    } else {
      i = 0;
    } 
    int j = getHeight();
    this.mTempRect.top = 0;
    this.mTempRect.bottom = j;
    if (i) {
      i = getChildCount();
      if (i > 0) {
        View view = getChildAt(i - 1);
        this.mTempRect.bottom = view.getBottom() + getPaddingBottom();
        this.mTempRect.top = this.mTempRect.bottom - j;
      } 
    } 
    return scrollAndFocus(paramInt, this.mTempRect.top, this.mTempRect.bottom);
  }
  
  protected float getBottomFadingEdgeStrength() {
    if (getChildCount() == 0)
      return 0.0F; 
    int i = getVerticalFadingEdgeLength();
    int j = getHeight();
    int k = getPaddingBottom();
    j = getChildAt(0).getBottom() - getScrollY() - j - k;
    return (j < i) ? (j / i) : 1.0F;
  }
  
  public int getMaxScrollAmount() { return (int)(getHeight() * 0.5F); }
  
  public int getNestedScrollAxes() { return this.mParentHelper.getNestedScrollAxes(); }
  
  int getScrollRange() {
    int j = getChildCount();
    int i = 0;
    if (j > 0)
      i = Math.max(0, getChildAt(0).getHeight() - getHeight() - getPaddingBottom() - getPaddingTop()); 
    return i;
  }
  
  protected float getTopFadingEdgeStrength() {
    if (getChildCount() == 0)
      return 0.0F; 
    int i = getVerticalFadingEdgeLength();
    int j = getScrollY();
    return (j < i) ? (j / i) : 1.0F;
  }
  
  public boolean hasNestedScrollingParent() { return this.mChildHelper.hasNestedScrollingParent(); }
  
  public boolean hasNestedScrollingParent(int paramInt) { return this.mChildHelper.hasNestedScrollingParent(paramInt); }
  
  public boolean isFillViewport() { return this.mFillViewport; }
  
  public boolean isNestedScrollingEnabled() { return this.mChildHelper.isNestedScrollingEnabled(); }
  
  public boolean isSmoothScrollingEnabled() { return this.mSmoothScrollingEnabled; }
  
  protected void measureChild(View paramView, int paramInt1, int paramInt2) {
    ViewGroup.LayoutParams layoutParams = paramView.getLayoutParams();
    paramView.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight(), layoutParams.width), View.MeasureSpec.makeMeasureSpec(0, 0));
  }
  
  protected void measureChildWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    paramView.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + paramInt2, marginLayoutParams.width), View.MeasureSpec.makeMeasureSpec(marginLayoutParams.topMargin + marginLayoutParams.bottomMargin, 0));
  }
  
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    this.mIsLaidOut = false;
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent) {
    if ((paramMotionEvent.getSource() & 0x2) != 0) {
      if (paramMotionEvent.getAction() != 8)
        return false; 
      if (!this.mIsBeingDragged) {
        float f = paramMotionEvent.getAxisValue(9);
        if (f != 0.0F) {
          int i = (int)(f * getVerticalScrollFactorCompat());
          int j = getScrollRange();
          int m = getScrollY();
          int k = m - i;
          if (k < 0) {
            i = 0;
          } else {
            i = k;
            if (k > j)
              i = j; 
          } 
          if (i != m) {
            super.scrollTo(getScrollX(), i);
            return true;
          } 
        } 
      } 
    } 
    return false;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
    ViewParent viewParent;
    int i = paramMotionEvent.getAction();
    if (i == 2 && this.mIsBeingDragged)
      return true; 
    i &= 0xFF;
    if (i != 6) {
      switch (i) {
        default:
          return this.mIsBeingDragged;
        case 2:
          i = this.mActivePointerId;
          if (i != -1) {
            int j = paramMotionEvent.findPointerIndex(i);
            if (j == -1) {
              viewParent = new StringBuilder();
              viewParent.append("Invalid pointerId=");
              viewParent.append(i);
              viewParent.append(" in onInterceptTouchEvent");
              Log.e("NestedScrollView", viewParent.toString());
            } else {
              i = (int)viewParent.getY(j);
              if (Math.abs(i - this.mLastMotionY) > this.mTouchSlop && (0x2 & getNestedScrollAxes()) == 0) {
                this.mIsBeingDragged = true;
                this.mLastMotionY = i;
                initVelocityTrackerIfNotExists();
                this.mVelocityTracker.addMovement(viewParent);
                this.mNestedYOffset = 0;
                viewParent = getParent();
                if (viewParent != null)
                  viewParent.requestDisallowInterceptTouchEvent(true); 
              } 
            } 
          } 
        case 1:
        case 3:
          this.mIsBeingDragged = false;
          this.mActivePointerId = -1;
          recycleVelocityTracker();
          if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange()))
            ViewCompat.postInvalidateOnAnimation(this); 
          stopNestedScroll(0);
        case 0:
          break;
      } 
      i = (int)viewParent.getY();
      if (!inChild((int)viewParent.getX(), i)) {
        this.mIsBeingDragged = false;
        recycleVelocityTracker();
      } 
      this.mLastMotionY = i;
      this.mActivePointerId = viewParent.getPointerId(0);
      initOrResetVelocityTracker();
      this.mVelocityTracker.addMovement(viewParent);
      this.mScroller.computeScrollOffset();
      this.mIsBeingDragged = this.mScroller.isFinished() ^ true;
      startNestedScroll(2, 0);
    } 
    onSecondaryPointerUp(viewParent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.mIsLayoutDirty = false;
    if (this.mChildToScrollTo != null && isViewDescendantOf(this.mChildToScrollTo, this))
      scrollToChild(this.mChildToScrollTo); 
    this.mChildToScrollTo = null;
    if (!this.mIsLaidOut) {
      if (this.mSavedState != null) {
        scrollTo(getScrollX(), this.mSavedState.scrollPosition);
        this.mSavedState = null;
      } 
      if (getChildCount() > 0) {
        paramInt1 = getChildAt(0).getMeasuredHeight();
      } else {
        paramInt1 = 0;
      } 
      paramInt1 = Math.max(0, paramInt1 - paramInt4 - paramInt2 - getPaddingBottom() - getPaddingTop());
      if (getScrollY() > paramInt1) {
        scrollTo(getScrollX(), paramInt1);
      } else if (getScrollY() < 0) {
        scrollTo(getScrollX(), 0);
      } 
    } 
    scrollTo(getScrollX(), getScrollY());
    this.mIsLaidOut = true;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    super.onMeasure(paramInt1, paramInt2);
    if (!this.mFillViewport)
      return; 
    if (View.MeasureSpec.getMode(paramInt2) == 0)
      return; 
    if (getChildCount() > 0) {
      View view = getChildAt(0);
      paramInt2 = getMeasuredHeight();
      if (view.getMeasuredHeight() < paramInt2) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
        view.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight(), layoutParams.width), View.MeasureSpec.makeMeasureSpec(paramInt2 - getPaddingTop() - getPaddingBottom(), 1073741824));
      } 
    } 
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean) {
    if (!paramBoolean) {
      flingWithNestedDispatch((int)paramFloat2);
      return true;
    } 
    return false;
  }
  
  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2) { return dispatchNestedPreFling(paramFloat1, paramFloat2); }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt) { dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt, null); }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramInt1 = getScrollY();
    scrollBy(0, paramInt4);
    paramInt1 = getScrollY() - paramInt1;
    dispatchNestedScroll(0, paramInt1, 0, paramInt4 - paramInt1, null);
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt) {
    this.mParentHelper.onNestedScrollAccepted(paramView1, paramView2, paramInt);
    startNestedScroll(2);
  }
  
  protected void onOverScrolled(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) { super.scrollTo(paramInt1, paramInt2); }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect) {
    View view;
    int i;
    if (paramInt == 2) {
      i = 130;
    } else {
      i = paramInt;
      if (paramInt == 1)
        i = 33; 
    } 
    if (paramRect == null) {
      view = FocusFinder.getInstance().findNextFocus(this, null, i);
    } else {
      view = FocusFinder.getInstance().findNextFocusFromRect(this, paramRect, i);
    } 
    return (view == null) ? false : (isOffScreen(view) ? false : view.requestFocus(i, paramRect));
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable) {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
      return;
    } 
    SavedState savedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(savedState.getSuperState());
    this.mSavedState = savedState;
    requestLayout();
  }
  
  protected Parcelable onSaveInstanceState() {
    SavedState savedState = new SavedState(super.onSaveInstanceState());
    savedState.scrollPosition = getScrollY();
    return savedState;
  }
  
  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mOnScrollChangeListener != null)
      this.mOnScrollChangeListener.onScrollChange(this, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    View view = findFocus();
    if (view != null) {
      if (this == view)
        return; 
      if (isWithinDeltaOfScreen(view, 0, paramInt4)) {
        view.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(view, this.mTempRect);
        doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
      } 
      return;
    } 
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt) { return ((paramInt & 0x2) != 0); }
  
  public void onStopNestedScroll(View paramView) {
    this.mParentHelper.onStopNestedScroll(paramView);
    stopNestedScroll();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) {
    boolean bool;
    int m;
    int k;
    int j;
    VelocityTracker velocityTracker;
    StringBuilder stringBuilder;
    initVelocityTrackerIfNotExists();
    MotionEvent motionEvent = MotionEvent.obtain(paramMotionEvent);
    int i = paramMotionEvent.getActionMasked();
    if (i == 0)
      this.mNestedYOffset = 0; 
    motionEvent.offsetLocation(0.0F, this.mNestedYOffset);
    switch (i) {
      case 6:
        onSecondaryPointerUp(paramMotionEvent);
        this.mLastMotionY = (int)paramMotionEvent.getY(paramMotionEvent.findPointerIndex(this.mActivePointerId));
        break;
      case 5:
        i = paramMotionEvent.getActionIndex();
        this.mLastMotionY = (int)paramMotionEvent.getY(i);
        this.mActivePointerId = paramMotionEvent.getPointerId(i);
        break;
      case 3:
        if (this.mIsBeingDragged && getChildCount() > 0 && this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange()))
          ViewCompat.postInvalidateOnAnimation(this); 
        this.mActivePointerId = -1;
        endDrag();
        break;
      case 2:
        k = paramMotionEvent.findPointerIndex(this.mActivePointerId);
        if (k == -1) {
          stringBuilder = new StringBuilder();
          stringBuilder.append("Invalid pointerId=");
          stringBuilder.append(this.mActivePointerId);
          stringBuilder.append(" in onTouchEvent");
          Log.e("NestedScrollView", stringBuilder.toString());
          break;
        } 
        m = (int)stringBuilder.getY(k);
        i = this.mLastMotionY - m;
        j = i;
        if (dispatchNestedPreScroll(0, i, this.mScrollConsumed, this.mScrollOffset, 0)) {
          j = i - this.mScrollConsumed[1];
          motionEvent.offsetLocation(0.0F, this.mScrollOffset[1]);
          this.mNestedYOffset += this.mScrollOffset[1];
        } 
        i = j;
        if (!this.mIsBeingDragged) {
          i = j;
          if (Math.abs(j) > this.mTouchSlop) {
            ViewParent viewParent = getParent();
            if (viewParent != null)
              viewParent.requestDisallowInterceptTouchEvent(true); 
            this.mIsBeingDragged = true;
            if (j > 0) {
              i = j - this.mTouchSlop;
            } else {
              i = j + this.mTouchSlop;
            } 
          } 
        } 
        if (this.mIsBeingDragged) {
          this.mLastMotionY = m - this.mScrollOffset[1];
          int n = getScrollY();
          m = getScrollRange();
          j = getOverScrollMode();
          if (j == 0 || (j == 1 && m > 0)) {
            j = 1;
          } else {
            j = 0;
          } 
          if (overScrollByCompat(0, i, 0, getScrollY(), 0, m, 0, 0, true) && !hasNestedScrollingParent(0))
            this.mVelocityTracker.clear(); 
          int i1 = getScrollY() - n;
          if (dispatchNestedScroll(0, i1, 0, i - i1, this.mScrollOffset, 0)) {
            this.mLastMotionY -= this.mScrollOffset[1];
            motionEvent.offsetLocation(0.0F, this.mScrollOffset[1]);
            this.mNestedYOffset += this.mScrollOffset[1];
            break;
          } 
          if (j != 0) {
            ensureGlows();
            j = n + i;
            if (j < 0) {
              EdgeEffectCompat.onPull(this.mEdgeGlowTop, i / getHeight(), stringBuilder.getX(k) / getWidth());
              if (!this.mEdgeGlowBottom.isFinished())
                this.mEdgeGlowBottom.onRelease(); 
            } else if (j > m) {
              EdgeEffectCompat.onPull(this.mEdgeGlowBottom, i / getHeight(), 1.0F - stringBuilder.getX(k) / getWidth());
              if (!this.mEdgeGlowTop.isFinished())
                this.mEdgeGlowTop.onRelease(); 
            } 
            if (this.mEdgeGlowTop != null && (!this.mEdgeGlowTop.isFinished() || !this.mEdgeGlowBottom.isFinished()))
              ViewCompat.postInvalidateOnAnimation(this); 
          } 
        } 
        break;
      case 1:
        velocityTracker = this.mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
        i = (int)velocityTracker.getYVelocity(this.mActivePointerId);
        if (Math.abs(i) > this.mMinimumVelocity) {
          flingWithNestedDispatch(-i);
        } else if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
          ViewCompat.postInvalidateOnAnimation(this);
        } 
        this.mActivePointerId = -1;
        endDrag();
        break;
      case 0:
        if (getChildCount() == 0)
          return false; 
        bool = this.mScroller.isFinished() ^ true;
        this.mIsBeingDragged = bool;
        if (bool) {
          ViewParent viewParent = getParent();
          if (viewParent != null)
            viewParent.requestDisallowInterceptTouchEvent(true); 
        } 
        if (!this.mScroller.isFinished())
          this.mScroller.abortAnimation(); 
        this.mLastMotionY = (int)velocityTracker.getY();
        this.mActivePointerId = velocityTracker.getPointerId(0);
        startNestedScroll(2, 0);
        break;
    } 
    if (this.mVelocityTracker != null)
      this.mVelocityTracker.addMovement(motionEvent); 
    motionEvent.recycle();
    return true;
  }
  
  boolean overScrollByCompat(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean) { // Byte code:
    //   0: aload_0
    //   1: invokevirtual getOverScrollMode : ()I
    //   4: istore #12
    //   6: aload_0
    //   7: invokevirtual computeHorizontalScrollRange : ()I
    //   10: istore #10
    //   12: aload_0
    //   13: invokevirtual computeHorizontalScrollExtent : ()I
    //   16: istore #11
    //   18: iconst_0
    //   19: istore #14
    //   21: iload #10
    //   23: iload #11
    //   25: if_icmple -> 34
    //   28: iconst_1
    //   29: istore #10
    //   31: goto -> 37
    //   34: iconst_0
    //   35: istore #10
    //   37: aload_0
    //   38: invokevirtual computeVerticalScrollRange : ()I
    //   41: aload_0
    //   42: invokevirtual computeVerticalScrollExtent : ()I
    //   45: if_icmple -> 54
    //   48: iconst_1
    //   49: istore #11
    //   51: goto -> 57
    //   54: iconst_0
    //   55: istore #11
    //   57: iload #12
    //   59: ifeq -> 82
    //   62: iload #12
    //   64: iconst_1
    //   65: if_icmpne -> 76
    //   68: iload #10
    //   70: ifeq -> 76
    //   73: goto -> 82
    //   76: iconst_0
    //   77: istore #10
    //   79: goto -> 85
    //   82: iconst_1
    //   83: istore #10
    //   85: iload #12
    //   87: ifeq -> 110
    //   90: iload #12
    //   92: iconst_1
    //   93: if_icmpne -> 104
    //   96: iload #11
    //   98: ifeq -> 104
    //   101: goto -> 110
    //   104: iconst_0
    //   105: istore #11
    //   107: goto -> 113
    //   110: iconst_1
    //   111: istore #11
    //   113: iload_3
    //   114: iload_1
    //   115: iadd
    //   116: istore_3
    //   117: iload #10
    //   119: ifne -> 127
    //   122: iconst_0
    //   123: istore_1
    //   124: goto -> 130
    //   127: iload #7
    //   129: istore_1
    //   130: iload #4
    //   132: iload_2
    //   133: iadd
    //   134: istore #4
    //   136: iload #11
    //   138: ifne -> 146
    //   141: iconst_0
    //   142: istore_2
    //   143: goto -> 149
    //   146: iload #8
    //   148: istore_2
    //   149: iload_1
    //   150: ineg
    //   151: istore #7
    //   153: iload_1
    //   154: iload #5
    //   156: iadd
    //   157: istore_1
    //   158: iload_2
    //   159: ineg
    //   160: istore #5
    //   162: iload_2
    //   163: iload #6
    //   165: iadd
    //   166: istore #6
    //   168: iload_3
    //   169: iload_1
    //   170: if_icmple -> 181
    //   173: iconst_1
    //   174: istore #9
    //   176: iload_1
    //   177: istore_2
    //   178: goto -> 198
    //   181: iload_3
    //   182: iload #7
    //   184: if_icmpge -> 193
    //   187: iload #7
    //   189: istore_1
    //   190: goto -> 173
    //   193: iload_3
    //   194: istore_2
    //   195: iconst_0
    //   196: istore #9
    //   198: iload #4
    //   200: iload #6
    //   202: if_icmple -> 214
    //   205: iload #6
    //   207: istore_1
    //   208: iconst_1
    //   209: istore #13
    //   211: goto -> 233
    //   214: iload #4
    //   216: iload #5
    //   218: if_icmpge -> 227
    //   221: iload #5
    //   223: istore_1
    //   224: goto -> 208
    //   227: iload #4
    //   229: istore_1
    //   230: iconst_0
    //   231: istore #13
    //   233: iload #13
    //   235: ifeq -> 263
    //   238: aload_0
    //   239: iconst_1
    //   240: invokevirtual hasNestedScrollingParent : (I)Z
    //   243: ifne -> 263
    //   246: aload_0
    //   247: getfield mScroller : Landroid/widget/OverScroller;
    //   250: iload_2
    //   251: iload_1
    //   252: iconst_0
    //   253: iconst_0
    //   254: iconst_0
    //   255: aload_0
    //   256: invokevirtual getScrollRange : ()I
    //   259: invokevirtual springBack : (IIIIII)Z
    //   262: pop
    //   263: aload_0
    //   264: iload_2
    //   265: iload_1
    //   266: iload #9
    //   268: iload #13
    //   270: invokevirtual onOverScrolled : (IIZZ)V
    //   273: iload #9
    //   275: ifne -> 287
    //   278: iload #14
    //   280: istore #9
    //   282: iload #13
    //   284: ifeq -> 290
    //   287: iconst_1
    //   288: istore #9
    //   290: iload #9
    //   292: ireturn }
  
  public boolean pageScroll(int paramInt) {
    int i;
    if (paramInt == 130) {
      i = 1;
    } else {
      i = 0;
    } 
    int j = getHeight();
    if (i) {
      this.mTempRect.top = getScrollY() + j;
      i = getChildCount();
      if (i > 0) {
        View view = getChildAt(i - 1);
        if (this.mTempRect.top + j > view.getBottom())
          this.mTempRect.top = view.getBottom() - j; 
      } 
    } else {
      this.mTempRect.top = getScrollY() - j;
      if (this.mTempRect.top < 0)
        this.mTempRect.top = 0; 
    } 
    this.mTempRect.bottom = this.mTempRect.top + j;
    return scrollAndFocus(paramInt, this.mTempRect.top, this.mTempRect.bottom);
  }
  
  public void requestChildFocus(View paramView1, View paramView2) {
    if (!this.mIsLayoutDirty) {
      scrollToChild(paramView2);
    } else {
      this.mChildToScrollTo = paramView2;
    } 
    super.requestChildFocus(paramView1, paramView2);
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean) {
    paramRect.offset(paramView.getLeft() - paramView.getScrollX(), paramView.getTop() - paramView.getScrollY());
    return scrollToChildRect(paramRect, paramBoolean);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean) {
    if (paramBoolean)
      recycleVelocityTracker(); 
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  public void requestLayout() {
    this.mIsLayoutDirty = true;
    super.requestLayout();
  }
  
  public void scrollTo(int paramInt1, int paramInt2) {
    if (getChildCount() > 0) {
      View view = getChildAt(0);
      paramInt1 = clamp(paramInt1, getWidth() - getPaddingRight() - getPaddingLeft(), view.getWidth());
      paramInt2 = clamp(paramInt2, getHeight() - getPaddingBottom() - getPaddingTop(), view.getHeight());
      if (paramInt1 != getScrollX() || paramInt2 != getScrollY())
        super.scrollTo(paramInt1, paramInt2); 
    } 
  }
  
  public void setFillViewport(boolean paramBoolean) {
    if (paramBoolean != this.mFillViewport) {
      this.mFillViewport = paramBoolean;
      requestLayout();
    } 
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean) { this.mChildHelper.setNestedScrollingEnabled(paramBoolean); }
  
  public void setOnScrollChangeListener(@Nullable OnScrollChangeListener paramOnScrollChangeListener) { this.mOnScrollChangeListener = paramOnScrollChangeListener; }
  
  public void setSmoothScrollingEnabled(boolean paramBoolean) { this.mSmoothScrollingEnabled = paramBoolean; }
  
  public boolean shouldDelayChildPressedState() { return true; }
  
  public final void smoothScrollBy(int paramInt1, int paramInt2) {
    if (getChildCount() == 0)
      return; 
    if (AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll > 250L) {
      paramInt1 = getHeight();
      int i = getPaddingBottom();
      int j = getPaddingTop();
      i = Math.max(0, getChildAt(0).getHeight() - paramInt1 - i - j);
      paramInt1 = getScrollY();
      paramInt2 = Math.max(0, Math.min(paramInt2 + paramInt1, i));
      this.mScroller.startScroll(getScrollX(), paramInt1, 0, paramInt2 - paramInt1);
      ViewCompat.postInvalidateOnAnimation(this);
    } else {
      if (!this.mScroller.isFinished())
        this.mScroller.abortAnimation(); 
      scrollBy(paramInt1, paramInt2);
    } 
    this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
  }
  
  public final void smoothScrollTo(int paramInt1, int paramInt2) { smoothScrollBy(paramInt1 - getScrollX(), paramInt2 - getScrollY()); }
  
  public boolean startNestedScroll(int paramInt) { return this.mChildHelper.startNestedScroll(paramInt); }
  
  public boolean startNestedScroll(int paramInt1, int paramInt2) { return this.mChildHelper.startNestedScroll(paramInt1, paramInt2); }
  
  public void stopNestedScroll() { this.mChildHelper.stopNestedScroll(); }
  
  public void stopNestedScroll(int paramInt) { this.mChildHelper.stopNestedScroll(paramInt); }
  
  static class AccessibilityDelegate extends AccessibilityDelegateCompat {
    public void onInitializeAccessibilityEvent(View param1View, AccessibilityEvent param1AccessibilityEvent) {
      boolean bool;
      super.onInitializeAccessibilityEvent(param1View, param1AccessibilityEvent);
      NestedScrollView nestedScrollView = (NestedScrollView)param1View;
      param1AccessibilityEvent.setClassName(android.widget.ScrollView.class.getName());
      if (nestedScrollView.getScrollRange() > 0) {
        bool = true;
      } else {
        bool = false;
      } 
      param1AccessibilityEvent.setScrollable(bool);
      param1AccessibilityEvent.setScrollX(nestedScrollView.getScrollX());
      param1AccessibilityEvent.setScrollY(nestedScrollView.getScrollY());
      AccessibilityRecordCompat.setMaxScrollX(param1AccessibilityEvent, nestedScrollView.getScrollX());
      AccessibilityRecordCompat.setMaxScrollY(param1AccessibilityEvent, nestedScrollView.getScrollRange());
    }
    
    public void onInitializeAccessibilityNodeInfo(View param1View, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) {
      super.onInitializeAccessibilityNodeInfo(param1View, param1AccessibilityNodeInfoCompat);
      NestedScrollView nestedScrollView = (NestedScrollView)param1View;
      param1AccessibilityNodeInfoCompat.setClassName(android.widget.ScrollView.class.getName());
      if (nestedScrollView.isEnabled()) {
        int i = nestedScrollView.getScrollRange();
        if (i > 0) {
          param1AccessibilityNodeInfoCompat.setScrollable(true);
          if (nestedScrollView.getScrollY() > 0)
            param1AccessibilityNodeInfoCompat.addAction(8192); 
          if (nestedScrollView.getScrollY() < i)
            param1AccessibilityNodeInfoCompat.addAction(4096); 
        } 
      } 
    }
    
    public boolean performAccessibilityAction(View param1View, int param1Int, Bundle param1Bundle) {
      if (super.performAccessibilityAction(param1View, param1Int, param1Bundle))
        return true; 
      NestedScrollView nestedScrollView = (NestedScrollView)param1View;
      if (!nestedScrollView.isEnabled())
        return false; 
      if (param1Int != 4096) {
        if (param1Int != 8192)
          return false; 
        param1Int = nestedScrollView.getHeight();
        int k = nestedScrollView.getPaddingBottom();
        int m = nestedScrollView.getPaddingTop();
        param1Int = Math.max(nestedScrollView.getScrollY() - param1Int - k - m, 0);
        if (param1Int != nestedScrollView.getScrollY()) {
          nestedScrollView.smoothScrollTo(0, param1Int);
          return true;
        } 
        return false;
      } 
      param1Int = nestedScrollView.getHeight();
      int i = nestedScrollView.getPaddingBottom();
      int j = nestedScrollView.getPaddingTop();
      param1Int = Math.min(nestedScrollView.getScrollY() + param1Int - i - j, nestedScrollView.getScrollRange());
      if (param1Int != nestedScrollView.getScrollY()) {
        nestedScrollView.smoothScrollTo(0, param1Int);
        return true;
      } 
      return false;
    }
  }
  
  public static interface OnScrollChangeListener {
    void onScrollChange(NestedScrollView param1NestedScrollView, int param1Int1, int param1Int2, int param1Int3, int param1Int4);
  }
  
  static class SavedState extends View.BaseSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
        public NestedScrollView.SavedState createFromParcel(Parcel param2Parcel) { return new NestedScrollView.SavedState(param2Parcel); }
        
        public NestedScrollView.SavedState[] newArray(int param2Int) { return new NestedScrollView.SavedState[param2Int]; }
      };
    
    public int scrollPosition;
    
    SavedState(Parcel param1Parcel) {
      super(param1Parcel);
      this.scrollPosition = param1Parcel.readInt();
    }
    
    SavedState(Parcelable param1Parcelable) { super(param1Parcelable); }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("HorizontalScrollView.SavedState{");
      stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      stringBuilder.append(" scrollPosition=");
      stringBuilder.append(this.scrollPosition);
      stringBuilder.append("}");
      return stringBuilder.toString();
    }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) {
      super.writeToParcel(param1Parcel, param1Int);
      param1Parcel.writeInt(this.scrollPosition);
    }
  }
  
  static final class null extends Object implements Parcelable.Creator<SavedState> {
    public NestedScrollView.SavedState createFromParcel(Parcel param1Parcel) { return new NestedScrollView.SavedState(param1Parcel); }
    
    public NestedScrollView.SavedState[] newArray(int param1Int) { return new NestedScrollView.SavedState[param1Int]; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/widget/NestedScrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */