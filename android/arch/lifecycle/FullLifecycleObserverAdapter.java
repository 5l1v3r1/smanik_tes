package android.arch.lifecycle;

class FullLifecycleObserverAdapter implements GenericLifecycleObserver {
  private final FullLifecycleObserver mObserver;
  
  FullLifecycleObserverAdapter(FullLifecycleObserver paramFullLifecycleObserver) { this.mObserver = paramFullLifecycleObserver; }
  
  public void onStateChanged(LifecycleOwner paramLifecycleOwner, Lifecycle.Event paramEvent) {
    switch (paramEvent) {
      default:
        return;
      case null:
        throw new IllegalArgumentException("ON_ANY must not been send by anybody");
      case ON_DESTROY:
        this.mObserver.onDestroy(paramLifecycleOwner);
        return;
      case ON_STOP:
        this.mObserver.onStop(paramLifecycleOwner);
        return;
      case ON_PAUSE:
        this.mObserver.onPause(paramLifecycleOwner);
        return;
      case ON_RESUME:
        this.mObserver.onResume(paramLifecycleOwner);
        return;
      case ON_START:
        this.mObserver.onStart(paramLifecycleOwner);
        return;
      case ON_CREATE:
        break;
    } 
    this.mObserver.onCreate(paramLifecycleOwner);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/lifecycle/FullLifecycleObserverAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */