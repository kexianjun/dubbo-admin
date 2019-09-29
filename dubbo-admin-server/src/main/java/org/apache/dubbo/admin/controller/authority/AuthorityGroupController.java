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
package org.apache.dubbo.admin.controller.authority;

import org.apache.dubbo.admin.authority.store.AuthorityStore;
import org.apache.dubbo.admin.model.domain.User;
import org.apache.dubbo.admin.model.dto.AuthorityGroupDTO;
import org.apache.dubbo.admin.model.dto.UserAuthorityDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/{env}/authority/authorityGroup")
public class AuthorityGroupController {
    @Autowired
    private AuthorityStore authorityStore;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public boolean createAuthorityGroup(@RequestBody AuthorityGroupDTO authorityGroupDTO) {
        authorityStore.saveAuthorityGroup(authorityGroupDTO);
        User userInfo = getUserInfo();
        UserAuthorityDTO userAuthorityDTO = new UserAuthorityDTO();
        userAuthorityDTO.setUserName(userInfo.getUserName());
        Set<String> authorityGroup = userInfo.getAuthorityGroup();
        if (null == authorityGroup) {
            authorityGroup = new HashSet<>();
            userInfo.setAuthorityGroup(authorityGroup);
        }
        authorityGroup.add(authorityGroupDTO.getAuthorityGroupName());
        userAuthorityDTO.setAuthorityGroupList(authorityGroup);
        authorityStore.authorityToUser(userAuthorityDTO);
        return true;
    }

    @RequestMapping(value = "listAuthorityGroup", method = RequestMethod.GET)
    public Set<String> getAuthorityGroup() {
        User userInfo = getUserInfo();
        if (userInfo != null) {
            return userInfo.getAuthorityGroup();
        }
        return null;
    }

    private User getUserInfo() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == attributes) {
            return null;
        }
        return (User) attributes.getAttribute("userInfo", 0);

    }
}
