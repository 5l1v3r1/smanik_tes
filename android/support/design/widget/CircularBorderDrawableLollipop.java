package android.support.design.widget;

import android.graphics.Outline;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class CircularBorderDrawableLollipop extends CircularBorderDrawable {
  public void getOutline(Outline paramOutline) {
    copyBounds(this.mRect);
    paramOutline.setOval(this.mRect);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/CircularBorderDrawableLollipop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */