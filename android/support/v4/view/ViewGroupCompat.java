package android.support.v4.view;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.compat.R;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

public final class ViewGroupCompat {
  static final ViewGroupCompatBaseImpl IMPL;
  
  public static final int LAYOUT_MODE_CLIP_BOUNDS = 0;
  
  public static final int LAYOUT_MODE_OPTICAL_BOUNDS = 1;
  
  static  {
    if (Build.VERSION.SDK_INT >= 21) {
      IMPL = new ViewGroupCompatApi21Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 18) {
      IMPL = new ViewGroupCompatApi18Impl();
      return;
    } 
    IMPL = new ViewGroupCompatBaseImpl();
  }
  
  public static int getLayoutMode(ViewGroup paramViewGroup) { return IMPL.getLayoutMode(paramViewGroup); }
  
  public static int getNestedScrollAxes(@NonNull ViewGroup paramViewGroup) { return IMPL.getNestedScrollAxes(paramViewGroup); }
  
  public static boolean isTransitionGroup(ViewGroup paramViewGroup) { return IMPL.isTransitionGroup(paramViewGroup); }
  
  @Deprecated
  public static boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent) { return paramViewGroup.onRequestSendAccessibilityEvent(paramView, paramAccessibilityEvent); }
  
  public static void setLayoutMode(ViewGroup paramViewGroup, int paramInt) { IMPL.setLayoutMode(paramViewGroup, paramInt); }
  
  @Deprecated
  public static void setMotionEventSplittingEnabled(ViewGroup paramViewGroup, boolean paramBoolean) { paramViewGroup.setMotionEventSplittingEnabled(paramBoolean); }
  
  public static void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean) { IMPL.setTransitionGroup(paramViewGroup, paramBoolean); }
  
  @RequiresApi(18)
  static class ViewGroupCompatApi18Impl extends ViewGroupCompatBaseImpl {
    public int getLayoutMode(ViewGroup param1ViewGroup) { return param1ViewGroup.getLayoutMode(); }
    
    public void setLayoutMode(ViewGroup param1ViewGroup, int param1Int) { param1ViewGroup.setLayoutMode(param1Int); }
  }
  
  @RequiresApi(21)
  static class ViewGroupCompatApi21Impl extends ViewGroupCompatApi18Impl {
    public int getNestedScrollAxes(ViewGroup param1ViewGroup) { return param1ViewGroup.getNestedScrollAxes(); }
    
    public boolean isTransitionGroup(ViewGroup param1ViewGroup) { return param1ViewGroup.isTransitionGroup(); }
    
    public void setTransitionGroup(ViewGroup param1ViewGroup, boolean param1Boolean) { param1ViewGroup.setTransitionGroup(param1Boolean); }
  }
  
  static class ViewGroupCompatBaseImpl {
    public int getLayoutMode(ViewGroup param1ViewGroup) { return 0; }
    
    public int getNestedScrollAxes(ViewGroup param1ViewGroup) { return (param1ViewGroup instanceof NestedScrollingParent) ? ((NestedScrollingParent)param1ViewGroup).getNestedScrollAxes() : 0; }
    
    public boolean isTransitionGroup(ViewGroup param1ViewGroup) {
      Boolean bool = (Boolean)param1ViewGroup.getTag(R.id.tag_transition_group);
      return ((bool != null && bool.booleanValue()) || param1ViewGroup.getBackground() != null || ViewCompat.getTransitionName(param1ViewGroup) != null);
    }
    
    public void setLayoutMode(ViewGroup param1ViewGroup, int param1Int) {}
    
    public void setTransitionGroup(ViewGroup param1ViewGroup, boolean param1Boolean) { param1ViewGroup.setTag(R.id.tag_transition_group, Boolean.valueOf(param1Boolean)); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/ViewGroupCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */