package android.support.design.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.coreui.R;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.math.MathUtils;
import android.support.v4.util.ObjectsCompat;
import android.support.v4.util.Pools;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.DirectedAcyclicGraph;
import android.support.v4.widget.ViewGroupUtils;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoordinatorLayout extends ViewGroup implements NestedScrollingParent2 {
  static final Class<?>[] CONSTRUCTOR_PARAMS;
  
  static final int EVENT_NESTED_SCROLL = 1;
  
  static final int EVENT_PRE_DRAW = 0;
  
  static final int EVENT_VIEW_REMOVED = 2;
  
  static final String TAG = "CoordinatorLayout";
  
  static final Comparator<View> TOP_SORTED_CHILDREN_COMPARATOR;
  
  private static final int TYPE_ON_INTERCEPT = 0;
  
  private static final int TYPE_ON_TOUCH = 1;
  
  static final String WIDGET_PACKAGE_NAME;
  
  static final ThreadLocal<Map<String, Constructor<Behavior>>> sConstructors;
  
  private static final Pools.Pool<Rect> sRectPool;
  
  private OnApplyWindowInsetsListener mApplyWindowInsetsListener;
  
  private View mBehaviorTouchView;
  
  private final DirectedAcyclicGraph<View> mChildDag = new DirectedAcyclicGraph();
  
  private final List<View> mDependencySortedChildren = new ArrayList();
  
  private boolean mDisallowInterceptReset;
  
  private boolean mDrawStatusBarBackground;
  
  private boolean mIsAttachedToWindow;
  
  private int[] mKeylines;
  
  private WindowInsetsCompat mLastInsets;
  
  private boolean mNeedsPreDrawListener;
  
  private final NestedScrollingParentHelper mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
  
  private View mNestedScrollingTarget;
  
  ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;
  
  private OnPreDrawListener mOnPreDrawListener;
  
  private Paint mScrimPaint;
  
  private Drawable mStatusBarBackground;
  
  private final List<View> mTempDependenciesList = new ArrayList();
  
  private final int[] mTempIntPair = new int[2];
  
  private final List<View> mTempList1 = new ArrayList();
  
  static  {
    Package package = CoordinatorLayout.class.getPackage();
    if (package != null) {
      String str = package.getName();
    } else {
      package = null;
    } 
    WIDGET_PACKAGE_NAME = package;
    if (Build.VERSION.SDK_INT >= 21) {
      TOP_SORTED_CHILDREN_COMPARATOR = new ViewElevationComparator();
    } else {
      TOP_SORTED_CHILDREN_COMPARATOR = null;
    } 
    CONSTRUCTOR_PARAMS = new Class[] { Context.class, AttributeSet.class };
    sConstructors = new ThreadLocal();
    sRectPool = new Pools.SynchronizedPool(12);
  }
  
  public CoordinatorLayout(Context paramContext) { this(paramContext, null); }
  
  public CoordinatorLayout(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, R.attr.coordinatorLayoutStyle); }
  
  public CoordinatorLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    int i = 0;
    if (paramInt == 0) {
      typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CoordinatorLayout, 0, R.style.Widget_Support_CoordinatorLayout);
    } else {
      typedArray = paramContext.obtainStyledAttributes(typedArray, R.styleable.CoordinatorLayout, paramInt, 0);
    } 
    paramInt = typedArray.getResourceId(R.styleable.CoordinatorLayout_keylines, 0);
    if (paramInt != 0) {
      Resources resources = paramContext.getResources();
      this.mKeylines = resources.getIntArray(paramInt);
      float f = (resources.getDisplayMetrics()).density;
      int j = this.mKeylines.length;
      for (paramInt = i; paramInt < j; paramInt++)
        this.mKeylines[paramInt] = (int)(this.mKeylines[paramInt] * f); 
    } 
    this.mStatusBarBackground = typedArray.getDrawable(R.styleable.CoordinatorLayout_statusBarBackground);
    typedArray.recycle();
    setupForInsets();
    super.setOnHierarchyChangeListener(new HierarchyChangeListener());
  }
  
  @NonNull
  private static Rect acquireTempRect() {
    Rect rect2 = (Rect)sRectPool.acquire();
    Rect rect1 = rect2;
    if (rect2 == null)
      rect1 = new Rect(); 
    return rect1;
  }
  
  private void constrainChildRect(LayoutParams paramLayoutParams, Rect paramRect, int paramInt1, int paramInt2) {
    int j = getWidth();
    int i = getHeight();
    j = Math.max(getPaddingLeft() + paramLayoutParams.leftMargin, Math.min(paramRect.left, j - getPaddingRight() - paramInt1 - paramLayoutParams.rightMargin));
    i = Math.max(getPaddingTop() + paramLayoutParams.topMargin, Math.min(paramRect.top, i - getPaddingBottom() - paramInt2 - paramLayoutParams.bottomMargin));
    paramRect.set(j, i, paramInt1 + j, paramInt2 + i);
  }
  
  private WindowInsetsCompat dispatchApplyWindowInsetsToBehaviors(WindowInsetsCompat paramWindowInsetsCompat) {
    if (paramWindowInsetsCompat.isConsumed())
      return paramWindowInsetsCompat; 
    byte b = 0;
    int i = getChildCount();
    WindowInsetsCompat windowInsetsCompat;
    for (windowInsetsCompat = paramWindowInsetsCompat; b < i; windowInsetsCompat = paramWindowInsetsCompat) {
      View view = getChildAt(b);
      paramWindowInsetsCompat = windowInsetsCompat;
      if (ViewCompat.getFitsSystemWindows(view)) {
        Behavior behavior = ((LayoutParams)view.getLayoutParams()).getBehavior();
        paramWindowInsetsCompat = windowInsetsCompat;
        if (behavior != null) {
          windowInsetsCompat = behavior.onApplyWindowInsets(this, view, windowInsetsCompat);
          paramWindowInsetsCompat = windowInsetsCompat;
          if (windowInsetsCompat.isConsumed())
            return windowInsetsCompat; 
        } 
      } 
      b++;
    } 
    return windowInsetsCompat;
  }
  
  private void getDesiredAnchoredChildRectWithoutConstraints(View paramView, int paramInt1, Rect paramRect1, Rect paramRect2, LayoutParams paramLayoutParams, int paramInt2, int paramInt3) {
    int i = GravityCompat.getAbsoluteGravity(resolveAnchoredChildGravity(paramLayoutParams.gravity), paramInt1);
    paramInt1 = GravityCompat.getAbsoluteGravity(resolveGravity(paramLayoutParams.anchorGravity), paramInt1);
    int m = i & 0x7;
    int k = i & 0x70;
    int j = paramInt1 & 0x7;
    i = paramInt1 & 0x70;
    if (j != 1) {
      if (j != 5) {
        paramInt1 = paramRect1.left;
      } else {
        paramInt1 = paramRect1.right;
      } 
    } else {
      paramInt1 = paramRect1.left + paramRect1.width() / 2;
    } 
    if (i != 16) {
      if (i != 80) {
        i = paramRect1.top;
      } else {
        i = paramRect1.bottom;
      } 
    } else {
      i = paramRect1.top + paramRect1.height() / 2;
    } 
    if (m != 1) {
      j = paramInt1;
      if (m != 5)
        j = paramInt1 - paramInt2; 
    } else {
      j = paramInt1 - paramInt2 / 2;
    } 
    if (k != 16) {
      paramInt1 = i;
      if (k != 80)
        paramInt1 = i - paramInt3; 
    } else {
      paramInt1 = i - paramInt3 / 2;
    } 
    paramRect2.set(j, paramInt1, paramInt2 + j, paramInt3 + paramInt1);
  }
  
  private int getKeyline(int paramInt) {
    if (this.mKeylines == null) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("No keylines defined for ");
      stringBuilder.append(this);
      stringBuilder.append(" - attempted index lookup ");
      stringBuilder.append(paramInt);
      Log.e("CoordinatorLayout", stringBuilder.toString());
      return 0;
    } 
    if (paramInt < 0 || paramInt >= this.mKeylines.length) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Keyline index ");
      stringBuilder.append(paramInt);
      stringBuilder.append(" out of range for ");
      stringBuilder.append(this);
      Log.e("CoordinatorLayout", stringBuilder.toString());
      return 0;
    } 
    return this.mKeylines[paramInt];
  }
  
  private void getTopSortedChildren(List<View> paramList) {
    paramList.clear();
    boolean bool = isChildrenDrawingOrderEnabled();
    int j = getChildCount();
    for (int i = j - 1; i >= 0; i--) {
      int k;
      if (bool) {
        k = getChildDrawingOrder(j, i);
      } else {
        k = i;
      } 
      paramList.add(getChildAt(k));
    } 
    if (TOP_SORTED_CHILDREN_COMPARATOR != null)
      Collections.sort(paramList, TOP_SORTED_CHILDREN_COMPARATOR); 
  }
  
  private boolean hasDependencies(View paramView) { return this.mChildDag.hasOutgoingEdges(paramView); }
  
  private void layoutChild(View paramView, int paramInt) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect rect1 = acquireTempRect();
    rect1.set(getPaddingLeft() + layoutParams.leftMargin, getPaddingTop() + layoutParams.topMargin, getWidth() - getPaddingRight() - layoutParams.rightMargin, getHeight() - getPaddingBottom() - layoutParams.bottomMargin);
    if (this.mLastInsets != null && ViewCompat.getFitsSystemWindows(this) && !ViewCompat.getFitsSystemWindows(paramView)) {
      rect1.left += this.mLastInsets.getSystemWindowInsetLeft();
      rect1.top += this.mLastInsets.getSystemWindowInsetTop();
      rect1.right -= this.mLastInsets.getSystemWindowInsetRight();
      rect1.bottom -= this.mLastInsets.getSystemWindowInsetBottom();
    } 
    Rect rect2 = acquireTempRect();
    GravityCompat.apply(resolveGravity(layoutParams.gravity), paramView.getMeasuredWidth(), paramView.getMeasuredHeight(), rect1, rect2, paramInt);
    paramView.layout(rect2.left, rect2.top, rect2.right, rect2.bottom);
    releaseTempRect(rect1);
    releaseTempRect(rect2);
  }
  
  private void layoutChildWithAnchor(View paramView1, View paramView2, int paramInt) {
    LayoutParams layoutParams = (LayoutParams)paramView1.getLayoutParams();
    rect1 = acquireTempRect();
    rect2 = acquireTempRect();
    try {
      getDescendantRect(paramView2, rect1);
      getDesiredAnchoredChildRect(paramView1, paramInt, rect1, rect2);
      paramView1.layout(rect2.left, rect2.top, rect2.right, rect2.bottom);
      return;
    } finally {
      releaseTempRect(rect1);
      releaseTempRect(rect2);
    } 
  }
  
  private void layoutChildWithKeyline(View paramView, int paramInt1, int paramInt2) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = GravityCompat.getAbsoluteGravity(resolveKeylineGravity(layoutParams.gravity), paramInt2);
    int i2 = i & 0x7;
    int i1 = i & 0x70;
    int n = getWidth();
    int m = getHeight();
    int j = paramView.getMeasuredWidth();
    int k = paramView.getMeasuredHeight();
    i = paramInt1;
    if (paramInt2 == 1)
      i = n - paramInt1; 
    paramInt1 = getKeyline(i) - j;
    paramInt2 = 0;
    if (i2 != 1) {
      if (i2 == 5)
        paramInt1 += j; 
    } else {
      paramInt1 += j / 2;
    } 
    if (i1 != 16) {
      if (i1 == 80)
        paramInt2 = k + 0; 
    } else {
      paramInt2 = 0 + k / 2;
    } 
    paramInt1 = Math.max(getPaddingLeft() + layoutParams.leftMargin, Math.min(paramInt1, n - getPaddingRight() - j - layoutParams.rightMargin));
    paramInt2 = Math.max(getPaddingTop() + layoutParams.topMargin, Math.min(paramInt2, m - getPaddingBottom() - k - layoutParams.bottomMargin));
    paramView.layout(paramInt1, paramInt2, j + paramInt1, k + paramInt2);
  }
  
  private void offsetChildByInset(View paramView, Rect paramRect, int paramInt) { // Byte code:
    //   0: aload_1
    //   1: invokestatic isLaidOut : (Landroid/view/View;)Z
    //   4: ifne -> 8
    //   7: return
    //   8: aload_1
    //   9: invokevirtual getWidth : ()I
    //   12: ifle -> 450
    //   15: aload_1
    //   16: invokevirtual getHeight : ()I
    //   19: ifgt -> 23
    //   22: return
    //   23: aload_1
    //   24: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   27: checkcast android/support/design/widget/CoordinatorLayout$LayoutParams
    //   30: astore #9
    //   32: aload #9
    //   34: invokevirtual getBehavior : ()Landroid/support/design/widget/CoordinatorLayout$Behavior;
    //   37: astore #10
    //   39: invokestatic acquireTempRect : ()Landroid/graphics/Rect;
    //   42: astore #7
    //   44: invokestatic acquireTempRect : ()Landroid/graphics/Rect;
    //   47: astore #8
    //   49: aload #8
    //   51: aload_1
    //   52: invokevirtual getLeft : ()I
    //   55: aload_1
    //   56: invokevirtual getTop : ()I
    //   59: aload_1
    //   60: invokevirtual getRight : ()I
    //   63: aload_1
    //   64: invokevirtual getBottom : ()I
    //   67: invokevirtual set : (IIII)V
    //   70: aload #10
    //   72: ifnull -> 153
    //   75: aload #10
    //   77: aload_0
    //   78: aload_1
    //   79: aload #7
    //   81: invokevirtual getInsetDodgeRect : (Landroid/support/design/widget/CoordinatorLayout;Landroid/view/View;Landroid/graphics/Rect;)Z
    //   84: ifeq -> 153
    //   87: aload #8
    //   89: aload #7
    //   91: invokevirtual contains : (Landroid/graphics/Rect;)Z
    //   94: ifne -> 160
    //   97: new java/lang/StringBuilder
    //   100: dup
    //   101: invokespecial <init> : ()V
    //   104: astore_1
    //   105: aload_1
    //   106: ldc_w 'Rect should be within the child's bounds. Rect:'
    //   109: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: pop
    //   113: aload_1
    //   114: aload #7
    //   116: invokevirtual toShortString : ()Ljava/lang/String;
    //   119: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   122: pop
    //   123: aload_1
    //   124: ldc_w ' | Bounds:'
    //   127: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   130: pop
    //   131: aload_1
    //   132: aload #8
    //   134: invokevirtual toShortString : ()Ljava/lang/String;
    //   137: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   140: pop
    //   141: new java/lang/IllegalArgumentException
    //   144: dup
    //   145: aload_1
    //   146: invokevirtual toString : ()Ljava/lang/String;
    //   149: invokespecial <init> : (Ljava/lang/String;)V
    //   152: athrow
    //   153: aload #7
    //   155: aload #8
    //   157: invokevirtual set : (Landroid/graphics/Rect;)V
    //   160: aload #8
    //   162: invokestatic releaseTempRect : (Landroid/graphics/Rect;)V
    //   165: aload #7
    //   167: invokevirtual isEmpty : ()Z
    //   170: ifeq -> 179
    //   173: aload #7
    //   175: invokestatic releaseTempRect : (Landroid/graphics/Rect;)V
    //   178: return
    //   179: aload #9
    //   181: getfield dodgeInsetEdges : I
    //   184: iload_3
    //   185: invokestatic getAbsoluteGravity : (II)I
    //   188: istore #5
    //   190: iload #5
    //   192: bipush #48
    //   194: iand
    //   195: bipush #48
    //   197: if_icmpne -> 242
    //   200: aload #7
    //   202: getfield top : I
    //   205: aload #9
    //   207: getfield topMargin : I
    //   210: isub
    //   211: aload #9
    //   213: getfield mInsetOffsetY : I
    //   216: isub
    //   217: istore_3
    //   218: iload_3
    //   219: aload_2
    //   220: getfield top : I
    //   223: if_icmpge -> 242
    //   226: aload_0
    //   227: aload_1
    //   228: aload_2
    //   229: getfield top : I
    //   232: iload_3
    //   233: isub
    //   234: invokespecial setInsetOffsetY : (Landroid/view/View;I)V
    //   237: iconst_1
    //   238: istore_3
    //   239: goto -> 244
    //   242: iconst_0
    //   243: istore_3
    //   244: iload_3
    //   245: istore #4
    //   247: iload #5
    //   249: bipush #80
    //   251: iand
    //   252: bipush #80
    //   254: if_icmpne -> 308
    //   257: aload_0
    //   258: invokevirtual getHeight : ()I
    //   261: aload #7
    //   263: getfield bottom : I
    //   266: isub
    //   267: aload #9
    //   269: getfield bottomMargin : I
    //   272: isub
    //   273: aload #9
    //   275: getfield mInsetOffsetY : I
    //   278: iadd
    //   279: istore #6
    //   281: iload_3
    //   282: istore #4
    //   284: iload #6
    //   286: aload_2
    //   287: getfield bottom : I
    //   290: if_icmpge -> 308
    //   293: aload_0
    //   294: aload_1
    //   295: iload #6
    //   297: aload_2
    //   298: getfield bottom : I
    //   301: isub
    //   302: invokespecial setInsetOffsetY : (Landroid/view/View;I)V
    //   305: iconst_1
    //   306: istore #4
    //   308: iload #4
    //   310: ifne -> 319
    //   313: aload_0
    //   314: aload_1
    //   315: iconst_0
    //   316: invokespecial setInsetOffsetY : (Landroid/view/View;I)V
    //   319: iload #5
    //   321: iconst_3
    //   322: iand
    //   323: iconst_3
    //   324: if_icmpne -> 369
    //   327: aload #7
    //   329: getfield left : I
    //   332: aload #9
    //   334: getfield leftMargin : I
    //   337: isub
    //   338: aload #9
    //   340: getfield mInsetOffsetX : I
    //   343: isub
    //   344: istore_3
    //   345: iload_3
    //   346: aload_2
    //   347: getfield left : I
    //   350: if_icmpge -> 369
    //   353: aload_0
    //   354: aload_1
    //   355: aload_2
    //   356: getfield left : I
    //   359: iload_3
    //   360: isub
    //   361: invokespecial setInsetOffsetX : (Landroid/view/View;I)V
    //   364: iconst_1
    //   365: istore_3
    //   366: goto -> 371
    //   369: iconst_0
    //   370: istore_3
    //   371: iload_3
    //   372: istore #4
    //   374: iload #5
    //   376: iconst_5
    //   377: iand
    //   378: iconst_5
    //   379: if_icmpne -> 433
    //   382: aload_0
    //   383: invokevirtual getWidth : ()I
    //   386: aload #7
    //   388: getfield right : I
    //   391: isub
    //   392: aload #9
    //   394: getfield rightMargin : I
    //   397: isub
    //   398: aload #9
    //   400: getfield mInsetOffsetX : I
    //   403: iadd
    //   404: istore #5
    //   406: iload_3
    //   407: istore #4
    //   409: iload #5
    //   411: aload_2
    //   412: getfield right : I
    //   415: if_icmpge -> 433
    //   418: aload_0
    //   419: aload_1
    //   420: iload #5
    //   422: aload_2
    //   423: getfield right : I
    //   426: isub
    //   427: invokespecial setInsetOffsetX : (Landroid/view/View;I)V
    //   430: iconst_1
    //   431: istore #4
    //   433: iload #4
    //   435: ifne -> 444
    //   438: aload_0
    //   439: aload_1
    //   440: iconst_0
    //   441: invokespecial setInsetOffsetX : (Landroid/view/View;I)V
    //   444: aload #7
    //   446: invokestatic releaseTempRect : (Landroid/graphics/Rect;)V
    //   449: return
    //   450: return }
  
  static Behavior parseBehavior(Context paramContext, AttributeSet paramAttributeSet, String paramString) {
    String str;
    if (TextUtils.isEmpty(paramString))
      return null; 
    if (paramString.startsWith(".")) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramContext.getPackageName());
      stringBuilder.append(paramString);
      str = stringBuilder.toString();
    } else if (paramString.indexOf('.') >= 0) {
      str = paramString;
    } else {
      str = paramString;
      if (!TextUtils.isEmpty(WIDGET_PACKAGE_NAME)) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(WIDGET_PACKAGE_NAME);
        stringBuilder.append('.');
        stringBuilder.append(paramString);
        str = stringBuilder.toString();
      } 
    } 
    try {
      Map map2 = (Map)sConstructors.get();
      Map map1 = map2;
      if (map2 == null) {
        map1 = new HashMap();
        sConstructors.set(map1);
      } 
      Constructor constructor2 = (Constructor)map1.get(str);
      Constructor constructor1 = constructor2;
      if (constructor2 == null) {
        constructor1 = paramContext.getClassLoader().loadClass(str).getConstructor(CONSTRUCTOR_PARAMS);
        constructor1.setAccessible(true);
        map1.put(str, constructor1);
      } 
      return (Behavior)constructor1.newInstance(new Object[] { paramContext, paramAttributeSet });
    } catch (Exception paramContext) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Could not inflate Behavior subclass ");
      stringBuilder.append(str);
      throw new RuntimeException(stringBuilder.toString(), paramContext);
    } 
  }
  
  private boolean performIntercept(MotionEvent paramMotionEvent, int paramInt) {
    boolean bool;
    int i = paramMotionEvent.getActionMasked();
    List list = this.mTempList1;
    getTopSortedChildren(list);
    int j = list.size();
    LayoutParams layoutParams = null;
    byte b2 = 0;
    byte b3 = 0;
    byte b1 = 0;
    while (true) {
      bool = b3;
      if (b2 < j) {
        boolean bool2;
        byte b;
        View view = (View)list.get(b2);
        LayoutParams layoutParams1 = (LayoutParams)view.getLayoutParams();
        Behavior behavior = layoutParams1.getBehavior();
        if ((b3 || b1) && i != 0) {
          bool2 = b3;
          b = b1;
          layoutParams1 = layoutParams;
          if (behavior != null) {
            layoutParams1 = layoutParams;
            if (layoutParams == null) {
              long l = SystemClock.uptimeMillis();
              layoutParams1 = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
            } 
            switch (paramInt) {
              default:
                bool2 = b3;
                b = b1;
                break;
              case 1:
                behavior.onTouchEvent(this, view, layoutParams1);
                bool2 = b3;
                b = b1;
                break;
              case 0:
                behavior.onInterceptTouchEvent(this, view, layoutParams1);
                bool2 = b3;
                b = b1;
                break;
            } 
          } 
        } else {
          bool = b3;
          if (b3 == 0) {
            bool = b3;
            if (behavior != null) {
              boolean bool4;
              switch (paramInt) {
                case 1:
                  bool4 = behavior.onTouchEvent(this, view, paramMotionEvent);
                  break;
                case 0:
                  bool4 = behavior.onInterceptTouchEvent(this, view, paramMotionEvent);
                  break;
              } 
              bool = bool4;
              if (bool4) {
                this.mBehaviorTouchView = view;
                bool = bool4;
              } 
            } 
          } 
          bool2 = layoutParams1.didBlockInteraction();
          boolean bool3 = layoutParams1.isBlockingInteractionBelow(this, view);
          if (bool3 && !bool2) {
            b1 = 1;
          } else {
            b1 = 0;
          } 
          bool2 = bool;
          b = b1;
          layoutParams1 = layoutParams;
          if (bool3) {
            bool2 = bool;
            b = b1;
            layoutParams1 = layoutParams;
            if (b1 == 0)
              break; 
          } 
        } 
        b2++;
        boolean bool1 = bool2;
        b1 = b;
        layoutParams = layoutParams1;
        continue;
      } 
      break;
    } 
    list.clear();
    return bool;
  }
  
  private void prepareChildren() {
    this.mDependencySortedChildren.clear();
    this.mChildDag.clear();
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = getChildAt(b);
      LayoutParams layoutParams = getResolvedLayoutParams(view);
      layoutParams.findAnchorView(this, view);
      this.mChildDag.addNode(view);
      for (byte b1 = 0; b1 < i; b1++) {
        if (b1 != b) {
          View view1 = getChildAt(b1);
          if (layoutParams.dependsOn(this, view, view1)) {
            if (!this.mChildDag.contains(view1))
              this.mChildDag.addNode(view1); 
            this.mChildDag.addEdge(view1, view);
          } 
        } 
      } 
    } 
    this.mDependencySortedChildren.addAll(this.mChildDag.getSortedList());
    Collections.reverse(this.mDependencySortedChildren);
  }
  
  private static void releaseTempRect(@NonNull Rect paramRect) {
    paramRect.setEmpty();
    sRectPool.release(paramRect);
  }
  
  private void resetTouchBehaviors(boolean paramBoolean) {
    int i = getChildCount();
    byte b;
    for (b = 0; b < i; b++) {
      View view = getChildAt(b);
      Behavior behavior = ((LayoutParams)view.getLayoutParams()).getBehavior();
      if (behavior != null) {
        long l = SystemClock.uptimeMillis();
        MotionEvent motionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
        if (paramBoolean) {
          behavior.onInterceptTouchEvent(this, view, motionEvent);
        } else {
          behavior.onTouchEvent(this, view, motionEvent);
        } 
        motionEvent.recycle();
      } 
    } 
    for (b = 0; b < i; b++)
      ((LayoutParams)getChildAt(b).getLayoutParams()).resetTouchBehaviorTracking(); 
    this.mBehaviorTouchView = null;
    this.mDisallowInterceptReset = false;
  }
  
  private static int resolveAnchoredChildGravity(int paramInt) {
    int i = paramInt;
    if (paramInt == 0)
      i = 17; 
    return i;
  }
  
  private static int resolveGravity(int paramInt) {
    int i = paramInt;
    if ((paramInt & 0x7) == 0)
      i = paramInt | 0x800003; 
    paramInt = i;
    if ((i & 0x70) == 0)
      paramInt = i | 0x30; 
    return paramInt;
  }
  
  private static int resolveKeylineGravity(int paramInt) {
    int i = paramInt;
    if (paramInt == 0)
      i = 8388661; 
    return i;
  }
  
  private void setInsetOffsetX(View paramView, int paramInt) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if (layoutParams.mInsetOffsetX != paramInt) {
      ViewCompat.offsetLeftAndRight(paramView, paramInt - layoutParams.mInsetOffsetX);
      layoutParams.mInsetOffsetX = paramInt;
    } 
  }
  
  private void setInsetOffsetY(View paramView, int paramInt) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if (layoutParams.mInsetOffsetY != paramInt) {
      ViewCompat.offsetTopAndBottom(paramView, paramInt - layoutParams.mInsetOffsetY);
      layoutParams.mInsetOffsetY = paramInt;
    } 
  }
  
  private void setupForInsets() {
    if (Build.VERSION.SDK_INT < 21)
      return; 
    if (ViewCompat.getFitsSystemWindows(this)) {
      if (this.mApplyWindowInsetsListener == null)
        this.mApplyWindowInsetsListener = new OnApplyWindowInsetsListener() {
            public WindowInsetsCompat onApplyWindowInsets(View param1View, WindowInsetsCompat param1WindowInsetsCompat) { return CoordinatorLayout.this.setWindowInsets(param1WindowInsetsCompat); }
          }; 
      ViewCompat.setOnApplyWindowInsetsListener(this, this.mApplyWindowInsetsListener);
      setSystemUiVisibility(1280);
      return;
    } 
    ViewCompat.setOnApplyWindowInsetsListener(this, null);
  }
  
  void addPreDrawListener() {
    if (this.mIsAttachedToWindow) {
      if (this.mOnPreDrawListener == null)
        this.mOnPreDrawListener = new OnPreDrawListener(); 
      getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
    } 
    this.mNeedsPreDrawListener = true;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams instanceof LayoutParams && super.checkLayoutParams(paramLayoutParams)); }
  
  public void dispatchDependentViewsChanged(View paramView) {
    List list = this.mChildDag.getIncomingEdges(paramView);
    if (list != null && !list.isEmpty())
      for (byte b = 0; b < list.size(); b++) {
        View view = (View)list.get(b);
        Behavior behavior = ((LayoutParams)view.getLayoutParams()).getBehavior();
        if (behavior != null)
          behavior.onDependentViewChanged(this, view, paramView); 
      }  
  }
  
  public boolean doViewsOverlap(View paramView1, View paramView2) {
    int i = paramView1.getVisibility();
    boolean bool = false;
    if (i == 0 && paramView2.getVisibility() == 0) {
      rect2 = acquireTempRect();
      if (paramView1.getParent() != this) {
        bool1 = true;
      } else {
        bool1 = false;
      } 
      getChildRect(paramView1, bool1, rect2);
      rect1 = acquireTempRect();
      if (paramView2.getParent() != this) {
        bool1 = true;
      } else {
        bool1 = false;
      } 
      getChildRect(paramView2, bool1, rect1);
      bool1 = bool;
      try {
        if (rect2.left <= rect1.right) {
          bool1 = bool;
          if (rect2.top <= rect1.bottom) {
            bool1 = bool;
            if (rect2.right >= rect1.left) {
              i = rect2.bottom;
              int j = rect1.top;
              bool1 = bool;
              if (i >= j)
                bool1 = true; 
            } 
          } 
        } 
        return bool1;
      } finally {
        releaseTempRect(rect2);
        releaseTempRect(rect1);
      } 
    } 
    return false;
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if (layoutParams.mBehavior != null) {
      float f = layoutParams.mBehavior.getScrimOpacity(this, paramView);
      if (f > 0.0F) {
        if (this.mScrimPaint == null)
          this.mScrimPaint = new Paint(); 
        this.mScrimPaint.setColor(layoutParams.mBehavior.getScrimColor(this, paramView));
        this.mScrimPaint.setAlpha(MathUtils.clamp(Math.round(f * 255.0F), 0, 255));
        int i = paramCanvas.save();
        if (paramView.isOpaque())
          paramCanvas.clipRect(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom(), Region.Op.DIFFERENCE); 
        paramCanvas.drawRect(getPaddingLeft(), getPaddingTop(), (getWidth() - getPaddingRight()), (getHeight() - getPaddingBottom()), this.mScrimPaint);
        paramCanvas.restoreToCount(i);
      } 
    } 
    return super.drawChild(paramCanvas, paramView, paramLong);
  }
  
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    Drawable drawable = this.mStatusBarBackground;
    byte b = 0;
    boolean bool = b;
    if (drawable != null) {
      bool = b;
      if (drawable.isStateful())
        bool = false | drawable.setState(arrayOfInt); 
    } 
    if (bool)
      invalidate(); 
  }
  
  void ensurePreDrawListener() {
    boolean bool1;
    int i = getChildCount();
    boolean bool2 = false;
    byte b = 0;
    while (true) {
      bool1 = bool2;
      if (b < i) {
        if (hasDependencies(getChildAt(b))) {
          bool1 = true;
          break;
        } 
        b++;
        continue;
      } 
      break;
    } 
    if (bool1 != this.mNeedsPreDrawListener) {
      if (bool1) {
        addPreDrawListener();
        return;
      } 
      removePreDrawListener();
    } 
  }
  
  protected LayoutParams generateDefaultLayoutParams() { return new LayoutParams(-2, -2); }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return new LayoutParams(getContext(), paramAttributeSet); }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams instanceof LayoutParams) ? new LayoutParams((LayoutParams)paramLayoutParams) : ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams) ? new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams) : new LayoutParams(paramLayoutParams)); }
  
  void getChildRect(View paramView, boolean paramBoolean, Rect paramRect) {
    if (paramView.isLayoutRequested() || paramView.getVisibility() == 8) {
      paramRect.setEmpty();
      return;
    } 
    if (paramBoolean) {
      getDescendantRect(paramView, paramRect);
      return;
    } 
    paramRect.set(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
  }
  
  @NonNull
  public List<View> getDependencies(@NonNull View paramView) {
    List list = this.mChildDag.getOutgoingEdges(paramView);
    this.mTempDependenciesList.clear();
    if (list != null)
      this.mTempDependenciesList.addAll(list); 
    return this.mTempDependenciesList;
  }
  
  @VisibleForTesting
  final List<View> getDependencySortedChildren() {
    prepareChildren();
    return Collections.unmodifiableList(this.mDependencySortedChildren);
  }
  
  @NonNull
  public List<View> getDependents(@NonNull View paramView) {
    List list = this.mChildDag.getIncomingEdges(paramView);
    this.mTempDependenciesList.clear();
    if (list != null)
      this.mTempDependenciesList.addAll(list); 
    return this.mTempDependenciesList;
  }
  
  void getDescendantRect(View paramView, Rect paramRect) { ViewGroupUtils.getDescendantRect(this, paramView, paramRect); }
  
  void getDesiredAnchoredChildRect(View paramView, int paramInt, Rect paramRect1, Rect paramRect2) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = paramView.getMeasuredWidth();
    int j = paramView.getMeasuredHeight();
    getDesiredAnchoredChildRectWithoutConstraints(paramView, paramInt, paramRect1, paramRect2, layoutParams, i, j);
    constrainChildRect(layoutParams, paramRect2, i, j);
  }
  
  void getLastChildRect(View paramView, Rect paramRect) { paramRect.set(((LayoutParams)paramView.getLayoutParams()).getLastChildRect()); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public final WindowInsetsCompat getLastWindowInsets() { return this.mLastInsets; }
  
  public int getNestedScrollAxes() { return this.mNestedScrollingParentHelper.getNestedScrollAxes(); }
  
  LayoutParams getResolvedLayoutParams(View paramView) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if (!layoutParams.mBehaviorResolved) {
      if (paramView instanceof AttachedBehavior) {
        defaultBehavior = ((AttachedBehavior)paramView).getBehavior();
        if (defaultBehavior == null)
          Log.e("CoordinatorLayout", "Attached behavior class is null"); 
        layoutParams.setBehavior(defaultBehavior);
        layoutParams.mBehaviorResolved = true;
        return layoutParams;
      } 
      clazz = defaultBehavior.getClass();
      DefaultBehavior defaultBehavior = null;
      while (clazz != null) {
        DefaultBehavior defaultBehavior1 = (DefaultBehavior)clazz.getAnnotation(DefaultBehavior.class);
        defaultBehavior = defaultBehavior1;
        if (defaultBehavior1 == null) {
          clazz = clazz.getSuperclass();
          defaultBehavior = defaultBehavior1;
        } 
      } 
      if (defaultBehavior != null)
        try {
          layoutParams.setBehavior((Behavior)defaultBehavior.value().getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
        } catch (Exception clazz) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("Default behavior class ");
          stringBuilder.append(defaultBehavior.value().getName());
          stringBuilder.append(" could not be instantiated. Did you forget");
          stringBuilder.append(" a default constructor?");
          Log.e("CoordinatorLayout", stringBuilder.toString(), clazz);
        }  
      layoutParams.mBehaviorResolved = true;
    } 
    return layoutParams;
  }
  
  @Nullable
  public Drawable getStatusBarBackground() { return this.mStatusBarBackground; }
  
  protected int getSuggestedMinimumHeight() { return Math.max(super.getSuggestedMinimumHeight(), getPaddingTop() + getPaddingBottom()); }
  
  protected int getSuggestedMinimumWidth() { return Math.max(super.getSuggestedMinimumWidth(), getPaddingLeft() + getPaddingRight()); }
  
  public boolean isPointInChildBounds(View paramView, int paramInt1, int paramInt2) {
    rect = acquireTempRect();
    getDescendantRect(paramView, rect);
    try {
      return rect.contains(paramInt1, paramInt2);
    } finally {
      releaseTempRect(rect);
    } 
  }
  
  void offsetChildToAnchor(View paramView, int paramInt) { // Byte code:
    //   0: aload_1
    //   1: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   4: checkcast android/support/design/widget/CoordinatorLayout$LayoutParams
    //   7: astore #6
    //   9: aload #6
    //   11: getfield mAnchorView : Landroid/view/View;
    //   14: ifnull -> 212
    //   17: invokestatic acquireTempRect : ()Landroid/graphics/Rect;
    //   20: astore #7
    //   22: invokestatic acquireTempRect : ()Landroid/graphics/Rect;
    //   25: astore #8
    //   27: invokestatic acquireTempRect : ()Landroid/graphics/Rect;
    //   30: astore #9
    //   32: aload_0
    //   33: aload #6
    //   35: getfield mAnchorView : Landroid/view/View;
    //   38: aload #7
    //   40: invokevirtual getDescendantRect : (Landroid/view/View;Landroid/graphics/Rect;)V
    //   43: iconst_0
    //   44: istore_3
    //   45: aload_0
    //   46: aload_1
    //   47: iconst_0
    //   48: aload #8
    //   50: invokevirtual getChildRect : (Landroid/view/View;ZLandroid/graphics/Rect;)V
    //   53: aload_1
    //   54: invokevirtual getMeasuredWidth : ()I
    //   57: istore #4
    //   59: aload_1
    //   60: invokevirtual getMeasuredHeight : ()I
    //   63: istore #5
    //   65: aload_0
    //   66: aload_1
    //   67: iload_2
    //   68: aload #7
    //   70: aload #9
    //   72: aload #6
    //   74: iload #4
    //   76: iload #5
    //   78: invokespecial getDesiredAnchoredChildRectWithoutConstraints : (Landroid/view/View;ILandroid/graphics/Rect;Landroid/graphics/Rect;Landroid/support/design/widget/CoordinatorLayout$LayoutParams;II)V
    //   81: aload #9
    //   83: getfield left : I
    //   86: aload #8
    //   88: getfield left : I
    //   91: if_icmpne -> 109
    //   94: iload_3
    //   95: istore_2
    //   96: aload #9
    //   98: getfield top : I
    //   101: aload #8
    //   103: getfield top : I
    //   106: if_icmpeq -> 111
    //   109: iconst_1
    //   110: istore_2
    //   111: aload_0
    //   112: aload #6
    //   114: aload #9
    //   116: iload #4
    //   118: iload #5
    //   120: invokespecial constrainChildRect : (Landroid/support/design/widget/CoordinatorLayout$LayoutParams;Landroid/graphics/Rect;II)V
    //   123: aload #9
    //   125: getfield left : I
    //   128: aload #8
    //   130: getfield left : I
    //   133: isub
    //   134: istore_3
    //   135: aload #9
    //   137: getfield top : I
    //   140: aload #8
    //   142: getfield top : I
    //   145: isub
    //   146: istore #4
    //   148: iload_3
    //   149: ifeq -> 157
    //   152: aload_1
    //   153: iload_3
    //   154: invokestatic offsetLeftAndRight : (Landroid/view/View;I)V
    //   157: iload #4
    //   159: ifeq -> 168
    //   162: aload_1
    //   163: iload #4
    //   165: invokestatic offsetTopAndBottom : (Landroid/view/View;I)V
    //   168: iload_2
    //   169: ifeq -> 197
    //   172: aload #6
    //   174: invokevirtual getBehavior : ()Landroid/support/design/widget/CoordinatorLayout$Behavior;
    //   177: astore #10
    //   179: aload #10
    //   181: ifnull -> 197
    //   184: aload #10
    //   186: aload_0
    //   187: aload_1
    //   188: aload #6
    //   190: getfield mAnchorView : Landroid/view/View;
    //   193: invokevirtual onDependentViewChanged : (Landroid/support/design/widget/CoordinatorLayout;Landroid/view/View;Landroid/view/View;)Z
    //   196: pop
    //   197: aload #7
    //   199: invokestatic releaseTempRect : (Landroid/graphics/Rect;)V
    //   202: aload #8
    //   204: invokestatic releaseTempRect : (Landroid/graphics/Rect;)V
    //   207: aload #9
    //   209: invokestatic releaseTempRect : (Landroid/graphics/Rect;)V
    //   212: return }
  
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    resetTouchBehaviors(false);
    if (this.mNeedsPreDrawListener) {
      if (this.mOnPreDrawListener == null)
        this.mOnPreDrawListener = new OnPreDrawListener(); 
      getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
    } 
    if (this.mLastInsets == null && ViewCompat.getFitsSystemWindows(this))
      ViewCompat.requestApplyInsets(this); 
    this.mIsAttachedToWindow = true;
  }
  
  final void onChildViewsChanged(int paramInt) {
    int i = ViewCompat.getLayoutDirection(this);
    int j = this.mDependencySortedChildren.size();
    Rect rect1 = acquireTempRect();
    Rect rect2 = acquireTempRect();
    Rect rect3 = acquireTempRect();
    for (byte b = 0; b < j; b++) {
      View view = (View)this.mDependencySortedChildren.get(b);
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      if (paramInt == 0 && view.getVisibility() == 8)
        continue; 
      int k;
      for (k = 0; k < b; k++) {
        View view1 = (View)this.mDependencySortedChildren.get(k);
        if (layoutParams.mAnchorDirectChild == view1)
          offsetChildToAnchor(view, i); 
      } 
      getChildRect(view, true, rect2);
      if (layoutParams.insetEdge != 0 && !rect2.isEmpty()) {
        k = GravityCompat.getAbsoluteGravity(layoutParams.insetEdge, i);
        int m = k & 0x70;
        if (m != 48) {
          if (m == 80)
            rect1.bottom = Math.max(rect1.bottom, getHeight() - rect2.top); 
        } else {
          rect1.top = Math.max(rect1.top, rect2.bottom);
        } 
        k &= 0x7;
        if (k != 3) {
          if (k == 5)
            rect1.right = Math.max(rect1.right, getWidth() - rect2.left); 
        } else {
          rect1.left = Math.max(rect1.left, rect2.right);
        } 
      } 
      if (layoutParams.dodgeInsetEdges != 0 && view.getVisibility() == 0)
        offsetChildByInset(view, rect1, i); 
      if (paramInt != 2) {
        getLastChildRect(view, rect3);
        if (rect3.equals(rect2))
          continue; 
        recordLastChildRect(view, rect2);
      } 
      for (k = b + 1; k < j; k++) {
        View view1 = (View)this.mDependencySortedChildren.get(k);
        LayoutParams layoutParams1 = (LayoutParams)view1.getLayoutParams();
        Behavior behavior = layoutParams1.getBehavior();
        if (behavior != null && behavior.layoutDependsOn(this, view1, view))
          if (paramInt == 0 && layoutParams1.getChangedAfterNestedScroll()) {
            layoutParams1.resetChangedAfterNestedScroll();
          } else {
            boolean bool;
            if (paramInt != 2) {
              bool = behavior.onDependentViewChanged(this, view1, view);
            } else {
              behavior.onDependentViewRemoved(this, view1, view);
              bool = true;
            } 
            if (paramInt == 1)
              layoutParams1.setChangedAfterNestedScroll(bool); 
          }  
      } 
      continue;
    } 
    releaseTempRect(rect1);
    releaseTempRect(rect2);
    releaseTempRect(rect3);
  }
  
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    resetTouchBehaviors(false);
    if (this.mNeedsPreDrawListener && this.mOnPreDrawListener != null)
      getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener); 
    if (this.mNestedScrollingTarget != null)
      onStopNestedScroll(this.mNestedScrollingTarget); 
    this.mIsAttachedToWindow = false;
  }
  
  public void onDraw(Canvas paramCanvas) {
    super.onDraw(paramCanvas);
    if (this.mDrawStatusBarBackground && this.mStatusBarBackground != null) {
      byte b;
      if (this.mLastInsets != null) {
        b = this.mLastInsets.getSystemWindowInsetTop();
      } else {
        b = 0;
      } 
      if (b) {
        this.mStatusBarBackground.setBounds(0, 0, getWidth(), b);
        this.mStatusBarBackground.draw(paramCanvas);
      } 
    } 
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
    int i = paramMotionEvent.getActionMasked();
    if (i == 0)
      resetTouchBehaviors(true); 
    boolean bool = performIntercept(paramMotionEvent, 0);
    if (i == 1 || i == 3)
      resetTouchBehaviors(true); 
    return bool;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramInt2 = ViewCompat.getLayoutDirection(this);
    paramInt3 = this.mDependencySortedChildren.size();
    for (paramInt1 = 0; paramInt1 < paramInt3; paramInt1++) {
      View view = (View)this.mDependencySortedChildren.get(paramInt1);
      if (view.getVisibility() != 8) {
        Behavior behavior = ((LayoutParams)view.getLayoutParams()).getBehavior();
        if (behavior == null || !behavior.onLayoutChild(this, view, paramInt2))
          onLayoutChild(view, paramInt2); 
      } 
    } 
  }
  
  public void onLayoutChild(View paramView, int paramInt) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if (layoutParams.checkAnchorChanged())
      throw new IllegalStateException("An anchor may not be changed after CoordinatorLayout measurement begins before layout is complete."); 
    if (layoutParams.mAnchorView != null) {
      layoutChildWithAnchor(paramView, layoutParams.mAnchorView, paramInt);
      return;
    } 
    if (layoutParams.keyline >= 0) {
      layoutChildWithKeyline(paramView, layoutParams.keyline, paramInt);
      return;
    } 
    layoutChild(paramView, paramInt);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: invokespecial prepareChildren : ()V
    //   4: aload_0
    //   5: invokevirtual ensurePreDrawListener : ()V
    //   8: aload_0
    //   9: invokevirtual getPaddingLeft : ()I
    //   12: istore #13
    //   14: aload_0
    //   15: invokevirtual getPaddingTop : ()I
    //   18: istore #14
    //   20: aload_0
    //   21: invokevirtual getPaddingRight : ()I
    //   24: istore #15
    //   26: aload_0
    //   27: invokevirtual getPaddingBottom : ()I
    //   30: istore #16
    //   32: aload_0
    //   33: invokestatic getLayoutDirection : (Landroid/view/View;)I
    //   36: istore #17
    //   38: iload #17
    //   40: iconst_1
    //   41: if_icmpne -> 50
    //   44: iconst_1
    //   45: istore #6
    //   47: goto -> 53
    //   50: iconst_0
    //   51: istore #6
    //   53: iload_1
    //   54: invokestatic getMode : (I)I
    //   57: istore #18
    //   59: iload_1
    //   60: invokestatic getSize : (I)I
    //   63: istore #19
    //   65: iload_2
    //   66: invokestatic getMode : (I)I
    //   69: istore #20
    //   71: iload_2
    //   72: invokestatic getSize : (I)I
    //   75: istore #21
    //   77: aload_0
    //   78: invokevirtual getSuggestedMinimumWidth : ()I
    //   81: istore #10
    //   83: aload_0
    //   84: invokevirtual getSuggestedMinimumHeight : ()I
    //   87: istore #4
    //   89: aload_0
    //   90: getfield mLastInsets : Landroid/support/v4/view/WindowInsetsCompat;
    //   93: ifnull -> 109
    //   96: aload_0
    //   97: invokestatic getFitsSystemWindows : (Landroid/view/View;)Z
    //   100: ifeq -> 109
    //   103: iconst_1
    //   104: istore #7
    //   106: goto -> 112
    //   109: iconst_0
    //   110: istore #7
    //   112: aload_0
    //   113: getfield mDependencySortedChildren : Ljava/util/List;
    //   116: invokeinterface size : ()I
    //   121: istore #8
    //   123: iconst_0
    //   124: istore #5
    //   126: iconst_0
    //   127: istore #9
    //   129: iload #9
    //   131: iload #8
    //   133: if_icmpge -> 516
    //   136: aload_0
    //   137: getfield mDependencySortedChildren : Ljava/util/List;
    //   140: iload #9
    //   142: invokeinterface get : (I)Ljava/lang/Object;
    //   147: checkcast android/view/View
    //   150: astore #24
    //   152: aload #24
    //   154: invokevirtual getVisibility : ()I
    //   157: bipush #8
    //   159: if_icmpne -> 165
    //   162: goto -> 507
    //   165: aload #24
    //   167: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   170: checkcast android/support/design/widget/CoordinatorLayout$LayoutParams
    //   173: astore #25
    //   175: aload #25
    //   177: getfield keyline : I
    //   180: iflt -> 287
    //   183: iload #18
    //   185: ifeq -> 287
    //   188: aload_0
    //   189: aload #25
    //   191: getfield keyline : I
    //   194: invokespecial getKeyline : (I)I
    //   197: istore_3
    //   198: aload #25
    //   200: getfield gravity : I
    //   203: invokestatic resolveKeylineGravity : (I)I
    //   206: iload #17
    //   208: invokestatic getAbsoluteGravity : (II)I
    //   211: bipush #7
    //   213: iand
    //   214: istore #11
    //   216: iload #11
    //   218: iconst_3
    //   219: if_icmpne -> 227
    //   222: iload #6
    //   224: ifeq -> 238
    //   227: iload #11
    //   229: iconst_5
    //   230: if_icmpne -> 253
    //   233: iload #6
    //   235: ifeq -> 253
    //   238: iconst_0
    //   239: iload #19
    //   241: iload #15
    //   243: isub
    //   244: iload_3
    //   245: isub
    //   246: invokestatic max : (II)I
    //   249: istore_3
    //   250: goto -> 289
    //   253: iload #11
    //   255: iconst_5
    //   256: if_icmpne -> 264
    //   259: iload #6
    //   261: ifeq -> 275
    //   264: iload #11
    //   266: iconst_3
    //   267: if_icmpne -> 287
    //   270: iload #6
    //   272: ifeq -> 287
    //   275: iconst_0
    //   276: iload_3
    //   277: iload #13
    //   279: isub
    //   280: invokestatic max : (II)I
    //   283: istore_3
    //   284: goto -> 289
    //   287: iconst_0
    //   288: istore_3
    //   289: iload #5
    //   291: istore #11
    //   293: iload #4
    //   295: istore #12
    //   297: iload #7
    //   299: ifeq -> 379
    //   302: aload #24
    //   304: invokestatic getFitsSystemWindows : (Landroid/view/View;)Z
    //   307: ifne -> 379
    //   310: aload_0
    //   311: getfield mLastInsets : Landroid/support/v4/view/WindowInsetsCompat;
    //   314: invokevirtual getSystemWindowInsetLeft : ()I
    //   317: istore #4
    //   319: aload_0
    //   320: getfield mLastInsets : Landroid/support/v4/view/WindowInsetsCompat;
    //   323: invokevirtual getSystemWindowInsetRight : ()I
    //   326: istore #23
    //   328: aload_0
    //   329: getfield mLastInsets : Landroid/support/v4/view/WindowInsetsCompat;
    //   332: invokevirtual getSystemWindowInsetTop : ()I
    //   335: istore #5
    //   337: aload_0
    //   338: getfield mLastInsets : Landroid/support/v4/view/WindowInsetsCompat;
    //   341: invokevirtual getSystemWindowInsetBottom : ()I
    //   344: istore #22
    //   346: iload #19
    //   348: iload #4
    //   350: iload #23
    //   352: iadd
    //   353: isub
    //   354: iload #18
    //   356: invokestatic makeMeasureSpec : (II)I
    //   359: istore #4
    //   361: iload #21
    //   363: iload #5
    //   365: iload #22
    //   367: iadd
    //   368: isub
    //   369: iload #20
    //   371: invokestatic makeMeasureSpec : (II)I
    //   374: istore #5
    //   376: goto -> 385
    //   379: iload_1
    //   380: istore #4
    //   382: iload_2
    //   383: istore #5
    //   385: aload #25
    //   387: invokevirtual getBehavior : ()Landroid/support/design/widget/CoordinatorLayout$Behavior;
    //   390: astore #26
    //   392: aload #26
    //   394: ifnull -> 420
    //   397: aload #26
    //   399: aload_0
    //   400: aload #24
    //   402: iload #4
    //   404: iload_3
    //   405: iload #5
    //   407: iconst_0
    //   408: invokevirtual onMeasureChild : (Landroid/support/design/widget/CoordinatorLayout;Landroid/view/View;IIII)Z
    //   411: ifne -> 417
    //   414: goto -> 420
    //   417: goto -> 435
    //   420: aload_0
    //   421: aload #24
    //   423: iload #4
    //   425: iload_3
    //   426: iload #5
    //   428: iconst_0
    //   429: invokevirtual onMeasureChild : (Landroid/view/View;IIII)V
    //   432: goto -> 417
    //   435: iload #10
    //   437: iload #13
    //   439: iload #15
    //   441: iadd
    //   442: aload #24
    //   444: invokevirtual getMeasuredWidth : ()I
    //   447: iadd
    //   448: aload #25
    //   450: getfield leftMargin : I
    //   453: iadd
    //   454: aload #25
    //   456: getfield rightMargin : I
    //   459: iadd
    //   460: invokestatic max : (II)I
    //   463: istore #10
    //   465: iload #12
    //   467: iload #14
    //   469: iload #16
    //   471: iadd
    //   472: aload #24
    //   474: invokevirtual getMeasuredHeight : ()I
    //   477: iadd
    //   478: aload #25
    //   480: getfield topMargin : I
    //   483: iadd
    //   484: aload #25
    //   486: getfield bottomMargin : I
    //   489: iadd
    //   490: invokestatic max : (II)I
    //   493: istore #4
    //   495: iload #11
    //   497: aload #24
    //   499: invokevirtual getMeasuredState : ()I
    //   502: invokestatic combineMeasuredStates : (II)I
    //   505: istore #5
    //   507: iload #9
    //   509: iconst_1
    //   510: iadd
    //   511: istore #9
    //   513: goto -> 129
    //   516: aload_0
    //   517: iload #10
    //   519: iload_1
    //   520: ldc_w -16777216
    //   523: iload #5
    //   525: iand
    //   526: invokestatic resolveSizeAndState : (III)I
    //   529: iload #4
    //   531: iload_2
    //   532: iload #5
    //   534: bipush #16
    //   536: ishl
    //   537: invokestatic resolveSizeAndState : (III)I
    //   540: invokevirtual setMeasuredDimension : (II)V
    //   543: return }
  
  public void onMeasureChild(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { measureChildWithMargins(paramView, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean) {
    int i = getChildCount();
    byte b = 0;
    boolean bool;
    for (bool = false; b < i; bool = bool1) {
      boolean bool1;
      View view = getChildAt(b);
      if (view.getVisibility() == 8) {
        bool1 = bool;
      } else {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (!layoutParams.isNestedScrollAccepted(0)) {
          bool1 = bool;
        } else {
          Behavior behavior = layoutParams.getBehavior();
          bool1 = bool;
          if (behavior != null)
            bool1 = bool | behavior.onNestedFling(this, view, paramView, paramFloat1, paramFloat2, paramBoolean); 
        } 
      } 
      b++;
    } 
    if (bool)
      onChildViewsChanged(1); 
    return bool;
  }
  
  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2) {
    int i = getChildCount();
    byte b = 0;
    boolean bool;
    for (bool = false; b < i; bool = bool1) {
      boolean bool1;
      View view = getChildAt(b);
      if (view.getVisibility() == 8) {
        bool1 = bool;
      } else {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (!layoutParams.isNestedScrollAccepted(0)) {
          bool1 = bool;
        } else {
          Behavior behavior = layoutParams.getBehavior();
          bool1 = bool;
          if (behavior != null)
            bool1 = bool | behavior.onNestedPreFling(this, view, paramView, paramFloat1, paramFloat2); 
        } 
      } 
      b++;
    } 
    return bool;
  }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt) { onNestedPreScroll(paramView, paramInt1, paramInt2, paramArrayOfInt, 0); }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3) {
    int k = getChildCount();
    boolean bool = false;
    byte b = 0;
    int j = 0;
    int i;
    for (i = 0; b < k; i = m) {
      int n;
      int m;
      View view = getChildAt(b);
      if (view.getVisibility() == 8) {
        n = j;
        m = i;
      } else {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (!layoutParams.isNestedScrollAccepted(paramInt3)) {
          n = j;
          m = i;
        } else {
          Behavior behavior = layoutParams.getBehavior();
          n = j;
          m = i;
          if (behavior != null) {
            int[] arrayOfInt = this.mTempIntPair;
            this.mTempIntPair[1] = 0;
            arrayOfInt[0] = 0;
            behavior.onNestedPreScroll(this, view, paramView, paramInt1, paramInt2, this.mTempIntPair, paramInt3);
            if (paramInt1 > 0) {
              m = Math.max(j, this.mTempIntPair[0]);
            } else {
              m = Math.min(j, this.mTempIntPair[0]);
            } 
            if (paramInt2 > 0) {
              i = Math.max(i, this.mTempIntPair[1]);
            } else {
              i = Math.min(i, this.mTempIntPair[1]);
            } 
            n = m;
            m = i;
            bool = true;
          } 
        } 
      } 
      b++;
      j = n;
    } 
    paramArrayOfInt[0] = j;
    paramArrayOfInt[1] = i;
    if (bool)
      onChildViewsChanged(1); 
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { onNestedScroll(paramView, paramInt1, paramInt2, paramInt3, paramInt4, 0); }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    int i = getChildCount();
    boolean bool = false;
    byte b;
    for (b = 0; b < i; b++) {
      View view = getChildAt(b);
      if (view.getVisibility() != 8) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.isNestedScrollAccepted(paramInt5)) {
          Behavior behavior = layoutParams.getBehavior();
          if (behavior != null) {
            behavior.onNestedScroll(this, view, paramView, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
            bool = true;
          } 
        } 
      } 
    } 
    if (bool)
      onChildViewsChanged(1); 
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt) { onNestedScrollAccepted(paramView1, paramView2, paramInt, 0); }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt1, int paramInt2) {
    this.mNestedScrollingParentHelper.onNestedScrollAccepted(paramView1, paramView2, paramInt1, paramInt2);
    this.mNestedScrollingTarget = paramView2;
    int i = getChildCount();
    byte b;
    for (b = 0; b < i; b++) {
      View view = getChildAt(b);
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      if (layoutParams.isNestedScrollAccepted(paramInt2)) {
        Behavior behavior = layoutParams.getBehavior();
        if (behavior != null)
          behavior.onNestedScrollAccepted(this, view, paramView1, paramView2, paramInt1, paramInt2); 
      } 
    } 
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable) {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
      return;
    } 
    SavedState savedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(savedState.getSuperState());
    SparseArray sparseArray = savedState.behaviorStates;
    byte b = 0;
    int i = getChildCount();
    while (b < i) {
      View view = getChildAt(b);
      int j = view.getId();
      Behavior behavior = getResolvedLayoutParams(view).getBehavior();
      if (j != -1 && behavior != null) {
        Parcelable parcelable = (Parcelable)sparseArray.get(j);
        if (parcelable != null)
          behavior.onRestoreInstanceState(this, view, parcelable); 
      } 
      b++;
    } 
  }
  
  protected Parcelable onSaveInstanceState() {
    SavedState savedState = new SavedState(super.onSaveInstanceState());
    SparseArray sparseArray = new SparseArray();
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = getChildAt(b);
      int j = view.getId();
      Behavior behavior = ((LayoutParams)view.getLayoutParams()).getBehavior();
      if (j != -1 && behavior != null) {
        Parcelable parcelable = behavior.onSaveInstanceState(this, view);
        if (parcelable != null)
          sparseArray.append(j, parcelable); 
      } 
    } 
    savedState.behaviorStates = sparseArray;
    return savedState;
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt) { return onStartNestedScroll(paramView1, paramView2, paramInt, 0); }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt1, int paramInt2) {
    int i = getChildCount();
    byte b = 0;
    boolean bool = false;
    while (b < i) {
      View view = getChildAt(b);
      if (view.getVisibility() != 8) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        Behavior behavior = layoutParams.getBehavior();
        if (behavior != null) {
          boolean bool1 = behavior.onStartNestedScroll(this, view, paramView1, paramView2, paramInt1, paramInt2);
          layoutParams.setNestedScrollAccepted(paramInt2, bool1);
          bool |= bool1;
        } else {
          layoutParams.setNestedScrollAccepted(paramInt2, false);
        } 
      } 
      b++;
    } 
    return bool;
  }
  
  public void onStopNestedScroll(View paramView) { onStopNestedScroll(paramView, 0); }
  
  public void onStopNestedScroll(View paramView, int paramInt) {
    this.mNestedScrollingParentHelper.onStopNestedScroll(paramView, paramInt);
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = getChildAt(b);
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      if (layoutParams.isNestedScrollAccepted(paramInt)) {
        Behavior behavior = layoutParams.getBehavior();
        if (behavior != null)
          behavior.onStopNestedScroll(this, view, paramView, paramInt); 
        layoutParams.resetNestedScroll(paramInt);
        layoutParams.resetChangedAfterNestedScroll();
      } 
    } 
    this.mNestedScrollingTarget = null;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) { // Byte code:
    //   0: aload_1
    //   1: invokevirtual getActionMasked : ()I
    //   4: istore_2
    //   5: aload_0
    //   6: getfield mBehaviorTouchView : Landroid/view/View;
    //   9: ifnonnull -> 42
    //   12: aload_0
    //   13: aload_1
    //   14: iconst_1
    //   15: invokespecial performIntercept : (Landroid/view/MotionEvent;I)Z
    //   18: istore_3
    //   19: iload_3
    //   20: istore #4
    //   22: iload_3
    //   23: ifeq -> 29
    //   26: goto -> 44
    //   29: iconst_0
    //   30: istore #5
    //   32: iload #4
    //   34: istore_3
    //   35: iload #5
    //   37: istore #4
    //   39: goto -> 80
    //   42: iconst_0
    //   43: istore_3
    //   44: aload_0
    //   45: getfield mBehaviorTouchView : Landroid/view/View;
    //   48: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   51: checkcast android/support/design/widget/CoordinatorLayout$LayoutParams
    //   54: invokevirtual getBehavior : ()Landroid/support/design/widget/CoordinatorLayout$Behavior;
    //   57: astore #8
    //   59: iload_3
    //   60: istore #4
    //   62: aload #8
    //   64: ifnull -> 29
    //   67: aload #8
    //   69: aload_0
    //   70: aload_0
    //   71: getfield mBehaviorTouchView : Landroid/view/View;
    //   74: aload_1
    //   75: invokevirtual onTouchEvent : (Landroid/support/design/widget/CoordinatorLayout;Landroid/view/View;Landroid/view/MotionEvent;)Z
    //   78: istore #4
    //   80: aload_0
    //   81: getfield mBehaviorTouchView : Landroid/view/View;
    //   84: astore #9
    //   86: aconst_null
    //   87: astore #8
    //   89: aload #9
    //   91: ifnonnull -> 110
    //   94: iload #4
    //   96: aload_0
    //   97: aload_1
    //   98: invokespecial onTouchEvent : (Landroid/view/MotionEvent;)Z
    //   101: ior
    //   102: istore #5
    //   104: aload #8
    //   106: astore_1
    //   107: goto -> 148
    //   110: iload #4
    //   112: istore #5
    //   114: aload #8
    //   116: astore_1
    //   117: iload_3
    //   118: ifeq -> 148
    //   121: invokestatic uptimeMillis : ()J
    //   124: lstore #6
    //   126: lload #6
    //   128: lload #6
    //   130: iconst_3
    //   131: fconst_0
    //   132: fconst_0
    //   133: iconst_0
    //   134: invokestatic obtain : (JJIFFI)Landroid/view/MotionEvent;
    //   137: astore_1
    //   138: aload_0
    //   139: aload_1
    //   140: invokespecial onTouchEvent : (Landroid/view/MotionEvent;)Z
    //   143: pop
    //   144: iload #4
    //   146: istore #5
    //   148: aload_1
    //   149: ifnull -> 156
    //   152: aload_1
    //   153: invokevirtual recycle : ()V
    //   156: iload_2
    //   157: iconst_1
    //   158: if_icmpeq -> 166
    //   161: iload_2
    //   162: iconst_3
    //   163: if_icmpne -> 171
    //   166: aload_0
    //   167: iconst_0
    //   168: invokespecial resetTouchBehaviors : (Z)V
    //   171: iload #5
    //   173: ireturn }
  
  void recordLastChildRect(View paramView, Rect paramRect) { ((LayoutParams)paramView.getLayoutParams()).setLastChildRect(paramRect); }
  
  void removePreDrawListener() {
    if (this.mIsAttachedToWindow && this.mOnPreDrawListener != null)
      getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener); 
    this.mNeedsPreDrawListener = false;
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean) {
    Behavior behavior = ((LayoutParams)paramView.getLayoutParams()).getBehavior();
    return (behavior != null && behavior.onRequestChildRectangleOnScreen(this, paramView, paramRect, paramBoolean)) ? true : super.requestChildRectangleOnScreen(paramView, paramRect, paramBoolean);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean) {
    super.requestDisallowInterceptTouchEvent(paramBoolean);
    if (paramBoolean && !this.mDisallowInterceptReset) {
      resetTouchBehaviors(false);
      this.mDisallowInterceptReset = true;
    } 
  }
  
  public void setFitsSystemWindows(boolean paramBoolean) {
    super.setFitsSystemWindows(paramBoolean);
    setupForInsets();
  }
  
  public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener paramOnHierarchyChangeListener) { this.mOnHierarchyChangeListener = paramOnHierarchyChangeListener; }
  
  public void setStatusBarBackground(@Nullable Drawable paramDrawable) {
    if (this.mStatusBarBackground != paramDrawable) {
      Drawable drawable2 = this.mStatusBarBackground;
      Drawable drawable1 = null;
      if (drawable2 != null)
        this.mStatusBarBackground.setCallback(null); 
      if (paramDrawable != null)
        drawable1 = paramDrawable.mutate(); 
      this.mStatusBarBackground = drawable1;
      if (this.mStatusBarBackground != null) {
        boolean bool;
        if (this.mStatusBarBackground.isStateful())
          this.mStatusBarBackground.setState(getDrawableState()); 
        DrawableCompat.setLayoutDirection(this.mStatusBarBackground, ViewCompat.getLayoutDirection(this));
        paramDrawable = this.mStatusBarBackground;
        if (getVisibility() == 0) {
          bool = true;
        } else {
          bool = false;
        } 
        paramDrawable.setVisible(bool, false);
        this.mStatusBarBackground.setCallback(this);
      } 
      ViewCompat.postInvalidateOnAnimation(this);
    } 
  }
  
  public void setStatusBarBackgroundColor(@ColorInt int paramInt) { setStatusBarBackground(new ColorDrawable(paramInt)); }
  
  public void setStatusBarBackgroundResource(@DrawableRes int paramInt) {
    Drawable drawable;
    if (paramInt != 0) {
      drawable = ContextCompat.getDrawable(getContext(), paramInt);
    } else {
      drawable = null;
    } 
    setStatusBarBackground(drawable);
  }
  
  public void setVisibility(int paramInt) {
    boolean bool;
    super.setVisibility(paramInt);
    if (paramInt == 0) {
      bool = true;
    } else {
      bool = false;
    } 
    if (this.mStatusBarBackground != null && this.mStatusBarBackground.isVisible() != bool)
      this.mStatusBarBackground.setVisible(bool, false); 
  }
  
  final WindowInsetsCompat setWindowInsets(WindowInsetsCompat paramWindowInsetsCompat) {
    WindowInsetsCompat windowInsetsCompat = paramWindowInsetsCompat;
    if (!ObjectsCompat.equals(this.mLastInsets, paramWindowInsetsCompat)) {
      this.mLastInsets = paramWindowInsetsCompat;
      byte b = 0;
      if (paramWindowInsetsCompat != null && paramWindowInsetsCompat.getSystemWindowInsetTop() > 0) {
        i = 1;
      } else {
        i = 0;
      } 
      this.mDrawStatusBarBackground = i;
      int i = b;
      if (!this.mDrawStatusBarBackground) {
        i = b;
        if (getBackground() == null)
          i = 1; 
      } 
      setWillNotDraw(i);
      windowInsetsCompat = dispatchApplyWindowInsetsToBehaviors(paramWindowInsetsCompat);
      requestLayout();
    } 
    return windowInsetsCompat;
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable) { return (super.verifyDrawable(paramDrawable) || paramDrawable == this.mStatusBarBackground); }
  
  public static interface AttachedBehavior {
    @NonNull
    CoordinatorLayout.Behavior getBehavior();
  }
  
  public static abstract class Behavior<V extends View> extends Object {
    public Behavior() {}
    
    public Behavior(Context param1Context, AttributeSet param1AttributeSet) {}
    
    public static Object getTag(View param1View) { return ((CoordinatorLayout.LayoutParams)param1View.getLayoutParams()).mBehaviorTag; }
    
    public static void setTag(View param1View, Object param1Object) { ((CoordinatorLayout.LayoutParams)param1View.getLayoutParams()).mBehaviorTag = param1Object; }
    
    public boolean blocksInteractionBelow(CoordinatorLayout param1CoordinatorLayout, V param1V) { return (getScrimOpacity(param1CoordinatorLayout, param1V) > 0.0F); }
    
    public boolean getInsetDodgeRect(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull Rect param1Rect) { return false; }
    
    @ColorInt
    public int getScrimColor(CoordinatorLayout param1CoordinatorLayout, V param1V) { return -16777216; }
    
    @FloatRange(from = 0.0D, to = 1.0D)
    public float getScrimOpacity(CoordinatorLayout param1CoordinatorLayout, V param1V) { return 0.0F; }
    
    public boolean layoutDependsOn(CoordinatorLayout param1CoordinatorLayout, V param1V, View param1View) { return false; }
    
    @NonNull
    public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout param1CoordinatorLayout, V param1V, WindowInsetsCompat param1WindowInsetsCompat) { return param1WindowInsetsCompat; }
    
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams param1LayoutParams) {}
    
    public boolean onDependentViewChanged(CoordinatorLayout param1CoordinatorLayout, V param1V, View param1View) { return false; }
    
    public void onDependentViewRemoved(CoordinatorLayout param1CoordinatorLayout, V param1V, View param1View) {}
    
    public void onDetachedFromLayoutParams() {}
    
    public boolean onInterceptTouchEvent(CoordinatorLayout param1CoordinatorLayout, V param1V, MotionEvent param1MotionEvent) { return false; }
    
    public boolean onLayoutChild(CoordinatorLayout param1CoordinatorLayout, V param1V, int param1Int) { return false; }
    
    public boolean onMeasureChild(CoordinatorLayout param1CoordinatorLayout, V param1V, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { return false; }
    
    public boolean onNestedFling(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View, float param1Float1, float param1Float2, boolean param1Boolean) { return false; }
    
    public boolean onNestedPreFling(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View, float param1Float1, float param1Float2) { return false; }
    
    @Deprecated
    public void onNestedPreScroll(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View, int param1Int1, int param1Int2, @NonNull int[] param1ArrayOfInt) {}
    
    public void onNestedPreScroll(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View, int param1Int1, int param1Int2, @NonNull int[] param1ArrayOfInt, int param1Int3) {
      if (param1Int3 == 0)
        onNestedPreScroll(param1CoordinatorLayout, param1V, param1View, param1Int1, param1Int2, param1ArrayOfInt); 
    }
    
    @Deprecated
    public void onNestedScroll(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {}
    
    public void onNestedScroll(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      if (param1Int5 == 0)
        onNestedScroll(param1CoordinatorLayout, param1V, param1View, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    @Deprecated
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View1, @NonNull View param1View2, int param1Int) {}
    
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View1, @NonNull View param1View2, int param1Int1, int param1Int2) {
      if (param1Int2 == 0)
        onNestedScrollAccepted(param1CoordinatorLayout, param1V, param1View1, param1View2, param1Int1); 
    }
    
    public boolean onRequestChildRectangleOnScreen(CoordinatorLayout param1CoordinatorLayout, V param1V, Rect param1Rect, boolean param1Boolean) { return false; }
    
    public void onRestoreInstanceState(CoordinatorLayout param1CoordinatorLayout, V param1V, Parcelable param1Parcelable) {}
    
    public Parcelable onSaveInstanceState(CoordinatorLayout param1CoordinatorLayout, V param1V) { return View.BaseSavedState.EMPTY_STATE; }
    
    @Deprecated
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View1, @NonNull View param1View2, int param1Int) { return false; }
    
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View1, @NonNull View param1View2, int param1Int1, int param1Int2) { return (param1Int2 == 0) ? onStartNestedScroll(param1CoordinatorLayout, param1V, param1View1, param1View2, param1Int1) : 0; }
    
    @Deprecated
    public void onStopNestedScroll(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View) {}
    
    public void onStopNestedScroll(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull V param1V, @NonNull View param1View, int param1Int) {
      if (param1Int == 0)
        onStopNestedScroll(param1CoordinatorLayout, param1V, param1View); 
    }
    
    public boolean onTouchEvent(CoordinatorLayout param1CoordinatorLayout, V param1V, MotionEvent param1MotionEvent) { return false; }
  }
  
  @Deprecated
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface DefaultBehavior {
    Class<? extends CoordinatorLayout.Behavior> value();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface DispatchChangeEvent {}
  
  private class HierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {
    public void onChildViewAdded(View param1View1, View param1View2) {
      if (CoordinatorLayout.this.mOnHierarchyChangeListener != null)
        CoordinatorLayout.this.mOnHierarchyChangeListener.onChildViewAdded(param1View1, param1View2); 
    }
    
    public void onChildViewRemoved(View param1View1, View param1View2) {
      CoordinatorLayout.this.onChildViewsChanged(2);
      if (CoordinatorLayout.this.mOnHierarchyChangeListener != null)
        CoordinatorLayout.this.mOnHierarchyChangeListener.onChildViewRemoved(param1View1, param1View2); 
    }
  }
  
  public static class LayoutParams extends ViewGroup.MarginLayoutParams {
    public int anchorGravity = 0;
    
    public int dodgeInsetEdges = 0;
    
    public int gravity = 0;
    
    public int insetEdge = 0;
    
    public int keyline = -1;
    
    View mAnchorDirectChild;
    
    int mAnchorId = -1;
    
    View mAnchorView;
    
    CoordinatorLayout.Behavior mBehavior;
    
    boolean mBehaviorResolved = false;
    
    Object mBehaviorTag;
    
    private boolean mDidAcceptNestedScrollNonTouch;
    
    private boolean mDidAcceptNestedScrollTouch;
    
    private boolean mDidBlockInteraction;
    
    private boolean mDidChangeAfterNestedScroll;
    
    int mInsetOffsetX;
    
    int mInsetOffsetY;
    
    final Rect mLastChildRect = new Rect();
    
    public LayoutParams(int param1Int1, int param1Int2) { super(param1Int1, param1Int2); }
    
    LayoutParams(Context param1Context, AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, R.styleable.CoordinatorLayout_Layout);
      this.gravity = typedArray.getInteger(R.styleable.CoordinatorLayout_Layout_android_layout_gravity, 0);
      this.mAnchorId = typedArray.getResourceId(R.styleable.CoordinatorLayout_Layout_layout_anchor, -1);
      this.anchorGravity = typedArray.getInteger(R.styleable.CoordinatorLayout_Layout_layout_anchorGravity, 0);
      this.keyline = typedArray.getInteger(R.styleable.CoordinatorLayout_Layout_layout_keyline, -1);
      this.insetEdge = typedArray.getInt(R.styleable.CoordinatorLayout_Layout_layout_insetEdge, 0);
      this.dodgeInsetEdges = typedArray.getInt(R.styleable.CoordinatorLayout_Layout_layout_dodgeInsetEdges, 0);
      this.mBehaviorResolved = typedArray.hasValue(R.styleable.CoordinatorLayout_Layout_layout_behavior);
      if (this.mBehaviorResolved)
        this.mBehavior = CoordinatorLayout.parseBehavior(param1Context, param1AttributeSet, typedArray.getString(R.styleable.CoordinatorLayout_Layout_layout_behavior)); 
      typedArray.recycle();
      if (this.mBehavior != null)
        this.mBehavior.onAttachedToLayoutParams(this); 
    }
    
    public LayoutParams(LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.MarginLayoutParams param1MarginLayoutParams) { super(param1MarginLayoutParams); }
    
    private void resolveAnchorView(View param1View, CoordinatorLayout param1CoordinatorLayout) {
      this.mAnchorView = param1CoordinatorLayout.findViewById(this.mAnchorId);
      if (this.mAnchorView != null) {
        if (this.mAnchorView == param1CoordinatorLayout) {
          if (param1CoordinatorLayout.isInEditMode()) {
            this.mAnchorDirectChild = null;
            this.mAnchorView = null;
            return;
          } 
          throw new IllegalStateException("View can not be anchored to the the parent CoordinatorLayout");
        } 
        View view = this.mAnchorView;
        for (ViewParent viewParent = this.mAnchorView.getParent(); viewParent != param1CoordinatorLayout && viewParent != null; viewParent = viewParent.getParent()) {
          if (viewParent == param1View) {
            if (param1CoordinatorLayout.isInEditMode()) {
              this.mAnchorDirectChild = null;
              this.mAnchorView = null;
              return;
            } 
            throw new IllegalStateException("Anchor must not be a descendant of the anchored view");
          } 
          if (viewParent instanceof View)
            view = (View)viewParent; 
        } 
        this.mAnchorDirectChild = view;
        return;
      } 
      if (param1CoordinatorLayout.isInEditMode()) {
        this.mAnchorDirectChild = null;
        this.mAnchorView = null;
        return;
      } 
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Could not find CoordinatorLayout descendant view with id ");
      stringBuilder.append(param1CoordinatorLayout.getResources().getResourceName(this.mAnchorId));
      stringBuilder.append(" to anchor view ");
      stringBuilder.append(param1View);
      throw new IllegalStateException(stringBuilder.toString());
    }
    
    private boolean shouldDodge(View param1View, int param1Int) {
      int i = GravityCompat.getAbsoluteGravity(((LayoutParams)param1View.getLayoutParams()).insetEdge, param1Int);
      return (i != 0 && (GravityCompat.getAbsoluteGravity(this.dodgeInsetEdges, param1Int) & i) == i);
    }
    
    private boolean verifyAnchorView(View param1View, CoordinatorLayout param1CoordinatorLayout) {
      if (this.mAnchorView.getId() != this.mAnchorId)
        return false; 
      View view = this.mAnchorView;
      for (ViewParent viewParent = this.mAnchorView.getParent(); viewParent != param1CoordinatorLayout; viewParent = viewParent.getParent()) {
        if (viewParent == null || viewParent == param1View) {
          this.mAnchorDirectChild = null;
          this.mAnchorView = null;
          return false;
        } 
        if (viewParent instanceof View)
          view = (View)viewParent; 
      } 
      this.mAnchorDirectChild = view;
      return true;
    }
    
    boolean checkAnchorChanged() { return (this.mAnchorView == null && this.mAnchorId != -1); }
    
    boolean dependsOn(CoordinatorLayout param1CoordinatorLayout, View param1View1, View param1View2) { return (param1View2 == this.mAnchorDirectChild || shouldDodge(param1View2, ViewCompat.getLayoutDirection(param1CoordinatorLayout)) || (this.mBehavior != null && this.mBehavior.layoutDependsOn(param1CoordinatorLayout, param1View1, param1View2))); }
    
    boolean didBlockInteraction() {
      if (this.mBehavior == null)
        this.mDidBlockInteraction = false; 
      return this.mDidBlockInteraction;
    }
    
    View findAnchorView(CoordinatorLayout param1CoordinatorLayout, View param1View) {
      if (this.mAnchorId == -1) {
        this.mAnchorDirectChild = null;
        this.mAnchorView = null;
        return null;
      } 
      if (this.mAnchorView == null || !verifyAnchorView(param1View, param1CoordinatorLayout))
        resolveAnchorView(param1View, param1CoordinatorLayout); 
      return this.mAnchorView;
    }
    
    @IdRes
    public int getAnchorId() { return this.mAnchorId; }
    
    @Nullable
    public CoordinatorLayout.Behavior getBehavior() { return this.mBehavior; }
    
    boolean getChangedAfterNestedScroll() { return this.mDidChangeAfterNestedScroll; }
    
    Rect getLastChildRect() { return this.mLastChildRect; }
    
    void invalidateAnchor() {
      this.mAnchorDirectChild = null;
      this.mAnchorView = null;
    }
    
    boolean isBlockingInteractionBelow(CoordinatorLayout param1CoordinatorLayout, View param1View) {
      boolean bool1;
      if (this.mDidBlockInteraction)
        return true; 
      boolean bool2 = this.mDidBlockInteraction;
      if (this.mBehavior != null) {
        bool1 = this.mBehavior.blocksInteractionBelow(param1CoordinatorLayout, param1View);
      } else {
        bool1 = false;
      } 
      bool1 |= bool2;
      this.mDidBlockInteraction = bool1;
      return bool1;
    }
    
    boolean isNestedScrollAccepted(int param1Int) {
      switch (param1Int) {
        default:
          return false;
        case 1:
          return this.mDidAcceptNestedScrollNonTouch;
        case 0:
          break;
      } 
      return this.mDidAcceptNestedScrollTouch;
    }
    
    void resetChangedAfterNestedScroll() { this.mDidChangeAfterNestedScroll = false; }
    
    void resetNestedScroll(int param1Int) { setNestedScrollAccepted(param1Int, false); }
    
    void resetTouchBehaviorTracking() { this.mDidBlockInteraction = false; }
    
    public void setAnchorId(@IdRes int param1Int) {
      invalidateAnchor();
      this.mAnchorId = param1Int;
    }
    
    public void setBehavior(@Nullable CoordinatorLayout.Behavior param1Behavior) {
      if (this.mBehavior != param1Behavior) {
        if (this.mBehavior != null)
          this.mBehavior.onDetachedFromLayoutParams(); 
        this.mBehavior = param1Behavior;
        this.mBehaviorTag = null;
        this.mBehaviorResolved = true;
        if (param1Behavior != null)
          param1Behavior.onAttachedToLayoutParams(this); 
      } 
    }
    
    void setChangedAfterNestedScroll(boolean param1Boolean) { this.mDidChangeAfterNestedScroll = param1Boolean; }
    
    void setLastChildRect(Rect param1Rect) { this.mLastChildRect.set(param1Rect); }
    
    void setNestedScrollAccepted(int param1Int, boolean param1Boolean) {
      switch (param1Int) {
        default:
          return;
        case 1:
          this.mDidAcceptNestedScrollNonTouch = param1Boolean;
          return;
        case 0:
          break;
      } 
      this.mDidAcceptNestedScrollTouch = param1Boolean;
    }
  }
  
  class OnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
    public boolean onPreDraw() {
      CoordinatorLayout.this.onChildViewsChanged(0);
      return true;
    }
  }
  
  protected static class SavedState extends AbsSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
        public CoordinatorLayout.SavedState createFromParcel(Parcel param2Parcel) { return new CoordinatorLayout.SavedState(param2Parcel, null); }
        
        public CoordinatorLayout.SavedState createFromParcel(Parcel param2Parcel, ClassLoader param2ClassLoader) { return new CoordinatorLayout.SavedState(param2Parcel, param2ClassLoader); }
        
        public CoordinatorLayout.SavedState[] newArray(int param2Int) { return new CoordinatorLayout.SavedState[param2Int]; }
      };
    
    SparseArray<Parcelable> behaviorStates;
    
    public SavedState(Parcel param1Parcel, ClassLoader param1ClassLoader) {
      super(param1Parcel, param1ClassLoader);
      int i = param1Parcel.readInt();
      int[] arrayOfInt = new int[i];
      param1Parcel.readIntArray(arrayOfInt);
      Parcelable[] arrayOfParcelable = param1Parcel.readParcelableArray(param1ClassLoader);
      this.behaviorStates = new SparseArray(i);
      for (byte b = 0; b < i; b++)
        this.behaviorStates.append(arrayOfInt[b], arrayOfParcelable[b]); 
    }
    
    public SavedState(Parcelable param1Parcelable) { super(param1Parcelable); }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) {
      byte b1;
      super.writeToParcel(param1Parcel, param1Int);
      SparseArray sparseArray = this.behaviorStates;
      byte b2 = 0;
      if (sparseArray != null) {
        b1 = this.behaviorStates.size();
      } else {
        b1 = 0;
      } 
      param1Parcel.writeInt(b1);
      int[] arrayOfInt = new int[b1];
      Parcelable[] arrayOfParcelable = new Parcelable[b1];
      while (b2 < b1) {
        arrayOfInt[b2] = this.behaviorStates.keyAt(b2);
        arrayOfParcelable[b2] = (Parcelable)this.behaviorStates.valueAt(b2);
        b2++;
      } 
      param1Parcel.writeIntArray(arrayOfInt);
      param1Parcel.writeParcelableArray(arrayOfParcelable, param1Int);
    }
  }
  
  static final class null extends Object implements Parcelable.ClassLoaderCreator<SavedState> {
    public CoordinatorLayout.SavedState createFromParcel(Parcel param1Parcel) { return new CoordinatorLayout.SavedState(param1Parcel, null); }
    
    public CoordinatorLayout.SavedState createFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) { return new CoordinatorLayout.SavedState(param1Parcel, param1ClassLoader); }
    
    public CoordinatorLayout.SavedState[] newArray(int param1Int) { return new CoordinatorLayout.SavedState[param1Int]; }
  }
  
  static class ViewElevationComparator extends Object implements Comparator<View> {
    public int compare(View param1View1, View param1View2) {
      float f1 = ViewCompat.getZ(param1View1);
      float f2 = ViewCompat.getZ(param1View2);
      return (f1 > f2) ? -1 : ((f1 < f2) ? 1 : 0);
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/CoordinatorLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */