#
# Cookbook:: zsh
# Recipe:: default
#
# Copyright:: 2017, The Authors, All Rights Reserved.

apt_update

package 'zsh'

package 'curl'

package 'git'

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

# git '/home/vagrant/fonts' do
#   repository 'https://github.com/powerline/fonts.git'
#   action :sync
#   user 'vagrant'
#   group 'vagrant'
# end

# bash 'install fonts' do
#   cwd '/home/vagrant/fonts'
#   environment ({'HOME' => '/home/vagrant', 'USER' => 'vagrant'}) 
#   code <<-EOH 
#     chmod +x install.sh
#     ./install.sh
#     EOH
#   user 'vagrant'
#   group 'vagrant'
#   # password 'vagrant'
# end