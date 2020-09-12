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
package io.fabric8.openshift.client.dsl;

import io.fabric8.kubernetes.client.Client;
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

public interface OpenShiftOperatorHubAPIGroupDSL extends Client {
  /**
   * API entrypoint for CatalogSource related operations(operators.coreos.com/v1alpha1)
   *
   * @return MixedOperation object for CatalogSource type
   */
  MixedOperation<CatalogSource, CatalogSourceList, DoneableCatalogSource, Resource<CatalogSource, DoneableCatalogSource>> catalogSources();
  /**
   * API entrypoint for OperatorGroup related operations(operators.coreos.com/v1)
   *
   * @return MixedOperation object for OperatorGroup type
   */
  MixedOperation<OperatorGroup, OperatorGroupList, DoneableOperatorGroup, Resource<OperatorGroup, DoneableOperatorGroup>> operatorGroups();
  /**
   * API entrypoint for Subscription related operations(operators.coreos.com/v1alpha1)
   *
   * @return MixedOperation object for Subscription type
   */
  MixedOperation<Subscription, SubscriptionList, DoneableSubscription, Resource<Subscription, DoneableSubscription>> subscriptions();
  /**
   * API entrypoint for InstallPlan related operations(operators.coreos.com/v1alpha1)
   *
   * @return MixedOperation object for InstallPlan type
   */
  MixedOperation<InstallPlan, InstallPlanList, DoneableInstallPlan, Resource<InstallPlan, DoneableInstallPlan>> installPlans();
  /**
   * API entrypoint for CatalogSource related ClusterServiceVersion(operators.coreos.com/v1alpha1)
   *
   * @return MixedOperation object for ClusterServiceVersion type
   */
  MixedOperation<ClusterServiceVersion, ClusterServiceVersionList, DoneableClusterServiceVersion, Resource<ClusterServiceVersion, DoneableClusterServiceVersion>> clusterServiceVersions();
}
