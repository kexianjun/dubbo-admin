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
                    <v-btn outline small color="tiny" @click.stop="openEditDialog(props.item.name)"
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
          <span class="headline">{{editAuthorityGroupDetail ? $t('edit') : $t('detail')}}</span>
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
          <v-btn v-if="editAuthorityGroupDetail" flat @click.native="editAuthorityGroup">{{$t('edit')}}</v-btn>
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
      editAuthorityGroupName: '',
      dialog: false,
      editAuthorityGroupDetail: false,
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
        let authorityList = []
        let selectCount = 0
        for (let i in this.selection) {
          let item = this.selection[i]
          if (item.children) {
            for (let j in item.children) {
              let child = item.children[j]
              authorityList[selectCount++] = child.key
            }
          }
          authorityList[selectCount++] = item.key
        }
        authorityGroupDTO.authorityNameList = authorityList
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
            }
          })
      },
      getAuthorityGroupDetail: function (authorityGroupName) {
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
      },
      openAuthorityGroupDetail: function (authorityGroupName) {
        this.getAuthorityGroupDetail(authorityGroupName)
        this.authorityGroupDetailDialog = true
      },
      closeAuthorityGroupDetail: function () {
        this.detailSelection = []
        this.authorityGroupDetailDialog = false
        this.editAuthorityGroupDetail = false
        this.editAuthorityGroupName = ''
      },
      openEditDialog: function (groupName) {
        this.editAuthorityGroupDetail = true
        this.getAuthorityGroupDetail(groupName)
        this.editAuthorityGroupName = groupName
        this.authorityGroupDetailDialog = true
      },
      editAuthorityGroup: function () {
        if (this.detailSelection.length === 0) {
          this.$notify.error('Please select at least one menu')
        }
        let authorityList = []
        let authorityCount = 0
        for (let i in this.detailSelection) {
          let item = this.detailSelection[i]
          if (item.children > 0) {
            for (let j in item.children) {
              let child = item.children[j]
              authorityList[authorityCount++] = child.key
            }
          }
          authorityList[authorityCount++] = item.key
        }
        console.log(JSON.stringify(this.detailSelection))
        console.log(JSON.stringify(authorityList))
        let authorityGroupDTO = {}
        authorityGroupDTO.authorityGroupName = this.editAuthorityGroupName
        authorityGroupDTO.authorityNameList = authorityList
        this.$axios.post('/authority/authorityGroup/edit', authorityGroupDTO)
          .then(response => {
            if (response.status === 200 && response.data) {
              this.$notify.success('Edit success')
              this.closeAuthorityGroupDetail()
            } else {
              this.$notify.error('Edit failed, please try again')
            }
          })
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
