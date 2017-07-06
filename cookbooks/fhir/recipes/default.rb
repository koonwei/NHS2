#
# Cookbook:: fhir
# Recipe:: default
#
# Copyright:: 2017, The Authors, All Rights Reserved.

apt_update

apt_update 'Update the apt cache daily' do
  frequency 86_400
  action :periodic
end

package 'unzip'

package 'curl'

package 'git'

package 'vim'

package 'openjdk-8-jdk'

package 'zsh'

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

bash 'set chsh for no password' do
  cwd '/home/vagrant'
  code <<-EOH 
    sed -i '1iauth       sufficient   pam_wheel.so trust group=chsh' /etc/pam.d/chsh 
    groupadd chsh
    usermod -a -G chsh vagrant
    EOH
  not_if { ::File.exist?('/home/vagrant/.oh-my-zsh') }
end

bash 'install oh-my-zsh' do
  cwd '/home/vagrant'
  environment ({'HOME' => '/home/vagrant', 'USER' => 'vagrant'}) 
  code <<-EOH 
    sh -c "$(wget https://raw.githubusercontent.com/robbyrussell/oh-my-zsh/master/tools/install.sh -O -)"
    sed -i 's/"robbyrussell"/"avit"/g' /home/vagrant/.zshrc
    EOH
  user 'vagrant'
  group 'vagrant'
  not_if { ::File.exist?('/home/vagrant/.oh-my-zsh') }
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

bash 'install gradle' do
  code <<-EOH
    wget 'https://services.gradle.org/distributions/gradle-4.0-bin.zip'
    mkdir /opt/gradle
    unzip -d /opt/gradle gradle-4.0-bin.zip
    echo "export PATH=$PATH:/opt/gradle/gradle-4.0/bin" >> /home/vagrant/.bashrc
    echo "export PATH=$PATH:/opt/gradle/gradle-4.0/bin" >> /home/vagrant/.zshrc
    EOH
  not_if "/opt/gradle/gradle-4.0/bin/gradle --version >/dev/null"
end

package 'php'

bash 'install composer' do
  cwd '/home/vagrant'
  code <<-EOH
    php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');"
    php -r "if (hash_file('SHA384', 'composer-setup.php') === '669656bab3166a7aff8a7506b8cb2d1c292f042046c5a994c43155c0be6190fa0355160742ab2e1c88d40d5be660b410') { echo 'Installer verified'; } else { echo 'Installer corrupt'; unlink('composer-setup.php'); } echo PHP_EOL;"
    php composer-setup.php
    php -r "unlink('composer-setup.php');"
    mv composer.phar /usr/local/bin/composer
    EOH
  not_if "hash composer 2>/dev/null"
end