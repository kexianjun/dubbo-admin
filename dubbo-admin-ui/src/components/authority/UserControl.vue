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
                  <v-btn outline small color="tiny" @click.stop="openDialog" class="mb-2">{{$t('edit')}}</v-btn>
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
      resultPage: {}
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
      }
    },
    methods: {
      setHeaders: function () {
        this.headers = [
          {
            text: this.$t('userName'),
            value: 'service',
            align: 'left'
          },
          {
            text: this.$t('password'),
            value: 'group',
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
        this.loadingServices = true
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
          this.loadingServices = false
        })
      },
      getHref: function (userName) {
        return '#/userDetail?userName=' + userName
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
