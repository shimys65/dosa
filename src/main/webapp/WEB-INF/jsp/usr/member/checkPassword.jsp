<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="pageTitle" value="PAGE TITLE : 비밀번호 확인" />
<%@ include file="../common/head.jspf"%>
<!--  
<script>
let CheckPasswordModify__submitDone = false;

function CheckPasswordModify__submit(form){
  
  if(CheckPasswordModify__submitDone){
    return;
  }
  
  form.loginPw.value = form.loginPw.value.trim();
  
  if(form.loginPw.value.length == 0){
    alert('비밀번호를 입력하세요-script function.')
    form.loginPw.focus();
    
    return;
  }
  
    CheckPasswordModify__submitDone = true;
    form.submit();
}
</script>
-->

<section class="mt-5">
  <div class="container mx-auto px-3">
    <form class="table-box-type-1" method="POST" action="../member/doCheckPassword">
      <input type="hidden" name="replaceUri" value="${param.replaceUri}"/>
      <table>
        <colgroup>
          <col width="200" />
        </colgroup>
        <tbody>
          <tr>
            <th>로그인아이디</th>
            <td>${rq.loginedMember.loginId}</td>
          </tr>
          <tr>
            <th>로그인비밀번호</th>
              <td>
                <input type="password" name="loginPw" class="w-40 input input-bordered input-accent" 
                placeholder="로그인비밀번호" />
              </td>
            </tr>
            <tr>
              <th>비밀번호확인</th>
              <td>
                <button type="submit" class="btn btn-primary">비밀번호확인</button>
                <button type="button" class="btn btn-outline btn-secondary" onclick="history.back();">
                뒤로가기</button>
              </td>
            </tr>
        </tbody>
      </table>    
    </form>    
  </div>
</section>

<!--  
<section class="mt-5">
  <div class="container mx-auto px-3">
    <form class="table-box-type-1" method="POST" action="../member/doCheckPassword"
    onsubmit="CheckPasswordModify__submit(this); return false;">
      <input type="hidden" name="replaceUri" value="${param.replaceUri}" />
      <table>
        <colgroup>
          <col width="200" />
        </colgroup>
        <tbody>
          <tr>
            <th>로그인아이디</th>
            <td>${rq.loginedMember.loginId}</td>
          </tr>
          <tr>
            <th>로그인비밀번호</th>
              <td>
                <input name="loginPw" class="w-40 input input-bordered input-accent" type="password" 
                placeholder="로그인비밀번호" />
              </td>
            </tr>
            <tr>
              <th>비밀번호확인</th>
              <td>
                <button type="submit" class="btn btn-primary">비밀번호 확인</button>
                <button type="button" class="btn btn-outline btn-secondary" onclick="history.back();">
                뒤로가기</button>
              </td>
            </tr>
        </tbody>
      </table>    
    </form>    
  </div>
</section>  
-->

<%@ include file="../common/foot.jspf"%>
