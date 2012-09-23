package com.cubeia.games.poker.common;

import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;

import com.google.inject.Singleton;

@Singleton
public class SystemTestTime implements SystemTime {

	private final AtomicLong time = new AtomicLong(0);
	
	public void set(long t) {
		time.set(t);
	}
	
	@Override
	public DateTime date() {
		return new DateTime(now());
	}

	@Override
	public long now() {
		return time.get();
	}
}
