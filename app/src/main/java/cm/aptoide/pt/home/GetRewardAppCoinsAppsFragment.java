package cm.aptoide.pt.home;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.home.bundles.apps.RewardApp;
import cm.aptoide.pt.repository.request.RewardAppCoinsAppsRepository;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayableGroup;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

/**
 * Created by filipegoncalves on 4/27/18.
 */

public class GetRewardAppCoinsAppsFragment extends StoreTabGridRecyclerFragment {

  @Inject RewardAppCoinsAppsRepository rewardAppsRepository;
  @Inject AppNavigator appNavigator;
  @Inject AnalyticsManager analyticsManager;

  public static Fragment newInstance() {
    return new GetRewardAppCoinsAppsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Nullable @Override
  protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url,
      boolean bypassServerCache) {
    return rewardAppsRepository.getAppCoinsRewardAppsFromHomeMore(refresh, tag)
        .map(rewardApps -> {
          List<Displayable> displayables = new LinkedList<>();
          for (RewardApp app : rewardApps) {
            displayables.add(
                new GridAppCoinsRewardAppsDisplayable(app, tag + "-more", navigationTracker,
                    appNavigator, analyticsManager));
          }

          return Collections.singletonList(new DisplayableGroup(displayables,
              (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
              getContext().getResources()));
        });
  }
}
