package android.support.transition;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Scene {
  private Context mContext;
  
  private Runnable mEnterAction;
  
  private Runnable mExitAction;
  
  private View mLayout;
  
  private int mLayoutId = -1;
  
  private ViewGroup mSceneRoot;
  
  public Scene(@NonNull ViewGroup paramViewGroup) { this.mSceneRoot = paramViewGroup; }
  
  private Scene(ViewGroup paramViewGroup, int paramInt, Context paramContext) {
    this.mContext = paramContext;
    this.mSceneRoot = paramViewGroup;
    this.mLayoutId = paramInt;
  }
  
  public Scene(@NonNull ViewGroup paramViewGroup, @NonNull View paramView) {
    this.mSceneRoot = paramViewGroup;
    this.mLayout = paramView;
  }
  
  static Scene getCurrentScene(View paramView) { return (Scene)paramView.getTag(R.id.transition_current_scene); }
  
  @NonNull
  public static Scene getSceneForLayout(@NonNull ViewGroup paramViewGroup, @LayoutRes int paramInt, @NonNull Context paramContext) {
    SparseArray sparseArray2 = (SparseArray)paramViewGroup.getTag(R.id.transition_scene_layoutid_cache);
    SparseArray sparseArray1 = sparseArray2;
    if (sparseArray2 == null) {
      sparseArray1 = new SparseArray();
      paramViewGroup.setTag(R.id.transition_scene_layoutid_cache, sparseArray1);
    } 
    Scene scene2 = (Scene)sparseArray1.get(paramInt);
    if (scene2 != null)
      return scene2; 
    Scene scene1 = new Scene(paramViewGroup, paramInt, paramContext);
    sparseArray1.put(paramInt, scene1);
    return scene1;
  }
  
  static void setCurrentScene(View paramView, Scene paramScene) { paramView.setTag(R.id.transition_current_scene, paramScene); }
  
  public void enter() {
    if (this.mLayoutId > 0 || this.mLayout != null) {
      getSceneRoot().removeAllViews();
      if (this.mLayoutId > 0) {
        LayoutInflater.from(this.mContext).inflate(this.mLayoutId, this.mSceneRoot);
      } else {
        this.mSceneRoot.addView(this.mLayout);
      } 
    } 
    if (this.mEnterAction != null)
      this.mEnterAction.run(); 
    setCurrentScene(this.mSceneRoot, this);
  }
  
  public void exit() {
    if (getCurrentScene(this.mSceneRoot) == this && this.mExitAction != null)
      this.mExitAction.run(); 
  }
  
  @NonNull
  public ViewGroup getSceneRoot() { return this.mSceneRoot; }
  
  boolean isCreatedFromLayoutResource() { return (this.mLayoutId > 0); }
  
  public void setEnterAction(@Nullable Runnable paramRunnable) { this.mEnterAction = paramRunnable; }
  
  public void setExitAction(@Nullable Runnable paramRunnable) { this.mExitAction = paramRunnable; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/Scene.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */