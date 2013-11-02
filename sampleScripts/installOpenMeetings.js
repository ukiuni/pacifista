var host = VirtualMachine.getHost("MyVirtualMachineForOpemMeetings");
if(host.isExist()){
	host.remove();
}
var virtualImage = host.downloadImage("file:///Users/tito/Desktop/pacifista/pacifista.0.0.21/vmimages/MyVirtualMachineForOpemMeetings/CentOS-6.3-x86_64-minimal.box");
//var virtualImage = host.downloadImage("https://dl.dropbox.com/u/7225008/Vagrant/CentOS-6.3-x86_64-minimal.box");
var sshAddress = host.create(virtualImage, "RedHat_64", 1024, 1999);// set image absolute path
host.boot();
runtime.setEnv("host", sshAddress.getHost());
runtime.setEnv("port", sshAddress.getPort());
runtime.setEnv("user", "vagrant");
runtime.setEnv("password", "vagrant");
runtime.call("assets/apache_opemmeetings.install.js?fullInstall=true");
var remote = Remote.create();
remote.connect(sshAddress.getHost(), sshAddress.getPort(), "vagrant", "vagrant");
var openPorts = [1935, 5080, 8088, 9999, 4445];
for(var i in openPorts){
	var openPort = openPorts[i];
	host.openPort("tcp", openPort, openPort);
}
remote.call("sudo /sbin/iptables -F");
//remote.call("sudo /sbin/service iptables save");
remote.close();
console.log("all complete");
