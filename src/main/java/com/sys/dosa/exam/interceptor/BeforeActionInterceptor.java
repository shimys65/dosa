package com.sys.dosa.exam.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.sys.dosa.exam.vo.Rq;

@Component // Class를 Spring의 Bean으로 등록할 때 사용하는 Annotation
public class BeforeActionInterceptor implements HandlerInterceptor {
	private Rq rq;
	
	public BeforeActionInterceptor(Rq rq) {
		this.rq = rq;
	}

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception{
//		rq.initOnBeforeActionInterceptor(); 이 라인을 지우고 아래코드 추가 - 필요할 때만 rq를 만드는 방식
		req.setAttribute("rq", rq); 
		
		return HandlerInterceptor.super.preHandle(req, resp, handler);
	}

}
