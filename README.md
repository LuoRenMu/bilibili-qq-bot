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
  "text_forward":300, // 文本超过300将分块转发(较低将出现文本错误)
  "image_forward":3, // 超过该数量的图片将作为转发发送
  "group_bvid_listen": true, //bvid视频监听总开关
  "message_limit_list": true, //使用消息限制队列(3条消息内重复不会发送)
  "leave_group_owner_not_exists": true, // 待支持(主人不在该群时自动退出)
  "listen_list": [  //监听列表、支持监听多个用户 (暂时只支持监听用户动态)
    {
      "live_broadcast": false,  //监听是否正在直播中
      "uid": "",  //监听的b站用户uid
      "group_bilibili_push":[] //该监听只允许推送到的群(banned_group_bilibili_push字段无法对其限制)
    },
    {  
      "live_broadcast": false,  
      "uid": "",
      "group_bilibili_push":[]
    }
  ]
}
````


### 命令及权限

在了解命令之前你需要了解机器人权限
#### 权限
````kotlin
    // 机器人权限 (数字越大权限越大) 
    OWNER("owner", 10)  // bot所有者(配置文件手动添加)
    ADMIN("admin", 5)   // bot管理员(待支持)
    GroupOwner("group_owner", 4) // 群主
    GroupAdmin("group_admin", 3) // 群管理员
    Member("member", 0) // 群成员
````
#### 命令
* command_id -> 机器人的命令id为唯一标识符 确保程序能够正确识别该指令(正常情况你不应该去修改它)    
* return_message -> 表示权限不足时机器人返回的消息
* role   -> 当用户大于等于该权限时执行 owner > admin > group_owner > group_admin > member
* command -> 自定义命令以包含型正则表达式进行匹配

##### command
1. ^(1+1)$  这样的命令表示1+1开头并结尾 (如果没有进行头尾限制^和\$符合那么在任何包含1+1的情况下都会执行命令)     
2. ^(1+1)$   ->  1+1(命令执行)      
3. ^(1+1)$   ->  我猜1+1=2(命令不执行)      
4. 1+1   ->  我猜1+1=2(命令执行)     
!!!错误的设置将对群造成污染任何语句都可能执行命令      
````json
{
	"command_list":[
		{
			"command":"^(更新配置文件)$", 
			"command_id":"refresh_config",
			"return_message":"权限不允许",
			"role":"group_admin"
		},
		{
			"command":"^(获取最新动态)$",
			"command_id":"get_last_article",
			"return_message":"权限不允许",
			"role":"group_admin"
		}
	]
}
````

##### 自定义回复
通过配置文件customize_command.json
````json
{
	"customize_command_list":[
		{
			"command":"^(ping)$", //命令
			"group_list":[],    // 该命令只在该群生效
			"permissions_message":"权限不允许",  //权限不足回复内容 如果字符串为空"permissions_message":"" 表示不回复
			"return_message":" 我在哦~", // 成功执行
			"role":"member",  //权限
			"sender_list":[], // 指定发送者QQ 只有该用户能够触发 
			"probability": 1.0  // 触发概率 0.0 ~ 1.0
		}
	]
}
````
##### 获取发送者信息
* 通过语法${sender.senderName} 调用
````kotlin
    data class CommandSender(
    var groupOrSenderId: Long, // 群号或者私聊消息的发送者QQ
    var senderName: String, // 发送者名称
    var senderId: Long, // 发送者QQ
    var role: BotRole, // 发送者权限
    var messageId: Int, // 消息ID(自动生成的唯一ID)
    var message: String, // 发送者发送的原消息
)
````

自定义发送音频、视频、图片 [OneBotCQ码](https://283375.github.io/onebot_v11_vitepress/message/segment.html#%E7%BA%AF%E6%96%87%E6%9C%AC)

#### 自定义数据请求并发送
````json
{
  "request_list": [
    {
      "id": "get_img",  // 自定义名称
      "request_detailed": { //详细参看request下的bilibili_request.json
        "url": "https://image.anosu.top/pixiv/json?r18=0&keyword=arknights",  //请求url
        "method": "get" // get请求
      },
      "response_process": {
        "condition_process": {
          "condition": "明日方舟", //包含 正则表达式 判断http请求返回的数据中是否包含明日方舟字符 可不设置
          "not_exists": { // 不满足条件
            "process": "RE_REQUEST", // IGNORE 忽略,RE_REQUEST 重试
            "try_count": 5, //尝试次数  仅在RE_REQUEST下生效
            "interval": 1 //每次请求后睡眠时间 以整数秒为单位  仅在RE_REQUEST下生效  
          }
        },
        "return_json_filed": [ // 保存请求返回的字段
          "url", 
          "uid",
          "title",
          "uuid",  // 固有字段 随机生成的uuid
          "this"  // 固有字段 request_detailed下的url
        ],
        "download": { // 发送请求并下载流文件 可不设置
          "download_filed": "url", // 使用return_json_filed中保存的字段
          "download_path": "E:/images/ark/${title}-${uid}.png" //保存至本地  ${title}使用return_json_filed中保存的字段
        }
      }
    }
  ]
}
````

在customize_command中使用customize_request
````json
	{
			"command":"^(test)$",
			"group_list":[],
			"permissions_message":"权限不允许",
			"probability":1.0,
			"return_message":"return ${customize_request.get_img.url}", // 使用customize_request中id为get_img字段为url
			"role":"member",
			"sender_list":[]
		}
````

#### 发送前消息转换(待完善)
暂时不支持自定义变量更改 



本项目由LiteLoaderNTQQ、LLonebot 、shiro 强力驱动
