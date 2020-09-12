/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.openshift.client;

import io.fabric8.kubernetes.client.BaseClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.openshift.api.model.operatorhub.v1.DoneableOperatorGroup;
import io.fabric8.openshift.api.model.operatorhub.v1.OperatorGroup;
import io.fabric8.openshift.api.model.operatorhub.v1.OperatorGroupList;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.CatalogSource;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.CatalogSourceList;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.ClusterServiceVersion;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.ClusterServiceVersionList;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.DoneableCatalogSource;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.DoneableClusterServiceVersion;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.DoneableInstallPlan;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.DoneableSubscription;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.InstallPlan;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.InstallPlanList;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.Subscription;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.SubscriptionList;
import io.fabric8.openshift.client.dsl.OpenShiftOperatorHubAPIGroupDSL;
import io.fabric8.openshift.client.dsl.internal.CatalogSourceOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.ClusterServiceVersionOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.InstallPlanOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.OperatorGroupOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.SubscriptionOperationsImpl;
import okhttp3.OkHttpClient;

public class OpenShiftOperatorHubAPIGroupClient extends BaseClient implements OpenShiftOperatorHubAPIGroupDSL {
  public OpenShiftOperatorHubAPIGroupClient() {
    super();
  }

  public OpenShiftOperatorHubAPIGroupClient(OkHttpClient httpClient, final Config config) {
    super(httpClient, config);
  }

  @Override
  public MixedOperation<CatalogSource, CatalogSourceList, DoneableCatalogSource, Resource<CatalogSource, DoneableCatalogSource>> catalogSources() {
    return new CatalogSourceOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<OperatorGroup, OperatorGroupList, DoneableOperatorGroup, Resource<OperatorGroup, DoneableOperatorGroup>> operatorGroups() {
    return new OperatorGroupOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<Subscription, SubscriptionList, DoneableSubscription, Resource<Subscription, DoneableSubscription>> subscriptions() {
    return new SubscriptionOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<InstallPlan, InstallPlanList, DoneableInstallPlan, Resource<InstallPlan, DoneableInstallPlan>> installPlans() {
    return new InstallPlanOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<ClusterServiceVersion, ClusterServiceVersionList, DoneableClusterServiceVersion, Resource<ClusterServiceVersion, DoneableClusterServiceVersion>> clusterServiceVersions() {
    return new ClusterServiceVersionOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }
}
