package com.zetra.econsig.webservice.rest.filter;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

public class SessionData implements Serializable {
    /**
     *
     */
	private static final long serialVersionUID = 1L;

    Calendar date;

    int delay;

    int day;

    int tries;

    String ip;

    @Override
	public int hashCode() {
		return Objects.hash(day, ip);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
		if (obj == null) {
            return false;
        }
		if (getClass() != obj.getClass()) {
            return false;
        }
		final SessionData other = (SessionData) obj;
		if (day != other.day) {
            return false;
        }
		return Objects.equals(ip, other.ip);
	}

	@Override
	public String toString() {
		return "SessionData [date=" + date + ", delay=" + delay + ", day=" + day + ", tries=" + tries + ", ip=" + ip + "]";
	}
}
