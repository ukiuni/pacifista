var hostName = "MyVirtualMachine"
var host = VirtualMachine.getHost(hostName);
if(host.isExist()){
	if(host.isRunning()){
		host.shutdown();
	}
	host.remove();
}
console.log("download Vagrant box. it may cost some minute.");
var virtualImage = host.downloadImage("https://dl.dropbox.com/u/7225008/Vagrant/CentOS-6.3-x86_64-minimal.box");
var sshAddress = host.create(virtualImage);// set image absolute path
host.boot();
var remote = Remote.create();
while(true){
	try{
		remote.connect(sshAddress.getHost(), sshAddress.getPort(), "vagrant", "vagrant");
		break;
	} catch (e){
		console.log("waiting for vm up.");
		runtime.sleep(1000);
	}
}
console.log("vm is now up.");
runtime.setEnv("remote", remote);
runtime.setEnv("host", "vagranthost");
runtime.setEnv("zabbixServer", "zabbixServer");
runtime.call("./assets/install_zabbix-agent_to_linux.js");
