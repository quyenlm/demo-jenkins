/**
 * 
 */
package phn.nts.ams.fe.security;

import java.util.TimerTask;

import phn.com.nts.util.log.Logit;

/**
 * @author tungpv
 *
 */
public class CountTask extends TimerTask {
	public static volatile int count = 0;
	private static Logit log = Logit.getInstance(CountTask.class);
    public void run() {
    	StringBuilder sb= new StringBuilder("[OM] - Have ");
    	sb.append(count);
    	sb.append(" sessions online");
        log.info(sb.toString());
     }
 }

