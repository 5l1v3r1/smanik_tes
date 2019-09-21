package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.design.R;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class NavigationMenuPresenter implements MenuPresenter {
  private static final String STATE_ADAPTER = "android:menu:adapter";
  
  private static final String STATE_HEADER = "android:menu:header";
  
  private static final String STATE_HIERARCHY = "android:menu:list";
  
  NavigationMenuAdapter mAdapter;
  
  private MenuPresenter.Callback mCallback;
  
  LinearLayout mHeaderLayout;
  
  ColorStateList mIconTintList;
  
  private int mId;
  
  Drawable mItemBackground;
  
  LayoutInflater mLayoutInflater;
  
  MenuBuilder mMenu;
  
  private NavigationMenuView mMenuView;
  
  final View.OnClickListener mOnClickListener = new View.OnClickListener() {
      public void onClick(View param1View) {
        NavigationMenuItemView navigationMenuItemView = (NavigationMenuItemView)param1View;
        NavigationMenuPresenter.this.setUpdateSuspended(true);
        MenuItemImpl menuItemImpl = navigationMenuItemView.getItemData();
        boolean bool = NavigationMenuPresenter.this.mMenu.performItemAction(menuItemImpl, NavigationMenuPresenter.this, 0);
        if (menuItemImpl != null && menuItemImpl.isCheckable() && bool)
          NavigationMenuPresenter.this.mAdapter.setCheckedItem(menuItemImpl); 
        NavigationMenuPresenter.this.setUpdateSuspended(false);
        NavigationMenuPresenter.this.updateMenuView(false);
      }
    };
  
  int mPaddingSeparator;
  
  private int mPaddingTopDefault;
  
  int mTextAppearance;
  
  boolean mTextAppearanceSet;
  
  ColorStateList mTextColor;
  
  public void addHeaderView(@NonNull View paramView) {
    this.mHeaderLayout.addView(paramView);
    this.mMenuView.setPadding(0, 0, 0, this.mMenuView.getPaddingBottom());
  }
  
  public boolean collapseItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl) { return false; }
  
  public void dispatchApplyWindowInsets(WindowInsetsCompat paramWindowInsetsCompat) {
    int i = paramWindowInsetsCompat.getSystemWindowInsetTop();
    if (this.mPaddingTopDefault != i) {
      this.mPaddingTopDefault = i;
      if (this.mHeaderLayout.getChildCount() == 0)
        this.mMenuView.setPadding(0, this.mPaddingTopDefault, 0, this.mMenuView.getPaddingBottom()); 
    } 
    ViewCompat.dispatchApplyWindowInsets(this.mHeaderLayout, paramWindowInsetsCompat);
  }
  
  public boolean expandItemActionView(MenuBuilder paramMenuBuilder, MenuItemImpl paramMenuItemImpl) { return false; }
  
  public boolean flagActionItems() { return false; }
  
  public int getHeaderCount() { return this.mHeaderLayout.getChildCount(); }
  
  public View getHeaderView(int paramInt) { return this.mHeaderLayout.getChildAt(paramInt); }
  
  public int getId() { return this.mId; }
  
  @Nullable
  public Drawable getItemBackground() { return this.mItemBackground; }
  
  @Nullable
  public ColorStateList getItemTextColor() { return this.mTextColor; }
  
  @Nullable
  public ColorStateList getItemTintList() { return this.mIconTintList; }
  
  public MenuView getMenuView(ViewGroup paramViewGroup) {
    if (this.mMenuView == null) {
      this.mMenuView = (NavigationMenuView)this.mLayoutInflater.inflate(R.layout.design_navigation_menu, paramViewGroup, false);
      if (this.mAdapter == null)
        this.mAdapter = new NavigationMenuAdapter(); 
      this.mHeaderLayout = (LinearLayout)this.mLayoutInflater.inflate(R.layout.design_navigation_item_header, this.mMenuView, false);
      this.mMenuView.setAdapter(this.mAdapter);
    } 
    return this.mMenuView;
  }
  
  public View inflateHeaderView(@LayoutRes int paramInt) {
    View view = this.mLayoutInflater.inflate(paramInt, this.mHeaderLayout, false);
    addHeaderView(view);
    return view;
  }
  
  public void initForMenu(Context paramContext, MenuBuilder paramMenuBuilder) {
    this.mLayoutInflater = LayoutInflater.from(paramContext);
    this.mMenu = paramMenuBuilder;
    this.mPaddingSeparator = paramContext.getResources().getDimensionPixelOffset(R.dimen.design_navigation_separator_vertical_padding);
  }
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {
    if (this.mCallback != null)
      this.mCallback.onCloseMenu(paramMenuBuilder, paramBoolean); 
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable) {
    if (paramParcelable instanceof Bundle) {
      Bundle bundle1 = (Bundle)paramParcelable;
      SparseArray sparseArray2 = bundle1.getSparseParcelableArray("android:menu:list");
      if (sparseArray2 != null)
        this.mMenuView.restoreHierarchyState(sparseArray2); 
      Bundle bundle2 = bundle1.getBundle("android:menu:adapter");
      if (bundle2 != null)
        this.mAdapter.restoreInstanceState(bundle2); 
      SparseArray sparseArray1 = bundle1.getSparseParcelableArray("android:menu:header");
      if (sparseArray1 != null)
        this.mHeaderLayout.restoreHierarchyState(sparseArray1); 
    } 
  }
  
  public Parcelable onSaveInstanceState() {
    if (Build.VERSION.SDK_INT >= 11) {
      Bundle bundle = new Bundle();
      if (this.mMenuView != null) {
        SparseArray sparseArray = new SparseArray();
        this.mMenuView.saveHierarchyState(sparseArray);
        bundle.putSparseParcelableArray("android:menu:list", sparseArray);
      } 
      if (this.mAdapter != null)
        bundle.putBundle("android:menu:adapter", this.mAdapter.createInstanceState()); 
      if (this.mHeaderLayout != null) {
        SparseArray sparseArray = new SparseArray();
        this.mHeaderLayout.saveHierarchyState(sparseArray);
        bundle.putSparseParcelableArray("android:menu:header", sparseArray);
      } 
      return bundle;
    } 
    return null;
  }
  
  public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder) { return false; }
  
  public void removeHeaderView(@NonNull View paramView) {
    this.mHeaderLayout.removeView(paramView);
    if (this.mHeaderLayout.getChildCount() == 0)
      this.mMenuView.setPadding(0, this.mPaddingTopDefault, 0, this.mMenuView.getPaddingBottom()); 
  }
  
  public void setCallback(MenuPresenter.Callback paramCallback) { this.mCallback = paramCallback; }
  
  public void setCheckedItem(MenuItemImpl paramMenuItemImpl) { this.mAdapter.setCheckedItem(paramMenuItemImpl); }
  
  public void setId(int paramInt) { this.mId = paramInt; }
  
  public void setItemBackground(@Nullable Drawable paramDrawable) {
    this.mItemBackground = paramDrawable;
    updateMenuView(false);
  }
  
  public void setItemIconTintList(@Nullable ColorStateList paramColorStateList) {
    this.mIconTintList = paramColorStateList;
    updateMenuView(false);
  }
  
  public void setItemTextAppearance(@StyleRes int paramInt) {
    this.mTextAppearance = paramInt;
    this.mTextAppearanceSet = true;
    updateMenuView(false);
  }
  
  public void setItemTextColor(@Nullable ColorStateList paramColorStateList) {
    this.mTextColor = paramColorStateList;
    updateMenuView(false);
  }
  
  public void setUpdateSuspended(boolean paramBoolean) {
    if (this.mAdapter != null)
      this.mAdapter.setUpdateSuspended(paramBoolean); 
  }
  
  public void updateMenuView(boolean paramBoolean) {
    if (this.mAdapter != null)
      this.mAdapter.update(); 
  }
  
  private static class HeaderViewHolder extends ViewHolder {
    public HeaderViewHolder(View param1View) { super(param1View); }
  }
  
  private class NavigationMenuAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String STATE_ACTION_VIEWS = "android:menu:action_views";
    
    private static final String STATE_CHECKED_ITEM = "android:menu:checked";
    
    private static final int VIEW_TYPE_HEADER = 3;
    
    private static final int VIEW_TYPE_NORMAL = 0;
    
    private static final int VIEW_TYPE_SEPARATOR = 2;
    
    private static final int VIEW_TYPE_SUBHEADER = 1;
    
    private MenuItemImpl mCheckedItem;
    
    private final ArrayList<NavigationMenuPresenter.NavigationMenuItem> mItems = new ArrayList();
    
    private boolean mUpdateSuspended;
    
    NavigationMenuAdapter() { prepareMenuItems(); }
    
    private void appendTransparentIconIfMissing(int param1Int1, int param1Int2) {
      while (param1Int1 < param1Int2) {
        ((NavigationMenuPresenter.NavigationMenuTextItem)this.mItems.get(param1Int1)).needsEmptyIcon = true;
        param1Int1++;
      } 
    }
    
    private void prepareMenuItems() {
      if (this.mUpdateSuspended)
        return; 
      this.mUpdateSuspended = true;
      this.mItems.clear();
      this.mItems.add(new NavigationMenuPresenter.NavigationMenuHeaderItem());
      int k = NavigationMenuPresenter.this.mMenu.getVisibleItems().size();
      byte b1 = 0;
      int j = -1;
      byte b2 = 0;
      for (int i = 0; b1 < k; i = m) {
        byte b;
        int n;
        int m;
        MenuItemImpl menuItemImpl = (MenuItemImpl)NavigationMenuPresenter.this.mMenu.getVisibleItems().get(b1);
        if (menuItemImpl.isChecked())
          setCheckedItem(menuItemImpl); 
        if (menuItemImpl.isCheckable())
          menuItemImpl.setExclusiveCheckable(false); 
        if (menuItemImpl.hasSubMenu()) {
          SubMenu subMenu = menuItemImpl.getSubMenu();
          n = j;
          b = b2;
          m = i;
          if (subMenu.hasVisibleItems()) {
            if (b1 != 0)
              this.mItems.add(new NavigationMenuPresenter.NavigationMenuSeparatorItem(NavigationMenuPresenter.this.mPaddingSeparator, 0)); 
            this.mItems.add(new NavigationMenuPresenter.NavigationMenuTextItem(menuItemImpl));
            int i1 = this.mItems.size();
            int i2 = subMenu.size();
            n = 0;
            byte b3;
            for (b3 = 0; n < i2; b3 = m) {
              MenuItemImpl menuItemImpl1 = (MenuItemImpl)subMenu.getItem(n);
              m = b3;
              if (menuItemImpl1.isVisible()) {
                m = b3;
                if (b3 == 0) {
                  m = b3;
                  if (menuItemImpl1.getIcon() != null)
                    m = 1; 
                } 
                if (menuItemImpl1.isCheckable())
                  menuItemImpl1.setExclusiveCheckable(false); 
                if (menuItemImpl.isChecked())
                  setCheckedItem(menuItemImpl); 
                this.mItems.add(new NavigationMenuPresenter.NavigationMenuTextItem(menuItemImpl1));
              } 
              n++;
            } 
            n = j;
            b = b2;
            m = i;
            if (b3 != 0) {
              appendTransparentIconIfMissing(i1, this.mItems.size());
              n = j;
              b = b2;
              m = i;
            } 
          } 
        } else {
          int i1;
          n = menuItemImpl.getGroupId();
          if (n != j) {
            i = this.mItems.size();
            if (menuItemImpl.getIcon() != null) {
              b = 1;
            } else {
              b = 0;
            } 
            i1 = i;
            if (b1 != 0) {
              i1 = i + 1;
              this.mItems.add(new NavigationMenuPresenter.NavigationMenuSeparatorItem(NavigationMenuPresenter.this.mPaddingSeparator, NavigationMenuPresenter.this.mPaddingSeparator));
            } 
          } else {
            b = b2;
            i1 = i;
            if (b2 == 0) {
              b = b2;
              i1 = i;
              if (menuItemImpl.getIcon() != null) {
                appendTransparentIconIfMissing(i, this.mItems.size());
                b = 1;
                i1 = i;
              } 
            } 
          } 
          NavigationMenuPresenter.NavigationMenuTextItem navigationMenuTextItem = new NavigationMenuPresenter.NavigationMenuTextItem(menuItemImpl);
          navigationMenuTextItem.needsEmptyIcon = b;
          this.mItems.add(navigationMenuTextItem);
          m = i1;
        } 
        b1++;
        j = n;
        b2 = b;
      } 
      this.mUpdateSuspended = false;
    }
    
    public Bundle createInstanceState() {
      Bundle bundle = new Bundle();
      if (this.mCheckedItem != null)
        bundle.putInt("android:menu:checked", this.mCheckedItem.getItemId()); 
      SparseArray sparseArray = new SparseArray();
      byte b = 0;
      int i = this.mItems.size();
      while (b < i) {
        NavigationMenuPresenter.NavigationMenuItem navigationMenuItem = (NavigationMenuPresenter.NavigationMenuItem)this.mItems.get(b);
        if (navigationMenuItem instanceof NavigationMenuPresenter.NavigationMenuTextItem) {
          MenuItemImpl menuItemImpl = ((NavigationMenuPresenter.NavigationMenuTextItem)navigationMenuItem).getMenuItem();
          if (menuItemImpl != null) {
            View view = menuItemImpl.getActionView();
          } else {
            navigationMenuItem = null;
          } 
          if (navigationMenuItem != null) {
            ParcelableSparseArray parcelableSparseArray = new ParcelableSparseArray();
            navigationMenuItem.saveHierarchyState(parcelableSparseArray);
            sparseArray.put(menuItemImpl.getItemId(), parcelableSparseArray);
          } 
        } 
        b++;
      } 
      bundle.putSparseParcelableArray("android:menu:action_views", sparseArray);
      return bundle;
    }
    
    public int getItemCount() { return this.mItems.size(); }
    
    public long getItemId(int param1Int) { return param1Int; }
    
    public int getItemViewType(int param1Int) {
      NavigationMenuPresenter.NavigationMenuItem navigationMenuItem = (NavigationMenuPresenter.NavigationMenuItem)this.mItems.get(param1Int);
      if (navigationMenuItem instanceof NavigationMenuPresenter.NavigationMenuSeparatorItem)
        return 2; 
      if (navigationMenuItem instanceof NavigationMenuPresenter.NavigationMenuHeaderItem)
        return 3; 
      if (navigationMenuItem instanceof NavigationMenuPresenter.NavigationMenuTextItem)
        return ((NavigationMenuPresenter.NavigationMenuTextItem)navigationMenuItem).getMenuItem().hasSubMenu() ? 1 : 0; 
      throw new RuntimeException("Unknown item type.");
    }
    
    public void onBindViewHolder(NavigationMenuPresenter.ViewHolder param1ViewHolder, int param1Int) {
      NavigationMenuPresenter.NavigationMenuSeparatorItem navigationMenuSeparatorItem;
      switch (getItemViewType(param1Int)) {
        default:
          return;
        case 2:
          navigationMenuSeparatorItem = (NavigationMenuPresenter.NavigationMenuSeparatorItem)this.mItems.get(param1Int);
          param1ViewHolder.itemView.setPadding(0, navigationMenuSeparatorItem.getPaddingTop(), 0, navigationMenuSeparatorItem.getPaddingBottom());
          return;
        case 1:
          ((TextView)param1ViewHolder.itemView).setText(((NavigationMenuPresenter.NavigationMenuTextItem)this.mItems.get(param1Int)).getMenuItem().getTitle());
          return;
        case 0:
          break;
      } 
      NavigationMenuItemView navigationMenuItemView = (NavigationMenuItemView)param1ViewHolder.itemView;
      navigationMenuItemView.setIconTintList(NavigationMenuPresenter.this.mIconTintList);
      if (NavigationMenuPresenter.this.mTextAppearanceSet)
        navigationMenuItemView.setTextAppearance(NavigationMenuPresenter.this.mTextAppearance); 
      if (NavigationMenuPresenter.this.mTextColor != null)
        navigationMenuItemView.setTextColor(NavigationMenuPresenter.this.mTextColor); 
      if (NavigationMenuPresenter.this.mItemBackground != null) {
        Drawable drawable = NavigationMenuPresenter.this.mItemBackground.getConstantState().newDrawable();
      } else {
        param1ViewHolder = null;
      } 
      ViewCompat.setBackground(navigationMenuItemView, param1ViewHolder);
      NavigationMenuPresenter.NavigationMenuTextItem navigationMenuTextItem = (NavigationMenuPresenter.NavigationMenuTextItem)this.mItems.get(param1Int);
      navigationMenuItemView.setNeedsEmptyIcon(navigationMenuTextItem.needsEmptyIcon);
      navigationMenuItemView.initialize(navigationMenuTextItem.getMenuItem(), 0);
    }
    
    public NavigationMenuPresenter.ViewHolder onCreateViewHolder(ViewGroup param1ViewGroup, int param1Int) {
      switch (param1Int) {
        default:
          return null;
        case 3:
          return new NavigationMenuPresenter.HeaderViewHolder(NavigationMenuPresenter.this.mHeaderLayout);
        case 2:
          return new NavigationMenuPresenter.SeparatorViewHolder(NavigationMenuPresenter.this.mLayoutInflater, param1ViewGroup);
        case 1:
          return new NavigationMenuPresenter.SubheaderViewHolder(NavigationMenuPresenter.this.mLayoutInflater, param1ViewGroup);
        case 0:
          break;
      } 
      return new NavigationMenuPresenter.NormalViewHolder(NavigationMenuPresenter.this.mLayoutInflater, param1ViewGroup, NavigationMenuPresenter.this.mOnClickListener);
    }
    
    public void onViewRecycled(NavigationMenuPresenter.ViewHolder param1ViewHolder) {
      if (param1ViewHolder instanceof NavigationMenuPresenter.NormalViewHolder)
        ((NavigationMenuItemView)param1ViewHolder.itemView).recycle(); 
    }
    
    public void restoreInstanceState(Bundle param1Bundle) {
      boolean bool = false;
      int i = param1Bundle.getInt("android:menu:checked", 0);
      if (i != 0) {
        this.mUpdateSuspended = true;
        int j = this.mItems.size();
        for (byte b = 0; b < j; b++) {
          NavigationMenuPresenter.NavigationMenuItem navigationMenuItem = (NavigationMenuPresenter.NavigationMenuItem)this.mItems.get(b);
          if (navigationMenuItem instanceof NavigationMenuPresenter.NavigationMenuTextItem) {
            MenuItemImpl menuItemImpl = ((NavigationMenuPresenter.NavigationMenuTextItem)navigationMenuItem).getMenuItem();
            if (menuItemImpl != null && menuItemImpl.getItemId() == i) {
              setCheckedItem(menuItemImpl);
              break;
            } 
          } 
        } 
        this.mUpdateSuspended = false;
        prepareMenuItems();
      } 
      SparseArray sparseArray = param1Bundle.getSparseParcelableArray("android:menu:action_views");
      if (sparseArray != null) {
        i = this.mItems.size();
        for (byte b = bool; b < i; b++) {
          NavigationMenuPresenter.NavigationMenuItem navigationMenuItem = (NavigationMenuPresenter.NavigationMenuItem)this.mItems.get(b);
          if (navigationMenuItem instanceof NavigationMenuPresenter.NavigationMenuTextItem) {
            MenuItemImpl menuItemImpl = ((NavigationMenuPresenter.NavigationMenuTextItem)navigationMenuItem).getMenuItem();
            if (menuItemImpl != null) {
              View view = menuItemImpl.getActionView();
              if (view != null) {
                ParcelableSparseArray parcelableSparseArray = (ParcelableSparseArray)sparseArray.get(menuItemImpl.getItemId());
                if (parcelableSparseArray != null)
                  view.restoreHierarchyState(parcelableSparseArray); 
              } 
            } 
          } 
        } 
      } 
    }
    
    public void setCheckedItem(MenuItemImpl param1MenuItemImpl) {
      if (this.mCheckedItem != param1MenuItemImpl) {
        if (!param1MenuItemImpl.isCheckable())
          return; 
        if (this.mCheckedItem != null)
          this.mCheckedItem.setChecked(false); 
        this.mCheckedItem = param1MenuItemImpl;
        param1MenuItemImpl.setChecked(true);
        return;
      } 
    }
    
    public void setUpdateSuspended(boolean param1Boolean) { this.mUpdateSuspended = param1Boolean; }
    
    public void update() {
      prepareMenuItems();
      notifyDataSetChanged();
    }
  }
  
  private static class NavigationMenuHeaderItem implements NavigationMenuItem {}
  
  private static interface NavigationMenuItem {}
  
  private static class NavigationMenuSeparatorItem implements NavigationMenuItem {
    private final int mPaddingBottom;
    
    private final int mPaddingTop;
    
    public NavigationMenuSeparatorItem(int param1Int1, int param1Int2) {
      this.mPaddingTop = param1Int1;
      this.mPaddingBottom = param1Int2;
    }
    
    public int getPaddingBottom() { return this.mPaddingBottom; }
    
    public int getPaddingTop() { return this.mPaddingTop; }
  }
  
  private static class NavigationMenuTextItem implements NavigationMenuItem {
    private final MenuItemImpl mMenuItem;
    
    boolean needsEmptyIcon;
    
    NavigationMenuTextItem(MenuItemImpl param1MenuItemImpl) { this.mMenuItem = param1MenuItemImpl; }
    
    public MenuItemImpl getMenuItem() { return this.mMenuItem; }
  }
  
  private static class NormalViewHolder extends ViewHolder {
    public NormalViewHolder(LayoutInflater param1LayoutInflater, ViewGroup param1ViewGroup, View.OnClickListener param1OnClickListener) {
      super(param1LayoutInflater.inflate(R.layout.design_navigation_item, param1ViewGroup, false));
      this.itemView.setOnClickListener(param1OnClickListener);
    }
  }
  
  private static class SeparatorViewHolder extends ViewHolder {
    public SeparatorViewHolder(LayoutInflater param1LayoutInflater, ViewGroup param1ViewGroup) { super(param1LayoutInflater.inflate(R.layout.design_navigation_item_separator, param1ViewGroup, false)); }
  }
  
  private static class SubheaderViewHolder extends ViewHolder {
    public SubheaderViewHolder(LayoutInflater param1LayoutInflater, ViewGroup param1ViewGroup) { super(param1LayoutInflater.inflate(R.layout.design_navigation_item_subheader, param1ViewGroup, false)); }
  }
  
  private static abstract class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(View param1View) { super(param1View); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/internal/NavigationMenuPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */