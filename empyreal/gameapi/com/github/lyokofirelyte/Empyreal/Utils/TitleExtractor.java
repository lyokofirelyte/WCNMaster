package com.github.lyokofirelyte.Empyreal.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

import com.github.lyokofirelyte.Empyreal.Empyreal;
import com.github.lyokofirelyte.Empyreal.Modules.AutoRegister;

/**
 * 
 * @author A Web Coding Blog
 * @URL http://www.gotoquiz.com/web-coding/programming/java-programming/how-to-extract-titles-from-web-pages-in-java/
 *
 */
 
public class TitleExtractor implements AutoRegister<TitleExtractor> {
	
	private Empyreal api;
	
	@Getter
	private TitleExtractor type = this;
	
	public TitleExtractor(Empyreal i){
		api = i;
	}

    private final Pattern TITLE_TAG = Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    
    public String getPageTitle(String url){
    	
    	try {
			return gpt(url);
		} catch (Exception e) {
			return "URL (no title)";
		}
    }
 
    private String gpt(String url) throws IOException {
    	
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        
        ContentType contentType = getContentTypeHeader(conn);
        
        if (!contentType.contentType.equals("text/html")){
        	
            return "URL (no title)";
            
        }  else {

            Charset charset = getCharset(contentType) != null ? getCharset(contentType) : Charset.defaultCharset();
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
            StringBuilder content = new StringBuilder();
            
            int n = 0, totalRead = 0;
            char[] buf = new char[1024];

            while (totalRead < 8192 && (n = reader.read(buf, 0, buf.length)) != -1) {
                content.append(buf, 0, n);
                totalRead += n;
            }
            
            reader.close();

            Matcher matcher = TITLE_TAG.matcher(content);

            return matcher.find() ? matcher.group(1).replaceAll("[\\s\\<>]+", " ").trim() : null;
        }
    }

    private ContentType getContentTypeHeader(URLConnection conn) {
    	
        int i = 0;
        boolean moreHeaders = true;
        
        do {
            String headerName = conn.getHeaderFieldKey(i);
            String headerValue = conn.getHeaderField(i);
            if (headerName != null && headerName.equals("Content-Type"))
                return new ContentType(headerValue);
 
            i++;
            moreHeaders = headerName != null || headerValue != null;
        } while (moreHeaders);
 
        return null;
    }
 
    private Charset getCharset(ContentType contentType) {
    	
        if (contentType != null && contentType.charsetName != null && Charset.isSupported(contentType.charsetName)){
            return Charset.forName(contentType.charsetName);
        } else {
            return null;
        }
    }

    private class ContentType {
    	
        private final Pattern CHARSET_HEADER = Pattern.compile("charset=([-_a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
        private String contentType;
        private String charsetName;
        
        private ContentType(String headerValue) {
        	
            if (headerValue == null){
                throw new IllegalArgumentException("ContentType must be constructed with a not-null headerValue");
            }
            
            int n = headerValue.indexOf(";");
            
            if (n != -1) {
            	
                contentType = headerValue.substring(0, n);
                Matcher matcher = CHARSET_HEADER.matcher(headerValue);
                
                if (matcher.find()){
                    charsetName = matcher.group(1);
                }
                
            } else {
                contentType = headerValue;
            }
        }
    }
}