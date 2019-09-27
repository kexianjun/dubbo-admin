/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.admin.model.domain;

import java.util.Set;

public class AuthorityGroup extends BaseAuthority{
    private String authorityGroupKey;
    private String authorityGroupName;
    private Set<String> authorityKeyList;

    public String getAuthorityGroupKey() {
        return authorityGroupKey;
    }

    public void setAuthorityGroupKey(String authorityGroupKey) {
        this.authorityGroupKey = authorityGroupKey;
    }

    public String getAuthorityGroupName() {
        return authorityGroupName;
    }

    public void setAuthorityGroupName(String authorityGroupName) {
        this.authorityGroupName = authorityGroupName;
    }

    public Set<String> getAuthorityKeyList() {
        return authorityKeyList;
    }

    public void setAuthorityKeyList(Set<String> authorityKeyList) {
        this.authorityKeyList = authorityKeyList;
    }

    @java.lang.Override
    public String getKey() {
        return authorityGroupKey;
    }
}
