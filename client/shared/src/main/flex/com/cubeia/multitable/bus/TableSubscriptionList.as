package com.cubeia.multitable.bus
{
	
	public class TableSubscriptionList
	{
		
		public function TableSubscriptionList()
		{
			_subscriptionMap = new Object();
			
		}
		
		public function subscribeTable(clientId:String, tableId:int):void
		{
			trace("Adding client/table " + clientId + "/" + tableId + " to subscription list.");
			_subscriptionMap[clientId] = tableId;			
		}
		
		public function unSubscribeAnyTable(clientId:String):void
		{
			if ( _subscriptionMap[clientId] != null ) {
				trace("Removing client " + clientId + " from subscription list.");
				delete _subscriptionMap[clientId];
			}
		}
		
		
		public function unSubscribeTable(clientId:String, tableId:int):void
		{
			if ( isTableSubscribed(clientId, tableId) ) {
				trace("Removing client/table " + clientId + " from subscription list.");
				delete _subscriptionMap[clientId];
			} 
						
		}
		
		public function isTableSubscribed(clientId:String, tableId:int):Boolean
		{
			var subscribed:Boolean = _subscriptionMap[clientId] == tableId;
			// trace ("checking subscription client/table " + clientId + "/" + tableId + " = " + subscribed.toString() ); 
			return (subscribed);	
		}
			
		private var _subscriptionMap:Object;
		
	}
}



