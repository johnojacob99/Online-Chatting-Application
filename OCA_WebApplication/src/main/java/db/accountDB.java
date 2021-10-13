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
@Table(name = "account")

public class accountDB {


    @Column(name = "username")
    @JsonProperty
    public String username;

    @Column(name = "password")
    @JsonProperty
    public String password;

    @Column(name = "age")
    @JsonProperty
    public String age;

    @Column(name = "gender")
    @JsonProperty
    public String gender;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonProperty
    public String id;

   public accountDB() {
        super();
    }

    public accountDB(String idC, String usernameC, String passwordC, String ageC, String genderC) {

        this.id = idC;
        this.username = usernameC;
        this.password = passwordC;
        this.age = ageC;
        this.gender = genderC;

    }





}
