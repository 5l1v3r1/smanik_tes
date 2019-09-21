package android.support.transition;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(21)
class GhostViewApi21 implements GhostViewImpl {
  private static final String TAG = "GhostViewApi21";
  
  private static Method sAddGhostMethod;
  
  private static boolean sAddGhostMethodFetched;
  
  private static Class<?> sGhostViewClass;
  
  private static boolean sGhostViewClassFetched;
  
  private static Method sRemoveGhostMethod;
  
  private static boolean sRemoveGhostMethodFetched;
  
  private final View mGhostView;
  
  private GhostViewApi21(@NonNull View paramView) { this.mGhostView = paramView; }
  
  private static void fetchAddGhostMethod() {
    if (!sAddGhostMethodFetched) {
      try {
        fetchGhostViewClass();
        sAddGhostMethod = sGhostViewClass.getDeclaredMethod("addGhost", new Class[] { View.class, ViewGroup.class, Matrix.class });
        sAddGhostMethod.setAccessible(true);
      } catch (NoSuchMethodException noSuchMethodException) {
        Log.i("GhostViewApi21", "Failed to retrieve addGhost method", noSuchMethodException);
      } 
      sAddGhostMethodFetched = true;
    } 
  }
  
  private static void fetchGhostViewClass() {
    if (!sGhostViewClassFetched) {
      try {
        sGhostViewClass = Class.forName("android.view.GhostView");
      } catch (ClassNotFoundException classNotFoundException) {
        Log.i("GhostViewApi21", "Failed to retrieve GhostView class", classNotFoundException);
      } 
      sGhostViewClassFetched = true;
    } 
  }
  
  private static void fetchRemoveGhostMethod() {
    if (!sRemoveGhostMethodFetched) {
      try {
        fetchGhostViewClass();
        sRemoveGhostMethod = sGhostViewClass.getDeclaredMethod("removeGhost", new Class[] { View.class });
        sRemoveGhostMethod.setAccessible(true);
      } catch (NoSuchMethodException noSuchMethodException) {
        Log.i("GhostViewApi21", "Failed to retrieve removeGhost method", noSuchMethodException);
      } 
      sRemoveGhostMethodFetched = true;
    } 
  }
  
  public void reserveEndViewTransition(ViewGroup paramViewGroup, View paramView) {}
  
  public void setVisibility(int paramInt) { this.mGhostView.setVisibility(paramInt); }
  
  static class Creator implements GhostViewImpl.Creator {
    public GhostViewImpl addGhost(View param1View, ViewGroup param1ViewGroup, Matrix param1Matrix) {
      GhostViewApi21.fetchAddGhostMethod();
      if (sAddGhostMethod != null)
        try {
          return new GhostViewApi21((View)sAddGhostMethod.invoke(null, new Object[] { param1View, param1ViewGroup, param1Matrix }), null);
        } catch (IllegalAccessException param1View) {
          return null;
        } catch (InvocationTargetException param1View) {
          throw new RuntimeException(param1View.getCause());
        }  
      return null;
    }
    
    public void removeGhost(View param1View) {
      GhostViewApi21.fetchRemoveGhostMethod();
      if (sRemoveGhostMethod != null)
        try {
          sRemoveGhostMethod.invoke(null, new Object[] { param1View });
          return;
        } catch (IllegalAccessException param1View) {
          return;
        } catch (InvocationTargetException param1View) {
          throw new RuntimeException(param1View.getCause());
        }  
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/GhostViewApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */