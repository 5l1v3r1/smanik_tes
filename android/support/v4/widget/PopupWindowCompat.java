package android.support.v4.widget;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class PopupWindowCompat {
  static final PopupWindowCompatBaseImpl IMPL;
  
  static  {
    if (Build.VERSION.SDK_INT >= 23) {
      IMPL = new PopupWindowCompatApi23Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 21) {
      IMPL = new PopupWindowCompatApi21Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 19) {
      IMPL = new PopupWindowCompatApi19Impl();
      return;
    } 
    IMPL = new PopupWindowCompatBaseImpl();
  }
  
  public static boolean getOverlapAnchor(@NonNull PopupWindow paramPopupWindow) { return IMPL.getOverlapAnchor(paramPopupWindow); }
  
  public static int getWindowLayoutType(@NonNull PopupWindow paramPopupWindow) { return IMPL.getWindowLayoutType(paramPopupWindow); }
  
  public static void setOverlapAnchor(@NonNull PopupWindow paramPopupWindow, boolean paramBoolean) { IMPL.setOverlapAnchor(paramPopupWindow, paramBoolean); }
  
  public static void setWindowLayoutType(@NonNull PopupWindow paramPopupWindow, int paramInt) { IMPL.setWindowLayoutType(paramPopupWindow, paramInt); }
  
  public static void showAsDropDown(@NonNull PopupWindow paramPopupWindow, @NonNull View paramView, int paramInt1, int paramInt2, int paramInt3) { IMPL.showAsDropDown(paramPopupWindow, paramView, paramInt1, paramInt2, paramInt3); }
  
  @RequiresApi(19)
  static class PopupWindowCompatApi19Impl extends PopupWindowCompatBaseImpl {
    public void showAsDropDown(PopupWindow param1PopupWindow, View param1View, int param1Int1, int param1Int2, int param1Int3) { param1PopupWindow.showAsDropDown(param1View, param1Int1, param1Int2, param1Int3); }
  }
  
  @RequiresApi(21)
  static class PopupWindowCompatApi21Impl extends PopupWindowCompatApi19Impl {
    private static final String TAG = "PopupWindowCompatApi21";
    
    private static Field sOverlapAnchorField;
    
    static  {
      try {
        sOverlapAnchorField = PopupWindow.class.getDeclaredField("mOverlapAnchor");
        sOverlapAnchorField.setAccessible(true);
        return;
      } catch (NoSuchFieldException noSuchFieldException) {
        Log.i("PopupWindowCompatApi21", "Could not fetch mOverlapAnchor field from PopupWindow", noSuchFieldException);
        return;
      } 
    }
    
    public boolean getOverlapAnchor(PopupWindow param1PopupWindow) {
      if (sOverlapAnchorField != null)
        try {
          return ((Boolean)sOverlapAnchorField.get(param1PopupWindow)).booleanValue();
        } catch (IllegalAccessException param1PopupWindow) {
          Log.i("PopupWindowCompatApi21", "Could not get overlap anchor field in PopupWindow", param1PopupWindow);
        }  
      return false;
    }
    
    public void setOverlapAnchor(PopupWindow param1PopupWindow, boolean param1Boolean) {
      if (sOverlapAnchorField != null)
        try {
          sOverlapAnchorField.set(param1PopupWindow, Boolean.valueOf(param1Boolean));
          return;
        } catch (IllegalAccessException param1PopupWindow) {
          Log.i("PopupWindowCompatApi21", "Could not set overlap anchor field in PopupWindow", param1PopupWindow);
        }  
    }
  }
  
  @RequiresApi(23)
  static class PopupWindowCompatApi23Impl extends PopupWindowCompatApi21Impl {
    public boolean getOverlapAnchor(PopupWindow param1PopupWindow) { return param1PopupWindow.getOverlapAnchor(); }
    
    public int getWindowLayoutType(PopupWindow param1PopupWindow) { return param1PopupWindow.getWindowLayoutType(); }
    
    public void setOverlapAnchor(PopupWindow param1PopupWindow, boolean param1Boolean) { param1PopupWindow.setOverlapAnchor(param1Boolean); }
    
    public void setWindowLayoutType(PopupWindow param1PopupWindow, int param1Int) { param1PopupWindow.setWindowLayoutType(param1Int); }
  }
  
  static class PopupWindowCompatBaseImpl {
    private static Method sGetWindowLayoutTypeMethod;
    
    private static boolean sGetWindowLayoutTypeMethodAttempted;
    
    private static Method sSetWindowLayoutTypeMethod;
    
    private static boolean sSetWindowLayoutTypeMethodAttempted;
    
    public boolean getOverlapAnchor(PopupWindow param1PopupWindow) { return false; }
    
    public int getWindowLayoutType(PopupWindow param1PopupWindow) {
      if (!sGetWindowLayoutTypeMethodAttempted) {
        try {
          sGetWindowLayoutTypeMethod = PopupWindow.class.getDeclaredMethod("getWindowLayoutType", new Class[0]);
          sGetWindowLayoutTypeMethod.setAccessible(true);
        } catch (Exception exception) {}
        sGetWindowLayoutTypeMethodAttempted = true;
      } 
      if (sGetWindowLayoutTypeMethod != null)
        try {
          return ((Integer)sGetWindowLayoutTypeMethod.invoke(param1PopupWindow, new Object[0])).intValue();
        } catch (Exception param1PopupWindow) {
          return 0;
        }  
      return 0;
    }
    
    public void setOverlapAnchor(PopupWindow param1PopupWindow, boolean param1Boolean) {}
    
    public void setWindowLayoutType(PopupWindow param1PopupWindow, int param1Int) {
      if (!sSetWindowLayoutTypeMethodAttempted) {
        try {
          sSetWindowLayoutTypeMethod = PopupWindow.class.getDeclaredMethod("setWindowLayoutType", new Class[] { int.class });
          sSetWindowLayoutTypeMethod.setAccessible(true);
        } catch (Exception exception) {}
        sSetWindowLayoutTypeMethodAttempted = true;
      } 
      if (sSetWindowLayoutTypeMethod != null)
        try {
          sSetWindowLayoutTypeMethod.invoke(param1PopupWindow, new Object[] { Integer.valueOf(param1Int) });
          return;
        } catch (Exception param1PopupWindow) {
          return;
        }  
    }
    
    public void showAsDropDown(PopupWindow param1PopupWindow, View param1View, int param1Int1, int param1Int2, int param1Int3) {
      int i = param1Int1;
      if ((GravityCompat.getAbsoluteGravity(param1Int3, ViewCompat.getLayoutDirection(param1View)) & 0x7) == 5)
        i = param1Int1 - param1PopupWindow.getWidth() - param1View.getWidth(); 
      param1PopupWindow.showAsDropDown(param1View, i, param1Int2);
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/widget/PopupWindowCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */