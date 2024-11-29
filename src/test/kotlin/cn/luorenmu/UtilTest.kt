package cn.luorenmu

import cn.luorenmu.common.extensions.getValueByPath
import com.alibaba.fastjson2.parseObject

/**
 * @author LoMu
 * Date 2024.11.16 14:21
 */


fun main() {
    val jsonString = """
    {
        "errno": 0,
        "data": {
            "sugs": [
                {
                    "display": [
                        {
                            "show": "\u6da6\u8272\u8bd1\u6587",
                            "input": "",
                            "showTop": "\u6da6\u8272\u8bd1\u6587",
                            "iconTop": "https:\/\/fanyiapp.cdn.bcebos.com\/cms\/image\/0e6d2499028dfcbdee27cd6cbdc60a0a.png"
                        }
                    ],
                    "obj": "DST_ALL",
                    "param": "[{\"VkZgkXxl8e\":\"\\u8ba9\\u6211\\u4eec\\u8bf4\\u4e2d\\u6587!\\n\"},{\"VkZgkXxl8e\":\"Let's speak Chinese!\\n\"}]",
                    "task": "dpolish"
                },
                // ... 其他对象
            ]
        },
        "logid": 1899247300
    }
    """.trimIndent().parseObject()

    val result = jsonString.getValueByPath("data.sugs[0].obj")
    println("Result: $result") // 输出结果应为 "润色译文"

}