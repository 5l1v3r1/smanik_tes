package android.support.v7.view.menu;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

abstract class MenuPopup implements ShowableListMenu, MenuPresenter, AdapterView.OnItemClickListener {
  private Rect mEpicenterBounds;
  
  protected static int measureIndividualMenuWidth(ListAdapter paramListAdapter, ViewGroup paramViewGroup, Context paramContext, int paramInt) {
    byte b = 0;
    int k = View.MeasureSpec.makeMeasureSpec(0, 0);
    int m = View.MeasureSpec.makeMeasureSpec(0, 0);
    int n = paramListAdapter.getCount();
    ViewGroup viewGroup = paramViewGroup;
    paramViewGroup = null;
    int j = 0;
    int i = 0;
    while (b < n) {
      int i2 = paramListAdapter.getItemViewType(b);
      int i1 = j;
      if (i2 != j) {
        paramViewGroup = null;
        i1 = i2;
      } 
      FrameLayout frameLayout2 = viewGroup;
      if (viewGroup == null)
        frameLayout2 = new FrameLayout(paramContext); 
      View view = paramListAdapter.getView(b, paramViewGroup, frameLayout2);
      view.measure(k, m);
      i2 = view.getMeasuredWidth();
      if (i2 >= paramInt)
        return paramInt; 
      j = i;
      if (i2 > i)
        j = i2; 
      b++;
      i = j;
      FrameLayout frameLayout1 = frameLayout2;
      j = i1;
    } 
    return i;
  }
  
  protected static boolean shouldPreserveIconSpacing(MenuBuilder paramMenuBuilder) {
    int i = paramMenuBuilder.size();
    for (byte b = 0; b < i; b++) {
      MenuItem menuItem = paramMenuBuilder.getItem(b);
      if (menuItem.isVisible() && menuItem.getIcon() != null)
        return true; 
    } 
    return false;
  }
  
  protected static MenuAdapter toMenuAdapter(ListAdapter paramListAdapter) { return (paramListAdapter instanceof HeaderViewListAdapter) ? (MenuAdapter)((HeaderViewListAdapter)paramListAdapter).getWrappedAdapter() : (MenuAdapter)paramListAdapter; }
  
  public abstract void addMenu(MenuBuilder paramMenuBuilder);
  
  protected boolean closeMenuOnSubMenuOpened() { return true; }
  
  public boolean collapseItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl) { return false; }
  
  public boolean expandItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl) { return false; }
  
  public Rect getEpicenterBounds() { return this.mEpicenterBounds; }
  
  public int getId() { return 0; }
  
  public MenuView getMenuView(ViewGroup paramViewGroup) { throw new UnsupportedOperationException("MenuPopups manage their own views"); }
  
  public void initForMenu(@NonNull Context paramContext, @Nullable MenuBuilder paramMenuBuilder) {}
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
    ListAdapter listAdapter = (ListAdapter)paramAdapterView.getAdapter();
    MenuBuilder menuBuilder = (toMenuAdapter(listAdapter)).mAdapterMenu;
    MenuItem menuItem = (MenuItem)listAdapter.getItem(paramInt);
    if (closeMenuOnSubMenuOpened()) {
      paramInt = 0;
    } else {
      paramInt = 4;
    } 
    menuBuilder.performItemAction(menuItem, this, paramInt);
  }
  
  public abstract void setAnchorView(View paramView);
  
  public void setEpicenterBounds(Rect paramRect) { this.mEpicenterBounds = paramRect; }
  
  public abstract void setForceShowIcon(boolean paramBoolean);
  
  public abstract void setGravity(int paramInt);
  
  public abstract void setHorizontalOffset(int paramInt);
  
  public abstract void setOnDismissListener(PopupWindow.OnDismissListener paramOnDismissListener);
  
  public abstract void setShowTitle(boolean paramBoolean);
  
  public abstract void setVerticalOffset(int paramInt);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/view/menu/MenuPopup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */