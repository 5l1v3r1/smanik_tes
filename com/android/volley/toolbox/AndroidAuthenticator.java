package com.android.volley.toolbox;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.android.volley.AuthFailureError;

public class AndroidAuthenticator implements Authenticator {
  private final Account mAccount;
  
  private final AccountManager mAccountManager;
  
  private final String mAuthTokenType;
  
  private final boolean mNotifyAuthFailure;
  
  AndroidAuthenticator(AccountManager paramAccountManager, Account paramAccount, String paramString, boolean paramBoolean) {
    this.mAccountManager = paramAccountManager;
    this.mAccount = paramAccount;
    this.mAuthTokenType = paramString;
    this.mNotifyAuthFailure = paramBoolean;
  }
  
  public AndroidAuthenticator(Context paramContext, Account paramAccount, String paramString) { this(paramContext, paramAccount, paramString, false); }
  
  public AndroidAuthenticator(Context paramContext, Account paramAccount, String paramString, boolean paramBoolean) { this(AccountManager.get(paramContext), paramAccount, paramString, paramBoolean); }
  
  public Account getAccount() { return this.mAccount; }
  
  public String getAuthToken() throws AuthFailureError {
    AccountManagerFuture accountManagerFuture = this.mAccountManager.getAuthToken(this.mAccount, this.mAuthTokenType, this.mNotifyAuthFailure, null, null);
    try {
      Bundle bundle = (Bundle)accountManagerFuture.getResult();
      Object object = null;
      String str = object;
      if (accountManagerFuture.isDone()) {
        str = object;
        if (!accountManagerFuture.isCancelled()) {
          if (bundle.containsKey("intent"))
            throw new AuthFailureError((Intent)bundle.getParcelable("intent")); 
          str = bundle.getString("authtoken");
        } 
      } 
      if (str == null) {
        str = String.valueOf(this.mAuthTokenType);
        if (str.length() != 0) {
          str = "Got null auth token for type: ".concat(str);
        } else {
          str = new String("Got null auth token for type: ");
        } 
        throw new AuthFailureError(str);
      } 
      return str;
    } catch (Exception exception) {
      throw new AuthFailureError("Error while retrieving auth token", exception);
    } 
  }
  
  public String getAuthTokenType() throws AuthFailureError { return this.mAuthTokenType; }
  
  public void invalidateAuthToken(String paramString) { this.mAccountManager.invalidateAuthToken(this.mAccount.type, paramString); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/android/volley/toolbox/AndroidAuthenticator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */