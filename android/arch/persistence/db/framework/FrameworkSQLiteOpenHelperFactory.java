package android.arch.persistence.db.framework;

import android.arch.persistence.db.SupportSQLiteOpenHelper;

public final class FrameworkSQLiteOpenHelperFactory implements SupportSQLiteOpenHelper.Factory {
  public SupportSQLiteOpenHelper create(SupportSQLiteOpenHelper.Configuration paramConfiguration) { return new FrameworkSQLiteOpenHelper(paramConfiguration.context, paramConfiguration.name, paramConfiguration.callback); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/db/framework/FrameworkSQLiteOpenHelperFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */