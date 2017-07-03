#
# Cookbook:: fhir
# Recipe:: default
#
# Copyright:: 2017, The Authors, All Rights Reserved.

package 'unzip'

package 'default-jdk'

bash 'install gradle' do
  code <<-EOH
    wget 'https://services.gradle.org/distributions/gradle-4.0-bin.zip'
    mkdir /opt/gradle
    unzip -d /opt/gradle gradle-4.0-bin.zip
    echo "export PATH=$PATH:/opt/gradle/gradle-4.0/bin" >> /home/vagrant/.bashrc
    EOH
  not_if "/opt/gradle/gradle-4.0/bin/gradle --version >/dev/null"
end