package at.flying.interfaces;

import at.flying.domain.Talker;

/**
 * Created by FlyingHe on 2016/9/16.
 */
public interface TalkerMapper {
    Talker findById(Long id);

    Talker findByName(String name);
}
