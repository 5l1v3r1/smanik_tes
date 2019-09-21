package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public class TransitionManager {
  private static final String LOG_TAG = "TransitionManager";
  
  private static Transition sDefaultTransition = new AutoTransition();
  
  private static ArrayList<ViewGroup> sPendingTransitions;
  
  private static ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>> sRunningTransitions = new ThreadLocal();
  
  private ArrayMap<Scene, ArrayMap<Scene, Transition>> mScenePairTransitions = new ArrayMap();
  
  private ArrayMap<Scene, Transition> mSceneTransitions = new ArrayMap();
  
  static  {
    sPendingTransitions = new ArrayList();
  }
  
  public static void beginDelayedTransition(@NonNull ViewGroup paramViewGroup) { beginDelayedTransition(paramViewGroup, null); }
  
  public static void beginDelayedTransition(@NonNull ViewGroup paramViewGroup, @Nullable Transition paramTransition) {
    if (!sPendingTransitions.contains(paramViewGroup) && ViewCompat.isLaidOut(paramViewGroup)) {
      sPendingTransitions.add(paramViewGroup);
      Transition transition = paramTransition;
      if (paramTransition == null)
        transition = sDefaultTransition; 
      paramTransition = transition.clone();
      sceneChangeSetup(paramViewGroup, paramTransition);
      Scene.setCurrentScene(paramViewGroup, null);
      sceneChangeRunTransition(paramViewGroup, paramTransition);
    } 
  }
  
  private static void changeScene(Scene paramScene, Transition paramTransition) {
    ViewGroup viewGroup = paramScene.getSceneRoot();
    if (!sPendingTransitions.contains(viewGroup)) {
      if (paramTransition == null) {
        paramScene.enter();
        return;
      } 
      sPendingTransitions.add(viewGroup);
      paramTransition = paramTransition.clone();
      paramTransition.setSceneRoot(viewGroup);
      Scene scene = Scene.getCurrentScene(viewGroup);
      if (scene != null && scene.isCreatedFromLayoutResource())
        paramTransition.setCanRemoveViews(true); 
      sceneChangeSetup(viewGroup, paramTransition);
      paramScene.enter();
      sceneChangeRunTransition(viewGroup, paramTransition);
    } 
  }
  
  public static void endTransitions(ViewGroup paramViewGroup) {
    sPendingTransitions.remove(paramViewGroup);
    ArrayList arrayList = (ArrayList)getRunningTransitions().get(paramViewGroup);
    if (arrayList != null && !arrayList.isEmpty()) {
      arrayList = new ArrayList(arrayList);
      for (int i = arrayList.size() - 1; i >= 0; i--)
        ((Transition)arrayList.get(i)).forceToEnd(paramViewGroup); 
    } 
  }
  
  static ArrayMap<ViewGroup, ArrayList<Transition>> getRunningTransitions() {
    WeakReference weakReference2 = (WeakReference)sRunningTransitions.get();
    if (weakReference2 != null) {
      WeakReference weakReference = weakReference2;
      if (weakReference2.get() == null) {
        weakReference = new WeakReference(new ArrayMap());
        sRunningTransitions.set(weakReference);
        return (ArrayMap)weakReference.get();
      } 
      return (ArrayMap)weakReference.get();
    } 
    WeakReference weakReference1 = new WeakReference(new ArrayMap());
    sRunningTransitions.set(weakReference1);
    return (ArrayMap)weakReference1.get();
  }
  
  private Transition getTransition(Scene paramScene) {
    ViewGroup viewGroup = paramScene.getSceneRoot();
    if (viewGroup != null) {
      Scene scene = Scene.getCurrentScene(viewGroup);
      if (scene != null) {
        ArrayMap arrayMap = (ArrayMap)this.mScenePairTransitions.get(paramScene);
        if (arrayMap != null) {
          Transition transition1 = (Transition)arrayMap.get(scene);
          if (transition1 != null)
            return transition1; 
        } 
      } 
    } 
    Transition transition = (Transition)this.mSceneTransitions.get(paramScene);
    return (transition != null) ? transition : sDefaultTransition;
  }
  
  public static void go(@NonNull Scene paramScene) { changeScene(paramScene, sDefaultTransition); }
  
  public static void go(@NonNull Scene paramScene, @Nullable Transition paramTransition) { changeScene(paramScene, paramTransition); }
  
  private static void sceneChangeRunTransition(ViewGroup paramViewGroup, Transition paramTransition) {
    if (paramTransition != null && paramViewGroup != null) {
      MultiListener multiListener = new MultiListener(paramTransition, paramViewGroup);
      paramViewGroup.addOnAttachStateChangeListener(multiListener);
      paramViewGroup.getViewTreeObserver().addOnPreDrawListener(multiListener);
    } 
  }
  
  private static void sceneChangeSetup(ViewGroup paramViewGroup, Transition paramTransition) {
    ArrayList arrayList = (ArrayList)getRunningTransitions().get(paramViewGroup);
    if (arrayList != null && arrayList.size() > 0) {
      Iterator iterator = arrayList.iterator();
      while (iterator.hasNext())
        ((Transition)iterator.next()).pause(paramViewGroup); 
    } 
    if (paramTransition != null)
      paramTransition.captureValues(paramViewGroup, true); 
    Scene scene = Scene.getCurrentScene(paramViewGroup);
    if (scene != null)
      scene.exit(); 
  }
  
  public void setTransition(@NonNull Scene paramScene1, @NonNull Scene paramScene2, @Nullable Transition paramTransition) {
    ArrayMap arrayMap2 = (ArrayMap)this.mScenePairTransitions.get(paramScene2);
    ArrayMap arrayMap1 = arrayMap2;
    if (arrayMap2 == null) {
      arrayMap1 = new ArrayMap();
      this.mScenePairTransitions.put(paramScene2, arrayMap1);
    } 
    arrayMap1.put(paramScene1, paramTransition);
  }
  
  public void setTransition(@NonNull Scene paramScene, @Nullable Transition paramTransition) { this.mSceneTransitions.put(paramScene, paramTransition); }
  
  public void transitionTo(@NonNull Scene paramScene) { changeScene(paramScene, getTransition(paramScene)); }
  
  private static class MultiListener implements ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener {
    ViewGroup mSceneRoot;
    
    Transition mTransition;
    
    MultiListener(Transition param1Transition, ViewGroup param1ViewGroup) {
      this.mTransition = param1Transition;
      this.mSceneRoot = param1ViewGroup;
    }
    
    private void removeListeners() {
      this.mSceneRoot.getViewTreeObserver().removeOnPreDrawListener(this);
      this.mSceneRoot.removeOnAttachStateChangeListener(this);
    }
    
    public boolean onPreDraw() {
      ArrayList arrayList1;
      removeListeners();
      if (!sPendingTransitions.remove(this.mSceneRoot))
        return true; 
      final ArrayMap runningTransitions = TransitionManager.getRunningTransitions();
      ArrayList arrayList3 = (ArrayList)arrayMap.get(this.mSceneRoot);
      ArrayList arrayList2 = null;
      if (arrayList3 == null) {
        arrayList1 = new ArrayList();
        arrayMap.put(this.mSceneRoot, arrayList1);
      } else {
        arrayList1 = arrayList3;
        if (arrayList3.size() > 0) {
          arrayList2 = new ArrayList(arrayList3);
          arrayList1 = arrayList3;
        } 
      } 
      arrayList1.add(this.mTransition);
      this.mTransition.addListener(new TransitionListenerAdapter() {
            public void onTransitionEnd(@NonNull Transition param2Transition) { ((ArrayList)runningTransitions.get(TransitionManager.MultiListener.this.mSceneRoot)).remove(param2Transition); }
          });
      this.mTransition.captureValues(this.mSceneRoot, false);
      if (arrayList2 != null) {
        Iterator iterator = arrayList2.iterator();
        while (iterator.hasNext())
          ((Transition)iterator.next()).resume(this.mSceneRoot); 
      } 
      this.mTransition.playTransition(this.mSceneRoot);
      return true;
    }
    
    public void onViewAttachedToWindow(View param1View) {}
    
    public void onViewDetachedFromWindow(View param1View) {
      removeListeners();
      sPendingTransitions.remove(this.mSceneRoot);
      ArrayList arrayList = (ArrayList)TransitionManager.getRunningTransitions().get(this.mSceneRoot);
      if (arrayList != null && arrayList.size() > 0) {
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext())
          ((Transition)iterator.next()).resume(this.mSceneRoot); 
      } 
      this.mTransition.clearValues(true);
    }
  }
  
  class null extends TransitionListenerAdapter {
    public void onTransitionEnd(@NonNull Transition param1Transition) { ((ArrayList)runningTransitions.get(this.this$0.mSceneRoot)).remove(param1Transition); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/TransitionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */