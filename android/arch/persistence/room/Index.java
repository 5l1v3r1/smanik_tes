package android.arch.persistence.room;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({})
public @interface Index {
  String name() default "";
  
  boolean unique() default false;
  
  String[] value();
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/Index.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */