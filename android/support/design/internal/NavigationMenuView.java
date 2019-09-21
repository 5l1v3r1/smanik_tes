package android.support.design.internal;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class NavigationMenuView extends RecyclerView implements MenuView {
  public NavigationMenuView(Context paramContext) { this(paramContext, null); }
  
  public NavigationMenuView(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public NavigationMenuView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
  }
  
  public int getWindowAnimations() { return 0; }
  
  public void initialize(MenuBuilder paramMenuBuilder) {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/internal/NavigationMenuView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */