var user = runtime.getEnv("user");
var password = runtime.getEnv("password");
var opUser = runtime.getEnv("opUser");
var opPassword = runtime.getEnv("opPassword");
console.log("called");
if(null == user || null == password){
	throw "user and password must be specified in parameter. like apache_openmeetings.insta..js?user=user&password=password";
}
if(null == opUser || null == opPassword){
	opUser = user;
	password = opPassword;
}
var remote = Remote.create();
remote.connect("virtualhost", 22, user, password);
remote.execute("sudo yum install -y \*java-1.6\*");
remote.ecexute("mkdir apache-openmeetings");
remote.ecexute("sudo yum install -y wget");
remote.execute("wget -o apache-openmeetings/apache-openmeetings-2.1.1.zip \"http://ftp.meisei-u.ac.jp/mirror/apache/dist/openmeetings/2.1.1/bin/apache-openmeetings-2.1.1.zip\"");
remote.ecexute("sudo yum install -y unzip");
remote.execute("unzip -d apache-openmeetings apache-openmeetings/apache-openmeetings-2.1.1.zip");
remote.execute("rm -f apache-openmeetings/apache-openmeetings-2.1.1.zip");
remote.execute("sudo mv apache-openmeetings /usr/local/");
remote.execute("/usr/local/apache-openmeetings/admin.sh -i -email admin@example.com -group mygroup -tz \"Asia/Tokyo\" -user " + opUser + " --password " + opPassword);
remote.execute("nohup /usr/local/apache-openmeetings/red5.sh > /dev/null &");