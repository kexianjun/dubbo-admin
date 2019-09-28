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
package org.apache.dubbo.admin.model.dto;

import java.util.List;
import java.util.Set;

public class AuthorityGroupDTO {
    private String authorityGroupName;
    private Set<String> authorityNameList;

    public String getAuthorityGroupName() {
        return authorityGroupName;
    }

    public void setAuthorityGroupName(String authorityGroupName) {
        this.authorityGroupName = authorityGroupName;
    }

    public Set<String> getAuthorityNameList() {
        return authorityNameList;
    }

    public void setAuthorityNameList(Set<String> authorityNameList) {
        this.authorityNameList = authorityNameList;
    }
}
