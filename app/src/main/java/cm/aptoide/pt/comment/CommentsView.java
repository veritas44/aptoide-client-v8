package cm.aptoide.pt.comment;

import cm.aptoide.pt.presenter.View;
import java.util.List;

interface CommentsView extends View {

  void showComments(List<String> comments);
}
