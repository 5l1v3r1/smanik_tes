package android.support.v4.app;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.GuardedBy;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class NotificationManagerCompat {
  public static final String ACTION_BIND_SIDE_CHANNEL = "android.support.BIND_NOTIFICATION_SIDE_CHANNEL";
  
  private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
  
  public static final String EXTRA_USE_SIDE_CHANNEL = "android.support.useSideChannel";
  
  public static final int IMPORTANCE_DEFAULT = 3;
  
  public static final int IMPORTANCE_HIGH = 4;
  
  public static final int IMPORTANCE_LOW = 2;
  
  public static final int IMPORTANCE_MAX = 5;
  
  public static final int IMPORTANCE_MIN = 1;
  
  public static final int IMPORTANCE_NONE = 0;
  
  public static final int IMPORTANCE_UNSPECIFIED = -1000;
  
  static final int MAX_SIDE_CHANNEL_SDK_VERSION = 19;
  
  private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
  
  private static final String SETTING_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
  
  private static final int SIDE_CHANNEL_RETRY_BASE_INTERVAL_MS = 1000;
  
  private static final int SIDE_CHANNEL_RETRY_MAX_COUNT = 6;
  
  private static final String TAG = "NotifManCompat";
  
  @GuardedBy("sEnabledNotificationListenersLock")
  private static Set<String> sEnabledNotificationListenerPackages;
  
  @GuardedBy("sEnabledNotificationListenersLock")
  private static String sEnabledNotificationListeners;
  
  private static final Object sEnabledNotificationListenersLock = new Object();
  
  private static final Object sLock;
  
  @GuardedBy("sLock")
  private static SideChannelManager sSideChannelManager;
  
  private final Context mContext;
  
  private final NotificationManager mNotificationManager;
  
  static  {
    sEnabledNotificationListenerPackages = new HashSet();
    sLock = new Object();
  }
  
  private NotificationManagerCompat(Context paramContext) {
    this.mContext = paramContext;
    this.mNotificationManager = (NotificationManager)this.mContext.getSystemService("notification");
  }
  
  @NonNull
  public static NotificationManagerCompat from(@NonNull Context paramContext) { return new NotificationManagerCompat(paramContext); }
  
  @NonNull
  public static Set<String> getEnabledListenerPackages(@NonNull Context paramContext) { // Byte code:
    //   0: aload_0
    //   1: invokevirtual getContentResolver : ()Landroid/content/ContentResolver;
    //   4: ldc 'enabled_notification_listeners'
    //   6: invokestatic getString : (Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;
    //   9: astore_3
    //   10: getstatic android/support/v4/app/NotificationManagerCompat.sEnabledNotificationListenersLock : Ljava/lang/Object;
    //   13: astore_0
    //   14: aload_0
    //   15: monitorenter
    //   16: aload_3
    //   17: ifnull -> 103
    //   20: aload_3
    //   21: getstatic android/support/v4/app/NotificationManagerCompat.sEnabledNotificationListeners : Ljava/lang/String;
    //   24: invokevirtual equals : (Ljava/lang/Object;)Z
    //   27: ifne -> 103
    //   30: aload_3
    //   31: ldc ':'
    //   33: invokevirtual split : (Ljava/lang/String;)[Ljava/lang/String;
    //   36: astore #4
    //   38: new java/util/HashSet
    //   41: dup
    //   42: aload #4
    //   44: arraylength
    //   45: invokespecial <init> : (I)V
    //   48: astore #5
    //   50: aload #4
    //   52: arraylength
    //   53: istore_2
    //   54: iconst_0
    //   55: istore_1
    //   56: iload_1
    //   57: iload_2
    //   58: if_icmpge -> 91
    //   61: aload #4
    //   63: iload_1
    //   64: aaload
    //   65: invokestatic unflattenFromString : (Ljava/lang/String;)Landroid/content/ComponentName;
    //   68: astore #6
    //   70: aload #6
    //   72: ifnull -> 115
    //   75: aload #5
    //   77: aload #6
    //   79: invokevirtual getPackageName : ()Ljava/lang/String;
    //   82: invokeinterface add : (Ljava/lang/Object;)Z
    //   87: pop
    //   88: goto -> 115
    //   91: aload #5
    //   93: putstatic android/support/v4/app/NotificationManagerCompat.sEnabledNotificationListenerPackages : Ljava/util/Set;
    //   96: aload_3
    //   97: putstatic android/support/v4/app/NotificationManagerCompat.sEnabledNotificationListeners : Ljava/lang/String;
    //   100: goto -> 103
    //   103: getstatic android/support/v4/app/NotificationManagerCompat.sEnabledNotificationListenerPackages : Ljava/util/Set;
    //   106: astore_3
    //   107: aload_0
    //   108: monitorexit
    //   109: aload_3
    //   110: areturn
    //   111: aload_0
    //   112: monitorexit
    //   113: aload_3
    //   114: athrow
    //   115: iload_1
    //   116: iconst_1
    //   117: iadd
    //   118: istore_1
    //   119: goto -> 56
    //   122: astore_3
    //   123: goto -> 111
    // Exception table:
    //   from	to	target	type
    //   20	54	122	finally
    //   61	70	122	finally
    //   75	88	122	finally
    //   91	100	122	finally
    //   103	109	122	finally
    //   111	113	122	finally }
  
  private void pushSideChannelQueue(Task paramTask) {
    synchronized (sLock) {
      if (sSideChannelManager == null)
        sSideChannelManager = new SideChannelManager(this.mContext.getApplicationContext()); 
      sSideChannelManager.queueTask(paramTask);
      return;
    } 
  }
  
  private static boolean useSideChannelForNotification(Notification paramNotification) {
    Bundle bundle = NotificationCompat.getExtras(paramNotification);
    return (bundle != null && bundle.getBoolean("android.support.useSideChannel"));
  }
  
  public boolean areNotificationsEnabled() {
    if (Build.VERSION.SDK_INT >= 24)
      return this.mNotificationManager.areNotificationsEnabled(); 
    if (Build.VERSION.SDK_INT >= 19) {
      appOpsManager = (AppOpsManager)this.mContext.getSystemService("appops");
      ApplicationInfo applicationInfo = this.mContext.getApplicationInfo();
      String str = this.mContext.getApplicationContext().getPackageName();
      int i = applicationInfo.uid;
      try {
        Class clazz = Class.forName(AppOpsManager.class.getName());
        i = ((Integer)clazz.getMethod("checkOpNoThrow", new Class[] { int.class, int.class, String.class }).invoke(appOpsManager, new Object[] { Integer.valueOf(((Integer)clazz.getDeclaredField("OP_POST_NOTIFICATION").get(Integer.class)).intValue()), Integer.valueOf(i), str })).intValue();
        return (i == 0);
      } catch (ClassNotFoundException|NoSuchMethodException|NoSuchFieldException|java.lang.reflect.InvocationTargetException|IllegalAccessException|RuntimeException appOpsManager) {
        return true;
      } 
    } 
    return true;
  }
  
  public void cancel(int paramInt) { cancel(null, paramInt); }
  
  public void cancel(@Nullable String paramString, int paramInt) {
    this.mNotificationManager.cancel(paramString, paramInt);
    if (Build.VERSION.SDK_INT <= 19)
      pushSideChannelQueue(new CancelTask(this.mContext.getPackageName(), paramInt, paramString)); 
  }
  
  public void cancelAll() {
    this.mNotificationManager.cancelAll();
    if (Build.VERSION.SDK_INT <= 19)
      pushSideChannelQueue(new CancelTask(this.mContext.getPackageName())); 
  }
  
  public int getImportance() { return (Build.VERSION.SDK_INT >= 24) ? this.mNotificationManager.getImportance() : -1000; }
  
  public void notify(int paramInt, @NonNull Notification paramNotification) { notify(null, paramInt, paramNotification); }
  
  public void notify(@Nullable String paramString, int paramInt, @NonNull Notification paramNotification) {
    if (useSideChannelForNotification(paramNotification)) {
      pushSideChannelQueue(new NotifyTask(this.mContext.getPackageName(), paramInt, paramString, paramNotification));
      this.mNotificationManager.cancel(paramString, paramInt);
      return;
    } 
    this.mNotificationManager.notify(paramString, paramInt, paramNotification);
  }
  
  private static class CancelTask implements Task {
    final boolean all;
    
    final int id;
    
    final String packageName;
    
    final String tag;
    
    CancelTask(String param1String) {
      this.packageName = param1String;
      this.id = 0;
      this.tag = null;
      this.all = true;
    }
    
    CancelTask(String param1String1, int param1Int, String param1String2) {
      this.packageName = param1String1;
      this.id = param1Int;
      this.tag = param1String2;
      this.all = false;
    }
    
    public void send(INotificationSideChannel param1INotificationSideChannel) throws RemoteException {
      if (this.all) {
        param1INotificationSideChannel.cancelAll(this.packageName);
        return;
      } 
      param1INotificationSideChannel.cancel(this.packageName, this.id, this.tag);
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder("CancelTask[");
      stringBuilder.append("packageName:");
      stringBuilder.append(this.packageName);
      stringBuilder.append(", id:");
      stringBuilder.append(this.id);
      stringBuilder.append(", tag:");
      stringBuilder.append(this.tag);
      stringBuilder.append(", all:");
      stringBuilder.append(this.all);
      stringBuilder.append("]");
      return stringBuilder.toString();
    }
  }
  
  private static class NotifyTask implements Task {
    final int id;
    
    final Notification notif;
    
    final String packageName;
    
    final String tag;
    
    NotifyTask(String param1String1, int param1Int, String param1String2, Notification param1Notification) {
      this.packageName = param1String1;
      this.id = param1Int;
      this.tag = param1String2;
      this.notif = param1Notification;
    }
    
    public void send(INotificationSideChannel param1INotificationSideChannel) throws RemoteException { param1INotificationSideChannel.notify(this.packageName, this.id, this.tag, this.notif); }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder("NotifyTask[");
      stringBuilder.append("packageName:");
      stringBuilder.append(this.packageName);
      stringBuilder.append(", id:");
      stringBuilder.append(this.id);
      stringBuilder.append(", tag:");
      stringBuilder.append(this.tag);
      stringBuilder.append("]");
      return stringBuilder.toString();
    }
  }
  
  private static class ServiceConnectedEvent {
    final ComponentName componentName;
    
    final IBinder iBinder;
    
    ServiceConnectedEvent(ComponentName param1ComponentName, IBinder param1IBinder) {
      this.componentName = param1ComponentName;
      this.iBinder = param1IBinder;
    }
  }
  
  private static class SideChannelManager implements Handler.Callback, ServiceConnection {
    private static final int MSG_QUEUE_TASK = 0;
    
    private static final int MSG_RETRY_LISTENER_QUEUE = 3;
    
    private static final int MSG_SERVICE_CONNECTED = 1;
    
    private static final int MSG_SERVICE_DISCONNECTED = 2;
    
    private Set<String> mCachedEnabledPackages = new HashSet();
    
    private final Context mContext;
    
    private final Handler mHandler;
    
    private final HandlerThread mHandlerThread;
    
    private final Map<ComponentName, ListenerRecord> mRecordMap = new HashMap();
    
    SideChannelManager(Context param1Context) {
      this.mContext = param1Context;
      this.mHandlerThread = new HandlerThread("NotificationManagerCompat");
      this.mHandlerThread.start();
      this.mHandler = new Handler(this.mHandlerThread.getLooper(), this);
    }
    
    private boolean ensureServiceBound(ListenerRecord param1ListenerRecord) {
      if (param1ListenerRecord.bound)
        return true; 
      Intent intent = (new Intent("android.support.BIND_NOTIFICATION_SIDE_CHANNEL")).setComponent(param1ListenerRecord.componentName);
      param1ListenerRecord.bound = this.mContext.bindService(intent, this, 33);
      if (param1ListenerRecord.bound) {
        param1ListenerRecord.retryCount = 0;
      } else {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unable to bind to listener ");
        stringBuilder.append(param1ListenerRecord.componentName);
        Log.w("NotifManCompat", stringBuilder.toString());
        this.mContext.unbindService(this);
      } 
      return param1ListenerRecord.bound;
    }
    
    private void ensureServiceUnbound(ListenerRecord param1ListenerRecord) {
      if (param1ListenerRecord.bound) {
        this.mContext.unbindService(this);
        param1ListenerRecord.bound = false;
      } 
      param1ListenerRecord.service = null;
    }
    
    private void handleQueueTask(NotificationManagerCompat.Task param1Task) {
      updateListenerMap();
      for (ListenerRecord listenerRecord : this.mRecordMap.values()) {
        listenerRecord.taskQueue.add(param1Task);
        processListenerQueue(listenerRecord);
      } 
    }
    
    private void handleRetryListenerQueue(ComponentName param1ComponentName) {
      ListenerRecord listenerRecord = (ListenerRecord)this.mRecordMap.get(param1ComponentName);
      if (listenerRecord != null)
        processListenerQueue(listenerRecord); 
    }
    
    private void handleServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {
      ListenerRecord listenerRecord = (ListenerRecord)this.mRecordMap.get(param1ComponentName);
      if (listenerRecord != null) {
        listenerRecord.service = INotificationSideChannel.Stub.asInterface(param1IBinder);
        listenerRecord.retryCount = 0;
        processListenerQueue(listenerRecord);
      } 
    }
    
    private void handleServiceDisconnected(ComponentName param1ComponentName) {
      ListenerRecord listenerRecord = (ListenerRecord)this.mRecordMap.get(param1ComponentName);
      if (listenerRecord != null)
        ensureServiceUnbound(listenerRecord); 
    }
    
    private void processListenerQueue(ListenerRecord param1ListenerRecord) {
      if (Log.isLoggable("NotifManCompat", 3)) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Processing component ");
        stringBuilder.append(param1ListenerRecord.componentName);
        stringBuilder.append(", ");
        stringBuilder.append(param1ListenerRecord.taskQueue.size());
        stringBuilder.append(" queued tasks");
        Log.d("NotifManCompat", stringBuilder.toString());
      } 
      if (param1ListenerRecord.taskQueue.isEmpty())
        return; 
      if (!ensureServiceBound(param1ListenerRecord) || param1ListenerRecord.service == null) {
        scheduleListenerRetry(param1ListenerRecord);
        return;
      } 
      while (true) {
        task = (NotificationManagerCompat.Task)param1ListenerRecord.taskQueue.peek();
        if (task != null)
          try {
            if (Log.isLoggable("NotifManCompat", 3)) {
              StringBuilder stringBuilder = new StringBuilder();
              stringBuilder.append("Sending task ");
              stringBuilder.append(task);
              Log.d("NotifManCompat", stringBuilder.toString());
            } 
            task.send(param1ListenerRecord.service);
            param1ListenerRecord.taskQueue.remove();
            continue;
          } catch (DeadObjectException task) {
            if (Log.isLoggable("NotifManCompat", 3)) {
              StringBuilder stringBuilder = new StringBuilder();
              stringBuilder.append("Remote service has died: ");
              stringBuilder.append(param1ListenerRecord.componentName);
              Log.d("NotifManCompat", stringBuilder.toString());
            } 
          } catch (RemoteException task) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("RemoteException communicating with ");
            stringBuilder.append(param1ListenerRecord.componentName);
            Log.w("NotifManCompat", stringBuilder.toString(), task);
          }  
        if (!param1ListenerRecord.taskQueue.isEmpty())
          scheduleListenerRetry(param1ListenerRecord); 
        return;
      } 
    }
    
    private void scheduleListenerRetry(ListenerRecord param1ListenerRecord) {
      if (this.mHandler.hasMessages(3, param1ListenerRecord.componentName))
        return; 
      param1ListenerRecord.retryCount++;
      if (param1ListenerRecord.retryCount > 6) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Giving up on delivering ");
        stringBuilder.append(param1ListenerRecord.taskQueue.size());
        stringBuilder.append(" tasks to ");
        stringBuilder.append(param1ListenerRecord.componentName);
        stringBuilder.append(" after ");
        stringBuilder.append(param1ListenerRecord.retryCount);
        stringBuilder.append(" retries");
        Log.w("NotifManCompat", stringBuilder.toString());
        param1ListenerRecord.taskQueue.clear();
        return;
      } 
      int i = (1 << param1ListenerRecord.retryCount - 1) * 1000;
      if (Log.isLoggable("NotifManCompat", 3)) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Scheduling retry for ");
        stringBuilder.append(i);
        stringBuilder.append(" ms");
        Log.d("NotifManCompat", stringBuilder.toString());
      } 
      Message message = this.mHandler.obtainMessage(3, param1ListenerRecord.componentName);
      this.mHandler.sendMessageDelayed(message, i);
    }
    
    private void updateListenerMap() {
      Set set = NotificationManagerCompat.getEnabledListenerPackages(this.mContext);
      if (set.equals(this.mCachedEnabledPackages))
        return; 
      this.mCachedEnabledPackages = set;
      List list = this.mContext.getPackageManager().queryIntentServices((new Intent()).setAction("android.support.BIND_NOTIFICATION_SIDE_CHANNEL"), 0);
      HashSet hashSet = new HashSet();
      for (ResolveInfo resolveInfo : list) {
        if (!set.contains(resolveInfo.serviceInfo.packageName))
          continue; 
        ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
        if (resolveInfo.serviceInfo.permission != null) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("Permission present on component ");
          stringBuilder.append(componentName);
          stringBuilder.append(", not adding listener record.");
          Log.w("NotifManCompat", stringBuilder.toString());
          continue;
        } 
        hashSet.add(componentName);
      } 
      for (ComponentName componentName : hashSet) {
        if (!this.mRecordMap.containsKey(componentName)) {
          if (Log.isLoggable("NotifManCompat", 3)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Adding listener record for ");
            stringBuilder.append(componentName);
            Log.d("NotifManCompat", stringBuilder.toString());
          } 
          this.mRecordMap.put(componentName, new ListenerRecord(componentName));
        } 
      } 
      Iterator iterator = this.mRecordMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry)iterator.next();
        if (!hashSet.contains(entry.getKey())) {
          if (Log.isLoggable("NotifManCompat", 3)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Removing listener record for ");
            stringBuilder.append(entry.getKey());
            Log.d("NotifManCompat", stringBuilder.toString());
          } 
          ensureServiceUnbound((ListenerRecord)entry.getValue());
          iterator.remove();
        } 
      } 
    }
    
    public boolean handleMessage(Message param1Message) {
      NotificationManagerCompat.ServiceConnectedEvent serviceConnectedEvent;
      switch (param1Message.what) {
        default:
          return false;
        case 3:
          handleRetryListenerQueue((ComponentName)param1Message.obj);
          return true;
        case 2:
          handleServiceDisconnected((ComponentName)param1Message.obj);
          return true;
        case 1:
          serviceConnectedEvent = (NotificationManagerCompat.ServiceConnectedEvent)param1Message.obj;
          handleServiceConnected(serviceConnectedEvent.componentName, serviceConnectedEvent.iBinder);
          return true;
        case 0:
          break;
      } 
      handleQueueTask((NotificationManagerCompat.Task)serviceConnectedEvent.obj);
      return true;
    }
    
    public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {
      if (Log.isLoggable("NotifManCompat", 3)) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Connected to service ");
        stringBuilder.append(param1ComponentName);
        Log.d("NotifManCompat", stringBuilder.toString());
      } 
      this.mHandler.obtainMessage(1, new NotificationManagerCompat.ServiceConnectedEvent(param1ComponentName, param1IBinder)).sendToTarget();
    }
    
    public void onServiceDisconnected(ComponentName param1ComponentName) {
      if (Log.isLoggable("NotifManCompat", 3)) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Disconnected from service ");
        stringBuilder.append(param1ComponentName);
        Log.d("NotifManCompat", stringBuilder.toString());
      } 
      this.mHandler.obtainMessage(2, param1ComponentName).sendToTarget();
    }
    
    public void queueTask(NotificationManagerCompat.Task param1Task) { this.mHandler.obtainMessage(0, param1Task).sendToTarget(); }
    
    private static class ListenerRecord {
      boolean bound = false;
      
      final ComponentName componentName;
      
      int retryCount = 0;
      
      INotificationSideChannel service;
      
      ArrayDeque<NotificationManagerCompat.Task> taskQueue = new ArrayDeque();
      
      ListenerRecord(ComponentName param2ComponentName) { this.componentName = param2ComponentName; }
    }
  }
  
  private static class ListenerRecord {
    boolean bound = false;
    
    final ComponentName componentName;
    
    int retryCount = 0;
    
    INotificationSideChannel service;
    
    ArrayDeque<NotificationManagerCompat.Task> taskQueue = new ArrayDeque();
    
    ListenerRecord(ComponentName param1ComponentName) { this.componentName = param1ComponentName; }
  }
  
  private static interface Task {
    void send(INotificationSideChannel param1INotificationSideChannel) throws RemoteException;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/NotificationManagerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */