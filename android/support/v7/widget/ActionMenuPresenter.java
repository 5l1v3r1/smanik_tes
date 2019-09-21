package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ActionProvider;
import android.support.v7.appcompat.R;
import android.support.v7.view.ActionBarPolicy;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.BaseMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.ShowableListMenu;
import android.support.v7.view.menu.SubMenuBuilder;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

class ActionMenuPresenter extends BaseMenuPresenter implements ActionProvider.SubUiVisibilityListener {
  private static final String TAG = "ActionMenuPresenter";
  
  private final SparseBooleanArray mActionButtonGroups = new SparseBooleanArray();
  
  ActionButtonSubmenu mActionButtonPopup;
  
  private int mActionItemWidthLimit;
  
  private boolean mExpandedActionViewsExclusive;
  
  private int mMaxItems;
  
  private boolean mMaxItemsSet;
  
  private int mMinCellSize;
  
  int mOpenSubMenuId;
  
  OverflowMenuButton mOverflowButton;
  
  OverflowPopup mOverflowPopup;
  
  private Drawable mPendingOverflowIcon;
  
  private boolean mPendingOverflowIconSet;
  
  private ActionMenuPopupCallback mPopupCallback;
  
  final PopupPresenterCallback mPopupPresenterCallback = new PopupPresenterCallback();
  
  OpenOverflowRunnable mPostedOpenRunnable;
  
  private boolean mReserveOverflow;
  
  private boolean mReserveOverflowSet;
  
  private View mScrapActionButtonView;
  
  private boolean mStrictWidthLimit;
  
  private int mWidthLimit;
  
  private boolean mWidthLimitSet;
  
  public ActionMenuPresenter(Context paramContext) { super(paramContext, R.layout.abc_action_menu_layout, R.layout.abc_action_menu_item_layout); }
  
