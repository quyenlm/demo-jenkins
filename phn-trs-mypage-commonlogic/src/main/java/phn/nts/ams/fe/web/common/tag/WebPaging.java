package phn.nts.ams.fe.web.common.tag;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import phn.com.nts.db.common.PagingInfo;
import phn.com.nts.util.common.IConstants;
import phn.com.nts.util.common.MathUtil;
import phn.com.nts.util.common.StringUtil;
import phn.com.nts.util.log.Logit;
import phn.nts.ams.fe.domain.FrontUserOnline;
import phn.nts.ams.fe.security.FrontUserDetails;
import phn.nts.ams.fe.security.FrontUserOnlineContext;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.util.ValueStack;


public class WebPaging extends TagSupport implements TextProvider, LocaleProvider {
	private final transient TextProvider textProvider = new TextProviderFactory().createInstance(getClass(), this);
	private static final Logit LOG = Logit.getInstance(WebPaging.class);
	private static final String QUERY_STRING_PARAM = "page";
	private static final int FIRST_PAGE = 1;
	private static final String FUNC_NAV = "goUrlNav";
	private static final String FUNC_WITHOUT_NAV = "goUrlWithoutNav";
	private String totalRecordOfPage;
	private PagingInfo pagingInfo;
	private boolean usingUrlForNav = true;
	private String action;
	private String formId;
	private String url;
	private String function;
	private String pagingIndex;
	private String pagingOffSet;	
	private Boolean isShowTotalRecordOfPage;
	/**
	 * @return the pagingInfo
	 */
	public PagingInfo getPagingInfo() {
		return pagingInfo;
	}
	/**
	 * @param pagingInfo the pagingInfo to set
	 */
	public void setPagingInfo(PagingInfo pagingInfo) {
		this.pagingInfo = pagingInfo;
	}
	/**
	 * @return the usingUrlForNav
	 */
	public boolean isUsingUrlForNav() {
		return usingUrlForNav;
	}
	/**
	 * @param usingUrlForNav the usingUrlForNav to set
	 */
	public void setUsingUrlForNav(boolean usingUrlForNav) {
		this.usingUrlForNav = usingUrlForNav;
	}
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}
	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}
	
	private String buildWithNavAction(long index) {
		if(usingUrlForNav) {
			return (function + "('" + url + "?" + QUERY_STRING_PARAM + "=" + index + "');");
		} else {
			return (function + "('" + formId + "','" + action + "','" + pagingIndex + "'," + index + ")");
		}
	}
	/**
	 * 　
	 * do start tag 
	 * @param
	 * @return
	 * @auth QuyTM
	 * @CrDate Jul 25, 2012
	 * @MdDate
	 */
	@Override
	public int doStartTag() throws JspException {
		if(pagingInfo == null) {
			return SKIP_BODY;
		}	
		StringBuffer pagingBff = new StringBuffer();		
		buildPaging(pagingBff, pagingInfo);
		try {
			pageContext.getOut().print(pagingBff.toString());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return 0;
	}
	private void buildPaging(StringBuffer pagingBff, PagingInfo pagingInfo) {
		String[] listRecordOfPage = totalRecordOfPage.split(",");

		if(pagingInfo.getTotal() > 0) {
			pagingBff.append("<ul class=\"paging\">");
			if(FIRST_PAGE == pagingInfo.getIndexPage()) {
				pagingBff.append("<li><a href=\"javascript:void(0);\" >");
			} else {
				pagingBff.append("<li onclick=\"" + buildWithNavAction(FIRST_PAGE) + "\"><a href=\"javascript:void(0);\" >");
			}		
			pagingBff.append(getText("nts.ams.fe.label.first"));
			pagingBff.append("</a></li>");
			if(FIRST_PAGE == pagingInfo.getIndexPage()) {
				pagingBff.append("<li><a href=\"javascript:void(0);\" >");
			} else {
				pagingBff.append("<li onclick=\"" +  buildWithNavAction(pagingInfo.getIndexPage() - 1) + "\"><a href=\"javascript:void(0);\" >");
			}		
			pagingBff.append(getText("nts.ams.fe.label.previous"));
			pagingBff.append("</a></li>");
			for(int i = 1; i <= pagingInfo.getTotalPage(); i ++ ) {
				if(i == pagingInfo.getIndexPage()) {
					if (i > 1) {
						pagingBff.append("<li onclick=\"" +  buildWithNavAction(i-1) + "\">");
						pagingBff.append("<a href=\"javascript:void(0);\" >");
						pagingBff.append(i-1);
						pagingBff.append("</a>");
						pagingBff.append("</li>");
					}
					
					pagingBff.append("<li><a href=\"javascript:void(0);\"  class=\"current\">");
					pagingBff.append(i);
					pagingBff.append("</a></li>");
					
					if (i < pagingInfo.getTotalPage()) {
						pagingBff.append("<li onclick=\"" +  buildWithNavAction(i+1) + "\">");
						pagingBff.append("<a href=\"javascript:void(0);\" >");
						pagingBff.append(i+1);
						pagingBff.append("</a>");
						pagingBff.append("</li>");
					}
					
					break;
				} 
				/*else {
					pagingBff.append("<span class=\"paginate_button\" onclick=\"" +  buildWithNavAction(i) + "\">");
					pagingBff.append(i);
					pagingBff.append("</span>");
				}*/
			}			
			if(pagingInfo.getTotalPage() == pagingInfo.getIndexPage()) {
				pagingBff.append("<li><a href=\"javascript:void(0);\" >");
			} else {
				pagingBff.append("<li onclick=\"" +  buildWithNavAction(pagingInfo.getIndexPage() + 1) + "\"><a href=\"javascript:void(0);\" >");
			}		
			pagingBff.append(getText("nts.ams.fe.label.next"));
			pagingBff.append("</a></li>");
			if(pagingInfo.getTotalPage() == pagingInfo.getIndexPage()) {
				pagingBff.append("<li><a href=\"javascript:void(0);\" >");
			} else {
				pagingBff.append("<li onclick=\"" +  buildWithNavAction(pagingInfo.getTotalPage()) + "\"><a href=\"javascript:void(0);\" >");
			}		
			pagingBff.append(getText("nts.ams.fe.label.last"));
			pagingBff.append("</a></li>");
			pagingBff.append("</ul>");
			
			if(isShowTotalRecordOfPage) {
				// total record can show on one page
				pagingBff.append("<div class=\"dataTables_length\">");
				pagingBff.append("<label>");
				//String[] listRecordOfPage = totalRecordOfPage.split(",");
				pagingBff.append("<select size=\"1\" class=\"entries\" style=\"display: block;\" onchange=\"changeTotalOnPage(this, '" + pagingOffSet + "', '" + formId + "','" + action + "','" + pagingIndex + "');\">");		
				if(listRecordOfPage != null && listRecordOfPage.length > 0) {			
					for(String total : listRecordOfPage) {		
						if(MathUtil.parseInt(total, 0) == pagingInfo.getOffset()) {
							pagingBff.append("<option value=\"" + total + "\" selected=\"selected\">" + total + "</option>");
						} else {
							pagingBff.append("<option value=\"" + total + "\">" + total + "</option>");
						}
						
					}
				}
				pagingBff.append("</select>");
				pagingBff.append("</label></div>");
				// show total record
				pagingBff.append("<label style=\"float:left;padding:8px;\">");
				long endPage = pagingInfo.getTotal() > pagingInfo.getEndRecords() ? pagingInfo.getEndRecords() : pagingInfo.getTotal();
				MessageFormat messageFormat = new MessageFormat(getText("nts.ams.fe.label.paging_description"));
				Object[] arrmes = {pagingInfo.getIndex() + 1,endPage,pagingInfo.getTotal()}; 
				String strmes = messageFormat.format(arrmes);
				pagingBff.append(strmes);
				pagingBff.append("</label>");
			}
		}
		
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the function
	 */
	public String getFunction() {
		return function;
	}
	/**
	 * @param function the function to set
	 */
	public void setFunction(String function) {
		this.function = function;
	}
	/**
	 * @return the pagingIndex
	 */
	public String getPagingIndex() {
		return pagingIndex;
	}
	/**
	 * @param pagingIndex the pagingIndex to set
	 */
	public void setPagingIndex(String pagingIndex) {
		this.pagingIndex = pagingIndex;
	}
	public Locale getLocale() {
//        ActionContext ctx = ActionContext.getContext();
//        if (ctx != null) {
//            return ctx.getLocale();
//        } else {
//            LOG.debug("Action context not initialized");
//            return null;
//        }
		String language = StringUtil.toLowerCase(IConstants.Language.JAPANESE);
		FrontUserDetails frontUserDetails = FrontUserOnlineContext.getFrontUserOnline();
		if(frontUserDetails != null) {
			FrontUserOnline frontUserOnline = frontUserDetails.getFrontUserOnline();
			if(frontUserOnline != null) {
				language = StringUtil.toLowerCase(frontUserOnline.getLanguage());
			}
		}		
		return new Locale(language);
    }
	public boolean hasKey(String key) {
        return textProvider.hasKey(key);
    }

    public String getText(String aTextName) {
        return textProvider.getText(aTextName);
    }

    public String getText(String aTextName, String defaultValue) {
        return textProvider.getText(aTextName, defaultValue);
    }

    public String getText(String aTextName, String defaultValue, String obj) {
        return textProvider.getText(aTextName, defaultValue, obj);
    }

    public String getText(String key, String[] args) {
        return textProvider.getText(key, args);
    }

    public String getText(String key, String defaultValue, String[] args) {
        return textProvider.getText(key, defaultValue, args);
    }

    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return textProvider.getText(key, defaultValue, args, stack);
    }

    public ResourceBundle getTexts() {
        return textProvider.getTexts();
    }
	@Override
	public String getText(String arg0, List<?> arg1) {
		return textProvider.getText(arg0, arg1);
	}
	@Override
	public String getText(String arg0, String arg1, List<?> arg2) {
		return textProvider.getText(arg0, arg1, arg2);
	}
	@Override
	public String getText(String arg0, String arg1, List<?> arg2,
			ValueStack arg3) {
		return textProvider.getText(arg0, arg1, arg2, arg3);
	}
	@Override
	public ResourceBundle getTexts(String arg0) {
		return textProvider.getTexts();
	}
	/**
	 * @return the totalRecordOfPage
	 */
	public String getTotalRecordOfPage() {
		return totalRecordOfPage;
	}
	/**
	 * @param totalRecordOfPage the totalRecordOfPage to set
	 */
	public void setTotalRecordOfPage(String totalRecordOfPage) {
		this.totalRecordOfPage = totalRecordOfPage;
	}
	/**
	 * @return the pagingOffSet
	 */
	public String getPagingOffSet() {
		return pagingOffSet;
	}
	/**
	 * @param pagingOffSet the pagingOffSet to set
	 */
	public void setPagingOffSet(String pagingOffSet) {
		this.pagingOffSet = pagingOffSet;
	}
	/**
	 * @return the isShowTotalRecordOfPage
	 */
	public Boolean getIsShowTotalRecordOfPage() {
		return isShowTotalRecordOfPage;
	}
	/**
	 * @param isShowTotalRecordOfPage the isShowTotalRecordOfPage to set
	 */
	public void setIsShowTotalRecordOfPage(Boolean isShowTotalRecordOfPage) {
		this.isShowTotalRecordOfPage = isShowTotalRecordOfPage;
	}

}
