package com.nts.ams.api.controller.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import phn.com.nts.db.entity.AmsCashflow;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsDepositRef;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.common.StringUtil;
import phn.com.trs.util.common.ITrsConstants.BJP_CONFIG;
import phn.com.trs.util.file.FileLocker;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.common.SystemPropertyConfig;
import phn.nts.ams.fe.domain.BjpDepositInfo;
import phn.nts.ams.fe.domain.CustomerInfo;

import com.nts.ams.api.controller.common.Constant;

public class BjpLoadFileTimer {
private static Logger log = Logger.getLogger(BjpLoadFileTimer.class);
	
	private static final String APP_PROPS_FILE = "configs.properties";
	private static final String BJP_FOLDER_KEY = "bjpKeshikomiSourceFolder";
	private static final String BJP_BACKUP_FOLDER_KEY = "bjpKeshikomiTargetFolder";
	private static final String BJP_TIME_KEY = "internalTimeCheckKeshikomi";
	private static final String sessionCheckingInMilitime = "internalTimeLog";
	
	private static volatile Properties propsConfig;
	private static volatile String bjpFolder;
	private static volatile String bjpBackupFolder;
	private static volatile int TIME_IN_MILLISECOND_DEFAULT = 300000;
	private static volatile int COUNT_TIME_INTEVAL_IN_MILISECOND_DEFAULT=300000;
	@SuppressWarnings("unused")
	private static volatile int COUNT_TIME_INTEVAL_IN_MILISECOND=300000;
	private static int BJP_FILE_LOCKER_TIMEOUT_IN_MILLIS = 300000; // 5 minutes
	
	private static volatile int timeRecheckFileInMillSecond;
	private static volatile File sourceFolder;
	private static volatile File targetFolder;
	private static volatile IDepositManager depositManager;
	private static volatile IAccountManager accountManager;
	private static final String KESHIKOMI_ORIGINAL_FILE_KEY ="KESHIKOMI_ORIGINAL_FILE";
	private static String KESHIKOMI_ORIGINAL_FILE_VALUE ="";
	private static String bjpDeposit = "";
	
