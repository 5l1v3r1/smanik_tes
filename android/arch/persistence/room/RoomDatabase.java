package android.arch.persistence.room;

import android.arch.core.executor.ArchTaskExecutor;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class RoomDatabase {
  private static final String DB_IMPL_SUFFIX = "_Impl";
  
  private boolean mAllowMainThreadQueries;
  
  @Nullable
  protected List<Callback> mCallbacks;
  
  private final ReentrantLock mCloseLock = new ReentrantLock();
  
  private final InvalidationTracker mInvalidationTracker = createInvalidationTracker();
  
  private SupportSQLiteOpenHelper mOpenHelper;
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void assertNotMainThread() {
    if (this.mAllowMainThreadQueries)
      return; 
    if (ArchTaskExecutor.getInstance().isMainThread())
      throw new IllegalStateException("Cannot access database on the main thread since it may potentially lock the UI for a long period of time."); 
  }
  
  public void beginTransaction() {
    assertNotMainThread();
    this.mInvalidationTracker.syncTriggers();
    this.mOpenHelper.getWritableDatabase().beginTransaction();
  }
  
  public void close() {
    if (isOpen())
      try {
        this.mCloseLock.lock();
        this.mOpenHelper.close();
        return;
      } finally {
        this.mCloseLock.unlock();
      }  
  }
  
  public SupportSQLiteStatement compileStatement(String paramString) {
    assertNotMainThread();
    return this.mOpenHelper.getWritableDatabase().compileStatement(paramString);
  }
  
  protected abstract InvalidationTracker createInvalidationTracker();
  
  protected abstract SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration paramDatabaseConfiguration);
  
  public void endTransaction() {
    this.mOpenHelper.getWritableDatabase().endTransaction();
    if (!inTransaction())
      this.mInvalidationTracker.refreshVersionsAsync(); 
  }
  
  Lock getCloseLock() { return this.mCloseLock; }
  
  public InvalidationTracker getInvalidationTracker() { return this.mInvalidationTracker; }
  
  public SupportSQLiteOpenHelper getOpenHelper() { return this.mOpenHelper; }
  
  public boolean inTransaction() { return this.mOpenHelper.getWritableDatabase().inTransaction(); }
  
  @CallSuper
  public void init(DatabaseConfiguration paramDatabaseConfiguration) {
    this.mOpenHelper = createOpenHelper(paramDatabaseConfiguration);
    this.mCallbacks = paramDatabaseConfiguration.callbacks;
    this.mAllowMainThreadQueries = paramDatabaseConfiguration.allowMainThreadQueries;
  }
  
  protected void internalInitInvalidationTracker(SupportSQLiteDatabase paramSupportSQLiteDatabase) { this.mInvalidationTracker.internalInit(paramSupportSQLiteDatabase); }
  
  public boolean isOpen() {
    SupportSQLiteDatabase supportSQLiteDatabase = this.mDatabase;
    return (supportSQLiteDatabase != null && supportSQLiteDatabase.isOpen());
  }
  
  public Cursor query(SupportSQLiteQuery paramSupportSQLiteQuery) {
    assertNotMainThread();
    return this.mOpenHelper.getWritableDatabase().query(paramSupportSQLiteQuery);
  }
  
  public Cursor query(String paramString, @Nullable Object[] paramArrayOfObject) { return this.mOpenHelper.getWritableDatabase().query(new SimpleSQLiteQuery(paramString, paramArrayOfObject)); }
  
  public <V> V runInTransaction(Callable<V> paramCallable) {
    beginTransaction();
    try {
      Object object = paramCallable.call();
      setTransactionSuccessful();
      endTransaction();
      return (V)object;
    } catch (RuntimeException paramCallable) {
      throw paramCallable;
    } catch (Exception paramCallable) {
      throw new RuntimeException("Exception in transaction", paramCallable);
    } finally {}
    endTransaction();
    throw paramCallable;
  }
  
  public void runInTransaction(Runnable paramRunnable) {
    beginTransaction();
    try {
      paramRunnable.run();
      setTransactionSuccessful();
      return;
    } finally {
      endTransaction();
    } 
  }
  
  public void setTransactionSuccessful() { this.mOpenHelper.getWritableDatabase().setTransactionSuccessful(); }
  
  public static class Builder<T extends RoomDatabase> extends Object {
    private boolean mAllowMainThreadQueries;
    
    private ArrayList<RoomDatabase.Callback> mCallbacks;
    
    private final Context mContext;
    
    private final Class<T> mDatabaseClass;
    
    private SupportSQLiteOpenHelper.Factory mFactory;
    
    private RoomDatabase.MigrationContainer mMigrationContainer;
    
    private final String mName;
    
    private boolean mRequireMigration;
    
    Builder(@NonNull Context param1Context, @NonNull Class<T> param1Class, @Nullable String param1String) {
      this.mContext = param1Context;
      this.mDatabaseClass = param1Class;
      this.mName = param1String;
      this.mRequireMigration = true;
      this.mMigrationContainer = new RoomDatabase.MigrationContainer();
    }
    
    @NonNull
    public Builder<T> addCallback(@NonNull RoomDatabase.Callback param1Callback) {
      if (this.mCallbacks == null)
        this.mCallbacks = new ArrayList(); 
      this.mCallbacks.add(param1Callback);
      return this;
    }
    
    @NonNull
    public Builder<T> addMigrations(Migration... param1VarArgs) {
      this.mMigrationContainer.addMigrations(param1VarArgs);
      return this;
    }
    
    @NonNull
    public Builder<T> allowMainThreadQueries() {
      this.mAllowMainThreadQueries = true;
      return this;
    }
    
    @NonNull
    public T build() {
      if (this.mContext == null)
        throw new IllegalArgumentException("Cannot provide null context for the database."); 
      if (this.mDatabaseClass == null)
        throw new IllegalArgumentException("Must provide an abstract class that extends RoomDatabase"); 
      if (this.mFactory == null)
        this.mFactory = new FrameworkSQLiteOpenHelperFactory(); 
      DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(this.mContext, this.mName, this.mFactory, this.mMigrationContainer, this.mCallbacks, this.mAllowMainThreadQueries, this.mRequireMigration);
      RoomDatabase roomDatabase = (RoomDatabase)Room.getGeneratedImplementation(this.mDatabaseClass, "_Impl");
      roomDatabase.init(databaseConfiguration);
      return (T)roomDatabase;
    }
    
    @NonNull
    public Builder<T> fallbackToDestructiveMigration() {
      this.mRequireMigration = false;
      return this;
    }
    
    @NonNull
    public Builder<T> openHelperFactory(@Nullable SupportSQLiteOpenHelper.Factory param1Factory) {
      this.mFactory = param1Factory;
      return this;
    }
  }
  
  public static abstract class Callback {
    public void onCreate(@NonNull SupportSQLiteDatabase param1SupportSQLiteDatabase) {}
    
    public void onOpen(@NonNull SupportSQLiteDatabase param1SupportSQLiteDatabase) {}
  }
  
  public static class MigrationContainer {
    private SparseArrayCompat<SparseArrayCompat<Migration>> mMigrations = new SparseArrayCompat();
    
    private void addMigration(Migration param1Migration) {
      int i = param1Migration.startVersion;
      int j = param1Migration.endVersion;
      SparseArrayCompat sparseArrayCompat2 = (SparseArrayCompat)this.mMigrations.get(i);
      SparseArrayCompat sparseArrayCompat1 = sparseArrayCompat2;
      if (sparseArrayCompat2 == null) {
        sparseArrayCompat1 = new SparseArrayCompat();
        this.mMigrations.put(i, sparseArrayCompat1);
      } 
      Migration migration = (Migration)sparseArrayCompat1.get(j);
      if (migration != null) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Overriding migration ");
        stringBuilder.append(migration);
        stringBuilder.append(" with ");
        stringBuilder.append(param1Migration);
        Log.w("ROOM", stringBuilder.toString());
      } 
      sparseArrayCompat1.append(j, param1Migration);
    }
    
    private List<Migration> findUpMigrationPath(List<Migration> param1List, boolean param1Boolean, int param1Int1, int param1Int2) {
      int j;
      int i;
      if (param1Boolean) {
        i = -1;
        j = param1Int1;
      } else {
        i = 1;
        j = param1Int1;
      } 
      while (param1Boolean ? (j < param1Int2) : (j > param1Int2)) {
        int m;
        boolean bool1;
        SparseArrayCompat sparseArrayCompat = (SparseArrayCompat)this.mMigrations.get(j);
        if (sparseArrayCompat == null)
          return null; 
        int k = sparseArrayCompat.size();
        boolean bool2 = false;
        if (param1Boolean) {
          param1Int1 = k - 1;
          k = -1;
        } else {
          param1Int1 = 0;
        } 
        while (true) {
          bool1 = bool2;
          m = j;
          if (param1Int1 != k) {
            m = sparseArrayCompat.keyAt(param1Int1);
            if (m <= param1Int2 && m > j) {
              param1List.add(sparseArrayCompat.valueAt(param1Int1));
              bool1 = true;
              break;
            } 
            param1Int1 += i;
            continue;
          } 
          break;
        } 
        j = m;
        if (!bool1)
          return null; 
      } 
      return param1List;
    }
    
    public void addMigrations(Migration... param1VarArgs) {
      int i = param1VarArgs.length;
      for (byte b = 0; b < i; b++)
        addMigration(param1VarArgs[b]); 
    }
    
    @Nullable
    public List<Migration> findMigrationPath(int param1Int1, int param1Int2) {
      boolean bool;
      if (param1Int1 == param1Int2)
        return Collections.emptyList(); 
      if (param1Int2 > param1Int1) {
        bool = true;
      } else {
        bool = false;
      } 
      return findUpMigrationPath(new ArrayList(), bool, param1Int1, param1Int2);
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/RoomDatabase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */