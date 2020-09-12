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
package io.fabric8.kubernetes.client.utils;

import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apiextensions.v1beta1.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.apiextensions.v1beta1.JSONSchemaProps;
import io.fabric8.kubernetes.api.model.apps.Deployment;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class SerializationTest {


  @Test
  void unmarshalCRDWithSchema() throws Exception {
    final String input = readYamlToString("/test-crd-schema.yml");
    final CustomResourceDefinition crd = Serialization.unmarshal(input, CustomResourceDefinition.class);
    JSONSchemaProps spec = crd.getSpec()
      .getValidation()
      .getOpenAPIV3Schema()
      .getProperties()
      .get("spec");

    assertEquals(spec.getRequired().size(), 3);
    assertEquals(spec.getRequired().get(0), "builderName");
    assertEquals(spec.getRequired().get(1), "edges");
    assertEquals(spec.getRequired().get(2), "dimensions");

    Map<String, JSONSchemaProps> properties = spec.getProperties();
    assertNotNull(properties.get("builderName"));
    assertEquals(properties.get("builderName").getExample(), new TextNode("builder-example"));
    assertEquals(properties.get("hollow").getDefault(), BooleanNode.FALSE);

    assertNotNull(properties.get("dimensions"));
    assertNotNull(properties.get("dimensions").getProperties().get("x"));
    assertEquals(properties.get("dimensions").getProperties().get("x").getDefault(), new IntNode(10));

    String output = Serialization.asYaml(crd);
    assertEquals(input.trim(), output.trim());
  }

  @Test
  @DisplayName("unmarshal, String containing List with windows like line-ends (CRLF), all list items should be available")
  void unmarshalListWithWindowsLineSeparatorsString() throws Exception {
    // Given
    final String crlfFile = readYamlToString("/test-list.yml");
    // When
    final KubernetesResource result = Serialization.unmarshal(crlfFile, KubernetesResource.class);
    // Then
    assertTrue(result instanceof KubernetesList);
    final KubernetesList kubernetesList = (KubernetesList)result;
    assertEquals(2, kubernetesList.getItems().size());
    assertTrue(kubernetesList.getItems().get(0) instanceof Service);
    assertTrue(kubernetesList.getItems().get(1) instanceof Deployment);
  }

  private String readYamlToString(String path) throws IOException {
    return Files.readAllLines(
      new File(SerializationTest.class.getResource(path).getFile()).toPath(), StandardCharsets.UTF_8)
      .stream()
      .filter(line -> !line.startsWith("#"))
      .collect(Collectors.joining("\n"));
  }

  @Test
  @DisplayName("containsMultipleDocuments, multiple documents with windows line ends, should return true")
  void containsMultipleDocumentsWithMultipleDocumentsAndWindowsLineEnds() {
    // Given
    final String multiDocument = "---\r\napiVersion: v1\r\nKind: Something\r\n\r\n---\r\napiVersion: v2\r\nKind: Other";
    // When
    final boolean result = Serialization.containsMultipleDocuments(multiDocument);
    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("containsMultipleDocuments, single document with windows line ends, should return false")
  void containsMultipleDocumentsWithSingleDocumentAndWindowsLineEnds() {
    // Given
    final String multiDocument = "---\r\napiVersion: v1\r\nKind: Something\r\n\r\n";
    // When
    final boolean result = Serialization.containsMultipleDocuments(multiDocument);
    // Then
    assertFalse(result);
  }

  @Test
  @DisplayName("containsMultipleDocuments, multiple documents with linux line ends, should return true")
  void containsMultipleDocumentsWithMultipleDocumentsAndLinuxLineEnds() {
    // Given
    final String multiDocument = "---\napiVersion: v1\nKind: Something\n\n---\napiVersion: v2\nKind: Other";
    // When
    final boolean result = Serialization.containsMultipleDocuments(multiDocument);
    // Then
    assertTrue(result);
  }

  @Test
  @DisplayName("containsMultipleDocuments, single document with linux line ends, should return false")
  void containsMultipleDocumentsWithSingleDocumentAndLinuxLineEnds() {
    // Given
    final String multiDocument = "---\napiVersion: v1\nKind: Something\n\n";
    // When
    final boolean result = Serialization.containsMultipleDocuments(multiDocument);
    // Then
    assertFalse(result);
  }
}
