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
var shell = remote.startShell(30000);
var uname = remote.execute("/bin/uname -m");
var remoteIs64 = uname.contains("64");


var fullInstall = runtime.getEnv("fullInstall");
if(fullInstall){
	remote.call("sudo yum install -y ImageMagick*");
	remote.call("sudo yum install -y ghostscript");
	local.downloadAsFile("http://www.swftools.org/swftools-0.9.2.tar.gz", "swftools-0.9.2.tar.gz");
	local.decompress("swftools-0.9.2.tar.gz", ".")
	local.remove("swftools-0.9.2.tar.gz");
	remote.sendDirectory("swftools-0.9.2", "/tmp/swftools-0.9.2");
	local.remove("swftools-0.9.2");
	shell.call("cd /tmp/swftools-0.9.2");
	shell.call("sh ./configure");
	shell.call("make");
	remote.replaceLine("/tmp/swftools-0.9.2/swfs/Makefile", "\\s*rm -f \\$\\(pkgdatadir\\)/swfs/default_viewer.swf -o -L \\$\\(pkgdatadir\\)/swfs/default_viewer.swf", "\t\trm -f $(pkgdatadir)/swfs/default_viewer.swf");
	remote.replaceLine("/tmp/swftools-0.9.2/swfs/Makefile", "\\s*rm -f \\$\\(pkgdatadir\\)/swfs/default_loader.swf -o -L \\$\\(pkgdatadir\\)/swfs/default_loader.swf", "\t\trm -f $(pkgdatadir)/swfs/default_loader.swf");
	shell.call("sudo make install");
	if(remoteIs64){
		local.downloadAsFile("http://download.documentfoundation.org/libreoffice/stable/4.1.2/rpm/x86_64/LibreOffice_4.1.2_Linux_x86-64_rpm.tar.gz", "LibreOffice_4.1.2_Linux_x86-64_rpm.tar.gz");
		local.downloadAsFile("http://download.documentfoundation.org/libreoffice/stable/4.1.2/rpm/x86_64/LibreOffice_4.1.2_Linux_x86-64_rpm_langpack_ja.tar.gz", "LibreOffice_4.1.2_Linux_x86-64_rpm_langpack_ja.tar.gz");
		local.downloadAsFile("http://download.documentfoundation.org/libreoffice/stable/4.1.2/rpm/x86_64/LibreOffice_4.1.2_Linux_x86-64_rpm_helppack_ja.tar.gz", "LibreOffice_4.1.2_Linux_x86-64_rpm_helppack_ja.tar.gz");
		local.decompress("LibreOffice_4.1.2_Linux_x86-64_rpm.tar.gz", "LibreOffice_4.1.2_Linux_x86-64_rpm")
		local.remove("LibreOffice_4.1.2_Linux_x86-64_rpm.tar.gz");
		remote.sendDirectory("LibreOffice_4.1.2_Linux_x86-64_rpm/LibreOffice_4.1.2.3_Linux_x86-64_rpm", "/tmp/LibreOffice_4.1.2.3_Linux_x86-64_rpm")
		local.remove("LibreOffice_4.1.2_Linux_x86-64_rpm");
		shell.call("cd /tmp/LibreOffice_4.1.2.3_Linux_x86-64_rpm/RPMS");
		shell.call("sudo yum localinstall -y --nogpgcheck lib*");

		local.decompress("LibreOffice_4.1.2_Linux_x86-64_rpm_langpack_ja.tar.gz", "LibreOffice_4.1.2_Linux_x86-64_rpm_langpack_ja")
		local.remove("LibreOffice_4.1.2_Linux_x86-64_rpm_langpack_ja.tar.gz");
		remote.sendDirectory("LibreOffice_4.1.2_Linux_x86-64_rpm_langpack_ja/LibreOffice_4.1.2.3_Linux_x86-64_rpm_langpack_ja", "/tmp/LibreOffice_4.1.2_Linux_x86-64_rpm_langpack_ja")
		local.remove("LibreOffice_4.1.2_Linux_x86-64_rpm_langpack_ja");
		shell.call("cd /tmp/LibreOffice_4.1.2_Linux_x86-64_rpm_langpack_ja/RPMS");
		shell.call("sudo yum localinstall -y --nogpgcheck lib*");

		local.decompress("LibreOffice_4.1.2_Linux_x86-64_rpm_helppack_ja.tar.gz", "LibreOffice_4.1.2_Linux_x86-64_rpm_helppack_ja")
		local.remove("LibreOffice_4.1.2_Linux_x86-64_rpm_helppack_ja.tar.gz");
		remote.sendDirectory("LibreOffice_4.1.2_Linux_x86-64_rpm_helppack_ja/LibreOffice_4.1.2.3_Linux_x86-64_rpm_helppack_ja", "/tmp/LibreOffice_4.1.2_Linux_x86-64_rpm_helppack_ja")
		local.remove("LibreOffice_4.1.2_Linux_x86-64_rpm_helppack_ja");
		shell.call("cd /tmp/LibreOffice_4.1.2_Linux_x86-64_rpm_helppack_ja/RPMS");
		shell.call("sudo yumlocalinstall -y --nogpgcheck lib*");
		
		remote.call("sudo ln -s /opt/libreoffice4.1 /opt/libreffice");
	} else {
		local.downloadAsFile("http://download.documentfoundation.org/libreoffice/stable/4.1.2/rpm/x86/LibreOffice_4.1.2_Linux_x86_rpm.tar.gz", "LibreOffice_4.1.2_Linux_x86_rpm.tar.gz");
		local.downloadAsFile("http://download.documentfoundation.org/libreoffice/stable/4.1.2/rpm/x86/LibreOffice_4.1.2_Linux_x86_rpm_langpack_ja.tar.gz", "LibreOffice_4.1.2_Linux_x86_rpm_langpack_ja.tar.gz");
		local.downloadAsFile("http://download.documentfoundation.org/libreoffice/stable/4.1.2/rpm/x86/LibreOffice_4.1.2_Linux_x86_rpm_helppack_ja.tar.gz", "LibreOffice_4.1.2_Linux_x86_rpm_helppack_ja.tar.gz");
		local.decompress("LibreOffice_4.1.2_Linux_x86_rpm.tar.gz", "LibreOffice_4.1.2_Linux_x86_rpm")
		local.remove("LibreOffice_4.1.2_Linux_x86_rpm.tar.gz");
		remote.sendDirectory("LibreOffice_4.1.2_Linux_x86_rpm/LibreOffice_4.1.2.3_Linux_x86_rpm", "/tmp/LibreOffice_4.1.2.3_Linux_x86_rpm")
		local.remove("LibreOffice_4.1.2_Linux_x86_rpm");
		shell.call("cd /tmp/LibreOffice_4.1.2.3_Linux_x86_rpm/RPMS");
		shell.call("sudo yum localinstall -y --nogpgcheck lib*");

		local.decompress("LibreOffice_4.1.2_Linux_x86_rpm_langpack_ja.tar.gz", "LibreOffice_4.1.2_Linux_x86_rpm_langpack_ja")
		local.remove("LibreOffice_4.1.2_Linux_x86_rpm_langpack_ja.tar.gz");
		remote.sendDirectory("LibreOffice_4.1.2_Linux_x86_rpm_langpack_ja/LibreOffice_4.1.2.3_Linux_x86_rpm_langpack_ja", "/tmp/LibreOffice_4.1.2_Linux_x86_rpm_langpack_ja")
		local.remove("LibreOffice_4.1.2_Linux_x86_rpm_langpack_ja");
		shell.call("cd /tmp/LibreOffice_4.1.2_Linux_x86_rpm_langpack_ja/RPMS");
		shell.call("sudo yum localinstall -y --nogpgcheck lib*");

		local.decompress("LibreOffice_4.1.2_Linux_x86_rpm_helppack_ja.tar.gz", "LibreOffice_4.1.2_Linux_x86_rpm_helppack_ja")
		local.remove("LibreOffice_4.1.2_Linux_x86_rpm_helppack_ja.tar.gz");
		remote.sendDirectory("LibreOffice_4.1.2_Linux_x86_rpm_helppack_ja/LibreOffice_4.1.2.3_Linux_x86_rpm_helppack_ja", "/tmp/LibreOffice_4.1.2_Linux_x86_rpm_helppack_ja")
		local.remove("LibreOffice_4.1.2_Linux_x86_rpm_helppack_ja");
		shell.call("cd /tmp/LibreOffice_4.1.2_Linux_x86_rpm_helppack_ja/RPMS");
		shell.call("sudo yumlocalinstall -y --nogpgcheck lib*");
		
		remote.call("sudo ln -s /opt/libreoffice4.1 /opt/libreffice");
	}
	
	local.downloadAsFile("https://jodconverter.googlecode.com/files/jodconverter-core-3.0-beta-4-dist.zip", "jodconverter-core-3.0-beta-4-dist.zip");
	local.decompress("jodconverter-core-3.0-beta-4-dist.zip", "jodconverter-core-3.0-beta-4-dist");
	local.remove("jodconverter-core-3.0-beta-4-dist.zip");
	remote.sendDirectory("jodconverter-core-3.0-beta-4-dist", "/tmp/jodconverter-core-3.0-beta-4-dist");
	local.remove("jodconverter-core-3.0-beta-4-dist");
	remote.execute("sudo mv /tmp/jodconverter-core-3.0-beta-4-dist /usr/lib");
	
	local.downloadAsFile("https://dl-web.dropbox.com/s/3dnsr3mrlmz1olc/lame-3.99.5.tar.gz", "lame-3.99.5.tar.gz");
	local.decompress("lame-3.99.5.tar.gz",  ".");
	local.remove("lame-3.99.5.tar.gz");
	remote.sendDirectory("lame-3.99.5", "/tmp/lame-3.99.5");
	local.remove("lame-3.99.5");
	shell.call("cd /tmp/lame-3.99.5");
	shell.call("sh ./configure");
	shell.call("make");
	shell.call("sudo make install");
/*	
	local.downloadAsFile("http://ffmpeg.org/releases/ffmpeg-1.2.1.tar.gz", "ffmpeg-1.2.1.tar.gz");
	local.decompress("ffmpeg-1.2.1.tar.gz", ".");
	local.remove("ffmpeg-1.2.1.tar.gz");
	remote.sendDirectory("ffmpeg-1.2.1", "/tmp/ffmpeg-1.2.1");
	local.remove("ffmpeg-1.2.1");
	shell.call("cd /tmp/ffmpeg-1.2.1");
	shell.call("sh ./configure --enable-libmp3lame --disable-yasm");
	shell.call("make");
	shell.call("sudo make install");
*/
	local.downloadAsFile("http://packages.sw.be/rpmforge-release/rpmforge-release-0.5.2-2.el6.rf.x86_64.rpm", "rpmforge-release-0.5.2-2.el6.rf.x86_64.rpm");
	remote.sendFile("rpmforge-release-0.5.2-2.el6.rf.x86_64.rpm", "/tmp", "rpmforge-release-0.5.2-2.el6.rf.x86_64.rpm");
	local.remove("rpmforge-release-0.5.2-2.el6.rf.x86_64.rpm");
	remote.call("echo \"[base]\" | sudo tee /etc/yum.repos.d/centos.repo");
	remote.call("echo \"name=CentOS-6 - Base\" | sudo tee -a /etc/yum.repos.d/centos.repo");
	remote.call("echo \"mirrorlist=http://mirrorlist.centos.org/?release=6&arch=x86_64&repo=os\" | sudo tee -a /etc/yum.repos.d/centos.repo");
	remote.call("echo \"enabled=0\" | sudo tee -a /etc/yum.repos.d/centos.repo");
	remote.call("echo \"gpgcheck=1\" | sudo tee -a /etc/yum.repos.d/centos.repo");
	remote.call("echo \"gpgkey=http://mirror.centos.org/centos/RPM-GPG-KEY-CentOS-6\" | sudo tee -a /etc/yum.repos.d/centos.repo");
	remote.call("sudo rpm -Uvh /tmp/rpmforge-release-0.5.2-2.el6.rf.x86_64.rpm");
	remote.call("sudo yum install -y --enablerepo=base ffmpeg-devel");
	
	remote.call("sudo yum install -y sox");
	
	remote.call("sudo yum install -y mysql-server");
	remote.call("sudo /etc/init.d/mysqld start")
	remote.call("sudo /usr/bin/mysql -uroot -e'CREATE DATABASE openmeetings default character set utf8'");
}

