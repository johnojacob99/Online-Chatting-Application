package Resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JSON_OUTPUT{

    @JsonProperty
    private final int code;

    @JsonProperty
    private final String response;

    public JSON_OUTPUT(int code, String req){
        this.code = code;
        this.response = req;
    }

}