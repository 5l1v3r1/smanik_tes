package android.arch.persistence.db;

import java.util.regex.Pattern;

public final class SupportSQLiteQueryBuilder {
  private static final Pattern sLimitPattern = Pattern.compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");
  
  private Object[] mBindArgs;
  
  private String[] mColumns = null;
  
  private boolean mDistinct = false;
  
  private String mGroupBy = null;
  
  private String mHaving = null;
  
  private String mLimit = null;
  
  private String mOrderBy = null;
  
  private String mSelection;
  
  private final String mTable;
  
  private SupportSQLiteQueryBuilder(String paramString) { this.mTable = paramString; }
  
  private static void appendClause(StringBuilder paramStringBuilder, String paramString1, String paramString2) {
    if (!isEmpty(paramString2)) {
      paramStringBuilder.append(paramString1);
      paramStringBuilder.append(paramString2);
    } 
  }
  
  private static void appendColumns(StringBuilder paramStringBuilder, String[] paramArrayOfString) {
    int i = paramArrayOfString.length;
    for (byte b = 0; b < i; b++) {
      String str = paramArrayOfString[b];
      if (b)
        paramStringBuilder.append(", "); 
      paramStringBuilder.append(str);
    } 
    paramStringBuilder.append(' ');
  }
  
  public static SupportSQLiteQueryBuilder builder(String paramString) { return new SupportSQLiteQueryBuilder(paramString); }
  
  private static boolean isEmpty(String paramString) { return (paramString == null || paramString.length() == 0); }
  
  public SupportSQLiteQueryBuilder columns(String[] paramArrayOfString) {
    this.mColumns = paramArrayOfString;
    return this;
  }
  
  public SupportSQLiteQuery create() {
    if (isEmpty(this.mGroupBy) && !isEmpty(this.mHaving))
      throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause"); 
    StringBuilder stringBuilder = new StringBuilder(120);
    stringBuilder.append("SELECT ");
    if (this.mDistinct)
      stringBuilder.append("DISTINCT "); 
    if (this.mColumns != null && this.mColumns.length != 0) {
      appendColumns(stringBuilder, this.mColumns);
    } else {
      stringBuilder.append(" * ");
    } 
    stringBuilder.append(" FROM ");
    stringBuilder.append(this.mTable);
    appendClause(stringBuilder, " WHERE ", this.mSelection);
    appendClause(stringBuilder, " GROUP BY ", this.mGroupBy);
    appendClause(stringBuilder, " HAVING ", this.mHaving);
    appendClause(stringBuilder, " ORDER BY ", this.mOrderBy);
    appendClause(stringBuilder, " LIMIT ", this.mLimit);
    return new SimpleSQLiteQuery(stringBuilder.toString(), this.mBindArgs);
  }
  
  public SupportSQLiteQueryBuilder distinct() {
    this.mDistinct = true;
    return this;
  }
  
  public SupportSQLiteQueryBuilder groupBy(String paramString) {
    this.mGroupBy = paramString;
    return this;
  }
  
  public SupportSQLiteQueryBuilder having(String paramString) {
    this.mHaving = paramString;
    return this;
  }
  
  public SupportSQLiteQueryBuilder limit(String paramString) {
    if (!isEmpty(paramString) && !sLimitPattern.matcher(paramString).matches()) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("invalid LIMIT clauses:");
      stringBuilder.append(paramString);
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    this.mLimit = paramString;
    return this;
  }
  
  public SupportSQLiteQueryBuilder orderBy(String paramString) {
    this.mOrderBy = paramString;
    return this;
  }
  
  public SupportSQLiteQueryBuilder selection(String paramString, Object[] paramArrayOfObject) {
    this.mSelection = paramString;
    this.mBindArgs = paramArrayOfObject;
    return this;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/db/SupportSQLiteQueryBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */