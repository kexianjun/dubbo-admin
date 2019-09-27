<template>
  <v-app id="inspire">
    <v-content>
      <v-container fluid fill-height>
        <v-layout align-center justify-center>
          <v-flex xs12 sm8 md4>
            <v-card class="elevation-12">
              <v-toolbar dark color="primary">
                <v-spacer></v-spacer>
              </v-toolbar>
              <v-card-text>
                <v-form action='login' >
                  <v-text-field required
                                name="username"
                                v-model="userName"
                                append-icon="person"
                                :label="$t('userName')" type="text">
                  </v-text-field>
                  <v-text-field
                    name="input-10-2"
                    :label="$t('password')"
                    hint="At least 8 characters"
                    min="8"
                    :append-icon="e2 ? 'visibility' : 'visibility_off'"
                    :append-icon-cb="() => (e2 = !e2)"
                    v-model="password"
                    class="input-group--focused"
                    :type="e2 ? 'password' : 'text'"
                  ></v-text-field>

                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn @click="login" color="primary">{{$t('login')}}<v-icon>send</v-icon></v-btn>
                    <v-spacer></v-spacer>
                  </v-card-actions>
                </v-form>
              </v-card-text>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
    <footers></footers>
  </v-app>
</template>

<script>
  import Footers from '../components/public/Footers'

  export default {
    name: 'Login',
    data: () => ({
      userName: '',
      password: '',
      e2: true
    }),
    components: {
      Footers
    },
    methods: {
      closeDialog: function () {
      },
      login: function () {
        let userName = this.userName
        let password = this.password
        let vm = this
        this.$axios.get('/authority/user/login', {
          params: {
            userName,
            password
          }
        }).then(response => {
          console.log(response)
          if (response.status === 200 && response.data) {
            localStorage.setItem('token', response.data.token)
            localStorage.setItem('authorityList', JSON.stringify(response.data.authoritys))
            console.log('login success')
            this.$router.replace('/index')
          } else {
            vm.$notify('Login Failed')
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
