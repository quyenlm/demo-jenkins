package com.nts.ams.api.controller.service;

import com.nts.components.thread.ServerSocketCtrl;

/**
 * @author quyen.le.manh
 */
public class ServerSocketThread extends ServerSocketCtrl {

	public ServerSocketThread() {
	}

	@Override
	public void startProcessing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pasueProcessing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopProcessing() {
		AmsApiControllerStartup.stop();
		System.exit(0);
	}
}