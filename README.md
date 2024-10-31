# 从LoMu-Bot中分离出的单独功能
如何使用？:    
1. LiteLoaderNTQQ 下载 LLOneBot插件 正向websocket 3001端口(默认情况是3001)
2. 启动jar文件
3. 当日志窗口回显success时或浏览器访问http://localhost/8080 回显success 
4. 此时已经完成所有步骤，机器人将自动监听群消息的Bvid 视频短链接 视频长连接 卡片消息

## 修改配置信息
* 修改websocket端口 shiro.ws.client.url: "ws://127.0.0.1:3001"
* 修改视频大小限制  java -jar --bilibili.limit=5 (只发送5分钟以下的视频)        
* 修改视频删除时间  java -jar --bilibili.delete=0 (不删除 以小时为单位)

本项目由LiteLoaderNTQQ、LLonebot 、shiro 强力驱动
