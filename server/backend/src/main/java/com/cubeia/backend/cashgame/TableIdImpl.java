package com.cubeia.backend.cashgame;

import java.util.concurrent.atomic.AtomicLong;

public class TableIdImpl implements TableId {

	private static final AtomicLong idGenerator = new AtomicLong(0);
	
	private final long id;

	public TableIdImpl() {
		this.id = idGenerator.incrementAndGet();
	}
	
	public TableIdImpl(int id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return (int) id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (! (obj instanceof TableIdImpl)) {
			return false;
		}
		
		return id == ((TableIdImpl) obj).id;
	}
	
	@Override
	public String toString() {
		return "TableId(" + id + ")";
	}
}
