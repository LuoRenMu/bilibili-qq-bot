# 从LoMu-Bot中分离出的单独功能

如何使用？:

1. LiteLoaderNTQQ 下载 LLOneBot插件 正向websocket 3001端口(默认情况是3001)
2. 启动jar文件 java -jar onebot-Lomu-1.0.jar
3. 当日志窗口回显success时或浏览器访问http://localhost/8080 回显success
4. 此时已经完成所有步骤，机器人将自动监听群消息的Bvid 视频短链接 视频长连接 卡片消息

## 修改配置信息
json文件不支持注释
````json
{
  "banned_group_bilibili_push": [  // 禁止推送的群
    1234,
    1234
  ],
  "banned_group_bvid_listen": [  // 禁止监听bvid的群
    1234,
    1234
  ],
  "bilibili_video_delete_timing": 3,  //定时删除时间、以小时为单位
  "bilibili_video_length_limit": 5, //禁止监听超过的视频长度、以分钟为单位
  "bot_owner": 0, // 机器人主人QQ
  "group_bvid_listen": true, //bvid视频监听总开关
  "listen_list": [  //监听列表、支持监听多个用户 (暂时只支持监听用户动态)
    {
      "live_broadcast": false,  //暂未支持
      "uid": ""  //监听的b站用户uid
    },
    {  
      "live_broadcast": false,  
      "uid": ""  
    }
  ]
}
````


### 命令(后期将修改为自定义.)

需在setting中设置bot_owner

1. 更新配置文件

本项目由LiteLoaderNTQQ、LLonebot 、shiro 强力驱动
