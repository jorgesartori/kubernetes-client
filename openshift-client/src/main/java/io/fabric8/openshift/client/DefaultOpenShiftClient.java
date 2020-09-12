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

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.DoneableAPIService;
import io.fabric8.kubernetes.api.model.DoneableBinding;
import io.fabric8.kubernetes.api.model.DoneableComponentStatus;
import io.fabric8.kubernetes.api.model.DoneableConfigMap;
import io.fabric8.kubernetes.api.model.DoneableEndpoints;
import io.fabric8.kubernetes.api.model.DoneableEvent;
import io.fabric8.kubernetes.api.model.DoneableLimitRange;
import io.fabric8.kubernetes.api.model.DoneableNamespace;
import io.fabric8.kubernetes.api.model.DoneableNode;
import io.fabric8.kubernetes.api.model.DoneablePersistentVolume;
import io.fabric8.kubernetes.api.model.DoneablePersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.DoneablePod;
import io.fabric8.kubernetes.api.model.DoneableReplicationController;
import io.fabric8.kubernetes.api.model.DoneableResourceQuota;
import io.fabric8.kubernetes.api.model.DoneableSecret;
import io.fabric8.kubernetes.api.model.DoneableService;
import io.fabric8.kubernetes.api.model.DoneableServiceAccount;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.apiextensions.v1beta1.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.apiextensions.v1beta1.CustomResourceDefinitionList;
import io.fabric8.kubernetes.api.model.apiextensions.v1beta1.DoneableCustomResourceDefinition;
import io.fabric8.kubernetes.api.model.certificates.CertificateSigningRequest;
import io.fabric8.kubernetes.api.model.certificates.CertificateSigningRequestList;
import io.fabric8.kubernetes.api.model.certificates.DoneableCertificateSigningRequest;
import io.fabric8.kubernetes.api.model.authentication.DoneableTokenReview;
import io.fabric8.kubernetes.api.model.authentication.TokenReview;
import io.fabric8.kubernetes.api.model.coordination.v1.DoneableLease;
import io.fabric8.kubernetes.api.model.coordination.v1.Lease;
import io.fabric8.kubernetes.api.model.coordination.v1.LeaseList;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.EventList;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.dsl.*;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.kubernetes.client.dsl.internal.*;
import io.fabric8.kubernetes.client.dsl.internal.apiextensions.v1beta1.CustomResourceDefinitionOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.coordination.v1.LeaseOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.core.v1.ComponentStatusOperationsImpl;
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderElectorBuilder;
import io.fabric8.kubernetes.client.extended.run.RunConfigBuilder;
import io.fabric8.kubernetes.client.extended.run.RunOperations;
import io.fabric8.kubernetes.client.utils.BackwardsCompatibilityInterceptor;
import io.fabric8.kubernetes.client.utils.ImpersonatorInterceptor;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.fabric8.kubernetes.client.utils.Utils;
import io.fabric8.openshift.api.model.Build;
import io.fabric8.openshift.api.model.BuildConfig;
import io.fabric8.openshift.api.model.BuildConfigList;
import io.fabric8.openshift.api.model.BuildList;
import io.fabric8.openshift.api.model.ClusterNetwork;
import io.fabric8.openshift.api.model.ClusterNetworkList;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigList;
import io.fabric8.openshift.api.model.DoneableBuild;
import io.fabric8.openshift.api.model.DoneableBuildConfig;
import io.fabric8.openshift.api.model.DoneableClusterNetwork;
import io.fabric8.openshift.api.model.DoneableDeploymentConfig;
import io.fabric8.openshift.api.model.DoneableEgressNetworkPolicy;
import io.fabric8.openshift.api.model.DoneableGroup;
import io.fabric8.openshift.api.model.DoneableImage;
import io.fabric8.openshift.api.model.DoneableImageStream;
import io.fabric8.openshift.api.model.DoneableImageStreamTag;
import io.fabric8.openshift.api.model.DoneableImageTag;
import io.fabric8.openshift.api.model.DoneableNetNamespace;
import io.fabric8.openshift.api.model.DoneableOAuthAccessToken;
import io.fabric8.openshift.api.model.DoneableOAuthAuthorizeToken;
import io.fabric8.openshift.api.model.DoneableOAuthClient;
import io.fabric8.openshift.api.model.DoneableClusterRoleBinding;
import io.fabric8.openshift.api.model.DoneableRangeAllocation;
import io.fabric8.openshift.api.model.DoneableRole;
import io.fabric8.openshift.api.model.DoneableRoleBinding;
import io.fabric8.openshift.api.model.DoneableRoute;
import io.fabric8.openshift.api.model.DoneableSecurityContextConstraints;
import io.fabric8.openshift.api.model.DoneableSubjectAccessReview;
import io.fabric8.openshift.api.model.DoneableTemplate;
import io.fabric8.openshift.api.model.DoneableUser;
import io.fabric8.openshift.api.model.EgressNetworkPolicy;
import io.fabric8.openshift.api.model.EgressNetworkPolicyList;
import io.fabric8.openshift.api.model.Group;
import io.fabric8.openshift.api.model.GroupList;
import io.fabric8.openshift.api.model.Image;
import io.fabric8.openshift.api.model.ImageList;
import io.fabric8.openshift.api.model.ImageStream;
import io.fabric8.openshift.api.model.ImageStreamList;
import io.fabric8.openshift.api.model.ImageStreamTag;
import io.fabric8.openshift.api.model.ImageStreamTagList;
import io.fabric8.openshift.api.model.LocalSubjectAccessReview;
import io.fabric8.openshift.api.model.ImageTag;
import io.fabric8.openshift.api.model.ImageTagList;
import io.fabric8.openshift.api.model.NetNamespace;
import io.fabric8.openshift.api.model.NetNamespaceList;
import io.fabric8.openshift.api.model.OAuthAccessToken;
import io.fabric8.openshift.api.model.OAuthAccessTokenList;
import io.fabric8.openshift.api.model.OAuthAuthorizeToken;
import io.fabric8.openshift.api.model.OAuthAuthorizeTokenList;
import io.fabric8.openshift.api.model.OAuthClient;
import io.fabric8.openshift.api.model.OAuthClientList;
import io.fabric8.openshift.api.model.ClusterRoleBinding;
import io.fabric8.openshift.api.model.ClusterRoleBindingList;
import io.fabric8.openshift.api.model.RangeAllocation;
import io.fabric8.openshift.api.model.RangeAllocationList;
import io.fabric8.openshift.api.model.Role;
import io.fabric8.openshift.api.model.RoleBinding;
import io.fabric8.openshift.api.model.RoleBindingList;
import io.fabric8.openshift.api.model.RoleList;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteList;
import io.fabric8.openshift.api.model.SecurityContextConstraints;
import io.fabric8.openshift.api.model.SecurityContextConstraintsList;
import io.fabric8.openshift.api.model.SubjectAccessReview;
import io.fabric8.openshift.api.model.SubjectAccessReviewResponse;
import io.fabric8.openshift.api.model.Template;
import io.fabric8.openshift.api.model.TemplateList;
import io.fabric8.openshift.api.model.User;
import io.fabric8.openshift.api.model.UserList;
import io.fabric8.openshift.client.dsl.*;
import io.fabric8.openshift.client.dsl.internal.*;
import io.fabric8.openshift.client.dsl.internal.BuildConfigOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.BuildOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.ClusterRoleBindingOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.DeploymentConfigOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.GroupOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.ImageStreamOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.ImageStreamTagOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.OAuthAccessTokenOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.OAuthAuthorizeTokenOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.OAuthClientOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.ProjectOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.RouteOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.SecurityContextConstraintsOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.TemplateOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.UserOperationsImpl;
import io.fabric8.openshift.client.internal.OpenShiftOAuthInterceptor;
import okhttp3.Authenticator;
import okhttp3.OkHttpClient;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * Class for Default Openshift Client implementing KubernetesClient interface.
 * It is thread safe.
 */
public class DefaultOpenShiftClient extends BaseClient implements NamespacedOpenShiftClient {

  private static final String API_GROUPS_ENABLED = "API_GROUPS_ENABLED";
  private static final Map<String, Boolean> API_GROUPS_ENABLED_PER_URL = new HashMap<>();

  private URL openShiftUrl;
  private NamespacedKubernetesClient delegate;

  public DefaultOpenShiftClient() throws KubernetesClientException {
    this(new OpenShiftConfigBuilder().build());
  }

  public DefaultOpenShiftClient(String masterUrl) throws KubernetesClientException {
    this(new OpenShiftConfigBuilder().withMasterUrl(masterUrl).build());
  }

  public DefaultOpenShiftClient(final Config config) throws KubernetesClientException {
    this(new OpenShiftConfig(config));
  }

  public DefaultOpenShiftClient(final OpenShiftConfig config) throws KubernetesClientException {
    super(configWithApiGroupsEnabled(clientWithOpenShiftOAuthInterceptor(config), config));
    try {
      this.httpClient = clientWithOpenShiftOAuthInterceptor(this.httpClient, config);
      this.delegate = new DefaultKubernetesClient(this.httpClient, config);
      this.openShiftUrl = new URL(config.getOpenShiftUrl());
    } catch (MalformedURLException e) {
      throw new KubernetesClientException("Could not create client", e);
    }
  }

  public DefaultOpenShiftClient(OkHttpClient httpClient, OpenShiftConfig config) throws KubernetesClientException {
    super(httpClient, configWithApiGroupsEnabled(httpClient, config));
    try {
      this.httpClient = clientWithOpenShiftOAuthInterceptor(httpClient, getConfiguration());
      this.delegate = new DefaultKubernetesClient(this.httpClient, config);
      this.openShiftUrl = new URL(config.getOpenShiftUrl());
    } catch (MalformedURLException e) {
      throw new KubernetesClientException("Could not create client", e);
    }
  }

  private static OpenShiftConfig configWithApiGroupsEnabled(OkHttpClient httpClient, OpenShiftConfig config) {
    String url = config.getMasterUrl();
    Boolean openshiftApiGroupsEnabled = API_GROUPS_ENABLED_PER_URL.containsKey(url);
    if (openshiftApiGroupsEnabled) {
      return config;
    }

    if (!config.isDisableApiGroupCheck()) {
      return config.withOpenshiftApiGroupsEnabled(false);
    }

    Boolean enabled = OpenshiftAdapterSupport.isOpenShiftAPIGroups(httpClient, url);
    API_GROUPS_ENABLED_PER_URL.put(url, enabled);
   return config.withOpenshiftApiGroupsEnabled(enabled);
  }

  public static DefaultOpenShiftClient fromConfig(String config) {
    return new DefaultOpenShiftClient(Serialization.unmarshal(config, OpenShiftConfig.class));
  }

  public static DefaultOpenShiftClient fromConfig(InputStream is) {
    return new DefaultOpenShiftClient(Serialization.unmarshal(is, OpenShiftConfig.class));
  }

  private static OkHttpClient clientWithOpenShiftOAuthInterceptor(Config config) {
    return clientWithOpenShiftOAuthInterceptor(null, config);
  }

  private static OkHttpClient clientWithOpenShiftOAuthInterceptor(OkHttpClient httpClient, Config config) {
    OkHttpClient.Builder builder = httpClient != null ?
      httpClient.newBuilder().authenticator(Authenticator.NONE) :
      new OkHttpClient.Builder().authenticator(Authenticator.NONE);

    builder.interceptors().clear();
    return builder.addInterceptor(new OpenShiftOAuthInterceptor(httpClient, OpenShiftConfig.wrap(config)))
      .addInterceptor(new ImpersonatorInterceptor(config))
      .addInterceptor(new BackwardsCompatibilityInterceptor())
      .build();
  }

  @Override
  public URL getOpenshiftUrl() {
    return openShiftUrl;
  }

  @Override
  public OpenShiftConfigAPIGroupDSL config() {
    return adapt(OpenShiftConfigAPIGroupClient.class);
  }

  @Override
  public OpenShiftConsoleAPIGroupDSL console() {
    return adapt(OpenShiftConsoleAPIGroupClient.class);
  }

  @Override
  public OpenShiftOperatorAPIGroupDSL operator() {
    return adapt(OpenShiftOperatorAPIGroupClient.class);
  }

  @Override
  public OpenShiftOperatorHubAPIGroupDSL operatorHub() {
    return adapt(OpenShiftOperatorHubAPIGroupClient.class);
  }

  @Override
  public MixedOperation<ComponentStatus, ComponentStatusList, DoneableComponentStatus, Resource<ComponentStatus, DoneableComponentStatus>> componentstatuses() {
    return new ComponentStatusOperationsImpl(httpClient, getConfiguration());
  }

  @Override
  public ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> load(InputStream is) {
    return delegate.load(is);
  }

  @Override
  public NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata, Boolean> resource(HasMetadata item) {
    return delegate.resource(item);
  }

  @Override
  public NamespaceVisitFromServerGetWatchDeleteRecreateWaitApplicable<HasMetadata, Boolean> resource(String s) {
    return delegate.resource(s);
  }

  @Override
  public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(KubernetesResourceList is) {
    return delegate.resourceList(is);
  }

  @Override
  public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(HasMetadata... items) {
    return delegate.resourceList(items);
  }

  @Override
  public NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(Collection<HasMetadata> items) {
    return delegate.resourceList(items);
  }

  @Override
  public ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata, Boolean> resourceList(String s) {
    return delegate.resourceList(s);
  }

  @Override
  public MixedOperation<Endpoints, EndpointsList, DoneableEndpoints, Resource<Endpoints, DoneableEndpoints>> endpoints() {
    return delegate.endpoints();
  }

  @Override
  public MixedOperation<Binding, KubernetesResourceList<Binding>, DoneableBinding, Resource<Binding, DoneableBinding>> bindings() {
    return delegate.bindings();
  }

  @Override
  public MixedOperation<Event, EventList, DoneableEvent, Resource<Event, DoneableEvent>> events() {
    return delegate.events();
  }

  @Override
  public NonNamespaceOperation<Namespace, NamespaceList, DoneableNamespace, Resource<Namespace, DoneableNamespace>> namespaces() {
    return delegate.namespaces();
  }

  @Override
  public NonNamespaceOperation<Node, NodeList, DoneableNode, Resource<Node, DoneableNode>> nodes() {
    return delegate.nodes();
  }

  @Override
  public NonNamespaceOperation<PersistentVolume, PersistentVolumeList, DoneablePersistentVolume, Resource<PersistentVolume, DoneablePersistentVolume>> persistentVolumes() {
    return delegate.persistentVolumes();
  }

  @Override
  public MixedOperation<PersistentVolumeClaim, PersistentVolumeClaimList, DoneablePersistentVolumeClaim, Resource<PersistentVolumeClaim, DoneablePersistentVolumeClaim>> persistentVolumeClaims() {
    return delegate.persistentVolumeClaims();
  }

  @Override
  public MixedOperation<Pod, PodList, DoneablePod, PodResource<Pod, DoneablePod>> pods() {
    return delegate.pods();
  }

  @Override
  public MixedOperation<ReplicationController, ReplicationControllerList, DoneableReplicationController, RollableScalableResource<ReplicationController, DoneableReplicationController>> replicationControllers() {
    return delegate.replicationControllers();
  }

  @Override
  public MixedOperation<ResourceQuota, ResourceQuotaList, DoneableResourceQuota, Resource<ResourceQuota, DoneableResourceQuota>> resourceQuotas() {
    return delegate.resourceQuotas();
  }

  @Override
  public MixedOperation<Secret, SecretList, DoneableSecret, Resource<Secret, DoneableSecret>> secrets() {
    return delegate.secrets();
  }

  @Override
  public MixedOperation<Service, ServiceList, DoneableService, ServiceResource<Service, DoneableService>> services() {
    return delegate.services();
  }

  @Override
  public MixedOperation<ServiceAccount, ServiceAccountList, DoneableServiceAccount, Resource<ServiceAccount, DoneableServiceAccount>> serviceAccounts() {
    return delegate.serviceAccounts();
  }

  @Override
  public MixedOperation<APIService, APIServiceList, DoneableAPIService, Resource<APIService, DoneableAPIService>> apiServices() {
    return delegate.apiServices();
  }

  @Override
  public KubernetesListMixedOperation lists() {
    return delegate.lists();
  }

