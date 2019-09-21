package com.blogspot.scqq.b0x;

import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.blogspot.scqq.b0x.Util.AppController;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
  private AVLoadingIndicatorView avi;
  
  Button btnrefresh;
  
  String f;
  
  ImageView imgError;
  
  private DrawerLayout mDrawerLayout;
  
  MyTimerTask myTimerTask;
  
  private NavigationView navigationView;
  
  String password;
  
  String s;
  
  private SwipeRefreshLayout swipeRefreshLayout;
  
  TextView textError;
  
  TextView textViewLoading;
  
  Timer timer;
  
  TextView txtsolusi;
  
  WebView view;
  
  private void bisaKeluar() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("INFORMASI");
    builder.setMessage("Apakah anda yakin ingin keluar dari aplikasi ini?").setCancelable(false).setPositiveButton("Iya", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface param1DialogInterface, int param1Int) { System.exit(0); }
        }).setNegativeButton("Tidak, Terima kasih", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface param1DialogInterface, int param1Int) { param1DialogInterface.cancel(); }
        });
    builder.create().show();
  }
  
  private void bringApplicationToFront() {
    if (((KeyguardManager)getSystemService("keyguard")).inKeyguardRestrictedInputMode())
      return; 
    Log.d("TAG", "====Bringging Application to Front====");
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(603979776);
    pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    try {
      pendingIntent.send();
      return;
    } catch (android.app.PendingIntent.CanceledException pendingIntent) {
      pendingIntent.printStackTrace();
      return;
    } 
  }
  
  private void cekPrivacy() {
    try {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("http://");
      stringBuilder.append(this.s);
      stringBuilder.append("/api/getAndroid.php");
      JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(0, stringBuilder.toString(), null, new Response.Listener<JSONArray>(this) {
            public void onResponse(JSONArray param1JSONArray) {
              byte b = 0;
              while (true) {
                if (b < param1JSONArray.length()) {
                  try {
                    if (param1JSONArray.getJSONObject(b).getString("get").contains("1")) {
                      SharedPreferences.Editor editor = InputUrlActivity.prefs.edit();
                      editor.putString("flyexambro", "1");
                      editor.apply();
                    } 
                  } catch (JSONException jSONException) {}
                  b++;
                  continue;
                } 
                return;
              } 
            }
          }new Response.ErrorListener(this) {
            public void onErrorResponse(VolleyError param1VolleyError) {}
          });
      AppController.getInstance().addToRequestQueue(jsonArrayRequest);
      return;
    } catch (Error error) {
      error.printStackTrace();
      return;
    } 
  }
  
  private void tentangHarison() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Tentang Kami");
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Aplikasi Browser Ujian Online ");
    stringBuilder.append(String.format(getString(2131623980), new Object[0]));
    stringBuilder.append(", Copyright © Delta Microtech");
    builder.setMessage(stringBuilder.toString()).setCancelable(false).setPositiveButton("Ok, Terima Kasih", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface param1DialogInterface, int param1Int) { param1DialogInterface.cancel(); }
        });
    builder.create().show();
    this.mDrawerLayout.closeDrawers();
  }
  
  public void hideError() {
    this.swipeRefreshLayout.setRefreshing(false);
    this.textError.setVisibility(8);
    this.txtsolusi.setVisibility(8);
    this.imgError.setVisibility(8);
    this.btnrefresh.setVisibility(8);
  }
  
  public void onBackPressed() {
    InputUrlActivity.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    if (InputUrlActivity.prefs.getString("flyexambro", "").contains("1")) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("INFORMASI");
      builder.setCancelable(false);
      builder.setMessage("Untuk keluar dari aplikasi, silahkan masukkan TOKEN dengan benar!");
      final EditText input = new EditText(this);
      editText.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
      builder.setView(editText);
      builder.setIcon(2131165298);
      builder.setPositiveButton("Konfirmasi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface param1DialogInterface, int param1Int) {
              MainActivity.this.password = input.getText().toString();
              if (MainActivity.this.password.contains("b0x666")) {
                System.exit(0);
                return;
              } 
              StringBuilder stringBuilder = new StringBuilder();
              stringBuilder.append("http://");
              stringBuilder.append(InputUrlActivity.prefs.getString("autoSave", ""));
              stringBuilder.append("/api/fromAndroid.php");
              StringRequest stringRequest = new StringRequest(1, stringBuilder.toString(), new Response.Listener<String>(this, param1DialogInterface) {
                    public void onResponse(String param2String) {
                      try {
                        if ((new JSONObject(param2String)).getString("status").contains("1")) {
                          System.exit(0);
                          return;
                        } 
                        Toast.makeText(MainActivity.null.this.this$0.getApplicationContext(), "Oppss, Token yang anda masukkan salah", 0).show();
                        dialog.cancel();
                        return;
                      } catch (JSONException param2String) {
                        Toast.makeText(MainActivity.null.this.this$0.getApplicationContext(), "Maaf, Url tidak valid atau server sedang offline, Silahkan coba lagi nanti", 0).show();
                        return;
                      } 
                    }
                  }new Response.ErrorListener(this) {
                    public void onErrorResponse(VolleyError param2VolleyError) { Toast.makeText(MainActivity.null.this.this$0.getApplicationContext(), "Maaf, terjadi kesalahan, silahkan coba lagi nanti", 0).show(); }
                  }) {
                  protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap hashMap = new HashMap();
                    MainActivity.this.password = input.getText().toString();
                    hashMap.put("token", MainActivity.this.password);
                    return hashMap;
                  }
                };
              AppController.getInstance().addToRequestQueue(stringRequest);
            }
          });
      builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface param1DialogInterface, int param1Int) { param1DialogInterface.cancel(); }
          });
      builder.show();
      return;
    } 
    bisaKeluar();
  }
  
  protected void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);
    setContentView(2131361822);
    if (Build.VERSION.SDK_INT >= 11)
      getWindow().setFlags(8192, 8192); 
    getWindow().setFlags(1024, 1024);
    this.mDrawerLayout = (DrawerLayout)findViewById(2131230789);
    this.textError = (TextView)findViewById(2131230928);
    this.imgError = (ImageView)findViewById(2131230814);
    this.txtsolusi = (TextView)findViewById(2131230929);
    this.btnrefresh = (Button)findViewById(2131230761);
    this.avi = (AVLoadingIndicatorView)findViewById(2131230829);
    this.avi.setIndicator("BallSpinFadeLoaderIndicator");
    this.s = getIntent().getStringExtra("url");
    this.textViewLoading = (TextView)findViewById(2131230911);
    this.view = (WebView)findViewById(2131230747);
    this.view.getSettings().setJavaScriptEnabled(true);
    this.view.getSettings().setUseWideViewPort(true);
    this.view.getSettings().setLoadWithOverviewMode(true);
    this.view.getSettings().setSupportZoom(true);
    this.view.getSettings().setDomStorageEnabled(true);
    this.view.getSettings().setBuiltInZoomControls(true);
    this.view.getSettings().setDisplayZoomControls(false);
    this.view.getSettings().setUserAgentString("scqq.blogspot.com|harison");
    this.view.setWebViewClient(new ExamWebView(null));
    WebView webView = this.view;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("http://");
    stringBuilder.append(this.s);
    webView.loadUrl(stringBuilder.toString());
    this.swipeRefreshLayout = (SwipeRefreshLayout)findViewById(2131230901);
    this.swipeRefreshLayout.setColorSchemeColors(new int[] { -65536, -16711936, -16776961, -16711681 });
    this.swipeRefreshLayout.setRefreshing(true);
    this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
          public void onRefresh() {
            if (CekKoneksi.isNetworkStatusAvialable(MainActivity.this)) {
              MainActivity.this.view.setWebViewClient(new MainActivity.ExamWebView(MainActivity.this, null));
              WebView webView = MainActivity.this.view;
              StringBuilder stringBuilder = new StringBuilder();
              stringBuilder.append("http://");
              stringBuilder.append(MainActivity.this.s);
              webView.loadUrl(stringBuilder.toString());
              MainActivity.this.cekPrivacy();
              MainActivity.this.hideError();
              return;
            } 
            MainActivity.this.showError();
          }
        });
    hideError();
  }
  
  protected void onPause() {
    if (this.timer == null) {
      this.myTimerTask = new MyTimerTask();
      this.timer = new Timer();
      this.timer.schedule(this.myTimerTask, 100L, 100L);
    } 
    super.onPause();
  }
  
  protected void onResume() {
    super.onResume();
    if (this.timer != null) {
      this.timer.cancel();
      this.timer = null;
    } 
  }
  
  public void showError() {
    Toast.makeText(this, "Maaf, tampaknya terjadi kesalahan, periksa koneksi anda!", 0).show();
    this.swipeRefreshLayout.setRefreshing(false);
    this.textError.setVisibility(0);
    this.txtsolusi.setVisibility(0);
    this.imgError.setVisibility(0);
    this.btnrefresh.setVisibility(0);
    this.btnrefresh.setOnClickListener(new View.OnClickListener() {
          public void onClick(View param1View) {
            if (CekKoneksi.isNetworkStatusAvialable(MainActivity.this)) {
              MainActivity.this.view.setWebViewClient(new MainActivity.ExamWebView(MainActivity.this, null));
              WebView webView = MainActivity.this.view;
              StringBuilder stringBuilder = new StringBuilder();
              stringBuilder.append("http://");
              stringBuilder.append(MainActivity.this.s);
              webView.loadUrl(stringBuilder.toString());
              MainActivity.this.cekPrivacy();
              MainActivity.this.hideError();
              return;
            } 
            MainActivity.this.showError();
          }
        });
    ((WebView)findViewById(2131230747)).loadData("<html><body></body></html>", "text/html", "UTF-8");
  }
  
  private class ExamWebView extends WebViewClient {
    private ExamWebView() {}
    
    public void onPageFinished(WebView param1WebView, String param1String) {
      MainActivity.this.swipeRefreshLayout.setRefreshing(false);
      MainActivity.this.avi.smoothToHide();
      MainActivity.this.textViewLoading.setVisibility(8);
      super.onPageFinished(param1WebView, param1String);
    }
    
    public void onPageStarted(final WebView view, String param1String, Bitmap param1Bitmap) {
      InputUrlActivity.prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
      MainActivity.this.f = InputUrlActivity.prefs.getString("flyexambro", "");
      MainActivity.access$202(MainActivity.this, (NavigationView)MainActivity.this.findViewById(2131230838));
      Menu menu = MainActivity.this.navigationView.getMenu();
      if (MainActivity.this.f.contains("1")) {
        menu.findItem(2131230757).setVisible(true);
        menu.findItem(2131230919).setVisible(true);
        menu.findItem(2131230724).setVisible(true);
      } else {
        menu.findItem(2131230757).setVisible(false);
        menu.findItem(2131230919).setVisible(false);
        menu.findItem(2131230724).setVisible(false);
      } 
      MainActivity.this.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem param2MenuItem) {
              view.setWebViewClient(new MainActivity.ExamWebView(MainActivity.ExamWebView.this.this$0, null));
              switch (param2MenuItem.getItemId()) {
                default:
                  Toast.makeText(MainActivity.ExamWebView.this.this$0.getApplicationContext(), "Opss.. Terjadi kesalahan, silahkan coba lagi nanti", 0).show();
                  return true;
                case 2131230919:
                  webView = view;
                  stringBuilder = new StringBuilder();
                  stringBuilder.append("http://");
                  stringBuilder.append(MainActivity.this.s);
                  stringBuilder.append("/operator/token.php");
                  webView.loadUrl(stringBuilder.toString());
                  return true;
                case 2131230904:
                  MainActivity.ExamWebView.this.this$0.tentangHarison();
                  return true;
                case 2131230821:
                  MainActivity.ExamWebView.this.this$0.onBackPressed();
                  return true;
                case 2131230757:
                  webView = view;
                  stringBuilder = new StringBuilder();
                  stringBuilder.append("http://");
                  stringBuilder.append(MainActivity.this.s);
                  webView.loadUrl(stringBuilder.toString());
                  return true;
                case 2131230724:
                  break;
              } 
              WebView webView = view;
              StringBuilder stringBuilder = new StringBuilder();
              stringBuilder.append("http://");
              stringBuilder.append(MainActivity.this.s);
              stringBuilder.append("/operator/settings.php");
              webView.loadUrl(stringBuilder.toString());
              return true;
            }
          });
      MainActivity.this.mDrawerLayout.closeDrawers();
      MainActivity.this.avi.smoothToShow();
      MainActivity.this.textViewLoading.setVisibility(0);
      super.onPageStarted(param1WebView, param1String, param1Bitmap);
    }
    
    public void onReceivedError(WebView param1WebView, int param1Int, String param1String1, String param1String2) { MainActivity.this.showError(); }
    
    public boolean shouldOverrideUrlLoading(WebView param1WebView, String param1String) {
      if (CekKoneksi.isNetworkStatusAvialable(MainActivity.this)) {
        param1WebView.setWebViewClient(new ExamWebView(MainActivity.this));
        param1WebView.loadUrl(param1String);
      } else {
        MainActivity.this.showError();
      } 
      return true;
    }
  }
  
  class null implements NavigationView.OnNavigationItemSelectedListener {
    public boolean onNavigationItemSelected(MenuItem param1MenuItem) {
      view.setWebViewClient(new MainActivity.ExamWebView(this.this$1.this$0, null));
      switch (param1MenuItem.getItemId()) {
        default:
          Toast.makeText(this.this$1.this$0.getApplicationContext(), "Opss.. Terjadi kesalahan, silahkan coba lagi nanti", 0).show();
          return true;
        case 2131230919:
          webView = view;
          stringBuilder = new StringBuilder();
          stringBuilder.append("http://");
          stringBuilder.append(MainActivity.this.s);
          stringBuilder.append("/operator/token.php");
          webView.loadUrl(stringBuilder.toString());
          return true;
        case 2131230904:
          this.this$1.this$0.tentangHarison();
          return true;
        case 2131230821:
          this.this$1.this$0.onBackPressed();
          return true;
        case 2131230757:
          webView = view;
          stringBuilder = new StringBuilder();
          stringBuilder.append("http://");
          stringBuilder.append(MainActivity.this.s);
          webView.loadUrl(stringBuilder.toString());
          return true;
        case 2131230724:
          break;
      } 
      WebView webView = view;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("http://");
      stringBuilder.append(MainActivity.this.s);
      stringBuilder.append("/operator/settings.php");
      webView.loadUrl(stringBuilder.toString());
      return true;
    }
  }
  
  class MyTimerTask extends TimerTask {
    public void run() { MainActivity.this.bringApplicationToFront(); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/blogspot/scqq/b0x/MainActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */