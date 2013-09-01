var remote = Remote.create();
remote.connect("virtualhost", 22, "user", "password");
var version = remote.loadVersion();
version++;
remote.sendVersion(version);
console.log("version updated to "+version);