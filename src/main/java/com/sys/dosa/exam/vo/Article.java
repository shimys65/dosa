package com.sys.dosa.exam.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
	private int id;
	private String regDate;
	private String updateDate;
	private int memberId;
	private String title;
	private String body;
	private int hitCount;
	
	private int extra_sumReactionPoint;
//	private int extra_goodReactionPoint;
//	private int extra_badReactionPoint;
	private int goodReactionPoint;//좋아요
	private int badReactionPoint;
	
	private String extra_writerName;
// articleService의 updateForPrintData()에서 지우기, 수정이 가능한지 여부를 넣기위해 만든 변수.
// ResultData의 Data1에 저장되는 값이다. extra_actorCanDelete : false 또는 true
	private Boolean extra_actorCanDelete;
	private Boolean extra_actorCanModify;
	
	public String getForPrintType1RegDate() {
		return regDate.substring(2, 16).replace(" ", "<br>");
	}
	
	public String getForPrintType1UpdateDate() {
		return updateDate.substring(2, 16).replace(" ", "<br>");
	}
	
	public String getForPrintType2RegDate() {
		return regDate.substring(2, 16);
	}
	
	public String getForPrintType2UpdateDate() {
		return regDate.substring(2, 16);
	}
	
	public String getForPrintBody() {
		return body.replaceAll("/n", "<br>");
	}

}
