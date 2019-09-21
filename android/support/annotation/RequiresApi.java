package android.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface RequiresApi {
  @IntRange(from = 1L)
  int api() default 1;
  
  @IntRange(from = 1L)
  int value() default 1;
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/annotation/RequiresApi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */