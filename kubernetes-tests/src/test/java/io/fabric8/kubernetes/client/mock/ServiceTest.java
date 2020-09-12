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

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.extensions.IngressListBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import java.net.HttpURLConnection;
import java.util.Collections;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@EnableRuleMigrationSupport
class ServiceTest {
  @Rule
  public KubernetesServer server = new KubernetesServer();

  Service service;

  @BeforeEach
  void prepareService() {
    service = new ServiceBuilder()
      .withNewMetadata()
      .withName("httpbin")
      .withLabels(Collections.singletonMap("app", "httpbin"))
      .endMetadata()
      .withNewSpec()
      .addNewPort()
      .withName("http")
      .withPort(5511)
      .withTargetPort(new IntOrString(8080))
      .endPort()
      .addToSelector("deploymentconfig", "httpbin")
      .endSpec()
      .build();
  }

  @Test
  void testLoad() {
    KubernetesClient client = server.getClient();
    Service svc = client.services().load(getClass().getResourceAsStream("/test-service.yml")).get();
    assertNotNull(svc);
    assertEquals("httpbin", svc.getMetadata().getName());
  }

  @Test
  void testCreate() {
    server.expect().post()
      .withPath("/api/v1/namespaces/test/services")
      .andReturn(200, service)
      .once();

    KubernetesClient client = server.getClient();
    Service responseSvc = client.services().inNamespace("test").create(service);
    assertNotNull(responseSvc);
    assertEquals("httpbin", responseSvc.getMetadata().getName());
  }

  @Test
  void testReplace() throws InterruptedException {
    Service serviceFromServer = new ServiceBuilder(service)
      .editOrNewSpec().withClusterIP("10.96.129.1").endSpec().build();
    Service serviceUpdated = new ServiceBuilder(service)
      .editOrNewSpec().withClusterIP("10.96.129.1").endSpec()
      .editMetadata().addToAnnotations("foo", "bar").endMetadata()
      .build();

    server.expect().get()
      .withPath("/api/v1/namespaces/test/services/httpbin")
      .andReturn(HttpURLConnection.HTTP_OK, serviceFromServer)
      .times(3);

    server.expect().post()
      .withPath("/api/v1/namespaces/test/services")
      .andReturn(HttpURLConnection.HTTP_CONFLICT, serviceFromServer)
      .once();

    server.expect().put()
      .withPath("/api/v1/namespaces/test/services/httpbin")
      .andReturn(HttpURLConnection.HTTP_OK, serviceUpdated)
      .once();

    KubernetesClient client = server.getClient();
    Service responseSvc = client.services().inNamespace("test").createOrReplace(serviceUpdated);
    assertNotNull(responseSvc);
    assertEquals("httpbin", responseSvc.getMetadata().getName());

    RecordedRequest recordedRequest = server.getLastRequest();
    assertEquals("PUT", recordedRequest.getMethod());
    assertEquals("{\"apiVersion\":\"v1\",\"kind\":\"Service\",\"metadata\":{\"annotations\":{\"foo\":\"bar\"},\"labels\":{\"app\":\"httpbin\"},\"name\":\"httpbin\"},\"spec\":{\"clusterIP\":\"10.96.129.1\",\"ports\":[{\"name\":\"http\",\"port\":5511,\"targetPort\":8080}],\"selector\":{\"deploymentconfig\":\"httpbin\"}}}",
      recordedRequest.getBody().readUtf8());
  }

  @Test
  void testDelete() {
    server.expect().delete()
      .withPath("/api/v1/namespaces/test/services/httpbin")
      .andReturn(200, service)
      .once();

    KubernetesClient client = server.getClient();
    boolean isDeleted = client.services().inNamespace("test").withName("httpbin").delete();
    assertTrue(isDeleted);
  }

  @Test
  void testUpdate() {
    Service serviceFromServer = new ServiceBuilder(service)
      .editOrNewMetadata().addToLabels("foo", "bar").endMetadata()
      .editOrNewSpec().withClusterIP("10.96.129.1").endSpec().build();

    server.expect().get()
      .withPath("/api/v1/namespaces/test/services/httpbin")
      .andReturn(200, serviceFromServer)
      .times(3);
    server.expect().patch()
      .withPath("/api/v1/namespaces/test/services/httpbin")
      .andReturn(200, serviceFromServer)
      .once();

    KubernetesClient client = server.getClient();
    Service responseFromServer = client.services().inNamespace("test").withName("httpbin").edit()
      .editOrNewMetadata().addToLabels("foo", "bar").endMetadata()
      .done();

    assertNotNull(responseFromServer);
    assertEquals("bar", responseFromServer.getMetadata().getLabels().get("foo"));
  }

  @Test
  void testGetUrlFromClusterIPService() {
    // Given
    Service service = new ServiceBuilder()
      .withNewMetadata().withName("svc1").endMetadata()
      .withNewSpec()
      .withClusterIP("187.30.14.32")
      .addNewPort()
      .withName("http")
      .withPort(8080)
      .withProtocol("TCP")
      .withNewTargetPort().withIntVal(8080).endTargetPort()
      .endPort()
      .withType("ClusterIP")
      .endSpec()
      .build();
    server.expect().get().withPath("/api/v1/namespaces/ns1/services/svc1")
      .andReturn(HttpURLConnection.HTTP_OK, service).always();
    server.expect().get().withPath("/apis/extensions/v1beta1/namespaces/ns1/ingresses")
      .andReturn(HttpURLConnection.HTTP_OK, new IngressListBuilder().build()).always();
    KubernetesClient client = server.getClient();

    // When
    String url = client.services().inNamespace("ns1").withName("svc1").getURL("http");

    // Then
    assertEquals("tcp://187.30.14.32:8080", url);
  }
}
