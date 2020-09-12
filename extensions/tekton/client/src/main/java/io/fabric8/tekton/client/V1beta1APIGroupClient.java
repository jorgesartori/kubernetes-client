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
package io.fabric8.tekton.client;

import io.fabric8.kubernetes.client.BaseClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.tekton.client.dsl.V1beta1APIGroupDSL;
import io.fabric8.tekton.client.internal.v1beta1.ClusterTaskOperationsImpl;
import io.fabric8.tekton.client.internal.v1beta1.PipelineRunOperationsImpl;
import io.fabric8.tekton.client.internal.v1beta1.TaskOperationsImpl;
import io.fabric8.tekton.client.internal.v1beta1.TaskRunOperationsImpl;
import io.fabric8.tekton.pipeline.v1beta1.ClusterTask;
import io.fabric8.tekton.pipeline.v1beta1.ClusterTaskList;
import io.fabric8.tekton.pipeline.v1beta1.DoneableClusterTask;
import io.fabric8.tekton.pipeline.v1beta1.DoneablePipeline;
import io.fabric8.tekton.pipeline.v1beta1.DoneablePipelineRun;
import io.fabric8.tekton.pipeline.v1beta1.DoneableTask;
import io.fabric8.tekton.pipeline.v1beta1.DoneableTaskRun;
import io.fabric8.tekton.pipeline.v1beta1.Pipeline;
import io.fabric8.tekton.pipeline.v1beta1.PipelineList;
import io.fabric8.tekton.pipeline.v1beta1.PipelineRun;
import io.fabric8.tekton.pipeline.v1beta1.PipelineRunList;
import io.fabric8.tekton.pipeline.v1beta1.Task;
import io.fabric8.tekton.pipeline.v1beta1.TaskList;
import io.fabric8.tekton.pipeline.v1beta1.TaskRun;
import io.fabric8.tekton.pipeline.v1beta1.TaskRunList;
import io.fabric8.tekton.client.internal.v1beta1.PipelineOperationsImpl;
import okhttp3.OkHttpClient;

public class V1beta1APIGroupClient extends BaseClient implements V1beta1APIGroupDSL {
  public V1beta1APIGroupClient() {
    super();
  }

  public V1beta1APIGroupClient(OkHttpClient httpClient, final Config config) {
    super(httpClient, config);
  }

  @Override
  public MixedOperation<Pipeline, PipelineList, DoneablePipeline, Resource<Pipeline, DoneablePipeline>> pipelines() {
    return new PipelineOperationsImpl(this.getHttpClient(), this.getConfiguration());
  }

  @Override
  public MixedOperation<PipelineRun, PipelineRunList, DoneablePipelineRun, Resource<PipelineRun, DoneablePipelineRun>> pipelineRuns() {
    return new PipelineRunOperationsImpl(this.getHttpClient(), this.getConfiguration());
  }

  @Override
  public MixedOperation<Task, TaskList, DoneableTask, Resource<Task, DoneableTask>> tasks() {
    return new TaskOperationsImpl(this.getHttpClient(), this.getConfiguration());
  }

  @Override
  public MixedOperation<TaskRun, TaskRunList, DoneableTaskRun, Resource<TaskRun, DoneableTaskRun>> taskRuns() {
    return new TaskRunOperationsImpl(this.getHttpClient(), this.getConfiguration());
  }

  @Override
  public NonNamespaceOperation<ClusterTask, ClusterTaskList, DoneableClusterTask, Resource<ClusterTask, DoneableClusterTask>> clusterTasks() {
    return new ClusterTaskOperationsImpl(this.getHttpClient(), this.getConfiguration());
  }
}
