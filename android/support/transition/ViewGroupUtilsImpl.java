package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;

@RequiresApi(14)
interface ViewGroupUtilsImpl {
  ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup paramViewGroup);
  
  void suppressLayout(@NonNull ViewGroup paramViewGroup, boolean paramBoolean);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ViewGroupUtilsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */