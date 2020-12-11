20201211
1.关闭主界面，后台TcpServer无法关闭；
2.未加线程池
3.message会出现重复
20201207
1. 关闭主界面，后台TcpServer无法关闭；
2. 断开链接再重新接入，数据不能发送，TCP server的socket关闭。由于1的handler中serversocket.accpet存活。
3. 变更为MVN管理,
4. 启动SLF4J的日志
5. 多次点击“设备连接/断开”，程序报错：TCP server Line：165：int len = is.read(bytes);：socket is closed
6. TODO：增加DataFrame的数据处理功能，识别Client上报数值，以便于作图；另注意数据的存储

20201124
除功能外，存在以下问题：
1. 关闭主界面，后台TcpServer无法关闭；
2. 字符串的处理。16进制和字母无法区别，数字亦无法区别；
3. 单击停止接收，后台程序报错：java.lang.InterruptedException；再次开始接收，无法接收数据。
3.1 报错原因：TcpServer在handler线程使用blockigqueue传递数据，主界面（kejiapowerController）使用blockingqueue.take取数据。

# kejiapower
Clinet for KejiaPower based on JavaFx
