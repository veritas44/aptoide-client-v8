package cm.aptoide.pt.app;

import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.ads.data.ApplicationAdError;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.AppsList;
import java.util.Collections;
import java.util.List;

/**
 * Created by D01 on 07/05/18.
 */

public class SimilarAppsViewModel {

  private final ApplicationAd ad;
  private final List<Application> recommendedApps;
  private final boolean loading;
  private final AppsList.Error recommendedAppsError;
  private final ApplicationAdError adError;
  private boolean hasRecordedAdImpression;
  private boolean shouldLoadNativeAds;

  public SimilarAppsViewModel(ApplicationAd ad, List<Application> recommendedApps, boolean loading,
      AppsList.Error recommendedAppsError, ApplicationAdError adResultError) {
    this(ad, recommendedApps, loading, recommendedAppsError, adResultError, false);
  }

  public SimilarAppsViewModel(ApplicationAd ad, List<Application> recommendedApps, boolean loading,
      AppsList.Error recommendedAppsError, ApplicationAdError adResultError,
      boolean shouldLoadNativeAds) {
    this.ad = ad;
    this.recommendedApps = recommendedApps;
    this.loading = loading;
    this.recommendedAppsError = recommendedAppsError;
    this.adError = adResultError;
    this.hasRecordedAdImpression = false;
    this.shouldLoadNativeAds = shouldLoadNativeAds;
  }

  public SimilarAppsViewModel() {
    this.ad = null;
    this.recommendedApps = Collections.emptyList();
    this.loading = false;
    this.recommendedAppsError = null;
    this.adError = null;
    this.shouldLoadNativeAds = false;
  }

  public ApplicationAd getAd() {
    return ad;
  }

  public List<Application> getRecommendedApps() {
    return recommendedApps;
  }

  public boolean isLoading() {
    return loading;
  }

  public AppsList.Error getRecommendedAppsError() {
    return recommendedAppsError;
  }

  public boolean hasSimilarApps() {
    return !hasRecommendedAppsError() && !recommendedApps.isEmpty();
  }

  public boolean hasError() {
    return (recommendedAppsError != null || adError != null || ad == null);
  }

  public ApplicationAdError getAdError() {
    return adError;
  }

  public boolean hasAd() {
    return (ad != null);
  }

  public boolean hasRecommendedAppsError() {
    return (recommendedAppsError != null);
  }

  public boolean hasAdError() {
    return (adError != null || ad == null);
  }

  public void setHasRecordedAdImpression(boolean recorded) {
    hasRecordedAdImpression = recorded;
  }

  public boolean hasRecordedAdImpression() {
    return hasRecordedAdImpression;
  }

  public boolean shouldLoadNativeAds() {
    return shouldLoadNativeAds;
  }

  public void setShouldLoadNativeAds(boolean shouldLoadNativeAds) {
    this.shouldLoadNativeAds = shouldLoadNativeAds;
  }
}
