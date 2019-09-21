package android.arch.persistence.room.util;

import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class StringUtil {
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  public static void appendPlaceholders(StringBuilder paramStringBuilder, int paramInt) {
    for (byte b = 0; b < paramInt; b++) {
      paramStringBuilder.append("?");
      if (b < paramInt - 1)
        paramStringBuilder.append(","); 
    } 
  }
  
  @Nullable
  public static String joinIntoString(@Nullable List<Integer> paramList) {
    if (paramList == null)
      return null; 
    int i = paramList.size();
    if (i == 0)
      return ""; 
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < i; b++) {
      stringBuilder.append(Integer.toString(((Integer)paramList.get(b)).intValue()));
      if (b < i - 1)
        stringBuilder.append(","); 
    } 
    return stringBuilder.toString();
  }
  
  public static StringBuilder newStringBuilder() { return new StringBuilder(); }
  
  @Nullable
  public static List<Integer> splitToIntList(@Nullable String paramString) {
    if (paramString == null)
      return null; 
    ArrayList arrayList = new ArrayList();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
    while (stringTokenizer.hasMoreElements()) {
      str = stringTokenizer.nextToken();
      try {
        arrayList.add(Integer.valueOf(Integer.parseInt(str)));
      } catch (NumberFormatException str) {
        Log.e("ROOM", "Malformed integer list", str);
      } 
    } 
    return arrayList;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/util/StringUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */