package com.clockwise.tworcy.model.account;

public enum Access {
	NORMAL(0), MODERATOR(1), ADMIN(2), HEADADMIN(3);

	private int access;

	private Access(int access) {
		this.access = access;
	}

	public int getAccess() {
		return access;
	}
	
	public static Access byValue(int access)
	{
		Access[] values = Access.values();

		for(Access a : values)
			if(a.access == access)
				return a;
		
		return values[access];
	}
}
