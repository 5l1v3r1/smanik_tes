package android.support.v4.view.accessibility;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AccessibilityNodeInfoCompat {
  public static final int ACTION_ACCESSIBILITY_FOCUS = 64;
  
  public static final String ACTION_ARGUMENT_COLUMN_INT = "android.view.accessibility.action.ARGUMENT_COLUMN_INT";
  
  public static final String ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN = "ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN";
  
  public static final String ACTION_ARGUMENT_HTML_ELEMENT_STRING = "ACTION_ARGUMENT_HTML_ELEMENT_STRING";
  
  public static final String ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT = "ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT";
  
  public static final String ACTION_ARGUMENT_PROGRESS_VALUE = "android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE";
  
  public static final String ACTION_ARGUMENT_ROW_INT = "android.view.accessibility.action.ARGUMENT_ROW_INT";
  
  public static final String ACTION_ARGUMENT_SELECTION_END_INT = "ACTION_ARGUMENT_SELECTION_END_INT";
  
  public static final String ACTION_ARGUMENT_SELECTION_START_INT = "ACTION_ARGUMENT_SELECTION_START_INT";
  
  public static final String ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE = "ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE";
  
  public static final int ACTION_CLEAR_ACCESSIBILITY_FOCUS = 128;
  
  public static final int ACTION_CLEAR_FOCUS = 2;
  
  public static final int ACTION_CLEAR_SELECTION = 8;
  
  public static final int ACTION_CLICK = 16;
  
  public static final int ACTION_COLLAPSE = 524288;
  
  public static final int ACTION_COPY = 16384;
  
  public static final int ACTION_CUT = 65536;
  
  public static final int ACTION_DISMISS = 1048576;
  
  public static final int ACTION_EXPAND = 262144;
  
  public static final int ACTION_FOCUS = 1;
  
  public static final int ACTION_LONG_CLICK = 32;
  
  public static final int ACTION_NEXT_AT_MOVEMENT_GRANULARITY = 256;
  
  public static final int ACTION_NEXT_HTML_ELEMENT = 1024;
  
  public static final int ACTION_PASTE = 32768;
  
  public static final int ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = 512;
  
  public static final int ACTION_PREVIOUS_HTML_ELEMENT = 2048;
  
  public static final int ACTION_SCROLL_BACKWARD = 8192;
  
  public static final int ACTION_SCROLL_FORWARD = 4096;
  
  public static final int ACTION_SELECT = 4;
  
  public static final int ACTION_SET_SELECTION = 131072;
  
  public static final int ACTION_SET_TEXT = 2097152;
  
  public static final int FOCUS_ACCESSIBILITY = 2;
  
  public static final int FOCUS_INPUT = 1;
  
  public static final int MOVEMENT_GRANULARITY_CHARACTER = 1;
  
  public static final int MOVEMENT_GRANULARITY_LINE = 4;
  
  public static final int MOVEMENT_GRANULARITY_PAGE = 16;
  
  public static final int MOVEMENT_GRANULARITY_PARAGRAPH = 8;
  
  public static final int MOVEMENT_GRANULARITY_WORD = 2;
  
  private static final String ROLE_DESCRIPTION_KEY = "AccessibilityNodeInfo.roleDescription";
  
  private final AccessibilityNodeInfo mInfo;
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public int mParentVirtualDescendantId = -1;
  
  private AccessibilityNodeInfoCompat(AccessibilityNodeInfo paramAccessibilityNodeInfo) { this.mInfo = paramAccessibilityNodeInfo; }
  
  @Deprecated
  public AccessibilityNodeInfoCompat(Object paramObject) { this.mInfo = (AccessibilityNodeInfo)paramObject; }
  
  private static String getActionSymbolicName(int paramInt) {
    switch (paramInt) {
      default:
        return "ACTION_UNKNOWN";
      case 131072:
        return "ACTION_SET_SELECTION";
      case 65536:
        return "ACTION_CUT";
      case 32768:
        return "ACTION_PASTE";
      case 16384:
        return "ACTION_COPY";
      case 8192:
        return "ACTION_SCROLL_BACKWARD";
      case 4096:
        return "ACTION_SCROLL_FORWARD";
      case 2048:
        return "ACTION_PREVIOUS_HTML_ELEMENT";
      case 1024:
        return "ACTION_NEXT_HTML_ELEMENT";
      case 512:
        return "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
      case 256:
        return "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
      case 128:
        return "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
      case 64:
        return "ACTION_ACCESSIBILITY_FOCUS";
      case 32:
        return "ACTION_LONG_CLICK";
      case 16:
        return "ACTION_CLICK";
      case 8:
        return "ACTION_CLEAR_SELECTION";
      case 4:
        return "ACTION_SELECT";
      case 2:
        return "ACTION_CLEAR_FOCUS";
      case 1:
        break;
    } 
    return "ACTION_FOCUS";
  }
  
  public static AccessibilityNodeInfoCompat obtain() { return wrap(AccessibilityNodeInfo.obtain()); }
  
  public static AccessibilityNodeInfoCompat obtain(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat) { return wrap(AccessibilityNodeInfo.obtain(paramAccessibilityNodeInfoCompat.mInfo)); }
  
  public static AccessibilityNodeInfoCompat obtain(View paramView) { return wrap(AccessibilityNodeInfo.obtain(paramView)); }
  
  public static AccessibilityNodeInfoCompat obtain(View paramView, int paramInt) { return (Build.VERSION.SDK_INT >= 16) ? wrapNonNullInstance(AccessibilityNodeInfo.obtain(paramView, paramInt)) : null; }
  
  public static AccessibilityNodeInfoCompat wrap(@NonNull AccessibilityNodeInfo paramAccessibilityNodeInfo) { return new AccessibilityNodeInfoCompat(paramAccessibilityNodeInfo); }
  
  static AccessibilityNodeInfoCompat wrapNonNullInstance(Object paramObject) { return (paramObject != null) ? new AccessibilityNodeInfoCompat(paramObject) : null; }
  
  public void addAction(int paramInt) { this.mInfo.addAction(paramInt); }
  
  public void addAction(AccessibilityActionCompat paramAccessibilityActionCompat) {
    if (Build.VERSION.SDK_INT >= 21)
      this.mInfo.addAction((AccessibilityNodeInfo.AccessibilityAction)paramAccessibilityActionCompat.mAction); 
  }
  
  public void addChild(View paramView) { this.mInfo.addChild(paramView); }
  
  public void addChild(View paramView, int paramInt) {
    if (Build.VERSION.SDK_INT >= 16)
      this.mInfo.addChild(paramView, paramInt); 
  }
  
  public boolean canOpenPopup() { return (Build.VERSION.SDK_INT >= 19) ? this.mInfo.canOpenPopup() : 0; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null)
      return false; 
    if (getClass() != paramObject.getClass())
      return false; 
    paramObject = (AccessibilityNodeInfoCompat)paramObject;
    if (this.mInfo == null) {
      if (paramObject.mInfo != null)
        return false; 
    } else if (!this.mInfo.equals(paramObject.mInfo)) {
      return false;
    } 
    return true;
  }
  
  public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText(String paramString) {
    ArrayList arrayList = new ArrayList();
    List list = this.mInfo.findAccessibilityNodeInfosByText(paramString);
    int i = list.size();
    for (byte b = 0; b < i; b++)
      arrayList.add(wrap((AccessibilityNodeInfo)list.get(b))); 
    return arrayList;
  }
  
  public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByViewId(String paramString) {
    if (Build.VERSION.SDK_INT >= 18) {
      List list = this.mInfo.findAccessibilityNodeInfosByViewId(paramString);
      ArrayList arrayList = new ArrayList();
      Iterator iterator = list.iterator();
      while (iterator.hasNext())
        arrayList.add(wrap((AccessibilityNodeInfo)iterator.next())); 
      return arrayList;
    } 
    return Collections.emptyList();
  }
  
  public AccessibilityNodeInfoCompat findFocus(int paramInt) { return (Build.VERSION.SDK_INT >= 16) ? wrapNonNullInstance(this.mInfo.findFocus(paramInt)) : null; }
  
  public AccessibilityNodeInfoCompat focusSearch(int paramInt) { return (Build.VERSION.SDK_INT >= 16) ? wrapNonNullInstance(this.mInfo.focusSearch(paramInt)) : null; }
  
  public List<AccessibilityActionCompat> getActionList() {
    Object object;
    if (Build.VERSION.SDK_INT >= 21) {
      object = this.mInfo.getActionList();
    } else {
      object = null;
    } 
    if (object != null) {
      ArrayList arrayList = new ArrayList();
      int i = object.size();
      for (byte b = 0; b < i; b++)
        arrayList.add(new AccessibilityActionCompat(object.get(b))); 
      return arrayList;
    } 
    return Collections.emptyList();
  }
  
  public int getActions() { return this.mInfo.getActions(); }
  
  public void getBoundsInParent(Rect paramRect) { this.mInfo.getBoundsInParent(paramRect); }
  
  public void getBoundsInScreen(Rect paramRect) { this.mInfo.getBoundsInScreen(paramRect); }
  
  public AccessibilityNodeInfoCompat getChild(int paramInt) { return wrapNonNullInstance(this.mInfo.getChild(paramInt)); }
  
  public int getChildCount() { return this.mInfo.getChildCount(); }
  
  public CharSequence getClassName() { return this.mInfo.getClassName(); }
  
  public CollectionInfoCompat getCollectionInfo() {
    if (Build.VERSION.SDK_INT >= 19) {
      AccessibilityNodeInfo.CollectionInfo collectionInfo = this.mInfo.getCollectionInfo();
      if (collectionInfo != null)
        return new CollectionInfoCompat(collectionInfo); 
    } 
    return null;
  }
  
  public CollectionItemInfoCompat getCollectionItemInfo() {
    if (Build.VERSION.SDK_INT >= 19) {
      AccessibilityNodeInfo.CollectionItemInfo collectionItemInfo = this.mInfo.getCollectionItemInfo();
      if (collectionItemInfo != null)
        return new CollectionItemInfoCompat(collectionItemInfo); 
    } 
    return null;
  }
  
  public CharSequence getContentDescription() { return this.mInfo.getContentDescription(); }
  
  public int getDrawingOrder() { return (Build.VERSION.SDK_INT >= 24) ? this.mInfo.getDrawingOrder() : 0; }
  
  public CharSequence getError() { return (Build.VERSION.SDK_INT >= 21) ? this.mInfo.getError() : null; }
  
  public Bundle getExtras() { return (Build.VERSION.SDK_INT >= 19) ? this.mInfo.getExtras() : new Bundle(); }
  
  @Deprecated
  public Object getInfo() { return this.mInfo; }
  
  public int getInputType() { return (Build.VERSION.SDK_INT >= 19) ? this.mInfo.getInputType() : 0; }
  
  public AccessibilityNodeInfoCompat getLabelFor() { return (Build.VERSION.SDK_INT >= 17) ? wrapNonNullInstance(this.mInfo.getLabelFor()) : null; }
  
  public AccessibilityNodeInfoCompat getLabeledBy() { return (Build.VERSION.SDK_INT >= 17) ? wrapNonNullInstance(this.mInfo.getLabeledBy()) : null; }
  
  public int getLiveRegion() { return (Build.VERSION.SDK_INT >= 19) ? this.mInfo.getLiveRegion() : 0; }
  
  public int getMaxTextLength() { return (Build.VERSION.SDK_INT >= 21) ? this.mInfo.getMaxTextLength() : -1; }
  
  public int getMovementGranularities() { return (Build.VERSION.SDK_INT >= 16) ? this.mInfo.getMovementGranularities() : 0; }
  
  public CharSequence getPackageName() { return this.mInfo.getPackageName(); }
  
  public AccessibilityNodeInfoCompat getParent() { return wrapNonNullInstance(this.mInfo.getParent()); }
  
  public RangeInfoCompat getRangeInfo() {
    if (Build.VERSION.SDK_INT >= 19) {
      AccessibilityNodeInfo.RangeInfo rangeInfo = this.mInfo.getRangeInfo();
      if (rangeInfo != null)
        return new RangeInfoCompat(rangeInfo); 
    } 
    return null;
  }
  
  @Nullable
  public CharSequence getRoleDescription() { return (Build.VERSION.SDK_INT >= 19) ? this.mInfo.getExtras().getCharSequence("AccessibilityNodeInfo.roleDescription") : null; }
  
  public CharSequence getText() { return this.mInfo.getText(); }
  
  public int getTextSelectionEnd() { return (Build.VERSION.SDK_INT >= 18) ? this.mInfo.getTextSelectionEnd() : -1; }
  
  public int getTextSelectionStart() { return (Build.VERSION.SDK_INT >= 18) ? this.mInfo.getTextSelectionStart() : -1; }
  
  public AccessibilityNodeInfoCompat getTraversalAfter() { return (Build.VERSION.SDK_INT >= 22) ? wrapNonNullInstance(this.mInfo.getTraversalAfter()) : null; }
  
  public AccessibilityNodeInfoCompat getTraversalBefore() { return (Build.VERSION.SDK_INT >= 22) ? wrapNonNullInstance(this.mInfo.getTraversalBefore()) : null; }
  
  public String getViewIdResourceName() { return (Build.VERSION.SDK_INT >= 18) ? this.mInfo.getViewIdResourceName() : null; }
  
  public AccessibilityWindowInfoCompat getWindow() { return (Build.VERSION.SDK_INT >= 21) ? AccessibilityWindowInfoCompat.wrapNonNullInstance(this.mInfo.getWindow()) : null; }
  
  public int getWindowId() { return this.mInfo.getWindowId(); }
  
  public int hashCode() { return (this.mInfo == null) ? 0 : this.mInfo.hashCode(); }
  
  public boolean isAccessibilityFocused() { return (Build.VERSION.SDK_INT >= 16) ? this.mInfo.isAccessibilityFocused() : 0; }
  
  public boolean isCheckable() { return this.mInfo.isCheckable(); }
  
  public boolean isChecked() { return this.mInfo.isChecked(); }
  
  public boolean isClickable() { return this.mInfo.isClickable(); }
  
  public boolean isContentInvalid() { return (Build.VERSION.SDK_INT >= 19) ? this.mInfo.isContentInvalid() : 0; }
  
  public boolean isContextClickable() { return (Build.VERSION.SDK_INT >= 23) ? this.mInfo.isContextClickable() : 0; }
  
  public boolean isDismissable() { return (Build.VERSION.SDK_INT >= 19) ? this.mInfo.isDismissable() : 0; }
  
  public boolean isEditable() { return (Build.VERSION.SDK_INT >= 18) ? this.mInfo.isEditable() : 0; }
  
  public boolean isEnabled() { return this.mInfo.isEnabled(); }
  
  public boolean isFocusable() { return this.mInfo.isFocusable(); }
  
  public boolean isFocused() { return this.mInfo.isFocused(); }
  
  public boolean isImportantForAccessibility() { return (Build.VERSION.SDK_INT >= 24) ? this.mInfo.isImportantForAccessibility() : 1; }
  
  public boolean isLongClickable() { return this.mInfo.isLongClickable(); }
  
  public boolean isMultiLine() { return (Build.VERSION.SDK_INT >= 19) ? this.mInfo.isMultiLine() : 0; }
  
  public boolean isPassword() { return this.mInfo.isPassword(); }
  
  public boolean isScrollable() { return this.mInfo.isScrollable(); }
  
  public boolean isSelected() { return this.mInfo.isSelected(); }
  
  public boolean isVisibleToUser() { return (Build.VERSION.SDK_INT >= 16) ? this.mInfo.isVisibleToUser() : 0; }
  
  public boolean performAction(int paramInt) { return this.mInfo.performAction(paramInt); }
  
  public boolean performAction(int paramInt, Bundle paramBundle) { return (Build.VERSION.SDK_INT >= 16) ? this.mInfo.performAction(paramInt, paramBundle) : 0; }
  
  public void recycle() { this.mInfo.recycle(); }
  
  public boolean refresh() { return (Build.VERSION.SDK_INT >= 18) ? this.mInfo.refresh() : 0; }
  
  public boolean removeAction(AccessibilityActionCompat paramAccessibilityActionCompat) { return (Build.VERSION.SDK_INT >= 21) ? this.mInfo.removeAction((AccessibilityNodeInfo.AccessibilityAction)paramAccessibilityActionCompat.mAction) : 0; }
  
  public boolean removeChild(View paramView) { return (Build.VERSION.SDK_INT >= 21) ? this.mInfo.removeChild(paramView) : 0; }
  
  public boolean removeChild(View paramView, int paramInt) { return (Build.VERSION.SDK_INT >= 21) ? this.mInfo.removeChild(paramView, paramInt) : 0; }
  
  public void setAccessibilityFocused(boolean paramBoolean) {
    if (Build.VERSION.SDK_INT >= 16)
      this.mInfo.setAccessibilityFocused(paramBoolean); 
  }
  
  public void setBoundsInParent(Rect paramRect) { this.mInfo.setBoundsInParent(paramRect); }
  
  public void setBoundsInScreen(Rect paramRect) { this.mInfo.setBoundsInScreen(paramRect); }
  
  public void setCanOpenPopup(boolean paramBoolean) {
    if (Build.VERSION.SDK_INT >= 19)
      this.mInfo.setCanOpenPopup(paramBoolean); 
  }
  
  public void setCheckable(boolean paramBoolean) { this.mInfo.setCheckable(paramBoolean); }
  
  public void setChecked(boolean paramBoolean) { this.mInfo.setChecked(paramBoolean); }
  
  public void setClassName(CharSequence paramCharSequence) { this.mInfo.setClassName(paramCharSequence); }
  
  public void setClickable(boolean paramBoolean) { this.mInfo.setClickable(paramBoolean); }
  
  public void setCollectionInfo(Object paramObject) {
    if (Build.VERSION.SDK_INT >= 19)
      this.mInfo.setCollectionInfo((AccessibilityNodeInfo.CollectionInfo)((CollectionInfoCompat)paramObject).mInfo); 
  }
  
  public void setCollectionItemInfo(Object paramObject) {
    if (Build.VERSION.SDK_INT >= 19)
      this.mInfo.setCollectionItemInfo((AccessibilityNodeInfo.CollectionItemInfo)((CollectionItemInfoCompat)paramObject).mInfo); 
  }
  
  public void setContentDescription(CharSequence paramCharSequence) { this.mInfo.setContentDescription(paramCharSequence); }
  
  public void setContentInvalid(boolean paramBoolean) {
    if (Build.VERSION.SDK_INT >= 19)
      this.mInfo.setContentInvalid(paramBoolean); 
  }
  
  public void setContextClickable(boolean paramBoolean) {
    if (Build.VERSION.SDK_INT >= 23)
      this.mInfo.setContextClickable(paramBoolean); 
  }
  
  public void setDismissable(boolean paramBoolean) {
    if (Build.VERSION.SDK_INT >= 19)
      this.mInfo.setDismissable(paramBoolean); 
  }
  
  public void setDrawingOrder(int paramInt) {
    if (Build.VERSION.SDK_INT >= 24)
      this.mInfo.setDrawingOrder(paramInt); 
  }
  
  public void setEditable(boolean paramBoolean) {
    if (Build.VERSION.SDK_INT >= 18)
      this.mInfo.setEditable(paramBoolean); 
  }
  
  public void setEnabled(boolean paramBoolean) { this.mInfo.setEnabled(paramBoolean); }
  
  public void setError(CharSequence paramCharSequence) {
    if (Build.VERSION.SDK_INT >= 21)
      this.mInfo.setError(paramCharSequence); 
  }
  
  public void setFocusable(boolean paramBoolean) { this.mInfo.setFocusable(paramBoolean); }
  
  public void setFocused(boolean paramBoolean) { this.mInfo.setFocused(paramBoolean); }
  
  public void setImportantForAccessibility(boolean paramBoolean) {
    if (Build.VERSION.SDK_INT >= 24)
      this.mInfo.setImportantForAccessibility(paramBoolean); 
  }
  
  public void setInputType(int paramInt) {
    if (Build.VERSION.SDK_INT >= 19)
      this.mInfo.setInputType(paramInt); 
  }
  
  public void setLabelFor(View paramView) {
    if (Build.VERSION.SDK_INT >= 17)
      this.mInfo.setLabelFor(paramView); 
  }
  
  public void setLabelFor(View paramView, int paramInt) {
    if (Build.VERSION.SDK_INT >= 17)
      this.mInfo.setLabelFor(paramView, paramInt); 
  }
  
  public void setLabeledBy(View paramView) {
    if (Build.VERSION.SDK_INT >= 17)
      this.mInfo.setLabeledBy(paramView); 
  }
  
  public void setLabeledBy(View paramView, int paramInt) {
    if (Build.VERSION.SDK_INT >= 17)
      this.mInfo.setLabeledBy(paramView, paramInt); 
  }
  
  public void setLiveRegion(int paramInt) {
    if (Build.VERSION.SDK_INT >= 19)
      this.mInfo.setLiveRegion(paramInt); 
  }
  
  public void setLongClickable(boolean paramBoolean) { this.mInfo.setLongClickable(paramBoolean); }
  
  public void setMaxTextLength(int paramInt) {
    if (Build.VERSION.SDK_INT >= 21)
      this.mInfo.setMaxTextLength(paramInt); 
  }
  
  public void setMovementGranularities(int paramInt) {
    if (Build.VERSION.SDK_INT >= 16)
      this.mInfo.setMovementGranularities(paramInt); 
  }
  
  public void setMultiLine(boolean paramBoolean) {
    if (Build.VERSION.SDK_INT >= 19)
      this.mInfo.setMultiLine(paramBoolean); 
  }
  
  public void setPackageName(CharSequence paramCharSequence) { this.mInfo.setPackageName(paramCharSequence); }
  
  public void setParent(View paramView) { this.mInfo.setParent(paramView); }
  
  public void setParent(View paramView, int paramInt) {
    this.mParentVirtualDescendantId = paramInt;
    if (Build.VERSION.SDK_INT >= 16)
      this.mInfo.setParent(paramView, paramInt); 
  }
  
  public void setPassword(boolean paramBoolean) { this.mInfo.setPassword(paramBoolean); }
  
  public void setRangeInfo(RangeInfoCompat paramRangeInfoCompat) {
    if (Build.VERSION.SDK_INT >= 19)
      this.mInfo.setRangeInfo((AccessibilityNodeInfo.RangeInfo)paramRangeInfoCompat.mInfo); 
  }
  
  public void setRoleDescription(@Nullable CharSequence paramCharSequence) {
    if (Build.VERSION.SDK_INT >= 19)
      this.mInfo.getExtras().putCharSequence("AccessibilityNodeInfo.roleDescription", paramCharSequence); 
  }
  
  public void setScrollable(boolean paramBoolean) { this.mInfo.setScrollable(paramBoolean); }
  
  public void setSelected(boolean paramBoolean) { this.mInfo.setSelected(paramBoolean); }
  
  public void setSource(View paramView) { this.mInfo.setSource(paramView); }
  
  public void setSource(View paramView, int paramInt) {
    if (Build.VERSION.SDK_INT >= 16)
      this.mInfo.setSource(paramView, paramInt); 
  }
  
  public void setText(CharSequence paramCharSequence) { this.mInfo.setText(paramCharSequence); }
  
  public void setTextSelection(int paramInt1, int paramInt2) {
    if (Build.VERSION.SDK_INT >= 18)
      this.mInfo.setTextSelection(paramInt1, paramInt2); 
  }
  
  public void setTraversalAfter(View paramView) {
    if (Build.VERSION.SDK_INT >= 22)
      this.mInfo.setTraversalAfter(paramView); 
  }
  
  public void setTraversalAfter(View paramView, int paramInt) {
    if (Build.VERSION.SDK_INT >= 22)
      this.mInfo.setTraversalAfter(paramView, paramInt); 
  }
  
  public void setTraversalBefore(View paramView) {
    if (Build.VERSION.SDK_INT >= 22)
      this.mInfo.setTraversalBefore(paramView); 
  }
  
  public void setTraversalBefore(View paramView, int paramInt) {
    if (Build.VERSION.SDK_INT >= 22)
      this.mInfo.setTraversalBefore(paramView, paramInt); 
  }
  
  public void setViewIdResourceName(String paramString) {
    if (Build.VERSION.SDK_INT >= 18)
      this.mInfo.setViewIdResourceName(paramString); 
  }
  
  public void setVisibleToUser(boolean paramBoolean) {
    if (Build.VERSION.SDK_INT >= 16)
      this.mInfo.setVisibleToUser(paramBoolean); 
  }
  
  public String toString() {
    StringBuilder stringBuilder1 = new StringBuilder();
    stringBuilder1.append(super.toString());
    Rect rect = new Rect();
    getBoundsInParent(rect);
    StringBuilder stringBuilder3 = new StringBuilder();
    stringBuilder3.append("; boundsInParent: ");
    stringBuilder3.append(rect);
    stringBuilder1.append(stringBuilder3.toString());
    getBoundsInScreen(rect);
    stringBuilder3 = new StringBuilder();
    stringBuilder3.append("; boundsInScreen: ");
    stringBuilder3.append(rect);
    stringBuilder1.append(stringBuilder3.toString());
    stringBuilder1.append("; packageName: ");
    stringBuilder1.append(getPackageName());
    stringBuilder1.append("; className: ");
    stringBuilder1.append(getClassName());
    stringBuilder1.append("; text: ");
    stringBuilder1.append(getText());
    stringBuilder1.append("; contentDescription: ");
    stringBuilder1.append(getContentDescription());
    stringBuilder1.append("; viewId: ");
    stringBuilder1.append(getViewIdResourceName());
    stringBuilder1.append("; checkable: ");
    stringBuilder1.append(isCheckable());
    stringBuilder1.append("; checked: ");
    stringBuilder1.append(isChecked());
    stringBuilder1.append("; focusable: ");
    stringBuilder1.append(isFocusable());
    stringBuilder1.append("; focused: ");
    stringBuilder1.append(isFocused());
    stringBuilder1.append("; selected: ");
    stringBuilder1.append(isSelected());
    stringBuilder1.append("; clickable: ");
    stringBuilder1.append(isClickable());
    stringBuilder1.append("; longClickable: ");
    stringBuilder1.append(isLongClickable());
    stringBuilder1.append("; enabled: ");
    stringBuilder1.append(isEnabled());
    stringBuilder1.append("; password: ");
    stringBuilder1.append(isPassword());
    StringBuilder stringBuilder2 = new StringBuilder();
    stringBuilder2.append("; scrollable: ");
    stringBuilder2.append(isScrollable());
    stringBuilder1.append(stringBuilder2.toString());
    stringBuilder1.append("; [");
    int i = getActions();
    while (i != 0) {
      int k = 1 << Integer.numberOfTrailingZeros(i);
      int j = i & (k ^ 0xFFFFFFFF);
      stringBuilder1.append(getActionSymbolicName(k));
      i = j;
      if (j != 0) {
        stringBuilder1.append(", ");
        i = j;
      } 
    } 
    stringBuilder1.append("]");
    return stringBuilder1.toString();
  }
  
  public AccessibilityNodeInfo unwrap() { return this.mInfo; }
  
  public static class AccessibilityActionCompat {
    public static final AccessibilityActionCompat ACTION_ACCESSIBILITY_FOCUS;
    
    public static final AccessibilityActionCompat ACTION_CLEAR_ACCESSIBILITY_FOCUS;
    
    public static final AccessibilityActionCompat ACTION_CLEAR_FOCUS;
    
    public static final AccessibilityActionCompat ACTION_CLEAR_SELECTION;
    
    public static final AccessibilityActionCompat ACTION_CLICK;
    
    public static final AccessibilityActionCompat ACTION_COLLAPSE;
    
    public static final AccessibilityActionCompat ACTION_CONTEXT_CLICK;
    
    public static final AccessibilityActionCompat ACTION_COPY;
    
    public static final AccessibilityActionCompat ACTION_CUT;
    
    public static final AccessibilityActionCompat ACTION_DISMISS;
    
    public static final AccessibilityActionCompat ACTION_EXPAND;
    
    public static final AccessibilityActionCompat ACTION_FOCUS;
    
    public static final AccessibilityActionCompat ACTION_LONG_CLICK;
    
    public static final AccessibilityActionCompat ACTION_NEXT_AT_MOVEMENT_GRANULARITY;
    
    public static final AccessibilityActionCompat ACTION_NEXT_HTML_ELEMENT;
    
    public static final AccessibilityActionCompat ACTION_PASTE;
    
    public static final AccessibilityActionCompat ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY;
    
    public static final AccessibilityActionCompat ACTION_PREVIOUS_HTML_ELEMENT;
    
    public static final AccessibilityActionCompat ACTION_SCROLL_BACKWARD;
    
    public static final AccessibilityActionCompat ACTION_SCROLL_DOWN;
    
    public static final AccessibilityActionCompat ACTION_SCROLL_FORWARD;
    
    public static final AccessibilityActionCompat ACTION_SCROLL_LEFT;
    
    public static final AccessibilityActionCompat ACTION_SCROLL_RIGHT;
    
    public static final AccessibilityActionCompat ACTION_SCROLL_TO_POSITION;
    
    public static final AccessibilityActionCompat ACTION_SCROLL_UP;
    
    public static final AccessibilityActionCompat ACTION_SELECT;
    
    public static final AccessibilityActionCompat ACTION_SET_PROGRESS;
    
    public static final AccessibilityActionCompat ACTION_SET_SELECTION;
    
    public static final AccessibilityActionCompat ACTION_SET_TEXT;
    
    public static final AccessibilityActionCompat ACTION_SHOW_ON_SCREEN;
    
    final Object mAction;
    
    static  {
      AccessibilityNodeInfo.AccessibilityAction accessibilityAction2 = null;
      ACTION_FOCUS = new AccessibilityActionCompat(1, null);
      ACTION_CLEAR_FOCUS = new AccessibilityActionCompat(2, null);
      ACTION_SELECT = new AccessibilityActionCompat(4, null);
      ACTION_CLEAR_SELECTION = new AccessibilityActionCompat(8, null);
      ACTION_CLICK = new AccessibilityActionCompat(16, null);
      ACTION_LONG_CLICK = new AccessibilityActionCompat(32, null);
      ACTION_ACCESSIBILITY_FOCUS = new AccessibilityActionCompat(64, null);
      ACTION_CLEAR_ACCESSIBILITY_FOCUS = new AccessibilityActionCompat(128, null);
      ACTION_NEXT_AT_MOVEMENT_GRANULARITY = new AccessibilityActionCompat(256, null);
      ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = new AccessibilityActionCompat(512, null);
      ACTION_NEXT_HTML_ELEMENT = new AccessibilityActionCompat(1024, null);
      ACTION_PREVIOUS_HTML_ELEMENT = new AccessibilityActionCompat(2048, null);
      ACTION_SCROLL_FORWARD = new AccessibilityActionCompat(4096, null);
      ACTION_SCROLL_BACKWARD = new AccessibilityActionCompat(8192, null);
      ACTION_COPY = new AccessibilityActionCompat(16384, null);
      ACTION_PASTE = new AccessibilityActionCompat(32768, null);
      ACTION_CUT = new AccessibilityActionCompat(65536, null);
      ACTION_SET_SELECTION = new AccessibilityActionCompat(131072, null);
      ACTION_EXPAND = new AccessibilityActionCompat(262144, null);
      ACTION_COLLAPSE = new AccessibilityActionCompat(524288, null);
      ACTION_DISMISS = new AccessibilityActionCompat(1048576, null);
      ACTION_SET_TEXT = new AccessibilityActionCompat(2097152, null);
      if (Build.VERSION.SDK_INT >= 23) {
        accessibilityAction1 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_ON_SCREEN;
      } else {
        accessibilityAction1 = null;
      } 
      ACTION_SHOW_ON_SCREEN = new AccessibilityActionCompat(accessibilityAction1);
      if (Build.VERSION.SDK_INT >= 23) {
        accessibilityAction1 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_TO_POSITION;
      } else {
        accessibilityAction1 = null;
      } 
      ACTION_SCROLL_TO_POSITION = new AccessibilityActionCompat(accessibilityAction1);
      if (Build.VERSION.SDK_INT >= 23) {
        accessibilityAction1 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP;
      } else {
        accessibilityAction1 = null;
      } 
      ACTION_SCROLL_UP = new AccessibilityActionCompat(accessibilityAction1);
      if (Build.VERSION.SDK_INT >= 23) {
        accessibilityAction1 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT;
      } else {
        accessibilityAction1 = null;
      } 
      ACTION_SCROLL_LEFT = new AccessibilityActionCompat(accessibilityAction1);
      if (Build.VERSION.SDK_INT >= 23) {
        accessibilityAction1 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN;
      } else {
        accessibilityAction1 = null;
      } 
      ACTION_SCROLL_DOWN = new AccessibilityActionCompat(accessibilityAction1);
      if (Build.VERSION.SDK_INT >= 23) {
        accessibilityAction1 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT;
      } else {
        accessibilityAction1 = null;
      } 
      ACTION_SCROLL_RIGHT = new AccessibilityActionCompat(accessibilityAction1);
      if (Build.VERSION.SDK_INT >= 23) {
        accessibilityAction1 = AccessibilityNodeInfo.AccessibilityAction.ACTION_CONTEXT_CLICK;
      } else {
        accessibilityAction1 = null;
      } 
      ACTION_CONTEXT_CLICK = new AccessibilityActionCompat(accessibilityAction1);
      AccessibilityNodeInfo.AccessibilityAction accessibilityAction1 = accessibilityAction2;
      if (Build.VERSION.SDK_INT >= 24)
        accessibilityAction1 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS; 
      ACTION_SET_PROGRESS = new AccessibilityActionCompat(accessibilityAction1);
    }
    
    public AccessibilityActionCompat(int param1Int, CharSequence param1CharSequence) { this(param1CharSequence); }
    
    AccessibilityActionCompat(Object param1Object) { this.mAction = param1Object; }
    
    public int getId() { return (Build.VERSION.SDK_INT >= 21) ? ((AccessibilityNodeInfo.AccessibilityAction)this.mAction).getId() : 0; }
    
    public CharSequence getLabel() { return (Build.VERSION.SDK_INT >= 21) ? ((AccessibilityNodeInfo.AccessibilityAction)this.mAction).getLabel() : null; }
  }
  
  public static class CollectionInfoCompat {
    public static final int SELECTION_MODE_MULTIPLE = 2;
    
    public static final int SELECTION_MODE_NONE = 0;
    
    public static final int SELECTION_MODE_SINGLE = 1;
    
    final Object mInfo;
    
    CollectionInfoCompat(Object param1Object) { this.mInfo = param1Object; }
    
    public static CollectionInfoCompat obtain(int param1Int1, int param1Int2, boolean param1Boolean) { return (Build.VERSION.SDK_INT >= 19) ? new CollectionInfoCompat(AccessibilityNodeInfo.CollectionInfo.obtain(param1Int1, param1Int2, param1Boolean)) : new CollectionInfoCompat(null); }
    
    public static CollectionInfoCompat obtain(int param1Int1, int param1Int2, boolean param1Boolean, int param1Int3) { return (Build.VERSION.SDK_INT >= 21) ? new CollectionInfoCompat(AccessibilityNodeInfo.CollectionInfo.obtain(param1Int1, param1Int2, param1Boolean, param1Int3)) : ((Build.VERSION.SDK_INT >= 19) ? new CollectionInfoCompat(AccessibilityNodeInfo.CollectionInfo.obtain(param1Int1, param1Int2, param1Boolean)) : new CollectionInfoCompat(null)); }
    
    public int getColumnCount() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.CollectionInfo)this.mInfo).getColumnCount() : 0; }
    
    public int getRowCount() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.CollectionInfo)this.mInfo).getRowCount() : 0; }
    
    public int getSelectionMode() { return (Build.VERSION.SDK_INT >= 21) ? ((AccessibilityNodeInfo.CollectionInfo)this.mInfo).getSelectionMode() : 0; }
    
    public boolean isHierarchical() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.CollectionInfo)this.mInfo).isHierarchical() : 0; }
  }
  
  public static class CollectionItemInfoCompat {
    final Object mInfo;
    
    CollectionItemInfoCompat(Object param1Object) { this.mInfo = param1Object; }
    
    public static CollectionItemInfoCompat obtain(int param1Int1, int param1Int2, int param1Int3, int param1Int4, boolean param1Boolean) { return (Build.VERSION.SDK_INT >= 19) ? new CollectionItemInfoCompat(AccessibilityNodeInfo.CollectionItemInfo.obtain(param1Int1, param1Int2, param1Int3, param1Int4, param1Boolean)) : new CollectionItemInfoCompat(null); }
    
    public static CollectionItemInfoCompat obtain(int param1Int1, int param1Int2, int param1Int3, int param1Int4, boolean param1Boolean1, boolean param1Boolean2) { return (Build.VERSION.SDK_INT >= 21) ? new CollectionItemInfoCompat(AccessibilityNodeInfo.CollectionItemInfo.obtain(param1Int1, param1Int2, param1Int3, param1Int4, param1Boolean1, param1Boolean2)) : ((Build.VERSION.SDK_INT >= 19) ? new CollectionItemInfoCompat(AccessibilityNodeInfo.CollectionItemInfo.obtain(param1Int1, param1Int2, param1Int3, param1Int4, param1Boolean1)) : new CollectionItemInfoCompat(null)); }
    
    public int getColumnIndex() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.CollectionItemInfo)this.mInfo).getColumnIndex() : 0; }
    
    public int getColumnSpan() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.CollectionItemInfo)this.mInfo).getColumnSpan() : 0; }
    
    public int getRowIndex() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.CollectionItemInfo)this.mInfo).getRowIndex() : 0; }
    
    public int getRowSpan() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.CollectionItemInfo)this.mInfo).getRowSpan() : 0; }
    
    public boolean isHeading() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.CollectionItemInfo)this.mInfo).isHeading() : 0; }
    
    public boolean isSelected() { return (Build.VERSION.SDK_INT >= 21) ? ((AccessibilityNodeInfo.CollectionItemInfo)this.mInfo).isSelected() : 0; }
  }
  
  public static class RangeInfoCompat {
    public static final int RANGE_TYPE_FLOAT = 1;
    
    public static final int RANGE_TYPE_INT = 0;
    
    public static final int RANGE_TYPE_PERCENT = 2;
    
    final Object mInfo;
    
    RangeInfoCompat(Object param1Object) { this.mInfo = param1Object; }
    
    public static RangeInfoCompat obtain(int param1Int, float param1Float1, float param1Float2, float param1Float3) { return (Build.VERSION.SDK_INT >= 19) ? new RangeInfoCompat(AccessibilityNodeInfo.RangeInfo.obtain(param1Int, param1Float1, param1Float2, param1Float3)) : new RangeInfoCompat(null); }
    
    public float getCurrent() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.RangeInfo)this.mInfo).getCurrent() : 0.0F; }
    
    public float getMax() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.RangeInfo)this.mInfo).getMax() : 0.0F; }
    
    public float getMin() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.RangeInfo)this.mInfo).getMin() : 0.0F; }
    
    public int getType() { return (Build.VERSION.SDK_INT >= 19) ? ((AccessibilityNodeInfo.RangeInfo)this.mInfo).getType() : 0; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/accessibility/AccessibilityNodeInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */