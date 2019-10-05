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
      window.getApp.$on('APP_LOGOUT', () => {
        window.getApp.$axios.delete('/authority/user/logout')
          .then(response => {
            if (response.status === 200 && response.data) {
              localStorage.removeItem('token')
              localStorage.removeItem('authorityList')
              localStorage.removeItem('username')
              window.getApp.$router.replace('/login')
            }
          })
      })
    }
  }
</script>

<style scoped>

</style>
