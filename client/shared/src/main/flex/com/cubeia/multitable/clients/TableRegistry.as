package com.cubeia.multitable.clients
{
	
	/**
	 * The table registry holds the id:s of all current subscribers to table messages
	 */
	public class TableRegistry
	{
		private var _data:Array;
		
		public function TableRegistry()
		{
			_data = new Array();
		}

		/**
		 * Add a table to the registry
		 */
		public function addTable(tableid:int):void
		{
			for each ( var tid:int in _data ) {
				// don't register twice
				if ( tid == tableid ) {
					return;
				}
			}
			_data.push(tableid);
		}

		/**
		 * Remove a table from the registry
		 */
		public function removeTable(tableid:int):void
		{
			for ( var i:int = 0; i < _data.length; i ++ ) {
				if ( _data[i] == tableid ) {
					_data.splice(i,1);
					return;
				}
			}
			_data.push(tableid);
		}

		/**
		 * Get the full list of tables
		 */
		public function getTableList():Array
		{
			return _data;
		}

	}
}