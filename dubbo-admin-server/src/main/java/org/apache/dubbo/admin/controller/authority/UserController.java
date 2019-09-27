package org.apache.dubbo.admin.controller.authority;

import org.apache.dubbo.admin.authority.store.AuthorityStore;
import org.apache.dubbo.admin.model.domain.User;
import org.apache.dubbo.admin.model.domain.UserAuthorityDetail;
import org.apache.dubbo.admin.model.dto.UserDTO;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/{env}/authority/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private AuthorityStore authorityStore;

    @RequestMapping(value = "/create")
    public boolean createUser(@RequestBody UserDTO userDTO) {
        log.info("create new user:[{}]", JSON.toJSONString(userDTO));
        return authorityStore.saveUser(userDTO);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Page<User> searchUser(@RequestParam String filter, Pageable pageable) {
        List<User> users = authorityStore.getUsers(filter);
        int total = users.size();
        List<User> content = users.stream().skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, total);

    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public UserAuthorityDetail login(@RequestParam String userName, @RequestParam String password) {
        return authorityStore.login(userName, password);
    }
}
