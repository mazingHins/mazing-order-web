package com.yogu.commons.server.decompress.servlet.wrapper.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import javax.servlet.http.HttpServletRequest;

/**
 * <br>
 * 
 * JFan 2015年1月7日 下午12:28:36
 */
public class DecompressInflateRequestWrapper extends NonHttpServletRequestWrapper {

    public DecompressInflateRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
    }

    /*
     * （非 Javadoc）
     * 
     * @see
     * com.vip.commons.server.compress.wrapper.request.NonHttpServletRequestWrapper#giveInputStream(javax.servlet.http
     * .HttpServletRequest)
     */
    @Override
    public InputStream giveInputStream(HttpServletRequest request) throws IOException {
        return new InflaterInputStream(request.getInputStream());
    }

}
