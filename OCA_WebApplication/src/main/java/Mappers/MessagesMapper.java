package Mappers;


import java.sql.ResultSet;
import java.sql.SQLException;

import db.accountDB;
import db.messagesDB;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;


public class MessagesMapper implements RowMapper<messagesDB>{

    @Override
    public messagesDB map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new messagesDB(
                rs.getString("id"),
                rs.getString("time"),
                rs.getString("user_name"),
                rs.getString("message"));

    }

}