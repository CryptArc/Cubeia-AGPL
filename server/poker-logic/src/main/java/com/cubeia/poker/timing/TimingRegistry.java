package com.cubeia.poker.timing;

/**
 * A registry for returning timing definitions.
 * 
 * A Timing is a set of predefeined pause/waiting times between 
 * events in the game. E.g. waitperiod between last call to new cards are 
 * dealt.
 * 
 * My intentions are that we should eventually support different
 * sets of waiting period. This way we could support slow and express 
 * tables, the difference is just what timing profile they use.
 * 
 * Currently I am only using a default definition. This is hardly
 * a prioritized task atm, but I think it nice to have a well defined
 * interface so it wont be such a pain to implement different time
 * profiles later on.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public interface TimingRegistry {
	
	/**
	 * Get the default timing profile.
	 * 
	 * @return
	 */
	public TimingProfile getDefaultTimingProfile();
	
	/**
	 * Get a timing profile based on the given profile.
	 * 
	 * @param name
	 * @return
	 */
	public TimingProfile getTimingProfile(Timings profile);
	
	
	
}
