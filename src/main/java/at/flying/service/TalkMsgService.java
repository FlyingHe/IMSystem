package at.flying.service;

import at.flying.domain.TalkMsg;
import at.flying.interfaces.TalkMsgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by FlyingHe on 2016/9/18.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Service("talkMsgService")
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class TalkMsgService {
    @Autowired
    private TalkMsgMapper talkMsgMapper;

    @Transactional(readOnly = false)
    public void add(TalkMsg talkMsg) {
        this.talkMsgMapper.add(talkMsg);
    }

    public TalkMsg findById(Long id) {
        return this.talkMsgMapper.findById(id);
    }

    public List<TalkMsg> findByFromAndTo(Long from, Long to) {
        return this.talkMsgMapper.findByFromAndTo(from, to);
    }
}

