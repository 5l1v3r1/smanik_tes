package android.support.v7.view.menu;

import android.support.annotation.RestrictTo;
import android.widget.ListView;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public interface ShowableListMenu {
  void dismiss();
  
  ListView getListView();
  
  boolean isShowing();
  
  void show();
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/view/menu/ShowableListMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */