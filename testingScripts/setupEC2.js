var hostName = "MyVirtualMacine"
var host = VirtualMacine.getHost("AWS", hostName);
host.setParameters("accessKey=AKIAIOWTKMBOJSTI6QBQ&secretKey=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&endpoint=ec2.ap-northeast-1.amazonaws.com&imageId=ami-39b23d38");
if(host.isExist()){
	if(host.isRunning()){
		host.shutdown();
	}
}
host.remove();
var sshAddress = host.create();
host.boot();
var remote = Remote.create();
while(true){
	try{
		remote.connectWithAuthFile(sshAddress.getHost(), sshAddress.getPort(), "ec2-user", sshAddress.getKeyPath());
		break;
	}catch(e){
		runtime.sleep(3000);
		console.log("."+e);
	}
}
runtime.setEnv("remote",remote);
runtime.setEnv("zabbixServer", "zabbixServer");
runtime.setEnv("host", "myhost");
runtime.call("assets/install_zabbix-agent_to_linux.js");
console.log(remote.execute("ls -al"));
//host.shutdown();
//host.remove();
//local.remove("vmimages/"+hostName);