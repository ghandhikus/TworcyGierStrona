package com.clockwise.tworcy.model.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public @Component class DummyAccountInjector {
	@Autowired AccountConverter convert;
	
	public void inject(AccountInject inject) { 
		Access[] needs = inject.needs();
		Account[] accounts = new Account[needs.length];
		int max = needs.length;
		for(int i=0;i<max;i++)
			accounts[i] = createDummy(needs[i]);
		
		inject.inject(accounts);
	}
	
	Account createDummy(Access access) {
		AccountData data = new AccountData();
		data.setId(1);
		data.setUsername(access.toString());
		data.setPassword("TESTS_ACCOUNT_k92k092fjk902j9fgj290fj290oksopskfpo_TESTS_ACCOUNT");
		data.setAccess(access.getAccess());
		data.setEnabled(true);
		data.setLocked(false);
		return convert.convert(data);
	}
}
