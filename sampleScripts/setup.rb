remote = $Remote.create()
remote.connect "virtualhost", 22, "user", "password"
remote.execute "mkdir forSendDirtest"
remote.execute "touch forSendDirtest/jruby.pacifista.txt"
remote.close