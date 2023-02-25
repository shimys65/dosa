package com.sys.dosa.exam.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sys.dosa.exam.service.ArticleService;
import com.sys.dosa.exam.service.BoardService;
import com.sys.dosa.exam.service.ReactionPointService;
import com.sys.dosa.exam.service.ReplyService;
import com.sys.dosa.exam.util.Ut;
import com.sys.dosa.exam.vo.Article;
import com.sys.dosa.exam.vo.Board;
import com.sys.dosa.exam.vo.Reply;
import com.sys.dosa.exam.vo.ResultData;
import com.sys.dosa.exam.vo.Rq;

@Controller
public class UsrArticleController {
	
	private ArticleService articleService;//DI(Dependency Injection)
	private BoardService boardService;
	private ReactionPointService reactionPointService;
	private ReplyService replyService;
	private Rq rq;		

	//생성자를 통해 위 4개의 의존 객체를 주입 받음
	public UsrArticleController(ArticleService articleService, BoardService boardService, 
			ReactionPointService reactionPointService, ReplyService replyService, Rq rq) {
		
	//주입받은 객체를 필드에 할당	
		this.articleService = articleService;
		this.boardService = boardService;
		this.reactionPointService = reactionPointService;
		this.replyService = replyService;
		this.rq = rq;
	}
	
	
	@RequestMapping("/usr/article/doWrite")
	@ResponseBody
	public String doWrited(int boardId, String title, String body, String replaceUri) {
		
//BeforeActionInterceptor도입 전에는 "Rq rq = new Rq(req)"를 이용하여 각 액션에 인스턴스를 생성했음.
		if(Ut.empty(title)) {
			return rq.jsHistoryBack(Ut.f("제목을 입력하세요."));
		}
		
		if(Ut.empty(body)) {
			return rq.jsHistoryBack(Ut.f("내용을 입력하세요."));
		}
		
		// 누가 write했는지 알기위해 rq.getLoginedMemberId() 추가
		ResultData<Integer> writeArticleRd = articleService.writeArticle(rq.getLoginedMemberId(),
				boardId, title, body);
		int id = writeArticleRd.getData1();//마지막 입력된 article수로 uri 주소를 저장하는데 사용.
		
		if(Ut.empty(replaceUri)) {
			replaceUri = Ut.f("../article/detail?id=%d", id);
//			System.out.printf("%s", replaceUri); 결과 ../article/detail?id=7
		}
		
		return rq.jsReplace(Ut.f("%d번 글이 생성 되었습니다-dowrite", id), replaceUri);
	}
	
	
	@RequestMapping("/usr/article/write")
	public String showWrite(HttpServletRequest req, Model model) {
		
		return "usr/article/write";
	}
	
	
	@RequestMapping("/usr/article/doDelete")
	@ResponseBody
	public String doDelete(int id) {//String인 이유 : return 값이 문자		
		//setAttribute()를 통해 값을 설정해주지 않으면 null값을 리턴, BeforeActionInterceptor에서 설정함
		if(rq.isLogined() == false) { 
			return rq.jsHistoryBack("로그인 후 이용-doDelete");
		}
		
		Article article = articleService.getForPrintArticle(rq.getLoginedMemberId(), id);
		
		if(article == null) {
			return rq.jsHistoryBack(Ut.f("%d번 게시물은 없습니다-doDelete", id));
		}

		if(article.getMemberId() != rq.getLoginedMemberId()) {
			return rq.jsHistoryBack("권한이 없습니다-doDelete");
		}
		
		articleService.deleteArticle(id);
		
		//메시지를 alert으로 보여주고, 지정 uri로 이동
		return rq.jsReplace(Ut.f("%d번 게시물 삭제 됨-doDelete", id), "../article/list");		
	}
	
	
	@RequestMapping("/usr/article/doModify")
	@ResponseBody
	public String doModify(int id, String title, String body, String replaceUri) {
		
		if(rq.isLogined() == false) {
			return rq.jsHistoryBack(Ut.f("로그인 후 이용하세요-doModify"));
		}
		
		Article article = articleService.getForPrintArticle(rq.getLoginedMemberId(), id);
		
		if(article == null) {
			return rq.jsHistoryBack(Ut.f("%d번 게시물은 없습니다-doModify", id));
		}
		
		ResultData actorCanModifyRd = articleService.actorCanModify(rq.getLoginedMemberId(), article);
		
		if(actorCanModifyRd.isFail()) {
			return rq.jsHistoryBack(actorCanModifyRd.getMsg());
		}
		
		articleService.modifyArticle(id, title, body);
		
		if(Ut.empty(replaceUri)) {
			replaceUri = Ut.f("../article/detail?id=%d", id);
		}
		
		return rq.jsReplace(Ut.f("%d번 글이 수정 되었습니다-doModify", id), replaceUri);
	}
	
	
	@RequestMapping("/usr/article/modify")
	public String doModify(Model model, int id) {
		Article article = articleService.getForPrintArticle(rq.getLoginedMemberId(), id);
		
		ResultData actorCanModifyRd = articleService.actorCanModify(rq.getLoginedMemberId(), article);
		
// 아래 문자열을 리턴하기위해 rq에 historyBackJsOnView() 함수를 만들어서 처리 함
		if (actorCanModifyRd.isFail()) {
			if(article == null) {
				return rq.historyBackJsOnView(Ut.f("게시물이 없고 권한도 없고만-modify"));
			}

			return rq.historyBackJsOnView(actorCanModifyRd.getMsg());
		}		

		if (article == null) {

			return rq.historyBackJsOnView(Ut.f("%d번 게시물은 없네요-modify", id));
		}
		
		model.addAttribute("article", article);

		return "usr/article/modify";
	}
	
	
	@RequestMapping("/usr/article/getArticle")
	@ResponseBody
	public ResultData<Article> getArticle(int id) {
		
		Article article = articleService.getForPrintArticle(rq.getLoginedMemberId(), id);
		
		if(article == null) {
			return ResultData.from("F-1", Ut.f("%d번 게시물이 없습니다.",id)) ;
		}
		return ResultData.from("S-1", Ut.f("%d번 게시물입니다.",id), "article", article);
	}
	
	
	@RequestMapping("/usr/article/detail")
	public String showDetail(Model model, int id) {
		
		Article article = articleService.getForPrintArticle(rq.getLoginedMemberId(), id);
		model.addAttribute("article", article);
		
		List<Reply> replies = replyService.getForPrintReplies(rq.getLoginedMember(), "article", id);
		System.out.println(replies); // 댓글이 전부 출력
//		int repliesCount = replies.size();// 예) 2번(id) 글에대하여 댓글이 2개(repliesCont)
		model.addAttribute("replies", replies);

		ResultData actorCanMakeReactionPointRd = reactionPointService.actorCanMakeReactionPoint(
				rq.getLoginedMemberId(),"article", id);
		
// Reaction 안한 경우 : resultCode=S-1, msg=추천이 가능-reactionService actorCanMakeReactionPoint,
//			  				data1Name=sumReactionPointByMemberId, data1=0
// Reaction 한경우(좋아요) : resultCode=F-2, msg=추천이 불가능-reactionService actorCanMakeReactionPoint,
// 					  		data1Name=sumReactionPointByMemberId, data1=1		
		model.addAttribute("actorCanMakeReaction", actorCanMakeReactionPointRd.isSuccess());
		
// 좋아요를 누른 후 값 -> resultCode=F-2, msg=추천이 불가능-reactionService actorCanMakeReactionPoint,
// 					 		data1Name=sumReactionPointByMemberId, data1=1
// 취소를 처리하는 영역, 취소하기위한 아이콘이 나오기위해서 F-2인경우여야하고, data1이 1인지 -1인지 구별하여 처리
		if(actorCanMakeReactionPointRd.getResultCode().equals("F-2")) {
//			int sumReactionPointByMemberId = (int) actorCanMakeReactionPointRd.getData1();
			
			if((int) actorCanMakeReactionPointRd.getData1() > 0) {
				model.addAttribute("actorCanCancelGoodReaction", true);
			}
			else {
				model.addAttribute("actorCanCancelBadReaction", true);
			}
		}
		
		return "usr/article/detail";
	}
	
	
	@RequestMapping("/usr/article/doIncreaseHitCountRd")	
	@ResponseBody
	public ResultData<Integer> doIncreaseHitCountRd(int id){		
		
		ResultData<Integer> increaseHitCountRd = articleService.increaseHitCount(id);
		
//		System.out.println(increaseHitCountRd);콘솔에서 확인하려 작성
		if(increaseHitCountRd.isFail()){
			return increaseHitCountRd;
		}
		
		ResultData<Integer> rd = ResultData.newData(increaseHitCountRd, "hitCount", 
				articleService.getArticleHitCount(id));
		
		rd.setData2("id", id);
		
		return rd;
	}
	
	
//searchKeywordTypeCode = title, body이고, searchKeyword는 찾고자하는 단어.
	@RequestMapping("/usr/article/list")
	public String showList(Model model, @RequestParam(defaultValue = "1") int boardId,
			@RequestParam(defaultValue = "title, body") String searchKeywordTypeCode,
			@RequestParam(defaultValue = "") String searchKeyword, @RequestParam(defaultValue = "1") int page) {
		
		Board board = boardService.getBoardById(boardId);
		
		if(board == null) {
			return rq.historyBackJsOnView(Ut.f("%d번 게시판은 없습니다-list", boardId));
		}
		// title이나 body에서 찾고자하는 게시물 수를 나타냄.
		int articlesCount = articleService.getArticlesCount(boardId, searchKeywordTypeCode, searchKeyword);
		
		int itemsCountInAPage = 10;
		int pagesCount = (int)Math.ceil( (double)articlesCount / itemsCountInAPage);
		
		List<Article> articles = articleService.getForPrintArticles(rq.getLoginedMemberId(), boardId, 
				searchKeywordTypeCode, searchKeyword, itemsCountInAPage, page);

//		System.out.println(articles); //해당 article이 전부 출력
		model.addAttribute("boardId", boardId);//공지, 자유
		model.addAttribute("board", board);//${board.name} 게시물 리스트
		model.addAttribute("page", page);//1번 페이지때문에 list.jsp에서 param.page를 page로 교체하기위해(1번 페이지가 색이 없어서)
		model.addAttribute("articlesCount", articlesCount);//상단의 게시물 개수, ${articlesCount}개
		model.addAttribute("pagesCount", pagesCount);//하단의 버튼들
		model.addAttribute("articles", articles);//Model 객체를 이용 뷰(jsp)로 값을 넘김.
		
		return "usr/article/list";
	}	

}
