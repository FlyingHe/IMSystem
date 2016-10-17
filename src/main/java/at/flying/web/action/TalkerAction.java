package at.flying.web.action;

import at.flying.domain.Talker;
import at.flying.service.TalkerService;
import at.flying.websocket.TalkWebSoketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FlyingHe on 2016/9/16.
 */
@Controller
@RequestMapping("talker")
public class TalkerAction {

    @Autowired
    private TalkerService talkerService;

    @RequestMapping("findById")
    @ResponseBody
    public Map<String, Object> findById(
            @RequestParam("id")
                    Long id) {
        Talker talker = this.talkerService.findById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("status", 0);
        if (talker != null) {
            map.put("status", 1);
            map.put("talker", talker);
        }
        return map;
    }

    @RequestMapping("findByName")
    @ResponseBody
    public Map<String, Object> findByName(
            @RequestParam("name")
                    String name) {

        Map<String, Object> map = new HashMap<>();
        map.put("status", 0);//0代表登录失败

        //用户名不能是机器人
        if (!"机器人".equalsIgnoreCase(name)) {
            Talker talker = this.talkerService.findByName(name);
            if (talker != null) {
            /*检测当前用户是否已登录*/
                if (TalkWebSoketHandler.getSessions().containsKey(talker.getId())) {
                    map.put("status", -1);//-1代表当前用户已经登录，不能重复登录
                } else {
                    map.put("status", 1);//1代表登录成功
                }

                map.put("talker", talker);
            }
        }
        return map;
    }

    /*查找在线用户*/
    @RequestMapping("findTalkersOfLogin")
    @ResponseBody
    public Map<String, Object> findTalkersOfLogin(
            @RequestParam("id")
                    Long id) {
        Map<Long, WebSocketSession> sessions = TalkWebSoketHandler.getSessions();
        List<Talker> talkers = new ArrayList<>();
        talkers.add(this.talkerService.findById(1L));
        for (Map.Entry<Long, WebSocketSession> entry :
                sessions.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                Talker talker = this.talkerService.findById(entry.getKey());
                if (talker != null) {
                    talkers.add(talker);
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total", talkers.size());
        map.put("talkers", talkers);
        return map;
    }
}
