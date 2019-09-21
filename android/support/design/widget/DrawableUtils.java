package android.support.design.widget;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.util.Log;
import java.lang.reflect.Method;

class DrawableUtils {
  private static final String LOG_TAG = "DrawableUtils";
  
  private static Method sSetConstantStateMethod;
  
  private static boolean sSetConstantStateMethodFetched;
  
  static boolean setContainerConstantState(DrawableContainer paramDrawableContainer, Drawable.ConstantState paramConstantState) { return setContainerConstantStateV9(paramDrawableContainer, paramConstantState); }
  
  private static boolean setContainerConstantStateV9(DrawableContainer paramDrawableContainer, Drawable.ConstantState paramConstantState) {
    if (!sSetConstantStateMethodFetched) {
      try {
        sSetConstantStateMethod = DrawableContainer.class.getDeclaredMethod("setConstantState", new Class[] { DrawableContainer.DrawableContainerState.class });
        sSetConstantStateMethod.setAccessible(true);
      } catch (NoSuchMethodException noSuchMethodException) {
        Log.e("DrawableUtils", "Could not fetch setConstantState(). Oh well.");
      } 
      sSetConstantStateMethodFetched = true;
    } 
    if (sSetConstantStateMethod != null)
      try {
        sSetConstantStateMethod.invoke(paramDrawableContainer, new Object[] { paramConstantState });
        return true;
      } catch (Exception paramDrawableContainer) {
        Log.e("DrawableUtils", "Could not invoke setConstantState(). Oh well.");
      }  
    return false;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/DrawableUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */