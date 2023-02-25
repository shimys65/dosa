package com.sys.dosa.exam.vo;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.sys.dosa.exam.service.MemberService;
import com.sys.dosa.exam.util.Ut;

import lombok.Getter;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)//req가 실행될때마다 rq를 실행
public class Rq {
	// 필드:클래스에 포함된 변수	
	@Getter
	private boolean isLogined;
	@Getter
	private int loginedMemberId;
	@Getter
	private Member loginedMember;

	private HttpServletRequest req; // 인스턴스 변수, static 사용 시 클래스 변수	
	private HttpServletResponse resp;
	private HttpSession session;
	private Map<String, String> paramMap;
	
	// 인스턴스 변수를 원하는 값으로 초기화할 수 있는 생성자 메소드를 제공	
	public Rq(HttpServletRequest req, HttpServletResponse resp, MemberService memberService) {
	// 생성자의 매개변수 이름과 인스턴스 변수의 이름이 같을 경우 인스턴스 변수 앞에 this 키워드를 붙여 구분
		System.out.println("여기");
		this.req = req;
		this.resp = resp;
		this.session = req.getSession();
		paramMap = Ut.getParamMap(req);

		
		boolean isLogined = false;
		int loginedMemberId = 0;
		
// write.jsp에서 사용할 rq.loginedMember.nickname를 위한 로컬변수(Member에서 참조)
		Member loginedMember = null;
		
		if(session.getAttribute("loginedMemberId") != null) {
			isLogined = true;
			
// Key "loginedMemberId"에 바인딩된 id(member.getId()로 가져옴)를 가져온다.			
			loginedMemberId = (int) session.getAttribute("loginedMemberId");
			
// 위에서 가져온 id를 가지고 member의 내용을 가져온다(id가 1이면->loginId는 user1, name은 사용자1, 
// nickname은 유저1 등)			
			loginedMember = memberService.getMemberById(loginedMemberId);
		}
		
		this.isLogined = isLogined;//이것이 T이면 로그인된 것 
		this.loginedMemberId = loginedMemberId;
		this.loginedMember = loginedMember;
//		this.req.setAttribute("rq", this);// jsp 파일로 data 보내기
// 현재 rq(여기에서 this)를 rq에 넣어 보냄, 그러면 jsp에서 ${rq.loginedMember.loginId}, ${rq.isLogined()} 등 
// rq.을 사용할 수있음, BeforeActionInterceptor에서 rq.initOnBeforeActionInterceptor();를 생성해야 함
	}

	public void printHistoryBackJs(String msg) {
		resp.setContentType("text/html; charset=UTF-8");
		
/*		println("<script>");		
		if (!Ut.empty(msg)) {
			println("alert('" + msg + "');");
		}		
		println("</script>");	*/
		
		print(Ut.jsHistoryBack(msg));
		
	}
	
	public void printReplaceJs(String msg, String url) {
		resp.setContentType("text/html; charset=UTF-8");
		
		print(Ut.jsReplace(msg, url));		
	}

	private void print(String str) {
		try {
			resp.getWriter().append(str);
		} catch (IOException e) {			
			e.printStackTrace();
		}		
	}
	
	public boolean isNotLogined() {
		return !isLogined;
	}
	
	private void println(String str) {
		print(str + "\n");		
	}

	public void login(Member member) {
		session.setAttribute("loginedMemberId", member.getId());
	}

	public void logout() {
		session.removeAttribute("loginedMemberId");
	}

//mag면 메시지를 alert으로 보이고, historyback이면 뒤로 이동.
	public String historyBackJsOnView(String msg) {
		req.setAttribute("msg", msg);
		req.setAttribute("historyBack", "true");
		
		return "common/js";
	}

//메시지를 alert으로 보이고 뒤로가기
	public String jsHistoryBack(String msg) {
		return Ut.jsHistoryBack(msg);
	}
	
	public String jsReplace(String msg, String uri) {
		return Ut.jsReplace(msg, uri);
	}
	
	public String getCurrentUri() {
		String currentUri = req.getRequestURI();
        String queryString = req.getQueryString();
//System.out.println("currentUri = " + currentUri);  처음 로그인 시 currentUri = /usr/home/main
//System.out.println("queryString = " + queryString);  처음 로그인 시 queryString = null
System.out.println("currentUri = " + currentUri);
System.out.println("queryString = " + queryString);
        if (queryString != null && queryString.length() > 0) {
            currentUri += "?" + queryString;
        }
        return currentUri;
	}

	public String getEncodedCurrentUri() {
		return Ut.getUriEncoded(getCurrentUri());
	}
	
	public String getLoginUri() {
		return "../member/login?afterLoginUri=" + getAfterLoginUri();
	}

	public String getAfterLoginUri() {
		String requestUri = req.getRequestURI();
// http://localhost:8011/usr/member/login?afterLoginUri=/usr/home/main
System.out.println("requestUri = " + requestUri);
// 처음 requestUri = /usr/home/main이 나오고
// 메인에서 로그인 들어가면 Ut의 getParamMap에서 paramname = afterLoginUri, paramvalue = /usr/home/main를 처리하고
// http://localhost:8011/usr/member/login?afterLoginUri=/usr/home/main가 되므로 requestUri = /usr/member/login이 되고
// 다시 로그인 누르면 Ut의 getParamMap에서 paramname = afterLoginUri, paramvalue = /usr/home/main를 처리하고
// requestUri = /usr/member/login이 나온다.
		switch (requestUri) {
		case "/usr/member/login":
		case "/usr/member/join":
		case "/usr/member/findLoginId":
		case "/usr/member/findLoginPw":
			
			return Ut.getUriEncoded(paramMap.get("afterLoginUri"));
		}
		
		return getEncodedCurrentUri();
	}

	// 이 메서드는 Rq 객체가 자연스럽게 생성되도록 유도하는 역할을 한다.
	// 지우면 안되고,
	// 편의를 위해 BeforeActionInterceptor 에서 꼭 호출해줘야 한다.
//	public void initOnBeforeActionInterceptor() {
		
//	}
	
}
