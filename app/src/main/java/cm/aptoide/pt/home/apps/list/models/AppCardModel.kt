package cm.aptoide.pt.home.apps.list.models

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import cm.aptoide.aptoideviews.downloadprogressview.DownloadEventListener
import cm.aptoide.aptoideviews.downloadprogressview.DownloadProgressView
import cm.aptoide.pt.R
import cm.aptoide.pt.home.apps.App
import cm.aptoide.pt.home.apps.AppClick
import cm.aptoide.pt.home.apps.model.*
import cm.aptoide.pt.networking.image.ImageLoader
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fa.epoxysample.bundles.models.base.BaseViewHolder
import rx.subjects.PublishSubject

@EpoxyModelClass(layout = R.layout.apps_app_card_item)
abstract class AppCardModel : EpoxyModelWithHolder<AppCardModel.AppCardHolder>() {

  @EpoxyAttribute
  var application: App? = null

  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var eventSubject: PublishSubject<AppClick>? = null

  override fun bind(holder: AppCardHolder) {
    application?.let { app ->
      when (app.type) {
        App.Type.UPDATE -> bindUpdate(holder, app as UpdateApp)
        App.Type.DOWNLOAD -> bindDownloadApp(holder, app as DownloadApp)
        else -> Unit
      }
    }
  }

  override fun bind(holder: AppCardHolder, previouslyBoundModel: EpoxyModel<*>) {
    (application as? StateApp)?.let { app ->
      processDownload(holder, app)
    } ?: bind(holder)
  }

  private fun bindDownloadApp(holder: AppCardHolder, downloadApp: DownloadApp) {
    setupDownload(holder, downloadApp)

    val constraintSet = ConstraintSet()
    constraintSet.clone(holder.rootLayout)
    constraintSet.clear(R.id.appc_disclaimer_icon)
    constraintSet.connect(R.id.appc_disclaimer_icon, ConstraintSet.LEFT, ConstraintSet.PARENT_ID,
        ConstraintSet.LEFT)
    constraintSet.connect(R.id.appc_disclaimer_icon, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID,
        ConstraintSet.RIGHT)
    constraintSet.connect(R.id.appc_disclaimer_icon, ConstraintSet.TOP, R.id.message,
        ConstraintSet.BOTTOM)

    holder.actionButton.setOnClickListener {
      holder.downloadProgressView.reset()
      setDownloadViewVisibility(holder, true)
      eventSubject?.onNext(AppClick(downloadApp, AppClick.ClickType.DOWNLOAD_ACTION_CLICK))
    }
    holder.itemView.setOnClickListener {
      eventSubject?.onNext(AppClick(downloadApp, AppClick.ClickType.CARD_CLICK))
    }
  }

  private fun bindUpdate(holder: AppCardHolder,
                         app: UpdateApp) {
    holder.nameTextView.text = app.name
    ImageLoader.with(holder.itemView.context).load(app.icon, holder.appIcon)
    holder.secondaryText.text = app.version
    holder.secondaryText.setTextAppearance(holder.itemView.context,
        R.style.Aptoide_TextView_Medium_XS_Grey)
    holder.secondaryIcon.visibility = View.VISIBLE
    holder.secondaryIcon.setImageResource(R.drawable.ic_refresh_orange)
    holder.actionButton.visibility = View.VISIBLE
    holder.actionButton.setImageResource(R.drawable.ic_refresh_action_black)

    setupDownload(holder, app)
    processDownload(holder, app)

    holder.actionButton.setOnClickListener {
      processDownload(holder, app)
      eventSubject?.onNext(AppClick(app, AppClick.ClickType.DOWNLOAD_ACTION_CLICK))
    }
    holder.itemView.setOnClickListener {
      eventSubject?.onNext(AppClick(app, AppClick.ClickType.CARD_CLICK))
    }
    holder.itemView.setOnLongClickListener {
      eventSubject?.onNext(AppClick(app, AppClick.ClickType.CARD_LONG_CLICK))
      true
    }
  }

  private fun setupDownload(holder: AppCardHolder,
                            app: StateApp) {
    holder.downloadProgressView.setEventListener(object : DownloadEventListener {
      override fun onActionClick(action: DownloadEventListener.Action) {
        when (action.type) {
          DownloadEventListener.Action.Type.CANCEL -> eventSubject?.onNext(
              AppClick(app, AppClick.ClickType.CANCEL_CLICK))
          DownloadEventListener.Action.Type.RESUME -> eventSubject?.onNext(
              AppClick(app, AppClick.ClickType.RESUME_CLICK))
          DownloadEventListener.Action.Type.PAUSE -> eventSubject?.onNext(
              AppClick(app, AppClick.ClickType.PAUSE_CLICK))
          else -> Unit
        }
      }
    })
  }

  private fun processDownload(holder: AppCardHolder, app: StateApp) {
    when (app.status) {
      StateApp.Status.ACTIVE -> {
        setDownloadViewVisibility(holder, true)
        holder.downloadProgressView.startDownload()
      }
      StateApp.Status.INSTALLING -> {
        setDownloadViewVisibility(holder, true)
        holder.downloadProgressView.startInstallation()
      }
      StateApp.Status.PAUSE -> {
        setDownloadViewVisibility(holder, true)
        holder.downloadProgressView.pauseDownload()
      }
      StateApp.Status.ERROR -> {
        setDownloadViewVisibility(holder, true)

      } // TODO: Implement error
      StateApp.Status.IN_QUEUE -> {
        setDownloadViewVisibility(holder, true)
      }
      StateApp.Status.STANDBY -> {
        setDownloadViewVisibility(holder, false)
        holder.downloadProgressView.reset()
      }
      else -> Unit
    }
    holder.downloadProgressView.setProgress(app.progress)
  }


  private fun setDownloadViewVisibility(holder: AppCardHolder, visible: Boolean) {
    if (visible) {
      holder.downloadProgressView.visibility = View.VISIBLE
      holder.secondaryIcon.visibility = View.GONE
      holder.secondaryText.visibility = View.GONE
      holder.actionButton.visibility = View.GONE
    } else {
      holder.downloadProgressView.visibility = View.GONE
      holder.secondaryIcon.visibility = View.VISIBLE
      holder.secondaryText.visibility = View.VISIBLE
      holder.actionButton.visibility = View.VISIBLE
    }
  }

  class AppCardHolder : BaseViewHolder() {
    val nameTextView by bind<TextView>(R.id.apps_updates_app_name)
    val appIcon by bind<ImageView>(R.id.apps_updates_app_icon)
    val secondaryText by bind<TextView>(R.id.apps_secondary_text)
    val secondaryIcon by bind<ImageView>(R.id.secondary_icon)
    val actionButton by bind<ImageView>(R.id.apps_action_button)
    val downloadProgressView by bind<DownloadProgressView>(R.id.download_progress_view)
    val rootLayout by bind<ConstraintLayout>(R.id.root_layout)
  }
}