package android.arch.persistence.room;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class RoomOpenHelper extends SupportSQLiteOpenHelper.Callback {
  @Nullable
  private DatabaseConfiguration mConfiguration;
  
  @NonNull
  private final Delegate mDelegate;
  
  @NonNull
  private final String mIdentityHash;
  
  public RoomOpenHelper(@NonNull DatabaseConfiguration paramDatabaseConfiguration, @NonNull Delegate paramDelegate, @NonNull String paramString) {
    super(paramDelegate.version);
    this.mConfiguration = paramDatabaseConfiguration;
    this.mDelegate = paramDelegate;
    this.mIdentityHash = paramString;
  }
  
  private void checkIdentity(SupportSQLiteDatabase paramSupportSQLiteDatabase) {
    createMasterTableIfNotExists(paramSupportSQLiteDatabase);
    String str = "";
    Cursor cursor = paramSupportSQLiteDatabase.query(new SimpleSQLiteQuery("SELECT identity_hash FROM room_master_table WHERE id = 42 LIMIT 1"));
    null = str;
    try {
      if (cursor.moveToFirst())
        null = cursor.getString(0); 
      cursor.close();
      return;
    } finally {
      cursor.close();
    } 
  }
  
  private void createMasterTableIfNotExists(SupportSQLiteDatabase paramSupportSQLiteDatabase) { paramSupportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)"); }
  
  private void updateIdentity(SupportSQLiteDatabase paramSupportSQLiteDatabase) {
    createMasterTableIfNotExists(paramSupportSQLiteDatabase);
    paramSupportSQLiteDatabase.execSQL(RoomMasterTable.createInsertQuery(this.mIdentityHash));
  }
  
  public void onConfigure(SupportSQLiteDatabase paramSupportSQLiteDatabase) { super.onConfigure(paramSupportSQLiteDatabase); }
  
  public void onCreate(SupportSQLiteDatabase paramSupportSQLiteDatabase) {
    updateIdentity(paramSupportSQLiteDatabase);
    this.mDelegate.createAllTables(paramSupportSQLiteDatabase);
    this.mDelegate.onCreate(paramSupportSQLiteDatabase);
  }
  
  public void onDowngrade(SupportSQLiteDatabase paramSupportSQLiteDatabase, int paramInt1, int paramInt2) { onUpgrade(paramSupportSQLiteDatabase, paramInt1, paramInt2); }
  
  public void onOpen(SupportSQLiteDatabase paramSupportSQLiteDatabase) {
    super.onOpen(paramSupportSQLiteDatabase);
    checkIdentity(paramSupportSQLiteDatabase);
    this.mDelegate.onOpen(paramSupportSQLiteDatabase);
    this.mConfiguration = null;
  }
  
  public void onUpgrade(SupportSQLiteDatabase paramSupportSQLiteDatabase, int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: getfield mConfiguration : Landroid/arch/persistence/room/DatabaseConfiguration;
    //   4: ifnull -> 81
    //   7: aload_0
    //   8: getfield mConfiguration : Landroid/arch/persistence/room/DatabaseConfiguration;
    //   11: getfield migrationContainer : Landroid/arch/persistence/room/RoomDatabase$MigrationContainer;
    //   14: iload_2
    //   15: iload_3
    //   16: invokevirtual findMigrationPath : (II)Ljava/util/List;
    //   19: astore #5
    //   21: aload #5
    //   23: ifnull -> 81
    //   26: aload #5
    //   28: invokeinterface iterator : ()Ljava/util/Iterator;
    //   33: astore #5
    //   35: aload #5
    //   37: invokeinterface hasNext : ()Z
    //   42: ifeq -> 62
    //   45: aload #5
    //   47: invokeinterface next : ()Ljava/lang/Object;
    //   52: checkcast android/arch/persistence/room/migration/Migration
    //   55: aload_1
    //   56: invokevirtual migrate : (Landroid/arch/persistence/db/SupportSQLiteDatabase;)V
    //   59: goto -> 35
    //   62: aload_0
    //   63: getfield mDelegate : Landroid/arch/persistence/room/RoomOpenHelper$Delegate;
    //   66: aload_1
    //   67: invokevirtual validateMigration : (Landroid/arch/persistence/db/SupportSQLiteDatabase;)V
    //   70: aload_0
    //   71: aload_1
    //   72: invokespecial updateIdentity : (Landroid/arch/persistence/db/SupportSQLiteDatabase;)V
    //   75: iconst_1
    //   76: istore #4
    //   78: goto -> 84
    //   81: iconst_0
    //   82: istore #4
    //   84: iload #4
    //   86: ifne -> 193
    //   89: aload_0
    //   90: getfield mConfiguration : Landroid/arch/persistence/room/DatabaseConfiguration;
    //   93: ifnull -> 126
    //   96: aload_0
    //   97: getfield mConfiguration : Landroid/arch/persistence/room/DatabaseConfiguration;
    //   100: getfield requireMigration : Z
    //   103: ifeq -> 109
    //   106: goto -> 126
    //   109: aload_0
    //   110: getfield mDelegate : Landroid/arch/persistence/room/RoomOpenHelper$Delegate;
    //   113: aload_1
    //   114: invokevirtual dropAllTables : (Landroid/arch/persistence/db/SupportSQLiteDatabase;)V
    //   117: aload_0
    //   118: getfield mDelegate : Landroid/arch/persistence/room/RoomOpenHelper$Delegate;
    //   121: aload_1
    //   122: invokevirtual createAllTables : (Landroid/arch/persistence/db/SupportSQLiteDatabase;)V
    //   125: return
    //   126: new java/lang/StringBuilder
    //   129: dup
    //   130: invokespecial <init> : ()V
    //   133: astore_1
    //   134: aload_1
    //   135: ldc 'A migration from '
    //   137: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   140: pop
    //   141: aload_1
    //   142: iload_2
    //   143: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   146: pop
    //   147: aload_1
    //   148: ldc ' to '
    //   150: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   153: pop
    //   154: aload_1
    //   155: iload_3
    //   156: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   159: pop
    //   160: aload_1
    //   161: ldc ' is necessary. Please provide a Migration in the builder or call'
    //   163: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   166: pop
    //   167: aload_1
    //   168: ldc ' fallbackToDestructiveMigration in the builder in which case Room will'
    //   170: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   173: pop
    //   174: aload_1
    //   175: ldc ' re-create all of the tables.'
    //   177: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: pop
    //   181: new java/lang/IllegalStateException
    //   184: dup
    //   185: aload_1
    //   186: invokevirtual toString : ()Ljava/lang/String;
    //   189: invokespecial <init> : (Ljava/lang/String;)V
    //   192: athrow
    //   193: return }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static abstract class Delegate {
    public final int version;
    
    public Delegate(int param1Int) { this.version = param1Int; }
    
    protected abstract void createAllTables(SupportSQLiteDatabase param1SupportSQLiteDatabase);
    
    protected abstract void dropAllTables(SupportSQLiteDatabase param1SupportSQLiteDatabase);
    
    protected abstract void onCreate(SupportSQLiteDatabase param1SupportSQLiteDatabase);
    
    protected abstract void onOpen(SupportSQLiteDatabase param1SupportSQLiteDatabase);
    
    protected abstract void validateMigration(SupportSQLiteDatabase param1SupportSQLiteDatabase);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/RoomOpenHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */