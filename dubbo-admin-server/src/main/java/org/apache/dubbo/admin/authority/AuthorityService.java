package org.apache.dubbo.admin.authority;

import org.apache.dubbo.admin.authority.store.AuthorityStore;
import org.apache.dubbo.admin.model.dto.UserDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService {
    @Autowired
    private AuthorityStore authorityStore;

    public boolean saveUser(UserDTO userDTO) {
        return authorityStore.saveUser(userDTO);
    }

}
