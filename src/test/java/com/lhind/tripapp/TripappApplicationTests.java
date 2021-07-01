package com.lhind.tripapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@SpringBootTest
class TripappApplicationTests {

	@Autowired
	PasswordEncoder encoder;

	@Test
	void contextLoads() {

		System.out.println(encoder.encode("12345678"));
		System.out.println(new Date(""));
	}

}
