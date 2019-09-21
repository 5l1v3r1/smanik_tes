package android.support.v4.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseArray;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@RequiresApi(16)
class NotificationCompatJellybean {
  static final String EXTRA_ALLOW_GENERATED_REPLIES = "android.support.allowGeneratedReplies";
  
  static final String EXTRA_DATA_ONLY_REMOTE_INPUTS = "android.support.dataRemoteInputs";
  
  private static final String KEY_ACTION_INTENT = "actionIntent";
  
  private static final String KEY_ALLOWED_DATA_TYPES = "allowedDataTypes";
  
  private static final String KEY_ALLOW_FREE_FORM_INPUT = "allowFreeFormInput";
  
  private static final String KEY_CHOICES = "choices";
  
  private static final String KEY_DATA_ONLY_REMOTE_INPUTS = "dataOnlyRemoteInputs";
  
  private static final String KEY_EXTRAS = "extras";
  
  private static final String KEY_ICON = "icon";
  
  private static final String KEY_LABEL = "label";
  
  private static final String KEY_REMOTE_INPUTS = "remoteInputs";
  
  private static final String KEY_RESULT_KEY = "resultKey";
  
  private static final String KEY_TITLE = "title";
  
  public static final String TAG = "NotificationCompat";
  
  private static Class<?> sActionClass;
  
  private static Field sActionIconField;
  
  private static Field sActionIntentField;
  
  private static Field sActionTitleField;
  
  private static boolean sActionsAccessFailed;
  
  private static Field sActionsField;
  
  private static final Object sActionsLock;
  
  private static Field sExtrasField;
  
  private static boolean sExtrasFieldAccessFailed;
  
  private static final Object sExtrasLock = new Object();
  
  static  {
    sActionsLock = new Object();
  }
  
  public static SparseArray<Bundle> buildActionExtrasMap(List<Bundle> paramList) {
    int i = paramList.size();
    SparseArray sparseArray = null;
    byte b = 0;
    while (b < i) {
      Bundle bundle = (Bundle)paramList.get(b);
      SparseArray sparseArray1 = sparseArray;
      if (bundle != null) {
        sparseArray1 = sparseArray;
        if (sparseArray == null)
          sparseArray1 = new SparseArray(); 
        sparseArray1.put(b, bundle);
      } 
      b++;
      sparseArray = sparseArray1;
    } 
    return sparseArray;
  }
  
