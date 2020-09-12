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

import io.fabric8.kubernetes.api.model.authorization.v1beta1.DoneableLocalSubjectAccessReview;
import io.fabric8.kubernetes.api.model.authorization.v1beta1.DoneableSelfSubjectAccessReview;
import io.fabric8.kubernetes.api.model.authorization.v1beta1.DoneableSelfSubjectRulesReview;
import io.fabric8.kubernetes.api.model.authorization.v1beta1.DoneableSubjectAccessReview;
import io.fabric8.kubernetes.api.model.authorization.v1beta1.LocalSubjectAccessReview;
import io.fabric8.kubernetes.api.model.authorization.v1beta1.SelfSubjectAccessReview;
import io.fabric8.kubernetes.api.model.authorization.v1beta1.SelfSubjectRulesReview;
import io.fabric8.kubernetes.api.model.authorization.v1beta1.SubjectAccessReview;
import io.fabric8.kubernetes.client.dsl.Createable;
import io.fabric8.kubernetes.client.dsl.internal.LocalCreateOnlyResourceReviewOperationsImpl;

public interface V1beta1AuthorizationAPIGroupDSL extends Client {
  Createable<SelfSubjectAccessReview, SelfSubjectAccessReview, DoneableSelfSubjectAccessReview> selfSubjectAccessReview();
  Createable<SubjectAccessReview, SubjectAccessReview, DoneableSubjectAccessReview> subjectAccessReview();
  LocalCreateOnlyResourceReviewOperationsImpl<LocalSubjectAccessReview, DoneableLocalSubjectAccessReview> localSubjectAccessReview();
  Createable<SelfSubjectRulesReview, SelfSubjectRulesReview, DoneableSelfSubjectRulesReview> selfSubjectRulesReview();
}
