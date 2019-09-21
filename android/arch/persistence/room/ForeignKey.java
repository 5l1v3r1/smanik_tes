package android.arch.persistence.room;

public @interface ForeignKey {
  public static final int CASCADE = 5;
  
  public static final int NO_ACTION = 1;
  
  public static final int RESTRICT = 2;
  
  public static final int SET_DEFAULT = 4;
  
  public static final int SET_NULL = 3;
  
  String[] childColumns();
  
  boolean deferred() default false;
  
  Class entity();
  
  @Action
  int onDelete() default 1;
  
  @Action
  int onUpdate() default 1;
  
  String[] parentColumns();
  
  public static @interface Action {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/ForeignKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */