remote = $Remote.create
remote.connect "virtualhost", 22, "user", "password"
# or remote.connectWithAuthFile "localhost", 28279, "vagrant", "/home/user/.ssh/pacifista" ;
remote.execute "mkdir forSendDirtest"
remote.execute "touch forSendDirtest/jruby.pacifista.txt"
templateparameter = "replaced value"
username = $runtime.get_env "USER"
template = $Template.create "testTemplate.vm"
template.put "parameter", templateparameter
template.put "username", username
template.put "lang", "Ruby";
config = template.toValue;
remote.send config, "forSendDirtest", "rubyConfig"
tester = $Tester.create remote
tester.assertFile "/etc/hosts", "rw-r--r--", "root"
tester.assertPortOpen 22
tester.assertCommand "hostname", "localhost.localdomain"
tester.assertFileHasLine "forSendDirtest/rubyConfig", "by lang[Ruby]"
tester.assertFileIsFile "forSendDirtest/groovyConfig"
tester.assertFileIsDirectory "forSendDirtest"
tester.assertUserExists "user"
$local.mkdir "data/test/localCreatedDir_ruby"
$local.save "data/test/localCreatedDir_ruby/saved.txt", "this is\nPacifista!!"
$local.copy "data/test/localCreatedDir_ruby/saved.txt", "data/test/localCreatedDir_ruby/copyed.txt"
tester.assertEquals "this is\nPacifista!!", ($local.load "data/test/localCreatedDir_ruby/saved.txt")
$local.remove "data/test/gitDir"
$git.clone "https://github.com/ukiuni/pacifista.git", "data/test/gitDir"
$local.remove "data/test"
remote.close
$runtime.call "sampleScripts/otherScript.js"
puts "ruby complete"