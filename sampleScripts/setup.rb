remote = $Remote.create
remote.connect "virtualhost", 22, "user", "password"
remote.execute "mkdir forSendDirtest"
remote.execute "touch forSendDirtest/jruby.pacifista.txt"
templateparameter = "replaced value"
username = $runtime.get_env "USER"
template = $Template.create "testTemplate.vm"
template.put "parameter", templateparameter
template.put "username", username
template.put "lang", "Ruby";
config = template.toValue;
remote.send config, "forSendDirtest", "rubyConfig";
remote.close
$runtime.call("sampleScripts/otherScript.js");
puts "ruby complete"