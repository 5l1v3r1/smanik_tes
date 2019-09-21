package android.support.v4.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.compat.R;
import android.support.v4.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.SparseArray;
import android.widget.RemoteViews;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NotificationCompat {
  public static final int BADGE_ICON_LARGE = 2;
  
  public static final int BADGE_ICON_NONE = 0;
  
  public static final int BADGE_ICON_SMALL = 1;
  
  public static final String CATEGORY_ALARM = "alarm";
  
  public static final String CATEGORY_CALL = "call";
  
  public static final String CATEGORY_EMAIL = "email";
  
  public static final String CATEGORY_ERROR = "err";
  
  public static final String CATEGORY_EVENT = "event";
  
  public static final String CATEGORY_MESSAGE = "msg";
  
  public static final String CATEGORY_PROGRESS = "progress";
  
  public static final String CATEGORY_PROMO = "promo";
  
  public static final String CATEGORY_RECOMMENDATION = "recommendation";
  
  public static final String CATEGORY_REMINDER = "reminder";
  
  public static final String CATEGORY_SERVICE = "service";
  
  public static final String CATEGORY_SOCIAL = "social";
  
  public static final String CATEGORY_STATUS = "status";
  
  public static final String CATEGORY_SYSTEM = "sys";
  
  public static final String CATEGORY_TRANSPORT = "transport";
  
  @ColorInt
  public static final int COLOR_DEFAULT = 0;
  
  public static final int DEFAULT_ALL = -1;
  
  public static final int DEFAULT_LIGHTS = 4;
  
  public static final int DEFAULT_SOUND = 1;
  
  public static final int DEFAULT_VIBRATE = 2;
  
  public static final String EXTRA_AUDIO_CONTENTS_URI = "android.audioContents";
  
  public static final String EXTRA_BACKGROUND_IMAGE_URI = "android.backgroundImageUri";
  
  public static final String EXTRA_BIG_TEXT = "android.bigText";
  
  public static final String EXTRA_COMPACT_ACTIONS = "android.compactActions";
  
  public static final String EXTRA_CONVERSATION_TITLE = "android.conversationTitle";
  
  public static final String EXTRA_INFO_TEXT = "android.infoText";
  
  public static final String EXTRA_LARGE_ICON = "android.largeIcon";
  
  public static final String EXTRA_LARGE_ICON_BIG = "android.largeIcon.big";
  
  public static final String EXTRA_MEDIA_SESSION = "android.mediaSession";
  
  public static final String EXTRA_MESSAGES = "android.messages";
  
  public static final String EXTRA_PEOPLE = "android.people";
  
  public static final String EXTRA_PICTURE = "android.picture";
  
  public static final String EXTRA_PROGRESS = "android.progress";
  
  public static final String EXTRA_PROGRESS_INDETERMINATE = "android.progressIndeterminate";
  
  public static final String EXTRA_PROGRESS_MAX = "android.progressMax";
  
  public static final String EXTRA_REMOTE_INPUT_HISTORY = "android.remoteInputHistory";
  
  public static final String EXTRA_SELF_DISPLAY_NAME = "android.selfDisplayName";
  
  public static final String EXTRA_SHOW_CHRONOMETER = "android.showChronometer";
  
  public static final String EXTRA_SHOW_WHEN = "android.showWhen";
  
  public static final String EXTRA_SMALL_ICON = "android.icon";
  
  public static final String EXTRA_SUB_TEXT = "android.subText";
  
  public static final String EXTRA_SUMMARY_TEXT = "android.summaryText";
  
  public static final String EXTRA_TEMPLATE = "android.template";
  
  public static final String EXTRA_TEXT = "android.text";
  
  public static final String EXTRA_TEXT_LINES = "android.textLines";
  
  public static final String EXTRA_TITLE = "android.title";
  
  public static final String EXTRA_TITLE_BIG = "android.title.big";
  
  public static final int FLAG_AUTO_CANCEL = 16;
  
  public static final int FLAG_FOREGROUND_SERVICE = 64;
  
  public static final int FLAG_GROUP_SUMMARY = 512;
  
  @Deprecated
  public static final int FLAG_HIGH_PRIORITY = 128;
  
  public static final int FLAG_INSISTENT = 4;
  
  public static final int FLAG_LOCAL_ONLY = 256;
  
  public static final int FLAG_NO_CLEAR = 32;
  
  public static final int FLAG_ONGOING_EVENT = 2;
  
  public static final int FLAG_ONLY_ALERT_ONCE = 8;
  
  public static final int FLAG_SHOW_LIGHTS = 1;
  
  public static final int GROUP_ALERT_ALL = 0;
  
  public static final int GROUP_ALERT_CHILDREN = 2;
  
  public static final int GROUP_ALERT_SUMMARY = 1;
  
  public static final int PRIORITY_DEFAULT = 0;
  
  public static final int PRIORITY_HIGH = 1;
  
  public static final int PRIORITY_LOW = -1;
  
  public static final int PRIORITY_MAX = 2;
  
  public static final int PRIORITY_MIN = -2;
  
  public static final int STREAM_DEFAULT = -1;
  
  public static final int VISIBILITY_PRIVATE = 0;
  
  public static final int VISIBILITY_PUBLIC = 1;
  
  public static final int VISIBILITY_SECRET = -1;
  
  public static Action getAction(Notification paramNotification, int paramInt) {
    Bundle bundle;
    if (Build.VERSION.SDK_INT >= 20)
      return getActionCompatFromAction(paramNotification.actions[paramInt]); 
    int i = Build.VERSION.SDK_INT;
    Notification notification = null;
    if (i >= 19) {
      Notification.Action action = paramNotification.actions[paramInt];
      SparseArray sparseArray = paramNotification.extras.getSparseParcelableArray("android.support.actionExtras");
      paramNotification = notification;
      if (sparseArray != null)
        bundle = (Bundle)sparseArray.get(paramInt); 
      return NotificationCompatJellybean.readAction(action.icon, action.title, action.actionIntent, bundle);
    } 
    return (Build.VERSION.SDK_INT >= 16) ? NotificationCompatJellybean.getAction(bundle, paramInt) : null;
  }
  
  @RequiresApi(20)
  static Action getActionCompatFromAction(Notification.Action paramAction) {
    RemoteInput[] arrayOfRemoteInput;
    RemoteInput[] arrayOfRemoteInput1 = paramAction.getRemoteInputs();
    boolean bool = false;
    if (arrayOfRemoteInput1 == null) {
      arrayOfRemoteInput = null;
    } else {
      arrayOfRemoteInput = new RemoteInput[arrayOfRemoteInput1.length];
      for (byte b = 0; b < arrayOfRemoteInput1.length; b++) {
        RemoteInput remoteInput = arrayOfRemoteInput1[b];
        arrayOfRemoteInput[b] = new RemoteInput(remoteInput.getResultKey(), remoteInput.getLabel(), remoteInput.getChoices(), remoteInput.getAllowFreeFormInput(), remoteInput.getExtras(), null);
      } 
    } 
    if (Build.VERSION.SDK_INT >= 24) {
      if (paramAction.getExtras().getBoolean("android.support.allowGeneratedReplies") || paramAction.getAllowGeneratedReplies())
        bool = true; 
    } else {
      bool = paramAction.getExtras().getBoolean("android.support.allowGeneratedReplies");
    } 
    return new Action(paramAction.icon, paramAction.title, paramAction.actionIntent, paramAction.getExtras(), arrayOfRemoteInput, null, bool);
  }
  
  public static int getActionCount(Notification paramNotification) {
    int j = Build.VERSION.SDK_INT;
    int i = 0;
    if (j >= 19) {
      if (paramNotification.actions != null)
        i = paramNotification.actions.length; 
      return i;
    } 
    return (Build.VERSION.SDK_INT >= 16) ? NotificationCompatJellybean.getActionCount(paramNotification) : 0;
  }
  
  public static int getBadgeIconType(Notification paramNotification) { return (Build.VERSION.SDK_INT >= 26) ? paramNotification.getBadgeIconType() : 0; }
  
  public static String getCategory(Notification paramNotification) { return (Build.VERSION.SDK_INT >= 21) ? paramNotification.category : null; }
  
  public static String getChannelId(Notification paramNotification) { return (Build.VERSION.SDK_INT >= 26) ? paramNotification.getChannelId() : null; }
  
  public static Bundle getExtras(Notification paramNotification) { return (Build.VERSION.SDK_INT >= 19) ? paramNotification.extras : ((Build.VERSION.SDK_INT >= 16) ? NotificationCompatJellybean.getExtras(paramNotification) : null); }
  
  public static String getGroup(Notification paramNotification) { return (Build.VERSION.SDK_INT >= 20) ? paramNotification.getGroup() : ((Build.VERSION.SDK_INT >= 19) ? paramNotification.extras.getString("android.support.groupKey") : ((Build.VERSION.SDK_INT >= 16) ? NotificationCompatJellybean.getExtras(paramNotification).getString("android.support.groupKey") : null)); }
  
  public static int getGroupAlertBehavior(Notification paramNotification) { return (Build.VERSION.SDK_INT >= 26) ? paramNotification.getGroupAlertBehavior() : 0; }
  
  public static boolean getLocalOnly(Notification paramNotification) {
    int i = Build.VERSION.SDK_INT;
    boolean bool = false;
    if (i >= 20) {
      if ((paramNotification.flags & 0x100) != 0)
        bool = true; 
      return bool;
    } 
    return (Build.VERSION.SDK_INT >= 19) ? paramNotification.extras.getBoolean("android.support.localOnly") : ((Build.VERSION.SDK_INT >= 16) ? NotificationCompatJellybean.getExtras(paramNotification).getBoolean("android.support.localOnly") : 0);
  }
  
  static Notification[] getNotificationArrayFromBundle(Bundle paramBundle, String paramString) {
    Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray(paramString);
    if (arrayOfParcelable instanceof Notification[] || arrayOfParcelable == null)
      return (Notification[])arrayOfParcelable; 
    Notification[] arrayOfNotification = new Notification[arrayOfParcelable.length];
    for (byte b = 0; b < arrayOfParcelable.length; b++)
      arrayOfNotification[b] = (Notification)arrayOfParcelable[b]; 
    paramBundle.putParcelableArray(paramString, arrayOfNotification);
    return arrayOfNotification;
  }
  
  public static String getShortcutId(Notification paramNotification) { return (Build.VERSION.SDK_INT >= 26) ? paramNotification.getShortcutId() : null; }
  
  public static String getSortKey(Notification paramNotification) { return (Build.VERSION.SDK_INT >= 20) ? paramNotification.getSortKey() : ((Build.VERSION.SDK_INT >= 19) ? paramNotification.extras.getString("android.support.sortKey") : ((Build.VERSION.SDK_INT >= 16) ? NotificationCompatJellybean.getExtras(paramNotification).getString("android.support.sortKey") : null)); }
  
  public static long getTimeoutAfter(Notification paramNotification) { return (Build.VERSION.SDK_INT >= 26) ? paramNotification.getTimeoutAfter() : 0L; }
  
  public static boolean isGroupSummary(Notification paramNotification) {
    int i = Build.VERSION.SDK_INT;
    boolean bool = false;
    if (i >= 20) {
      if ((paramNotification.flags & 0x200) != 0)
        bool = true; 
      return bool;
    } 
    return (Build.VERSION.SDK_INT >= 19) ? paramNotification.extras.getBoolean("android.support.isGroupSummary") : ((Build.VERSION.SDK_INT >= 16) ? NotificationCompatJellybean.getExtras(paramNotification).getBoolean("android.support.isGroupSummary") : 0);
  }
  
  public static class Action {
    public PendingIntent actionIntent;
    
    public int icon;
    
    private boolean mAllowGeneratedReplies;
    
    private final RemoteInput[] mDataOnlyRemoteInputs;
    
    final Bundle mExtras;
    
    private final RemoteInput[] mRemoteInputs;
    
    public CharSequence title;
    
    public Action(int param1Int, CharSequence param1CharSequence, PendingIntent param1PendingIntent) { this(param1Int, param1CharSequence, param1PendingIntent, new Bundle(), null, null, true); }
    
    Action(int param1Int, CharSequence param1CharSequence, PendingIntent param1PendingIntent, Bundle param1Bundle, RemoteInput[] param1ArrayOfRemoteInput1, RemoteInput[] param1ArrayOfRemoteInput2, boolean param1Boolean) {
      this.icon = param1Int;
      this.title = NotificationCompat.Builder.limitCharSequenceLength(param1CharSequence);
      this.actionIntent = param1PendingIntent;
      if (param1Bundle == null)
        param1Bundle = new Bundle(); 
      this.mExtras = param1Bundle;
      this.mRemoteInputs = param1ArrayOfRemoteInput1;
      this.mDataOnlyRemoteInputs = param1ArrayOfRemoteInput2;
      this.mAllowGeneratedReplies = param1Boolean;
    }
    
    public PendingIntent getActionIntent() { return this.actionIntent; }
    
    public boolean getAllowGeneratedReplies() { return this.mAllowGeneratedReplies; }
    
    public RemoteInput[] getDataOnlyRemoteInputs() { return this.mDataOnlyRemoteInputs; }
    
    public Bundle getExtras() { return this.mExtras; }
    
    public int getIcon() { return this.icon; }
    
    public RemoteInput[] getRemoteInputs() { return this.mRemoteInputs; }
    
    public CharSequence getTitle() { return this.title; }
    
    public static final class Builder {
      private boolean mAllowGeneratedReplies = true;
      
      private final Bundle mExtras;
      
      private final int mIcon;
      
      private final PendingIntent mIntent;
      
      private ArrayList<RemoteInput> mRemoteInputs;
      
      private final CharSequence mTitle;
      
      public Builder(int param2Int, CharSequence param2CharSequence, PendingIntent param2PendingIntent) { this(param2Int, param2CharSequence, param2PendingIntent, new Bundle(), null, true); }
      
      private Builder(int param2Int, CharSequence param2CharSequence, PendingIntent param2PendingIntent, Bundle param2Bundle, RemoteInput[] param2ArrayOfRemoteInput, boolean param2Boolean) {
        this.mIcon = param2Int;
        this.mTitle = NotificationCompat.Builder.limitCharSequenceLength(param2CharSequence);
        this.mIntent = param2PendingIntent;
        this.mExtras = param2Bundle;
        if (param2ArrayOfRemoteInput == null) {
          param2CharSequence = null;
        } else {
          arrayList = new ArrayList(Arrays.asList(param2ArrayOfRemoteInput));
        } 
        this.mRemoteInputs = arrayList;
        this.mAllowGeneratedReplies = param2Boolean;
      }
      
      public Builder(NotificationCompat.Action param2Action) { this(param2Action.icon, param2Action.title, param2Action.actionIntent, new Bundle(param2Action.mExtras), param2Action.getRemoteInputs(), param2Action.getAllowGeneratedReplies()); }
      
      public Builder addExtras(Bundle param2Bundle) {
        if (param2Bundle != null)
          this.mExtras.putAll(param2Bundle); 
        return this;
      }
      
      public Builder addRemoteInput(RemoteInput param2RemoteInput) {
        if (this.mRemoteInputs == null)
          this.mRemoteInputs = new ArrayList(); 
        this.mRemoteInputs.add(param2RemoteInput);
        return this;
      }
      
      public NotificationCompat.Action build() {
        RemoteInput[] arrayOfRemoteInput1 = new ArrayList();
        ArrayList arrayList = new ArrayList();
        if (this.mRemoteInputs != null)
          for (RemoteInput remoteInput : this.mRemoteInputs) {
            if (remoteInput.isDataOnly()) {
              arrayOfRemoteInput1.add(remoteInput);
              continue;
            } 
            arrayList.add(remoteInput);
          }  
        boolean bool = arrayOfRemoteInput1.isEmpty();
        RemoteInput[] arrayOfRemoteInput2 = null;
        if (bool) {
          arrayOfRemoteInput1 = null;
        } else {
          arrayOfRemoteInput1 = (RemoteInput[])arrayOfRemoteInput1.toArray(new RemoteInput[arrayOfRemoteInput1.size()]);
        } 
        if (!arrayList.isEmpty())
          arrayOfRemoteInput2 = (RemoteInput[])arrayList.toArray(new RemoteInput[arrayList.size()]); 
        return new NotificationCompat.Action(this.mIcon, this.mTitle, this.mIntent, this.mExtras, arrayOfRemoteInput2, arrayOfRemoteInput1, this.mAllowGeneratedReplies);
      }
      
      public Builder extend(NotificationCompat.Action.Extender param2Extender) {
        param2Extender.extend(this);
        return this;
      }
      
      public Bundle getExtras() { return this.mExtras; }
      
      public Builder setAllowGeneratedReplies(boolean param2Boolean) {
        this.mAllowGeneratedReplies = param2Boolean;
        return this;
      }
    }
    
    public static interface Extender {
      NotificationCompat.Action.Builder extend(NotificationCompat.Action.Builder param2Builder);
    }
    
    public static final class WearableExtender implements Extender {
      private static final int DEFAULT_FLAGS = 1;
      
      private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
      
      private static final int FLAG_AVAILABLE_OFFLINE = 1;
      
      private static final int FLAG_HINT_DISPLAY_INLINE = 4;
      
      private static final int FLAG_HINT_LAUNCHES_ACTIVITY = 2;
      
      private static final String KEY_CANCEL_LABEL = "cancelLabel";
      
      private static final String KEY_CONFIRM_LABEL = "confirmLabel";
      
      private static final String KEY_FLAGS = "flags";
      
      private static final String KEY_IN_PROGRESS_LABEL = "inProgressLabel";
      
      private CharSequence mCancelLabel;
      
      private CharSequence mConfirmLabel;
      
      private int mFlags = 1;
      
      private CharSequence mInProgressLabel;
      
      public WearableExtender() {}
      
      public WearableExtender(NotificationCompat.Action param2Action) {
        Bundle bundle = param2Action.getExtras().getBundle("android.wearable.EXTENSIONS");
        if (bundle != null) {
          this.mFlags = bundle.getInt("flags", 1);
          this.mInProgressLabel = bundle.getCharSequence("inProgressLabel");
          this.mConfirmLabel = bundle.getCharSequence("confirmLabel");
          this.mCancelLabel = bundle.getCharSequence("cancelLabel");
        } 
      }
      
      private void setFlag(int param2Int, boolean param2Boolean) {
        if (param2Boolean) {
          this.mFlags = param2Int | this.mFlags;
          return;
        } 
        this.mFlags = (param2Int ^ 0xFFFFFFFF) & this.mFlags;
      }
      
      public WearableExtender clone() {
        WearableExtender wearableExtender = new WearableExtender();
        wearableExtender.mFlags = this.mFlags;
        wearableExtender.mInProgressLabel = this.mInProgressLabel;
        wearableExtender.mConfirmLabel = this.mConfirmLabel;
        wearableExtender.mCancelLabel = this.mCancelLabel;
        return wearableExtender;
      }
      
      public NotificationCompat.Action.Builder extend(NotificationCompat.Action.Builder param2Builder) {
        Bundle bundle = new Bundle();
        if (this.mFlags != 1)
          bundle.putInt("flags", this.mFlags); 
        if (this.mInProgressLabel != null)
          bundle.putCharSequence("inProgressLabel", this.mInProgressLabel); 
        if (this.mConfirmLabel != null)
          bundle.putCharSequence("confirmLabel", this.mConfirmLabel); 
        if (this.mCancelLabel != null)
          bundle.putCharSequence("cancelLabel", this.mCancelLabel); 
        param2Builder.getExtras().putBundle("android.wearable.EXTENSIONS", bundle);
        return param2Builder;
      }
      
      public CharSequence getCancelLabel() { return this.mCancelLabel; }
      
      public CharSequence getConfirmLabel() { return this.mConfirmLabel; }
      
      public boolean getHintDisplayActionInline() { return ((this.mFlags & 0x4) != 0); }
      
      public boolean getHintLaunchesActivity() { return ((this.mFlags & 0x2) != 0); }
      
      public CharSequence getInProgressLabel() { return this.mInProgressLabel; }
      
      public boolean isAvailableOffline() { return ((this.mFlags & true) != 0); }
      
      public WearableExtender setAvailableOffline(boolean param2Boolean) {
        setFlag(1, param2Boolean);
        return this;
      }
      
      public WearableExtender setCancelLabel(CharSequence param2CharSequence) {
        this.mCancelLabel = param2CharSequence;
        return this;
      }
      
      public WearableExtender setConfirmLabel(CharSequence param2CharSequence) {
        this.mConfirmLabel = param2CharSequence;
        return this;
      }
      
      public WearableExtender setHintDisplayActionInline(boolean param2Boolean) {
        setFlag(4, param2Boolean);
        return this;
      }
      
      public WearableExtender setHintLaunchesActivity(boolean param2Boolean) {
        setFlag(2, param2Boolean);
        return this;
      }
      
      public WearableExtender setInProgressLabel(CharSequence param2CharSequence) {
        this.mInProgressLabel = param2CharSequence;
        return this;
      }
    }
  }
  
  public static final class Builder {
    private boolean mAllowGeneratedReplies = true;
    
    private final Bundle mExtras;
    
    private final int mIcon;
    
    private final PendingIntent mIntent;
    
    private ArrayList<RemoteInput> mRemoteInputs;
    
    private final CharSequence mTitle;
    
    public Builder(int param1Int, CharSequence param1CharSequence, PendingIntent param1PendingIntent) { this(param1Int, param1CharSequence, param1PendingIntent, new Bundle(), null, true); }
    
    private Builder(int param1Int, CharSequence param1CharSequence, PendingIntent param1PendingIntent, Bundle param1Bundle, RemoteInput[] param1ArrayOfRemoteInput, boolean param1Boolean) {
      this.mIcon = param1Int;
      this.mTitle = NotificationCompat.Builder.limitCharSequenceLength(param1CharSequence);
      this.mIntent = param1PendingIntent;
      this.mExtras = param1Bundle;
      if (param1ArrayOfRemoteInput == null) {
        param1CharSequence = null;
      } else {
        arrayList = new ArrayList(Arrays.asList(param1ArrayOfRemoteInput));
      } 
      this.mRemoteInputs = arrayList;
      this.mAllowGeneratedReplies = param1Boolean;
    }
    
    public Builder(NotificationCompat.Action param1Action) { this(param1Action.icon, param1Action.title, param1Action.actionIntent, new Bundle(param1Action.mExtras), param1Action.getRemoteInputs(), param1Action.getAllowGeneratedReplies()); }
    
    public Builder addExtras(Bundle param1Bundle) {
      if (param1Bundle != null)
        this.mExtras.putAll(param1Bundle); 
      return this;
    }
    
    public Builder addRemoteInput(RemoteInput param1RemoteInput) {
      if (this.mRemoteInputs == null)
        this.mRemoteInputs = new ArrayList(); 
      this.mRemoteInputs.add(param1RemoteInput);
      return this;
    }
    
    public NotificationCompat.Action build() {
      RemoteInput[] arrayOfRemoteInput1 = new ArrayList();
      ArrayList arrayList = new ArrayList();
      if (this.mRemoteInputs != null)
        for (RemoteInput remoteInput : this.mRemoteInputs) {
          if (remoteInput.isDataOnly()) {
            arrayOfRemoteInput1.add(remoteInput);
            continue;
          } 
          arrayList.add(remoteInput);
        }  
      boolean bool = arrayOfRemoteInput1.isEmpty();
      RemoteInput[] arrayOfRemoteInput2 = null;
      if (bool) {
        arrayOfRemoteInput1 = null;
      } else {
        arrayOfRemoteInput1 = (RemoteInput[])arrayOfRemoteInput1.toArray(new RemoteInput[arrayOfRemoteInput1.size()]);
      } 
      if (!arrayList.isEmpty())
        arrayOfRemoteInput2 = (RemoteInput[])arrayList.toArray(new RemoteInput[arrayList.size()]); 
      return new NotificationCompat.Action(this.mIcon, this.mTitle, this.mIntent, this.mExtras, arrayOfRemoteInput2, arrayOfRemoteInput1, this.mAllowGeneratedReplies);
    }
    
    public Builder extend(NotificationCompat.Action.Extender param1Extender) {
      param1Extender.extend(this);
      return this;
    }
    
    public Bundle getExtras() { return this.mExtras; }
    
    public Builder setAllowGeneratedReplies(boolean param1Boolean) {
      this.mAllowGeneratedReplies = param1Boolean;
      return this;
    }
  }
  
  public static interface Extender {
    NotificationCompat.Action.Builder extend(NotificationCompat.Action.Builder param1Builder);
  }
  
  public static final class WearableExtender implements Action.Extender {
    private static final int DEFAULT_FLAGS = 1;
    
    private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
    
    private static final int FLAG_AVAILABLE_OFFLINE = 1;
    
    private static final int FLAG_HINT_DISPLAY_INLINE = 4;
    
    private static final int FLAG_HINT_LAUNCHES_ACTIVITY = 2;
    
    private static final String KEY_CANCEL_LABEL = "cancelLabel";
    
    private static final String KEY_CONFIRM_LABEL = "confirmLabel";
    
    private static final String KEY_FLAGS = "flags";
    
    private static final String KEY_IN_PROGRESS_LABEL = "inProgressLabel";
    
    private CharSequence mCancelLabel;
    
    private CharSequence mConfirmLabel;
    
    private int mFlags = 1;
    
    private CharSequence mInProgressLabel;
    
    public WearableExtender() {}
    
    public WearableExtender(NotificationCompat.Action param1Action) {
      Bundle bundle = param1Action.getExtras().getBundle("android.wearable.EXTENSIONS");
      if (bundle != null) {
        this.mFlags = bundle.getInt("flags", 1);
        this.mInProgressLabel = bundle.getCharSequence("inProgressLabel");
        this.mConfirmLabel = bundle.getCharSequence("confirmLabel");
        this.mCancelLabel = bundle.getCharSequence("cancelLabel");
      } 
    }
    
    private void setFlag(int param1Int, boolean param1Boolean) {
      if (param1Boolean) {
        this.mFlags = param1Int | this.mFlags;
        return;
      } 
      this.mFlags = (param1Int ^ 0xFFFFFFFF) & this.mFlags;
    }
    
    public WearableExtender clone() {
      WearableExtender wearableExtender = new WearableExtender();
      wearableExtender.mFlags = this.mFlags;
      wearableExtender.mInProgressLabel = this.mInProgressLabel;
      wearableExtender.mConfirmLabel = this.mConfirmLabel;
      wearableExtender.mCancelLabel = this.mCancelLabel;
      return wearableExtender;
    }
    
    public NotificationCompat.Action.Builder extend(NotificationCompat.Action.Builder param1Builder) {
      Bundle bundle = new Bundle();
      if (this.mFlags != 1)
        bundle.putInt("flags", this.mFlags); 
      if (this.mInProgressLabel != null)
        bundle.putCharSequence("inProgressLabel", this.mInProgressLabel); 
      if (this.mConfirmLabel != null)
        bundle.putCharSequence("confirmLabel", this.mConfirmLabel); 
      if (this.mCancelLabel != null)
        bundle.putCharSequence("cancelLabel", this.mCancelLabel); 
      param1Builder.getExtras().putBundle("android.wearable.EXTENSIONS", bundle);
      return param1Builder;
    }
    
    public CharSequence getCancelLabel() { return this.mCancelLabel; }
    
    public CharSequence getConfirmLabel() { return this.mConfirmLabel; }
    
    public boolean getHintDisplayActionInline() { return ((this.mFlags & 0x4) != 0); }
    
    public boolean getHintLaunchesActivity() { return ((this.mFlags & 0x2) != 0); }
    
    public CharSequence getInProgressLabel() { return this.mInProgressLabel; }
    
    public boolean isAvailableOffline() { return ((this.mFlags & true) != 0); }
    
    public WearableExtender setAvailableOffline(boolean param1Boolean) {
      setFlag(1, param1Boolean);
      return this;
    }
    
    public WearableExtender setCancelLabel(CharSequence param1CharSequence) {
      this.mCancelLabel = param1CharSequence;
      return this;
    }
    
    public WearableExtender setConfirmLabel(CharSequence param1CharSequence) {
      this.mConfirmLabel = param1CharSequence;
      return this;
    }
    
    public WearableExtender setHintDisplayActionInline(boolean param1Boolean) {
      setFlag(4, param1Boolean);
      return this;
    }
    
    public WearableExtender setHintLaunchesActivity(boolean param1Boolean) {
      setFlag(2, param1Boolean);
      return this;
    }
    
    public WearableExtender setInProgressLabel(CharSequence param1CharSequence) {
      this.mInProgressLabel = param1CharSequence;
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface BadgeIconType {}
  
  public static class BigPictureStyle extends Style {
    private Bitmap mBigLargeIcon;
    
    private boolean mBigLargeIconSet;
    
    private Bitmap mPicture;
    
    public BigPictureStyle() {}
    
    public BigPictureStyle(NotificationCompat.Builder param1Builder) { setBuilder(param1Builder); }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      if (Build.VERSION.SDK_INT >= 16) {
        Notification.BigPictureStyle bigPictureStyle = (new Notification.BigPictureStyle(param1NotificationBuilderWithBuilderAccessor.getBuilder())).setBigContentTitle(this.mBigContentTitle).bigPicture(this.mPicture);
        if (this.mBigLargeIconSet)
          bigPictureStyle.bigLargeIcon(this.mBigLargeIcon); 
        if (this.mSummaryTextSet)
          bigPictureStyle.setSummaryText(this.mSummaryText); 
      } 
    }
    
    public BigPictureStyle bigLargeIcon(Bitmap param1Bitmap) {
      this.mBigLargeIcon = param1Bitmap;
      this.mBigLargeIconSet = true;
      return this;
    }
    
    public BigPictureStyle bigPicture(Bitmap param1Bitmap) {
      this.mPicture = param1Bitmap;
      return this;
    }
    
    public BigPictureStyle setBigContentTitle(CharSequence param1CharSequence) {
      this.mBigContentTitle = NotificationCompat.Builder.limitCharSequenceLength(param1CharSequence);
      return this;
    }
    
    public BigPictureStyle setSummaryText(CharSequence param1CharSequence) {
      this.mSummaryText = NotificationCompat.Builder.limitCharSequenceLength(param1CharSequence);
      this.mSummaryTextSet = true;
      return this;
    }
  }
  
  public static class BigTextStyle extends Style {
    private CharSequence mBigText;
    
    public BigTextStyle() {}
    
    public BigTextStyle(NotificationCompat.Builder param1Builder) { setBuilder(param1Builder); }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      if (Build.VERSION.SDK_INT >= 16) {
        Notification.BigTextStyle bigTextStyle = (new Notification.BigTextStyle(param1NotificationBuilderWithBuilderAccessor.getBuilder())).setBigContentTitle(this.mBigContentTitle).bigText(this.mBigText);
        if (this.mSummaryTextSet)
          bigTextStyle.setSummaryText(this.mSummaryText); 
      } 
    }
    
    public BigTextStyle bigText(CharSequence param1CharSequence) {
      this.mBigText = NotificationCompat.Builder.limitCharSequenceLength(param1CharSequence);
      return this;
    }
    
    public BigTextStyle setBigContentTitle(CharSequence param1CharSequence) {
      this.mBigContentTitle = NotificationCompat.Builder.limitCharSequenceLength(param1CharSequence);
      return this;
    }
    
    public BigTextStyle setSummaryText(CharSequence param1CharSequence) {
      this.mSummaryText = NotificationCompat.Builder.limitCharSequenceLength(param1CharSequence);
      this.mSummaryTextSet = true;
      return this;
    }
  }
  
  public static class Builder {
    private static final int MAX_CHARSEQUENCE_LENGTH = 5120;
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public ArrayList<NotificationCompat.Action> mActions = new ArrayList();
    
    int mBadgeIcon = 0;
    
    RemoteViews mBigContentView;
    
    String mCategory;
    
    String mChannelId;
    
    int mColor = 0;
    
    boolean mColorized;
    
    boolean mColorizedSet;
    
    CharSequence mContentInfo;
    
    PendingIntent mContentIntent;
    
    CharSequence mContentText;
    
    CharSequence mContentTitle;
    
    RemoteViews mContentView;
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public Context mContext;
    
    Bundle mExtras;
    
    PendingIntent mFullScreenIntent;
    
    int mGroupAlertBehavior = 0;
    
    String mGroupKey;
    
    boolean mGroupSummary;
    
    RemoteViews mHeadsUpContentView;
    
    Bitmap mLargeIcon;
    
    boolean mLocalOnly = false;
    
    Notification mNotification = new Notification();
    
    int mNumber;
    
    @Deprecated
    public ArrayList<String> mPeople;
    
    int mPriority;
    
    int mProgress;
    
    boolean mProgressIndeterminate;
    
    int mProgressMax;
    
    Notification mPublicVersion;
    
    CharSequence[] mRemoteInputHistory;
    
    String mShortcutId;
    
    boolean mShowWhen = true;
    
    String mSortKey;
    
    NotificationCompat.Style mStyle;
    
    CharSequence mSubText;
    
    RemoteViews mTickerView;
    
    long mTimeout;
    
    boolean mUseChronometer;
    
    int mVisibility = 0;
    
    @Deprecated
    public Builder(Context param1Context) { this(param1Context, null); }
    
    public Builder(@NonNull Context param1Context, @NonNull String param1String) {
      this.mContext = param1Context;
      this.mChannelId = param1String;
      this.mNotification.when = System.currentTimeMillis();
      this.mNotification.audioStreamType = -1;
      this.mPriority = 0;
      this.mPeople = new ArrayList();
    }
    
    protected static CharSequence limitCharSequenceLength(CharSequence param1CharSequence) {
      if (param1CharSequence == null)
        return param1CharSequence; 
      CharSequence charSequence = param1CharSequence;
      if (param1CharSequence.length() > 5120)
        charSequence = param1CharSequence.subSequence(0, 5120); 
      return charSequence;
    }
    
    private void setFlag(int param1Int, boolean param1Boolean) {
      if (param1Boolean) {
        Notification notification1 = this.mNotification;
        notification1.flags = param1Int | notification1.flags;
        return;
      } 
      Notification notification = this.mNotification;
      notification.flags = (param1Int ^ 0xFFFFFFFF) & notification.flags;
    }
    
    public Builder addAction(int param1Int, CharSequence param1CharSequence, PendingIntent param1PendingIntent) {
      this.mActions.add(new NotificationCompat.Action(param1Int, param1CharSequence, param1PendingIntent));
      return this;
    }
    
    public Builder addAction(NotificationCompat.Action param1Action) {
      this.mActions.add(param1Action);
      return this;
    }
    
    public Builder addExtras(Bundle param1Bundle) {
      if (param1Bundle != null) {
        if (this.mExtras == null) {
          this.mExtras = new Bundle(param1Bundle);
          return this;
        } 
        this.mExtras.putAll(param1Bundle);
      } 
      return this;
    }
    
    public Builder addPerson(String param1String) {
      this.mPeople.add(param1String);
      return this;
    }
    
    public Notification build() { return (new NotificationCompatBuilder(this)).build(); }
    
    public Builder extend(NotificationCompat.Extender param1Extender) {
      param1Extender.extend(this);
      return this;
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews getBigContentView() { return this.mBigContentView; }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getColor() { return this.mColor; }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews getContentView() { return this.mContentView; }
    
    public Bundle getExtras() {
      if (this.mExtras == null)
        this.mExtras = new Bundle(); 
      return this.mExtras;
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews getHeadsUpContentView() { return this.mHeadsUpContentView; }
    
    @Deprecated
    public Notification getNotification() { return build(); }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getPriority() { return this.mPriority; }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public long getWhenIfShowing() { return this.mShowWhen ? this.mNotification.when : 0L; }
    
    public Builder setAutoCancel(boolean param1Boolean) {
      setFlag(16, param1Boolean);
      return this;
    }
    
    public Builder setBadgeIconType(int param1Int) {
      this.mBadgeIcon = param1Int;
      return this;
    }
    
    public Builder setCategory(String param1String) {
      this.mCategory = param1String;
      return this;
    }
    
    public Builder setChannelId(@NonNull String param1String) {
      this.mChannelId = param1String;
      return this;
    }
    
    public Builder setColor(@ColorInt int param1Int) {
      this.mColor = param1Int;
      return this;
    }
    
    public Builder setColorized(boolean param1Boolean) {
      this.mColorized = param1Boolean;
      this.mColorizedSet = true;
      return this;
    }
    
    public Builder setContent(RemoteViews param1RemoteViews) {
      this.mNotification.contentView = param1RemoteViews;
      return this;
    }
    
    public Builder setContentInfo(CharSequence param1CharSequence) {
      this.mContentInfo = limitCharSequenceLength(param1CharSequence);
      return this;
    }
    
    public Builder setContentIntent(PendingIntent param1PendingIntent) {
      this.mContentIntent = param1PendingIntent;
      return this;
    }
    
    public Builder setContentText(CharSequence param1CharSequence) {
      this.mContentText = limitCharSequenceLength(param1CharSequence);
      return this;
    }
    
    public Builder setContentTitle(CharSequence param1CharSequence) {
      this.mContentTitle = limitCharSequenceLength(param1CharSequence);
      return this;
    }
    
    public Builder setCustomBigContentView(RemoteViews param1RemoteViews) {
      this.mBigContentView = param1RemoteViews;
      return this;
    }
    
    public Builder setCustomContentView(RemoteViews param1RemoteViews) {
      this.mContentView = param1RemoteViews;
      return this;
    }
    
    public Builder setCustomHeadsUpContentView(RemoteViews param1RemoteViews) {
      this.mHeadsUpContentView = param1RemoteViews;
      return this;
    }
    
    public Builder setDefaults(int param1Int) {
      this.mNotification.defaults = param1Int;
      if ((param1Int & 0x4) != 0) {
        Notification notification = this.mNotification;
        notification.flags |= 0x1;
      } 
      return this;
    }
    
    public Builder setDeleteIntent(PendingIntent param1PendingIntent) {
      this.mNotification.deleteIntent = param1PendingIntent;
      return this;
    }
    
    public Builder setExtras(Bundle param1Bundle) {
      this.mExtras = param1Bundle;
      return this;
    }
    
    public Builder setFullScreenIntent(PendingIntent param1PendingIntent, boolean param1Boolean) {
      this.mFullScreenIntent = param1PendingIntent;
      setFlag(128, param1Boolean);
      return this;
    }
    
    public Builder setGroup(String param1String) {
      this.mGroupKey = param1String;
      return this;
    }
    
    public Builder setGroupAlertBehavior(int param1Int) {
      this.mGroupAlertBehavior = param1Int;
      return this;
    }
    
    public Builder setGroupSummary(boolean param1Boolean) {
      this.mGroupSummary = param1Boolean;
      return this;
    }
    
    public Builder setLargeIcon(Bitmap param1Bitmap) {
      this.mLargeIcon = param1Bitmap;
      return this;
    }
    
    public Builder setLights(@ColorInt int param1Int1, int param1Int2, int param1Int3) {
      this.mNotification.ledARGB = param1Int1;
      this.mNotification.ledOnMS = param1Int2;
      this.mNotification.ledOffMS = param1Int3;
      if (this.mNotification.ledOnMS != 0 && this.mNotification.ledOffMS != 0) {
        param1Int1 = 1;
      } else {
        param1Int1 = 0;
      } 
      this.mNotification.flags = param1Int1 | this.mNotification.flags & 0xFFFFFFFE;
      return this;
    }
    
    public Builder setLocalOnly(boolean param1Boolean) {
      this.mLocalOnly = param1Boolean;
      return this;
    }
    
    public Builder setNumber(int param1Int) {
      this.mNumber = param1Int;
      return this;
    }
    
    public Builder setOngoing(boolean param1Boolean) {
      setFlag(2, param1Boolean);
      return this;
    }
    
    public Builder setOnlyAlertOnce(boolean param1Boolean) {
      setFlag(8, param1Boolean);
      return this;
    }
    
    public Builder setPriority(int param1Int) {
      this.mPriority = param1Int;
      return this;
    }
    
    public Builder setProgress(int param1Int1, int param1Int2, boolean param1Boolean) {
      this.mProgressMax = param1Int1;
      this.mProgress = param1Int2;
      this.mProgressIndeterminate = param1Boolean;
      return this;
    }
    
    public Builder setPublicVersion(Notification param1Notification) {
      this.mPublicVersion = param1Notification;
      return this;
    }
    
    public Builder setRemoteInputHistory(CharSequence[] param1ArrayOfCharSequence) {
      this.mRemoteInputHistory = param1ArrayOfCharSequence;
      return this;
    }
    
    public Builder setShortcutId(String param1String) {
      this.mShortcutId = param1String;
      return this;
    }
    
    public Builder setShowWhen(boolean param1Boolean) {
      this.mShowWhen = param1Boolean;
      return this;
    }
    
    public Builder setSmallIcon(int param1Int) {
      this.mNotification.icon = param1Int;
      return this;
    }
    
    public Builder setSmallIcon(int param1Int1, int param1Int2) {
      this.mNotification.icon = param1Int1;
      this.mNotification.iconLevel = param1Int2;
      return this;
    }
    
    public Builder setSortKey(String param1String) {
      this.mSortKey = param1String;
      return this;
    }
    
    public Builder setSound(Uri param1Uri) {
      this.mNotification.sound = param1Uri;
      this.mNotification.audioStreamType = -1;
      if (Build.VERSION.SDK_INT >= 21)
        this.mNotification.audioAttributes = (new AudioAttributes.Builder()).setContentType(4).setUsage(5).build(); 
      return this;
    }
    
    public Builder setSound(Uri param1Uri, int param1Int) {
      this.mNotification.sound = param1Uri;
      this.mNotification.audioStreamType = param1Int;
      if (Build.VERSION.SDK_INT >= 21)
        this.mNotification.audioAttributes = (new AudioAttributes.Builder()).setContentType(4).setLegacyStreamType(param1Int).build(); 
      return this;
    }
    
    public Builder setStyle(NotificationCompat.Style param1Style) {
      if (this.mStyle != param1Style) {
        this.mStyle = param1Style;
        if (this.mStyle != null)
          this.mStyle.setBuilder(this); 
      } 
      return this;
    }
    
    public Builder setSubText(CharSequence param1CharSequence) {
      this.mSubText = limitCharSequenceLength(param1CharSequence);
      return this;
    }
    
    public Builder setTicker(CharSequence param1CharSequence) {
      this.mNotification.tickerText = limitCharSequenceLength(param1CharSequence);
      return this;
    }
    
    public Builder setTicker(CharSequence param1CharSequence, RemoteViews param1RemoteViews) {
      this.mNotification.tickerText = limitCharSequenceLength(param1CharSequence);
      this.mTickerView = param1RemoteViews;
      return this;
    }
    
    public Builder setTimeoutAfter(long param1Long) {
      this.mTimeout = param1Long;
      return this;
    }
    
    public Builder setUsesChronometer(boolean param1Boolean) {
      this.mUseChronometer = param1Boolean;
      return this;
    }
    
    public Builder setVibrate(long[] param1ArrayOfLong) {
      this.mNotification.vibrate = param1ArrayOfLong;
      return this;
    }
    
    public Builder setVisibility(int param1Int) {
      this.mVisibility = param1Int;
      return this;
    }
    
    public Builder setWhen(long param1Long) {
      this.mNotification.when = param1Long;
      return this;
    }
  }
  
  public static final class CarExtender implements Extender {
    private static final String EXTRA_CAR_EXTENDER = "android.car.EXTENSIONS";
    
    private static final String EXTRA_COLOR = "app_color";
    
    private static final String EXTRA_CONVERSATION = "car_conversation";
    
    private static final String EXTRA_LARGE_ICON = "large_icon";
    
    private static final String KEY_AUTHOR = "author";
    
    private static final String KEY_MESSAGES = "messages";
    
    private static final String KEY_ON_READ = "on_read";
    
    private static final String KEY_ON_REPLY = "on_reply";
    
    private static final String KEY_PARTICIPANTS = "participants";
    
    private static final String KEY_REMOTE_INPUT = "remote_input";
    
    private static final String KEY_TEXT = "text";
    
    private static final String KEY_TIMESTAMP = "timestamp";
    
    private int mColor = 0;
    
    private Bitmap mLargeIcon;
    
    private UnreadConversation mUnreadConversation;
    
    public CarExtender() {}
    
    public CarExtender(Notification param1Notification) {
      if (Build.VERSION.SDK_INT < 21)
        return; 
      if (NotificationCompat.getExtras(param1Notification) == null) {
        param1Notification = null;
      } else {
        bundle = NotificationCompat.getExtras(param1Notification).getBundle("android.car.EXTENSIONS");
      } 
      if (bundle != null) {
        this.mLargeIcon = (Bitmap)bundle.getParcelable("large_icon");
        this.mColor = bundle.getInt("app_color", 0);
        this.mUnreadConversation = getUnreadConversationFromBundle(bundle.getBundle("car_conversation"));
      } 
    }
    
    @RequiresApi(21)
    private static Bundle getBundleForUnreadConversation(@NonNull UnreadConversation param1UnreadConversation) {
      Bundle bundle = new Bundle();
      String[] arrayOfString = param1UnreadConversation.getParticipants();
      byte b = 0;
      if (arrayOfString != null && param1UnreadConversation.getParticipants().length > 1) {
        String str = param1UnreadConversation.getParticipants()[0];
      } else {
        arrayOfString = null;
      } 
      Parcelable[] arrayOfParcelable = new Parcelable[param1UnreadConversation.getMessages().length];
      while (b < arrayOfParcelable.length) {
        Bundle bundle1 = new Bundle();
        bundle1.putString("text", param1UnreadConversation.getMessages()[b]);
        bundle1.putString("author", arrayOfString);
        arrayOfParcelable[b] = bundle1;
        b++;
      } 
      bundle.putParcelableArray("messages", arrayOfParcelable);
      RemoteInput remoteInput = param1UnreadConversation.getRemoteInput();
      if (remoteInput != null)
        bundle.putParcelable("remote_input", (new RemoteInput.Builder(remoteInput.getResultKey())).setLabel(remoteInput.getLabel()).setChoices(remoteInput.getChoices()).setAllowFreeFormInput(remoteInput.getAllowFreeFormInput()).addExtras(remoteInput.getExtras()).build()); 
      bundle.putParcelable("on_reply", param1UnreadConversation.getReplyPendingIntent());
      bundle.putParcelable("on_read", param1UnreadConversation.getReadPendingIntent());
      bundle.putStringArray("participants", param1UnreadConversation.getParticipants());
      bundle.putLong("timestamp", param1UnreadConversation.getLatestTimestamp());
      return bundle;
    }
    
    @RequiresApi(21)
    private static UnreadConversation getUnreadConversationFromBundle(@Nullable Bundle param1Bundle) {
      String[] arrayOfString1;
      RemoteInput remoteInput = null;
      if (param1Bundle == null)
        return null; 
      Parcelable[] arrayOfParcelable = param1Bundle.getParcelableArray("messages");
      if (arrayOfParcelable != null) {
        arrayOfString1 = new String[arrayOfParcelable.length];
        byte b2 = 0;
        byte b1 = 0;
        while (true) {
          if (b1 < arrayOfString1.length) {
            if (!(arrayOfParcelable[b1] instanceof Bundle)) {
              b1 = b2;
              break;
            } 
            arrayOfString1[b1] = ((Bundle)arrayOfParcelable[b1]).getString("text");
            if (arrayOfString1[b1] == null) {
              b1 = b2;
              break;
            } 
            b1++;
            continue;
          } 
          b1 = 1;
          break;
        } 
        if (b1 == 0)
          return null; 
      } else {
        arrayOfString1 = null;
      } 
      PendingIntent pendingIntent1 = (PendingIntent)param1Bundle.getParcelable("on_read");
      PendingIntent pendingIntent2 = (PendingIntent)param1Bundle.getParcelable("on_reply");
      RemoteInput remoteInput1 = (RemoteInput)param1Bundle.getParcelable("remote_input");
      String[] arrayOfString2 = param1Bundle.getStringArray("participants");
      if (arrayOfString2 != null) {
        if (arrayOfString2.length != 1)
          return null; 
        if (remoteInput1 != null)
          remoteInput = new RemoteInput(remoteInput1.getResultKey(), remoteInput1.getLabel(), remoteInput1.getChoices(), remoteInput1.getAllowFreeFormInput(), remoteInput1.getExtras(), null); 
        return new UnreadConversation(arrayOfString1, remoteInput, pendingIntent2, pendingIntent1, arrayOfString2, param1Bundle.getLong("timestamp"));
      } 
      return null;
    }
    
    public NotificationCompat.Builder extend(NotificationCompat.Builder param1Builder) {
      if (Build.VERSION.SDK_INT < 21)
        return param1Builder; 
      Bundle bundle = new Bundle();
      if (this.mLargeIcon != null)
        bundle.putParcelable("large_icon", this.mLargeIcon); 
      if (this.mColor != 0)
        bundle.putInt("app_color", this.mColor); 
      if (this.mUnreadConversation != null)
        bundle.putBundle("car_conversation", getBundleForUnreadConversation(this.mUnreadConversation)); 
      param1Builder.getExtras().putBundle("android.car.EXTENSIONS", bundle);
      return param1Builder;
    }
    
    @ColorInt
    public int getColor() { return this.mColor; }
    
    public Bitmap getLargeIcon() { return this.mLargeIcon; }
    
    public UnreadConversation getUnreadConversation() { return this.mUnreadConversation; }
    
    public CarExtender setColor(@ColorInt int param1Int) {
      this.mColor = param1Int;
      return this;
    }
    
    public CarExtender setLargeIcon(Bitmap param1Bitmap) {
      this.mLargeIcon = param1Bitmap;
      return this;
    }
    
    public CarExtender setUnreadConversation(UnreadConversation param1UnreadConversation) {
      this.mUnreadConversation = param1UnreadConversation;
      return this;
    }
    
    public static class UnreadConversation {
      private final long mLatestTimestamp;
      
      private final String[] mMessages;
      
      private final String[] mParticipants;
      
      private final PendingIntent mReadPendingIntent;
      
      private final RemoteInput mRemoteInput;
      
      private final PendingIntent mReplyPendingIntent;
      
      UnreadConversation(String[] param2ArrayOfString1, RemoteInput param2RemoteInput, PendingIntent param2PendingIntent1, PendingIntent param2PendingIntent2, String[] param2ArrayOfString2, long param2Long) {
        this.mMessages = param2ArrayOfString1;
        this.mRemoteInput = param2RemoteInput;
        this.mReadPendingIntent = param2PendingIntent2;
        this.mReplyPendingIntent = param2PendingIntent1;
        this.mParticipants = param2ArrayOfString2;
        this.mLatestTimestamp = param2Long;
      }
      
      public long getLatestTimestamp() { return this.mLatestTimestamp; }
      
      public String[] getMessages() { return this.mMessages; }
      
      public String getParticipant() { return (this.mParticipants.length > 0) ? this.mParticipants[0] : null; }
      
      public String[] getParticipants() { return this.mParticipants; }
      
      public PendingIntent getReadPendingIntent() { return this.mReadPendingIntent; }
      
      public RemoteInput getRemoteInput() { return this.mRemoteInput; }
      
      public PendingIntent getReplyPendingIntent() { return this.mReplyPendingIntent; }
      
      public static class Builder {
        private long mLatestTimestamp;
        
        private final List<String> mMessages = new ArrayList();
        
        private final String mParticipant;
        
        private PendingIntent mReadPendingIntent;
        
        private RemoteInput mRemoteInput;
        
        private PendingIntent mReplyPendingIntent;
        
        public Builder(String param3String) { this.mParticipant = param3String; }
        
        public Builder addMessage(String param3String) {
          this.mMessages.add(param3String);
          return this;
        }
        
        public NotificationCompat.CarExtender.UnreadConversation build() {
          String[] arrayOfString = (String[])this.mMessages.toArray(new String[this.mMessages.size()]);
          String str = this.mParticipant;
          RemoteInput remoteInput = this.mRemoteInput;
          PendingIntent pendingIntent1 = this.mReplyPendingIntent;
          PendingIntent pendingIntent2 = this.mReadPendingIntent;
          long l = this.mLatestTimestamp;
          return new NotificationCompat.CarExtender.UnreadConversation(arrayOfString, remoteInput, pendingIntent1, pendingIntent2, new String[] { str }, l);
        }
        
        public Builder setLatestTimestamp(long param3Long) {
          this.mLatestTimestamp = param3Long;
          return this;
        }
        
        public Builder setReadPendingIntent(PendingIntent param3PendingIntent) {
          this.mReadPendingIntent = param3PendingIntent;
          return this;
        }
        
        public Builder setReplyAction(PendingIntent param3PendingIntent, RemoteInput param3RemoteInput) {
          this.mRemoteInput = param3RemoteInput;
          this.mReplyPendingIntent = param3PendingIntent;
          return this;
        }
      }
    }
    
    public static class Builder {
      private long mLatestTimestamp;
      
      private final List<String> mMessages = new ArrayList();
      
      private final String mParticipant;
      
      private PendingIntent mReadPendingIntent;
      
      private RemoteInput mRemoteInput;
      
      private PendingIntent mReplyPendingIntent;
      
      public Builder(String param2String) { this.mParticipant = param2String; }
      
      public Builder addMessage(String param2String) {
        this.mMessages.add(param2String);
        return this;
      }
      
      public NotificationCompat.CarExtender.UnreadConversation build() {
        String[] arrayOfString = (String[])this.mMessages.toArray(new String[this.mMessages.size()]);
        String str = this.mParticipant;
        RemoteInput remoteInput = this.mRemoteInput;
        PendingIntent pendingIntent1 = this.mReplyPendingIntent;
        PendingIntent pendingIntent2 = this.mReadPendingIntent;
        long l = this.mLatestTimestamp;
        return new NotificationCompat.CarExtender.UnreadConversation(arrayOfString, remoteInput, pendingIntent1, pendingIntent2, new String[] { str }, l);
      }
      
      public Builder setLatestTimestamp(long param2Long) {
        this.mLatestTimestamp = param2Long;
        return this;
      }
      
      public Builder setReadPendingIntent(PendingIntent param2PendingIntent) {
        this.mReadPendingIntent = param2PendingIntent;
        return this;
      }
      
      public Builder setReplyAction(PendingIntent param2PendingIntent, RemoteInput param2RemoteInput) {
        this.mRemoteInput = param2RemoteInput;
        this.mReplyPendingIntent = param2PendingIntent;
        return this;
      }
    }
  }
  
  public static class UnreadConversation {
    private final long mLatestTimestamp;
    
    private final String[] mMessages;
    
    private final String[] mParticipants;
    
    private final PendingIntent mReadPendingIntent;
    
    private final RemoteInput mRemoteInput;
    
    private final PendingIntent mReplyPendingIntent;
    
    UnreadConversation(String[] param1ArrayOfString1, RemoteInput param1RemoteInput, PendingIntent param1PendingIntent1, PendingIntent param1PendingIntent2, String[] param1ArrayOfString2, long param1Long) {
      this.mMessages = param1ArrayOfString1;
      this.mRemoteInput = param1RemoteInput;
      this.mReadPendingIntent = param1PendingIntent2;
      this.mReplyPendingIntent = param1PendingIntent1;
      this.mParticipants = param1ArrayOfString2;
      this.mLatestTimestamp = param1Long;
    }
    
    public long getLatestTimestamp() { return this.mLatestTimestamp; }
    
    public String[] getMessages() { return this.mMessages; }
    
    public String getParticipant() { return (this.mParticipants.length > 0) ? this.mParticipants[0] : null; }
    
    public String[] getParticipants() { return this.mParticipants; }
    
    public PendingIntent getReadPendingIntent() { return this.mReadPendingIntent; }
    
    public RemoteInput getRemoteInput() { return this.mRemoteInput; }
    
    public PendingIntent getReplyPendingIntent() { return this.mReplyPendingIntent; }
    
    public static class Builder {
      private long mLatestTimestamp;
      
      private final List<String> mMessages = new ArrayList();
      
      private final String mParticipant;
      
      private PendingIntent mReadPendingIntent;
      
      private RemoteInput mRemoteInput;
      
      private PendingIntent mReplyPendingIntent;
      
      public Builder(String param3String) { this.mParticipant = param3String; }
      
      public Builder addMessage(String param3String) {
        this.mMessages.add(param3String);
        return this;
      }
      
      public NotificationCompat.CarExtender.UnreadConversation build() {
        String[] arrayOfString = (String[])this.mMessages.toArray(new String[this.mMessages.size()]);
        String str = this.mParticipant;
        RemoteInput remoteInput = this.mRemoteInput;
        PendingIntent pendingIntent1 = this.mReplyPendingIntent;
        PendingIntent pendingIntent2 = this.mReadPendingIntent;
        long l = this.mLatestTimestamp;
        return new NotificationCompat.CarExtender.UnreadConversation(arrayOfString, remoteInput, pendingIntent1, pendingIntent2, new String[] { str }, l);
      }
      
      public Builder setLatestTimestamp(long param3Long) {
        this.mLatestTimestamp = param3Long;
        return this;
      }
      
      public Builder setReadPendingIntent(PendingIntent param3PendingIntent) {
        this.mReadPendingIntent = param3PendingIntent;
        return this;
      }
      
      public Builder setReplyAction(PendingIntent param3PendingIntent, RemoteInput param3RemoteInput) {
        this.mRemoteInput = param3RemoteInput;
        this.mReplyPendingIntent = param3PendingIntent;
        return this;
      }
    }
  }
  
  public static class Builder {
    private long mLatestTimestamp;
    
    private final List<String> mMessages = new ArrayList();
    
    private final String mParticipant;
    
    private PendingIntent mReadPendingIntent;
    
    private RemoteInput mRemoteInput;
    
    private PendingIntent mReplyPendingIntent;
    
    public Builder(String param1String) { this.mParticipant = param1String; }
    
    public Builder addMessage(String param1String) {
      this.mMessages.add(param1String);
      return this;
    }
    
    public NotificationCompat.CarExtender.UnreadConversation build() {
      String[] arrayOfString = (String[])this.mMessages.toArray(new String[this.mMessages.size()]);
      String str = this.mParticipant;
      RemoteInput remoteInput = this.mRemoteInput;
      PendingIntent pendingIntent1 = this.mReplyPendingIntent;
      PendingIntent pendingIntent2 = this.mReadPendingIntent;
      long l = this.mLatestTimestamp;
      return new NotificationCompat.CarExtender.UnreadConversation(arrayOfString, remoteInput, pendingIntent1, pendingIntent2, new String[] { str }, l);
    }
    
    public Builder setLatestTimestamp(long param1Long) {
      this.mLatestTimestamp = param1Long;
      return this;
    }
    
    public Builder setReadPendingIntent(PendingIntent param1PendingIntent) {
      this.mReadPendingIntent = param1PendingIntent;
      return this;
    }
    
    public Builder setReplyAction(PendingIntent param1PendingIntent, RemoteInput param1RemoteInput) {
      this.mRemoteInput = param1RemoteInput;
      this.mReplyPendingIntent = param1PendingIntent;
      return this;
    }
  }
  
  public static class DecoratedCustomViewStyle extends Style {
    private static final int MAX_ACTION_BUTTONS = 3;
    
    private RemoteViews createRemoteViews(RemoteViews param1RemoteViews, boolean param1Boolean) { // Byte code:
      //   0: getstatic android/support/compat/R$layout.notification_template_custom_big : I
      //   3: istore_3
      //   4: iconst_1
      //   5: istore #6
      //   7: iconst_0
      //   8: istore #5
      //   10: aload_0
      //   11: iconst_1
      //   12: iload_3
      //   13: iconst_0
      //   14: invokevirtual applyStandardTemplate : (ZIZ)Landroid/widget/RemoteViews;
      //   17: astore #8
      //   19: aload #8
      //   21: getstatic android/support/compat/R$id.actions : I
      //   24: invokevirtual removeAllViews : (I)V
      //   27: iload_2
      //   28: ifeq -> 111
      //   31: aload_0
      //   32: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   35: getfield mActions : Ljava/util/ArrayList;
      //   38: ifnull -> 111
      //   41: aload_0
      //   42: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   45: getfield mActions : Ljava/util/ArrayList;
      //   48: invokevirtual size : ()I
      //   51: iconst_3
      //   52: invokestatic min : (II)I
      //   55: istore #7
      //   57: iload #7
      //   59: ifle -> 111
      //   62: iconst_0
      //   63: istore_3
      //   64: iload #6
      //   66: istore #4
      //   68: iload_3
      //   69: iload #7
      //   71: if_icmpge -> 114
      //   74: aload_0
      //   75: aload_0
      //   76: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   79: getfield mActions : Ljava/util/ArrayList;
      //   82: iload_3
      //   83: invokevirtual get : (I)Ljava/lang/Object;
      //   86: checkcast android/support/v4/app/NotificationCompat$Action
      //   89: invokespecial generateActionButton : (Landroid/support/v4/app/NotificationCompat$Action;)Landroid/widget/RemoteViews;
      //   92: astore #9
      //   94: aload #8
      //   96: getstatic android/support/compat/R$id.actions : I
      //   99: aload #9
      //   101: invokevirtual addView : (ILandroid/widget/RemoteViews;)V
      //   104: iload_3
      //   105: iconst_1
      //   106: iadd
      //   107: istore_3
      //   108: goto -> 64
      //   111: iconst_0
      //   112: istore #4
      //   114: iload #4
      //   116: ifeq -> 125
      //   119: iload #5
      //   121: istore_3
      //   122: goto -> 128
      //   125: bipush #8
      //   127: istore_3
      //   128: aload #8
      //   130: getstatic android/support/compat/R$id.actions : I
      //   133: iload_3
      //   134: invokevirtual setViewVisibility : (II)V
      //   137: aload #8
      //   139: getstatic android/support/compat/R$id.action_divider : I
      //   142: iload_3
      //   143: invokevirtual setViewVisibility : (II)V
      //   146: aload_0
      //   147: aload #8
      //   149: aload_1
      //   150: invokevirtual buildIntoRemoteViews : (Landroid/widget/RemoteViews;Landroid/widget/RemoteViews;)V
      //   153: aload #8
      //   155: areturn }
    
    private RemoteViews generateActionButton(NotificationCompat.Action param1Action) {
      int i;
      boolean bool;
      if (param1Action.actionIntent == null) {
        bool = true;
      } else {
        bool = false;
      } 
      String str = this.mBuilder.mContext.getPackageName();
      if (bool) {
        i = R.layout.notification_action_tombstone;
      } else {
        i = R.layout.notification_action;
      } 
      RemoteViews remoteViews = new RemoteViews(str, i);
      remoteViews.setImageViewBitmap(R.id.action_image, createColoredBitmap(param1Action.getIcon(), this.mBuilder.mContext.getResources().getColor(R.color.notification_action_color_filter)));
      remoteViews.setTextViewText(R.id.action_text, param1Action.title);
      if (!bool)
        remoteViews.setOnClickPendingIntent(R.id.action_container, param1Action.actionIntent); 
      if (Build.VERSION.SDK_INT >= 15)
        remoteViews.setContentDescription(R.id.action_container, param1Action.title); 
      return remoteViews;
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      if (Build.VERSION.SDK_INT >= 24)
        param1NotificationBuilderWithBuilderAccessor.getBuilder().setStyle(new Notification.DecoratedCustomViewStyle()); 
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      if (Build.VERSION.SDK_INT >= 24)
        return null; 
      RemoteViews remoteViews = this.mBuilder.getBigContentView();
      if (remoteViews == null)
        remoteViews = this.mBuilder.getContentView(); 
      return (remoteViews == null) ? null : createRemoteViews(remoteViews, true);
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) { return (Build.VERSION.SDK_INT >= 24) ? null : ((this.mBuilder.getContentView() == null) ? null : createRemoteViews(this.mBuilder.getContentView(), false)); }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeHeadsUpContentView(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      RemoteViews remoteViews1;
      if (Build.VERSION.SDK_INT >= 24)
        return null; 
      RemoteViews remoteViews2 = this.mBuilder.getHeadsUpContentView();
      if (remoteViews2 != null) {
        remoteViews1 = remoteViews2;
      } else {
        remoteViews1 = this.mBuilder.getContentView();
      } 
      return (remoteViews2 == null) ? null : createRemoteViews(remoteViews1, true);
    }
  }
  
  public static interface Extender {
    NotificationCompat.Builder extend(NotificationCompat.Builder param1Builder);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface GroupAlertBehavior {}
  
  public static class InboxStyle extends Style {
    private ArrayList<CharSequence> mTexts = new ArrayList();
    
    public InboxStyle() {}
    
    public InboxStyle(NotificationCompat.Builder param1Builder) { setBuilder(param1Builder); }
    
    public InboxStyle addLine(CharSequence param1CharSequence) {
      this.mTexts.add(NotificationCompat.Builder.limitCharSequenceLength(param1CharSequence));
      return this;
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      if (Build.VERSION.SDK_INT >= 16) {
        Notification.InboxStyle inboxStyle = (new Notification.InboxStyle(param1NotificationBuilderWithBuilderAccessor.getBuilder())).setBigContentTitle(this.mBigContentTitle);
        if (this.mSummaryTextSet)
          inboxStyle.setSummaryText(this.mSummaryText); 
        Iterator iterator = this.mTexts.iterator();
        while (iterator.hasNext())
          inboxStyle.addLine((CharSequence)iterator.next()); 
      } 
    }
    
    public InboxStyle setBigContentTitle(CharSequence param1CharSequence) {
      this.mBigContentTitle = NotificationCompat.Builder.limitCharSequenceLength(param1CharSequence);
      return this;
    }
    
    public InboxStyle setSummaryText(CharSequence param1CharSequence) {
      this.mSummaryText = NotificationCompat.Builder.limitCharSequenceLength(param1CharSequence);
      this.mSummaryTextSet = true;
      return this;
    }
  }
  
  public static class MessagingStyle extends Style {
    public static final int MAXIMUM_RETAINED_MESSAGES = 25;
    
    CharSequence mConversationTitle;
    
    List<Message> mMessages = new ArrayList();
    
    CharSequence mUserDisplayName;
    
    MessagingStyle() {}
    
    public MessagingStyle(@NonNull CharSequence param1CharSequence) { this.mUserDisplayName = param1CharSequence; }
    
    public static MessagingStyle extractMessagingStyleFromNotification(Notification param1Notification) {
      bundle = NotificationCompat.getExtras(param1Notification);
      if (bundle != null && !bundle.containsKey("android.selfDisplayName"))
        return null; 
      try {
        MessagingStyle messagingStyle = new MessagingStyle();
        messagingStyle.restoreFromCompatExtras(bundle);
        return messagingStyle;
      } catch (ClassCastException bundle) {
        return null;
      } 
    }
    
    @Nullable
    private Message findLatestIncomingMessage() {
      for (int i = this.mMessages.size() - 1; i >= 0; i--) {
        Message message = (Message)this.mMessages.get(i);
        if (!TextUtils.isEmpty(message.getSender()))
          return message; 
      } 
      return !this.mMessages.isEmpty() ? (Message)this.mMessages.get(this.mMessages.size() - 1) : null;
    }
    
    private boolean hasMessagesWithoutSender() {
      for (int i = this.mMessages.size() - 1; i >= 0; i--) {
        if (((Message)this.mMessages.get(i)).getSender() == null)
          return true; 
      } 
      return false;
    }
    
    @NonNull
    private TextAppearanceSpan makeFontColorSpan(int param1Int) { return new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(param1Int), null); }
    
    private CharSequence makeMessageLine(Message param1Message) {
      boolean bool;
      byte b;
      CharSequence charSequence1;
      BidiFormatter bidiFormatter = BidiFormatter.getInstance();
      SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
      if (Build.VERSION.SDK_INT >= 21) {
        bool = true;
      } else {
        bool = false;
      } 
      if (bool) {
        b = -16777216;
      } else {
        b = -1;
      } 
      CharSequence charSequence3 = param1Message.getSender();
      int i = b;
      if (TextUtils.isEmpty(param1Message.getSender())) {
        CharSequence charSequence;
        if (this.mUserDisplayName == null) {
          charSequence = "";
        } else {
          charSequence = this.mUserDisplayName;
        } 
        i = b;
        charSequence3 = charSequence;
        if (bool) {
          i = b;
          charSequence3 = charSequence;
          if (this.mBuilder.getColor() != 0) {
            i = this.mBuilder.getColor();
            charSequence3 = charSequence;
          } 
        } 
      } 
      CharSequence charSequence2 = bidiFormatter.unicodeWrap(charSequence3);
      spannableStringBuilder.append(charSequence2);
      spannableStringBuilder.setSpan(makeFontColorSpan(i), spannableStringBuilder.length() - charSequence2.length(), spannableStringBuilder.length(), 33);
      if (param1Message.getText() == null) {
        charSequence1 = "";
      } else {
        charSequence1 = charSequence1.getText();
      } 
      spannableStringBuilder.append("  ").append(bidiFormatter.unicodeWrap(charSequence1));
      return spannableStringBuilder;
    }
    
    public void addCompatExtras(Bundle param1Bundle) {
      super.addCompatExtras(param1Bundle);
      if (this.mUserDisplayName != null)
        param1Bundle.putCharSequence("android.selfDisplayName", this.mUserDisplayName); 
      if (this.mConversationTitle != null)
        param1Bundle.putCharSequence("android.conversationTitle", this.mConversationTitle); 
      if (!this.mMessages.isEmpty())
        param1Bundle.putParcelableArray("android.messages", Message.getBundleArrayForMessages(this.mMessages)); 
    }
    
    public MessagingStyle addMessage(Message param1Message) {
      this.mMessages.add(param1Message);
      if (this.mMessages.size() > 25)
        this.mMessages.remove(0); 
      return this;
    }
    
    public MessagingStyle addMessage(CharSequence param1CharSequence1, long param1Long, CharSequence param1CharSequence2) {
      this.mMessages.add(new Message(param1CharSequence1, param1Long, param1CharSequence2));
      if (this.mMessages.size() > 25)
        this.mMessages.remove(0); 
      return this;
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      if (Build.VERSION.SDK_INT >= 24) {
        Notification.MessagingStyle messagingStyle = (new Notification.MessagingStyle(this.mUserDisplayName)).setConversationTitle(this.mConversationTitle);
        for (Message message1 : this.mMessages) {
          Notification.MessagingStyle.Message message2 = new Notification.MessagingStyle.Message(message1.getText(), message1.getTimestamp(), message1.getSender());
          if (message1.getDataMimeType() != null)
            message2.setData(message1.getDataMimeType(), message1.getDataUri()); 
          messagingStyle.addMessage(message2);
        } 
        messagingStyle.setBuilder(param1NotificationBuilderWithBuilderAccessor.getBuilder());
        return;
      } 
      Message message = findLatestIncomingMessage();
      if (this.mConversationTitle != null) {
        param1NotificationBuilderWithBuilderAccessor.getBuilder().setContentTitle(this.mConversationTitle);
      } else if (message != null) {
        param1NotificationBuilderWithBuilderAccessor.getBuilder().setContentTitle(message.getSender());
      } 
      if (message != null) {
        CharSequence charSequence;
        Notification.Builder builder = param1NotificationBuilderWithBuilderAccessor.getBuilder();
        if (this.mConversationTitle != null) {
          charSequence = makeMessageLine(message);
        } else {
          charSequence = charSequence.getText();
        } 
        builder.setContentText(charSequence);
      } 
      if (Build.VERSION.SDK_INT >= 16) {
        boolean bool;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (this.mConversationTitle != null || hasMessagesWithoutSender()) {
          bool = true;
        } else {
          bool = false;
        } 
        for (int i = this.mMessages.size() - 1; i >= 0; i--) {
          CharSequence charSequence;
          message = (Message)this.mMessages.get(i);
          if (bool) {
            charSequence = makeMessageLine(message);
          } else {
            charSequence = charSequence.getText();
          } 
          if (i != this.mMessages.size() - 1)
            spannableStringBuilder.insert(0, "\n"); 
          spannableStringBuilder.insert(0, charSequence);
        } 
        (new Notification.BigTextStyle(param1NotificationBuilderWithBuilderAccessor.getBuilder())).setBigContentTitle(null).bigText(spannableStringBuilder);
      } 
    }
    
    public CharSequence getConversationTitle() { return this.mConversationTitle; }
    
    public List<Message> getMessages() { return this.mMessages; }
    
    public CharSequence getUserDisplayName() { return this.mUserDisplayName; }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    protected void restoreFromCompatExtras(Bundle param1Bundle) {
      this.mMessages.clear();
      this.mUserDisplayName = param1Bundle.getString("android.selfDisplayName");
      this.mConversationTitle = param1Bundle.getString("android.conversationTitle");
      Parcelable[] arrayOfParcelable = param1Bundle.getParcelableArray("android.messages");
      if (arrayOfParcelable != null)
        this.mMessages = Message.getMessagesFromBundleArray(arrayOfParcelable); 
    }
    
    public MessagingStyle setConversationTitle(CharSequence param1CharSequence) {
      this.mConversationTitle = param1CharSequence;
      return this;
    }
    
    public static final class Message {
      static final String KEY_DATA_MIME_TYPE = "type";
      
      static final String KEY_DATA_URI = "uri";
      
      static final String KEY_EXTRAS_BUNDLE = "extras";
      
      static final String KEY_SENDER = "sender";
      
      static final String KEY_TEXT = "text";
      
      static final String KEY_TIMESTAMP = "time";
      
      private String mDataMimeType;
      
      private Uri mDataUri;
      
      private Bundle mExtras = new Bundle();
      
      private final CharSequence mSender;
      
      private final CharSequence mText;
      
      private final long mTimestamp;
      
      public Message(CharSequence param2CharSequence1, long param2Long, CharSequence param2CharSequence2) {
        this.mText = param2CharSequence1;
        this.mTimestamp = param2Long;
        this.mSender = param2CharSequence2;
      }
      
      static Bundle[] getBundleArrayForMessages(List<Message> param2List) {
        Bundle[] arrayOfBundle = new Bundle[param2List.size()];
        int i = param2List.size();
        for (byte b = 0; b < i; b++)
          arrayOfBundle[b] = ((Message)param2List.get(b)).toBundle(); 
        return arrayOfBundle;
      }
      
      static Message getMessageFromBundle(Bundle param2Bundle) {
        try {
          if (param2Bundle.containsKey("text")) {
            if (!param2Bundle.containsKey("time"))
              return null; 
            Message message = new Message(param2Bundle.getCharSequence("text"), param2Bundle.getLong("time"), param2Bundle.getCharSequence("sender"));
            if (param2Bundle.containsKey("type") && param2Bundle.containsKey("uri"))
              message.setData(param2Bundle.getString("type"), (Uri)param2Bundle.getParcelable("uri")); 
            if (param2Bundle.containsKey("extras"))
              message.getExtras().putAll(param2Bundle.getBundle("extras")); 
            return message;
          } 
          return null;
        } catch (ClassCastException param2Bundle) {
          return null;
        } 
      }
      
      static List<Message> getMessagesFromBundleArray(Parcelable[] param2ArrayOfParcelable) {
        ArrayList arrayList = new ArrayList(param2ArrayOfParcelable.length);
        for (byte b = 0; b < param2ArrayOfParcelable.length; b++) {
          if (param2ArrayOfParcelable[b] instanceof Bundle) {
            Message message = getMessageFromBundle((Bundle)param2ArrayOfParcelable[b]);
            if (message != null)
              arrayList.add(message); 
          } 
        } 
        return arrayList;
      }
      
      private Bundle toBundle() {
        Bundle bundle = new Bundle();
        if (this.mText != null)
          bundle.putCharSequence("text", this.mText); 
        bundle.putLong("time", this.mTimestamp);
        if (this.mSender != null)
          bundle.putCharSequence("sender", this.mSender); 
        if (this.mDataMimeType != null)
          bundle.putString("type", this.mDataMimeType); 
        if (this.mDataUri != null)
          bundle.putParcelable("uri", this.mDataUri); 
        if (this.mExtras != null)
          bundle.putBundle("extras", this.mExtras); 
        return bundle;
      }
      
      public String getDataMimeType() { return this.mDataMimeType; }
      
      public Uri getDataUri() { return this.mDataUri; }
      
      public Bundle getExtras() { return this.mExtras; }
      
      public CharSequence getSender() { return this.mSender; }
      
      public CharSequence getText() { return this.mText; }
      
      public long getTimestamp() { return this.mTimestamp; }
      
      public Message setData(String param2String, Uri param2Uri) {
        this.mDataMimeType = param2String;
        this.mDataUri = param2Uri;
        return this;
      }
    }
  }
  
  public static final class Message {
    static final String KEY_DATA_MIME_TYPE = "type";
    
    static final String KEY_DATA_URI = "uri";
    
    static final String KEY_EXTRAS_BUNDLE = "extras";
    
    static final String KEY_SENDER = "sender";
    
    static final String KEY_TEXT = "text";
    
    static final String KEY_TIMESTAMP = "time";
    
    private String mDataMimeType;
    
    private Uri mDataUri;
    
    private Bundle mExtras = new Bundle();
    
    private final CharSequence mSender;
    
    private final CharSequence mText;
    
    private final long mTimestamp;
    
    public Message(CharSequence param1CharSequence1, long param1Long, CharSequence param1CharSequence2) {
      this.mText = param1CharSequence1;
      this.mTimestamp = param1Long;
      this.mSender = param1CharSequence2;
    }
    
    static Bundle[] getBundleArrayForMessages(List<Message> param1List) {
      Bundle[] arrayOfBundle = new Bundle[param1List.size()];
      int i = param1List.size();
      for (byte b = 0; b < i; b++)
        arrayOfBundle[b] = ((Message)param1List.get(b)).toBundle(); 
      return arrayOfBundle;
    }
    
    static Message getMessageFromBundle(Bundle param1Bundle) {
      try {
        if (param1Bundle.containsKey("text")) {
          if (!param1Bundle.containsKey("time"))
            return null; 
          Message message = new Message(param1Bundle.getCharSequence("text"), param1Bundle.getLong("time"), param1Bundle.getCharSequence("sender"));
          if (param1Bundle.containsKey("type") && param1Bundle.containsKey("uri"))
            message.setData(param1Bundle.getString("type"), (Uri)param1Bundle.getParcelable("uri")); 
          if (param1Bundle.containsKey("extras"))
            message.getExtras().putAll(param1Bundle.getBundle("extras")); 
          return message;
        } 
        return null;
      } catch (ClassCastException param1Bundle) {
        return null;
      } 
    }
    
    static List<Message> getMessagesFromBundleArray(Parcelable[] param1ArrayOfParcelable) {
      ArrayList arrayList = new ArrayList(param1ArrayOfParcelable.length);
      for (byte b = 0; b < param1ArrayOfParcelable.length; b++) {
        if (param1ArrayOfParcelable[b] instanceof Bundle) {
          Message message = getMessageFromBundle((Bundle)param1ArrayOfParcelable[b]);
          if (message != null)
            arrayList.add(message); 
        } 
      } 
      return arrayList;
    }
    
    private Bundle toBundle() {
      Bundle bundle = new Bundle();
      if (this.mText != null)
        bundle.putCharSequence("text", this.mText); 
      bundle.putLong("time", this.mTimestamp);
      if (this.mSender != null)
        bundle.putCharSequence("sender", this.mSender); 
      if (this.mDataMimeType != null)
        bundle.putString("type", this.mDataMimeType); 
      if (this.mDataUri != null)
        bundle.putParcelable("uri", this.mDataUri); 
      if (this.mExtras != null)
        bundle.putBundle("extras", this.mExtras); 
      return bundle;
    }
    
    public String getDataMimeType() { return this.mDataMimeType; }
    
    public Uri getDataUri() { return this.mDataUri; }
    
    public Bundle getExtras() { return this.mExtras; }
    
    public CharSequence getSender() { return this.mSender; }
    
    public CharSequence getText() { return this.mText; }
    
    public long getTimestamp() { return this.mTimestamp; }
    
    public Message setData(String param1String, Uri param1Uri) {
      this.mDataMimeType = param1String;
      this.mDataUri = param1Uri;
      return this;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface NotificationVisibility {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface StreamType {}
  
  public static abstract class Style {
    CharSequence mBigContentTitle;
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    protected NotificationCompat.Builder mBuilder;
    
    CharSequence mSummaryText;
    
    boolean mSummaryTextSet = false;
    
    private int calculateTopPadding() {
      Resources resources = this.mBuilder.mContext.getResources();
      int i = resources.getDimensionPixelSize(R.dimen.notification_top_pad);
      int j = resources.getDimensionPixelSize(R.dimen.notification_top_pad_large_text);
      float f = (constrain((resources.getConfiguration()).fontScale, 1.0F, 1.3F) - 1.0F) / 0.29999995F;
      return Math.round((1.0F - f) * i + f * j);
    }
    
    private static float constrain(float param1Float1, float param1Float2, float param1Float3) {
      if (param1Float1 < param1Float2)
        return param1Float2; 
      param1Float2 = param1Float1;
      if (param1Float1 > param1Float3)
        param1Float2 = param1Float3; 
      return param1Float2;
    }
    
    private Bitmap createColoredBitmap(int param1Int1, int param1Int2, int param1Int3) {
      Drawable drawable = this.mBuilder.mContext.getResources().getDrawable(param1Int1);
      if (param1Int3 == 0) {
        param1Int1 = drawable.getIntrinsicWidth();
      } else {
        param1Int1 = param1Int3;
      } 
      int i = param1Int3;
      if (param1Int3 == 0)
        i = drawable.getIntrinsicHeight(); 
      Bitmap bitmap = Bitmap.createBitmap(param1Int1, i, Bitmap.Config.ARGB_8888);
      drawable.setBounds(0, 0, param1Int1, i);
      if (param1Int2 != 0)
        drawable.mutate().setColorFilter(new PorterDuffColorFilter(param1Int2, PorterDuff.Mode.SRC_IN)); 
      drawable.draw(new Canvas(bitmap));
      return bitmap;
    }
    
    private Bitmap createIconWithBackground(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      int j = R.drawable.notification_icon_background;
      int i = param1Int4;
      if (param1Int4 == 0)
        i = 0; 
      Bitmap bitmap = createColoredBitmap(j, i, param1Int2);
      Canvas canvas = new Canvas(bitmap);
      Drawable drawable = this.mBuilder.mContext.getResources().getDrawable(param1Int1).mutate();
      drawable.setFilterBitmap(true);
      param1Int1 = (param1Int2 - param1Int3) / 2;
      param1Int2 = param1Int3 + param1Int1;
      drawable.setBounds(param1Int1, param1Int1, param1Int2, param1Int2);
      drawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_ATOP));
      drawable.draw(canvas);
      return bitmap;
    }
    
    private void hideNormalContent(RemoteViews param1RemoteViews) {
      param1RemoteViews.setViewVisibility(R.id.title, 8);
      param1RemoteViews.setViewVisibility(R.id.text2, 8);
      param1RemoteViews.setViewVisibility(R.id.text, 8);
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void addCompatExtras(Bundle param1Bundle) {}
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {}
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews applyStandardTemplate(boolean param1Boolean1, int param1Int, boolean param1Boolean2) { // Byte code:
      //   0: aload_0
      //   1: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   4: getfield mContext : Landroid/content/Context;
      //   7: invokevirtual getResources : ()Landroid/content/res/Resources;
      //   10: astore #9
      //   12: new android/widget/RemoteViews
      //   15: dup
      //   16: aload_0
      //   17: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   20: getfield mContext : Landroid/content/Context;
      //   23: invokevirtual getPackageName : ()Ljava/lang/String;
      //   26: iload_2
      //   27: invokespecial <init> : (Ljava/lang/String;I)V
      //   30: astore #10
      //   32: aload_0
      //   33: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   36: invokevirtual getPriority : ()I
      //   39: istore_2
      //   40: iconst_1
      //   41: istore #8
      //   43: iconst_0
      //   44: istore #7
      //   46: iload_2
      //   47: iconst_m1
      //   48: if_icmpge -> 56
      //   51: iconst_1
      //   52: istore_2
      //   53: goto -> 58
      //   56: iconst_0
      //   57: istore_2
      //   58: getstatic android/os/Build$VERSION.SDK_INT : I
      //   61: bipush #16
      //   63: if_icmplt -> 133
      //   66: getstatic android/os/Build$VERSION.SDK_INT : I
      //   69: bipush #21
      //   71: if_icmpge -> 133
      //   74: iload_2
      //   75: ifeq -> 107
      //   78: aload #10
      //   80: getstatic android/support/compat/R$id.notification_background : I
      //   83: ldc 'setBackgroundResource'
      //   85: getstatic android/support/compat/R$drawable.notification_bg_low : I
      //   88: invokevirtual setInt : (ILjava/lang/String;I)V
      //   91: aload #10
      //   93: getstatic android/support/compat/R$id.icon : I
      //   96: ldc 'setBackgroundResource'
      //   98: getstatic android/support/compat/R$drawable.notification_template_icon_low_bg : I
      //   101: invokevirtual setInt : (ILjava/lang/String;I)V
      //   104: goto -> 133
      //   107: aload #10
      //   109: getstatic android/support/compat/R$id.notification_background : I
      //   112: ldc 'setBackgroundResource'
      //   114: getstatic android/support/compat/R$drawable.notification_bg : I
      //   117: invokevirtual setInt : (ILjava/lang/String;I)V
      //   120: aload #10
      //   122: getstatic android/support/compat/R$id.icon : I
      //   125: ldc 'setBackgroundResource'
      //   127: getstatic android/support/compat/R$drawable.notification_template_icon_bg : I
      //   130: invokevirtual setInt : (ILjava/lang/String;I)V
      //   133: aload_0
      //   134: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   137: getfield mLargeIcon : Landroid/graphics/Bitmap;
      //   140: ifnull -> 310
      //   143: getstatic android/os/Build$VERSION.SDK_INT : I
      //   146: bipush #16
      //   148: if_icmplt -> 178
      //   151: aload #10
      //   153: getstatic android/support/compat/R$id.icon : I
      //   156: iconst_0
      //   157: invokevirtual setViewVisibility : (II)V
      //   160: aload #10
      //   162: getstatic android/support/compat/R$id.icon : I
      //   165: aload_0
      //   166: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   169: getfield mLargeIcon : Landroid/graphics/Bitmap;
      //   172: invokevirtual setImageViewBitmap : (ILandroid/graphics/Bitmap;)V
      //   175: goto -> 188
      //   178: aload #10
      //   180: getstatic android/support/compat/R$id.icon : I
      //   183: bipush #8
      //   185: invokevirtual setViewVisibility : (II)V
      //   188: iload_1
      //   189: ifeq -> 438
      //   192: aload_0
      //   193: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   196: getfield mNotification : Landroid/app/Notification;
      //   199: getfield icon : I
      //   202: ifeq -> 438
      //   205: aload #9
      //   207: getstatic android/support/compat/R$dimen.notification_right_icon_size : I
      //   210: invokevirtual getDimensionPixelSize : (I)I
      //   213: istore_2
      //   214: aload #9
      //   216: getstatic android/support/compat/R$dimen.notification_small_icon_background_padding : I
      //   219: invokevirtual getDimensionPixelSize : (I)I
      //   222: istore #5
      //   224: getstatic android/os/Build$VERSION.SDK_INT : I
      //   227: bipush #21
      //   229: if_icmplt -> 275
      //   232: aload_0
      //   233: aload_0
      //   234: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   237: getfield mNotification : Landroid/app/Notification;
      //   240: getfield icon : I
      //   243: iload_2
      //   244: iload_2
      //   245: iload #5
      //   247: iconst_2
      //   248: imul
      //   249: isub
      //   250: aload_0
      //   251: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   254: invokevirtual getColor : ()I
      //   257: invokespecial createIconWithBackground : (IIII)Landroid/graphics/Bitmap;
      //   260: astore #11
      //   262: aload #10
      //   264: getstatic android/support/compat/R$id.right_icon : I
      //   267: aload #11
      //   269: invokevirtual setImageViewBitmap : (ILandroid/graphics/Bitmap;)V
      //   272: goto -> 298
      //   275: aload #10
      //   277: getstatic android/support/compat/R$id.right_icon : I
      //   280: aload_0
      //   281: aload_0
      //   282: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   285: getfield mNotification : Landroid/app/Notification;
      //   288: getfield icon : I
      //   291: iconst_m1
      //   292: invokevirtual createColoredBitmap : (II)Landroid/graphics/Bitmap;
      //   295: invokevirtual setImageViewBitmap : (ILandroid/graphics/Bitmap;)V
      //   298: aload #10
      //   300: getstatic android/support/compat/R$id.right_icon : I
      //   303: iconst_0
      //   304: invokevirtual setViewVisibility : (II)V
      //   307: goto -> 438
      //   310: iload_1
      //   311: ifeq -> 438
      //   314: aload_0
      //   315: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   318: getfield mNotification : Landroid/app/Notification;
      //   321: getfield icon : I
      //   324: ifeq -> 438
      //   327: aload #10
      //   329: getstatic android/support/compat/R$id.icon : I
      //   332: iconst_0
      //   333: invokevirtual setViewVisibility : (II)V
      //   336: getstatic android/os/Build$VERSION.SDK_INT : I
      //   339: bipush #21
      //   341: if_icmplt -> 415
      //   344: aload #9
      //   346: getstatic android/support/compat/R$dimen.notification_large_icon_width : I
      //   349: invokevirtual getDimensionPixelSize : (I)I
      //   352: istore_2
      //   353: aload #9
      //   355: getstatic android/support/compat/R$dimen.notification_big_circle_margin : I
      //   358: invokevirtual getDimensionPixelSize : (I)I
      //   361: istore #5
      //   363: aload #9
      //   365: getstatic android/support/compat/R$dimen.notification_small_icon_size_as_large : I
      //   368: invokevirtual getDimensionPixelSize : (I)I
      //   371: istore #6
      //   373: aload_0
      //   374: aload_0
      //   375: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   378: getfield mNotification : Landroid/app/Notification;
      //   381: getfield icon : I
      //   384: iload_2
      //   385: iload #5
      //   387: isub
      //   388: iload #6
      //   390: aload_0
      //   391: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   394: invokevirtual getColor : ()I
      //   397: invokespecial createIconWithBackground : (IIII)Landroid/graphics/Bitmap;
      //   400: astore #11
      //   402: aload #10
      //   404: getstatic android/support/compat/R$id.icon : I
      //   407: aload #11
      //   409: invokevirtual setImageViewBitmap : (ILandroid/graphics/Bitmap;)V
      //   412: goto -> 438
      //   415: aload #10
      //   417: getstatic android/support/compat/R$id.icon : I
      //   420: aload_0
      //   421: aload_0
      //   422: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   425: getfield mNotification : Landroid/app/Notification;
      //   428: getfield icon : I
      //   431: iconst_m1
      //   432: invokevirtual createColoredBitmap : (II)Landroid/graphics/Bitmap;
      //   435: invokevirtual setImageViewBitmap : (ILandroid/graphics/Bitmap;)V
      //   438: aload_0
      //   439: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   442: getfield mContentTitle : Ljava/lang/CharSequence;
      //   445: ifnull -> 463
      //   448: aload #10
      //   450: getstatic android/support/compat/R$id.title : I
      //   453: aload_0
      //   454: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   457: getfield mContentTitle : Ljava/lang/CharSequence;
      //   460: invokevirtual setTextViewText : (ILjava/lang/CharSequence;)V
      //   463: aload_0
      //   464: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   467: getfield mContentText : Ljava/lang/CharSequence;
      //   470: ifnull -> 493
      //   473: aload #10
      //   475: getstatic android/support/compat/R$id.text : I
      //   478: aload_0
      //   479: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   482: getfield mContentText : Ljava/lang/CharSequence;
      //   485: invokevirtual setTextViewText : (ILjava/lang/CharSequence;)V
      //   488: iconst_1
      //   489: istore_2
      //   490: goto -> 495
      //   493: iconst_0
      //   494: istore_2
      //   495: getstatic android/os/Build$VERSION.SDK_INT : I
      //   498: bipush #21
      //   500: if_icmpge -> 519
      //   503: aload_0
      //   504: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   507: getfield mLargeIcon : Landroid/graphics/Bitmap;
      //   510: ifnull -> 519
      //   513: iconst_1
      //   514: istore #5
      //   516: goto -> 522
      //   519: iconst_0
      //   520: istore #5
      //   522: aload_0
      //   523: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   526: getfield mContentInfo : Ljava/lang/CharSequence;
      //   529: ifnull -> 564
      //   532: aload #10
      //   534: getstatic android/support/compat/R$id.info : I
      //   537: aload_0
      //   538: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   541: getfield mContentInfo : Ljava/lang/CharSequence;
      //   544: invokevirtual setTextViewText : (ILjava/lang/CharSequence;)V
      //   547: aload #10
      //   549: getstatic android/support/compat/R$id.info : I
      //   552: iconst_0
      //   553: invokevirtual setViewVisibility : (II)V
      //   556: iconst_1
      //   557: istore_2
      //   558: iconst_1
      //   559: istore #5
      //   561: goto -> 671
      //   564: aload_0
      //   565: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   568: getfield mNumber : I
      //   571: ifle -> 651
      //   574: aload #9
      //   576: getstatic android/support/compat/R$integer.status_bar_notification_info_maxnum : I
      //   579: invokevirtual getInteger : (I)I
      //   582: istore_2
      //   583: aload_0
      //   584: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   587: getfield mNumber : I
      //   590: iload_2
      //   591: if_icmple -> 613
      //   594: aload #10
      //   596: getstatic android/support/compat/R$id.info : I
      //   599: aload #9
      //   601: getstatic android/support/compat/R$string.status_bar_notification_info_overflow : I
      //   604: invokevirtual getString : (I)Ljava/lang/String;
      //   607: invokevirtual setTextViewText : (ILjava/lang/CharSequence;)V
      //   610: goto -> 639
      //   613: invokestatic getIntegerInstance : ()Ljava/text/NumberFormat;
      //   616: astore #11
      //   618: aload #10
      //   620: getstatic android/support/compat/R$id.info : I
      //   623: aload #11
      //   625: aload_0
      //   626: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   629: getfield mNumber : I
      //   632: i2l
      //   633: invokevirtual format : (J)Ljava/lang/String;
      //   636: invokevirtual setTextViewText : (ILjava/lang/CharSequence;)V
      //   639: aload #10
      //   641: getstatic android/support/compat/R$id.info : I
      //   644: iconst_0
      //   645: invokevirtual setViewVisibility : (II)V
      //   648: goto -> 556
      //   651: aload #10
      //   653: getstatic android/support/compat/R$id.info : I
      //   656: bipush #8
      //   658: invokevirtual setViewVisibility : (II)V
      //   661: iload #5
      //   663: istore #6
      //   665: iload_2
      //   666: istore #5
      //   668: iload #6
      //   670: istore_2
      //   671: aload_0
      //   672: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   675: getfield mSubText : Ljava/lang/CharSequence;
      //   678: ifnull -> 754
      //   681: getstatic android/os/Build$VERSION.SDK_INT : I
      //   684: bipush #16
      //   686: if_icmplt -> 754
      //   689: aload #10
      //   691: getstatic android/support/compat/R$id.text : I
      //   694: aload_0
      //   695: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   698: getfield mSubText : Ljava/lang/CharSequence;
      //   701: invokevirtual setTextViewText : (ILjava/lang/CharSequence;)V
      //   704: aload_0
      //   705: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   708: getfield mContentText : Ljava/lang/CharSequence;
      //   711: ifnull -> 744
      //   714: aload #10
      //   716: getstatic android/support/compat/R$id.text2 : I
      //   719: aload_0
      //   720: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   723: getfield mContentText : Ljava/lang/CharSequence;
      //   726: invokevirtual setTextViewText : (ILjava/lang/CharSequence;)V
      //   729: aload #10
      //   731: getstatic android/support/compat/R$id.text2 : I
      //   734: iconst_0
      //   735: invokevirtual setViewVisibility : (II)V
      //   738: iconst_1
      //   739: istore #6
      //   741: goto -> 757
      //   744: aload #10
      //   746: getstatic android/support/compat/R$id.text2 : I
      //   749: bipush #8
      //   751: invokevirtual setViewVisibility : (II)V
      //   754: iconst_0
      //   755: istore #6
      //   757: iload #6
      //   759: ifeq -> 808
      //   762: getstatic android/os/Build$VERSION.SDK_INT : I
      //   765: bipush #16
      //   767: if_icmplt -> 808
      //   770: iload_3
      //   771: ifeq -> 796
      //   774: aload #9
      //   776: getstatic android/support/compat/R$dimen.notification_subtext_size : I
      //   779: invokevirtual getDimensionPixelSize : (I)I
      //   782: i2f
      //   783: fstore #4
      //   785: aload #10
      //   787: getstatic android/support/compat/R$id.text : I
      //   790: iconst_0
      //   791: fload #4
      //   793: invokevirtual setTextViewTextSize : (IIF)V
      //   796: aload #10
      //   798: getstatic android/support/compat/R$id.line1 : I
      //   801: iconst_0
      //   802: iconst_0
      //   803: iconst_0
      //   804: iconst_0
      //   805: invokevirtual setViewPadding : (IIIII)V
      //   808: aload_0
      //   809: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   812: invokevirtual getWhenIfShowing : ()J
      //   815: lconst_0
      //   816: lcmp
      //   817: ifeq -> 924
      //   820: aload_0
      //   821: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   824: getfield mUseChronometer : Z
      //   827: ifeq -> 891
      //   830: getstatic android/os/Build$VERSION.SDK_INT : I
      //   833: bipush #16
      //   835: if_icmplt -> 891
      //   838: aload #10
      //   840: getstatic android/support/compat/R$id.chronometer : I
      //   843: iconst_0
      //   844: invokevirtual setViewVisibility : (II)V
      //   847: aload #10
      //   849: getstatic android/support/compat/R$id.chronometer : I
      //   852: ldc_w 'setBase'
      //   855: aload_0
      //   856: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   859: invokevirtual getWhenIfShowing : ()J
      //   862: invokestatic elapsedRealtime : ()J
      //   865: invokestatic currentTimeMillis : ()J
      //   868: lsub
      //   869: ladd
      //   870: invokevirtual setLong : (ILjava/lang/String;J)V
      //   873: aload #10
      //   875: getstatic android/support/compat/R$id.chronometer : I
      //   878: ldc_w 'setStarted'
      //   881: iconst_1
      //   882: invokevirtual setBoolean : (ILjava/lang/String;Z)V
      //   885: iload #8
      //   887: istore_2
      //   888: goto -> 924
      //   891: aload #10
      //   893: getstatic android/support/compat/R$id.time : I
      //   896: iconst_0
      //   897: invokevirtual setViewVisibility : (II)V
      //   900: aload #10
      //   902: getstatic android/support/compat/R$id.time : I
      //   905: ldc_w 'setTime'
      //   908: aload_0
      //   909: getfield mBuilder : Landroid/support/v4/app/NotificationCompat$Builder;
      //   912: invokevirtual getWhenIfShowing : ()J
      //   915: invokevirtual setLong : (ILjava/lang/String;J)V
      //   918: iload #8
      //   920: istore_2
      //   921: goto -> 924
      //   924: getstatic android/support/compat/R$id.right_side : I
      //   927: istore #6
      //   929: iload_2
      //   930: ifeq -> 938
      //   933: iconst_0
      //   934: istore_2
      //   935: goto -> 941
      //   938: bipush #8
      //   940: istore_2
      //   941: aload #10
      //   943: iload #6
      //   945: iload_2
      //   946: invokevirtual setViewVisibility : (II)V
      //   949: getstatic android/support/compat/R$id.line3 : I
      //   952: istore #6
      //   954: iload #5
      //   956: ifeq -> 965
      //   959: iload #7
      //   961: istore_2
      //   962: goto -> 968
      //   965: bipush #8
      //   967: istore_2
      //   968: aload #10
      //   970: iload #6
      //   972: iload_2
      //   973: invokevirtual setViewVisibility : (II)V
      //   976: aload #10
      //   978: areturn }
    
    public Notification build() { return (this.mBuilder != null) ? this.mBuilder.build() : null; }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void buildIntoRemoteViews(RemoteViews param1RemoteViews1, RemoteViews param1RemoteViews2) {
      hideNormalContent(param1RemoteViews1);
      param1RemoteViews1.removeAllViews(R.id.notification_main_column);
      param1RemoteViews1.addView(R.id.notification_main_column, param1RemoteViews2.clone());
      param1RemoteViews1.setViewVisibility(R.id.notification_main_column, 0);
      if (Build.VERSION.SDK_INT >= 21)
        param1RemoteViews1.setViewPadding(R.id.notification_main_column_container, 0, calculateTopPadding(), 0, 0); 
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public Bitmap createColoredBitmap(int param1Int1, int param1Int2) { return createColoredBitmap(param1Int1, param1Int2, 0); }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) { return null; }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) { return null; }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeHeadsUpContentView(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) { return null; }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    protected void restoreFromCompatExtras(Bundle param1Bundle) {}
    
    public void setBuilder(NotificationCompat.Builder param1Builder) {
      if (this.mBuilder != param1Builder) {
        this.mBuilder = param1Builder;
        if (this.mBuilder != null)
          this.mBuilder.setStyle(this); 
      } 
    }
  }
  
  public static final class WearableExtender implements Extender {
    private static final int DEFAULT_CONTENT_ICON_GRAVITY = 8388613;
    
    private static final int DEFAULT_FLAGS = 1;
    
    private static final int DEFAULT_GRAVITY = 80;
    
    private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
    
    private static final int FLAG_BIG_PICTURE_AMBIENT = 32;
    
    private static final int FLAG_CONTENT_INTENT_AVAILABLE_OFFLINE = 1;
    
    private static final int FLAG_HINT_AVOID_BACKGROUND_CLIPPING = 16;
    
    private static final int FLAG_HINT_CONTENT_INTENT_LAUNCHES_ACTIVITY = 64;
    
    private static final int FLAG_HINT_HIDE_ICON = 2;
    
    private static final int FLAG_HINT_SHOW_BACKGROUND_ONLY = 4;
    
    private static final int FLAG_START_SCROLL_BOTTOM = 8;
    
    private static final String KEY_ACTIONS = "actions";
    
    private static final String KEY_BACKGROUND = "background";
    
    private static final String KEY_BRIDGE_TAG = "bridgeTag";
    
    private static final String KEY_CONTENT_ACTION_INDEX = "contentActionIndex";
    
    private static final String KEY_CONTENT_ICON = "contentIcon";
    
    private static final String KEY_CONTENT_ICON_GRAVITY = "contentIconGravity";
    
    private static final String KEY_CUSTOM_CONTENT_HEIGHT = "customContentHeight";
    
    private static final String KEY_CUSTOM_SIZE_PRESET = "customSizePreset";
    
    private static final String KEY_DISMISSAL_ID = "dismissalId";
    
    private static final String KEY_DISPLAY_INTENT = "displayIntent";
    
    private static final String KEY_FLAGS = "flags";
    
    private static final String KEY_GRAVITY = "gravity";
    
    private static final String KEY_HINT_SCREEN_TIMEOUT = "hintScreenTimeout";
    
    private static final String KEY_PAGES = "pages";
    
    public static final int SCREEN_TIMEOUT_LONG = -1;
    
    public static final int SCREEN_TIMEOUT_SHORT = 0;
    
    public static final int SIZE_DEFAULT = 0;
    
    public static final int SIZE_FULL_SCREEN = 5;
    
    public static final int SIZE_LARGE = 4;
    
    public static final int SIZE_MEDIUM = 3;
    
    public static final int SIZE_SMALL = 2;
    
    public static final int SIZE_XSMALL = 1;
    
    public static final int UNSET_ACTION_INDEX = -1;
    
    private ArrayList<NotificationCompat.Action> mActions = new ArrayList();
    
    private Bitmap mBackground;
    
    private String mBridgeTag;
    
    private int mContentActionIndex = -1;
    
    private int mContentIcon;
    
    private int mContentIconGravity = 8388613;
    
    private int mCustomContentHeight;
    
    private int mCustomSizePreset = 0;
    
    private String mDismissalId;
    
    private PendingIntent mDisplayIntent;
    
    private int mFlags = 1;
    
    private int mGravity = 80;
    
    private int mHintScreenTimeout;
    
    private ArrayList<Notification> mPages = new ArrayList();
    
    public WearableExtender() {}
    
    public WearableExtender(Notification param1Notification) {
      Bundle bundle = NotificationCompat.getExtras(param1Notification);
      if (bundle != null) {
        bundle = bundle.getBundle("android.wearable.EXTENSIONS");
      } else {
        bundle = null;
      } 
      if (bundle != null) {
        ArrayList arrayList = bundle.getParcelableArrayList("actions");
        if (Build.VERSION.SDK_INT >= 16 && arrayList != null) {
          NotificationCompat.Action[] arrayOfAction = new NotificationCompat.Action[arrayList.size()];
          for (byte b = 0; b < arrayOfAction.length; b++) {
            if (Build.VERSION.SDK_INT >= 20) {
              arrayOfAction[b] = NotificationCompat.getActionCompatFromAction((Notification.Action)arrayList.get(b));
            } else if (Build.VERSION.SDK_INT >= 16) {
              arrayOfAction[b] = NotificationCompatJellybean.getActionFromBundle((Bundle)arrayList.get(b));
            } 
          } 
          Collections.addAll(this.mActions, (Action[])arrayOfAction);
        } 
        this.mFlags = bundle.getInt("flags", 1);
        this.mDisplayIntent = (PendingIntent)bundle.getParcelable("displayIntent");
        Notification[] arrayOfNotification = NotificationCompat.getNotificationArrayFromBundle(bundle, "pages");
        if (arrayOfNotification != null)
          Collections.addAll(this.mPages, arrayOfNotification); 
        this.mBackground = (Bitmap)bundle.getParcelable("background");
        this.mContentIcon = bundle.getInt("contentIcon");
        this.mContentIconGravity = bundle.getInt("contentIconGravity", 8388613);
        this.mContentActionIndex = bundle.getInt("contentActionIndex", -1);
        this.mCustomSizePreset = bundle.getInt("customSizePreset", 0);
        this.mCustomContentHeight = bundle.getInt("customContentHeight");
        this.mGravity = bundle.getInt("gravity", 80);
        this.mHintScreenTimeout = bundle.getInt("hintScreenTimeout");
        this.mDismissalId = bundle.getString("dismissalId");
        this.mBridgeTag = bundle.getString("bridgeTag");
      } 
    }
    
    @RequiresApi(20)
    private static Notification.Action getActionFromActionCompat(NotificationCompat.Action param1Action) {
      Bundle bundle;
      Notification.Action.Builder builder = new Notification.Action.Builder(param1Action.getIcon(), param1Action.getTitle(), param1Action.getActionIntent());
      if (param1Action.getExtras() != null) {
        bundle = new Bundle(param1Action.getExtras());
      } else {
        bundle = new Bundle();
      } 
      bundle.putBoolean("android.support.allowGeneratedReplies", param1Action.getAllowGeneratedReplies());
      if (Build.VERSION.SDK_INT >= 24)
        builder.setAllowGeneratedReplies(param1Action.getAllowGeneratedReplies()); 
      builder.addExtras(bundle);
      RemoteInput[] arrayOfRemoteInput = param1Action.getRemoteInputs();
      if (arrayOfRemoteInput != null) {
        RemoteInput[] arrayOfRemoteInput1 = RemoteInput.fromCompat(arrayOfRemoteInput);
        int i = arrayOfRemoteInput1.length;
        for (byte b = 0; b < i; b++)
          builder.addRemoteInput(arrayOfRemoteInput1[b]); 
      } 
      return builder.build();
    }
    
    private void setFlag(int param1Int, boolean param1Boolean) {
      if (param1Boolean) {
        this.mFlags = param1Int | this.mFlags;
        return;
      } 
      this.mFlags = (param1Int ^ 0xFFFFFFFF) & this.mFlags;
    }
    
    public WearableExtender addAction(NotificationCompat.Action param1Action) {
      this.mActions.add(param1Action);
      return this;
    }
    
    public WearableExtender addActions(List<NotificationCompat.Action> param1List) {
      this.mActions.addAll(param1List);
      return this;
    }
    
    public WearableExtender addPage(Notification param1Notification) {
      this.mPages.add(param1Notification);
      return this;
    }
    
    public WearableExtender addPages(List<Notification> param1List) {
      this.mPages.addAll(param1List);
      return this;
    }
    
    public WearableExtender clearActions() {
      this.mActions.clear();
      return this;
    }
    
    public WearableExtender clearPages() {
      this.mPages.clear();
      return this;
    }
    
    public WearableExtender clone() {
      WearableExtender wearableExtender = new WearableExtender();
      wearableExtender.mActions = new ArrayList(this.mActions);
      wearableExtender.mFlags = this.mFlags;
      wearableExtender.mDisplayIntent = this.mDisplayIntent;
      wearableExtender.mPages = new ArrayList(this.mPages);
      wearableExtender.mBackground = this.mBackground;
      wearableExtender.mContentIcon = this.mContentIcon;
      wearableExtender.mContentIconGravity = this.mContentIconGravity;
      wearableExtender.mContentActionIndex = this.mContentActionIndex;
      wearableExtender.mCustomSizePreset = this.mCustomSizePreset;
      wearableExtender.mCustomContentHeight = this.mCustomContentHeight;
      wearableExtender.mGravity = this.mGravity;
      wearableExtender.mHintScreenTimeout = this.mHintScreenTimeout;
      wearableExtender.mDismissalId = this.mDismissalId;
      wearableExtender.mBridgeTag = this.mBridgeTag;
      return wearableExtender;
    }
    
    public NotificationCompat.Builder extend(NotificationCompat.Builder param1Builder) {
      Bundle bundle = new Bundle();
      if (!this.mActions.isEmpty())
        if (Build.VERSION.SDK_INT >= 16) {
          ArrayList arrayList = new ArrayList(this.mActions.size());
          for (NotificationCompat.Action action : this.mActions) {
            if (Build.VERSION.SDK_INT >= 20) {
              arrayList.add(getActionFromActionCompat(action));
              continue;
            } 
            if (Build.VERSION.SDK_INT >= 16)
              arrayList.add(NotificationCompatJellybean.getBundleForAction(action)); 
          } 
          bundle.putParcelableArrayList("actions", arrayList);
        } else {
          bundle.putParcelableArrayList("actions", null);
        }  
      if (this.mFlags != 1)
        bundle.putInt("flags", this.mFlags); 
      if (this.mDisplayIntent != null)
        bundle.putParcelable("displayIntent", this.mDisplayIntent); 
      if (!this.mPages.isEmpty())
        bundle.putParcelableArray("pages", (Parcelable[])this.mPages.toArray(new Notification[this.mPages.size()])); 
      if (this.mBackground != null)
        bundle.putParcelable("background", this.mBackground); 
      if (this.mContentIcon != 0)
        bundle.putInt("contentIcon", this.mContentIcon); 
      if (this.mContentIconGravity != 8388613)
        bundle.putInt("contentIconGravity", this.mContentIconGravity); 
      if (this.mContentActionIndex != -1)
        bundle.putInt("contentActionIndex", this.mContentActionIndex); 
      if (this.mCustomSizePreset != 0)
        bundle.putInt("customSizePreset", this.mCustomSizePreset); 
      if (this.mCustomContentHeight != 0)
        bundle.putInt("customContentHeight", this.mCustomContentHeight); 
      if (this.mGravity != 80)
        bundle.putInt("gravity", this.mGravity); 
      if (this.mHintScreenTimeout != 0)
        bundle.putInt("hintScreenTimeout", this.mHintScreenTimeout); 
      if (this.mDismissalId != null)
        bundle.putString("dismissalId", this.mDismissalId); 
      if (this.mBridgeTag != null)
        bundle.putString("bridgeTag", this.mBridgeTag); 
      param1Builder.getExtras().putBundle("android.wearable.EXTENSIONS", bundle);
      return param1Builder;
    }
    
    public List<NotificationCompat.Action> getActions() { return this.mActions; }
    
    public Bitmap getBackground() { return this.mBackground; }
    
    public String getBridgeTag() { return this.mBridgeTag; }
    
    public int getContentAction() { return this.mContentActionIndex; }
    
    public int getContentIcon() { return this.mContentIcon; }
    
    public int getContentIconGravity() { return this.mContentIconGravity; }
    
    public boolean getContentIntentAvailableOffline() { return ((this.mFlags & true) != 0); }
    
    public int getCustomContentHeight() { return this.mCustomContentHeight; }
    
    public int getCustomSizePreset() { return this.mCustomSizePreset; }
    
    public String getDismissalId() { return this.mDismissalId; }
    
    public PendingIntent getDisplayIntent() { return this.mDisplayIntent; }
    
    public int getGravity() { return this.mGravity; }
    
    public boolean getHintAmbientBigPicture() { return ((this.mFlags & 0x20) != 0); }
    
    public boolean getHintAvoidBackgroundClipping() { return ((this.mFlags & 0x10) != 0); }
    
    public boolean getHintContentIntentLaunchesActivity() { return ((this.mFlags & 0x40) != 0); }
    
    public boolean getHintHideIcon() { return ((this.mFlags & 0x2) != 0); }
    
    public int getHintScreenTimeout() { return this.mHintScreenTimeout; }
    
    public boolean getHintShowBackgroundOnly() { return ((this.mFlags & 0x4) != 0); }
    
    public List<Notification> getPages() { return this.mPages; }
    
    public boolean getStartScrollBottom() { return ((this.mFlags & 0x8) != 0); }
    
    public WearableExtender setBackground(Bitmap param1Bitmap) {
      this.mBackground = param1Bitmap;
      return this;
    }
    
    public WearableExtender setBridgeTag(String param1String) {
      this.mBridgeTag = param1String;
      return this;
    }
    
    public WearableExtender setContentAction(int param1Int) {
      this.mContentActionIndex = param1Int;
      return this;
    }
    
    public WearableExtender setContentIcon(int param1Int) {
      this.mContentIcon = param1Int;
      return this;
    }
    
    public WearableExtender setContentIconGravity(int param1Int) {
      this.mContentIconGravity = param1Int;
      return this;
    }
    
    public WearableExtender setContentIntentAvailableOffline(boolean param1Boolean) {
      setFlag(1, param1Boolean);
      return this;
    }
    
    public WearableExtender setCustomContentHeight(int param1Int) {
      this.mCustomContentHeight = param1Int;
      return this;
    }
    
    public WearableExtender setCustomSizePreset(int param1Int) {
      this.mCustomSizePreset = param1Int;
      return this;
    }
    
    public WearableExtender setDismissalId(String param1String) {
      this.mDismissalId = param1String;
      return this;
    }
    
    public WearableExtender setDisplayIntent(PendingIntent param1PendingIntent) {
      this.mDisplayIntent = param1PendingIntent;
      return this;
    }
    
    public WearableExtender setGravity(int param1Int) {
      this.mGravity = param1Int;
      return this;
    }
    
    public WearableExtender setHintAmbientBigPicture(boolean param1Boolean) {
      setFlag(32, param1Boolean);
      return this;
    }
    
    public WearableExtender setHintAvoidBackgroundClipping(boolean param1Boolean) {
      setFlag(16, param1Boolean);
      return this;
    }
    
    public WearableExtender setHintContentIntentLaunchesActivity(boolean param1Boolean) {
      setFlag(64, param1Boolean);
      return this;
    }
    
    public WearableExtender setHintHideIcon(boolean param1Boolean) {
      setFlag(2, param1Boolean);
      return this;
    }
    
    public WearableExtender setHintScreenTimeout(int param1Int) {
      this.mHintScreenTimeout = param1Int;
      return this;
    }
    
    public WearableExtender setHintShowBackgroundOnly(boolean param1Boolean) {
      setFlag(4, param1Boolean);
      return this;
    }
    
    public WearableExtender setStartScrollBottom(boolean param1Boolean) {
      setFlag(8, param1Boolean);
      return this;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/NotificationCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */