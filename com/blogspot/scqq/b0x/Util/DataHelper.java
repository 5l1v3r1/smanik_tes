package com.blogspot.scqq.b0x.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "flyexambrowser.db";
  
  private static final int DATABASE_VERSION = 1;
  
  public DataHelper(Context paramContext) { super(paramContext, "flyexambrowser.db", null, 1); }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("onCreate: ");
    stringBuilder.append("create table serialNumber(idSerialNumber integer primary key, nama text null);");
    Log.d("Data", stringBuilder.toString());
    paramSQLiteDatabase.execSQL("create table serialNumber(idSerialNumber integer primary key, nama text null);");
    paramSQLiteDatabase.execSQL("INSERT INTO serialNumber (idSerialNumber, nama) VALUES ('1', 'flyexam');");
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/blogspot/scqq/b0x/Util/DataHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */