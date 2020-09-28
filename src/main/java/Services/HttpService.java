package Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import models.LogDataEntry;
import models.User;
import models.WaitlistItem;
import models.Workout;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Field;

import java.io.DataInput;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Controller
public class HttpService {
    String dbLocal = "sfdata";
    String dbHeroku = "heroku_17ddbc5da53f828";
    Database db = new Database(dbHeroku);

    @RequestMapping("/home")
    public @ResponseBody
    String home() {
        return getTextFromFile("home.html");
    }


    @RequestMapping("/login")
    public @ResponseBody
    User login(@RequestParam("email") String email, @RequestParam("password") String password) {
        System.out.println("Vardas: " + email + " slaptazodis " + password);
        User user = db.login(email, password);
        return user;
    }

    //return user on userId
    @RequestMapping("/get_user_data")
    public @ResponseBody
    User getUserData(@RequestParam("userId") int userId) {
        return db.getUserData(userId);
    }

    // returns all Users list
    @RequestMapping("/get_all_users")
    public @ResponseBody
    List<User> getAllUsers(@RequestParam("userRights") int userRights) {
        List<User> allUsers = new ArrayList<>();
        if (userRights == 1) {
            allUsers = db.getAllUsers();
        }
        return allUsers;
    }

    //save user data or create new user if userId<=0
    //todo fix method to accept User data type
    @RequestMapping("/save_user_data")
    public @ResponseBody
    String saveUserData(@RequestParam("userId") int userId, @RequestParam("firstName") String firstName,
                        @RequestParam("familyName") String familyName, @RequestParam("email") String email,
                        @RequestParam("password") String password, @RequestParam("phone") String phone) {

        User user = new User();
        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setFamilyName(familyName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);

        if (user.getUserId() > 0) {
            if (db.saveUserData(user)) {
                return "User data updated";
            } else {
                return "User data not updated";
            }
        } else {
            if (db.insertNewUser(user)) {
                return "New account created";
            } else {
                return "Unable to create new account";
            }
        }

    }

    // returns all workouts later or equal than given date
    @RequestMapping("/get_workouts")
    public @ResponseBody
    List<Workout> getWorkouts(@RequestParam("date") String date) {
        return db.getWorkouts(date);
    }

    //returns list of workouts later or equal than given date
    // with extra info for exact user: has he booked and is he in line for it
    @RequestMapping("/get_workouts_with_extra")
    public @ResponseBody
    List<Workout> getWorkouts(@RequestParam("date") String date, @RequestParam("userId") int userId) {
        return db.getWorkouts(date, userId);
    }

    //returns workouts of exact user
    @RequestMapping("/get_my_workouts")
    public @ResponseBody
    List<Workout> getMyWorkouts(@RequestParam("userId") int userId) {
        return db.getMyWorkouts(userId);
    }

    //todo fix db.addworkout method to return boolean type to indicate if workout adding was successful
    //todo fix method to accept parameter of Workout data type
    //adds workout to database
    @RequestMapping("/add_workout")
    public @ResponseBody
    String addWorkout(@RequestParam("date") long date,
                      @RequestParam("maxGroupSize") int maxGroupSize,
                      @RequestParam("description") String description) {
        Workout workout = new Workout();
        workout.setDateTime(date);
        workout.setMaxGroupSize(maxGroupSize);
        workout.setDescription(description);
        workout.setFreePlaces(workout.getMaxGroupSize());
        db.addWorkout(workout);
        return "workout added";
    }

    //adds reservation
    @RequestMapping("/reserve")
    public @ResponseBody
    String reserve(@RequestParam("workoutId") int workoutId, @RequestParam("userId") int userId) {
        User user = db.getUserData(userId);
        Workout workout = db.getWorkout(workoutId);
        if (user.getCredits() <= 0) {
            return "No credits";
        } else if (workout.getFreePlaces() < 1) {
            return "No free places";
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
            return "Reservation added";
        }
    }

    //todo check if this method could not be improved
    //cancels reservation
    @RequestMapping("/cancel_reservation")
    public @ResponseBody
    String cancelReservation(@RequestParam("workoutId") int workoutId, @RequestParam("userId") int userId) {
        long workoutDate = db.getWorkout(workoutId).getDateTime();
        db.cancelReservation(workoutId, userId, workoutDate);

        //sends notifications and updates waitlist
        NotificationHandler notificationHandler = new NotificationHandler();
        notificationHandler.handleWaitlist(workoutId, db);

        return "Reservation canceled";
    }

    //returns waitlist of exact user
    @RequestMapping("/get_my_waitlists")
    public @ResponseBody
    List<WaitlistItem> getMyWaitlist(@RequestParam("userId") int userId) {
        return db.getMyWaitlist(userId);
    }

    //add to waitlist
    @RequestMapping("/add_waitlist")
    public @ResponseBody
    String addToWaitlist(@RequestParam("workoutId") int workoutId, @RequestParam("userId") int userId) {
        db.addToWaitlist(workoutId, userId);
        return "Added to waitlist added";
    }

    //remove from waitlist
    @RequestMapping("/quit_waitlist")
    public @ResponseBody
    String quitWatilist(@RequestParam("waitlistId") int waitlistId) {
        db.quitWaitlist(waitlistId);
        return "Removed from waitlist";
    }

    //remove from waitlist based on workoutId and userId
    @RequestMapping("/quit_waitlist_by_workoutid_userid")
    public @ResponseBody
    String quitWatilist(@RequestParam("workoutId") int workoutId, @RequestParam("userId") int userId) {
        ArrayList<WaitlistItem> myWaitlists = db.getMyWaitlist(userId);
        WaitlistItem myWaitlist = new WaitlistItem();
        if (myWaitlists != null) {
            for (WaitlistItem waitlist : myWaitlists) {
                if (waitlist.getWorkoutId() == workoutId) {
                    myWaitlist = waitlist;
                }
            }
        }
        if (myWaitlist.getWaitlistId() > 0) {
            db.quitWaitlist(myWaitlist.getWaitlistId());
            return "Removed from waitlist";
        } else {
            return "waitlist item didnt found";
        }

    }


    //add credits to user account and returns new balance
    @RequestMapping("/add_credits")
    public @ResponseBody
    int updateBalance(@RequestParam("userId") int userId,
                      @RequestParam("addCredits") int addCredits,
                      @RequestParam("referenceId") int referenceId) {
        User user = db.getUserData(userId);
        int newBalance = user.getCredits() + addCredits;
        db.updateBalance(userId, newBalance);
        db.operationLog(userId, System.currentTimeMillis(), addCredits, referenceId, "Credits added");
        return newBalance;
    }

    //save token
    @RequestMapping("/save_token")
    public @ResponseBody
    String saveToken(@RequestParam("userId") int userId, @RequestParam("token") String token) {
        db.saveToken(userId, token);
        return "token saved";
    }


    @RequestMapping("/change_rights")
    public @ResponseBody
    int changeRights(@RequestParam("userId") int userId, @RequestParam("newRights") int newRights) {
        db.updateRights(userId, newRights);
        return newRights;
    }

    @RequestMapping("/get_operation_log")
    public @ResponseBody
    List<LogDataEntry> getLog(@RequestParam("userId") int userId) {
        return db.getOperationLog(userId);
    }

    @RequestMapping("/reset_password")
    public @ResponseBody
    String resetPassword(@RequestParam("email") String email,
                         @RequestParam("newPassword") String newPassword,
                         @RequestParam("encryptedPassword") String encryptedPassword) {
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

    }


    private String getTextFromFile(String path) {
        byte[] data;
        try {
            InputStream is = HttpService.class.getClassLoader().getResourceAsStream(path);
            data = is.readAllBytes();
            String result = new String(data);
            return result; //Files.readString(Paths.get(fullPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Loading error";
    }

}
