package me.jsj.chapter3.user.dao.v4;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.jsj.chapter1.user.dao.v3.ConnectionMaker;
import me.jsj.chapter1.user.domain.User;
import me.jsj.chapter3.user.dao.v3.strategy.AddStatement;
import me.jsj.chapter3.user.dao.v3.strategy.DeleteAllStrategy;
import me.jsj.chapter3.user.dao.v3.strategy.StatementStrategy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class UserDaoV5 {
    private DataSource dataSource;
    private JdbcContext jdbcContext;

    public UserDaoV5(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcContext = new JdbcContext(dataSource);
    }

    public void add(final User user) throws SQLException {
        jdbcContext.workWithStatementStrategy(conn -> {
            PreparedStatement ps = conn.prepareStatement("insert into users(id, name, password) values (?, ?, ?)");
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());

            return ps;
        });
    }

    public void deleteAll() throws SQLException {
        jdbcContext.workWithStatementStrategy(conn -> conn.prepareStatement("delete from users"));
    }

    public void deleteAllV2() throws SQLException {
        executeSql("delete from users");
    }

    public void deleteAllV3() throws SQLException {
        jdbcContext.executeSql("delete from users");
    }

    private void executeSql(final String query) throws SQLException {
        jdbcContext.workWithStatementStrategy(conn -> conn.prepareStatement(query));
    }

    public int getCount() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();

            ps = conn.prepareStatement("select count(*) from users");

            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) {} }
            if (ps != null) { try { ps.close(); } catch (SQLException e) {} }
            if (conn != null) { try { conn.close(); } catch (SQLException e) {} }
        }
    }

}
