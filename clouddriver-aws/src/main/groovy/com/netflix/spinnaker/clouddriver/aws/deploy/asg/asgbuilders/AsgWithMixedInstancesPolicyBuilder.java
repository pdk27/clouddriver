/*
 * Copyright 2020 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.netflix.spinnaker.clouddriver.aws.deploy.asgbuilders;

import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
// import com.amazonaws.services.ec2.model.LaunchTemplate;
import com.netflix.spinnaker.clouddriver.aws.deploy.AutoScalingWorker.AsgConfiguration;
import com.netflix.spinnaker.clouddriver.aws.services.LaunchTemplateService;
import com.netflix.spinnaker.clouddriver.aws.services.RegionScopedProviderFactory;
import com.netflix.spinnaker.clouddriver.data.task.Task;
import groovy.util.logging.Slf4j;

/** A builder used to build an AWS Autoscaling group. */
@Slf4j
public class AsgWithMixedInstancesPolicyBuilder extends AsgBuilder {
  private LaunchTemplateService ec2LtService;

  AsgWithMixedInstancesPolicyBuilder(
      RegionScopedProviderFactory.RegionScopedProvider regionScopedProvider) {
    super(regionScopedProvider);

    ec2LtService = regionScopedProvider.getLaunchTemplateService();
  }

  @Override
  public CreateAutoScalingGroupRequest buildRequest(
      Task task, String taskPhase, String asgName, AsgConfiguration config) {

    //    // create EC2 LT using LTService
    //
    //    // main LT spec
    //    LaunchTemplateSpecification ltSpec =
    //        new LaunchTemplateSpecification()
    //            .withLaunchTemplateId()
    //            .withLaunchTemplateName()
    //            .withVersion();
    //
    //    // loop over instanceTypesConfig and create a LTSpec and override
    //    //    List<LaunchTemplateOverrides> overrides =
    //    LaunchTemplateOverrides overrides =
    //        new LaunchTemplateOverrides().withInstanceType().withWeightedCapacity();
    //
    //    LaunchTemplate asgLt =
    //        new LaunchTemplate().withLaunchTemplateSpecification(ltSpec).withOverrides(overrides);
    //
    //    MixedInstancesPolicy mixedInsPolicy =
    //        new MixedInstancesPolicy().withLaunchTemplate(asgLt).withInstancesDistribution(dist);
    //
    //    // create LT with overrides
    //    // create InstancesDist from configuration
    //    // add with MIP to Asg request and return
    //
    //    task.updateStatus(
    //        taskPhase, "Deploying ASG " + asgName + " with mixed instances policy " +
    // mixedInsPolicy);
    //    CreateAutoScalingGroupRequest request = buildPartialRequest(task, taskPhase, asgName,
    // cfg);
    //
    //    return request.withMixedInstancesPolicy(mixedInsPolicy);

    return null;
  }
}

// TODO: validations
// - max 20 instance types per ASG
// - limit on LT ?

// https://docs.aws.amazon.com/autoscaling/ec2/userguide/asg-purchase-options.html
// https://docs.aws.amazon.com/autoscaling/ec2/userguide/asg-launch-template-overrides.html
// https://docs.aws.amazon.com/autoscaling/ec2/userguide/asg-override-options.html
