package com.cubeia.backend.firebase;

import com.cubeia.backend.cashgame.CashGamesBackend;
import com.cubeia.firebase.api.action.service.ServiceAction;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.RoutableService;
import com.cubeia.firebase.api.service.Service;
import com.cubeia.firebase.api.service.ServiceContext;
import com.cubeia.firebase.api.service.ServiceRouter;
import com.cubeia.network.wallet.firebase.api.WalletServiceContract;

public class CachGamesBackendServiceImpl extends CashGamesBackendServiceBase implements CashGamesBackendService, Service, RoutableService {

	private CashGamesBackendAdapter adapter;
	private ServiceRouter router;

	public CachGamesBackendServiceImpl() {
		super(20, 500);
	}

	@Override
	protected CashGamesBackend getCashGamesBackend() {
		return adapter;
	}

	@Override
	protected ServiceRouter getServiceRouter() {
		return router;
	}

	@Override
	public void init(ServiceContext con) throws SystemException {
		WalletServiceContract walletService = con.getParentRegistry().getServiceInstance(WalletServiceContract.class);
		adapter = new CashGamesBackendAdapter(walletService, new AccountLookupUtil());
	}

	@Override
	public void setRouter(ServiceRouter router) {
		this.router = router;
	}
	
	@Override
	public void onAction(ServiceAction e) { }

	@Override
	public void destroy() { }

	@Override
	public void start() { }
 
	@Override
	public void stop() { }
	
}
