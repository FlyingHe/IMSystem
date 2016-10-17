package at.flying.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FlyingHe on 2016/8/12.
 */
public class CustomDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value));
    }
}
