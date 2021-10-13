import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ConfigClass extends Configuration {

    @Valid
    @NotNull
    private DataSourceFactory database  = new DataSourceFactory();

    @JsonProperty("accounts") //accounts of both user accounts and chat account messages

    public void setDataSourceFactory(DataSourceFactory db){
        this.database = db;
    }

    @JsonProperty("accounts")
    public DataSourceFactory getDataSourceFactory(){
        return database;
    } //config the database

}
