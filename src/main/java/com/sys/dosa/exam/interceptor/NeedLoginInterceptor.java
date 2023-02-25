package com.sys.dosa.exam.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.sys.dosa.exam.util.Ut;
import com.sys.dosa.exam.vo.Rq;

// BeforeActionInterceptor은 모든 액션을 위해 사용하며, 아랫것은 로그인 여부를 확인하기 위해
@Component // Class를 Spring의 Bean으로 등록할 때 사용하는 Annotation
public class NeedLoginInterceptor implements HandlerInterceptor {
	private Rq rq;
	
	public NeedLoginInterceptor(Rq rq) {
		this.rq = rq;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		
		if(!rq.isLogined()) {
			String afterLoginUri = rq.getAfterLoginUri();
//			return Ut.jsHistoryBack("로그인 후 이용하삼"); 리턴값이 Boolean 이므로 사용 불가
//			rq.printHistoryBackJs("로그인 후 이용하삼 - NeedLoginInterceptor임다.");
//			rq.printReplaceJs("로그인 후 이용하삼 - NeedLoginInterceptor.", "../member/login");
			rq.printReplaceJs("로그인 후 이용해주세요- NeedLoginInterceptor임다.", 
											"../member/login?afterLoginUri=" + afterLoginUri);
			
			return false; //반드시 사용할것, 더이상 진행이 안되게 함
		}

		return HandlerInterceptor.super.preHandle(req, resp, handler);
	}
}
