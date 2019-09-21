package com.blogspot.scqq.b0x;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.blogspot.scqq.b0x.Util.AppController;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;

public class InputUrlActivity extends AppCompatActivity {
  private static EditText inputAddress;
  
  public static SharedPreferences prefs;
  
  private Button btnLanjut;
  
  public String flyExambro;
  
  private TextInputLayout inputLayoutAddress;
  
  private String s = "online";
  
  private void requestFocus(View paramView) {
    if (paramView.requestFocus())
      getWindow().setSoftInputMode(5); 
  }
  
  private void submitForm() {
    if (validateAddress()) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("http://");
      stringBuilder.append(inputAddress.getText().toString());
      stringBuilder.append("/api/getAndroid.php");
      JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(0, stringBuilder.toString(), null, new Response.Listener<JSONArray>(this) {
            public void onResponse(JSONArray param1JSONArray) {
              for (byte b = 0; b < param1JSONArray.length(); b++) {
                try {
                  if (param1JSONArray.getJSONObject(b).getString("get").contains("1")) {
                    SharedPreferences.Editor editor = InputUrlActivity.prefs.edit();
                    editor.putString("flyexambro", "1");
                    editor.apply();
                  } else {
                    SharedPreferences.Editor editor = InputUrlActivity.prefs.edit();
                    editor.putString("flyexambro", "2");
                    editor.apply();
                  } 
                } catch (JSONException jSONException) {
                  jSONException.printStackTrace();
                  SharedPreferences.Editor editor = InputUrlActivity.prefs.edit();
                  editor.putString("flyexambro", "2");
                  editor.apply();
                } 
              } 
            }
          }new Response.ErrorListener(this) {
            public void onErrorResponse(VolleyError param1VolleyError) {
              SharedPreferences.Editor editor = InputUrlActivity.prefs.edit();
              editor.putString("flyexambro", "2");
              editor.apply();
            }
          });
      AppController.getInstance().addToRequestQueue(jsonArrayRequest);
      Intent intent = new Intent(getBaseContext(), MainActivity.class);
      intent.putExtra("url", inputAddress.getText().toString());
      startActivity(intent);
      finish();
      return;
    } 
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString("flyexambro", "2");
    editor.apply();
  }
  
  private boolean validateAddress() {
    if (inputAddress.getText().toString().trim().isEmpty()) {
      this.inputLayoutAddress.setError(getString(2131623969));
      requestFocus(inputAddress);
      return false;
    } 
    this.inputLayoutAddress.setErrorEnabled(false);
    return true;
  }
  
  public void onBackPressed() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Informasi");
    builder.setMessage("Apakah anda yakin ingin keluar dari aplikasi ini?").setCancelable(false).setPositiveButton("Iya", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface param1DialogInterface, int param1Int) { System.exit(0); }
        }).setNegativeButton("Tidak, Terimakasih", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface param1DialogInterface, int param1Int) { param1DialogInterface.cancel(); }
        });
    builder.create().show();
  }
  
  protected void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);
    setContentView(2131361821);
    getWindow().setFlags(1024, 1024);
    int i = Calendar.getInstance().get(1);
    TextView textView = (TextView)findViewById(2131230811);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Copyright © ");
    stringBuilder.append(String.valueOf(i));
    textView.setText(stringBuilder.toString());
    this.inputLayoutAddress = (TextInputLayout)findViewById(2131230817);
    inputAddress = (EditText)findViewById(2131230816);
    inputAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          public void onFocusChange(View param1View, boolean param1Boolean) {
            if (param1Boolean) {
              InputUrlActivity inputUrlActivity1;
              EditText editText = inputAddress;
              InputUrlActivity inputUrlActivity2;
              editText.setSelection(inputAddress.getText().length());
            } 
          }
        });
    this.btnLanjut = (Button)findViewById(2131230760);
    inputAddress.addTextChangedListener(new MyTextWatcher(inputAddress, null));
    this.btnLanjut.setOnClickListener(new View.OnClickListener() {
          public void onClick(View param1View) { InputUrlActivity.this.submitForm(); }
        });
    if (!CekKoneksi.isNetworkStatusAvialable(this))
      (new AlertDialog.Builder(this)).setIcon(2131165298).setTitle("Peringatan").setMessage("Maaf, Tampaknya perangkat anda tidak terhubung dengan jaringan!").setCancelable(false).setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface param1DialogInterface, int param1Int) { param1DialogInterface.dismiss(); }
          }).show(); 
    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    inputAddress.setText(prefs.getString("autoSave", ""));
    inputAddress.addTextChangedListener(new TextWatcher() {
          public void afterTextChanged(Editable param1Editable) { InputUrlActivity.prefs.edit().putString("autoSave", param1Editable.toString()).commit(); }
          
          public void beforeTextChanged(CharSequence param1CharSequence, int param1Int1, int param1Int2, int param1Int3) {}
          
          public void onTextChanged(CharSequence param1CharSequence, int param1Int1, int param1Int2, int param1Int3) {}
        });
    inputAddress.setOnKeyListener(new View.OnKeyListener() {
          public boolean onKey(View param1View, int param1Int, KeyEvent param1KeyEvent) {
            if (param1KeyEvent.getAction() != 0 || (param1Int != 23 && param1Int != 66))
              return false; 
            InputUrlActivity.this.submitForm();
            return true;
          }
        });
  }
  
  private class MyTextWatcher implements TextWatcher {
    private View view;
    
    private MyTextWatcher(View param1View) { this.view = param1View; }
    
    public void afterTextChanged(Editable param1Editable) {
      if (this.view.getId() != 2131230816)
        return; 
      InputUrlActivity.this.validateAddress();
    }
    
    public void beforeTextChanged(CharSequence param1CharSequence, int param1Int1, int param1Int2, int param1Int3) {}
    
    public void onTextChanged(CharSequence param1CharSequence, int param1Int1, int param1Int2, int param1Int3) {}
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/blogspot/scqq/b0x/InputUrlActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */