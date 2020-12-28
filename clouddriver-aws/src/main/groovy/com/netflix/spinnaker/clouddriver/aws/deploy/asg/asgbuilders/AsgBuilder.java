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
import com.netflix.spinnaker.clouddriver.aws.services.RegionScopedProviderFactory;
import com.netflix.spinnaker.clouddriver.data.task.Task;
import com.netflix.spinnaker.kork.core.RetrySupport;
import groovy.util.logging.Slf4j;

/**
 * A builder used to build an AWS Autoscaling group.
 *
 * @param <T> configuration / settings required to build an ASG
 */
@Slf4j
public abstract class AsgBuilder {
  private final RetrySupport retrySupport = new RetrySupport();
  protected RegionScopedProviderFactory.RegionScopedProvider regionScopedProvider;

  AsgBuilder(RegionScopedProviderFactory.RegionScopedProvider regionScopedProvider) {
    this.regionScopedProvider = regionScopedProvider;
  }

  // TODO: worker determines what type of ASG to build / allow or reject conditioanl things
  // worker calls builder to build the request and create / update Asg

  // start: abstract methods
  public abstract CreateAutoScalingGroupRequest buildRequest(
      Task task, String taskPhase, String asgName, AsgConfiguration configuration);
  // end: abstract methods

  public String build(Task task, String taskPhase, String asgName, AsgConfiguration config) {
    return createAsg(buildRequest(task, taskPhase, asgName, config));
  }

  protected CreateAutoScalingGroupRequest buildPartialRequest(
      Task task, String taskPhase, String name, AsgConfiguration cfg) {
    //    CreateAutoScalingGroupRequest request = new CreateAutoScalingGroupRequest()
    //            .withAutoScalingGroupName(name)
    //            .withMinSize(0)
    //            .withMaxSize(0)
    //            .withDesiredCapacity(0)
    //            .withLoadBalancerNames(cfg.classicLoadBalancers)
    //            .withTargetGroupARNs(cfg.targetGroupArns)
    //            .withDefaultCooldown(cfg.cooldown)
    //            .withHealthCheckGracePeriod(cfg.healthCheckGracePeriod)
    //            .withHealthCheckType(cfg.healthCheckType)
    //            .withTerminationPolicies(cfg.terminationPolicies);
    //
    //    cfg.tags?.each { key, value ->
    //            request.withTags(new Tag()
    //                    .withKey(key)
    //                    .withValue(value)
    //                    .withPropagateAtLaunch(true))
    //    }
    //
    //    // if we have explicitly specified subnetIds, don't require that they are tagged with a
    // subnetType/purpose
    //    boolean filterForSubnetPurposeTags = !cfg..subnetIds
    //    // Favor subnetIds over availability zones
    //    def subnetIds =
    // Helper.getSubnetIds(Helper.getSubnets(filterForSubnetPurposeTags))?.join(',')
    //    if (subnetIds) {
    //      task.updateStatus AWS_PHASE, " > Deploying to subnetIds: $subnetIds" // todo: update
    // task in worker after request is built?
    //      request.withVPCZoneIdentifier(subnetIds)
    //    } else if (cfg.subnetType && !getSubnets()) {
    //      throw new RuntimeException("No suitable subnet was found for internal subnet purpose
    // '${subnetType}'!")
    //    } else {
    //      task.updateStatus AWS_PHASE, "Deploying to availabilityZones: $availabilityZones"
    //      request.withAvailabilityZones(cfg.availabilityZones)
    //    }
    return null;
  }

  private String createAsg(CreateAutoScalingGroupRequest request) {
    //    // todo: move retry logic and create/ udpate code from createAutoScalingGroup
    //
    //    String asgName = request.getAutoScalingGroupName()
    //
    //    // create ASG
    //    def autoScaling = regionScopedProvider.autoScaling
    //    Exception ex = retrySupport.retry({ ->
    //    try {
    //      autoScaling.createAutoScalingGroup(request)
    //      return null
    //    } catch (AlreadyExistsException e) {
    //      if (!shouldProceedWithExistingState(autoScaling, asgName, request)) {
    //        return e
    //      }
    //      log.debug("Determined pre-existing ASG is desired state, continuing...", e)
    //      return null
    //    }
    //    }, 10, 1000, false)
    //    if (ex != null) {
    //      throw ex
    //    }
    //
    //    // if ASG already exists, ....todo:
    //
    //    // configure lifecycle hooks
    //    if (cfg.lifecycleHooks != null && !cfg.lifecycleHooks.isEmpty()) {
    //      Exception e = retrySupport.retry({ ->
    //      task.updateStatus AWS_PHASE, "Creating lifecycle hooks for: $asgName"
    //      regionScopedProvider.asgLifecycleHookWorker.attach(task, lifecycleHooks, asgName)
    //      }, 10, 1000, false)
    //
    //      if (e != null) {
    //        task.updateStatus AWS_PHASE, "Unable to attach lifecycle hooks to ASG ($asgName):
    // ${e.message}"
    //      }
    //    }
    //
    //    // todo:
    //    if (cfg.suspendedProcesses) {
    //      retrySupport.retry({ ->
    //      autoScaling.suspendProcesses(new SuspendProcessesRequest(autoScalingGroupName: asgName,
    // scalingProcesses: suspendedProcesses))
    //      }, 10, 1000, false)
    //    }
    //
    //
    //    // enable metrics and monitoring
    //    if (cfg.enabledMetrics && cfg.instanceMonitoring) {
    //      task.updateStatus AWS_PHASE, "Enabling metrics collection for: $asgName"
    //      retrySupport.retry({ ->
    //      autoScaling.enableMetricsCollection(new EnableMetricsCollectionRequest()
    //              .withAutoScalingGroupName(asgName)
    //              .withGranularity('1Minute')
    //              .withMetrics(cfg.enabledMetrics))
    //      }, 10, 1000, false)
    //    }
    //
    //    // update ASG
    //    retrySupport.retry({ ->
    //    task.updateStatus AWS_PHASE, "Setting size of $asgName in
    // ${cfg.credentials.name}/$cfg.region to " +
    //            "[min=$cfg.minInstances, max=$cfg.maxInstances, desired=$cfg.desiredInstances]"
    //    autoScaling.updateAutoScalingGroup(
    //            new UpdateAutoScalingGroupRequest(
    //                    autoScalingGroupName: asgName,
    //            minSize: cfg.minInstances,
    //            maxSize: cfg.maxInstances,
    //            desiredCapacity: cfg.desiredInstances
    //        )
    //      )
    //    }, 10, 1000, false)
    //
    //    asgName
    //  }
    //
    //  private boolean shouldProceedWithExistingState(AmazonAutoScaling autoScaling, String
    // asgName, CreateAutoScalingGroupRequest request) {
    //    DescribeAutoScalingGroupsResult result = autoScaling.describeAutoScalingGroups(
    //      new DescribeAutoScalingGroupsRequest().withAutoScalingGroupNames(asgName)
    //    )
    //    if (result.autoScalingGroups.isEmpty()) {
    //      // This will only happen if we get an AlreadyExistsException from AWS, then immediately
    // after describing it, we
    //      // don't get a result back. We'll continue with trying to create because who knows...
    // may as well try.
    //      log.error("Attempted to find pre-existing ASG but none was found: $asgName")
    //      return true
    //    }
    //    AutoScalingGroup existingAsg = result.autoScalingGroups.first()
    //
    //    Set<String> failedPredicates = [
    //      "launch configuration": { return existingAsg.launchConfigurationName ==
    // request.launchConfigurationName },
    //      "launch template": { return existingAsg.launchTemplate == request.launchTemplate },
    //      "availability zones": { return existingAsg.availabilityZones.sort() ==
    // request.availabilityZones.sort() },
    //      "subnets": { return existingAsg.getVPCZoneIdentifier()?.split(",")?.sort()?.toList() ==
    // request.getVPCZoneIdentifier()?.split(",")?.sort()?.toList() },
    //      "load balancers": { return existingAsg.loadBalancerNames.sort() ==
    // request.loadBalancerNames.sort() },
    //      "target groups": { return existingAsg.targetGroupARNs.sort() ==
    // request.targetGroupARNs.sort() },
    //      "cooldown": { return existingAsg.defaultCooldown == request.defaultCooldown },
    //      "health check grace period": { return existingAsg.healthCheckGracePeriod ==
    // request.healthCheckGracePeriod },
    //      "health check type": { return existingAsg.healthCheckType == request.healthCheckType },
    //      "termination policies": { return existingAsg.terminationPolicies.sort() ==
    // request.terminationPolicies.sort() }
    //    ].findAll { !((Supplier<Boolean>) it.value).get() }.keySet()
    //
    //    if (!failedPredicates.isEmpty()) {
    //      task.updateStatus AWS_PHASE, "$asgName already exists and does not seem to match desired
    // state on: ${failedPredicates.join(", ")}"
    //      return false
    //    }
    //    if (existingAsg.createdTime.toInstant().isBefore(Instant.now().minus(1,
    // ChronoUnit.HOURS))) {
    //      task.updateStatus AWS_PHASE, "$asgName already exists and appears to be valid, but falls
    // outside of safety window for idempotent deploy (1 hour)"
    //      return false
    //    }
    //
    //    return true
    //  }
    //
    //  /**
    //   * This is an obscure rule that Subnets are tagged at Amazon with a data structure, which
    // defines their purpose and
    //   * what type of resources (elb or ec2) are able to make use of them. We also need to ensure
    // that the Subnet IDs that
    //   * we provide back are able to be deployed to based off of the supplied availability zones.
    //   *
    //   * @return list of subnet ids applicable to this deployment.
    //   */
    //  private List<String> getSubnetIds(List<Subnet> allSubnetsForTypeAndAvailabilityZone) {
    //    def subnetIds = allSubnetsForTypeAndAvailabilityZone*.subnetId
    //
    //    def invalidSubnetIds = (this.subnetIds ?: []).findAll { !subnetIds.contains(it) }
    //    if (invalidSubnetIds) {
    //      throw new IllegalStateException(
    //              "One or more subnet ids are not valid (invalidSubnetIds:
    // ${invalidSubnetIds.join(", ")}, availabilityZones: ${availabilityZones})"
    //      )
    //    }
    //
    //    return this.subnetIds ?: subnetIds
    //  }
    //
    //  private List<Subnet> getSubnets(boolean filterForSubnetPurposeTags = true) {
    //    if (!subnetType) {
    //      return []
    //    }
    //
    //    DescribeSubnetsResult result = regionScopedProvider.amazonEC2.describeSubnets()
    //    List<Subnet> mySubnets = []
    //    for (subnet in result.subnets) {
    //      if (availabilityZones && !availabilityZones.contains(subnet.availabilityZone)) {
    //        continue
    //      }
    //      if (filterForSubnetPurposeTags) {
    //        SubnetData sd = SubnetData.from(subnet)
    //        if (sd.purpose == subnetType && (sd.target == null || sd.target == SubnetTarget.EC2))
    // {
    //          mySubnets << subnet
    //        }
    //      } else {
    //        mySubnets << subnet
    //      }
    //    }
    //    mySubnets

    return null;
  }
}
