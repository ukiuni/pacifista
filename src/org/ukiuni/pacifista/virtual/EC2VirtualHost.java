package org.ukiuni.pacifista.virtual;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.ukiuni.pacifista.util.ScriptingUtil;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class EC2VirtualHost implements VirtualHost {
	private static final String SUFFIX_GROUP_NAME = "_s_Security_Group";
	private static final String SUFFIX_KEY_NAME = "_s_Key_Name";
	private Map<String, String> parameterMap = new HashMap<String, String>();
	private String host;
	private File baseDir;
	private String instanceId;

	public EC2VirtualHost(File baseDir, String host) {
		this.baseDir = baseDir;
		this.host = host;
	}

	@Override
	public void setParameters(String parameters) {
		ScriptingUtil.parseParameters(parameterMap, parameters);
	}

	@Override
	public void boot() throws IOException, InterruptedException {
		String instanceId = loadInstanceId();
		StartInstancesRequest startInstancesRequest = new StartInstancesRequest(Arrays.asList(instanceId));
		AmazonEC2Client client = createClient();
		client.startInstances(startInstancesRequest);
	}

	@Override
	public void shutdown() throws IOException, InterruptedException {
		String instanceId = loadInstanceId();
		StopInstancesRequest stopInstancesRequest = new StopInstancesRequest(Arrays.asList(instanceId));
		AmazonEC2Client client = createClient();
		client.stopInstances(stopInstancesRequest);
	}

	@Override
	public boolean isRunning() {
		return isStatus(com.amazonaws.services.ec2.model.InstanceStateName.Running);
	}

	private boolean isStatus(InstanceStateName status) {
		DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
		describeInstancesRequest.setInstanceIds(Arrays.asList(this.instanceId));
		DescribeInstancesResult describeInstancesResult = createClient().describeInstances(describeInstancesRequest);
		if (!describeInstancesResult.getReservations().isEmpty()) {
			return false;
		}
		if (describeInstancesResult.getReservations().get(0).getInstances().isEmpty()) {
			return false;
		}
		return status.equals(describeInstancesResult.getReservations().get(0).getInstances().get(0).getState().getName());
	}

	@Override
	public boolean isExist() throws IOException, InterruptedException {
		return null != loadInstanceId();
	}

	@Override
	public String downloadImage(String url) throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public String downloadImage(String url, String proxyHost, int proxyPort) throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public String downloadImage(String url, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws IOException {
		throw new NotImplementedException();
	}

	public InstanceSSHAddress create() throws IOException, InterruptedException {
		return create(null);
	}

	@Override
	public InstanceSSHAddress create(String stragePath) throws IOException, InterruptedException {
		AmazonEC2Client amazonEC2Client = createClient();

		String securityGroupName = host + SUFFIX_GROUP_NAME;
		String keyName = host + SUFFIX_KEY_NAME;
		CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest();
		createSecurityGroupRequest.withGroupName(securityGroupName).withDescription(host + " s Security Group");
		CreateSecurityGroupResult createSecurityGroupResult = amazonEC2Client.createSecurityGroup(createSecurityGroupRequest);
		IpPermission ipPermission = new IpPermission();
		String ipRange = "0.0.0.0/0";
		if (null != parameterMap.get("sshAccessibleIpRange")) {
			ipRange = parameterMap.get("sshAccessibleIpRange");
		}
		ipPermission.withIpRanges(ipRange).withIpProtocol("tcp").withFromPort(22).withToPort(22);
		AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = new AuthorizeSecurityGroupIngressRequest();
		authorizeSecurityGroupIngressRequest.withGroupName(securityGroupName).withIpPermissions(ipPermission);
		amazonEC2Client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);

		CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();

		createKeyPairRequest.withKeyName(keyName);
		CreateKeyPairResult createKeyPairResult = amazonEC2Client.createKeyPair(createKeyPairRequest);
		KeyPair keyPair = new KeyPair();
		keyPair = createKeyPairResult.getKeyPair();
		String privateKey = keyPair.getKeyMaterial();
		File virtualMacineDir = new File(new File(baseDir, "vmimages"), host);
		virtualMacineDir.mkdirs();
		File keyFile = new File(virtualMacineDir, keyPair.getKeyName() + ".key");
		FileOutputStream out = new FileOutputStream(keyFile);
		out.write(privateKey.getBytes());
		out.close();

		String imageId = "ami-05355a6c";
		if (parameterMap.containsKey("imageId")) {
			imageId = parameterMap.get("imageId");
		}
		String instanceType = "t1.micro";
		if (parameterMap.containsKey("instanceType")) {
			imageId = parameterMap.get("instanceType");
		}
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		runInstancesRequest.withImageId(imageId).withInstanceType(instanceType).withMinCount(1).withMaxCount(1).withKeyName(keyName).withSecurityGroups(securityGroupName);
		RunInstancesResult runInstancesResult = amazonEC2Client.runInstances(runInstancesRequest);

		this.instanceId = runInstancesResult.getReservation().getInstances().get(0).getInstanceId();

		//while (!isStatus(InstanceStateName.Running)) {
		//	Thread.sleep(5000);
		//}
		Thread.sleep(10000);
		
		addTag(amazonEC2Client, "Name", host);

		DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
		describeInstancesRequest.setInstanceIds(Arrays.asList(this.instanceId));
		DescribeInstancesResult describeInstancesResult = amazonEC2Client.describeInstances(describeInstancesRequest);
		return new InstanceSSHAddress(describeInstancesResult.getReservations().get(0).getInstances().get(0).getPublicDnsName(), 22, keyFile.getAbsolutePath());
	}

	private void addTag(AmazonEC2Client amazonEC2Client, String key, String value) {
		ArrayList<Tag> requestTags = new ArrayList<Tag>();
		requestTags.add(new Tag(key, value));
		CreateTagsRequest createTagsRequest = new CreateTagsRequest();
		createTagsRequest.withResources(this.instanceId);
		createTagsRequest.setTags(requestTags);
		amazonEC2Client.createTags(createTagsRequest);
	}

	@Override
	public InstanceSSHAddress create(String stragePath, String type, String memory, int port) throws IOException, InterruptedException {
		return this.create(stragePath);
	}

	private Instance loadInstance() {
		DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
		Filter filter = new Filter("tag:Name", Arrays.asList(host));
		describeInstancesRequest.withFilters(filter);
		AmazonEC2Client client = createClient();
		DescribeInstancesResult describeInstancesResult = client.describeInstances(describeInstancesRequest);
		if (describeInstancesResult.getReservations().isEmpty() || describeInstancesResult.getReservations().get(0).getInstances().isEmpty()) {
			return null;
		} else {
			return describeInstancesResult.getReservations().get(0).getInstances().get(0);
		}
	}

	private String loadInstanceId() {
		if (null != this.instanceId) {
			return this.instanceId;
		}
		Instance instance = loadInstance();
		if (null == instance) {
			return null;
		}
		return instance.getInstanceId();
	}

	@Override
	public void remove() throws IOException, InterruptedException {
		AmazonEC2Client client = createClient();
		String instanceId = loadInstanceId();
		TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest(Arrays.asList(instanceId));
		client.terminateInstances(terminateInstancesRequest);
		// addTag(client, "Name", host+"_deleted");
		//while (!isStatus(InstanceStateName.Terminated)) {
		//	Thread.sleep(5000);
		//}
		try {
			DeleteKeyPairRequest deleteKeyPairRequest = new DeleteKeyPairRequest(host + SUFFIX_KEY_NAME);
			client.deleteKeyPair(deleteKeyPairRequest);
		} catch (Throwable e) {
			// Do nothing.
		}
		try {
			DeleteSecurityGroupRequest deleteSecurityGroupRequest = new DeleteSecurityGroupRequest(host + SUFFIX_GROUP_NAME);
			client.deleteSecurityGroup(deleteSecurityGroupRequest);
		} catch (Throwable e) {
			e.printStackTrace();
			// Do nothing.
		}
	}

	private AmazonEC2Client createClient() {
		BasicAWSCredentials credentials = new BasicAWSCredentials(parameterMap.get("accessKey"), parameterMap.get("secretKey"));
		AmazonEC2Client amazonEC2Client = new AmazonEC2Client(credentials);
		if (null != parameterMap.get("proxyHost")) {
			ClientConfiguration configuration = new ClientConfiguration();
			configuration.setProxyHost(parameterMap.get("proxyHost"));
			configuration.setProxyPort(Integer.valueOf(parameterMap.get("proxyPort")));
			if (null != parameterMap.get("proxyUser")) {
				configuration.setProxyUsername(parameterMap.get("proxyUser"));
				configuration.setProxyPassword(parameterMap.get("proxyPassword"));
			}
			amazonEC2Client.setConfiguration(configuration);
		}
		if (parameterMap.containsKey("endpoint")) {
			amazonEC2Client.setEndpoint(parameterMap.get("endpoint"));
		}
		return amazonEC2Client;
	}
}
