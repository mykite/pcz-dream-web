package com.pcz.dream.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pcz.dream.Bootstrap;
import com.pcz.dream.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Bootstrap.class)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;
	@Test
	public void test() {
		User save = repository.save(new User(null, "kite", 11));
		System.out.println(save);
	}
}
