package android.arch.persistence.room;

import android.content.Context;
import android.support.annotation.NonNull;

public class Room {
  private static final String CURSOR_CONV_SUFFIX = "_CursorConverter";
  
  static final String LOG_TAG = "ROOM";
  
  public static final String MASTER_TABLE_NAME = "room_master_table";
  
  @NonNull
  public static <T extends RoomDatabase> RoomDatabase.Builder<T> databaseBuilder(@NonNull Context paramContext, @NonNull Class<T> paramClass, @NonNull String paramString) {
    if (paramString == null || paramString.trim().length() == 0)
      throw new IllegalArgumentException("Cannot build a database with null or empty name. If you are trying to create an in memory database, use Room.inMemoryDatabaseBuilder"); 
    return new RoomDatabase.Builder(paramContext, paramClass, paramString);
  }
  
  @NonNull
  static <T, C> T getGeneratedImplementation(Class<C> paramClass, String paramString) {
    String str2 = paramClass.getPackage().getName();
    String str1 = paramClass.getCanonicalName();
    if (!str2.isEmpty())
      str1 = str1.substring(str2.length() + 1); 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(str1.replace('.', '_'));
    stringBuilder.append(paramString);
    str1 = stringBuilder.toString();
    try {
      String str;
      if (str2.isEmpty()) {
        paramString = str1;
      } else {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(str2);
        stringBuilder1.append(".");
        stringBuilder1.append(str1);
        str = stringBuilder1.toString();
      } 
      return (T)Class.forName(str).newInstance();
    } catch (ClassNotFoundException paramString) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("cannot find implementation for ");
      stringBuilder1.append(paramClass.getCanonicalName());
      stringBuilder1.append(". ");
      stringBuilder1.append(str1);
      stringBuilder1.append(" does not exist");
      throw new RuntimeException(stringBuilder1.toString());
    } catch (IllegalAccessException paramString) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("Cannot access the constructor");
      stringBuilder1.append(paramClass.getCanonicalName());
      throw new RuntimeException(stringBuilder1.toString());
    } catch (InstantiationException paramString) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("Failed to create an instance of ");
      stringBuilder1.append(paramClass.getCanonicalName());
      throw new RuntimeException(stringBuilder1.toString());
    } 
  }
  
  @NonNull
  public static <T extends RoomDatabase> RoomDatabase.Builder<T> inMemoryDatabaseBuilder(@NonNull Context paramContext, @NonNull Class<T> paramClass) { return new RoomDatabase.Builder(paramContext, paramClass, null); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/Room.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */