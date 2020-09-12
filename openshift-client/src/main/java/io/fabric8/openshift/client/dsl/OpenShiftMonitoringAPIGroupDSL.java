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

public interface OpenShiftMonitoringAPIGroupDSL extends Client {
  /**
   * API entrypoint for Prometheus related operations(monitoring.coreos.com/v1)
   *
   * @return MixedOperation object for Prometheus type
   */
  MixedOperation<Prometheus, PrometheusList, DoneablePrometheus, Resource<Prometheus, DoneablePrometheus>> prometheuses();
  /**
   * API entrypoint for PodMonitor related operations(monitoring.coreos.com/v1)
   *
   * @return MixedOperation object for PodMonitor type
   */
  MixedOperation<PodMonitor, PodMonitorList, DoneablePodMonitor, Resource<PodMonitor, DoneablePodMonitor>> podMonitors();
  /**
   * API entrypoint for Alertmanager related operations(monitoring.coreos.com/v1)
   *
   * @return MixedOperation object for Alertmanager type
   */
  MixedOperation<Alertmanager, AlertmanagerList, DoneableAlertmanager, Resource<Alertmanager, DoneableAlertmanager>> alertmanagers();
  /**
   * API entrypoint for PrometheusRule related operations(monitoring.coreos.com/v1)
   *
   * @return MixedOperation object for PrometheusRule type
   */
  MixedOperation<PrometheusRule, PrometheusRuleList, DoneablePrometheusRule, Resource<PrometheusRule, DoneablePrometheusRule>> prometheusRules();
  /**
   * API entrypoint for ServiceMonitor related operations(monitoring.coreos.com/v1)
   *
   * @return MixedOperation object for ServiceMonitor type
   */
  MixedOperation<ServiceMonitor, ServiceMonitorList, DoneableServiceMonitor, Resource<ServiceMonitor, DoneableServiceMonitor>> serviceMonitors();
}
