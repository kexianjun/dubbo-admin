<template>
  <v-app :dark="dark">
    <drawer></drawer>
    <toolbar></toolbar>
    <v-content>
      <router-view/>
    </v-content>
    <footers></footers>
  </v-app>
</template>

<script>
  import Drawer from '@/components/public/Drawer'
  import Toolbar from '@/components/public/Toolbar'
  import Footers from '@/components/public/Footers'

  export default {
    name: 'Index',
    components: {
      Drawer,
      Toolbar,
      Footers
    },
    data () {
      return {
        dark: false
      }
    },
    created () {
      window.getApp = this
    }
  }
</script>

<style scoped>

</style>
