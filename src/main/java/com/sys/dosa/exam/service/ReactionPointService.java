package com.sys.dosa.exam.service;

import org.springframework.stereotype.Service;

import com.sys.dosa.exam.repository.ReactionPointRepository;
import com.sys.dosa.exam.vo.ResultData;
import com.sys.dosa.exam.vo.Rq;

@Service
public class ReactionPointService {
	
	
	private ReactionPointRepository reactionPointRepository;
	private ArticleService articleService;

	public ReactionPointService(ReactionPointRepository reactionPointRepository, ArticleService articleService) {
		this.reactionPointRepository = reactionPointRepository;
		this.articleService = articleService;
	}

	public ResultData actorCanMakeReactionPoint(int actorId, String relTypeCode, int relId) {		
		if(actorId == 0) {
			
			return ResultData.from("F-1", "로그인 후 이용바람 -reactionService actorCanMakeReactionPoint");
		}		
				int sumReactionPointByMemberId = reactionPointRepository.getSumReactionPointByMemberId(actorId, 
				relTypeCode, relId);
		
		if(sumReactionPointByMemberId != 0) {
			
			return ResultData.from("F-2", "추천이 불가능-reactionService actorCanMakeReactionPoint", 
				"sumReactionPointByMemberId", sumReactionPointByMemberId);
		}
		
		return ResultData.from("S-1", "추천이 가능-reactionService actorCanMakeReactionPoint", 
				"sumReactionPointByMemberId", sumReactionPointByMemberId);
	}
	
	public ResultData addGoodReactionPoint(int actorId, String relTypeCode, int relId) {
		
		reactionPointRepository.addGoodReactionPoint(actorId, relTypeCode, relId);
		
		switch (relTypeCode) {
		case "article":
			articleService.increaseGoodReactionPoint(relId);
			break;
		}
		
		return ResultData.from("S-1", "좋아요를 처리");
	}

	public ResultData addBadReactionPoint(int actorId, String relTypeCode, int relId) {
		
		reactionPointRepository.addBadReactionPoint(actorId, relTypeCode, relId);
		
		switch (relTypeCode) {
		case "article":
			articleService.increaseBadReactionPoint(relId);
			break;
		}
		
		return ResultData.from("F-1", "싫어요를 처리");
	}

	public ResultData deleteGoodReactionPoint(int actorId, String relTypeCode, int relId) {
		
		reactionPointRepository.deleteReactionPoint(actorId, relTypeCode, relId);
		
		switch (relTypeCode) {
		case "article":
			articleService.decreaseGoodReactionPoint(relId);
			break;
		}
		
		return ResultData.from("S-1", "좋아요를 취소처리");		
	}

	public ResultData deleteBadReactionPoint(int actorId, String relTypeCode, int relId) {
		reactionPointRepository.deleteReactionPoint(actorId, relTypeCode, relId);
		
		switch (relTypeCode) {
		case "article":
			articleService.decreaseBadReactionPoint(relId);
			break;
		}
		
		return ResultData.from("S-1", "싫어요를 취소처리");		
	}
	
}
