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
import com.amazonaws.services.autoscaling.model.LaunchTemplateSpecification;
import com.amazonaws.services.ec2.model.LaunchTemplate;
import com.netflix.spinnaker.clouddriver.aws.deploy.AsgConfigHelper;
import com.netflix.spinnaker.clouddriver.aws.deploy.AutoScalingWorker.AsgConfiguration;
import com.netflix.spinnaker.clouddriver.aws.services.LaunchTemplateService;
import com.netflix.spinnaker.clouddriver.aws.services.RegionScopedProviderFactory;
import com.netflix.spinnaker.clouddriver.aws.services.SecurityGroupService;
import com.netflix.spinnaker.clouddriver.data.task.Task;
import com.netflix.spinnaker.config.AwsConfiguration.DeployDefaults;
import groovy.util.logging.Slf4j;

/** A builder used to build an AWS Autoscaling group. */
@Slf4j
public class AsgWithLaunchTemplateBuilder extends AsgBuilder {
  private LaunchTemplateService ltService;
  private SecurityGroupService securityGroupService;
  private DeployDefaults deployDefaults;

  AsgWithLaunchTemplateBuilder(
      RegionScopedProviderFactory.RegionScopedProvider regionScopedProvider) {
    super(regionScopedProvider);

    ltService = regionScopedProvider.getLaunchTemplateService();
    securityGroupService = regionScopedProvider.getSecurityGroupService();
    deployDefaults = regionScopedProvider.getDeploymentDefaults();
  }

  @Override
  public CreateAutoScalingGroupRequest buildRequest(
      Task task, String taskPhase, String asgName, AsgConfiguration config) {

    config = AsgConfigHelper.setAppSecurityGroup(config, securityGroupService, deployDefaults);

    final LaunchTemplate lt =
        regionScopedProvider
            .getLaunchTemplateService()
            .createLaunchTemplate(config, asgName, AsgConfigHelper.createName(asgName, null));

    final LaunchTemplateSpecification ltSpec =
        (new LaunchTemplateSpecification()
            .withLaunchTemplateId(lt.getLaunchTemplateId())
            .withVersion(lt.getLatestVersionNumber().toString()));

    task.updateStatus(
        taskPhase,
        "Deploying ASG " + asgName + " with launch template " + lt.getLaunchTemplateId());
    CreateAutoScalingGroupRequest request = buildPartialRequest(task, taskPhase, asgName, config);

    return request.withLaunchTemplate(ltSpec);
  }
}
