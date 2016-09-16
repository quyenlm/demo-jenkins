/**
 * 
 */
package phn.nts.ams.fe.common;

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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import phn.com.nts.db.entity.AmsCashflow;
import phn.com.nts.db.entity.AmsCustomer;
import phn.com.nts.db.entity.AmsDeposit;
import phn.com.nts.db.entity.AmsDepositRef;
import phn.com.nts.util.common.DateUtil;
import phn.com.nts.util.common.Helpers;
import phn.com.trs.util.common.ITrsConstants.BJP_CONFIG;
import phn.com.trs.util.file.FileLocker;
import phn.nts.ams.fe.business.IAccountManager;
import phn.nts.ams.fe.business.IDepositManager;
import phn.nts.ams.fe.domain.BjpDepositInfo;
import phn.nts.ams.fe.domain.CustomerInfo;
import phn.nts.ams.fe.security.CountThreadLog;

/**
 * @author tungpv
 * 
 */
public class BjpLoadFileListener implements ServletContextListener {

	private static Logger log = Logger.getLogger(BjpLoadFileListener.class);
	
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
	private static volatile int COUNT_TIME_INTEVAL_IN_MILISECOND=300000;
	//[TRSPT-4008-chien.nghe.xuan]Mar 25, 2015A - Start 
	private static int BJP_FILE_LOCKER_TIMEOUT_IN_MILLIS = 300000; // 5 minutes
	//[TRSPT-4008-chien.nghe.xuan]Mar 25, 2015A - End
	
	
	private static volatile int timeRecheckFileInMillSecond;
	private static volatile WebApplicationContext ctx;
	private static volatile File sourceFolder;
	private static volatile File targetFolder;
	private static volatile IDepositManager depositManager;
	private static volatile IAccountManager accountManager;
	private static final String KESHIKOMI_ORIGINAL_FILE_KEY ="KESHIKOMI_ORIGINAL_FILE";
	private static String KESHIKOMI_ORIGINAL_FILE_VALUE ="";
	CountThreadLog countThreadLog ;
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
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
			countThreadLog = new CountThreadLog(COUNT_TIME_INTEVAL_IN_MILISECOND);
			sourceFolder = new File(bjpFolder);
			if (!sourceFolder.exists()) {
				sourceFolder.mkdirs();
			}
			targetFolder = new File(bjpBackupFolder);
			if (!targetFolder.exists()) {
				targetFolder.mkdirs();
			}
			ctx = ContextLoader.getCurrentWebApplicationContext();
			if (ctx != null) {
				depositManager = (IDepositManager) ctx.getBean("DepositManager");
				accountManager = (IAccountManager) ctx.getBean("AccountManager");
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
		// [TRSPT-4008-chien.nghe.xuan]Mar 25, 2015M - Start
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
						// [TRSPT-4008-chien.nghe.xuan]Mar 25, 2015M - End
	//					if(file.endsWith(".txt")||file.endsWith(".TXT")){
						log.info("start read KESHIKOMI filename= "+file);
						List<BjpDepositInfo> listDeposit = new ArrayList<BjpDepositInfo>();
						String date = DateUtil.getCurrentDateTime(DateUtil.PATTERN_YYYYMMDD_BLANK);
						try{
							//check run file or data file start
							InputStream inStreamchk = new FileInputStream(srcFile);
		 				    BufferedReader reader = new BufferedReader(new InputStreamReader(inStreamchk));
		 				    String firstLine = reader.readLine();
		 				    reader.close();
							inStreamchk.close();
		 				    if(KESHIKOMI_ORIGINAL_FILE_VALUE!=null&&KESHIKOMI_ORIGINAL_FILE_VALUE!=""&&firstLine.startsWith(KESHIKOMI_ORIGINAL_FILE_VALUE)){
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
	//					}else{
	//						log.error("Format of file Keshikomi is not correct!");
	//						log.error("KESHIKOMI filename= "+file);
	//						getDepositManager().sendMailBjpWarning(null,file);	
	//						continue;
	//					}
					} catch (FileNotFoundException e) {
						log.error("KESHIKOMI  "+e.getMessage(), e);
					} catch (Exception e) {
						log.error("KESHIKOMI  "+e.getMessage(), e);
					// [TRSPT-4008-chien.nghe.xuan]Mar 25, 2015A - Start
					}finally{
						locker.releaseLock(srcFile);
					}
				}
			}
			
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		// [TRSPT-4008-chien.nghe.xuan]Mar 25, 2015M - End
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
			// X = 0 update some field and send mail to accounting
			// 0 = 1 update some field
			// US =2 success
			// UF =3 fail
			int action = -1;
			BjpDepositInfo element = listDeposit.get(i);
			String depId = element.getDEPOSIT_ID();
			Integer count = counter.get(depId);
			if(count.intValue()!=1){
				log.error("KESHIKOMI  DUPLICATE DEPOSITID  = " + depId + "("+count+")");
				listDepositWarning.add(element);
				continue;
			}
			AmsDeposit bjpDep = getDepositManager().getBjpDeposit(depId);
			if (bjpDep == null) {
				log.error("KESHIKOMI  DEPOSIT ID = " + depId + " NOT EXIST");
				listDepositWarning.add(element);
				continue;
			}else{
				AmsDepositRef ref = bjpDep.getAmsDepositRef();
				ref.setBeneficiaryAccountName(element.getACCOUNT_NAME_KANA());
				ref.setUpdateDate(new Timestamp(System.currentTimeMillis()));
				depositManager.keshicomiUpdateDepositRef(ref);
			}
//			if (KanaUtil.toHankanaCase(bjpDep.getAmsDepositRef().getBeneficiaryAccountNameKana()) .equalsIgnoreCase(element.getACCOUNT_NAME_KANA())||bjpDep.getAmsDepositRef().getBeneficiaryAccountNameKana() .equalsIgnoreCase(element.getACCOUNT_NAME_KANA())||KanaUtil.toHankanaCase(bjpDep.getAmsDepositRef().getBeneficiaryAccountNameKana()) .equalsIgnoreCase(KanaUtil.toHankanaCase(element.getACCOUNT_NAME_KANA()))) {
//			}else{
//				listDepositWarning.add(element);
//				log.error(" name in DATABASE="+ bjpDep.getAmsDepositRef().getBeneficiaryAccountNameKana());
//				log.error(" name in file KESHIKOMI="+ element.getACCOUNT_NAME_KANA());
//				log.error(" name in DATABASE after using kanaUtil.toHankanaCase="+ KanaUtil.toHankanaCase(bjpDep.getAmsDepositRef().getBeneficiaryAccountNameKana()));
//				log.error(" name in file KESHIKOMI after using kanaUtil.toHankanaCase="+ KanaUtil.toHankanaCase(element.getACCOUNT_NAME_KANA()));
//				log.error("KESHIKOMI  ACCOUNT_NAME_KANA   NOT MAPPING DEPID = "+ depId);
//				continue;
//				
//			}
			if (BJP_CONFIG.DEPOSIT_MEIGI_STAT_HOLDER.equalsIgnoreCase(element.getCHECK_MEIGI_STAT())||"4".equalsIgnoreCase(element.getCHECK_MEIGI_STAT())||"5".equalsIgnoreCase(element.getCHECK_MEIGI_STAT())||"6".equalsIgnoreCase(element.getCHECK_MEIGI_STAT())||bjpDep.getStatus().intValue() == BJP_CONFIG.DEPOSIT_STATUS_CANCEL) {
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
				listDepositWarning.add(element);
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
				// have not tranID
//				getDepositManager().updateBjpDepositRef(bjpDep.getDepositId(), element.getEdiInformation(), "");
				try {
					bjpDep = depositManager.getBjpDeposit(depId);
					depositManager.sendMailBjpDeposit(bjpDep, BJP_CONFIG.DEPOSIT_STATUS_SUCCESS, "Bjp deposit");
				} catch (Exception e) {
					log.error("KESHIKOMI  "+e.getMessage());
				}
				break;
			case 3:// fail UF
				getDepositManager().updateBjpUploadDepositFail(bjpDep.getDepositId(), element.getCHECK_MEIGI_STAT(), csvFileName);
				try {
					bjpDep = depositManager.getBjpDeposit(depId);
					depositManager.sendMailBjpDeposit(bjpDep, BJP_CONFIG.DEPOSIT_STATUS_FAIL, "Bjp deposit");
				} catch (Exception e) {
					log.error("KESHIKOMI  "+e.getMessage());
				}
				break;
			default:
				break;
			}
		}
		return listDepositWarning;
	}

	private static IDepositManager getDepositManager() {
		if (ctx == null) {
			ctx = ContextLoader.getCurrentWebApplicationContext();
		}
		if (ctx != null && depositManager == null) {
			depositManager = (IDepositManager) ctx.getBean("DepositManager");
		}
		return depositManager;
	}

	private static IAccountManager getAccountManager() {
		if (ctx == null) {
			ctx = ContextLoader.getCurrentWebApplicationContext();
		}
		if (ctx != null && accountManager == null) {
			accountManager = (IAccountManager) ctx.getBean("AccountManager");
		}
		return accountManager;
	}
}
