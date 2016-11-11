/*
 * Copyright 2016 Target, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.clouddriver.openstack.controllers

import com.netflix.spinnaker.clouddriver.model.LoadBalancerProviderTempShim
import com.netflix.spinnaker.clouddriver.openstack.model.OpenstackLoadBalancer
import com.netflix.spinnaker.clouddriver.openstack.model.OpenstackLoadBalancerSummary
import com.netflix.spinnaker.clouddriver.openstack.provider.view.OpenstackLoadBalancerProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * TODO Refactor when addressing https://github.com/spinnaker/spinnaker/issues/807
 */
@RestController
@RequestMapping("/openstack/loadBalancers")
class OpenstackLoadBalancerController implements LoadBalancerProviderTempShim {

  OpenstackLoadBalancerProvider provider

  @Autowired
  OpenstackLoadBalancerController(final OpenstackLoadBalancerProvider provider) {
    this.provider = provider
  }

  // TODO: OpenstackLoadBalancerSummary is not a LoadBalancerProviderTempShim.Item, but still
  // compiles anyway because of groovy magic.
  @RequestMapping(method = RequestMethod.GET)
  List<OpenstackLoadBalancerSummary> list() {
    provider.getLoadBalancers('*', '*', '*').collect { lb ->
      new OpenstackLoadBalancerSummary(account: lb.account, region: lb.region, id: lb.id, name: lb.name)
    }.sort { it.name }
  }

  @RequestMapping(value = "/{name:.+}", method = RequestMethod.GET)
  LoadBalancerProviderTempShim.Item get(@PathVariable String name) {
    throw new UnsupportedOperationException("TODO: Support a single getter")
  }

  @RequestMapping(value = "/{account}/{region}/{name:.+}", method = RequestMethod.GET)
  List<OpenstackLoadBalancer.View> byAccountAndRegionAndName(@PathVariable String account,
                                                             @PathVariable String region,
                                                             @PathVariable String name) {
    provider.getLoadBalancers(account, region, name) as List
  }
}
