var remote = Remote.create();
remote.connectWithAuthFile("localhost", 28279, "vagrant", "/tmp/.ssh/pacifista");
runtime.setEnv("remote", remote);
runtime.setEnv("host", "vagranthost");
runtime.setEnv("zabbixServer", "zabbixServer");
runtime.call("./assets/install_zabbix-agent_to_linux.js");