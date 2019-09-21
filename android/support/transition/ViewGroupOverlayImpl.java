package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;

@RequiresApi(14)
interface ViewGroupOverlayImpl extends ViewOverlayImpl {
  void add(@NonNull View paramView);
  
  void remove(@NonNull View paramView);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ViewGroupOverlayImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */