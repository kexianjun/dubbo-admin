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
            :selection-type="leaf"
            selectable
            return-object
            open-all
          ></v-treeview>
        </v-card>
      </v-flex>
        <v-divider vertical></v-divider>
      <v-flex lg4 sm6 xs12>
        <v-card>
          <template v-if="!selection.length">
            No nodes selected.
          </template>
          <template v-else>
            <div v-for="node in selection" :key="node.id">
              {{ node.name }} : {{ node.key }}
            </div>
          </template>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import menu from '@/api/menu'

  export default {
    name: 'Authority',
    data: () => ({
      treeItems: [],
      selection: []
    }),
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
    }
  }
</script>

<style scoped>
</style>
