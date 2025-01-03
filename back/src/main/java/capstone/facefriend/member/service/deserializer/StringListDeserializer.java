package capstone.facefriend.member.service.deserializer;

import static capstone.facefriend.member.exception.analysis.AnalysisInfoExceptionType.FAIL_TO_DESERIALIZE_ANALYSIS;
import static com.fasterxml.jackson.core.JsonToken.START_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.VALUE_STRING;

import capstone.facefriend.member.exception.analysis.AnalysisInfoException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class StringListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken jt = p.getCurrentToken();
        if (jt == START_ARRAY) {
            return p.readValueAs(List.class);
        } else if (jt == VALUE_STRING) {
            return Arrays.asList(p.getValueAsString());
        }
        throw new AnalysisInfoException(FAIL_TO_DESERIALIZE_ANALYSIS);
    }
}
