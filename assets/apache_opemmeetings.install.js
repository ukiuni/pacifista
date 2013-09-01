var host = runtime.getEnv("host");
var user = runtime.getEnv("user");
var password = runtime.getEnv("password");
var opUser = runtime.getEnv("opUser");
var opPassword = runtime.getEnv("opPassword");
if (null == host || null == user || null == password) {
	throw "host, user and password must be specified in parameter. like apache_openmeetings.insta..js?user=user&password=password";
}
console.log("setup openmeetings to " + host);
if (null == opUser || null == opPassword) {
	opUser = user;
	password = opPassword;
}
var remote = Remote.create();
function call(command) {
	console.log(command);
	var result = remote.execute(command);
	console.log(result);
}
remote.connect(host, 22, user, password);
//call("sudo yum install -y \*java-1.6\*");
call("wget -O jre1.7.tar.gz http://javadl.sun.com/webapps/download/AutoDL?BundleId=78697");
call("tar -zxvf jre1.7.tar.gz");
call("rm -f jre1.7.tar.gz");
call("sudo mkdir /usr/local/java");
call("sudo mv jre1.7* /usr/local/java");
call("mkdir \"apache-openmeetings\"");
call("sudo yum install -y wget");
call("wget -O apache-openmeetings/apache-openmeetings-2.1.1.zip \"http://ftp.meisei-u.ac.jp/mirror/apache/dist/openmeetings/2.1.1/bin/apache-openmeetings-2.1.1.zip\"");
call("sudo yum install -y unzip");
call("unzip -d apache-openmeetings apache-openmeetings/apache-openmeetings-2.1.1.zip");
call("rm -f apache-openmeetings/apache-openmeetings-2.1.1.zip");
call("sudo mv \"apache-openmeetings\" /usr/local/");
call("/usr/local/apache-openmeetings/admin.sh -i -email admin@example.com -group mygroup -tz \"Asia/Tokyo\" -user " + opUser + " --password " + opPassword);
var shell = remote.startShell();
shell.execute("cd /usr/local/apache-openmeetings");
call("nohup ./red5.sh > /dev/null &");
remote.close();

console.log("called");
