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
import io.fabric8.openshift.api.model.monitoring.v1.Alertmanager;
import io.fabric8.openshift.api.model.monitoring.v1.AlertmanagerList;
import io.fabric8.openshift.api.model.monitoring.v1.DoneableAlertmanager;
import io.fabric8.openshift.api.model.monitoring.v1.DoneablePodMonitor;
import io.fabric8.openshift.api.model.monitoring.v1.DoneablePrometheus;
import io.fabric8.openshift.api.model.monitoring.v1.DoneablePrometheusRule;
import io.fabric8.openshift.api.model.monitoring.v1.DoneableServiceMonitor;
import io.fabric8.openshift.api.model.monitoring.v1.PodMonitor;
import io.fabric8.openshift.api.model.monitoring.v1.PodMonitorList;
import io.fabric8.openshift.api.model.monitoring.v1.Prometheus;
import io.fabric8.openshift.api.model.monitoring.v1.PrometheusList;
import io.fabric8.openshift.api.model.monitoring.v1.PrometheusRule;
import io.fabric8.openshift.api.model.monitoring.v1.PrometheusRuleList;
import io.fabric8.openshift.api.model.monitoring.v1.ServiceMonitor;
import io.fabric8.openshift.api.model.monitoring.v1.ServiceMonitorList;
import io.fabric8.openshift.client.dsl.OpenShiftMonitoringAPIGroupDSL;
import io.fabric8.openshift.client.dsl.internal.AlertmanagerOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.PodMonitorOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.PrometheusOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.PrometheusRuleOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.ServiceMonitorOperationsImpl;
import okhttp3.OkHttpClient;

public class OpenShiftMonitoringAPIGroupClient extends BaseClient implements OpenShiftMonitoringAPIGroupDSL {
  public OpenShiftMonitoringAPIGroupClient() {
    super();
  }

  public OpenShiftMonitoringAPIGroupClient(OkHttpClient httpClient, final Config config) {
    super(httpClient, config);
  }

  @Override
  public MixedOperation<Prometheus, PrometheusList, DoneablePrometheus, Resource<Prometheus, DoneablePrometheus>> prometheuses() {
    return new PrometheusOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<PodMonitor, PodMonitorList, DoneablePodMonitor, Resource<PodMonitor, DoneablePodMonitor>> podMonitors() {
    return new PodMonitorOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<Alertmanager, AlertmanagerList, DoneableAlertmanager, Resource<Alertmanager, DoneableAlertmanager>> alertmanagers() {
    return new AlertmanagerOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<PrometheusRule, PrometheusRuleList, DoneablePrometheusRule, Resource<PrometheusRule, DoneablePrometheusRule>> prometheusRules() {
    return new PrometheusRuleOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<ServiceMonitor, ServiceMonitorList, DoneableServiceMonitor, Resource<ServiceMonitor, DoneableServiceMonitor>> serviceMonitors() {
    return new ServiceMonitorOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }
}
