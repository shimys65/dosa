package com.sys.dosa.exam.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
	private int id;
	private String regDate;
	private String updateDate;
	private int memberId;
	private String relTypeCode;
	private int relId;
	private String body;
	private int hitCount;
	private int goodReactionPoint;
	private int badReactionPoint;

	private String extra_writerName;
	private boolean extra_actorCanModify;
	private boolean extra_actorCanDelete;

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
		return body.replaceAll("\n", "<br>");
	}
	
}
