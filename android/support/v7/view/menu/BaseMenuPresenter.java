package android.support.v7.view.menu;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public abstract class BaseMenuPresenter implements MenuPresenter {
  private MenuPresenter.Callback mCallback;
  
  protected Context mContext;
  
  private int mId;
  
  protected LayoutInflater mInflater;
  
  private int mItemLayoutRes;
  
  protected MenuBuilder mMenu;
  
  private int mMenuLayoutRes;
  
  protected MenuView mMenuView;
  
  protected Context mSystemContext;
  
  protected LayoutInflater mSystemInflater;
  
  public BaseMenuPresenter(Context paramContext, int paramInt1, int paramInt2) {
    this.mSystemContext = paramContext;
    this.mSystemInflater = LayoutInflater.from(paramContext);
    this.mMenuLayoutRes = paramInt1;
    this.mItemLayoutRes = paramInt2;
  }
  
  protected void addItemView(View paramView, int paramInt) {
    ViewGroup viewGroup = (ViewGroup)paramView.getParent();
    if (viewGroup != null)
      viewGroup.removeView(paramView); 
    ((ViewGroup)this.mMenuView).addView(paramView, paramInt);
  }
  
  public abstract void bindItemView(MenuItemImpl paramMenuItemImpl, MenuView.ItemView paramItemView);
  
  public boolean collapseItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl) { return false; }
  
  public MenuView.ItemView createItemView(ViewGroup paramViewGroup) { return (MenuView.ItemView)this.mSystemInflater.inflate(this.mItemLayoutRes, paramViewGroup, false); }
  
  public boolean expandItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl) { return false; }
  
  protected boolean filterLeftoverView(ViewGroup paramViewGroup, int paramInt) {
    paramViewGroup.removeViewAt(paramInt);
    return true;
  }
  
  public boolean flagActionItems() { return false; }
  
  public MenuPresenter.Callback getCallback() { return this.mCallback; }
  
  public int getId() { return this.mId; }
  
  public View getItemView(MenuItemImpl paramMenuItemImpl, View paramView, ViewGroup paramViewGroup) {
    MenuView.ItemView itemView;
    if (paramView instanceof MenuView.ItemView) {
      itemView = (MenuView.ItemView)paramView;
    } else {
      itemView = createItemView(paramViewGroup);
    } 
    bindItemView(paramMenuItemImpl, itemView);
    return (View)itemView;
  }
  
  public MenuView getMenuView(ViewGroup paramViewGroup) {
    if (this.mMenuView == null) {
      this.mMenuView = (MenuView)this.mSystemInflater.inflate(this.mMenuLayoutRes, paramViewGroup, false);
      this.mMenuView.initialize(this.mMenu);
      updateMenuView(true);
    } 
    return this.mMenuView;
  }
  
  public void initForMenu(Context paramContext, MenuBuilder paramMenuBuilder) {
    this.mContext = paramContext;
    this.mInflater = LayoutInflater.from(this.mContext);
    this.mMenu = paramMenuBuilder;
  }
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {
    if (this.mCallback != null)
      this.mCallback.onCloseMenu(paramMenuBuilder, paramBoolean); 
  }
  
  public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder) { return (this.mCallback != null) ? this.mCallback.onOpenSubMenu(paramSubMenuBuilder) : 0; }
  
  public void setCallback(MenuPresenter.Callback paramCallback) { this.mCallback = paramCallback; }
  
  public void setId(int paramInt) { this.mId = paramInt; }
  
  public boolean shouldIncludeItem(int paramInt, MenuItemImpl paramMenuItemImpl) { return true; }
  
  public void updateMenuView(boolean paramBoolean) {
    ViewGroup viewGroup = (ViewGroup)this.mMenuView;
    if (viewGroup == null)
      return; 
    MenuBuilder menuBuilder = this.mMenu;
    byte b = 0;
    if (menuBuilder != null) {
      this.mMenu.flagActionItems();
      ArrayList arrayList = this.mMenu.getVisibleItems();
      int i = arrayList.size();
      byte b1 = 0;
      for (b = 0; b1 < i; b = b2) {
        MenuItemImpl menuItemImpl = (MenuItemImpl)arrayList.get(b1);
        byte b2 = b;
        if (shouldIncludeItem(b, menuItemImpl)) {
          View view1 = viewGroup.getChildAt(b);
          if (view1 instanceof MenuView.ItemView) {
            MenuItemImpl menuItemImpl1 = ((MenuView.ItemView)view1).getItemData();
          } else {
            menuBuilder = null;
          } 
          View view2 = getItemView(menuItemImpl, view1, viewGroup);
          if (menuItemImpl != menuBuilder) {
            view2.setPressed(false);
            view2.jumpDrawablesToCurrentState();
          } 
          if (view2 != view1)
            addItemView(view2, b); 
          b2 = b + 1;
        } 
        b1++;
      } 
    } 
    while (b < viewGroup.getChildCount()) {
      if (!filterLeftoverView(viewGroup, b))
        b++; 
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/view/menu/BaseMenuPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */