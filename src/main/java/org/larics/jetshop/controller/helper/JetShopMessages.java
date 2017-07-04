package org.larics.jetshop.controller.helper;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/*
 * Container of messages to be shown to user.
 * 
 * @author Igor Laryukhin
 */
public class JetShopMessages {

	private List<String> errorMessages = new ArrayList<>();
	private List<String> infoMessages = new ArrayList<>();
	private List<String> debugMessages = new ArrayList<>();

	private enum Level {DEBUG, INFO};
	private Level level;

	public JetShopMessages() {
		this(Level.INFO.toString());
	}

	public JetShopMessages(String level) {
		this.level = parseLevel(level);
	}

	public void setLevel(String level) {
		this.level = parseLevel(level);
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public List<String> getInfoMessages() {
		return infoMessages;
	}

	public List<String> getDebugMessages() {
		return debugMessages;
	}

	public void addErrorMessage(String message) {
		errorMessages.add(message);
	}

	public void addInfoMessage(String message) {
		infoMessages.add(message);
	}

	public void addDebugMessage(String message) {
		if (level == Level.DEBUG) {
			debugMessages.add(LocalTime.now() + " " + message);
		}
	}

	private Level parseLevel(String level) {
		if (level != null && level.equalsIgnoreCase(Level.DEBUG.toString())) {
			return Level.DEBUG;
		} else {
			return Level.INFO;
		}
	}

	@Override
	public String toString() {
		return "JetShopMessages" + "@" + Integer.toHexString(hashCode())
				+ " [errorMessages=" + errorMessages
				+ ", infoMessages=" + infoMessages + ", debugMessages="
				+ debugMessages + ", level=" + level + "]";
	}

}
