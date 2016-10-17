package at.flying.web.action;

import at.flying.domain.TalkMsg;
import at.flying.domain.Talker;
import at.flying.service.TalkMsgService;
import at.flying.service.TalkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FlyingHe on 2016/9/18.
 */
@Controller
@RequestMapping("talkMsg")
public class TalkMsgAction {

    @Autowired
    private TalkMsgService talkMsgService;
    @Autowired
    private TalkerService talkerService;

    @RequestMapping("add")
    @ResponseBody
    public Map<String, Object> add(
            @RequestBody
                    TalkMsg talkMsg) {
        this.talkMsgService.add(talkMsg);
        Map<String, Object> map = new HashMap<>();
        map.put("status", 1);
        return map;
    }

    @RequestMapping("findById")
    @ResponseBody
    public TalkMsg findById(
            @RequestParam("id")
                    Long id) {
        return this.talkMsgService.findById(id);
    }

    @RequestMapping("findByFromAndTo")
    public ModelAndView findByFromAndTo(
            @RequestParam("from")
                    Long from,
            @RequestParam("to")
                    Long to,
            ModelAndView modelAndView) {
        List<TalkMsg> talkMsgs = to == 1 ? new ArrayList<>() : this.talkMsgService.findByFromAndTo(from, to);
        Talker tot = this.talkerService.findById(to);
        Talker fromt = this.talkerService.findById(from);
        modelAndView.addObject("talkMsgs", talkMsgs);
        modelAndView.addObject("to", tot);
        modelAndView.addObject("from", fromt);
        modelAndView.setViewName("talkMsg");
        return modelAndView;
    }
}
