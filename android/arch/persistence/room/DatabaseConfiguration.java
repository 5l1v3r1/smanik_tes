package android.arch.persistence.room;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import java.util.List;

public class DatabaseConfiguration {
  public final boolean allowMainThreadQueries;
  
  @Nullable
  public final List<RoomDatabase.Callback> callbacks;
  
  @NonNull
  public final Context context;
  
  @NonNull
  public final RoomDatabase.MigrationContainer migrationContainer;
  
  @Nullable
  public final String name;
  
  public final boolean requireMigration;
  
  @NonNull
  public final SupportSQLiteOpenHelper.Factory sqliteOpenHelperFactory;
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public DatabaseConfiguration(@NonNull Context paramContext, @Nullable String paramString, @NonNull SupportSQLiteOpenHelper.Factory paramFactory, @NonNull RoomDatabase.MigrationContainer paramMigrationContainer, @Nullable List<RoomDatabase.Callback> paramList, boolean paramBoolean1, boolean paramBoolean2) {
    this.sqliteOpenHelperFactory = paramFactory;
    this.context = paramContext;
    this.name = paramString;
    this.migrationContainer = paramMigrationContainer;
    this.callbacks = paramList;
    this.allowMainThreadQueries = paramBoolean1;
    this.requireMigration = paramBoolean2;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/DatabaseConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */