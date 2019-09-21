package com.google.android.vending.licensing;

import android.text.TextUtils;
import android.util.Log;
import com.google.android.vending.licensing.util.Base64;
import com.google.android.vending.licensing.util.Base64DecoderException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

class LicenseValidator {
  private static final int ERROR_CONTACTING_SERVER = 257;
  
  private static final int ERROR_INVALID_PACKAGE_NAME = 258;
  
  private static final int ERROR_NON_MATCHING_UID = 259;
  
  private static final int ERROR_NOT_MARKET_MANAGED = 3;
  
  private static final int ERROR_OVER_QUOTA = 5;
  
  private static final int ERROR_SERVER_FAILURE = 4;
  
  private static final int LICENSED = 0;
  
  private static final int LICENSED_OLD_KEY = 2;
  
  private static final int NOT_LICENSED = 1;
  
  private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
  
  private static final String TAG = "LicenseValidator";
  
  private final LicenseCheckerCallback mCallback;
  
  private final DeviceLimiter mDeviceLimiter;
  
  private final int mNonce;
  
  private final String mPackageName;
  
  private final Policy mPolicy;
  
  private final String mVersionCode;
  
  LicenseValidator(Policy paramPolicy, DeviceLimiter paramDeviceLimiter, LicenseCheckerCallback paramLicenseCheckerCallback, int paramInt, String paramString1, String paramString2) {
    this.mPolicy = paramPolicy;
    this.mDeviceLimiter = paramDeviceLimiter;
    this.mCallback = paramLicenseCheckerCallback;
    this.mNonce = paramInt;
    this.mPackageName = paramString1;
    this.mVersionCode = paramString2;
  }
  
  private void handleApplicationError(int paramInt) { this.mCallback.applicationError(paramInt); }
  
  private void handleInvalidResponse() { this.mCallback.dontAllow(561); }
  
  private void handleResponse(int paramInt, ResponseData paramResponseData) {
    this.mPolicy.processServerResponse(paramInt, paramResponseData);
    if (this.mPolicy.allowAccess()) {
      this.mCallback.allow(paramInt);
      return;
    } 
    this.mCallback.dontAllow(paramInt);
  }
  
  public LicenseCheckerCallback getCallback() { return this.mCallback; }
  
  public int getNonce() { return this.mNonce; }
  
  public String getPackageName() { return this.mPackageName; }
  
  public void verify(PublicKey paramPublicKey, int paramInt, String paramString1, String paramString2) {
    Signature signature2 = null;
    if (paramInt == 0 || paramInt == 1 || paramInt == 2)
      try {
        signature2 = Signature.getInstance("SHA1withRSA");
        signature2.initVerify(paramPublicKey);
        signature2.update(paramString1.getBytes());
        signature2.verify(Base64.decode(paramString2));
        if (!true) {
          Log.e("LicenseValidator", "Signature verification failed.");
          handleInvalidResponse();
          return;
        } 
        try {
          ResponseData responseData = ResponseData.parse(paramString1);
          if (responseData.responseCode != paramInt) {
            Log.e("LicenseValidator", "Response codes don't match.");
            handleInvalidResponse();
            return;
          } 
          if (responseData.nonce != this.mNonce) {
            Log.e("LicenseValidator", "Nonce doesn't match.");
            handleInvalidResponse();
            return;
          } 
          if (!responseData.packageName.equals(this.mPackageName)) {
            Log.e("LicenseValidator", "Package name doesn't match.");
            handleInvalidResponse();
            return;
          } 
          if (!responseData.versionCode.equals(this.mVersionCode)) {
            Log.e("LicenseValidator", "Version codes don't match.");
            handleInvalidResponse();
            return;
          } 
          paramString2 = responseData.userId;
          paramString1 = paramString2;
          if (TextUtils.isEmpty(paramString2)) {
            Log.e("LicenseValidator", "User identifier is empty.");
            handleInvalidResponse();
            return;
          } 
          switch (paramInt) {
            default:
              switch (paramInt) {
                default:
                  Log.e("LicenseValidator", "Unknown response code for license check.");
                  handleInvalidResponse();
                  return;
                case 259:
                  handleApplicationError(2);
                  return;
                case 258:
                  handleApplicationError(1);
                  return;
                case 257:
                  break;
              } 
              Log.w("LicenseValidator", "Error contacting licensing server.");
              handleResponse(291, responseData);
              return;
            case 5:
              Log.w("LicenseValidator", "Licensing server is refusing to talk to this device, over quota.");
              handleResponse(291, responseData);
              return;
            case 4:
              Log.w("LicenseValidator", "An error has occurred on the licensing server.");
              handleResponse(291, responseData);
              return;
            case 3:
              handleApplicationError(3);
              return;
            case 1:
              handleResponse(561, responseData);
              return;
            case 0:
            case 2:
              break;
          } 
          handleResponse(this.mDeviceLimiter.isDeviceAllowed(paramString1), responseData);
          return;
        } catch (IllegalArgumentException paramPublicKey) {
          Log.e("LicenseValidator", "Could not parse response.");
          handleInvalidResponse();
        } 
        return;
      } catch (NoSuchAlgorithmException paramPublicKey) {
        throw new RuntimeException(paramPublicKey);
      } catch (InvalidKeyException paramPublicKey) {
        handleApplicationError(5);
        return;
      } catch (SignatureException paramPublicKey) {
        throw new RuntimeException(paramPublicKey);
      } catch (Base64DecoderException paramPublicKey) {
        Log.e("LicenseValidator", "Could not Base64-decode signature.");
        handleInvalidResponse();
        return;
      }  
    paramString1 = null;
    Signature signature1 = signature2;
    switch (paramInt) {
      default:
        switch (paramInt) {
          default:
            Log.e("LicenseValidator", "Unknown response code for license check.");
            handleInvalidResponse();
            return;
          case 259:
            handleApplicationError(2);
            return;
          case 258:
            handleApplicationError(1);
            return;
          case 257:
            break;
        } 
        Log.w("LicenseValidator", "Error contacting licensing server.");
        handleResponse(291, signature1);
        return;
      case 5:
        Log.w("LicenseValidator", "Licensing server is refusing to talk to this device, over quota.");
        handleResponse(291, signature1);
        return;
      case 4:
        Log.w("LicenseValidator", "An error has occurred on the licensing server.");
        handleResponse(291, signature1);
        return;
      case 3:
        handleApplicationError(3);
        return;
      case 1:
        handleResponse(561, signature1);
        return;
      case 0:
      case 2:
        break;
    } 
    handleResponse(this.mDeviceLimiter.isDeviceAllowed(paramString1), signature1);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/google/android/vending/licensing/LicenseValidator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */