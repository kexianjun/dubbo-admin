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
                <td>{{props.item.authorityGroup}}</td>
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
  </v-container>
</template>

<script>
  import Search from '@/components/public/Search'

  export default {
    name: 'UserControl',
    components: {
      Search
    },
    data: () => ({
      dialog: false,
      authorityDialog: false,
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
      },
      authorityGroupList () {
        return this.authorityGroup
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
