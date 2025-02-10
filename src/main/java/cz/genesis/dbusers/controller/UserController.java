package cz.genesis.dbusers.controller;

import cz.genesis.dbusers.model.User;
import cz.genesis.dbusers.service.UserService;
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
        if (userService.notEmptyName(userData) && userService.getIsPersonIDinList() && !userService.getIsSamePersonID()) {
            return ResponseEntity.status(HttpStatus.OK).body("OK. " +
                   "The user has been successfully inserted into the table.");
        }
        else if (!userService.notEmptyName(userData)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
                   "The name has not to be empty!");
        }
        else if (!userService.getIsPersonIDinList()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
                   "The personID does not exist in the list and has not to be empty!");
        }
        else return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
                   "The user with that personID already exists in the table!");
    }

    @GetMapping("/{id}")
    @ResponseBody
    public User getUserById(@PathVariable("id") int id, @RequestParam(name = "detail", required = false) boolean detail) throws SQLException {
        if (userService.checkingId(id)) {
            return userService.getUserInfo(id, detail);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping("")
    public List<User> getUsers(@RequestParam(name = "detail", required = false) boolean detail) throws SQLException {
        return userService.getAllUsersInfo(detail);
    }

    @PutMapping()
    public ResponseEntity<?> updatingData(@RequestBody User userData) throws SQLException {
        if (userService.notEmptyName(userData) && userService.checkingId(userData.getId())) {
            userService.updatingUser(userData);
            return ResponseEntity.status(HttpStatus.OK).body("OK. " +
                   "The data of the user has been successfully changed in the table.");
        }
        else if (!userService.notEmptyName(userData)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
                   "The name has not to be empty!");
        }
        else return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
                    "The user with that id does not exist in the table!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") int id) throws SQLException {
        if (userService.checkingId(id)) {
            userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body("OK. " +
                   "The user has been successfully deleted from the table.");
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Warning! " +
               "The user with that id does not exist in the table!");
    }

}//konec tridy