package com.cubeia.poker.event
{
	import com.cubeia.poker.event.PokerEvent;

	[RemoteClass(alias="StyleChangedEvent")]
	public class StyleChangedEvent extends PokerEvent
	{
		public static const STYLE_CHANGED_EVENT:String = "style_changed_event";
		
		public function StyleChangedEvent(newStyleName:String = "", isConnectorEvent:Boolean = true)
		{
			super(STYLE_CHANGED_EVENT);
			styleName = newStyleName;
			connectorEvent = isConnectorEvent;
		}
		
		public var styleName:String;
		public var connectorEvent:Boolean;
	}
}