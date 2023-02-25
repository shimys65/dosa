package com.sys.dosa.exam.service;

import org.springframework.stereotype.Service;

import com.sys.dosa.exam.repository.MemberRepository;
import com.sys.dosa.exam.util.Ut;
import com.sys.dosa.exam.vo.Member;
import com.sys.dosa.exam.vo.ResultData;

@Service
public class MemberService {
	private MemberRepository memberRepository;
	private AttrService attrService;
	
	
	public MemberService(MemberRepository memberRepository, AttrService attrService) {
		this.memberRepository = memberRepository;
		this.attrService = attrService;
	}

	public ResultData<Integer> join(String loginId, String loginPw, String name, String nickname, 
			String cellphoneNo, String email) {
		Member oldMember = getMemberByLoginId(loginId);	
		
		if(oldMember != null) {
			return ResultData.from("F-7", 
					Ut.f("해당 로그인 아이디(%s)는 이미 사용 중입니다 - memberService.join입니다.", loginId));
		}
		
		oldMember = getMemberByNameAndEmail(name, email);
		
		if(oldMember != null) {
			return ResultData.from("F-8", 
					Ut.f("해당 이름(%s)과 E-mail(%s)는 이미 사용 중입니다-memberService.join입니다.", name, email));
		}
		
		memberRepository.join(loginId, loginPw, name, nickname, cellphoneNo, email);
		
		int id = memberRepository.getLastInsertId();

		return ResultData.from("S-1", "회원가입이 완료되었습니다-memberService.join입니다.", "id", id);
	}

	public Member getMemberByNameAndEmail(String name, String email) {		
		return memberRepository.getMemberByNameAndEmail(name, email);
	}

	public Member getMemberByLoginId(String loginId) {
		return memberRepository.getMemberByloginId(loginId);
	}

	public Member getMemberById(int id) {
		return memberRepository.getMemberById(id);
	}

	public ResultData modify(int id, String loginPw, String name, String nickname, String email,
			String cellphoneNo) {
		memberRepository.modify(id, loginPw, name, nickname, email, cellphoneNo);
		
		return ResultData.from("S-1", "회원정보가 수정됨-service.");
	}

	public String genMemberModifyAuthKey(int actorId) {
		String memberModifyAuthKey = Ut.getTempPassword(10);

		attrService.setValue("member", actorId, "extra", "memberModifyAuthKey", memberModifyAuthKey, 
				Ut.getDateStrLater(60 * 5));

		return memberModifyAuthKey;
	}

	public ResultData checkMemberModifyAuthKey(int loginedMemberId, String memberModifyAuthKey) {
		// TODO Auto-generated method stub
		return null;
	}

}