local.downloadAsFile("http://ftp.meisei-u.ac.jp/mirror/apache/dist/openmeetings/2.1.1/bin/apache-openmeetings-2.1.1.zip", "apache-openmeetings-2.1.1.zip");
local.decompress("apache-openmeetings-2.1.1.zip", "apache-openmeetings-2.1.1");

remote.sendDirectory("apache-openmeetings-2.1.1", "/tmp/apache-openmeetings-2.1.1");
local.remove("apache-openmeetings-2.1.1.zip");
local.remove("apache-openmeetings-2.1.1");
remote.call("sudo mv /tmp/apache-openmeetings-2.1.1 /usr/local/");
if(fullInstall) {
	remote.call("mv /usr/local/apache-openmeetings-2.1.1/webapps/openmeetings/WEB-INF/classes/META-INF/mysql_persistence.xml  /usr/local/apache-openmeetings-2.1.1/webapps/openmeetings/WEB-INF/classes/META-INF/persistence.xml");
	local.downloadAsFile("http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.26.tar.gz/from/http://cdn.mysql.com/", "mysql-connector-java-5.1.26.tar.gz");
	local.decompress("mysql-connector-java-5.1.26.tar.gz", "mysql-connector");
	local.remove("mysql-connector-java-5.1.26.tar.gz");
	remote.sendFile("mysql-connector/mysql-connector-java-5.1.26/mysql-connector-java-5.1.26-bin.jar", "/tmp", "mysql-connector-java-5.1.26-bin.jar");
	local.remove("mysql-connector");
	remote.call("sudo mv /tmp/mysql-connector-java-5.1.26-bin.jar /usr/local/apache-openmeetings-2.1.1/webapps/openmeetings/WEB-INF/lib/mysql-connector-java-5.1.26-bin.jar");
}

if(remoteIs64){
	local.downloadAsFile("https://dl-web.dropbox.com/s/zgiz3o6i7q9prwh/jre-7u40-linux-x64.rpm", "jre-7u40-linux-x64.rpm");
	remote.sendFile("jre-7u40-linux-x64.rpm", "/tmp", "jre-7u40-linux-x64.rpm");
	local.remove("jre-7u40-linux-x64.rpm");
	remote.call("sudo rpm -ivh /tmp/jre-7u40-linux-x64.rpm");
	remote.call("rm -f /tmp/jre-7u40-linux-x64.rpm");
//	remote.call("sudo /sbin/update-alternatives --set java /usr/java");
} else {
	local.downloadAsFile("https://dl-web.dropbox.com/s/y5im9qrb7i3h2yo/jre-7u40-linux-i586.rpm", "jre-7u40-linux-i586.rpm");
	remote.sendFile("jre-7u40-linux-i586.rpm", "/tmp", "jre-7u40-linux-i586.rpm");
	local.remove("jre-7u40-linux-i586.rpm");
	remote.call("sudo rpm -ivh /tmp/jre-7u40-linux-i586.rpm");
	remote.call("rm -f /tmp/jre-7u40-linux-i586.rpm");	
}
remote.call("sh /usr/local/apache-openmeetings-2.1.1/admin.sh -i -email admin@example.com -group mygroup -tz \"Asia/Tokyo\" -user " + opUser + " --password " + opPassword);
if(fullInstall){
	remote.call("sudo /usr/bin/mysql -uroot -e\"update configuration set conf_value='\/usr\/local\/bin' where conf_key='swftools_path'\" openmeetings");
	remote.call("sudo /usr/bin/mysql -uroot -e\"update configuration set conf_value='\/usr\/bin' where conf_key='imagemagick_path'\" openmeetings");
	remote.call("sudo /usr/bin/mysql -uroot -e\"update configuration set conf_value='\/usr\/bin' where conf_key='sox_path'\" openmeetings");
	remote.call("sudo /usr/bin/mysql -uroot -e\"update configuration set conf_value='\/usr\/bin' where conf_key='ffmpeg_path'\" openmeetings");
	remote.call("sudo /usr/bin/mysql -uroot -e\"update configuration set conf_value='\/opt\/libreoffice' where conf_key='office.path'\" openmeetings");
	remote.call("sudo /usr/bin/mysql -uroot -e\"update configuration set conf_value='\/usr\/lib\/jodconverter-core-3.0-beta-4-dist\/jodconverter-core-3.0-beta-4\/lib' where conf_key='jod.path'\" openmeetings");
}
shell.call("cd /usr/local/apache-openmeetings-2.1.1");
shell.call("nohup sh ./red5.sh > /dev/null 2>&1 &");
shell.close();
remote.close();
console.log("install complete!");
