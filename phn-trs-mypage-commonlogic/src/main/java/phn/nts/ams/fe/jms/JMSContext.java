package phn.nts.ams.fe.jms;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import phn.com.nts.util.log.Logit;

public class JMSContext {
	private static final Logit LOG = Logit.getInstance(JMSContext.class);
	private static JMSContext instance;
	
	private String jmsJndiFilename = "jndi.xml";
	private Map<String, Properties> mapProperties = new HashMap<String, Properties>();
	private Map<String, IJmsReceiver> mapJmsReceiver = new HashMap<String, IJmsReceiver>();
	
	public JMSContext() {
		loadJmsConfiguration();
	}
	public static JMSContext getInstance() {
		if(instance == null) {
			instance = new JMSContext();
		}
		return instance;
	}
	
	private void loadJmsConfiguration() {

		LOG.info("[start] load JMS Configuration");
		try {
			
			String configPath = System.getProperty("configPath");
			File file = new File((configPath == null ? "" : configPath + "/") + jmsJndiFilename);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("server");
			Properties props;
			for (int idx = 0; idx < nList.getLength(); idx++) {
				Node nNode = nList.item(idx);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					props = new Properties();
					String key = getTagValue("id", eElement).trim();
					props.setProperty("id", key);
					props.setProperty("java.naming.factory.initial", getTagValue("java.naming.factory.initial", eElement).trim());
					props.setProperty("java.naming.provider.url", getTagValue("java.naming.provider.url", eElement).trim());
					props.setProperty("connectionFactoryNames", getTagValue("connectionFactoryNames", eElement).trim());
					props.setProperty("type", getTagValue("type", eElement).trim());
					props.setProperty("name", getTagValue("name", eElement).trim());
					if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_MT4_QUOTE_RESPONSE.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_MT4_QUOTE_RESPONSE, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_AUTHENTICATION_REQUEST.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_AUTHENTICATION_REQUEST, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_AUTHENTICATION_RESPONSE.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_AUTHENTICATION_RESPONSE, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_FEED_MESSAGE_REQUEST.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_FEED_MESSAGE_REQUEST, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_FEED_MESSAGE_RESPONSE.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_FEED_MESSAGE_RESPONSE, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_ORDER_RESPONSE.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_ORDER_RESPONSE, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_MT4_ORDER_REQUEST.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_MT4_ORDER_REQUEST, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_MT4_ORDER_RESPONSE.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_MT4_ORDER_RESPONSE, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_TRADING_AUTHENTICATION_REQUEST.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_TRADING_AUTHENTICATION_REQUEST, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_TRADING_AUTHENTICATION_RESPONSE.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_TRADING_AUTHENTICATION_RESPONSE, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_ORDER_REQUEST.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_ORDER_REQUEST, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_QUOTE_REQUEST.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_QUOTE_REQUEST, props);
					} else if (phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_QUOTE_RESPONSE.equalsIgnoreCase(props.getProperty("type"))) {
						mapProperties.put(phn.com.nts.util.common.IConstants.ACTIVEMQ.SC_QUOTE_RESPONSE, props);
					} 
				}
			}
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		LOG.info("[end] load JMS Configuration");
	}
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue().replaceAll("[ \t]+(\r\n?|\n)", "");
	}
	/**
	 * @return the mapProperties
	 */
	public Map<String, Properties> getMapProperties() {
		return mapProperties;
	}

	/**
	 * @param mapProperties the mapProperties to set
	 */
	public void setMapProperties(Map<String, Properties> mapProperties) {
		this.mapProperties = mapProperties;
	}
	public Properties getProperties(String key) {
		return this.mapProperties.get(key);
	}
	
}
