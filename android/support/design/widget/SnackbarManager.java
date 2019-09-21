package android.support.design.widget;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;

class SnackbarManager {
  private static final int LONG_DURATION_MS = 2750;
  
  static final int MSG_TIMEOUT = 0;
  
  private static final int SHORT_DURATION_MS = 1500;
  
  private static SnackbarManager sSnackbarManager;
  
  private SnackbarRecord mCurrentSnackbar;
  
  private final Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback(this) {
        public boolean handleMessage(Message param1Message) {
          if (param1Message.what != 0)
            return false; 
          SnackbarManager.this.handleTimeout((SnackbarManager.SnackbarRecord)param1Message.obj);
          return true;
        }
      });
  
  private final Object mLock = new Object();
  
  private SnackbarRecord mNextSnackbar;
  
  private boolean cancelSnackbarLocked(SnackbarRecord paramSnackbarRecord, int paramInt) {
    Callback callback = (Callback)paramSnackbarRecord.callback.get();
    if (callback != null) {
      this.mHandler.removeCallbacksAndMessages(paramSnackbarRecord);
      callback.dismiss(paramInt);
      return true;
    } 
    return false;
  }
  
  static SnackbarManager getInstance() {
    if (sSnackbarManager == null)
      sSnackbarManager = new SnackbarManager(); 
    return sSnackbarManager;
  }
  
  private boolean isCurrentSnackbarLocked(Callback paramCallback) { return (this.mCurrentSnackbar != null && this.mCurrentSnackbar.isSnackbar(paramCallback)); }
  
  private boolean isNextSnackbarLocked(Callback paramCallback) { return (this.mNextSnackbar != null && this.mNextSnackbar.isSnackbar(paramCallback)); }
  
  private void scheduleTimeoutLocked(SnackbarRecord paramSnackbarRecord) {
    if (paramSnackbarRecord.duration == -2)
      return; 
    int i = 2750;
    if (paramSnackbarRecord.duration > 0) {
      i = paramSnackbarRecord.duration;
    } else if (paramSnackbarRecord.duration == -1) {
      i = 1500;
    } 
    this.mHandler.removeCallbacksAndMessages(paramSnackbarRecord);
    this.mHandler.sendMessageDelayed(Message.obtain(this.mHandler, 0, paramSnackbarRecord), i);
  }
  
  private void showNextSnackbarLocked() {
    if (this.mNextSnackbar != null) {
      this.mCurrentSnackbar = this.mNextSnackbar;
      this.mNextSnackbar = null;
      Callback callback = (Callback)this.mCurrentSnackbar.callback.get();
      if (callback != null) {
        callback.show();
        return;
      } 
      this.mCurrentSnackbar = null;
    } 
  }
  
  public void dismiss(Callback paramCallback, int paramInt) {
    synchronized (this.mLock) {
      if (isCurrentSnackbarLocked(paramCallback)) {
        cancelSnackbarLocked(this.mCurrentSnackbar, paramInt);
      } else if (isNextSnackbarLocked(paramCallback)) {
        cancelSnackbarLocked(this.mNextSnackbar, paramInt);
      } 
      return;
    } 
  }
  
  void handleTimeout(SnackbarRecord paramSnackbarRecord) {
    synchronized (this.mLock) {
      if (this.mCurrentSnackbar == paramSnackbarRecord || this.mNextSnackbar == paramSnackbarRecord)
        cancelSnackbarLocked(paramSnackbarRecord, 2); 
      return;
    } 
  }
  
  public boolean isCurrent(Callback paramCallback) {
    synchronized (this.mLock) {
      return isCurrentSnackbarLocked(paramCallback);
    } 
  }
  
  public boolean isCurrentOrNext(Callback paramCallback) {
    synchronized (this.mLock) {
      if (isCurrentSnackbarLocked(paramCallback) || isNextSnackbarLocked(paramCallback))
        return true; 
    } 
    boolean bool = false;
    /* monitor exit ClassFileLocalVariableReferenceExpression{type=ObjectType{java/lang/Object}, name=SYNTHETIC_LOCAL_VARIABLE_3} */
    return bool;
  }
  
  public void onDismissed(Callback paramCallback) {
    synchronized (this.mLock) {
      if (isCurrentSnackbarLocked(paramCallback)) {
        this.mCurrentSnackbar = null;
        if (this.mNextSnackbar != null)
          showNextSnackbarLocked(); 
      } 
      return;
    } 
  }
  
  public void onShown(Callback paramCallback) {
    synchronized (this.mLock) {
      if (isCurrentSnackbarLocked(paramCallback))
        scheduleTimeoutLocked(this.mCurrentSnackbar); 
      return;
    } 
  }
  
  public void pauseTimeout(Callback paramCallback) {
    synchronized (this.mLock) {
      if (isCurrentSnackbarLocked(paramCallback) && !this.mCurrentSnackbar.paused) {
        this.mCurrentSnackbar.paused = true;
        this.mHandler.removeCallbacksAndMessages(this.mCurrentSnackbar);
      } 
      return;
    } 
  }
  
  public void restoreTimeoutIfPaused(Callback paramCallback) {
    synchronized (this.mLock) {
      if (isCurrentSnackbarLocked(paramCallback) && this.mCurrentSnackbar.paused) {
        this.mCurrentSnackbar.paused = false;
        scheduleTimeoutLocked(this.mCurrentSnackbar);
      } 
      return;
    } 
  }
  
  public void show(int paramInt, Callback paramCallback) {
    synchronized (this.mLock) {
      if (isCurrentSnackbarLocked(paramCallback)) {
        this.mCurrentSnackbar.duration = paramInt;
        this.mHandler.removeCallbacksAndMessages(this.mCurrentSnackbar);
        scheduleTimeoutLocked(this.mCurrentSnackbar);
        return;
      } 
      if (isNextSnackbarLocked(paramCallback)) {
        this.mNextSnackbar.duration = paramInt;
      } else {
        this.mNextSnackbar = new SnackbarRecord(paramInt, paramCallback);
      } 
      if (this.mCurrentSnackbar != null && cancelSnackbarLocked(this.mCurrentSnackbar, 4))
        return; 
      this.mCurrentSnackbar = null;
      showNextSnackbarLocked();
      return;
    } 
  }
  
  static interface Callback {
    void dismiss(int param1Int);
    
    void show();
  }
  
  private static class SnackbarRecord {
    final WeakReference<SnackbarManager.Callback> callback;
    
    int duration;
    
    boolean paused;
    
    SnackbarRecord(int param1Int, SnackbarManager.Callback param1Callback) {
      this.callback = new WeakReference(param1Callback);
      this.duration = param1Int;
    }
    
    boolean isSnackbar(SnackbarManager.Callback param1Callback) { return (param1Callback != null && this.callback.get() == param1Callback); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/SnackbarManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */