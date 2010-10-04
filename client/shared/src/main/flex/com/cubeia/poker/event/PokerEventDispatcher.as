package com.cubeia.poker.event
{
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;

	/**
	 * Singleton event dispatcher for poker events.
	 * Acts as a message broker.
	 */
	public class PokerEventDispatcher extends EventDispatcher
	{
		public static var instance:PokerEventDispatcher = new PokerEventDispatcher();
		
		public function PokerEventDispatcher(target:IEventDispatcher=null)
		{
			super(target);
		}
		
		public static function dispatch(event:PokerEvent, global:Boolean = false):void {
			instance.dispatchEvent(event);
			if (global) {
				instance.dispatchEvent(new PokerEventWrapper(event));
			}
			
		}
		
	}
}