package com.nts.ams.api.controller.test;

import java.text.ParseException;
import java.util.Scanner;

import javax.jms.JMSException;

import com.nts.common.exchange.proto.ams.internal.AmsCustomerinfoModel.AmsCustomerInfo;

/**
 * @description
 * @version NTS
 * @author quyen.le.manh
 * @CrDate Jan 19, 2015
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class ApiControllerTestCommand implements Runnable {
	
	public static void main(String[] args) throws JMSException, ParseException {
		new Thread(new ApiControllerTestCommand()).start();
	}
	
	@Override
	public void run() {
		try {
			ApiControllerTestImpl apiControllerTestImpl = new ApiControllerTestImpl(ApiControllerTestImpl.QUEUE_CUSTOMER_REQUEST, ApiControllerTestImpl.TOPIC_CUSTOMER_RESPONSE);
	    	apiControllerTestImpl.startConnection();
	    	
	    	ApiControllerTestImpl txApiControllerTestImpl = new ApiControllerTestImpl(ApiControllerTestImpl.QUEUE_TRANSACTION_REQUEST, ApiControllerTestImpl.TOPIC_TRANSACTION_RESPONSE);
	    	txApiControllerTestImpl.startConnection();
			
	    	ApiControllerTestImpl.printHelp();
			
			Scanner scaner = new Scanner(System.in);
			String[] arrParam;
			while(true) {
				arrParam = scaner.nextLine().split(" ", -1);
				if(arrParam[0].equals("help")) {
					ApiControllerTestImpl.printHelp();
					continue;
				} else if(arrParam[0].equals("exit")) {
					break;
				}
				
				if(arrParam.length > 1) {
					//Send request
					if(arrParam[0].equalsIgnoreCase("getcus")) {
						//get customer
						apiControllerTestImpl.getAmsCustomerInfo(arrParam[1], arrParam.length > 2 ? arrParam[2] : null);
					} else if(arrParam[0].equalsIgnoreCase("getbl")) {
						//Get Balance
						apiControllerTestImpl.getAmsCustomerBalance(arrParam[1]).toByteString();
					} else if(arrParam[0].equalsIgnoreCase("upcus")) {
						AmsCustomerInfo amsCustomerInfo = apiControllerTestImpl.getAmsCustomerInfo(arrParam[1], "TRS");
						
						if(amsCustomerInfo != null) {
							//Clear pass before update
							apiControllerTestImpl.updateAmsCustomerInfo(amsCustomerInfo.toBuilder().clearPassword().build()).toByteString();
						}
					} else if(arrParam[0].equalsIgnoreCase("botest")) {
						//BoTestUpdate
						apiControllerTestImpl.updateAmsCustomerBoTest(arrParam[1]).toByteString();
					} else if(arrParam[0].equalsIgnoreCase("gnew")) {
						//News Agreement
						apiControllerTestImpl.getAmsCustomerNews(arrParam[1]).toByteString();
					} else if(arrParam[0].equalsIgnoreCase("addbo")) {
						//News Agreement
						apiControllerTestImpl.updateAmsBoAdditionalInfo(arrParam[1]).toByteString();
					} else if(arrParam[0].equalsIgnoreCase("upAgree")) {
						//News Agreement
						apiControllerTestImpl.updateAmsCustomerNews(arrParam[1]).toByteString();
					}  else if(arrParam[0].equalsIgnoreCase("wd")) {
						//withDrawal
						txApiControllerTestImpl.requestAmsWithdrawal(arrParam[1]).toByteString();
					} else if(arrParam[0].equalsIgnoreCase("tf")) {
						//Transfer
						txApiControllerTestImpl.requestAmsTransfer(arrParam[1]);
					} else if(arrParam[0].equalsIgnoreCase("getpm")) {
						//Get payment method
						txApiControllerTestImpl.getAmsCustomerPaymentInfo(arrParam[1]).toByteString();
					} else if (arrParam[0].equalsIgnoreCase("reportbo")) {
						//Get report from BO database
						apiControllerTestImpl.getReportBo();
					} else if (arrParam[0].equalsIgnoreCase("reportsc")) {
						//Get report from BO database
						apiControllerTestImpl.getReportSc();
					} else if (arrParam[0].equalsIgnoreCase("msc")) {
						apiControllerTestImpl.modifyAmsCustomerSocial(arrParam[1]);
					}
				} else
					System.out.println("Not valid param");
			}
			scaner.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}
}