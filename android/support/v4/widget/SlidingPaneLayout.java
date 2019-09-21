package android.support.v4.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SlidingPaneLayout extends ViewGroup {
  private static final int DEFAULT_FADE_COLOR = -858993460;
  
  private static final int DEFAULT_OVERHANG_SIZE = 32;
  
  static final SlidingPanelLayoutImpl IMPL;
  
  private static final int MIN_FLING_VELOCITY = 400;
  
  private static final String TAG = "SlidingPaneLayout";
  
  private boolean mCanSlide;
  
  private int mCoveredFadeColor;
  
  final ViewDragHelper mDragHelper;
  
  private boolean mFirstLayout = true;
  
  private float mInitialMotionX;
  
  private float mInitialMotionY;
  
  boolean mIsUnableToDrag;
  
  private final int mOverhangSize;
  
  private PanelSlideListener mPanelSlideListener;
  
  private int mParallaxBy;
  
  private float mParallaxOffset;
  
  final ArrayList<DisableLayerRunnable> mPostedRunnables = new ArrayList();
  
  boolean mPreservedOpenState;
  
  private Drawable mShadowDrawableLeft;
  
  private Drawable mShadowDrawableRight;
  
  float mSlideOffset;
  
  int mSlideRange;
  
  View mSlideableView;
  
  private int mSliderFadeColor = -858993460;
  
  private final Rect mTmpRect = new Rect();
  
  static  {
    if (Build.VERSION.SDK_INT >= 17) {
      IMPL = new SlidingPanelLayoutImplJBMR1();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 16) {
      IMPL = new SlidingPanelLayoutImplJB();
      return;
    } 
    IMPL = new SlidingPanelLayoutImplBase();
  }
  
  public SlidingPaneLayout(@NonNull Context paramContext) { this(paramContext, null); }
  
  public SlidingPaneLayout(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public SlidingPaneLayout(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    float f = (paramContext.getResources().getDisplayMetrics()).density;
    this.mOverhangSize = (int)(32.0F * f + 0.5F);
    setWillNotDraw(false);
    ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
    ViewCompat.setImportantForAccessibility(this, 1);
    this.mDragHelper = ViewDragHelper.create(this, 0.5F, new DragHelperCallback());
    this.mDragHelper.setMinVelocity(f * 400.0F);
  }
  
  private boolean closePane(View paramView, int paramInt) {
    if (this.mFirstLayout || smoothSlideTo(0.0F, paramInt)) {
      this.mPreservedOpenState = false;
      return true;
    } 
    return false;
  }
  
  private void dimChildView(View paramView, float paramFloat, int paramInt) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if (paramFloat > 0.0F && paramInt != 0) {
      int i = (int)(((0xFF000000 & paramInt) >>> 24) * paramFloat);
      if (layoutParams.dimPaint == null)
        layoutParams.dimPaint = new Paint(); 
      layoutParams.dimPaint.setColorFilter(new PorterDuffColorFilter(i << 24 | paramInt & 0xFFFFFF, PorterDuff.Mode.SRC_OVER));
      if (paramView.getLayerType() != 2)
        paramView.setLayerType(2, layoutParams.dimPaint); 
      invalidateChildRegion(paramView);
      return;
    } 
    if (paramView.getLayerType() != 0) {
      if (layoutParams.dimPaint != null)
        layoutParams.dimPaint.setColorFilter(null); 
      DisableLayerRunnable disableLayerRunnable = new DisableLayerRunnable(paramView);
      this.mPostedRunnables.add(disableLayerRunnable);
      ViewCompat.postOnAnimation(this, disableLayerRunnable);
    } 
  }
  
  private boolean openPane(View paramView, int paramInt) {
    if (this.mFirstLayout || smoothSlideTo(1.0F, paramInt)) {
      this.mPreservedOpenState = true;
      return true;
    } 
    return false;
  }
  
  private void parallaxOtherViews(float paramFloat) { // Byte code:
    //   0: aload_0
    //   1: invokevirtual isLayoutRtlSupport : ()Z
    //   4: istore #8
    //   6: aload_0
    //   7: getfield mSlideableView : Landroid/view/View;
    //   10: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   13: checkcast android/support/v4/widget/SlidingPaneLayout$LayoutParams
    //   16: astore #10
    //   18: aload #10
    //   20: getfield dimWhenOffset : Z
    //   23: istore #9
    //   25: iconst_0
    //   26: istore #4
    //   28: iload #9
    //   30: ifeq -> 62
    //   33: iload #8
    //   35: ifeq -> 47
    //   38: aload #10
    //   40: getfield rightMargin : I
    //   43: istore_3
    //   44: goto -> 53
    //   47: aload #10
    //   49: getfield leftMargin : I
    //   52: istore_3
    //   53: iload_3
    //   54: ifgt -> 62
    //   57: iconst_1
    //   58: istore_3
    //   59: goto -> 64
    //   62: iconst_0
    //   63: istore_3
    //   64: aload_0
    //   65: invokevirtual getChildCount : ()I
    //   68: istore #7
    //   70: iload #4
    //   72: iload #7
    //   74: if_icmpge -> 199
    //   77: aload_0
    //   78: iload #4
    //   80: invokevirtual getChildAt : (I)Landroid/view/View;
    //   83: astore #10
    //   85: aload #10
    //   87: aload_0
    //   88: getfield mSlideableView : Landroid/view/View;
    //   91: if_acmpne -> 97
    //   94: goto -> 190
    //   97: fconst_1
    //   98: aload_0
    //   99: getfield mParallaxOffset : F
    //   102: fsub
    //   103: aload_0
    //   104: getfield mParallaxBy : I
    //   107: i2f
    //   108: fmul
    //   109: f2i
    //   110: istore #5
    //   112: aload_0
    //   113: fload_1
    //   114: putfield mParallaxOffset : F
    //   117: iload #5
    //   119: fconst_1
    //   120: fload_1
    //   121: fsub
    //   122: aload_0
    //   123: getfield mParallaxBy : I
    //   126: i2f
    //   127: fmul
    //   128: f2i
    //   129: isub
    //   130: istore #6
    //   132: iload #6
    //   134: istore #5
    //   136: iload #8
    //   138: ifeq -> 146
    //   141: iload #6
    //   143: ineg
    //   144: istore #5
    //   146: aload #10
    //   148: iload #5
    //   150: invokevirtual offsetLeftAndRight : (I)V
    //   153: iload_3
    //   154: ifeq -> 190
    //   157: iload #8
    //   159: ifeq -> 172
    //   162: aload_0
    //   163: getfield mParallaxOffset : F
    //   166: fconst_1
    //   167: fsub
    //   168: fstore_2
    //   169: goto -> 179
    //   172: fconst_1
    //   173: aload_0
    //   174: getfield mParallaxOffset : F
    //   177: fsub
    //   178: fstore_2
    //   179: aload_0
    //   180: aload #10
    //   182: fload_2
    //   183: aload_0
    //   184: getfield mCoveredFadeColor : I
    //   187: invokespecial dimChildView : (Landroid/view/View;FI)V
    //   190: iload #4
    //   192: iconst_1
    //   193: iadd
    //   194: istore #4
    //   196: goto -> 70
    //   199: return }
  
  private static boolean viewIsOpaque(View paramView) {
    if (paramView.isOpaque())
      return true; 
    if (Build.VERSION.SDK_INT >= 18)
      return false; 
    Drawable drawable = paramView.getBackground();
    return (drawable != null) ? ((drawable.getOpacity() == -1)) : false;
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
    if (paramBoolean) {
      if (!isLayoutRtlSupport())
        paramInt1 = -paramInt1; 
      if (paramView.canScrollHorizontally(paramInt1))
        return true; 
    } 
    return false;
  }
  
  @Deprecated
  public boolean canSlide() { return this.mCanSlide; }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams instanceof LayoutParams && super.checkLayoutParams(paramLayoutParams)); }
  
  public boolean closePane() { return closePane(this.mSlideableView, 0); }
  
  public void computeScroll() {
    if (this.mDragHelper.continueSettling(true)) {
      if (!this.mCanSlide) {
        this.mDragHelper.abort();
        return;
      } 
      ViewCompat.postInvalidateOnAnimation(this);
    } 
  }
  
  void dispatchOnPanelClosed(View paramView) {
    if (this.mPanelSlideListener != null)
      this.mPanelSlideListener.onPanelClosed(paramView); 
    sendAccessibilityEvent(32);
  }
  
  void dispatchOnPanelOpened(View paramView) {
    if (this.mPanelSlideListener != null)
      this.mPanelSlideListener.onPanelOpened(paramView); 
    sendAccessibilityEvent(32);
  }
  
  void dispatchOnPanelSlide(View paramView) {
    if (this.mPanelSlideListener != null)
      this.mPanelSlideListener.onPanelSlide(paramView, this.mSlideOffset); 
  }
  
  public void draw(Canvas paramCanvas) {
    Object object;
    Drawable drawable;
    super.draw(paramCanvas);
    if (isLayoutRtlSupport()) {
      drawable = this.mShadowDrawableRight;
    } else {
      drawable = this.mShadowDrawableLeft;
    } 
    if (getChildCount() > 1) {
      object = getChildAt(1);
    } else {
      object = null;
    } 
    if (object != null) {
      int j;
      int i;
      if (drawable == null)
        return; 
      int m = object.getTop();
      int n = object.getBottom();
      int k = drawable.getIntrinsicWidth();
      if (isLayoutRtlSupport()) {
        i = object.getRight();
        j = k + i;
      } else {
        j = object.getLeft();
        i = j;
        k = j - k;
        j = i;
        i = k;
      } 
      drawable.setBounds(i, m, j, n);
      drawable.draw(paramCanvas);
      return;
    } 
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = paramCanvas.save();
    if (this.mCanSlide && !layoutParams.slideable && this.mSlideableView != null) {
      paramCanvas.getClipBounds(this.mTmpRect);
      if (isLayoutRtlSupport()) {
        this.mTmpRect.left = Math.max(this.mTmpRect.left, this.mSlideableView.getRight());
      } else {
        this.mTmpRect.right = Math.min(this.mTmpRect.right, this.mSlideableView.getLeft());
      } 
      paramCanvas.clipRect(this.mTmpRect);
    } 
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    paramCanvas.restoreToCount(i);
    return bool;
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams() { return new LayoutParams(); }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return new LayoutParams(getContext(), paramAttributeSet); }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams instanceof ViewGroup.MarginLayoutParams) ? new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams) : new LayoutParams(paramLayoutParams); }
  
  @ColorInt
  public int getCoveredFadeColor() { return this.mCoveredFadeColor; }
  
  public int getParallaxDistance() { return this.mParallaxBy; }
  
  @ColorInt
  public int getSliderFadeColor() { return this.mSliderFadeColor; }
  
  void invalidateChildRegion(View paramView) { IMPL.invalidateChildRegion(this, paramView); }
  
  boolean isDimmed(View paramView) {
    byte b = 0;
    if (paramView == null)
      return false; 
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = b;
    if (this.mCanSlide) {
      i = b;
      if (layoutParams.dimWhenOffset) {
        i = b;
        if (this.mSlideOffset > 0.0F)
          i = 1; 
      } 
    } 
    return i;
  }
  
  boolean isLayoutRtlSupport() { return (ViewCompat.getLayoutDirection(this) == 1); }
  
  public boolean isOpen() { return (!this.mCanSlide || this.mSlideOffset == 1.0F); }
  
  public boolean isSlideable() { return this.mCanSlide; }
  
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    this.mFirstLayout = true;
  }
  
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    this.mFirstLayout = true;
    int i = this.mPostedRunnables.size();
    for (byte b = 0; b < i; b++)
      ((DisableLayerRunnable)this.mPostedRunnables.get(b)).run(); 
    this.mPostedRunnables.clear();
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) { // Byte code:
    //   0: aload_1
    //   1: invokevirtual getActionMasked : ()I
    //   4: istore #4
    //   6: aload_0
    //   7: getfield mCanSlide : Z
    //   10: istore #6
    //   12: iconst_1
    //   13: istore #5
    //   15: iload #6
    //   17: ifne -> 70
    //   20: iload #4
    //   22: ifne -> 70
    //   25: aload_0
    //   26: invokevirtual getChildCount : ()I
    //   29: iconst_1
    //   30: if_icmple -> 70
    //   33: aload_0
    //   34: iconst_1
    //   35: invokevirtual getChildAt : (I)Landroid/view/View;
    //   38: astore #7
    //   40: aload #7
    //   42: ifnull -> 70
    //   45: aload_0
    //   46: aload_0
    //   47: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
    //   50: aload #7
    //   52: aload_1
    //   53: invokevirtual getX : ()F
    //   56: f2i
    //   57: aload_1
    //   58: invokevirtual getY : ()F
    //   61: f2i
    //   62: invokevirtual isViewUnder : (Landroid/view/View;II)Z
    //   65: iconst_1
    //   66: ixor
    //   67: putfield mPreservedOpenState : Z
    //   70: aload_0
    //   71: getfield mCanSlide : Z
    //   74: ifeq -> 280
    //   77: aload_0
    //   78: getfield mIsUnableToDrag : Z
    //   81: ifeq -> 92
    //   84: iload #4
    //   86: ifeq -> 92
    //   89: goto -> 280
    //   92: iload #4
    //   94: iconst_3
    //   95: if_icmpeq -> 271
    //   98: iload #4
    //   100: iconst_1
    //   101: if_icmpne -> 107
    //   104: goto -> 271
    //   107: iload #4
    //   109: ifeq -> 184
    //   112: iload #4
    //   114: iconst_2
    //   115: if_icmpeq -> 121
    //   118: goto -> 244
    //   121: aload_1
    //   122: invokevirtual getX : ()F
    //   125: fstore_3
    //   126: aload_1
    //   127: invokevirtual getY : ()F
    //   130: fstore_2
    //   131: fload_3
    //   132: aload_0
    //   133: getfield mInitialMotionX : F
    //   136: fsub
    //   137: invokestatic abs : (F)F
    //   140: fstore_3
    //   141: fload_2
    //   142: aload_0
    //   143: getfield mInitialMotionY : F
    //   146: fsub
    //   147: invokestatic abs : (F)F
    //   150: fstore_2
    //   151: fload_3
    //   152: aload_0
    //   153: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
    //   156: invokevirtual getTouchSlop : ()I
    //   159: i2f
    //   160: fcmpl
    //   161: ifle -> 244
    //   164: fload_2
    //   165: fload_3
    //   166: fcmpl
    //   167: ifle -> 244
    //   170: aload_0
    //   171: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
    //   174: invokevirtual cancel : ()V
    //   177: aload_0
    //   178: iconst_1
    //   179: putfield mIsUnableToDrag : Z
    //   182: iconst_0
    //   183: ireturn
    //   184: aload_0
    //   185: iconst_0
    //   186: putfield mIsUnableToDrag : Z
    //   189: aload_1
    //   190: invokevirtual getX : ()F
    //   193: fstore_2
    //   194: aload_1
    //   195: invokevirtual getY : ()F
    //   198: fstore_3
    //   199: aload_0
    //   200: fload_2
    //   201: putfield mInitialMotionX : F
    //   204: aload_0
    //   205: fload_3
    //   206: putfield mInitialMotionY : F
    //   209: aload_0
    //   210: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
    //   213: aload_0
    //   214: getfield mSlideableView : Landroid/view/View;
    //   217: fload_2
    //   218: f2i
    //   219: fload_3
    //   220: f2i
    //   221: invokevirtual isViewUnder : (Landroid/view/View;II)Z
    //   224: ifeq -> 244
    //   227: aload_0
    //   228: aload_0
    //   229: getfield mSlideableView : Landroid/view/View;
    //   232: invokevirtual isDimmed : (Landroid/view/View;)Z
    //   235: ifeq -> 244
    //   238: iconst_1
    //   239: istore #4
    //   241: goto -> 247
    //   244: iconst_0
    //   245: istore #4
    //   247: aload_0
    //   248: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
    //   251: aload_1
    //   252: invokevirtual shouldInterceptTouchEvent : (Landroid/view/MotionEvent;)Z
    //   255: ifne -> 268
    //   258: iload #4
    //   260: ifeq -> 265
    //   263: iconst_1
    //   264: ireturn
    //   265: iconst_0
    //   266: istore #5
    //   268: iload #5
    //   270: ireturn
    //   271: aload_0
    //   272: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
    //   275: invokevirtual cancel : ()V
    //   278: iconst_0
    //   279: ireturn
    //   280: aload_0
    //   281: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
    //   284: invokevirtual cancel : ()V
    //   287: aload_0
    //   288: aload_1
    //   289: invokespecial onInterceptTouchEvent : (Landroid/view/MotionEvent;)Z
    //   292: ireturn }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { // Byte code:
    //   0: aload_0
    //   1: invokevirtual isLayoutRtlSupport : ()Z
    //   4: istore #14
    //   6: iload #14
    //   8: ifeq -> 22
    //   11: aload_0
    //   12: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
    //   15: iconst_2
    //   16: invokevirtual setEdgeTrackingEnabled : (I)V
    //   19: goto -> 30
    //   22: aload_0
    //   23: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
    //   26: iconst_1
    //   27: invokevirtual setEdgeTrackingEnabled : (I)V
    //   30: iload #4
    //   32: iload_2
    //   33: isub
    //   34: istore #9
    //   36: iload #14
    //   38: ifeq -> 49
    //   41: aload_0
    //   42: invokevirtual getPaddingRight : ()I
    //   45: istore_2
    //   46: goto -> 54
    //   49: aload_0
    //   50: invokevirtual getPaddingLeft : ()I
    //   53: istore_2
    //   54: iload #14
    //   56: ifeq -> 68
    //   59: aload_0
    //   60: invokevirtual getPaddingLeft : ()I
    //   63: istore #4
    //   65: goto -> 74
    //   68: aload_0
    //   69: invokevirtual getPaddingRight : ()I
    //   72: istore #4
    //   74: aload_0
    //   75: invokevirtual getPaddingTop : ()I
    //   78: istore #11
    //   80: aload_0
    //   81: invokevirtual getChildCount : ()I
    //   84: istore #10
    //   86: aload_0
    //   87: getfield mFirstLayout : Z
    //   90: ifeq -> 122
    //   93: aload_0
    //   94: getfield mCanSlide : Z
    //   97: ifeq -> 113
    //   100: aload_0
    //   101: getfield mPreservedOpenState : Z
    //   104: ifeq -> 113
    //   107: fconst_1
    //   108: fstore #6
    //   110: goto -> 116
    //   113: fconst_0
    //   114: fstore #6
    //   116: aload_0
    //   117: fload #6
    //   119: putfield mSlideOffset : F
    //   122: iload_2
    //   123: istore_3
    //   124: iconst_0
    //   125: istore #5
    //   127: iload #5
    //   129: iload #10
    //   131: if_icmpge -> 427
    //   134: aload_0
    //   135: iload #5
    //   137: invokevirtual getChildAt : (I)Landroid/view/View;
    //   140: astore #15
    //   142: aload #15
    //   144: invokevirtual getVisibility : ()I
    //   147: bipush #8
    //   149: if_icmpne -> 155
    //   152: goto -> 418
    //   155: aload #15
    //   157: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   160: checkcast android/support/v4/widget/SlidingPaneLayout$LayoutParams
    //   163: astore #16
    //   165: aload #15
    //   167: invokevirtual getMeasuredWidth : ()I
    //   170: istore #12
    //   172: aload #16
    //   174: getfield slideable : Z
    //   177: ifeq -> 315
    //   180: aload #16
    //   182: getfield leftMargin : I
    //   185: istore #7
    //   187: aload #16
    //   189: getfield rightMargin : I
    //   192: istore #13
    //   194: iload #9
    //   196: iload #4
    //   198: isub
    //   199: istore #8
    //   201: iload_2
    //   202: iload #8
    //   204: aload_0
    //   205: getfield mOverhangSize : I
    //   208: isub
    //   209: invokestatic min : (II)I
    //   212: iload_3
    //   213: isub
    //   214: iload #7
    //   216: iload #13
    //   218: iadd
    //   219: isub
    //   220: istore #13
    //   222: aload_0
    //   223: iload #13
    //   225: putfield mSlideRange : I
    //   228: iload #14
    //   230: ifeq -> 243
    //   233: aload #16
    //   235: getfield rightMargin : I
    //   238: istore #7
    //   240: goto -> 250
    //   243: aload #16
    //   245: getfield leftMargin : I
    //   248: istore #7
    //   250: iload_3
    //   251: iload #7
    //   253: iadd
    //   254: iload #13
    //   256: iadd
    //   257: iload #12
    //   259: iconst_2
    //   260: idiv
    //   261: iadd
    //   262: iload #8
    //   264: if_icmple -> 272
    //   267: iconst_1
    //   268: istore_1
    //   269: goto -> 274
    //   272: iconst_0
    //   273: istore_1
    //   274: aload #16
    //   276: iload_1
    //   277: putfield dimWhenOffset : Z
    //   280: iload #13
    //   282: i2f
    //   283: aload_0
    //   284: getfield mSlideOffset : F
    //   287: fmul
    //   288: f2i
    //   289: istore #8
    //   291: iload #7
    //   293: iload #8
    //   295: iadd
    //   296: iload_3
    //   297: iadd
    //   298: istore_3
    //   299: aload_0
    //   300: iload #8
    //   302: i2f
    //   303: aload_0
    //   304: getfield mSlideRange : I
    //   307: i2f
    //   308: fdiv
    //   309: putfield mSlideOffset : F
    //   312: goto -> 351
    //   315: aload_0
    //   316: getfield mCanSlide : Z
    //   319: ifeq -> 349
    //   322: aload_0
    //   323: getfield mParallaxBy : I
    //   326: ifeq -> 349
    //   329: fconst_1
    //   330: aload_0
    //   331: getfield mSlideOffset : F
    //   334: fsub
    //   335: aload_0
    //   336: getfield mParallaxBy : I
    //   339: i2f
    //   340: fmul
    //   341: f2i
    //   342: istore #7
    //   344: iload_2
    //   345: istore_3
    //   346: goto -> 354
    //   349: iload_2
    //   350: istore_3
    //   351: iconst_0
    //   352: istore #7
    //   354: iload #14
    //   356: ifeq -> 378
    //   359: iload #9
    //   361: iload_3
    //   362: isub
    //   363: iload #7
    //   365: iadd
    //   366: istore #8
    //   368: iload #8
    //   370: iload #12
    //   372: isub
    //   373: istore #7
    //   375: goto -> 391
    //   378: iload_3
    //   379: iload #7
    //   381: isub
    //   382: istore #7
    //   384: iload #7
    //   386: iload #12
    //   388: iadd
    //   389: istore #8
    //   391: aload #15
    //   393: iload #7
    //   395: iload #11
    //   397: iload #8
    //   399: aload #15
    //   401: invokevirtual getMeasuredHeight : ()I
    //   404: iload #11
    //   406: iadd
    //   407: invokevirtual layout : (IIII)V
    //   410: iload_2
    //   411: aload #15
    //   413: invokevirtual getWidth : ()I
    //   416: iadd
    //   417: istore_2
    //   418: iload #5
    //   420: iconst_1
    //   421: iadd
    //   422: istore #5
    //   424: goto -> 127
    //   427: aload_0
    //   428: getfield mFirstLayout : Z
    //   431: ifeq -> 528
    //   434: aload_0
    //   435: getfield mCanSlide : Z
    //   438: ifeq -> 491
    //   441: aload_0
    //   442: getfield mParallaxBy : I
    //   445: ifeq -> 456
    //   448: aload_0
    //   449: aload_0
    //   450: getfield mSlideOffset : F
    //   453: invokespecial parallaxOtherViews : (F)V
    //   456: aload_0
    //   457: getfield mSlideableView : Landroid/view/View;
    //   460: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   463: checkcast android/support/v4/widget/SlidingPaneLayout$LayoutParams
    //   466: getfield dimWhenOffset : Z
    //   469: ifeq -> 520
    //   472: aload_0
    //   473: aload_0
    //   474: getfield mSlideableView : Landroid/view/View;
    //   477: aload_0
    //   478: getfield mSlideOffset : F
    //   481: aload_0
    //   482: getfield mSliderFadeColor : I
    //   485: invokespecial dimChildView : (Landroid/view/View;FI)V
    //   488: goto -> 520
    //   491: iconst_0
    //   492: istore_2
    //   493: iload_2
    //   494: iload #10
    //   496: if_icmpge -> 520
    //   499: aload_0
    //   500: aload_0
    //   501: iload_2
    //   502: invokevirtual getChildAt : (I)Landroid/view/View;
    //   505: fconst_0
    //   506: aload_0
    //   507: getfield mSliderFadeColor : I
    //   510: invokespecial dimChildView : (Landroid/view/View;FI)V
    //   513: iload_2
    //   514: iconst_1
    //   515: iadd
    //   516: istore_2
    //   517: goto -> 493
    //   520: aload_0
    //   521: aload_0
    //   522: getfield mSlideableView : Landroid/view/View;
    //   525: invokevirtual updateObscuredViewsVisibility : (Landroid/view/View;)V
    //   528: aload_0
    //   529: iconst_0
    //   530: putfield mFirstLayout : Z
    //   533: return }
  
  protected void onMeasure(int paramInt1, int paramInt2) { // Byte code:
    //   0: iload_1
    //   1: invokestatic getMode : (I)I
    //   4: istore #8
    //   6: iload_1
    //   7: invokestatic getSize : (I)I
    //   10: istore #5
    //   12: iload_2
    //   13: invokestatic getMode : (I)I
    //   16: istore #6
    //   18: iload_2
    //   19: invokestatic getSize : (I)I
    //   22: istore_2
    //   23: iload #8
    //   25: ldc_w 1073741824
    //   28: if_icmpeq -> 99
    //   31: aload_0
    //   32: invokevirtual isInEditMode : ()Z
    //   35: ifeq -> 88
    //   38: iload #8
    //   40: ldc_w -2147483648
    //   43: if_icmpne -> 59
    //   46: iload #5
    //   48: istore #7
    //   50: iload #6
    //   52: istore #9
    //   54: iload_2
    //   55: istore_1
    //   56: goto -> 163
    //   59: iload #5
    //   61: istore #7
    //   63: iload #6
    //   65: istore #9
    //   67: iload_2
    //   68: istore_1
    //   69: iload #8
    //   71: ifne -> 163
    //   74: sipush #300
    //   77: istore #7
    //   79: iload #6
    //   81: istore #9
    //   83: iload_2
    //   84: istore_1
    //   85: goto -> 163
    //   88: new java/lang/IllegalStateException
    //   91: dup
    //   92: ldc_w 'Width must have an exact value or MATCH_PARENT'
    //   95: invokespecial <init> : (Ljava/lang/String;)V
    //   98: athrow
    //   99: iload #5
    //   101: istore #7
    //   103: iload #6
    //   105: istore #9
    //   107: iload_2
    //   108: istore_1
    //   109: iload #6
    //   111: ifne -> 163
    //   114: aload_0
    //   115: invokevirtual isInEditMode : ()Z
    //   118: ifeq -> 152
    //   121: iload #5
    //   123: istore #7
    //   125: iload #6
    //   127: istore #9
    //   129: iload_2
    //   130: istore_1
    //   131: iload #6
    //   133: ifne -> 163
    //   136: ldc_w -2147483648
    //   139: istore #9
    //   141: sipush #300
    //   144: istore_1
    //   145: iload #5
    //   147: istore #7
    //   149: goto -> 163
    //   152: new java/lang/IllegalStateException
    //   155: dup
    //   156: ldc_w 'Height must not be UNSPECIFIED'
    //   159: invokespecial <init> : (Ljava/lang/String;)V
    //   162: athrow
    //   163: iload #9
    //   165: ldc_w -2147483648
    //   168: if_icmpeq -> 203
    //   171: iload #9
    //   173: ldc_w 1073741824
    //   176: if_icmpeq -> 186
    //   179: iconst_0
    //   180: istore_1
    //   181: iconst_0
    //   182: istore_2
    //   183: goto -> 217
    //   186: iload_1
    //   187: aload_0
    //   188: invokevirtual getPaddingTop : ()I
    //   191: isub
    //   192: aload_0
    //   193: invokevirtual getPaddingBottom : ()I
    //   196: isub
    //   197: istore_1
    //   198: iload_1
    //   199: istore_2
    //   200: goto -> 217
    //   203: iload_1
    //   204: aload_0
    //   205: invokevirtual getPaddingTop : ()I
    //   208: isub
    //   209: aload_0
    //   210: invokevirtual getPaddingBottom : ()I
    //   213: isub
    //   214: istore_2
    //   215: iconst_0
    //   216: istore_1
    //   217: iload #7
    //   219: aload_0
    //   220: invokevirtual getPaddingLeft : ()I
    //   223: isub
    //   224: aload_0
    //   225: invokevirtual getPaddingRight : ()I
    //   228: isub
    //   229: istore #11
    //   231: aload_0
    //   232: invokevirtual getChildCount : ()I
    //   235: istore #12
    //   237: iload #12
    //   239: iconst_2
    //   240: if_icmple -> 252
    //   243: ldc 'SlidingPaneLayout'
    //   245: ldc_w 'onMeasure: More than two child views are not supported.'
    //   248: invokestatic e : (Ljava/lang/String;Ljava/lang/String;)I
    //   251: pop
    //   252: aload_0
    //   253: aconst_null
    //   254: putfield mSlideableView : Landroid/view/View;
    //   257: iload #11
    //   259: istore #8
    //   261: iconst_0
    //   262: istore #10
    //   264: iconst_0
    //   265: istore #15
    //   267: fconst_0
    //   268: fstore #4
    //   270: iload_1
    //   271: istore #5
    //   273: iload #10
    //   275: iload #12
    //   277: if_icmpge -> 607
    //   280: aload_0
    //   281: iload #10
    //   283: invokevirtual getChildAt : (I)Landroid/view/View;
    //   286: astore #17
    //   288: aload #17
    //   290: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   293: checkcast android/support/v4/widget/SlidingPaneLayout$LayoutParams
    //   296: astore #18
    //   298: aload #17
    //   300: invokevirtual getVisibility : ()I
    //   303: bipush #8
    //   305: if_icmpne -> 323
    //   308: aload #18
    //   310: iconst_0
    //   311: putfield dimWhenOffset : Z
    //   314: fload #4
    //   316: fstore_3
    //   317: iload #5
    //   319: istore_1
    //   320: goto -> 592
    //   323: fload #4
    //   325: fstore_3
    //   326: aload #18
    //   328: getfield weight : F
    //   331: fconst_0
    //   332: fcmpl
    //   333: ifle -> 366
    //   336: fload #4
    //   338: aload #18
    //   340: getfield weight : F
    //   343: fadd
    //   344: fstore #4
    //   346: fload #4
    //   348: fstore_3
    //   349: aload #18
    //   351: getfield width : I
    //   354: ifne -> 366
    //   357: fload #4
    //   359: fstore_3
    //   360: iload #5
    //   362: istore_1
    //   363: goto -> 592
    //   366: aload #18
    //   368: getfield leftMargin : I
    //   371: aload #18
    //   373: getfield rightMargin : I
    //   376: iadd
    //   377: istore_1
    //   378: aload #18
    //   380: getfield width : I
    //   383: bipush #-2
    //   385: if_icmpne -> 402
    //   388: iload #11
    //   390: iload_1
    //   391: isub
    //   392: ldc_w -2147483648
    //   395: invokestatic makeMeasureSpec : (II)I
    //   398: istore_1
    //   399: goto -> 437
    //   402: aload #18
    //   404: getfield width : I
    //   407: iconst_m1
    //   408: if_icmpne -> 425
    //   411: iload #11
    //   413: iload_1
    //   414: isub
    //   415: ldc_w 1073741824
    //   418: invokestatic makeMeasureSpec : (II)I
    //   421: istore_1
    //   422: goto -> 437
    //   425: aload #18
    //   427: getfield width : I
    //   430: ldc_w 1073741824
    //   433: invokestatic makeMeasureSpec : (II)I
    //   436: istore_1
    //   437: aload #18
    //   439: getfield height : I
    //   442: bipush #-2
    //   444: if_icmpne -> 459
    //   447: iload_2
    //   448: ldc_w -2147483648
    //   451: invokestatic makeMeasureSpec : (II)I
    //   454: istore #6
    //   456: goto -> 493
    //   459: aload #18
    //   461: getfield height : I
    //   464: iconst_m1
    //   465: if_icmpne -> 480
    //   468: iload_2
    //   469: ldc_w 1073741824
    //   472: invokestatic makeMeasureSpec : (II)I
    //   475: istore #6
    //   477: goto -> 493
    //   480: aload #18
    //   482: getfield height : I
    //   485: ldc_w 1073741824
    //   488: invokestatic makeMeasureSpec : (II)I
    //   491: istore #6
    //   493: aload #17
    //   495: iload_1
    //   496: iload #6
    //   498: invokevirtual measure : (II)V
    //   501: aload #17
    //   503: invokevirtual getMeasuredWidth : ()I
    //   506: istore #6
    //   508: aload #17
    //   510: invokevirtual getMeasuredHeight : ()I
    //   513: istore #13
    //   515: iload #5
    //   517: istore_1
    //   518: iload #9
    //   520: ldc_w -2147483648
    //   523: if_icmpne -> 543
    //   526: iload #5
    //   528: istore_1
    //   529: iload #13
    //   531: iload #5
    //   533: if_icmple -> 543
    //   536: iload #13
    //   538: iload_2
    //   539: invokestatic min : (II)I
    //   542: istore_1
    //   543: iload #8
    //   545: iload #6
    //   547: isub
    //   548: istore #8
    //   550: iload #8
    //   552: ifge -> 561
    //   555: iconst_1
    //   556: istore #16
    //   558: goto -> 564
    //   561: iconst_0
    //   562: istore #16
    //   564: aload #18
    //   566: iload #16
    //   568: putfield slideable : Z
    //   571: aload #18
    //   573: getfield slideable : Z
    //   576: ifeq -> 585
    //   579: aload_0
    //   580: aload #17
    //   582: putfield mSlideableView : Landroid/view/View;
    //   585: iload #16
    //   587: iload #15
    //   589: ior
    //   590: istore #15
    //   592: iload #10
    //   594: iconst_1
    //   595: iadd
    //   596: istore #10
    //   598: fload_3
    //   599: fstore #4
    //   601: iload_1
    //   602: istore #5
    //   604: goto -> 273
    //   607: iload #15
    //   609: ifne -> 619
    //   612: fload #4
    //   614: fconst_0
    //   615: fcmpl
    //   616: ifle -> 1032
    //   619: iload #11
    //   621: aload_0
    //   622: getfield mOverhangSize : I
    //   625: isub
    //   626: istore #6
    //   628: iconst_0
    //   629: istore #9
    //   631: iload #9
    //   633: iload #12
    //   635: if_icmpge -> 1032
    //   638: aload_0
    //   639: iload #9
    //   641: invokevirtual getChildAt : (I)Landroid/view/View;
    //   644: astore #17
    //   646: aload #17
    //   648: invokevirtual getVisibility : ()I
    //   651: bipush #8
    //   653: if_icmpne -> 659
    //   656: goto -> 1023
    //   659: aload #17
    //   661: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   664: checkcast android/support/v4/widget/SlidingPaneLayout$LayoutParams
    //   667: astore #18
    //   669: aload #17
    //   671: invokevirtual getVisibility : ()I
    //   674: bipush #8
    //   676: if_icmpne -> 682
    //   679: goto -> 656
    //   682: aload #18
    //   684: getfield width : I
    //   687: ifne -> 705
    //   690: aload #18
    //   692: getfield weight : F
    //   695: fconst_0
    //   696: fcmpl
    //   697: ifle -> 705
    //   700: iconst_1
    //   701: istore_1
    //   702: goto -> 707
    //   705: iconst_0
    //   706: istore_1
    //   707: iload_1
    //   708: ifeq -> 717
    //   711: iconst_0
    //   712: istore #10
    //   714: goto -> 724
    //   717: aload #17
    //   719: invokevirtual getMeasuredWidth : ()I
    //   722: istore #10
    //   724: iload #15
    //   726: ifeq -> 852
    //   729: aload #17
    //   731: aload_0
    //   732: getfield mSlideableView : Landroid/view/View;
    //   735: if_acmpeq -> 852
    //   738: aload #18
    //   740: getfield width : I
    //   743: ifge -> 656
    //   746: iload #10
    //   748: iload #6
    //   750: if_icmpgt -> 763
    //   753: aload #18
    //   755: getfield weight : F
    //   758: fconst_0
    //   759: fcmpl
    //   760: ifle -> 656
    //   763: iload_1
    //   764: ifeq -> 823
    //   767: aload #18
    //   769: getfield height : I
    //   772: bipush #-2
    //   774: if_icmpne -> 788
    //   777: iload_2
    //   778: ldc_w -2147483648
    //   781: invokestatic makeMeasureSpec : (II)I
    //   784: istore_1
    //   785: goto -> 835
    //   788: aload #18
    //   790: getfield height : I
    //   793: iconst_m1
    //   794: if_icmpne -> 808
    //   797: iload_2
    //   798: ldc_w 1073741824
    //   801: invokestatic makeMeasureSpec : (II)I
    //   804: istore_1
    //   805: goto -> 835
    //   808: aload #18
    //   810: getfield height : I
    //   813: ldc_w 1073741824
    //   816: invokestatic makeMeasureSpec : (II)I
    //   819: istore_1
    //   820: goto -> 835
    //   823: aload #17
    //   825: invokevirtual getMeasuredHeight : ()I
    //   828: ldc_w 1073741824
    //   831: invokestatic makeMeasureSpec : (II)I
    //   834: istore_1
    //   835: aload #17
    //   837: iload #6
    //   839: ldc_w 1073741824
    //   842: invokestatic makeMeasureSpec : (II)I
    //   845: iload_1
    //   846: invokevirtual measure : (II)V
    //   849: goto -> 656
    //   852: aload #18
    //   854: getfield weight : F
    //   857: fconst_0
    //   858: fcmpl
    //   859: ifle -> 656
    //   862: aload #18
    //   864: getfield width : I
    //   867: ifne -> 926
    //   870: aload #18
    //   872: getfield height : I
    //   875: bipush #-2
    //   877: if_icmpne -> 891
    //   880: iload_2
    //   881: ldc_w -2147483648
    //   884: invokestatic makeMeasureSpec : (II)I
    //   887: istore_1
    //   888: goto -> 938
    //   891: aload #18
    //   893: getfield height : I
    //   896: iconst_m1
    //   897: if_icmpne -> 911
    //   900: iload_2
    //   901: ldc_w 1073741824
    //   904: invokestatic makeMeasureSpec : (II)I
    //   907: istore_1
    //   908: goto -> 938
    //   911: aload #18
    //   913: getfield height : I
    //   916: ldc_w 1073741824
    //   919: invokestatic makeMeasureSpec : (II)I
    //   922: istore_1
    //   923: goto -> 938
    //   926: aload #17
    //   928: invokevirtual getMeasuredHeight : ()I
    //   931: ldc_w 1073741824
    //   934: invokestatic makeMeasureSpec : (II)I
    //   937: istore_1
    //   938: iload #15
    //   940: ifeq -> 987
    //   943: iload #11
    //   945: aload #18
    //   947: getfield leftMargin : I
    //   950: aload #18
    //   952: getfield rightMargin : I
    //   955: iadd
    //   956: isub
    //   957: istore #13
    //   959: iload #13
    //   961: ldc_w 1073741824
    //   964: invokestatic makeMeasureSpec : (II)I
    //   967: istore #14
    //   969: iload #10
    //   971: iload #13
    //   973: if_icmpeq -> 656
    //   976: aload #17
    //   978: iload #14
    //   980: iload_1
    //   981: invokevirtual measure : (II)V
    //   984: goto -> 656
    //   987: iconst_0
    //   988: iload #8
    //   990: invokestatic max : (II)I
    //   993: istore #13
    //   995: aload #17
    //   997: iload #10
    //   999: aload #18
    //   1001: getfield weight : F
    //   1004: iload #13
    //   1006: i2f
    //   1007: fmul
    //   1008: fload #4
    //   1010: fdiv
    //   1011: f2i
    //   1012: iadd
    //   1013: ldc_w 1073741824
    //   1016: invokestatic makeMeasureSpec : (II)I
    //   1019: iload_1
    //   1020: invokevirtual measure : (II)V
    //   1023: iload #9
    //   1025: iconst_1
    //   1026: iadd
    //   1027: istore #9
    //   1029: goto -> 631
    //   1032: aload_0
    //   1033: iload #7
    //   1035: iload #5
    //   1037: aload_0
    //   1038: invokevirtual getPaddingTop : ()I
    //   1041: iadd
    //   1042: aload_0
    //   1043: invokevirtual getPaddingBottom : ()I
    //   1046: iadd
    //   1047: invokevirtual setMeasuredDimension : (II)V
    //   1050: aload_0
    //   1051: iload #15
    //   1053: putfield mCanSlide : Z
    //   1056: aload_0
    //   1057: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
    //   1060: invokevirtual getViewDragState : ()I
    //   1063: ifeq -> 1078
    //   1066: iload #15
    //   1068: ifne -> 1078
    //   1071: aload_0
    //   1072: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
    //   1075: invokevirtual abort : ()V
    //   1078: return }
  
  void onPanelDragged(int paramInt) {
    if (this.mSlideableView == null) {
      this.mSlideOffset = 0.0F;
      return;
    } 
    boolean bool = isLayoutRtlSupport();
    LayoutParams layoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
    int j = this.mSlideableView.getWidth();
    int i = paramInt;
    if (bool)
      i = getWidth() - paramInt - j; 
    if (bool) {
      paramInt = getPaddingRight();
    } else {
      paramInt = getPaddingLeft();
    } 
    if (bool) {
      j = layoutParams.rightMargin;
    } else {
      j = layoutParams.leftMargin;
    } 
    this.mSlideOffset = (i - paramInt + j) / this.mSlideRange;
    if (this.mParallaxBy != 0)
      parallaxOtherViews(this.mSlideOffset); 
    if (layoutParams.dimWhenOffset)
      dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor); 
    dispatchOnPanelSlide(this.mSlideableView);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable) {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
      return;
    } 
    SavedState savedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(savedState.getSuperState());
    if (savedState.isOpen) {
      openPane();
    } else {
      closePane();
    } 
    this.mPreservedOpenState = savedState.isOpen;
  }
  
  protected Parcelable onSaveInstanceState() {
    boolean bool;
    SavedState savedState = new SavedState(super.onSaveInstanceState());
    if (isSlideable()) {
      bool = isOpen();
    } else {
      bool = this.mPreservedOpenState;
    } 
    savedState.isOpen = bool;
    return savedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 != paramInt3)
      this.mFirstLayout = true; 
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) {
    if (!this.mCanSlide)
      return super.onTouchEvent(paramMotionEvent); 
    this.mDragHelper.processTouchEvent(paramMotionEvent);
    switch (paramMotionEvent.getActionMasked()) {
      default:
        return true;
      case 1:
        if (isDimmed(this.mSlideableView)) {
          float f3 = paramMotionEvent.getX();
          float f4 = paramMotionEvent.getY();
          float f5 = f3 - this.mInitialMotionX;
          float f6 = f4 - this.mInitialMotionY;
          int i = this.mDragHelper.getTouchSlop();
          if (f5 * f5 + f6 * f6 < (i * i) && this.mDragHelper.isViewUnder(this.mSlideableView, (int)f3, (int)f4)) {
            closePane(this.mSlideableView, 0);
            return true;
          } 
        } 
        return true;
      case 0:
        break;
    } 
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    this.mInitialMotionX = f1;
    this.mInitialMotionY = f2;
    return true;
  }
  
  public boolean openPane() { return openPane(this.mSlideableView, 0); }
  
  public void requestChildFocus(View paramView1, View paramView2) {
    super.requestChildFocus(paramView1, paramView2);
    if (!isInTouchMode() && !this.mCanSlide) {
      boolean bool;
      if (paramView1 == this.mSlideableView) {
        bool = true;
      } else {
        bool = false;
      } 
      this.mPreservedOpenState = bool;
    } 
  }
  
  void setAllChildrenVisible() {
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = getChildAt(b);
      if (view.getVisibility() == 4)
        view.setVisibility(0); 
    } 
  }
  
  public void setCoveredFadeColor(@ColorInt int paramInt) { this.mCoveredFadeColor = paramInt; }
  
  public void setPanelSlideListener(@Nullable PanelSlideListener paramPanelSlideListener) { this.mPanelSlideListener = paramPanelSlideListener; }
  
  public void setParallaxDistance(int paramInt) {
    this.mParallaxBy = paramInt;
    requestLayout();
  }
  
  @Deprecated
  public void setShadowDrawable(Drawable paramDrawable) { setShadowDrawableLeft(paramDrawable); }
  
  public void setShadowDrawableLeft(@Nullable Drawable paramDrawable) { this.mShadowDrawableLeft = paramDrawable; }
  
  public void setShadowDrawableRight(@Nullable Drawable paramDrawable) { this.mShadowDrawableRight = paramDrawable; }
  
  @Deprecated
  public void setShadowResource(@DrawableRes int paramInt) { setShadowDrawable(getResources().getDrawable(paramInt)); }
  
  public void setShadowResourceLeft(int paramInt) { setShadowDrawableLeft(ContextCompat.getDrawable(getContext(), paramInt)); }
  
  public void setShadowResourceRight(int paramInt) { setShadowDrawableRight(ContextCompat.getDrawable(getContext(), paramInt)); }
  
  public void setSliderFadeColor(@ColorInt int paramInt) { this.mSliderFadeColor = paramInt; }
  
  @Deprecated
  public void smoothSlideClosed() { closePane(); }
  
  @Deprecated
  public void smoothSlideOpen() { openPane(); }
  
  boolean smoothSlideTo(float paramFloat, int paramInt) {
    if (!this.mCanSlide)
      return false; 
    boolean bool = isLayoutRtlSupport();
    LayoutParams layoutParams = (LayoutParams)this.mSlideableView.getLayoutParams();
    if (bool) {
      paramInt = getPaddingRight();
      int i = layoutParams.rightMargin;
      int j = this.mSlideableView.getWidth();
      paramInt = (int)(getWidth() - (paramInt + i) + paramFloat * this.mSlideRange + j);
    } else {
      paramInt = (int)((getPaddingLeft() + layoutParams.leftMargin) + paramFloat * this.mSlideRange);
    } 
    if (this.mDragHelper.smoothSlideViewTo(this.mSlideableView, paramInt, this.mSlideableView.getTop())) {
      setAllChildrenVisible();
      ViewCompat.postInvalidateOnAnimation(this);
      return true;
    } 
    return false;
  }
  
  void updateObscuredViewsVisibility(View paramView) {
    boolean bool4;
    boolean bool3;
    boolean bool2;
    boolean bool1;
    int j;
    int i;
    boolean bool = isLayoutRtlSupport();
    if (bool) {
      i = getWidth() - getPaddingRight();
    } else {
      i = getPaddingLeft();
    } 
    if (bool) {
      j = getPaddingLeft();
    } else {
      j = getWidth() - getPaddingRight();
    } 
    int k = getPaddingTop();
    int m = getHeight();
    int n = getPaddingBottom();
    if (paramView != null && viewIsOpaque(paramView)) {
      bool1 = paramView.getLeft();
      bool2 = paramView.getRight();
      bool3 = paramView.getTop();
      bool4 = paramView.getBottom();
    } else {
      bool1 = false;
      bool2 = false;
      bool3 = false;
      bool4 = false;
    } 
    int i1 = getChildCount();
    byte b;
    for (b = 0; b < i1; b++) {
      View view = getChildAt(b);
      if (view == paramView)
        return; 
      if (view.getVisibility() != 8) {
        if (bool) {
          i2 = j;
        } else {
          i2 = i;
        } 
        int i3 = Math.max(i2, view.getLeft());
        int i4 = Math.max(k, view.getTop());
        if (bool) {
          i2 = i;
        } else {
          i2 = j;
        } 
        int i2 = Math.min(i2, view.getRight());
        int i5 = Math.min(m - n, view.getBottom());
        if (i3 >= bool1 && i4 >= bool3 && i2 <= bool2 && i5 <= bool4) {
          i2 = 4;
        } else {
          i2 = 0;
        } 
        view.setVisibility(i2);
      } 
    } 
  }
  
  class AccessibilityDelegate extends AccessibilityDelegateCompat {
    private final Rect mTmpRect = new Rect();
    
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
      param1AccessibilityNodeInfoCompat1.setMovementGranularities(param1AccessibilityNodeInfoCompat2.getMovementGranularities());
    }
    
    public boolean filter(View param1View) { return SlidingPaneLayout.this.isDimmed(param1View); }
    
    public void onInitializeAccessibilityEvent(View param1View, AccessibilityEvent param1AccessibilityEvent) {
      super.onInitializeAccessibilityEvent(param1View, param1AccessibilityEvent);
      param1AccessibilityEvent.setClassName(SlidingPaneLayout.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(View param1View, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) {
      AccessibilityNodeInfoCompat accessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain(param1AccessibilityNodeInfoCompat);
      super.onInitializeAccessibilityNodeInfo(param1View, accessibilityNodeInfoCompat);
      copyNodeInfoNoChildren(param1AccessibilityNodeInfoCompat, accessibilityNodeInfoCompat);
      accessibilityNodeInfoCompat.recycle();
      param1AccessibilityNodeInfoCompat.setClassName(SlidingPaneLayout.class.getName());
      param1AccessibilityNodeInfoCompat.setSource(param1View);
      ViewParent viewParent = ViewCompat.getParentForAccessibility(param1View);
      if (viewParent instanceof View)
        param1AccessibilityNodeInfoCompat.setParent((View)viewParent); 
      int i = SlidingPaneLayout.this.getChildCount();
      for (byte b = 0; b < i; b++) {
        View view = SlidingPaneLayout.this.getChildAt(b);
        if (!filter(view) && view.getVisibility() == 0) {
          ViewCompat.setImportantForAccessibility(view, 1);
          param1AccessibilityNodeInfoCompat.addChild(view);
        } 
      } 
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup param1ViewGroup, View param1View, AccessibilityEvent param1AccessibilityEvent) { return !filter(param1View) ? super.onRequestSendAccessibilityEvent(param1ViewGroup, param1View, param1AccessibilityEvent) : 0; }
  }
  
  private class DisableLayerRunnable implements Runnable {
    final View mChildView;
    
    DisableLayerRunnable(View param1View) { this.mChildView = param1View; }
    
    public void run() {
      if (this.mChildView.getParent() == SlidingPaneLayout.this) {
        this.mChildView.setLayerType(0, null);
        SlidingPaneLayout.this.invalidateChildRegion(this.mChildView);
      } 
      SlidingPaneLayout.this.mPostedRunnables.remove(this);
    }
  }
  
  private class DragHelperCallback extends ViewDragHelper.Callback {
    public int clampViewPositionHorizontal(View param1View, int param1Int1, int param1Int2) {
      SlidingPaneLayout.LayoutParams layoutParams = (SlidingPaneLayout.LayoutParams)SlidingPaneLayout.this.mSlideableView.getLayoutParams();
      if (SlidingPaneLayout.this.isLayoutRtlSupport()) {
        param1Int2 = SlidingPaneLayout.this.getWidth() - SlidingPaneLayout.this.getPaddingRight() + layoutParams.rightMargin + SlidingPaneLayout.this.mSlideableView.getWidth();
        int j = SlidingPaneLayout.this.mSlideRange;
        return Math.max(Math.min(param1Int1, param1Int2), param1Int2 - j);
      } 
      param1Int2 = SlidingPaneLayout.this.getPaddingLeft() + layoutParams.leftMargin;
      int i = SlidingPaneLayout.this.mSlideRange;
      return Math.min(Math.max(param1Int1, param1Int2), i + param1Int2);
    }
    
    public int clampViewPositionVertical(View param1View, int param1Int1, int param1Int2) { return param1View.getTop(); }
    
    public int getViewHorizontalDragRange(View param1View) { return SlidingPaneLayout.this.mSlideRange; }
    
    public void onEdgeDragStarted(int param1Int1, int param1Int2) { SlidingPaneLayout.this.mDragHelper.captureChildView(SlidingPaneLayout.this.mSlideableView, param1Int2); }
    
    public void onViewCaptured(View param1View, int param1Int) { SlidingPaneLayout.this.setAllChildrenVisible(); }
    
    public void onViewDragStateChanged(int param1Int) {
      if (SlidingPaneLayout.this.mDragHelper.getViewDragState() == 0) {
        if (SlidingPaneLayout.this.mSlideOffset == 0.0F) {
          SlidingPaneLayout.this.updateObscuredViewsVisibility(SlidingPaneLayout.this.mSlideableView);
          SlidingPaneLayout.this.dispatchOnPanelClosed(SlidingPaneLayout.this.mSlideableView);
          SlidingPaneLayout.this.mPreservedOpenState = false;
          return;
        } 
        SlidingPaneLayout.this.dispatchOnPanelOpened(SlidingPaneLayout.this.mSlideableView);
        SlidingPaneLayout.this.mPreservedOpenState = true;
      } 
    }
    
    public void onViewPositionChanged(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      SlidingPaneLayout.this.onPanelDragged(param1Int1);
      SlidingPaneLayout.this.invalidate();
    }
    
    public void onViewReleased(View param1View, float param1Float1, float param1Float2) { // Byte code:
      //   0: aload_1
      //   1: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
      //   4: checkcast android/support/v4/widget/SlidingPaneLayout$LayoutParams
      //   7: astore #6
      //   9: aload_0
      //   10: getfield this$0 : Landroid/support/v4/widget/SlidingPaneLayout;
      //   13: invokevirtual isLayoutRtlSupport : ()Z
      //   16: ifeq -> 109
      //   19: aload_0
      //   20: getfield this$0 : Landroid/support/v4/widget/SlidingPaneLayout;
      //   23: invokevirtual getPaddingRight : ()I
      //   26: aload #6
      //   28: getfield rightMargin : I
      //   31: iadd
      //   32: istore #5
      //   34: fload_2
      //   35: fconst_0
      //   36: fcmpg
      //   37: iflt -> 67
      //   40: iload #5
      //   42: istore #4
      //   44: fload_2
      //   45: fconst_0
      //   46: fcmpl
      //   47: ifne -> 79
      //   50: iload #5
      //   52: istore #4
      //   54: aload_0
      //   55: getfield this$0 : Landroid/support/v4/widget/SlidingPaneLayout;
      //   58: getfield mSlideOffset : F
      //   61: ldc 0.5
      //   63: fcmpl
      //   64: ifle -> 79
      //   67: iload #5
      //   69: aload_0
      //   70: getfield this$0 : Landroid/support/v4/widget/SlidingPaneLayout;
      //   73: getfield mSlideRange : I
      //   76: iadd
      //   77: istore #4
      //   79: aload_0
      //   80: getfield this$0 : Landroid/support/v4/widget/SlidingPaneLayout;
      //   83: getfield mSlideableView : Landroid/view/View;
      //   86: invokevirtual getWidth : ()I
      //   89: istore #5
      //   91: aload_0
      //   92: getfield this$0 : Landroid/support/v4/widget/SlidingPaneLayout;
      //   95: invokevirtual getWidth : ()I
      //   98: iload #4
      //   100: isub
      //   101: iload #5
      //   103: isub
      //   104: istore #4
      //   106: goto -> 173
      //   109: aload_0
      //   110: getfield this$0 : Landroid/support/v4/widget/SlidingPaneLayout;
      //   113: invokevirtual getPaddingLeft : ()I
      //   116: istore #4
      //   118: aload #6
      //   120: getfield leftMargin : I
      //   123: iload #4
      //   125: iadd
      //   126: istore #5
      //   128: fload_2
      //   129: fconst_0
      //   130: fcmpl
      //   131: ifgt -> 161
      //   134: iload #5
      //   136: istore #4
      //   138: fload_2
      //   139: fconst_0
      //   140: fcmpl
      //   141: ifne -> 173
      //   144: iload #5
      //   146: istore #4
      //   148: aload_0
      //   149: getfield this$0 : Landroid/support/v4/widget/SlidingPaneLayout;
      //   152: getfield mSlideOffset : F
      //   155: ldc 0.5
      //   157: fcmpl
      //   158: ifle -> 173
      //   161: iload #5
      //   163: aload_0
      //   164: getfield this$0 : Landroid/support/v4/widget/SlidingPaneLayout;
      //   167: getfield mSlideRange : I
      //   170: iadd
      //   171: istore #4
      //   173: aload_0
      //   174: getfield this$0 : Landroid/support/v4/widget/SlidingPaneLayout;
      //   177: getfield mDragHelper : Landroid/support/v4/widget/ViewDragHelper;
      //   180: iload #4
      //   182: aload_1
      //   183: invokevirtual getTop : ()I
      //   186: invokevirtual settleCapturedViewAt : (II)Z
      //   189: pop
      //   190: aload_0
      //   191: getfield this$0 : Landroid/support/v4/widget/SlidingPaneLayout;
      //   194: invokevirtual invalidate : ()V
      //   197: return }
    
    public boolean tryCaptureView(View param1View, int param1Int) { return SlidingPaneLayout.this.mIsUnableToDrag ? false : ((SlidingPaneLayout.LayoutParams)param1View.getLayoutParams()).slideable; }
  }
  
  public static class LayoutParams extends ViewGroup.MarginLayoutParams {
    private static final int[] ATTRS = { 16843137 };
    
    Paint dimPaint;
    
    boolean dimWhenOffset;
    
    boolean slideable;
    
    public float weight = 0.0F;
    
    public LayoutParams() { super(-1, -1); }
    
    public LayoutParams(int param1Int1, int param1Int2) { super(param1Int1, param1Int2); }
    
    public LayoutParams(@NonNull Context param1Context, @Nullable AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, ATTRS);
      this.weight = typedArray.getFloat(0, 0.0F);
      typedArray.recycle();
    }
    
    public LayoutParams(@NonNull LayoutParams param1LayoutParams) {
      super(param1LayoutParams);
      this.weight = param1LayoutParams.weight;
    }
    
    public LayoutParams(@NonNull ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(@NonNull ViewGroup.MarginLayoutParams param1MarginLayoutParams) { super(param1MarginLayoutParams); }
  }
  
  public static interface PanelSlideListener {
    void onPanelClosed(@NonNull View param1View);
    
    void onPanelOpened(@NonNull View param1View);
    
    void onPanelSlide(@NonNull View param1View, float param1Float);
  }
  
  static class SavedState extends AbsSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
        public SlidingPaneLayout.SavedState createFromParcel(Parcel param2Parcel) { return new SlidingPaneLayout.SavedState(param2Parcel, null); }
        
        public SlidingPaneLayout.SavedState createFromParcel(Parcel param2Parcel, ClassLoader param2ClassLoader) { return new SlidingPaneLayout.SavedState(param2Parcel, null); }
        
        public SlidingPaneLayout.SavedState[] newArray(int param2Int) { return new SlidingPaneLayout.SavedState[param2Int]; }
      };
    
    boolean isOpen;
    
    SavedState(Parcel param1Parcel, ClassLoader param1ClassLoader) {
      super(param1Parcel, param1ClassLoader);
      if (param1Parcel.readInt() != 0) {
        bool = true;
      } else {
        bool = false;
      } 
      this.isOpen = bool;
    }
    
    SavedState(Parcelable param1Parcelable) { super(param1Parcelable); }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  }
  
  static final class null extends Object implements Parcelable.ClassLoaderCreator<SavedState> {
    public SlidingPaneLayout.SavedState createFromParcel(Parcel param1Parcel) { return new SlidingPaneLayout.SavedState(param1Parcel, null); }
    
    public SlidingPaneLayout.SavedState createFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) { return new SlidingPaneLayout.SavedState(param1Parcel, null); }
    
    public SlidingPaneLayout.SavedState[] newArray(int param1Int) { return new SlidingPaneLayout.SavedState[param1Int]; }
  }
  
  public static class SimplePanelSlideListener implements PanelSlideListener {
    public void onPanelClosed(View param1View) {}
    
    public void onPanelOpened(View param1View) {}
    
    public void onPanelSlide(View param1View, float param1Float) {}
  }
  
  static interface SlidingPanelLayoutImpl {
    void invalidateChildRegion(SlidingPaneLayout param1SlidingPaneLayout, View param1View);
  }
  
  static class SlidingPanelLayoutImplBase implements SlidingPanelLayoutImpl {
    public void invalidateChildRegion(SlidingPaneLayout param1SlidingPaneLayout, View param1View) { ViewCompat.postInvalidateOnAnimation(param1SlidingPaneLayout, param1View.getLeft(), param1View.getTop(), param1View.getRight(), param1View.getBottom()); }
  }
  
  @RequiresApi(16)
  static class SlidingPanelLayoutImplJB extends SlidingPanelLayoutImplBase {
    private Method mGetDisplayList;
    
    private Field mRecreateDisplayList;
    
    SlidingPanelLayoutImplJB() {
      try {
        this.mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class[])null);
      } catch (NoSuchMethodException noSuchMethodException) {
        Log.e("SlidingPaneLayout", "Couldn't fetch getDisplayList method; dimming won't work right.", noSuchMethodException);
      } 
      try {
        this.mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
        this.mRecreateDisplayList.setAccessible(true);
        return;
      } catch (NoSuchFieldException noSuchFieldException) {
        Log.e("SlidingPaneLayout", "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", noSuchFieldException);
        return;
      } 
    }
    
    public void invalidateChildRegion(SlidingPaneLayout param1SlidingPaneLayout, View param1View) {
      if (this.mGetDisplayList != null && this.mRecreateDisplayList != null) {
        try {
          this.mRecreateDisplayList.setBoolean(param1View, true);
          this.mGetDisplayList.invoke(param1View, (Object[])null);
        } catch (Exception exception) {
          Log.e("SlidingPaneLayout", "Error refreshing display list state", exception);
        } 
        super.invalidateChildRegion(param1SlidingPaneLayout, param1View);
        return;
      } 
      param1View.invalidate();
    }
  }
  
  @RequiresApi(17)
  static class SlidingPanelLayoutImplJBMR1 extends SlidingPanelLayoutImplBase {
    public void invalidateChildRegion(SlidingPaneLayout param1SlidingPaneLayout, View param1View) { ViewCompat.setLayerPaint(param1View, ((SlidingPaneLayout.LayoutParams)param1View.getLayoutParams()).dimPaint); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/widget/SlidingPaneLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */