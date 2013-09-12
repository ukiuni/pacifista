var host = VirtualMacine.getHost("MyVirtualMacine");
var virtualImage = host.downloadImage("https://dl.dropbox.com/u/7225008/Vagrant/CentOS-6.3-x86_64-minimal.box");
var sshAddress = host.create(virtualImage);// set image absolute path
host.boot();