package uz.codebyz.message.config.gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.ZonedDateTime;

public class ZonedDateTimeTypeAdapter extends TypeAdapter<ZonedDateTime> {

    @Override
    public void write(JsonWriter out, ZonedDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString()); // ISO-8601
        }
    }

    @Override
    public ZonedDateTime read(JsonReader in) throws IOException {
        if (in.peek() == null) {
            in.nextNull();
            return null;
        }
        return ZonedDateTime.parse(in.nextString());
    }
}
