package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.CollapsibleActionView;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class Toolbar extends ViewGroup {
  private static final String TAG = "Toolbar";
  
  private MenuPresenter.Callback mActionMenuPresenterCallback;
  
  int mButtonGravity;
  
  ImageButton mCollapseButtonView;
  
  private CharSequence mCollapseDescription;
  
  private Drawable mCollapseIcon;
  
  private boolean mCollapsible;
  
  private int mContentInsetEndWithActions;
  
  private int mContentInsetStartWithNavigation;
  
  private RtlSpacingHelper mContentInsets;
  
  private boolean mEatingHover;
  
  private boolean mEatingTouch;
  
  View mExpandedActionView;
  
  private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
  
  private int mGravity = 8388627;
  
  private final ArrayList<View> mHiddenViews = new ArrayList();
  
  private ImageView mLogoView;
  
  private int mMaxButtonHeight;
  
  private MenuBuilder.Callback mMenuBuilderCallback;
  
  private ActionMenuView mMenuView;
  
  private final ActionMenuView.OnMenuItemClickListener mMenuViewItemClickListener = new ActionMenuView.OnMenuItemClickListener() {
      public boolean onMenuItemClick(MenuItem param1MenuItem) { return (Toolbar.this.mOnMenuItemClickListener != null) ? Toolbar.this.mOnMenuItemClickListener.onMenuItemClick(param1MenuItem) : 0; }
    };
  
  private ImageButton mNavButtonView;
  
  OnMenuItemClickListener mOnMenuItemClickListener;
  
  private ActionMenuPresenter mOuterActionMenuPresenter;
  
  private Context mPopupContext;
  
  private int mPopupTheme;
  
  private final Runnable mShowOverflowMenuRunnable = new Runnable() {
      public void run() { Toolbar.this.showOverflowMenu(); }
    };
  
  private CharSequence mSubtitleText;
  
  private int mSubtitleTextAppearance;
  
  private int mSubtitleTextColor;
  
  private TextView mSubtitleTextView;
  
  private final int[] mTempMargins = new int[2];
  
  private final ArrayList<View> mTempViews = new ArrayList();
  
  private int mTitleMarginBottom;
  
  private int mTitleMarginEnd;
  
  private int mTitleMarginStart;
  
  private int mTitleMarginTop;
  
  private CharSequence mTitleText;
  
  private int mTitleTextAppearance;
  
  private int mTitleTextColor;
  
  private TextView mTitleTextView;
  
  private ToolbarWidgetWrapper mWrapper;
  
  public Toolbar(Context paramContext) { this(paramContext, null); }
  
  public Toolbar(Context paramContext, @Nullable AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, R.attr.toolbarStyle); }
  
  public Toolbar(Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(getContext(), paramAttributeSet, R.styleable.Toolbar, paramInt, 0);
    this.mTitleTextAppearance = tintTypedArray.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
    this.mSubtitleTextAppearance = tintTypedArray.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, 0);
    this.mGravity = tintTypedArray.getInteger(R.styleable.Toolbar_android_gravity, this.mGravity);
    this.mButtonGravity = tintTypedArray.getInteger(R.styleable.Toolbar_buttonGravity, 48);
    int i = tintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMargin, 0);
    paramInt = i;
    if (tintTypedArray.hasValue(R.styleable.Toolbar_titleMargins))
      paramInt = tintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMargins, i); 
    this.mTitleMarginBottom = paramInt;
    this.mTitleMarginTop = paramInt;
    this.mTitleMarginEnd = paramInt;
    this.mTitleMarginStart = paramInt;
    paramInt = tintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginStart, -1);
    if (paramInt >= 0)
      this.mTitleMarginStart = paramInt; 
    paramInt = tintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginEnd, -1);
    if (paramInt >= 0)
      this.mTitleMarginEnd = paramInt; 
    paramInt = tintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginTop, -1);
    if (paramInt >= 0)
      this.mTitleMarginTop = paramInt; 
    paramInt = tintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginBottom, -1);
    if (paramInt >= 0)
      this.mTitleMarginBottom = paramInt; 
    this.mMaxButtonHeight = tintTypedArray.getDimensionPixelSize(R.styleable.Toolbar_maxButtonHeight, -1);
    paramInt = tintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStart, -2147483648);
    i = tintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEnd, -2147483648);
    int j = tintTypedArray.getDimensionPixelSize(R.styleable.Toolbar_contentInsetLeft, 0);
    int k = tintTypedArray.getDimensionPixelSize(R.styleable.Toolbar_contentInsetRight, 0);
    ensureContentInsets();
    this.mContentInsets.setAbsolute(j, k);
    if (paramInt != Integer.MIN_VALUE || i != Integer.MIN_VALUE)
      this.mContentInsets.setRelative(paramInt, i); 
    this.mContentInsetStartWithNavigation = tintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStartWithNavigation, -2147483648);
    this.mContentInsetEndWithActions = tintTypedArray.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEndWithActions, -2147483648);
    this.mCollapseIcon = tintTypedArray.getDrawable(R.styleable.Toolbar_collapseIcon);
    this.mCollapseDescription = tintTypedArray.getText(R.styleable.Toolbar_collapseContentDescription);
    CharSequence charSequence3 = tintTypedArray.getText(R.styleable.Toolbar_title);
    if (!TextUtils.isEmpty(charSequence3))
      setTitle(charSequence3); 
    charSequence3 = tintTypedArray.getText(R.styleable.Toolbar_subtitle);
    if (!TextUtils.isEmpty(charSequence3))
      setSubtitle(charSequence3); 
    this.mPopupContext = getContext();
    setPopupTheme(tintTypedArray.getResourceId(R.styleable.Toolbar_popupTheme, 0));
    Drawable drawable2 = tintTypedArray.getDrawable(R.styleable.Toolbar_navigationIcon);
    if (drawable2 != null)
      setNavigationIcon(drawable2); 
    CharSequence charSequence2 = tintTypedArray.getText(R.styleable.Toolbar_navigationContentDescription);
    if (!TextUtils.isEmpty(charSequence2))
      setNavigationContentDescription(charSequence2); 
    Drawable drawable1 = tintTypedArray.getDrawable(R.styleable.Toolbar_logo);
    if (drawable1 != null)
      setLogo(drawable1); 
    CharSequence charSequence1 = tintTypedArray.getText(R.styleable.Toolbar_logoDescription);
    if (!TextUtils.isEmpty(charSequence1))
      setLogoDescription(charSequence1); 
    if (tintTypedArray.hasValue(R.styleable.Toolbar_titleTextColor))
      setTitleTextColor(tintTypedArray.getColor(R.styleable.Toolbar_titleTextColor, -1)); 
    if (tintTypedArray.hasValue(R.styleable.Toolbar_subtitleTextColor))
      setSubtitleTextColor(tintTypedArray.getColor(R.styleable.Toolbar_subtitleTextColor, -1)); 
    tintTypedArray.recycle();
  }
  
  private void addCustomViewsWithGravity(List<View> paramList, int paramInt) {
    int i = ViewCompat.getLayoutDirection(this);
    int j = 0;
    if (i == 1) {
      i = 1;
    } else {
      i = 0;
    } 
    int m = getChildCount();
    int k = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    paramList.clear();
    paramInt = j;
    if (i != 0) {
      for (paramInt = m - 1; paramInt >= 0; paramInt--) {
        View view = getChildAt(paramInt);
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.mViewType == 0 && shouldLayout(view) && getChildHorizontalGravity(layoutParams.gravity) == k)
          paramList.add(view); 
      } 
    } else {
      while (paramInt < m) {
        View view = getChildAt(paramInt);
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.mViewType == 0 && shouldLayout(view) && getChildHorizontalGravity(layoutParams.gravity) == k)
          paramList.add(view); 
        paramInt++;
      } 
    } 
  }
  
  private void addSystemView(View paramView, boolean paramBoolean) {
    LayoutParams layoutParams = paramView.getLayoutParams();
    if (layoutParams == null) {
      layoutParams = generateDefaultLayoutParams();
    } else if (!checkLayoutParams(layoutParams)) {
      layoutParams = generateLayoutParams(layoutParams);
    } else {
      layoutParams = (LayoutParams)layoutParams;
    } 
    layoutParams.mViewType = 1;
    if (paramBoolean && this.mExpandedActionView != null) {
      paramView.setLayoutParams(layoutParams);
      this.mHiddenViews.add(paramView);
      return;
    } 
    addView(paramView, layoutParams);
  }
  
  private void ensureContentInsets() {
    if (this.mContentInsets == null)
      this.mContentInsets = new RtlSpacingHelper(); 
  }
  
  private void ensureLogoView() {
    if (this.mLogoView == null)
      this.mLogoView = new AppCompatImageView(getContext()); 
  }
  
  private void ensureMenu() {
    ensureMenuView();
    if (this.mMenuView.peekMenu() == null) {
      MenuBuilder menuBuilder = (MenuBuilder)this.mMenuView.getMenu();
      if (this.mExpandedMenuPresenter == null)
        this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter(); 
      this.mMenuView.setExpandedActionViewsExclusive(true);
      menuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
    } 
  }
  
  private void ensureMenuView() {
    if (this.mMenuView == null) {
      this.mMenuView = new ActionMenuView(getContext());
      this.mMenuView.setPopupTheme(this.mPopupTheme);
      this.mMenuView.setOnMenuItemClickListener(this.mMenuViewItemClickListener);
      this.mMenuView.setMenuCallbacks(this.mActionMenuPresenterCallback, this.mMenuBuilderCallback);
      LayoutParams layoutParams = generateDefaultLayoutParams();
      layoutParams.gravity = 0x800005 | this.mButtonGravity & 0x70;
      this.mMenuView.setLayoutParams(layoutParams);
      addSystemView(this.mMenuView, false);
    } 
  }
  
  private void ensureNavButtonView() {
    if (this.mNavButtonView == null) {
      this.mNavButtonView = new AppCompatImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
      LayoutParams layoutParams = generateDefaultLayoutParams();
      layoutParams.gravity = 0x800003 | this.mButtonGravity & 0x70;
      this.mNavButtonView.setLayoutParams(layoutParams);
    } 
  }
  
  private int getChildHorizontalGravity(int paramInt) {
    int i = ViewCompat.getLayoutDirection(this);
    int j = GravityCompat.getAbsoluteGravity(paramInt, i) & 0x7;
    if (j != 1) {
      paramInt = 3;
      if (j != 3 && j != 5) {
        if (i == 1)
          paramInt = 5; 
        return paramInt;
      } 
    } 
    return j;
  }
  
  private int getChildTop(View paramView, int paramInt) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    int j = paramView.getMeasuredHeight();
    if (paramInt > 0) {
      paramInt = (j - paramInt) / 2;
    } else {
      paramInt = 0;
    } 
    int i = getChildVerticalGravity(layoutParams.gravity);
    if (i != 48) {
      if (i != 80) {
        int k = getPaddingTop();
        paramInt = getPaddingBottom();
        int m = getHeight();
        i = (m - k - paramInt - j) / 2;
        if (i < layoutParams.topMargin) {
          paramInt = layoutParams.topMargin;
        } else {
          j = m - paramInt - j - i - k;
          paramInt = i;
          if (j < layoutParams.bottomMargin)
            paramInt = Math.max(0, i - layoutParams.bottomMargin - j); 
        } 
        return k + paramInt;
      } 
      return getHeight() - getPaddingBottom() - j - layoutParams.bottomMargin - paramInt;
    } 
    return getPaddingTop() - paramInt;
  }
  
  private int getChildVerticalGravity(int paramInt) {
    paramInt &= 0x70;
    return (paramInt != 16 && paramInt != 48 && paramInt != 80) ? (this.mGravity & 0x70) : paramInt;
  }
  
  private int getHorizontalMargins(View paramView) {
    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    return MarginLayoutParamsCompat.getMarginStart(marginLayoutParams) + MarginLayoutParamsCompat.getMarginEnd(marginLayoutParams);
  }
  
  private MenuInflater getMenuInflater() { return new SupportMenuInflater(getContext()); }
  
  private int getVerticalMargins(View paramView) {
    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    return marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
  }
  
  private int getViewListMeasuredWidth(List<View> paramList, int[] paramArrayOfInt) {
    int k = paramArrayOfInt[0];
    int j = paramArrayOfInt[1];
    int m = paramList.size();
    byte b = 0;
    int i = 0;
    while (b < m) {
      View view = (View)paramList.get(b);
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      k = layoutParams.leftMargin - k;
      j = layoutParams.rightMargin - j;
      int n = Math.max(0, k);
      int i1 = Math.max(0, j);
      k = Math.max(0, -k);
      j = Math.max(0, -j);
      i += n + view.getMeasuredWidth() + i1;
      b++;
    } 
    return i;
  }
  
  private boolean isChildOrHidden(View paramView) { return (paramView.getParent() == this || this.mHiddenViews.contains(paramView)); }
  
  private static boolean isCustomView(View paramView) { return (((LayoutParams)paramView.getLayoutParams()).mViewType == 0); }
  
  private int layoutChildLeft(View paramView, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = layoutParams.leftMargin - paramArrayOfInt[0];
    paramInt1 += Math.max(0, i);
    paramArrayOfInt[0] = Math.max(0, -i);
    paramInt2 = getChildTop(paramView, paramInt2);
    i = paramView.getMeasuredWidth();
    paramView.layout(paramInt1, paramInt2, paramInt1 + i, paramView.getMeasuredHeight() + paramInt2);
    return paramInt1 + i + layoutParams.rightMargin;
  }
  
  private int layoutChildRight(View paramView, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = layoutParams.rightMargin - paramArrayOfInt[1];
    paramInt1 -= Math.max(0, i);
    paramArrayOfInt[1] = Math.max(0, -i);
    paramInt2 = getChildTop(paramView, paramInt2);
    i = paramView.getMeasuredWidth();
    paramView.layout(paramInt1 - i, paramInt2, paramInt1, paramView.getMeasuredHeight() + paramInt2);
    return paramInt1 - i + layoutParams.leftMargin;
  }
  
  private int measureChildCollapseMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) {
    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = marginLayoutParams.leftMargin - paramArrayOfInt[0];
    int j = marginLayoutParams.rightMargin - paramArrayOfInt[1];
    int k = Math.max(0, i) + Math.max(0, j);
    paramArrayOfInt[0] = Math.max(0, -i);
    paramArrayOfInt[1] = Math.max(0, -j);
    paramView.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight() + k + paramInt2, marginLayoutParams.width), getChildMeasureSpec(paramInt3, getPaddingTop() + getPaddingBottom() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin + paramInt4, marginLayoutParams.height));
    return paramView.getMeasuredWidth() + k;
  }
  
  private void measureChildConstrained(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    int i = getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + paramInt2, marginLayoutParams.width);
    paramInt2 = getChildMeasureSpec(paramInt3, getPaddingTop() + getPaddingBottom() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin + paramInt4, marginLayoutParams.height);
    paramInt3 = View.MeasureSpec.getMode(paramInt2);
    paramInt1 = paramInt2;
    if (paramInt3 != 1073741824) {
      paramInt1 = paramInt2;
      if (paramInt5 >= 0) {
        paramInt1 = paramInt5;
        if (paramInt3 != 0)
          paramInt1 = Math.min(View.MeasureSpec.getSize(paramInt2), paramInt5); 
        paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824);
      } 
    } 
    paramView.measure(i, paramInt1);
  }
  
  private void postShowOverflowMenu() {
    removeCallbacks(this.mShowOverflowMenuRunnable);
    post(this.mShowOverflowMenuRunnable);
  }
  
  private boolean shouldCollapse() {
    if (!this.mCollapsible)
      return false; 
    int i = getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = getChildAt(b);
      if (shouldLayout(view) && view.getMeasuredWidth() > 0 && view.getMeasuredHeight() > 0)
        return false; 
    } 
    return true;
  }
  
  private boolean shouldLayout(View paramView) { return (paramView != null && paramView.getParent() == this && paramView.getVisibility() != 8); }
  
  void addChildrenForExpandedActionView() {
    for (int i = this.mHiddenViews.size() - 1; i >= 0; i--)
      addView((View)this.mHiddenViews.get(i)); 
    this.mHiddenViews.clear();
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public boolean canShowOverflowMenu() { return (getVisibility() == 0 && this.mMenuView != null && this.mMenuView.isOverflowReserved()); }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (super.checkLayoutParams(paramLayoutParams) && paramLayoutParams instanceof LayoutParams); }
  
  public void collapseActionView() {
    MenuItemImpl menuItemImpl;
    if (this.mExpandedMenuPresenter == null) {
      menuItemImpl = null;
    } else {
      menuItemImpl = this.mExpandedMenuPresenter.mCurrentExpandedItem;
    } 
    if (menuItemImpl != null)
      menuItemImpl.collapseActionView(); 
  }
  
  public void dismissPopupMenus() {
    if (this.mMenuView != null)
      this.mMenuView.dismissPopupMenus(); 
  }
  
  void ensureCollapseButtonView() {
    if (this.mCollapseButtonView == null) {
      this.mCollapseButtonView = new AppCompatImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
      this.mCollapseButtonView.setImageDrawable(this.mCollapseIcon);
      this.mCollapseButtonView.setContentDescription(this.mCollapseDescription);
      LayoutParams layoutParams = generateDefaultLayoutParams();
      layoutParams.gravity = 0x800003 | this.mButtonGravity & 0x70;
      layoutParams.mViewType = 2;
      this.mCollapseButtonView.setLayoutParams(layoutParams);
      this.mCollapseButtonView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) { Toolbar.this.collapseActionView(); }
          });
    } 
  }
  
  protected LayoutParams generateDefaultLayoutParams() { return new LayoutParams(-2, -2); }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return new LayoutParams(getContext(), paramAttributeSet); }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams instanceof LayoutParams) ? new LayoutParams((LayoutParams)paramLayoutParams) : ((paramLayoutParams instanceof ActionBar.LayoutParams) ? new LayoutParams((ActionBar.LayoutParams)paramLayoutParams) : ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams) ? new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams) : new LayoutParams(paramLayoutParams))); }
  
  public int getContentInsetEnd() { return (this.mContentInsets != null) ? this.mContentInsets.getEnd() : 0; }
  
  public int getContentInsetEndWithActions() { return (this.mContentInsetEndWithActions != Integer.MIN_VALUE) ? this.mContentInsetEndWithActions : getContentInsetEnd(); }
  
  public int getContentInsetLeft() { return (this.mContentInsets != null) ? this.mContentInsets.getLeft() : 0; }
  
  public int getContentInsetRight() { return (this.mContentInsets != null) ? this.mContentInsets.getRight() : 0; }
  
  public int getContentInsetStart() { return (this.mContentInsets != null) ? this.mContentInsets.getStart() : 0; }
  
  public int getContentInsetStartWithNavigation() { return (this.mContentInsetStartWithNavigation != Integer.MIN_VALUE) ? this.mContentInsetStartWithNavigation : getContentInsetStart(); }
  
  public int getCurrentContentInsetEnd() { // Byte code:
    //   0: aload_0
    //   1: getfield mMenuView : Landroid/support/v7/widget/ActionMenuView;
    //   4: ifnull -> 31
    //   7: aload_0
    //   8: getfield mMenuView : Landroid/support/v7/widget/ActionMenuView;
    //   11: invokevirtual peekMenu : ()Landroid/support/v7/view/menu/MenuBuilder;
    //   14: astore_2
    //   15: aload_2
    //   16: ifnull -> 31
    //   19: aload_2
    //   20: invokevirtual hasVisibleItems : ()Z
    //   23: ifeq -> 31
    //   26: iconst_1
    //   27: istore_1
    //   28: goto -> 33
    //   31: iconst_0
    //   32: istore_1
    //   33: iload_1
    //   34: ifeq -> 53
    //   37: aload_0
    //   38: invokevirtual getContentInsetEnd : ()I
    //   41: aload_0
    //   42: getfield mContentInsetEndWithActions : I
    //   45: iconst_0
    //   46: invokestatic max : (II)I
    //   49: invokestatic max : (II)I
    //   52: ireturn
    //   53: aload_0
    //   54: invokevirtual getContentInsetEnd : ()I
    //   57: ireturn }
  
  public int getCurrentContentInsetLeft() { return (ViewCompat.getLayoutDirection(this) == 1) ? getCurrentContentInsetEnd() : getCurrentContentInsetStart(); }
  
  public int getCurrentContentInsetRight() { return (ViewCompat.getLayoutDirection(this) == 1) ? getCurrentContentInsetStart() : getCurrentContentInsetEnd(); }
  
  public int getCurrentContentInsetStart() { return (getNavigationIcon() != null) ? Math.max(getContentInsetStart(), Math.max(this.mContentInsetStartWithNavigation, 0)) : getContentInsetStart(); }
  
  public Drawable getLogo() { return (this.mLogoView != null) ? this.mLogoView.getDrawable() : null; }
  
  public CharSequence getLogoDescription() { return (this.mLogoView != null) ? this.mLogoView.getContentDescription() : null; }
  
  public Menu getMenu() {
    ensureMenu();
    return this.mMenuView.getMenu();
  }
  
  @Nullable
  public CharSequence getNavigationContentDescription() { return (this.mNavButtonView != null) ? this.mNavButtonView.getContentDescription() : null; }
  
  @Nullable
  public Drawable getNavigationIcon() { return (this.mNavButtonView != null) ? this.mNavButtonView.getDrawable() : null; }
  
  ActionMenuPresenter getOuterActionMenuPresenter() { return this.mOuterActionMenuPresenter; }
  
  @Nullable
  public Drawable getOverflowIcon() {
    ensureMenu();
    return this.mMenuView.getOverflowIcon();
  }
  
  Context getPopupContext() { return this.mPopupContext; }
  
  public int getPopupTheme() { return this.mPopupTheme; }
  
  public CharSequence getSubtitle() { return this.mSubtitleText; }
  
  public CharSequence getTitle() { return this.mTitleText; }
  
  public int getTitleMarginBottom() { return this.mTitleMarginBottom; }
  
  public int getTitleMarginEnd() { return this.mTitleMarginEnd; }
  
  public int getTitleMarginStart() { return this.mTitleMarginStart; }
  
  public int getTitleMarginTop() { return this.mTitleMarginTop; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public DecorToolbar getWrapper() {
    if (this.mWrapper == null)
      this.mWrapper = new ToolbarWidgetWrapper(this, true); 
    return this.mWrapper;
  }
  
  public boolean hasExpandedActionView() { return (this.mExpandedMenuPresenter != null && this.mExpandedMenuPresenter.mCurrentExpandedItem != null); }
  
  public boolean hideOverflowMenu() { return (this.mMenuView != null && this.mMenuView.hideOverflowMenu()); }
  
  public void inflateMenu(@MenuRes int paramInt) { getMenuInflater().inflate(paramInt, getMenu()); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public boolean isOverflowMenuShowPending() { return (this.mMenuView != null && this.mMenuView.isOverflowMenuShowPending()); }
  
  public boolean isOverflowMenuShowing() { return (this.mMenuView != null && this.mMenuView.isOverflowMenuShowing()); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public boolean isTitleTruncated() {
    if (this.mTitleTextView == null)
      return false; 
    Layout layout = this.mTitleTextView.getLayout();
    if (layout == null)
      return false; 
    int i = layout.getLineCount();
    for (byte b = 0; b < i; b++) {
      if (layout.getEllipsisCount(b) > 0)
        return true; 
    } 
    return false;
  }
  
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    removeCallbacks(this.mShowOverflowMenuRunnable);
  }
  
  public boolean onHoverEvent(MotionEvent paramMotionEvent) {
    int i = paramMotionEvent.getActionMasked();
    if (i == 9)
      this.mEatingHover = false; 
    if (!this.mEatingHover) {
      boolean bool = super.onHoverEvent(paramMotionEvent);
      if (i == 9 && !bool)
        this.mEatingHover = true; 
    } 
    if (i == 10 || i == 3)
      this.mEatingHover = false; 
    return true;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (ViewCompat.getLayoutDirection(this) == 1) {
      k = 1;
    } else {
      k = 0;
    } 
    int n = getWidth();
    int i3 = getHeight();
    int i = getPaddingLeft();
    int i1 = getPaddingRight();
    int i2 = getPaddingTop();
    int i4 = getPaddingBottom();
    int m = n - i1;
    int[] arrayOfInt = this.mTempMargins;
    arrayOfInt[1] = 0;
    arrayOfInt[0] = 0;
    paramInt1 = ViewCompat.getMinimumHeight(this);
    if (paramInt1 >= 0) {
      paramInt4 = Math.min(paramInt1, paramInt4 - paramInt2);
    } else {
      paramInt4 = 0;
    } 
    if (shouldLayout(this.mNavButtonView)) {
      if (k) {
        paramInt3 = layoutChildRight(this.mNavButtonView, m, arrayOfInt, paramInt4);
        j = i;
      } else {
        j = layoutChildLeft(this.mNavButtonView, i, arrayOfInt, paramInt4);
        paramInt3 = m;
      } 
    } else {
      j = i;
      paramInt3 = m;
    } 
    paramInt1 = paramInt3;
    paramInt2 = j;
    if (shouldLayout(this.mCollapseButtonView))
      if (k) {
        paramInt1 = layoutChildRight(this.mCollapseButtonView, paramInt3, arrayOfInt, paramInt4);
        paramInt2 = j;
      } else {
        paramInt2 = layoutChildLeft(this.mCollapseButtonView, j, arrayOfInt, paramInt4);
        paramInt1 = paramInt3;
      }  
    paramInt3 = paramInt1;
    int j = paramInt2;
    if (shouldLayout(this.mMenuView))
      if (k) {
        j = layoutChildLeft(this.mMenuView, paramInt2, arrayOfInt, paramInt4);
        paramInt3 = paramInt1;
      } else {
        paramInt3 = layoutChildRight(this.mMenuView, paramInt1, arrayOfInt, paramInt4);
        j = paramInt2;
      }  
    paramInt2 = getCurrentContentInsetLeft();
    paramInt1 = getCurrentContentInsetRight();
    arrayOfInt[0] = Math.max(0, paramInt2 - j);
    arrayOfInt[1] = Math.max(0, paramInt1 - m - paramInt3);
    paramInt2 = Math.max(j, paramInt2);
    paramInt3 = Math.min(paramInt3, m - paramInt1);
    paramInt1 = paramInt2;
    j = paramInt3;
    if (shouldLayout(this.mExpandedActionView))
      if (k) {
        j = layoutChildRight(this.mExpandedActionView, paramInt3, arrayOfInt, paramInt4);
        paramInt1 = paramInt2;
      } else {
        paramInt1 = layoutChildLeft(this.mExpandedActionView, paramInt2, arrayOfInt, paramInt4);
        j = paramInt3;
      }  
    paramInt3 = paramInt1;
    paramInt2 = j;
    if (shouldLayout(this.mLogoView))
      if (k) {
        paramInt2 = layoutChildRight(this.mLogoView, j, arrayOfInt, paramInt4);
        paramInt3 = paramInt1;
      } else {
        paramInt3 = layoutChildLeft(this.mLogoView, paramInt1, arrayOfInt, paramInt4);
        paramInt2 = j;
      }  
    paramBoolean = shouldLayout(this.mTitleTextView);
    boolean bool = shouldLayout(this.mSubtitleTextView);
    if (paramBoolean) {
      LayoutParams layoutParams = (LayoutParams)this.mTitleTextView.getLayoutParams();
      paramInt1 = layoutParams.topMargin + this.mTitleTextView.getMeasuredHeight() + layoutParams.bottomMargin + 0;
    } else {
      paramInt1 = 0;
    } 
    if (bool) {
      LayoutParams layoutParams = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
      paramInt1 += layoutParams.topMargin + this.mSubtitleTextView.getMeasuredHeight() + layoutParams.bottomMargin;
    } 
    if (paramBoolean || bool) {
      TextView textView2;
      TextView textView1;
      if (paramBoolean) {
        textView1 = this.mTitleTextView;
      } else {
        textView1 = this.mSubtitleTextView;
      } 
      if (bool) {
        textView2 = this.mSubtitleTextView;
      } else {
        textView2 = this.mTitleTextView;
      } 
      LayoutParams layoutParams1 = (LayoutParams)textView1.getLayoutParams();
      LayoutParams layoutParams2 = (LayoutParams)textView2.getLayoutParams();
      if ((paramBoolean && this.mTitleTextView.getMeasuredWidth() > 0) || (bool && this.mSubtitleTextView.getMeasuredWidth() > 0)) {
        j = 1;
      } else {
        j = 0;
      } 
      m = this.mGravity & 0x70;
      if (m != 48) {
        if (m != 80) {
          m = (i3 - i2 - i4 - paramInt1) / 2;
          if (m < layoutParams1.topMargin + this.mTitleMarginTop) {
            paramInt1 = layoutParams1.topMargin + this.mTitleMarginTop;
          } else {
            i3 = i3 - i4 - paramInt1 - m - i2;
            paramInt1 = m;
            if (i3 < layoutParams1.bottomMargin + this.mTitleMarginBottom)
              paramInt1 = Math.max(0, m - layoutParams2.bottomMargin + this.mTitleMarginBottom - i3); 
          } 
          paramInt1 = i2 + paramInt1;
        } else {
          paramInt1 = i3 - i4 - layoutParams2.bottomMargin - this.mTitleMarginBottom - paramInt1;
        } 
      } else {
        paramInt1 = getPaddingTop() + layoutParams1.topMargin + this.mTitleMarginTop;
      } 
      if (k) {
        if (j != 0) {
          k = this.mTitleMarginStart;
        } else {
          k = 0;
        } 
        k -= arrayOfInt[1];
        paramInt2 -= Math.max(0, k);
        arrayOfInt[1] = Math.max(0, -k);
        if (paramBoolean) {
          layoutParams1 = (LayoutParams)this.mTitleTextView.getLayoutParams();
          m = paramInt2 - this.mTitleTextView.getMeasuredWidth();
          k = this.mTitleTextView.getMeasuredHeight() + paramInt1;
          this.mTitleTextView.layout(m, paramInt1, paramInt2, k);
          paramInt1 = m - this.mTitleMarginEnd;
          m = k + layoutParams1.bottomMargin;
        } else {
          k = paramInt2;
          m = paramInt1;
          paramInt1 = k;
        } 
        if (bool) {
          layoutParams1 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
          k = m + layoutParams1.topMargin;
          m = this.mSubtitleTextView.getMeasuredWidth();
          i2 = this.mSubtitleTextView.getMeasuredHeight();
          this.mSubtitleTextView.layout(paramInt2 - m, k, paramInt2, i2 + k);
          k = paramInt2 - this.mTitleMarginEnd;
          m = layoutParams1.bottomMargin;
        } else {
          k = paramInt2;
        } 
        if (j != 0)
          paramInt2 = Math.min(paramInt1, k); 
        paramInt1 = paramInt3;
      } else {
        if (j != 0) {
          k = this.mTitleMarginStart;
        } else {
          k = 0;
        } 
        k -= arrayOfInt[0];
        paramInt3 += Math.max(0, k);
        arrayOfInt[0] = Math.max(0, -k);
        if (paramBoolean) {
          layoutParams1 = (LayoutParams)this.mTitleTextView.getLayoutParams();
          k = this.mTitleTextView.getMeasuredWidth() + paramInt3;
          m = this.mTitleTextView.getMeasuredHeight() + paramInt1;
          this.mTitleTextView.layout(paramInt3, paramInt1, k, m);
          k += this.mTitleMarginEnd;
          paramInt1 = m + layoutParams1.bottomMargin;
        } else {
          k = paramInt3;
        } 
        if (bool) {
          layoutParams1 = (LayoutParams)this.mSubtitleTextView.getLayoutParams();
          paramInt1 += layoutParams1.topMargin;
          m = this.mSubtitleTextView.getMeasuredWidth() + paramInt3;
          i2 = this.mSubtitleTextView.getMeasuredHeight();
          this.mSubtitleTextView.layout(paramInt3, paramInt1, m, i2 + paramInt1);
          m += this.mTitleMarginEnd;
          paramInt1 = layoutParams1.bottomMargin;
        } else {
          m = paramInt3;
        } 
        paramInt1 = paramInt3;
        paramInt3 = paramInt2;
        if (j != 0) {
          paramInt1 = Math.max(k, m);
          paramInt3 = paramInt2;
        } 
        j = i;
        i = 0;
        addCustomViewsWithGravity(this.mTempViews, 3);
        k = this.mTempViews.size();
        paramInt2 = 0;
      } 
    } else {
      paramInt1 = paramInt3;
    } 
    paramInt3 = paramInt2;
    j = i;
    i = 0;
    addCustomViewsWithGravity(this.mTempViews, 3);
    int k = this.mTempViews.size();
    paramInt2 = 0;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    int[] arrayOfInt = this.mTempMargins;
    if (ViewUtils.isLayoutRtl(this)) {
      i1 = 1;
      n = 0;
    } else {
      i1 = 0;
      n = 1;
    } 
    if (shouldLayout(this.mNavButtonView)) {
      measureChildConstrained(this.mNavButtonView, paramInt1, 0, paramInt2, 0, this.mMaxButtonHeight);
      i = this.mNavButtonView.getMeasuredWidth() + getHorizontalMargins(this.mNavButtonView);
      m = Math.max(0, this.mNavButtonView.getMeasuredHeight() + getVerticalMargins(this.mNavButtonView));
      k = View.combineMeasuredStates(0, this.mNavButtonView.getMeasuredState());
    } else {
      i = 0;
      m = 0;
      k = 0;
    } 
    int i2 = i;
    int j = m;
    int i = k;
    if (shouldLayout(this.mCollapseButtonView)) {
      measureChildConstrained(this.mCollapseButtonView, paramInt1, 0, paramInt2, 0, this.mMaxButtonHeight);
      i2 = this.mCollapseButtonView.getMeasuredWidth() + getHorizontalMargins(this.mCollapseButtonView);
      j = Math.max(m, this.mCollapseButtonView.getMeasuredHeight() + getVerticalMargins(this.mCollapseButtonView));
      i = View.combineMeasuredStates(k, this.mCollapseButtonView.getMeasuredState());
    } 
    int k = getCurrentContentInsetStart();
    int m = Math.max(k, i2) + 0;
    arrayOfInt[i1] = Math.max(0, k - i2);
    if (shouldLayout(this.mMenuView)) {
      measureChildConstrained(this.mMenuView, paramInt1, m, paramInt2, 0, this.mMaxButtonHeight);
      k = this.mMenuView.getMeasuredWidth() + getHorizontalMargins(this.mMenuView);
      j = Math.max(j, this.mMenuView.getMeasuredHeight() + getVerticalMargins(this.mMenuView));
      i = View.combineMeasuredStates(i, this.mMenuView.getMeasuredState());
    } else {
      k = 0;
    } 
    i2 = getCurrentContentInsetEnd();
    int i1 = m + Math.max(i2, k);
    arrayOfInt[n] = Math.max(0, i2 - k);
    int n = i1;
    m = j;
    k = i;
    if (shouldLayout(this.mExpandedActionView)) {
      n = i1 + measureChildCollapseMargins(this.mExpandedActionView, paramInt1, i1, paramInt2, 0, arrayOfInt);
      m = Math.max(j, this.mExpandedActionView.getMeasuredHeight() + getVerticalMargins(this.mExpandedActionView));
      k = View.combineMeasuredStates(i, this.mExpandedActionView.getMeasuredState());
    } 
    j = n;
    i1 = m;
    i = k;
    if (shouldLayout(this.mLogoView)) {
      j = n + measureChildCollapseMargins(this.mLogoView, paramInt1, n, paramInt2, 0, arrayOfInt);
      i1 = Math.max(m, this.mLogoView.getMeasuredHeight() + getVerticalMargins(this.mLogoView));
      i = View.combineMeasuredStates(k, this.mLogoView.getMeasuredState());
    } 
    int i3 = getChildCount();
    n = i1;
    k = 0;
    m = j;
    j = k;
    while (j < i3) {
      View view = getChildAt(j);
      i2 = m;
      i1 = i;
      k = n;
      if (((LayoutParams)view.getLayoutParams()).mViewType == 0)
        if (!shouldLayout(view)) {
          i2 = m;
          i1 = i;
          k = n;
        } else {
          i2 = m + measureChildCollapseMargins(view, paramInt1, m, paramInt2, 0, arrayOfInt);
          k = Math.max(n, view.getMeasuredHeight() + getVerticalMargins(view));
          i1 = View.combineMeasuredStates(i, view.getMeasuredState());
        }  
      j++;
      m = i2;
      i = i1;
      n = k;
    } 
    i1 = this.mTitleMarginTop + this.mTitleMarginBottom;
    i2 = this.mTitleMarginStart + this.mTitleMarginEnd;
    if (shouldLayout(this.mTitleTextView)) {
      measureChildCollapseMargins(this.mTitleTextView, paramInt1, m + i2, paramInt2, i1, arrayOfInt);
      j = this.mTitleTextView.getMeasuredWidth();
      i3 = getHorizontalMargins(this.mTitleTextView);
      k = this.mTitleTextView.getMeasuredHeight();
      int i4 = getVerticalMargins(this.mTitleTextView);
      i = View.combineMeasuredStates(i, this.mTitleTextView.getMeasuredState());
      k += i4;
      j += i3;
    } else {
      j = 0;
      k = 0;
    } 
    if (shouldLayout(this.mSubtitleTextView)) {
      j = Math.max(j, measureChildCollapseMargins(this.mSubtitleTextView, paramInt1, m + i2, paramInt2, k + i1, arrayOfInt));
      k += this.mSubtitleTextView.getMeasuredHeight() + getVerticalMargins(this.mSubtitleTextView);
      i = View.combineMeasuredStates(i, this.mSubtitleTextView.getMeasuredState());
    } 
    k = Math.max(n, k);
    i2 = getPaddingLeft();
    i3 = getPaddingRight();
    n = getPaddingTop();
    i1 = getPaddingBottom();
    j = View.resolveSizeAndState(Math.max(m + j + i2 + i3, getSuggestedMinimumWidth()), paramInt1, 0xFF000000 & i);
    paramInt1 = View.resolveSizeAndState(Math.max(k + n + i1, getSuggestedMinimumHeight()), paramInt2, i << 16);
    if (shouldCollapse())
      paramInt1 = 0; 
    setMeasuredDimension(j, paramInt1);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable) {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
      return;
    } 
    SavedState savedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(savedState.getSuperState());
    if (this.mMenuView != null) {
      MenuBuilder menuBuilder = this.mMenuView.peekMenu();
    } else {
      paramParcelable = null;
    } 
    if (savedState.expandedMenuItemId != 0 && this.mExpandedMenuPresenter != null && paramParcelable != null) {
      MenuItem menuItem = paramParcelable.findItem(savedState.expandedMenuItemId);
      if (menuItem != null)
        menuItem.expandActionView(); 
    } 
    if (savedState.isOverflowOpen)
      postShowOverflowMenu(); 
  }
  
  public void onRtlPropertiesChanged(int paramInt) {
    if (Build.VERSION.SDK_INT >= 17)
      super.onRtlPropertiesChanged(paramInt); 
    ensureContentInsets();
    RtlSpacingHelper rtlSpacingHelper = this.mContentInsets;
    boolean bool = true;
    if (paramInt != 1)
      bool = false; 
    rtlSpacingHelper.setDirection(bool);
  }
  
  protected Parcelable onSaveInstanceState() {
    SavedState savedState = new SavedState(super.onSaveInstanceState());
    if (this.mExpandedMenuPresenter != null && this.mExpandedMenuPresenter.mCurrentExpandedItem != null)
      savedState.expandedMenuItemId = this.mExpandedMenuPresenter.mCurrentExpandedItem.getItemId(); 
    savedState.isOverflowOpen = isOverflowMenuShowing();
    return savedState;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) {
    int i = paramMotionEvent.getActionMasked();
    if (i == 0)
      this.mEatingTouch = false; 
    if (!this.mEatingTouch) {
      boolean bool = super.onTouchEvent(paramMotionEvent);
      if (i == 0 && !bool)
        this.mEatingTouch = true; 
    } 
    if (i == 1 || i == 3)
      this.mEatingTouch = false; 
    return true;
  }
  
  void removeChildrenForExpandedActionView() {
    for (int i = getChildCount() - 1; i >= 0; i--) {
      View view = getChildAt(i);
      if (((LayoutParams)view.getLayoutParams()).mViewType != 2 && view != this.mMenuView) {
        removeViewAt(i);
        this.mHiddenViews.add(view);
      } 
    } 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setCollapsible(boolean paramBoolean) {
    this.mCollapsible = paramBoolean;
    requestLayout();
  }
  
  public void setContentInsetEndWithActions(int paramInt) {
    int i = paramInt;
    if (paramInt < 0)
      i = Integer.MIN_VALUE; 
    if (i != this.mContentInsetEndWithActions) {
      this.mContentInsetEndWithActions = i;
      if (getNavigationIcon() != null)
        requestLayout(); 
    } 
  }
  
  public void setContentInsetStartWithNavigation(int paramInt) {
    int i = paramInt;
    if (paramInt < 0)
      i = Integer.MIN_VALUE; 
    if (i != this.mContentInsetStartWithNavigation) {
      this.mContentInsetStartWithNavigation = i;
      if (getNavigationIcon() != null)
        requestLayout(); 
    } 
  }
  
  public void setContentInsetsAbsolute(int paramInt1, int paramInt2) {
    ensureContentInsets();
    this.mContentInsets.setAbsolute(paramInt1, paramInt2);
  }
  
  public void setContentInsetsRelative(int paramInt1, int paramInt2) {
    ensureContentInsets();
    this.mContentInsets.setRelative(paramInt1, paramInt2);
  }
  
  public void setLogo(@DrawableRes int paramInt) { setLogo(AppCompatResources.getDrawable(getContext(), paramInt)); }
  
  public void setLogo(Drawable paramDrawable) {
    if (paramDrawable != null) {
      ensureLogoView();
      if (!isChildOrHidden(this.mLogoView))
        addSystemView(this.mLogoView, true); 
    } else if (this.mLogoView != null && isChildOrHidden(this.mLogoView)) {
      removeView(this.mLogoView);
      this.mHiddenViews.remove(this.mLogoView);
    } 
    if (this.mLogoView != null)
      this.mLogoView.setImageDrawable(paramDrawable); 
  }
  
  public void setLogoDescription(@StringRes int paramInt) { setLogoDescription(getContext().getText(paramInt)); }
  
  public void setLogoDescription(CharSequence paramCharSequence) {
    if (!TextUtils.isEmpty(paramCharSequence))
      ensureLogoView(); 
    if (this.mLogoView != null)
      this.mLogoView.setContentDescription(paramCharSequence); 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setMenu(MenuBuilder paramMenuBuilder, ActionMenuPresenter paramActionMenuPresenter) {
    if (paramMenuBuilder == null && this.mMenuView == null)
      return; 
    ensureMenuView();
    MenuBuilder menuBuilder = this.mMenuView.peekMenu();
    if (menuBuilder == paramMenuBuilder)
      return; 
    if (menuBuilder != null) {
      menuBuilder.removeMenuPresenter(this.mOuterActionMenuPresenter);
      menuBuilder.removeMenuPresenter(this.mExpandedMenuPresenter);
    } 
    if (this.mExpandedMenuPresenter == null)
      this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter(); 
    paramActionMenuPresenter.setExpandedActionViewsExclusive(true);
    if (paramMenuBuilder != null) {
      paramMenuBuilder.addMenuPresenter(paramActionMenuPresenter, this.mPopupContext);
      paramMenuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
    } else {
      paramActionMenuPresenter.initForMenu(this.mPopupContext, null);
      this.mExpandedMenuPresenter.initForMenu(this.mPopupContext, null);
      paramActionMenuPresenter.updateMenuView(true);
      this.mExpandedMenuPresenter.updateMenuView(true);
    } 
    this.mMenuView.setPopupTheme(this.mPopupTheme);
    this.mMenuView.setPresenter(paramActionMenuPresenter);
    this.mOuterActionMenuPresenter = paramActionMenuPresenter;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setMenuCallbacks(MenuPresenter.Callback paramCallback, MenuBuilder.Callback paramCallback1) {
    this.mActionMenuPresenterCallback = paramCallback;
    this.mMenuBuilderCallback = paramCallback1;
    if (this.mMenuView != null)
      this.mMenuView.setMenuCallbacks(paramCallback, paramCallback1); 
  }
  
  public void setNavigationContentDescription(@StringRes int paramInt) {
    CharSequence charSequence;
    if (paramInt != 0) {
      charSequence = getContext().getText(paramInt);
    } else {
      charSequence = null;
    } 
    setNavigationContentDescription(charSequence);
  }
  
  public void setNavigationContentDescription(@Nullable CharSequence paramCharSequence) {
    if (!TextUtils.isEmpty(paramCharSequence))
      ensureNavButtonView(); 
    if (this.mNavButtonView != null)
      this.mNavButtonView.setContentDescription(paramCharSequence); 
  }
  
  public void setNavigationIcon(@DrawableRes int paramInt) { setNavigationIcon(AppCompatResources.getDrawable(getContext(), paramInt)); }
  
  public void setNavigationIcon(@Nullable Drawable paramDrawable) {
    if (paramDrawable != null) {
      ensureNavButtonView();
      if (!isChildOrHidden(this.mNavButtonView))
        addSystemView(this.mNavButtonView, true); 
    } else if (this.mNavButtonView != null && isChildOrHidden(this.mNavButtonView)) {
      removeView(this.mNavButtonView);
      this.mHiddenViews.remove(this.mNavButtonView);
    } 
    if (this.mNavButtonView != null)
      this.mNavButtonView.setImageDrawable(paramDrawable); 
  }
  
  public void setNavigationOnClickListener(View.OnClickListener paramOnClickListener) {
    ensureNavButtonView();
    this.mNavButtonView.setOnClickListener(paramOnClickListener);
  }
  
  public void setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener) { this.mOnMenuItemClickListener = paramOnMenuItemClickListener; }
  
  public void setOverflowIcon(@Nullable Drawable paramDrawable) {
    ensureMenu();
    this.mMenuView.setOverflowIcon(paramDrawable);
  }
  
  public void setPopupTheme(@StyleRes int paramInt) {
    if (this.mPopupTheme != paramInt) {
      this.mPopupTheme = paramInt;
      if (paramInt == 0) {
        this.mPopupContext = getContext();
        return;
      } 
      this.mPopupContext = new ContextThemeWrapper(getContext(), paramInt);
    } 
  }
  
  public void setSubtitle(@StringRes int paramInt) { setSubtitle(getContext().getText(paramInt)); }
  
  public void setSubtitle(CharSequence paramCharSequence) {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      if (this.mSubtitleTextView == null) {
        Context context = getContext();
        this.mSubtitleTextView = new AppCompatTextView(context);
        this.mSubtitleTextView.setSingleLine();
        this.mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        if (this.mSubtitleTextAppearance != 0)
          this.mSubtitleTextView.setTextAppearance(context, this.mSubtitleTextAppearance); 
        if (this.mSubtitleTextColor != 0)
          this.mSubtitleTextView.setTextColor(this.mSubtitleTextColor); 
      } 
      if (!isChildOrHidden(this.mSubtitleTextView))
        addSystemView(this.mSubtitleTextView, true); 
    } else if (this.mSubtitleTextView != null && isChildOrHidden(this.mSubtitleTextView)) {
      removeView(this.mSubtitleTextView);
      this.mHiddenViews.remove(this.mSubtitleTextView);
    } 
    if (this.mSubtitleTextView != null)
      this.mSubtitleTextView.setText(paramCharSequence); 
    this.mSubtitleText = paramCharSequence;
  }
  
  public void setSubtitleTextAppearance(Context paramContext, @StyleRes int paramInt) {
    this.mSubtitleTextAppearance = paramInt;
    if (this.mSubtitleTextView != null)
      this.mSubtitleTextView.setTextAppearance(paramContext, paramInt); 
  }
  
  public void setSubtitleTextColor(@ColorInt int paramInt) {
    this.mSubtitleTextColor = paramInt;
    if (this.mSubtitleTextView != null)
      this.mSubtitleTextView.setTextColor(paramInt); 
  }
  
  public void setTitle(@StringRes int paramInt) { setTitle(getContext().getText(paramInt)); }
  
  public void setTitle(CharSequence paramCharSequence) {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      if (this.mTitleTextView == null) {
        Context context = getContext();
        this.mTitleTextView = new AppCompatTextView(context);
        this.mTitleTextView.setSingleLine();
        this.mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        if (this.mTitleTextAppearance != 0)
          this.mTitleTextView.setTextAppearance(context, this.mTitleTextAppearance); 
        if (this.mTitleTextColor != 0)
          this.mTitleTextView.setTextColor(this.mTitleTextColor); 
      } 
      if (!isChildOrHidden(this.mTitleTextView))
        addSystemView(this.mTitleTextView, true); 
    } else if (this.mTitleTextView != null && isChildOrHidden(this.mTitleTextView)) {
      removeView(this.mTitleTextView);
      this.mHiddenViews.remove(this.mTitleTextView);
    } 
    if (this.mTitleTextView != null)
      this.mTitleTextView.setText(paramCharSequence); 
    this.mTitleText = paramCharSequence;
  }
  
  public void setTitleMargin(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.mTitleMarginStart = paramInt1;
    this.mTitleMarginTop = paramInt2;
    this.mTitleMarginEnd = paramInt3;
    this.mTitleMarginBottom = paramInt4;
    requestLayout();
  }
  
  public void setTitleMarginBottom(int paramInt) {
    this.mTitleMarginBottom = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginEnd(int paramInt) {
    this.mTitleMarginEnd = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginStart(int paramInt) {
    this.mTitleMarginStart = paramInt;
    requestLayout();
  }
  
  public void setTitleMarginTop(int paramInt) {
    this.mTitleMarginTop = paramInt;
    requestLayout();
  }
  
  public void setTitleTextAppearance(Context paramContext, @StyleRes int paramInt) {
    this.mTitleTextAppearance = paramInt;
    if (this.mTitleTextView != null)
      this.mTitleTextView.setTextAppearance(paramContext, paramInt); 
  }
  
  public void setTitleTextColor(@ColorInt int paramInt) {
    this.mTitleTextColor = paramInt;
    if (this.mTitleTextView != null)
      this.mTitleTextView.setTextColor(paramInt); 
  }
  
  public boolean showOverflowMenu() { return (this.mMenuView != null && this.mMenuView.showOverflowMenu()); }
  
  private class ExpandedActionViewMenuPresenter implements MenuPresenter {
    MenuItemImpl mCurrentExpandedItem;
    
    MenuBuilder mMenu;
    
    public boolean collapseItemActionView(MenuBuilder param1MenuBuilder, MenuItemImpl param1MenuItemImpl) {
      if (Toolbar.this.mExpandedActionView instanceof CollapsibleActionView)
        ((CollapsibleActionView)Toolbar.this.mExpandedActionView).onActionViewCollapsed(); 
      Toolbar.this.removeView(Toolbar.this.mExpandedActionView);
      Toolbar.this.removeView(Toolbar.this.mCollapseButtonView);
      Toolbar.this.mExpandedActionView = null;
      Toolbar.this.addChildrenForExpandedActionView();
      this.mCurrentExpandedItem = null;
      Toolbar.this.requestLayout();
      param1MenuItemImpl.setActionViewExpanded(false);
      return true;
    }
    
    public boolean expandItemActionView(MenuBuilder param1MenuBuilder, MenuItemImpl param1MenuItemImpl) {
      Toolbar.this.ensureCollapseButtonView();
      ViewParent viewParent = Toolbar.this.mCollapseButtonView.getParent();
      if (viewParent != Toolbar.this) {
        if (viewParent instanceof ViewGroup)
          ((ViewGroup)viewParent).removeView(Toolbar.this.mCollapseButtonView); 
        Toolbar.this.addView(Toolbar.this.mCollapseButtonView);
      } 
      Toolbar.this.mExpandedActionView = param1MenuItemImpl.getActionView();
      this.mCurrentExpandedItem = param1MenuItemImpl;
      viewParent = Toolbar.this.mExpandedActionView.getParent();
      if (viewParent != Toolbar.this) {
        if (viewParent instanceof ViewGroup)
          ((ViewGroup)viewParent).removeView(Toolbar.this.mExpandedActionView); 
        Toolbar.LayoutParams layoutParams = Toolbar.this.generateDefaultLayoutParams();
        layoutParams.gravity = 0x800003 | Toolbar.this.mButtonGravity & 0x70;
        layoutParams.mViewType = 2;
        Toolbar.this.mExpandedActionView.setLayoutParams(layoutParams);
        Toolbar.this.addView(Toolbar.this.mExpandedActionView);
      } 
      Toolbar.this.removeChildrenForExpandedActionView();
      Toolbar.this.requestLayout();
      param1MenuItemImpl.setActionViewExpanded(true);
      if (Toolbar.this.mExpandedActionView instanceof CollapsibleActionView)
        ((CollapsibleActionView)Toolbar.this.mExpandedActionView).onActionViewExpanded(); 
      return true;
    }
    
    public boolean flagActionItems() { return false; }
    
    public int getId() { return 0; }
    
    public MenuView getMenuView(ViewGroup param1ViewGroup) { return null; }
    
    public void initForMenu(Context param1Context, MenuBuilder param1MenuBuilder) {
      if (this.mMenu != null && this.mCurrentExpandedItem != null)
        this.mMenu.collapseItemActionView(this.mCurrentExpandedItem); 
      this.mMenu = param1MenuBuilder;
    }
    
    public void onCloseMenu(MenuBuilder param1MenuBuilder, boolean param1Boolean) {}
    
    public void onRestoreInstanceState(Parcelable param1Parcelable) {}
    
    public Parcelable onSaveInstanceState() { return null; }
    
    public boolean onSubMenuSelected(SubMenuBuilder param1SubMenuBuilder) { return false; }
    
    public void setCallback(MenuPresenter.Callback param1Callback) {}
    
    public void updateMenuView(boolean param1Boolean) {
      if (this.mCurrentExpandedItem != null) {
        MenuBuilder menuBuilder = this.mMenu;
        byte b2 = 0;
        byte b1 = b2;
        if (menuBuilder != null) {
          int i = this.mMenu.size();
          byte b = 0;
          while (true) {
            b1 = b2;
            if (b < i) {
              if (this.mMenu.getItem(b) == this.mCurrentExpandedItem) {
                b1 = 1;
                break;
              } 
              b++;
              continue;
            } 
            break;
          } 
        } 
        if (b1 == 0)
          collapseItemActionView(this.mMenu, this.mCurrentExpandedItem); 
      } 
    }
  }
  
  public static class LayoutParams extends ActionBar.LayoutParams {
    static final int CUSTOM = 0;
    
    static final int EXPANDED = 2;
    
    static final int SYSTEM = 1;
    
    int mViewType = 0;
    
    public LayoutParams(int param1Int) { this(-2, -1, param1Int); }
    
    public LayoutParams(int param1Int1, int param1Int2) {
      super(param1Int1, param1Int2);
      this.gravity = 8388627;
    }
    
    public LayoutParams(int param1Int1, int param1Int2, int param1Int3) {
      super(param1Int1, param1Int2);
      this.gravity = param1Int3;
    }
    
    public LayoutParams(@NonNull Context param1Context, AttributeSet param1AttributeSet) { super(param1Context, param1AttributeSet); }
    
    public LayoutParams(ActionBar.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(LayoutParams param1LayoutParams) {
      super(param1LayoutParams);
      this.mViewType = param1LayoutParams.mViewType;
    }
    
    public LayoutParams(ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.MarginLayoutParams param1MarginLayoutParams) {
      super(param1MarginLayoutParams);
      copyMarginsFromCompat(param1MarginLayoutParams);
    }
    
    void copyMarginsFromCompat(ViewGroup.MarginLayoutParams param1MarginLayoutParams) {
      this.leftMargin = param1MarginLayoutParams.leftMargin;
      this.topMargin = param1MarginLayoutParams.topMargin;
      this.rightMargin = param1MarginLayoutParams.rightMargin;
      this.bottomMargin = param1MarginLayoutParams.bottomMargin;
    }
  }
  
  public static interface OnMenuItemClickListener {
    boolean onMenuItemClick(MenuItem param1MenuItem);
  }
  
  public static class SavedState extends AbsSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
        public Toolbar.SavedState createFromParcel(Parcel param2Parcel) { return new Toolbar.SavedState(param2Parcel, null); }
        
        public Toolbar.SavedState createFromParcel(Parcel param2Parcel, ClassLoader param2ClassLoader) { return new Toolbar.SavedState(param2Parcel, param2ClassLoader); }
        
        public Toolbar.SavedState[] newArray(int param2Int) { return new Toolbar.SavedState[param2Int]; }
      };
    
    int expandedMenuItemId;
    
    boolean isOverflowOpen;
    
    public SavedState(Parcel param1Parcel) { this(param1Parcel, null); }
    
    public SavedState(Parcel param1Parcel, ClassLoader param1ClassLoader) {
      super(param1Parcel, param1ClassLoader);
      this.expandedMenuItemId = param1Parcel.readInt();
      if (param1Parcel.readInt() != 0) {
        bool = true;
      } else {
        bool = false;
      } 
      this.isOverflowOpen = bool;
    }
    
    public SavedState(Parcelable param1Parcelable) { super(param1Parcelable); }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  }
  
  static final class null extends Object implements Parcelable.ClassLoaderCreator<SavedState> {
    public Toolbar.SavedState createFromParcel(Parcel param1Parcel) { return new Toolbar.SavedState(param1Parcel, null); }
    
    public Toolbar.SavedState createFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) { return new Toolbar.SavedState(param1Parcel, param1ClassLoader); }
    
    public Toolbar.SavedState[] newArray(int param1Int) { return new Toolbar.SavedState[param1Int]; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/Toolbar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */