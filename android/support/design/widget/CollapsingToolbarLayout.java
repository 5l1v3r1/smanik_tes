package android.support.design.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.design.R;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.math.MathUtils;
import android.support.v4.util.ObjectsCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.ViewGroupUtils;
import android.support.v7.appcompat.R;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CollapsingToolbarLayout extends FrameLayout {
  private static final int DEFAULT_SCRIM_ANIMATION_DURATION = 600;
  
  final CollapsingTextHelper mCollapsingTextHelper;
  
  private boolean mCollapsingTitleEnabled;
  
  private Drawable mContentScrim;
  
  int mCurrentOffset;
  
  private boolean mDrawCollapsingTitle;
  
  private View mDummyView;
  
  private int mExpandedMarginBottom;
  
  private int mExpandedMarginEnd;
  
  private int mExpandedMarginStart;
  
  private int mExpandedMarginTop;
  
  WindowInsetsCompat mLastInsets;
  
  private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;
  
  private boolean mRefreshToolbar = true;
  
  private int mScrimAlpha;
  
  private long mScrimAnimationDuration;
  
  private ValueAnimator mScrimAnimator;
  
  private int mScrimVisibleHeightTrigger = -1;
  
  private boolean mScrimsAreShown;
  
  Drawable mStatusBarScrim;
  
  private final Rect mTmpRect = new Rect();
  
  private Toolbar mToolbar;
  
  private View mToolbarDirectChild;
  
  private int mToolbarId;
  
  public CollapsingToolbarLayout(Context paramContext) { this(paramContext, null); }
  
  public CollapsingToolbarLayout(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public CollapsingToolbarLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    ThemeUtils.checkAppCompatTheme(paramContext);
    this.mCollapsingTextHelper = new CollapsingTextHelper(this);
    this.mCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
    TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CollapsingToolbarLayout, paramInt, R.style.Widget_Design_CollapsingToolbar);
    this.mCollapsingTextHelper.setExpandedTextGravity(typedArray.getInt(R.styleable.CollapsingToolbarLayout_expandedTitleGravity, 8388691));
    this.mCollapsingTextHelper.setCollapsedTextGravity(typedArray.getInt(R.styleable.CollapsingToolbarLayout_collapsedTitleGravity, 8388627));
    paramInt = typedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMargin, 0);
    this.mExpandedMarginBottom = paramInt;
    this.mExpandedMarginEnd = paramInt;
    this.mExpandedMarginTop = paramInt;
    this.mExpandedMarginStart = paramInt;
    if (typedArray.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart))
      this.mExpandedMarginStart = typedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart, 0); 
    if (typedArray.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd))
      this.mExpandedMarginEnd = typedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd, 0); 
    if (typedArray.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop))
      this.mExpandedMarginTop = typedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop, 0); 
    if (typedArray.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom))
      this.mExpandedMarginBottom = typedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom, 0); 
    this.mCollapsingTitleEnabled = typedArray.getBoolean(R.styleable.CollapsingToolbarLayout_titleEnabled, true);
    setTitle(typedArray.getText(R.styleable.CollapsingToolbarLayout_title));
    this.mCollapsingTextHelper.setExpandedTextAppearance(R.style.TextAppearance_Design_CollapsingToolbar_Expanded);
    this.mCollapsingTextHelper.setCollapsedTextAppearance(R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);
    if (typedArray.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance))
      this.mCollapsingTextHelper.setExpandedTextAppearance(typedArray.getResourceId(R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance, 0)); 
    if (typedArray.hasValue(R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance))
      this.mCollapsingTextHelper.setCollapsedTextAppearance(typedArray.getResourceId(R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance, 0)); 
    this.mScrimVisibleHeightTrigger = typedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_scrimVisibleHeightTrigger, -1);
    this.mScrimAnimationDuration = typedArray.getInt(R.styleable.CollapsingToolbarLayout_scrimAnimationDuration, 600);
    setContentScrim(typedArray.getDrawable(R.styleable.CollapsingToolbarLayout_contentScrim));
    setStatusBarScrim(typedArray.getDrawable(R.styleable.CollapsingToolbarLayout_statusBarScrim));
    this.mToolbarId = typedArray.getResourceId(R.styleable.CollapsingToolbarLayout_toolbarId, -1);
    typedArray.recycle();
    setWillNotDraw(false);
    ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener() {
          public WindowInsetsCompat onApplyWindowInsets(View param1View, WindowInsetsCompat param1WindowInsetsCompat) { return CollapsingToolbarLayout.this.onWindowInsetChanged(param1WindowInsetsCompat); }
        });
  }
  
  private void animateScrim(int paramInt) {
    ensureToolbar();
    if (this.mScrimAnimator == null) {
      Interpolator interpolator;
      this.mScrimAnimator = new ValueAnimator();
      this.mScrimAnimator.setDuration(this.mScrimAnimationDuration);
      ValueAnimator valueAnimator = this.mScrimAnimator;
      if (paramInt > this.mScrimAlpha) {
        interpolator = AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR;
      } else {
        interpolator = AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR;
      } 
      valueAnimator.setInterpolator(interpolator);
      this.mScrimAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) { CollapsingToolbarLayout.this.setScrimAlpha(((Integer)param1ValueAnimator.getAnimatedValue()).intValue()); }
          });
    } else if (this.mScrimAnimator.isRunning()) {
      this.mScrimAnimator.cancel();
    } 
    this.mScrimAnimator.setIntValues(new int[] { this.mScrimAlpha, paramInt });
    this.mScrimAnimator.start();
  }
  
  private void ensureToolbar() {
    if (!this.mRefreshToolbar)
      return; 
    Object object = null;
    this.mToolbar = null;
    this.mToolbarDirectChild = null;
    if (this.mToolbarId != -1) {
      this.mToolbar = (Toolbar)findViewById(this.mToolbarId);
      if (this.mToolbar != null)
        this.mToolbarDirectChild = findDirectChild(this.mToolbar); 
    } 
    if (this.mToolbar == null) {
      Toolbar toolbar;
      int i = getChildCount();
      byte b = 0;
      while (true) {
        toolbar = object;
        if (b < i) {
          toolbar = getChildAt(b);
          if (toolbar instanceof Toolbar) {
            toolbar = (Toolbar)toolbar;
            break;
          } 
          b++;
          continue;
        } 
        break;
      } 
      this.mToolbar = toolbar;
    } 
    updateDummyView();
    this.mRefreshToolbar = false;
  }
  
  private View findDirectChild(View paramView) {
    ViewParent viewParent2 = paramView.getParent();
    View view = paramView;
    for (ViewParent viewParent1 = viewParent2; viewParent1 != this && viewParent1 != null; viewParent1 = viewParent1.getParent()) {
      if (viewParent1 instanceof View)
        view = (View)viewParent1; 
    } 
    return view;
  }
  
  private static int getHeightWithMargins(@NonNull View paramView) {
    ViewGroup.LayoutParams layoutParams = paramView.getLayoutParams();
    if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
      ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)layoutParams;
      return paramView.getHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
    } 
    return paramView.getHeight();
  }
  
  static ViewOffsetHelper getViewOffsetHelper(View paramView) {
    ViewOffsetHelper viewOffsetHelper2 = (ViewOffsetHelper)paramView.getTag(R.id.view_offset_helper);
    ViewOffsetHelper viewOffsetHelper1 = viewOffsetHelper2;
    if (viewOffsetHelper2 == null) {
      viewOffsetHelper1 = new ViewOffsetHelper(paramView);
      paramView.setTag(R.id.view_offset_helper, viewOffsetHelper1);
    } 
    return viewOffsetHelper1;
  }
  
  private boolean isToolbarChild(View paramView) {
    View view = this.mToolbarDirectChild;
    boolean bool = false;
    if ((this.mToolbarDirectChild == this) ? (paramView == this.mToolbar) : (paramView == this.mToolbarDirectChild))
      bool = true; 
    return bool;
  }
  
  private void updateDummyView() {
    if (!this.mCollapsingTitleEnabled && this.mDummyView != null) {
      ViewParent viewParent = this.mDummyView.getParent();
      if (viewParent instanceof ViewGroup)
        ((ViewGroup)viewParent).removeView(this.mDummyView); 
    } 
    if (this.mCollapsingTitleEnabled && this.mToolbar != null) {
      if (this.mDummyView == null)
        this.mDummyView = new View(getContext()); 
      if (this.mDummyView.getParent() == null)
        this.mToolbar.addView(this.mDummyView, -1, -1); 
    } 
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return paramLayoutParams instanceof LayoutParams; }
  
  public void draw(Canvas paramCanvas) {
    super.draw(paramCanvas);
    ensureToolbar();
    if (this.mToolbar == null && this.mContentScrim != null && this.mScrimAlpha > 0) {
      this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
      this.mContentScrim.draw(paramCanvas);
    } 
    if (this.mCollapsingTitleEnabled && this.mDrawCollapsingTitle)
      this.mCollapsingTextHelper.draw(paramCanvas); 
    if (this.mStatusBarScrim != null && this.mScrimAlpha > 0) {
      int i;
      if (this.mLastInsets != null) {
        i = this.mLastInsets.getSystemWindowInsetTop();
      } else {
        i = 0;
      } 
      if (i) {
        this.mStatusBarScrim.setBounds(0, -this.mCurrentOffset, getWidth(), i - this.mCurrentOffset);
        this.mStatusBarScrim.mutate().setAlpha(this.mScrimAlpha);
        this.mStatusBarScrim.draw(paramCanvas);
      } 
    } 
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong) {
    boolean bool;
    Drawable drawable = this.mContentScrim;
    boolean bool1 = true;
    if (drawable != null && this.mScrimAlpha > 0 && isToolbarChild(paramView)) {
      this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
      this.mContentScrim.draw(paramCanvas);
      bool = true;
    } else {
      bool = false;
    } 
    if (!super.drawChild(paramCanvas, paramView, paramLong)) {
      if (bool)
        return true; 
      bool1 = false;
    } 
    return bool1;
  }
  
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    Drawable drawable = this.mStatusBarScrim;
    byte b = 0;
    boolean bool1 = b;
    if (drawable != null) {
      bool1 = b;
      if (drawable.isStateful())
        bool1 = false | drawable.setState(arrayOfInt); 
    } 
    drawable = this.mContentScrim;
    boolean bool2 = bool1;
    if (drawable != null) {
      bool2 = bool1;
      if (drawable.isStateful())
        bool2 = bool1 | drawable.setState(arrayOfInt); 
    } 
    bool1 = bool2;
    if (this.mCollapsingTextHelper != null)
      bool1 = bool2 | this.mCollapsingTextHelper.setState(arrayOfInt); 
    if (bool1)
      invalidate(); 
  }
  
  protected LayoutParams generateDefaultLayoutParams() { return new LayoutParams(-1, -1); }
  
  public FrameLayout.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return new LayoutParams(getContext(), paramAttributeSet); }
  
  protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return new LayoutParams(paramLayoutParams); }
  
  public int getCollapsedTitleGravity() { return this.mCollapsingTextHelper.getCollapsedTextGravity(); }
  
  @NonNull
  public Typeface getCollapsedTitleTypeface() { return this.mCollapsingTextHelper.getCollapsedTypeface(); }
  
  @Nullable
  public Drawable getContentScrim() { return this.mContentScrim; }
  
  public int getExpandedTitleGravity() { return this.mCollapsingTextHelper.getExpandedTextGravity(); }
  
  public int getExpandedTitleMarginBottom() { return this.mExpandedMarginBottom; }
  
  public int getExpandedTitleMarginEnd() { return this.mExpandedMarginEnd; }
  
  public int getExpandedTitleMarginStart() { return this.mExpandedMarginStart; }
  
  public int getExpandedTitleMarginTop() { return this.mExpandedMarginTop; }
  
  @NonNull
  public Typeface getExpandedTitleTypeface() { return this.mCollapsingTextHelper.getExpandedTypeface(); }
  
  final int getMaxOffsetForPinChild(View paramView) {
    ViewOffsetHelper viewOffsetHelper = getViewOffsetHelper(paramView);
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    return getHeight() - viewOffsetHelper.getLayoutTop() - paramView.getHeight() - layoutParams.bottomMargin;
  }
  
  int getScrimAlpha() { return this.mScrimAlpha; }
  
  public long getScrimAnimationDuration() { return this.mScrimAnimationDuration; }
  
  public int getScrimVisibleHeightTrigger() {
    int i;
    if (this.mScrimVisibleHeightTrigger >= 0)
      return this.mScrimVisibleHeightTrigger; 
    if (this.mLastInsets != null) {
      i = this.mLastInsets.getSystemWindowInsetTop();
    } else {
      i = 0;
    } 
    int j = ViewCompat.getMinimumHeight(this);
    return (j > 0) ? Math.min(j * 2 + i, getHeight()) : (getHeight() / 3);
  }
  
  @Nullable
  public Drawable getStatusBarScrim() { return this.mStatusBarScrim; }
  
  @Nullable
  public CharSequence getTitle() { return this.mCollapsingTitleEnabled ? this.mCollapsingTextHelper.getText() : null; }
  
  public boolean isTitleEnabled() { return this.mCollapsingTitleEnabled; }
  
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    ViewParent viewParent = getParent();
    if (viewParent instanceof AppBarLayout) {
      ViewCompat.setFitsSystemWindows(this, ViewCompat.getFitsSystemWindows((View)viewParent));
      if (this.mOnOffsetChangedListener == null)
        this.mOnOffsetChangedListener = new OffsetUpdateListener(); 
      ((AppBarLayout)viewParent).addOnOffsetChangedListener(this.mOnOffsetChangedListener);
      ViewCompat.requestApplyInsets(this);
    } 
  }
  
  protected void onDetachedFromWindow() {
    ViewParent viewParent = getParent();
    if (this.mOnOffsetChangedListener != null && viewParent instanceof AppBarLayout)
      ((AppBarLayout)viewParent).removeOnOffsetChangedListener(this.mOnOffsetChangedListener); 
    super.onDetachedFromWindow();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    WindowInsetsCompat windowInsetsCompat = this.mLastInsets;
    int i = 0;
    if (windowInsetsCompat != null) {
      int j = this.mLastInsets.getSystemWindowInsetTop();
      int k = getChildCount();
      byte b;
      for (b = 0; b < k; b++) {
        View view = getChildAt(b);
        if (!ViewCompat.getFitsSystemWindows(view) && view.getTop() < j)
          ViewCompat.offsetTopAndBottom(view, j); 
      } 
    } 
    if (this.mCollapsingTitleEnabled && this.mDummyView != null) {
      paramBoolean = ViewCompat.isAttachedToWindow(this.mDummyView);
      int j = 1;
      if (paramBoolean && this.mDummyView.getVisibility() == 0) {
        paramBoolean = true;
      } else {
        paramBoolean = false;
      } 
      this.mDrawCollapsingTitle = paramBoolean;
      if (this.mDrawCollapsingTitle) {
        Toolbar toolbar;
        int k;
        if (ViewCompat.getLayoutDirection(this) != 1)
          j = 0; 
        if (this.mToolbarDirectChild != null) {
          toolbar = this.mToolbarDirectChild;
        } else {
          toolbar = this.mToolbar;
        } 
        int n = getMaxOffsetForPinChild(toolbar);
        ViewGroupUtils.getDescendantRect(this, this.mDummyView, this.mTmpRect);
        CollapsingTextHelper collapsingTextHelper = this.mCollapsingTextHelper;
        int i1 = this.mTmpRect.left;
        if (j) {
          k = this.mToolbar.getTitleMarginEnd();
        } else {
          k = this.mToolbar.getTitleMarginStart();
        } 
        int i2 = this.mTmpRect.top;
        int i3 = this.mToolbar.getTitleMarginTop();
        int i4 = this.mTmpRect.right;
        if (j) {
          m = this.mToolbar.getTitleMarginStart();
        } else {
          m = this.mToolbar.getTitleMarginEnd();
        } 
        collapsingTextHelper.setCollapsedBounds(i1 + k, i2 + n + i3, i4 + m, this.mTmpRect.bottom + n - this.mToolbar.getTitleMarginBottom());
        collapsingTextHelper = this.mCollapsingTextHelper;
        if (j) {
          k = this.mExpandedMarginEnd;
        } else {
          k = this.mExpandedMarginStart;
        } 
        int m = this.mTmpRect.top;
        n = this.mExpandedMarginTop;
        if (j) {
          j = this.mExpandedMarginStart;
        } else {
          j = this.mExpandedMarginEnd;
        } 
        collapsingTextHelper.setExpandedBounds(k, m + n, paramInt3 - paramInt1 - j, paramInt4 - paramInt2 - this.mExpandedMarginBottom);
        this.mCollapsingTextHelper.recalculate();
      } 
    } 
    paramInt2 = getChildCount();
    for (paramInt1 = i; paramInt1 < paramInt2; paramInt1++)
      getViewOffsetHelper(getChildAt(paramInt1)).onViewLayout(); 
    if (this.mToolbar != null) {
      if (this.mCollapsingTitleEnabled && TextUtils.isEmpty(this.mCollapsingTextHelper.getText()))
        this.mCollapsingTextHelper.setText(this.mToolbar.getTitle()); 
      if (this.mToolbarDirectChild == null || this.mToolbarDirectChild == this) {
        setMinimumHeight(getHeightWithMargins(this.mToolbar));
      } else {
        setMinimumHeight(getHeightWithMargins(this.mToolbarDirectChild));
      } 
    } 
    updateScrimVisibility();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    ensureToolbar();
    super.onMeasure(paramInt1, paramInt2);
    int i = View.MeasureSpec.getMode(paramInt2);
    if (this.mLastInsets != null) {
      paramInt2 = this.mLastInsets.getSystemWindowInsetTop();
    } else {
      paramInt2 = 0;
    } 
    if (i == 0 && paramInt2 > 0)
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() + paramInt2, 1073741824)); 
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mContentScrim != null)
      this.mContentScrim.setBounds(0, 0, paramInt1, paramInt2); 
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
      requestLayout();
    } 
    return paramWindowInsetsCompat.consumeSystemWindowInsets();
  }
  
  public void setCollapsedTitleGravity(int paramInt) { this.mCollapsingTextHelper.setCollapsedTextGravity(paramInt); }
  
  public void setCollapsedTitleTextAppearance(@StyleRes int paramInt) { this.mCollapsingTextHelper.setCollapsedTextAppearance(paramInt); }
  
  public void setCollapsedTitleTextColor(@ColorInt int paramInt) { setCollapsedTitleTextColor(ColorStateList.valueOf(paramInt)); }
  
  public void setCollapsedTitleTextColor(@NonNull ColorStateList paramColorStateList) { this.mCollapsingTextHelper.setCollapsedTextColor(paramColorStateList); }
  
  public void setCollapsedTitleTypeface(@Nullable Typeface paramTypeface) { this.mCollapsingTextHelper.setCollapsedTypeface(paramTypeface); }
  
  public void setContentScrim(@Nullable Drawable paramDrawable) {
    if (this.mContentScrim != paramDrawable) {
      Drawable drawable2 = this.mContentScrim;
      Drawable drawable1 = null;
      if (drawable2 != null)
        this.mContentScrim.setCallback(null); 
      if (paramDrawable != null)
        drawable1 = paramDrawable.mutate(); 
      this.mContentScrim = drawable1;
      if (this.mContentScrim != null) {
        this.mContentScrim.setBounds(0, 0, getWidth(), getHeight());
        this.mContentScrim.setCallback(this);
        this.mContentScrim.setAlpha(this.mScrimAlpha);
      } 
      ViewCompat.postInvalidateOnAnimation(this);
    } 
  }
  
  public void setContentScrimColor(@ColorInt int paramInt) { setContentScrim(new ColorDrawable(paramInt)); }
  
  public void setContentScrimResource(@DrawableRes int paramInt) { setContentScrim(ContextCompat.getDrawable(getContext(), paramInt)); }
  
  public void setExpandedTitleColor(@ColorInt int paramInt) { setExpandedTitleTextColor(ColorStateList.valueOf(paramInt)); }
  
  public void setExpandedTitleGravity(int paramInt) { this.mCollapsingTextHelper.setExpandedTextGravity(paramInt); }
  
  public void setExpandedTitleMargin(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.mExpandedMarginStart = paramInt1;
    this.mExpandedMarginTop = paramInt2;
    this.mExpandedMarginEnd = paramInt3;
    this.mExpandedMarginBottom = paramInt4;
    requestLayout();
  }
  
  public void setExpandedTitleMarginBottom(int paramInt) {
    this.mExpandedMarginBottom = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleMarginEnd(int paramInt) {
    this.mExpandedMarginEnd = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleMarginStart(int paramInt) {
    this.mExpandedMarginStart = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleMarginTop(int paramInt) {
    this.mExpandedMarginTop = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleTextAppearance(@StyleRes int paramInt) { this.mCollapsingTextHelper.setExpandedTextAppearance(paramInt); }
  
  public void setExpandedTitleTextColor(@NonNull ColorStateList paramColorStateList) { this.mCollapsingTextHelper.setExpandedTextColor(paramColorStateList); }
  
  public void setExpandedTitleTypeface(@Nullable Typeface paramTypeface) { this.mCollapsingTextHelper.setExpandedTypeface(paramTypeface); }
  
  void setScrimAlpha(int paramInt) {
    if (paramInt != this.mScrimAlpha) {
      if (this.mContentScrim != null && this.mToolbar != null)
        ViewCompat.postInvalidateOnAnimation(this.mToolbar); 
      this.mScrimAlpha = paramInt;
      ViewCompat.postInvalidateOnAnimation(this);
    } 
  }
  
  public void setScrimAnimationDuration(@IntRange(from = 0L) long paramLong) { this.mScrimAnimationDuration = paramLong; }
  
  public void setScrimVisibleHeightTrigger(@IntRange(from = 0L) int paramInt) {
    if (this.mScrimVisibleHeightTrigger != paramInt) {
      this.mScrimVisibleHeightTrigger = paramInt;
      updateScrimVisibility();
    } 
  }
  
  public void setScrimsShown(boolean paramBoolean) {
    boolean bool;
    if (ViewCompat.isLaidOut(this) && !isInEditMode()) {
      bool = true;
    } else {
      bool = false;
    } 
    setScrimsShown(paramBoolean, bool);
  }
  
  public void setScrimsShown(boolean paramBoolean1, boolean paramBoolean2) {
    if (this.mScrimsAreShown != paramBoolean1) {
      byte b = 0;
      char c = Character.MIN_VALUE;
      if (paramBoolean2) {
        if (paramBoolean1)
          c = 'ÿ'; 
        animateScrim(c);
      } else {
        c = b;
        if (paramBoolean1)
          c = 'ÿ'; 
        setScrimAlpha(c);
      } 
      this.mScrimsAreShown = paramBoolean1;
    } 
  }
  
  public void setStatusBarScrim(@Nullable Drawable paramDrawable) {
    if (this.mStatusBarScrim != paramDrawable) {
      Drawable drawable2 = this.mStatusBarScrim;
      Drawable drawable1 = null;
      if (drawable2 != null)
        this.mStatusBarScrim.setCallback(null); 
      if (paramDrawable != null)
        drawable1 = paramDrawable.mutate(); 
      this.mStatusBarScrim = drawable1;
      if (this.mStatusBarScrim != null) {
        boolean bool;
        if (this.mStatusBarScrim.isStateful())
          this.mStatusBarScrim.setState(getDrawableState()); 
        DrawableCompat.setLayoutDirection(this.mStatusBarScrim, ViewCompat.getLayoutDirection(this));
        paramDrawable = this.mStatusBarScrim;
        if (getVisibility() == 0) {
          bool = true;
        } else {
          bool = false;
        } 
        paramDrawable.setVisible(bool, false);
        this.mStatusBarScrim.setCallback(this);
        this.mStatusBarScrim.setAlpha(this.mScrimAlpha);
      } 
      ViewCompat.postInvalidateOnAnimation(this);
    } 
  }
  
  public void setStatusBarScrimColor(@ColorInt int paramInt) { setStatusBarScrim(new ColorDrawable(paramInt)); }
  
  public void setStatusBarScrimResource(@DrawableRes int paramInt) { setStatusBarScrim(ContextCompat.getDrawable(getContext(), paramInt)); }
  
  public void setTitle(@Nullable CharSequence paramCharSequence) { this.mCollapsingTextHelper.setText(paramCharSequence); }
  
  public void setTitleEnabled(boolean paramBoolean) {
    if (paramBoolean != this.mCollapsingTitleEnabled) {
      this.mCollapsingTitleEnabled = paramBoolean;
      updateDummyView();
      requestLayout();
    } 
  }
  
  public void setVisibility(int paramInt) {
    boolean bool;
    super.setVisibility(paramInt);
    if (paramInt == 0) {
      bool = true;
    } else {
      bool = false;
    } 
    if (this.mStatusBarScrim != null && this.mStatusBarScrim.isVisible() != bool)
      this.mStatusBarScrim.setVisible(bool, false); 
    if (this.mContentScrim != null && this.mContentScrim.isVisible() != bool)
      this.mContentScrim.setVisible(bool, false); 
  }
  
  final void updateScrimVisibility() {
    if (this.mContentScrim != null || this.mStatusBarScrim != null) {
      boolean bool;
      if (getHeight() + this.mCurrentOffset < getScrimVisibleHeightTrigger()) {
        bool = true;
      } else {
        bool = false;
      } 
      setScrimsShown(bool);
    } 
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable) { return (super.verifyDrawable(paramDrawable) || paramDrawable == this.mContentScrim || paramDrawable == this.mStatusBarScrim); }
  
  public static class LayoutParams extends FrameLayout.LayoutParams {
    public static final int COLLAPSE_MODE_OFF = 0;
    
    public static final int COLLAPSE_MODE_PARALLAX = 2;
    
    public static final int COLLAPSE_MODE_PIN = 1;
    
    private static final float DEFAULT_PARALLAX_MULTIPLIER = 0.5F;
    
    int mCollapseMode = 0;
    
    float mParallaxMult = 0.5F;
    
    public LayoutParams(int param1Int1, int param1Int2) { super(param1Int1, param1Int2); }
    
    public LayoutParams(int param1Int1, int param1Int2, int param1Int3) { super(param1Int1, param1Int2, param1Int3); }
    
    public LayoutParams(Context param1Context, AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, R.styleable.CollapsingToolbarLayout_Layout);
      this.mCollapseMode = typedArray.getInt(R.styleable.CollapsingToolbarLayout_Layout_layout_collapseMode, 0);
      setParallaxMultiplier(typedArray.getFloat(R.styleable.CollapsingToolbarLayout_Layout_layout_collapseParallaxMultiplier, 0.5F));
      typedArray.recycle();
    }
    
    public LayoutParams(ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.MarginLayoutParams param1MarginLayoutParams) { super(param1MarginLayoutParams); }
    
    @RequiresApi(19)
    public LayoutParams(FrameLayout.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public int getCollapseMode() { return this.mCollapseMode; }
    
    public float getParallaxMultiplier() { return this.mParallaxMult; }
    
    public void setCollapseMode(int param1Int) { this.mCollapseMode = param1Int; }
    
    public void setParallaxMultiplier(float param1Float) { this.mParallaxMult = param1Float; }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    static @interface CollapseMode {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static @interface CollapseMode {}
  
  private class OffsetUpdateListener implements AppBarLayout.OnOffsetChangedListener {
    public void onOffsetChanged(AppBarLayout param1AppBarLayout, int param1Int) {
      int i;
      CollapsingToolbarLayout.this.mCurrentOffset = param1Int;
      if (CollapsingToolbarLayout.this.mLastInsets != null) {
        i = CollapsingToolbarLayout.this.mLastInsets.getSystemWindowInsetTop();
      } else {
        i = 0;
      } 
      int k = CollapsingToolbarLayout.this.getChildCount();
      int j;
      for (j = 0; j < k; j++) {
        View view = CollapsingToolbarLayout.this.getChildAt(j);
        CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams)view.getLayoutParams();
        ViewOffsetHelper viewOffsetHelper = CollapsingToolbarLayout.getViewOffsetHelper(view);
        switch (layoutParams.mCollapseMode) {
          case 2:
            viewOffsetHelper.setTopAndBottomOffset(Math.round(-param1Int * layoutParams.mParallaxMult));
            break;
          case 1:
            viewOffsetHelper.setTopAndBottomOffset(MathUtils.clamp(-param1Int, 0, CollapsingToolbarLayout.this.getMaxOffsetForPinChild(view)));
            break;
        } 
      } 
      CollapsingToolbarLayout.this.updateScrimVisibility();
      if (CollapsingToolbarLayout.this.mStatusBarScrim != null && i)
        ViewCompat.postInvalidateOnAnimation(CollapsingToolbarLayout.this); 
      j = CollapsingToolbarLayout.this.getHeight();
      k = ViewCompat.getMinimumHeight(CollapsingToolbarLayout.this);
      CollapsingToolbarLayout.this.mCollapsingTextHelper.setExpansionFraction(Math.abs(param1Int) / (j - k - i));
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/CollapsingToolbarLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */