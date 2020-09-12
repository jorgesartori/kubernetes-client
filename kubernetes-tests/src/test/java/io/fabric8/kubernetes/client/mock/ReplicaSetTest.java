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

package io.fabric8.kubernetes.client.mock;

import io.fabric8.kubernetes.api.model.KubernetesListBuilder;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetBuilder;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetList;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetListBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@EnableRuleMigrationSupport
public class ReplicaSetTest {
  @Rule
  public KubernetesServer server = new KubernetesServer();

  @Test
  public void testList() {
   server.expect().withPath("/apis/apps/v1/namespaces/test/replicasets").andReturn(200, new ReplicaSetListBuilder().build()).once();
   server.expect().withPath("/apis/apps/v1/namespaces/ns1/replicasets").andReturn(200,  new ReplicaSetListBuilder()
      .addNewItem().and()
      .addNewItem().and().build())
      .once();

    KubernetesClient client = server.getClient();
    ReplicaSetList replicaSetList = client.apps().replicaSets().list();
    assertNotNull(replicaSetList);
    assertEquals(0, replicaSetList.getItems().size());

    replicaSetList = client.apps().replicaSets().inNamespace("ns1").list();
    assertNotNull(replicaSetList);
    assertEquals(2, replicaSetList.getItems().size());
  }


  @Test
  public void testGet() {
   server.expect().withPath("/apis/apps/v1/namespaces/test/replicasets/repl1").andReturn(200, new ReplicaSetBuilder().build()).once();
   server.expect().withPath("/apis/apps/v1/namespaces/ns1/replicasets/repl2").andReturn(200, new ReplicaSetBuilder().build()).once();

    KubernetesClient client = server.getClient();

    ReplicaSet repl1 = client.apps().replicaSets().withName("repl1").get();
    assertNotNull(repl1);

    repl1 = client.apps().replicaSets().withName("repl2").get();
    assertNull(repl1);

    repl1 = client.apps().replicaSets().inNamespace("ns1").withName("repl2").get();
    assertNotNull(repl1);
  }


  @Test
  public void testDelete() {
   server.expect().withPath("/apis/apps/v1/namespaces/test/replicasets/repl1").andReturn(200, new ReplicaSetBuilder() .withNewMetadata()
      .withName("repl1")
      .withResourceVersion("1")
      .endMetadata()
      .withNewSpec()
      .withReplicas(0)
      .endSpec()
      .withNewStatus()
      .withReplicas(1)
      .endStatus()
      .build()).once();

   server.expect().withPath("/apis/apps/v1/namespaces/test/replicasets/repl1").andReturn(200, new ReplicaSetBuilder() .withNewMetadata()
      .withName("repl1")
      .withResourceVersion("1")
      .endMetadata()
      .withNewSpec()
      .withReplicas(0)
      .endSpec()
      .withNewStatus()
      .withReplicas(0)
      .endStatus()
      .build()).times(5);

   server.expect().withPath("/apis/apps/v1/namespaces/ns1/replicasets/repl2").andReturn(200, new ReplicaSetBuilder() .withNewMetadata()
      .withName("repl2")
      .withResourceVersion("1")
      .endMetadata()
      .withNewSpec()
      .withReplicas(0)
      .endSpec()
      .withNewStatus()
      .withReplicas(1)
      .endStatus()
      .build()).once();

   server.expect().withPath("/apis/apps/v1/namespaces/ns1/replicasets/repl2").andReturn(200, new ReplicaSetBuilder() .withNewMetadata()
      .withName("repl2")
      .withResourceVersion("1")
      .endMetadata()
      .withNewSpec()
      .withReplicas(0)
      .endSpec()
      .withNewStatus()
      .withReplicas(0)
      .endStatus()
      .build()).times(5);

    KubernetesClient client = server.getClient();

    Boolean deleted = client.apps().replicaSets().withName("repl1").delete();
    assertTrue(deleted);

    deleted = client.apps().replicaSets().withName("repl2").delete();
    assertFalse(deleted);

    deleted = client.apps().replicaSets().inNamespace("ns1").withName("repl2").delete();
    assertTrue(deleted);
  }

  @Test
  public void testScale() {
   server.expect().withPath("/apis/apps/v1/namespaces/test/replicasets/repl1").andReturn(200, new ReplicaSetBuilder()
      .withNewMetadata()
      .withName("repl1")
      .withResourceVersion("1")
      .endMetadata()
      .withNewSpec()
      .withReplicas(5)
      .endSpec()
      .withNewStatus()
        .withReplicas(1)
      .endStatus()
      .build()).always();

    KubernetesClient client = server.getClient();
    ReplicaSet repl = client.apps().replicaSets().withName("repl1").scale(5);
    assertNotNull(repl);
    assertNotNull(repl.getSpec());
    assertEquals(5, repl.getSpec().getReplicas().intValue());
    assertEquals(1, repl.getStatus().getReplicas().intValue());
  }

  @Test
  public void testScaleAndWait() {
   server.expect().withPath("/apis/apps/v1/namespaces/test/replicasets/repl1").andReturn(200, new ReplicaSetBuilder()
      .withNewMetadata()
      .withName("repl1")
      .withResourceVersion("1")
      .endMetadata()
      .withNewSpec()
      .withReplicas(5)
      .endSpec()
      .withNewStatus()
        .withReplicas(1)
      .endStatus()
      .build()).once();

    server.expect().withPath("/apis/apps/v1/namespaces/test/replicasets/repl1").andReturn(200, new ReplicaSetBuilder()
        .withNewMetadata()
        .withName("repl1")
        .withResourceVersion("1")
        .endMetadata()
        .withNewSpec()
        .withReplicas(5)
        .endSpec()
        .withNewStatus()
        .withReplicas(5)
        .endStatus()
        .build()).always();

    KubernetesClient client = server.getClient();
    ReplicaSet repl = client.apps().replicaSets().withName("repl1").scale(5, true);
    assertNotNull(repl);
    assertNotNull(repl.getSpec());
    assertEquals(5, repl.getSpec().getReplicas().intValue());
    assertEquals(5, repl.getStatus().getReplicas().intValue());
  }

  @Disabled
  @Test
  public void testUpdate() {
    ReplicaSet repl1 = new ReplicaSetBuilder()
      .withNewMetadata()
      .withName("repl1")
      .withNamespace("test")
      .endMetadata()
      .withNewSpec()
        .withReplicas(1)
        .withNewTemplate()
          .withNewMetadata().withLabels(new HashMap<String, String>()).endMetadata()
          .withNewSpec()
            .addNewContainer()
              .withImage("img1")
            .endContainer()
          .endSpec()
        .endTemplate()
      .endSpec()
      .withNewStatus().withReplicas(1).endStatus()
      .build();

   server.expect().withPath("/apis/apps/v1/namespaces/test/replicasets/repl1").andReturn(200, repl1).once();
   server.expect().put().withPath("/apis/apps/v1/namespaces/test/replicasets/repl1").andReturn(200, repl1).once();
   server.expect().get().withPath("/apis/apps/v1/namespaces/test/replicasets").andReturn(200, new ReplicaSetListBuilder().withItems(repl1).build()).once();
   server.expect().post().withPath("/apis/apps/v1/namespaces/test/replicasets").andReturn(201, repl1).once();
   server.expect().withPath("/apis/apps/v1/namespaces/test/pods").andReturn(200, new KubernetesListBuilder().build()).once();
    KubernetesClient client = server.getClient();

    repl1 = client.apps().replicaSets().withName("repl1")
      .rolling()
      .withTimeout(5, TimeUnit.MINUTES)
      .updateImage("");
    assertNotNull(repl1);
  }

  @Test
  public void testDeprecatedApiVersion() {
    io.fabric8.kubernetes.api.model.extensions.ReplicaSet repl1 = new io.fabric8.kubernetes.api.model.extensions.ReplicaSetBuilder()
      .withApiVersion("extensions/v1beta1")
      .withNewMetadata()
      .withName("repl1")
      .withNamespace("test")
      .endMetadata()
      .withNewSpec()
      .withReplicas(1)
      .withNewTemplate()
      .withNewMetadata().withLabels(new HashMap<String, String>()).endMetadata()
      .withNewSpec()
      .addNewContainer()
      .withImage("img1")
      .endContainer()
      .endSpec()
      .endTemplate()
      .endSpec()
      .withNewStatus().withReplicas(1).endStatus()
      .build();

    server.expect().withPath("/apis/extensions/v1beta1/namespaces/test/replicasets").andReturn(200, repl1).once();

    KubernetesClient client = server.getClient();
    client.resource(repl1).inNamespace("test").createOrReplace();
  }

