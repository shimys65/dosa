package com.sys.dosa.exam.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.sys.dosa.exam.vo.ResultData;

@Mapper
public interface ReactionPointRepository {

	@Select("""
			<script>
			SELECT IFNULL (SUM(RP.point), 0) AS TEM
			FROM reactionPoint AS RP
			WHERE RP.relTypeCode = 'article'
			AND RP.memberId = #{memberId}
			AND RP.relId = #{relId}
			</script>
			""")
	int getSumReactionPointByMemberId(int memberId, String relTypeCode, int relId);

	@Insert("""
			INSERT INTO reactionPoint SET regDate = NOW(), updateDate = NOW(), memberId = #{memberId}, 
			relTypeCode = #{relTypeCode}, relId = #{relId}, `point` = 1
			""")
	void addGoodReactionPoint(int memberId, String relTypeCode, int relId);

	@Insert("""
			INSERT INTO reactionPoint SET regDate = NOW(), updateDate = NOW(), memberId = #{memberId}, 
			relTypeCode = #{relTypeCode}, relId = #{relId}, `point` = -1
			""")
	void addBadReactionPoint(int memberId, String relTypeCode, int relId);

	@Delete("""
			DELETE FROM reactionPoint
			WHERE memberId = #{memberId}
			AND relTypeCode = #{relTypeCode}
			AND relId = #{relId}
			""")
	void deleteReactionPoint(int memberId, String relTypeCode, int relId);

}
