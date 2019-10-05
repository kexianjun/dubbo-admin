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
package org.apache.dubbo.admin.authority.store.impl;

import org.apache.dubbo.admin.authority.store.AuthorityStore;
import org.apache.dubbo.admin.model.domain.User;
import org.apache.dubbo.admin.model.domain.UserAuthorityDetail;
import org.apache.dubbo.admin.model.dto.AuthorityGroupDTO;
import org.apache.dubbo.admin.model.dto.UserAuthorityDTO;
import org.apache.dubbo.admin.model.dto.UserDTO;
import org.apache.dubbo.common.URL;

import java.util.List;
import java.util.Set;

public class NoOpAuthorityStore implements AuthorityStore {
    public static final String NAME = "noop";

    @Override
    public void setUrl(URL url) {

    }

    @Override
    public URL getUrl() {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public User getUser(String userName) {
        return null;
    }

    @Override
    public boolean saveUser(UserDTO userDTO) {
        return false;
    }

    @Override
    public List<User> getUsers(String userNamePattern) {
        return null;
    }

    @Override
    public UserAuthorityDetail login(String userName, String password) {
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public boolean saveAuthorityGroup(AuthorityGroupDTO authorityGroupDTO) {
        return false;
    }

    @Override
    public User getUserByToken(String authorization) {
        return null;
    }

    @Override
    public void authorityToUser(UserAuthorityDTO userAuthorityDTO) {

    }

    @Override
    public Set<String> getGroupAuthority(String groupName) {
        return null;
    }

    @Override
    public boolean logout(String token) {
        return false;
    }
}