  @Test
  @DisplayName("Should update image based in single argument")
  void testRolloutUpdateSingleImage() throws InterruptedException {
    // Given
    String imageToUpdate = "nginx:latest";
    server.expect().get().withPath("/apis/apps/v1/namespaces/ns1/replicasets/replicaset1")
      .andReturn(HttpURLConnection.HTTP_OK, getReplicaSetBuilder().build()).times(3);
    server.expect().patch().withPath("/apis/apps/v1/namespaces/ns1/replicasets/replicaset1")
      .andReturn(HttpURLConnection.HTTP_OK, getReplicaSetBuilder()
        .editSpec().editTemplate().editSpec().editContainer(0)
        .withImage(imageToUpdate)
        .endContainer().endSpec().endTemplate().endSpec()
        .build()).times(2);
    KubernetesClient client = server.getClient();

    // When
    ReplicaSet replicationController = client.apps().replicaSets().inNamespace("ns1").withName("replicaset1")
      .rolling().updateImage(imageToUpdate);

    // Then
    assertNotNull(replicationController);
    assertEquals(imageToUpdate, replicationController.getSpec().getTemplate().getSpec().getContainers().get(0).getImage());
    RecordedRequest recordedRequest = server.getLastRequest();
    assertEquals("PATCH", recordedRequest.getMethod());
    assertTrue(recordedRequest.getBody().readUtf8().contains(imageToUpdate));
  }

  @Test
  @DisplayName("Should update image based in map based argument")
  void testRolloutUpdateImage() throws InterruptedException {
    // Given
    Map<String, String> containerToImageMap = Collections.singletonMap("nginx", "nginx:latest");
    server.expect().get().withPath("/apis/apps/v1/namespaces/ns1/replicasets/replicaset1")
      .andReturn(HttpURLConnection.HTTP_OK, getReplicaSetBuilder().build()).times(3);
    server.expect().patch().withPath("/apis/apps/v1/namespaces/ns1/replicasets/replicaset1")
      .andReturn(HttpURLConnection.HTTP_OK, getReplicaSetBuilder()
        .editSpec().editTemplate().editSpec().editContainer(0)
        .withImage(containerToImageMap.get("nginx"))
        .endContainer().endSpec().endTemplate().endSpec()
        .build()).once();
    KubernetesClient client = server.getClient();

    // When
    ReplicaSet replicationController = client.apps().replicaSets().inNamespace("ns1").withName("replicaset1")
      .rolling().updateImage(containerToImageMap);

    // Then
    assertNotNull(replicationController);
    assertEquals(containerToImageMap.get("nginx"), replicationController.getSpec().getTemplate().getSpec().getContainers().get(0).getImage());
    RecordedRequest recordedRequest = server.getLastRequest();
    assertEquals("PATCH", recordedRequest.getMethod());
    assertTrue(recordedRequest.getBody().readUtf8().contains(containerToImageMap.get("nginx")));
  }

  private ReplicaSetBuilder getReplicaSetBuilder() {
    return new ReplicaSetBuilder()
      .withNewMetadata()
      .withName("replicaset1")
      .addToLabels("app", "nginx")
      .addToAnnotations("app", "nginx")
      .endMetadata()
      .withNewSpec()
      .withReplicas(1)
      .withNewTemplate()
      .withNewMetadata().addToLabels("app", "nginx").endMetadata()
      .withNewSpec()
      .addNewContainer()
      .withName("nginx")
      .withImage("nginx:1.7.9")
      .addNewPort().withContainerPort(80).endPort()
      .endContainer()
      .endSpec()
      .endTemplate()
      .endSpec();

  }
}
