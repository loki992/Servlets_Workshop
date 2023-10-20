package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.DbUtil;
import pl.coderslab.User;

import java.sql.*;
import java.util.Arrays;

public class UserDao {
    private static final String CREATE_USER_QUERY = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
    private static final String READ_USER_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String READ_USER_BY_USERNAME_QUERY = "SELECT * FROM users WHERE username = ?";
    private static final String READ_USER_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, username = ?, password = ? WHERE id = ?;";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";

    public void update(User user) {
        try (Connection conn = DbUtil.getConnection()) {
            User tempUser = read(user.getId());
            if (tempUser == null) {
                return;
            }
            PreparedStatement statement = conn.prepareStatement(UPDATE_USER_QUERY);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUserName());
            statement.setString(3, user.getPassword());
            statement.setInt(4, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User create(User user) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

            PreparedStatement testStatement = conn.prepareStatement(READ_USER_BY_EMAIL_QUERY);
            testStatement.setString(1, user.getEmail());
            ResultSet testResultSet = testStatement.executeQuery();
            User testUser = new User();
            while (testResultSet.next()) {
                testUser.setUserName(testResultSet.getString(3));
            }


            if (testUser.getUserName() == null) {
                user.setId(statement.executeUpdate());
                return user;
            } else {
                System.out.println("Istnieje juz taki user");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean canLogin(String userName, String password) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(READ_USER_BY_USERNAME_QUERY);
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            String hash = null;

            while (resultSet.next()) {
                if (resultSet.getString(3) != null) {
                    hash = resultSet.getString(4);
                    return BCrypt.checkpw(password, hash);
                } else return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }


        public User read ( int userID){
            try (Connection conn = DbUtil.getConnection()) {
                PreparedStatement statement = conn.prepareStatement(READ_USER_QUERY);
                statement.setInt(1, userID);

                ResultSet resultSet = statement.executeQuery();
                User user = new User();
                while (resultSet.next()) {
                    user.setId(userID);
                    user.setEmail(resultSet.getString(2));
                    user.setUserName(resultSet.getString(3));
                    user.setPassword(resultSet.getString(4));
                }
                if (user.getUserName() == null || user.getUserName().equalsIgnoreCase("")) {
                    System.out.println("Brak użytkownika o takim id");
                    return null;
                }
                return user;

            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void delete ( int userID){

            try (Connection conn = DbUtil.getConnection()) {
                User tempUser = read(userID);
                if (tempUser == null) {
                    return;
                }
                PreparedStatement statement = conn.prepareStatement(DELETE_USER_QUERY);
                statement.setInt(1, userID);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public User[] findall () {
            try (Connection conn = DbUtil.getConnection()) {
                PreparedStatement statement = conn.prepareStatement(FIND_ALL_QUERY);
                ResultSet resultSet = statement.executeQuery();
                User[] allUsers = new User[0];
                while (resultSet.next()) {
                    allUsers = Arrays.copyOf(allUsers, allUsers.length + 1);
                    allUsers[allUsers.length - 1] = new User();
                    allUsers[allUsers.length - 1].setId(resultSet.getInt(1));
                    allUsers[allUsers.length - 1].setEmail(resultSet.getString(2));
                    allUsers[allUsers.length - 1].setUserName(resultSet.getString(3));
                    allUsers[allUsers.length - 1].setPassword(resultSet.getString(4));
                }
                return allUsers;

            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

    }
