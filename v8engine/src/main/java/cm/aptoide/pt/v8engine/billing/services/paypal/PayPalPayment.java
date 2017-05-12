/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.services.paypal;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Price;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.billing.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.billing.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.billing.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.PaymentRepositoryFactory;
import cm.aptoide.pt.v8engine.billing.services.AptoidePayment;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import java.math.BigDecimal;
import rx.Completable;
import rx.Observable;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PayPalPayment extends AptoidePayment {

  public static final String PAYMENT_RESULT_ACTION =
      "cm.aptoide.pt.v8engine.payment.service.action.PAYMENT_RESULT";
  public static final String PAYMENT_CONFIRMATION_EXTRA =
      "cm.aptoide.pt.v8engine.payment.service.extra.PAYMENT_CONFIRMATION";
  public static final String PAYMENT_STATUS_EXTRA =
      "cm.aptoide.pt.v8engine.payment.service.extra.PAYMENT_STATUS";

  public static final int PAYMENT_STATUS_OK = 0;
  public static final int PAYMENT_STATUS_FAILED = 1;
  public static final int PAYMENT_STATUS_CANCELLED = 2;

  private final Context context;
  private final PayPalConfiguration configuration;
  private final PaymentRepositoryFactory paymentRepositoryFactory;

  public PayPalPayment(Context context, int id, String name, String description,
      PaymentRepositoryFactory paymentRepositoryFactory,
      PaymentAuthorizationRepository authorizationRepository, PayPalConfiguration configuration,
      boolean authorizationRequired, PaymentAuthorizationFactory authorizationFactory,
      Payer payer) {
    super(id, name, description, paymentRepositoryFactory);
    this.context = context;
    this.configuration = configuration;
    this.paymentRepositoryFactory = paymentRepositoryFactory;
  }

  @Override public Completable process(Product product) {
    final IntentFilter paymentResultFilter = new IntentFilter();
    paymentResultFilter.addAction(PAYMENT_RESULT_ACTION);
    return Observable.create(
        new BroadcastRegisterOnSubscribe(context, paymentResultFilter, null, null))
        .doOnSubscribe(() -> startPayPalActivity(product.getPrice(), product))
        .first(intent -> isPaymentConfirmationIntent(intent))
        .flatMap(intent -> getIntentPaymentConfirmationId(intent, getId(), product.getId()))
        .flatMap(paymentConfirmationId -> paymentRepositoryFactory.getPaymentConfirmationRepository(
            product)
            .createPaymentConfirmation(getId(), paymentConfirmationId, product)
            .toObservable())
        .toCompletable();
  }

  private void startPayPalActivity(Price price, Product product) {
    context.startActivity(PayPalPaymentActivity.getIntent(context,
        new com.paypal.android.sdk.payments.PayPalPayment(BigDecimal.valueOf(price.getAmount()),
            price.getCurrency(), product.getTitle(),
            com.paypal.android.sdk.payments.PayPalPayment.PAYMENT_INTENT_SALE), configuration));
  }

  private boolean isPaymentConfirmationIntent(Intent intent) {
    return intent != null && PAYMENT_RESULT_ACTION.equals(intent.getAction()) && intent.hasExtra(
        PAYMENT_STATUS_EXTRA);
  }

  private Observable<String> getIntentPaymentConfirmationId(Intent intent, int id, int productId) {
    final com.paypal.android.sdk.payments.PaymentConfirmation payPalConfirmation;
    switch (intent.getIntExtra(PAYMENT_STATUS_EXTRA, PAYMENT_STATUS_FAILED)) {
      case PAYMENT_STATUS_OK:
        payPalConfirmation = intent.getParcelableExtra(PAYMENT_CONFIRMATION_EXTRA);
        if (payPalConfirmation != null) {
          return Observable.just(payPalConfirmation.getProofOfPayment()
              .getPaymentId());
        }
        return Observable.error(
            new PaymentFailureException("PayPal payment returned invalid payment confirmation"));
      case PAYMENT_STATUS_CANCELLED:
        return Observable.error(
            new PaymentCancellationException("PayPal payment cancelled by user"));
      case PAYMENT_STATUS_FAILED:
      default:
        return Observable.error(new PaymentFailureException("PayPal payment failed"));
    }
  }
}