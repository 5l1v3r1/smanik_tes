package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(18)
class ViewGroupUtilsApi18 extends ViewGroupUtilsApi14 {
  private static final String TAG = "ViewUtilsApi18";
  
  private static Method sSuppressLayoutMethod;
  
  private static boolean sSuppressLayoutMethodFetched;
  
  private void fetchSuppressLayoutMethod() {
    if (!sSuppressLayoutMethodFetched) {
      try {
        sSuppressLayoutMethod = ViewGroup.class.getDeclaredMethod("suppressLayout", new Class[] { boolean.class });
        sSuppressLayoutMethod.setAccessible(true);
      } catch (NoSuchMethodException noSuchMethodException) {
        Log.i("ViewUtilsApi18", "Failed to retrieve suppressLayout method", noSuchMethodException);
      } 
      sSuppressLayoutMethodFetched = true;
    } 
  }
  
  public ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup paramViewGroup) { return new ViewGroupOverlayApi18(paramViewGroup); }
  
  public void suppressLayout(@NonNull ViewGroup paramViewGroup, boolean paramBoolean) {
    fetchSuppressLayoutMethod();
    if (sSuppressLayoutMethod != null)
      try {
        sSuppressLayoutMethod.invoke(paramViewGroup, new Object[] { Boolean.valueOf(paramBoolean) });
        return;
      } catch (IllegalAccessException paramViewGroup) {
        Log.i("ViewUtilsApi18", "Failed to invoke suppressLayout method", paramViewGroup);
      } catch (InvocationTargetException paramViewGroup) {
        Log.i("ViewUtilsApi18", "Error invoking suppressLayout method", paramViewGroup);
        return;
      }  
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ViewGroupUtilsApi18.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */