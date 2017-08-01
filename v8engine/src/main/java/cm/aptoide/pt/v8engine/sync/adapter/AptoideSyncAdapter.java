/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync.adapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.v8engine.billing.authorization.AuthorizationService;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.transaction.TransactionPersistence;
import cm.aptoide.pt.v8engine.billing.transaction.TransactionService;

public class AptoideSyncAdapter extends AbstractThreadedSyncAdapter {

  public static final String EXTRA_PAYMENT_ID = "cm.aptoide.pt.v8engine.repository.sync.PAYMENT_ID";
  public static final String EXTRA_PAYMENT_AUTHORIZATIONS =
      "cm.aptoide.pt.v8engine.repository.sync.EXTRA_PAYMENT_AUTHORIZATIONS";
  public static final String EXTRA_PAYMENT_TRANSACTIONS =
      "cm.aptoide.pt.v8engine.repository.sync.EXTRA_PAYMENT_CONFIRMATIONS";

  private final ProductBundleMapper productConverter;
  private final TransactionPersistence transactionPersistence;
  private final BillingAnalytics billingAnalytics;
  private final Payer payer;
  private final TransactionService transactionService;
  private final AuthorizationService authorizationService;
  private final AuthorizationPersistence authorizationPersistence;

  public AptoideSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs,
      ProductBundleMapper productConverter, BillingAnalytics billingAnalytics, Payer payer,
      TransactionService transactionService, TransactionPersistence transactionPersistence,
      AuthorizationService authorizationService,
      AuthorizationPersistence authorizationPersistence) {
    super(context, autoInitialize, allowParallelSyncs);
    this.productConverter = productConverter;
    this.transactionPersistence = transactionPersistence;
    this.billingAnalytics = billingAnalytics;
    this.payer = payer;
    this.transactionService = transactionService;
    this.authorizationService = authorizationService;
    this.authorizationPersistence = authorizationPersistence;
  }

  @Override public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {
    final boolean authorizations = extras.getBoolean(EXTRA_PAYMENT_AUTHORIZATIONS);
    final boolean transactions = extras.getBoolean(EXTRA_PAYMENT_TRANSACTIONS);

    if (transactions) {
      final Product product = productConverter.mapToProduct(extras);
      new TransactionSync(product, transactionPersistence, payer, billingAnalytics,
          transactionService).sync(syncResult);
    } else if (authorizations) {
      final int paymentId = extras.getInt(EXTRA_PAYMENT_ID);
      new AuthorizationSync(paymentId, payer, billingAnalytics, authorizationService,
          authorizationPersistence).sync(syncResult);
    }
  }
}
