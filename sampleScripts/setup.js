var remote = Remote.create();
remote.connect("virtualhost", 22, "user", "password");
//or remote.connectWithAuthFile("localhost", 28279, "vagrant", "/home/user/.ssh/pacifista");
remote.execute("mkdir forSendDirtest");
remote.execute("touch forSendDirtest/javaScript.pacifista.txt");
var templateparameter = "replaced value";
var username = runtime.getEnv("USER");
var template = Template.createWithFile("testTemplate.vm");
template.put("parameter", templateparameter);
template.put("username", username);
template.put("lang", "javaScript");
var config = template.toValue();
remote.send(config, "forSendDirtest", "jsConfig");
var tester = Tester.create(remote);
tester.assertFile("/etc/hosts", "rw-r--r--", "root");
tester.assertPortOpen(22);
tester.assertCommand("hostname", "localhost.localdomain");
tester.assertFileHasLine("forSendDirtest/jsConfig", "by lang[javaScript]");
tester.assertFileIsFile("forSendDirtest/groovyConfig");
tester.assertFileIsDirectory("forSendDirtest");
tester.assertUserExists("user");
local.mkdir("data/test/localCreatedDir_javaScript");
local.save("data/test/localCreatedDir_javaScript/saved.txt", "this is\nPacifista!!");
local.copy("data/test/localCreatedDir_javaScript/saved.txt", "data/test/localCreatedDir_javaScript/copyed.txt");
tester.assertEquals("this is\nPacifista!!", local.load("data/test/localCreatedDir_javaScript/saved.txt"));
local.remove("data/test/gitDir");
git.clone("https://github.com/ukiuni/pacifista.git", "data/test/gitDir");
local.remove("data/test");
remote.close();
runtime.call("sampleScripts/otherScript.js");
console.log(plugin.sayHello("javaScript"));
console.log("javaScript complete");