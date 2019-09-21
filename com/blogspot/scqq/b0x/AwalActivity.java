package com.blogspot.scqq.b0x;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.TextView;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

public class AwalActivity extends Activity {
  private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA21V+WQut092mHG64lWCHuYdawk7NwcVZAp7m54dnr019C9kFS+BcNsh9u0SsCpcQig/pC1groKPQuiYP7VdOntKvo0ym3jq2PNLDBraRCavIHJrQE/Ifeks+pIuZ8eio2wbnDIW4gwIysEP2Xr8iCe+LKoXiJVaXpWXONs7zNJYnWb4i/KzHbokDGR0UsOQYxFKD8zJrF7GMz3Xalxw4l5TCsMe1oxMowxF7G2NDtzuKWGfeY/IhDLngYPBs7WdMXE8Sy2Nxm/R5+cyhNID7T6+YH3zQletxfaf0d+v33U6jl4zMfPA+M5ZlAJvQdUyPvdZbcehz8VkQL0tfYzEZGQIDAQAB";
  
  private static final byte[] SALT = { 
      -46, 65, 30, Byte.MIN_VALUE, -103, -57, 74, -64, 51, 88, 
      -95, -45, 77, -117, -36, -113, -11, 32, -64, 89 };
  
  private LicenseChecker mChecker;
  
  private Handler mHandler;
  
  private LicenseCheckerCallback mLicenseCheckerCallback;
  
  private TextView mStatusText;
  
  private void displayDialog(final boolean showRetry) { this.mHandler.post(new Runnable() {
          public void run() { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
        }); }
  
  private void displayResult(String paramString) { this.mHandler.post(new Runnable() {
          public void run() { AwalActivity.this.setProgressBarIndeterminateVisibility(false); }
        }); }
  
  private void doCheck() {
    setProgressBarIndeterminateVisibility(true);
    this.mChecker.checkAccess(this.mLicenseCheckerCallback);
  }
  
  public void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);
    requestWindowFeature(5);
    setContentView(2131361819);
    this.mStatusText = (TextView)findViewById(2131230909);
    this.mHandler = new Handler();
    String str = Settings.Secure.getString(getContentResolver(), "android_id");
    this.mLicenseCheckerCallback = new MyLicenseCheckerCallback(null);
    this.mChecker = new LicenseChecker(this, new ServerManagedPolicy(this, new AESObfuscator(SALT, getPackageName(), str)), "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA21V+WQut092mHG64lWCHuYdawk7NwcVZAp7m54dnr019C9kFS+BcNsh9u0SsCpcQig/pC1groKPQuiYP7VdOntKvo0ym3jq2PNLDBraRCavIHJrQE/Ifeks+pIuZ8eio2wbnDIW4gwIysEP2Xr8iCe+LKoXiJVaXpWXONs7zNJYnWb4i/KzHbokDGR0UsOQYxFKD8zJrF7GMz3Xalxw4l5TCsMe1oxMowxF7G2NDtzuKWGfeY/IhDLngYPBs7WdMXE8Sy2Nxm/R5+cyhNID7T6+YH3zQletxfaf0d+v33U6jl4zMfPA+M5ZlAJvQdUyPvdZbcehz8VkQL0tfYzEZGQIDAQAB");
    doCheck();
  }
  
  protected Dialog onCreateDialog(int paramInt) {
    String str;
    final boolean bRetry = true;
    if (paramInt != 1)
      bool = false; 
    AlertDialog.Builder builder = (new AlertDialog.Builder(this)).setTitle("Informasi Lisensi");
    if (bool) {
      str = "Kesalahan saat verifikasi lisensi flyexam, pastikan hp anda terkoneksi ke internet dan silahkan coba lagi";
    } else {
      str = "Maaf, Aplikasi flyexam yang anda gunakan dideteksi sebagai aplikasi BAJAKAN, pastikan anda mengunduh flyexam terbaru dengan menyentuh tombol Beli FlyExam";
    } 
    builder = builder.setMessage(str);
    if (bool) {
      str = "Coba Lgi";
    } else {
      str = "Beli";
    } 
    return builder.setPositiveButton(str, new DialogInterface.OnClickListener() {
          boolean mRetry = bRetry;
          
          public void onClick(DialogInterface param1DialogInterface, int param1Int) {
            if (this.mRetry) {
              AwalActivity.this.doCheck();
              return;
            } 
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://market.android.com/details?id=");
            stringBuilder.append(AwalActivity.this.getPackageName());
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString()));
            AwalActivity.this.startActivity(intent);
          }
        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface param1DialogInterface, int param1Int) { AwalActivity.this.finish(); }
        }).create();
  }
  
  protected void onDestroy() {
    super.onDestroy();
    this.mChecker.onDestroy();
  }
  
  private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
    private MyLicenseCheckerCallback() {}
    
    public void allow(int param1Int) {
      if (AwalActivity.this.isFinishing())
        return; 
      AwalActivity.this.displayResult("Boleh");
      Intent intent = new Intent(AwalActivity.this, InputUrlActivity.class);
      AwalActivity.this.startActivity(intent);
      AwalActivity.this.finish();
    }
    
    public void applicationError(int param1Int) {
      if (AwalActivity.this.isFinishing())
        return; 
      String str = String.format("Error", new Object[] { Integer.valueOf(param1Int) });
      AwalActivity.this.displayResult(str);
    }
    
    public void dontAllow(int param1Int) {
      boolean bool;
      if (AwalActivity.this.isFinishing())
        return; 
      AwalActivity awalActivity = AwalActivity.this;
      if (param1Int == 291) {
        bool = true;
      } else {
        bool = false;
      } 
      awalActivity.displayDialog(bool);
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/blogspot/scqq/b0x/AwalActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */