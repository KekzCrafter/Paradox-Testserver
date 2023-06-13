package de.kekz.testserver.manager.verify;

import java.security.SecureRandom;

import org.apache.commons.lang.RandomStringUtils;

public class VerifySession {

	private long sessionId;
	private String verificationId;

	public VerifySession(long sessionId) {
		this.sessionId = sessionId;
		this.verificationId = RandomStringUtils.random(6, 0, 0, true, true, null, new SecureRandom());
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public String getVerificationId() {
		return verificationId;
	}
}
