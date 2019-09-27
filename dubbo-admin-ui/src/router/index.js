/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Vue from 'vue'
import Router from 'vue-router'
import ServiceSearch from '@/components/ServiceSearch'
import ServiceDetail from '@/components/ServiceDetail'
import TestMethod from '@/components/test/TestMethod'
import RoutingRule from '@/components/governance/RoutingRule'
import TagRule from '@/components/governance/TagRule'
import AccessControl from '@/components/governance/AccessControl'
import LoadBalance from '@/components/governance/LoadBalance'
import WeightAdjust from '@/components/governance/WeightAdjust'
import Overrides from '@/components/governance/Overrides'
import ServiceTest from '@/components/test/ServiceTest'
import ServiceMock from '@/components/test/ServiceMock'
import ServiceMetrics from '@/components/metrics/ServiceMetrics'
import Management from '@/components/Management'
import Authority from '@/components/authority/Authority'
import UserControl from '@/components/authority/UserControl'
import AuthorityGroup from '@/components/authority/AuthorityGroup'
import Login from '../page/Login'
import Index from '../page/Index'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/index',
      name: 'index',
      component: Index,
      meta: {
        requireAuth: true
      },
      children: [
        {
          path: '/service',
          name: 'ServiceSearch',
          component: ServiceSearch,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/serviceDetail',
          name: 'ServiceDetail',
          component: ServiceDetail,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/testMethod',
          name: 'TestMethod',
          component: TestMethod,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/governance/routingRule',
          name: 'RoutingRule',
          component: RoutingRule,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/governance/tagRule',
          name: 'TagRule',
          component: TagRule,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/governance/access',
          name: 'AccessControl',
          component: AccessControl,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/governance/loadbalance',
          name: 'LoadBalance',
          component: LoadBalance,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/governance/weight',
          name: 'WeightAdjust',
          component: WeightAdjust,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/governance/config',
          name: 'Overrides',
          component: Overrides,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/test',
          name: 'ServiceTest',
          component: ServiceTest,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/mock',
          name: 'ServiceMock',
          component: ServiceMock,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/metrics',
          name: 'ServiceMetrics',
          component: ServiceMetrics,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/management',
          name: 'Management',
          component: Management,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/authority/authority',
          name: 'Authority',
          component: Authority,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/authority/user',
          name: 'User',
          component: UserControl,
          meta: {
            requireAuth: true
          }
        },
        {
          path: '/authority/authgroup',
          name: 'AuthorityGroup',
          component: AuthorityGroup,
          meta: {
            requireAuth: true
          }
        }
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    }
  ]
})
