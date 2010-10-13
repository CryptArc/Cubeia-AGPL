package com.cubeia.poker.login
{
	import com.cubeia.poker.event.PokerEvent;

	public class LoginEvent extends PokerEvent
	{
		public static const LOGIN_EVENT:String = "login_event";
		
		public function LoginEvent(username:String, password:String, host:String, operatorId:int)
		{
			_username = username;
			_password = password;
			_host = host;
			_operatorId = operatorId;
			super(LOGIN_EVENT);
		}

		public function get username():String
		{
			return _username;
		}

		public function get password():String
		{
			return _password;
		}

		public function get host():String
		{
			return _host;
		}
		
		public function get operatorId():int
		{
			return _operatorId;
		}

		private var	_username:String;
		private var	_password:String;
		private var	_host:String;
		private var	_operatorId:int;
	}
}