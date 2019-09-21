package android.support.v4.content.pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.support.v4.graphics.drawable.IconCompat;
import android.text.TextUtils;
import java.util.Arrays;

public class ShortcutInfoCompat {
  private ComponentName mActivity;
  
  private Context mContext;
  
  private CharSequence mDisabledMessage;
  
  private IconCompat mIcon;
  
  private String mId;
  
  private Intent[] mIntents;
  
  private boolean mIsAlwaysBadged;
  
  private CharSequence mLabel;
  
  private CharSequence mLongLabel;
  
  private ShortcutInfoCompat() {}
  
  @VisibleForTesting
  Intent addToIntent(Intent paramIntent) {
    paramIntent.putExtra("android.intent.extra.shortcut.INTENT", this.mIntents[this.mIntents.length - 1]).putExtra("android.intent.extra.shortcut.NAME", this.mLabel.toString());
    if (this.mIcon != null) {
      Drawable drawable1 = null;
      Drawable drawable2 = null;
      if (this.mIsAlwaysBadged) {
        PackageManager packageManager = this.mContext.getPackageManager();
        drawable = drawable2;
        if (this.mActivity != null)
          try {
            drawable = packageManager.getActivityIcon(this.mActivity);
          } catch (android.content.pm.PackageManager.NameNotFoundException drawable) {
            drawable = drawable2;
          }  
        drawable1 = drawable;
        if (drawable == null)
          drawable1 = this.mContext.getApplicationInfo().loadIcon(packageManager); 
      } 
      this.mIcon.addToShortcutIntent(paramIntent, drawable1);
    } 
    return paramIntent;
  }
  
  @Nullable
  public ComponentName getActivity() { return this.mActivity; }
  
  @Nullable
  public CharSequence getDisabledMessage() { return this.mDisabledMessage; }
  
  @NonNull
  public String getId() { return this.mId; }
  
  @NonNull
  public Intent getIntent() { return this.mIntents[this.mIntents.length - 1]; }
  
  @NonNull
  public Intent[] getIntents() { return (Intent[])Arrays.copyOf(this.mIntents, this.mIntents.length); }
  
  @Nullable
  public CharSequence getLongLabel() { return this.mLongLabel; }
  
  @NonNull
  public CharSequence getShortLabel() { return this.mLabel; }
  
  @RequiresApi(25)
  public ShortcutInfo toShortcutInfo() {
    ShortcutInfo.Builder builder = (new ShortcutInfo.Builder(this.mContext, this.mId)).setShortLabel(this.mLabel).setIntents(this.mIntents);
    if (this.mIcon != null)
      builder.setIcon(this.mIcon.toIcon()); 
    if (!TextUtils.isEmpty(this.mLongLabel))
      builder.setLongLabel(this.mLongLabel); 
    if (!TextUtils.isEmpty(this.mDisabledMessage))
      builder.setDisabledMessage(this.mDisabledMessage); 
    if (this.mActivity != null)
      builder.setActivity(this.mActivity); 
    return builder.build();
  }
  
  public static class Builder {
    private final ShortcutInfoCompat mInfo;
    
    public Builder(@NonNull Context param1Context, @NonNull String param1String) {
      (this.mInfo = new ShortcutInfoCompat(null)).access$102(this.mInfo, param1Context);
      ShortcutInfoCompat.access$202(this.mInfo, param1String);
    }
    
    @NonNull
    public ShortcutInfoCompat build() {
      if (TextUtils.isEmpty(this.mInfo.mLabel))
        throw new IllegalArgumentException("Shortcut much have a non-empty label"); 
      if (this.mInfo.mIntents == null || this.mInfo.mIntents.length == 0)
        throw new IllegalArgumentException("Shortcut much have an intent"); 
      return this.mInfo;
    }
    
    @NonNull
    public Builder setActivity(@NonNull ComponentName param1ComponentName) {
      ShortcutInfoCompat.access$802(this.mInfo, param1ComponentName);
      return this;
    }
    
    public Builder setAlwaysBadged() {
      ShortcutInfoCompat.access$902(this.mInfo, true);
      return this;
    }
    
    @NonNull
    public Builder setDisabledMessage(@NonNull CharSequence param1CharSequence) {
      ShortcutInfoCompat.access$502(this.mInfo, param1CharSequence);
      return this;
    }
    
    @NonNull
    public Builder setIcon(IconCompat param1IconCompat) {
      ShortcutInfoCompat.access$702(this.mInfo, param1IconCompat);
      return this;
    }
    
    @NonNull
    public Builder setIntent(@NonNull Intent param1Intent) { return setIntents(new Intent[] { param1Intent }); }
    
    @NonNull
    public Builder setIntents(@NonNull Intent[] param1ArrayOfIntent) {
      ShortcutInfoCompat.access$602(this.mInfo, param1ArrayOfIntent);
      return this;
    }
    
    @NonNull
    public Builder setLongLabel(@NonNull CharSequence param1CharSequence) {
      ShortcutInfoCompat.access$402(this.mInfo, param1CharSequence);
      return this;
    }
    
    @NonNull
    public Builder setShortLabel(@NonNull CharSequence param1CharSequence) {
      ShortcutInfoCompat.access$302(this.mInfo, param1CharSequence);
      return this;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/content/pm/ShortcutInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */