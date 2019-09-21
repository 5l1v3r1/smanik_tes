package android.support.v4.app;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class LoaderManager {
  public static void enableDebugLogging(boolean paramBoolean) { LoaderManagerImpl.DEBUG = paramBoolean; }
  
  @MainThread
  public abstract void destroyLoader(int paramInt);
  
  public abstract void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString);
  
  @Nullable
  public abstract <D> Loader<D> getLoader(int paramInt);
  
  public boolean hasRunningLoaders() { return false; }
  
  @MainThread
  @NonNull
  public abstract <D> Loader<D> initLoader(int paramInt, @Nullable Bundle paramBundle, @NonNull LoaderCallbacks<D> paramLoaderCallbacks);
  
  @MainThread
  @NonNull
  public abstract <D> Loader<D> restartLoader(int paramInt, @Nullable Bundle paramBundle, @NonNull LoaderCallbacks<D> paramLoaderCallbacks);
  
  public static interface LoaderCallbacks<D> {
    @MainThread
    @NonNull
    Loader<D> onCreateLoader(int param1Int, @Nullable Bundle param1Bundle);
    
    @MainThread
    void onLoadFinished(@NonNull Loader<D> param1Loader, D param1D);
    
    @MainThread
    void onLoaderReset(@NonNull Loader<D> param1Loader);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/LoaderManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */