package at.flying.service;

import at.flying.domain.Talker;
import at.flying.interfaces.TalkerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by FlyingHe on 2016/9/16.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Service("talkerService")
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class TalkerService {
    @Autowired
    private TalkerMapper talkerMapper;

    public Talker findById(Long id) {
        return this.talkerMapper.findById(id);
    }

    public Talker findByName(String name) {
        return this.talkerMapper.findByName(name);
    }
}
