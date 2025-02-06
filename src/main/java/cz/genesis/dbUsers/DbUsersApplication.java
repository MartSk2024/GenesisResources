package cz.genesis.dbUsers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DbUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbUsersApplication.class, args);
	}

}
