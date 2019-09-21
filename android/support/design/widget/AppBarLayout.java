package android.support.design.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.R;
import android.support.v4.math.MathUtils;
import android.support.v4.util.ObjectsCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@DefaultBehavior(AppBarLayout.Behavior.class)
public class AppBarLayout extends LinearLayout {
  private static final int INVALID_SCROLL_RANGE = -1;
  
  static final int PENDING_ACTION_ANIMATE_ENABLED = 4;
  
  static final int PENDING_ACTION_COLLAPSED = 2;
  
  static final int PENDING_ACTION_EXPANDED = 1;
  
  static final int PENDING_ACTION_FORCE = 8;
  
  static final int PENDING_ACTION_NONE = 0;
  
  private boolean mCollapsed;
  
  private boolean mCollapsible;
  
  private int mDownPreScrollRange = -1;
  
  private int mDownScrollRange = -1;
  
  private boolean mHaveChildWithInterpolator;
  
  private WindowInsetsCompat mLastInsets;
  
  private List<OnOffsetChangedListener> mListeners;
  
  private int mPendingAction = 0;
  
  private int[] mTmpStatesArray;
  
  private int mTotalScrollRange = -1;
  
  public AppBarLayout(Context paramContext) { this(paramContext, null); }
  
  public AppBarLayout(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    setOrientation(1);
    ThemeUtils.checkAppCompatTheme(paramContext);
    if (Build.VERSION.SDK_INT >= 21) {
      ViewUtilsLollipop.setBoundsViewOutlineProvider(this);
      ViewUtilsLollipop.setStateListAnimatorFromAttrs(this, paramAttributeSet, 0, R.style.Widget_Design_AppBarLayout);
    } 
    TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AppBarLayout, 0, R.style.Widget_Design_AppBarLayout);
    ViewCompat.setBackground(this, typedArray.getDrawable(R.styleable.AppBarLayout_android_background));
    if (typedArray.hasValue(R.styleable.AppBarLayout_expanded))
      setExpanded(typedArray.getBoolean(R.styleable.AppBarLayout_expanded, false), false, false); 
    if (Build.VERSION.SDK_INT >= 21 && typedArray.hasValue(R.styleable.AppBarLayout_elevation))
      ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, typedArray.getDimensionPixelSize(R.styleable.AppBarLayout_elevation, 0)); 
    if (Build.VERSION.SDK_INT >= 26) {
      if (typedArray.hasValue(R.styleable.AppBarLayout_android_keyboardNavigationCluster))
        setKeyboardNavigationCluster(typedArray.getBoolean(R.styleable.AppBarLayout_android_keyboardNavigationCluster, false)); 
      if (typedArray.hasValue(R.styleable.AppBarLayout_android_touchscreenBlocksFocus))
        setTouchscreenBlocksFocus(typedArray.getBoolean(R.styleable.AppBarLayout_android_touchscreenBlocksFocus, false)); 
    } 
    typedArray.recycle();
    ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener() {
          public WindowInsetsCompat onApplyWindowInsets(View param1View, WindowInsetsCompat param1WindowInsetsCompat) { return AppBarLayout.this.onWindowInsetChanged(param1WindowInsetsCompat); }
        });
  }
  
  private void invalidateScrollRanges() {
    this.mTotalScrollRange = -1;
    this.mDownPreScrollRange = -1;
    this.mDownScrollRange = -1;
  }
  
  private boolean setCollapsibleState(boolean paramBoolean) {
    if (this.mCollapsible != paramBoolean) {
      this.mCollapsible = paramBoolean;
      refreshDrawableState();
      return true;
    } 
    return false;
  }
  
  private void setExpanded(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    byte b2;
    byte b1;
    if (paramBoolean1) {
      b1 = 1;
    } else {
      b1 = 2;
    } 
    byte b3 = 0;
    if (paramBoolean2) {
      b2 = 4;
    } else {
      b2 = 0;
    } 
    if (paramBoolean3)
      b3 = 8; 
    this.mPendingAction = b1 | b2 | b3;
    requestLayout();
  }
  
  private void updateCollapsible() {
    boolean bool;
    int i = getChildCount();
    boolean bool1 = false;
    byte b = 0;
    while (true) {
      bool = bool1;
      if (b < i) {
        if (((LayoutParams)getChildAt(b).getLayoutParams()).isCollapsible()) {
          bool = true;
          break;
        } 
        b++;
        continue;
      } 
      break;
    } 
    setCollapsibleState(bool);
  }
  
  public void addOnOffsetChangedListener(OnOffsetChangedListener paramOnOffsetChangedListener) {
    if (this.mListeners == null)
      this.mListeners = new ArrayList(); 
    if (paramOnOffsetChangedListener != null && !this.mListeners.contains(paramOnOffsetChangedListener))
      this.mListeners.add(paramOnOffsetChangedListener); 
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return paramLayoutParams instanceof LayoutParams; }
  
  void dispatchOffsetUpdates(int paramInt) {
    if (this.mListeners != null) {
      byte b = 0;
      int i = this.mListeners.size();
      while (b < i) {
        OnOffsetChangedListener onOffsetChangedListener = (OnOffsetChangedListener)this.mListeners.get(b);
        if (onOffsetChangedListener != null)
          onOffsetChangedListener.onOffsetChanged(this, paramInt); 
        b++;
      } 
    } 
  }
  
  protected LayoutParams generateDefaultLayoutParams() { return new LayoutParams(-1, -2); }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return new LayoutParams(getContext(), paramAttributeSet); }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (Build.VERSION.SDK_INT >= 19 && paramLayoutParams instanceof LinearLayout.LayoutParams) ? new LayoutParams((LinearLayout.LayoutParams)paramLayoutParams) : ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams) ? new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams) : new LayoutParams(paramLayoutParams)); }
  
  int getDownNestedPreScrollRange() {
    if (this.mDownPreScrollRange != -1)
      return this.mDownPreScrollRange; 
    int j = getChildCount() - 1;
    int k;
    for (k = 0; j >= 0; k = m) {
      View view = getChildAt(j);
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      int m = view.getMeasuredHeight();
      int n = layoutParams.mScrollFlags;
      if ((n & 0x5) == 5) {
        k += layoutParams.topMargin + layoutParams.bottomMargin;
        if ((n & 0x8) != 0) {
          m = k + ViewCompat.getMinimumHeight(view);
        } else if ((n & 0x2) != 0) {
          m = k + m - ViewCompat.getMinimumHeight(view);
        } else {
          m = k + m - getTopInset();
        } 
      } else {
        m = k;
        if (k > 0)
          break; 
      } 
      j--;
    } 
    int i = Math.max(0, k);
    this.mDownPreScrollRange = i;
    return i;
  }
  
  int getDownNestedScrollRange() {
    int j;
    if (this.mDownScrollRange != -1)
      return this.mDownScrollRange; 
    int k = getChildCount();
    byte b = 0;
    int i = 0;
    while (true) {
      j = i;
      if (b < k) {
        View view = getChildAt(b);
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        int n = view.getMeasuredHeight();
        int i1 = layoutParams.topMargin;
        int i2 = layoutParams.bottomMargin;
        int m = layoutParams.mScrollFlags;
        j = i;
        if ((m & true) != 0) {
          i += n + i1 + i2;
          if ((m & 0x2) != 0) {
            j = i - ViewCompat.getMinimumHeight(view) + getTopInset();
            break;
          } 
          b++;
          continue;
        } 
      } 
      break;
    } 
    i = Math.max(0, j);
    this.mDownScrollRange = i;
    return i;
  }
  
  final int getMinimumHeightForVisibleOverlappingContent() {
    int j = getTopInset();
    int i = ViewCompat.getMinimumHeight(this);
    if (i != 0)
      return i * 2 + j; 
    i = getChildCount();
    if (i >= 1) {
      i = ViewCompat.getMinimumHeight(getChildAt(i - 1));
    } else {
      i = 0;
    } 
    return (i != 0) ? (i * 2 + j) : (getHeight() / 3);
  }
  
  int getPendingAction() { return this.mPendingAction; }
  
  @Deprecated
  public float getTargetElevation() { return 0.0F; }
  
  @VisibleForTesting
  final int getTopInset() { return (this.mLastInsets != null) ? this.mLastInsets.getSystemWindowInsetTop() : 0; }
  
  public final int getTotalScrollRange() {
    int j;
    if (this.mTotalScrollRange != -1)
      return this.mTotalScrollRange; 
    int k = getChildCount();
    byte b = 0;
    int i = 0;
    while (true) {
      j = i;
      if (b < k) {
        View view = getChildAt(b);
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        int n = view.getMeasuredHeight();
        int m = layoutParams.mScrollFlags;
        j = i;
        if ((m & true) != 0) {
          i += n + layoutParams.topMargin + layoutParams.bottomMargin;
          if ((m & 0x2) != 0) {
            j = i - ViewCompat.getMinimumHeight(view);
            break;
          } 
          b++;
          continue;
        } 
      } 
      break;
    } 
    i = Math.max(0, j - getTopInset());
    this.mTotalScrollRange = i;
    return i;
  }
  
  int getUpNestedPreScrollRange() { return getTotalScrollRange(); }
  
  boolean hasChildWithInterpolator() { return this.mHaveChildWithInterpolator; }
  
  boolean hasScrollableChildren() { return (getTotalScrollRange() != 0); }
  
  protected int[] onCreateDrawableState(int paramInt) {
    if (this.mTmpStatesArray == null)
      this.mTmpStatesArray = new int[2]; 
    int[] arrayOfInt1 = this.mTmpStatesArray;
    int[] arrayOfInt2 = super.onCreateDrawableState(paramInt + arrayOfInt1.length);
    if (this.mCollapsible) {
      paramInt = R.attr.state_collapsible;
    } else {
      paramInt = -R.attr.state_collapsible;
    } 
    arrayOfInt1[0] = paramInt;
    if (this.mCollapsible && this.mCollapsed) {
      paramInt = R.attr.state_collapsed;
    } else {
      paramInt = -R.attr.state_collapsed;
    } 
    arrayOfInt1[1] = paramInt;
    return mergeDrawableStates(arrayOfInt2, arrayOfInt1);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    invalidateScrollRanges();
    paramInt1 = 0;
    this.mHaveChildWithInterpolator = false;
    paramInt2 = getChildCount();
    while (paramInt1 < paramInt2) {
      if (((LayoutParams)getChildAt(paramInt1).getLayoutParams()).getScrollInterpolator() != null) {
        this.mHaveChildWithInterpolator = true;
        break;
      } 
      paramInt1++;
    } 
    updateCollapsible();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    super.onMeasure(paramInt1, paramInt2);
    invalidateScrollRanges();
  }
  
  WindowInsetsCompat onWindowInsetChanged(WindowInsetsCompat paramWindowInsetsCompat) {
    Object object;
    if (ViewCompat.getFitsSystemWindows(this)) {
      object = paramWindowInsetsCompat;
    } else {
      object = null;
    } 
    if (!ObjectsCompat.equals(this.mLastInsets, object)) {
      this.mLastInsets = object;
      invalidateScrollRanges();
    } 
    return paramWindowInsetsCompat;
  }
  
  public void removeOnOffsetChangedListener(OnOffsetChangedListener paramOnOffsetChangedListener) {
    if (this.mListeners != null && paramOnOffsetChangedListener != null)
      this.mListeners.remove(paramOnOffsetChangedListener); 
  }
  
  void resetPendingAction() { this.mPendingAction = 0; }
  
  boolean setCollapsedState(boolean paramBoolean) {
    if (this.mCollapsed != paramBoolean) {
      this.mCollapsed = paramBoolean;
      refreshDrawableState();
      return true;
    } 
    return false;
  }
  
  public void setExpanded(boolean paramBoolean) { setExpanded(paramBoolean, ViewCompat.isLaidOut(this)); }
  
  public void setExpanded(boolean paramBoolean1, boolean paramBoolean2) { setExpanded(paramBoolean1, paramBoolean2, true); }
  
  public void setOrientation(int paramInt) {
    if (paramInt != 1)
      throw new IllegalArgumentException("AppBarLayout is always vertical and does not support horizontal orientation"); 
    super.setOrientation(paramInt);
  }
  
  @Deprecated
  public void setTargetElevation(float paramFloat) {
    if (Build.VERSION.SDK_INT >= 21)
      ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, paramFloat); 
  }
  
  public static class Behavior extends HeaderBehavior<AppBarLayout> {
    private static final int INVALID_POSITION = -1;
    
    private static final int MAX_OFFSET_ANIMATION_DURATION = 600;
    
    private WeakReference<View> mLastNestedScrollingChildRef;
    
    private ValueAnimator mOffsetAnimator;
    
    private int mOffsetDelta;
    
    private int mOffsetToChildIndexOnLayout = -1;
    
    private boolean mOffsetToChildIndexOnLayoutIsMinHeight;
    
    private float mOffsetToChildIndexOnLayoutPerc;
    
    private DragCallback mOnDragCallback;
    
    public Behavior() {}
    
    public Behavior(Context param1Context, AttributeSet param1AttributeSet) { super(param1Context, param1AttributeSet); }
    
    private void animateOffsetTo(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout, int param1Int, float param1Float) {
      int i = Math.abs(getTopBottomOffsetForScrollingSibling() - param1Int);
      param1Float = Math.abs(param1Float);
      if (param1Float > 0.0F) {
        i = Math.round(i / param1Float * 1000.0F) * 3;
      } else {
        i = (int)((i / param1AppBarLayout.getHeight() + 1.0F) * 150.0F);
      } 
      animateOffsetWithDuration(param1CoordinatorLayout, param1AppBarLayout, param1Int, i);
    }
    
    private void animateOffsetWithDuration(final CoordinatorLayout coordinatorLayout, final AppBarLayout child, int param1Int1, int param1Int2) {
      int i = getTopBottomOffsetForScrollingSibling();
      if (i == param1Int1) {
        if (this.mOffsetAnimator != null && this.mOffsetAnimator.isRunning())
          this.mOffsetAnimator.cancel(); 
        return;
      } 
      if (this.mOffsetAnimator == null) {
        this.mOffsetAnimator = new ValueAnimator();
        this.mOffsetAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
        this.mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
              public void onAnimationUpdate(ValueAnimator param2ValueAnimator) { AppBarLayout.Behavior.this.setHeaderTopBottomOffset(coordinatorLayout, child, ((Integer)param2ValueAnimator.getAnimatedValue()).intValue()); }
            });
      } else {
        this.mOffsetAnimator.cancel();
      } 
      this.mOffsetAnimator.setDuration(Math.min(param1Int2, 600));
      this.mOffsetAnimator.setIntValues(new int[] { i, param1Int1 });
      this.mOffsetAnimator.start();
    }
    
    private static boolean checkFlag(int param1Int1, int param1Int2) { return ((param1Int1 & param1Int2) == param1Int2); }
    
    private static View getAppBarChildOnOffset(AppBarLayout param1AppBarLayout, int param1Int) {
      int i = Math.abs(param1Int);
      int j = param1AppBarLayout.getChildCount();
      for (param1Int = 0; param1Int < j; param1Int++) {
        View view = param1AppBarLayout.getChildAt(param1Int);
        if (i >= view.getTop() && i <= view.getBottom())
          return view; 
      } 
      return null;
    }
    
    private int getChildIndexOnOffset(AppBarLayout param1AppBarLayout, int param1Int) {
      int i = param1AppBarLayout.getChildCount();
      for (byte b = 0; b < i; b++) {
        View view = param1AppBarLayout.getChildAt(b);
        int j = view.getTop();
        int k = -param1Int;
        if (j <= k && view.getBottom() >= k)
          return b; 
      } 
      return -1;
    }
    
    private int interpolateOffset(AppBarLayout param1AppBarLayout, int param1Int) {
      int k = Math.abs(param1Int);
      int m = param1AppBarLayout.getChildCount();
      int j = 0;
      int i;
      for (i = 0; i < m; i++) {
        View view = param1AppBarLayout.getChildAt(i);
        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams)view.getLayoutParams();
        Interpolator interpolator = layoutParams.getScrollInterpolator();
        if (k >= view.getTop() && k <= view.getBottom()) {
          if (interpolator != null) {
            m = layoutParams.getScrollFlags();
            i = j;
            if ((m & true) != 0) {
              j = 0 + view.getHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
              i = j;
              if ((m & 0x2) != 0)
                i = j - ViewCompat.getMinimumHeight(view); 
            } 
            j = i;
            if (ViewCompat.getFitsSystemWindows(view))
              j = i - param1AppBarLayout.getTopInset(); 
            if (j > 0) {
              i = view.getTop();
              float f = j;
              i = Math.round(f * interpolator.getInterpolation((k - i) / f));
              return Integer.signum(param1Int) * (view.getTop() + i);
            } 
          } 
          break;
        } 
      } 
      return param1Int;
    }
    
    private boolean shouldJumpElevationState(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout) {
      List list = param1CoordinatorLayout.getDependents(param1AppBarLayout);
      int i = list.size();
      boolean bool = false;
      for (byte b = 0; b < i; b++) {
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams)((View)list.get(b)).getLayoutParams()).getBehavior();
        if (behavior instanceof AppBarLayout.ScrollingViewBehavior) {
          if (((AppBarLayout.ScrollingViewBehavior)behavior).getOverlayTop() != 0)
            bool = true; 
          return bool;
        } 
      } 
      return false;
    }
    
    private void snapToChildIfNeeded(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout) {
      int j = getTopBottomOffsetForScrollingSibling();
      int i = getChildIndexOnOffset(param1AppBarLayout, j);
      if (i >= 0) {
        View view = param1AppBarLayout.getChildAt(i);
        int k = ((AppBarLayout.LayoutParams)view.getLayoutParams()).getScrollFlags();
        if ((k & 0x11) == 17) {
          int i1 = -view.getTop();
          int m = -view.getBottom();
          int n = m;
          if (i == param1AppBarLayout.getChildCount() - 1)
            n = m + param1AppBarLayout.getTopInset(); 
          if (checkFlag(k, 2)) {
            m = n + ViewCompat.getMinimumHeight(view);
            i = i1;
          } else {
            i = i1;
            m = n;
            if (checkFlag(k, 5)) {
              m = ViewCompat.getMinimumHeight(view) + n;
              if (j < m) {
                i = m;
                m = n;
              } else {
                i = i1;
              } 
            } 
          } 
          n = i;
          if (j < (m + i) / 2)
            n = m; 
          animateOffsetTo(param1CoordinatorLayout, param1AppBarLayout, MathUtils.clamp(n, -param1AppBarLayout.getTotalScrollRange(), 0), 0.0F);
        } 
      } 
    }
    
    private void updateAppBarLayoutDrawableState(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout, int param1Int1, int param1Int2, boolean param1Boolean) { // Byte code:
      //   0: aload_2
      //   1: iload_3
      //   2: invokestatic getAppBarChildOnOffset : (Landroid/support/design/widget/AppBarLayout;I)Landroid/view/View;
      //   5: astore #10
      //   7: aload #10
      //   9: ifnull -> 162
      //   12: aload #10
      //   14: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
      //   17: checkcast android/support/design/widget/AppBarLayout$LayoutParams
      //   20: invokevirtual getScrollFlags : ()I
      //   23: istore #6
      //   25: iconst_0
      //   26: istore #9
      //   28: iload #9
      //   30: istore #8
      //   32: iload #6
      //   34: iconst_1
      //   35: iand
      //   36: ifeq -> 123
      //   39: aload #10
      //   41: invokestatic getMinimumHeight : (Landroid/view/View;)I
      //   44: istore #7
      //   46: iload #4
      //   48: ifle -> 87
      //   51: iload #6
      //   53: bipush #12
      //   55: iand
      //   56: ifeq -> 87
      //   59: iload #9
      //   61: istore #8
      //   63: iload_3
      //   64: ineg
      //   65: aload #10
      //   67: invokevirtual getBottom : ()I
      //   70: iload #7
      //   72: isub
      //   73: aload_2
      //   74: invokevirtual getTopInset : ()I
      //   77: isub
      //   78: if_icmplt -> 123
      //   81: iconst_1
      //   82: istore #8
      //   84: goto -> 123
      //   87: iload #9
      //   89: istore #8
      //   91: iload #6
      //   93: iconst_2
      //   94: iand
      //   95: ifeq -> 123
      //   98: iload #9
      //   100: istore #8
      //   102: iload_3
      //   103: ineg
      //   104: aload #10
      //   106: invokevirtual getBottom : ()I
      //   109: iload #7
      //   111: isub
      //   112: aload_2
      //   113: invokevirtual getTopInset : ()I
      //   116: isub
      //   117: if_icmplt -> 123
      //   120: goto -> 81
      //   123: aload_2
      //   124: iload #8
      //   126: invokevirtual setCollapsedState : (Z)Z
      //   129: istore #8
      //   131: getstatic android/os/Build$VERSION.SDK_INT : I
      //   134: bipush #11
      //   136: if_icmplt -> 162
      //   139: iload #5
      //   141: ifne -> 158
      //   144: iload #8
      //   146: ifeq -> 162
      //   149: aload_0
      //   150: aload_1
      //   151: aload_2
      //   152: invokespecial shouldJumpElevationState : (Landroid/support/design/widget/CoordinatorLayout;Landroid/support/design/widget/AppBarLayout;)Z
      //   155: ifeq -> 162
      //   158: aload_2
      //   159: invokevirtual jumpDrawablesToCurrentState : ()V
      //   162: return }
    
    boolean canDragView(AppBarLayout param1AppBarLayout) {
      if (this.mOnDragCallback != null)
        return this.mOnDragCallback.canDrag(param1AppBarLayout); 
      if (this.mLastNestedScrollingChildRef != null) {
        View view = (View)this.mLastNestedScrollingChildRef.get();
        return (view != null && view.isShown() && !view.canScrollVertically(-1));
      } 
      return true;
    }
    
    int getMaxDragOffset(AppBarLayout param1AppBarLayout) { return -param1AppBarLayout.getDownNestedScrollRange(); }
    
    int getScrollRangeForDragFling(AppBarLayout param1AppBarLayout) { return param1AppBarLayout.getTotalScrollRange(); }
    
    int getTopBottomOffsetForScrollingSibling() { return getTopAndBottomOffset() + this.mOffsetDelta; }
    
    @VisibleForTesting
    boolean isOffsetAnimatorRunning() { return (this.mOffsetAnimator != null && this.mOffsetAnimator.isRunning()); }
    
    void onFlingFinished(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout) { snapToChildIfNeeded(param1CoordinatorLayout, param1AppBarLayout); }
    
    public boolean onLayoutChild(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout, int param1Int) {
      boolean bool = super.onLayoutChild(param1CoordinatorLayout, param1AppBarLayout, param1Int);
      int i = param1AppBarLayout.getPendingAction();
      if (this.mOffsetToChildIndexOnLayout >= 0 && (i & 0x8) == 0) {
        View view = param1AppBarLayout.getChildAt(this.mOffsetToChildIndexOnLayout);
        param1Int = -view.getBottom();
        if (this.mOffsetToChildIndexOnLayoutIsMinHeight) {
          param1Int += ViewCompat.getMinimumHeight(view) + param1AppBarLayout.getTopInset();
        } else {
          param1Int += Math.round(view.getHeight() * this.mOffsetToChildIndexOnLayoutPerc);
        } 
        setHeaderTopBottomOffset(param1CoordinatorLayout, param1AppBarLayout, param1Int);
      } else if (i != 0) {
        if ((i & 0x4) != 0) {
          param1Int = 1;
        } else {
          param1Int = 0;
        } 
        if ((i & 0x2) != 0) {
          i = -param1AppBarLayout.getUpNestedPreScrollRange();
          if (param1Int != 0) {
            animateOffsetTo(param1CoordinatorLayout, param1AppBarLayout, i, 0.0F);
          } else {
            setHeaderTopBottomOffset(param1CoordinatorLayout, param1AppBarLayout, i);
          } 
        } else if ((i & true) != 0) {
          if (param1Int != 0) {
            animateOffsetTo(param1CoordinatorLayout, param1AppBarLayout, 0, 0.0F);
          } else {
            setHeaderTopBottomOffset(param1CoordinatorLayout, param1AppBarLayout, 0);
          } 
        } 
      } 
      param1AppBarLayout.resetPendingAction();
      this.mOffsetToChildIndexOnLayout = -1;
      setTopAndBottomOffset(MathUtils.clamp(getTopAndBottomOffset(), -param1AppBarLayout.getTotalScrollRange(), 0));
      updateAppBarLayoutDrawableState(param1CoordinatorLayout, param1AppBarLayout, getTopAndBottomOffset(), 0, true);
      param1AppBarLayout.dispatchOffsetUpdates(getTopAndBottomOffset());
      return bool;
    }
    
    public boolean onMeasureChild(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (((CoordinatorLayout.LayoutParams)param1AppBarLayout.getLayoutParams()).height == -2) {
        param1CoordinatorLayout.onMeasureChild(param1AppBarLayout, param1Int1, param1Int2, View.MeasureSpec.makeMeasureSpec(0, 0), param1Int4);
        return true;
      } 
      return super.onMeasureChild(param1CoordinatorLayout, param1AppBarLayout, param1Int1, param1Int2, param1Int3, param1Int4);
    }
    
    public void onNestedPreScroll(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout, View param1View, int param1Int1, int param1Int2, int[] param1ArrayOfInt, int param1Int3) {
      if (param1Int2 != 0) {
        if (param1Int2 < 0) {
          param1Int3 = -param1AppBarLayout.getTotalScrollRange();
          int i = param1AppBarLayout.getDownNestedPreScrollRange();
          param1Int1 = param1Int3;
          param1Int3 = i + param1Int3;
        } else {
          param1Int1 = -param1AppBarLayout.getUpNestedPreScrollRange();
          param1Int3 = 0;
        } 
        if (param1Int1 != param1Int3)
          param1ArrayOfInt[1] = scroll(param1CoordinatorLayout, param1AppBarLayout, param1Int2, param1Int1, param1Int3); 
      } 
    }
    
    public void onNestedScroll(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout, View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      if (param1Int4 < 0)
        scroll(param1CoordinatorLayout, param1AppBarLayout, param1Int4, -param1AppBarLayout.getDownNestedScrollRange(), 0); 
    }
    
    public void onRestoreInstanceState(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout, Parcelable param1Parcelable) {
      SavedState savedState;
      if (param1Parcelable instanceof SavedState) {
        savedState = (SavedState)param1Parcelable;
        super.onRestoreInstanceState(param1CoordinatorLayout, param1AppBarLayout, savedState.getSuperState());
        this.mOffsetToChildIndexOnLayout = savedState.firstVisibleChildIndex;
        this.mOffsetToChildIndexOnLayoutPerc = savedState.firstVisibleChildPercentageShown;
        this.mOffsetToChildIndexOnLayoutIsMinHeight = savedState.firstVisibleChildAtMinimumHeight;
        return;
      } 
      super.onRestoreInstanceState(param1CoordinatorLayout, param1AppBarLayout, savedState);
      this.mOffsetToChildIndexOnLayout = -1;
    }
    
    public Parcelable onSaveInstanceState(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout) {
      SavedState savedState = super.onSaveInstanceState(param1CoordinatorLayout, param1AppBarLayout);
      int i = getTopAndBottomOffset();
      int j = param1AppBarLayout.getChildCount();
      boolean bool = false;
      for (byte b = 0; b < j; b++) {
        View view = param1AppBarLayout.getChildAt(b);
        int k = view.getBottom() + i;
        if (view.getTop() + i <= 0 && k >= 0) {
          savedState = new SavedState(savedState);
          savedState.firstVisibleChildIndex = b;
          if (k == ViewCompat.getMinimumHeight(view) + param1AppBarLayout.getTopInset())
            bool = true; 
          savedState.firstVisibleChildAtMinimumHeight = bool;
          savedState.firstVisibleChildPercentageShown = k / view.getHeight();
          return savedState;
        } 
      } 
      return savedState;
    }
    
    public boolean onStartNestedScroll(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout, View param1View1, View param1View2, int param1Int1, int param1Int2) {
      boolean bool;
      if ((param1Int1 & 0x2) != 0 && param1AppBarLayout.hasScrollableChildren() && param1CoordinatorLayout.getHeight() - param1View1.getHeight() <= param1AppBarLayout.getHeight()) {
        bool = true;
      } else {
        bool = false;
      } 
      if (bool && this.mOffsetAnimator != null)
        this.mOffsetAnimator.cancel(); 
      this.mLastNestedScrollingChildRef = null;
      return bool;
    }
    
    public void onStopNestedScroll(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout, View param1View, int param1Int) {
      if (param1Int == 0)
        snapToChildIfNeeded(param1CoordinatorLayout, param1AppBarLayout); 
      this.mLastNestedScrollingChildRef = new WeakReference(param1View);
    }
    
    public void setDragCallback(@Nullable DragCallback param1DragCallback) { this.mOnDragCallback = param1DragCallback; }
    
    int setHeaderTopBottomOffset(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout, int param1Int1, int param1Int2, int param1Int3) {
      int i = getTopBottomOffsetForScrollingSibling();
      if (param1Int2 != 0 && i >= param1Int2 && i <= param1Int3) {
        param1Int2 = MathUtils.clamp(param1Int1, param1Int2, param1Int3);
        if (i != param1Int2) {
          if (param1AppBarLayout.hasChildWithInterpolator()) {
            param1Int1 = interpolateOffset(param1AppBarLayout, param1Int2);
          } else {
            param1Int1 = param1Int2;
          } 
          boolean bool = setTopAndBottomOffset(param1Int1);
          this.mOffsetDelta = param1Int2 - param1Int1;
          if (!bool && param1AppBarLayout.hasChildWithInterpolator())
            param1CoordinatorLayout.dispatchDependentViewsChanged(param1AppBarLayout); 
          param1AppBarLayout.dispatchOffsetUpdates(getTopAndBottomOffset());
          if (param1Int2 < i) {
            param1Int1 = -1;
          } else {
            param1Int1 = 1;
          } 
          updateAppBarLayoutDrawableState(param1CoordinatorLayout, param1AppBarLayout, param1Int2, param1Int1, false);
          return i - param1Int2;
        } 
      } else {
        this.mOffsetDelta = 0;
      } 
      return 0;
    }
    
    public static abstract class DragCallback {
      public abstract boolean canDrag(@NonNull AppBarLayout param2AppBarLayout);
    }
    
    protected static class SavedState extends AbsSavedState {
      public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
          public AppBarLayout.Behavior.SavedState createFromParcel(Parcel param3Parcel) { return new AppBarLayout.Behavior.SavedState(param3Parcel, null); }
          
          public AppBarLayout.Behavior.SavedState createFromParcel(Parcel param3Parcel, ClassLoader param3ClassLoader) { return new AppBarLayout.Behavior.SavedState(param3Parcel, param3ClassLoader); }
          
          public AppBarLayout.Behavior.SavedState[] newArray(int param3Int) { return new AppBarLayout.Behavior.SavedState[param3Int]; }
        };
      
      boolean firstVisibleChildAtMinimumHeight;
      
      int firstVisibleChildIndex;
      
      float firstVisibleChildPercentageShown;
      
      public SavedState(Parcel param2Parcel, ClassLoader param2ClassLoader) {
        super(param2Parcel, param2ClassLoader);
        this.firstVisibleChildIndex = param2Parcel.readInt();
        this.firstVisibleChildPercentageShown = param2Parcel.readFloat();
        if (param2Parcel.readByte() != 0) {
          bool = true;
        } else {
          bool = false;
        } 
        this.firstVisibleChildAtMinimumHeight = bool;
      }
      
      public SavedState(Parcelable param2Parcelable) { super(param2Parcelable); }
      
      public void writeToParcel(Parcel param2Parcel, int param2Int) {
        super.writeToParcel(param2Parcel, param2Int);
        param2Parcel.writeInt(this.firstVisibleChildIndex);
        param2Parcel.writeFloat(this.firstVisibleChildPercentageShown);
        param2Parcel.writeByte((byte)this.firstVisibleChildAtMinimumHeight);
      }
    }
    
    static final class null extends Object implements Parcelable.ClassLoaderCreator<SavedState> {
      public AppBarLayout.Behavior.SavedState createFromParcel(Parcel param2Parcel) { return new AppBarLayout.Behavior.SavedState(param2Parcel, null); }
      
      public AppBarLayout.Behavior.SavedState createFromParcel(Parcel param2Parcel, ClassLoader param2ClassLoader) { return new AppBarLayout.Behavior.SavedState(param2Parcel, param2ClassLoader); }
      
      public AppBarLayout.Behavior.SavedState[] newArray(int param2Int) { return new AppBarLayout.Behavior.SavedState[param2Int]; }
    }
  }
  
  class null implements ValueAnimator.AnimatorUpdateListener {
    public void onAnimationUpdate(ValueAnimator param1ValueAnimator) { this.this$0.setHeaderTopBottomOffset(coordinatorLayout, child, ((Integer)param1ValueAnimator.getAnimatedValue()).intValue()); }
  }
  
  public static abstract class DragCallback {
    public abstract boolean canDrag(@NonNull AppBarLayout param1AppBarLayout);
  }
  
  protected static class SavedState extends AbsSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
        public AppBarLayout.Behavior.SavedState createFromParcel(Parcel param3Parcel) { return new AppBarLayout.Behavior.SavedState(param3Parcel, null); }
        
        public AppBarLayout.Behavior.SavedState createFromParcel(Parcel param3Parcel, ClassLoader param3ClassLoader) { return new AppBarLayout.Behavior.SavedState(param3Parcel, param3ClassLoader); }
        
        public AppBarLayout.Behavior.SavedState[] newArray(int param3Int) { return new AppBarLayout.Behavior.SavedState[param3Int]; }
      };
    
    boolean firstVisibleChildAtMinimumHeight;
    
    int firstVisibleChildIndex;
    
    float firstVisibleChildPercentageShown;
    
    public SavedState(Parcel param1Parcel, ClassLoader param1ClassLoader) {
      super(param1Parcel, param1ClassLoader);
      this.firstVisibleChildIndex = param1Parcel.readInt();
      this.firstVisibleChildPercentageShown = param1Parcel.readFloat();
      if (param1Parcel.readByte() != 0) {
        bool = true;
      } else {
        bool = false;
      } 
      this.firstVisibleChildAtMinimumHeight = bool;
    }
    
    public SavedState(Parcelable param1Parcelable) { super(param1Parcelable); }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) {
      super.writeToParcel(param1Parcel, param1Int);
      param1Parcel.writeInt(this.firstVisibleChildIndex);
      param1Parcel.writeFloat(this.firstVisibleChildPercentageShown);
      param1Parcel.writeByte((byte)this.firstVisibleChildAtMinimumHeight);
    }
  }
  
  static final class null extends Object implements Parcelable.ClassLoaderCreator<Behavior.SavedState> {
    public AppBarLayout.Behavior.SavedState createFromParcel(Parcel param1Parcel) { return new AppBarLayout.Behavior.SavedState(param1Parcel, null); }
    
    public AppBarLayout.Behavior.SavedState createFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) { return new AppBarLayout.Behavior.SavedState(param1Parcel, param1ClassLoader); }
    
    public AppBarLayout.Behavior.SavedState[] newArray(int param1Int) { return new AppBarLayout.Behavior.SavedState[param1Int]; }
  }
  
  public static class LayoutParams extends LinearLayout.LayoutParams {
    static final int COLLAPSIBLE_FLAGS = 10;
    
    static final int FLAG_QUICK_RETURN = 5;
    
    static final int FLAG_SNAP = 17;
    
    public static final int SCROLL_FLAG_ENTER_ALWAYS = 4;
    
    public static final int SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED = 8;
    
    public static final int SCROLL_FLAG_EXIT_UNTIL_COLLAPSED = 2;
    
    public static final int SCROLL_FLAG_SCROLL = 1;
    
    public static final int SCROLL_FLAG_SNAP = 16;
    
    int mScrollFlags = 1;
    
    Interpolator mScrollInterpolator;
    
    public LayoutParams(int param1Int1, int param1Int2) { super(param1Int1, param1Int2); }
    
    public LayoutParams(int param1Int1, int param1Int2, float param1Float) { super(param1Int1, param1Int2, param1Float); }
    
    public LayoutParams(Context param1Context, AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, R.styleable.AppBarLayout_Layout);
      this.mScrollFlags = typedArray.getInt(R.styleable.AppBarLayout_Layout_layout_scrollFlags, 0);
      if (typedArray.hasValue(R.styleable.AppBarLayout_Layout_layout_scrollInterpolator))
        this.mScrollInterpolator = AnimationUtils.loadInterpolator(param1Context, typedArray.getResourceId(R.styleable.AppBarLayout_Layout_layout_scrollInterpolator, 0)); 
      typedArray.recycle();
    }
    
    @RequiresApi(19)
    public LayoutParams(LayoutParams param1LayoutParams) {
      super(param1LayoutParams);
      this.mScrollFlags = param1LayoutParams.mScrollFlags;
      this.mScrollInterpolator = param1LayoutParams.mScrollInterpolator;
    }
    
    public LayoutParams(ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.MarginLayoutParams param1MarginLayoutParams) { super(param1MarginLayoutParams); }
    
    @RequiresApi(19)
    public LayoutParams(LinearLayout.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public int getScrollFlags() { return this.mScrollFlags; }
    
    public Interpolator getScrollInterpolator() { return this.mScrollInterpolator; }
    
    boolean isCollapsible() { return ((this.mScrollFlags & true) == 1 && (this.mScrollFlags & 0xA) != 0); }
    
    public void setScrollFlags(int param1Int) { this.mScrollFlags = param1Int; }
    
    public void setScrollInterpolator(Interpolator param1Interpolator) { this.mScrollInterpolator = param1Interpolator; }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static @interface ScrollFlags {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface ScrollFlags {}
  
  public static interface OnOffsetChangedListener {
    void onOffsetChanged(AppBarLayout param1AppBarLayout, int param1Int);
  }
  
  public static class ScrollingViewBehavior extends HeaderScrollingViewBehavior {
    public ScrollingViewBehavior() {}
    
    public ScrollingViewBehavior(Context param1Context, AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, R.styleable.ScrollingViewBehavior_Layout);
      setOverlayTop(typedArray.getDimensionPixelSize(R.styleable.ScrollingViewBehavior_Layout_behavior_overlapTop, 0));
      typedArray.recycle();
    }
    
    private static int getAppBarLayoutOffset(AppBarLayout param1AppBarLayout) {
      CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams)param1AppBarLayout.getLayoutParams()).getBehavior();
      return (behavior instanceof AppBarLayout.Behavior) ? ((AppBarLayout.Behavior)behavior).getTopBottomOffsetForScrollingSibling() : 0;
    }
    
    private void offsetChildAsNeeded(CoordinatorLayout param1CoordinatorLayout, View param1View1, View param1View2) {
      CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams)param1View2.getLayoutParams()).getBehavior();
      if (behavior instanceof AppBarLayout.Behavior) {
        behavior = (AppBarLayout.Behavior)behavior;
        ViewCompat.offsetTopAndBottom(param1View1, param1View2.getBottom() - param1View1.getTop() + behavior.mOffsetDelta + getVerticalLayoutGap() - getOverlapPixelsForOffset(param1View2));
      } 
    }
    
    AppBarLayout findFirstDependency(List<View> param1List) {
      int i = param1List.size();
      for (byte b = 0; b < i; b++) {
        View view = (View)param1List.get(b);
        if (view instanceof AppBarLayout)
          return (AppBarLayout)view; 
      } 
      return null;
    }
    
    float getOverlapRatioForOffset(View param1View) {
      if (param1View instanceof AppBarLayout) {
        AppBarLayout appBarLayout = (AppBarLayout)param1View;
        int j = appBarLayout.getTotalScrollRange();
        int k = appBarLayout.getDownNestedPreScrollRange();
        int i = getAppBarLayoutOffset(appBarLayout);
        if (k != 0 && j + i <= k)
          return 0.0F; 
        j -= k;
        if (j != 0)
          return i / j + 1.0F; 
      } 
      return 0.0F;
    }
    
    int getScrollRange(View param1View) { return (param1View instanceof AppBarLayout) ? ((AppBarLayout)param1View).getTotalScrollRange() : super.getScrollRange(param1View); }
    
    public boolean layoutDependsOn(CoordinatorLayout param1CoordinatorLayout, View param1View1, View param1View2) { return param1View2 instanceof AppBarLayout; }
    
    public boolean onDependentViewChanged(CoordinatorLayout param1CoordinatorLayout, View param1View1, View param1View2) {
      offsetChildAsNeeded(param1CoordinatorLayout, param1View1, param1View2);
      return false;
    }
    
    public boolean onRequestChildRectangleOnScreen(CoordinatorLayout param1CoordinatorLayout, View param1View, Rect param1Rect, boolean param1Boolean) {
      AppBarLayout appBarLayout = findFirstDependency(param1CoordinatorLayout.getDependencies(param1View));
      if (appBarLayout != null) {
        param1Rect.offset(param1View.getLeft(), param1View.getTop());
        Rect rect = this.mTempRect1;
        rect.set(0, 0, param1CoordinatorLayout.getWidth(), param1CoordinatorLayout.getHeight());
        if (!rect.contains(param1Rect)) {
          appBarLayout.setExpanded(false, param1Boolean ^ true);
          return true;
        } 
      } 
      return false;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/AppBarLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */