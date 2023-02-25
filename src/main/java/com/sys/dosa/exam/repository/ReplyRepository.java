package com.sys.dosa.exam.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.sys.dosa.exam.vo.Reply;

@Mapper
public interface ReplyRepository {

	@Update("""
			<script>
			UPDATE article
			SET goodReactionPoint = goodReactionPoint + 1
			WHERE id = #{relId}
			</script>			
			""")
	public int increaseGoodReactionPoint(int relId);

	@Update("""
			<script>
			UPDATE article
			SET badReactionPoint = badReactionPoint + 1
			WHERE id = #{relId}
			</script>
			""")
	public int increaseBadReactionPoint(int relId);

	@Update("""
			<script>
			UPDATE article
			SET goodReactionPoint = goodReactionPoint - 1
			WHERE id = #{relId}
			</script>			
			""")	
	int decreaseGoodReactionPoint(int relId);

	@Update("""
			<script>
			UPDATE article
			SET badReactionPoint = badReactionPoint - 1
			WHERE id = #{relId}
			</script>
			""")
	int decreaseBadReactionPoint(int relId);

	@Insert("""
			INSERT INTO reply SET regDate = NOW(), updateDate = NOW(), memberId = #{memberId}, 
			relTypeCode = #{relTypeCode}, relId = #{relId}, `body` = #{body}
			""")
	void writeReply(int memberId, String relTypeCode, int relId, String body);	

	@Select("""
			SELECT LAST_INSERT_ID()
			""")
	int getLastInsertId();
	
	@Select("""
			SELECT R.*,	M.nickname AS extra_writerName
			FROM reply AS R
			LEFT JOIN member AS M
			ON R.memberId = M.id
			WHERE R.id = #{id}
			""")	
	public Reply getForPrintReply(int id);

	@Select("""
			SELECT R.*,	M.nickname AS extra_writerName
			FROM reply AS R
			LEFT JOIN member AS M
			ON R.memberId = M.id
			WHERE R.relTypeCode = #{relTypeCode}
			AND R.relId = #{relId}
			GROUP BY R.id DESC
			""")
	public List<Reply> getForPrintReplies(String relTypeCode, int relId);

	
	@Delete("""
			DELETE FROM reply
			WHERE id = #{id}
			""")	
	public void deleteReply(int id);

	@Update("""
			UPDATE reply
			SET updateDate = NOW(),	body = #{body}
			WHERE id = #{id}
			""")
	public void modifyReply(int id, String body);


}
