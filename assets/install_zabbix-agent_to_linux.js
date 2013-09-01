var host = runtime.getEnv("host");
var user = runtime.getEnv("user");
var password = runtime.getEnv("password");
if (null == host || null == user || null == password) {
	throw "host, user and password must be specified in parameter. like apache_openmeetings.insta..js?user=user&password=password";
}
var remote = Remote.create();
remote.connect(host, 22, user, password);
//remote.execute("sudo yum install -y openssh-clients");
remote.sendFile("../data/zabbix.repo", "/tmp/", "zabbix.repo");
remote.execute("sudo mv /tmp/zabbix.repo /etc/yum.repos.d/");
remote.execute("sudo yum install zabbix-agent");