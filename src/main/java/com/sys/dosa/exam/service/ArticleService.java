package com.sys.dosa.exam.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sys.dosa.exam.repository.ArticleRepository;
import com.sys.dosa.exam.util.Ut;
import com.sys.dosa.exam.vo.Article;
import com.sys.dosa.exam.vo.ResultData;

@Service
public class ArticleService {
	
	@Autowired
	private ArticleRepository articleRepository;
	
	//누가 write했는지 알기위해 memberId 추가
	public ResultData writeArticle(int memberId, int boardId, String title, String body) {		
		articleRepository.writeArticle(memberId, boardId, title, body);
		int id = articleRepository.getLastInsertId();
		
		return ResultData.from("S-1", Ut.f("%d번 게시물이 생성-writeArticle", id), "id", id);
	}
	
	
	public List<Article> getForPrintArticles(int actorId, int boardId, String searchKeywordTypeCode, 
			String searchKeyword, int itemsCountInAPage, int page) {
		
// SELECT * FROM article WHERE boardId=1 ORDER BY id DESC LIMIT 0,10,20...(limitStart), 10(itemsCountInAPage)
		int limitStart = (page-1) * itemsCountInAPage;//page가 1이면 0, 2이면 10...
		int limitTake = itemsCountInAPage;
		
		List<Article> articles = articleRepository.getForPrintArticles(boardId, searchKeywordTypeCode, 
				searchKeyword, limitStart, limitTake);
		
		for ( Article article : articles ) {
			updateForPrintData(actorId, article);
		}
		
		return articles;
	}
	

	public Article getForPrintArticle(int actorId, int id) { // actorId는 loginedMemberId
		
		Article article = articleRepository.getForPrintArticle(id);

// 아래 문장이 수행되면 extra_actorCanDelete와 extra_actorCanModify에 true, false가 저장됨
		updateForPrintData(actorId, article);
		
		return article;
	}
	
	
	// delete, modify가 가능한지 점검하고 가능하면 isSuccess() 실행 함.	
	public void updateForPrintData(int actorId, Article article) {
		
		if(article == null) {
			return;
		}
		
		ResultData actorCanDeleteRd = actorCanDelete(actorId, article);
		article.setExtra_actorCanDelete(actorCanDeleteRd.isSuccess());
		
		ResultData actorCanModifyRd = actorCanModify(actorId, article);
		article.setExtra_actorCanModify(actorCanModifyRd.isSuccess());
	}
	
	
	public void deleteArticle(int id) {
		
		articleRepository.deleteArticle(id);		
	}
	

	private ResultData actorCanDelete(int actorId, Article article) {
		
		if ( article == null ) {
			return ResultData.from("F-1", "게시물이 없어요-actorCanDelete");
		}
		
		if ( article.getMemberId() != actorId ) {
			return ResultData.from("F-2", "권한이 없습니다-actorCanDelete");
		}
		
		return ResultData.from("S-1", "게시물 삭제가 가능합니다-actorCanDelete");
	}
	

	public ResultData<Article> modifyArticle(int id, String title, String body) {
		
		articleRepository.modifyArticle(id, title, body);
		
		Article article = getForPrintArticle(0,id);
		
		return ResultData.from("S-1", Ut.f("%d번 게시물 수정 됨-modifyArticle", id), "article", article);
	}
	

	public ResultData actorCanModify(int actorId, Article article) {
		
		if ( article == null ) {
			return ResultData.from("F-1", "게시물이 없어요-actorCanModify");
		}
		
		if ( article.getMemberId() != actorId ) {
			return ResultData.from("F-2", "권한이 없어요-actorCanModify");
		}
		
		return ResultData.from("S-1", "게시물 수정이 가능합니다-actorCanModify.");
	}
	

	public int getArticlesCount(int boardId, String searchKeywordTypeCode, String searchKeyword) {
		
		return articleRepository.getArticlesCount(boardId, searchKeywordTypeCode, searchKeyword);
	}
	

	public ResultData<Integer> increaseHitCount(int id) {
		
		int affectedRowsCount = articleRepository.increaseHitCount(id);

		if (affectedRowsCount == 0) {
			return ResultData.from("F-1", "해당 게시물이 존재하지 않습니다.-ServiceincreaseHitCount", 
					"affectedRowsCount", affectedRowsCount);
		}

		return ResultData.from("S-1", "조회수가 증가되었습니다.-ServiceincreaseHitCount", 
				"affectedRowsCount", affectedRowsCount);
	}
	

	public int getArticleHitCount(int id) {
		
		return articleRepository.getArticleHitCount(id);
	}
	

	public ResultData increaseGoodReactionPoint(int relId) {
		
		int affectedRowsCount = articleRepository.increaseGoodReactionPoint(relId);
//		System.out.println(affectedRowsCount); 리턴 1
		
		if(affectedRowsCount == 0) {
			return ResultData.from("F-1", "해당 게시물이 없음", "affectedRowsCount", affectedRowsCount);
		}
		
		return ResultData.from("S-1", "좋아요 수가 증가 - article", "affectedRowsCount", affectedRowsCount);
	}
	

	public ResultData increaseBadReactionPoint(int relId) {
		
		int affectedRowsCount = articleRepository.increaseBadReactionPoint(relId);
		
		if(affectedRowsCount == 0) {
			return ResultData.from("F-1", "해당 게시물이 없음", "affectedRowsCount", affectedRowsCount);
		}
		
		return ResultData.from("S-1", "싫어요 수가 증가 - article", "affectedRowsCount", affectedRowsCount);
	}
	

	public ResultData decreaseGoodReactionPoint(int relId) {
		
		int affectedRowsCount = articleRepository.decreaseGoodReactionPoint(relId);

		if(affectedRowsCount == 0) {
			return ResultData.from("F-1", "해당 게시물이 없음", "affectedRowsCount", affectedRowsCount);
		}
		
		return ResultData.from("S-1", "좋아요 수가 감소 - article", "affectedRowsCount", affectedRowsCount);		
	}
	

	public ResultData decreaseBadReactionPoint(int relId) {
		int affectedRowsCount = articleRepository.decreaseBadReactionPoint(relId);

		if(affectedRowsCount == 0) {
			return ResultData.from("F-1", "해당 게시물이 없음", "affectedRowsCount", affectedRowsCount);
		}
		
		return ResultData.from("S-1", "싫어요 수가 감소 - article", "affectedRowsCount", affectedRowsCount);		
	}


	public Article getArticle(int id) {
		
		return articleRepository.getArticle(id);
	}

	
}