	public void bjpInitialized() {
		try {
			bjpDeposit = SystemPropertyConfig.getInstance().getText(Constant.BJP_DEPOSIT);
			propsConfig = Helpers.getProperties(APP_PROPS_FILE);
			bjpFolder = propsConfig.getProperty(BJP_FOLDER_KEY);
			bjpBackupFolder = propsConfig.getProperty(BJP_BACKUP_FOLDER_KEY);
			KESHIKOMI_ORIGINAL_FILE_VALUE = propsConfig.getProperty(KESHIKOMI_ORIGINAL_FILE_KEY);
			try {
				timeRecheckFileInMillSecond = Integer.parseInt(propsConfig.getProperty(BJP_TIME_KEY));
			} catch (Exception e) {
				log.error("KESHIKOMI  Could not time interval for bjp check upload file from load configuration file from: " + APP_PROPS_FILE, e);
				timeRecheckFileInMillSecond = TIME_IN_MILLISECOND_DEFAULT;
			}
			try {
				COUNT_TIME_INTEVAL_IN_MILISECOND=  Integer.parseInt(propsConfig.getProperty(sessionCheckingInMilitime));
			} catch (Exception e) {
				log.error("KESHIKOMI  Could not time interval for bjp check upload file from load configuration file from: " + APP_PROPS_FILE, e);
				COUNT_TIME_INTEVAL_IN_MILISECOND = COUNT_TIME_INTEVAL_IN_MILISECOND_DEFAULT;
			}
			sourceFolder = new File(bjpFolder);
			if (!sourceFolder.exists()) {
				sourceFolder.mkdirs();
			}
			targetFolder = new File(bjpBackupFolder);
			if (!targetFolder.exists()) {
				targetFolder.mkdirs();
			}
			Timer timer = new Timer("Start Timer Upload BJP KESHIKOMI ");
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					startRunUploadBjpFile();
				}

			}, 0, timeRecheckFileInMillSecond);
			
		} catch (Exception e) {
			log.error("KESHIKOMI Could not load configuration file from: " + APP_PROPS_FILE, e);
			return;
		}

	}
	
	private static synchronized void startRunUploadBjpFile() {
		try{
			if (sourceFolder.isDirectory()) {
				String files[] = sourceFolder.list();
				if(files.length==0){
					log.info("KESHIKOMI sourceFolder is empty ");
				}
				FileLocker locker = new FileLocker(BJP_FILE_LOCKER_TIMEOUT_IN_MILLIS);
				for (String file : files) {
					if(file.endsWith(".lock")) continue;
					File srcFile = new File(sourceFolder, file);
					try {
						if(!locker.getLock(srcFile)) continue;
						log.info("start read KESHIKOMI filename= "+file);
						List<BjpDepositInfo> listDeposit = new ArrayList<BjpDepositInfo>();
						String date = DateUtil.getCurrentDateTime(DateUtil.PATTERN_YYYYMMDD_BLANK);
						try{
							//check run file or data file start
							InputStream inStreamchk = new FileInputStream(srcFile);
		 				    BufferedReader reader = new BufferedReader(new InputStreamReader(inStreamchk));
		 				    String firstLine = reader.readLine();
		 				    
			 				//[PROD-255-ThinhPH]Jan 11, 2016M - Start
		 				    if(srcFile.length() == 0L){
		 				    	log.warn("File " + file + " is empty");
		 				    	continue;
		 				    }
		 				    
		 				    if(firstLine == null){
		 				    	log.warn("First line of file " + file + " is empty");
		 				    	continue;
		 				    }
			 				//[PROD-255-ThinhPH]Jan 11, 2016M - End
		 				    
		 				    reader.close();
							inStreamchk.close();
		 				    if(!StringUtil.isEmpty(KESHIKOMI_ORIGINAL_FILE_VALUE) && firstLine.startsWith(KESHIKOMI_ORIGINAL_FILE_VALUE)){
		 				    	continue;
		 				    }
		 				   
							//check run file or data file end
						}catch(Exception ex){
							log.error("BJP KESHIKOMI"+ex.getMessage(), ex);
						}
						File destFileFolder = new File(bjpBackupFolder+ File.separator+date);
						
						if (!destFileFolder.exists()) {
							destFileFolder.mkdirs();
						}
						
						File destFile = new File(destFileFolder,file);
						
						// Start Update database
						InputStream inStream = new FileInputStream(srcFile);
						
						byte[] record = new byte[250];
						while ((inStream.read(record)) > 0) {
				
							String rc = new String(record, BJP_CONFIG.Charset_Shift_JIS);
							
							if (rc.length() == 250) {
								BjpDepositInfo bdi = new BjpDepositInfo(rc);
								if(bdi.isDataRecordCheck()){
									listDeposit.add(bdi);
								}
							}
						}
						inStream.close();
						
						// End Update database
						if (listDeposit.size() > 0) {
							List<BjpDepositInfo> listDepositWarning =	manipulateWithRecord(listDeposit, file);
							if(listDepositWarning.size()>0){
								getDepositManager().sendMailBjpWarning(listDepositWarning,file);	
							}
						}
						// Start Move File
						InputStream in = new FileInputStream(srcFile);
						OutputStream out = new FileOutputStream(destFile);
						byte[] buffer = new byte[1024];
						int length;
						// copy the file content in bytes
						while ((length = in.read(buffer)) > 0) {
							out.write(buffer, 0, length);
						}
	
						in.close();
						out.close();
						srcFile.delete();
						// End Move File
					} catch (FileNotFoundException e) {
						log.error("KESHIKOMI  "+e.getMessage(), e);
					} catch (Exception e) {
						log.error("KESHIKOMI  "+e.getMessage(), e);
					}finally{
						locker.releaseLock(srcFile);
					}
				}
			}
			
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}

	private static List<BjpDepositInfo> manipulateWithRecord(List<BjpDepositInfo> listDeposit, String csvFileName) {
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		List<BjpDepositInfo> listDepositWarning = new ArrayList<BjpDepositInfo>();
		for (int i = 0; i < listDeposit.size(); i++) {
			BjpDepositInfo element = listDeposit.get(i);
			String depId = element.getDEPOSIT_ID();
			Integer value = counter.get(depId);
			if(value==null){
				value=1;
			}else{
				value++;
			}
			counter.put(depId, value);
		}
		for (int i = 0; i < listDeposit.size(); i++) {
			log.info(listDeposit.get(i).toString());
			// X = 0 update some field and send mail to accounting
			// 0 = 1 update some field
			// US =2 success
			// UF =3 fail
			int action = -1;
			BjpDepositInfo element = listDeposit.get(i);
			String depId = element.getDEPOSIT_ID();
			Integer count = counter.get(depId);
			if(count.intValue()!=1){
				log.warn("KESHIKOMI  DUPLICATE DEPOSITID  = " + depId + "("+count+")");
				listDepositWarning.add(element);
				continue;
			}
			AmsDeposit bjpDep = getDepositManager().getBjpDeposit(depId);
			
			if (bjpDep == null) {
				log.warn("KESHIKOMI  DEPOSIT ID = " + depId + " NOT EXIST");
				listDepositWarning.add(element);
				continue;
			}else{
				if(Constant.SERVICE_AMS.compareTo(bjpDep.getServiceType()) != 0){
					log.warn("KESHIKOMI  DEPOSIT NOT AMS");
					listDepositWarning.add(element);
					continue;
				}
				AmsDepositRef ref = bjpDep.getAmsDepositRef();
				if (ref == null) {
					log.warn("KESHIKOMI DEPOSIT ID = " + depId + " NOT EXIST IN AMS_DEPOSIT_REF");
					listDepositWarning.add(element);
					continue;
				}
				ref.setBeneficiaryAccountName(element.getACCOUNT_NAME_KANA());
				ref.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				depositManager.keshicomiUpdateDepositRef(ref);
			}
			if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_HOLDER.equalsIgnoreCase(element.getCHECK_MEIGI_STAT())||"4".equalsIgnoreCase(element.getCHECK_MEIGI_STAT())
					||"5".equalsIgnoreCase(element.getCHECK_MEIGI_STAT())||"6".equalsIgnoreCase(element.getCHECK_MEIGI_STAT())||bjpDep.getStatus().intValue() == BJP_CONFIG.DEPOSIT_STATUS_CANCEL) {
				action = 0;//update some field and send mail to accounting
			} else if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_SUCCESS.equalsIgnoreCase(element.getCHECK_MEIGI_STAT())) {
				if (bjpDep.getStatus().intValue() == BJP_CONFIG.DEPOSIT_STATUS_SUCCESS || bjpDep.getStatus().intValue() == BJP_CONFIG.DEPOSIT_STATUS_FAIL) {
					action = 1;//update some field
				}else if (bjpDep.getStatus().intValue() == BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS ) {
					if(bjpDep.getDepositAmount()!=element.getDEPOSIT_AMOUNT()){
						listDepositWarning.add(element);
						log.warn("KESHIKOMI AMOUNT NOT EQUAL DEPID="+depId);
						continue;
					}else{
						action = 2;// success
					}
				}
			} else if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_FAIL.equalsIgnoreCase(element.getCHECK_MEIGI_STAT())) {
				if (bjpDep.getStatus().intValue() == BJP_CONFIG.DEPOSIT_STATUS_SUCCESS) {
					action = 0;//update some field and send mail to accounting
				}else if (bjpDep.getStatus().intValue() == BJP_CONFIG.DEPOSIT_STATUS_FAIL) {
					action = 1;//update some field
				}else if (bjpDep.getStatus().intValue() == BJP_CONFIG.DEPOSIT_STATUS_INPROGRESS) {
					action = 3;// fail
				}
			} 

			switch (action) {
			case 0: // update some field and send mail to accounting
				getDepositManager().updateBjpUploadDepositFail(bjpDep.getDepositId(), element.getCHECK_MEIGI_STAT(), csvFileName);
//				listDepositWarning.add(element);
				break;
			case 1: //1 update some field
				getDepositManager().updateBjpUploadDepositFail(bjpDep.getDepositId(), element.getCHECK_MEIGI_STAT(), csvFileName);
				break;
			case 2:// success 4.1 2
				String customerId = bjpDep.getAmsCustomer().getCustomerId();
				CustomerInfo customer = getAccountManager().getCustomerInfo(customerId);
				AmsCashflow cashflow = new AmsCashflow();
				AmsCustomer cus = new AmsCustomer();
				cus.setCustomerId(customerId);
				cashflow.setAmsCustomer(cus);
				cashflow.setCashflowType(1);
				cashflow.setCashflowAmount(element.getDEPOSIT_AMOUNT());
				cashflow.setCurrencyCode(customer.getCurrencyCode());
				cashflow.setRate(1d);
				cashflow.setSourceType(1);
				cashflow.setSourceId(element.getDEPOSIT_ID());
				cashflow.setActiveFlg(1);
				cashflow.setServiceType(0);
				depositManager.insertBjpCashFlow(cashflow);
				depositManager.updateBjpCashBalance(customerId, customer.getCurrencyCode(), 0, element.getDEPOSIT_AMOUNT(), 0d);
				getDepositManager().updateBjpUploadDeposit(bjpDep.getDepositId(), element.getCHECK_MEIGI_STAT(), csvFileName);
				
				//[TRSPT-7201-quyen.le.manh]Jan 4, 2016D - Start: only send mail to customer when deposit status changed
				try {
					bjpDep = depositManager.getBjpDeposit(depId);
					depositManager.sendMailBjpDeposit(bjpDep, BJP_CONFIG.DEPOSIT_STATUS_SUCCESS, bjpDeposit);
				} catch (Exception e) {
					log.error("KESHIKOMI " + e.getMessage());
				}
				//[TRSPT-7201-quyen.le.manh]Jan 4, 2016D - End
				break;
			case 3:// fail UF
				getDepositManager().updateBjpUploadDepositFail(bjpDep.getDepositId(), element.getCHECK_MEIGI_STAT(), csvFileName);
				
				//[TRSPT-7201-quyen.le.manh]Jan 4, 2016D - Start: only send mail to customer when deposit status changed
				//[NTS1.0-quyen.le.manh]Jan 4, 2016A - End
				try {
					bjpDep = depositManager.getBjpDeposit(depId);
					depositManager.sendMailBjpDeposit(bjpDep, BJP_CONFIG.DEPOSIT_STATUS_FAIL, bjpDeposit);
				} catch (Exception e) {
					log.error("KESHIKOMI " + e.getMessage());
				}
				//[TRSPT-7201-quyen.le.manh]Jan 4, 2016D - End
				break;
			default:
				break;
			}
			
			try {
				//[TRSPT-7201-quyen.le.manh]Jan 4, 2016D - Start: only send mail to customer when deposit status changed
				//send mail to customer
//				bjpDep = depositManager.getBjpDeposit(depId);
//				if(bjpDep.getStatus() == BJP_CONFIG.DEPOSIT_STATUS_SUCCESS){
//					depositManager.sendMailBjpDeposit(bjpDep, BJP_CONFIG.DEPOSIT_STATUS_SUCCESS, bjpDeposit);
//				}else if(bjpDep.getStatus() == BJP_CONFIG.DEPOSIT_STATUS_FAIL){
//					depositManager.sendMailBjpDeposit(bjpDep, BJP_CONFIG.DEPOSIT_STATUS_FAIL, bjpDeposit);
//				}
				//[TRSPT-7201-quyen.le.manh]Jan 4, 2016D - End
				
				//send mail to accountant, admin
				if(BJP_CONFIG.DEPOSIT_MEIGI_STAT_HOLDER.equalsIgnoreCase(element.getCHECK_MEIGI_STAT())
						|| "4".equalsIgnoreCase(element.getCHECK_MEIGI_STAT())
						|| "5".equalsIgnoreCase(element.getCHECK_MEIGI_STAT())
						|| "6".equalsIgnoreCase(element.getCHECK_MEIGI_STAT())){
					listDepositWarning.add(element);
				}else if("3".equalsIgnoreCase(element.getCHECK_MEIGI_STAT())
						&& (bjpDep.getStatus() == BJP_CONFIG.DEPOSIT_STATUS_SUCCESS || bjpDep.getStatus() == BJP_CONFIG.DEPOSIT_STATUS_CANCEL)){
					listDepositWarning.add(element);
				}else if(BJP_CONFIG.DEPOSIT_MEIGI_STAT_SUCCESS.equalsIgnoreCase(element.getCHECK_MEIGI_STAT())
						&& bjpDep.getStatus() == BJP_CONFIG.DEPOSIT_STATUS_CANCEL){
					listDepositWarning.add(element);
				}
			} catch (Exception e) {
				log.error("KESHIKOMI not send mail "+e.getMessage());
			}
		}
		return listDepositWarning;
	}

	public static IDepositManager getDepositManager() {
		return depositManager;
	}

	public static void setDepositManager(IDepositManager depositManager) {
		BjpLoadFileTimer.depositManager = depositManager;
	}

	public static IAccountManager getAccountManager() {
		return accountManager;
	}

	public static void setAccountManager(IAccountManager accountManager) {
		BjpLoadFileTimer.accountManager = accountManager;
	}
	
	
}
