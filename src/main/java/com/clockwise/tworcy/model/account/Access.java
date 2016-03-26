package com.clockwise.tworcy.model.account;

public enum Access {
	NORMAL(0), MODERATOR(1), ADMIN(2), HEADADMIN(3);

	private byte access;

	private Access(int access) {
		this.access = (byte)access;
	}
	private Access(byte access) {
		this.access = access;
	}

	public byte getAccess() {
		return (byte) access;
	}

	public static Access byValue(byte access)
	{
		Access[] values = Access.values();

		for(Access a : values)
			if(a.access == access)
				return a;
		
		return values[access];
	}
	public static Access byValue(int access)
	{
		return byValue((byte)access);
	}
}
