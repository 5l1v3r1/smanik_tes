package android.support.v4.content;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.os.OperationCanceledException;
import android.support.v4.util.TimeUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public abstract class AsyncTaskLoader<D> extends Loader<D> {
  static final boolean DEBUG = false;
  
  static final String TAG = "AsyncTaskLoader";
  
  private final Executor mExecutor;
  
  Handler mHandler;
  
  long mLastLoadCompleteTime = -10000L;
  
  long mUpdateThrottle;
  
  public AsyncTaskLoader(@NonNull Context paramContext) { this(paramContext, ModernAsyncTask.THREAD_POOL_EXECUTOR); }
  
  private AsyncTaskLoader(@NonNull Context paramContext, @NonNull Executor paramExecutor) {
    super(paramContext);
    this.mExecutor = paramExecutor;
  }
  
  public void cancelLoadInBackground() {}
  
  void dispatchOnCancelled(LoadTask paramLoadTask, D paramD) {
    onCanceled(paramD);
    if (this.mCancellingTask == paramLoadTask) {
      rollbackContentChanged();
      this.mLastLoadCompleteTime = SystemClock.uptimeMillis();
      this.mCancellingTask = null;
      deliverCancellation();
      executePendingTask();
    } 
  }
  
  void dispatchOnLoadComplete(LoadTask paramLoadTask, D paramD) {
    if (this.mTask != paramLoadTask) {
      dispatchOnCancelled(paramLoadTask, paramD);
      return;
    } 
    if (isAbandoned()) {
      onCanceled(paramD);
      return;
    } 
    commitContentChanged();
    this.mLastLoadCompleteTime = SystemClock.uptimeMillis();
    this.mTask = null;
    deliverResult(paramD);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {
    super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    if (this.mTask != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mTask=");
      paramPrintWriter.print(this.mTask);
      paramPrintWriter.print(" waiting=");
      paramPrintWriter.println(this.mTask.waiting);
    } 
    if (this.mCancellingTask != null) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCancellingTask=");
      paramPrintWriter.print(this.mCancellingTask);
      paramPrintWriter.print(" waiting=");
      paramPrintWriter.println(this.mCancellingTask.waiting);
    } 
    if (this.mUpdateThrottle != 0L) {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mUpdateThrottle=");
      TimeUtils.formatDuration(this.mUpdateThrottle, paramPrintWriter);
      paramPrintWriter.print(" mLastLoadCompleteTime=");
      TimeUtils.formatDuration(this.mLastLoadCompleteTime, SystemClock.uptimeMillis(), paramPrintWriter);
      paramPrintWriter.println();
    } 
  }
  
  void executePendingTask() {
    if (this.mCancellingTask == null && this.mTask != null) {
      if (this.mTask.waiting) {
        this.mTask.waiting = false;
        this.mHandler.removeCallbacks(this.mTask);
      } 
      if (this.mUpdateThrottle > 0L && SystemClock.uptimeMillis() < this.mLastLoadCompleteTime + this.mUpdateThrottle) {
        this.mTask.waiting = true;
        this.mHandler.postAtTime(this.mTask, this.mLastLoadCompleteTime + this.mUpdateThrottle);
        return;
      } 
      this.mTask.executeOnExecutor(this.mExecutor, (Void[])null);
    } 
  }
  
  public boolean isLoadInBackgroundCanceled() { return (this.mCancellingTask != null); }
  
  @Nullable
  public abstract D loadInBackground();
  
  protected boolean onCancelLoad() {
    if (this.mTask != null) {
      if (!this.mStarted)
        this.mContentChanged = true; 
      if (this.mCancellingTask != null) {
        if (this.mTask.waiting) {
          this.mTask.waiting = false;
          this.mHandler.removeCallbacks(this.mTask);
        } 
        this.mTask = null;
        return false;
      } 
      if (this.mTask.waiting) {
        this.mTask.waiting = false;
        this.mHandler.removeCallbacks(this.mTask);
        this.mTask = null;
        return false;
      } 
      boolean bool = this.mTask.cancel(false);
      if (bool) {
        this.mCancellingTask = this.mTask;
        cancelLoadInBackground();
      } 
      this.mTask = null;
      return bool;
    } 
    return false;
  }
  
  public void onCanceled(@Nullable D paramD) {}
  
  protected void onForceLoad() {
    super.onForceLoad();
    cancelLoad();
    this.mTask = new LoadTask();
    executePendingTask();
  }
  
  @Nullable
  protected D onLoadInBackground() { return (D)loadInBackground(); }
  
  public void setUpdateThrottle(long paramLong) {
    this.mUpdateThrottle = paramLong;
    if (paramLong != 0L)
      this.mHandler = new Handler(); 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void waitForLoader() {
    LoadTask loadTask = this.mTask;
    if (loadTask != null)
      loadTask.waitForLoader(); 
  }
  
  final class LoadTask extends ModernAsyncTask<Void, Void, D> implements Runnable {
    private final CountDownLatch mDone = new CountDownLatch(1);
    
    boolean waiting;
    
    protected D doInBackground(Void... param1VarArgs) {
      try {
        return (D)AsyncTaskLoader.this.onLoadInBackground();
      } catch (OperationCanceledException param1VarArgs) {
        if (!isCancelled())
          throw param1VarArgs; 
        return null;
      } 
    }
    
    protected void onCancelled(D param1D) {
      try {
        AsyncTaskLoader.this.dispatchOnCancelled(this, param1D);
        return;
      } finally {
        this.mDone.countDown();
      } 
    }
    
    protected void onPostExecute(D param1D) {
      try {
        AsyncTaskLoader.this.dispatchOnLoadComplete(this, param1D);
        return;
      } finally {
        this.mDone.countDown();
      } 
    }
    
    public void run() {
      this.waiting = false;
      AsyncTaskLoader.this.executePendingTask();
    }
    
    public void waitForLoader() {
      try {
        this.mDone.await();
        return;
      } catch (InterruptedException interruptedException) {
        return;
      } 
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/content/AsyncTaskLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */