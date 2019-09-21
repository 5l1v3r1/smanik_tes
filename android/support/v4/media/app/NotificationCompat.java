package android.support.v4.media.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.mediacompat.R;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;

public class NotificationCompat {
  public static class DecoratedMediaCustomViewStyle extends MediaStyle {
    private void setBackgroundColor(RemoteViews param1RemoteViews) {
      int i;
      if (this.mBuilder.getColor() != 0) {
        i = this.mBuilder.getColor();
      } else {
        i = this.mBuilder.mContext.getResources().getColor(R.color.notification_material_background_media_default_color);
      } 
      param1RemoteViews.setInt(R.id.status_bar_latest_event_content, "setBackgroundColor", i);
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      if (Build.VERSION.SDK_INT >= 24) {
        param1NotificationBuilderWithBuilderAccessor.getBuilder().setStyle(fillInMediaStyle(new Notification.DecoratedMediaCustomViewStyle()));
        return;
      } 
      super.apply(param1NotificationBuilderWithBuilderAccessor);
    }
    
    int getBigContentViewLayoutResource(int param1Int) { return (param1Int <= 3) ? R.layout.notification_template_big_media_narrow_custom : R.layout.notification_template_big_media_custom; }
    
    int getContentViewLayoutResource() { return (this.mBuilder.getContentView() != null) ? R.layout.notification_template_media_custom : super.getContentViewLayoutResource(); }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      RemoteViews remoteViews1;
      if (Build.VERSION.SDK_INT >= 24)
        return null; 
      if (this.mBuilder.getBigContentView() != null) {
        remoteViews1 = this.mBuilder.getBigContentView();
      } else {
        remoteViews1 = this.mBuilder.getContentView();
      } 
      if (remoteViews1 == null)
        return null; 
      RemoteViews remoteViews2 = generateBigContentView();
      buildIntoRemoteViews(remoteViews2, remoteViews1);
      if (Build.VERSION.SDK_INT >= 21)
        setBackgroundColor(remoteViews2); 
      return remoteViews2;
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      boolean bool1;
      if (Build.VERSION.SDK_INT >= 24)
        return null; 
      RemoteViews remoteViews = this.mBuilder.getContentView();
      boolean bool2 = false;
      if (remoteViews != null) {
        bool1 = true;
      } else {
        bool1 = false;
      } 
      if (Build.VERSION.SDK_INT >= 21) {
        if (bool1 || this.mBuilder.getBigContentView() != null)
          bool2 = true; 
        if (bool2) {
          remoteViews = generateContentView();
          if (bool1)
            buildIntoRemoteViews(remoteViews, this.mBuilder.getContentView()); 
          setBackgroundColor(remoteViews);
          return remoteViews;
        } 
      } else {
        remoteViews = generateContentView();
        if (bool1) {
          buildIntoRemoteViews(remoteViews, this.mBuilder.getContentView());
          return remoteViews;
        } 
      } 
      return null;
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeHeadsUpContentView(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      RemoteViews remoteViews1;
      if (Build.VERSION.SDK_INT >= 24)
        return null; 
      if (this.mBuilder.getHeadsUpContentView() != null) {
        remoteViews1 = this.mBuilder.getHeadsUpContentView();
      } else {
        remoteViews1 = this.mBuilder.getContentView();
      } 
      if (remoteViews1 == null)
        return null; 
      RemoteViews remoteViews2 = generateBigContentView();
      buildIntoRemoteViews(remoteViews2, remoteViews1);
      if (Build.VERSION.SDK_INT >= 21)
        setBackgroundColor(remoteViews2); 
      return remoteViews2;
    }
  }
  
  public static class MediaStyle extends NotificationCompat.Style {
    private static final int MAX_MEDIA_BUTTONS = 5;
    
    private static final int MAX_MEDIA_BUTTONS_IN_COMPACT = 3;
    
    int[] mActionsToShowInCompact = null;
    
    PendingIntent mCancelButtonIntent;
    
    boolean mShowCancelButton;
    
    MediaSessionCompat.Token mToken;
    
    public MediaStyle() {}
    
    public MediaStyle(NotificationCompat.Builder param1Builder) { setBuilder(param1Builder); }
    
    private RemoteViews generateMediaActionButton(NotificationCompat.Action param1Action) {
      boolean bool;
      if (param1Action.getActionIntent() == null) {
        bool = true;
      } else {
        bool = false;
      } 
      RemoteViews remoteViews = new RemoteViews(this.mBuilder.mContext.getPackageName(), R.layout.notification_media_action);
      remoteViews.setImageViewResource(R.id.action0, param1Action.getIcon());
      if (!bool)
        remoteViews.setOnClickPendingIntent(R.id.action0, param1Action.getActionIntent()); 
      if (Build.VERSION.SDK_INT >= 15)
        remoteViews.setContentDescription(R.id.action0, param1Action.getTitle()); 
      return remoteViews;
    }
    
