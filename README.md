2020.1.19
1.心跳包检测及显示通信失败
2.自动测试
3.是否有同时报故障的情况，如有则需要对Status进行分解，程序需要较大改动
20210118
1.配置界面检测到电压电流为负值后，无法自动重设为0.0；检测到数据非法后，无法捕捉异常进行处理，暂时用try屏蔽；
2.配置界面数据如何传回主界面？
3.如何遍历传回数据进行数据发送？开始检测按钮
4.发送数据和收到数据的存储？
5.Caused by: java.lang.NullPointerException
  	at com.longriver.kejiapower.controllers.KejiaPowerController.startBtnOnClick(KejiaPowerController.java:1037)
20210104
1.采用线程sleep方法处理tab生成及折线图更新的问题（高并发有雷）
20210101
1.解决主界面关闭后，后台线程继续运行的问题。
2.要实现冲突IP设备的识别。
20201229
1.连接设备按钮，断开连接以后不能再次连接
20201225
1.新增dev分支，在dev里进行Property优化
20201222
1.存在坐标轴不对应问题
2.存在关闭UI后，线程不停止
3.时间轴设置优化
20201212
1.完成message优化
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
