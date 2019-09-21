package android.support.v4.app;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v13.view.DragAndDropPermissionsCompat;
import android.support.v4.content.ContextCompat;
import android.view.DragEvent;
import android.view.View;
import java.util.List;
import java.util.Map;

public class ActivityCompat extends ContextCompat {
  private static PermissionCompatDelegate sDelegate;
  
  public static void finishAffinity(@NonNull Activity paramActivity) {
    if (Build.VERSION.SDK_INT >= 16) {
      paramActivity.finishAffinity();
      return;
    } 
    paramActivity.finish();
  }
  
  public static void finishAfterTransition(@NonNull Activity paramActivity) {
    if (Build.VERSION.SDK_INT >= 21) {
      paramActivity.finishAfterTransition();
      return;
    } 
    paramActivity.finish();
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static PermissionCompatDelegate getPermissionCompatDelegate() { return sDelegate; }
  
  @Nullable
  public static Uri getReferrer(@NonNull Activity paramActivity) {
    if (Build.VERSION.SDK_INT >= 22)
      return paramActivity.getReferrer(); 
    Intent intent = paramActivity.getIntent();
    Uri uri = (Uri)intent.getParcelableExtra("android.intent.extra.REFERRER");
    if (uri != null)
      return uri; 
    String str = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
    return (str != null) ? Uri.parse(str) : null;
  }
  
  @Deprecated
  public static boolean invalidateOptionsMenu(Activity paramActivity) {
    paramActivity.invalidateOptionsMenu();
    return true;
  }
  
  public static void postponeEnterTransition(@NonNull Activity paramActivity) {
    if (Build.VERSION.SDK_INT >= 21)
      paramActivity.postponeEnterTransition(); 
  }
  
  @Nullable
  public static DragAndDropPermissionsCompat requestDragAndDropPermissions(Activity paramActivity, DragEvent paramDragEvent) { return DragAndDropPermissionsCompat.request(paramActivity, paramDragEvent); }
  
  public static void requestPermissions(@NonNull final Activity activity, @NonNull final String[] permissions, @IntRange(from = 0L) final int requestCode) {
    if (sDelegate != null && sDelegate.requestPermissions(paramActivity, paramArrayOfString, paramInt))
      return; 
    if (Build.VERSION.SDK_INT >= 23) {
      if (paramActivity instanceof RequestPermissionsRequestCodeValidator)
        ((RequestPermissionsRequestCodeValidator)paramActivity).validateRequestPermissionsRequestCode(paramInt); 
      paramActivity.requestPermissions(paramArrayOfString, paramInt);
      return;
    } 
    if (paramActivity instanceof OnRequestPermissionsResultCallback)
      (new Handler(Looper.getMainLooper())).post(new Runnable() {
            public void run() {
              int[] arrayOfInt = new int[permissions.length];
              PackageManager packageManager = activity.getPackageManager();
              String str = activity.getPackageName();
              int i = permissions.length;
              for (byte b = 0; b < i; b++)
                arrayOfInt[b] = packageManager.checkPermission(permissions[b], str); 
              ((ActivityCompat.OnRequestPermissionsResultCallback)activity).onRequestPermissionsResult(requestCode, permissions, arrayOfInt);
            }
          }); 
  }
  
  @NonNull
  public static <T extends View> T requireViewById(@NonNull Activity paramActivity, @IdRes int paramInt) {
    View view = paramActivity.findViewById(paramInt);
    if (view == null)
      throw new IllegalArgumentException("ID does not reference a View inside this Activity"); 
    return (T)view;
  }
  
  public static void setEnterSharedElementCallback(@NonNull Activity paramActivity, @Nullable SharedElementCallback paramSharedElementCallback) {
    int i = Build.VERSION.SDK_INT;
    SharedElementCallback23Impl sharedElementCallback23Impl2 = null;
    SharedElementCallback23Impl sharedElementCallback23Impl1 = null;
    if (i >= 23) {
      if (paramSharedElementCallback != null)
        sharedElementCallback23Impl1 = new SharedElementCallback23Impl(paramSharedElementCallback); 
      paramActivity.setEnterSharedElementCallback(sharedElementCallback23Impl1);
      return;
    } 
    if (Build.VERSION.SDK_INT >= 21) {
      SharedElementCallback21Impl sharedElementCallback21Impl;
      sharedElementCallback23Impl1 = sharedElementCallback23Impl2;
      if (paramSharedElementCallback != null)
        sharedElementCallback21Impl = new SharedElementCallback21Impl(paramSharedElementCallback); 
      paramActivity.setEnterSharedElementCallback(sharedElementCallback21Impl);
    } 
  }
  
  public static void setExitSharedElementCallback(@NonNull Activity paramActivity, @Nullable SharedElementCallback paramSharedElementCallback) {
    int i = Build.VERSION.SDK_INT;
    SharedElementCallback23Impl sharedElementCallback23Impl2 = null;
    SharedElementCallback23Impl sharedElementCallback23Impl1 = null;
    if (i >= 23) {
      if (paramSharedElementCallback != null)
        sharedElementCallback23Impl1 = new SharedElementCallback23Impl(paramSharedElementCallback); 
      paramActivity.setExitSharedElementCallback(sharedElementCallback23Impl1);
      return;
    } 
    if (Build.VERSION.SDK_INT >= 21) {
      SharedElementCallback21Impl sharedElementCallback21Impl;
      sharedElementCallback23Impl1 = sharedElementCallback23Impl2;
      if (paramSharedElementCallback != null)
        sharedElementCallback21Impl = new SharedElementCallback21Impl(paramSharedElementCallback); 
      paramActivity.setExitSharedElementCallback(sharedElementCallback21Impl);
    } 
  }
  
  public static void setPermissionCompatDelegate(@Nullable PermissionCompatDelegate paramPermissionCompatDelegate) { sDelegate = paramPermissionCompatDelegate; }
  
  public static boolean shouldShowRequestPermissionRationale(@NonNull Activity paramActivity, @NonNull String paramString) { return (Build.VERSION.SDK_INT >= 23) ? paramActivity.shouldShowRequestPermissionRationale(paramString) : 0; }
  
  public static void startActivityForResult(@NonNull Activity paramActivity, @NonNull Intent paramIntent, int paramInt, @Nullable Bundle paramBundle) {
    if (Build.VERSION.SDK_INT >= 16) {
      paramActivity.startActivityForResult(paramIntent, paramInt, paramBundle);
      return;
    } 
    paramActivity.startActivityForResult(paramIntent, paramInt);
  }
  
  public static void startIntentSenderForResult(@NonNull Activity paramActivity, @NonNull IntentSender paramIntentSender, int paramInt1, @Nullable Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, @Nullable Bundle paramBundle) throws IntentSender.SendIntentException {
    if (Build.VERSION.SDK_INT >= 16) {
      paramActivity.startIntentSenderForResult(paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
      return;
    } 
    paramActivity.startIntentSenderForResult(paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4);
  }
  
  public static void startPostponedEnterTransition(@NonNull Activity paramActivity) {
    if (Build.VERSION.SDK_INT >= 21)
      paramActivity.startPostponedEnterTransition(); 
  }
  
  public static interface OnRequestPermissionsResultCallback {
    void onRequestPermissionsResult(int param1Int, @NonNull String[] param1ArrayOfString, @NonNull int[] param1ArrayOfInt);
  }
  
  public static interface PermissionCompatDelegate {
    boolean onActivityResult(@NonNull Activity param1Activity, @IntRange(from = 0L) int param1Int1, int param1Int2, @Nullable Intent param1Intent);
    
    boolean requestPermissions(@NonNull Activity param1Activity, @NonNull String[] param1ArrayOfString, @IntRange(from = 0L) int param1Int);
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static interface RequestPermissionsRequestCodeValidator {
    void validateRequestPermissionsRequestCode(int param1Int);
  }
  
  @RequiresApi(21)
  private static class SharedElementCallback21Impl extends SharedElementCallback {
    protected SharedElementCallback mCallback;
    
    SharedElementCallback21Impl(SharedElementCallback param1SharedElementCallback) { this.mCallback = param1SharedElementCallback; }
    
    public Parcelable onCaptureSharedElementSnapshot(View param1View, Matrix param1Matrix, RectF param1RectF) { return this.mCallback.onCaptureSharedElementSnapshot(param1View, param1Matrix, param1RectF); }
    
    public View onCreateSnapshotView(Context param1Context, Parcelable param1Parcelable) { return this.mCallback.onCreateSnapshotView(param1Context, param1Parcelable); }
    
    public void onMapSharedElements(List<String> param1List, Map<String, View> param1Map) { this.mCallback.onMapSharedElements(param1List, param1Map); }
    
    public void onRejectSharedElements(List<View> param1List) { this.mCallback.onRejectSharedElements(param1List); }
    
    public void onSharedElementEnd(List<String> param1List1, List<View> param1List2, List<View> param1List3) { this.mCallback.onSharedElementEnd(param1List1, param1List2, param1List3); }
    
    public void onSharedElementStart(List<String> param1List1, List<View> param1List2, List<View> param1List3) { this.mCallback.onSharedElementStart(param1List1, param1List2, param1List3); }
  }
  
  @RequiresApi(23)
  private static class SharedElementCallback23Impl extends SharedElementCallback21Impl {
    SharedElementCallback23Impl(SharedElementCallback param1SharedElementCallback) { super(param1SharedElementCallback); }
    
    public void onSharedElementsArrived(List<String> param1List1, List<View> param1List2, final SharedElementCallback.OnSharedElementsReadyListener listener) { this.mCallback.onSharedElementsArrived(param1List1, param1List2, new SharedElementCallback.OnSharedElementsReadyListener() {
            public void onSharedElementsReady() { listener.onSharedElementsReady(); }
          }); }
  }
  
  class null implements SharedElementCallback.OnSharedElementsReadyListener {
    public void onSharedElementsReady() { listener.onSharedElementsReady(); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/ActivityCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */