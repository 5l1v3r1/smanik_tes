package android.support.v4.media;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.app.BundleCompat;
import android.support.v4.media.session.IMediaSession;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class MediaBrowserServiceCompat extends Service {
  static final boolean DEBUG = Log.isLoggable("MBServiceCompat", 3);
  
  private static final float EPSILON = 1.0E-5F;
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static final String KEY_MEDIA_ITEM = "media_item";
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static final String KEY_SEARCH_RESULTS = "search_results";
  
  static final int RESULT_ERROR = -1;
  
  static final int RESULT_FLAG_ON_LOAD_ITEM_NOT_IMPLEMENTED = 2;
  
  static final int RESULT_FLAG_ON_SEARCH_NOT_IMPLEMENTED = 4;
  
  static final int RESULT_FLAG_OPTION_NOT_HANDLED = 1;
  
  static final int RESULT_OK = 0;
  
  static final int RESULT_PROGRESS_UPDATE = 1;
  
  public static final String SERVICE_INTERFACE = "android.media.browse.MediaBrowserService";
  
  static final String TAG = "MBServiceCompat";
  
  final ArrayMap<IBinder, ConnectionRecord> mConnections = new ArrayMap();
  
  ConnectionRecord mCurConnection;
  
  final ServiceHandler mHandler = new ServiceHandler();
  
  private MediaBrowserServiceImpl mImpl;
  
  MediaSessionCompat.Token mSession;
  
  void addSubscription(String paramString, ConnectionRecord paramConnectionRecord, IBinder paramIBinder, Bundle paramBundle) {
    List list2 = (List)paramConnectionRecord.subscriptions.get(paramString);
    List list1 = list2;
    if (list2 == null)
      list1 = new ArrayList(); 
    for (Pair pair : list1) {
      if (paramIBinder == pair.first && MediaBrowserCompatUtils.areSameOptions(paramBundle, (Bundle)pair.second))
        return; 
    } 
    list1.add(new Pair(paramIBinder, paramBundle));
    paramConnectionRecord.subscriptions.put(paramString, list1);
    performLoadChildren(paramString, paramConnectionRecord, paramBundle);
  }
  
  List<MediaBrowserCompat.MediaItem> applyOptions(List<MediaBrowserCompat.MediaItem> paramList, Bundle paramBundle) {
    if (paramList == null)
      return null; 
    int i = paramBundle.getInt("android.media.browse.extra.PAGE", -1);
    int m = paramBundle.getInt("android.media.browse.extra.PAGE_SIZE", -1);
    if (i == -1 && m == -1)
      return paramList; 
    int k = m * i;
    int j = k + m;
    if (i < 0 || m < 1 || k >= paramList.size())
      return Collections.EMPTY_LIST; 
    i = j;
    if (j > paramList.size())
      i = paramList.size(); 
    return paramList.subList(k, i);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public final Bundle getBrowserRootHints() { return this.mImpl.getBrowserRootHints(); }
  
  @Nullable
  public MediaSessionCompat.Token getSessionToken() { return this.mSession; }
  
  boolean isValidPackage(String paramString, int paramInt) {
    if (paramString == null)
      return false; 
    String[] arrayOfString = getPackageManager().getPackagesForUid(paramInt);
    int i = arrayOfString.length;
    for (paramInt = 0; paramInt < i; paramInt++) {
      if (arrayOfString[paramInt].equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public void notifyChildrenChanged(@NonNull String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("parentId cannot be null in notifyChildrenChanged"); 
    this.mImpl.notifyChildrenChanged(paramString, null);
  }
  
  public void notifyChildrenChanged(@NonNull String paramString, @NonNull Bundle paramBundle) {
    if (paramString == null)
      throw new IllegalArgumentException("parentId cannot be null in notifyChildrenChanged"); 
    if (paramBundle == null)
      throw new IllegalArgumentException("options cannot be null in notifyChildrenChanged"); 
    this.mImpl.notifyChildrenChanged(paramString, paramBundle);
  }
  
  public IBinder onBind(Intent paramIntent) { return this.mImpl.onBind(paramIntent); }
  
  public void onCreate() {
    super.onCreate();
    if (Build.VERSION.SDK_INT >= 26) {
      this.mImpl = new MediaBrowserServiceImplApi26();
    } else if (Build.VERSION.SDK_INT >= 23) {
      this.mImpl = new MediaBrowserServiceImplApi23();
    } else if (Build.VERSION.SDK_INT >= 21) {
      this.mImpl = new MediaBrowserServiceImplApi21();
    } else {
      this.mImpl = new MediaBrowserServiceImplBase();
    } 
    this.mImpl.onCreate();
  }
  
  public void onCustomAction(@NonNull String paramString, Bundle paramBundle, @NonNull Result<Bundle> paramResult) { paramResult.sendError(null); }
  
  @Nullable
  public abstract BrowserRoot onGetRoot(@NonNull String paramString, int paramInt, @Nullable Bundle paramBundle);
  
  public abstract void onLoadChildren(@NonNull String paramString, @NonNull Result<List<MediaBrowserCompat.MediaItem>> paramResult);
  
  public void onLoadChildren(@NonNull String paramString, @NonNull Result<List<MediaBrowserCompat.MediaItem>> paramResult, @NonNull Bundle paramBundle) {
    paramResult.setFlags(1);
    onLoadChildren(paramString, paramResult);
  }
  
  public void onLoadItem(String paramString, @NonNull Result<MediaBrowserCompat.MediaItem> paramResult) {
    paramResult.setFlags(2);
    paramResult.sendResult(null);
  }
  
  public void onSearch(@NonNull String paramString, Bundle paramBundle, @NonNull Result<List<MediaBrowserCompat.MediaItem>> paramResult) {
    paramResult.setFlags(4);
    paramResult.sendResult(null);
  }
  
  void performCustomAction(String paramString, Bundle paramBundle, ConnectionRecord paramConnectionRecord, final ResultReceiver receiver) {
    Result<Bundle> result = new Result<Bundle>(paramString) {
        void onErrorSent(Bundle param1Bundle) { receiver.send(-1, param1Bundle); }
        
        void onProgressUpdateSent(Bundle param1Bundle) { receiver.send(1, param1Bundle); }
        
        void onResultSent(Bundle param1Bundle) { receiver.send(0, param1Bundle); }
      };
    this.mCurConnection = paramConnectionRecord;
    onCustomAction(paramString, paramBundle, result);
    this.mCurConnection = null;
    if (!result.isDone()) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("onCustomAction must call detach() or sendResult() or sendError() before returning for action=");
      stringBuilder.append(paramString);
      stringBuilder.append(" extras=");
      stringBuilder.append(paramBundle);
      throw new IllegalStateException(stringBuilder.toString());
    } 
  }
  
  void performLoadChildren(final String parentId, final ConnectionRecord connection, final Bundle options) {
    Result<List<MediaBrowserCompat.MediaItem>> result = new Result<List<MediaBrowserCompat.MediaItem>>(paramString) {
        void onResultSent(List<MediaBrowserCompat.MediaItem> param1List) {
          if (MediaBrowserServiceCompat.this.mConnections.get(this.val$connection.callbacks.asBinder()) != connection) {
            if (MediaBrowserServiceCompat.DEBUG) {
              stringBuilder = new StringBuilder();
              stringBuilder.append("Not sending onLoadChildren result for connection that has been disconnected. pkg=");
              stringBuilder.append(this.val$connection.pkg);
              stringBuilder.append(" id=");
              stringBuilder.append(parentId);
              Log.d("MBServiceCompat", stringBuilder.toString());
            } 
            return;
          } 
          List list = stringBuilder;
          if ((getFlags() & true) != 0)
            list = MediaBrowserServiceCompat.this.applyOptions(stringBuilder, options); 
          try {
            this.val$connection.callbacks.onLoadChildren(parentId, list, options);
            return;
          } catch (RemoteException stringBuilder) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Calling onLoadChildren() failed for id=");
            stringBuilder.append(parentId);
            stringBuilder.append(" package=");
            stringBuilder.append(this.val$connection.pkg);
            Log.w("MBServiceCompat", stringBuilder.toString());
            return;
          } 
        }
      };
    this.mCurConnection = paramConnectionRecord;
    if (paramBundle == null) {
      onLoadChildren(paramString, result);
    } else {
      onLoadChildren(paramString, result, paramBundle);
    } 
    this.mCurConnection = null;
    if (!result.isDone()) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("onLoadChildren must call detach() or sendResult() before returning for package=");
      stringBuilder.append(paramConnectionRecord.pkg);
      stringBuilder.append(" id=");
      stringBuilder.append(paramString);
      throw new IllegalStateException(stringBuilder.toString());
    } 
  }
  
  void performLoadItem(String paramString, ConnectionRecord paramConnectionRecord, final ResultReceiver receiver) {
    Result<MediaBrowserCompat.MediaItem> result = new Result<MediaBrowserCompat.MediaItem>(paramString) {
        void onResultSent(MediaBrowserCompat.MediaItem param1MediaItem) {
          if ((getFlags() & 0x2) != 0) {
            receiver.send(-1, null);
            return;
          } 
          Bundle bundle = new Bundle();
          bundle.putParcelable("media_item", param1MediaItem);
          receiver.send(0, bundle);
        }
      };
    this.mCurConnection = paramConnectionRecord;
    onLoadItem(paramString, result);
    this.mCurConnection = null;
    if (!result.isDone()) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("onLoadItem must call detach() or sendResult() before returning for id=");
      stringBuilder.append(paramString);
      throw new IllegalStateException(stringBuilder.toString());
    } 
  }
  
  void performSearch(String paramString, Bundle paramBundle, ConnectionRecord paramConnectionRecord, final ResultReceiver receiver) {
    Result<List<MediaBrowserCompat.MediaItem>> result = new Result<List<MediaBrowserCompat.MediaItem>>(paramString) {
        void onResultSent(List<MediaBrowserCompat.MediaItem> param1List) {
          if ((getFlags() & 0x4) != 0 || param1List == null) {
            receiver.send(-1, null);
            return;
          } 
          Bundle bundle = new Bundle();
          bundle.putParcelableArray("search_results", (Parcelable[])param1List.toArray(new MediaBrowserCompat.MediaItem[0]));
          receiver.send(0, bundle);
        }
      };
    this.mCurConnection = paramConnectionRecord;
    onSearch(paramString, paramBundle, result);
    this.mCurConnection = null;
    if (!result.isDone()) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("onSearch must call detach() or sendResult() before returning for query=");
      stringBuilder.append(paramString);
      throw new IllegalStateException(stringBuilder.toString());
    } 
  }
  
  boolean removeSubscription(String paramString, ConnectionRecord paramConnectionRecord, IBinder paramIBinder) {
    int i = 0;
    byte b = 0;
    int j = 0;
    if (paramIBinder == null) {
      i = j;
      if (paramConnectionRecord.subscriptions.remove(paramString) != null)
        i = 1; 
      return i;
    } 
    List list = (List)paramConnectionRecord.subscriptions.get(paramString);
    j = b;
    if (list != null) {
      byte b1;
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        if (paramIBinder == ((Pair)iterator.next()).first) {
          iterator.remove();
          b1 = 1;
        } 
      } 
      j = b1;
      if (list.size() == 0) {
        paramConnectionRecord.subscriptions.remove(paramString);
        j = b1;
      } 
    } 
    return j;
  }
  
  public void setSessionToken(MediaSessionCompat.Token paramToken) {
    if (paramToken == null)
      throw new IllegalArgumentException("Session token may not be null."); 
    if (this.mSession != null)
      throw new IllegalStateException("The session token has already been set."); 
    this.mSession = paramToken;
    this.mImpl.setSessionToken(paramToken);
  }
  
  public static final class BrowserRoot {
    public static final String EXTRA_OFFLINE = "android.service.media.extra.OFFLINE";
    
    public static final String EXTRA_RECENT = "android.service.media.extra.RECENT";
    
    public static final String EXTRA_SUGGESTED = "android.service.media.extra.SUGGESTED";
    
    @Deprecated
    public static final String EXTRA_SUGGESTION_KEYWORDS = "android.service.media.extra.SUGGESTION_KEYWORDS";
    
    private final Bundle mExtras;
    
    private final String mRootId;
    
    public BrowserRoot(@NonNull String param1String, @Nullable Bundle param1Bundle) {
      if (param1String == null)
        throw new IllegalArgumentException("The root id in BrowserRoot cannot be null. Use null for BrowserRoot instead."); 
      this.mRootId = param1String;
      this.mExtras = param1Bundle;
    }
    
    public Bundle getExtras() { return this.mExtras; }
    
    public String getRootId() { return this.mRootId; }
  }
  
  private class ConnectionRecord implements IBinder.DeathRecipient {
    MediaBrowserServiceCompat.ServiceCallbacks callbacks;
    
    String pkg;
    
    MediaBrowserServiceCompat.BrowserRoot root;
    
    Bundle rootHints;
    
    HashMap<String, List<Pair<IBinder, Bundle>>> subscriptions = new HashMap();
    
    public void binderDied() { MediaBrowserServiceCompat.this.mHandler.post(new Runnable() {
            public void run() { MediaBrowserServiceCompat.this.mConnections.remove(MediaBrowserServiceCompat.ConnectionRecord.this.callbacks.asBinder()); }
          }); }
  }
  
  class null implements Runnable {
    null() {}
    
    public void run() { MediaBrowserServiceCompat.this.mConnections.remove(this.this$1.callbacks.asBinder()); }
  }
  
  static interface MediaBrowserServiceImpl {
    Bundle getBrowserRootHints();
    
    void notifyChildrenChanged(String param1String, Bundle param1Bundle);
    
    IBinder onBind(Intent param1Intent);
    
    void onCreate();
    
    void setSessionToken(MediaSessionCompat.Token param1Token);
  }
  
  @RequiresApi(21)
  class MediaBrowserServiceImplApi21 implements MediaBrowserServiceImpl, MediaBrowserServiceCompatApi21.ServiceCompatProxy {
    Messenger mMessenger;
    
    final List<Bundle> mRootExtrasList = new ArrayList();
    
    Object mServiceObj;
    
    public Bundle getBrowserRootHints() {
      if (this.mMessenger == null)
        return null; 
      if (MediaBrowserServiceCompat.this.mCurConnection == null)
        throw new IllegalStateException("This should be called inside of onLoadChildren, onLoadItem or onSearch methods"); 
      return (this.this$0.mCurConnection.rootHints == null) ? null : new Bundle(this.this$0.mCurConnection.rootHints);
    }
    
    public void notifyChildrenChanged(String param1String, Bundle param1Bundle) {
      notifyChildrenChangedForFramework(param1String, param1Bundle);
      notifyChildrenChangedForCompat(param1String, param1Bundle);
    }
    
    void notifyChildrenChangedForCompat(final String parentId, final Bundle options) { MediaBrowserServiceCompat.this.mHandler.post(new Runnable() {
            public void run() {
              for (IBinder iBinder : MediaBrowserServiceCompat.this.mConnections.keySet()) {
                MediaBrowserServiceCompat.ConnectionRecord connectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
                List list = (List)connectionRecord.subscriptions.get(parentId);
                if (list != null)
                  for (Pair pair : list) {
                    if (MediaBrowserCompatUtils.hasDuplicatedItems(options, (Bundle)pair.second))
                      MediaBrowserServiceCompat.MediaBrowserServiceImplApi21.this.this$0.performLoadChildren(parentId, connectionRecord, (Bundle)pair.second); 
                  }  
              } 
            }
          }); }
    
    void notifyChildrenChangedForFramework(String param1String, Bundle param1Bundle) { MediaBrowserServiceCompatApi21.notifyChildrenChanged(this.mServiceObj, param1String); }
    
    public IBinder onBind(Intent param1Intent) { return MediaBrowserServiceCompatApi21.onBind(this.mServiceObj, param1Intent); }
    
    public void onCreate() {
      this.mServiceObj = MediaBrowserServiceCompatApi21.createService(MediaBrowserServiceCompat.this, this);
      MediaBrowserServiceCompatApi21.onCreate(this.mServiceObj);
    }
    
    public MediaBrowserServiceCompatApi21.BrowserRoot onGetRoot(String param1String, int param1Int, Bundle param1Bundle) {
      String str;
      if (param1Bundle != null && param1Bundle.getInt("extra_client_version", 0) != 0) {
        param1Bundle.remove("extra_client_version");
        this.mMessenger = new Messenger(MediaBrowserServiceCompat.this.mHandler);
        Bundle bundle = new Bundle();
        bundle.putInt("extra_service_version", 2);
        BundleCompat.putBinder(bundle, "extra_messenger", this.mMessenger.getBinder());
        if (MediaBrowserServiceCompat.this.mSession != null) {
          IBinder iBinder = MediaBrowserServiceCompat.this.mSession.getExtraBinder();
          if (iBinder == null) {
            iBinder = null;
          } else {
            iBinder = iBinder.asBinder();
          } 
          BundleCompat.putBinder(bundle, "extra_session_binder", iBinder);
          str = bundle;
        } else {
          this.mRootExtrasList.add(bundle);
          str = bundle;
        } 
      } else {
        str = null;
      } 
      MediaBrowserServiceCompat.BrowserRoot browserRoot = MediaBrowserServiceCompat.this.onGetRoot(param1String, param1Int, param1Bundle);
      if (browserRoot == null)
        return null; 
      if (str == null) {
        Bundle bundle = browserRoot.getExtras();
      } else {
        param1String = str;
        if (browserRoot.getExtras() != null) {
          str.putAll(browserRoot.getExtras());
          param1String = str;
        } 
      } 
      return new MediaBrowserServiceCompatApi21.BrowserRoot(browserRoot.getRootId(), param1String);
    }
    
    public void onLoadChildren(String param1String, final MediaBrowserServiceCompatApi21.ResultWrapper<List<Parcel>> resultWrapper) {
      MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>> result = new MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>(param1String) {
          public void detach() { resultWrapper.detach(); }
          
          void onResultSent(List<MediaBrowserCompat.MediaItem> param2List) {
            if (param2List != null) {
              ArrayList arrayList = new ArrayList();
              Iterator iterator = param2List.iterator();
              while (true) {
                param2List = arrayList;
                if (iterator.hasNext()) {
                  MediaBrowserCompat.MediaItem mediaItem = (MediaBrowserCompat.MediaItem)iterator.next();
                  Parcel parcel = Parcel.obtain();
                  mediaItem.writeToParcel(parcel, 0);
                  arrayList.add(parcel);
                  continue;
                } 
                break;
              } 
            } else {
              param2List = null;
            } 
            resultWrapper.sendResult(param2List);
          }
        };
      MediaBrowserServiceCompat.this.onLoadChildren(param1String, result);
    }
    
    public void setSessionToken(final MediaSessionCompat.Token token) { MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
            public void run() {
              if (!MediaBrowserServiceCompat.MediaBrowserServiceImplApi21.this.mRootExtrasList.isEmpty()) {
                IMediaSession iMediaSession = token.getExtraBinder();
                if (iMediaSession != null) {
                  Iterator iterator = MediaBrowserServiceCompat.MediaBrowserServiceImplApi21.this.mRootExtrasList.iterator();
                  while (iterator.hasNext())
                    BundleCompat.putBinder((Bundle)iterator.next(), "extra_session_binder", iMediaSession.asBinder()); 
                } 
                MediaBrowserServiceCompat.MediaBrowserServiceImplApi21.this.mRootExtrasList.clear();
              } 
              MediaBrowserServiceCompatApi21.setSessionToken(MediaBrowserServiceCompat.MediaBrowserServiceImplApi21.this.mServiceObj, token.getToken());
            }
          }); }
  }
  
  class null implements Runnable {
    public void run() {
      if (!this.this$1.mRootExtrasList.isEmpty()) {
        IMediaSession iMediaSession = token.getExtraBinder();
        if (iMediaSession != null) {
          Iterator iterator = this.this$1.mRootExtrasList.iterator();
          while (iterator.hasNext())
            BundleCompat.putBinder((Bundle)iterator.next(), "extra_session_binder", iMediaSession.asBinder()); 
        } 
        this.this$1.mRootExtrasList.clear();
      } 
      MediaBrowserServiceCompatApi21.setSessionToken(this.this$1.mServiceObj, token.getToken());
    }
  }
  
  class null extends Result<List<MediaBrowserCompat.MediaItem>> {
    null(Object param1Object) { super(param1Object); }
    
    public void detach() { resultWrapper.detach(); }
    
    void onResultSent(List<MediaBrowserCompat.MediaItem> param1List) {
      if (param1List != null) {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = param1List.iterator();
        while (true) {
          param1List = arrayList;
          if (iterator.hasNext()) {
            MediaBrowserCompat.MediaItem mediaItem = (MediaBrowserCompat.MediaItem)iterator.next();
            Parcel parcel = Parcel.obtain();
            mediaItem.writeToParcel(parcel, 0);
            arrayList.add(parcel);
            continue;
          } 
          break;
        } 
      } else {
        param1List = null;
      } 
      resultWrapper.sendResult(param1List);
    }
  }
  
  class null implements Runnable {
    public void run() {
      for (IBinder iBinder : MediaBrowserServiceCompat.this.mConnections.keySet()) {
        MediaBrowserServiceCompat.ConnectionRecord connectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
        List list = (List)connectionRecord.subscriptions.get(parentId);
        if (list != null)
          for (Pair pair : list) {
            if (MediaBrowserCompatUtils.hasDuplicatedItems(options, (Bundle)pair.second))
              this.this$1.this$0.performLoadChildren(parentId, connectionRecord, (Bundle)pair.second); 
          }  
      } 
    }
  }
  
  @RequiresApi(23)
  class MediaBrowserServiceImplApi23 extends MediaBrowserServiceImplApi21 implements MediaBrowserServiceCompatApi23.ServiceCompatProxy {
    MediaBrowserServiceImplApi23() { super(MediaBrowserServiceCompat.this); }
    
    public void onCreate() {
      this.mServiceObj = MediaBrowserServiceCompatApi23.createService(MediaBrowserServiceCompat.this, this);
      MediaBrowserServiceCompatApi21.onCreate(this.mServiceObj);
    }
    
    public void onLoadItem(String param1String, final MediaBrowserServiceCompatApi21.ResultWrapper<Parcel> resultWrapper) {
      MediaBrowserServiceCompat.Result<MediaBrowserCompat.MediaItem> result = new MediaBrowserServiceCompat.Result<MediaBrowserCompat.MediaItem>(param1String) {
          public void detach() { resultWrapper.detach(); }
          
          void onResultSent(MediaBrowserCompat.MediaItem param2MediaItem) {
            if (param2MediaItem == null) {
              resultWrapper.sendResult(null);
              return;
            } 
            Parcel parcel = Parcel.obtain();
            param2MediaItem.writeToParcel(parcel, 0);
            resultWrapper.sendResult(parcel);
          }
        };
      MediaBrowserServiceCompat.this.onLoadItem(param1String, result);
    }
  }
  
  class null extends Result<MediaBrowserCompat.MediaItem> {
    null(Object param1Object) { super(param1Object); }
    
    public void detach() { resultWrapper.detach(); }
    
    void onResultSent(MediaBrowserCompat.MediaItem param1MediaItem) {
      if (param1MediaItem == null) {
        resultWrapper.sendResult(null);
        return;
      } 
      Parcel parcel = Parcel.obtain();
      param1MediaItem.writeToParcel(parcel, 0);
      resultWrapper.sendResult(parcel);
    }
  }
  
  @RequiresApi(26)
  class MediaBrowserServiceImplApi26 extends MediaBrowserServiceImplApi23 implements MediaBrowserServiceCompatApi26.ServiceCompatProxy {
    MediaBrowserServiceImplApi26() { super(MediaBrowserServiceCompat.this); }
    
    public Bundle getBrowserRootHints() { return (MediaBrowserServiceCompat.this.mCurConnection != null) ? ((this.this$0.mCurConnection.rootHints == null) ? null : new Bundle(this.this$0.mCurConnection.rootHints)) : MediaBrowserServiceCompatApi26.getBrowserRootHints(this.mServiceObj); }
    
    void notifyChildrenChangedForFramework(String param1String, Bundle param1Bundle) {
      if (param1Bundle != null) {
        MediaBrowserServiceCompatApi26.notifyChildrenChanged(this.mServiceObj, param1String, param1Bundle);
        return;
      } 
      super.notifyChildrenChangedForFramework(param1String, param1Bundle);
    }
    
    public void onCreate() {
      this.mServiceObj = MediaBrowserServiceCompatApi26.createService(MediaBrowserServiceCompat.this, this);
      MediaBrowserServiceCompatApi21.onCreate(this.mServiceObj);
    }
    
    public void onLoadChildren(String param1String, final MediaBrowserServiceCompatApi26.ResultWrapper resultWrapper, Bundle param1Bundle) {
      MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>> result = new MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>(param1String) {
          public void detach() { resultWrapper.detach(); }
          
          void onResultSent(List<MediaBrowserCompat.MediaItem> param2List) {
            if (param2List != null) {
              ArrayList arrayList = new ArrayList();
              Iterator iterator = param2List.iterator();
              while (true) {
                param2List = arrayList;
                if (iterator.hasNext()) {
                  MediaBrowserCompat.MediaItem mediaItem = (MediaBrowserCompat.MediaItem)iterator.next();
                  Parcel parcel = Parcel.obtain();
                  mediaItem.writeToParcel(parcel, 0);
                  arrayList.add(parcel);
                  continue;
                } 
                break;
              } 
            } else {
              param2List = null;
            } 
            resultWrapper.sendResult(param2List, getFlags());
          }
        };
      MediaBrowserServiceCompat.this.onLoadChildren(param1String, result, param1Bundle);
    }
  }
  
  class null extends Result<List<MediaBrowserCompat.MediaItem>> {
    null(Object param1Object) { super(param1Object); }
    
    public void detach() { resultWrapper.detach(); }
    
    void onResultSent(List<MediaBrowserCompat.MediaItem> param1List) {
      if (param1List != null) {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = param1List.iterator();
        while (true) {
          param1List = arrayList;
          if (iterator.hasNext()) {
            MediaBrowserCompat.MediaItem mediaItem = (MediaBrowserCompat.MediaItem)iterator.next();
            Parcel parcel = Parcel.obtain();
            mediaItem.writeToParcel(parcel, 0);
            arrayList.add(parcel);
            continue;
          } 
          break;
        } 
      } else {
        param1List = null;
      } 
      resultWrapper.sendResult(param1List, getFlags());
    }
  }
  
  class MediaBrowserServiceImplBase implements MediaBrowserServiceImpl {
    private Messenger mMessenger;
    
    public Bundle getBrowserRootHints() {
      if (MediaBrowserServiceCompat.this.mCurConnection == null)
        throw new IllegalStateException("This should be called inside of onLoadChildren, onLoadItem or onSearch methods"); 
      return (this.this$0.mCurConnection.rootHints == null) ? null : new Bundle(this.this$0.mCurConnection.rootHints);
    }
    
    public void notifyChildrenChanged(@NonNull final String parentId, final Bundle options) { MediaBrowserServiceCompat.this.mHandler.post(new Runnable() {
            public void run() {
              for (IBinder iBinder : MediaBrowserServiceCompat.this.mConnections.keySet()) {
                MediaBrowserServiceCompat.ConnectionRecord connectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
                List list = (List)connectionRecord.subscriptions.get(parentId);
                if (list != null)
                  for (Pair pair : list) {
                    if (MediaBrowserCompatUtils.hasDuplicatedItems(options, (Bundle)pair.second))
                      MediaBrowserServiceCompat.MediaBrowserServiceImplBase.this.this$0.performLoadChildren(parentId, connectionRecord, (Bundle)pair.second); 
                  }  
              } 
            }
          }); }
    
    public IBinder onBind(Intent param1Intent) { return "android.media.browse.MediaBrowserService".equals(param1Intent.getAction()) ? this.mMessenger.getBinder() : null; }
    
    public void onCreate() { this.mMessenger = new Messenger(MediaBrowserServiceCompat.this.mHandler); }
    
    public void setSessionToken(final MediaSessionCompat.Token token) { MediaBrowserServiceCompat.this.mHandler.post(new Runnable() {
            public void run() {
              Iterator iterator = MediaBrowserServiceCompat.this.mConnections.values().iterator();
              while (true) {
                if (iterator.hasNext()) {
                  MediaBrowserServiceCompat.ConnectionRecord connectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)iterator.next();
                  try {
                    connectionRecord.callbacks.onConnect(connectionRecord.root.getRootId(), token, connectionRecord.root.getExtras());
                    continue;
                  } catch (RemoteException remoteException) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Connection for ");
                    stringBuilder.append(connectionRecord.pkg);
                    stringBuilder.append(" is no longer valid.");
                    Log.w("MBServiceCompat", stringBuilder.toString());
                    iterator.remove();
                    continue;
                  } 
                } 
                return;
              } 
            }
          }); }
  }
  
  class null implements Runnable {
    public void run() {
      Iterator iterator = MediaBrowserServiceCompat.this.mConnections.values().iterator();
      while (true) {
        if (iterator.hasNext()) {
          MediaBrowserServiceCompat.ConnectionRecord connectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)iterator.next();
          try {
            connectionRecord.callbacks.onConnect(connectionRecord.root.getRootId(), token, connectionRecord.root.getExtras());
            continue;
          } catch (RemoteException remoteException) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Connection for ");
            stringBuilder.append(connectionRecord.pkg);
            stringBuilder.append(" is no longer valid.");
            Log.w("MBServiceCompat", stringBuilder.toString());
            iterator.remove();
            continue;
          } 
        } 
        return;
      } 
    }
  }
  
  class null implements Runnable {
    public void run() {
      for (IBinder iBinder : MediaBrowserServiceCompat.this.mConnections.keySet()) {
        MediaBrowserServiceCompat.ConnectionRecord connectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
        List list = (List)connectionRecord.subscriptions.get(parentId);
        if (list != null)
          for (Pair pair : list) {
            if (MediaBrowserCompatUtils.hasDuplicatedItems(options, (Bundle)pair.second))
              this.this$1.this$0.performLoadChildren(parentId, connectionRecord, (Bundle)pair.second); 
          }  
      } 
    }
  }
  
  public static class Result<T> extends Object {
    private final Object mDebug;
    
    private boolean mDetachCalled;
    
    private int mFlags;
    
    private boolean mSendErrorCalled;
    
    private boolean mSendProgressUpdateCalled;
    
    private boolean mSendResultCalled;
    
    Result(Object param1Object) { this.mDebug = param1Object; }
    
    private void checkExtraFields(Bundle param1Bundle) {
      if (param1Bundle == null)
        return; 
      if (param1Bundle.containsKey("android.media.browse.extra.DOWNLOAD_PROGRESS")) {
        float f = param1Bundle.getFloat("android.media.browse.extra.DOWNLOAD_PROGRESS");
        if (f < -1.0E-5F || f > 1.00001F)
          throw new IllegalArgumentException("The value of the EXTRA_DOWNLOAD_PROGRESS field must be a float number within [0.0, 1.0]."); 
      } 
    }
    
    public void detach() {
      if (this.mDetachCalled) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("detach() called when detach() had already been called for: ");
        stringBuilder.append(this.mDebug);
        throw new IllegalStateException(stringBuilder.toString());
      } 
      if (this.mSendResultCalled) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("detach() called when sendResult() had already been called for: ");
        stringBuilder.append(this.mDebug);
        throw new IllegalStateException(stringBuilder.toString());
      } 
      if (this.mSendErrorCalled) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("detach() called when sendError() had already been called for: ");
        stringBuilder.append(this.mDebug);
        throw new IllegalStateException(stringBuilder.toString());
      } 
      this.mDetachCalled = true;
    }
    
    int getFlags() { return this.mFlags; }
    
    boolean isDone() { return (this.mDetachCalled || this.mSendResultCalled || this.mSendErrorCalled); }
    
    void onErrorSent(Bundle param1Bundle) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("It is not supported to send an error for ");
      stringBuilder.append(this.mDebug);
      throw new UnsupportedOperationException(stringBuilder.toString());
    }
    
    void onProgressUpdateSent(Bundle param1Bundle) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("It is not supported to send an interim update for ");
      stringBuilder.append(this.mDebug);
      throw new UnsupportedOperationException(stringBuilder.toString());
    }
    
    void onResultSent(T param1T) {}
    
    public void sendError(Bundle param1Bundle) {
      StringBuilder stringBuilder;
      if (this.mSendResultCalled || this.mSendErrorCalled) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("sendError() called when either sendResult() or sendError() had already been called for: ");
        stringBuilder.append(this.mDebug);
        throw new IllegalStateException(stringBuilder.toString());
      } 
      this.mSendErrorCalled = true;
      onErrorSent(stringBuilder);
    }
    
    public void sendProgressUpdate(Bundle param1Bundle) {
      StringBuilder stringBuilder;
      if (this.mSendResultCalled || this.mSendErrorCalled) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("sendProgressUpdate() called when either sendResult() or sendError() had already been called for: ");
        stringBuilder.append(this.mDebug);
        throw new IllegalStateException(stringBuilder.toString());
      } 
      checkExtraFields(stringBuilder);
      this.mSendProgressUpdateCalled = true;
      onProgressUpdateSent(stringBuilder);
    }
    
    public void sendResult(T param1T) { // Byte code:
      //   0: aload_0
      //   1: getfield mSendResultCalled : Z
      //   4: ifne -> 28
      //   7: aload_0
      //   8: getfield mSendErrorCalled : Z
      //   11: ifeq -> 17
      //   14: goto -> 28
      //   17: aload_0
      //   18: iconst_1
      //   19: putfield mSendResultCalled : Z
      //   22: aload_0
      //   23: aload_1
      //   24: invokevirtual onResultSent : (Ljava/lang/Object;)V
      //   27: return
      //   28: new java/lang/StringBuilder
      //   31: dup
      //   32: invokespecial <init> : ()V
      //   35: astore_1
      //   36: aload_1
      //   37: ldc 'sendResult() called when either sendResult() or sendError() had already been called for: '
      //   39: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   42: pop
      //   43: aload_1
      //   44: aload_0
      //   45: getfield mDebug : Ljava/lang/Object;
      //   48: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   51: pop
      //   52: new java/lang/IllegalStateException
      //   55: dup
      //   56: aload_1
      //   57: invokevirtual toString : ()Ljava/lang/String;
      //   60: invokespecial <init> : (Ljava/lang/String;)V
      //   63: athrow }
    
    void setFlags(int param1Int) { this.mFlags = param1Int; }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  private static @interface ResultFlags {}
  
  private class ServiceBinderImpl {
    public void addSubscription(final String id, final IBinder token, final Bundle options, final MediaBrowserServiceCompat.ServiceCallbacks callbacks) { MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
            public void run() {
              IBinder iBinder = callbacks.asBinder();
              StringBuilder stringBuilder = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
              if (stringBuilder == null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("addSubscription for callback that isn't registered id=");
                stringBuilder.append(id);
                Log.w("MBServiceCompat", stringBuilder.toString());
                return;
              } 
              MediaBrowserServiceCompat.ServiceBinderImpl.this.this$0.addSubscription(id, stringBuilder, token, options);
            }
          }); }
    
    public void connect(final String pkg, final int uid, Bundle param1Bundle, final MediaBrowserServiceCompat.ServiceCallbacks callbacks) {
      final StringBuilder rootHints;
      if (!MediaBrowserServiceCompat.this.isValidPackage(param1String, param1Int)) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("Package/uid mismatch: uid=");
        stringBuilder.append(param1Int);
        stringBuilder.append(" package=");
        stringBuilder.append(param1String);
        throw new IllegalArgumentException(stringBuilder.toString());
      } 
      MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
            public void run() {
              stringBuilder = callbacks.asBinder();
              MediaBrowserServiceCompat.this.mConnections.remove(stringBuilder);
              connectionRecord = new MediaBrowserServiceCompat.ConnectionRecord(MediaBrowserServiceCompat.ServiceBinderImpl.this.this$0);
              connectionRecord.pkg = pkg;
              connectionRecord.rootHints = rootHints;
              connectionRecord.callbacks = callbacks;
              connectionRecord.root = MediaBrowserServiceCompat.ServiceBinderImpl.this.this$0.onGetRoot(pkg, uid, rootHints);
              if (connectionRecord.root == null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("No root for client ");
                stringBuilder.append(pkg);
                stringBuilder.append(" from service ");
                stringBuilder.append(getClass().getName());
                Log.i("MBServiceCompat", stringBuilder.toString());
                try {
                  callbacks.onConnectFailed();
                  return;
                } catch (RemoteException stringBuilder) {
                  stringBuilder = new StringBuilder();
                  stringBuilder.append("Calling onConnectFailed() failed. Ignoring. pkg=");
                  stringBuilder.append(pkg);
                  Log.w("MBServiceCompat", stringBuilder.toString());
                  return;
                } 
              } 
              try {
                MediaBrowserServiceCompat.this.mConnections.put(stringBuilder, connectionRecord);
                stringBuilder.linkToDeath(connectionRecord, 0);
                if (MediaBrowserServiceCompat.this.mSession != null) {
                  callbacks.onConnect(connectionRecord.root.getRootId(), MediaBrowserServiceCompat.this.mSession, connectionRecord.root.getExtras());
                  return;
                } 
              } catch (RemoteException connectionRecord) {
                StringBuilder stringBuilder1 = new StringBuilder();
                stringBuilder1.append("Calling onConnect() failed. Dropping client. pkg=");
                stringBuilder1.append(pkg);
                Log.w("MBServiceCompat", stringBuilder1.toString());
                MediaBrowserServiceCompat.this.mConnections.remove(stringBuilder);
              } 
            }
          });
    }
    
    public void disconnect(final MediaBrowserServiceCompat.ServiceCallbacks callbacks) { MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
            public void run() {
              IBinder iBinder = callbacks.asBinder();
              MediaBrowserServiceCompat.ConnectionRecord connectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.remove(iBinder);
              if (connectionRecord != null)
                connectionRecord.callbacks.asBinder().unlinkToDeath(connectionRecord, 0); 
            }
          }); }
    
    public void getMediaItem(final String mediaId, final ResultReceiver receiver, final MediaBrowserServiceCompat.ServiceCallbacks callbacks) {
      if (!TextUtils.isEmpty(param1String)) {
        if (param1ResultReceiver == null)
          return; 
        MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
              public void run() {
                IBinder iBinder = callbacks.asBinder();
                StringBuilder stringBuilder = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
                if (stringBuilder == null) {
                  stringBuilder = new StringBuilder();
                  stringBuilder.append("getMediaItem for callback that isn't registered id=");
                  stringBuilder.append(mediaId);
                  Log.w("MBServiceCompat", stringBuilder.toString());
                  return;
                } 
                MediaBrowserServiceCompat.ServiceBinderImpl.this.this$0.performLoadItem(mediaId, stringBuilder, receiver);
              }
            });
        return;
      } 
    }
    
    public void registerCallbacks(final MediaBrowserServiceCompat.ServiceCallbacks callbacks, final Bundle rootHints) { MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
            public void run() {
              iBinder = callbacks.asBinder();
              MediaBrowserServiceCompat.this.mConnections.remove(iBinder);
              MediaBrowserServiceCompat.ConnectionRecord connectionRecord = new MediaBrowserServiceCompat.ConnectionRecord(MediaBrowserServiceCompat.ServiceBinderImpl.this.this$0);
              connectionRecord.callbacks = callbacks;
              connectionRecord.rootHints = rootHints;
              MediaBrowserServiceCompat.this.mConnections.put(iBinder, connectionRecord);
              try {
                iBinder.linkToDeath(connectionRecord, 0);
                return;
              } catch (RemoteException iBinder) {
                Log.w("MBServiceCompat", "IBinder is already dead.");
                return;
              } 
            }
          }); }
    
    public void removeSubscription(final String id, final IBinder token, final MediaBrowserServiceCompat.ServiceCallbacks callbacks) { MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
            public void run() {
              IBinder iBinder = callbacks.asBinder();
              StringBuilder stringBuilder = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
              if (stringBuilder == null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("removeSubscription for callback that isn't registered id=");
                stringBuilder.append(id);
                Log.w("MBServiceCompat", stringBuilder.toString());
                return;
              } 
              if (!MediaBrowserServiceCompat.ServiceBinderImpl.this.this$0.removeSubscription(id, stringBuilder, token)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("removeSubscription called for ");
                stringBuilder.append(id);
                stringBuilder.append(" which is not subscribed");
                Log.w("MBServiceCompat", stringBuilder.toString());
              } 
            }
          }); }
    
    public void search(final String query, final Bundle extras, final ResultReceiver receiver, final MediaBrowserServiceCompat.ServiceCallbacks callbacks) {
      if (!TextUtils.isEmpty(param1String)) {
        if (param1ResultReceiver == null)
          return; 
        MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
              public void run() {
                IBinder iBinder = callbacks.asBinder();
                StringBuilder stringBuilder = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
                if (stringBuilder == null) {
                  stringBuilder = new StringBuilder();
                  stringBuilder.append("search for callback that isn't registered query=");
                  stringBuilder.append(query);
                  Log.w("MBServiceCompat", stringBuilder.toString());
                  return;
                } 
                MediaBrowserServiceCompat.ServiceBinderImpl.this.this$0.performSearch(query, extras, stringBuilder, receiver);
              }
            });
        return;
      } 
    }
    
    public void sendCustomAction(final String action, final Bundle extras, final ResultReceiver receiver, final MediaBrowserServiceCompat.ServiceCallbacks callbacks) {
      if (!TextUtils.isEmpty(param1String)) {
        if (param1ResultReceiver == null)
          return; 
        MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
              public void run() {
                IBinder iBinder = callbacks.asBinder();
                StringBuilder stringBuilder = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
                if (stringBuilder == null) {
                  stringBuilder = new StringBuilder();
                  stringBuilder.append("sendCustomAction for callback that isn't registered action=");
                  stringBuilder.append(action);
                  stringBuilder.append(", extras=");
                  stringBuilder.append(extras);
                  Log.w("MBServiceCompat", stringBuilder.toString());
                  return;
                } 
                MediaBrowserServiceCompat.ServiceBinderImpl.this.this$0.performCustomAction(action, extras, stringBuilder, receiver);
              }
            });
        return;
      } 
    }
    
    public void unregisterCallbacks(final MediaBrowserServiceCompat.ServiceCallbacks callbacks) { MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
            public void run() {
              IBinder iBinder = callbacks.asBinder();
              MediaBrowserServiceCompat.ConnectionRecord connectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.remove(iBinder);
              if (connectionRecord != null)
                iBinder.unlinkToDeath(connectionRecord, 0); 
            }
          }); }
  }
  
  class null implements Runnable {
    public void run() {
      stringBuilder = callbacks.asBinder();
      MediaBrowserServiceCompat.this.mConnections.remove(stringBuilder);
      connectionRecord = new MediaBrowserServiceCompat.ConnectionRecord(this.this$1.this$0);
      connectionRecord.pkg = pkg;
      connectionRecord.rootHints = rootHints;
      connectionRecord.callbacks = callbacks;
      connectionRecord.root = this.this$1.this$0.onGetRoot(pkg, uid, rootHints);
      if (connectionRecord.root == null) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("No root for client ");
        stringBuilder.append(pkg);
        stringBuilder.append(" from service ");
        stringBuilder.append(getClass().getName());
        Log.i("MBServiceCompat", stringBuilder.toString());
        try {
          callbacks.onConnectFailed();
          return;
        } catch (RemoteException stringBuilder) {
          stringBuilder = new StringBuilder();
          stringBuilder.append("Calling onConnectFailed() failed. Ignoring. pkg=");
          stringBuilder.append(pkg);
          Log.w("MBServiceCompat", stringBuilder.toString());
          return;
        } 
      } 
      try {
        MediaBrowserServiceCompat.this.mConnections.put(stringBuilder, connectionRecord);
        stringBuilder.linkToDeath(connectionRecord, 0);
        if (MediaBrowserServiceCompat.this.mSession != null) {
          callbacks.onConnect(connectionRecord.root.getRootId(), MediaBrowserServiceCompat.this.mSession, connectionRecord.root.getExtras());
          return;
        } 
      } catch (RemoteException connectionRecord) {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Calling onConnect() failed. Dropping client. pkg=");
        stringBuilder1.append(pkg);
        Log.w("MBServiceCompat", stringBuilder1.toString());
        MediaBrowserServiceCompat.this.mConnections.remove(stringBuilder);
      } 
    }
  }
  
  class null implements Runnable {
    public void run() {
      IBinder iBinder = callbacks.asBinder();
      MediaBrowserServiceCompat.ConnectionRecord connectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.remove(iBinder);
      if (connectionRecord != null)
        connectionRecord.callbacks.asBinder().unlinkToDeath(connectionRecord, 0); 
    }
  }
  
  class null implements Runnable {
    public void run() {
      IBinder iBinder = callbacks.asBinder();
      StringBuilder stringBuilder = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
      if (stringBuilder == null) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("addSubscription for callback that isn't registered id=");
        stringBuilder.append(id);
        Log.w("MBServiceCompat", stringBuilder.toString());
        return;
      } 
      this.this$1.this$0.addSubscription(id, stringBuilder, token, options);
    }
  }
  
  class null implements Runnable {
    public void run() {
      IBinder iBinder = callbacks.asBinder();
      StringBuilder stringBuilder = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
      if (stringBuilder == null) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("removeSubscription for callback that isn't registered id=");
        stringBuilder.append(id);
        Log.w("MBServiceCompat", stringBuilder.toString());
        return;
      } 
      if (!this.this$1.this$0.removeSubscription(id, stringBuilder, token)) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("removeSubscription called for ");
        stringBuilder.append(id);
        stringBuilder.append(" which is not subscribed");
        Log.w("MBServiceCompat", stringBuilder.toString());
      } 
    }
  }
  
  class null implements Runnable {
    public void run() {
      IBinder iBinder = callbacks.asBinder();
      StringBuilder stringBuilder = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
      if (stringBuilder == null) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("getMediaItem for callback that isn't registered id=");
        stringBuilder.append(mediaId);
        Log.w("MBServiceCompat", stringBuilder.toString());
        return;
      } 
      this.this$1.this$0.performLoadItem(mediaId, stringBuilder, receiver);
    }
  }
  
  class null implements Runnable {
    public void run() {
      iBinder = callbacks.asBinder();
      MediaBrowserServiceCompat.this.mConnections.remove(iBinder);
      MediaBrowserServiceCompat.ConnectionRecord connectionRecord = new MediaBrowserServiceCompat.ConnectionRecord(this.this$1.this$0);
      connectionRecord.callbacks = callbacks;
      connectionRecord.rootHints = rootHints;
      MediaBrowserServiceCompat.this.mConnections.put(iBinder, connectionRecord);
      try {
        iBinder.linkToDeath(connectionRecord, 0);
        return;
      } catch (RemoteException iBinder) {
        Log.w("MBServiceCompat", "IBinder is already dead.");
        return;
      } 
    }
  }
  
  class null implements Runnable {
    public void run() {
      IBinder iBinder = callbacks.asBinder();
      MediaBrowserServiceCompat.ConnectionRecord connectionRecord = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.remove(iBinder);
      if (connectionRecord != null)
        iBinder.unlinkToDeath(connectionRecord, 0); 
    }
  }
  
  class null implements Runnable {
    public void run() {
      IBinder iBinder = callbacks.asBinder();
      StringBuilder stringBuilder = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
      if (stringBuilder == null) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("search for callback that isn't registered query=");
        stringBuilder.append(query);
        Log.w("MBServiceCompat", stringBuilder.toString());
        return;
      } 
      this.this$1.this$0.performSearch(query, extras, stringBuilder, receiver);
    }
  }
  
  class null implements Runnable {
    public void run() {
      IBinder iBinder = callbacks.asBinder();
      StringBuilder stringBuilder = (MediaBrowserServiceCompat.ConnectionRecord)MediaBrowserServiceCompat.this.mConnections.get(iBinder);
      if (stringBuilder == null) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("sendCustomAction for callback that isn't registered action=");
        stringBuilder.append(action);
        stringBuilder.append(", extras=");
        stringBuilder.append(extras);
        Log.w("MBServiceCompat", stringBuilder.toString());
        return;
      } 
      this.this$1.this$0.performCustomAction(action, extras, stringBuilder, receiver);
    }
  }
  
  private static interface ServiceCallbacks {
    IBinder asBinder();
    
    void onConnect(String param1String, MediaSessionCompat.Token param1Token, Bundle param1Bundle) throws RemoteException;
    
    void onConnectFailed();
    
    void onLoadChildren(String param1String, List<MediaBrowserCompat.MediaItem> param1List, Bundle param1Bundle) throws RemoteException;
  }
  
  private static class ServiceCallbacksCompat implements ServiceCallbacks {
    final Messenger mCallbacks;
    
    ServiceCallbacksCompat(Messenger param1Messenger) { this.mCallbacks = param1Messenger; }
    
    private void sendRequest(int param1Int, Bundle param1Bundle) throws RemoteException {
      Message message = Message.obtain();
      message.what = param1Int;
      message.arg1 = 2;
      message.setData(param1Bundle);
      this.mCallbacks.send(message);
    }
    
    public IBinder asBinder() { return this.mCallbacks.getBinder(); }
    
    public void onConnect(String param1String, MediaSessionCompat.Token param1Token, Bundle param1Bundle) throws RemoteException {
      Bundle bundle = param1Bundle;
      if (param1Bundle == null)
        bundle = new Bundle(); 
      bundle.putInt("extra_service_version", 2);
      param1Bundle = new Bundle();
      param1Bundle.putString("data_media_item_id", param1String);
      param1Bundle.putParcelable("data_media_session_token", param1Token);
      param1Bundle.putBundle("data_root_hints", bundle);
      sendRequest(1, param1Bundle);
    }
    
    public void onConnectFailed() { sendRequest(2, null); }
    
    public void onLoadChildren(String param1String, List<MediaBrowserCompat.MediaItem> param1List, Bundle param1Bundle) throws RemoteException {
      Bundle bundle = new Bundle();
      bundle.putString("data_media_item_id", param1String);
      bundle.putBundle("data_options", param1Bundle);
      if (param1List != null) {
        ArrayList arrayList;
        if (param1List instanceof ArrayList) {
          arrayList = (ArrayList)param1List;
        } else {
          arrayList = new ArrayList(param1List);
        } 
        bundle.putParcelableArrayList("data_media_item_list", arrayList);
      } 
      sendRequest(3, bundle);
    }
  }
  
  private final class ServiceHandler extends Handler {
    private final MediaBrowserServiceCompat.ServiceBinderImpl mServiceBinderImpl = new MediaBrowserServiceCompat.ServiceBinderImpl(MediaBrowserServiceCompat.this);
    
    public void handleMessage(Message param1Message) {
      StringBuilder stringBuilder;
      Bundle bundle = param1Message.getData();
      switch (param1Message.what) {
        default:
          stringBuilder = new StringBuilder();
          stringBuilder.append("Unhandled message: ");
          stringBuilder.append(param1Message);
          stringBuilder.append("\n  Service version: ");
          stringBuilder.append(2);
          stringBuilder.append("\n  Client version: ");
          stringBuilder.append(param1Message.arg1);
          Log.w("MBServiceCompat", stringBuilder.toString());
          return;
        case 9:
          this.mServiceBinderImpl.sendCustomAction(stringBuilder.getString("data_custom_action"), stringBuilder.getBundle("data_custom_action_extras"), (ResultReceiver)stringBuilder.getParcelable("data_result_receiver"), new MediaBrowserServiceCompat.ServiceCallbacksCompat(param1Message.replyTo));
          return;
        case 8:
          this.mServiceBinderImpl.search(stringBuilder.getString("data_search_query"), stringBuilder.getBundle("data_search_extras"), (ResultReceiver)stringBuilder.getParcelable("data_result_receiver"), new MediaBrowserServiceCompat.ServiceCallbacksCompat(param1Message.replyTo));
          return;
        case 7:
          this.mServiceBinderImpl.unregisterCallbacks(new MediaBrowserServiceCompat.ServiceCallbacksCompat(param1Message.replyTo));
          return;
        case 6:
          this.mServiceBinderImpl.registerCallbacks(new MediaBrowserServiceCompat.ServiceCallbacksCompat(param1Message.replyTo), stringBuilder.getBundle("data_root_hints"));
          return;
        case 5:
          this.mServiceBinderImpl.getMediaItem(stringBuilder.getString("data_media_item_id"), (ResultReceiver)stringBuilder.getParcelable("data_result_receiver"), new MediaBrowserServiceCompat.ServiceCallbacksCompat(param1Message.replyTo));
          return;
        case 4:
          this.mServiceBinderImpl.removeSubscription(stringBuilder.getString("data_media_item_id"), BundleCompat.getBinder(stringBuilder, "data_callback_token"), new MediaBrowserServiceCompat.ServiceCallbacksCompat(param1Message.replyTo));
          return;
        case 3:
          this.mServiceBinderImpl.addSubscription(stringBuilder.getString("data_media_item_id"), BundleCompat.getBinder(stringBuilder, "data_callback_token"), stringBuilder.getBundle("data_options"), new MediaBrowserServiceCompat.ServiceCallbacksCompat(param1Message.replyTo));
          return;
        case 2:
          this.mServiceBinderImpl.disconnect(new MediaBrowserServiceCompat.ServiceCallbacksCompat(param1Message.replyTo));
          return;
        case 1:
          break;
      } 
      this.mServiceBinderImpl.connect(stringBuilder.getString("data_package_name"), stringBuilder.getInt("data_calling_uid"), stringBuilder.getBundle("data_root_hints"), new MediaBrowserServiceCompat.ServiceCallbacksCompat(param1Message.replyTo));
    }
    
    public void postOrRun(Runnable param1Runnable) {
      if (Thread.currentThread() == getLooper().getThread()) {
        param1Runnable.run();
        return;
      } 
      post(param1Runnable);
    }
    
    public boolean sendMessageAtTime(Message param1Message, long param1Long) {
      Bundle bundle = param1Message.getData();
      bundle.setClassLoader(MediaBrowserCompat.class.getClassLoader());
      bundle.putInt("data_calling_uid", Binder.getCallingUid());
      return super.sendMessageAtTime(param1Message, param1Long);
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/media/MediaBrowserServiceCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */