#
# Cookbook:: openempi
# Recipe:: default
#
# Copyright:: 2017, The Authors, All Rights Reserved.

# include_recipe 'apt'

# include_recipe 'docker-compose'

apt_update 'Update the apt cache daily' do
  frequency 86_400
  action :periodic
end

package 'curl'

package 'vim'

package 'git'

execute 'install docker-compose' do
  action :run
  command "curl -L https://github.com/docker/compose/releases/download/1.14.0/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose && sudo chmod +x /usr/local/bin/docker-compose"
  not_if "hash docker-compose 2>/dev/null"
end

git '/home/vagrant/openempi' do
  repository 'https://github.com/Yuan-W/docker-openempi.git'
  action :sync
  user 'vagrant'
  group 'vagrant'
end

bash 'download openempi-3.3.0 war' do
  cwd '/home/vagrant/openempi/application/openempi-3.3.0/'
  code <<-EOH 
    if [ ! -f openempi-entity-webapp-web-3.3.0c.war ]; then
      wget -O openempi-entity-webapp-web-3.3.0c.war https://www.dropbox.com/s/8ftbfrl8fku7ccd/openempi-entity-webapp-web-3.3.0c.war
    elif [[ "$(sha256sum openempi-entity-webapp-web-3.3.0c.war | head -c 64)" != 1b3c437039b8cef6c43340be4a29de4b6794f7051b5dada83fe29348deca79ea ]]; 
    then 
      wget -O openempi-entity-webapp-web-3.3.0c.war https://www.dropbox.com/s/8ftbfrl8fku7ccd/openempi-entity-webapp-web-3.3.0c.war 
    fi
  EOH
  user 'vagrant'
  group 'vagrant'
end

# remote_file '/home/vagrant/openempi/application/openempi-3.3.0/openempi-entity-webapp-web-3.3.0c.war' do
#   source 'https://www.dropbox.com/s/8ftbfrl8fku7ccd/openempi-entity-webapp-web-3.3.0c.war'
#   checksum '1b3c437039b8cef6c43340be4a29de4b6794f7051b5dada83fe29348deca79ea'
# end
