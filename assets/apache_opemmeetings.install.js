var host = runtime.getEnv("host");
var port = runtime.getEnv("port");
var user = runtime.getEnv("user");
var password = runtime.getEnv("password");
var opUser = runtime.getEnv("opUser");
var opPassword = runtime.getEnv("opPassword");
if (null == host || null == port || null == user || null == password) {
	throw "host, port, user and password must be specified in parameter. like apache_openmeetings.insta..js?user=user&password=password";
}
console.log("setup openmeetings to " + host);
if (null == opUser || null == opPassword) {
	opUser = user;
	opPassword = password;
}
var remote = Remote.create();
remote.connect(host, port, user, password);
var uname = remote.execute("/bin/uname -m");
if(uname.contains("64")){
	local.downloadAsFile("https://dl-web.dropbox.com/s/zgiz3o6i7q9prwh/jre-7u40-linux-x64.rpm", "jre-7u40-linux-x64.rpm");
	remote.sendFile("jre-7u40-linux-x64.rpm", "/tmp", "jre-7u40-linux-x64.rpm");
	local.remove("jre-7u40-linux-x64.rpm");
	remote.call("sudo rpm -ivh /tmp/jre-7u40-linux-x64.rpm");
	remote.call("rm -f /tmp/jre-7u40-linux-x64.rpm");
} else {
	local.downloadAsFile("https://dl-web.dropbox.com/s/y5im9qrb7i3h2yo/jre-7u40-linux-i586.rpm", "jre-7u40-linux-i586.rpm");
	remote.sendFile("jre-7u40-linux-i586.rpm", "/tmp", "jre-7u40-linux-i586.rpm");
	local.remove("jre-7u40-linux-i586.rpm");
	remote.call("sudo rpm -ivh /tmp/jre-7u40-linux-i586.rpm");
	remote.call("rm -f /tmp/jre-7u40-linux-i586.rpm");	
}
var full = false;
if(full){
	remote.call("yum install -y ImageMagick*");
	remote.call("yum install -y ghostscript");
	local.downloadAsFile("http://www.swftools.org/swftools-0.9.2.tar.gz", "swftools-0.9.2.tar.gz");
	remote.sendFile("swftools-0.9.2.tar.gz", "/tmp", "swftools-0.9.2.tar.gz");
	//local.decompress("swftools-0.9.2.tar.gz", "swftools-0.9.2")
	//remote.sendDirectory("swftools-0.9.2", "/tmp/swftools-0.9.2");
	//local.remove("swftools-0.9.2.tar.gz");
	//local.remove("swftools-0.9.2");
	var shell = remote.startShell();
	shell.call("cd /tmp/");
	shell.call("tar -zxvf swftools-0.9.2.tar.gz");
	shell.call("cd swftools-0.9.2");
	shell.call("sudo ./configure");
	shell.call("sudo /usr/bin/make");
	remote.replaceLine("/tmp/swftools-0.9.2/swfs/Makefile", "rm -f $(pkgdatadir)/swfs/default_viewer.swf -o -L $(pkgdatadir)/swfs/default_viewer.swf", "rm -f $(pkgdatadir)/swfs/default_viewer.swf");
	remote.replaceLine("/tmp/swftools-0.9.2/swfs/Makefile", "rm -f $(pkgdatadir)/swfs/default_loader.swf -o -L $(pkgdatadir)/swfs/default_loader.swf", "rm -f $(pkgdatadir)/swfs/default_loader.swf");
	shell.call("sudo /usr/bin/make install");
}
local.downloadAsFile("http://ftp.meisei-u.ac.jp/mirror/apache/dist/openmeetings/2.1.1/bin/apache-openmeetings-2.1.1.zip", "apache-openmeetings-2.1.1.zip");
local.decompress("apache-openmeetings-2.1.1.zip", "apache-openmeetings-2.1.1");
remote.sendDirectory("apache-openmeetings-2.1.1", "/tmp/apache-openmeetings-2.1.1");
local.remove("apache-openmeetings-2.1.1.zip");
local.remove("apache-openmeetings-2.1.1");
remote.call("sudo mv /tmp/apache-openmeetings-2.1.1 /usr/local/");
remote.call("sudo sh /usr/local/apache-openmeetings-2.1.1/admin.sh -i -email admin@example.com -group mygroup -tz \"Asia/Tokyo\" -user " + opUser + " --password " + opPassword);
var shell = remote.startShell();
shell.call("cd /usr/local/apache-openmeetings-2.1.1");
shell.call("nohup sh ./red5.sh > /dev/null 2>&1 &");
shell.close();
remote.close();
console.log("install complete!");
