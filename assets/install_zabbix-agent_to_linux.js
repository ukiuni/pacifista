var host = runtime.getEnv("host");
var user = runtime.getEnv("user");
var password = runtime.getEnv("password");
var zabbixServer = runtime.getEnv("zabbixServer");
var hostName = host;
if (null == host || null == user || null == password || null == zabbixServer) {
	throw "host, user, password and zabbixServer must be specified in parameter. like apache_openmeetings.insta..js?user=user&password=password";
}
var remote = Remote.create();
remote.connect(host, 22, user, password);
remote.execute("sudo yum install -y openssh-clients");
remote.sendFile("../data/zabbix.repo", "/tmp/", "zabbix.repo");
remote.execute("sudo mv /tmp/zabbix.repo /etc/yum.repos.d/");
remote.execute("sudo yum install -y zabbix-agent");
var template = Template.create("zabbix_agentd.conf.vm");
template.put("zabbixServer", zabbixServer);
template.put("hostName", hostName);
var zabbixAgentConf = template.toValue();
remote.send(zabbixAgentConf, "/tmp/", "zabbix_agentd.conf");
remote.execute("sudo mv /tmp/zabbix_agentd.conf /etc/zabbix/zabbix_agentd.conf");
remote.execute("sudo chkconfig zabbix-agent on");
remote.execute("sudo service zabbix-agent start");
remote.close();