package com.sys.dosa.exam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sys.dosa.exam.service.ReactionPointService;
import com.sys.dosa.exam.vo.ResultData;
import com.sys.dosa.exam.vo.Rq;

@Controller
public class UsrReactionPointController {
	
	private ReactionPointService reactionPointService;
	private Rq rq;
	
	public UsrReactionPointController(ReactionPointService reactionPointService, Rq rq) {
		this.reactionPointService = reactionPointService;
		this.rq = rq;
	}
	

	@RequestMapping("/usr/reactionPoint/doGoodReaction")
	@ResponseBody
	String doGoodReaction(String relTypeCode, int relId, String replaceUri) {
		
		ResultData actorCanMakeReactionPointRd = reactionPointService.actorCanMakeReactionPoint(rq.getLoginedMemberId(), 
				relTypeCode, relId);
//resultCode=S-1, msg=Do Reaction-reactionService, data1Name=sumReactionPointByMemberId, data1=0
		
		if(actorCanMakeReactionPointRd.isFail()) {
//			return rq.jsHistoryBack("이미 처리되었습니다.");
			return rq.jsHistoryBack(actorCanMakeReactionPointRd.getMsg());
		}
		
// reactionPoint 테이블에 내용 insert하기		
		ResultData addGoodReactionPointRd = reactionPointService.addGoodReactionPoint(rq.getLoginedMemberId(), 
				relTypeCode, relId);
		
//		return rq.jsReplace("좋아요를 선택했네요!! -reactionPointController", replaceUri);
		return rq.jsReplace(addGoodReactionPointRd.getMsg(), replaceUri);
	}
	
	
	@RequestMapping("/usr/reactionPoint/doBadReaction")
	@ResponseBody
	String doBadReaction(String relTypeCode, int relId, String replaceUri) {
		
		ResultData actorCanMakeReactionPointRd = reactionPointService.actorCanMakeReactionPoint(rq.getLoginedMemberId(), 
				relTypeCode, relId);

		if(actorCanMakeReactionPointRd.isFail()) {
//			return rq.jsHistoryBack("이미 처리되었습니다.");
			return rq.jsHistoryBack(actorCanMakeReactionPointRd.getMsg());
		}
// reactionPoint 테이블에 내용 insert하기		
		ResultData addBadReactionPointRd = reactionPointService.addBadReactionPoint(rq.getLoginedMemberId(), 
				relTypeCode, relId);
		
		return rq.jsReplace(addBadReactionPointRd.getMsg(), replaceUri);
	}
	
	
	@RequestMapping("/usr/reactionPoint/doCancelGoodReaction")
	@ResponseBody
	String doCancelGoodReaction(String relTypeCode, int relId, String replaceUri) {
		ResultData actorCanMakeReactionPointRd = reactionPointService.actorCanMakeReactionPoint(rq.getLoginedMemberId(), 
				relTypeCode, relId);
		
		if(actorCanMakeReactionPointRd.isSuccess()) {			
			return rq.jsHistoryBack("이미 취소 되었음");
		}
//resultCode=F-2, msg=추천이 불가능-Service actorCanMakeReactionPoint, data1Name=sumReactionPointByMemberId, data1=5
		
		ResultData deleteGoodReactionPointRd = reactionPointService.deleteGoodReactionPoint(rq.getLoginedMemberId(),
				relTypeCode, relId);

		return rq.jsReplace(deleteGoodReactionPointRd.getMsg(), replaceUri);
	}
	
	
	@RequestMapping("/usr/reactionPoint/doCancelBadReaction")
	@ResponseBody
	String doCancelGBadReaction(String relTypeCode, int relId, String replaceUri) {
		ResultData actorCanMakeReactionPointRd = reactionPointService.actorCanMakeReactionPoint(rq.getLoginedMemberId(), 
				relTypeCode, relId);
		
		if(actorCanMakeReactionPointRd.isSuccess()) {			
			return rq.jsHistoryBack("이미 취소 되었음");
		}
		
		ResultData deleteBadReactionPointRd = reactionPointService.deleteBadReactionPoint(rq.getLoginedMemberId(),
				relTypeCode, relId);

		return rq.jsReplace(deleteBadReactionPointRd.getMsg(), replaceUri);
	}

}
