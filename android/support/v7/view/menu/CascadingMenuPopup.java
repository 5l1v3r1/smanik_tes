package android.support.v7.view.menu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.widget.MenuItemHoverListener;
import android.support.v7.widget.MenuPopupWindow;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class CascadingMenuPopup extends MenuPopup implements MenuPresenter, View.OnKeyListener, PopupWindow.OnDismissListener {
  static final int HORIZ_POSITION_LEFT = 0;
  
  static final int HORIZ_POSITION_RIGHT = 1;
  
  static final int SUBMENU_TIMEOUT_MS = 200;
  
  private View mAnchorView;
  
  private final View.OnAttachStateChangeListener mAttachStateChangeListener = new View.OnAttachStateChangeListener() {
      public void onViewAttachedToWindow(View param1View) {}
      
      public void onViewDetachedFromWindow(View param1View) {
        if (CascadingMenuPopup.this.mTreeObserver != null) {
          if (!CascadingMenuPopup.this.mTreeObserver.isAlive())
            CascadingMenuPopup.access$002(CascadingMenuPopup.this, param1View.getViewTreeObserver()); 
          CascadingMenuPopup.this.mTreeObserver.removeGlobalOnLayoutListener(CascadingMenuPopup.this.mGlobalLayoutListener);
        } 
        param1View.removeOnAttachStateChangeListener(this);
      }
    };
  
  private final Context mContext;
  
  private int mDropDownGravity = 0;
  
  private boolean mForceShowIcon;
  
  private final ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
      public void onGlobalLayout() {
        if (CascadingMenuPopup.this.isShowing() && CascadingMenuPopup.this.mShowingMenus.size() > 0 && !((CascadingMenuPopup.CascadingMenuInfo)this.this$0.mShowingMenus.get(0)).window.isModal()) {
          View view = CascadingMenuPopup.this.mShownAnchorView;
          if (view == null || !view.isShown()) {
            CascadingMenuPopup.this.dismiss();
            return;
          } 
          Iterator iterator = CascadingMenuPopup.this.mShowingMenus.iterator();
          while (iterator.hasNext())
            ((CascadingMenuPopup.CascadingMenuInfo)iterator.next()).window.show(); 
        } 
      }
    };
  
  private boolean mHasXOffset;
  
  private boolean mHasYOffset;
  
  private int mLastPosition;
  
  private final MenuItemHoverListener mMenuItemHoverListener = new MenuItemHoverListener() {
      public void onItemHoverEnter(@NonNull MenuBuilder param1MenuBuilder, @NonNull MenuItem param1MenuItem) { // Byte code:
        //   0: aload_0
        //   1: getfield this$0 : Landroid/support/v7/view/menu/CascadingMenuPopup;
        //   4: getfield mSubMenuHoverHandler : Landroid/os/Handler;
        //   7: astore #8
        //   9: aconst_null
        //   10: astore #7
        //   12: aload #8
        //   14: aconst_null
        //   15: invokevirtual removeCallbacksAndMessages : (Ljava/lang/Object;)V
        //   18: aload_0
        //   19: getfield this$0 : Landroid/support/v7/view/menu/CascadingMenuPopup;
        //   22: getfield mShowingMenus : Ljava/util/List;
        //   25: invokeinterface size : ()I
        //   30: istore #4
        //   32: iconst_0
        //   33: istore_3
        //   34: iload_3
        //   35: iload #4
        //   37: if_icmpge -> 73
        //   40: aload_1
        //   41: aload_0
        //   42: getfield this$0 : Landroid/support/v7/view/menu/CascadingMenuPopup;
        //   45: getfield mShowingMenus : Ljava/util/List;
        //   48: iload_3
        //   49: invokeinterface get : (I)Ljava/lang/Object;
        //   54: checkcast android/support/v7/view/menu/CascadingMenuPopup$CascadingMenuInfo
        //   57: getfield menu : Landroid/support/v7/view/menu/MenuBuilder;
        //   60: if_acmpne -> 66
        //   63: goto -> 75
        //   66: iload_3
        //   67: iconst_1
        //   68: iadd
        //   69: istore_3
        //   70: goto -> 34
        //   73: iconst_m1
        //   74: istore_3
        //   75: iload_3
        //   76: iconst_m1
        //   77: if_icmpne -> 81
        //   80: return
        //   81: iload_3
        //   82: iconst_1
        //   83: iadd
        //   84: istore_3
        //   85: iload_3
        //   86: aload_0
        //   87: getfield this$0 : Landroid/support/v7/view/menu/CascadingMenuPopup;
        //   90: getfield mShowingMenus : Ljava/util/List;
        //   93: invokeinterface size : ()I
        //   98: if_icmpge -> 119
        //   101: aload_0
        //   102: getfield this$0 : Landroid/support/v7/view/menu/CascadingMenuPopup;
        //   105: getfield mShowingMenus : Ljava/util/List;
        //   108: iload_3
        //   109: invokeinterface get : (I)Ljava/lang/Object;
        //   114: checkcast android/support/v7/view/menu/CascadingMenuPopup$CascadingMenuInfo
        //   117: astore #7
        //   119: new android/support/v7/view/menu/CascadingMenuPopup$3$1
        //   122: dup
        //   123: aload_0
        //   124: aload #7
        //   126: aload_2
        //   127: aload_1
        //   128: invokespecial <init> : (Landroid/support/v7/view/menu/CascadingMenuPopup$3;Landroid/support/v7/view/menu/CascadingMenuPopup$CascadingMenuInfo;Landroid/view/MenuItem;Landroid/support/v7/view/menu/MenuBuilder;)V
        //   131: astore_2
        //   132: invokestatic uptimeMillis : ()J
        //   135: lstore #5
        //   137: aload_0
        //   138: getfield this$0 : Landroid/support/v7/view/menu/CascadingMenuPopup;
        //   141: getfield mSubMenuHoverHandler : Landroid/os/Handler;
        //   144: aload_2
        //   145: aload_1
        //   146: lload #5
        //   148: ldc2_w 200
        //   151: ladd
        //   152: invokevirtual postAtTime : (Ljava/lang/Runnable;Ljava/lang/Object;J)Z
        //   155: pop
        //   156: return }
      
      public void onItemHoverExit(@NonNull MenuBuilder param1MenuBuilder, @NonNull MenuItem param1MenuItem) { CascadingMenuPopup.this.mSubMenuHoverHandler.removeCallbacksAndMessages(param1MenuBuilder); }
    };
  
  private final int mMenuMaxWidth;
  
  private PopupWindow.OnDismissListener mOnDismissListener;
  
  private final boolean mOverflowOnly;
  
  private final List<MenuBuilder> mPendingMenus = new ArrayList();
  
  private final int mPopupStyleAttr;
  
  private final int mPopupStyleRes;
  
  private MenuPresenter.Callback mPresenterCallback;
  
  private int mRawDropDownGravity = 0;
  
  boolean mShouldCloseImmediately;
  
  private boolean mShowTitle;
  
  final List<CascadingMenuInfo> mShowingMenus = new ArrayList();
  
  View mShownAnchorView;
  
  final Handler mSubMenuHoverHandler;
  
  private ViewTreeObserver mTreeObserver;
  
  private int mXOffset;
  
  private int mYOffset;
  
  public CascadingMenuPopup(@NonNull Context paramContext, @NonNull View paramView, @AttrRes int paramInt1, @StyleRes int paramInt2, boolean paramBoolean) {
    this.mContext = paramContext;
    this.mAnchorView = paramView;
    this.mPopupStyleAttr = paramInt1;
    this.mPopupStyleRes = paramInt2;
    this.mOverflowOnly = paramBoolean;
    this.mForceShowIcon = false;
    this.mLastPosition = getInitialMenuPosition();
    Resources resources = paramContext.getResources();
    this.mMenuMaxWidth = Math.max((resources.getDisplayMetrics()).widthPixels / 2, resources.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
    this.mSubMenuHoverHandler = new Handler();
  }
  
  private MenuPopupWindow createPopupWindow() {
    MenuPopupWindow menuPopupWindow = new MenuPopupWindow(this.mContext, null, this.mPopupStyleAttr, this.mPopupStyleRes);
    menuPopupWindow.setHoverListener(this.mMenuItemHoverListener);
    menuPopupWindow.setOnItemClickListener(this);
    menuPopupWindow.setOnDismissListener(this);
    menuPopupWindow.setAnchorView(this.mAnchorView);
    menuPopupWindow.setDropDownGravity(this.mDropDownGravity);
    menuPopupWindow.setModal(true);
    menuPopupWindow.setInputMethodMode(2);
    return menuPopupWindow;
  }
  
  private int findIndexOfAddedMenu(@NonNull MenuBuilder paramMenuBuilder) {
    int i = this.mShowingMenus.size();
    for (byte b = 0; b < i; b++) {
      if (paramMenuBuilder == ((CascadingMenuInfo)this.mShowingMenus.get(b)).menu)
        return b; 
    } 
    return -1;
  }
  
  private MenuItem findMenuItemForSubmenu(@NonNull MenuBuilder paramMenuBuilder1, @NonNull MenuBuilder paramMenuBuilder2) {
    int i = paramMenuBuilder1.size();
    for (byte b = 0; b < i; b++) {
      MenuItem menuItem = paramMenuBuilder1.getItem(b);
      if (menuItem.hasSubMenu() && paramMenuBuilder2 == menuItem.getSubMenu())
        return menuItem; 
    } 
    return null;
  }
  
  @Nullable
  private View findParentViewForSubmenu(@NonNull CascadingMenuInfo paramCascadingMenuInfo, @NonNull MenuBuilder paramMenuBuilder) { // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: getfield menu : Landroid/support/v7/view/menu/MenuBuilder;
    //   5: aload_2
    //   6: invokespecial findMenuItemForSubmenu : (Landroid/support/v7/view/menu/MenuBuilder;Landroid/support/v7/view/menu/MenuBuilder;)Landroid/view/MenuItem;
    //   9: astore_2
    //   10: aload_2
    //   11: ifnonnull -> 16
    //   14: aconst_null
    //   15: areturn
    //   16: aload_1
    //   17: invokevirtual getListView : ()Landroid/widget/ListView;
    //   20: astore #7
    //   22: aload #7
    //   24: invokevirtual getAdapter : ()Landroid/widget/ListAdapter;
    //   27: astore_1
    //   28: aload_1
    //   29: instanceof android/widget/HeaderViewListAdapter
    //   32: istore #6
    //   34: iconst_0
    //   35: istore_3
    //   36: iload #6
    //   38: ifeq -> 63
    //   41: aload_1
    //   42: checkcast android/widget/HeaderViewListAdapter
    //   45: astore_1
    //   46: aload_1
    //   47: invokevirtual getHeadersCount : ()I
    //   50: istore #4
    //   52: aload_1
    //   53: invokevirtual getWrappedAdapter : ()Landroid/widget/ListAdapter;
    //   56: checkcast android/support/v7/view/menu/MenuAdapter
    //   59: astore_1
    //   60: goto -> 71
    //   63: aload_1
    //   64: checkcast android/support/v7/view/menu/MenuAdapter
    //   67: astore_1
    //   68: iconst_0
    //   69: istore #4
    //   71: aload_1
    //   72: invokevirtual getCount : ()I
    //   75: istore #5
    //   77: iload_3
    //   78: iload #5
    //   80: if_icmpge -> 102
    //   83: aload_2
    //   84: aload_1
    //   85: iload_3
    //   86: invokevirtual getItem : (I)Landroid/support/v7/view/menu/MenuItemImpl;
    //   89: if_acmpne -> 95
    //   92: goto -> 104
    //   95: iload_3
    //   96: iconst_1
    //   97: iadd
    //   98: istore_3
    //   99: goto -> 77
    //   102: iconst_m1
    //   103: istore_3
    //   104: iload_3
    //   105: iconst_m1
    //   106: if_icmpne -> 111
    //   109: aconst_null
    //   110: areturn
    //   111: iload_3
    //   112: iload #4
    //   114: iadd
    //   115: aload #7
    //   117: invokevirtual getFirstVisiblePosition : ()I
    //   120: isub
    //   121: istore_3
    //   122: iload_3
    //   123: iflt -> 144
    //   126: iload_3
    //   127: aload #7
    //   129: invokevirtual getChildCount : ()I
    //   132: if_icmplt -> 137
    //   135: aconst_null
    //   136: areturn
    //   137: aload #7
    //   139: iload_3
    //   140: invokevirtual getChildAt : (I)Landroid/view/View;
    //   143: areturn
    //   144: aconst_null
    //   145: areturn }
  
  private int getInitialMenuPosition() {
    int i = ViewCompat.getLayoutDirection(this.mAnchorView);
    byte b = 1;
    if (i == 1)
      b = 0; 
    return b;
  }
  
  private int getNextMenuPosition(int paramInt) {
    ListView listView = ((CascadingMenuInfo)this.mShowingMenus.get(this.mShowingMenus.size() - 1)).getListView();
    int[] arrayOfInt = new int[2];
    listView.getLocationOnScreen(arrayOfInt);
    Rect rect = new Rect();
    this.mShownAnchorView.getWindowVisibleDisplayFrame(rect);
    return (this.mLastPosition == 1) ? ((arrayOfInt[0] + listView.getWidth() + paramInt > rect.right) ? 0 : 1) : ((arrayOfInt[0] - paramInt < 0) ? 1 : 0);
  }
  
  private void showMenu(@NonNull MenuBuilder paramMenuBuilder) {
    MenuAdapter menuAdapter2;
    LayoutInflater layoutInflater = LayoutInflater.from(this.mContext);
    MenuAdapter menuAdapter1 = new MenuAdapter(paramMenuBuilder, layoutInflater, this.mOverflowOnly);
    if (!isShowing() && this.mForceShowIcon) {
      menuAdapter1.setForceShowIcon(true);
    } else if (isShowing()) {
      menuAdapter1.setForceShowIcon(MenuPopup.shouldPreserveIconSpacing(paramMenuBuilder));
    } 
    int i = measureIndividualMenuWidth(menuAdapter1, null, this.mContext, this.mMenuMaxWidth);
    MenuPopupWindow menuPopupWindow = createPopupWindow();
    menuPopupWindow.setAdapter(menuAdapter1);
    menuPopupWindow.setContentWidth(i);
    menuPopupWindow.setDropDownGravity(this.mDropDownGravity);
    if (this.mShowingMenus.size() > 0) {
      CascadingMenuInfo cascadingMenuInfo1 = (CascadingMenuInfo)this.mShowingMenus.get(this.mShowingMenus.size() - 1);
      menuAdapter2 = findParentViewForSubmenu(cascadingMenuInfo1, paramMenuBuilder);
    } else {
      menuAdapter1 = null;
      menuAdapter2 = menuAdapter1;
    } 
    if (menuAdapter2 != null) {
      int m;
      int j;
      menuPopupWindow.setTouchModal(false);
      menuPopupWindow.setEnterTransition(null);
      int k = getNextMenuPosition(i);
      if (k == 1) {
        j = 1;
      } else {
        j = 0;
      } 
      this.mLastPosition = k;
      if (Build.VERSION.SDK_INT >= 26) {
        menuPopupWindow.setAnchorView(menuAdapter2);
        k = 0;
        m = 0;
      } else {
        int[] arrayOfInt1 = new int[2];
        this.mAnchorView.getLocationOnScreen(arrayOfInt1);
        int[] arrayOfInt2 = new int[2];
        menuAdapter2.getLocationOnScreen(arrayOfInt2);
        if ((this.mDropDownGravity & 0x7) == 5) {
          arrayOfInt1[0] = arrayOfInt1[0] + this.mAnchorView.getWidth();
          arrayOfInt2[0] = arrayOfInt2[0] + menuAdapter2.getWidth();
        } 
        m = arrayOfInt2[0] - arrayOfInt1[0];
        k = arrayOfInt2[1] - arrayOfInt1[1];
      } 
      if ((this.mDropDownGravity & 0x5) == 5) {
        if (j) {
          j = m + i;
        } else {
          j = m - menuAdapter2.getWidth();
        } 
      } else if (j != 0) {
        j = m + menuAdapter2.getWidth();
      } else {
        j = m - i;
      } 
      menuPopupWindow.setHorizontalOffset(j);
      menuPopupWindow.setOverlapAnchor(true);
      menuPopupWindow.setVerticalOffset(k);
    } else {
      if (this.mHasXOffset)
        menuPopupWindow.setHorizontalOffset(this.mXOffset); 
      if (this.mHasYOffset)
        menuPopupWindow.setVerticalOffset(this.mYOffset); 
      menuPopupWindow.setEpicenterBounds(getEpicenterBounds());
    } 
    CascadingMenuInfo cascadingMenuInfo = new CascadingMenuInfo(menuPopupWindow, paramMenuBuilder, this.mLastPosition);
    this.mShowingMenus.add(cascadingMenuInfo);
    menuPopupWindow.show();
    ListView listView = menuPopupWindow.getListView();
    listView.setOnKeyListener(this);
    if (menuAdapter1 == null && this.mShowTitle && paramMenuBuilder.getHeaderTitle() != null) {
      FrameLayout frameLayout = (FrameLayout)layoutInflater.inflate(R.layout.abc_popup_menu_header_item_layout, listView, false);
      TextView textView = (TextView)frameLayout.findViewById(16908310);
      frameLayout.setEnabled(false);
      textView.setText(paramMenuBuilder.getHeaderTitle());
      listView.addHeaderView(frameLayout, null, false);
      menuPopupWindow.show();
    } 
  }
  
  public void addMenu(MenuBuilder paramMenuBuilder) {
    paramMenuBuilder.addMenuPresenter(this, this.mContext);
    if (isShowing()) {
      showMenu(paramMenuBuilder);
      return;
    } 
    this.mPendingMenus.add(paramMenuBuilder);
  }
  
  protected boolean closeMenuOnSubMenuOpened() { return false; }
  
  public void dismiss() {
    int i = this.mShowingMenus.size();
    if (i > 0) {
      CascadingMenuInfo[] arrayOfCascadingMenuInfo = (CascadingMenuInfo[])this.mShowingMenus.toArray(new CascadingMenuInfo[i]);
      while (--i >= 0) {
        CascadingMenuInfo cascadingMenuInfo = arrayOfCascadingMenuInfo[i];
        if (cascadingMenuInfo.window.isShowing())
          cascadingMenuInfo.window.dismiss(); 
        i--;
      } 
    } 
  }
  
  public boolean flagActionItems() { return false; }
  
  public ListView getListView() { return this.mShowingMenus.isEmpty() ? null : ((CascadingMenuInfo)this.mShowingMenus.get(this.mShowingMenus.size() - 1)).getListView(); }
  
  public boolean isShowing() {
    int i = this.mShowingMenus.size();
    byte b = 0;
    int j = b;
    if (i > 0) {
      j = b;
      if (((CascadingMenuInfo)this.mShowingMenus.get(0)).window.isShowing())
        j = 1; 
    } 
    return j;
  }
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {
    int i = findIndexOfAddedMenu(paramMenuBuilder);
    if (i < 0)
      return; 
    int j = i + 1;
    if (j < this.mShowingMenus.size())
      ((CascadingMenuInfo)this.mShowingMenus.get(j)).menu.close(false); 
    CascadingMenuInfo cascadingMenuInfo = (CascadingMenuInfo)this.mShowingMenus.remove(i);
    cascadingMenuInfo.menu.removeMenuPresenter(this);
    if (this.mShouldCloseImmediately) {
      cascadingMenuInfo.window.setExitTransition(null);
      cascadingMenuInfo.window.setAnimationStyle(0);
    } 
    cascadingMenuInfo.window.dismiss();
    i = this.mShowingMenus.size();
    if (i > 0) {
      this.mLastPosition = ((CascadingMenuInfo)this.mShowingMenus.get(i - 1)).position;
    } else {
      this.mLastPosition = getInitialMenuPosition();
    } 
    if (i == 0) {
      dismiss();
      if (this.mPresenterCallback != null)
        this.mPresenterCallback.onCloseMenu(paramMenuBuilder, true); 
      if (this.mTreeObserver != null) {
        if (this.mTreeObserver.isAlive())
          this.mTreeObserver.removeGlobalOnLayoutListener(this.mGlobalLayoutListener); 
        this.mTreeObserver = null;
      } 
      this.mShownAnchorView.removeOnAttachStateChangeListener(this.mAttachStateChangeListener);
      this.mOnDismissListener.onDismiss();
      return;
    } 
    if (paramBoolean)
      ((CascadingMenuInfo)this.mShowingMenus.get(0)).menu.close(false); 
  }
  
  public void onDismiss() { // Byte code:
    //   0: aload_0
    //   1: getfield mShowingMenus : Ljava/util/List;
    //   4: invokeinterface size : ()I
    //   9: istore_2
    //   10: iconst_0
    //   11: istore_1
    //   12: iload_1
    //   13: iload_2
    //   14: if_icmpge -> 51
    //   17: aload_0
    //   18: getfield mShowingMenus : Ljava/util/List;
    //   21: iload_1
    //   22: invokeinterface get : (I)Ljava/lang/Object;
    //   27: checkcast android/support/v7/view/menu/CascadingMenuPopup$CascadingMenuInfo
    //   30: astore_3
    //   31: aload_3
    //   32: getfield window : Landroid/support/v7/widget/MenuPopupWindow;
    //   35: invokevirtual isShowing : ()Z
    //   38: ifne -> 44
    //   41: goto -> 53
    //   44: iload_1
    //   45: iconst_1
    //   46: iadd
    //   47: istore_1
    //   48: goto -> 12
    //   51: aconst_null
    //   52: astore_3
    //   53: aload_3
    //   54: ifnull -> 65
    //   57: aload_3
    //   58: getfield menu : Landroid/support/v7/view/menu/MenuBuilder;
    //   61: iconst_0
    //   62: invokevirtual close : (Z)V
    //   65: return }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent) {
    if (paramKeyEvent.getAction() == 1 && paramInt == 82) {
      dismiss();
      return true;
    } 
    return false;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable) {}
  
  public Parcelable onSaveInstanceState() { return null; }
  
  public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder) {
    for (CascadingMenuInfo cascadingMenuInfo : this.mShowingMenus) {
      if (paramSubMenuBuilder == cascadingMenuInfo.menu) {
        cascadingMenuInfo.getListView().requestFocus();
        return true;
      } 
    } 
    if (paramSubMenuBuilder.hasVisibleItems()) {
      addMenu(paramSubMenuBuilder);
      if (this.mPresenterCallback != null)
        this.mPresenterCallback.onOpenSubMenu(paramSubMenuBuilder); 
      return true;
    } 
    return false;
  }
  
  public void setAnchorView(@NonNull View paramView) {
    if (this.mAnchorView != paramView) {
      this.mAnchorView = paramView;
      this.mDropDownGravity = GravityCompat.getAbsoluteGravity(this.mRawDropDownGravity, ViewCompat.getLayoutDirection(this.mAnchorView));
    } 
  }
  
  public void setCallback(MenuPresenter.Callback paramCallback) { this.mPresenterCallback = paramCallback; }
  
  public void setForceShowIcon(boolean paramBoolean) { this.mForceShowIcon = paramBoolean; }
  
  public void setGravity(int paramInt) {
    if (this.mRawDropDownGravity != paramInt) {
      this.mRawDropDownGravity = paramInt;
      this.mDropDownGravity = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this.mAnchorView));
    } 
  }
  
  public void setHorizontalOffset(int paramInt) {
    this.mHasXOffset = true;
    this.mXOffset = paramInt;
  }
  
  public void setOnDismissListener(PopupWindow.OnDismissListener paramOnDismissListener) { this.mOnDismissListener = paramOnDismissListener; }
  
  public void setShowTitle(boolean paramBoolean) { this.mShowTitle = paramBoolean; }
  
  public void setVerticalOffset(int paramInt) {
    this.mHasYOffset = true;
    this.mYOffset = paramInt;
  }
  
  public void show() {
    if (isShowing())
      return; 
    Iterator iterator = this.mPendingMenus.iterator();
    while (iterator.hasNext())
      showMenu((MenuBuilder)iterator.next()); 
    this.mPendingMenus.clear();
    this.mShownAnchorView = this.mAnchorView;
    if (this.mShownAnchorView != null) {
      boolean bool;
      if (this.mTreeObserver == null) {
        bool = true;
      } else {
        bool = false;
      } 
      this.mTreeObserver = this.mShownAnchorView.getViewTreeObserver();
      if (bool)
        this.mTreeObserver.addOnGlobalLayoutListener(this.mGlobalLayoutListener); 
      this.mShownAnchorView.addOnAttachStateChangeListener(this.mAttachStateChangeListener);
    } 
  }
  
  public void updateMenuView(boolean paramBoolean) {
    Iterator iterator = this.mShowingMenus.iterator();
    while (iterator.hasNext())
      toMenuAdapter(((CascadingMenuInfo)iterator.next()).getListView().getAdapter()).notifyDataSetChanged(); 
  }
  
  private static class CascadingMenuInfo {
    public final MenuBuilder menu;
    
    public final int position;
    
    public final MenuPopupWindow window;
    
    public CascadingMenuInfo(@NonNull MenuPopupWindow param1MenuPopupWindow, @NonNull MenuBuilder param1MenuBuilder, int param1Int) {
      this.window = param1MenuPopupWindow;
      this.menu = param1MenuBuilder;
      this.position = param1Int;
    }
    
    public ListView getListView() { return this.window.getListView(); }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface HorizPosition {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/view/menu/CascadingMenuPopup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */