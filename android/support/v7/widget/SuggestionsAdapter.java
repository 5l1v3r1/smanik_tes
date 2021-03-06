package android.support.v7.widget;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ResourceCursorAdapter;
import android.support.v7.appcompat.R;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.WeakHashMap;

class SuggestionsAdapter extends ResourceCursorAdapter implements View.OnClickListener {
  private static final boolean DBG = false;
  
  static final int INVALID_INDEX = -1;
  
  private static final String LOG_TAG = "SuggestionsAdapter";
  
  private static final int QUERY_LIMIT = 50;
  
  static final int REFINE_ALL = 2;
  
  static final int REFINE_BY_ENTRY = 1;
  
  static final int REFINE_NONE = 0;
  
  private boolean mClosed = false;
  
  private final int mCommitIconResId;
  
  private int mFlagsCol = -1;
  
  private int mIconName1Col = -1;
  
  private int mIconName2Col = -1;
  
  private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache;
  
  private final Context mProviderContext;
  
  private int mQueryRefinement = 1;
  
  private final SearchManager mSearchManager = (SearchManager)this.mContext.getSystemService("search");
  
  private final SearchView mSearchView;
  
  private final SearchableInfo mSearchable;
  
  private int mText1Col = -1;
  
  private int mText2Col = -1;
  
  private int mText2UrlCol = -1;
  
  private ColorStateList mUrlColor;
  
  public SuggestionsAdapter(Context paramContext, SearchView paramSearchView, SearchableInfo paramSearchableInfo, WeakHashMap<String, Drawable.ConstantState> paramWeakHashMap) {
    super(paramContext, paramSearchView.getSuggestionRowLayout(), null, true);
    this.mSearchView = paramSearchView;
    this.mSearchable = paramSearchableInfo;
    this.mCommitIconResId = paramSearchView.getSuggestionCommitIconResId();
    this.mProviderContext = paramContext;
    this.mOutsideDrawablesCache = paramWeakHashMap;
  }
  
  private Drawable checkIconCache(String paramString) {
    Drawable.ConstantState constantState = (Drawable.ConstantState)this.mOutsideDrawablesCache.get(paramString);
    return (constantState == null) ? null : constantState.newDrawable();
  }
  
  private CharSequence formatUrl(CharSequence paramCharSequence) {
    if (this.mUrlColor == null) {
      TypedValue typedValue = new TypedValue();
      this.mContext.getTheme().resolveAttribute(R.attr.textColorSearchUrl, typedValue, true);
      this.mUrlColor = this.mContext.getResources().getColorStateList(typedValue.resourceId);
    } 
    SpannableString spannableString = new SpannableString(paramCharSequence);
    spannableString.setSpan(new TextAppearanceSpan(null, 0, 0, this.mUrlColor, null), 0, paramCharSequence.length(), 33);
    return spannableString;
  }
  
  private Drawable getActivityIcon(ComponentName paramComponentName) {
    PackageManager packageManager = this.mContext.getPackageManager();
    try {
      ActivityInfo activityInfo = packageManager.getActivityInfo(paramComponentName, 128);
      int i = activityInfo.getIconResource();
      if (i == 0)
        return null; 
      StringBuilder stringBuilder = packageManager.getDrawable(paramComponentName.getPackageName(), i, activityInfo.applicationInfo);
      if (stringBuilder == null) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid icon resource ");
        stringBuilder.append(i);
        stringBuilder.append(" for ");
        stringBuilder.append(paramComponentName.flattenToShortString());
        Log.w("SuggestionsAdapter", stringBuilder.toString());
        return null;
      } 
      return stringBuilder;
    } catch (android.content.pm.PackageManager.NameNotFoundException paramComponentName) {
      Log.w("SuggestionsAdapter", paramComponentName.toString());
      return null;
    } 
  }
  
  private Drawable getActivityIconWithCache(ComponentName paramComponentName) {
    Drawable.ConstantState constantState1;
    String str = paramComponentName.flattenToShortString();
    boolean bool = this.mOutsideDrawablesCache.containsKey(str);
    Drawable.ConstantState constantState2 = null;
    if (bool) {
      constantState1 = (Drawable.ConstantState)this.mOutsideDrawablesCache.get(str);
      return (constantState1 == null) ? null : constantState1.newDrawable(this.mProviderContext.getResources());
    } 
    Drawable drawable = getActivityIcon(constantState1);
    if (drawable == null) {
      constantState1 = constantState2;
    } else {
      constantState1 = drawable.getConstantState();
    } 
    this.mOutsideDrawablesCache.put(str, constantState1);
    return drawable;
  }
  
  public static String getColumnString(Cursor paramCursor, String paramString) { return getStringOrNull(paramCursor, paramCursor.getColumnIndex(paramString)); }
  
  private Drawable getDefaultIcon1(Cursor paramCursor) {
    Drawable drawable = getActivityIconWithCache(this.mSearchable.getSearchActivity());
    return (drawable != null) ? drawable : this.mContext.getPackageManager().getDefaultActivityIcon();
  }
  
  private Drawable getDrawable(Uri paramUri) {
    try {
      boolean bool = "android.resource".equals(paramUri.getScheme());
      if (bool)
        try {
          return getDrawableFromResourceUri(paramUri);
        } catch (android.content.res.Resources.NotFoundException notFoundException) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("Resource does not exist: ");
          stringBuilder.append(paramUri);
          throw new FileNotFoundException(stringBuilder.toString());
        }  
      inputStream = this.mProviderContext.getContentResolver().openInputStream(paramUri);
      if (inputStream == null) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Failed to open ");
        stringBuilder.append(paramUri);
        throw new FileNotFoundException(stringBuilder.toString());
      } 
      try {
        drawable = Drawable.createFromStream(inputStream, null);
      } finally {
        try {
          inputStream.close();
        } catch (IOException inputStream) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("Error closing icon stream for ");
          stringBuilder.append(paramUri);
          Log.e("SuggestionsAdapter", stringBuilder.toString(), inputStream);
        } 
      } 
    } catch (FileNotFoundException fileNotFoundException) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Icon not found: ");
      stringBuilder.append(paramUri);
      stringBuilder.append(", ");
      stringBuilder.append(fileNotFoundException.getMessage());
      Log.w("SuggestionsAdapter", stringBuilder.toString());
      return null;
    } 
  }
  
  private Drawable getDrawableFromResourceValue(String paramString) {
    if (paramString != null && !paramString.isEmpty()) {
      if ("0".equals(paramString))
        return null; 
      try {
        int i = Integer.parseInt(paramString);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("android.resource://");
        stringBuilder.append(this.mProviderContext.getPackageName());
        stringBuilder.append("/");
        stringBuilder.append(i);
        String str = stringBuilder.toString();
        Drawable drawable = checkIconCache(str);
        if (drawable != null)
          return drawable; 
        drawable = ContextCompat.getDrawable(this.mProviderContext, i);
        storeInIconCache(str, drawable);
        return drawable;
      } catch (NumberFormatException numberFormatException) {
        Drawable drawable = checkIconCache(paramString);
        if (drawable != null)
          return drawable; 
        drawable = getDrawable(Uri.parse(paramString));
        storeInIconCache(paramString, drawable);
        return drawable;
      } catch (android.content.res.Resources.NotFoundException notFoundException) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Icon resource not found: ");
        stringBuilder.append(paramString);
        Log.w("SuggestionsAdapter", stringBuilder.toString());
        return null;
      } 
    } 
    return null;
  }
  
  private Drawable getIcon1(Cursor paramCursor) {
    if (this.mIconName1Col == -1)
      return null; 
    Drawable drawable = getDrawableFromResourceValue(paramCursor.getString(this.mIconName1Col));
    return (drawable != null) ? drawable : getDefaultIcon1(paramCursor);
  }
  
  private Drawable getIcon2(Cursor paramCursor) { return (this.mIconName2Col == -1) ? null : getDrawableFromResourceValue(paramCursor.getString(this.mIconName2Col)); }
  
  private static String getStringOrNull(Cursor paramCursor, int paramInt) {
    if (paramInt == -1)
      return null; 
    try {
      return paramCursor.getString(paramInt);
    } catch (Exception paramCursor) {
      Log.e("SuggestionsAdapter", "unexpected error retrieving valid column from cursor, did the remote process die?", paramCursor);
      return null;
    } 
  }
  
  private void setViewDrawable(ImageView paramImageView, Drawable paramDrawable, int paramInt) {
    paramImageView.setImageDrawable(paramDrawable);
    if (paramDrawable == null) {
      paramImageView.setVisibility(paramInt);
      return;
    } 
    paramImageView.setVisibility(0);
    paramDrawable.setVisible(false, false);
    paramDrawable.setVisible(true, false);
  }
  
  private void setViewText(TextView paramTextView, CharSequence paramCharSequence) {
    paramTextView.setText(paramCharSequence);
    if (TextUtils.isEmpty(paramCharSequence)) {
      paramTextView.setVisibility(8);
      return;
    } 
    paramTextView.setVisibility(0);
  }
  
  private void storeInIconCache(String paramString, Drawable paramDrawable) {
    if (paramDrawable != null)
      this.mOutsideDrawablesCache.put(paramString, paramDrawable.getConstantState()); 
  }
  
  private void updateSpinnerState(Cursor paramCursor) {
    if (paramCursor != null) {
      Bundle bundle = paramCursor.getExtras();
    } else {
      paramCursor = null;
    } 
    if (paramCursor != null && paramCursor.getBoolean("in_progress"))
      return; 
  }
  
  public void bindView(View paramView, Context paramContext, Cursor paramCursor) {
    boolean bool;
    ChildViewCache childViewCache = (ChildViewCache)paramView.getTag();
    if (this.mFlagsCol != -1) {
      bool = paramCursor.getInt(this.mFlagsCol);
    } else {
      bool = false;
    } 
    if (childViewCache.mText1 != null) {
      String str = getStringOrNull(paramCursor, this.mText1Col);
      setViewText(childViewCache.mText1, str);
    } 
    if (childViewCache.mText2 != null) {
      String str = getStringOrNull(paramCursor, this.mText2UrlCol);
      if (str != null) {
        CharSequence charSequence = formatUrl(str);
      } else {
        str = getStringOrNull(paramCursor, this.mText2Col);
      } 
      if (TextUtils.isEmpty(str)) {
        if (childViewCache.mText1 != null) {
          childViewCache.mText1.setSingleLine(false);
          childViewCache.mText1.setMaxLines(2);
        } 
      } else if (childViewCache.mText1 != null) {
        childViewCache.mText1.setSingleLine(true);
        childViewCache.mText1.setMaxLines(1);
      } 
      setViewText(childViewCache.mText2, str);
    } 
    if (childViewCache.mIcon1 != null)
      setViewDrawable(childViewCache.mIcon1, getIcon1(paramCursor), 4); 
    if (childViewCache.mIcon2 != null)
      setViewDrawable(childViewCache.mIcon2, getIcon2(paramCursor), 8); 
    if (this.mQueryRefinement == 2 || (this.mQueryRefinement == 1 && bool & true)) {
      childViewCache.mIconRefine.setVisibility(0);
      childViewCache.mIconRefine.setTag(childViewCache.mText1.getText());
      childViewCache.mIconRefine.setOnClickListener(this);
      return;
    } 
    childViewCache.mIconRefine.setVisibility(8);
  }
  
  public void changeCursor(Cursor paramCursor) {
    if (this.mClosed) {
      Log.w("SuggestionsAdapter", "Tried to change cursor after adapter was closed.");
      if (paramCursor != null)
        paramCursor.close(); 
      return;
    } 
    try {
      super.changeCursor(paramCursor);
      if (paramCursor != null) {
        this.mText1Col = paramCursor.getColumnIndex("suggest_text_1");
        this.mText2Col = paramCursor.getColumnIndex("suggest_text_2");
        this.mText2UrlCol = paramCursor.getColumnIndex("suggest_text_2_url");
        this.mIconName1Col = paramCursor.getColumnIndex("suggest_icon_1");
        this.mIconName2Col = paramCursor.getColumnIndex("suggest_icon_2");
        this.mFlagsCol = paramCursor.getColumnIndex("suggest_flags");
        return;
      } 
    } catch (Exception paramCursor) {
      Log.e("SuggestionsAdapter", "error changing cursor and caching columns", paramCursor);
    } 
  }
  
  public void close() {
    changeCursor(null);
    this.mClosed = true;
  }
  
  public CharSequence convertToString(Cursor paramCursor) {
    if (paramCursor == null)
      return null; 
    String str = getColumnString(paramCursor, "suggest_intent_query");
    if (str != null)
      return str; 
    if (this.mSearchable.shouldRewriteQueryFromData()) {
      str = getColumnString(paramCursor, "suggest_intent_data");
      if (str != null)
        return str; 
    } 
    if (this.mSearchable.shouldRewriteQueryFromText()) {
      String str1 = getColumnString(paramCursor, "suggest_text_1");
      if (str1 != null)
        return str1; 
    } 
    return null;
  }
  
  Drawable getDrawableFromResourceUri(Uri paramUri) {
    stringBuilder = paramUri.getAuthority();
    if (TextUtils.isEmpty(stringBuilder)) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("No authority: ");
      stringBuilder.append(paramUri);
      throw new FileNotFoundException(stringBuilder.toString());
    } 
    try {
      Resources resources = this.mContext.getPackageManager().getResourcesForApplication(stringBuilder);
      List list = paramUri.getPathSegments();
      if (list == null) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("No path: ");
        stringBuilder.append(paramUri);
        throw new FileNotFoundException(stringBuilder.toString());
      } 
      int i = list.size();
      if (i == 1)
        try {
          i = Integer.parseInt((String)list.get(0));
          if (i == 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("No resource found for: ");
            stringBuilder.append(paramUri);
            throw new FileNotFoundException(stringBuilder.toString());
          } 
          return resources.getDrawable(i);
        } catch (NumberFormatException stringBuilder) {
          stringBuilder = new StringBuilder();
          stringBuilder.append("Single path segment is not a resource ID: ");
          stringBuilder.append(paramUri);
          throw new FileNotFoundException(stringBuilder.toString());
        }  
      if (i == 2) {
        i = resources.getIdentifier((String)list.get(1), (String)list.get(0), stringBuilder);
      } else {
        stringBuilder = new StringBuilder();
        stringBuilder.append("More than two path segments: ");
        stringBuilder.append(paramUri);
        throw new FileNotFoundException(stringBuilder.toString());
      } 
      if (i == 0) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("No resource found for: ");
        stringBuilder.append(paramUri);
        throw new FileNotFoundException(stringBuilder.toString());
      } 
      return resources.getDrawable(i);
    } catch (android.content.pm.PackageManager.NameNotFoundException stringBuilder) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("No package found for authority: ");
      stringBuilder.append(paramUri);
      throw new FileNotFoundException(stringBuilder.toString());
    } 
  }
  
  public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup) {
    try {
      return super.getDropDownView(paramInt, paramView, paramViewGroup);
    } catch (RuntimeException paramView) {
      Log.w("SuggestionsAdapter", "Search suggestions cursor threw exception.", paramView);
      View view = newDropDownView(this.mContext, this.mCursor, paramViewGroup);
      if (view != null)
        ((ChildViewCache)view.getTag()).mText1.setText(paramView.toString()); 
      return view;
    } 
  }
  
  public int getQueryRefinement() { return this.mQueryRefinement; }
  
  Cursor getSearchManagerSuggestions(SearchableInfo paramSearchableInfo, String paramString, int paramInt) {
    SearchableInfo searchableInfo = null;
    if (paramSearchableInfo == null)
      return null; 
    String str1 = paramSearchableInfo.getSuggestAuthority();
    if (str1 == null)
      return null; 
    Uri.Builder builder = (new Uri.Builder()).scheme("content").authority(str1).query("").fragment("");
    String str2 = paramSearchableInfo.getSuggestPath();
    if (str2 != null)
      builder.appendEncodedPath(str2); 
    builder.appendPath("search_suggest_query");
    str2 = paramSearchableInfo.getSuggestSelection();
    if (str2 != null) {
      String[] arrayOfString = new String[1];
      arrayOfString[0] = paramString;
    } else {
      builder.appendPath(paramString);
      paramSearchableInfo = searchableInfo;
    } 
    if (paramInt > 0)
      builder.appendQueryParameter("limit", String.valueOf(paramInt)); 
    Uri uri = builder.build();
    return this.mContext.getContentResolver().query(uri, null, str2, paramSearchableInfo, null);
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
    try {
      return super.getView(paramInt, paramView, paramViewGroup);
    } catch (RuntimeException paramView) {
      Log.w("SuggestionsAdapter", "Search suggestions cursor threw exception.", paramView);
      View view = newView(this.mContext, this.mCursor, paramViewGroup);
      if (view != null)
        ((ChildViewCache)view.getTag()).mText1.setText(paramView.toString()); 
      return view;
    } 
  }
  
  public boolean hasStableIds() { return false; }
  
  public View newView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup) {
    View view = super.newView(paramContext, paramCursor, paramViewGroup);
    view.setTag(new ChildViewCache(view));
    ((ImageView)view.findViewById(R.id.edit_query)).setImageResource(this.mCommitIconResId);
    return view;
  }
  
  public void notifyDataSetChanged() {
    super.notifyDataSetChanged();
    updateSpinnerState(getCursor());
  }
  
  public void notifyDataSetInvalidated() {
    super.notifyDataSetInvalidated();
    updateSpinnerState(getCursor());
  }
  
  public void onClick(View paramView) {
    Object object = paramView.getTag();
    if (object instanceof CharSequence)
      this.mSearchView.onQueryRefine((CharSequence)object); 
  }
  
  public Cursor runQueryOnBackgroundThread(CharSequence paramCharSequence) {
    if (paramCharSequence == null) {
      paramCharSequence = "";
    } else {
      paramCharSequence = paramCharSequence.toString();
    } 
    if (this.mSearchView.getVisibility() == 0) {
      if (this.mSearchView.getWindowVisibility() != 0)
        return null; 
      try {
        Cursor cursor = getSearchManagerSuggestions(this.mSearchable, paramCharSequence, 50);
        if (cursor != null) {
          cursor.getCount();
          return cursor;
        } 
      } catch (RuntimeException paramCharSequence) {
        Log.w("SuggestionsAdapter", "Search suggestions query threw an exception.", paramCharSequence);
      } 
      return null;
    } 
    return null;
  }
  
  public void setQueryRefinement(int paramInt) { this.mQueryRefinement = paramInt; }
  
  private static final class ChildViewCache {
    public final ImageView mIcon1;
    
    public final ImageView mIcon2;
    
    public final ImageView mIconRefine;
    
    public final TextView mText1;
    
    public final TextView mText2;
    
    public ChildViewCache(View param1View) {
      this.mText1 = (TextView)param1View.findViewById(16908308);
      this.mText2 = (TextView)param1View.findViewById(16908309);
      this.mIcon1 = (ImageView)param1View.findViewById(16908295);
      this.mIcon2 = (ImageView)param1View.findViewById(16908296);
      this.mIconRefine = (ImageView)param1View.findViewById(R.id.edit_query);
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/SuggestionsAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */