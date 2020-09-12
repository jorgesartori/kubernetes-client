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
package io.fabric8.kubernetes.client.dsl;

import io.fabric8.kubernetes.api.model.ListOptions;

public interface Watchable<H, W> {

  /**
   * Watch returns {@link H} interface that watches requested resource
   *
   * @param watcher Watcher interface of Kubernetes resource
   * @return watch interface {@link H}
   */
    H watch(W watcher);

  /**
   * Watch returns {@link H} interface that watches requested resource
   *
   * @param options options available for watch operation
   * @param watcher Watcher interface of Kubernetes resource
   * @return watch interface {@link H}
   */
    H watch(ListOptions options, W watcher);

  /**
   * Watch returns {@link H} interface that watches requested resource from
   * specified resourceVersion
   *
   * @param resourceVersion resource version from where to start watch
   * @param watcher Watcher interface of Kubernetes resource
   * @deprecated Please use {@link #watch(ListOptions, Object)} instead, it has a parameter of resourceVersion
   * @return watch interface {@link H}
   */
    @Deprecated
    H watch(String resourceVersion, W watcher);

}
