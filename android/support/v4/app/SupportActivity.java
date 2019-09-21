package android.support.v4.app;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.ReportFragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.util.SimpleArrayMap;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class SupportActivity extends Activity implements LifecycleOwner {
  private SimpleArrayMap<Class<? extends ExtraData>, ExtraData> mExtraDataMap = new SimpleArrayMap();
  
  private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public <T extends ExtraData> T getExtraData(Class<T> paramClass) { return (T)(ExtraData)this.mExtraDataMap.get(paramClass); }
  
  public Lifecycle getLifecycle() { return this.mLifecycleRegistry; }
  
  protected void onCreate(@Nullable Bundle paramBundle) {
    super.onCreate(paramBundle);
    ReportFragment.injectIfNeededIn(this);
  }
  
  @CallSuper
  protected void onSaveInstanceState(Bundle paramBundle) {
    this.mLifecycleRegistry.markState(Lifecycle.State.CREATED);
    super.onSaveInstanceState(paramBundle);
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void putExtraData(ExtraData paramExtraData) { this.mExtraDataMap.put(paramExtraData.getClass(), paramExtraData); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static class ExtraData {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/SupportActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */