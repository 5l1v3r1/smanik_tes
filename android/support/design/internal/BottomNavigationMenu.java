package android.support.design.internal;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.MenuItem;
import android.view.SubMenu;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class BottomNavigationMenu extends MenuBuilder {
  public static final int MAX_ITEM_COUNT = 5;
  
  public BottomNavigationMenu(Context paramContext) { super(paramContext); }
  
  protected MenuItem addInternal(int paramInt1, int paramInt2, int paramInt3, CharSequence paramCharSequence) {
    if (size() + 1 > 5)
      throw new IllegalArgumentException("Maximum number of items supported by BottomNavigationView is 5. Limit can be checked with BottomNavigationView#getMaxItemCount()"); 
    stopDispatchingItemsChanged();
    MenuItem menuItem = super.addInternal(paramInt1, paramInt2, paramInt3, paramCharSequence);
    if (menuItem instanceof MenuItemImpl)
      ((MenuItemImpl)menuItem).setExclusiveCheckable(true); 
    startDispatchingItemsChanged();
    return menuItem;
  }
  
  public SubMenu addSubMenu(int paramInt1, int paramInt2, int paramInt3, CharSequence paramCharSequence) { throw new UnsupportedOperationException("BottomNavigationView does not support submenus"); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/internal/BottomNavigationMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */