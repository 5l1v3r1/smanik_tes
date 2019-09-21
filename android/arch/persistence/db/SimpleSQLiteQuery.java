package android.arch.persistence.db;

public final class SimpleSQLiteQuery implements SupportSQLiteQuery {
  private final Object[] mBindArgs;
  
  private final String mQuery;
  
  public SimpleSQLiteQuery(String paramString) { this(paramString, null); }
  
  public SimpleSQLiteQuery(String paramString, Object[] paramArrayOfObject) {
    this.mQuery = paramString;
    this.mBindArgs = paramArrayOfObject;
  }
  
  private static void bind(SupportSQLiteProgram paramSupportSQLiteProgram, int paramInt, Object paramObject) {
    if (paramObject == null) {
      paramSupportSQLiteProgram.bindNull(paramInt);
      return;
    } 
    if (paramObject instanceof byte[]) {
      paramSupportSQLiteProgram.bindBlob(paramInt, (byte[])paramObject);
      return;
    } 
    if (paramObject instanceof Float) {
      paramSupportSQLiteProgram.bindDouble(paramInt, ((Float)paramObject).floatValue());
      return;
    } 
    if (paramObject instanceof Double) {
      paramSupportSQLiteProgram.bindDouble(paramInt, ((Double)paramObject).doubleValue());
      return;
    } 
    if (paramObject instanceof Long) {
      paramSupportSQLiteProgram.bindLong(paramInt, ((Long)paramObject).longValue());
      return;
    } 
    if (paramObject instanceof Integer) {
      paramSupportSQLiteProgram.bindLong(paramInt, ((Integer)paramObject).intValue());
      return;
    } 
    if (paramObject instanceof Short) {
      paramSupportSQLiteProgram.bindLong(paramInt, ((Short)paramObject).shortValue());
      return;
    } 
    if (paramObject instanceof Byte) {
      paramSupportSQLiteProgram.bindLong(paramInt, ((Byte)paramObject).byteValue());
      return;
    } 
    if (paramObject instanceof String) {
      paramSupportSQLiteProgram.bindString(paramInt, (String)paramObject);
      return;
    } 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Cannot bind ");
    stringBuilder.append(paramObject);
    stringBuilder.append(" at index ");
    stringBuilder.append(paramInt);
    stringBuilder.append(" Supported types: null, byte[], float, double, long, int, short, byte,");
    stringBuilder.append(" string");
    throw new IllegalArgumentException(stringBuilder.toString());
  }
  
  public static void bind(SupportSQLiteProgram paramSupportSQLiteProgram, Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null)
      return; 
    int i = paramArrayOfObject.length;
    byte b = 0;
    while (b < i) {
      Object object = paramArrayOfObject[b];
      bind(paramSupportSQLiteProgram, ++b, object);
    } 
  }
  
  public void bindTo(SupportSQLiteProgram paramSupportSQLiteProgram) { bind(paramSupportSQLiteProgram, this.mBindArgs); }
  
  public String getSql() { return this.mQuery; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/db/SimpleSQLiteQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */