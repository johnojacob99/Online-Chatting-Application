package Mappers;


import java.sql.ResultSet;
import java.sql.SQLException;

import db.accountDB;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;


    public class AccountMapper implements RowMapper<accountDB>{

        @Override
        public accountDB map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new accountDB(
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("age"),
                    rs.getString("gender"));
        }

    }

