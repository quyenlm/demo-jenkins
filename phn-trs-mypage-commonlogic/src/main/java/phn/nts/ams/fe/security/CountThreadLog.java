/**
 * 
 */
package phn.nts.ams.fe.security;

/**
 * @author tungpv
 *
 */

import java.util.Timer;
public class CountThreadLog {
	Timer timer;
    public CountThreadLog(int miliSeconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new CountTask(),0 ,miliSeconds);
	}
   
}