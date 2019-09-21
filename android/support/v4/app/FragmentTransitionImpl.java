package android.support.v4.app;

import android.graphics.Rect;
import android.support.annotation.RestrictTo;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public abstract class FragmentTransitionImpl {
  protected static void bfsAddViewChildren(List<View> paramList, View paramView) {
    int j = paramList.size();
    if (containedBeforeIndex(paramList, paramView, j))
      return; 
    paramList.add(paramView);
    for (int i = j; i < paramList.size(); i++) {
      paramView = (View)paramList.get(i);
      if (paramView instanceof ViewGroup) {
        ViewGroup viewGroup = (ViewGroup)paramView;
        int k = viewGroup.getChildCount();
        for (byte b = 0; b < k; b++) {
          View view = viewGroup.getChildAt(b);
          if (!containedBeforeIndex(paramList, view, j))
            paramList.add(view); 
        } 
      } 
    } 
  }
  
  private static boolean containedBeforeIndex(List<View> paramList, View paramView, int paramInt) {
    for (byte b = 0; b < paramInt; b++) {
      if (paramList.get(b) == paramView)
        return true; 
    } 
    return false;
  }
  
  static String findKeyForValue(Map<String, String> paramMap, String paramString) {
    for (Map.Entry entry : paramMap.entrySet()) {
      if (paramString.equals(entry.getValue()))
        return (String)entry.getKey(); 
    } 
    return null;
  }
  
  protected static boolean isNullOrEmpty(List paramList) { return (paramList == null || paramList.isEmpty()); }
  
  public abstract void addTarget(Object paramObject, View paramView);
  
  public abstract void addTargets(Object paramObject, ArrayList<View> paramArrayList);
  
  public abstract void beginDelayedTransition(ViewGroup paramViewGroup, Object paramObject);
  
  public abstract boolean canHandle(Object paramObject);
  
  void captureTransitioningViews(ArrayList<View> paramArrayList, View paramView) {
    if (paramView.getVisibility() == 0) {
      ViewGroup viewGroup;
      if (paramView instanceof ViewGroup) {
        viewGroup = (ViewGroup)paramView;
        if (ViewGroupCompat.isTransitionGroup(viewGroup)) {
          paramArrayList.add(viewGroup);
          return;
        } 
        int i = viewGroup.getChildCount();
        for (byte b = 0; b < i; b++)
          captureTransitioningViews(paramArrayList, viewGroup.getChildAt(b)); 
      } else {
        paramArrayList.add(viewGroup);
      } 
    } 
  }
  
  public abstract Object cloneTransition(Object paramObject);
  
  void findNamedViews(Map<String, View> paramMap, View paramView) {
    if (paramView.getVisibility() == 0) {
      String str = ViewCompat.getTransitionName(paramView);
      if (str != null)
        paramMap.put(str, paramView); 
      if (paramView instanceof ViewGroup) {
        ViewGroup viewGroup = (ViewGroup)paramView;
        int i = viewGroup.getChildCount();
        for (byte b = 0; b < i; b++)
          findNamedViews(paramMap, viewGroup.getChildAt(b)); 
      } 
    } 
  }
  
  protected void getBoundsOnScreen(View paramView, Rect paramRect) {
    int[] arrayOfInt = new int[2];
    paramView.getLocationOnScreen(arrayOfInt);
    paramRect.set(arrayOfInt[0], arrayOfInt[1], arrayOfInt[0] + paramView.getWidth(), arrayOfInt[1] + paramView.getHeight());
  }
  
  public abstract Object mergeTransitionsInSequence(Object paramObject1, Object paramObject2, Object paramObject3);
  
  public abstract Object mergeTransitionsTogether(Object paramObject1, Object paramObject2, Object paramObject3);
  
  ArrayList<String> prepareSetNameOverridesReordered(ArrayList<View> paramArrayList) {
    ArrayList arrayList = new ArrayList();
    int i = paramArrayList.size();
    for (byte b = 0; b < i; b++) {
      View view = (View)paramArrayList.get(b);
      arrayList.add(ViewCompat.getTransitionName(view));
      ViewCompat.setTransitionName(view, null);
    } 
    return arrayList;
  }
  
  public abstract void removeTarget(Object paramObject, View paramView);
  
  public abstract void replaceTargets(Object paramObject, ArrayList<View> paramArrayList1, ArrayList<View> paramArrayList2);
  
  public abstract void scheduleHideFragmentView(Object paramObject, View paramView, ArrayList<View> paramArrayList);
  
  void scheduleNameReset(ViewGroup paramViewGroup, final ArrayList<View> sharedElementsIn, final Map<String, String> nameOverrides) { OneShotPreDrawListener.add(paramViewGroup, new Runnable() {
          public void run() {
            int i = sharedElementsIn.size();
            for (byte b = 0; b < i; b++) {
              View view = (View)sharedElementsIn.get(b);
              String str = ViewCompat.getTransitionName(view);
              ViewCompat.setTransitionName(view, (String)nameOverrides.get(str));
            } 
          }
        }); }
  
  public abstract void scheduleRemoveTargets(Object paramObject1, Object paramObject2, ArrayList<View> paramArrayList1, Object paramObject3, ArrayList<View> paramArrayList2, Object paramObject4, ArrayList<View> paramArrayList3);
  
  public abstract void setEpicenter(Object paramObject, Rect paramRect);
  
  public abstract void setEpicenter(Object paramObject, View paramView);
  
  void setNameOverridesOrdered(View paramView, final ArrayList<View> sharedElementsIn, final Map<String, String> nameOverrides) { OneShotPreDrawListener.add(paramView, new Runnable() {
          public void run() {
            int i = sharedElementsIn.size();
            for (byte b = 0; b < i; b++) {
              View view = (View)sharedElementsIn.get(b);
              String str = ViewCompat.getTransitionName(view);
              if (str != null)
                ViewCompat.setTransitionName(view, FragmentTransitionImpl.findKeyForValue(nameOverrides, str)); 
            } 
          }
        }); }
  
  void setNameOverridesReordered(View paramView, final ArrayList<View> sharedElementsOut, final ArrayList<View> sharedElementsIn, final ArrayList<String> inNames, Map<String, String> paramMap) {
    final int numSharedElements = paramArrayList2.size();
    final ArrayList outNames = new ArrayList();
    byte b;
    for (b = 0; b < i; b++) {
      View view = (View)paramArrayList1.get(b);
      String str = ViewCompat.getTransitionName(view);
      arrayList.add(str);
      if (str != null) {
        ViewCompat.setTransitionName(view, null);
        String str1 = (String)paramMap.get(str);
        byte b1;
        for (b1 = 0; b1 < i; b1++) {
          if (str1.equals(paramArrayList3.get(b1))) {
            ViewCompat.setTransitionName((View)paramArrayList2.get(b1), str);
            break;
          } 
        } 
      } 
    } 
    OneShotPreDrawListener.add(paramView, new Runnable() {
          public void run() {
            for (byte b = 0; b < numSharedElements; b++) {
              ViewCompat.setTransitionName((View)sharedElementsIn.get(b), (String)inNames.get(b));
              ViewCompat.setTransitionName((View)sharedElementsOut.get(b), (String)outNames.get(b));
            } 
          }
        });
  }
  
  public abstract void setSharedElementTargets(Object paramObject, View paramView, ArrayList<View> paramArrayList);
  
  public abstract void swapSharedElementTargets(Object paramObject, ArrayList<View> paramArrayList1, ArrayList<View> paramArrayList2);
  
  public abstract Object wrapTransitionInSet(Object paramObject);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/FragmentTransitionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */