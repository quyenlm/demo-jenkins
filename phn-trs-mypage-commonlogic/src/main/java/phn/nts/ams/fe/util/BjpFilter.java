package phn.nts.ams.fe.util;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet Filter implementation class BjpFilter
 */
public class BjpFilter implements Filter {
	String bjpSuccess = "deposit/successful";
	String bjpError = "deposit/error";
    /**
     * Default constructor. 
     */
    public BjpFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		String url = httpRequest.getRequestURL().toString();
		if (url.indexOf(bjpSuccess)>0||url.indexOf(bjpError)>0) {
			try {
				request.setCharacterEncoding("Shift-JIS");
				String PORTAL_CODE= httpRequest.getParameter("PORTAL_CODE");
				String SHOP_CODE= httpRequest.getParameter("SHOP_CODE");
				String BANK_CODE= httpRequest.getParameter("BANK_CODE");
				String KESSAI_FLAG= httpRequest.getParameter("KESSAI_FLAG");
				String CTRL_NO= httpRequest.getParameter("CTRL_NO");
				String TRAN_STAT= httpRequest.getParameter("TRAN_STAT");
				String TRAN_REASON_CODE= httpRequest.getParameter("TRAN_REASON_CODE");
				String TRAN_RESULT_MSG= httpRequest.getParameter("TRAN_RESULT_MSG");
			
				String TRAN_DATE= httpRequest.getParameter("TRAN_DATE");
				String TRAN_TIME= httpRequest.getParameter("TRAN_TIME");
				String CUST_NAME= httpRequest.getParameter("CUST_NAME");
				String CUST_LNAME= httpRequest.getParameter("CUST_LNAME");
				String CUST_FNAME= httpRequest.getParameter("CUST_FNAME");
				String TRAN_AMOUNT = httpRequest.getParameter("TRAN_AMOUNT");
				String TRAN_FEE = httpRequest.getParameter("TRAN_FEE");
				String PAYMENT_DAY = httpRequest.getParameter("PAYMENT_DAY");
				String GOODS_NAME= httpRequest.getParameter("GOODS_NAME");
				String REMARKS_1= httpRequest.getParameter("REMARKS_1");
				String REMARKS_2= httpRequest.getParameter("REMARKS_2");
				String REMARKS_3= httpRequest.getParameter("REMARKS_3");
				String TRAN_ID= httpRequest.getParameter("TRAN_ID");
				String TRAN_DIGEST= httpRequest.getParameter("TRAN_DIGEST");
				String BANK_CUST_NAME	= httpRequest.getParameter("BANK_CUST_NAME");			
				String MEIGI_STAT		= httpRequest.getParameter("MEIGI_STAT");		
				String CONFIRM_TIME		= httpRequest.getParameter("CONFIRM_TIME");		
				String OPTRAN_DIGEST	= httpRequest.getParameter("OPTRAN_DIGEST");			
				String depositId = httpRequest.getParameter("CTRL_NO");
			} catch (Exception e) {}
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		if(fConfig.getInitParameter("bjpSuccess")!=null){
			bjpSuccess=fConfig.getInitParameter("bjpSuccess");
		}
		if(fConfig.getInitParameter("bjpError")!=null){
			bjpError=fConfig.getInitParameter("bjpError");
		}
	}

	/**
	 * @return the bjpSuccess
	 */
	public String getBjpSuccess() {
		return bjpSuccess;
	}

	/**
	 * @param bjpSuccess the bjpSuccess to set
	 */
	public void setBjpSuccess(String bjpSuccess) {
		this.bjpSuccess = bjpSuccess;
	}

	/**
	 * @return the bjpError
	 */
	public String getBjpError() {
		return bjpError;
	}

	/**
	 * @param bjpError the bjpError to set
	 */
	public void setBjpError(String bjpError) {
		this.bjpError = bjpError;
	}

}