  @Override
  public MixedOperation<ConfigMap, ConfigMapList, DoneableConfigMap, Resource<ConfigMap, DoneableConfigMap>> configMaps() {
    return delegate.configMaps();
  }

  @Override
  public MixedOperation<LimitRange, LimitRangeList, DoneableLimitRange, Resource<LimitRange, DoneableLimitRange>> limitRanges() {
    return delegate.limitRanges();
  }

  @Override
  public <T extends HasMetadata, L extends KubernetesResourceList<T>, D extends Doneable<T>> MixedOperation<T, L, D, Resource<T, D>> customResources(CustomResourceDefinitionContext crdContext, Class<T> resourceType, Class<L> listClass, Class<D> doneClass) {
    return new CustomResourceOperationsImpl<>(new CustomResourceOperationContext().withOkhttpClient(httpClient).withConfig(getConfiguration()).withCrdContext(crdContext).withType(resourceType).withListType(listClass).withDoneableType(doneClass));
  }

  @Override
  public <T extends HasMetadata, L extends KubernetesResourceList<T>, D extends Doneable<T>> MixedOperation<T, L, D, Resource<T, D>> customResources(CustomResourceDefinition crd, Class<T> resourceType, Class<L> listClass, Class<D> doneClass) {
    return new CustomResourceOperationsImpl<>(new CustomResourceOperationContext().withOkhttpClient(httpClient).withConfig(getConfiguration()).withCrd(crd).withType(resourceType).withListType(listClass).withDoneableType(doneClass));
  }

  @Override
  public <T extends HasMetadata, L extends KubernetesResourceList<T>, D extends Doneable<T>> MixedOperation<T, L, D, Resource<T, D>> customResource(CustomResourceDefinition crd, Class<T> resourceType, Class<L> listClass, Class<D> doneClass) {
    return customResources(crd, resourceType, listClass, doneClass);
  }

  @Override
  public NonNamespaceOperation<CustomResourceDefinition, CustomResourceDefinitionList, DoneableCustomResourceDefinition, Resource<CustomResourceDefinition, DoneableCustomResourceDefinition>> customResourceDefinitions() {
    return new CustomResourceDefinitionOperationsImpl(httpClient, getConfiguration());
  }

  @Override
  public ApiextensionsAPIGroupDSL apiextensions() {
    return delegate.apiextensions();
  }

  @Override
  public NonNamespaceOperation<CertificateSigningRequest, CertificateSigningRequestList, DoneableCertificateSigningRequest, Resource<CertificateSigningRequest, DoneableCertificateSigningRequest>> certificateSigningRequests() {
    return delegate.certificateSigningRequests();
  }

  @Override
  public AuthorizationAPIGroupDSL authorization() {
    return delegate.authorization();
  }

  @Override
  public Createable<TokenReview, TokenReview, DoneableTokenReview> tokenReviews() {
    return delegate.tokenReviews();
  }

  public RawCustomResourceOperationsImpl customResource(CustomResourceDefinitionContext customResourceDefinition) {
    return new RawCustomResourceOperationsImpl(httpClient, getConfiguration(), customResourceDefinition);
  }

  @Override
  public MixedOperation<Build, BuildList, DoneableBuild, BuildResource<Build, DoneableBuild, String, LogWatch>> builds() {
    return new BuildOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<BuildConfig, BuildConfigList, DoneableBuildConfig, BuildConfigResource<BuildConfig, DoneableBuildConfig, Void, Build>> buildConfigs() {
    return new BuildConfigOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<DeploymentConfig, DeploymentConfigList, DoneableDeploymentConfig, DeployableScalableResource<DeploymentConfig, DoneableDeploymentConfig>> deploymentConfigs() {
    return new DeploymentConfigOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<Group, GroupList, DoneableGroup, Resource<Group, DoneableGroup>> groups() {
    return new GroupOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public NonNamespaceOperation<Image, ImageList, DoneableImage, Resource<Image, DoneableImage>> images() {
    return new ImageOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<ImageTag, ImageTagList, DoneableImageTag, Resource<ImageTag, DoneableImageTag>> imageTags() {
    return new ImageTagOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<ImageStream, ImageStreamList, DoneableImageStream, Resource<ImageStream, DoneableImageStream>> imageStreams() {
    return new ImageStreamOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<ImageStreamTag, ImageStreamTagList, DoneableImageStreamTag, Resource<ImageStreamTag, DoneableImageStreamTag>> imageStreamTags() {
    return new ImageStreamTagOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public NonNamespaceOperation<OAuthAccessToken, OAuthAccessTokenList, DoneableOAuthAccessToken, Resource<OAuthAccessToken, DoneableOAuthAccessToken>> oAuthAccessTokens() {
    return new OAuthAccessTokenOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public NonNamespaceOperation<OAuthAuthorizeToken, OAuthAuthorizeTokenList, DoneableOAuthAuthorizeToken, Resource<OAuthAuthorizeToken, DoneableOAuthAuthorizeToken>> oAuthAuthorizeTokens() {
    return new OAuthAuthorizeTokenOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public NonNamespaceOperation<OAuthClient, OAuthClientList, DoneableOAuthClient, Resource<OAuthClient, DoneableOAuthClient>> oAuthClients() {
    return new OAuthClientOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public ProjectOperation projects() {
    return new ProjectOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public OpenShiftQuotaAPIGroupDSL quotas() {
    return adapt(OpenShiftQuotaAPIGroupClient.class);
  }

  @Override
  public NonNamespaceOperation<RangeAllocation, RangeAllocationList, DoneableRangeAllocation, Resource<RangeAllocation, DoneableRangeAllocation>> rangeAllocations() {
    return new RangeAllocationOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public NonNamespaceOperation<SecurityContextConstraints, SecurityContextConstraintsList, DoneableSecurityContextConstraints, Resource<SecurityContextConstraints, DoneableSecurityContextConstraints>> securityContextConstraints() {
    return new SecurityContextConstraintsOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public ProjectRequestOperation projectrequests() {
    return new ProjectRequestsOperationImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<Role, RoleList, DoneableRole, Resource<Role, DoneableRole>> roles() {
    return new RoleOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<RoleBinding, RoleBindingList, DoneableRoleBinding, Resource<RoleBinding, DoneableRoleBinding>> roleBindings() {
    return new RoleBindingOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<Route, RouteList, DoneableRoute, Resource<Route, DoneableRoute>> routes() {
    return new RouteOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public ParameterMixedOperation<Template, TemplateList, DoneableTemplate, TemplateResource<Template, KubernetesList, DoneableTemplate>> templates() {
    return new TemplateOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<User, UserList, DoneableUser, Resource<User, DoneableUser>> users() {
    return new UserOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<ClusterRoleBinding, ClusterRoleBindingList, DoneableClusterRoleBinding, Resource<ClusterRoleBinding, DoneableClusterRoleBinding>> clusterRoleBindings() {
    return new ClusterRoleBindingOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public NamespacedOpenShiftClient inNamespace(String namespace) {
    OpenShiftConfig updated = new OpenShiftConfigBuilder(new OpenShiftConfig(getConfiguration()))
      .withOpenShiftUrl(openShiftUrl.toString())
      .withNamespace(namespace)
      .build();
    return new DefaultOpenShiftClient(httpClient, updated);
  }

  @Override
  public NamespacedOpenShiftClient inAnyNamespace() {
    return inNamespace(null);
  }

  @Override
  public ExtensionsAPIGroupClient extensions() {
    return adapt(ExtensionsAPIGroupClient.class);
  }

  @Override
  public VersionInfo getVersion() {
    return new ClusterOperationsImpl(httpClient, getConfiguration(), ClusterOperationsImpl.OPENSHIFT_VERSION_ENDPOINT).fetchVersion();
  }

  @Override
  public V1APIGroupDSL v1() {
    return adapt(V1APIGroupClient.class);
  }

  @Override
  public RunOperations run() {
    return new RunOperations(httpClient, getConfiguration(), getNamespace(), new RunConfigBuilder());
  }

  @Override
  public AdmissionRegistrationAPIGroupDSL admissionRegistration() {
    return adapt(AdmissionRegistrationAPIGroupClient.class);
  }

  @Override
  public AppsAPIGroupDSL apps() {
    return adapt(AppsAPIGroupClient.class);
  }

  @Override
  public AutoscalingAPIGroupDSL autoscaling() {
    return adapt(AutoscalingAPIGroupClient.class);
  }

  @Override
  public OpenShiftMonitoringAPIGroupDSL monitoring() { return adapt(OpenShiftMonitoringAPIGroupClient.class); }

  @Override
  public NonNamespaceOperation<NetNamespace, NetNamespaceList, DoneableNetNamespace, Resource<NetNamespace, DoneableNetNamespace>> netNamespaces() {
    return new NetNamespaceOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public NonNamespaceOperation<ClusterNetwork, ClusterNetworkList, DoneableClusterNetwork, Resource<ClusterNetwork, DoneableClusterNetwork>> clusterNetworks() {
    return new ClusterNetworkOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public MixedOperation<EgressNetworkPolicy, EgressNetworkPolicyList, DoneableEgressNetworkPolicy, Resource<EgressNetworkPolicy, DoneableEgressNetworkPolicy>> egressNetworkPolicies() {
    return new EgressNetworkPolicyOperationsImpl(httpClient, OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public NetworkAPIGroupDSL network() { return adapt(NetworkAPIGroupClient.class); }

  @Override
  public StorageAPIGroupDSL storage() { return adapt(StorageAPIGroupClient.class); }

  @Override
  public SettingsAPIGroupDSL settings() { return adapt(SettingsAPIGroupClient.class); }

  @Override
  public Createable<SubjectAccessReview, SubjectAccessReviewResponse, DoneableSubjectAccessReview> subjectAccessReviews() {
    return new OpenShiftSubjectAccessReviewOperationsImpl(httpClient, getConfiguration(), "authorization.openshift.io", "v1", Utils.getPluralFromKind(SubjectAccessReview.class.getSimpleName()));
  }

  @Override
  public OpenShiftLocalSubjectAccessReviewOperationsImpl localSubjectAccessReviews() {
    return new OpenShiftLocalSubjectAccessReviewOperationsImpl(httpClient, getConfiguration(), "authorization.openshift.io", "v1", Utils.getPluralFromKind(LocalSubjectAccessReview.class.getSimpleName()));
  }

  @Override
  public BatchAPIGroupDSL batch() { return adapt(BatchAPIGroupClient.class); }

  @Override
  public MetricAPIGroupDSL top() { return adapt(MetricAPIGroupClient.class); }

  @Override
  public PolicyAPIGroupDSL policy() { return adapt(PolicyAPIGroupClient.class); }

  @Override
  public RbacAPIGroupDSL rbac() { return adapt(RbacAPIGroupClient.class); }

  @Override
  public SchedulingAPIGroupDSL scheduling() { return adapt(SchedulingAPIGroupClient.class); }

  @Override
  public SharedInformerFactory informers() { return new SharedInformerFactory(ForkJoinPool.commonPool(), httpClient, getConfiguration()); }

  @Override
  public SharedInformerFactory informers(ExecutorService executorService) { return new SharedInformerFactory(executorService, httpClient, getConfiguration()); }

  /**
   * {@inheritDoc}
   */
  @Override
  public LeaderElectorBuilder<NamespacedOpenShiftClient> leaderElector() {
    return new LeaderElectorBuilder<>(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MixedOperation<Lease, LeaseList, DoneableLease, Resource<Lease, DoneableLease>> leases() {
    return new LeaseOperationsImpl(httpClient, getConfiguration());
  }

  @Override
  public FunctionCallable<NamespacedOpenShiftClient> withRequestConfig(RequestConfig requestConfig) {
    return new WithRequestCallable<>(this, requestConfig);
  }

  @Override
  public User currentUser() {
    return users().withName("~").get();
  }

  @Override
  public boolean supportsOpenShiftAPIGroup(String apiGroup) {
    String apiGroupPath = "/apis/" + apiGroup;
    RootPaths rootPaths = rootPaths();
    if (rootPaths != null) {
      List<String> paths = rootPaths.getPaths();
      if (paths != null) {
        for (String path : paths) {
          if (Objects.equals("/oapi", path) || Objects.equals("oapi", path) || Objects.equals(apiGroupPath, path)) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
