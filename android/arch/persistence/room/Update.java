package android.arch.persistence.room;

public @interface Update {
  int onConflict() default 3;
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/persistence/room/Update.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */