package cz.genesis.dbUsers.service;

import cz.genesis.dbUsers.model.User;
import cz.genesis.dbUsers.model.UserMin;
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
    public final List<String> listOfPersonIDs = new ArrayList<>();
    public boolean isPersonIDinList;
    public boolean isSamePersonID;
    public boolean isEmptyName;
    public boolean isIDinDatabase;
    public int countUsedPersonID;
    public int countID;

    @Autowired
    JdbcTemplate jdbcTemplate;

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
            }
        }
    }

    public void notEmptyName(User user) {
        isEmptyName = false;
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            isEmptyName = true;
        }
    }

    public void uniquenessOfPersonID(User user) throws SQLException {
        isSamePersonID = false;
        String UserPersonID = user.getPersonID();
        String sql = "select count(*) from users where personID = ?";
        countUsedPersonID = jdbcTemplate.queryForObject(sql, new Object[]{UserPersonID}, Integer.class);
        if (countUsedPersonID > 0) {
            isSamePersonID = true;
        }
    }

    public void checkingID(int id) throws SQLException {
        isIDinDatabase = false;
        String sql = "select count(*) from users where id =?";
        countID = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);
        if (countID > 0) {
            isIDinDatabase = true;
        }
    }

    public void createNewUser(User user) throws SQLException {
        notEmptyName(user);
        existenceOfPersonID(user);
        uniquenessOfPersonID(user);
        final String uuid = UUID.randomUUID().toString();
        user.setUuid(uuid);
        if (((!isEmptyName) && (isPersonIDinList)) && (!isSamePersonID)) {
            String sqlNewData = "insert into users (name, surname, personID, uuid) values (?, ?, ?, ?)";
            jdbcTemplate.update(sqlNewData, user.getName(), user.getSurname(),
                                user.getPersonID(), user.getUuid());
        }
    }

    public UserMin getUserMin(int id) throws SQLException {
        if (isIDinDatabase) {
            String sqlUserMin = "select id, name, surname from users where id =" + id;
            UserMin userMin = jdbcTemplate.queryForObject(sqlUserMin, new RowMapper<UserMin>() {
                @Override
                public UserMin mapRow(ResultSet result, int rowNum) throws SQLException {
                    UserMin userMin = new UserMin();
                    userMin.setId(result.getInt("id"));
                    userMin.setName(result.getString("name"));
                    userMin.setSurname(result.getString("surname"));
                    return userMin;
                }
            });
            return userMin;
        }
        return null;
    }

    public User getUser(int id) throws SQLException {
        if (isIDinDatabase) {
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
        return null;
    }

    public List<UserMin> getAllUsersMin() throws SQLException {
        String sqlUsersMin = "select id, name, surname from users";
        List<UserMin> outUsersMin = jdbcTemplate.query(sqlUsersMin, new RowMapper<UserMin>() {
            @Override
            public UserMin mapRow(ResultSet result, int rowNum) throws SQLException {
                UserMin userMin = new UserMin();
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

    public User getUserDetail(int id, String detail) throws SQLException {
        if (Objects.equals(detail, "true")) {
            return getUser(id);
        }
        return null;
    }

    public List<User> getAllUsersDetail(String detail) throws SQLException {
        if (Objects.equals(detail, "true")) {
            return getAllUsers();
        }
        return null;
    }

    public void updatingUser(User user) throws SQLException {
        notEmptyName(user);
        checkingID(user.getId());
        if ((!isEmptyName) && (isIDinDatabase)) {
            String sqlData = "update users set name = ?, surname = ? where id =" + user.getId();
            jdbcTemplate.update(sqlData, user.getName(), user.getSurname());
        }
    }

    public void deleteUser(int id) throws SQLException {
        checkingID(id);
        if (isIDinDatabase) {
            String sql = "delete from users where id =?";
            jdbcTemplate.update(sql, id);
        }
    }

}//konec tridy
