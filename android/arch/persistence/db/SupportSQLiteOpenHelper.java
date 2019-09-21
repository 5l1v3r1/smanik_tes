package android.arch.persistence.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Pair;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public interface SupportSQLiteOpenHelper {
  void close();
  
  String getDatabaseName();
  
  SupportSQLiteDatabase getReadableDatabase();
  
  SupportSQLiteDatabase getWritableDatabase();
  
  @RequiresApi(api = 16)
  void setWriteAheadLoggingEnabled(boolean paramBoolean);
  
  public static abstract class Callback {
    private static final String TAG = "SupportSQLite";
    
    public final int version;
    
    public Callback(int param1Int) { this.version = param1Int; }
    
    private void deleteDatabaseFile(String param1String) {
      if (!param1String.equalsIgnoreCase(":memory:")) {
        if (param1String.trim().length() == 0)
          return; 
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("deleting the database file: ");
        stringBuilder.append(param1String);
        Log.w("SupportSQLite", stringBuilder.toString());
        try {
          if (Build.VERSION.SDK_INT >= 16) {
            SQLiteDatabase.deleteDatabase(new File(param1String));
            return;
          } 
          try {
            if (!(new File(param1String)).delete()) {
              stringBuilder = new StringBuilder();
              stringBuilder.append("Could not delete the database file ");
              stringBuilder.append(param1String);
              Log.e("SupportSQLite", stringBuilder.toString());
              return;
            } 
          } catch (Exception param1String) {
            Log.e("SupportSQLite", "error while deleting corrupted database file", param1String);
            return;
          } 
        } catch (Exception param1String) {
          Log.w("SupportSQLite", "delete failed: ", param1String);
        } 
        return;
      } 
    }
    
    public void onConfigure(SupportSQLiteDatabase param1SupportSQLiteDatabase) {}
    
    public void onCorruption(SupportSQLiteDatabase param1SupportSQLiteDatabase) {
      Iterator iterator;
      null = new StringBuilder();
      null.append("Corruption reported by sqlite on database: ");
      null.append(param1SupportSQLiteDatabase.getPath());
      Log.e("SupportSQLite", null.toString());
      if (!param1SupportSQLiteDatabase.isOpen()) {
        deleteDatabaseFile(param1SupportSQLiteDatabase.getPath());
        return;
      } 
      null = null;
      object = null;
      try {
        list2 = param1SupportSQLiteDatabase.getAttachedDbs();
      } catch (SQLiteException object) {
      
      } finally {
        if (object != null) {
          iterator = object.iterator();
          while (iterator.hasNext())
            deleteDatabaseFile((String)((Pair)iterator.next()).second); 
        } else {
          deleteDatabaseFile(iterator.getPath());
        } 
      } 
      object = SYNTHETIC_LOCAL_VARIABLE_2;
      try {
        iterator.close();
      } catch (IOException object) {}
      if (SYNTHETIC_LOCAL_VARIABLE_2 != null) {
        iterator = SYNTHETIC_LOCAL_VARIABLE_2.iterator();
        while (iterator.hasNext())
          deleteDatabaseFile((String)((Pair)iterator.next()).second); 
      } else {
        deleteDatabaseFile(iterator.getPath());
      } 
    }
    
    public abstract void onCreate(SupportSQLiteDatabase param1SupportSQLiteDatabase);
    
    public void onDowngrade(SupportSQLiteDatabase param1SupportSQLiteDatabase, int param1Int1, int param1Int2) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Can't downgrade database from version ");
      stringBuilder.append(param1Int1);
      stringBuilder.append(" to ");
      stringBuilder.append(param1Int2);
      throw new SQLiteException(stringBuilder.toString());
    }
    
    public void onOpen(SupportSQLiteDatabase param1SupportSQLiteDatabase) {}
    
    public abstract void onUpgrade(SupportSQLiteDatabase param1SupportSQLiteDatabase, int param1Int1, int param1Int2);
  }
  
  public static class Configuration {
    @NonNull
    public final SupportSQLiteOpenHelper.Callback callback;
    
    @NonNull
    public final Context context;
    
    @Nullable
    public final String name;
    
    Configuration(@NonNull Context param1Context, @Nullable String param1String, @NonNull SupportSQLiteOpenHelper.Callback param1Callback) {
      this.context = param1Context;
      this.name = param1String;
      this.callback = param1Callback;
    }
    
    public static Builder builder(Context param1Context) { return new Builder(param1Context); }
    
    public static class Builder {
      SupportSQLiteOpenHelper.Callback mCallback;
      
      Context mContext;
      
      String mName;
      
      Builder(@NonNull Context param2Context) { this.mContext = param2Context; }
      
      public SupportSQLiteOpenHelper.Configuration build() {
        if (this.mCallback == null)
          throw new IllegalArgumentException("Must set a callback to create the configuration."); 
        if (this.mContext == null)
          throw new IllegalArgumentException("Must set a non-null context to create the configuration."); 
        return new SupportSQLiteOpenHelper.Configuration(this.mContext, this.mName, this.mCallback);
      }
      
      public Builder callback(@NonNull SupportSQLiteOpenHelper.Callback param2Callback) {
        this.mCallback = param2Callback;
        return this;
      }
      
      public Builder name(@Nullable String param2String) {
        this.mName = param2String;
        return this;
      }
    }
  }
  
  public static class Builder {
    SupportSQLiteOpenHelper.Callback mCallback;
    
    Context mContext;
    
    String mName;
    
    Builder(@NonNull Context param1Context) { this.mContext = param1Context; }
    
    public SupportSQLiteOpenHelper.Configuration build() {
      if (this.mCallback == null)
        throw new IllegalArgumentException("Must set a callback to create the configuration."); 
      if (this.mContext == null)
        throw new IllegalArgumentException("Must set a non-null context to create the configuration."); 
      return new SupportSQLiteOpenHelper.Configuration(this.mContext, this.mName, this.mCallback);
    }
    
    public Builder callback(@NonNull SupportSQLiteOpenHelper.Callback param1Callback) {
      this.mCallback = param1Callback;
      return this;
    }
    
    public Builder name(@Nullable String param1String) {
      this.mName = param1String;
      return this;
    }
  }
  
  public static interface Factory {
    SupportSQLiteOpenHelper create(SupportSQLiteOpenHelper.Configuration param1Configuration);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/db/SupportSQLiteOpenHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */