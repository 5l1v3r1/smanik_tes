package android.support.v7.app;

import android.support.annotation.Nullable;
import android.support.v7.view.ActionMode;

public interface AppCompatCallback {
  void onSupportActionModeFinished(ActionMode paramActionMode);
  
  void onSupportActionModeStarted(ActionMode paramActionMode);
  
  @Nullable
  ActionMode onWindowStartingSupportActionMode(ActionMode.Callback paramCallback);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/app/AppCompatCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */