#### 系统
shutdown -h now 关闭系统
reboot 重启

#### 文件和目录
cd /home 进入 '/home' 目录'
**cd ~ 进入个人的主目录**
**cd - 返回上次所在的目录**
**pwd 显示工作路径**
ls 查看目录中的文件
ls -l 显示文件和目录的详细资料
ls -a 显示隐藏文件
**lstree 显示文件和目录由根目录开始的树形结构**
**touch file_name 创建名为fiile_name的文件**
mkdir dir1 创建一个叫做 'dir1' 的目录'
mkdir dir1 dir2 同时创建两个目录
**mkdir -p /tmp/dir1/dir2 创建一个目录树**
**rm -f file1 删除一个叫做 'file1' 的文件'**
**rmdir dir1 删除一个叫做 'dir1' 的目录'**
**rm -rf dir1 删除一个叫做 'dir1' 的目录并同时删除其内容**
rm -rf dir1 dir2 同时删除两个目录及它们的内容
mv dir1 new_dir 重命名/移动 一个目录
cp file1 file2 复制一个文件
**cp dir/* . 复制一个目录下的所有文件到当前工作目录**
cp -a /tmp/dir1 . 复制一个目录到当前工作目录
**cp -a dir1 dir2 复制一个目录**
**ln -s file1 lnk1 创建一个指向文件或目录的软链接**
**ln file1 lnk1 创建一个指向文件或目录的硬链接**

#### 磁盘空间
**df -h 显示已经挂载的分区列表**
ls -lSr |more 以尺寸大小排列文件和目录
du -sh dir1 估算目录 'dir1' 已经使用的磁盘空间'
du -sk * | sort -rn 以容量大小为依据依次显示文件和目录的大小

#### 用户和群组
groupadd group_name 创建一个新用户组
groupdel group_name 删除一个用户组
groupmod -n new_group_name old_group_name 重命名一个用户组
useradd -c "Name Surname " -g admin -d /home/user1 -s /bin/bash user1 创建一个属于 "admin" 用户组的用户
**useradd user1 创建一个新用户**
**userdel -r user1 删除一个用户 ( '-r' 排除主目录)**
usermod -c "User FTP" -g system -d /ftp/user1 -s /bin/nologin user1 修改用户属性
**passwd 修改口令**
**passwd user1 修改一个用户的口令 (只允许root执行)**


#### 文件权限
**ls -lh 显示权限**
**chmod 777 dir 设置目录的所有人权限**

#### 打包和压缩文件 
**tar -cvf archive.tar file1 file2 dir1 创建一个包含了 'file1', 'file2' 以及 'dir1'的档案文件**
tar -tf archive.tar 显示一个包中的内容
**tar -xvf archive.tar 释放一个包**
**tar -xvf archive.tar -C /tmp 将压缩包释放到 /tmp目录下**

#### 查看文件内容 
**cat file1 从第一个字节开始正向查看文件的内容**
**tac file1 从最后一行开始反向查看一个文件的内容**
**more file1 分页查看一个长文件的内容**
**less file1 类似于 'more' 命令，但是它允许在文件中和正向操作一样的反向操作**
head -2 file1 查看一个文件的前两行
tail -2 file1 查看一个文件的最后两行

#### APT 软件工具 (Debian, Ubuntu 以及类似系统) 
**apt-get install package_name 安装/更新一个 deb 包**
**apt-get update 升级列表中的软件包**
**apt-get upgrade 升级所有已安装的软件**
apt-get remove package_name 从系统删除一个deb包
apt-get check 确认依赖的软件仓库正确
apt-get clean 从下载的软件包中清理缓存

#### 进程管理
**ps -a | grep mysql 查询mysql相关的进程信息**
**pstree 树形查看进程信息**
**lsof -i:8080 查看占用8080端口进程的信息**
kill id 根据id结束进程
kill -9 id 根据id结束进程 - 无条件终止

#### 网络
**tcpdump 监视第一个网络接口上所有流过的数据包**
tcpdump -i eth0 -c 10 监视指定网络接口的数据包
tcpdump -i eth0 host 10.20.3.25 监视指定主机的数据包
ifconfig 网络配置查看
route 用于显示和操作 IP 路由表
netstat -anpt 用来查看当前操作系统的网络连接状态、路由表、接口统计等信息
ss -l 显示本地打开的所有端口
ss -s 列出当前 socket 详细信息
ss -at 显示所有 tcp socket
nslookup www.baidu.com 域名解析工具
arp -n 显示 ARP 表

#### 防火墙
```shell
# 开启
service firewalld start
# 重启
service firewalld restart
# 关闭
service firewalld stop
# 查看防火墙规则
firewall-cmd --list-all
# 查询端口是否开放
firewall-cmd --query-port=8080/tcp
# 开放80端口
firewall-cmd --permanent --add-port=80/tcp
# 移除端口
firewall-cmd --permanent --remove-port=8080/tcp
#重启防火墙(修改配置后要重启防火墙)
firewall-cmd --reload
# 参数解释
1、firwall-cmd：是Linux提供的操作firewall的一个工具；
2、--permanent：表示设置为持久；
3、--add-port：标识添加的端口；
```