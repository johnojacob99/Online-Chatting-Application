package dao;

import java.util.List;

import db.accountDB;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;


public interface accountDAO { //sql statements for the different methods in the account resource


    @SqlQuery("select * from account")
    public List<accountDB> get();

    @SqlQuery("select * from account where (username = :username)")
    public accountDB get(@Bind("username") String un);

    @SqlUpdate("delete from account where (username = :username)")
    public void delete(@Bind("username") String un);

    @SqlUpdate("insert into account(id,username, password, age, gender) values (:id, :username, :password, :age, :gender)")
    public boolean put(@Bind("id") int id,
                         @Bind("username") String username,
                         @Bind("password") String password,
                         @Bind("age") String age,
                         @Bind("gender") String gender);

    @SqlUpdate("update account set username=:username,password=:password where id=:id")
    public void post(@Bind("id") int id,
                       @Bind("username") String username,
                       @Bind("password") String password);

    @SqlQuery("select count(*) from account")
    public int getCount();


}
