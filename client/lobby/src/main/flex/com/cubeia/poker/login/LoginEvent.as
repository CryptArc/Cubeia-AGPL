package com.cubeia.poker.login
{
	import com.cubeia.poker.event.PokerEvent;

	public class LoginEvent extends PokerEvent
	{
		public static const LOGIN_EVENT:String = "login_event";
		
		public function LoginEvent(username:String, password:String, host:String)
		{
			_username = username;
			_password = password;
			_host = host;
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

		private var	_username:String;
		private var	_password:String;
		private var	_host:String;
	}
}