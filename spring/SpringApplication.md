#1.Spring程序的生命周期
##1.1 程序能一直保持运行的原因
###1.1.1 监听端口
最常见的就是Tomcat启动后监听8080端口，也可以是直接通过Socket进行相应端口监听
###1.1.2 线程持续运行
+ 单一线程for循环
+ 线程池启动后未关闭（线程池本身有一个线程进行维护）

一般的Spring程序启动后，代码执行完毕，程序就停止了
