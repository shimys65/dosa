package com.sys.dosa.exam.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sys.dosa.exam.service.MemberService;
import com.sys.dosa.exam.util.Ut;
import com.sys.dosa.exam.vo.Member;
import com.sys.dosa.exam.vo.ResultData;
import com.sys.dosa.exam.vo.Rq;

@Controller//이것을 입력 후 ctr+shift+o하면 import 진행됨
public class UsrMemberController {
	private MemberService memberService;
	private Rq rq;
	
	public UsrMemberController(MemberService memberService, Rq rq) {
		this.memberService = memberService;
		this.rq = rq;
	}
	
	
	@RequestMapping("/usr/member/doJoin")
	@ResponseBody
	public ResultData<Member> doJoin(String loginId, String loginPw, String name, String nickname, 
			String cellphoneNo, String email) {
		
		if (Ut.empty(loginId)) {
			return ResultData.from("F-1", "loginId(을)를 입력해주세요-doJoin입니다.");
		}

		if (Ut.empty(loginPw)) {
			return ResultData.from("F-2", "loginPw(을)를 입력해주세요-doJoin입니다.");
		}

		if (Ut.empty(name)) {
			return ResultData.from("F-3", "name(을)를 입력해주세요-doJoin입니다.");
		}

		if (Ut.empty(nickname)) {
			return ResultData.from("F-4", "nickname(을)를 입력해주세요-doJoin입니다.");
		}

		if (Ut.empty(cellphoneNo)) {
			return ResultData.from("F-5", "cellphoneNo(을)를 입력해주세요-doJoin입니다.");
		}

		if (Ut.empty(email)) {
			return ResultData.from("F-6", "email(을)를 입력해주세요-doJoin입니다.");
		}
		
		ResultData<Integer> joinRd = memberService.join(loginId, loginPw, name, nickname, cellphoneNo, email);
		
		if(joinRd.isFail()) {
			return (ResultData) joinRd ;
		}		
		
		Member member = memberService.getMemberById(joinRd.getData1());
		
		return ResultData.newData(joinRd, "member", member);
	}
	
	
	@RequestMapping("/usr/member/login")
	public String showLogin(HttpSession httpSession) {
		
		return "usr/member/login";
	}
	
	
	@RequestMapping("/usr/member/doLogin")
	@ResponseBody
	public String doLogin(String loginId, String loginPw, @RequestParam(defaultValue = "/") String afterLoginUri) {
		if(Ut.empty(loginId)) {
			return rq.jsHistoryBack("loginId를 입력하세요-doLogin.");
		}
		
		Member member = memberService.getMemberByLoginId(loginId);	
		
		if(member == null) {
			return rq.jsHistoryBack("존재하지 않은 loginId입니다-doLogin.");
		}
		
		if(Ut.empty(loginPw)) {
			return rq.jsHistoryBack("loginPw를 입력하세요-doLogin.");
		}
		
		if(member.getLoginPw().equals(loginPw) == false) {
			return rq.jsHistoryBack("비밀번호가 일치하지 않습니다-doLogin.");
		}
		
		rq.login(member);
		
		return rq.jsReplace(Ut.f("%s님 환영합니다-doLogin.", member.getNickname()), afterLoginUri);
	}
	
	
	@RequestMapping("/usr/member/doLogout")
	@ResponseBody
	public String doLogout() {
		rq.logout();
		
		return rq.jsReplace("로그아웃 되었음.", "/");
	}
	
	
	@RequestMapping("/usr/member/myPage")
	public String showMypage() {
		
		return "usr/member/myPage";
	}
	

	@RequestMapping("/usr/member/checkPassword")
	public String showCheckPassword() {
		
		return "usr/member/checkPassword";
	}
	
	
	@RequestMapping("/usr/member/doCheckPassword")
	@ResponseBody
	public String doCheckPassword(String loginPw, String replaceUri) {		
		if(Ut.empty(loginPw)) {
			return rq.jsHistoryBack("loginPw를 입력하세요-doCheckPassword.");
		}
		
		if(rq.getLoginedMember().getLoginPw().equals(loginPw) == false) {		
			return rq.jsHistoryBack("비밀번호가 일치하지 않습니다-doCheckPassword.");
		}
		
		if ( replaceUri.equals("../member/modify") ) {
			String memberModifyAuthKey = memberService.genMemberModifyAuthKey(rq.getLoginedMemberId());

			replaceUri += "?memberModifyAuthKey=" + memberModifyAuthKey; 
		}
		
		return rq.jsReplace("",replaceUri);
	}


	@RequestMapping("/usr/member/modify")
	public String showModify(String memberModifyAuthKey) {
		if(Ut.empty(memberModifyAuthKey)) {
			return rq.historyBackJsOnView("인증키가 필요합니다.");
		}
		
		ResultData checkMemberModifyAuthKeyRd = memberService.checkMemberModifyAuthKey(rq.getLoginedMemberId(), 
				memberModifyAuthKey);
		
		
		return "usr/member/modify";
	}
	
	
	@RequestMapping("/usr/member/doModify")
	@ResponseBody
	public String doModify(String loginPw, String name, String nickname, String email, String cellphoneNo) {
		if (Ut.empty(loginPw)) {
			loginPw = null;
		}

		if (Ut.empty(name)) {
			return rq.jsHistoryBack("name(을)를 입력해주세요-doModify.");
		}

		if (Ut.empty(nickname)) {
			return rq.jsHistoryBack("nickname(을)를 입력해주세요-doModify.");
		}

		if (Ut.empty(email)) {
			return rq.jsHistoryBack("email(을)를 입력해주세요-doModify.");
		}

		if (Ut.empty(cellphoneNo)) {
			return rq.jsHistoryBack("cellphoneNo(을)를 입력해주세요-doModify.");
		}

		ResultData modifyRd = memberService.modify(rq.getLoginedMemberId(), loginPw, name, nickname, 
				email, cellphoneNo);

		return rq.jsReplace(modifyRd.getMsg(), "/");
	}
	

}
