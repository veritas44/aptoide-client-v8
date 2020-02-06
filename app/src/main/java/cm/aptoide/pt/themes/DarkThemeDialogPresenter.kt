package cm.aptoide.pt.themes

import cm.aptoide.pt.presenter.Presenter
import cm.aptoide.pt.presenter.View

class DarkThemeDialogPresenter(val view: DarkThemeDialogView,
                               val newFeatureManager: NewFeatureManager,
                               val themeManager: ThemeManager,
                               val themeAnalytics: ThemeAnalytics) : Presenter {

  override fun present() {
    handleDismissClick()
    handleTurnItOnClick()
  }

  private fun handleTurnItOnClick() {
    view.lifecycleEvent
        .filter { e -> e == View.LifecycleEvent.CREATE }
        .flatMap { view.clickTurnItOn() }
        .doOnNext {
          newFeatureManager.setFeatureAsShown()
          newFeatureManager.unscheduleNotification()
          view.dismissView()
          themeManager.setThemeOption(ThemeManager.ThemeOption.DARK)
          themeManager.resetToBaseTheme()
          themeAnalytics.setDarkThemeUserProperty(themeManager.isThemeDark())
          themeAnalytics.sendDarkThemeDialogTurnItOnClickEvent("HomeFragment")
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {})
  }

  private fun handleDismissClick() {
    view.lifecycleEvent
        .filter { e -> e == View.LifecycleEvent.CREATE }
        .flatMap { view.clickDismiss() }
        .doOnNext {
          newFeatureManager.setFeatureAsShown()
          newFeatureManager.unscheduleNotification()
          view.dismissView()
          themeAnalytics.sendDarkThemeDismissClickEvent("HomeFragment")
        }
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe({}, {})
  }

}