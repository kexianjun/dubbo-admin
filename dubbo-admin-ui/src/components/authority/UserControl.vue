<template>
  <v-container grid-list-xl fluid>
    <v-layout row wrap>
      <v-flex xs12>
        <search id="userSearch" v-model="filter" :submit="submit" :label="$t('searchUser')"
                :hint="$t('searchUserHint')"></search>
      </v-flex>
    </v-layout>
    <v-flex lg12>
      <v-card>
        <v-toolbar flat color="transparent" class="elevation-0">
          <v-toolbar-title><span class="headline">{{$t('searchResult')}}</span></v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn outline color="primary" @click.stop="openDialog" class="mb-2">{{$t('createUser')}}</v-btn>
        </v-toolbar>

        <v-card-text class="pa-0">
          <template>
            <v-data-table
              class="elevation-0 table-striped"
              :pagination.sync="pagination"
              :total-items="totalItems"
              :headers="headers"
              :items="users"
              :loading="loadingUsers">
              <template slot="items" slot-scope="props">
                <td>{{props.item.userName}}</td>
                <td>{{props.item.password}}</td>
                <td>
                  <v-layout row>
                    <v-flex xs1 lg4 sm6
                            v-for="group in props.item.authorityGroup"
                            :key="group"
                    >
                      <v-btn small color="tiny" @click.stop='showAuthorityGroupDetail(group)' class="mb-2">{{group}}
                      </v-btn>
                    </v-flex>
                  </v-layout>
                </td>
                <td class="text-xs-center px-0" nowrap>
                  <v-btn outline small color="tiny" @click.stop="openAuthorityDialog(props.item.userName)" class="mb-2">
                    {{$t('authority')}}
                  </v-btn>
                </td>
              </template>
            </v-data-table>
          </template>
          <v-divider></v-divider>
        </v-card-text>
      </v-card>
    </v-flex>

    <v-dialog v-model="dialog" width="800px" persistent>
      <v-card>
        <v-card-title class="justify-center">
          <span class="headline">{{$t('createNewUser')}}</span>
        </v-card-title>
        <v-card-text>
          <v-text-field
            :label="$t('userName')"
            :hint="$t('userNameHint')"
            v-model="userName"
          ></v-text-field>

          <v-text-field
            :label="$t('password')"
            :hint="$t('passwordHint')"
            v-model="password"
          ></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn flat @click.native="closeDialog">{{$t('close')}}</v-btn>
          <v-btn depressed color="primary" @click.native="saveItem">{{$t('save')}}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="authorityDialog" width="400px" persistent>
      <v-card>
        <v-card-title class="justify-center">
          <span class="headline">{{$t('authority')}}</span>
        </v-card-title>
        <v-data-table
          v-model="selected"
          :headers="authorityGroupHeader"
          :items="authorityGroup"
          item-key="name"
          class="elevation-1"
          select-all
        >
          <template v-slot:headers="props">
            <tr>
              <th>
                <v-checkbox
                  :input-value="props.all"
                  :indeterminate="props.indeterminate"
                  primary
                  hide-details
                  @click.stop="toggleAll"
                ></v-checkbox>
              </th>
              <th
                v-for="header in props.headers"
                :key="header.text"
                :class="['column sortable', pagination.descending ? 'desc' : 'asc', header.value === pagination.sortBy ? 'active' : '']"
                @click="changeSort(header.value)"
              >
                <v-icon small>arrow_upward</v-icon>
                {{ header.text }}
              </th>
            </tr>
          </template>
          <template v-slot:items="props">
            <tr :active="props.selected" @click="props.selected = !props.selected">
              <td>
                <v-checkbox
                  :input-value="props.selected"
                  primary
                  hide-details
                ></v-checkbox>
              </td>
              <td class="text-xs-center px-0" nowrap>
                {{props.item.name}}
              </td>
            </tr>
          </template>
        </v-data-table>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn flat @click.native="closeAuthorityDialog">{{$t('close')}}</v-btn>
          <v-btn depressed color="primary" @click.native="addAuthorityGroup">{{$t('save')}}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="authorityGroupDetailDialog" width="400px" persistent>
      <v-card>
        <v-card-title class="justify-center">
          <span class="headline">{{$t('detail')}}</span>
        </v-card-title>
        <v-flex lg4 sm6 xs12>
          <v-treeview
            v-model="detailSelection"
            :items="treeItems"
            selectable
            return-object
            open-all
          ></v-treeview>
        </v-flex>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn flat @click.native="closeAuthorityGroupDetail">{{$t('close')}}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
  import Search from '@/components/public/Search'
  import menu from '@/api/menu'

  export default {
    name: 'UserControl',
    components: {
      Search
    },
    data: () => ({
      dialog: false,
      authorityDialog: false,
      authorityGroupDetailDialog: false,
      detailSelection: [],
      treeItems: [],
      userName: '',
      password: '',
      filter: '',
      headers: [],
      pagination: {
        page: 1,
        rowsPerPage: 10 // -1 for All
      },
      totalItems: 0,
      loadingUsers: false,
      resultPage: {},
      selected: [],
      authorityGroupHeader: [],
      authorityGroup: [],
      selectedUserName: ''
    }),
    computed: {
      area () {
        return this.$i18n.locale
      },
      users () {
        if (!this.resultPage || !this.resultPage.content) {
          return []
        }
        return this.resultPage.content
      }
    },
    watch: {
      area () {
        this.setHeaders()
      },
      pagination: {
        handler (newVal, oldVal) {
          if (newVal.page === oldVal.page && newVal.rowsPerPage === oldVal.rowsPerPage) {
            return
          }
          const filter = this.$route.query.filter || '*'
          this.search(filter, false)
        },
        deep: true
      }
    },
    created: function () {
      let itemList = []
      let id = 1
      for (let i in menu) {
        let item = menu[i]
        let treeItem = {}
        treeItem.id = id++
        treeItem.name = this.$t(item.title)
        treeItem.key = item.title
        if (item.items) {
          let children = []
          for (let j in item.items) {
            let child = {}
            child.id = id++
            child.name = this.$t(item.items[j].title)
            child.key = item.items[j].title
            children[parseInt(j)] = child
          }
          treeItem.children = children
        }
        itemList[parseInt(i)] = treeItem
      }
      this.treeItems = itemList
    },
    methods: {
      setHeaders: function () {
        this.headers = [
          {
            text: this.$t('userName'),
            value: 'userName',
            align: 'left'
          },
          {
            text: this.$t('password'),
            value: 'password',
            align: 'left'
          },
          {
            text: this.$t('authGroup'),
            value: 'group',
            align: 'left'
          },
          {
            text: this.$t('operation'),
            value: 'operation',
            sortable: false,
            width: '110px'
          }
        ]
        this.authorityGroupHeader = [{
          text: this.$t('authGroup'),
          value: 'name',
          align: 'center'
        }]
      },
      openDialog: function () {
        this.userName = ''
        this.password = ''
        this.dialog = true
      },
      closeDialog: function () {
        this.userName = ''
        this.password = ''
        this.dialog = false
      },
      saveItem: function () {
        let userDTO = {}
        if (!this.userName || !this.password) {
          this.$notify.error('Username and password key is needed')
          return
        }
        userDTO.userName = this.userName
        userDTO.password = this.password
        this.$axios.put('/authority/user/create', userDTO)
          .then(response => {
            if (response.status === 200) {
              this.closeDialog()
              this.$notify.success('Add success')
              this.search('', true)
            }
          })
      },
      submit () {
        this.search(this.filter, true)
      },
      search: function (filter, rewrite) {
        const page = this.pagination.page - 1
        const size = this.pagination.rowsPerPage === -1 ? this.totalItems : this.pagination.rowsPerPage
        this.loadingUsers = true
        this.$axios.get('/authority/user/list', {
          params: {
            filter,
            page,
            size
          }
        }).then(response => {
          this.resultPage = response.data
          this.totalItems = this.resultPage.totalElements
        }).finally(() => {
          this.loadingUsers = false
        })
      },
      getHref: function (userName) {
        return '#/userDetail?userName=' + userName
      },
      openAuthorityDialog: function (userName) {
        this.$axios.get('/authority/authorityGroup/listAuthorityGroup').then(response => {
          if (response.status === 200 && response.data) {
            let group = []
            response.data.map(function (item, index) {
              group[index] = {name: item}
            })
            this.authorityGroup = group
          }
        })
        this.selectedUserName = userName
        this.authorityDialog = true
      },
      closeAuthorityDialog: function () {
        this.selected = []
        this.selectedUserName = ''
        this.authorityDialog = false
      },
      toggleAll: function () {
        if (this.selected.length) {
          this.selected = []
        } else {
          this.selected = this.authorityGroup
        }
      },
      addAuthorityGroup: function () {
        console.log(JSON.stringify(this.selected))
        if (this.selected.length === 0) {
          this.$notify.error('None authority group has been selected')
          return
        }
        let groupList = []
        this.selected.map(function (item, index) {
          groupList[index] = item.name
        })
        let userAuthorityDTO = {}
        userAuthorityDTO.userName = this.selectedUserName
        userAuthorityDTO.authorityGroupList = groupList
        this.$axios.post('/authority/user/authority', userAuthorityDTO).then(response => {
          if (response.status === 200 && response.data) {
            this.selectedUserName = ''
            this.selected = []
            this.authorityDialog = false
            this.search('', true)
          }
        })
      },
      showAuthorityGroupDetail: function (authorityGroup) {
        this.openAuthorityGroupDetail(authorityGroup)
      },
      openAuthorityGroupDetail: function (authorityGroupName) {
        this.$axios.get('/authority/authorityGroup/getGroupAuthority', {
          params: {groupName: authorityGroupName}
        }).then(response => {
          if (response.status === 200 && response.data.length > 0) {
            let selectedTreeNodes = []
            let selectItems = 0
            for (let treeNodeIndex in this.treeItems) {
              let treeNode = this.treeItems[treeNodeIndex]
              let allNodeSelected = false
              if (treeNode.children) {
                let childSize = 0
                let childItems = []
                for (let childNodeIndex in treeNode.children) {
                  let childNode = treeNode.children[childNodeIndex]
                  for (let authorityTitle in response.data) {
                    if (response.data[authorityTitle] === childNode.key) {
                      childSize++
                      let item = {}
                      item.id = childNode.id
                      item.name = childNode.name
                      item.key = childNode.key
                      childItems[childSize++] = item
                    }
                  }
                }
                if (childSize === treeNode.children.length) {
                  allNodeSelected = true
                  let paramItem = {}
                  paramItem.id = treeNode.id
                  paramItem.name = treeNode.name
                  paramItem.key = treeNode.key
                  paramItem.children = childItems
                  selectedTreeNodes[selectItems++] = paramItem
                } else if (childItems.length > 0) {
                  for (let selectChild in childItems) {
                    selectedTreeNodes[selectItems++] = childItems[selectChild]
                  }
                }
              }
              for (let selectedTitle in response.data) {
                if (!allNodeSelected && response.data[selectedTitle] === treeNode.key) {
                  let item = {}
                  item.id = treeNode.id
                  item.name = treeNode.name
                  item.key = treeNode.key
                  selectedTreeNodes[selectItems++] = item
                }
              }
            }
            this.detailSelection = selectedTreeNodes
          }
        })
        this.authorityGroupDetailDialog = true
      },
      closeAuthorityGroupDetail: function () {
        this.detailSelection = []
        this.authorityGroupDetailDialog = false
      }
    },
    mounted: function () {
      this.setHeaders()
      this.search(this.filter, true)
    }
  }
</script>

<style scoped>

</style>
