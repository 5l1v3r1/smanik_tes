package android.support.v4.widget;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.os.BuildCompat;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public interface AutoSizeableTextView {
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static final boolean PLATFORM_SUPPORTS_AUTOSIZE = BuildCompat.isAtLeastOMR1();
  
  int getAutoSizeMaxTextSize();
  
  int getAutoSizeMinTextSize();
  
  int getAutoSizeStepGranularity();
  
  int[] getAutoSizeTextAvailableSizes();
  
  int getAutoSizeTextType();
  
  void setAutoSizeTextTypeUniformWithConfiguration(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws IllegalArgumentException;
  
  void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull int[] paramArrayOfInt, int paramInt) throws IllegalArgumentException;
  
  void setAutoSizeTextTypeWithDefaults(int paramInt);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/widget/AutoSizeableTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */