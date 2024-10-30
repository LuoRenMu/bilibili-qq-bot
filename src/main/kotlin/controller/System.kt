package cn.luorenmu.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * @author LoMu
 * Date 2024.07.05 9:27
 */

@RestController
@RequestMapping("")
class System(

) {
    @RequestMapping("/")
    fun system(): String {
        return "running success "
    }

}