package com.clockwise.tworcy.model.account;

public interface AccountInject {
	public Access[] needs();
	public void inject(Account[] account);
}
