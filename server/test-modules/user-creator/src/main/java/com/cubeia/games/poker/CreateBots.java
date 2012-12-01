package com.cubeia.games.poker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

public class CreateBots {
	
	public static void main(String[] args) {
		for (String s : args) {
			System.out.println("L: " + s);
		}
		CreateBots action = new CreateBots();
		try {
			Args.parse(action, args);
			action.execute();
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			Args.usage(action);
		} catch(Exception e) {
			System.out.println("!!! FAILURE !!!");
			e.printStackTrace();
		}
	}
	
	@Argument(alias="us", description="user service URL, defaults to http://localhost:8080/user-service-rest/rest", required=false)
	private String userService = "http://localhost:8080/user-service-rest/rest";
	
	@Argument(alias="ws", description="wallet service URL, defaults to http://localhost:8080/wallet-service-rest/rest", required=false)
	private String walletService = "http://localhost:8080/wallet-service-rest/rest";
	
	@Argument(alias="i", description="initial balance, set to -1 to disable, defaults to 50000", required=false)
	private Long balance = 50000L;
	
	@Argument(alias="b", description="bank account for initial balance, defaults to -3000", required=false)
	private Long bankaccount = -3000L;
	
	@Argument(alias="n", description="number of bots to create, mandatory", required=true)
	private Integer numberOfBots;
	
	@Argument(alias="s", description="start id for the bots, defaults to 1", required=false)
	private Integer startId = 1;
	
	@Argument(alias="t", description="number of threads to use, defaults to 1", required=false)
	private Integer threads = 1;
	
	@Argument(alias="c", description="currency, defaults to EUR", required=false)
	private String currency = "EUR";
	
	public void execute() throws Exception {
		Connector con = new Connector(userService, walletService);
		ExecutorService exec = Executors.newFixedThreadPool(threads);
		for (int i = startId; i < (startId + numberOfBots); i++) {
			exec.submit(new Task(i, con));
		}
	}
	
	private class Task implements Runnable {
		
		private final int botId;
		private final Connector con;

		public Task(int botId, Connector con) {
			this.botId = botId;
			this.con = con;
		}
		
		@Override
		public void run() {
			Request req = new Request("Bot_" + botId, String.valueOf(botId));
			req.setBalance(balance);
			req.setBankaccount(bankaccount);
			req.setCurrency(currency);
			try {
				con.createUser(req);
			} catch (Exception e) {
				throw new RuntimeException("Failed to create bot", e);
			}
		}
	}
}
