package com.clockwise.tworcy.model.account;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Creates dummy accounts just for testing.
 * @author Daniel
 */
@WebAppConfiguration // MVC
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring-dispatcher-servlet.xml"})
public class DummyAccountInjectorTests implements AccountInject {

	@Autowired DummyAccountInjector injector;
	
	Account normal, admin;
	
	public @Before @Test void testAccountInjector(){
		injector.inject(this);
		assertNotNull("Normal accout is null", normal);
		assertNotNull("Admin accout is null", admin);
		assertTrue("Normal account has different access than normal", normal.getAccessLevel() == Access.NORMAL);
		assertTrue("Admin account has different access than headadmin", admin.getAccessLevel() == Access.HEADADMIN);
	}

	@Override
	public Access[] needs() {
		return new Access[]{Access.NORMAL, Access.HEADADMIN};
	}

	@Override
	public void inject(Account[] account) {
		normal = account[0];
		admin = account[1];
	}
}
