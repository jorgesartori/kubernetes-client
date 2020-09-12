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
package io.fabric8.kubernetes.client;

import io.fabric8.kubernetes.api.model.DoneablePodTemplate;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.EventList;
import io.fabric8.kubernetes.api.model.PodTemplate;
import io.fabric8.kubernetes.api.model.PodTemplateList;
import io.fabric8.kubernetes.api.model.DoneableEvent;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.V1APIGroupDSL;
import io.fabric8.kubernetes.client.dsl.internal.core.v1.PodTemplateOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.core.v1.EventOperationsImpl;
import okhttp3.OkHttpClient;

public class V1APIGroupClient extends BaseClient implements V1APIGroupDSL {
  public V1APIGroupClient() {
    super();
  }

  public V1APIGroupClient(OkHttpClient httpClient, final Config config) {
    super(httpClient, config);
  }

  @Override
  public MixedOperation<PodTemplate, PodTemplateList, DoneablePodTemplate, Resource<PodTemplate, DoneablePodTemplate>> podTemplates() {
    return new PodTemplateOperationsImpl(httpClient, getConfiguration());
  }

  @Override
  public MixedOperation<Event, EventList, DoneableEvent, Resource<Event, DoneableEvent>> events() {
    return new EventOperationsImpl(httpClient, getConfiguration());
  }
}
