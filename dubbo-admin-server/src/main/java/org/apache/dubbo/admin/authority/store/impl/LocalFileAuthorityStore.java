package org.apache.dubbo.admin.authority.store.impl;

import org.apache.dubbo.admin.authority.store.AuthorityStore;
import org.apache.dubbo.admin.common.util.Constants;
import org.apache.dubbo.admin.model.domain.Authority;
import org.apache.dubbo.admin.model.domain.AuthorityGroup;
import org.apache.dubbo.admin.model.domain.BaseAuthority;
import org.apache.dubbo.admin.model.domain.User;
import org.apache.dubbo.admin.model.domain.UserAuthorityDetail;
import org.apache.dubbo.admin.model.domain.UserToken;
import org.apache.dubbo.admin.model.dto.AuthorityGroupDTO;
import org.apache.dubbo.admin.model.dto.UserAuthorityDTO;
import org.apache.dubbo.admin.model.dto.UserDTO;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LocalFileAuthorityStore implements AuthorityStore {
    private static final Logger log = LoggerFactory.getLogger(LocalFileAuthorityStore.class);

    public static final String NAME = "localfile";
    private static final String DEFAULT_AUTHORITY_STORE_PATH = System.getProperty("user.home") + File.separator + ".dubbo-admin" + File.separator;
    private static final String USER_STORE_FILE_NAME = "user.json";
    private static final String AUTHORITY_FILE_NAME = "authority.json";
    private static final String AUTHORITY_GROUP_FILE_NAME = "authority_group.json";
    private static final String USER_TOKEN_FILE_NAME = "user_token.json";

    private UserStore userStore;
    private TokenStore tokenStore;
    private AuthorityGroupStore authorityGroupStore;
    private AuthorityStore authorityStore;

    private static Map<String, List<String>> menuAuthorityMap = new ConcurrentHashMap<>();

    private static Map<String, List<String>> authorityGroupMap = new ConcurrentHashMap<>();
    private URL url;

    static {
        List<String> serviceSearchList = new LinkedList<>();
        menuAuthorityMap.put("serviceSearch", serviceSearchList);

        List<String> serviceGovernanceList = new LinkedList<>();
        menuAuthorityMap.put("serviceGovernance", serviceGovernanceList);

        List<String> routingRuleList = new LinkedList<>();
        menuAuthorityMap.put("routingRule", routingRuleList);

        List<String> tagRuleList = new LinkedList<>();
        menuAuthorityMap.put("tagRule", tagRuleList);

        List<String> accessControlList = new LinkedList<>();
        menuAuthorityMap.put("accessControl", accessControlList);

        List<String> dynamicConfigList = new LinkedList<>();
        menuAuthorityMap.put("dynamicConfig", dynamicConfigList);

        List<String> weightAdjustList = new LinkedList<>();
        menuAuthorityMap.put("weightAdjust", weightAdjustList);

        List<String> loadBalanceList = new LinkedList<>();
        menuAuthorityMap.put("loadBalance", loadBalanceList);

        List<String> serviceTestList = new LinkedList<>();
        menuAuthorityMap.put("serviceTest", serviceTestList);

        List<String> serviceMockList = new LinkedList<>();
        menuAuthorityMap.put("serviceMock", serviceMockList);

        List<String> metricsList = new LinkedList<>();
        menuAuthorityMap.put("metrics", metricsList);

        List<String> configManageList = new LinkedList<>();
        menuAuthorityMap.put("configManage", configManageList);

        List<String> authControllerList = new LinkedList<>();
        menuAuthorityMap.put("authController", authControllerList);

        List<String> userControllerList = new LinkedList<>();
        menuAuthorityMap.put("userController", userControllerList);

        List<String> authConfigList = new LinkedList<>();
        menuAuthorityMap.put("authConfig", authConfigList);

        List<String> adminAuthorityList = new LinkedList<>();
        adminAuthorityList.add("serviceSearch");
        adminAuthorityList.add("serviceGovernance");
        adminAuthorityList.add("routingRule");
        adminAuthorityList.add("tagRule");
        adminAuthorityList.add("accessControl");
        adminAuthorityList.add("dynamicConfig");
        adminAuthorityList.add("weightAdjust");
        adminAuthorityList.add("loadBalance");
        adminAuthorityList.add("serviceTest");
        adminAuthorityList.add("serviceMock");
        adminAuthorityList.add("metrics");
        adminAuthorityList.add("configManage");
        adminAuthorityList.add("authController");
        adminAuthorityList.add("userController");
        adminAuthorityList.add("authConfig");
        authorityGroupMap.put("admin", adminAuthorityList);

    }

    public LocalFileAuthorityStore() {
    }

    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void init() {
        // load user, authority,authorityGroup from file
        String storePath = url.getParameter(Constants.PATH_KEY, DEFAULT_AUTHORITY_STORE_PATH);
        File file = new File(storePath);
        if (!file.exists()) {
            if (file.mkdirs()) {
                log.info("Create directory {}", storePath);
            } else {
                log.error("Create directory {} error", storePath);
                return;
            }

        }
        loadUser(storePath + USER_STORE_FILE_NAME);
        loadToken(storePath + USER_TOKEN_FILE_NAME);
        loadAuthority(storePath + AUTHORITY_FILE_NAME);
        loadAuthorityGroup(storePath + AUTHORITY_GROUP_FILE_NAME);
        loadDefaultAuthorityAndAuthorityGroup();
    }

    @Override
    public User getUser(String userName) {
        return userStore.getStore(userName);
    }

    private void loadUser(String fileName) {
        if (null == userStore) {
            userStore = new UserStore(fileName);
        }
        userStore.load();
    }

    private void loadAuthority(String fileName) {
        if (null == authorityStore) {
            authorityStore = new AuthorityStore(fileName);
        }
        authorityStore.load();
    }

    private void loadAuthorityGroup(String fileName) {
        if (null == authorityGroupStore) {
            authorityGroupStore = new AuthorityGroupStore(fileName);
        }
        authorityGroupStore.load();
    }

    private void loadToken(String fileName) {
        if (null == tokenStore) {
            tokenStore = new TokenStore(fileName);
        }
        tokenStore.load();
    }

    private void loadDefaultAuthorityAndAuthorityGroup() {
        Map<String, Authority> authorityStoreMap = authorityStore.getStore();
        if (authorityStoreMap.size() == 0) {
            Map<String, Authority> authorityMap = new HashMap<>();
            for (Map.Entry<String, List<String>> menuEntry : menuAuthorityMap.entrySet()) {
                Authority menuAuthority = new Authority();
                menuAuthority.setParent(null);
                menuAuthority.setAuthorityKey(menuEntry.getKey());
                menuAuthority.setAuthorityType(Authority.MENU);
                authorityMap.put(menuEntry.getKey(), menuAuthority);
                if (CollectionUtils.isNotEmpty(menuEntry.getValue())) {
                    for (String interfaceAuthority : menuEntry.getValue()) {
                        Authority authority = new Authority();
                        authority.setAuthorityKey(interfaceAuthority);
                        authority.setParent(menuEntry.getKey());
                        authority.setAuthorityType(Authority.INTERFACE);
                        authorityMap.put(interfaceAuthority, authority);
                    }
                }
            }

            authorityStore.saveStore(authorityMap);
        }

        if (authorityGroupStore.getStore().size() == 0) {
            AuthorityGroup authorityGroup = new AuthorityGroup();
            for (Map.Entry<String, List<String>> authorityGroupEntry : authorityGroupMap.entrySet()) {
                authorityGroup.setAuthorityGroupKey(authorityGroupEntry.getKey());
                Set<String> set = new HashSet<>();
                set.addAll(authorityGroupEntry.getValue());
                authorityGroup.setAuthorityKeyList(set);
            }
            authorityGroupStore.saveStore(authorityGroup);
        }

    }

    @Override
    public boolean saveUser(UserDTO userDTO) {
        User store = userStore.getStore(userDTO.getUserName());
        if (null != store) {
            return false;
        }
        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setPassword(userDTO.getPassword());

        return userStore.saveStore(user);
    }

    @Override
    public List<User> getUsers(String userNamePattern) {

        return userStore.getStore()
                .entrySet()
                .stream()
                .filter(entry -> (Constants.ANY_VALUE.equals(userNamePattern)) || entry.getKey().contains(userNamePattern))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public UserAuthorityDetail login(String userName, String password) {
        User store = userStore.getStore(userName);
        if (null != store && password.equals(store.getPassword())) {
            // Login success, remove previous token and generate token and store it
            Map<String, UserToken> userTokenMap = tokenStore.getStore();
            if (null != userTokenMap) {
                Set<Map.Entry<String, UserToken>> entries = userTokenMap.entrySet();
                entries.removeIf(entry -> entry.getValue().getUserName().equals(userName));
            }
            UserToken userToken = new UserToken();
            userToken.setUserName(userName);
            userToken.setToken(TokenStore.generateToken(userName));
            userToken.setLastUpdateTimestamp(System.currentTimeMillis());
            tokenStore.saveStore(userToken);
            Set<String> authorityGroupSet = store.getAuthorityGroup();
            Set<String> authoritySet = new HashSet<>();
            if (null != authorityGroupSet) {
                for (String authorityGroup : authorityGroupSet) {
                    AuthorityGroup authorityGroupStoreStore = authorityGroupStore.getStore(authorityGroup);
                    if (null != authorityGroupStoreStore) {
                        authoritySet.addAll(authorityGroupStoreStore.getAuthorityKeyList());
                    }
                }
            }

            UserAuthorityDetail userAuthorityDetail = new UserAuthorityDetail();
            userAuthorityDetail.setUserName(userName);
            userAuthorityDetail.setAuthoritys(authoritySet);
            userAuthorityDetail.setToken(userToken.getToken());
            return userAuthorityDetail;
        }
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        UserToken userToken = tokenStore.getStore(token);
        if (null == userToken || System.currentTimeMillis() - userToken.getLastUpdateTimestamp() >= 1000 * 60 * 30) {
            // Token validate failed, remove it anyway
            tokenStore.remove(token);
            return false;
        }
        // Update last update timestamp
        userToken.setLastUpdateTimestamp(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean saveAuthorityGroup(AuthorityGroupDTO authorityGroupDTO) {
        AuthorityGroup authorityGroup = new AuthorityGroup();
        authorityGroup.setAuthorityGroupKey(authorityGroupDTO.getAuthorityGroupName());
        authorityGroup.setAuthorityKeyList(authorityGroupDTO.getAuthorityNameList());
        authorityGroupStore.saveStore(authorityGroup);
        return true;
    }

    @Override
    public User getUserByToken(String authorization) {
        UserToken store = tokenStore.getStore(authorization);
        return userStore.getStore(store.getUserName());
    }

    @Override
    public void authorityToUser(UserAuthorityDTO userAuthorityDTO) {
        User store = userStore.getStore(userAuthorityDTO.getUserName());

        Set<String> authorityGroup = new HashSet<>(userAuthorityDTO.getAuthorityGroupList());
        store.setAuthorityGroup(authorityGroup);
        userStore.saveStore(store);
    }

    @Override
    public Set<String> getGroupAuthority(String groupName) {
        AuthorityGroup store = authorityGroupStore.getStore(groupName);
        if (null != store) {
            return store.getAuthorityKeyList();
        }
        return null;
    }

    @Override
    public boolean logout(String token) {
        return tokenStore.remove(token);
    }

    private static class UserStore extends AbstractStore<User> {
        public UserStore(String fileName) {
            super(fileName);
        }
    }

    private static class TokenStore extends AbstractStore<UserToken> {
        public TokenStore(String fileName) {
            super(fileName);
        }

        public static String generateToken(String userName) {
            return UUID.randomUUID().toString();
        }

    }

    private static class AuthorityStore extends AbstractStore<Authority> {
        public AuthorityStore(String fileName) {
            super(fileName);
        }
    }

    private static class AuthorityGroupStore extends AbstractStore<AuthorityGroup> {
        public AuthorityGroupStore(String fileName) {
            super(fileName);
        }
    }

    private static class AbstractStore<T extends BaseAuthority> {
        protected static final Logger log = LoggerFactory.getLogger(AbstractStore.class);
        private String fileName;
        private Map<String, T> storeMap = new ConcurrentHashMap<>();

        public AbstractStore(String fileName) {
            this.fileName = fileName;
        }

        protected Map<String, T> getStore() {
            return storeMap;
        }

        protected T getStore(String key) {
            return storeMap.get(key);
        }

        protected boolean saveStore(T authority) {
            storeMap.put(authority.getKey(), authority);
            if (saveFile()) {
                return true;
            }
            storeMap.remove(authority.getKey());
            return false;
        }

        protected boolean saveStore(Map<String, T> map) {
            storeMap.putAll(map);
            if (saveFile()) {
                return true;
            }
            storeMap.entrySet().removeAll(map.entrySet());
            return false;
        }

        protected boolean remove(String key) {
            storeMap.remove(key);
            saveFile();
            return true;
        }

        private boolean saveFile() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                String jsonString = JSON.toJSONString(storeMap.values());
                writer.write(jsonString);
                return true;
            } catch (IOException e) {
                log.error("Save to file error, e");
            }
            return false;
        }

        public void load() {
            if (StringUtils.isBlank(fileName)) {
                log.warn("Load authority store file failed, file name is empty");
                return;
            }
            File file = new File(fileName);
            if (!file.exists()) {
                try {
                    if (!file.createNewFile()) {
                        log.error("Create file {} failed", fileName);
                    }
                } catch (IOException e) {
                    log.error("Create file {} with exception", fileName, e);
                    return;
                }
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                StringBuilder stringBuilder = new StringBuilder("");
                String temp;
                while ((temp = reader.readLine()) != null) {
                    stringBuilder.append(temp);
                }
                if (stringBuilder.length() > 0) {
                    JSONArray jsonArray = JSONArray.parseArray(stringBuilder.toString());
                    Class<? extends BaseAuthority> storeClass =
                            (Class<? extends BaseAuthority>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        BaseAuthority baseAuthority = JSONObject.toJavaObject(jsonObject, storeClass);
                        storeMap.put(baseAuthority.getKey(), (T) baseAuthority);
                    }
                }
            } catch (Exception e) {
                log.error("Load authority store file error,", e);
            }
        }
    }
}
