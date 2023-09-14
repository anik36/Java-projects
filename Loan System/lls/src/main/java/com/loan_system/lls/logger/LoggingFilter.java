package com.loan_system.lls.logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

		long startTime = System.currentTimeMillis();
		filterChain.doFilter(requestWrapper, responseWrapper);
		long timeTaken = System.currentTimeMillis() - startTime;

		String requestBody = getStringValue(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
		String responseBody = getStringValue(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());

		LOGGER.info(
				"FINISHED : METHOD={}; REMOTE_ADDRESS={};  REQUEST PARAMETER NAME={}; REQUEST PATHINFO={}; REQUESTURI={}; REQUEST PAYLOAD={}; RESPONSE CODE={}; RESPONSE={}; TIM TAKEN={}",
				request.getMethod(), request.getRemoteAddr(),Collections.list(request.getParameterNames()),request.getPathTranslated(), request.getRequestURI(), requestBody,
				response.getStatus(), responseBody, timeTaken);
		responseWrapper.copyBodyToResponse();
	}

	private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
		try {
			return new String(contentAsByteArray, 0, contentAsByteArray.length, characterEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

//	Map<String, String> requestMap = this.getTypesafeRequestMap(request);
//	String requestId = requestMap.containsKey(requestIdParamName) ? requestMap.get(requestIdParamName)
//			: UUID.randomUUID().toString();
	
	
//	private Map<String, String> getTypesafeRequestMap(HttpServletRequest request) {
//		Map<String, String> typesafeRequestMap = new HashMap<String, String>();
//		Enumeration<?> requestParamNames = request.getParameterNames();
//		while (requestParamNames.hasMoreElements()) {
//			String requestParamName = (String) requestParamNames.nextElement();
//			String requestParamValue;
//			if (requestParamName.equalsIgnoreCase("password")) {
//				requestParamValue = "********";
//			} else {
//				requestParamValue = request.getParameter(requestParamName);
//			}
//			typesafeRequestMap.put(requestParamName, requestParamValue);
//		}
//		return typesafeRequestMap;
//	}

}
