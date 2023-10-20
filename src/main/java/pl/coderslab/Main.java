package pl.coderslab;

import pl.coderslab.entity.UserDao;

public class Main {
    public static void main(String[] args) {
        UserDao dao = new UserDao();
        User user1 = new User("asasddsd", "el", "scvzxcd");
        User user2 = new User(18, "UPDATEDTESTUSER", "createdTestemail", "superduperstrongpassword");


    }
}