  private View findViewForItem(MenuItem paramMenuItem) {
    ViewGroup viewGroup = (ViewGroup)this.mMenuView;
    if (viewGroup == null)
      return null; 
    int i = viewGroup.getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = viewGroup.getChildAt(b);
      if (view instanceof MenuView.ItemView && ((MenuView.ItemView)view).getItemData() == paramMenuItem)
        return view; 
    } 
    return null;
  }
  
  public void bindItemView(MenuItemImpl paramMenuItemImpl, MenuView.ItemView paramItemView) {
    paramItemView.initialize(paramMenuItemImpl, 0);
    ActionMenuView actionMenuView = (ActionMenuView)this.mMenuView;
    ActionMenuItemView actionMenuItemView = (ActionMenuItemView)paramItemView;
    actionMenuItemView.setItemInvoker(actionMenuView);
    if (this.mPopupCallback == null)
      this.mPopupCallback = new ActionMenuPopupCallback(); 
    actionMenuItemView.setPopupCallback(this.mPopupCallback);
  }
  
  public boolean dismissPopupMenus() { return hideOverflowMenu() | hideSubMenus(); }
  
  public boolean filterLeftoverView(ViewGroup paramViewGroup, int paramInt) { return (paramViewGroup.getChildAt(paramInt) == this.mOverflowButton) ? false : super.filterLeftoverView(paramViewGroup, paramInt); }
  
  public boolean flagActionItems() { // Byte code:
    //   0: aload_0
    //   1: astore #15
    //   3: aload #15
    //   5: getfield mMenu : Landroid/support/v7/view/menu/MenuBuilder;
    //   8: ifnull -> 31
    //   11: aload #15
    //   13: getfield mMenu : Landroid/support/v7/view/menu/MenuBuilder;
    //   16: invokevirtual getVisibleItems : ()Ljava/util/ArrayList;
    //   19: astore #14
    //   21: aload #14
    //   23: invokevirtual size : ()I
    //   26: istore #4
    //   28: goto -> 37
    //   31: aconst_null
    //   32: astore #14
    //   34: iconst_0
    //   35: istore #4
    //   37: aload #15
    //   39: getfield mMaxItems : I
    //   42: istore_1
    //   43: aload #15
    //   45: getfield mActionItemWidthLimit : I
    //   48: istore #8
    //   50: iconst_0
    //   51: iconst_0
    //   52: invokestatic makeMeasureSpec : (II)I
    //   55: istore #10
    //   57: aload #15
    //   59: getfield mMenuView : Landroid/support/v7/view/menu/MenuView;
    //   62: checkcast android/view/ViewGroup
    //   65: astore #16
    //   67: iconst_0
    //   68: istore_3
    //   69: iconst_0
    //   70: istore #5
    //   72: iconst_0
    //   73: istore #6
    //   75: iconst_0
    //   76: istore_2
    //   77: iload_3
    //   78: iload #4
    //   80: if_icmpge -> 164
    //   83: aload #14
    //   85: iload_3
    //   86: invokevirtual get : (I)Ljava/lang/Object;
    //   89: checkcast android/support/v7/view/menu/MenuItemImpl
    //   92: astore #17
    //   94: aload #17
    //   96: invokevirtual requiresActionButton : ()Z
    //   99: ifeq -> 111
    //   102: iload #5
    //   104: iconst_1
    //   105: iadd
    //   106: istore #5
    //   108: goto -> 129
    //   111: aload #17
    //   113: invokevirtual requestsActionButton : ()Z
    //   116: ifeq -> 126
    //   119: iload_2
    //   120: iconst_1
    //   121: iadd
    //   122: istore_2
    //   123: goto -> 129
    //   126: iconst_1
    //   127: istore #6
    //   129: iload_1
    //   130: istore #7
    //   132: aload #15
    //   134: getfield mExpandedActionViewsExclusive : Z
    //   137: ifeq -> 154
    //   140: iload_1
    //   141: istore #7
    //   143: aload #17
    //   145: invokevirtual isActionViewExpanded : ()Z
    //   148: ifeq -> 154
    //   151: iconst_0
    //   152: istore #7
    //   154: iload_3
    //   155: iconst_1
    //   156: iadd
    //   157: istore_3
    //   158: iload #7
    //   160: istore_1
    //   161: goto -> 77
    //   164: iload_1
    //   165: istore_3
    //   166: aload #15
    //   168: getfield mReserveOverflow : Z
    //   171: ifeq -> 193
    //   174: iload #6
    //   176: ifne -> 189
    //   179: iload_1
    //   180: istore_3
    //   181: iload_2
    //   182: iload #5
    //   184: iadd
    //   185: iload_1
    //   186: if_icmple -> 193
    //   189: iload_1
    //   190: iconst_1
    //   191: isub
    //   192: istore_3
    //   193: iload_3
    //   194: iload #5
    //   196: isub
    //   197: istore_1
    //   198: aload #15
    //   200: getfield mActionButtonGroups : Landroid/util/SparseBooleanArray;
    //   203: astore #17
    //   205: aload #17
    //   207: invokevirtual clear : ()V
    //   210: aload #15
    //   212: getfield mStrictWidthLimit : Z
    //   215: ifeq -> 254
    //   218: iload #8
    //   220: aload #15
    //   222: getfield mMinCellSize : I
    //   225: idiv
    //   226: istore_3
    //   227: aload #15
    //   229: getfield mMinCellSize : I
    //   232: istore_2
    //   233: aload #15
    //   235: getfield mMinCellSize : I
    //   238: istore #5
    //   240: iload #8
    //   242: iload_2
    //   243: irem
    //   244: iload_3
    //   245: idiv
    //   246: iload #5
    //   248: iadd
    //   249: istore #6
    //   251: goto -> 259
    //   254: iconst_0
    //   255: istore_3
    //   256: iconst_0
    //   257: istore #6
    //   259: iload #8
    //   261: istore #5
    //   263: iconst_0
    //   264: istore #8
    //   266: iconst_0
    //   267: istore_2
    //   268: iload #4
    //   270: istore #7
    //   272: aload_0
    //   273: astore #15
    //   275: iload #8
    //   277: iload #7
    //   279: if_icmpge -> 806
    //   282: aload #14
    //   284: iload #8
    //   286: invokevirtual get : (I)Ljava/lang/Object;
    //   289: checkcast android/support/v7/view/menu/MenuItemImpl
    //   292: astore #18
    //   294: aload #18
    //   296: invokevirtual requiresActionButton : ()Z
    //   299: ifeq -> 424
    //   302: aload #15
    //   304: aload #18
    //   306: aload #15
    //   308: getfield mScrapActionButtonView : Landroid/view/View;
    //   311: aload #16
    //   313: invokevirtual getItemView : (Landroid/support/v7/view/menu/MenuItemImpl;Landroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    //   316: astore #19
    //   318: aload #15
    //   320: getfield mScrapActionButtonView : Landroid/view/View;
    //   323: ifnonnull -> 333
    //   326: aload #15
    //   328: aload #19
    //   330: putfield mScrapActionButtonView : Landroid/view/View;
    //   333: aload #15
    //   335: getfield mStrictWidthLimit : Z
    //   338: ifeq -> 358
    //   341: iload_3
    //   342: aload #19
    //   344: iload #6
    //   346: iload_3
    //   347: iload #10
    //   349: iconst_0
    //   350: invokestatic measureChildForCells : (Landroid/view/View;IIII)I
    //   353: isub
    //   354: istore_3
    //   355: goto -> 367
    //   358: aload #19
    //   360: iload #10
    //   362: iload #10
    //   364: invokevirtual measure : (II)V
    //   367: aload #19
    //   369: invokevirtual getMeasuredWidth : ()I
    //   372: istore #9
    //   374: iload #5
    //   376: iload #9
    //   378: isub
    //   379: istore #5
    //   381: iload_2
    //   382: istore #4
    //   384: iload_2
    //   385: ifne -> 392
    //   388: iload #9
    //   390: istore #4
    //   392: aload #18
    //   394: invokevirtual getGroupId : ()I
    //   397: istore_2
    //   398: iload_2
    //   399: ifeq -> 412
    //   402: aload #17
    //   404: iload_2
    //   405: iconst_1
    //   406: invokevirtual put : (IZ)V
    //   409: goto -> 412
    //   412: aload #18
    //   414: iconst_1
    //   415: invokevirtual setIsActionButton : (Z)V
    //   418: iload #4
    //   420: istore_2
    //   421: goto -> 797
    //   424: aload #18
    //   426: invokevirtual requestsActionButton : ()Z
    //   429: ifeq -> 791
    //   432: aload #18
    //   434: invokevirtual getGroupId : ()I
    //   437: istore #11
    //   439: aload #17
    //   441: iload #11
    //   443: invokevirtual get : (I)Z
    //   446: istore #13
    //   448: iload_1
    //   449: ifgt -> 457
    //   452: iload #13
    //   454: ifeq -> 480
    //   457: iload #5
    //   459: ifle -> 480
    //   462: aload #15
    //   464: getfield mStrictWidthLimit : Z
    //   467: ifeq -> 474
    //   470: iload_3
    //   471: ifle -> 480
    //   474: iconst_1
    //   475: istore #12
    //   477: goto -> 483
    //   480: iconst_0
    //   481: istore #12
    //   483: iload #12
    //   485: ifeq -> 656
    //   488: aload #15
    //   490: aload #18
    //   492: aload #15
    //   494: getfield mScrapActionButtonView : Landroid/view/View;
    //   497: aload #16
    //   499: invokevirtual getItemView : (Landroid/support/v7/view/menu/MenuItemImpl;Landroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    //   502: astore #19
    //   504: aload #15
    //   506: getfield mScrapActionButtonView : Landroid/view/View;
    //   509: ifnonnull -> 519
    //   512: aload #15
    //   514: aload #19
    //   516: putfield mScrapActionButtonView : Landroid/view/View;
    //   519: aload #15
    //   521: getfield mStrictWidthLimit : Z
    //   524: ifeq -> 563
    //   527: aload #19
    //   529: iload #6
    //   531: iload_3
    //   532: iload #10
    //   534: iconst_0
    //   535: invokestatic measureChildForCells : (Landroid/view/View;IIII)I
    //   538: istore #9
    //   540: iload_3
    //   541: iload #9
    //   543: isub
    //   544: istore #4
    //   546: iload #4
    //   548: istore_3
    //   549: iload #9
    //   551: ifne -> 572
    //   554: iconst_0
    //   555: istore #12
    //   557: iload #4
    //   559: istore_3
    //   560: goto -> 572
    //   563: aload #19
    //   565: iload #10
    //   567: iload #10
    //   569: invokevirtual measure : (II)V
    //   572: aload #19
    //   574: invokevirtual getMeasuredWidth : ()I
    //   577: istore #9
    //   579: iload #5
    //   581: iload #9
    //   583: isub
    //   584: istore #5
    //   586: iload_2
    //   587: istore #4
    //   589: iload_2
    //   590: ifne -> 597
    //   593: iload #9
    //   595: istore #4
    //   597: aload #15
    //   599: getfield mStrictWidthLimit : Z
    //   602: ifeq -> 629
    //   605: iload #5
    //   607: iflt -> 615
    //   610: iconst_1
    //   611: istore_2
    //   612: goto -> 617
    //   615: iconst_0
    //   616: istore_2
    //   617: iload #12
    //   619: iload_2
    //   620: iand
    //   621: istore #12
    //   623: iload #4
    //   625: istore_2
    //   626: goto -> 656
    //   629: iload #5
    //   631: iload #4
    //   633: iadd
    //   634: ifle -> 642
    //   637: iconst_1
    //   638: istore_2
    //   639: goto -> 644
    //   642: iconst_0
    //   643: istore_2
    //   644: iload #12
    //   646: iload_2
    //   647: iand
    //   648: istore #12
    //   650: iload #4
    //   652: istore_2
    //   653: goto -> 656
    //   656: iload #12
    //   658: ifeq -> 680
    //   661: iload #11
    //   663: ifeq -> 680
    //   666: aload #17
    //   668: iload #11
    //   670: iconst_1
    //   671: invokevirtual put : (IZ)V
    //   674: iload_1
    //   675: istore #4
    //   677: goto -> 768
    //   680: iload_1
    //   681: istore #4
    //   683: iload #13
    //   685: ifeq -> 768
    //   688: aload #17
    //   690: iload #11
    //   692: iconst_0
    //   693: invokevirtual put : (IZ)V
    //   696: iconst_0
    //   697: istore #9
    //   699: iload_1
    //   700: istore #4
    //   702: iload #9
    //   704: iload #8
    //   706: if_icmpge -> 768
    //   709: aload #14
    //   711: iload #9
    //   713: invokevirtual get : (I)Ljava/lang/Object;
    //   716: checkcast android/support/v7/view/menu/MenuItemImpl
    //   719: astore #15
    //   721: iload_1
    //   722: istore #4
    //   724: aload #15
    //   726: invokevirtual getGroupId : ()I
    //   729: iload #11
    //   731: if_icmpne -> 756
    //   734: iload_1
    //   735: istore #4
    //   737: aload #15
    //   739: invokevirtual isActionButton : ()Z
    //   742: ifeq -> 750
    //   745: iload_1
    //   746: iconst_1
    //   747: iadd
    //   748: istore #4
    //   750: aload #15
    //   752: iconst_0
    //   753: invokevirtual setIsActionButton : (Z)V
    //   756: iload #9
    //   758: iconst_1
    //   759: iadd
    //   760: istore #9
    //   762: iload #4
    //   764: istore_1
    //   765: goto -> 699
    //   768: iload #4
    //   770: istore_1
    //   771: iload #12
    //   773: ifeq -> 781
    //   776: iload #4
    //   778: iconst_1
    //   779: isub
    //   780: istore_1
    //   781: aload #18
    //   783: iload #12
    //   785: invokevirtual setIsActionButton : (Z)V
    //   788: goto -> 421
    //   791: aload #18
    //   793: iconst_0
    //   794: invokevirtual setIsActionButton : (Z)V
    //   797: iload #8
    //   799: iconst_1
    //   800: iadd
    //   801: istore #8
    //   803: goto -> 272
    //   806: iconst_1
    //   807: ireturn }
  
  public View getItemView(MenuItemImpl paramMenuItemImpl, View paramView, ViewGroup paramViewGroup) {
    byte b;
    View view = paramMenuItemImpl.getActionView();
    if (view == null || paramMenuItemImpl.hasCollapsibleActionView())
      view = super.getItemView(paramMenuItemImpl, paramView, paramViewGroup); 
    if (paramMenuItemImpl.isActionViewExpanded()) {
      b = 8;
    } else {
      b = 0;
    } 
    view.setVisibility(b);
    ActionMenuView actionMenuView = (ActionMenuView)paramViewGroup;
    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
    if (!actionMenuView.checkLayoutParams(layoutParams))
      view.setLayoutParams(actionMenuView.generateLayoutParams(layoutParams)); 
    return view;
  }
  
  public MenuView getMenuView(ViewGroup paramViewGroup) {
    MenuView menuView2 = this.mMenuView;
    MenuView menuView1 = super.getMenuView(paramViewGroup);
    if (menuView2 != menuView1)
      ((ActionMenuView)menuView1).setPresenter(this); 
    return menuView1;
  }
  
  public Drawable getOverflowIcon() { return (this.mOverflowButton != null) ? this.mOverflowButton.getDrawable() : (this.mPendingOverflowIconSet ? this.mPendingOverflowIcon : null); }
  
  public boolean hideOverflowMenu() {
    if (this.mPostedOpenRunnable != null && this.mMenuView != null) {
      ((View)this.mMenuView).removeCallbacks(this.mPostedOpenRunnable);
      this.mPostedOpenRunnable = null;
      return true;
    } 
    OverflowPopup overflowPopup = this.mOverflowPopup;
    if (overflowPopup != null) {
      overflowPopup.dismiss();
      return true;
    } 
    return false;
  }
  
  public boolean hideSubMenus() {
    if (this.mActionButtonPopup != null) {
      this.mActionButtonPopup.dismiss();
      return true;
    } 
    return false;
  }
  
  public void initForMenu(@NonNull Context paramContext, @Nullable MenuBuilder paramMenuBuilder) {
    super.initForMenu(paramContext, paramMenuBuilder);
    Resources resources = paramContext.getResources();
    ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(paramContext);
    if (!this.mReserveOverflowSet)
      this.mReserveOverflow = actionBarPolicy.showsOverflowMenuButton(); 
    if (!this.mWidthLimitSet)
      this.mWidthLimit = actionBarPolicy.getEmbeddedMenuWidthLimit(); 
    if (!this.mMaxItemsSet)
      this.mMaxItems = actionBarPolicy.getMaxActionButtons(); 
    int i = this.mWidthLimit;
    if (this.mReserveOverflow) {
      if (this.mOverflowButton == null) {
        this.mOverflowButton = new OverflowMenuButton(this.mSystemContext);
        if (this.mPendingOverflowIconSet) {
          this.mOverflowButton.setImageDrawable(this.mPendingOverflowIcon);
          this.mPendingOverflowIcon = null;
          this.mPendingOverflowIconSet = false;
        } 
        int j = View.MeasureSpec.makeMeasureSpec(0, 0);
        this.mOverflowButton.measure(j, j);
      } 
      i -= this.mOverflowButton.getMeasuredWidth();
    } else {
      this.mOverflowButton = null;
    } 
    this.mActionItemWidthLimit = i;
    this.mMinCellSize = (int)((resources.getDisplayMetrics()).density * 56.0F);
    this.mScrapActionButtonView = null;
  }
  
  public boolean isOverflowMenuShowPending() { return (this.mPostedOpenRunnable != null || isOverflowMenuShowing()); }
  
  public boolean isOverflowMenuShowing() { return (this.mOverflowPopup != null && this.mOverflowPopup.isShowing()); }
  
  public boolean isOverflowReserved() { return this.mReserveOverflow; }
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {
    dismissPopupMenus();
    super.onCloseMenu(paramMenuBuilder, paramBoolean);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {
    if (!this.mMaxItemsSet)
      this.mMaxItems = ActionBarPolicy.get(this.mContext).getMaxActionButtons(); 
    if (this.mMenu != null)
      this.mMenu.onItemsChanged(true); 
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable) {
    if (!(paramParcelable instanceof SavedState))
      return; 
    paramParcelable = (SavedState)paramParcelable;
    if (paramParcelable.openSubMenuId > 0) {
      MenuItem menuItem = this.mMenu.findItem(paramParcelable.openSubMenuId);
      if (menuItem != null)
        onSubMenuSelected((SubMenuBuilder)menuItem.getSubMenu()); 
    } 
  }
  
  public Parcelable onSaveInstanceState() {
    SavedState savedState = new SavedState();
    savedState.openSubMenuId = this.mOpenSubMenuId;
    return savedState;
  }
  
  public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder) {
    boolean bool = paramSubMenuBuilder.hasVisibleItems();
    boolean bool1 = false;
    if (!bool)
      return false; 
    SubMenuBuilder subMenuBuilder;
    for (subMenuBuilder = paramSubMenuBuilder; subMenuBuilder.getParentMenu() != this.mMenu; subMenuBuilder = (SubMenuBuilder)subMenuBuilder.getParentMenu());
    View view = findViewForItem(subMenuBuilder.getItem());
    if (view == null)
      return false; 
    this.mOpenSubMenuId = paramSubMenuBuilder.getItem().getItemId();
    int i = paramSubMenuBuilder.size();
    byte b = 0;
    while (true) {
      bool = bool1;
      if (b < i) {
        MenuItem menuItem = paramSubMenuBuilder.getItem(b);
        if (menuItem.isVisible() && menuItem.getIcon() != null) {
          bool = true;
          break;
        } 
        b++;
        continue;
      } 
      break;
    } 
    this.mActionButtonPopup = new ActionButtonSubmenu(this.mContext, paramSubMenuBuilder, view);
    this.mActionButtonPopup.setForceShowIcon(bool);
    this.mActionButtonPopup.show();
    super.onSubMenuSelected(paramSubMenuBuilder);
    return true;
  }
  
  public void onSubUiVisibilityChanged(boolean paramBoolean) {
    if (paramBoolean) {
      super.onSubMenuSelected(null);
      return;
    } 
    if (this.mMenu != null)
      this.mMenu.close(false); 
  }
  
  public void setExpandedActionViewsExclusive(boolean paramBoolean) { this.mExpandedActionViewsExclusive = paramBoolean; }
  
  public void setItemLimit(int paramInt) {
    this.mMaxItems = paramInt;
    this.mMaxItemsSet = true;
  }
  
  public void setMenuView(ActionMenuView paramActionMenuView) {
    this.mMenuView = paramActionMenuView;
    paramActionMenuView.initialize(this.mMenu);
  }
  
  public void setOverflowIcon(Drawable paramDrawable) {
    if (this.mOverflowButton != null) {
      this.mOverflowButton.setImageDrawable(paramDrawable);
      return;
    } 
    this.mPendingOverflowIconSet = true;
    this.mPendingOverflowIcon = paramDrawable;
  }
  
  public void setReserveOverflow(boolean paramBoolean) {
    this.mReserveOverflow = paramBoolean;
    this.mReserveOverflowSet = true;
  }
  
  public void setWidthLimit(int paramInt, boolean paramBoolean) {
    this.mWidthLimit = paramInt;
    this.mStrictWidthLimit = paramBoolean;
    this.mWidthLimitSet = true;
  }
  
  public boolean shouldIncludeItem(int paramInt, MenuItemImpl paramMenuItemImpl) { return paramMenuItemImpl.isActionButton(); }
  
  public boolean showOverflowMenu() {
    if (this.mReserveOverflow && !isOverflowMenuShowing() && this.mMenu != null && this.mMenuView != null && this.mPostedOpenRunnable == null && !this.mMenu.getNonActionItems().isEmpty()) {
      this.mPostedOpenRunnable = new OpenOverflowRunnable(new OverflowPopup(this, this.mContext, this.mMenu, this.mOverflowButton, true));
      ((View)this.mMenuView).post(this.mPostedOpenRunnable);
      super.onSubMenuSelected(null);
      return true;
    } 
    return false;
  }
  
  public void updateMenuView(boolean paramBoolean) {
    super.updateMenuView(paramBoolean);
    ((View)this.mMenuView).requestLayout();
    MenuBuilder menuBuilder = this.mMenu;
    byte b2 = 0;
    if (menuBuilder != null) {
      ArrayList arrayList = this.mMenu.getActionItems();
      int i = arrayList.size();
      for (byte b = 0; b < i; b++) {
        ActionProvider actionProvider = ((MenuItemImpl)arrayList.get(b)).getSupportActionProvider();
        if (actionProvider != null)
          actionProvider.setSubUiVisibilityListener(this); 
      } 
    } 
    if (this.mMenu != null) {
      ArrayList arrayList = this.mMenu.getNonActionItems();
    } else {
      menuBuilder = null;
    } 
    byte b1 = b2;
    if (this.mReserveOverflow) {
      b1 = b2;
      if (menuBuilder != null) {
        int i = menuBuilder.size();
        if (i == 1) {
          boolean bool = ((MenuItemImpl)menuBuilder.get(0)).isActionViewExpanded() ^ true;
        } else {
          b1 = b2;
          if (i > 0)
            b1 = 1; 
        } 
      } 
    } 
    if (b1 != 0) {
      if (this.mOverflowButton == null)
        this.mOverflowButton = new OverflowMenuButton(this.mSystemContext); 
      ViewGroup viewGroup = (ViewGroup)this.mOverflowButton.getParent();
      if (viewGroup != this.mMenuView) {
        if (viewGroup != null)
          viewGroup.removeView(this.mOverflowButton); 
        viewGroup = (ActionMenuView)this.mMenuView;
        viewGroup.addView(this.mOverflowButton, viewGroup.generateOverflowButtonLayoutParams());
      } 
    } else if (this.mOverflowButton != null && this.mOverflowButton.getParent() == this.mMenuView) {
      ((ViewGroup)this.mMenuView).removeView(this.mOverflowButton);
    } 
    ((ActionMenuView)this.mMenuView).setOverflowReserved(this.mReserveOverflow);
  }
  
  private class ActionButtonSubmenu extends MenuPopupHelper {
    public ActionButtonSubmenu(Context param1Context, SubMenuBuilder param1SubMenuBuilder, View param1View) {
      super(param1Context, param1SubMenuBuilder, param1View, false, R.attr.actionOverflowMenuStyle);
      if (!((MenuItemImpl)param1SubMenuBuilder.getItem()).isActionButton()) {
        ActionMenuPresenter.OverflowMenuButton overflowMenuButton;
        if (ActionMenuPresenter.this.mOverflowButton == null) {
          overflowMenuButton = (View)this$0.mMenuView;
        } else {
          overflowMenuButton = ActionMenuPresenter.this.mOverflowButton;
        } 
        setAnchorView(overflowMenuButton);
      } 
      setPresenterCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
    }
    
    protected void onDismiss() {
      ActionMenuPresenter.this.mActionButtonPopup = null;
      ActionMenuPresenter.this.mOpenSubMenuId = 0;
      super.onDismiss();
    }
  }
  
  private class ActionMenuPopupCallback extends ActionMenuItemView.PopupCallback {
    public ShowableListMenu getPopup() { return (ActionMenuPresenter.this.mActionButtonPopup != null) ? ActionMenuPresenter.this.mActionButtonPopup.getPopup() : null; }
  }
  
  private class OpenOverflowRunnable implements Runnable {
    private ActionMenuPresenter.OverflowPopup mPopup;
    
    public OpenOverflowRunnable(ActionMenuPresenter.OverflowPopup param1OverflowPopup) { this.mPopup = param1OverflowPopup; }
    
    public void run() {
      if (ActionMenuPresenter.this.mMenu != null)
        ActionMenuPresenter.this.mMenu.changeMenuMode(); 
      View view = (View)ActionMenuPresenter.this.mMenuView;
      if (view != null && view.getWindowToken() != null && this.mPopup.tryShow())
        ActionMenuPresenter.this.mOverflowPopup = this.mPopup; 
      ActionMenuPresenter.this.mPostedOpenRunnable = null;
    }
  }
  
  private class OverflowMenuButton extends AppCompatImageView implements ActionMenuView.ActionMenuChildView {
    private final float[] mTempPts = new float[2];
    
    public OverflowMenuButton(Context param1Context) {
      super(param1Context, null, R.attr.actionOverflowButtonStyle);
      setClickable(true);
      setFocusable(true);
      setVisibility(0);
      setEnabled(true);
      TooltipCompat.setTooltipText(this, getContentDescription());
      setOnTouchListener(new ForwardingListener(this) {
            public ShowableListMenu getPopup() { return (ActionMenuPresenter.this.mOverflowPopup == null) ? null : ActionMenuPresenter.this.mOverflowPopup.getPopup(); }
            
            public boolean onForwardingStarted() {
              ActionMenuPresenter.OverflowMenuButton.this.this$0.showOverflowMenu();
              return true;
            }
            
            public boolean onForwardingStopped() {
              if (ActionMenuPresenter.this.mPostedOpenRunnable != null)
                return false; 
              ActionMenuPresenter.OverflowMenuButton.this.this$0.hideOverflowMenu();
              return true;
            }
          });
    }
    
    public boolean needsDividerAfter() { return false; }
    
    public boolean needsDividerBefore() { return false; }
    
    public boolean performClick() {
      if (super.performClick())
        return true; 
      playSoundEffect(0);
      ActionMenuPresenter.this.showOverflowMenu();
      return true;
    }
    
    protected boolean setFrame(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      boolean bool = super.setFrame(param1Int1, param1Int2, param1Int3, param1Int4);
      Drawable drawable1 = getDrawable();
      Drawable drawable2 = getBackground();
      if (drawable1 != null && drawable2 != null) {
        int i = getWidth();
        param1Int2 = getHeight();
        param1Int1 = Math.max(i, param1Int2) / 2;
        int j = getPaddingLeft();
        int k = getPaddingRight();
        param1Int3 = getPaddingTop();
        param1Int4 = getPaddingBottom();
        i = (i + j - k) / 2;
        param1Int2 = (param1Int2 + param1Int3 - param1Int4) / 2;
        DrawableCompat.setHotspotBounds(drawable2, i - param1Int1, param1Int2 - param1Int1, i + param1Int1, param1Int2 + param1Int1);
      } 
      return bool;
    }
  }
  
  class null extends ForwardingListener {
    null(View param1View) { super(param1View); }
    
    public ShowableListMenu getPopup() { return (ActionMenuPresenter.this.mOverflowPopup == null) ? null : ActionMenuPresenter.this.mOverflowPopup.getPopup(); }
    
    public boolean onForwardingStarted() {
      this.this$1.this$0.showOverflowMenu();
      return true;
    }
    
    public boolean onForwardingStopped() {
      if (ActionMenuPresenter.this.mPostedOpenRunnable != null)
        return false; 
      this.this$1.this$0.hideOverflowMenu();
      return true;
    }
  }
  
  private class OverflowPopup extends MenuPopupHelper {
    public OverflowPopup(Context param1Context, MenuBuilder param1MenuBuilder, View param1View, boolean param1Boolean) {
      super(param1Context, param1MenuBuilder, param1View, param1Boolean, R.attr.actionOverflowMenuStyle);
      setGravity(8388613);
      setPresenterCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
    }
    
    protected void onDismiss() {
      if (ActionMenuPresenter.this.mMenu != null)
        ActionMenuPresenter.this.mMenu.close(); 
      ActionMenuPresenter.this.mOverflowPopup = null;
      super.onDismiss();
    }
  }
  
  private class PopupPresenterCallback implements MenuPresenter.Callback {
    public void onCloseMenu(MenuBuilder param1MenuBuilder, boolean param1Boolean) {
      if (param1MenuBuilder instanceof SubMenuBuilder)
        param1MenuBuilder.getRootMenu().close(false); 
      MenuPresenter.Callback callback = ActionMenuPresenter.this.getCallback();
      if (callback != null)
        callback.onCloseMenu(param1MenuBuilder, param1Boolean); 
    }
    
    public boolean onOpenSubMenu(MenuBuilder param1MenuBuilder) {
      boolean bool = false;
      if (param1MenuBuilder == null)
        return false; 
      ActionMenuPresenter.this.mOpenSubMenuId = ((SubMenuBuilder)param1MenuBuilder).getItem().getItemId();
      MenuPresenter.Callback callback = ActionMenuPresenter.this.getCallback();
      if (callback != null)
        bool = callback.onOpenSubMenu(param1MenuBuilder); 
      return bool;
    }
  }
  
  private static class SavedState implements Parcelable {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
        public ActionMenuPresenter.SavedState createFromParcel(Parcel param2Parcel) { return new ActionMenuPresenter.SavedState(param2Parcel); }
        
        public ActionMenuPresenter.SavedState[] newArray(int param2Int) { return new ActionMenuPresenter.SavedState[param2Int]; }
      };
    
    public int openSubMenuId;
    
    SavedState() {}
    
    SavedState(Parcel param1Parcel) { this.openSubMenuId = param1Parcel.readInt(); }
    
    public int describeContents() { return 0; }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) { param1Parcel.writeInt(this.openSubMenuId); }
  }
  
  static final class null extends Object implements Parcelable.Creator<SavedState> {
    public ActionMenuPresenter.SavedState createFromParcel(Parcel param1Parcel) { return new ActionMenuPresenter.SavedState(param1Parcel); }
    
    public ActionMenuPresenter.SavedState[] newArray(int param1Int) { return new ActionMenuPresenter.SavedState[param1Int]; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/ActionMenuPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */