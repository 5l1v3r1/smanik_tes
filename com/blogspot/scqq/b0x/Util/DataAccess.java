package com.blogspot.scqq.b0x.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataAccess {
  public static SQLiteDatabase database;
  
  public static DataAccess instance;
  
  public SQLiteOpenHelper openHelper;
  
  private DataAccess(Context paramContext) { this.openHelper = new DataHelper(paramContext); }
  
  public static DataAccess getInstance(Context paramContext) {
    if (instance == null)
      instance = new DataAccess(paramContext); 
    return instance;
  }
  
  public void close() {
    if (database != null)
      database.close(); 
  }
  
  public String getSerialNumber() {
    String str = "";
    Cursor cursor = database.rawQuery("SELECT * FROM serialNumber where idSerialNumber=1", null);
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      str = cursor.getString(1);
      cursor.moveToNext();
    } 
    cursor.close();
    return str;
  }
  
  public void open() { database = this.openHelper.getWritableDatabase(); }
  
  public boolean updateSerialNumber(Long paramLong) {
    ContentValues contentValues = new ContentValues();
    contentValues.put("nomor", paramLong);
    return (database.update("serialNumber", contentValues, "idSerialNumber=1", null) > 0);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/blogspot/scqq/b0x/Util/DataAccess.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */