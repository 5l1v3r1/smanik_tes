package android.support.transition;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.app.FragmentTransitionImpl;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class FragmentTransitionSupport extends FragmentTransitionImpl {
  private static boolean hasSimpleTarget(Transition paramTransition) { return (!isNullOrEmpty(paramTransition.getTargetIds()) || !isNullOrEmpty(paramTransition.getTargetNames()) || !isNullOrEmpty(paramTransition.getTargetTypes())); }
  
  public void addTarget(Object paramObject, View paramView) {
    if (paramObject != null)
      ((Transition)paramObject).addTarget(paramView); 
  }
  
  public void addTargets(Object paramObject, ArrayList<View> paramArrayList) {
    paramObject = (Transition)paramObject;
    if (paramObject == null)
      return; 
    boolean bool = paramObject instanceof TransitionSet;
    int j = 0;
    int i = 0;
    if (bool) {
      paramObject = (TransitionSet)paramObject;
      j = paramObject.getTransitionCount();
      while (i < j) {
        addTargets(paramObject.getTransitionAt(i), paramArrayList);
        i++;
      } 
    } else if (!hasSimpleTarget(paramObject) && isNullOrEmpty(paramObject.getTargets())) {
      int k = paramArrayList.size();
      for (i = j; i < k; i++)
        paramObject.addTarget((View)paramArrayList.get(i)); 
    } 
  }
  
  public void beginDelayedTransition(ViewGroup paramViewGroup, Object paramObject) { TransitionManager.beginDelayedTransition(paramViewGroup, (Transition)paramObject); }
  
  public boolean canHandle(Object paramObject) { return paramObject instanceof Transition; }
  
  public Object cloneTransition(Object paramObject) { return (paramObject != null) ? ((Transition)paramObject).clone() : null; }
  
  public Object mergeTransitionsInSequence(Object paramObject1, Object paramObject2, Object paramObject3) {
    paramObject1 = (Transition)paramObject1;
    paramObject2 = (Transition)paramObject2;
    paramObject3 = (Transition)paramObject3;
    if (paramObject1 != null && paramObject2 != null) {
      paramObject1 = (new TransitionSet()).addTransition(paramObject1).addTransition(paramObject2).setOrdering(1);
    } else if (paramObject1 == null) {
      if (paramObject2 != null) {
        paramObject1 = paramObject2;
      } else {
        paramObject1 = null;
      } 
    } 
    if (paramObject3 != null) {
      paramObject2 = new TransitionSet();
      if (paramObject1 != null)
        paramObject2.addTransition(paramObject1); 
      paramObject2.addTransition(paramObject3);
      return paramObject2;
    } 
    return paramObject1;
  }
  
  public Object mergeTransitionsTogether(Object paramObject1, Object paramObject2, Object paramObject3) {
    TransitionSet transitionSet = new TransitionSet();
    if (paramObject1 != null)
      transitionSet.addTransition((Transition)paramObject1); 
    if (paramObject2 != null)
      transitionSet.addTransition((Transition)paramObject2); 
    if (paramObject3 != null)
      transitionSet.addTransition((Transition)paramObject3); 
    return transitionSet;
  }
  
  public void removeTarget(Object paramObject, View paramView) {
    if (paramObject != null)
      ((Transition)paramObject).removeTarget(paramView); 
  }
  
  public void replaceTargets(Object paramObject, ArrayList<View> paramArrayList1, ArrayList<View> paramArrayList2) {
    paramObject = (Transition)paramObject;
    boolean bool = paramObject instanceof TransitionSet;
    int j = 0;
    int i = 0;
    if (bool) {
      paramObject = (TransitionSet)paramObject;
      j = paramObject.getTransitionCount();
      while (i < j) {
        replaceTargets(paramObject.getTransitionAt(i), paramArrayList1, paramArrayList2);
        i++;
      } 
    } else if (!hasSimpleTarget(paramObject)) {
      List list = paramObject.getTargets();
      if (list.size() == paramArrayList1.size() && list.containsAll(paramArrayList1)) {
        if (paramArrayList2 == null) {
          i = 0;
        } else {
          i = paramArrayList2.size();
        } 
        while (j < i) {
          paramObject.addTarget((View)paramArrayList2.get(j));
          j++;
        } 
        for (i = paramArrayList1.size() - 1; i >= 0; i--)
          paramObject.removeTarget((View)paramArrayList1.get(i)); 
      } 
    } 
  }
  
  public void scheduleHideFragmentView(Object paramObject, final View fragmentView, final ArrayList<View> exitingViews) { ((Transition)paramObject).addListener(new Transition.TransitionListener() {
          public void onTransitionCancel(@NonNull Transition param1Transition) {}
          
          public void onTransitionEnd(@NonNull Transition param1Transition) {
            param1Transition.removeListener(this);
            fragmentView.setVisibility(8);
            int i = exitingViews.size();
            for (byte b = 0; b < i; b++)
              ((View)exitingViews.get(b)).setVisibility(0); 
          }
          
          public void onTransitionPause(@NonNull Transition param1Transition) {}
          
          public void onTransitionResume(@NonNull Transition param1Transition) {}
          
          public void onTransitionStart(@NonNull Transition param1Transition) {}
        }); }
  
  public void scheduleRemoveTargets(Object paramObject1, final Object enterTransition, final ArrayList<View> enteringViews, final Object exitTransition, final ArrayList<View> exitingViews, final Object sharedElementTransition, final ArrayList<View> sharedElementsIn) { ((Transition)paramObject1).addListener(new Transition.TransitionListener() {
          public void onTransitionCancel(@NonNull Transition param1Transition) {}
          
          public void onTransitionEnd(@NonNull Transition param1Transition) {}
          
          public void onTransitionPause(@NonNull Transition param1Transition) {}
          
          public void onTransitionResume(@NonNull Transition param1Transition) {}
          
          public void onTransitionStart(@NonNull Transition param1Transition) {
            if (enterTransition != null)
              FragmentTransitionSupport.this.replaceTargets(enterTransition, enteringViews, null); 
            if (exitTransition != null)
              FragmentTransitionSupport.this.replaceTargets(exitTransition, exitingViews, null); 
            if (sharedElementTransition != null)
              FragmentTransitionSupport.this.replaceTargets(sharedElementTransition, sharedElementsIn, null); 
          }
        }); }
  
  public void setEpicenter(Object paramObject, final Rect epicenter) {
    if (paramObject != null)
      ((Transition)paramObject).setEpicenterCallback(new Transition.EpicenterCallback() {
            public Rect onGetEpicenter(@NonNull Transition param1Transition) { return (epicenter == null || epicenter.isEmpty()) ? null : epicenter; }
          }); 
  }
  
  public void setEpicenter(Object paramObject, View paramView) {
    if (paramView != null) {
      paramObject = (Transition)paramObject;
      final Rect epicenter = new Rect();
      getBoundsOnScreen(paramView, rect);
      paramObject.setEpicenterCallback(new Transition.EpicenterCallback() {
            public Rect onGetEpicenter(@NonNull Transition param1Transition) { return epicenter; }
          });
    } 
  }
  
  public void setSharedElementTargets(Object paramObject, View paramView, ArrayList<View> paramArrayList) {
    paramObject = (TransitionSet)paramObject;
    List list = paramObject.getTargets();
    list.clear();
    int i = paramArrayList.size();
    byte b;
    for (b = 0; b < i; b++)
      bfsAddViewChildren(list, (View)paramArrayList.get(b)); 
    list.add(paramView);
    paramArrayList.add(paramView);
    addTargets(paramObject, paramArrayList);
  }
  
  public void swapSharedElementTargets(Object paramObject, ArrayList<View> paramArrayList1, ArrayList<View> paramArrayList2) {
    paramObject = (TransitionSet)paramObject;
    if (paramObject != null) {
      paramObject.getTargets().clear();
      paramObject.getTargets().addAll(paramArrayList2);
      replaceTargets(paramObject, paramArrayList1, paramArrayList2);
    } 
  }
  
  public Object wrapTransitionInSet(Object paramObject) {
    if (paramObject == null)
      return null; 
    TransitionSet transitionSet = new TransitionSet();
    transitionSet.addTransition((Transition)paramObject);
    return transitionSet;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/FragmentTransitionSupport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */