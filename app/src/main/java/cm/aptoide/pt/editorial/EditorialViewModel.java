package cm.aptoide.pt.editorial;

import cm.aptoide.pt.dataprovider.model.v7.Obb;
import java.util.Collections;
import java.util.List;

/**
 * Created by D01 on 29/08/2018.
 */

public class EditorialViewModel {

  private final List<EditorialContent> contentList;
  private final String title;
  private final String caption;
  private final String background;
  private final List<Integer> placeHolderPositions;
  private final List<EditorialContent> placeHolderContent;
  private final String appName;
  private final String icon;
  private final long id;
  private final String packageName;
  private final String md5sum;
  private final int versionCode;
  private final String versionName;
  private final String path;
  private final String pathAlt;
  private final Obb obb;
  private final boolean shouldHaveAnimation;
  private final boolean loading;
  private final Error error;

  public EditorialViewModel(List<EditorialContent> editorialContentList, String title,
      String caption, String background, List<Integer> placeHolderPositions,
      List<EditorialContent> placeHolderContent, boolean shouldHaveAnimation) {
    contentList = editorialContentList;
    this.title = title;
    this.caption = caption;
    this.background = background;
    this.placeHolderPositions = placeHolderPositions;
    this.placeHolderContent = placeHolderContent;
    this.shouldHaveAnimation = shouldHaveAnimation;
    appName = "";
    icon = null;
    id = -1;
    packageName = "";
    md5sum = "";
    versionCode = -1;
    versionName = "";
    path = "";
    pathAlt = "";
    obb = null;
    loading = false;
    error = null;
  }

  public EditorialViewModel(boolean loading) {
    this.loading = loading;
    title = "";
    caption = "";
    background = "";
    placeHolderPositions = Collections.emptyList();
    contentList = Collections.emptyList();
    placeHolderContent = Collections.emptyList();
    appName = "";
    icon = null;
    id = -1;
    packageName = "";
    md5sum = "";
    versionCode = -1;
    versionName = "";
    path = "";
    pathAlt = "";
    obb = null;
    shouldHaveAnimation = false;
    error = null;
  }

  public EditorialViewModel(Error error) {
    this.error = error;
    loading = false;
    contentList = Collections.emptyList();
    title = "";
    caption = "";
    background = "";
    placeHolderPositions = Collections.emptyList();
    placeHolderContent = Collections.emptyList();
    appName = "";
    icon = null;
    id = -1;
    packageName = "";
    md5sum = "";
    versionCode = -1;
    versionName = "";
    path = "";
    pathAlt = "";
    obb = null;
    shouldHaveAnimation = false;
  }

  public EditorialViewModel(List<EditorialContent> editorialContentList, String title,
      String caption, String background, List<Integer> placeHolderPositions,
      List<EditorialContent> placeHolderContent, String appName, String icon, long id,
      String packageName, String md5sum, int versionCode, String versionName, String path,
      String pathAlt, Obb obb, boolean shouldHaveAnimation) {
    contentList = editorialContentList;
    this.title = title;
    this.caption = caption;
    this.background = background;
    this.placeHolderPositions = placeHolderPositions;
    this.placeHolderContent = placeHolderContent;
    this.appName = appName;
    this.icon = icon;
    this.id = id;
    this.packageName = packageName;
    this.md5sum = md5sum;
    this.versionCode = versionCode;
    this.versionName = versionName;
    this.path = path;
    this.pathAlt = pathAlt;
    this.obb = obb;
    this.shouldHaveAnimation = shouldHaveAnimation;
    error = null;
    loading = false;
  }

  boolean hasContent() {
    return contentList != null && !contentList.isEmpty();
  }

  public EditorialContent getContent(int position) {
    return contentList.get(position);
  }

  List<EditorialContent> getContentList() {
    return contentList;
  }

  public boolean isLoading() {
    return loading;
  }

  public Error getError() {
    return error;
  }

  public boolean hasError() {
    return error != null;
  }

  String getCaption() {
    return caption;
  }

  public String getTitle() {
    return title;
  }

  List<Integer> getPlaceHolderPositions() {
    return placeHolderPositions;
  }

  String getBottomCardAppName() {
    return appName;
  }

  String getBottomCardIcon() {
    return icon;
  }

  long getBottomCardAppId() {
    return id;
  }

  String getBottomCardPackageName() {
    return packageName;
  }

  String getBottomCardMd5() {
    return md5sum;
  }

  int getBottomCardVersionCode() {
    return versionCode;
  }

  String getBottomCardVersionName() {
    return versionName;
  }

  String getBottomCardPath() {
    return path;
  }

  String getBottomCardPathAlt() {
    return pathAlt;
  }

  Obb getBottomCardObb() {
    return obb;
  }

  String getBackgroundImage() {
    return background;
  }

  boolean hasBackgroundImage() {
    return !background.equals("");
  }

  List<EditorialContent> getPlaceHolderContent() {
    return placeHolderContent;
  }

  boolean shouldHaveAnimation() {
    return shouldHaveAnimation;
  }

  public enum Error {
    NETWORK, GENERIC;
  }
}