<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="pageTitle" value="게시물 내용" />
<%@ include file="../common/head.jspf"%>
<%@ include file="../../common/toastUiEditorLib.jspf"%>

<script>
	const params={};
	params.id = parseInt('${param.id}');
</script>

<script>
	function ArticleDetail_increaseHitCount() {
//  키에 데이터 쓰기  localStorage.setItem("key", value);
// 키로 부터 데이터 읽기  localStorage.getItem("key");	
// 브라우저 콘솔 창에서 > localStorage.setItem('email', 'test@user.com')하고
// > localStorage.getItem('email')하면  "test@user.com" 나옴.
		const localStorageKey = 'article_' + params.id;
		
		if (localStorage.getItem(localStorageKey)){
			return;
		}
		
		localStorage.setItem(localStorageKey, true);
		
		$.get('../article/doIncreaseHitCountRd', {
			id : params.id,
			ajaxMode: 'Y'
		}, function(data) {
			$('.article-detail_hit-count').empty().html(data.data1);
		}, 'json');
	}
//	alert(params.id);
	$(function() {
		// 실전코드
		ArticleDetail_increaseHitCount();
		
		// 임시코드
		//setTimeout(ArticleDetail__increaseHitCount, 500);
	})
</script>

<script>
  // 댓글작성 관련
  let ReplyWrite__submitFormDone = false;
  
  function ReplyWrite__submitForm(form) {
    if ( ReplyWrite__submitFormDone ) {
      return;
    }
    // 좌우공백 제거
    form.body.value = form.body.value.trim();
    
    if ( form.body.value.length == 0 ) {
      alert('댓글을 입력해주세요.');
      form.body.focus();
      return;
    }
    
    if ( form.body.value.length < 2 ) {
      alert('댓글내용을 2자이상 입력해주세요.');
      form.body.focus();
      return;
    }
    
    ReplyWrite__submitFormDone = true;
    form.submit();
  }
</script>

<section class="mt-5">
  <div class="container mx-auto fx-3">
    <div class="table-box-type-1">
      <table>
        <colgroup>
          <col width="200" />
        </colgroup>
        <tbody>
          <tr>
            <th>번호</th>
            <td><span class="badge badge-primary">${article.id}</span></td>
          </tr>
          <tr>
            <th>작성날짜</th>
            <td>${article.forPrintType2RegDate}</td>
          </tr>
          <tr>
            <th>수정날짜</th>
            <td>${article.forPrintType2UpdateDate}</td>
          </tr>
          <tr>
            <th>작성자</th>
            <td>${article.extra_writerName}</td>
          </tr>
          <tr>
            <th>조회</th>
            <td>
              <span class="badge badge-primary article-detail_hit-count">${article.hitCount}</span>
            </td>
          </tr>
 
          <tr>
            <th>추천</th>
            <td>
              <span class="badge badge-primary ">${article.goodReactionPoint}</span>
              <c:if test="${actorCanMakeReaction}">
                <a href="../reactionPoint/doGoodReaction?relTypeCode=article&relId=${param.id}&replaceUri=
                ${rq.encodedCurrentUri}" class="btn btn-xs btn-primary btn-outline">
                  좋아요 🤩
                </a>                
                <a href="/usr/reactionPoint/doBadReaction?relTypeCode=article&relId=${param.id}&replaceUri=
                ${rq.encodedCurrentUri}" class="btn btn-xs btn-secondary btn-outline">
                  싫어요 😝
                </a>
              </c:if>
              
              <c:if test="${actorCanCancelGoodReaction}">
                <a href="../reactionPoint/doCancelGoodReaction?relTypeCode=article&relId=${param.id}&replaceUri=
                ${rq.encodedCurrentUri}" class="btn btn-xs btn-primary">
                  좋아요 🤩
                </a>                
                <a onclick="alert(this.title);" href="#" title="먼저 좋아요를 취소해주세요" 
                class="btn btn-xs btn-secondary btn-outline"> 싫어요 😝
                </a>                
              </c:if>
              
              <c:if test="${actorCanCancelBadReaction}">
                <a onclick="alert(this.title);" href="#" title="먼저 싫어요를 취소해주세요" 
                class="btn btn-xs btn-primary btn-outline""> 좋아요 🤩
                </a>                
                <a href="/usr/reactionPoint/doCancelBadReaction?relTypeCode=article&relId=${param.id}&replaceUri=
                ${rq.encodedCurrentUri}" class="btn btn-xs btn-secondary">
                  싫어요 😝
                </a>
              </c:if>
            </td>
          </tr>
          
          <tr>
            <th>제목</th>
            <td>${article.title}</td>
          </tr>
          <tr>
            <th>내용</th>
            <td>
              <div class="toast-ui-viewer">
                <script type="text/x-template">${article.body}</script>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div class="btns">
      <button class="btn btn-link" type="button" onclick="history.back();">뒤로가기</button>
      <c:if test="${article.extra_actorCanModify}">
        <a class="btn btn-link" href="../article/modify?id=${article.id}">게시물 수정</a>
      </c:if>
      <c:if test="${article.extra_actorCanDelete}">
        <a class="btn btn-link" onclick="if ( confirm('정말 삭제하시겠습니까?') == false ) 
        	return false;" href="../article/doDelete?id=${article.id}">게시물 삭제</a>
      </c:if>
    </div>
  </div>
</section>

<section class="mt-5">
  <div class="container mx-auto fx-3">
    <h1>댓글 작성</h1>
    <c:if test="${rq.isLogined()}">
      <form class="table-box-type-1" method="POST" action="../reply/doWrite" 
            onsubmit="ReplyWrite__submitForm(this); return false;">
        <input type="hidden" name="relTypeCode" value="article" />
        <input type="hidden" name="relId" value="${article.id}" />
        <table>
          <colgroup>
            <col width="200" />
          </colgroup>
          <tbody>
            <tr>
              <th>작성자</th>
              <td>${rq.loginedMember.nickname}</td>
            </tr>
            <tr>
              <th>내용</th>
              <td>
                <textarea class="w-full textarea textarea-bordered" name="body" rows="3" 
                placeholder="내용"></textarea>
              </td>
            </tr>
            <tr>
              <th>댓글작성</th>
              <td>
                <button type="submit" class="btn btn-primary">댓글작성</button>
              </td>
            </tr>
          </tbody>
        </table>
      </form>
     </c:if>
     <c:if test="${!rq.logined}">
       <a class="link link-primary" href="${rq.loginUri}">로그인</a> 후 이용해주세요.
     </c:if>      
  </div>
</section>

<section class="mt-5">
  <div class="container mx-auto px-3">
    <h1>댓글 리스트(${replies.size()})</h1>
    
      <table class="table table-fixed w-full">
        <colgroup>
          <col width="50" />  <%--번호 --%>
          <col width="100" /> <%--작성날짜 --%>
          <col width="100" /> <%--수정날짜 --%>
          <col width="50" />  <%--추천 --%>
          <col width="100" /> <%--작성자 --%>
          <col width="150" /> <%--작성자 --%>
          <col />
        </colgroup>
        <thead>
          <tr>
            <th>번호</th>
            <th>작성날짜</th>
            <th>수정날짜</th>
            <th>추천</th>
            <th>작성자</th>
            <th>비  고</th>
            <th>내용</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="reply" items="${replies}">
            <tr class="align-top">
              <th>${reply.id}</th>                      <%--번호 --%>
              <td>${reply.forPrintType1RegDate}</td>    <%--작성날짜 --%>
              <td>${reply.forPrintType1UpdateDate}</td> <%--수정날짜 --%>
              <td>${reply.goodReactionPoint}</td>       <%--추천 --%>
              <td>${reply.extra_writerName}</td>        <%--작성자 --%>
              <td>
                <c:if test="${reply.extra_actorCanModify}">
                  <a class="btn btn-link" href="../reply/modify?id=${reply.id}">수정</a>
                </c:if>
                <c:if test="${reply.extra_actorCanDelete}">
                  <a class="btn btn-link" onclick="if (confirm('정말 삭제하시겠습니까?') == false) return false;
                    " href="../reply/doDelete?id=${reply.id}">삭제</a>
                </c:if>
              </td>
              <td>${reply.forPrintBody}</td>            <%--내용 --%>
            </tr>
          </c:forEach>
        </tbody>
      </table>
          
  </div>
</section>

<!--
<iframe src="http://localhost:8011/usr/article/doIncreaseHitCountRd?id=2" frameborder="0"></iframe>
-->

<%@ include file="../common/foot.jspf"%>