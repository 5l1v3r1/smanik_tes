package android.support.v4.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class DrawerLayout extends ViewGroup {
  private static final boolean ALLOW_EDGE_LOCK = false;
  
  static final boolean CAN_HIDE_DESCENDANTS;
  
  private static final boolean CHILDREN_DISALLOW_INTERCEPT = true;
  
  private static final int DEFAULT_SCRIM_COLOR = -1728053248;
  
  private static final int DRAWER_ELEVATION = 10;
  
  static final int[] LAYOUT_ATTRS;
  
  public static final int LOCK_MODE_LOCKED_CLOSED = 1;
  
  public static final int LOCK_MODE_LOCKED_OPEN = 2;
  
  public static final int LOCK_MODE_UNDEFINED = 3;
  
  public static final int LOCK_MODE_UNLOCKED = 0;
  
  private static final int MIN_DRAWER_MARGIN = 64;
  
  private static final int MIN_FLING_VELOCITY = 400;
  
  private static final int PEEK_DELAY = 160;
  
  private static final boolean SET_DRAWER_SHADOW_FROM_ELEVATION;
  
  public static final int STATE_DRAGGING = 1;
  
  public static final int STATE_IDLE = 0;
  
  public static final int STATE_SETTLING = 2;
  
  private static final String TAG = "DrawerLayout";
  
  private static final int[] THEME_ATTRS;
  
  private static final float TOUCH_SLOP_SENSITIVITY = 1.0F;
  
  private final ChildAccessibilityDelegate mChildAccessibilityDelegate = new ChildAccessibilityDelegate();
  
  private boolean mChildrenCanceledTouch;
  
  private boolean mDisallowInterceptRequested;
  
  private boolean mDrawStatusBarBackground;
  
  private float mDrawerElevation;
  
  private int mDrawerState;
  
  private boolean mFirstLayout = true;
  
  private boolean mInLayout;
  
  private float mInitialMotionX;
  
  private float mInitialMotionY;
  
  private Object mLastInsets;
  
  private final ViewDragCallback mLeftCallback;
  
  private final ViewDragHelper mLeftDragger;
  
  @Nullable
  private DrawerListener mListener;
  
  private List<DrawerListener> mListeners;
  
  private int mLockModeEnd = 3;
  
  private int mLockModeLeft = 3;
  
  private int mLockModeRight = 3;
  
  private int mLockModeStart = 3;
  
  private int mMinDrawerMargin;
  
  private final ArrayList<View> mNonDrawerViews;
  
  private final ViewDragCallback mRightCallback;
  
  private final ViewDragHelper mRightDragger;
  
  private int mScrimColor = -1728053248;
  
  private float mScrimOpacity;
  
  private Paint mScrimPaint = new Paint();
  
  private Drawable mShadowEnd = null;
  
  private Drawable mShadowLeft = null;
  
  private Drawable mShadowLeftResolved;
  
  private Drawable mShadowRight = null;
  
  private Drawable mShadowRightResolved;
  
  private Drawable mShadowStart = null;
  
  private Drawable mStatusBarBackground;
  
  private CharSequence mTitleLeft;
  
  private CharSequence mTitleRight;
  
  static  {
    byte b1;
    byte b2 = 1;
    THEME_ATTRS = new int[] { 16843828 };
    LAYOUT_ATTRS = new int[] { 16842931 };
    if (Build.VERSION.SDK_INT >= 19) {
      b1 = 1;
    } else {
      b1 = 0;
    } 
    CAN_HIDE_DESCENDANTS = b1;
    if (Build.VERSION.SDK_INT >= 21) {
      b1 = b2;
    } else {
      b1 = 0;
    } 
    SET_DRAWER_SHADOW_FROM_ELEVATION = b1;
  }
  
  public DrawerLayout(@NonNull Context paramContext) { this(paramContext, null); }
  
  public DrawerLayout(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public DrawerLayout(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    setDescendantFocusability(262144);
    float f1 = (getResources().getDisplayMetrics()).density;
    this.mMinDrawerMargin = (int)(64.0F * f1 + 0.5F);
    float f2 = 400.0F * f1;
    this.mLeftCallback = new ViewDragCallback(3);
    this.mRightCallback = new ViewDragCallback(5);
    this.mLeftDragger = ViewDragHelper.create(this, 1.0F, this.mLeftCallback);
    this.mLeftDragger.setEdgeTrackingEnabled(1);
    this.mLeftDragger.setMinVelocity(f2);
    this.mLeftCallback.setDragger(this.mLeftDragger);
    this.mRightDragger = ViewDragHelper.create(this, 1.0F, this.mRightCallback);
    this.mRightDragger.setEdgeTrackingEnabled(2);
    this.mRightDragger.setMinVelocity(f2);
    this.mRightCallback.setDragger(this.mRightDragger);
    setFocusableInTouchMode(true);
    ViewCompat.setImportantForAccessibility(this, 1);
    ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
    setMotionEventSplittingEnabled(false);
    if (ViewCompat.getFitsSystemWindows(this))
      if (Build.VERSION.SDK_INT >= 21) {
        setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
              @TargetApi(21)
              public WindowInsets onApplyWindowInsets(View param1View, WindowInsets param1WindowInsets) {
                boolean bool;
                DrawerLayout drawerLayout = (DrawerLayout)param1View;
                if (param1WindowInsets.getSystemWindowInsetTop() > 0) {
                  bool = true;
                } else {
                  bool = false;
                } 
                drawerLayout.setChildInsets(param1WindowInsets, bool);
                return param1WindowInsets.consumeSystemWindowInsets();
              }
            });
        setSystemUiVisibility(1280);
        typedArray = paramContext.obtainStyledAttributes(THEME_ATTRS);
        try {
          this.mStatusBarBackground = typedArray.getDrawable(0);
        } finally {
          typedArray.recycle();
        } 
      } else {
        this.mStatusBarBackground = null;
      }  
    this.mDrawerElevation = f1 * 10.0F;
    this.mNonDrawerViews = new ArrayList();
  }
  
  static String gravityToString(int paramInt) { return ((paramInt & 0x3) == 3) ? "LEFT" : (((paramInt & 0x5) == 5) ? "RIGHT" : Integer.toHexString(paramInt)); }
  
  private static boolean hasOpaqueBackground(View paramView) {
    Drawable drawable = paramView.getBackground();
    boolean bool = false;
    if (drawable != null) {
      if (drawable.getOpacity() == -1)
        bool = true; 
      return bool;
    } 
    return false;
  }
  
  private boolean hasPeekingDrawer() {
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      if (((LayoutParams)getChildAt(b).getLayoutParams()).isPeeking)
        return true; 
    } 
    return false;
  }
  
  private boolean hasVisibleDrawer() { return (findVisibleDrawer() != null); }
  
  static boolean includeChildForAccessibility(View paramView) { return (ViewCompat.getImportantForAccessibility(paramView) != 4 && ViewCompat.getImportantForAccessibility(paramView) != 2); }
  
  private boolean mirror(Drawable paramDrawable, int paramInt) {
    if (paramDrawable == null || !DrawableCompat.isAutoMirrored(paramDrawable))
      return false; 
    DrawableCompat.setLayoutDirection(paramDrawable, paramInt);
    return true;
  }
  
  private Drawable resolveLeftShadow() {
    int i = ViewCompat.getLayoutDirection(this);
    if (i == 0) {
      if (this.mShadowStart != null) {
        mirror(this.mShadowStart, i);
        return this.mShadowStart;
      } 
    } else if (this.mShadowEnd != null) {
      mirror(this.mShadowEnd, i);
      return this.mShadowEnd;
    } 
    return this.mShadowLeft;
  }
  
  private Drawable resolveRightShadow() {
    int i = ViewCompat.getLayoutDirection(this);
    if (i == 0) {
      if (this.mShadowEnd != null) {
        mirror(this.mShadowEnd, i);
        return this.mShadowEnd;
      } 
    } else if (this.mShadowStart != null) {
      mirror(this.mShadowStart, i);
      return this.mShadowStart;
    } 
    return this.mShadowRight;
  }
  
  private void resolveShadowDrawables() {
    if (SET_DRAWER_SHADOW_FROM_ELEVATION)
      return; 
    this.mShadowLeftResolved = resolveLeftShadow();
    this.mShadowRightResolved = resolveRightShadow();
  }
  
  private void updateChildrenImportantForAccessibility(View paramView, boolean paramBoolean) {
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = getChildAt(b);
      if ((!paramBoolean && !isDrawerView(view)) || (paramBoolean && view == paramView)) {
        ViewCompat.setImportantForAccessibility(view, 1);
      } else {
        ViewCompat.setImportantForAccessibility(view, 4);
      } 
    } 
  }
  
  public void addDrawerListener(@NonNull DrawerListener paramDrawerListener) {
    if (paramDrawerListener == null)
      return; 
    if (this.mListeners == null)
      this.mListeners = new ArrayList(); 
    this.mListeners.add(paramDrawerListener);
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2) {
    if (getDescendantFocusability() == 393216)
      return; 
    int j = getChildCount();
    byte b2 = 0;
    byte b1 = 0;
    int i = 0;
    while (b1 < j) {
      View view = getChildAt(b1);
      if (isDrawerView(view)) {
        if (isDrawerOpen(view)) {
          view.addFocusables(paramArrayList, paramInt1, paramInt2);
          i = 1;
        } 
      } else {
        this.mNonDrawerViews.add(view);
      } 
      b1++;
    } 
    if (!i) {
      i = this.mNonDrawerViews.size();
      for (b1 = b2; b1 < i; b1++) {
        View view = (View)this.mNonDrawerViews.get(b1);
        if (view.getVisibility() == 0)
          view.addFocusables(paramArrayList, paramInt1, paramInt2); 
      } 
    } 
    this.mNonDrawerViews.clear();
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams) {
    super.addView(paramView, paramInt, paramLayoutParams);
    if (findOpenDrawer() != null || isDrawerView(paramView)) {
      ViewCompat.setImportantForAccessibility(paramView, 4);
    } else {
      ViewCompat.setImportantForAccessibility(paramView, 1);
    } 
    if (!CAN_HIDE_DESCENDANTS)
      ViewCompat.setAccessibilityDelegate(paramView, this.mChildAccessibilityDelegate); 
  }
  
  void cancelChildViewTouch() {
    if (!this.mChildrenCanceledTouch) {
      long l = SystemClock.uptimeMillis();
      MotionEvent motionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
      int i = getChildCount();
      for (byte b = 0; b < i; b++)
        getChildAt(b).dispatchTouchEvent(motionEvent); 
      motionEvent.recycle();
      this.mChildrenCanceledTouch = true;
    } 
  }
  
  boolean checkDrawerViewAbsoluteGravity(View paramView, int paramInt) { return ((getDrawerViewAbsoluteGravity(paramView) & paramInt) == paramInt); }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams instanceof LayoutParams && super.checkLayoutParams(paramLayoutParams)); }
  
  public void closeDrawer(int paramInt) { closeDrawer(paramInt, true); }
  
  public void closeDrawer(int paramInt, boolean paramBoolean) {
    StringBuilder stringBuilder = findDrawerWithGravity(paramInt);
    if (stringBuilder == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("No drawer view found with gravity ");
      stringBuilder.append(gravityToString(paramInt));
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    closeDrawer(stringBuilder, paramBoolean);
  }
  
  public void closeDrawer(@NonNull View paramView) { closeDrawer(paramView, true); }
  
  public void closeDrawer(@NonNull View paramView, boolean paramBoolean) {
    if (!isDrawerView(paramView)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("View ");
      stringBuilder.append(paramView);
      stringBuilder.append(" is not a sliding drawer");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if (this.mFirstLayout) {
      layoutParams.onScreen = 0.0F;
      layoutParams.openState = 0;
    } else if (paramBoolean) {
      layoutParams.openState |= 0x4;
      if (checkDrawerViewAbsoluteGravity(paramView, 3)) {
        this.mLeftDragger.smoothSlideViewTo(paramView, -paramView.getWidth(), paramView.getTop());
      } else {
        this.mRightDragger.smoothSlideViewTo(paramView, getWidth(), paramView.getTop());
      } 
    } else {
      moveDrawerToOffset(paramView, 0.0F);
      updateDrawerState(layoutParams.gravity, 0, paramView);
      paramView.setVisibility(4);
    } 
    invalidate();
  }
  
  public void closeDrawers() { closeDrawers(false); }
  
  void closeDrawers(boolean paramBoolean) {
    int j = getChildCount();
    byte b = 0;
    int i;
    for (i = 0; b < j; i = k) {
      View view = getChildAt(b);
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      int k = i;
      if (isDrawerView(view))
        if (paramBoolean && !layoutParams.isPeeking) {
          k = i;
        } else {
          k = view.getWidth();
          if (checkDrawerViewAbsoluteGravity(view, 3)) {
            i |= this.mLeftDragger.smoothSlideViewTo(view, -k, view.getTop());
          } else {
            i |= this.mRightDragger.smoothSlideViewTo(view, getWidth(), view.getTop());
          } 
          layoutParams.isPeeking = false;
          k = i;
        }  
      b++;
    } 
    this.mLeftCallback.removeCallbacks();
    this.mRightCallback.removeCallbacks();
    if (i != 0)
      invalidate(); 
  }
  
  public void computeScroll() {
    int i = getChildCount();
    float f = 0.0F;
    for (byte b = 0; b < i; b++)
      f = Math.max(f, ((LayoutParams)getChildAt(b).getLayoutParams()).onScreen); 
    this.mScrimOpacity = f;
    boolean bool1 = this.mLeftDragger.continueSettling(true);
    boolean bool2 = this.mRightDragger.continueSettling(true);
    if (bool1 || bool2)
      ViewCompat.postInvalidateOnAnimation(this); 
  }
  
  void dispatchOnDrawerClosed(View paramView) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if ((layoutParams.openState & true) == 1) {
      layoutParams.openState = 0;
      if (this.mListeners != null)
        for (int i = this.mListeners.size() - 1; i >= 0; i--)
          ((DrawerListener)this.mListeners.get(i)).onDrawerClosed(paramView);  
      updateChildrenImportantForAccessibility(paramView, false);
      if (hasWindowFocus()) {
        paramView = getRootView();
        if (paramView != null)
          paramView.sendAccessibilityEvent(32); 
      } 
    } 
  }
  
  void dispatchOnDrawerOpened(View paramView) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if ((layoutParams.openState & true) == 0) {
      layoutParams.openState = 1;
      if (this.mListeners != null)
        for (int i = this.mListeners.size() - 1; i >= 0; i--)
          ((DrawerListener)this.mListeners.get(i)).onDrawerOpened(paramView);  
      updateChildrenImportantForAccessibility(paramView, true);
      if (hasWindowFocus())
        sendAccessibilityEvent(32); 
    } 
  }
  
  void dispatchOnDrawerSlide(View paramView, float paramFloat) {
    if (this.mListeners != null)
      for (int i = this.mListeners.size() - 1; i >= 0; i--)
        ((DrawerListener)this.mListeners.get(i)).onDrawerSlide(paramView, paramFloat);  
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong) {
    int j;
    int m = getHeight();
    boolean bool1 = isContentView(paramView);
    int i = getWidth();
    int k = paramCanvas.save();
    if (bool1) {
      int n = getChildCount();
      byte b = 0;
      j = 0;
      while (b < n) {
        View view = getChildAt(b);
        int i1 = j;
        int i2 = i;
        if (view != paramView) {
          i1 = j;
          i2 = i;
          if (view.getVisibility() == 0) {
            i1 = j;
            i2 = i;
            if (hasOpaqueBackground(view)) {
              i1 = j;
              i2 = i;
              if (isDrawerView(view))
                if (view.getHeight() < m) {
                  i1 = j;
                  i2 = i;
                } else if (checkDrawerViewAbsoluteGravity(view, 3)) {
                  int i3 = view.getRight();
                  i1 = j;
                  i2 = i;
                  if (i3 > j) {
                    i1 = i3;
                    i2 = i;
                  } 
                } else {
                  int i3 = view.getLeft();
                  i1 = j;
                  i2 = i;
                  if (i3 < i) {
                    i2 = i3;
                    i1 = j;
                  } 
                }  
            } 
          } 
        } 
        b++;
        j = i1;
        i = i2;
      } 
      paramCanvas.clipRect(j, 0, i, getHeight());
    } else {
      j = 0;
    } 
    boolean bool2 = super.drawChild(paramCanvas, paramView, paramLong);
    paramCanvas.restoreToCount(k);
    if (this.mScrimOpacity > 0.0F && bool1) {
      int n = (int)(((this.mScrimColor & 0xFF000000) >>> 24) * this.mScrimOpacity);
      int i1 = this.mScrimColor;
      this.mScrimPaint.setColor(n << 24 | i1 & 0xFFFFFF);
      paramCanvas.drawRect(j, 0.0F, i, getHeight(), this.mScrimPaint);
      return bool2;
    } 
    if (this.mShadowLeftResolved != null && checkDrawerViewAbsoluteGravity(paramView, 3)) {
      i = this.mShadowLeftResolved.getIntrinsicWidth();
      j = paramView.getRight();
      int n = this.mLeftDragger.getEdgeSize();
      float f = Math.max(0.0F, Math.min(j / n, 1.0F));
      this.mShadowLeftResolved.setBounds(j, paramView.getTop(), i + j, paramView.getBottom());
      this.mShadowLeftResolved.setAlpha((int)(f * 255.0F));
      this.mShadowLeftResolved.draw(paramCanvas);
      return bool2;
    } 
    if (this.mShadowRightResolved != null && checkDrawerViewAbsoluteGravity(paramView, 5)) {
      i = this.mShadowRightResolved.getIntrinsicWidth();
      j = paramView.getLeft();
      int n = getWidth();
      int i1 = this.mRightDragger.getEdgeSize();
      float f = Math.max(0.0F, Math.min((n - j) / i1, 1.0F));
      this.mShadowRightResolved.setBounds(j - i, paramView.getTop(), j, paramView.getBottom());
      this.mShadowRightResolved.setAlpha((int)(f * 255.0F));
      this.mShadowRightResolved.draw(paramCanvas);
    } 
    return bool2;
  }
  
  View findDrawerWithGravity(int paramInt) {
    int i = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    int j = getChildCount();
    for (paramInt = 0; paramInt < j; paramInt++) {
      View view = getChildAt(paramInt);
      if ((getDrawerViewAbsoluteGravity(view) & 0x7) == (i & 0x7))
        return view; 
    } 
    return null;
  }
  
  View findOpenDrawer() {
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = getChildAt(b);
      if ((((LayoutParams)view.getLayoutParams()).openState & true) == 1)
        return view; 
    } 
    return null;
  }
  
  View findVisibleDrawer() {
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = getChildAt(b);
      if (isDrawerView(view) && isDrawerVisible(view))
        return view; 
    } 
    return null;
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams() { return new LayoutParams(-1, -1); }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return new LayoutParams(getContext(), paramAttributeSet); }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams instanceof LayoutParams) ? new LayoutParams((LayoutParams)paramLayoutParams) : ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams) ? new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams) : new LayoutParams(paramLayoutParams)); }
  
  public float getDrawerElevation() { return SET_DRAWER_SHADOW_FROM_ELEVATION ? this.mDrawerElevation : 0.0F; }
  
  public int getDrawerLockMode(int paramInt) {
    int i = ViewCompat.getLayoutDirection(this);
    if (paramInt != 3) {
      if (paramInt != 5) {
        if (paramInt != 8388611) {
          if (paramInt == 8388613) {
            if (this.mLockModeEnd != 3)
              return this.mLockModeEnd; 
            if (i == 0) {
              paramInt = this.mLockModeRight;
            } else {
              paramInt = this.mLockModeLeft;
            } 
            if (paramInt != 3)
              return paramInt; 
          } 
        } else {
          if (this.mLockModeStart != 3)
            return this.mLockModeStart; 
          if (i == 0) {
            paramInt = this.mLockModeLeft;
          } else {
            paramInt = this.mLockModeRight;
          } 
          if (paramInt != 3)
            return paramInt; 
        } 
      } else {
        if (this.mLockModeRight != 3)
          return this.mLockModeRight; 
        if (i == 0) {
          paramInt = this.mLockModeEnd;
        } else {
          paramInt = this.mLockModeStart;
        } 
        if (paramInt != 3)
          return paramInt; 
      } 
    } else {
      if (this.mLockModeLeft != 3)
        return this.mLockModeLeft; 
      if (i == 0) {
        paramInt = this.mLockModeStart;
      } else {
        paramInt = this.mLockModeEnd;
      } 
      if (paramInt != 3)
        return paramInt; 
    } 
    return 0;
  }
  
  public int getDrawerLockMode(@NonNull View paramView) {
    if (!isDrawerView(paramView)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("View ");
      stringBuilder.append(paramView);
      stringBuilder.append(" is not a drawer");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    return getDrawerLockMode(((LayoutParams)paramView.getLayoutParams()).gravity);
  }
  
  @Nullable
  public CharSequence getDrawerTitle(int paramInt) {
    paramInt = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    return (paramInt == 3) ? this.mTitleLeft : ((paramInt == 5) ? this.mTitleRight : null);
  }
  
  int getDrawerViewAbsoluteGravity(View paramView) { return GravityCompat.getAbsoluteGravity(((LayoutParams)paramView.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(this)); }
  
  float getDrawerViewOffset(View paramView) { return ((LayoutParams)paramView.getLayoutParams()).onScreen; }
  
  @Nullable
  public Drawable getStatusBarBackgroundDrawable() { return this.mStatusBarBackground; }
  
  boolean isContentView(View paramView) { return (((LayoutParams)paramView.getLayoutParams()).gravity == 0); }
  
  public boolean isDrawerOpen(int paramInt) {
    View view = findDrawerWithGravity(paramInt);
    return (view != null) ? isDrawerOpen(view) : 0;
  }
  
  public boolean isDrawerOpen(@NonNull View paramView) {
    if (!isDrawerView(paramView)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("View ");
      stringBuilder.append(paramView);
      stringBuilder.append(" is not a drawer");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    return ((((LayoutParams)paramView.getLayoutParams()).openState & true) == 1);
  }
  
  boolean isDrawerView(View paramView) {
    int i = GravityCompat.getAbsoluteGravity(((LayoutParams)paramView.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(paramView));
    return ((i & 0x3) != 0) ? true : (((i & 0x5) != 0));
  }
  
  public boolean isDrawerVisible(int paramInt) {
    View view = findDrawerWithGravity(paramInt);
    return (view != null) ? isDrawerVisible(view) : 0;
  }
  
  public boolean isDrawerVisible(@NonNull View paramView) {
    if (!isDrawerView(paramView)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("View ");
      stringBuilder.append(paramView);
      stringBuilder.append(" is not a drawer");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    return (((LayoutParams)paramView.getLayoutParams()).onScreen > 0.0F);
  }
  
  void moveDrawerToOffset(View paramView, float paramFloat) {
    float f1 = getDrawerViewOffset(paramView);
    float f2 = paramView.getWidth();
    int i = (int)(f1 * f2);
    i = (int)(f2 * paramFloat) - i;
    if (!checkDrawerViewAbsoluteGravity(paramView, 3))
      i = -i; 
    paramView.offsetLeftAndRight(i);
    setDrawerViewOffset(paramView, paramFloat);
  }
  
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    this.mFirstLayout = true;
  }
  
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    this.mFirstLayout = true;
  }
  
  public void onDraw(Canvas paramCanvas) {
    super.onDraw(paramCanvas);
    if (this.mDrawStatusBarBackground && this.mStatusBarBackground != null) {
      byte b;
      if (Build.VERSION.SDK_INT >= 21 && this.mLastInsets != null) {
        b = ((WindowInsets)this.mLastInsets).getSystemWindowInsetTop();
      } else {
        b = 0;
      } 
      if (b) {
        this.mStatusBarBackground.setBounds(0, 0, getWidth(), b);
        this.mStatusBarBackground.draw(paramCanvas);
      } 
    } 
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) { // Byte code:
    //   0: aload_1
    //   1: invokevirtual getActionMasked : ()I
    //   4: istore #4
    //   6: aload_0
    //   7: getfield mLeftDragger : Landroid/support/v4/widget/ViewDragHelper;
    //   10: aload_1
    //   11: invokevirtual shouldInterceptTouchEvent : (Landroid/view/MotionEvent;)Z
    //   14: istore #7
    //   16: aload_0
    //   17: getfield mRightDragger : Landroid/support/v4/widget/ViewDragHelper;
    //   20: aload_1
    //   21: invokevirtual shouldInterceptTouchEvent : (Landroid/view/MotionEvent;)Z
    //   24: istore #8
    //   26: iconst_1
    //   27: istore #6
    //   29: iload #4
    //   31: tableswitch default -> 60, 0 -> 109, 1 -> 91, 2 -> 63, 3 -> 91
    //   60: goto -> 184
    //   63: aload_0
    //   64: getfield mLeftDragger : Landroid/support/v4/widget/ViewDragHelper;
    //   67: iconst_3
    //   68: invokevirtual checkTouchSlop : (I)Z
    //   71: ifeq -> 184
    //   74: aload_0
    //   75: getfield mLeftCallback : Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;
    //   78: invokevirtual removeCallbacks : ()V
    //   81: aload_0
    //   82: getfield mRightCallback : Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;
    //   85: invokevirtual removeCallbacks : ()V
    //   88: goto -> 184
    //   91: aload_0
    //   92: iconst_1
    //   93: invokevirtual closeDrawers : (Z)V
    //   96: aload_0
    //   97: iconst_0
    //   98: putfield mDisallowInterceptRequested : Z
    //   101: aload_0
    //   102: iconst_0
    //   103: putfield mChildrenCanceledTouch : Z
    //   106: goto -> 184
    //   109: aload_1
    //   110: invokevirtual getX : ()F
    //   113: fstore_2
    //   114: aload_1
    //   115: invokevirtual getY : ()F
    //   118: fstore_3
    //   119: aload_0
    //   120: fload_2
    //   121: putfield mInitialMotionX : F
    //   124: aload_0
    //   125: fload_3
    //   126: putfield mInitialMotionY : F
    //   129: aload_0
    //   130: getfield mScrimOpacity : F
    //   133: fconst_0
    //   134: fcmpl
    //   135: ifle -> 168
    //   138: aload_0
    //   139: getfield mLeftDragger : Landroid/support/v4/widget/ViewDragHelper;
    //   142: fload_2
    //   143: f2i
    //   144: fload_3
    //   145: f2i
    //   146: invokevirtual findTopChildUnder : (II)Landroid/view/View;
    //   149: astore_1
    //   150: aload_1
    //   151: ifnull -> 168
    //   154: aload_0
    //   155: aload_1
    //   156: invokevirtual isContentView : (Landroid/view/View;)Z
    //   159: ifeq -> 168
    //   162: iconst_1
    //   163: istore #4
    //   165: goto -> 171
    //   168: iconst_0
    //   169: istore #4
    //   171: aload_0
    //   172: iconst_0
    //   173: putfield mDisallowInterceptRequested : Z
    //   176: aload_0
    //   177: iconst_0
    //   178: putfield mChildrenCanceledTouch : Z
    //   181: goto -> 187
    //   184: iconst_0
    //   185: istore #4
    //   187: iload #6
    //   189: istore #5
    //   191: iload #7
    //   193: iload #8
    //   195: ior
    //   196: ifne -> 231
    //   199: iload #6
    //   201: istore #5
    //   203: iload #4
    //   205: ifne -> 231
    //   208: iload #6
    //   210: istore #5
    //   212: aload_0
    //   213: invokespecial hasPeekingDrawer : ()Z
    //   216: ifne -> 231
    //   219: aload_0
    //   220: getfield mChildrenCanceledTouch : Z
    //   223: ifeq -> 228
    //   226: iconst_1
    //   227: ireturn
    //   228: iconst_0
    //   229: istore #5
    //   231: iload #5
    //   233: ireturn }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
    if (paramInt == 4 && hasVisibleDrawer()) {
      paramKeyEvent.startTracking();
      return true;
    } 
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
    View view;
    if (paramInt == 4) {
      view = findVisibleDrawer();
      if (view != null && getDrawerLockMode(view) == 0)
        closeDrawers(); 
      return (view != null);
    } 
    return super.onKeyUp(paramInt, view);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.mInLayout = true;
    int i = paramInt3 - paramInt1;
    int j = getChildCount();
    for (paramInt3 = 0; paramInt3 < j; paramInt3++) {
      View view = getChildAt(paramInt3);
      if (view.getVisibility() != 8) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (isContentView(view)) {
          view.layout(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.leftMargin + view.getMeasuredWidth(), layoutParams.topMargin + view.getMeasuredHeight());
        } else {
          boolean bool;
          int k;
          float f;
          int m = view.getMeasuredWidth();
          int n = view.getMeasuredHeight();
          if (checkDrawerViewAbsoluteGravity(view, 3)) {
            paramInt1 = -m;
            f = m;
            k = paramInt1 + (int)(layoutParams.onScreen * f);
            f = (m + k) / f;
          } else {
            f = m;
            k = i - (int)(layoutParams.onScreen * f);
            f = (i - k) / f;
          } 
          if (f != layoutParams.onScreen) {
            bool = true;
          } else {
            bool = false;
          } 
          paramInt1 = layoutParams.gravity & 0x70;
          if (paramInt1 != 16) {
            if (paramInt1 != 80) {
              view.layout(k, layoutParams.topMargin, m + k, layoutParams.topMargin + n);
            } else {
              paramInt1 = paramInt4 - paramInt2;
              view.layout(k, paramInt1 - layoutParams.bottomMargin - view.getMeasuredHeight(), m + k, paramInt1 - layoutParams.bottomMargin);
            } 
          } else {
            int i2 = paramInt4 - paramInt2;
            int i1 = (i2 - n) / 2;
            if (i1 < layoutParams.topMargin) {
              paramInt1 = layoutParams.topMargin;
            } else {
              paramInt1 = i1;
              if (i1 + n > i2 - layoutParams.bottomMargin)
                paramInt1 = i2 - layoutParams.bottomMargin - n; 
            } 
            view.layout(k, paramInt1, m + k, n + paramInt1);
          } 
          if (bool)
            setDrawerViewOffset(view, f); 
          if (layoutParams.onScreen > 0.0F) {
            paramInt1 = 0;
          } else {
            paramInt1 = 4;
          } 
          if (view.getVisibility() != paramInt1)
            view.setVisibility(paramInt1); 
        } 
      } 
    } 
    this.mInLayout = false;
    this.mFirstLayout = false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) { // Byte code:
    //   0: iload_1
    //   1: invokestatic getMode : (I)I
    //   4: istore #8
    //   6: iload_2
    //   7: invokestatic getMode : (I)I
    //   10: istore #7
    //   12: iload_1
    //   13: invokestatic getSize : (I)I
    //   16: istore_3
    //   17: iload_2
    //   18: invokestatic getSize : (I)I
    //   21: istore #4
    //   23: iload #8
    //   25: ldc_w 1073741824
    //   28: if_icmpne -> 46
    //   31: iload_3
    //   32: istore #5
    //   34: iload #4
    //   36: istore #6
    //   38: iload #7
    //   40: ldc_w 1073741824
    //   43: if_icmpeq -> 111
    //   46: aload_0
    //   47: invokevirtual isInEditMode : ()Z
    //   50: ifeq -> 803
    //   53: iload #8
    //   55: ldc_w -2147483648
    //   58: if_icmpne -> 64
    //   61: goto -> 73
    //   64: iload #8
    //   66: ifne -> 73
    //   69: sipush #300
    //   72: istore_3
    //   73: iload #7
    //   75: ldc_w -2147483648
    //   78: if_icmpne -> 91
    //   81: iload_3
    //   82: istore #5
    //   84: iload #4
    //   86: istore #6
    //   88: goto -> 111
    //   91: iload_3
    //   92: istore #5
    //   94: iload #4
    //   96: istore #6
    //   98: iload #7
    //   100: ifne -> 111
    //   103: sipush #300
    //   106: istore #6
    //   108: iload_3
    //   109: istore #5
    //   111: aload_0
    //   112: iload #5
    //   114: iload #6
    //   116: invokevirtual setMeasuredDimension : (II)V
    //   119: aload_0
    //   120: getfield mLastInsets : Ljava/lang/Object;
    //   123: ifnull -> 139
    //   126: aload_0
    //   127: invokestatic getFitsSystemWindows : (Landroid/view/View;)Z
    //   130: ifeq -> 139
    //   133: iconst_1
    //   134: istore #7
    //   136: goto -> 142
    //   139: iconst_0
    //   140: istore #7
    //   142: aload_0
    //   143: invokestatic getLayoutDirection : (Landroid/view/View;)I
    //   146: istore #10
    //   148: aload_0
    //   149: invokevirtual getChildCount : ()I
    //   152: istore #11
    //   154: iconst_0
    //   155: istore #8
    //   157: iconst_0
    //   158: istore #4
    //   160: iconst_0
    //   161: istore_3
    //   162: iload #8
    //   164: iload #11
    //   166: if_icmpge -> 802
    //   169: aload_0
    //   170: iload #8
    //   172: invokevirtual getChildAt : (I)Landroid/view/View;
    //   175: astore #15
    //   177: aload #15
    //   179: invokevirtual getVisibility : ()I
    //   182: bipush #8
    //   184: if_icmpne -> 190
    //   187: goto -> 494
    //   190: aload #15
    //   192: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   195: checkcast android/support/v4/widget/DrawerLayout$LayoutParams
    //   198: astore #16
    //   200: iload #7
    //   202: ifeq -> 440
    //   205: aload #16
    //   207: getfield gravity : I
    //   210: iload #10
    //   212: invokestatic getAbsoluteGravity : (II)I
    //   215: istore #9
    //   217: aload #15
    //   219: invokestatic getFitsSystemWindows : (Landroid/view/View;)Z
    //   222: ifeq -> 318
    //   225: getstatic android/os/Build$VERSION.SDK_INT : I
    //   228: bipush #21
    //   230: if_icmplt -> 440
    //   233: aload_0
    //   234: getfield mLastInsets : Ljava/lang/Object;
    //   237: checkcast android/view/WindowInsets
    //   240: astore #14
    //   242: iload #9
    //   244: iconst_3
    //   245: if_icmpne -> 274
    //   248: aload #14
    //   250: aload #14
    //   252: invokevirtual getSystemWindowInsetLeft : ()I
    //   255: aload #14
    //   257: invokevirtual getSystemWindowInsetTop : ()I
    //   260: iconst_0
    //   261: aload #14
    //   263: invokevirtual getSystemWindowInsetBottom : ()I
    //   266: invokevirtual replaceSystemWindowInsets : (IIII)Landroid/view/WindowInsets;
    //   269: astore #13
    //   271: goto -> 307
    //   274: aload #14
    //   276: astore #13
    //   278: iload #9
    //   280: iconst_5
    //   281: if_icmpne -> 307
    //   284: aload #14
    //   286: iconst_0
    //   287: aload #14
    //   289: invokevirtual getSystemWindowInsetTop : ()I
    //   292: aload #14
    //   294: invokevirtual getSystemWindowInsetRight : ()I
    //   297: aload #14
    //   299: invokevirtual getSystemWindowInsetBottom : ()I
    //   302: invokevirtual replaceSystemWindowInsets : (IIII)Landroid/view/WindowInsets;
    //   305: astore #13
    //   307: aload #15
    //   309: aload #13
    //   311: invokevirtual dispatchApplyWindowInsets : (Landroid/view/WindowInsets;)Landroid/view/WindowInsets;
    //   314: pop
    //   315: goto -> 440
    //   318: getstatic android/os/Build$VERSION.SDK_INT : I
    //   321: bipush #21
    //   323: if_icmplt -> 440
    //   326: aload_0
    //   327: getfield mLastInsets : Ljava/lang/Object;
    //   330: checkcast android/view/WindowInsets
    //   333: astore #14
    //   335: iload #9
    //   337: iconst_3
    //   338: if_icmpne -> 367
    //   341: aload #14
    //   343: aload #14
    //   345: invokevirtual getSystemWindowInsetLeft : ()I
    //   348: aload #14
    //   350: invokevirtual getSystemWindowInsetTop : ()I
    //   353: iconst_0
    //   354: aload #14
    //   356: invokevirtual getSystemWindowInsetBottom : ()I
    //   359: invokevirtual replaceSystemWindowInsets : (IIII)Landroid/view/WindowInsets;
    //   362: astore #13
    //   364: goto -> 400
    //   367: aload #14
    //   369: astore #13
    //   371: iload #9
    //   373: iconst_5
    //   374: if_icmpne -> 400
    //   377: aload #14
    //   379: iconst_0
    //   380: aload #14
    //   382: invokevirtual getSystemWindowInsetTop : ()I
    //   385: aload #14
    //   387: invokevirtual getSystemWindowInsetRight : ()I
    //   390: aload #14
    //   392: invokevirtual getSystemWindowInsetBottom : ()I
    //   395: invokevirtual replaceSystemWindowInsets : (IIII)Landroid/view/WindowInsets;
    //   398: astore #13
    //   400: aload #16
    //   402: aload #13
    //   404: invokevirtual getSystemWindowInsetLeft : ()I
    //   407: putfield leftMargin : I
    //   410: aload #16
    //   412: aload #13
    //   414: invokevirtual getSystemWindowInsetTop : ()I
    //   417: putfield topMargin : I
    //   420: aload #16
    //   422: aload #13
    //   424: invokevirtual getSystemWindowInsetRight : ()I
    //   427: putfield rightMargin : I
    //   430: aload #16
    //   432: aload #13
    //   434: invokevirtual getSystemWindowInsetBottom : ()I
    //   437: putfield bottomMargin : I
    //   440: aload_0
    //   441: aload #15
    //   443: invokevirtual isContentView : (Landroid/view/View;)Z
    //   446: ifeq -> 497
    //   449: aload #15
    //   451: iload #5
    //   453: aload #16
    //   455: getfield leftMargin : I
    //   458: isub
    //   459: aload #16
    //   461: getfield rightMargin : I
    //   464: isub
    //   465: ldc_w 1073741824
    //   468: invokestatic makeMeasureSpec : (II)I
    //   471: iload #6
    //   473: aload #16
    //   475: getfield topMargin : I
    //   478: isub
    //   479: aload #16
    //   481: getfield bottomMargin : I
    //   484: isub
    //   485: ldc_w 1073741824
    //   488: invokestatic makeMeasureSpec : (II)I
    //   491: invokevirtual measure : (II)V
    //   494: goto -> 719
    //   497: aload_0
    //   498: aload #15
    //   500: invokevirtual isDrawerView : (Landroid/view/View;)Z
    //   503: ifeq -> 728
    //   506: getstatic android/support/v4/widget/DrawerLayout.SET_DRAWER_SHADOW_FROM_ELEVATION : Z
    //   509: ifeq -> 534
    //   512: aload #15
    //   514: invokestatic getElevation : (Landroid/view/View;)F
    //   517: aload_0
    //   518: getfield mDrawerElevation : F
    //   521: fcmpl
    //   522: ifeq -> 534
    //   525: aload #15
    //   527: aload_0
    //   528: getfield mDrawerElevation : F
    //   531: invokestatic setElevation : (Landroid/view/View;F)V
    //   534: aload_0
    //   535: aload #15
    //   537: invokevirtual getDrawerViewAbsoluteGravity : (Landroid/view/View;)I
    //   540: bipush #7
    //   542: iand
    //   543: istore #12
    //   545: iload #12
    //   547: iconst_3
    //   548: if_icmpne -> 557
    //   551: iconst_1
    //   552: istore #9
    //   554: goto -> 560
    //   557: iconst_0
    //   558: istore #9
    //   560: iload #9
    //   562: ifeq -> 570
    //   565: iload #4
    //   567: ifne -> 579
    //   570: iload #9
    //   572: ifne -> 656
    //   575: iload_3
    //   576: ifeq -> 656
    //   579: new java/lang/StringBuilder
    //   582: dup
    //   583: invokespecial <init> : ()V
    //   586: astore #13
    //   588: aload #13
    //   590: ldc_w 'Child drawer has absolute gravity '
    //   593: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   596: pop
    //   597: aload #13
    //   599: iload #12
    //   601: invokestatic gravityToString : (I)Ljava/lang/String;
    //   604: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   607: pop
    //   608: aload #13
    //   610: ldc_w ' but this '
    //   613: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   616: pop
    //   617: aload #13
    //   619: ldc 'DrawerLayout'
    //   621: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   624: pop
    //   625: aload #13
    //   627: ldc_w ' already has a '
    //   630: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   633: pop
    //   634: aload #13
    //   636: ldc_w 'drawer view along that edge'
    //   639: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   642: pop
    //   643: new java/lang/IllegalStateException
    //   646: dup
    //   647: aload #13
    //   649: invokevirtual toString : ()Ljava/lang/String;
    //   652: invokespecial <init> : (Ljava/lang/String;)V
    //   655: athrow
    //   656: iload #9
    //   658: ifeq -> 667
    //   661: iconst_1
    //   662: istore #4
    //   664: goto -> 669
    //   667: iconst_1
    //   668: istore_3
    //   669: aload #15
    //   671: iload_1
    //   672: aload_0
    //   673: getfield mMinDrawerMargin : I
    //   676: aload #16
    //   678: getfield leftMargin : I
    //   681: iadd
    //   682: aload #16
    //   684: getfield rightMargin : I
    //   687: iadd
    //   688: aload #16
    //   690: getfield width : I
    //   693: invokestatic getChildMeasureSpec : (III)I
    //   696: iload_2
    //   697: aload #16
    //   699: getfield topMargin : I
    //   702: aload #16
    //   704: getfield bottomMargin : I
    //   707: iadd
    //   708: aload #16
    //   710: getfield height : I
    //   713: invokestatic getChildMeasureSpec : (III)I
    //   716: invokevirtual measure : (II)V
    //   719: iload #8
    //   721: iconst_1
    //   722: iadd
    //   723: istore #8
    //   725: goto -> 162
    //   728: new java/lang/StringBuilder
    //   731: dup
    //   732: invokespecial <init> : ()V
    //   735: astore #13
    //   737: aload #13
    //   739: ldc_w 'Child '
    //   742: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   745: pop
    //   746: aload #13
    //   748: aload #15
    //   750: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   753: pop
    //   754: aload #13
    //   756: ldc_w ' at index '
    //   759: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   762: pop
    //   763: aload #13
    //   765: iload #8
    //   767: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   770: pop
    //   771: aload #13
    //   773: ldc_w ' does not have a valid layout_gravity - must be Gravity.LEFT, '
    //   776: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   779: pop
    //   780: aload #13
    //   782: ldc_w 'Gravity.RIGHT or Gravity.NO_GRAVITY'
    //   785: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   788: pop
    //   789: new java/lang/IllegalStateException
    //   792: dup
    //   793: aload #13
    //   795: invokevirtual toString : ()Ljava/lang/String;
    //   798: invokespecial <init> : (Ljava/lang/String;)V
    //   801: athrow
    //   802: return
    //   803: new java/lang/IllegalArgumentException
    //   806: dup
    //   807: ldc_w 'DrawerLayout must be measured with MeasureSpec.EXACTLY.'
    //   810: invokespecial <init> : (Ljava/lang/String;)V
    //   813: athrow }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable) {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
      return;
    } 
    SavedState savedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(savedState.getSuperState());
    if (savedState.openDrawerGravity != 0) {
      View view = findDrawerWithGravity(savedState.openDrawerGravity);
      if (view != null)
        openDrawer(view); 
    } 
    if (savedState.lockModeLeft != 3)
      setDrawerLockMode(savedState.lockModeLeft, 3); 
    if (savedState.lockModeRight != 3)
      setDrawerLockMode(savedState.lockModeRight, 5); 
    if (savedState.lockModeStart != 3)
      setDrawerLockMode(savedState.lockModeStart, 8388611); 
    if (savedState.lockModeEnd != 3)
      setDrawerLockMode(savedState.lockModeEnd, 8388613); 
  }
  
  public void onRtlPropertiesChanged(int paramInt) { resolveShadowDrawables(); }
  
  protected Parcelable onSaveInstanceState() {
    SavedState savedState = new SavedState(super.onSaveInstanceState());
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      LayoutParams layoutParams = (LayoutParams)getChildAt(b).getLayoutParams();
      int j = layoutParams.openState;
      boolean bool = true;
      if (j == 1) {
        j = 1;
      } else {
        j = 0;
      } 
      if (layoutParams.openState != 2)
        bool = false; 
      if (j != 0 || bool) {
        savedState.openDrawerGravity = layoutParams.gravity;
        break;
      } 
    } 
    savedState.lockModeLeft = this.mLockModeLeft;
    savedState.lockModeRight = this.mLockModeRight;
    savedState.lockModeStart = this.mLockModeStart;
    savedState.lockModeEnd = this.mLockModeEnd;
    return savedState;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) {
    this.mLeftDragger.processTouchEvent(paramMotionEvent);
    this.mRightDragger.processTouchEvent(paramMotionEvent);
    int i = paramMotionEvent.getAction() & 0xFF;
    if (i != 3) {
      boolean bool;
      View view;
      switch (i) {
        default:
          return true;
        case 1:
          f2 = paramMotionEvent.getX();
          f1 = paramMotionEvent.getY();
          view = this.mLeftDragger.findTopChildUnder((int)f2, (int)f1);
          if (view != null && isContentView(view)) {
            f2 -= this.mInitialMotionX;
            f1 -= this.mInitialMotionY;
            i = this.mLeftDragger.getTouchSlop();
            if (f2 * f2 + f1 * f1 < (i * i)) {
              view = findOpenDrawer();
              if (view == null || getDrawerLockMode(view) == 2) {
                boolean bool2 = true;
                closeDrawers(bool2);
                this.mDisallowInterceptRequested = false;
                return true;
              } 
              boolean bool1 = false;
              closeDrawers(bool1);
              this.mDisallowInterceptRequested = false;
              return true;
            } 
          } 
          bool = true;
          closeDrawers(bool);
          this.mDisallowInterceptRequested = false;
          return true;
        case 0:
          break;
      } 
      float f1 = view.getX();
      float f2 = view.getY();
      this.mInitialMotionX = f1;
      this.mInitialMotionY = f2;
      this.mDisallowInterceptRequested = false;
      this.mChildrenCanceledTouch = false;
      return true;
    } 
    closeDrawers(true);
    this.mDisallowInterceptRequested = false;
    this.mChildrenCanceledTouch = false;
    return true;
  }
  
  public void openDrawer(int paramInt) { openDrawer(paramInt, true); }
  
  public void openDrawer(int paramInt, boolean paramBoolean) {
    StringBuilder stringBuilder = findDrawerWithGravity(paramInt);
    if (stringBuilder == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("No drawer view found with gravity ");
      stringBuilder.append(gravityToString(paramInt));
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    openDrawer(stringBuilder, paramBoolean);
  }
  
  public void openDrawer(@NonNull View paramView) { openDrawer(paramView, true); }
  
  public void openDrawer(@NonNull View paramView, boolean paramBoolean) {
    if (!isDrawerView(paramView)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("View ");
      stringBuilder.append(paramView);
      stringBuilder.append(" is not a sliding drawer");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if (this.mFirstLayout) {
      layoutParams.onScreen = 1.0F;
      layoutParams.openState = 1;
      updateChildrenImportantForAccessibility(paramView, true);
    } else if (paramBoolean) {
      layoutParams.openState |= 0x2;
      if (checkDrawerViewAbsoluteGravity(paramView, 3)) {
        this.mLeftDragger.smoothSlideViewTo(paramView, 0, paramView.getTop());
      } else {
        this.mRightDragger.smoothSlideViewTo(paramView, getWidth() - paramView.getWidth(), paramView.getTop());
      } 
    } else {
      moveDrawerToOffset(paramView, 1.0F);
      updateDrawerState(layoutParams.gravity, 0, paramView);
      paramView.setVisibility(0);
    } 
    invalidate();
  }
  
  public void removeDrawerListener(@NonNull DrawerListener paramDrawerListener) {
    if (paramDrawerListener == null)
      return; 
    if (this.mListeners == null)
      return; 
    this.mListeners.remove(paramDrawerListener);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean) {
    super.requestDisallowInterceptTouchEvent(paramBoolean);
    this.mDisallowInterceptRequested = paramBoolean;
    if (paramBoolean)
      closeDrawers(true); 
  }
  
  public void requestLayout() {
    if (!this.mInLayout)
      super.requestLayout(); 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setChildInsets(Object paramObject, boolean paramBoolean) {
    this.mLastInsets = paramObject;
    this.mDrawStatusBarBackground = paramBoolean;
    if (!paramBoolean && getBackground() == null) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    } 
    setWillNotDraw(paramBoolean);
    requestLayout();
  }
  
  public void setDrawerElevation(float paramFloat) {
    this.mDrawerElevation = paramFloat;
    for (byte b = 0; b < getChildCount(); b++) {
      View view = getChildAt(b);
      if (isDrawerView(view))
        ViewCompat.setElevation(view, this.mDrawerElevation); 
    } 
  }
  
  @Deprecated
  public void setDrawerListener(DrawerListener paramDrawerListener) {
    if (this.mListener != null)
      removeDrawerListener(this.mListener); 
    if (paramDrawerListener != null)
      addDrawerListener(paramDrawerListener); 
    this.mListener = paramDrawerListener;
  }
  
  public void setDrawerLockMode(int paramInt) {
    setDrawerLockMode(paramInt, 3);
    setDrawerLockMode(paramInt, 5);
  }
  
  public void setDrawerLockMode(int paramInt1, int paramInt2) {
    int i = GravityCompat.getAbsoluteGravity(paramInt2, ViewCompat.getLayoutDirection(this));
    if (paramInt2 != 3) {
      if (paramInt2 != 5) {
        if (paramInt2 != 8388611) {
          if (paramInt2 == 8388613)
            this.mLockModeEnd = paramInt1; 
        } else {
          this.mLockModeStart = paramInt1;
        } 
      } else {
        this.mLockModeRight = paramInt1;
      } 
    } else {
      this.mLockModeLeft = paramInt1;
    } 
    if (paramInt1 != 0) {
      ViewDragHelper viewDragHelper;
      if (i == 3) {
        viewDragHelper = this.mLeftDragger;
      } else {
        viewDragHelper = this.mRightDragger;
      } 
      viewDragHelper.cancel();
    } 
    switch (paramInt1) {
      default:
        return;
      case 2:
        view = findDrawerWithGravity(i);
        if (view != null) {
          openDrawer(view);
          return;
        } 
        return;
      case 1:
        break;
    } 
    View view = findDrawerWithGravity(i);
    if (view != null)
      closeDrawer(view); 
  }
  
  public void setDrawerLockMode(int paramInt, @NonNull View paramView) {
    if (!isDrawerView(paramView)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("View ");
      stringBuilder.append(paramView);
      stringBuilder.append(" is not a ");
      stringBuilder.append("drawer with appropriate layout_gravity");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    setDrawerLockMode(paramInt, ((LayoutParams)paramView.getLayoutParams()).gravity);
  }
  
  public void setDrawerShadow(@DrawableRes int paramInt1, int paramInt2) { setDrawerShadow(ContextCompat.getDrawable(getContext(), paramInt1), paramInt2); }
  
  public void setDrawerShadow(Drawable paramDrawable, int paramInt) {
    if (SET_DRAWER_SHADOW_FROM_ELEVATION)
      return; 
    if ((paramInt & 0x800003) == 8388611) {
      this.mShadowStart = paramDrawable;
    } else if ((paramInt & 0x800005) == 8388613) {
      this.mShadowEnd = paramDrawable;
    } else if ((paramInt & 0x3) == 3) {
      this.mShadowLeft = paramDrawable;
    } else if ((paramInt & 0x5) == 5) {
      this.mShadowRight = paramDrawable;
    } else {
      return;
    } 
    resolveShadowDrawables();
    invalidate();
  }
  
  public void setDrawerTitle(int paramInt, @Nullable CharSequence paramCharSequence) {
    paramInt = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    if (paramInt == 3) {
      this.mTitleLeft = paramCharSequence;
      return;
    } 
    if (paramInt == 5)
      this.mTitleRight = paramCharSequence; 
  }
  
  void setDrawerViewOffset(View paramView, float paramFloat) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if (paramFloat == layoutParams.onScreen)
      return; 
    layoutParams.onScreen = paramFloat;
    dispatchOnDrawerSlide(paramView, paramFloat);
  }
  
  public void setScrimColor(@ColorInt int paramInt) {
    this.mScrimColor = paramInt;
    invalidate();
  }
  
  public void setStatusBarBackground(int paramInt) {
    Object object;
    if (paramInt != 0) {
      object = ContextCompat.getDrawable(getContext(), paramInt);
    } else {
      object = null;
    } 
    this.mStatusBarBackground = object;
    invalidate();
  }
  
  public void setStatusBarBackground(@Nullable Drawable paramDrawable) {
    this.mStatusBarBackground = paramDrawable;
    invalidate();
  }
  
  public void setStatusBarBackgroundColor(@ColorInt int paramInt) {
    this.mStatusBarBackground = new ColorDrawable(paramInt);
    invalidate();
  }
  
  void updateDrawerState(int paramInt1, int paramInt2, View paramView) {
    int j = this.mLeftDragger.getViewDragState();
    int k = this.mRightDragger.getViewDragState();
    int i = 2;
    if (j == 1 || k == 1) {
      paramInt1 = 1;
    } else {
      paramInt1 = i;
      if (j != 2)
        if (k == 2) {
          paramInt1 = i;
        } else {
          paramInt1 = 0;
        }  
    } 
    if (paramView != null && paramInt2 == 0) {
      LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
      if (layoutParams.onScreen == 0.0F) {
        dispatchOnDrawerClosed(paramView);
      } else if (layoutParams.onScreen == 1.0F) {
        dispatchOnDrawerOpened(paramView);
      } 
    } 
    if (paramInt1 != this.mDrawerState) {
      this.mDrawerState = paramInt1;
      if (this.mListeners != null)
        for (paramInt2 = this.mListeners.size() - 1; paramInt2 >= 0; paramInt2--)
          ((DrawerListener)this.mListeners.get(paramInt2)).onDrawerStateChanged(paramInt1);  
    } 
  }
  
  class AccessibilityDelegate extends AccessibilityDelegateCompat {
    private final Rect mTmpRect = new Rect();
    
    private void addChildrenForAccessibility(AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat, ViewGroup param1ViewGroup) {
      int i = param1ViewGroup.getChildCount();
      for (byte b = 0; b < i; b++) {
        View view = param1ViewGroup.getChildAt(b);
        if (DrawerLayout.includeChildForAccessibility(view))
          param1AccessibilityNodeInfoCompat.addChild(view); 
      } 
    }
    
    private void copyNodeInfoNoChildren(AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat1, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat2) {
      Rect rect = this.mTmpRect;
      param1AccessibilityNodeInfoCompat2.getBoundsInParent(rect);
      param1AccessibilityNodeInfoCompat1.setBoundsInParent(rect);
      param1AccessibilityNodeInfoCompat2.getBoundsInScreen(rect);
      param1AccessibilityNodeInfoCompat1.setBoundsInScreen(rect);
      param1AccessibilityNodeInfoCompat1.setVisibleToUser(param1AccessibilityNodeInfoCompat2.isVisibleToUser());
      param1AccessibilityNodeInfoCompat1.setPackageName(param1AccessibilityNodeInfoCompat2.getPackageName());
      param1AccessibilityNodeInfoCompat1.setClassName(param1AccessibilityNodeInfoCompat2.getClassName());
      param1AccessibilityNodeInfoCompat1.setContentDescription(param1AccessibilityNodeInfoCompat2.getContentDescription());
      param1AccessibilityNodeInfoCompat1.setEnabled(param1AccessibilityNodeInfoCompat2.isEnabled());
      param1AccessibilityNodeInfoCompat1.setClickable(param1AccessibilityNodeInfoCompat2.isClickable());
      param1AccessibilityNodeInfoCompat1.setFocusable(param1AccessibilityNodeInfoCompat2.isFocusable());
      param1AccessibilityNodeInfoCompat1.setFocused(param1AccessibilityNodeInfoCompat2.isFocused());
      param1AccessibilityNodeInfoCompat1.setAccessibilityFocused(param1AccessibilityNodeInfoCompat2.isAccessibilityFocused());
      param1AccessibilityNodeInfoCompat1.setSelected(param1AccessibilityNodeInfoCompat2.isSelected());
      param1AccessibilityNodeInfoCompat1.setLongClickable(param1AccessibilityNodeInfoCompat2.isLongClickable());
      param1AccessibilityNodeInfoCompat1.addAction(param1AccessibilityNodeInfoCompat2.getActions());
    }
    
    public boolean dispatchPopulateAccessibilityEvent(View param1View, AccessibilityEvent param1AccessibilityEvent) {
      CharSequence charSequence;
      List list;
      if (param1AccessibilityEvent.getEventType() == 32) {
        list = param1AccessibilityEvent.getText();
        charSequence = DrawerLayout.this.findVisibleDrawer();
        if (charSequence != null) {
          int i = DrawerLayout.this.getDrawerViewAbsoluteGravity(charSequence);
          charSequence = DrawerLayout.this.getDrawerTitle(i);
          if (charSequence != null)
            list.add(charSequence); 
        } 
        return true;
      } 
      return super.dispatchPopulateAccessibilityEvent(list, charSequence);
    }
    
    public void onInitializeAccessibilityEvent(View param1View, AccessibilityEvent param1AccessibilityEvent) {
      super.onInitializeAccessibilityEvent(param1View, param1AccessibilityEvent);
      param1AccessibilityEvent.setClassName(DrawerLayout.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(View param1View, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) {
      if (DrawerLayout.CAN_HIDE_DESCENDANTS) {
        super.onInitializeAccessibilityNodeInfo(param1View, param1AccessibilityNodeInfoCompat);
      } else {
        AccessibilityNodeInfoCompat accessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain(param1AccessibilityNodeInfoCompat);
        super.onInitializeAccessibilityNodeInfo(param1View, accessibilityNodeInfoCompat);
        param1AccessibilityNodeInfoCompat.setSource(param1View);
        ViewParent viewParent = ViewCompat.getParentForAccessibility(param1View);
        if (viewParent instanceof View)
          param1AccessibilityNodeInfoCompat.setParent((View)viewParent); 
        copyNodeInfoNoChildren(param1AccessibilityNodeInfoCompat, accessibilityNodeInfoCompat);
        accessibilityNodeInfoCompat.recycle();
        addChildrenForAccessibility(param1AccessibilityNodeInfoCompat, (ViewGroup)param1View);
      } 
      param1AccessibilityNodeInfoCompat.setClassName(DrawerLayout.class.getName());
      param1AccessibilityNodeInfoCompat.setFocusable(false);
      param1AccessibilityNodeInfoCompat.setFocused(false);
      param1AccessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_FOCUS);
      param1AccessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLEAR_FOCUS);
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup param1ViewGroup, View param1View, AccessibilityEvent param1AccessibilityEvent) { return (DrawerLayout.CAN_HIDE_DESCENDANTS || DrawerLayout.includeChildForAccessibility(param1View)) ? super.onRequestSendAccessibilityEvent(param1ViewGroup, param1View, param1AccessibilityEvent) : 0; }
  }
  
  static final class ChildAccessibilityDelegate extends AccessibilityDelegateCompat {
    public void onInitializeAccessibilityNodeInfo(View param1View, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) {
      super.onInitializeAccessibilityNodeInfo(param1View, param1AccessibilityNodeInfoCompat);
      if (!DrawerLayout.includeChildForAccessibility(param1View))
        param1AccessibilityNodeInfoCompat.setParent(null); 
    }
  }
  
  public static interface DrawerListener {
    void onDrawerClosed(@NonNull View param1View);
    
    void onDrawerOpened(@NonNull View param1View);
    
    void onDrawerSlide(@NonNull View param1View, float param1Float);
    
    void onDrawerStateChanged(int param1Int);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface EdgeGravity {}
  
  public static class LayoutParams extends ViewGroup.MarginLayoutParams {
    private static final int FLAG_IS_CLOSING = 4;
    
    private static final int FLAG_IS_OPENED = 1;
    
    private static final int FLAG_IS_OPENING = 2;
    
    public int gravity = 0;
    
    boolean isPeeking;
    
    float onScreen;
    
    int openState;
    
    public LayoutParams(int param1Int1, int param1Int2) { super(param1Int1, param1Int2); }
    
    public LayoutParams(int param1Int1, int param1Int2, int param1Int3) {
      this(param1Int1, param1Int2);
      this.gravity = param1Int3;
    }
    
    public LayoutParams(@NonNull Context param1Context, @Nullable AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, DrawerLayout.LAYOUT_ATTRS);
      this.gravity = typedArray.getInt(0, 0);
      typedArray.recycle();
    }
    
    public LayoutParams(@NonNull LayoutParams param1LayoutParams) {
      super(param1LayoutParams);
      this.gravity = param1LayoutParams.gravity;
    }
    
    public LayoutParams(@NonNull ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(@NonNull ViewGroup.MarginLayoutParams param1MarginLayoutParams) { super(param1MarginLayoutParams); }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface LockMode {}
  
  protected static class SavedState extends AbsSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
        public DrawerLayout.SavedState createFromParcel(Parcel param2Parcel) { return new DrawerLayout.SavedState(param2Parcel, null); }
        
        public DrawerLayout.SavedState createFromParcel(Parcel param2Parcel, ClassLoader param2ClassLoader) { return new DrawerLayout.SavedState(param2Parcel, param2ClassLoader); }
        
        public DrawerLayout.SavedState[] newArray(int param2Int) { return new DrawerLayout.SavedState[param2Int]; }
      };
    
    int lockModeEnd;
    
    int lockModeLeft;
    
    int lockModeRight;
    
    int lockModeStart;
    
    int openDrawerGravity = 0;
    
    public SavedState(@NonNull Parcel param1Parcel, @Nullable ClassLoader param1ClassLoader) {
      super(param1Parcel, param1ClassLoader);
      this.openDrawerGravity = param1Parcel.readInt();
      this.lockModeLeft = param1Parcel.readInt();
      this.lockModeRight = param1Parcel.readInt();
      this.lockModeStart = param1Parcel.readInt();
      this.lockModeEnd = param1Parcel.readInt();
    }
    
    public SavedState(@NonNull Parcelable param1Parcelable) { super(param1Parcelable); }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) {
      super.writeToParcel(param1Parcel, param1Int);
      param1Parcel.writeInt(this.openDrawerGravity);
      param1Parcel.writeInt(this.lockModeLeft);
      param1Parcel.writeInt(this.lockModeRight);
      param1Parcel.writeInt(this.lockModeStart);
      param1Parcel.writeInt(this.lockModeEnd);
    }
  }
  
  static final class null extends Object implements Parcelable.ClassLoaderCreator<SavedState> {
    public DrawerLayout.SavedState createFromParcel(Parcel param1Parcel) { return new DrawerLayout.SavedState(param1Parcel, null); }
    
    public DrawerLayout.SavedState createFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) { return new DrawerLayout.SavedState(param1Parcel, param1ClassLoader); }
    
    public DrawerLayout.SavedState[] newArray(int param1Int) { return new DrawerLayout.SavedState[param1Int]; }
  }
  
  public static abstract class SimpleDrawerListener implements DrawerListener {
    public void onDrawerClosed(View param1View) {}
    
    public void onDrawerOpened(View param1View) {}
    
    public void onDrawerSlide(View param1View, float param1Float) {}
    
    public void onDrawerStateChanged(int param1Int) {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface State {}
  
  private class ViewDragCallback extends ViewDragHelper.Callback {
    private final int mAbsGravity;
    
    private ViewDragHelper mDragger;
    
    private final Runnable mPeekRunnable = new Runnable() {
        public void run() { DrawerLayout.ViewDragCallback.this.peekDrawer(); }
      };
    
    ViewDragCallback(int param1Int) { this.mAbsGravity = param1Int; }
    
    private void closeOtherDrawer() {
      int i = this.mAbsGravity;
      byte b = 3;
      if (i == 3)
        b = 5; 
      View view = DrawerLayout.this.findDrawerWithGravity(b);
      if (view != null)
        DrawerLayout.this.closeDrawer(view); 
    }
    
    public int clampViewPositionHorizontal(View param1View, int param1Int1, int param1Int2) {
      if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(param1View, 3))
        return Math.max(-param1View.getWidth(), Math.min(param1Int1, 0)); 
      param1Int2 = DrawerLayout.this.getWidth();
      return Math.max(param1Int2 - param1View.getWidth(), Math.min(param1Int1, param1Int2));
    }
    
    public int clampViewPositionVertical(View param1View, int param1Int1, int param1Int2) { return param1View.getTop(); }
    
    public int getViewHorizontalDragRange(View param1View) { return DrawerLayout.this.isDrawerView(param1View) ? param1View.getWidth() : 0; }
    
    public void onEdgeDragStarted(int param1Int1, int param1Int2) {
      View view;
      if ((param1Int1 & true) == 1) {
        view = DrawerLayout.this.findDrawerWithGravity(3);
      } else {
        view = DrawerLayout.this.findDrawerWithGravity(5);
      } 
      if (view != null && DrawerLayout.this.getDrawerLockMode(view) == 0)
        this.mDragger.captureChildView(view, param1Int2); 
    }
    
    public boolean onEdgeLock(int param1Int) { return false; }
    
    public void onEdgeTouched(int param1Int1, int param1Int2) { DrawerLayout.this.postDelayed(this.mPeekRunnable, 160L); }
    
    public void onViewCaptured(View param1View, int param1Int) {
      ((DrawerLayout.LayoutParams)param1View.getLayoutParams()).isPeeking = false;
      closeOtherDrawer();
    }
    
    public void onViewDragStateChanged(int param1Int) { DrawerLayout.this.updateDrawerState(this.mAbsGravity, param1Int, this.mDragger.getCapturedView()); }
    
    public void onViewPositionChanged(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      float f;
      param1Int2 = param1View.getWidth();
      if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(param1View, 3)) {
        f = (param1Int1 + param1Int2) / param1Int2;
      } else {
        f = (DrawerLayout.this.getWidth() - param1Int1) / param1Int2;
      } 
      DrawerLayout.this.setDrawerViewOffset(param1View, f);
      if (f == 0.0F) {
        param1Int1 = 4;
      } else {
        param1Int1 = 0;
      } 
      param1View.setVisibility(param1Int1);
      DrawerLayout.this.invalidate();
    }
    
    public void onViewReleased(View param1View, float param1Float1, float param1Float2) { // Byte code:
      //   0: aload_0
      //   1: getfield this$0 : Landroid/support/v4/widget/DrawerLayout;
      //   4: aload_1
      //   5: invokevirtual getDrawerViewOffset : (Landroid/view/View;)F
      //   8: fstore_3
      //   9: aload_1
      //   10: invokevirtual getWidth : ()I
      //   13: istore #6
      //   15: aload_0
      //   16: getfield this$0 : Landroid/support/v4/widget/DrawerLayout;
      //   19: aload_1
      //   20: iconst_3
      //   21: invokevirtual checkDrawerViewAbsoluteGravity : (Landroid/view/View;I)Z
      //   24: ifeq -> 63
      //   27: fload_2
      //   28: fconst_0
      //   29: fcmpl
      //   30: ifgt -> 57
      //   33: fload_2
      //   34: fconst_0
      //   35: fcmpl
      //   36: ifne -> 49
      //   39: fload_3
      //   40: ldc 0.5
      //   42: fcmpl
      //   43: ifle -> 49
      //   46: goto -> 57
      //   49: iload #6
      //   51: ineg
      //   52: istore #4
      //   54: goto -> 106
      //   57: iconst_0
      //   58: istore #4
      //   60: goto -> 106
      //   63: aload_0
      //   64: getfield this$0 : Landroid/support/v4/widget/DrawerLayout;
      //   67: invokevirtual getWidth : ()I
      //   70: istore #5
      //   72: fload_2
      //   73: fconst_0
      //   74: fcmpg
      //   75: iflt -> 99
      //   78: iload #5
      //   80: istore #4
      //   82: fload_2
      //   83: fconst_0
      //   84: fcmpl
      //   85: ifne -> 106
      //   88: iload #5
      //   90: istore #4
      //   92: fload_3
      //   93: ldc 0.5
      //   95: fcmpl
      //   96: ifle -> 106
      //   99: iload #5
      //   101: iload #6
      //   103: isub
      //   104: istore #4
      //   106: aload_0
      //   107: getfield mDragger : Landroid/support/v4/widget/ViewDragHelper;
      //   110: iload #4
      //   112: aload_1
      //   113: invokevirtual getTop : ()I
      //   116: invokevirtual settleCapturedViewAt : (II)Z
      //   119: pop
      //   120: aload_0
      //   121: getfield this$0 : Landroid/support/v4/widget/DrawerLayout;
      //   124: invokevirtual invalidate : ()V
      //   127: return }
    
    void peekDrawer() {
      View view;
      int k = this.mDragger.getEdgeSize();
      int i = this.mAbsGravity;
      int j = 0;
      if (i == 3) {
        i = 1;
      } else {
        i = 0;
      } 
      if (i != 0) {
        view = DrawerLayout.this.findDrawerWithGravity(3);
        if (view != null)
          j = -view.getWidth(); 
        j += k;
      } else {
        view = DrawerLayout.this.findDrawerWithGravity(5);
        j = DrawerLayout.this.getWidth() - k;
      } 
      if (view != null && ((i != 0 && view.getLeft() < j) || (i == 0 && view.getLeft() > j)) && DrawerLayout.this.getDrawerLockMode(view) == 0) {
        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams)view.getLayoutParams();
        this.mDragger.smoothSlideViewTo(view, j, view.getTop());
        layoutParams.isPeeking = true;
        DrawerLayout.this.invalidate();
        closeOtherDrawer();
        DrawerLayout.this.cancelChildViewTouch();
      } 
    }
    
    public void removeCallbacks() { DrawerLayout.this.removeCallbacks(this.mPeekRunnable); }
    
    public void setDragger(ViewDragHelper param1ViewDragHelper) { this.mDragger = param1ViewDragHelper; }
    
    public boolean tryCaptureView(View param1View, int param1Int) { return (DrawerLayout.this.isDrawerView(param1View) && DrawerLayout.this.checkDrawerViewAbsoluteGravity(param1View, this.mAbsGravity) && DrawerLayout.this.getDrawerLockMode(param1View) == 0); }
  }
  
  class null implements Runnable {
    null() {}
    
    public void run() { this.this$1.peekDrawer(); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/widget/DrawerLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */