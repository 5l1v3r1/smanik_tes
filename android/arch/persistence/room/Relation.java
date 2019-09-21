package android.arch.persistence.room;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD})
public @interface Relation {
  Class entity() default Object.class;
  
  String entityColumn();
  
  String parentColumn();
  
  String[] projection() default {};
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/Relation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */