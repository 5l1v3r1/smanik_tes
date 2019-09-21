package com.google.android.vending.licensing;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.vending.licensing.util.Base64;
import com.google.android.vending.licensing.util.Base64DecoderException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class LicenseChecker implements ServiceConnection {
  private static final boolean DEBUG_LICENSE_ERROR = false;
  
  private static final String KEY_FACTORY_ALGORITHM = "RSA";
  
  private static final SecureRandom RANDOM = new SecureRandom();
  
  private static final String TAG = "LicenseChecker";
  
  private static final int TIMEOUT_MS = 10000;
  
  private final Set<LicenseValidator> mChecksInProgress = new HashSet();
  
  private final Context mContext;
  
  private Handler mHandler;
  
  private final String mPackageName;
  
  private final Queue<LicenseValidator> mPendingChecks = new LinkedList();
  
  private final Policy mPolicy;
  
  private PublicKey mPublicKey;
  
  private ILicensingService mService;
  
  private final String mVersionCode;
  
  public LicenseChecker(Context paramContext, Policy paramPolicy, String paramString) {
    this.mContext = paramContext;
    this.mPolicy = paramPolicy;
    this.mPublicKey = generatePublicKey(paramString);
    this.mPackageName = this.mContext.getPackageName();
    this.mVersionCode = getVersionCode(paramContext, this.mPackageName);
    HandlerThread handlerThread = new HandlerThread("background thread");
    handlerThread.start();
    this.mHandler = new Handler(handlerThread.getLooper());
  }
  
  private void cleanupService() {
    if (this.mService != null) {
      try {
        this.mContext.unbindService(this);
      } catch (IllegalArgumentException illegalArgumentException) {
        Log.e("LicenseChecker", "Unable to unbind from licensing service (already unbound)");
      } 
      this.mService = null;
    } 
  }
  
  private void finishCheck(LicenseValidator paramLicenseValidator) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mChecksInProgress : Ljava/util/Set;
    //   6: aload_1
    //   7: invokeinterface remove : (Ljava/lang/Object;)Z
    //   12: pop
    //   13: aload_0
    //   14: getfield mChecksInProgress : Ljava/util/Set;
    //   17: invokeinterface isEmpty : ()Z
    //   22: ifeq -> 29
    //   25: aload_0
    //   26: invokespecial cleanupService : ()V
    //   29: aload_0
    //   30: monitorexit
    //   31: return
    //   32: astore_1
    //   33: aload_0
    //   34: monitorexit
    //   35: aload_1
    //   36: athrow
    // Exception table:
    //   from	to	target	type
    //   2	29	32	finally }
  
  private int generateNonce() { return RANDOM.nextInt(); }
  
  private static PublicKey generatePublicKey(String paramString) {
    try {
      byte[] arrayOfByte = Base64.decode(paramString);
      return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(arrayOfByte));
    } catch (NoSuchAlgorithmException paramString) {
      throw new RuntimeException(paramString);
    } catch (Base64DecoderException paramString) {
      Log.e("LicenseChecker", "Could not decode from Base64.");
      throw new IllegalArgumentException(paramString);
    } catch (InvalidKeySpecException paramString) {
      Log.e("LicenseChecker", "Invalid key specification.");
      throw new IllegalArgumentException(paramString);
    } 
  }
  
  private static String getVersionCode(Context paramContext, String paramString) {
    try {
      int i = (paramContext.getPackageManager().getPackageInfo(paramString, 0)).versionCode;
      return String.valueOf(i);
    } catch (android.content.pm.PackageManager.NameNotFoundException paramContext) {
      Log.e("LicenseChecker", "Package not found. could not get version code.");
      return "";
    } 
  }
  
  private void handleServiceConnectionError(LicenseValidator paramLicenseValidator) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mPolicy : Lcom/google/android/vending/licensing/Policy;
    //   6: sipush #291
    //   9: aconst_null
    //   10: invokeinterface processServerResponse : (ILcom/google/android/vending/licensing/ResponseData;)V
    //   15: aload_0
    //   16: getfield mPolicy : Lcom/google/android/vending/licensing/Policy;
    //   19: invokeinterface allowAccess : ()Z
    //   24: ifeq -> 42
    //   27: aload_1
    //   28: invokevirtual getCallback : ()Lcom/google/android/vending/licensing/LicenseCheckerCallback;
    //   31: sipush #291
    //   34: invokeinterface allow : (I)V
    //   39: goto -> 54
    //   42: aload_1
    //   43: invokevirtual getCallback : ()Lcom/google/android/vending/licensing/LicenseCheckerCallback;
    //   46: sipush #291
    //   49: invokeinterface dontAllow : (I)V
    //   54: aload_0
    //   55: monitorexit
    //   56: return
    //   57: astore_1
    //   58: aload_0
    //   59: monitorexit
    //   60: aload_1
    //   61: athrow
    // Exception table:
    //   from	to	target	type
    //   2	39	57	finally
    //   42	54	57	finally }
  
  private void runChecks() {
    while (true) {
      LicenseValidator licenseValidator = (LicenseValidator)this.mPendingChecks.poll();
      if (licenseValidator != null)
        try {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("Calling checkLicense on service for ");
          stringBuilder.append(licenseValidator.getPackageName());
          Log.i("LicenseChecker", stringBuilder.toString());
          this.mService.checkLicense(licenseValidator.getNonce(), licenseValidator.getPackageName(), new ResultListener(licenseValidator));
          this.mChecksInProgress.add(licenseValidator);
          continue;
        } catch (RemoteException remoteException) {
          Log.w("LicenseChecker", "RemoteException in checkLicense call.", remoteException);
          handleServiceConnectionError(licenseValidator);
          continue;
        }  
      break;
    } 
  }
  
  public void checkAccess(LicenseCheckerCallback paramLicenseCheckerCallback) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mPolicy : Lcom/google/android/vending/licensing/Policy;
    //   6: invokeinterface allowAccess : ()Z
    //   11: ifeq -> 35
    //   14: ldc 'LicenseChecker'
    //   16: ldc_w 'Using cached license response'
    //   19: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
    //   22: pop
    //   23: aload_1
    //   24: sipush #256
    //   27: invokeinterface allow : (I)V
    //   32: goto -> 257
    //   35: new com/google/android/vending/licensing/LicenseValidator
    //   38: dup
    //   39: aload_0
    //   40: getfield mPolicy : Lcom/google/android/vending/licensing/Policy;
    //   43: new com/google/android/vending/licensing/NullDeviceLimiter
    //   46: dup
    //   47: invokespecial <init> : ()V
    //   50: aload_1
    //   51: aload_0
    //   52: invokespecial generateNonce : ()I
    //   55: aload_0
    //   56: getfield mPackageName : Ljava/lang/String;
    //   59: aload_0
    //   60: getfield mVersionCode : Ljava/lang/String;
    //   63: invokespecial <init> : (Lcom/google/android/vending/licensing/Policy;Lcom/google/android/vending/licensing/DeviceLimiter;Lcom/google/android/vending/licensing/LicenseCheckerCallback;ILjava/lang/String;Ljava/lang/String;)V
    //   66: astore_2
    //   67: aload_0
    //   68: getfield mService : Lcom/google/android/vending/licensing/ILicensingService;
    //   71: ifnonnull -> 242
    //   74: ldc 'LicenseChecker'
    //   76: ldc_w 'Binding to licensing service.'
    //   79: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
    //   82: pop
    //   83: getstatic android/os/Build$VERSION.SDK_INT : I
    //   86: bipush #21
    //   88: if_icmplt -> 160
    //   91: aload_0
    //   92: getfield mContext : Landroid/content/Context;
    //   95: new android/content/Intent
    //   98: dup
    //   99: new java/lang/String
    //   102: dup
    //   103: ldc_w 'Y29tLmFuZHJvaWQudmVuZGluZy5saWNlbnNpbmcuSUxpY2Vuc2luZ1NlcnZpY2U='
    //   106: invokestatic decode : (Ljava/lang/String;)[B
    //   109: invokespecial <init> : ([B)V
    //   112: invokespecial <init> : (Ljava/lang/String;)V
    //   115: ldc_w 'com.android.vending'
    //   118: invokevirtual setPackage : (Ljava/lang/String;)Landroid/content/Intent;
    //   121: aload_0
    //   122: iconst_1
    //   123: invokevirtual bindService : (Landroid/content/Intent;Landroid/content/ServiceConnection;I)Z
    //   126: ifeq -> 143
    //   129: aload_0
    //   130: getfield mPendingChecks : Ljava/util/Queue;
    //   133: aload_2
    //   134: invokeinterface offer : (Ljava/lang/Object;)Z
    //   139: pop
    //   140: goto -> 257
    //   143: ldc 'LicenseChecker'
    //   145: ldc_w 'Could not bind to service.'
    //   148: invokestatic e : (Ljava/lang/String;Ljava/lang/String;)I
    //   151: pop
    //   152: aload_0
    //   153: aload_2
    //   154: invokespecial handleServiceConnectionError : (Lcom/google/android/vending/licensing/LicenseValidator;)V
    //   157: goto -> 257
    //   160: aload_0
    //   161: getfield mContext : Landroid/content/Context;
    //   164: new android/content/Intent
    //   167: dup
    //   168: new java/lang/String
    //   171: dup
    //   172: ldc_w 'Y29tLmFuZHJvaWQudmVuZGluZy5saWNlbnNpbmcuSUxpY2Vuc2luZ1NlcnZpY2U='
    //   175: invokestatic decode : (Ljava/lang/String;)[B
    //   178: invokespecial <init> : ([B)V
    //   181: invokespecial <init> : (Ljava/lang/String;)V
    //   184: aload_0
    //   185: iconst_1
    //   186: invokevirtual bindService : (Landroid/content/Intent;Landroid/content/ServiceConnection;I)Z
    //   189: ifeq -> 206
    //   192: aload_0
    //   193: getfield mPendingChecks : Ljava/util/Queue;
    //   196: aload_2
    //   197: invokeinterface offer : (Ljava/lang/Object;)Z
    //   202: pop
    //   203: goto -> 257
    //   206: ldc 'LicenseChecker'
    //   208: ldc_w 'Could not bind to service.'
    //   211: invokestatic e : (Ljava/lang/String;Ljava/lang/String;)I
    //   214: pop
    //   215: aload_0
    //   216: aload_2
    //   217: invokespecial handleServiceConnectionError : (Lcom/google/android/vending/licensing/LicenseValidator;)V
    //   220: goto -> 257
    //   223: astore_1
    //   224: aload_1
    //   225: invokevirtual printStackTrace : ()V
    //   228: goto -> 257
    //   231: aload_1
    //   232: bipush #6
    //   234: invokeinterface applicationError : (I)V
    //   239: goto -> 257
    //   242: aload_0
    //   243: getfield mPendingChecks : Ljava/util/Queue;
    //   246: aload_2
    //   247: invokeinterface offer : (Ljava/lang/Object;)Z
    //   252: pop
    //   253: aload_0
    //   254: invokespecial runChecks : ()V
    //   257: aload_0
    //   258: monitorexit
    //   259: return
    //   260: astore_1
    //   261: aload_0
    //   262: monitorexit
    //   263: aload_1
    //   264: athrow
    //   265: astore_2
    //   266: goto -> 231
    // Exception table:
    //   from	to	target	type
    //   2	32	260	finally
    //   35	83	260	finally
    //   83	140	265	java/lang/SecurityException
    //   83	140	223	com/google/android/vending/licensing/util/Base64DecoderException
    //   83	140	260	finally
    //   143	157	265	java/lang/SecurityException
    //   143	157	223	com/google/android/vending/licensing/util/Base64DecoderException
    //   143	157	260	finally
    //   160	203	265	java/lang/SecurityException
    //   160	203	223	com/google/android/vending/licensing/util/Base64DecoderException
    //   160	203	260	finally
    //   206	220	265	java/lang/SecurityException
    //   206	220	223	com/google/android/vending/licensing/util/Base64DecoderException
    //   206	220	260	finally
    //   224	228	260	finally
    //   231	239	260	finally
    //   242	257	260	finally }
  
  public void onDestroy() { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial cleanupService : ()V
    //   6: aload_0
    //   7: getfield mHandler : Landroid/os/Handler;
    //   10: invokevirtual getLooper : ()Landroid/os/Looper;
    //   13: invokevirtual quit : ()V
    //   16: aload_0
    //   17: monitorexit
    //   18: return
    //   19: astore_1
    //   20: aload_0
    //   21: monitorexit
    //   22: aload_1
    //   23: athrow
    // Exception table:
    //   from	to	target	type
    //   2	16	19	finally }
  
  public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_2
    //   4: invokestatic asInterface : (Landroid/os/IBinder;)Lcom/google/android/vending/licensing/ILicensingService;
    //   7: putfield mService : Lcom/google/android/vending/licensing/ILicensingService;
    //   10: aload_0
    //   11: invokespecial runChecks : ()V
    //   14: aload_0
    //   15: monitorexit
    //   16: return
    //   17: astore_1
    //   18: aload_0
    //   19: monitorexit
    //   20: aload_1
    //   21: athrow
    // Exception table:
    //   from	to	target	type
    //   2	14	17	finally }
  
  public void onServiceDisconnected(ComponentName paramComponentName) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: ldc 'LicenseChecker'
    //   4: ldc_w 'Service unexpectedly disconnected.'
    //   7: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
    //   10: pop
    //   11: aload_0
    //   12: aconst_null
    //   13: putfield mService : Lcom/google/android/vending/licensing/ILicensingService;
    //   16: aload_0
    //   17: monitorexit
    //   18: return
    //   19: astore_1
    //   20: aload_0
    //   21: monitorexit
    //   22: aload_1
    //   23: athrow
    // Exception table:
    //   from	to	target	type
    //   2	16	19	finally }
  
  private class ResultListener extends ILicenseResultListener.Stub {
    private static final int ERROR_CONTACTING_SERVER = 257;
    
    private static final int ERROR_INVALID_PACKAGE_NAME = 258;
    
    private static final int ERROR_NON_MATCHING_UID = 259;
    
    private Runnable mOnTimeout;
    
    private final LicenseValidator mValidator;
    
    public ResultListener(LicenseValidator param1LicenseValidator) {
      this.mValidator = param1LicenseValidator;
      this.mOnTimeout = new Runnable() {
          public void run() {
            Log.i("LicenseChecker", "Check timed out.");
            LicenseChecker.ResultListener.this.this$0.handleServiceConnectionError(LicenseChecker.ResultListener.this.mValidator);
            LicenseChecker.ResultListener.this.this$0.finishCheck(LicenseChecker.ResultListener.this.mValidator);
          }
        };
      startTimeout();
    }
    
    private void clearTimeout() {
      Log.i("LicenseChecker", "Clearing timeout.");
      LicenseChecker.this.mHandler.removeCallbacks(this.mOnTimeout);
    }
    
    private void startTimeout() {
      Log.i("LicenseChecker", "Start monitoring timeout.");
      LicenseChecker.this.mHandler.postDelayed(this.mOnTimeout, 10000L);
    }
    
    public void verifyLicense(final int responseCode, final String signedData, final String signature) { LicenseChecker.this.mHandler.post(new Runnable() {
            public void run() {
              Log.i("LicenseChecker", "Received response.");
              if (LicenseChecker.ResultListener.this.this$0.mChecksInProgress.contains(LicenseChecker.ResultListener.this.mValidator)) {
                LicenseChecker.ResultListener.this.clearTimeout();
                LicenseChecker.ResultListener.this.mValidator.verify(LicenseChecker.ResultListener.this.this$0.mPublicKey, responseCode, signedData, signature);
                LicenseChecker.ResultListener.this.this$0.finishCheck(LicenseChecker.ResultListener.this.mValidator);
              } 
            }
          }); }
  }
  
  class null implements Runnable {
    public void run() {
      Log.i("LicenseChecker", "Check timed out.");
      this.this$1.this$0.handleServiceConnectionError(this.this$1.mValidator);
      this.this$1.this$0.finishCheck(this.this$1.mValidator);
    }
  }
  
  class null implements Runnable {
    public void run() {
      Log.i("LicenseChecker", "Received response.");
      if (this.this$1.this$0.mChecksInProgress.contains(this.this$1.mValidator)) {
        this.this$1.clearTimeout();
        this.this$1.mValidator.verify(this.this$1.this$0.mPublicKey, responseCode, signedData, signature);
        this.this$1.this$0.finishCheck(this.this$1.mValidator);
      } 
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/google/android/vending/licensing/LicenseChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */