;
import org.apache.commons.dbcp.BasicDataSource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database {
    private BasicDataSource dataSource;

    //konstruktorius
    public Database(String databaseName) {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        dataSource.setUsername("root");
//        dataSource.setPassword("");

        dataSource.setUsername("b29641f9babf25");
        dataSource.setPassword("651ac2d0");

//        dataSource.setUrl("jdbc:mysql://localhost:3306/" + databaseName + "?allowMultiQueries=true"); //"?useUnicode=yes&characterEncoding=UTF-8&amp;allowMultiQueries=true");
//        dataSource.setUrl("jdbc:mysql://eu-cdbr-west-03.cleardb.net/" + databaseName + "?reconnect=true"+"?allowMultiQueries=true"); //"?useUnicode=yes&characterEncoding=UTF-8&amp;allowMultiQueries=true");
        dataSource.setUrl("jdbc:mysql://eu-cdbr-west-03.cleardb.net/heroku_17ddbc5da53f828?"+"allowMultiQueries=true");
        dataSource.setValidationQuery("SELECT 1");
    }

    public String printAll() {
        String query = "SELECT * FROM users";
        String result = "";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String familyName = resultSet.getString("family_name");
                int credits = resultSet.getInt("credits");
                result += ("ID: " + id + ", Vardas: " + firstName
                        + ", Pavarde: " + familyName + ", kreditu skaicius: " + credits);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public User login(String email, String password) {
        String query = "SELECT * FROM users WHERE email=? AND password=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("id"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setFamilyName(resultSet.getString("family_name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setPhone(resultSet.getString("phone"));
                user.setRights(resultSet.getInt("rights"));
                user.setCredits(resultSet.getInt("credits"));
                return user;

            } else {
                System.out.println("wrong login data: " + email + " " + password);
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //return user data
    public User getUserData(int userId) {
        String query = "SELECT * FROM users WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("id"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setFamilyName(resultSet.getString("family_name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setPhone(resultSet.getString("phone"));
                user.setRights(resultSet.getInt("rights"));
                user.setCredits(resultSet.getInt("credits"));
                user.setToken(resultSet.getString("token"));
                return user;

            } else {
                System.out.println("no user");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserData(String email) {
        String query = "SELECT * FROM users WHERE email=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("id"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setFamilyName(resultSet.getString("family_name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setPhone(resultSet.getString("phone"));
                user.setRights(resultSet.getInt("rights"));
                user.setCredits(resultSet.getInt("credits"));
                user.setToken(resultSet.getString("token"));
                return user;

            } else {
                System.out.println("no user");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void makeReservation(int workoutId, int userId) {
        String query =
                "INSERT INTO reservations(date, user_id, workout_id) VALUES(?,?,?); " +
                        "UPDATE workout SET free_places=free_places-1 WHERE id=?; " +
                        "UPDATE users SET credits=credits-1 WHERE id=?; ";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            long date = System.currentTimeMillis();
            statement.setLong(1, date);
            statement.setInt(2, userId);
            statement.setInt(3, workoutId);
            statement.setInt(4, workoutId);
            statement.setInt(5, userId);
            statement.executeUpdate();
            System.out.println("reservation executed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelReservation(int workoutId, int userId, long workoutDate) {

        Connection connection = null;
        PreparedStatement statement1 = null; //selects reservations maching cirteria
        PreparedStatement statement2 = null; //updates reservations
        PreparedStatement statement3 = null; //updates workouts free places
        PreparedStatement statement4 = null; //updates users credits
        ResultSet resultSet1 = null;
        try {
            connection = dataSource.getConnection();
            String query1 = " SELECT id FROM reservations WHERE reservations.user_id=? " +
                    "AND reservations.workout_id=? " +
                    "AND reservations.reservation_deleted=0;";

            statement1 = connection.prepareStatement(query1);
            long date = System.currentTimeMillis();
            statement1.setInt(1, userId);
            statement1.setInt(2, workoutId);
            resultSet1 = statement1.executeQuery();
            System.out.println("statement1 executed");

            int reservationId = 0;
            while (resultSet1.next()) {
                reservationId = resultSet1.getInt("id");
            }
            String query2 = "UPDATE reservations SET reservation_deleted=1 WHERE id=?;";
            statement2 = connection.prepareStatement(query2);
            statement2.setInt(1, reservationId);
            statement2.executeUpdate();
            System.out.println("statement2 executed");

            String query3 = "UPDATE workout SET free_places=free_places+1 WHERE id=?;";
            statement3 = connection.prepareStatement(query3);
            statement3.setInt(1, workoutId);
            statement3.executeUpdate();
            System.out.println("statement3 executed");


            if (workoutDate - System.currentTimeMillis() > Workout.TIME_TO_CANCEL) {
                String query4 = "UPDATE users SET credits=credits+1  WHERE id=?;";
                statement4 = connection.prepareStatement(query4);
                statement4.setInt(1, userId);
                statement4.executeUpdate();
                System.out.println("statement4 executed");
                operationLog(userId, System.currentTimeMillis(), 1, userId, "reservation canceled for workout " + workoutId);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void addWorkout(Workout workout) {
        String query = "INSERT INTO workout (date_time, max_group_size, description, free_places) VALUES(?,?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, workout.getDateTime());
            statement.setInt(2, workout.getMaxGroupSize());
            statement.setString(3, workout.getDescription());
            statement.setInt(4, workout.getFreePlaces());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Workout> getWorkouts(String startDateTime) {
        String query = "SELECT * FROM workout WHERE date_time > ? ORDER BY date_time; ";
        System.out.println("db.getWorkouts method initialized ");
        ArrayList<Workout> workouts = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            long minDate = Long.parseLong(startDateTime);


            statement.setLong(1, minDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Workout workout = new Workout();
                workout.setWorkoutId(resultSet.getInt("id"));
                workout.setDateTime(resultSet.getLong("date_time"));
                workout.setDuration(resultSet.getLong("duration"));
                workout.setMaxGroupSize(resultSet.getInt("max_group_size"));
                workout.setDescription(resultSet.getString("description"));
                workout.setFreePlaces(resultSet.getInt("free_places"));
                workouts.add(workout);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workouts;
    }

    public ArrayList<Workout> getWorkouts(String startDateTime, int userId) {
        String query = "SELECT workout.*, id IN(SELECT workout.id FROM workout JOIN reservations on workout.id=reservations.workout_id" +
                "                WHERE reservations.user_id=? " +
                "                AND reservations.reservation_deleted=0 " +
                "                AND workout.date_time>=?) AS reserved," +
                "                id IN (SELECT workout.id FROM workout " +
                "                JOIN waitlist ON waitlist.workout_id=workout.id " +
                "                WHERE waitlist.user_id =? " +
                "                AND waitlist.number_in_line>0 " +
                "                AND workout.date_time>=?) AS inLine" +
                "                FROM workout WHERE workout.date_time>?" +
                "                ORDER BY date_time;";


        System.out.println("db.getWorkouts with users data method initialized ");
        ArrayList<Workout> workouts = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            long minDate = Long.parseLong(startDateTime);
            statement.setInt(1, userId);
            statement.setLong(2, minDate);
            statement.setInt(3, userId);
            statement.setLong(4, minDate);
            statement.setLong(5, minDate);
            ResultSet resultSet = statement.executeQuery();
//            System.out.println(statement.toString());

            while (resultSet.next()) {
                Workout workout = new Workout();
                workout.setWorkoutId(resultSet.getInt("id"));
                workout.setDateTime(resultSet.getLong("date_time"));
                workout.setDuration(resultSet.getLong("duration"));
                workout.setMaxGroupSize(resultSet.getInt("max_group_size"));
                workout.setDescription(resultSet.getString("description"));
                workout.setFreePlaces(resultSet.getInt("free_places"));
                workout.setExtraInfo1(resultSet.getInt("reserved"));
                workout.setExtraInfo2(resultSet.getInt("inLine"));
                workouts.add(workout);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workouts;
    }


    public ArrayList<Workout> getMyWorkouts(int userId) {

        String query = "SELECT workout.* FROM workout,reservations " +
                "WHERE reservations.user_id=? " +
                "AND reservations.reservation_deleted=0 " +
                "AND workout.date_time>=? " +
                "AND workout.id=reservations.workout_id ORDER BY date_time; ";
        System.out.println("db.getMyWorkouts method initialized ");
        ArrayList<Workout> workouts = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            long minDate = System.currentTimeMillis();

            statement.setInt(1, userId);
            statement.setLong(2, minDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Workout workout = new Workout();
                workout.setWorkoutId(resultSet.getInt("id"));
                workout.setDateTime(resultSet.getLong("date_time"));
                workout.setDuration(resultSet.getLong("duration"));
                workout.setMaxGroupSize(resultSet.getInt("max_group_size"));
                workout.setDescription(resultSet.getString("description"));
                workout.setFreePlaces(resultSet.getInt("free_places"));
                workouts.add(workout);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workouts;
    }

    //add to waitlist
    public void addToWaitlist(int workoutId, int userId) {
        System.out.println("Database addToWaitlist method started");
        Connection connection = null;
        PreparedStatement statement1 = null; //counts waitlist for given workout
        PreparedStatement statement2 = null; //adds waitlist
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            String query1 = "SELECT COUNT(*) id  from waitlist WHERE workout_id=? ;";
            statement1 = connection.prepareStatement(query1);
            statement1.setInt(1, workoutId);
            resultSet = statement1.executeQuery();
            int countInLine = 0;
            while (resultSet.next()) {
                countInLine = resultSet.getInt("id");
            }
            countInLine++;

            String query2 = "INSERT INTO waitlist(waitlist_date, user_id, workout_id, number_in_line) VALUES(?,?,?,?);";
            long date = System.currentTimeMillis();
            statement2 = connection.prepareStatement(query2);
            statement2.setLong(1, date);
            statement2.setInt(2, userId);
            statement2.setInt(3, workoutId);
            statement2.setInt(4, countInLine);
            statement2.executeUpdate();
            System.out.println("added to waitlist");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //quit waitlist
    public void quitWaitlist(int waitlistId) {
        System.out.println("Database quitWaitlist method started");
        Connection connection = null;
        PreparedStatement statement1 = null; //selects waitlists maching cirteria
        PreparedStatement statement2 = null; //updates waitlists
        PreparedStatement statement3 = null; //updates target waitlist
        ResultSet resultSet1 = null;
        try {
            connection = dataSource.getConnection();
            String query1 = "SELECT number_in_line FROM waitlist WHERE id=? ;";
            statement1 = connection.prepareStatement(query1);
            statement1.setInt(1, waitlistId);
            resultSet1 = statement1.executeQuery();
            System.out.println("statement1 executed");

            int numberInLine = 0;
            while (resultSet1.next()) {
                numberInLine = resultSet1.getInt("number_in_line");
            }

            if (numberInLine > 0) {
                String query2 = "UPDATE waitlist " +
                        "SET number_in_line = number_in_line-1 " +
                        "WHERE number_in_line>?;";
                statement2 = connection.prepareStatement(query2);
                statement2.setInt(1, numberInLine);
                statement2.executeUpdate();
                System.out.println("statement2 executed");
            }

            String query3 = "UPDATE waitlist SET number_in_line=0 WHERE id=? ;";
            statement3 = connection.prepareStatement(query3);
            statement3.setInt(1, waitlistId);
            statement3.executeUpdate();
            System.out.println("statement3 executed, deleted from waitlist");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //get my waitlists
    public ArrayList<WaitlistItem> getMyWaitlist(int userId) {
        System.out.println("Database getMyWaitlist method started");

        String query = "SELECT workout.*, waitlist.id, waitlist.number_in_line FROM workout " +
                "JOIN waitlist ON waitlist.workout_id=workout.id " +
                "WHERE waitlist.user_id =? " +
                "AND waitlist.number_in_line>0 " +
                "AND workout.date_time>=? ";
        System.out.println("db.getMyWorkouts method initialized ");
        ArrayList<WaitlistItem> waitlists = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            long minDate = System.currentTimeMillis();

            statement.setInt(1, userId);
            statement.setLong(2, minDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                WaitlistItem waitlistItem = new WaitlistItem();
                waitlistItem.setWorkoutId(resultSet.getInt("workout.id"));
                waitlistItem.setDateTime(resultSet.getLong("workout.date_time"));
                waitlistItem.setDuration(resultSet.getLong("workout.duration"));
                waitlistItem.setMaxGroupSize(resultSet.getInt("workout.max_group_size"));
                waitlistItem.setDescription(resultSet.getString("workout.description"));
                waitlistItem.setFreePlaces(resultSet.getInt("workout.free_places"));
                waitlistItem.setWaitlistId(resultSet.getInt("waitlist.id"));
                waitlistItem.setNumberInLine(resultSet.getInt("waitlist.number_in_line"));
                waitlists.add(waitlistItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return waitlists;
    }

    public Workout getWorkout(int workoutId) {
        String query = "SELECT * FROM workout WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, workoutId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Workout workout = new Workout();
                workout.setWorkoutId(resultSet.getInt("id"));
                workout.setDateTime(resultSet.getLong("date_time"));
                workout.setDuration(resultSet.getLong("duration"));
                workout.setMaxGroupSize(resultSet.getInt("max_group_size"));
                workout.setDescription(resultSet.getString("description"));
                workout.setFreePlaces(resultSet.getInt("free_places"));
                return workout;

            } else {
                System.out.println("wrong workout id: " + workoutId);
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void saveToken(int userId, String token) {
        String query = "UPDATE users SET token=? where id=? ;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, token);
            statement.setInt(2, userId);
            statement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<WaitlistItem> getWaitlistByWorkout(int workoutId) {
        String query = "SELECT*FROM waitlist WHERE workout_id=? AND number_in_line>0";
        ArrayList<WaitlistItem> waitlist = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, workoutId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                WaitlistItem waitlistItem = new WaitlistItem();
                waitlistItem.setWaitlistId(resultSet.getInt("id"));
                waitlistItem.setWaitlistDate(resultSet.getLong("waitlist_date"));
                waitlistItem.setUserId(resultSet.getInt("user_id"));
                waitlistItem.setWorkoutId(resultSet.getInt("workout_id"));
                waitlistItem.setNumberInLine(resultSet.getInt("number_in_line"));
                waitlist.add(waitlistItem);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return waitlist;
    }

    public ArrayList<Workout> getWorkouts(long startDateTime, long endDateTime) {
        String query = "SELECT * FROM workout WHERE date_time > ? AND date_time < ?; ";
        System.out.println("db.getWorkouts method initialized ");
        ArrayList<Workout> workouts = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, startDateTime);
            statement.setLong(2, endDateTime);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Workout workout = new Workout();
                workout.setWorkoutId(resultSet.getInt("id"));
                workout.setDateTime(resultSet.getLong("date_time"));
                workout.setDuration(resultSet.getLong("duration"));
                workout.setMaxGroupSize(resultSet.getInt("max_group_size"));
                workout.setDescription(resultSet.getString("description"));
                workout.setFreePlaces(resultSet.getInt("free_places"));
                workouts.add(workout);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workouts;
    }

    public ArrayList<User> getWorkoutParticipants(Workout workout) {
        String query = "SELECT u.* FROM users u JOIN reservations r ON u.id=r.user_id " +
                "WHERE r.workout_id=? AND r.reservation_deleted=0;";
        ArrayList<User> participants = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, workout.getWorkoutId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("id"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setFamilyName(resultSet.getString("family_name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setPhone(resultSet.getString("phone"));
                user.setRights(resultSet.getInt("rights"));
                user.setCredits(resultSet.getInt("credits"));
                user.setToken(resultSet.getString("token"));
                participants.add(user);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    public boolean saveUserData(User user) {
        String query = "UPDATE users SET first_name=?, family_name=?, email=?, password=?, phone=? WHERE id=? ;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getFamilyName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getPhone());
            statement.setInt(6, user.getUserId());
            statement.executeUpdate();
            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

    }

    public boolean insertNewUser(User user) {
        String query = "INSERT INTO users (first_name, family_name, email, password, phone) VALUES(?,?,?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getFamilyName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getPhone());
            statement.executeUpdate();
            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

    }

    public ArrayList<User> getAllUsers() {
        String query = "SELECT * FROM users ; ";
        System.out.println("db.getAllUsers method initialized ");
        ArrayList<User> usersList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("id"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setFamilyName(resultSet.getString("family_name"));
                user.setEmail(resultSet.getString("email"));
                user.setPhone(resultSet.getString("phone"));
                user.setRights(resultSet.getInt("rights"));
                user.setCredits(resultSet.getInt("credits"));
                usersList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usersList;
    }

    //changes user balance
    public void updateBalance(int userId, int newBalance) {

        String query = "UPDATE users SET credits=? where id=? ;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, newBalance);
            statement.setInt(2, userId);
            statement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //updates user rights
    public void updateRights(int userId, int newRights) {
        String query = "UPDATE users SET rights=? where id=? ;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, newRights);
            statement.setInt(2, userId);
            statement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void operationLog(int userId, long currentTimeMillis, int balanceChange, int referenceId, String info) {
        String query = "INSERT INTO balance_changes(user_id, operation_date_time, balance_change, reference_id, operation_info) " +
                "VALUES(?,?,?,?,?);";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            long date = System.currentTimeMillis();
            statement.setInt(1, userId);
            statement.setLong(2, currentTimeMillis);
            statement.setInt(3, balanceChange);
            statement.setInt(4, referenceId);
            statement.setString(5, info);
            statement.executeUpdate();
            System.out.println("log saved");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //get all log entries for user
    public JSONArray getOperationLog(int userId) {
        String query = "SELECT log.operation_date_time, log.balance_change,log.operation_info, log.reference_id, u.first_name, u.family_name  FROM " +
                "balance_changes log JOIN users u ON log.reference_id=u.id " +
                "WHERE log.user_id=? ORDER BY log.operation_date_time DESC";
        JSONArray jsonArray = new JSONArray();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JSONObject logJson = new JSONObject();
                logJson.put("operationDateTime", resultSet.getLong("operation_date_time"));
                logJson.put("balanceChange", resultSet.getInt("balance_change"));
                logJson.put("operationInfo", resultSet.getString("operation_info"));
                logJson.put("referenceName", resultSet.getString("first_name"));
                logJson.put("referenceFamilyName", resultSet.getString("family_name"));
                jsonArray.put(logJson);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return jsonArray;
    }


}
