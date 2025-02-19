package cz.genesis.dbusers.service;

import cz.genesis.dbusers.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class UserService {

    private static final String FILE_LIST_PERSON_ID = "src/main/resources/static/dataPersonId.txt";
    private final List<String> listOfPersonIDs = new ArrayList<>();
    private boolean isPersonIDinList;
    private boolean isSamePersonID;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public boolean getIsPersonIDinList() { return isPersonIDinList; }
    public boolean getIsSamePersonID() { return isSamePersonID; }

    public void createListOfPersonIDs() {
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(FILE_LIST_PERSON_ID)))) {
            while (scanner.hasNextLine()) {
                listOfPersonIDs.add(scanner.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void existenceOfPersonID(User user) {
        createListOfPersonIDs();
        isPersonIDinList = false;
        for (String item : listOfPersonIDs) {
            if (item.equals(user.getPersonID())) {
                isPersonIDinList = true;
                break;
            }
        }
    }

    public void uniquenessOfPersonID(User user) throws SQLException {
        isSamePersonID = false;
        String UserPersonID = user.getPersonID();
        String sql = "select count(*) from users where personID = ?";
        int countUsedPersonID = jdbcTemplate.queryForObject(sql, new Object[]{UserPersonID}, Integer.class);
        if (countUsedPersonID > 0) {
            isSamePersonID = true;
        }
    }

    public boolean notEmptyName(User user) {
        return user.getName() != null && !user.getName().trim().isEmpty();
    }

    public boolean checkingId(int id) throws SQLException {
        String sql = "select count(*) from users where id =?";
        int countId = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);
        return countId > 0;
    }

    public void createNewUser(User user) throws SQLException {
        existenceOfPersonID(user);
        uniquenessOfPersonID(user);
        final String uuid = UUID.randomUUID().toString();
        user.setUuid(uuid);
        if (notEmptyName(user) && isPersonIDinList && !isSamePersonID) {
            String sqlNewData = "insert into users (name, surname, personID, uuid) values (?, ?, ?, ?)";
            jdbcTemplate.update(sqlNewData, user.getName(), user.getSurname(),
                    user.getPersonID(), user.getUuid());
        }
    }

    public User getUserMin(int id) throws SQLException {
        String sqlUserMin = "select id, name, surname from users where id =" + id;
            User userMin = jdbcTemplate.queryForObject(sqlUserMin, new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet result, int rowNum) throws SQLException {
                    User userMin = new User();
                    userMin.setId(result.getInt("id"));
                    userMin.setName(result.getString("name"));
                    userMin.setSurname(result.getString("surname"));
                    return userMin;
                }
            });
        return userMin;
    }

    public User getUser(int id) throws SQLException {
        String sqlUser = "select * from users where id =" + id;
            User user = jdbcTemplate.queryForObject(sqlUser, new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet result, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(result.getInt("id"));
                    user.setName(result.getString("name"));
                    user.setSurname(result.getString("surname"));
                    user.setPersonID(result.getString("personID"));
                    user.setUuid(result.getString("uuid"));
                    return user;
                }
            });
        return user;
    }

    public List<User> getAllUsersMin() throws SQLException {
        String sqlUsersMin = "select id, name, surname from users";
        List<User> outUsersMin = jdbcTemplate.query(sqlUsersMin, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet result, int rowNum) throws SQLException {
                User userMin = new User();
                userMin.setId(result.getInt("id"));
                userMin.setName(result.getString("name"));
                userMin.setSurname(result.getString("surname"));
                return userMin;
            }
        });
        return outUsersMin;
    }

    public List<User> getAllUsers() throws SQLException {
        String sqlUsers = "select * from users";
        List<User> outUsers = jdbcTemplate.query(sqlUsers, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet result, int rowNum) throws SQLException {
                User user = new User();
                user.setId(result.getInt("id"));
                user.setName(result.getString("name"));
                user.setSurname(result.getString("surname"));
                user.setPersonID(result.getString("personID"));
                user.setUuid(result.getString("uuid"));
                return user;
            }
        });
        return outUsers;
    }

    public User getUserInfo(int id, boolean detail) throws SQLException {
        if (detail) {
            return getUser(id);
        }
        else return getUserMin(id);
    }

    public List<User> getAllUsersInfo(boolean detail) throws SQLException {
        if (detail) {
            return getAllUsers();
        }
        else return getAllUsersMin();
    }

    public void updatingUser(User user) throws SQLException {
        String sqlData = "update users set name = ?, surname = ? where id =" + user.getId();
        jdbcTemplate.update(sqlData, user.getName(), user.getSurname());
    }

    public void deleteUser(int id) throws SQLException {
        String sql = "delete from users where id =?";
        jdbcTemplate.update(sql, id);
    }

}//konec tridy