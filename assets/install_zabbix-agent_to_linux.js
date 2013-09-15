var host = runtime.getEnv("host");
var user = runtime.getEnv("user");
var password = runtime.getEnv("password");
var authFile = runtime.getEnv("authFile");
var zabbixServer = runtime.getEnv("zabbixServer");
var remote = runtime.getEnv("remote");
var isRemoteRecieved = null != remote;
var hostName = host;
if (null == zabbixServer || null == host || (null == remote && ((null == host || null == user) || ( null == password && null == authFile)))) {
	throw "host, user, password and zabbixServer must be specified in parameter. or zabbixServer and host in parameter and remote(Remote Object) in runtime.setEnv, like apache_openmeetings.insta..js?user=user&password=password";
}
if(false)for(var i in remote){
	console.log("arg = "+remote[i]);
}
if(null == remote){
	remote = Remote.create();
	remote.connect(host, 22, user, password);	
} else if(!remote.isConnected()){
	throw "remote object must be connected"
} else {
	
}
remote.execute("sudo yum install -y openssh-clients");
remote.sendFile("./data/zabbix.repo", "/tmp/", "zabbix.repo");
remote.execute("sudo mv /tmp/zabbix.repo /etc/yum.repos.d/");
remote.execute("sudo yum install -y zabbix-agent");
var template = Template.createWithFile("zabbix_agentd.conf.vm");
template.put("zabbixServer", zabbixServer);
template.put("hostName", hostName);
var zabbixAgentConf = template.toValue();
remote.send(zabbixAgentConf, "/tmp/", "zabbix_agentd.conf");
remote.execute("sudo mv /tmp/zabbix_agentd.conf /etc/zabbix/zabbix_agentd.conf");
remote.execute("sudo /sbin/chkconfig zabbix-agent on");
remote.execute("sudo /etc/init.d/zabbix-agent start");
while(!remote.execute("sudo /etc/init.d/zabbix-agent status").contains("running")){
	runtime.sleep(3000);
	console.log("wait for zabbix-agent to running.");
	remote.execute("sudo /etc/init.d/zabbix-agent start");
}
console.log("zabbix-agent is now running.");
if(!isRemoteRecieved){
	remote.close();
}