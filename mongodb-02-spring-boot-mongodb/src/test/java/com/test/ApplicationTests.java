package com.test;

import com.test.dao.UserDao;
import com.test.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	UserDao userDao;

	@Test
	public void testInsert() {
		User user = new User();
		user.setId("1");
		user.setName("ab");
		userDao.save(user);

		user = new User();
		user.setId("2");
		user.setName("ac");
		userDao.save(user);
	}

	@Test
	public void testUpdate() {
		User user = new User();
		user.setId("1");
		user.setName("b");
		userDao.save(user);
	}

	@Test
	public void testDelete() {
		User user = new User();
		user.setId("1");
		userDao.delete(user);
		//userDao.delete("1");
	}

	@Test
	public void testDeleteAll() {
		userDao.deleteAll();
	}

	@Test
	public void testFindOne() {
		User user = userDao.findOne("1");
		System.out.println(user);
	}

	@Test
	public void testFindAll() {
		List<User> userList = userDao.findAll();
		for (User user : userList) {
			System.out.println(user);
		}
	}

	@Test
	public void testFindAll2() {
		User user = new User();
		user.setName("a");

		ExampleMatcher matcher = ExampleMatcher.matching()
				.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());

		Example<User> example = Example.of(user, matcher);

		List<User> userList = userDao.findAll(example);
		for (User u : userList) {
			System.out.println(u);
		}
	}

}
