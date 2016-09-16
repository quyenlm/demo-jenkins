/**
 * 
 */
package phn.nts.ams.fe.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import phn.com.nts.util.log.Logit;

/**
 * @author tungpv
 *
 */
public class CustomizeEncodingCharacter extends org.springframework.web.filter.CharacterEncodingFilter{
	private static Logit log = Logit.getInstance(CustomizeEncodingCharacter.class);
	/* (non-Javadoc)
	 * @see org.springframework.web.filter.OncePerRequestFilter#shouldNotFilter(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request)
			throws ServletException {
		// TODO Auto-generated method stub
		log.info("shouldNotFilter BJP response CharacterEncoding = "+	request.getCharacterEncoding());
		String url = request.getRequestURL().toString();
		String success = "deposit/successfull";
		String error = "deposit/error";
		if (url.indexOf(success)>0||url.indexOf(error)>0) {
			log.info("IN SHOULD NOT FILTER");
			log.info("url="+url);
		    return true;
		}
		return super.shouldNotFilter(request);
	}
	
}
