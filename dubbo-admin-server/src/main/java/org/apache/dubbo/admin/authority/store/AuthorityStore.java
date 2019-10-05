package org.apache.dubbo.admin.authority.store;

import org.apache.dubbo.admin.authority.store.impl.LocalFileAuthorityStore;
import org.apache.dubbo.admin.model.domain.User;
import org.apache.dubbo.admin.model.domain.UserAuthorityDetail;
import org.apache.dubbo.admin.model.dto.AuthorityGroupDTO;
import org.apache.dubbo.admin.model.dto.UserAuthorityDTO;
import org.apache.dubbo.admin.model.dto.UserDTO;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.SPI;

import java.util.List;
import java.util.Set;

@SPI(LocalFileAuthorityStore.NAME)
public interface AuthorityStore {

    void setUrl(URL url);

    URL getUrl();

    void init();

    User getUser(String userName);

    boolean saveUser(UserDTO userDTO);

    List<User> getUsers(String userNamePattern);

    UserAuthorityDetail login(String userName, String password);

    boolean validateToken(String token);

    boolean saveAuthorityGroup(AuthorityGroupDTO authorityGroupDTO);

    User getUserByToken(String authorization);

    void authorityToUser(UserAuthorityDTO userAuthorityDTO);

    Set<String> getGroupAuthority(String groupName);

    boolean logout(String token);
}
