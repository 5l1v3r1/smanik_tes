package android.support.transition;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewOverlay;

@RequiresApi(18)
class ViewOverlayApi18 implements ViewOverlayImpl {
  private final ViewOverlay mViewOverlay;
  
  ViewOverlayApi18(@NonNull View paramView) { this.mViewOverlay = paramView.getOverlay(); }
  
  public void add(@NonNull Drawable paramDrawable) { this.mViewOverlay.add(paramDrawable); }
  
  public void clear() { this.mViewOverlay.clear(); }
  
  public void remove(@NonNull Drawable paramDrawable) { this.mViewOverlay.remove(paramDrawable); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ViewOverlayApi18.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */