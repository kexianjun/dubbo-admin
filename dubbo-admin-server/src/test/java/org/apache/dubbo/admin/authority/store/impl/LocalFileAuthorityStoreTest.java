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

import org.apache.dubbo.admin.model.domain.User;
import org.apache.dubbo.admin.model.dto.UserDTO;
import org.apache.dubbo.common.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LocalFileAuthorityStoreTest {
    private LocalFileAuthorityStore localFileAuthorityStore;

    @Before
    public void setUp() {
        localFileAuthorityStore = new LocalFileAuthorityStore();
        URL url = new URL("localfile", "localhost", 1234);
        localFileAuthorityStore.setUrl(url);
        localFileAuthorityStore.init();
    }

    @Test
    public void saveUserTest() {
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword("sssss");
        userDTO.setUserName("xxxxx");
        boolean saveUser = localFileAuthorityStore.saveUser(userDTO);
        userDTO.setUserName("kexianjun");
        localFileAuthorityStore.saveUser(userDTO);
        Assert.assertTrue(saveUser);

        User user = localFileAuthorityStore.getUser("kexianjun");
        System.out.println(user);
    }
}
