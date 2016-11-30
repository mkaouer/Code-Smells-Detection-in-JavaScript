## Downloads

Distro-specific packages are built for each supported operating system.
These packages contain the osquery daemon, shell, example configuration and startup scripts. Note that the `/etc/init.d/osqueryd` script does not automatically start the daemon until a configuration file is created*.

Supported distributions for package installs are:

- Ubuntu Trusty 14.04 LTS, Precise 12.04 LTS
- CentOS 6.6, 7.1

Each osquery tag (release) is published to **yum** and **apt** repositories for our supported operating systems: [https://osquery.io/downloads](http://osquery.io/downloads/).

The default packages create the following structure:

```sh
/etc/osquery/
/usr/share/osquery/osquery.example.conf
/usr/share/osquery/packs/{*}.conf
/var/log/osquery/
/usr/lib/osquery/
/usr/bin/osqueryctl
/usr/bin/osqueryd
/usr/bin/osqueryi
```

### yum-based Distros

We publish two packages, osquery and osquery-latest**, in a yum repository for CentOS/RHEL 6.3-6.6 and 7.0-7.1 built from our Jenkins build hosts. You may install the "auto-repo-add" RPM or add the repository target:

**CentOS/RHEL 7.0**

```sh
$ sudo rpm -ivh https://osquery-packages.s3.amazonaws.com/centos7/noarch/osquery-s3-centos7-repo-1-0.0.noarch.rpm
$ sudo yum install osquery
```

**CentOS/RHEL 6.6**

```sh
$ sudo rpm -ivh https://osquery-packages.s3.amazonaws.com/centos6/noarch/osquery-s3-centos6-repo-1-0.0.noarch.rpm
$ sudo yum install osquery
```

### dpkg-based Distros

We publish the same two packages, osquery and osquery-latest, in an apt repository for Ubuntu 12.04 (precise) and 14.04 (trusty):

**Ubuntu Trusty 14.04 LTS**

```sh
$ sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 1484120AC4E9F8A1A577AEEE97A80C63C9D8B80B
$ sudo add-apt-repository "deb [arch=amd64] https://osquery-packages.s3.amazonaws.com/trusty trusty main"
$ sudo apt-get update
$ sudo apt-get install osquery
```

**Ubuntu Precise 12.04 LTS**

```sh
$ sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 1484120AC4E9F8A1A577AEEE97A80C63C9D8B80B
$ sudo add-apt-repository "deb [arch=amd64] https://osquery-packages.s3.amazonaws.com/precise precise main"
$ sudo apt-get update
$ sudo apt-get install osquery
```


\* You may also set a different config plugin using a [**flagfile**](../installation/cli-flags.md).<br />
\** We do not recommend using the latest/unstable package as it is built
from our master branch and does not guarantee safety.

## Running osquery

To start a standalone osquery use: `osqueryi`. This does not need a server or service. All the table implementations are included!

After exploring the rest of the documentation you should understand the basics of configuration and logging. These and most other concepts apply to the **osqueryd**, the daemon, tool. To start the daemon:

```
sudo cp /usr/share/osquery/osquery.example.conf /etc/osquery/osquery.conf
sudo service osqueryd start
sudo service osqueryd status
```

Note: The interactive shell and daemon do NOT communicate!
