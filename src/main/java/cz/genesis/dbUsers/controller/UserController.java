package cz.genesis.dbUsers.controller;

import cz.genesis.dbUsers.model.User;
import cz.genesis.dbUsers.model.UserMin;
import cz.genesis.dbUsers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping()
    public ResponseEntity<?> insertNewUser(@RequestBody User userData) throws SQLException {
        userService.createNewUser(userData);
        if ((!userService.isEmptyName && userService.isPersonIDinList) && !userService.isSamePersonID) {
            return ResponseEntity.status(HttpStatus.OK).body("OK. " +
                   "The user has been successfully inserted into the table.");
        }
        else if (userService.isEmptyName) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
                   "The name has not to be empty!");
        }
        else if (!userService.isPersonIDinList) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
                   "The personID does not exist in the list and has not to be empty!");
        }
        else return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
                    "The user with that personID already exists in the table!");
    }

    @GetMapping("/{id}")
    @ResponseBody
    public UserMin getUserById(@PathVariable("id") int id) throws SQLException {
        userService.checkingID(id);
        if (userService.isIDinDatabase) {
            return userService.getUserMin(id);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/{id}", params = {"detail"})
    @ResponseBody
    public User getUserDetailById(@PathVariable("id") int id, @RequestParam String detail) throws SQLException {
        userService.checkingID(id);
        if (userService.isIDinDatabase) {
            return userService.getUserDetail(id, detail);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping("")
    public List<UserMin> getUsers() throws SQLException {
        return userService.getAllUsersMin();
    }

    @GetMapping(value = "", params = {"detail"})
    public List<User> getUsersDetail(@RequestParam String detail) throws SQLException {
        return userService.getAllUsersDetail(detail);
    }

    @PutMapping()
    public ResponseEntity<?> updatingData(@RequestBody User userData) throws SQLException {
        userService.updatingUser(userData);
        if (!userService.isEmptyName && userService.isIDinDatabase) {
            return ResponseEntity.status(HttpStatus.OK).body("OK. " +
                   "The data of the user has been successfully changed in the table.");
        }
        else if (userService.isEmptyName) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
                   "The name has not to be empty!");
        }
        else return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
                   "The user with that ID does not exist in the table!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") int id) throws SQLException {
        userService.deleteUser(id);
        if (userService.isIDinDatabase) {
            return ResponseEntity.status(HttpStatus.OK).body("OK. " +
                   "The user has been successfully deleted from the table.");
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
               "The user with that ID does not exist in the table!");
    }

}//konec tridy
