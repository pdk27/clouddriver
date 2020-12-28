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
import com.netflix.spinnaker.clouddriver.aws.deploy.AutoScalingWorker.AsgConfiguration;
import com.netflix.spinnaker.clouddriver.aws.deploy.LaunchConfigurationBuilder.LaunchConfigurationSettings;
import com.netflix.spinnaker.clouddriver.aws.services.RegionScopedProviderFactory;
import com.netflix.spinnaker.clouddriver.data.task.Task;
import groovy.util.logging.Slf4j;

/** A builder used to build an AWS Autoscaling group. */
@Slf4j
public class AsgWithLaunchConfigurationBuilder extends AsgBuilder {

  AsgWithLaunchConfigurationBuilder(
      RegionScopedProviderFactory.RegionScopedProvider regionScopedProvider) {
    super(regionScopedProvider);
  }

  @Override
  public CreateAutoScalingGroupRequest buildRequest(
      Task task, String taskPhase, String asgName, AsgConfiguration cfg) {

    // create LC settings
    LaunchConfigurationSettings settings = new LaunchConfigurationSettings();
    settings.setAccount(cfg.getCredentials().getName());
    settings.setEnvironment(cfg.getCredentials().getEnvironment());
    settings.setAccountType(cfg.getCredentials().getAccountType());
    settings.setRegion(cfg.getRegion());
    settings.setBaseName(asgName);
    settings.setSuffix(null);
    settings.setAmi(cfg.getAmi());
    settings.setIamRole(cfg.getIamRole());
    settings.setClassicLinkVpcId(cfg.getClassicLinkVpcId());
    settings.setClassicLinkVpcSecurityGroups(cfg.getClassicLinkVpcSecurityGroups());
    settings.setInstanceType(cfg.getInstanceType());
    settings.setKeyPair(cfg.getKeyPair());
    settings.setBase64UserData(cfg.getBase64UserData());
    settings.setAssociatePublicIpAddress(cfg.getAssociatePublicIpAddress());
    settings.setKernelId(cfg.getKernelId());
    settings.setRamdiskId(cfg.getRamdiskId());
    settings.setEbsOptimized(cfg.getEbsOptimized());
    settings.setSpotPrice(cfg.getSpotMaxPrice());
    settings.setInstanceMonitoring(cfg.getInstanceMonitoring());
    settings.setBlockDevices(cfg.getBlockDevices());
    settings.setSecurityGroups(cfg.getSecurityGroups());

    String launchConfigName =
        regionScopedProvider
            .getLaunchConfigurationBuilder()
            .buildLaunchConfiguration(
                cfg.getApplication(), cfg.getSubnetType(), settings, cfg.getLegacyUdf());

    task.updateStatus(
        taskPhase, "Deploying ASG " + asgName + " with launch configuration " + launchConfigName);
    CreateAutoScalingGroupRequest request = buildPartialRequest(task, taskPhase, asgName, cfg);

    return request.withLaunchConfigurationName(launchConfigName);
  }
}
