package android.support.v4.view.accessibility;

import android.graphics.Rect;
import android.os.Build;
import android.view.accessibility.AccessibilityWindowInfo;

public class AccessibilityWindowInfoCompat {
  public static final int TYPE_ACCESSIBILITY_OVERLAY = 4;
  
  public static final int TYPE_APPLICATION = 1;
  
  public static final int TYPE_INPUT_METHOD = 2;
  
  public static final int TYPE_SPLIT_SCREEN_DIVIDER = 5;
  
  public static final int TYPE_SYSTEM = 3;
  
  private static final int UNDEFINED = -1;
  
  private Object mInfo;
  
  private AccessibilityWindowInfoCompat(Object paramObject) { this.mInfo = paramObject; }
  
  public static AccessibilityWindowInfoCompat obtain() { return (Build.VERSION.SDK_INT >= 21) ? wrapNonNullInstance(AccessibilityWindowInfo.obtain()) : null; }
  
  public static AccessibilityWindowInfoCompat obtain(AccessibilityWindowInfoCompat paramAccessibilityWindowInfoCompat) { return (Build.VERSION.SDK_INT >= 21) ? ((paramAccessibilityWindowInfoCompat == null) ? null : wrapNonNullInstance(AccessibilityWindowInfo.obtain((AccessibilityWindowInfo)paramAccessibilityWindowInfoCompat.mInfo))) : null; }
  
  private static String typeToString(int paramInt) {
    switch (paramInt) {
      default:
        return "<UNKNOWN>";
      case 4:
        return "TYPE_ACCESSIBILITY_OVERLAY";
      case 3:
        return "TYPE_SYSTEM";
      case 2:
        return "TYPE_INPUT_METHOD";
      case 1:
        break;
    } 
    return "TYPE_APPLICATION";
  }
  
  static AccessibilityWindowInfoCompat wrapNonNullInstance(Object paramObject) { return (paramObject != null) ? new AccessibilityWindowInfoCompat(paramObject) : null; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null)
      return false; 
    if (getClass() != paramObject.getClass())
      return false; 
    paramObject = (AccessibilityWindowInfoCompat)paramObject;
    if (this.mInfo == null) {
      if (paramObject.mInfo != null)
        return false; 
    } else if (!this.mInfo.equals(paramObject.mInfo)) {
      return false;
    } 
    return true;
  }
  
  public AccessibilityNodeInfoCompat getAnchor() { return (Build.VERSION.SDK_INT >= 24) ? AccessibilityNodeInfoCompat.wrapNonNullInstance(((AccessibilityWindowInfo)this.mInfo).getAnchor()) : null; }
  
  public void getBoundsInScreen(Rect paramRect) {
    if (Build.VERSION.SDK_INT >= 21)
      ((AccessibilityWindowInfo)this.mInfo).getBoundsInScreen(paramRect); 
  }
  
  public AccessibilityWindowInfoCompat getChild(int paramInt) { return (Build.VERSION.SDK_INT >= 21) ? wrapNonNullInstance(((AccessibilityWindowInfo)this.mInfo).getChild(paramInt)) : null; }
  
  public int getChildCount() { return (Build.VERSION.SDK_INT >= 21) ? ((AccessibilityWindowInfo)this.mInfo).getChildCount() : 0; }
  
  public int getId() { return (Build.VERSION.SDK_INT >= 21) ? ((AccessibilityWindowInfo)this.mInfo).getId() : -1; }
  
  public int getLayer() { return (Build.VERSION.SDK_INT >= 21) ? ((AccessibilityWindowInfo)this.mInfo).getLayer() : -1; }
  
  public AccessibilityWindowInfoCompat getParent() { return (Build.VERSION.SDK_INT >= 21) ? wrapNonNullInstance(((AccessibilityWindowInfo)this.mInfo).getParent()) : null; }
  
  public AccessibilityNodeInfoCompat getRoot() { return (Build.VERSION.SDK_INT >= 21) ? AccessibilityNodeInfoCompat.wrapNonNullInstance(((AccessibilityWindowInfo)this.mInfo).getRoot()) : null; }
  
  public CharSequence getTitle() { return (Build.VERSION.SDK_INT >= 24) ? ((AccessibilityWindowInfo)this.mInfo).getTitle() : null; }
  
  public int getType() { return (Build.VERSION.SDK_INT >= 21) ? ((AccessibilityWindowInfo)this.mInfo).getType() : -1; }
  
  public int hashCode() { return (this.mInfo == null) ? 0 : this.mInfo.hashCode(); }
  
  public boolean isAccessibilityFocused() { return (Build.VERSION.SDK_INT >= 21) ? ((AccessibilityWindowInfo)this.mInfo).isAccessibilityFocused() : 1; }
  
  public boolean isActive() { return (Build.VERSION.SDK_INT >= 21) ? ((AccessibilityWindowInfo)this.mInfo).isActive() : 1; }
  
  public boolean isFocused() { return (Build.VERSION.SDK_INT >= 21) ? ((AccessibilityWindowInfo)this.mInfo).isFocused() : 1; }
  
  public void recycle() {
    if (Build.VERSION.SDK_INT >= 21)
      ((AccessibilityWindowInfo)this.mInfo).recycle(); 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    Rect rect = new Rect();
    getBoundsInScreen(rect);
    stringBuilder.append("AccessibilityWindowInfo[");
    stringBuilder.append("id=");
    stringBuilder.append(getId());
    stringBuilder.append(", type=");
    stringBuilder.append(typeToString(getType()));
    stringBuilder.append(", layer=");
    stringBuilder.append(getLayer());
    stringBuilder.append(", bounds=");
    stringBuilder.append(rect);
    stringBuilder.append(", focused=");
    stringBuilder.append(isFocused());
    stringBuilder.append(", active=");
    stringBuilder.append(isActive());
    stringBuilder.append(", hasParent=");
    AccessibilityWindowInfoCompat accessibilityWindowInfoCompat = getParent();
    boolean bool1 = false;
    if (accessibilityWindowInfoCompat != null) {
      bool = true;
    } else {
      bool = false;
    } 
    stringBuilder.append(bool);
    stringBuilder.append(", hasChildren=");
    boolean bool = bool1;
    if (getChildCount() > 0)
      bool = true; 
    stringBuilder.append(bool);
    stringBuilder.append(']');
    return stringBuilder.toString();
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/accessibility/AccessibilityWindowInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */