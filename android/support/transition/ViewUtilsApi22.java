package android.support.transition;

import android.annotation.SuppressLint;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(22)
class ViewUtilsApi22 extends ViewUtilsApi21 {
  private static final String TAG = "ViewUtilsApi22";
  
  private static Method sSetLeftTopRightBottomMethod;
  
  private static boolean sSetLeftTopRightBottomMethodFetched;
  
  @SuppressLint({"PrivateApi"})
  private void fetchSetLeftTopRightBottomMethod() {
    if (!sSetLeftTopRightBottomMethodFetched) {
      try {
        sSetLeftTopRightBottomMethod = View.class.getDeclaredMethod("setLeftTopRightBottom", new Class[] { int.class, int.class, int.class, int.class });
        sSetLeftTopRightBottomMethod.setAccessible(true);
      } catch (NoSuchMethodException noSuchMethodException) {
        Log.i("ViewUtilsApi22", "Failed to retrieve setLeftTopRightBottom method", noSuchMethodException);
      } 
      sSetLeftTopRightBottomMethodFetched = true;
    } 
  }
  
  public void setLeftTopRightBottom(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    fetchSetLeftTopRightBottomMethod();
    if (sSetLeftTopRightBottomMethod != null)
      try {
        sSetLeftTopRightBottomMethod.invoke(paramView, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) });
        return;
      } catch (IllegalAccessException paramView) {
        return;
      } catch (InvocationTargetException paramView) {
        throw new RuntimeException(paramView.getCause());
      }  
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ViewUtilsApi22.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */