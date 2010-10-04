package com.cubeia.poker.ui
{
	import com.cubeia.poker.config.PokerConfig;
	import com.cubeia.poker.event.PokerEventDispatcher;
	import com.cubeia.poker.event.StyleChangedEvent;
	
	import mx.core.Application;
	import mx.styles.IStyleManager2;
	import mx.styles.StyleManager;

	public class StyledApplication extends Application
	{
		private static var _currentStyle:String;
		
		public function StyledApplication()
		{
			super();
			loadDefaultStyle();
		}
		
		
		/**
		 * Change current style
		 * propagates globally by default through the message bus
		 */ 
		public static function changeStyle(stylename:String, global:Boolean = true):void
		{
			PokerEventDispatcher.dispatch(new StyleChangedEvent(stylename), global);
		}
		
		private function loadDefaultStyle():void
		{
			// Wait for configuration to load
			if ( PokerConfig.getInstance().configLoaded == false ) {
				callLater(loadDefaultStyle);
				return;
			}
			// add event listener
			PokerEventDispatcher.instance.addEventListener(StyleChangedEvent.STYLE_CHANGED_EVENT, onStyleChanged);
			
			changeStyle(PokerConfig.getInstance().defaultStyleName, false);
		}

		private function onStyleChanged(event:StyleChangedEvent):void {
			var styleManager:IStyleManager2 = StyleManager.getStyleManager(null);
			if ( styleManager != null && _currentStyle != null ) {
				styleManager.unloadStyleDeclarations(_currentStyle);
			}
			_currentStyle = "styles/" + event.styleName +"/poker.swf";
			styleManager.loadStyleDeclarations2(_currentStyle, true);

		}

	}
}