package phn.nts.ams.fe.util;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import phn.com.nts.util.log.Logit;

/**
 * @description
 * @version NTS1.0
 * @author Quan.Le.Minh
 * @CrDate Jan 26, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class DownloadUtil {
	private static Logit log = Logit.getInstance(DownloadUtil.class);
	
	public static void download(String fileName, InputStream in) {
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			ServletOutputStream out = response.getOutputStream();

			int length = in.toString().getBytes("UTF-8").length;
			byte[] outputByte = new byte[length];

			while (in.read(outputByte, 0, length) != -1) {
				out.write(outputByte, 0, length);
			}

			in.close();
			out.flush();
			out.close();
			
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
