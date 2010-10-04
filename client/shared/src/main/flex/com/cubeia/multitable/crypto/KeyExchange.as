package com.cubeia.multitable.crypto
{
	import flash.utils.ByteArray;
	
	public interface KeyExchange
	{
		function getPublicKey():String;
		function decryptSessionKey(buffer:ByteArray):ByteArray;
	}
}