    public static MediaSessionCompat.Token getMediaSession(Notification param1Notification) {
      Bundle bundle = NotificationCompat.getExtras(param1Notification);
      if (bundle != null) {
        Parcelable parcelable;
        if (Build.VERSION.SDK_INT >= 21) {
          parcelable = bundle.getParcelable("android.mediaSession");
          if (parcelable != null)
            return MediaSessionCompat.Token.fromToken(parcelable); 
        } else {
          IBinder iBinder = BundleCompat.getBinder(parcelable, "android.mediaSession");
          if (iBinder != null) {
            Parcel parcel = Parcel.obtain();
            parcel.writeStrongBinder(iBinder);
            parcel.setDataPosition(0);
            MediaSessionCompat.Token token = (MediaSessionCompat.Token)MediaSessionCompat.Token.CREATOR.createFromParcel(parcel);
            parcel.recycle();
            return token;
          } 
        } 
      } 
      return null;
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void apply(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) {
      if (Build.VERSION.SDK_INT >= 21) {
        param1NotificationBuilderWithBuilderAccessor.getBuilder().setStyle(fillInMediaStyle(new Notification.MediaStyle()));
        return;
      } 
      if (this.mShowCancelButton)
        param1NotificationBuilderWithBuilderAccessor.getBuilder().setOngoing(true); 
    }
    
    @RequiresApi(21)
    Notification.MediaStyle fillInMediaStyle(Notification.MediaStyle param1MediaStyle) {
      if (this.mActionsToShowInCompact != null)
        param1MediaStyle.setShowActionsInCompactView(this.mActionsToShowInCompact); 
      if (this.mToken != null)
        param1MediaStyle.setMediaSession((MediaSession.Token)this.mToken.getToken()); 
      return param1MediaStyle;
    }
    
    RemoteViews generateBigContentView() {
      int i = Math.min(this.mBuilder.mActions.size(), 5);
      RemoteViews remoteViews = applyStandardTemplate(false, getBigContentViewLayoutResource(i), false);
      remoteViews.removeAllViews(R.id.media_actions);
      if (i > 0)
        for (byte b = 0; b < i; b++) {
          RemoteViews remoteViews1 = generateMediaActionButton((NotificationCompat.Action)this.mBuilder.mActions.get(b));
          remoteViews.addView(R.id.media_actions, remoteViews1);
        }  
      if (this.mShowCancelButton) {
        remoteViews.setViewVisibility(R.id.cancel_action, 0);
        remoteViews.setInt(R.id.cancel_action, "setAlpha", this.mBuilder.mContext.getResources().getInteger(R.integer.cancel_button_image_alpha));
        remoteViews.setOnClickPendingIntent(R.id.cancel_action, this.mCancelButtonIntent);
        return remoteViews;
      } 
      remoteViews.setViewVisibility(R.id.cancel_action, 8);
      return remoteViews;
    }
    
    RemoteViews generateContentView() {
      int i;
      RemoteViews remoteViews = applyStandardTemplate(false, getContentViewLayoutResource(), true);
      int j = this.mBuilder.mActions.size();
      if (this.mActionsToShowInCompact == null) {
        i = 0;
      } else {
        i = Math.min(this.mActionsToShowInCompact.length, 3);
      } 
      remoteViews.removeAllViews(R.id.media_actions);
      if (i > 0)
        for (byte b = 0; b < i; b++) {
          if (b >= j)
            throw new IllegalArgumentException(String.format("setShowActionsInCompactView: action %d out of bounds (max %d)", new Object[] { Integer.valueOf(b), Integer.valueOf(j - 1) })); 
          RemoteViews remoteViews1 = generateMediaActionButton((NotificationCompat.Action)this.mBuilder.mActions.get(this.mActionsToShowInCompact[b]));
          remoteViews.addView(R.id.media_actions, remoteViews1);
        }  
      if (this.mShowCancelButton) {
        remoteViews.setViewVisibility(R.id.end_padder, 8);
        remoteViews.setViewVisibility(R.id.cancel_action, 0);
        remoteViews.setOnClickPendingIntent(R.id.cancel_action, this.mCancelButtonIntent);
        remoteViews.setInt(R.id.cancel_action, "setAlpha", this.mBuilder.mContext.getResources().getInteger(R.integer.cancel_button_image_alpha));
        return remoteViews;
      } 
      remoteViews.setViewVisibility(R.id.end_padder, 0);
      remoteViews.setViewVisibility(R.id.cancel_action, 8);
      return remoteViews;
    }
    
    int getBigContentViewLayoutResource(int param1Int) { return (param1Int <= 3) ? R.layout.notification_template_big_media_narrow : R.layout.notification_template_big_media; }
    
    int getContentViewLayoutResource() { return R.layout.notification_template_media; }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) { return (Build.VERSION.SDK_INT >= 21) ? null : generateBigContentView(); }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor param1NotificationBuilderWithBuilderAccessor) { return (Build.VERSION.SDK_INT >= 21) ? null : generateContentView(); }
    
    public MediaStyle setCancelButtonIntent(PendingIntent param1PendingIntent) {
      this.mCancelButtonIntent = param1PendingIntent;
      return this;
    }
    
    public MediaStyle setMediaSession(MediaSessionCompat.Token param1Token) {
      this.mToken = param1Token;
      return this;
    }
    
    public MediaStyle setShowActionsInCompactView(int... param1VarArgs) {
      this.mActionsToShowInCompact = param1VarArgs;
      return this;
    }
    
    public MediaStyle setShowCancelButton(boolean param1Boolean) {
      if (Build.VERSION.SDK_INT < 21)
        this.mShowCancelButton = param1Boolean; 
      return this;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/media/app/NotificationCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */