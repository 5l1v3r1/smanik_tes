package android.arch.lifecycle;

class ReflectiveGenericLifecycleObserver implements GenericLifecycleObserver {
  private final ClassesInfoCache.CallbackInfo mInfo;
  
  private final Object mWrapped;
  
  ReflectiveGenericLifecycleObserver(Object paramObject) {
    this.mWrapped = paramObject;
    this.mInfo = ClassesInfoCache.sInstance.getInfo(this.mWrapped.getClass());
  }
  
  public void onStateChanged(LifecycleOwner paramLifecycleOwner, Lifecycle.Event paramEvent) { this.mInfo.invokeCallbacks(paramLifecycleOwner, paramEvent, this.mWrapped); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/lifecycle/ReflectiveGenericLifecycleObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */