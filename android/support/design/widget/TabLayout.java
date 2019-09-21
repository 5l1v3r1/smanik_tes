package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;
import android.support.design.R;
import android.support.v4.util.Pools;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.DecorView;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.TooltipCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

@DecorView
public class TabLayout extends HorizontalScrollView {
  private static final int ANIMATION_DURATION = 300;
  
  static final int DEFAULT_GAP_TEXT_ICON = 8;
  
  private static final int DEFAULT_HEIGHT = 48;
  
  private static final int DEFAULT_HEIGHT_WITH_TEXT_ICON = 72;
  
  static final int FIXED_WRAP_GUTTER_MIN = 16;
  
  public static final int GRAVITY_CENTER = 1;
  
  public static final int GRAVITY_FILL = 0;
  
  private static final int INVALID_WIDTH = -1;
  
  public static final int MODE_FIXED = 1;
  
  public static final int MODE_SCROLLABLE = 0;
  
  static final int MOTION_NON_ADJACENT_OFFSET = 24;
  
  private static final int TAB_MIN_WIDTH_MARGIN = 56;
  
  private static final Pools.Pool<Tab> sTabPool = new Pools.SynchronizedPool(16);
  
  private AdapterChangeListener mAdapterChangeListener;
  
  private int mContentInsetStart;
  
  private OnTabSelectedListener mCurrentVpSelectedListener;
  
  int mMode;
  
  private TabLayoutOnPageChangeListener mPageChangeListener;
  
  private PagerAdapter mPagerAdapter;
  
  private DataSetObserver mPagerAdapterObserver;
  
  private final int mRequestedTabMaxWidth;
  
  private final int mRequestedTabMinWidth;
  
  private ValueAnimator mScrollAnimator;
  
  private final int mScrollableTabMinWidth;
  
  private OnTabSelectedListener mSelectedListener;
  
  private final ArrayList<OnTabSelectedListener> mSelectedListeners = new ArrayList();
  
  private Tab mSelectedTab;
  
  private boolean mSetupViewPagerImplicitly;
  
  final int mTabBackgroundResId;
  
  int mTabGravity;
  
  int mTabMaxWidth = Integer.MAX_VALUE;
  
  int mTabPaddingBottom;
  
  int mTabPaddingEnd;
  
  int mTabPaddingStart;
  
  int mTabPaddingTop;
  
  private final SlidingTabStrip mTabStrip;
  
  int mTabTextAppearance;
  
  ColorStateList mTabTextColors;
  
  float mTabTextMultiLineSize;
  
  float mTabTextSize;
  
  private final Pools.Pool<TabView> mTabViewPool = new Pools.SimplePool(12);
  
  private final ArrayList<Tab> mTabs = new ArrayList();
  
  ViewPager mViewPager;
  
  public TabLayout(Context paramContext) { this(paramContext, null); }
  
  public TabLayout(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public TabLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    ThemeUtils.checkAppCompatTheme(paramContext);
    setHorizontalScrollBarEnabled(false);
    this.mTabStrip = new SlidingTabStrip(paramContext);
    super.addView(this.mTabStrip, 0, new FrameLayout.LayoutParams(-2, -1));
    null = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.TabLayout, paramInt, R.style.Widget_Design_TabLayout);
    this.mTabStrip.setSelectedIndicatorHeight(null.getDimensionPixelSize(R.styleable.TabLayout_tabIndicatorHeight, 0));
    this.mTabStrip.setSelectedIndicatorColor(null.getColor(R.styleable.TabLayout_tabIndicatorColor, 0));
    paramInt = null.getDimensionPixelSize(R.styleable.TabLayout_tabPadding, 0);
    this.mTabPaddingBottom = paramInt;
    this.mTabPaddingEnd = paramInt;
    this.mTabPaddingTop = paramInt;
    this.mTabPaddingStart = paramInt;
    this.mTabPaddingStart = null.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingStart, this.mTabPaddingStart);
    this.mTabPaddingTop = null.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingTop, this.mTabPaddingTop);
    this.mTabPaddingEnd = null.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingEnd, this.mTabPaddingEnd);
    this.mTabPaddingBottom = null.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingBottom, this.mTabPaddingBottom);
    this.mTabTextAppearance = null.getResourceId(R.styleable.TabLayout_tabTextAppearance, R.style.TextAppearance_Design_Tab);
    Resources resources = paramContext.obtainStyledAttributes(this.mTabTextAppearance, R.styleable.TextAppearance);
    try {
      this.mTabTextSize = resources.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, 0);
      this.mTabTextColors = resources.getColorStateList(R.styleable.TextAppearance_android_textColor);
      resources.recycle();
      if (null.hasValue(R.styleable.TabLayout_tabTextColor))
        this.mTabTextColors = null.getColorStateList(R.styleable.TabLayout_tabTextColor); 
      if (null.hasValue(R.styleable.TabLayout_tabSelectedTextColor)) {
        paramInt = null.getColor(R.styleable.TabLayout_tabSelectedTextColor, 0);
        this.mTabTextColors = createColorStateList(this.mTabTextColors.getDefaultColor(), paramInt);
      } 
      this.mRequestedTabMinWidth = null.getDimensionPixelSize(R.styleable.TabLayout_tabMinWidth, -1);
      this.mRequestedTabMaxWidth = null.getDimensionPixelSize(R.styleable.TabLayout_tabMaxWidth, -1);
      this.mTabBackgroundResId = null.getResourceId(R.styleable.TabLayout_tabBackground, 0);
      this.mContentInsetStart = null.getDimensionPixelSize(R.styleable.TabLayout_tabContentStart, 0);
      this.mMode = null.getInt(R.styleable.TabLayout_tabMode, 1);
      this.mTabGravity = null.getInt(R.styleable.TabLayout_tabGravity, 0);
      null.recycle();
      resources = getResources();
      this.mTabTextMultiLineSize = resources.getDimensionPixelSize(R.dimen.design_tab_text_size_2line);
      this.mScrollableTabMinWidth = resources.getDimensionPixelSize(R.dimen.design_tab_scrollable_min_width);
      return;
    } finally {
      resources.recycle();
    } 
  }
  
  private void addTabFromItemView(@NonNull TabItem paramTabItem) {
    Tab tab = newTab();
    if (paramTabItem.mText != null)
      tab.setText(paramTabItem.mText); 
    if (paramTabItem.mIcon != null)
      tab.setIcon(paramTabItem.mIcon); 
    if (paramTabItem.mCustomLayout != 0)
      tab.setCustomView(paramTabItem.mCustomLayout); 
    if (!TextUtils.isEmpty(paramTabItem.getContentDescription()))
      tab.setContentDescription(paramTabItem.getContentDescription()); 
    addTab(tab);
  }
  
  private void addTabView(Tab paramTab) {
    TabView tabView = paramTab.mView;
    this.mTabStrip.addView(tabView, paramTab.getPosition(), createLayoutParamsForTabs());
  }
  
  private void addViewInternal(View paramView) {
    if (paramView instanceof TabItem) {
      addTabFromItemView((TabItem)paramView);
      return;
    } 
    throw new IllegalArgumentException("Only TabItem instances can be added to TabLayout");
  }
  
  private void animateToTab(int paramInt) {
    if (paramInt == -1)
      return; 
    if (getWindowToken() == null || !ViewCompat.isLaidOut(this) || this.mTabStrip.childrenNeedLayout()) {
      setScrollPosition(paramInt, 0.0F, true);
      return;
    } 
    int i = getScrollX();
    int j = calculateScrollXForTab(paramInt, 0.0F);
    if (i != j) {
      ensureScrollAnimator();
      this.mScrollAnimator.setIntValues(new int[] { i, j });
      this.mScrollAnimator.start();
    } 
    this.mTabStrip.animateIndicatorToPosition(paramInt, 300);
  }
  
  private void applyModeAndGravity() {
    byte b;
    if (this.mMode == 0) {
      b = Math.max(0, this.mContentInsetStart - this.mTabPaddingStart);
    } else {
      b = 0;
    } 
    ViewCompat.setPaddingRelative(this.mTabStrip, b, 0, 0, 0);
    switch (this.mMode) {
      case 1:
        this.mTabStrip.setGravity(1);
        break;
      case 0:
        this.mTabStrip.setGravity(8388611);
        break;
    } 
    updateTabViews(true);
  }
  
  private int calculateScrollXForTab(int paramInt, float paramFloat) {
    int j = this.mMode;
    int i = 0;
    if (j == 0) {
      Object object;
      View view = this.mTabStrip.getChildAt(paramInt);
      if (++paramInt < this.mTabStrip.getChildCount()) {
        object = this.mTabStrip.getChildAt(paramInt);
      } else {
        object = null;
      } 
      if (view != null) {
        paramInt = view.getWidth();
      } else {
        paramInt = 0;
      } 
      if (object != null)
        i = object.getWidth(); 
      j = view.getLeft() + paramInt / 2 - getWidth() / 2;
      paramInt = (int)((paramInt + i) * 0.5F * paramFloat);
      return (ViewCompat.getLayoutDirection(this) == 0) ? (j + paramInt) : (j - paramInt);
    } 
    return 0;
  }
  
  private void configureTab(Tab paramTab, int paramInt) {
    paramTab.setPosition(paramInt);
    this.mTabs.add(paramInt, paramTab);
    int i = this.mTabs.size();
    while (true) {
      if (++paramInt < i) {
        ((Tab)this.mTabs.get(paramInt)).setPosition(paramInt);
        continue;
      } 
      break;
    } 
  }
  
  private static ColorStateList createColorStateList(int paramInt1, int paramInt2) { return new ColorStateList(new int[][] { SELECTED_STATE_SET, EMPTY_STATE_SET }, new int[] { paramInt2, paramInt1 }); }
  
  private LinearLayout.LayoutParams createLayoutParamsForTabs() {
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -1);
    updateTabViewLayoutParams(layoutParams);
    return layoutParams;
  }
  
  private TabView createTabView(@NonNull Tab paramTab) {
    Object object;
    if (this.mTabViewPool != null) {
      object = (TabView)this.mTabViewPool.acquire();
    } else {
      object = null;
    } 
    TabView tabView = object;
    if (object == null)
      tabView = new TabView(getContext()); 
    tabView.setTab(paramTab);
    tabView.setFocusable(true);
    tabView.setMinimumWidth(getTabMinWidth());
    return tabView;
  }
  
  private void dispatchTabReselected(@NonNull Tab paramTab) {
    for (int i = this.mSelectedListeners.size() - 1; i >= 0; i--)
      ((OnTabSelectedListener)this.mSelectedListeners.get(i)).onTabReselected(paramTab); 
  }
  
  private void dispatchTabSelected(@NonNull Tab paramTab) {
    for (int i = this.mSelectedListeners.size() - 1; i >= 0; i--)
      ((OnTabSelectedListener)this.mSelectedListeners.get(i)).onTabSelected(paramTab); 
  }
  
  private void dispatchTabUnselected(@NonNull Tab paramTab) {
    for (int i = this.mSelectedListeners.size() - 1; i >= 0; i--)
      ((OnTabSelectedListener)this.mSelectedListeners.get(i)).onTabUnselected(paramTab); 
  }
  
  private void ensureScrollAnimator() {
    if (this.mScrollAnimator == null) {
      this.mScrollAnimator = new ValueAnimator();
      this.mScrollAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
      this.mScrollAnimator.setDuration(300L);
      this.mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) { TabLayout.this.scrollTo(((Integer)param1ValueAnimator.getAnimatedValue()).intValue(), 0); }
          });
    } 
  }
  
  private int getDefaultHeight() {
    boolean bool1;
    int i = this.mTabs.size();
    boolean bool2 = false;
    byte b = 0;
    while (true) {
      bool1 = bool2;
      if (b < i) {
        Tab tab = (Tab)this.mTabs.get(b);
        if (tab != null && tab.getIcon() != null && !TextUtils.isEmpty(tab.getText())) {
          bool1 = true;
          break;
        } 
        b++;
        continue;
      } 
      break;
    } 
    return bool1 ? 72 : 48;
  }
  
  private float getScrollPosition() { return this.mTabStrip.getIndicatorPosition(); }
  
  private int getTabMinWidth() { return (this.mRequestedTabMinWidth != -1) ? this.mRequestedTabMinWidth : ((this.mMode == 0) ? this.mScrollableTabMinWidth : 0); }
  
  private int getTabScrollRange() { return Math.max(0, this.mTabStrip.getWidth() - getWidth() - getPaddingLeft() - getPaddingRight()); }
  
  private void removeTabViewAt(int paramInt) {
    TabView tabView = (TabView)this.mTabStrip.getChildAt(paramInt);
    this.mTabStrip.removeViewAt(paramInt);
    if (tabView != null) {
      tabView.reset();
      this.mTabViewPool.release(tabView);
    } 
    requestLayout();
  }
  
  private void setSelectedTabView(int paramInt) {
    int i = this.mTabStrip.getChildCount();
    if (paramInt < i)
      for (byte b = 0; b < i; b++) {
        boolean bool;
        View view = this.mTabStrip.getChildAt(b);
        if (b == paramInt) {
          bool = true;
        } else {
          bool = false;
        } 
        view.setSelected(bool);
      }  
  }
  
  private void setupWithViewPager(@Nullable ViewPager paramViewPager, boolean paramBoolean1, boolean paramBoolean2) {
    if (this.mViewPager != null) {
      if (this.mPageChangeListener != null)
        this.mViewPager.removeOnPageChangeListener(this.mPageChangeListener); 
      if (this.mAdapterChangeListener != null)
        this.mViewPager.removeOnAdapterChangeListener(this.mAdapterChangeListener); 
    } 
    if (this.mCurrentVpSelectedListener != null) {
      removeOnTabSelectedListener(this.mCurrentVpSelectedListener);
      this.mCurrentVpSelectedListener = null;
    } 
    if (paramViewPager != null) {
      this.mViewPager = paramViewPager;
      if (this.mPageChangeListener == null)
        this.mPageChangeListener = new TabLayoutOnPageChangeListener(this); 
      this.mPageChangeListener.reset();
      paramViewPager.addOnPageChangeListener(this.mPageChangeListener);
      this.mCurrentVpSelectedListener = new ViewPagerOnTabSelectedListener(paramViewPager);
      addOnTabSelectedListener(this.mCurrentVpSelectedListener);
      PagerAdapter pagerAdapter = paramViewPager.getAdapter();
      if (pagerAdapter != null)
        setPagerAdapter(pagerAdapter, paramBoolean1); 
      if (this.mAdapterChangeListener == null)
        this.mAdapterChangeListener = new AdapterChangeListener(); 
      this.mAdapterChangeListener.setAutoRefresh(paramBoolean1);
      paramViewPager.addOnAdapterChangeListener(this.mAdapterChangeListener);
      setScrollPosition(paramViewPager.getCurrentItem(), 0.0F, true);
    } else {
      this.mViewPager = null;
      setPagerAdapter(null, false);
    } 
    this.mSetupViewPagerImplicitly = paramBoolean2;
  }
  
  private void updateAllTabs() {
    int i = this.mTabs.size();
    for (byte b = 0; b < i; b++)
      ((Tab)this.mTabs.get(b)).updateView(); 
  }
  
  private void updateTabViewLayoutParams(LinearLayout.LayoutParams paramLayoutParams) {
    if (this.mMode == 1 && this.mTabGravity == 0) {
      paramLayoutParams.width = 0;
      paramLayoutParams.weight = 1.0F;
      return;
    } 
    paramLayoutParams.width = -2;
    paramLayoutParams.weight = 0.0F;
  }
  
  public void addOnTabSelectedListener(@NonNull OnTabSelectedListener paramOnTabSelectedListener) {
    if (!this.mSelectedListeners.contains(paramOnTabSelectedListener))
      this.mSelectedListeners.add(paramOnTabSelectedListener); 
  }
  
  public void addTab(@NonNull Tab paramTab) { addTab(paramTab, this.mTabs.isEmpty()); }
  
  public void addTab(@NonNull Tab paramTab, int paramInt) { addTab(paramTab, paramInt, this.mTabs.isEmpty()); }
  
  public void addTab(@NonNull Tab paramTab, int paramInt, boolean paramBoolean) {
    if (paramTab.mParent != this)
      throw new IllegalArgumentException("Tab belongs to a different TabLayout."); 
    configureTab(paramTab, paramInt);
    addTabView(paramTab);
    if (paramBoolean)
      paramTab.select(); 
  }
  
  public void addTab(@NonNull Tab paramTab, boolean paramBoolean) { addTab(paramTab, this.mTabs.size(), paramBoolean); }
  
  public void addView(View paramView) { addViewInternal(paramView); }
  
  public void addView(View paramView, int paramInt) { addViewInternal(paramView); }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams) { addViewInternal(paramView); }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams) { addViewInternal(paramView); }
  
  public void clearOnTabSelectedListeners() { this.mSelectedListeners.clear(); }
  
  int dpToPx(int paramInt) { return Math.round((getResources().getDisplayMetrics()).density * paramInt); }
  
  public FrameLayout.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return generateDefaultLayoutParams(); }
  
  public int getSelectedTabPosition() { return (this.mSelectedTab != null) ? this.mSelectedTab.getPosition() : -1; }
  
  @Nullable
  public Tab getTabAt(int paramInt) { return (paramInt < 0 || paramInt >= getTabCount()) ? null : (Tab)this.mTabs.get(paramInt); }
  
  public int getTabCount() { return this.mTabs.size(); }
  
  public int getTabGravity() { return this.mTabGravity; }
  
  int getTabMaxWidth() { return this.mTabMaxWidth; }
  
  public int getTabMode() { return this.mMode; }
  
  @Nullable
  public ColorStateList getTabTextColors() { return this.mTabTextColors; }
  
  @NonNull
  public Tab newTab() {
    Tab tab2 = (Tab)sTabPool.acquire();
    Tab tab1 = tab2;
    if (tab2 == null)
      tab1 = new Tab(); 
    tab1.mParent = this;
    tab1.mView = createTabView(tab1);
    return tab1;
  }
  
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (this.mViewPager == null) {
      ViewParent viewParent = getParent();
      if (viewParent instanceof ViewPager)
        setupWithViewPager((ViewPager)viewParent, true, true); 
    } 
  }
  
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (this.mSetupViewPagerImplicitly) {
      setupWithViewPager(null);
      this.mSetupViewPagerImplicitly = false;
    } 
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: aload_0
    //   2: invokespecial getDefaultHeight : ()I
    //   5: invokevirtual dpToPx : (I)I
    //   8: aload_0
    //   9: invokevirtual getPaddingTop : ()I
    //   12: iadd
    //   13: aload_0
    //   14: invokevirtual getPaddingBottom : ()I
    //   17: iadd
    //   18: istore_3
    //   19: iload_2
    //   20: invokestatic getMode : (I)I
    //   23: istore #4
    //   25: iload #4
    //   27: ldc_w -2147483648
    //   30: if_icmpeq -> 52
    //   33: iload #4
    //   35: ifeq -> 41
    //   38: goto -> 67
    //   41: iload_3
    //   42: ldc_w 1073741824
    //   45: invokestatic makeMeasureSpec : (II)I
    //   48: istore_2
    //   49: goto -> 67
    //   52: iload_3
    //   53: iload_2
    //   54: invokestatic getSize : (I)I
    //   57: invokestatic min : (II)I
    //   60: ldc_w 1073741824
    //   63: invokestatic makeMeasureSpec : (II)I
    //   66: istore_2
    //   67: iload_1
    //   68: invokestatic getSize : (I)I
    //   71: istore_3
    //   72: iload_1
    //   73: invokestatic getMode : (I)I
    //   76: ifeq -> 108
    //   79: aload_0
    //   80: getfield mRequestedTabMaxWidth : I
    //   83: ifle -> 94
    //   86: aload_0
    //   87: getfield mRequestedTabMaxWidth : I
    //   90: istore_3
    //   91: goto -> 103
    //   94: iload_3
    //   95: aload_0
    //   96: bipush #56
    //   98: invokevirtual dpToPx : (I)I
    //   101: isub
    //   102: istore_3
    //   103: aload_0
    //   104: iload_3
    //   105: putfield mTabMaxWidth : I
    //   108: aload_0
    //   109: iload_1
    //   110: iload_2
    //   111: invokespecial onMeasure : (II)V
    //   114: aload_0
    //   115: invokevirtual getChildCount : ()I
    //   118: iconst_1
    //   119: if_icmpne -> 233
    //   122: iconst_0
    //   123: istore_1
    //   124: aload_0
    //   125: iconst_0
    //   126: invokevirtual getChildAt : (I)Landroid/view/View;
    //   129: astore #5
    //   131: aload_0
    //   132: getfield mMode : I
    //   135: tableswitch default -> 156, 0 -> 176, 1 -> 159
    //   156: goto -> 191
    //   159: aload #5
    //   161: invokevirtual getMeasuredWidth : ()I
    //   164: aload_0
    //   165: invokevirtual getMeasuredWidth : ()I
    //   168: if_icmpeq -> 191
    //   171: iconst_1
    //   172: istore_1
    //   173: goto -> 191
    //   176: aload #5
    //   178: invokevirtual getMeasuredWidth : ()I
    //   181: aload_0
    //   182: invokevirtual getMeasuredWidth : ()I
    //   185: if_icmpge -> 191
    //   188: goto -> 171
    //   191: iload_1
    //   192: ifeq -> 233
    //   195: iload_2
    //   196: aload_0
    //   197: invokevirtual getPaddingTop : ()I
    //   200: aload_0
    //   201: invokevirtual getPaddingBottom : ()I
    //   204: iadd
    //   205: aload #5
    //   207: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   210: getfield height : I
    //   213: invokestatic getChildMeasureSpec : (III)I
    //   216: istore_1
    //   217: aload #5
    //   219: aload_0
    //   220: invokevirtual getMeasuredWidth : ()I
    //   223: ldc_w 1073741824
    //   226: invokestatic makeMeasureSpec : (II)I
    //   229: iload_1
    //   230: invokevirtual measure : (II)V
    //   233: return }
  
  void populateFromPagerAdapter() {
    removeAllTabs();
    if (this.mPagerAdapter != null) {
      int j = this.mPagerAdapter.getCount();
      int i;
      for (i = 0; i < j; i++)
        addTab(newTab().setText(this.mPagerAdapter.getPageTitle(i)), false); 
      if (this.mViewPager != null && j > 0) {
        i = this.mViewPager.getCurrentItem();
        if (i != getSelectedTabPosition() && i < getTabCount())
          selectTab(getTabAt(i)); 
      } 
    } 
  }
  
  public void removeAllTabs() {
    for (int i = this.mTabStrip.getChildCount() - 1; i >= 0; i--)
      removeTabViewAt(i); 
    Iterator iterator = this.mTabs.iterator();
    while (iterator.hasNext()) {
      Tab tab = (Tab)iterator.next();
      iterator.remove();
      tab.reset();
      sTabPool.release(tab);
    } 
    this.mSelectedTab = null;
  }
  
  public void removeOnTabSelectedListener(@NonNull OnTabSelectedListener paramOnTabSelectedListener) { this.mSelectedListeners.remove(paramOnTabSelectedListener); }
  
  public void removeTab(Tab paramTab) {
    if (paramTab.mParent != this)
      throw new IllegalArgumentException("Tab does not belong to this TabLayout."); 
    removeTabAt(paramTab.getPosition());
  }
  
  public void removeTabAt(int paramInt) {
    boolean bool;
    if (this.mSelectedTab != null) {
      bool = this.mSelectedTab.getPosition();
    } else {
      bool = false;
    } 
    removeTabViewAt(paramInt);
    Tab tab = (Tab)this.mTabs.remove(paramInt);
    if (tab != null) {
      tab.reset();
      sTabPool.release(tab);
    } 
    int j = this.mTabs.size();
    for (int i = paramInt; i < j; i++)
      ((Tab)this.mTabs.get(i)).setPosition(i); 
    if (bool == paramInt) {
      if (this.mTabs.isEmpty()) {
        tab = null;
      } else {
        tab = (Tab)this.mTabs.get(Math.max(0, paramInt - 1));
      } 
      selectTab(tab);
    } 
  }
  
  void selectTab(Tab paramTab) { selectTab(paramTab, true); }
  
  void selectTab(Tab paramTab, boolean paramBoolean) {
    Tab tab = this.mSelectedTab;
    if (tab == paramTab) {
      if (tab != null) {
        dispatchTabReselected(paramTab);
        animateToTab(paramTab.getPosition());
        return;
      } 
    } else {
      byte b;
      if (paramTab != null) {
        b = paramTab.getPosition();
      } else {
        b = -1;
      } 
      if (paramBoolean) {
        if ((tab == null || tab.getPosition() == -1) && b != -1) {
          setScrollPosition(b, 0.0F, true);
        } else {
          animateToTab(b);
        } 
        if (b != -1)
          setSelectedTabView(b); 
      } 
      if (tab != null)
        dispatchTabUnselected(tab); 
      this.mSelectedTab = paramTab;
      if (paramTab != null)
        dispatchTabSelected(paramTab); 
    } 
  }
  
  @Deprecated
  public void setOnTabSelectedListener(@Nullable OnTabSelectedListener paramOnTabSelectedListener) {
    if (this.mSelectedListener != null)
      removeOnTabSelectedListener(this.mSelectedListener); 
    this.mSelectedListener = paramOnTabSelectedListener;
    if (paramOnTabSelectedListener != null)
      addOnTabSelectedListener(paramOnTabSelectedListener); 
  }
  
  void setPagerAdapter(@Nullable PagerAdapter paramPagerAdapter, boolean paramBoolean) {
    if (this.mPagerAdapter != null && this.mPagerAdapterObserver != null)
      this.mPagerAdapter.unregisterDataSetObserver(this.mPagerAdapterObserver); 
    this.mPagerAdapter = paramPagerAdapter;
    if (paramBoolean && paramPagerAdapter != null) {
      if (this.mPagerAdapterObserver == null)
        this.mPagerAdapterObserver = new PagerAdapterObserver(); 
      paramPagerAdapter.registerDataSetObserver(this.mPagerAdapterObserver);
    } 
    populateFromPagerAdapter();
  }
  
  void setScrollAnimatorListener(Animator.AnimatorListener paramAnimatorListener) {
    ensureScrollAnimator();
    this.mScrollAnimator.addListener(paramAnimatorListener);
  }
  
  public void setScrollPosition(int paramInt, float paramFloat, boolean paramBoolean) { setScrollPosition(paramInt, paramFloat, paramBoolean, true); }
  
  void setScrollPosition(int paramInt, float paramFloat, boolean paramBoolean1, boolean paramBoolean2) {
    int i = Math.round(paramInt + paramFloat);
    if (i >= 0) {
      if (i >= this.mTabStrip.getChildCount())
        return; 
      if (paramBoolean2)
        this.mTabStrip.setIndicatorPositionFromTabPosition(paramInt, paramFloat); 
      if (this.mScrollAnimator != null && this.mScrollAnimator.isRunning())
        this.mScrollAnimator.cancel(); 
      scrollTo(calculateScrollXForTab(paramInt, paramFloat), 0);
      if (paramBoolean1)
        setSelectedTabView(i); 
      return;
    } 
  }
  
  public void setSelectedTabIndicatorColor(@ColorInt int paramInt) { this.mTabStrip.setSelectedIndicatorColor(paramInt); }
  
  public void setSelectedTabIndicatorHeight(int paramInt) { this.mTabStrip.setSelectedIndicatorHeight(paramInt); }
  
  public void setTabGravity(int paramInt) {
    if (this.mTabGravity != paramInt) {
      this.mTabGravity = paramInt;
      applyModeAndGravity();
    } 
  }
  
  public void setTabMode(int paramInt) {
    if (paramInt != this.mMode) {
      this.mMode = paramInt;
      applyModeAndGravity();
    } 
  }
  
  public void setTabTextColors(int paramInt1, int paramInt2) { setTabTextColors(createColorStateList(paramInt1, paramInt2)); }
  
  public void setTabTextColors(@Nullable ColorStateList paramColorStateList) {
    if (this.mTabTextColors != paramColorStateList) {
      this.mTabTextColors = paramColorStateList;
      updateAllTabs();
    } 
  }
  
  @Deprecated
  public void setTabsFromPagerAdapter(@Nullable PagerAdapter paramPagerAdapter) { setPagerAdapter(paramPagerAdapter, false); }
  
  public void setupWithViewPager(@Nullable ViewPager paramViewPager) { setupWithViewPager(paramViewPager, true); }
  
  public void setupWithViewPager(@Nullable ViewPager paramViewPager, boolean paramBoolean) { setupWithViewPager(paramViewPager, paramBoolean, false); }
  
  public boolean shouldDelayChildPressedState() { return (getTabScrollRange() > 0); }
  
  void updateTabViews(boolean paramBoolean) {
    for (byte b = 0; b < this.mTabStrip.getChildCount(); b++) {
      View view = this.mTabStrip.getChildAt(b);
      view.setMinimumWidth(getTabMinWidth());
      updateTabViewLayoutParams((LinearLayout.LayoutParams)view.getLayoutParams());
      if (paramBoolean)
        view.requestLayout(); 
    } 
  }
  
  private class AdapterChangeListener implements ViewPager.OnAdapterChangeListener {
    private boolean mAutoRefresh;
    
    public void onAdapterChanged(@NonNull ViewPager param1ViewPager, @Nullable PagerAdapter param1PagerAdapter1, @Nullable PagerAdapter param1PagerAdapter2) {
      if (TabLayout.this.mViewPager == param1ViewPager)
        TabLayout.this.setPagerAdapter(param1PagerAdapter2, this.mAutoRefresh); 
    }
    
    void setAutoRefresh(boolean param1Boolean) { this.mAutoRefresh = param1Boolean; }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface Mode {}
  
  public static interface OnTabSelectedListener {
    void onTabReselected(TabLayout.Tab param1Tab);
    
    void onTabSelected(TabLayout.Tab param1Tab);
    
    void onTabUnselected(TabLayout.Tab param1Tab);
  }
  
  private class PagerAdapterObserver extends DataSetObserver {
    public void onChanged() { TabLayout.this.populateFromPagerAdapter(); }
    
    public void onInvalidated() { TabLayout.this.populateFromPagerAdapter(); }
  }
  
  private class SlidingTabStrip extends LinearLayout {
    private ValueAnimator mIndicatorAnimator;
    
    private int mIndicatorLeft = -1;
    
    private int mIndicatorRight = -1;
    
    private int mLayoutDirection = -1;
    
    private int mSelectedIndicatorHeight;
    
    private final Paint mSelectedIndicatorPaint;
    
    int mSelectedPosition = -1;
    
    float mSelectionOffset;
    
    SlidingTabStrip(Context param1Context) {
      super(param1Context);
      setWillNotDraw(false);
      this.mSelectedIndicatorPaint = new Paint();
    }
    
    private void updateIndicatorPosition() {
      byte b2;
      byte b1;
      View view = getChildAt(this.mSelectedPosition);
      if (view != null && view.getWidth() > 0) {
        int j = view.getLeft();
        int i = view.getRight();
        b1 = i;
        b2 = j;
        if (this.mSelectionOffset > 0.0F) {
          b1 = i;
          b2 = j;
          if (this.mSelectedPosition < getChildCount() - 1) {
            view = getChildAt(this.mSelectedPosition + 1);
            b2 = (int)(this.mSelectionOffset * view.getLeft() + (1.0F - this.mSelectionOffset) * j);
            b1 = (int)(this.mSelectionOffset * view.getRight() + (1.0F - this.mSelectionOffset) * i);
          } 
        } 
      } else {
        b2 = -1;
        b1 = -1;
      } 
      setIndicatorPosition(b2, b1);
    }
    
    void animateIndicatorToPosition(final int position, int param1Int2) {
      final int startRight;
      final int startLeft;
      if (this.mIndicatorAnimator != null && this.mIndicatorAnimator.isRunning())
        this.mIndicatorAnimator.cancel(); 
      if (ViewCompat.getLayoutDirection(this) == 1) {
        i = 1;
      } else {
        i = 0;
      } 
      View view = getChildAt(param1Int1);
      if (view == null) {
        updateIndicatorPosition();
        return;
      } 
      final int targetLeft = view.getLeft();
      final int targetRight = view.getRight();
      if (Math.abs(param1Int1 - this.mSelectedPosition) <= 1) {
        i = this.mIndicatorLeft;
        j = this.mIndicatorRight;
      } else {
        j = TabLayout.this.dpToPx(24);
        if (param1Int1 < this.mSelectedPosition) {
          if (i != 0) {
            i = k - j;
          } else {
            i = j + m;
          } 
        } else if (i != 0) {
          i = j + m;
        } else {
          i = k - j;
        } 
        j = i;
      } 
      if (i != k || j != m) {
        ValueAnimator valueAnimator = new ValueAnimator();
        this.mIndicatorAnimator = valueAnimator;
        valueAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        valueAnimator.setDuration(param1Int2);
        valueAnimator.setFloatValues(new float[] { 0.0F, 1.0F });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
              public void onAnimationUpdate(ValueAnimator param2ValueAnimator) {
                float f = param2ValueAnimator.getAnimatedFraction();
                TabLayout.SlidingTabStrip.this.setIndicatorPosition(AnimationUtils.lerp(startLeft, targetLeft, f), AnimationUtils.lerp(startRight, targetRight, f));
              }
            });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
              public void onAnimationEnd(Animator param2Animator) {
                TabLayout.SlidingTabStrip.this.mSelectedPosition = position;
                TabLayout.SlidingTabStrip.this.mSelectionOffset = 0.0F;
              }
            });
        valueAnimator.start();
      } 
    }
    
    boolean childrenNeedLayout() {
      int i = getChildCount();
      for (byte b = 0; b < i; b++) {
        if (getChildAt(b).getWidth() <= 0)
          return true; 
      } 
      return false;
    }
    
    public void draw(Canvas param1Canvas) {
      super.draw(param1Canvas);
      if (this.mIndicatorLeft >= 0 && this.mIndicatorRight > this.mIndicatorLeft)
        param1Canvas.drawRect(this.mIndicatorLeft, (getHeight() - this.mSelectedIndicatorHeight), this.mIndicatorRight, getHeight(), this.mSelectedIndicatorPaint); 
    }
    
    float getIndicatorPosition() { return this.mSelectedPosition + this.mSelectionOffset; }
    
    protected void onLayout(boolean param1Boolean, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      super.onLayout(param1Boolean, param1Int1, param1Int2, param1Int3, param1Int4);
      if (this.mIndicatorAnimator != null && this.mIndicatorAnimator.isRunning()) {
        this.mIndicatorAnimator.cancel();
        long l = this.mIndicatorAnimator.getDuration();
        animateIndicatorToPosition(this.mSelectedPosition, Math.round((1.0F - this.mIndicatorAnimator.getAnimatedFraction()) * (float)l));
        return;
      } 
      updateIndicatorPosition();
    }
    
    protected void onMeasure(int param1Int1, int param1Int2) {
      super.onMeasure(param1Int1, param1Int2);
      if (View.MeasureSpec.getMode(param1Int1) != 1073741824)
        return; 
      int i = TabLayout.this.mMode;
      int j = 1;
      if (i == 1 && TabLayout.this.mTabGravity == 1) {
        int m = getChildCount();
        boolean bool = false;
        i = 0;
        int k;
        for (k = 0; i < m; k = n) {
          View view = getChildAt(i);
          int n = k;
          if (view.getVisibility() == 0)
            n = Math.max(k, view.getMeasuredWidth()); 
          i++;
        } 
        if (k <= 0)
          return; 
        i = TabLayout.this.dpToPx(16);
        if (k * m <= getMeasuredWidth() - i * 2) {
          i = 0;
          byte b;
          for (b = bool; b < m; b++) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)getChildAt(b).getLayoutParams();
            if (layoutParams.width != k || layoutParams.weight != 0.0F) {
              layoutParams.width = k;
              layoutParams.weight = 0.0F;
              i = 1;
            } 
          } 
        } else {
          TabLayout.this.mTabGravity = 0;
          TabLayout.this.updateTabViews(false);
          i = j;
        } 
        if (i != 0)
          super.onMeasure(param1Int1, param1Int2); 
      } 
    }
    
    public void onRtlPropertiesChanged(int param1Int) {
      super.onRtlPropertiesChanged(param1Int);
      if (Build.VERSION.SDK_INT < 23 && this.mLayoutDirection != param1Int) {
        requestLayout();
        this.mLayoutDirection = param1Int;
      } 
    }
    
    void setIndicatorPosition(int param1Int1, int param1Int2) {
      if (param1Int1 != this.mIndicatorLeft || param1Int2 != this.mIndicatorRight) {
        this.mIndicatorLeft = param1Int1;
        this.mIndicatorRight = param1Int2;
        ViewCompat.postInvalidateOnAnimation(this);
      } 
    }
    
    void setIndicatorPositionFromTabPosition(int param1Int, float param1Float) {
      if (this.mIndicatorAnimator != null && this.mIndicatorAnimator.isRunning())
        this.mIndicatorAnimator.cancel(); 
      this.mSelectedPosition = param1Int;
      this.mSelectionOffset = param1Float;
      updateIndicatorPosition();
    }
    
    void setSelectedIndicatorColor(int param1Int) {
      if (this.mSelectedIndicatorPaint.getColor() != param1Int) {
        this.mSelectedIndicatorPaint.setColor(param1Int);
        ViewCompat.postInvalidateOnAnimation(this);
      } 
    }
    
    void setSelectedIndicatorHeight(int param1Int) {
      if (this.mSelectedIndicatorHeight != param1Int) {
        this.mSelectedIndicatorHeight = param1Int;
        ViewCompat.postInvalidateOnAnimation(this);
      } 
    }
  }
  
  class null implements ValueAnimator.AnimatorUpdateListener {
    public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
      float f = param1ValueAnimator.getAnimatedFraction();
      this.this$1.setIndicatorPosition(AnimationUtils.lerp(startLeft, targetLeft, f), AnimationUtils.lerp(startRight, targetRight, f));
    }
  }
  
  class null extends AnimatorListenerAdapter {
    public void onAnimationEnd(Animator param1Animator) {
      this.this$1.mSelectedPosition = position;
      this.this$1.mSelectionOffset = 0.0F;
    }
  }
  
  public static final class Tab {
    public static final int INVALID_POSITION = -1;
    
    private CharSequence mContentDesc;
    
    private View mCustomView;
    
    private Drawable mIcon;
    
    TabLayout mParent;
    
    private int mPosition = -1;
    
    private Object mTag;
    
    private CharSequence mText;
    
    TabLayout.TabView mView;
    
    @Nullable
    public CharSequence getContentDescription() { return this.mContentDesc; }
    
    @Nullable
    public View getCustomView() { return this.mCustomView; }
    
    @Nullable
    public Drawable getIcon() { return this.mIcon; }
    
    public int getPosition() { return this.mPosition; }
    
    @Nullable
    public Object getTag() { return this.mTag; }
    
    @Nullable
    public CharSequence getText() { return this.mText; }
    
    public boolean isSelected() {
      if (this.mParent == null)
        throw new IllegalArgumentException("Tab not attached to a TabLayout"); 
      return (this.mParent.getSelectedTabPosition() == this.mPosition);
    }
    
    void reset() {
      this.mParent = null;
      this.mView = null;
      this.mTag = null;
      this.mIcon = null;
      this.mText = null;
      this.mContentDesc = null;
      this.mPosition = -1;
      this.mCustomView = null;
    }
    
    public void select() {
      if (this.mParent == null)
        throw new IllegalArgumentException("Tab not attached to a TabLayout"); 
      this.mParent.selectTab(this);
    }
    
    @NonNull
    public Tab setContentDescription(@StringRes int param1Int) {
      if (this.mParent == null)
        throw new IllegalArgumentException("Tab not attached to a TabLayout"); 
      return setContentDescription(this.mParent.getResources().getText(param1Int));
    }
    
    @NonNull
    public Tab setContentDescription(@Nullable CharSequence param1CharSequence) {
      this.mContentDesc = param1CharSequence;
      updateView();
      return this;
    }
    
    @NonNull
    public Tab setCustomView(@LayoutRes int param1Int) { return setCustomView(LayoutInflater.from(this.mView.getContext()).inflate(param1Int, this.mView, false)); }
    
    @NonNull
    public Tab setCustomView(@Nullable View param1View) {
      this.mCustomView = param1View;
      updateView();
      return this;
    }
    
    @NonNull
    public Tab setIcon(@DrawableRes int param1Int) {
      if (this.mParent == null)
        throw new IllegalArgumentException("Tab not attached to a TabLayout"); 
      return setIcon(AppCompatResources.getDrawable(this.mParent.getContext(), param1Int));
    }
    
    @NonNull
    public Tab setIcon(@Nullable Drawable param1Drawable) {
      this.mIcon = param1Drawable;
      updateView();
      return this;
    }
    
    void setPosition(int param1Int) { this.mPosition = param1Int; }
    
    @NonNull
    public Tab setTag(@Nullable Object param1Object) {
      this.mTag = param1Object;
      return this;
    }
    
    @NonNull
    public Tab setText(@StringRes int param1Int) {
      if (this.mParent == null)
        throw new IllegalArgumentException("Tab not attached to a TabLayout"); 
      return setText(this.mParent.getResources().getText(param1Int));
    }
    
    @NonNull
    public Tab setText(@Nullable CharSequence param1CharSequence) {
      this.mText = param1CharSequence;
      updateView();
      return this;
    }
    
    void updateView() {
      if (this.mView != null)
        this.mView.update(); 
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface TabGravity {}
  
  public static class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {
    private int mPreviousScrollState;
    
    private int mScrollState;
    
    private final WeakReference<TabLayout> mTabLayoutRef;
    
    public TabLayoutOnPageChangeListener(TabLayout param1TabLayout) { this.mTabLayoutRef = new WeakReference(param1TabLayout); }
    
    public void onPageScrollStateChanged(int param1Int) {
      this.mPreviousScrollState = this.mScrollState;
      this.mScrollState = param1Int;
    }
    
    public void onPageScrolled(int param1Int1, float param1Float, int param1Int2) {
      TabLayout tabLayout = (TabLayout)this.mTabLayoutRef.get();
      if (tabLayout != null) {
        boolean bool1;
        param1Int2 = this.mScrollState;
        boolean bool2 = false;
        if (param1Int2 != 2 || this.mPreviousScrollState == 1) {
          bool1 = true;
        } else {
          bool1 = false;
        } 
        if (this.mScrollState != 2 || this.mPreviousScrollState != 0)
          bool2 = true; 
        tabLayout.setScrollPosition(param1Int1, param1Float, bool1, bool2);
      } 
    }
    
    public void onPageSelected(int param1Int) {
      TabLayout tabLayout = (TabLayout)this.mTabLayoutRef.get();
      if (tabLayout != null && tabLayout.getSelectedTabPosition() != param1Int && param1Int < tabLayout.getTabCount()) {
        boolean bool;
        if (this.mScrollState == 0 || (this.mScrollState == 2 && this.mPreviousScrollState == 0)) {
          bool = true;
        } else {
          bool = false;
        } 
        tabLayout.selectTab(tabLayout.getTabAt(param1Int), bool);
      } 
    }
    
    void reset() {
      this.mScrollState = 0;
      this.mPreviousScrollState = 0;
    }
  }
  
  class TabView extends LinearLayout {
    private ImageView mCustomIconView;
    
    private TextView mCustomTextView;
    
    private View mCustomView;
    
    private int mDefaultMaxLines = 2;
    
    private ImageView mIconView;
    
    private TabLayout.Tab mTab;
    
    private TextView mTextView;
    
    public TabView(Context param1Context) {
      super(param1Context);
      if (TabLayout.this.mTabBackgroundResId != 0)
        ViewCompat.setBackground(this, AppCompatResources.getDrawable(param1Context, TabLayout.this.mTabBackgroundResId)); 
      ViewCompat.setPaddingRelative(this, TabLayout.this.mTabPaddingStart, TabLayout.this.mTabPaddingTop, TabLayout.this.mTabPaddingEnd, TabLayout.this.mTabPaddingBottom);
      setGravity(17);
      setOrientation(1);
      setClickable(true);
      ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), 1002));
    }
    
    private float approximateLineWidth(Layout param1Layout, int param1Int, float param1Float) { return param1Layout.getLineWidth(param1Int) * param1Float / param1Layout.getPaint().getTextSize(); }
    
    private void updateTextAndIcon(@Nullable TextView param1TextView, @Nullable ImageView param1ImageView) {
      CharSequence charSequence;
      Drawable drawable;
      TabLayout.Tab tab1 = this.mTab;
      TabLayout.Tab tab2 = null;
      if (tab1 != null) {
        drawable = this.mTab.getIcon();
      } else {
        drawable = null;
      } 
      if (this.mTab != null) {
        charSequence = this.mTab.getText();
      } else {
        charSequence = null;
      } 
      if (this.mTab != null) {
        CharSequence charSequence1 = this.mTab.getContentDescription();
      } else {
        tab1 = null;
      } 
      byte b = 0;
      if (param1ImageView != null) {
        if (drawable != null) {
          param1ImageView.setImageDrawable(drawable);
          param1ImageView.setVisibility(0);
          setVisibility(0);
        } else {
          param1ImageView.setVisibility(8);
          param1ImageView.setImageDrawable(null);
        } 
        param1ImageView.setContentDescription(tab1);
      } 
      boolean bool = TextUtils.isEmpty(charSequence) ^ true;
      if (param1TextView != null) {
        if (bool) {
          param1TextView.setText(charSequence);
          param1TextView.setVisibility(0);
          setVisibility(0);
        } else {
          param1TextView.setVisibility(8);
          param1TextView.setText(null);
        } 
        param1TextView.setContentDescription(tab1);
      } 
      if (param1ImageView != null) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)param1ImageView.getLayoutParams();
        int i = b;
        if (bool) {
          i = b;
          if (param1ImageView.getVisibility() == 0)
            i = TabLayout.this.dpToPx(8); 
        } 
        if (i != marginLayoutParams.bottomMargin) {
          marginLayoutParams.bottomMargin = i;
          param1ImageView.requestLayout();
        } 
      } 
      if (bool)
        tab1 = tab2; 
      TooltipCompat.setTooltipText(this, tab1);
    }
    
    public TabLayout.Tab getTab() { return this.mTab; }
    
    public void onInitializeAccessibilityEvent(AccessibilityEvent param1AccessibilityEvent) {
      super.onInitializeAccessibilityEvent(param1AccessibilityEvent);
      param1AccessibilityEvent.setClassName(android.support.v7.app.ActionBar.Tab.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo param1AccessibilityNodeInfo) {
      super.onInitializeAccessibilityNodeInfo(param1AccessibilityNodeInfo);
      param1AccessibilityNodeInfo.setClassName(android.support.v7.app.ActionBar.Tab.class.getName());
    }
    
    public void onMeasure(int param1Int1, int param1Int2) { // Byte code:
      //   0: iload_1
      //   1: invokestatic getSize : (I)I
      //   4: istore #6
      //   6: iload_1
      //   7: invokestatic getMode : (I)I
      //   10: istore #7
      //   12: aload_0
      //   13: getfield this$0 : Landroid/support/design/widget/TabLayout;
      //   16: invokevirtual getTabMaxWidth : ()I
      //   19: istore #8
      //   21: iload_1
      //   22: istore #5
      //   24: iload #8
      //   26: ifle -> 58
      //   29: iload #7
      //   31: ifeq -> 44
      //   34: iload_1
      //   35: istore #5
      //   37: iload #6
      //   39: iload #8
      //   41: if_icmple -> 58
      //   44: aload_0
      //   45: getfield this$0 : Landroid/support/design/widget/TabLayout;
      //   48: getfield mTabMaxWidth : I
      //   51: ldc -2147483648
      //   53: invokestatic makeMeasureSpec : (II)I
      //   56: istore #5
      //   58: aload_0
      //   59: iload #5
      //   61: iload_2
      //   62: invokespecial onMeasure : (II)V
      //   65: aload_0
      //   66: getfield mTextView : Landroid/widget/TextView;
      //   69: ifnull -> 323
      //   72: aload_0
      //   73: invokevirtual getResources : ()Landroid/content/res/Resources;
      //   76: pop
      //   77: aload_0
      //   78: getfield this$0 : Landroid/support/design/widget/TabLayout;
      //   81: getfield mTabTextSize : F
      //   84: fstore #4
      //   86: aload_0
      //   87: getfield mDefaultMaxLines : I
      //   90: istore #6
      //   92: aload_0
      //   93: getfield mIconView : Landroid/widget/ImageView;
      //   96: astore #9
      //   98: iconst_1
      //   99: istore #7
      //   101: aload #9
      //   103: ifnull -> 124
      //   106: aload_0
      //   107: getfield mIconView : Landroid/widget/ImageView;
      //   110: invokevirtual getVisibility : ()I
      //   113: ifne -> 124
      //   116: iconst_1
      //   117: istore_1
      //   118: fload #4
      //   120: fstore_3
      //   121: goto -> 165
      //   124: fload #4
      //   126: fstore_3
      //   127: iload #6
      //   129: istore_1
      //   130: aload_0
      //   131: getfield mTextView : Landroid/widget/TextView;
      //   134: ifnull -> 165
      //   137: fload #4
      //   139: fstore_3
      //   140: iload #6
      //   142: istore_1
      //   143: aload_0
      //   144: getfield mTextView : Landroid/widget/TextView;
      //   147: invokevirtual getLineCount : ()I
      //   150: iconst_1
      //   151: if_icmple -> 165
      //   154: aload_0
      //   155: getfield this$0 : Landroid/support/design/widget/TabLayout;
      //   158: getfield mTabTextMultiLineSize : F
      //   161: fstore_3
      //   162: iload #6
      //   164: istore_1
      //   165: aload_0
      //   166: getfield mTextView : Landroid/widget/TextView;
      //   169: invokevirtual getTextSize : ()F
      //   172: fstore #4
      //   174: aload_0
      //   175: getfield mTextView : Landroid/widget/TextView;
      //   178: invokevirtual getLineCount : ()I
      //   181: istore #8
      //   183: aload_0
      //   184: getfield mTextView : Landroid/widget/TextView;
      //   187: invokestatic getMaxLines : (Landroid/widget/TextView;)I
      //   190: istore #6
      //   192: fload_3
      //   193: fload #4
      //   195: fcmpl
      //   196: ifne -> 210
      //   199: iload #6
      //   201: iflt -> 323
      //   204: iload_1
      //   205: iload #6
      //   207: if_icmpeq -> 323
      //   210: iload #7
      //   212: istore #6
      //   214: aload_0
      //   215: getfield this$0 : Landroid/support/design/widget/TabLayout;
      //   218: getfield mMode : I
      //   221: iconst_1
      //   222: if_icmpne -> 294
      //   225: iload #7
      //   227: istore #6
      //   229: fload_3
      //   230: fload #4
      //   232: fcmpl
      //   233: ifle -> 294
      //   236: iload #7
      //   238: istore #6
      //   240: iload #8
      //   242: iconst_1
      //   243: if_icmpne -> 294
      //   246: aload_0
      //   247: getfield mTextView : Landroid/widget/TextView;
      //   250: invokevirtual getLayout : ()Landroid/text/Layout;
      //   253: astore #9
      //   255: aload #9
      //   257: ifnull -> 291
      //   260: iload #7
      //   262: istore #6
      //   264: aload_0
      //   265: aload #9
      //   267: iconst_0
      //   268: fload_3
      //   269: invokespecial approximateLineWidth : (Landroid/text/Layout;IF)F
      //   272: aload_0
      //   273: invokevirtual getMeasuredWidth : ()I
      //   276: aload_0
      //   277: invokevirtual getPaddingLeft : ()I
      //   280: isub
      //   281: aload_0
      //   282: invokevirtual getPaddingRight : ()I
      //   285: isub
      //   286: i2f
      //   287: fcmpl
      //   288: ifle -> 294
      //   291: iconst_0
      //   292: istore #6
      //   294: iload #6
      //   296: ifeq -> 323
      //   299: aload_0
      //   300: getfield mTextView : Landroid/widget/TextView;
      //   303: iconst_0
      //   304: fload_3
      //   305: invokevirtual setTextSize : (IF)V
      //   308: aload_0
      //   309: getfield mTextView : Landroid/widget/TextView;
      //   312: iload_1
      //   313: invokevirtual setMaxLines : (I)V
      //   316: aload_0
      //   317: iload #5
      //   319: iload_2
      //   320: invokespecial onMeasure : (II)V
      //   323: return }
    
    public boolean performClick() {
      boolean bool = super.performClick();
      if (this.mTab != null) {
        if (!bool)
          playSoundEffect(0); 
        this.mTab.select();
        return true;
      } 
      return bool;
    }
    
    void reset() {
      setTab(null);
      setSelected(false);
    }
    
    public void setSelected(boolean param1Boolean) {
      boolean bool;
      if (isSelected() != param1Boolean) {
        bool = true;
      } else {
        bool = false;
      } 
      super.setSelected(param1Boolean);
      if (bool && param1Boolean && Build.VERSION.SDK_INT < 16)
        sendAccessibilityEvent(4); 
      if (this.mTextView != null)
        this.mTextView.setSelected(param1Boolean); 
      if (this.mIconView != null)
        this.mIconView.setSelected(param1Boolean); 
      if (this.mCustomView != null)
        this.mCustomView.setSelected(param1Boolean); 
    }
    
    void setTab(@Nullable TabLayout.Tab param1Tab) {
      if (param1Tab != this.mTab) {
        this.mTab = param1Tab;
        update();
      } 
    }
    
    final void update() {
      TabLayout.Tab tab = this.mTab;
      if (tab != null) {
        view = tab.getCustomView();
      } else {
        view = null;
      } 
      if (view != null) {
        ViewParent viewParent = view.getParent();
        if (viewParent != this) {
          if (viewParent != null)
            ((ViewGroup)viewParent).removeView(view); 
          addView(view);
        } 
        this.mCustomView = view;
        if (this.mTextView != null)
          this.mTextView.setVisibility(8); 
        if (this.mIconView != null) {
          this.mIconView.setVisibility(8);
          this.mIconView.setImageDrawable(null);
        } 
        this.mCustomTextView = (TextView)view.findViewById(16908308);
        if (this.mCustomTextView != null)
          this.mDefaultMaxLines = TextViewCompat.getMaxLines(this.mCustomTextView); 
        this.mCustomIconView = (ImageView)view.findViewById(16908294);
      } else {
        if (this.mCustomView != null) {
          removeView(this.mCustomView);
          this.mCustomView = null;
        } 
        this.mCustomTextView = null;
        this.mCustomIconView = null;
      } 
      View view = this.mCustomView;
      byte b = 0;
      if (view == null) {
        if (this.mIconView == null) {
          ImageView imageView = (ImageView)LayoutInflater.from(getContext()).inflate(R.layout.design_layout_tab_icon, this, false);
          addView(imageView, 0);
          this.mIconView = imageView;
        } 
        if (this.mTextView == null) {
          TextView textView = (TextView)LayoutInflater.from(getContext()).inflate(R.layout.design_layout_tab_text, this, false);
          addView(textView);
          this.mTextView = textView;
          this.mDefaultMaxLines = TextViewCompat.getMaxLines(this.mTextView);
        } 
        TextViewCompat.setTextAppearance(this.mTextView, TabLayout.this.mTabTextAppearance);
        if (TabLayout.this.mTabTextColors != null)
          this.mTextView.setTextColor(TabLayout.this.mTabTextColors); 
        updateTextAndIcon(this.mTextView, this.mIconView);
      } else if (this.mCustomTextView != null || this.mCustomIconView != null) {
        updateTextAndIcon(this.mCustomTextView, this.mCustomIconView);
      } 
      int i = b;
      if (tab != null) {
        i = b;
        if (tab.isSelected())
          i = 1; 
      } 
      setSelected(i);
    }
  }
  
  public static class ViewPagerOnTabSelectedListener implements OnTabSelectedListener {
    private final ViewPager mViewPager;
    
    public ViewPagerOnTabSelectedListener(ViewPager param1ViewPager) { this.mViewPager = param1ViewPager; }
    
    public void onTabReselected(TabLayout.Tab param1Tab) {}
    
    public void onTabSelected(TabLayout.Tab param1Tab) { this.mViewPager.setCurrentItem(param1Tab.getPosition()); }
    
    public void onTabUnselected(TabLayout.Tab param1Tab) {}
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/TabLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */