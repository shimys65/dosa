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
public class UsrReplyController {	
	private ReplyService replyService;//DI(Dependency Injection)
	private Rq rq;
	private ArticleService articleService;		

	//생성자를 통해 위 4개의 의존 객체를 주입 받음
	public UsrReplyController(ReplyService replyService, ArticleService articleService, Rq rq) {
		
	//주입받은 객체를 필드에 할당	
		this.replyService = replyService;
		this.articleService = articleService;
		this.rq = rq;
	}
	
	
	@RequestMapping("/usr/reply/doWrite")
	@ResponseBody
	public String doWrite(String relTypeCode, int relId, String body, String replaceUri) {
		if(Ut.empty(relTypeCode)) {
			return rq.jsHistoryBack("relTypeCode을 입력하세요.");
		}
		
		if(Ut.empty(relId)) {
			return rq.jsHistoryBack("relId을 입력하세요.");
		}
		
		if(Ut.empty(body)) {
			return rq.jsHistoryBack("body을 입력하세요.");
		}
		
		ResultData<Integer> writeReplyRd = replyService.writeReply(rq.getLoginedMemberId(),
				relTypeCode, relId, body);		
//		int id = writeReplyRd.getData1();
		
		if(Ut.empty(replaceUri)) {
			switch (relTypeCode) {
			case "article":
				replaceUri = Ut.f("../article/detail?id=%d", relId);
//				System.out.printf("%s", replaceUri); 결과 ../article/detail?id=7
				break;
			}
		}
		
		return rq.jsReplace(writeReplyRd.getMsg(), replaceUri);
	}
	

	@RequestMapping("/usr/reply/modify")
	public String modify(Model model, int id) {
		
		if(Ut.empty(id)) {
			return rq.jsHistoryBack("id을 입력하세요.");
		}
		
		Reply reply = replyService.getForPrintReply(rq.getLoginedMember(),id);
		
		if(reply == null) {
			return rq.historyBackJsOnView(Ut.f("%d번 댓글은 없습니다.", id));
		}

		if(reply.isExtra_actorCanModify() == false) {
			return rq.historyBackJsOnView(Ut.f("%d번 댓글을 수정할 권한이 없습니다.", id));
		}
		
		String relDataTitle = null;
		
		switch (reply.getRelTypeCode()) {
		case "article":
			Article article = articleService.getArticle(reply.getRelId());
			
			relDataTitle = article.getTitle();
			
			break;
		}
		
		model.addAttribute("relDataTitle", relDataTitle);
		
		model.addAttribute("reply", reply);

		return "usr/reply/modify";
	}
	
	
	@RequestMapping("/usr/reply/doModify")
	@ResponseBody
	public String doModify(int id, String body, String replaceUri) {
		
		if(Ut.empty(id)) {
			return rq.jsHistoryBack("id을 입력하세요.");
		}
		
		Reply reply = replyService.getForPrintReply(rq.getLoginedMember(),id);
		
		if(reply == null) {
			return rq.jsHistoryBack(Ut.f("%d번 댓글은 없습니다.", id));
		}

		if(reply.isExtra_actorCanModify() == false) {
			return rq.jsHistoryBack(Ut.f("%d번 댓글을 수정할 권한이 없습니다.", id));
		}
		
		if(Ut.empty(body)) {
			return rq.jsHistoryBack("body을 입력하세요.");
		}
		
		ResultData modifyReplyRd = replyService.modifyReply(id, body);
		
		if(Ut.empty(replaceUri)) {
			switch (reply.getRelTypeCode()) {
			case "article":
				replaceUri = Ut.f("../article/detail?id=%d", reply.getRelId());
				
				break;
			}
		}
		
		return rq.jsReplace(modifyReplyRd.getMsg(), replaceUri);
	}
	
	
	@RequestMapping("/usr/reply/doDelete")
	@ResponseBody
	public String doDelete(int id, String replaceUri) {
		
		if(Ut.empty(id)) {
			return rq.jsHistoryBack("id을 입력하세요.");
		}
		
		Reply reply = replyService.getForPrintReply(rq.getLoginedMember(),id);
		
		if(reply == null) {
			return rq.jsHistoryBack(Ut.f("%d번 댓글은 없습니다.", id));
		}

		if(reply.isExtra_actorCanDelete() == false) {
			return rq.jsHistoryBack(Ut.f("%d번 댓글을 삭제할 권한이 없습니다.", id));
		}
		
		ResultData deleteReplyRd = replyService.deleteReply(id);
		
		if(Ut.empty(replaceUri)) {
			switch (reply.getRelTypeCode()) {
			case "article":
				replaceUri = Ut.f("../article/detail?id=%d", reply.getRelId());
				
				break;
			}
		}
		
		return rq.jsReplace(deleteReplyRd.getMsg(), replaceUri);
	}

}
