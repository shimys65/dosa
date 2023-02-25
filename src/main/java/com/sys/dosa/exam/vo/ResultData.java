package com.sys.dosa.exam.vo;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ResultData<DT> {
	@Getter
	private String resultCode;//성공, 실패 여부 S-1, F-1등
	@Getter
	private String msg;//그에따른 메시지
	@Getter
	private String data1Name;//data1의 내용이 무엇인지를 naming한것 임. 이것은 id, 이것은 articles, 이것은 article.. 
	@Getter
	private DT data1;// 게시물 내용
	@Getter
	private String data2Name;
	@Getter
	private Object data2;
	
	public static <DT> ResultData<DT> from(String resultCode, String msg) {
		return from(resultCode, msg, null, null);
	}

	public static <DT> ResultData<DT> from(String resultCode, String msg, String data1Name, DT data1) {
		ResultData<DT> rd = new ResultData<DT>();
		
		rd.resultCode = resultCode;
		rd.msg = msg;
		rd.data1Name = data1Name;
		rd.data1 = data1;
		
		return rd;
	}
	
	public Boolean isSuccess() {
		return resultCode.startsWith("S-");
	}
	
	public Boolean isFail() {
		return isSuccess() == false;
	}

	public static <DT> ResultData<DT> newData(ResultData newRd, String data1Name, DT newData) {
		return from(newRd.getResultCode(), newRd.getMsg(), data1Name, newData);
	}

	public void setData2(String dataName, Object data) {
		data2Name = dataName;
		data2 = data;
	}

}