  private static boolean ensureActionReflectionReadyLocked() {
    if (sActionsAccessFailed)
      return false; 
    try {
      if (sActionsField == null) {
        sActionClass = Class.forName("android.app.Notification$Action");
        sActionIconField = sActionClass.getDeclaredField("icon");
        sActionTitleField = sActionClass.getDeclaredField("title");
        sActionIntentField = sActionClass.getDeclaredField("actionIntent");
        sActionsField = Notification.class.getDeclaredField("actions");
        sActionsField.setAccessible(true);
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      Log.e("NotificationCompat", "Unable to access notification actions", classNotFoundException);
      sActionsAccessFailed = true;
    } catch (NoSuchFieldException noSuchFieldException) {
      Log.e("NotificationCompat", "Unable to access notification actions", noSuchFieldException);
      sActionsAccessFailed = true;
    } 
    return true ^ sActionsAccessFailed;
  }
  
  private static RemoteInput fromBundle(Bundle paramBundle) {
    ArrayList arrayList = paramBundle.getStringArrayList("allowedDataTypes");
    HashSet hashSet = new HashSet();
    if (arrayList != null) {
      Iterator iterator = arrayList.iterator();
      while (iterator.hasNext())
        hashSet.add((String)iterator.next()); 
    } 
    return new RemoteInput(paramBundle.getString("resultKey"), paramBundle.getCharSequence("label"), paramBundle.getCharSequenceArray("choices"), paramBundle.getBoolean("allowFreeFormInput"), paramBundle.getBundle("extras"), hashSet);
  }
  
  private static RemoteInput[] fromBundleArray(Bundle[] paramArrayOfBundle) {
    if (paramArrayOfBundle == null)
      return null; 
    RemoteInput[] arrayOfRemoteInput = new RemoteInput[paramArrayOfBundle.length];
    for (byte b = 0; b < paramArrayOfBundle.length; b++)
      arrayOfRemoteInput[b] = fromBundle(paramArrayOfBundle[b]); 
    return arrayOfRemoteInput;
  }
  
  public static NotificationCompat.Action getAction(Notification paramNotification, int paramInt) { // Byte code:
    //   0: getstatic android/support/v4/app/NotificationCompatJellybean.sActionsLock : Ljava/lang/Object;
    //   3: astore_2
    //   4: aload_2
    //   5: monitorenter
    //   6: aload_0
    //   7: invokestatic getActionObjectsLocked : (Landroid/app/Notification;)[Ljava/lang/Object;
    //   10: astore_3
    //   11: aload_3
    //   12: ifnull -> 101
    //   15: aload_3
    //   16: iload_1
    //   17: aaload
    //   18: astore_3
    //   19: aload_0
    //   20: invokestatic getExtras : (Landroid/app/Notification;)Landroid/os/Bundle;
    //   23: astore_0
    //   24: aload_0
    //   25: ifnull -> 109
    //   28: aload_0
    //   29: ldc 'android.support.actionExtras'
    //   31: invokevirtual getSparseParcelableArray : (Ljava/lang/String;)Landroid/util/SparseArray;
    //   34: astore_0
    //   35: aload_0
    //   36: ifnull -> 109
    //   39: aload_0
    //   40: iload_1
    //   41: invokevirtual get : (I)Ljava/lang/Object;
    //   44: checkcast android/os/Bundle
    //   47: astore_0
    //   48: goto -> 51
    //   51: getstatic android/support/v4/app/NotificationCompatJellybean.sActionIconField : Ljava/lang/reflect/Field;
    //   54: aload_3
    //   55: invokevirtual getInt : (Ljava/lang/Object;)I
    //   58: getstatic android/support/v4/app/NotificationCompatJellybean.sActionTitleField : Ljava/lang/reflect/Field;
    //   61: aload_3
    //   62: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   65: checkcast java/lang/CharSequence
    //   68: getstatic android/support/v4/app/NotificationCompatJellybean.sActionIntentField : Ljava/lang/reflect/Field;
    //   71: aload_3
    //   72: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   75: checkcast android/app/PendingIntent
    //   78: aload_0
    //   79: invokestatic readAction : (ILjava/lang/CharSequence;Landroid/app/PendingIntent;Landroid/os/Bundle;)Landroid/support/v4/app/NotificationCompat$Action;
    //   82: astore_0
    //   83: aload_2
    //   84: monitorexit
    //   85: aload_0
    //   86: areturn
    //   87: astore_0
    //   88: ldc 'NotificationCompat'
    //   90: ldc 'Unable to access notification actions'
    //   92: aload_0
    //   93: invokestatic e : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   96: pop
    //   97: iconst_1
    //   98: putstatic android/support/v4/app/NotificationCompatJellybean.sActionsAccessFailed : Z
    //   101: aload_2
    //   102: monitorexit
    //   103: aconst_null
    //   104: areturn
    //   105: aload_2
    //   106: monitorexit
    //   107: aload_0
    //   108: athrow
    //   109: aconst_null
    //   110: astore_0
    //   111: goto -> 51
    //   114: astore_0
    //   115: goto -> 105
    // Exception table:
    //   from	to	target	type
    //   6	11	87	java/lang/IllegalAccessException
    //   6	11	114	finally
    //   19	24	87	java/lang/IllegalAccessException
    //   19	24	114	finally
    //   28	35	87	java/lang/IllegalAccessException
    //   28	35	114	finally
    //   39	48	87	java/lang/IllegalAccessException
    //   39	48	114	finally
    //   51	83	87	java/lang/IllegalAccessException
    //   51	83	114	finally
    //   83	85	114	finally
    //   88	101	114	finally
    //   101	103	114	finally
    //   105	107	114	finally }
  
  public static int getActionCount(Notification paramNotification) {
    synchronized (sActionsLock) {
      Object[] arrayOfObject = getActionObjectsLocked(paramNotification);
      if (arrayOfObject != null)
        return arrayOfObject.length; 
    } 
    byte b = 0;
    /* monitor exit ClassFileLocalVariableReferenceExpression{type=ObjectType{java/lang/Object}, name=SYNTHETIC_LOCAL_VARIABLE_2} */
    return b;
  }
  
  static NotificationCompat.Action getActionFromBundle(Bundle paramBundle) {
    boolean bool;
    Bundle bundle = paramBundle.getBundle("extras");
    if (bundle != null) {
      bool = bundle.getBoolean("android.support.allowGeneratedReplies", false);
    } else {
      bool = false;
    } 
    return new NotificationCompat.Action(paramBundle.getInt("icon"), paramBundle.getCharSequence("title"), (PendingIntent)paramBundle.getParcelable("actionIntent"), paramBundle.getBundle("extras"), fromBundleArray(getBundleArrayFromBundle(paramBundle, "remoteInputs")), fromBundleArray(getBundleArrayFromBundle(paramBundle, "dataOnlyRemoteInputs")), bool);
  }
  
  private static Object[] getActionObjectsLocked(Notification paramNotification) {
    synchronized (sActionsLock) {
      if (!ensureActionReflectionReadyLocked())
        return null; 
      try {
        return (Object[])sActionsField.get(paramNotification);
      } catch (IllegalAccessException paramNotification) {
        Log.e("NotificationCompat", "Unable to access notification actions", paramNotification);
        sActionsAccessFailed = true;
        return null;
      } 
    } 
  }
  
  private static Bundle[] getBundleArrayFromBundle(Bundle paramBundle, String paramString) {
    Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray(paramString);
    if (arrayOfParcelable instanceof Bundle[] || arrayOfParcelable == null)
      return (Bundle[])arrayOfParcelable; 
    Bundle[] arrayOfBundle = (Bundle[])Arrays.copyOf(arrayOfParcelable, arrayOfParcelable.length, Bundle[].class);
    paramBundle.putParcelableArray(paramString, arrayOfBundle);
    return arrayOfBundle;
  }
  
  static Bundle getBundleForAction(NotificationCompat.Action paramAction) {
    Bundle bundle1;
    Bundle bundle2 = new Bundle();
    bundle2.putInt("icon", paramAction.getIcon());
    bundle2.putCharSequence("title", paramAction.getTitle());
    bundle2.putParcelable("actionIntent", paramAction.getActionIntent());
    if (paramAction.getExtras() != null) {
      bundle1 = new Bundle(paramAction.getExtras());
    } else {
      bundle1 = new Bundle();
    } 
    bundle1.putBoolean("android.support.allowGeneratedReplies", paramAction.getAllowGeneratedReplies());
    bundle2.putBundle("extras", bundle1);
    bundle2.putParcelableArray("remoteInputs", toBundleArray(paramAction.getRemoteInputs()));
    return bundle2;
  }
  
  public static Bundle getExtras(Notification paramNotification) {
    synchronized (sExtrasLock) {
      if (sExtrasFieldAccessFailed)
        return null; 
      try {
        if (sExtrasField == null) {
          Field field = Notification.class.getDeclaredField("extras");
          if (!Bundle.class.isAssignableFrom(field.getType())) {
            Log.e("NotificationCompat", "Notification.extras field is not of type Bundle");
            sExtrasFieldAccessFailed = true;
            return null;
          } 
          field.setAccessible(true);
          sExtrasField = field;
        } 
        Bundle bundle2 = (Bundle)sExtrasField.get(paramNotification);
        Bundle bundle1 = bundle2;
        if (bundle2 == null) {
          bundle1 = new Bundle();
          sExtrasField.set(paramNotification, bundle1);
        } 
        return bundle1;
      } catch (IllegalAccessException paramNotification) {
        Log.e("NotificationCompat", "Unable to access notification extras", paramNotification);
      } catch (NoSuchFieldException paramNotification) {
        Log.e("NotificationCompat", "Unable to access notification extras", paramNotification);
      } 
      sExtrasFieldAccessFailed = true;
      return null;
    } 
  }
  
  public static NotificationCompat.Action readAction(int paramInt, CharSequence paramCharSequence, PendingIntent paramPendingIntent, Bundle paramBundle) {
    RemoteInput[] arrayOfRemoteInput2;
    RemoteInput[] arrayOfRemoteInput1;
    boolean bool;
    if (paramBundle != null) {
      arrayOfRemoteInput1 = fromBundleArray(getBundleArrayFromBundle(paramBundle, "android.support.remoteInputs"));
      arrayOfRemoteInput2 = fromBundleArray(getBundleArrayFromBundle(paramBundle, "android.support.dataRemoteInputs"));
      bool = paramBundle.getBoolean("android.support.allowGeneratedReplies");
    } else {
      arrayOfRemoteInput1 = null;
      arrayOfRemoteInput2 = arrayOfRemoteInput1;
      bool = false;
    } 
    return new NotificationCompat.Action(paramInt, paramCharSequence, paramPendingIntent, paramBundle, arrayOfRemoteInput1, arrayOfRemoteInput2, bool);
  }
  
  private static Bundle toBundle(RemoteInput paramRemoteInput) {
    Bundle bundle = new Bundle();
    bundle.putString("resultKey", paramRemoteInput.getResultKey());
    bundle.putCharSequence("label", paramRemoteInput.getLabel());
    bundle.putCharSequenceArray("choices", paramRemoteInput.getChoices());
    bundle.putBoolean("allowFreeFormInput", paramRemoteInput.getAllowFreeFormInput());
    bundle.putBundle("extras", paramRemoteInput.getExtras());
    Set set = paramRemoteInput.getAllowedDataTypes();
    if (set != null && !set.isEmpty()) {
      ArrayList arrayList = new ArrayList(set.size());
      Iterator iterator = set.iterator();
      while (iterator.hasNext())
        arrayList.add((String)iterator.next()); 
      bundle.putStringArrayList("allowedDataTypes", arrayList);
    } 
    return bundle;
  }
  
  private static Bundle[] toBundleArray(RemoteInput[] paramArrayOfRemoteInput) {
    if (paramArrayOfRemoteInput == null)
      return null; 
    Bundle[] arrayOfBundle = new Bundle[paramArrayOfRemoteInput.length];
    for (byte b = 0; b < paramArrayOfRemoteInput.length; b++)
      arrayOfBundle[b] = toBundle(paramArrayOfRemoteInput[b]); 
    return arrayOfBundle;
  }
  
  public static Bundle writeActionAndGetExtras(Notification.Builder paramBuilder, NotificationCompat.Action paramAction) {
    paramBuilder.addAction(paramAction.getIcon(), paramAction.getTitle(), paramAction.getActionIntent());
    Bundle bundle = new Bundle(paramAction.getExtras());
    if (paramAction.getRemoteInputs() != null)
      bundle.putParcelableArray("android.support.remoteInputs", toBundleArray(paramAction.getRemoteInputs())); 
    if (paramAction.getDataOnlyRemoteInputs() != null)
      bundle.putParcelableArray("android.support.dataRemoteInputs", toBundleArray(paramAction.getDataOnlyRemoteInputs())); 
    bundle.putBoolean("android.support.allowGeneratedReplies", paramAction.getAllowGeneratedReplies());
    return bundle;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/NotificationCompatJellybean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */