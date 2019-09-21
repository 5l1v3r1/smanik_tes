package android.support.v4.util;

import android.os.Build;
import android.support.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class ObjectsCompat {
  public static boolean equals(@Nullable Object paramObject1, @Nullable Object paramObject2) { return (Build.VERSION.SDK_INT >= 19) ? Objects.equals(paramObject1, paramObject2) : ((paramObject1 == paramObject2 || (paramObject1 != null && paramObject1.equals(paramObject2))) ? 1 : 0); }
  
  public static int hash(@Nullable Object... paramVarArgs) { return (Build.VERSION.SDK_INT >= 19) ? Objects.hash(paramVarArgs) : Arrays.hashCode(paramVarArgs); }
  
  public static int hashCode(@Nullable Object paramObject) { return (paramObject != null) ? paramObject.hashCode() : 0; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/util/ObjectsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */