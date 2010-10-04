package com.cubeia.multitable.queue
{
	public class Queue
	{
		private var firstNode:QueueNode;
		private var lastNode:QueueNode;
			
		public function isEmpty ():Boolean
	    {
	        return firstNode == null;
	    }
	   
	    public function enqueue(object:Object):void 
	    {
	        var node:QueueNode = new QueueNode();
	        node.object = object;
	        node.next = null;
	        if (isEmpty()) {
	            firstNode = node;
	            lastNode = node;
	        } else {
	            lastNode.next = node;
	            lastNode = node;
	        }
	    }
    
	    public function dequeue():Object {
	        if ( isEmpty() ) {
	            return null;
	        }
	        var object:Object = firstNode.object;
	        firstNode = firstNode.next;
	        return object;
	    }
	    
	    public function peek():Object
	    {
	        if ( isEmpty() ) {
	            return null;
	        }
	        return firstNode.object;
	    }
	}
}
		
