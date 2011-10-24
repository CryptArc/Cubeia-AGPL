package com.cubeia.poker.variant.telesina;

import static com.cubeia.poker.variant.telesina.TelesinaDeckUtil.createDeckCards;
import junit.framework.Assert;

import org.junit.Test;

public class TelesinaDeckUtilTest {

	@Test
	public void checkDeckSize() {
		Assert.assertEquals(32, createDeckCards(4).size());
		Assert.assertEquals(36, createDeckCards(5).size());
		Assert.assertEquals(40, createDeckCards(6).size());
		Assert.assertEquals(52, createDeckCards(10).size());
	}
}
