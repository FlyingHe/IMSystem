package at.flying.interfaces;

import at.flying.domain.TalkMsg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by FlyingHe on 2016/9/18.
 */
public interface TalkMsgMapper {

    public void add(TalkMsg talkMsg);

    public TalkMsg findById(Long id);

    public List<TalkMsg> findByFromAndTo(
            @Param("from")
                    Long from,
            @Param("to")
                    Long to);
}
