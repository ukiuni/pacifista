var hostName = "MyVirtualMachine"
var host = VirtualMachine.getHost(hostName);
if(host.isExist()){
	if(host.isRunning()){
		host.shutdown();
	}
	host.remove();
}
var virtualImage = host.downloadImage("https://dl.dropbox.com/u/7225008/Vagrant/CentOS-6.3-x86_64-minimal.box");
var sshAddress = host.create(virtualImage);// set image absolute path
host.boot();
var remote = Remote.create();
while(true){
	try{
		remote.connect(sshAddress.getHost(), sshAddress.getPort(), "vagrant", "vagrant");
		break;
	}catch(e){
		runtime.sleep(3000);
		console.log(".");
	}
}
console.log(remote.execute("ls -al"));
//host.shutdown();
//host.remove();
//local.remove("vmimages/"+hostName);