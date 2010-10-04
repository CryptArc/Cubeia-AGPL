package com.cubeia.multitable.crypto
{
	import flash.utils.ByteArray;
	
	public interface CryptoProvider
	{
		function setSessionKey(buffer:ByteArray):void;
		function decrypt(buffer:ByteArray):ByteArray;
		function encrypt(buffer:ByteArray):ByteArray;
	}
}