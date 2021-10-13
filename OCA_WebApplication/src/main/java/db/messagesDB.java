package db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "chat")

public class messagesDB {


    @Column(name = "time")
    @JsonProperty
    public String time;

    @Column(name = "user_name")
    @JsonProperty
    public String user_name;

    @Column(name = "message")
    @JsonProperty
    public String message;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonProperty
    public String id;

    public messagesDB() {
        super();
    }

    public messagesDB(String idC, String timeC, String usernameC, String messageC) {

        this.id = idC;
        this.time = timeC;
        this.user_name = usernameC;
        this.message = messageC;
    }
}
