package android.arch.persistence.room.util;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class TableInfo {
  public final Map<String, Column> columns;
  
  public final Set<ForeignKey> foreignKeys;
  
  @Nullable
  public final Set<Index> indices;
  
  public final String name;
  
  public TableInfo(String paramString, Map<String, Column> paramMap, Set<ForeignKey> paramSet) { this(paramString, paramMap, paramSet, Collections.emptySet()); }
  
  public TableInfo(String paramString, Map<String, Column> paramMap, Set<ForeignKey> paramSet1, Set<Index> paramSet2) {
    this.name = paramString;
    this.columns = Collections.unmodifiableMap(paramMap);
    this.foreignKeys = Collections.unmodifiableSet(paramSet1);
    if (paramSet2 == null) {
      paramString = null;
    } else {
      set = Collections.unmodifiableSet(paramSet2);
    } 
    this.indices = set;
  }
  
  public static TableInfo read(SupportSQLiteDatabase paramSupportSQLiteDatabase, String paramString) { return new TableInfo(paramString, readColumns(paramSupportSQLiteDatabase, paramString), readForeignKeys(paramSupportSQLiteDatabase, paramString), readIndices(paramSupportSQLiteDatabase, paramString)); }
  
  private static Map<String, Column> readColumns(SupportSQLiteDatabase paramSupportSQLiteDatabase, String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("PRAGMA table_info(`");
    stringBuilder.append(paramString);
    stringBuilder.append("`)");
    cursor = paramSupportSQLiteDatabase.query(stringBuilder.toString());
    null = new HashMap();
    try {
      if (cursor.getColumnCount() > 0) {
        int i = cursor.getColumnIndex("name");
        int j = cursor.getColumnIndex("type");
        int k = cursor.getColumnIndex("notnull");
        int m = cursor.getColumnIndex("pk");
        while (true) {
          if (cursor.moveToNext()) {
            boolean bool;
            String str1 = cursor.getString(i);
            String str2 = cursor.getString(j);
            if (cursor.getInt(k) != 0) {
              bool = true;
            } else {
              bool = false;
            } 
            null.put(str1, new Column(str1, str2, bool, cursor.getInt(m)));
            continue;
          } 
          return null;
        } 
      } 
      return null;
    } finally {
      cursor.close();
    } 
  }
  
  private static List<ForeignKeyWithSequence> readForeignKeyFieldMappings(Cursor paramCursor) {
    int i = paramCursor.getColumnIndex("id");
    int j = paramCursor.getColumnIndex("seq");
    int k = paramCursor.getColumnIndex("from");
    int m = paramCursor.getColumnIndex("to");
    int n = paramCursor.getCount();
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < n; b++) {
      paramCursor.moveToPosition(b);
      arrayList.add(new ForeignKeyWithSequence(paramCursor.getInt(i), paramCursor.getInt(j), paramCursor.getString(k), paramCursor.getString(m)));
    } 
    Collections.sort(arrayList);
    return arrayList;
  }
  
  private static Set<ForeignKey> readForeignKeys(SupportSQLiteDatabase paramSupportSQLiteDatabase, String paramString) {
    hashSet = new HashSet();
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("PRAGMA foreign_key_list(`");
    stringBuilder.append(paramString);
    stringBuilder.append("`)");
    cursor = paramSupportSQLiteDatabase.query(stringBuilder.toString());
    try {
      int i = cursor.getColumnIndex("id");
      int j = cursor.getColumnIndex("seq");
      int k = cursor.getColumnIndex("table");
      int m = cursor.getColumnIndex("on_delete");
      int n = cursor.getColumnIndex("on_update");
      List list = readForeignKeyFieldMappings(cursor);
      int i1 = cursor.getCount();
      for (byte b = 0; b < i1; b++) {
        cursor.moveToPosition(b);
        if (cursor.getInt(j) == 0) {
          int i2 = cursor.getInt(i);
          ArrayList arrayList1 = new ArrayList();
          ArrayList arrayList2 = new ArrayList();
          for (ForeignKeyWithSequence foreignKeyWithSequence : list) {
            if (foreignKeyWithSequence.mId == i2) {
              arrayList1.add(foreignKeyWithSequence.mFrom);
              arrayList2.add(foreignKeyWithSequence.mTo);
            } 
          } 
          hashSet.add(new ForeignKey(cursor.getString(k), cursor.getString(m), cursor.getString(n), arrayList1, arrayList2));
        } 
      } 
      return hashSet;
    } finally {
      cursor.close();
    } 
  }
  
  @Nullable
  private static Index readIndex(SupportSQLiteDatabase paramSupportSQLiteDatabase, String paramString, boolean paramBoolean) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("PRAGMA index_xinfo(`");
    stringBuilder.append(paramString);
    stringBuilder.append("`)");
    cursor = paramSupportSQLiteDatabase.query(stringBuilder.toString());
    try {
      int i = cursor.getColumnIndex("seqno");
      int j = cursor.getColumnIndex("cid");
      int k = cursor.getColumnIndex("name");
      if (i == -1 || j == -1 || k == -1)
        return null; 
      TreeMap treeMap = new TreeMap();
      while (cursor.moveToNext()) {
        if (cursor.getInt(j) < 0)
          continue; 
        treeMap.put(Integer.valueOf(cursor.getInt(i)), cursor.getString(k));
      } 
      ArrayList arrayList = new ArrayList(treeMap.size());
      arrayList.addAll(treeMap.values());
      return new Index(paramString, paramBoolean, arrayList);
    } finally {
      cursor.close();
    } 
  }
  
  @Nullable
  private static Set<Index> readIndices(SupportSQLiteDatabase paramSupportSQLiteDatabase, String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("PRAGMA index_list(`");
    stringBuilder.append(paramString);
    stringBuilder.append("`)");
    cursor = paramSupportSQLiteDatabase.query(stringBuilder.toString());
    try {
      int i = cursor.getColumnIndex("name");
      int j = cursor.getColumnIndex("origin");
      int k = cursor.getColumnIndex("unique");
      if (i == -1 || j == -1 || k == -1)
        return null; 
      hashSet = new HashSet();
      while (true) {
        if (cursor.moveToNext()) {
          if (!"c".equals(cursor.getString(j)))
            continue; 
          String str = cursor.getString(i);
          int m = cursor.getInt(k);
          boolean bool = true;
          if (m != 1)
            bool = false; 
          Index index = readIndex(paramSupportSQLiteDatabase, str, bool);
          if (index == null)
            return null; 
          hashSet.add(index);
          continue;
        } 
        return hashSet;
      } 
    } finally {
      cursor.close();
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject != null) {
      if (getClass() != paramObject.getClass())
        return false; 
      paramObject = (TableInfo)paramObject;
      if (this.name != null) {
        if (!this.name.equals(paramObject.name))
          return false; 
      } else if (paramObject.name != null) {
        return false;
      } 
      if (this.columns != null) {
        if (!this.columns.equals(paramObject.columns))
          return false; 
      } else if (paramObject.columns != null) {
        return false;
      } 
      if (this.foreignKeys != null) {
        if (!this.foreignKeys.equals(paramObject.foreignKeys))
          return false; 
      } else if (paramObject.foreignKeys != null) {
        return false;
      } 
      return (this.indices != null) ? ((paramObject.indices == null) ? true : this.indices.equals(paramObject.indices)) : true;
    } 
    return false;
  }
  
  public int hashCode() {
    byte b;
    boolean bool;
    String str = this.name;
    int i = 0;
    if (str != null) {
      bool = this.name.hashCode();
    } else {
      bool = false;
    } 
    if (this.columns != null) {
      b = this.columns.hashCode();
    } else {
      b = 0;
    } 
    if (this.foreignKeys != null)
      i = this.foreignKeys.hashCode(); 
    return (bool * 31 + b) * 31 + i;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("TableInfo{name='");
    stringBuilder.append(this.name);
    stringBuilder.append('\'');
    stringBuilder.append(", columns=");
    stringBuilder.append(this.columns);
    stringBuilder.append(", foreignKeys=");
    stringBuilder.append(this.foreignKeys);
    stringBuilder.append(", indices=");
    stringBuilder.append(this.indices);
    stringBuilder.append('}');
    return stringBuilder.toString();
  }
  
  public static class Column {
    public final String name;
    
    public final boolean notNull;
    
    public final int primaryKeyPosition;
    
    public final String type;
    
    public Column(String param1String1, String param1String2, boolean param1Boolean, int param1Int) {
      this.name = param1String1;
      this.type = param1String2;
      this.notNull = param1Boolean;
      this.primaryKeyPosition = param1Int;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object != null) {
        if (getClass() != param1Object.getClass())
          return false; 
        param1Object = (Column)param1Object;
        if (Build.VERSION.SDK_INT >= 20) {
          if (this.primaryKeyPosition != param1Object.primaryKeyPosition)
            return false; 
        } else if (isPrimaryKey() != param1Object.isPrimaryKey()) {
          return false;
        } 
        return !this.name.equals(param1Object.name) ? false : ((this.notNull != param1Object.notNull) ? false : ((this.type != null) ? this.type.equalsIgnoreCase(param1Object.type) : ((param1Object.type == null) ? 1 : 0)));
      } 
      return false;
    }
    
    public int hashCode() {
      int j;
      int i;
      int k = this.name.hashCode();
      if (this.type != null) {
        i = this.type.hashCode();
      } else {
        i = 0;
      } 
      if (this.notNull) {
        j = 1231;
      } else {
        j = 1237;
      } 
      return ((k * 31 + i) * 31 + j) * 31 + this.primaryKeyPosition;
    }
    
    public boolean isPrimaryKey() { return (this.primaryKeyPosition > 0); }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Column{name='");
      stringBuilder.append(this.name);
      stringBuilder.append('\'');
      stringBuilder.append(", type='");
      stringBuilder.append(this.type);
      stringBuilder.append('\'');
      stringBuilder.append(", notNull=");
      stringBuilder.append(this.notNull);
      stringBuilder.append(", primaryKeyPosition=");
      stringBuilder.append(this.primaryKeyPosition);
      stringBuilder.append('}');
      return stringBuilder.toString();
    }
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static class ForeignKey {
    @NonNull
    public final List<String> columnNames;
    
    @NonNull
    public final String onDelete;
    
    @NonNull
    public final String onUpdate;
    
    @NonNull
    public final List<String> referenceColumnNames;
    
    @NonNull
    public final String referenceTable;
    
    public ForeignKey(@NonNull String param1String1, @NonNull String param1String2, @NonNull String param1String3, @NonNull List<String> param1List1, @NonNull List<String> param1List2) {
      this.referenceTable = param1String1;
      this.onDelete = param1String2;
      this.onUpdate = param1String3;
      this.columnNames = Collections.unmodifiableList(param1List1);
      this.referenceColumnNames = Collections.unmodifiableList(param1List2);
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object != null) {
        if (getClass() != param1Object.getClass())
          return false; 
        param1Object = (ForeignKey)param1Object;
        return !this.referenceTable.equals(param1Object.referenceTable) ? false : (!this.onDelete.equals(param1Object.onDelete) ? false : (!this.onUpdate.equals(param1Object.onUpdate) ? false : (!this.columnNames.equals(param1Object.columnNames) ? false : this.referenceColumnNames.equals(param1Object.referenceColumnNames))));
      } 
      return false;
    }
    
    public int hashCode() { return (((this.referenceTable.hashCode() * 31 + this.onDelete.hashCode()) * 31 + this.onUpdate.hashCode()) * 31 + this.columnNames.hashCode()) * 31 + this.referenceColumnNames.hashCode(); }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("ForeignKey{referenceTable='");
      stringBuilder.append(this.referenceTable);
      stringBuilder.append('\'');
      stringBuilder.append(", onDelete='");
      stringBuilder.append(this.onDelete);
      stringBuilder.append('\'');
      stringBuilder.append(", onUpdate='");
      stringBuilder.append(this.onUpdate);
      stringBuilder.append('\'');
      stringBuilder.append(", columnNames=");
      stringBuilder.append(this.columnNames);
      stringBuilder.append(", referenceColumnNames=");
      stringBuilder.append(this.referenceColumnNames);
      stringBuilder.append('}');
      return stringBuilder.toString();
    }
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static class ForeignKeyWithSequence extends Object implements Comparable<ForeignKeyWithSequence> {
    final String mFrom;
    
    final int mId;
    
    final int mSequence;
    
    final String mTo;
    
    ForeignKeyWithSequence(int param1Int1, int param1Int2, String param1String1, String param1String2) {
      this.mId = param1Int1;
      this.mSequence = param1Int2;
      this.mFrom = param1String1;
      this.mTo = param1String2;
    }
    
    public int compareTo(ForeignKeyWithSequence param1ForeignKeyWithSequence) {
      int i = this.mId - param1ForeignKeyWithSequence.mId;
      return (i == 0) ? (this.mSequence - param1ForeignKeyWithSequence.mSequence) : i;
    }
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static class Index {
    public static final String DEFAULT_PREFIX = "index_";
    
    public final List<String> columns;
    
    public final String name;
    
    public final boolean unique;
    
    public Index(String param1String, boolean param1Boolean, List<String> param1List) {
      this.name = param1String;
      this.unique = param1Boolean;
      this.columns = param1List;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object != null) {
        if (getClass() != param1Object.getClass())
          return false; 
        param1Object = (Index)param1Object;
        return (this.unique != param1Object.unique) ? false : (!this.columns.equals(param1Object.columns) ? false : (this.name.startsWith("index_") ? param1Object.name.startsWith("index_") : this.name.equals(param1Object.name)));
      } 
      return false;
    }
    
    public int hashCode() { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e2expr(TypeTransformer.java:632)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:716)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e2expr(TypeTransformer.java:629)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:716)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e2expr(TypeTransformer.java:629)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:716)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Index{name='");
      stringBuilder.append(this.name);
      stringBuilder.append('\'');
      stringBuilder.append(", unique=");
      stringBuilder.append(this.unique);
      stringBuilder.append(", columns=");
      stringBuilder.append(this.columns);
      stringBuilder.append('}');
      return stringBuilder.toString();
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/util/TableInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */