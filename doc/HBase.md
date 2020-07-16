## 简要介绍
HBase是一个分布式的、面向列的非关系型的开源数据库。

> Tips1：HBase不配置环境变量理论上不影响。
>
> Tips2：版本、目录等不一致，后续的路径、文件名等可能不一致，请注意区分和替换更改成自己的。
>
> Tips3：不同于mysql之类的数据库，这里的表，存在表名、行名、列族名、列名、 值的概念。

### 版本说明
HBase：hbase-2.2.5

### 简单入门

1. 下载HBase包，解压缩。示例路径为`/Users/zhangyu/hbase-2.2.5`。
2. 进入目录
```shell script
cd hbase-2.2.5
```
3. 启动HBase，默认是在目录下新建了tmp目录
```shell script
./bin/start-hbase.sh
```
4. 连接HBase进入编辑模式
```shell script
./bin/hbase shell
```
5. 创建表和列族
```shell script
create 'test', 'cf'
```
6. 新增数据
```shell script
put 'test', 'row1', 'cf:a', 'value1'
put 'test', 'row2', 'cf:b', 'value2'
put 'test', 'row3', 'cf:c', 'value3'
```
7. 查看表内的全部数据
```shell script
scan 'test'
```
8. 获取表内某行数据
```shell script
get 'test', 'row1'
```
9. 删除表或者修改表配置，需要先禁用表，处理完后再启用。
```shell script
disable 'test'
drop 'test'
enable 'test'
```
10. 访问`http://localhost:16010`，可以查看HBase的webUI界面。