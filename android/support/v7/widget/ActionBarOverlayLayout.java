package android.support.v7.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuPresenter;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.widget.OverScroller;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class ActionBarOverlayLayout extends ViewGroup implements DecorContentParent, NestedScrollingParent {
  static final int[] ATTRS = { R.attr.actionBarSize, 16842841 };
  
  private static final String TAG = "ActionBarOverlayLayout";
  
  private final int ACTION_BAR_ANIMATE_DELAY = 600;
  
  private int mActionBarHeight;
  
  ActionBarContainer mActionBarTop;
  
  private ActionBarVisibilityCallback mActionBarVisibilityCallback;
  
  private final Runnable mAddActionBarHideOffset = new Runnable() {
      public void run() {
        ActionBarOverlayLayout.this.haltActionBarHideOffsetAnimations();
        ActionBarOverlayLayout.this.mCurrentActionBarTopAnimator = ActionBarOverlayLayout.this.mActionBarTop.animate().translationY(-ActionBarOverlayLayout.this.mActionBarTop.getHeight()).setListener(ActionBarOverlayLayout.this.mTopAnimatorListener);
      }
    };
  
  boolean mAnimatingForFling;
  
  private final Rect mBaseContentInsets = new Rect();
  
  private final Rect mBaseInnerInsets = new Rect();
  
  private ContentFrameLayout mContent;
  
  private final Rect mContentInsets = new Rect();
  
  ViewPropertyAnimator mCurrentActionBarTopAnimator;
  
  private DecorToolbar mDecorToolbar;
  
  private OverScroller mFlingEstimator;
  
  private boolean mHasNonEmbeddedTabs;
  
  private boolean mHideOnContentScroll;
  
  private int mHideOnContentScrollReference;
  
  private boolean mIgnoreWindowContentOverlay;
  
  private final Rect mInnerInsets = new Rect();
  
  private final Rect mLastBaseContentInsets = new Rect();
  
  private final Rect mLastBaseInnerInsets = new Rect();
  
  private final Rect mLastInnerInsets = new Rect();
  
  private int mLastSystemUiVisibility;
  
  private boolean mOverlayMode;
  
  private final NestedScrollingParentHelper mParentHelper;
  
  private final Runnable mRemoveActionBarHideOffset = new Runnable() {
      public void run() {
        ActionBarOverlayLayout.this.haltActionBarHideOffsetAnimations();
        ActionBarOverlayLayout.this.mCurrentActionBarTopAnimator = ActionBarOverlayLayout.this.mActionBarTop.animate().translationY(0.0F).setListener(ActionBarOverlayLayout.this.mTopAnimatorListener);
      }
    };
  
  final AnimatorListenerAdapter mTopAnimatorListener = new AnimatorListenerAdapter() {
      public void onAnimationCancel(Animator param1Animator) {
        ActionBarOverlayLayout.this.mCurrentActionBarTopAnimator = null;
        ActionBarOverlayLayout.this.mAnimatingForFling = false;
      }
      
      public void onAnimationEnd(Animator param1Animator) {
        ActionBarOverlayLayout.this.mCurrentActionBarTopAnimator = null;
        ActionBarOverlayLayout.this.mAnimatingForFling = false;
      }
    };
  
  private Drawable mWindowContentOverlay;
  
  private int mWindowVisibility = 0;
  
  public ActionBarOverlayLayout(Context paramContext) { this(paramContext, null); }
  
  public ActionBarOverlayLayout(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    init(paramContext);
    this.mParentHelper = new NestedScrollingParentHelper(this);
  }
  
  private void addActionBarHideOffset() {
    haltActionBarHideOffsetAnimations();
    this.mAddActionBarHideOffset.run();
  }
  
  private boolean applyInsets(View paramView, Rect paramRect, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4) {
    boolean bool;
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if (paramBoolean1 && layoutParams.leftMargin != paramRect.left) {
      layoutParams.leftMargin = paramRect.left;
      bool = true;
    } else {
      bool = false;
    } 
    paramBoolean1 = bool;
    if (paramBoolean2) {
      paramBoolean1 = bool;
      if (layoutParams.topMargin != paramRect.top) {
        layoutParams.topMargin = paramRect.top;
        paramBoolean1 = true;
      } 
    } 
    paramBoolean2 = paramBoolean1;
    if (paramBoolean4) {
      paramBoolean2 = paramBoolean1;
      if (layoutParams.rightMargin != paramRect.right) {
        layoutParams.rightMargin = paramRect.right;
        paramBoolean2 = true;
      } 
    } 
    paramBoolean1 = paramBoolean2;
    if (paramBoolean3) {
      paramBoolean1 = paramBoolean2;
      if (layoutParams.bottomMargin != paramRect.bottom) {
        layoutParams.bottomMargin = paramRect.bottom;
        paramBoolean1 = true;
      } 
    } 
    return paramBoolean1;
  }
  
  private DecorToolbar getDecorToolbar(View paramView) {
    if (paramView instanceof DecorToolbar)
      return (DecorToolbar)paramView; 
    if (paramView instanceof Toolbar)
      return ((Toolbar)paramView).getWrapper(); 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Can't make a decor toolbar out of ");
    stringBuilder.append(paramView.getClass().getSimpleName());
    throw new IllegalStateException(stringBuilder.toString());
  }
  
  private void init(Context paramContext) {
    TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(ATTRS);
    boolean bool1 = false;
    this.mActionBarHeight = typedArray.getDimensionPixelSize(0, 0);
    this.mWindowContentOverlay = typedArray.getDrawable(1);
    if (this.mWindowContentOverlay == null) {
      bool = true;
    } else {
      bool = false;
    } 
    setWillNotDraw(bool);
    typedArray.recycle();
    boolean bool = bool1;
    if ((paramContext.getApplicationInfo()).targetSdkVersion < 19)
      bool = true; 
    this.mIgnoreWindowContentOverlay = bool;
    this.mFlingEstimator = new OverScroller(paramContext);
  }
  
  private void postAddActionBarHideOffset() {
    haltActionBarHideOffsetAnimations();
    postDelayed(this.mAddActionBarHideOffset, 600L);
  }
  
  private void postRemoveActionBarHideOffset() {
    haltActionBarHideOffsetAnimations();
    postDelayed(this.mRemoveActionBarHideOffset, 600L);
  }
  
  private void removeActionBarHideOffset() {
    haltActionBarHideOffsetAnimations();
    this.mRemoveActionBarHideOffset.run();
  }
  
  private boolean shouldHideActionBarOnFling(float paramFloat1, float paramFloat2) {
    this.mFlingEstimator.fling(0, 0, 0, (int)paramFloat2, 0, 0, -2147483648, 2147483647);
    return (this.mFlingEstimator.getFinalY() > this.mActionBarTop.getHeight());
  }
  
  public boolean canShowOverflowMenu() {
    pullChildren();
    return this.mDecorToolbar.canShowOverflowMenu();
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return paramLayoutParams instanceof LayoutParams; }
  
  public void dismissPopups() {
    pullChildren();
    this.mDecorToolbar.dismissPopupMenus();
  }
  
  public void draw(Canvas paramCanvas) {
    super.draw(paramCanvas);
    if (this.mWindowContentOverlay != null && !this.mIgnoreWindowContentOverlay) {
      int i;
      if (this.mActionBarTop.getVisibility() == 0) {
        i = (int)(this.mActionBarTop.getBottom() + this.mActionBarTop.getTranslationY() + 0.5F);
      } else {
        i = 0;
      } 
      this.mWindowContentOverlay.setBounds(0, i, getWidth(), this.mWindowContentOverlay.getIntrinsicHeight() + i);
      this.mWindowContentOverlay.draw(paramCanvas);
    } 
  }
  
  protected boolean fitSystemWindows(Rect paramRect) {
    pullChildren();
    ViewCompat.getWindowSystemUiVisibility(this);
    boolean bool = applyInsets(this.mActionBarTop, paramRect, true, true, false, true);
    this.mBaseInnerInsets.set(paramRect);
    ViewUtils.computeFitSystemWindows(this, this.mBaseInnerInsets, this.mBaseContentInsets);
    if (!this.mLastBaseInnerInsets.equals(this.mBaseInnerInsets)) {
      this.mLastBaseInnerInsets.set(this.mBaseInnerInsets);
      bool = true;
    } 
    if (!this.mLastBaseContentInsets.equals(this.mBaseContentInsets)) {
      this.mLastBaseContentInsets.set(this.mBaseContentInsets);
      bool = true;
    } 
    if (bool)
      requestLayout(); 
    return true;
  }
  
  protected LayoutParams generateDefaultLayoutParams() { return new LayoutParams(-1, -1); }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return new LayoutParams(getContext(), paramAttributeSet); }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return new LayoutParams(paramLayoutParams); }
  
  public int getActionBarHideOffset() { return (this.mActionBarTop != null) ? -((int)this.mActionBarTop.getTranslationY()) : 0; }
  
  public int getNestedScrollAxes() { return this.mParentHelper.getNestedScrollAxes(); }
  
  public CharSequence getTitle() {
    pullChildren();
    return this.mDecorToolbar.getTitle();
  }
  
  void haltActionBarHideOffsetAnimations() {
    removeCallbacks(this.mRemoveActionBarHideOffset);
    removeCallbacks(this.mAddActionBarHideOffset);
    if (this.mCurrentActionBarTopAnimator != null)
      this.mCurrentActionBarTopAnimator.cancel(); 
  }
  
  public boolean hasIcon() {
    pullChildren();
    return this.mDecorToolbar.hasIcon();
  }
  
  public boolean hasLogo() {
    pullChildren();
    return this.mDecorToolbar.hasLogo();
  }
  
  public boolean hideOverflowMenu() {
    pullChildren();
    return this.mDecorToolbar.hideOverflowMenu();
  }
  
  public void initFeature(int paramInt) {
    pullChildren();
    if (paramInt != 2) {
      if (paramInt != 5) {
        if (paramInt != 109)
          return; 
        setOverlayMode(true);
        return;
      } 
      this.mDecorToolbar.initIndeterminateProgress();
      return;
    } 
    this.mDecorToolbar.initProgress();
  }
  
  public boolean isHideOnContentScrollEnabled() { return this.mHideOnContentScroll; }
  
  public boolean isInOverlayMode() { return this.mOverlayMode; }
  
  public boolean isOverflowMenuShowPending() {
    pullChildren();
    return this.mDecorToolbar.isOverflowMenuShowPending();
  }
  
  public boolean isOverflowMenuShowing() {
    pullChildren();
    return this.mDecorToolbar.isOverflowMenuShowing();
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration) {
    super.onConfigurationChanged(paramConfiguration);
    init(getContext());
    ViewCompat.requestApplyInsets(this);
  }
  
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    haltActionBarHideOffsetAnimations();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramInt2 = getChildCount();
    paramInt3 = getPaddingLeft();
    getPaddingRight();
    paramInt4 = getPaddingTop();
    getPaddingBottom();
    for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++) {
      View view = getChildAt(paramInt1);
      if (view.getVisibility() != 8) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        int i = view.getMeasuredWidth();
        int j = view.getMeasuredHeight();
        int k = layoutParams.leftMargin + paramInt3;
        int m = layoutParams.topMargin + paramInt4;
        view.layout(k, m, i + k, j + m);
      } 
    } 
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    pullChildren();
    measureChildWithMargins(this.mActionBarTop, paramInt1, 0, paramInt2, 0);
    LayoutParams layoutParams = (LayoutParams)this.mActionBarTop.getLayoutParams();
    int i1 = Math.max(0, this.mActionBarTop.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
    int n = Math.max(0, this.mActionBarTop.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
    int m = View.combineMeasuredStates(0, this.mActionBarTop.getMeasuredState());
    if ((ViewCompat.getWindowSystemUiVisibility(this) & 0x100) != 0) {
      j = 1;
    } else {
      j = 0;
    } 
    if (j) {
      int i2 = this.mActionBarHeight;
      i = i2;
      if (this.mHasNonEmbeddedTabs) {
        i = i2;
        if (this.mActionBarTop.getTabContainer() != null)
          i = i2 + this.mActionBarHeight; 
      } 
    } else if (this.mActionBarTop.getVisibility() != 8) {
      i = this.mActionBarTop.getMeasuredHeight();
    } else {
      i = 0;
    } 
    this.mContentInsets.set(this.mBaseContentInsets);
    this.mInnerInsets.set(this.mBaseInnerInsets);
    if (!this.mOverlayMode && !j) {
      Rect rect = this.mContentInsets;
      rect.top += i;
      rect = this.mContentInsets;
      rect.bottom += 0;
    } else {
      Rect rect = this.mInnerInsets;
      rect.top += i;
      rect = this.mInnerInsets;
      rect.bottom += 0;
    } 
    applyInsets(this.mContent, this.mContentInsets, true, true, true, true);
    if (!this.mLastInnerInsets.equals(this.mInnerInsets)) {
      this.mLastInnerInsets.set(this.mInnerInsets);
      this.mContent.dispatchFitSystemWindows(this.mInnerInsets);
    } 
    measureChildWithMargins(this.mContent, paramInt1, 0, paramInt2, 0);
    layoutParams = (LayoutParams)this.mContent.getLayoutParams();
    int i = Math.max(i1, this.mContent.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
    int j = Math.max(n, this.mContent.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
    int k = View.combineMeasuredStates(m, this.mContent.getMeasuredState());
    m = getPaddingLeft();
    n = getPaddingRight();
    j = Math.max(j + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight());
    setMeasuredDimension(View.resolveSizeAndState(Math.max(i + m + n, getSuggestedMinimumWidth()), paramInt1, k), View.resolveSizeAndState(j, paramInt2, k << 16));
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean) {
    if (!this.mHideOnContentScroll || !paramBoolean)
      return false; 
    if (shouldHideActionBarOnFling(paramFloat1, paramFloat2)) {
      addActionBarHideOffset();
    } else {
      removeActionBarHideOffset();
    } 
    this.mAnimatingForFling = true;
    return true;
  }
  
  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2) { return false; }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt) {}
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.mHideOnContentScrollReference += paramInt2;
    setActionBarHideOffset(this.mHideOnContentScrollReference);
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt) {
    this.mParentHelper.onNestedScrollAccepted(paramView1, paramView2, paramInt);
    this.mHideOnContentScrollReference = getActionBarHideOffset();
    haltActionBarHideOffsetAnimations();
    if (this.mActionBarVisibilityCallback != null)
      this.mActionBarVisibilityCallback.onContentScrollStarted(); 
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt) { return ((paramInt & 0x2) == 0 || this.mActionBarTop.getVisibility() != 0) ? false : this.mHideOnContentScroll; }
  
  public void onStopNestedScroll(View paramView) {
    if (this.mHideOnContentScroll && !this.mAnimatingForFling)
      if (this.mHideOnContentScrollReference <= this.mActionBarTop.getHeight()) {
        postRemoveActionBarHideOffset();
      } else {
        postAddActionBarHideOffset();
      }  
    if (this.mActionBarVisibilityCallback != null)
      this.mActionBarVisibilityCallback.onContentScrollStopped(); 
  }
  
  public void onWindowSystemUiVisibilityChanged(int paramInt) {
    boolean bool1;
    if (Build.VERSION.SDK_INT >= 16)
      super.onWindowSystemUiVisibilityChanged(paramInt); 
    pullChildren();
    int i = this.mLastSystemUiVisibility;
    this.mLastSystemUiVisibility = paramInt;
    boolean bool2 = false;
    if ((paramInt & 0x4) == 0) {
      bool1 = true;
    } else {
      bool1 = false;
    } 
    if ((paramInt & 0x100) != 0)
      bool2 = true; 
    if (this.mActionBarVisibilityCallback != null) {
      this.mActionBarVisibilityCallback.enableContentAnimations(bool2 ^ true);
      if (bool1 || !bool2) {
        this.mActionBarVisibilityCallback.showForSystem();
      } else {
        this.mActionBarVisibilityCallback.hideForSystem();
      } 
    } 
    if (((i ^ paramInt) & 0x100) != 0 && this.mActionBarVisibilityCallback != null)
      ViewCompat.requestApplyInsets(this); 
  }
  
  protected void onWindowVisibilityChanged(int paramInt) {
    super.onWindowVisibilityChanged(paramInt);
    this.mWindowVisibility = paramInt;
    if (this.mActionBarVisibilityCallback != null)
      this.mActionBarVisibilityCallback.onWindowVisibilityChanged(paramInt); 
  }
  
  void pullChildren() {
    if (this.mContent == null) {
      this.mContent = (ContentFrameLayout)findViewById(R.id.action_bar_activity_content);
      this.mActionBarTop = (ActionBarContainer)findViewById(R.id.action_bar_container);
      this.mDecorToolbar = getDecorToolbar(findViewById(R.id.action_bar));
    } 
  }
  
  public void restoreToolbarHierarchyState(SparseArray<Parcelable> paramSparseArray) {
    pullChildren();
    this.mDecorToolbar.restoreHierarchyState(paramSparseArray);
  }
  
  public void saveToolbarHierarchyState(SparseArray<Parcelable> paramSparseArray) {
    pullChildren();
    this.mDecorToolbar.saveHierarchyState(paramSparseArray);
  }
  
  public void setActionBarHideOffset(int paramInt) {
    haltActionBarHideOffsetAnimations();
    paramInt = Math.max(0, Math.min(paramInt, this.mActionBarTop.getHeight()));
    this.mActionBarTop.setTranslationY(-paramInt);
  }
  
  public void setActionBarVisibilityCallback(ActionBarVisibilityCallback paramActionBarVisibilityCallback) {
    this.mActionBarVisibilityCallback = paramActionBarVisibilityCallback;
    if (getWindowToken() != null) {
      this.mActionBarVisibilityCallback.onWindowVisibilityChanged(this.mWindowVisibility);
      if (this.mLastSystemUiVisibility != 0) {
        onWindowSystemUiVisibilityChanged(this.mLastSystemUiVisibility);
        ViewCompat.requestApplyInsets(this);
      } 
    } 
  }
  
  public void setHasNonEmbeddedTabs(boolean paramBoolean) { this.mHasNonEmbeddedTabs = paramBoolean; }
  
  public void setHideOnContentScrollEnabled(boolean paramBoolean) {
    if (paramBoolean != this.mHideOnContentScroll) {
      this.mHideOnContentScroll = paramBoolean;
      if (!paramBoolean) {
        haltActionBarHideOffsetAnimations();
        setActionBarHideOffset(0);
      } 
    } 
  }
  
  public void setIcon(int paramInt) {
    pullChildren();
    this.mDecorToolbar.setIcon(paramInt);
  }
  
  public void setIcon(Drawable paramDrawable) {
    pullChildren();
    this.mDecorToolbar.setIcon(paramDrawable);
  }
  
  public void setLogo(int paramInt) {
    pullChildren();
    this.mDecorToolbar.setLogo(paramInt);
  }
  
  public void setMenu(Menu paramMenu, MenuPresenter.Callback paramCallback) {
    pullChildren();
    this.mDecorToolbar.setMenu(paramMenu, paramCallback);
  }
  
  public void setMenuPrepared() {
    pullChildren();
    this.mDecorToolbar.setMenuPrepared();
  }
  
  public void setOverlayMode(boolean paramBoolean) {
    this.mOverlayMode = paramBoolean;
    if (paramBoolean && (getContext().getApplicationInfo()).targetSdkVersion < 19) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    } 
    this.mIgnoreWindowContentOverlay = paramBoolean;
  }
  
  public void setShowingForActionMode(boolean paramBoolean) {}
  
  public void setUiOptions(int paramInt) {}
  
  public void setWindowCallback(Window.Callback paramCallback) {
    pullChildren();
    this.mDecorToolbar.setWindowCallback(paramCallback);
  }
  
  public void setWindowTitle(CharSequence paramCharSequence) {
    pullChildren();
    this.mDecorToolbar.setWindowTitle(paramCharSequence);
  }
  
  public boolean shouldDelayChildPressedState() { return false; }
  
  public boolean showOverflowMenu() {
    pullChildren();
    return this.mDecorToolbar.showOverflowMenu();
  }
  
  public static interface ActionBarVisibilityCallback {
    void enableContentAnimations(boolean param1Boolean);
    
    void hideForSystem();
    
    void onContentScrollStarted();
    
    void onContentScrollStopped();
    
    void onWindowVisibilityChanged(int param1Int);
    
    void showForSystem();
  }
  
  public static class LayoutParams extends ViewGroup.MarginLayoutParams {
    public LayoutParams(int param1Int1, int param1Int2) { super(param1Int1, param1Int2); }
    
    public LayoutParams(Context param1Context, AttributeSet param1AttributeSet) { super(param1Context, param1AttributeSet); }
    
    public LayoutParams(ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.MarginLayoutParams param1MarginLayoutParams) { super(param1MarginLayoutParams); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/ActionBarOverlayLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */