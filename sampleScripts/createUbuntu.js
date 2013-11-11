var hostName = "MyUbintuMachine"
var host = VirtualMachine.getHost(hostName);
if(host.isExist()){
	if(host.isRunning()){
		host.shutdown();
	}
	host.remove();
}
console.log("download Vagrant box. it may cost some minute.");
var virtualImage = host.downloadImage("http://files.vagrantup.com/precise64.box");
var sshAddress = host.create(virtualImage);// set image absolute path
host.boot();
var remote = Remote.create();
remote.connect(sshAddress.getHost(), sshAddress.getPort(), "vagrant", "vagrant");
remote.call("sudo apt-get update");
remote.call("sudo apt-get -y upgrade");
remote.call("sudo apt-get -y install --no-install-recommends ubuntu-desktop");