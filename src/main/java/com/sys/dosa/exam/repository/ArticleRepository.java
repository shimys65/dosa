package com.sys.dosa.exam.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.sys.dosa.exam.vo.Article;
import com.sys.dosa.exam.vo.ResultData;

@Mapper
public interface ArticleRepository {
	@Insert("""
			INSERT INTO article SET regDate = NOW(), updateDate = NOW(), memberId = #{memberId}, 
			boardId = #{boardId}, title = #{title}, `body` = #{body}
			""")
	void writeArticle(@Param("memberId") int memberId, @Param("boardId") int boardId, 
			@Param("title") String title, @Param("body") String body);

	@Delete("""
			DELETE FROM article 
			WHERE id = #{id}
			""")
	void deleteArticle(@Param("id") int id);

	public void modifyArticle(@Param("id") int id, @Param("title") String title, @Param("body") String body);
	
	@Select("""
			<script>
			SELECT A.*,	M.nickname AS extra_writerName,
			IFNULL(SUM(RP.point), 0) AS extra_sumReactionPoint,
			IFNULL(SUM(IF(RP.point &gt; 0, RP.point, 0)), 0) AS goodReactionPoint,
			IFNULL(SUM(IF(RP.point &lt; 0, RP.point, 0)), 0) AS badReactionPoint
			FROM article AS A
			LEFT JOIN `member` AS M
			ON A.memberId = M.id
			LEFT JOIN reactionPoint AS RP
			ON RP.relTypeCode = 'article'
			AND A.id = RP.relId			
			WHERE 1
			AND A.id = #{id}
			GROUP BY A.id
			</script>
			""")
	Article getForPrintArticle(@Param("id") int id);	
	
	@Select("""
			<script>
			SELECT A.*,
			IFNULL(SUM(RP.point), 0) AS extra_sumReactionPoint,
			IFNULL(SUM(IF(RP.point &gt; 0, RP.point, 0)), 0) AS goodReactionPoint,
			IFNULL(SUM(IF(RP.point &lt; 0, RP.point, 0)), 0) AS badReactionPoint
			FROM(			
			SELECT A.*, M.nickname AS extra_writerName 
			FROM article AS A
			LEFT JOIN member AS M 
			ON A.memberId=M.id 
			WHERE 1
			<if test="boardId != 0">
				AND A.boardId = #{boardId}
			</if>
			
			<if test="searchKeyword != ''">
				<choose>
					<when test="searchKeywordTypeCode == 'title'">
						AND A.title LIKE CONCAT('%', #{searchKeyword}, '%')
					</when>
					<when test="searchKeywordTypeCode == 'body'">
						AND A.body LIKE CONCAT('%', #{searchKeyword}, '%')
					</when>
					<otherwise>
					AND (
						A.title LIKE CONCAT('%', #{searchKeyword}, '%')
						OR 
						A.body LIKE CONCAT('%', #{searchKeyword}, '%')
						)
					</otherwise>
				</choose>
			</if>
			ORDER BY A.id DESC
			<if test="limitTake != -1">
				LIMIT #{limitStart}, #{limitTake}
			</if>
			) AS A
			LEFT JOIN reactionPoint AS RP
			ON RP.relTypeCode = 'article'
			AND A.id = RP.relId			
			GROUP BY A.id
			</script>
			""")
	List<Article> getForPrintArticles(int boardId, String searchKeywordTypeCode, String searchKeyword, 
			int limitStart,	int limitTake);
	
	@Select("SELECT LAST_INSERT_ID()")
	int getLastInsertId();
	
	@Select("""
			<script>
			SELECT COUNT(*) AS cnt 
			FROM article AS A 
			WHERE 1
			<if test="boardId != 0">
				AND A.boardId = #{boardId}
			</if>
			
			<if test="searchKeyword != ''">
				<choose>
					<when test="searchKeywordTypeCode == 'title'">
						AND A.title LIKE CONCAT('%', #{searchKeyword}, '%')
					</when>
					<when test="searchKeywordTypeCode == 'body'">
						AND A.body LIKE CONCAT('%', #{searchKeyword}, '%')
					</when>
					<otherwise>
						AND (
							A.title LIKE CONCAT('%', #{searchKeyword}, '%')
							OR 
							A.body LIKE CONCAT('%', #{searchKeyword}, '%')
						)
					</otherwise>
				</choose>
			</if>			
			</script>
			""")
	public int getArticlesCount(int boardId, String searchKeywordTypeCode, String searchKeyword);
	
	@Update("""
			<script>
			UPDATE article 
			SET hitCount = hitCount + 1
			WHERE id = #{id}
			</script>
			""")
	public int increaseHitCount(int id);
	
	@Select("""
			<script>
			SELECT hitCount 
			FROM article 
			WHERE id = #{id}
			</script>
			""")
	public int getArticleHitCount(int id);

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
			UPDATE article
			SET goodReactionPoint = goodReactionPoint - 1
			WHERE id = #{relId}
			""")	
	int decreaseGoodReactionPoint(int relId);

	@Update("""
			UPDATE article
			SET badReactionPoint = badReactionPoint - 1
			WHERE id = #{relId}
			""")
	int decreaseBadReactionPoint(int relId);

	@Select("""
			SELECT A.title
			FROM article AS A
			WHERE id = #{id}
			""")
	Article getArticle(int id);

}
