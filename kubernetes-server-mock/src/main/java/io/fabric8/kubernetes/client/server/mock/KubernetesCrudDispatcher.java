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
package io.fabric8.kubernetes.client.server.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.mockwebserver.Context;
import io.fabric8.mockwebserver.crud.Attribute;
import io.fabric8.mockwebserver.crud.AttributeSet;
import io.fabric8.mockwebserver.crud.CrudDispatcher;
import io.fabric8.mockwebserver.crud.ResponseComposer;
import io.fabric8.zjsonpatch.JsonPatch;
import okhttp3.mockwebserver.MockResponse;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesCrudDispatcher extends CrudDispatcher {

  private static final String POST = "POST";
  private static final String PUT = "PUT";
  private static final String PATCH = "PATCH";
  private static final String GET = "GET";
  private static final String DELETE = "DELETE";

  private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesCrudDispatcher.class);
  private final Set<WatchEventsListener> watchEventListeners = new CopyOnWriteArraySet<>();

  public KubernetesCrudDispatcher() {
    this(new KubernetesCrudAttributesExtractor(), new KubernetesResponseComposer());
  }

  public KubernetesCrudDispatcher(KubernetesCrudAttributesExtractor attributeExtractor, ResponseComposer responseComposer) {
    super(new Context(Serialization.jsonMapper()), attributeExtractor, responseComposer);
  }

  @Override
  public MockResponse dispatch(RecordedRequest request) {
    String path = request.getPath();
    String method = request.getMethod();
    switch (method.toUpperCase()) {
      case POST:
        return handleCreate(path, request.getBody().readUtf8());
      case PUT:
        return handleReplace(path, request.getBody().readUtf8());
      case PATCH:
        return handlePatch(path, request.getBody().readUtf8());
      case GET:
        return detectWatchMode(path)? handleWatch(path): handleGet(path);
      case DELETE:
        return handleDelete(path);
      default:
        return null;
    }
  }

  /**
   * Adds the specified object to the in-memory db.
   *
   * @param path String
   * @param s String
   * @return The {@link MockResponse}
   */
  @Override
  public MockResponse handleCreate(String path, String s) {
    return new MockResponse().setResponseCode(doCreate(path, s, "ADDED")).setBody(s);
  }

  /**
   * Replace the object on `path` endpoint with the object represented by `s`
   * @param path String
   * @param s String
   * @return The {@link MockResponse}
   */
  public MockResponse handleReplace(String path, String s) {
    if (doDelete(path, null) == 404) {
      return new MockResponse().setResponseCode(404);
    }
    return new MockResponse().setResponseCode(doCreate(path, s, "MODIFIED")).setBody(s);
  }

  /**
   * Performs a get for the corresponding object from the in-memory db.
   *
   * @param path The path.
   * @return The {@link MockResponse}
   */
  @Override
  public MockResponse handleGet(String path) {
    MockResponse response = new MockResponse();
    List<String> items = new ArrayList<>();
    AttributeSet query = attributeExtractor.fromPath(path);

    map.entrySet().stream().filter(entry -> entry.getKey()
      .matches(query)).forEach(entry -> {
      LOGGER.debug("Entry found for query {} : {}", query, entry);
      items.add(entry.getValue());
    });

    if (query.containsKey(KubernetesAttributesExtractor.NAME)) {
      if (!items.isEmpty()) {
        response.setBody(items.get(0));
        response.setResponseCode(HttpURLConnection.HTTP_OK);
      } else {
        response.setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
      }
    } else {
      response.setBody(responseComposer.compose(items));
      response.setResponseCode(HttpURLConnection.HTTP_OK);
    }
    return response;
  }

  /**
   * Patches the specified object to the in-memory db.
   *
   * @param path path of resource
   * @param s object
   * @return The {@link MockResponse}
   */
  @Override
  public MockResponse handlePatch(String path, String s) {
    MockResponse response = new MockResponse();
    String body = fetchResource(path);
    if (body == null) {
      response.setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    } else {
      try {
        JsonNode patch = context.getMapper().readTree(s);
        JsonNode source = context.getMapper().readTree(body);
        JsonNode updated = JsonPatch.apply(patch, source);
        String updatedAsString = context.getMapper().writeValueAsString(updated);

        AttributeSet attributeSet = null;

        AttributeSet query = attributeExtractor.fromPath(path);

        attributeSet = map.entrySet().stream()
          .filter(entry -> entry.getKey().matches(query))
          .findFirst().orElseThrow(IllegalStateException::new).getKey();

        map.remove(attributeSet);
        AttributeSet newAttributeSet = AttributeSet.merge(attributeSet, attributeExtractor.fromResource(updatedAsString));
        map.put(newAttributeSet, updatedAsString);

        final AtomicBoolean flag = new AtomicBoolean(false);
        AttributeSet finalAttributeSet = attributeSet;
        watchEventListeners.stream()
          .filter(watchEventsListener -> watchEventsListener.attributeMatches(finalAttributeSet))
          .forEach(watchEventsListener -> {
              flag.set(true);
              watchEventsListener.sendWebSocketResponse(updatedAsString, "MODIFIED");
          });

        if (!flag.get()) {
          watchEventListeners.stream()
            .filter(watchEventsListener -> watchEventsListener.attributeMatches(newAttributeSet))
            .forEach(watchEventsListener -> watchEventsListener.sendWebSocketResponse(updatedAsString, "ADDED"));
        }

        response.setResponseCode(HttpURLConnection.HTTP_ACCEPTED);
        response.setBody(updatedAsString);
      } catch (JsonProcessingException e) {
        throw new IllegalArgumentException(e);
      }
    }
    return response;
  }

  /**
   * Performs a delete for the corresponding object from the in-memory db.
   *
   * @param path String
   * @return The {@link MockResponse}
   */
  @Override
  public MockResponse handleDelete(String path) {
    return new MockResponse().setResponseCode(doDelete(path, "DELETED"));
  }

  /**
   * Watch the resource list on `path` endpoint
   *
   * @param path String
   * @return The {@link MockResponse}
   */
  public MockResponse handleWatch(String path) {
    MockResponse mockResponse = new MockResponse();
    String resourceName = fetchResourceNameFromWatchRequestPath(path);
    AttributeSet query = attributeExtractor.fromPath(path);
    if (resourceName != null) {
      query = query.add(new Attribute("name", resourceName));
    }
    WatchEventsListener watchEventListener = new WatchEventsListener(context, query, watchEventListeners, LOGGER);
    watchEventListeners.add(watchEventListener);
    mockResponse.setSocketPolicy(SocketPolicy.KEEP_OPEN);
    return mockResponse.withWebSocketUpgrade(watchEventListener);
  }

  private boolean detectWatchMode(String path) {
    String queryString = null;
    try {
      queryString = new URI(path).getQuery();
    } catch (URISyntaxException e) {
      LOGGER.debug("incorrect URI string: [{}]", path);
      return false;
    }
    if (queryString != null && !queryString.isEmpty()) {
      return queryString.contains("watch=true");
    }
    return false;
  }

  private String fetchResourceNameFromWatchRequestPath(String path) {
    String queryString = null;
    try {
      queryString = new URI(path).getQuery();
    } catch (URISyntaxException e) {
      LOGGER.debug("Incorrect URI string: [{}]", path);
      return null;
    }

    if (queryString == null || queryString.isEmpty()) {
      return null;
    }

    String name = "";
    for (String q: queryString.split("&")) {
      if (q.contains("fieldSelector") && q.contains("metadata.name")) {
        String[] s = q.split("=");
        name = s[s.length - 1];
      }
    }
    return name.isEmpty()? null: name;
  }

  private String fetchResource(String path) {
    List<String> items = new ArrayList<>();
    AttributeSet query = attributeExtractor.fromPath(path);

    map.entrySet().stream()
      .filter(entry -> entry.getKey().matches(query))
      .forEach(entry -> items.add(entry.getValue()));

    if (items.isEmpty()) {
      return null;
    } else if (items.size() == 1) {
      return items.get(0);
    } else {
      return responseComposer.compose(items);
    }
  }


  private int doDelete(String path, String event) {
    List<AttributeSet> items = new ArrayList<>();
    AttributeSet query = attributeExtractor.fromPath(path);

    map.entrySet().stream()
      .filter(entry -> entry.getKey().matches(query))
      .forEach(entry -> items.add(entry.getKey()));

    if (items.isEmpty()) return HttpURLConnection.HTTP_NOT_FOUND;

    items.stream().forEach(item -> {
      if (event != null && !event.isEmpty()) {
        watchEventListeners.stream()
          .filter(listener -> listener.attributeMatches(item))
          .forEach(listener -> listener.sendWebSocketResponse(map.get(item), event));
        map.remove(item);
      }
    });
    return HttpURLConnection.HTTP_OK;
  }

  private int doCreate(String path, String s, String event) {
    AttributeSet features = AttributeSet.merge(attributeExtractor.fromPath(path), attributeExtractor.fromResource(s));
    map.put(features, s);

    if (event != null && !event.isEmpty()) {
      watchEventListeners.stream()
        .filter(listener -> listener.attributeMatches(features))
        .forEach(listener -> listener.sendWebSocketResponse(s, event));
    }
    return HttpURLConnection.HTTP_OK;
  }
}
