import Services.EmailHandler;
import Services.MsgApiService;
import Services.NotificationHandler;
import Services.ReminderThread;
import database.Database;
import models.User;
import models.WaitlistItem;
import models.Workout;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Spark;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import database.Database;

public class Main {
    private MsgApiService msgService;

    public MsgApiService getMsgService() {
        return msgService;
    }


    public static void main(String[] args) {
        String dbLocal = "sfdata";
        String dbHeroku = "heroku_17ddbc5da53f828";

        Database db = new Database(dbHeroku);
        Thread reminderSending = new Thread(new ReminderThread());
        reminderSending.start();

        int port = getHerokuAssignedPort();
        System.out.println("port assigned "+ port);
        Spark.port(port);
        System.out.println(db.printAll());

        Spark.get("/home", (request, response) -> getTextFromFile("home.html"));
        Spark.get("/get_my_workouts", (request, response) -> getTextFromFile("get_array_list.html"));


/*        Spark.post("/login", (request, response) -> {
            String email = request.queryParams("email");
            String password = request.queryParams("password");
            System.out.println("Vardas: " + email + " slaptazodis " + password);
            User user = db.login(email, password);
            if (user != null) {
                JSONObject json = new JSONObject();
                json.put("userId", user.getUserId());
                json.put("firstName", user.getFirstName());
                json.put("familyName", user.getFamilyName());
                json.put("email", user.getEmail());
                json.put("password", user.getPassword());
                json.put("phone", user.getPhone());
                json.put("rights", user.getRights());
                json.put("credits", user.getCredits());
                System.out.println(json.toString());
                return json.toString();
            } else {
                System.out.println("wrong password. " + user.getEmail() + " " + user.getPassword());
                response.status(400);
                return "unable to login";
            }
        });*/

 /*       //return user data
        Spark.post("/get_user_data", (request, response) -> {
            int userId = Integer.parseInt(request.queryParams("userId"));
            System.out.println("models.User ID: " + userId);
            User user = db.getUserData(userId);
            if (user != null) {
                JSONObject json = new JSONObject();
                json.put("userId", user.getUserId());
                json.put("firstName", user.getFirstName());
                json.put("familyName", user.getFamilyName());
                json.put("email", user.getEmail());
                json.put("password", user.getPassword());
                json.put("phone", user.getPhone());
                json.put("rights", user.getRights());
                json.put("credits", user.getCredits());
                System.out.println(json.toString());
                return json.toString();
            } else {
                System.out.println("no such user " + userId);
                response.status(400);
                return "no such user";
            }
        });*/

        /*// returns all Users list
        Spark.post("/get_all_users", (request, response) -> {

            System.out.println("getAllUsers requested ");
            ArrayList<User> usersList = db.getAllUsers();

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < usersList.size(); i++) {
                JSONObject userJson = new JSONObject();
                userJson.put("userId", usersList.get(i).getUserId());
                userJson.put("firstName", usersList.get(i).getFirstName());
                userJson.put("familyName", usersList.get(i).getFamilyName());
                userJson.put("email", usersList.get(i).getEmail());
                userJson.put("phone", usersList.get(i).getPhone());
                userJson.put("rights", usersList.get(i).getRights());
                userJson.put("credits", usersList.get(i).getCredits());

                jsonArray.put(userJson);
            }

            System.out.println(jsonArray.toString());
            return jsonArray.toString();
        });
*/
/*        //save user data or create new user if userId<=0
        Spark.post("/save_user_data", (request, response) -> {
            User user = new User();
            user.setUserId(Integer.parseInt(request.queryParams("userId")));
            user.setFirstName(request.queryParams("firstName"));
            user.setFamilyName(request.queryParams("familyName"));
            user.setEmail(request.queryParams("email"));
            user.setPassword(request.queryParams("password"));
            user.setPhone(request.queryParams("phone"));

            if (user.getUserId() > 0) {
                if (db.saveUserData(user)) {
                    response.status(201);
                    return "models.User data updated";
                } else {
                    return "models.User data not updated";
                }
            } else {
                if (db.insertNewUser(user)) {
                    response.status(201);
                    return "New account created";
                } else {
                    return "Unable to create new account";
                }
            }
        });*/

/*
        //saves users device token to DB
        Spark.post("/save_token", (request, response) -> {
            int userId = Integer.parseInt(request.queryParams("userId"));
            String token = request.queryParams("token");
            db.saveToken(userId, token);
            return "token saved";
        });
*/


/*        // returns all workouts later or equal than given date
        Spark.post("/get_workouts_on_day", (request, response) -> {

            String date = request.queryParams("date");
            System.out.println("date " + date);
            ArrayList<Workout> workoutsList = db.getWorkouts(date);

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < workoutsList.size(); i++) {
                JSONObject workoutJson = new JSONObject();
                workoutJson.put("workoutId", workoutsList.get(i).getWorkoutId());
                workoutJson.put("dateTime", workoutsList.get(i).getDateTime());
                workoutJson.put("duration", workoutsList.get(i).getDuration());
                workoutJson.put("description", workoutsList.get(i).getDescription());
                workoutJson.put("maxGroupSize", workoutsList.get(i).getMaxGroupSize());
                workoutJson.put("freePlaces", workoutsList.get(i).getFreePlaces());

                jsonArray.put(workoutJson);
            }
            JSONObject json = new JSONObject();
            json.put("workoutsList", jsonArray);

            System.out.println(jsonArray.toString());
            return jsonArray.toString();
        });*/

/*
        // returns all workouts later or equal than given date + some extra data
        Spark.post("/get_workouts_with_extra", (request, response) -> {
//            System.out.println("request body: "+request.body());
            int userId = Integer.parseInt(request.queryParams("userId"));
            String date = request.queryParams("date");
            System.out.println("date " + date);
            ArrayList<Workout> workoutsList = db.getWorkouts(date,userId);

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < workoutsList.size(); i++) {
                JSONObject workoutJson = new JSONObject();
                workoutJson.put("workoutId", workoutsList.get(i).getWorkoutId());
                workoutJson.put("dateTime", workoutsList.get(i).getDateTime());
                workoutJson.put("duration", workoutsList.get(i).getDuration());
                workoutJson.put("description", workoutsList.get(i).getDescription());
                workoutJson.put("maxGroupSize", workoutsList.get(i).getMaxGroupSize());
                workoutJson.put("freePlaces", workoutsList.get(i).getFreePlaces());
                workoutJson.put("extraInfo1", workoutsList.get(i).getExtraInfo1());
                workoutJson.put("extraInfo2", workoutsList.get(i).getExtraInfo2());

                jsonArray.put(workoutJson);
            }
            JSONObject json = new JSONObject();
            json.put("workoutsList", jsonArray);
            return jsonArray.toString();
        });
*/

/*        //returns workouts of exact user
        Spark.post("/get_my_workouts", (request, response) -> {

            int userId = Integer.parseInt(request.queryParams("userId"));
            System.out.println("userId " + userId);
            ArrayList<Workout> workoutsList = db.getMyWorkouts(userId);

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < workoutsList.size(); i++) {
                JSONObject workoutJson = new JSONObject();
                workoutJson.put("workoutId", workoutsList.get(i).getWorkoutId());
                workoutJson.put("dateTime", workoutsList.get(i).getDateTime());
                workoutJson.put("duration", workoutsList.get(i).getDuration());
                workoutJson.put("description", workoutsList.get(i).getDescription());
                workoutJson.put("maxGroupSize", workoutsList.get(i).getMaxGroupSize());
                workoutJson.put("freePlaces", workoutsList.get(i).getFreePlaces());

                jsonArray.put(workoutJson);
            }
            JSONObject json = new JSONObject();
            json.put("workoutsList", jsonArray);

            System.out.println(jsonArray.toString());
            return jsonArray.toString();
        });*/

/*        //adds workout
        Spark.post("/add_workout", (request, response) -> {
            Workout workout = new Workout();
            workout.setDateTime(Long.parseLong(request.queryParams("date")));
            workout.setMaxGroupSize(Integer.parseInt(request.queryParams("maxGroupSize")));
            workout.setDescription(request.queryParams("description"));
            workout.setFreePlaces(workout.getMaxGroupSize());
            db.addWorkout(workout);
            return "Workout added";
        });*/

 /*       //adds reservation
        Spark.post("/reserve", (request, response) -> {
            int workoutId = Integer.parseInt(request.queryParams("workoutId"));
            int userId = Integer.parseInt(request.queryParams("userId"));
            User user = db.getUserData(userId);
            if (user.getCredits() <= 0) {
                return "No credits";
            } else {
                db.makeReservation(workoutId, userId);
                db.operationLog(userId, System.currentTimeMillis(), -1, userId, "workout booked " + workoutId);

                //checks if there are any same workouts in waitlist. If so, delete
                ArrayList<WaitlistItem> myWaitlist = db.getMyWaitlist(userId);
                for (WaitlistItem waitlistItem : myWaitlist) {
                    if (waitlistItem.getWorkoutId() == workoutId) {
                        db.quitWaitlist(waitlistItem.getWaitlistId());
                    }
                }
                response.status(201);
                return "Reservation added";
            }
        });*/

/*        //cancels reservation
        Spark.post("/cancel_reservation", (request, response) -> {
            System.out.println("Cancel reservation initialized");

            int workoutId = Integer.parseInt(request.queryParams("workoutId"));
            int userId = Integer.parseInt(request.queryParams("userId"));
            long workoutDate = db.getWorkout(workoutId).getDateTime();
            db.cancelReservation(workoutId, userId, workoutDate);

            //sends notifications and updates waitlist
            NotificationHandler notificationHandler = new NotificationHandler();
            notificationHandler.handleWaitlist(workoutId, db);

            return "Reservation canceled";
        });*/

 /*       //returns waitlist of exact user
        Spark.post("/get_my_waitlists", (request, response) -> {

            int userId = Integer.parseInt(request.queryParams("userId"));
            System.out.println("userId " + userId);
            ArrayList<WaitlistItem> waitlist = db.getMyWaitlist(userId);

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < waitlist.size(); i++) {
                JSONObject waitlistJson = new JSONObject();
                waitlistJson.put("workoutId", waitlist.get(i).getWorkoutId());
                waitlistJson.put("dateTime", waitlist.get(i).getDateTime());
                waitlistJson.put("duration", waitlist.get(i).getDuration());
                waitlistJson.put("description", waitlist.get(i).getDescription());
                waitlistJson.put("maxGroupSize", waitlist.get(i).getMaxGroupSize());
                waitlistJson.put("freePlaces", waitlist.get(i).getFreePlaces());
                waitlistJson.put("waitlistId", waitlist.get(i).getWaitlistId());
                waitlistJson.put("numberInLine", waitlist.get(i).getNumberInLine());
                jsonArray.put(waitlistJson);
            }

            System.out.println(jsonArray.toString());
            return jsonArray.toString();
        });*/

/*        //adds to waitlist
        Spark.post("/add_waitlist", (request, response) -> {
            int workoutId = Integer.parseInt(request.queryParams("workoutId"));
            int userId = Integer.parseInt(request.queryParams("userId"));
            db.addToWaitlist(workoutId, userId);
            return "Added to waitlist added";
        });*/

/*        //remove from waitlist
        Spark.post("/quit_waitlist", (request, response) -> {
            System.out.println("quit waitlist started");
            int waitlistId = Integer.parseInt(request.queryParams("waitlistId"));
            db.quitWaitlist(waitlistId);
            return "Removed from waitlist";
        });*/

/*        //add credits to user account and returns new balance
        Spark.post("/add_credits", (request, response) -> {
            int userId = Integer.parseInt(request.queryParams("userId"));
            int addCredits = Integer.parseInt(request.queryParams("addCredits"));
            int referenceId = Integer.parseInt(request.queryParams("referenceId"));
            User user = db.getUserData(userId);
            int newBalance = user.getCredits() + addCredits;
            db.updateBalance(userId, newBalance);
            db.operationLog(userId, System.currentTimeMillis(), addCredits, referenceId, "Credits added");
            return newBalance;
        });*/

  /*      Spark.post("/change_rights", (request, response) -> {
            int userId = Integer.parseInt(request.queryParams("userId"));
            int newRights = Integer.parseInt(request.queryParams("newRights"));
            db.updateRights(userId, newRights);
            return newRights;
        });*/

 /*       Spark.post("/get_operation_log", (request, response) -> {
            int userId = Integer.parseInt(request.queryParams("userId"));
            JSONArray jsonArray = db.getOperationLog(userId);
            System.out.println("Log for user Id " + userId + " " + jsonArray.toString());
            return jsonArray;
        });*/

        /*Spark.post("/reset_password", (request, response) -> {
            String email = request.queryParams("email");
            String newPassword = request.queryParams("newPassword");
            String encryptedPassword = request.queryParams("encryptedPassword");
            User user = db.getUserData(email);
            if (user == null) {
                return "no user found with such e-mail";
            } else {
                user.setPassword(encryptedPassword);
                db.saveUserData(user);
                EmailHandler postman = new EmailHandler();
                postman.sendEmail(email, newPassword);
                return "password has been changed";
            }
        });*/

    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 80; //return default port if heroku-port isn't set (i.e. on localhost)
    }


    private static String getTextFromFile(String path) {
        try {
            URI fullPath = Main.class.getClassLoader().getResource(path).toURI();
            return Files.readString(Paths.get(fullPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Loading error";
    }
}
