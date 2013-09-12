var host = VirtualMacine.getHost("AWS", "MyVirtualMacine");
host.setParameters("accessKey=XXXXXXXXX&secretKey=XXXXXXXXXXXX&endpoint=ec2.ap-northeast-1.amazonaws.com&imageId=ami-39b23d38");
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
		console.log(".");
	}
}
console.log(remote.execute("ls -al"));
//setup zabbix-agent

//host.shutdown();
//host.remove();
//local.remove("vmimages/"+hostName);