<template>
  <v-container id="search" grid-list-xl fluid>
    <v-layout row wrap>
      <v-toolbar flat color="transparent" class="elevation-0">
        <v-toolbar-title><span class="headline">{{$t('authConfig')}}</span></v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn outline color="primary" @click.stop="openDialog" class="mb-2">{{$t('create')}}</v-btn>
      </v-toolbar>
      <v-flex lg4 sm6 xs12>
        <v-card>
          <v-treeview
            v-model="selection"
            :items="treeItems"
            selectable
            return-object
            open-all
          ></v-treeview>
        </v-card>
      </v-flex>
      <v-flex lg4 sm6 xs12>
        <v-card>
          <v-card-text class="pa-0">
            <v-card-title class="justify-center">
              <span class="headline">{{$t('authGroup')}}</span>
            </v-card-title>
            <template>
              <v-data-table
                class="elevation-0 table-striped"
                :headers="headers"
                :items="authorityGroupTable"
                item-key="name"
              >
                <template slot="items" slot-scope="props">
                  <td>{{props.item.name}}</td>
                  <td class="text-xs-center px-0" nowrap>
                    <v-btn outline small color="tiny" @click.stop="openAuthorityDialog(props.item.name)"
                           class="mb-2">
                      {{$t('edit')}}
                    </v-btn>
                    <v-btn outline small color="tiny" @click.stop="openAuthorityGroupDetail(props.item.name)"
                           class="mb-2">
                      {{$t('detail')}}
                    </v-btn>
                  </td>
                </template>
              </v-data-table>
            </template>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>

    <v-dialog v-model="dialog" width="400px" persistent>
      <v-card>
        <v-card-title class="justify-center">
          <span class="headline">{{$t('createAuthorityGroup')}}</span>
        </v-card-title>
        <v-card-text>
          <v-text-field
            :label="$t('authorityGroupName')"
            :hint="$t('authorityGroupNameHint')"
            v-model="authorityGroupName"
          ></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn flat @click.native="closeDialog">{{$t('close')}}</v-btn>
          <v-btn depressed color="primary" @click.native="newAuthorityGroup">{{$t('save')}}</v-btn>
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
  import menu from '@/api/menu'

  export default {
    name: 'Authority',
    data: () => ({
      authorityGroupDetailDialog: false,
      treeItems: [],
      selection: [],
      detailSelection: [],
      authorityGroupName: '',
      dialog: false,
      authorityGroupItems: [],
      headers: []
    }),
    computed: {
      authorityGroupTable: function () {
        return this.authorityGroupItems
      }
    },
    methods: {
      setHeader: function () {
        this.headers = [{
          text: this.$t('authGroup'),
          value: 'name',
          align: 'center'
        }, {
          text: this.$t('operation'),
          value: '',
          align: 'center'
        }]
      },
      openDialog: function () {
        this.authorityGroupName = ''
        this.dialog = true
      },
      closeDialog: function () {
        this.authorityGroupName = ''
        this.dialog = false
      },
      newAuthorityGroup: function () {
        let authorityGroupDTO = {}
        if (!this.authorityGroupName) {
          this.$notify.error('Authority group name is needed')
          return
        }
        if (this.selection.length === 0) {
          this.$notify.error('Please select at least an authority')
          return
        }
        authorityGroupDTO.authorityGroupName = this.authorityGroupName

        authorityGroupDTO.authorityNameList = this.selection.map(function (item, index, arr) {
          return item.key
        })
        this.$axios.post('/authority/authorityGroup/create', authorityGroupDTO)
          .then(response => {
            if (response.status === 200) {
              this.closeDialog()
              this.selection = []
              this.listAuthorityGroup()
              this.$notify.success('Add success')
            }
          })
      },
      listAuthorityGroup: function () {
        this.$axios.get('/authority/authorityGroup/listAuthorityGroup')
          .then(response => {
            if (response.status === 200 && response.data.length > 0) {
              let group = []
              response.data.map(function (item, index) {
                group[index] = {name: item}
              })
              this.authorityGroupItems = group
              console.log(JSON.stringify(group))
            }
          })
      },
      openAuthorityGroupDetail: function () {
        this.authorityGroupDetailDialog = true
      },
      closeAuthorityGroupDetail: function () {
        this.detailSelection = []
        this.authorityGroupDetailDialog = false
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
    mounted: function () {
      this.setHeader()
      this.listAuthorityGroup()
    }
  }
</script>

<style scoped>
</style>
