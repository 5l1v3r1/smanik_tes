package android.support.transition;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
interface ViewOverlayImpl {
  void add(@NonNull Drawable paramDrawable);
  
  void clear();
  
  void remove(@NonNull Drawable paramDrawable);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ViewOverlayImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */