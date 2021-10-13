package dao;

import java.util.List;

import db.accountDB;
import db.messagesDB;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;


public interface messagesDAO { //sql statements for the different chat methods in messages resource

    @SqlQuery("select * from chat")
    public List<messagesDB> get();

    @SqlUpdate("delete from chat where (time = :time)")
    public boolean delete(@Bind("time") String time);

    @SqlUpdate("delete from chat")
    public boolean deleteAll();

    @SqlUpdate("insert into chat(id,time, user_name, message) values (:id, :time, :user_name, :message)")
    public boolean put(@Bind("id") int id,
                       @Bind("time") String time,
                       @Bind("user_name") String user_name,
                       @Bind("message") String message);

    @SqlQuery("select count(*) from chat")
    public int getCount();

    @SqlUpdate("update chat set time=:time,user_name=:user_name,message=:message where id=:id")
    public void post(@Bind("id") String id,
                     @Bind("time") String time,
                     @Bind("user_name") String user_name,
                     @Bind("message") String message);

    @SqlQuery("select * from chat where (time = :time)")
    public messagesDB getIDfromTime(@Bind("time") String time);

    @SqlUpdate("update chat set time=:time where time=:time")
    public boolean check(@Bind("time") String time);



}
