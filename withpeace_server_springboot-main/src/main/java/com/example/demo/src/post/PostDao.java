package com.example.demo.src.post;

import com.example.demo.src.post.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.Long;
import java.util.List;

import javax.sql.DataSource;

@Repository
public class PostDao {
    private JdbcTemplate jdbcTemplate;
    private List<GetPostImageRes> getPostImageRes;
    private List<GetCommentRes> getCommentRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 게시글 생성 - Post **/
    public int insertPost(PostPostsReq postPostsReq){
        // Post - Post
        // userIdx, title, content, isAnonymous
        String insertPostQuery = "insert into Post (userIdx, title, content, type, isAnonymous) VALUES (?,?,?,?,?)";

        Object[] insertPostParams = new Object[]{
                postPostsReq.getUserIdx(),
                postPostsReq.getTitle(),
                postPostsReq.getContent(),
                postPostsReq.getType(),
                postPostsReq.getIsAnonymous()};

        this.jdbcTemplate.update(insertPostQuery, insertPostParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    /** 게시글 생성 - PostImage **/
    public void insertPostImage(int postIdx, String postImageUrl){
        // Post - PostImage
        // postIdx, postImageUrl
        String insertPostImageQuery = "insert into PostImage (postIdx, postImageUrl) VALUES (?,?)";
        Object[] insertPostImageParams = new Object[]{postIdx, postImageUrl};

        this.jdbcTemplate.update(insertPostImageQuery, insertPostImageParams);
        String lastInsertIdQuery = "select last_insert_id()";
        System.out.println(this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class));

    }

    /** 게시글 존재여부 확인 **/
    public int checkPost(Integer postIdx){
        String checkEmailQuery = "select exists(select postIdx from Post where postIdx = ? and status='ACTIVE')";
        Integer checkEmailParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);
    }

    /** 유저가 접근가능한 게시글인지 확인 **/
    public Long checkPostUser(Integer postIdx){
        String checkPostUserQuery = "select userIdx from Post where postIdx = ? and status='ACTIVE'";
        Integer checkPostUserParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostUserQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> rs.getLong("userIdx"), checkPostUserParams);
    }

    /** 게시글 삭제 - Post **/
    public int deletePost(Integer postIdx){
        String deletePostQuery = "UPDATE Post SET status='DELETED' WHERE postIdx=?";
        Object[] deletePostParams = new Object[] {postIdx};
        return this.jdbcTemplate.update(deletePostQuery, deletePostParams);

    }

    /** 게시글 삭제 - 이미지 파일의 경로 get - PostImage **/
    public List<String> getPostImage(Integer postIdx){
        // Get - PostImage
        // postImageUrl
        String getPostImageQuery = "select postImageUrl from PostImage where postIdx=? and status='ACTIVE'";
        Integer getPostImageParams = postIdx;

        List<String> postImageUrls = this.jdbcTemplate.query(getPostImageQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> rs.getString("postImageUrl"), getPostImageParams);

        return postImageUrls;
    }

    /** 게시글 삭제 - PostImage **/
    public int deletePostImage(Integer postIdx){
        String deletePostQuery = "UPDATE PostImage SET status='DELETED' WHERE postIdx=?";
        Object[] deletePostParams = new Object[] {postIdx};
        return this.jdbcTemplate.update(deletePostQuery, deletePostParams);

    }

    /** 게시글 좋아요 - PostLike **/
    public int insertPostLike(Long userIdx, Integer postIdx){
        // Post - PostLike
        // userIdx, postIdx
        String insertPostLikeQuery = "insert into PostLike (userIdx, postIdx) VALUES (?,?)";

        Object[] insertPostLikeParams = new Object[]{
                userIdx,
                postIdx};

        this.jdbcTemplate.update(insertPostLikeQuery, insertPostLikeParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    /** 게시글 좋아요 존재여부 확인 **/
    public int checkPostLike(Integer postLikeIdx){
        String checkPostLikeQuery = "select exists(select postLikeIdx from PostLike where postLikeIdx = ?)";
        Integer checkPostLikeParams = postLikeIdx;
        return this.jdbcTemplate.queryForObject(checkPostLikeQuery,
                int.class,
                checkPostLikeParams);
    }

    /** 유저가 접근가능한 게시글 좋아요인지 확인 **/
    public Long checkPostLikeUser(Integer postLikeIdx){
        String checkPostLikeUserQuery = "select userIdx from PostLike where postLikeIdx = ?";
        Integer checkPostLikeUserParams = postLikeIdx;
        return this.jdbcTemplate.queryForObject(checkPostLikeUserQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> rs.getLong("userIdx"), checkPostLikeUserParams);
    }

    /** 게시글 좋아요 취소 - PostLike **/
    public void deletePostLike(Integer postLikeIdx){
        // Delete - PostLike
        // postLikeIdx
        String deletePostLikeQuery = "delete from PostLike where postLikeIdx = ?";

        Integer deletePostLikeParams = postLikeIdx;

        this.jdbcTemplate.update(deletePostLikeQuery, deletePostLikeParams);
    }

    /** 게시글 저장 - PostSave **/
    public int insertPostSave(Long userIdx, Integer postIdx){
        // Post - PostSave
        // userIdx, postIdx
        String insertPostSaveQuery = "insert into PostSave (userIdx, postIdx) VALUES (?,?)";

        Object[] insertPostSaveParams = new Object[]{
                userIdx,
                postIdx};

        this.jdbcTemplate.update(insertPostSaveQuery, insertPostSaveParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    /** 게시글 저장 존재여부 확인 **/
    public int checkPostSave(Integer postSaveIdx){
        String checkPostSaveQuery = "select exists(select postSaveIdx from PostSave where postSaveIdx = ?)";
        Integer checkPostSaveParams = postSaveIdx;
        return this.jdbcTemplate.queryForObject(checkPostSaveQuery,
                int.class,
                checkPostSaveParams);
    }

    /** 유저가 접근가능한 게시글 저장인지 확인 **/
    public Long checkPostSaveUser(Integer postSaveIdx){
        String checkPostSaveUserQuery = "select userIdx from PostSave where postSaveIdx = ?";
        Integer checkPostSaveUserParams = postSaveIdx;
        return this.jdbcTemplate.queryForObject(checkPostSaveUserQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> rs.getLong("userIdx"), checkPostSaveUserParams);
    }

    /** 게시글 저장 취소 - PostSave **/
    public void deletePostSave(Integer postSaveIdx){
        // Delete - PostSave
        // postSaveIdx
        String deletePostSaveQuery = "delete from PostSave where postSaveIdx = ?";

        Integer deletePostSaveParams = postSaveIdx;

        this.jdbcTemplate.update(deletePostSaveQuery, deletePostSaveParams);
    }

    /** 게시글 조회 **/
    public GetPostRes selectPost(Long userIdx, int postIdx, String accessToken){
        String selectPostQuery =
                "select U.userIdx, U.profileImgUrl, \n" +
                "       If(P.isAnonymous = 'Y', '익명', U.name) as name,\n" +
                "       P.postIdx, P.title, P.content,\n" +
                "       If(likeCount is null, 0, likeCount) as likeCount,\n" +
                "       If(commentCount is null, 0, commentCount) as commentCount,\n" +
                "       IF(exists(select userIdx from PostLike where userIdx=? and postIdx=?)=1, 'Y', 'N') as likeOrNot,\n" +
                "       IF(exists(select userIdx from PostSave where userIdx=? and postIdx=?)=1, 'Y', 'N') as saveOrNot,\n" +
                "       case\n" +
                "        when timestampdiff(day , P.updatedAt, current_timestamp) < 365\n" +
                "        then date_format(P.updatedAt, '%m/%d %h:%i')\n" +
                "        else date_format(P.updatedAt, '%Y/%m/%d %h:%i')\n" +
                "        end as updatedAt\n" +
                "from Post as P\n" +
                "    left join User U on P.userIdx = U.userIdx\n" +
                "    left join(select postIdx, userIdx, count(postLikeIdx)as likeCount from PostLike group by postIdx)\n" +
                "        PL on PL.postIdx = P.postIdx\n" +
                "    left join(select postIdx, userIdx, postSaveIdx from PostSave group by postIdx)\n" +
                "        PS on PS.postIdx = P.postIdx\n" +
                "    left join(select postIdx, commentIdx, count(commentIdx)as commentCount from Comment group by postIdx)\n" +
                "        C on C.postIdx = P.postIdx\n" +
                "where P.postIdx = ? and P.status = 'ACTIVE'";
        Object[] selectPostParam = new Object[] {userIdx, postIdx, userIdx, postIdx, postIdx};
        return this.jdbcTemplate.queryForObject(selectPostQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new GetPostRes(
                        rs.getInt("userIdx"),
                        rs.getString("profileImgUrl"),
                        rs.getString("name"),
                        rs.getString("title"),
                        rs.getString("content"),
                        getPostImageRes = this.jdbcTemplate.query(
                                "SELECT PI.postImageIdx, PI.postImageUrl\n" +
                                    "FROM PostImage as PI\n" +
                                    "join Post as P on P.postIdx = PI.postIdx\n" +
                                    "WHERE P.postIdx = ?",
                                (rk, rownum) -> new GetPostImageRes(
                                        rk.getInt("postImageIdx"),
                                        rk.getString("postImageUrl")
                                ), rs.getInt("postIdx")
                        ),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("updatedAt"),
                        rs.getString("likeOrNot"),
                        rs.getString("saveOrNot"),
                        getCommentRes = this.jdbcTemplate.query(
                                "SELECT C.commentIdx, U.userIdx,\n" +
                                    "       If(C.isAnonymous = 'Y', '익명', U.name) as name,\n" +
                                    "       U.profileImgUrl, C.content,\n" +
                                    "       case\n" +
                                    "        when timestampdiff(day , C.updatedAt, current_timestamp) < 365\n" +
                                    "        then date_format(C.updatedAt, '%m/%d %h:%i')\n" +
                                    "        else date_format(C.updatedAt, '%Y/%m/%d %h:%i')\n" +
                                    "        end as updatedAt\n" +
                                    "FROM Comment as C\n" +
                                    "    left join User U on C.userIdx = U.userIdx\n" +
                                    "    left join Post P on C.postIdx = P.postIdx\n" +
                                    "WHERE C.status = 'ACTIVE' and C.postIdx = ?",
                                (rc, rownuM) -> new GetCommentRes(
                                        rc.getInt("commentIdx"),
                                        rc.getInt("userIdx"),
                                        rc.getString("name"),
                                        rc.getString("profileImgUrl"),
                                        rc.getString("content"),
                                        rc.getString("updatedAt")
                                ), rs.getInt("postIdx")
                        ),
                        accessToken
                ), selectPostParam);
    }

    /** 관리자 공지 리스트 조회 **/
    public List<GetPostInfo> selectNoticeList(Long userIdx){
        String selectBuildingIdxQuery =
                "select buildingIdx\n" +
                        "from User\n" +
                        "where userIdx=?";
        int buildingIdx = this.jdbcTemplate.queryForObject(selectBuildingIdxQuery,
                (rs,rowNum) -> rs.getInt("buildingIdx"), userIdx);

        String selectNoticeListQuery =
                "select P.postIdx, P.title, P.content,\n" +
                "       IF(likeCount is null, 0, likeCount) as likeCount,\n" +
                "       If(commentCount is null, 0, commentCount) as commentCount,\n" +
                "       If(imageCount is null, 0, imageCount) as imageCount,\n" +
                "       case\n" +
                "        when timestampdiff(day , P.updatedAt, current_timestamp) < 365\n" +
                "        then date_format(P.updatedAt, '%m/%d %h:%i')\n" +
                "        else date_format(P.updatedAt, '%Y/%m/%d %h:%i')\n" +
                "        end as updatedAt\n" +
                "from Post as P\n" +
                "    left join (select userIdx, buildingIdx from User where userLevel='Manager')\n" +
                "        U on U.buildingIdx=?\n" +
                "    left join(select postIdx, userIdx, count(postLikeIdx)as likeCount from PostLike group by postIdx)\n" +
                "        PL on P.postIdx = PL.postIdx\n" +
                "    left join(select postIdx, count(commentIdx)as commentCount from Comment group by postIdx)\n" +
                "        C on C.postIdx = P.postIdx\n" +
                "    left join(select postIdx, count(postImageIdx)as imageCount from PostImage group by postIdx)\n" +
                "        PI on PI.postIdx = P.postIdx\n" +
                "where P.userIdx=U.userIdx and P.type='notice' and P.status='ACTIVE'\n" +
                "group by postIdx";
        return this.jdbcTemplate.query(selectNoticeListQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new GetPostInfo(
                        rs.getInt("postIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getInt("imageCount"),
                        rs.getString("updatedAt")
                ), buildingIdx);
    }

    /** 자유게시판 리스트 조회 **/
    public List<GetPostInfo> selectGeneralList(Long userIdx){
        String selectBuildingIdxQuery =
                "select buildingIdx\n" +
                        "from User\n" +
                        "where userIdx=?";
        int buildingIdx = this.jdbcTemplate.queryForObject(selectBuildingIdxQuery,
                (rs,rowNum) -> rs.getInt("buildingIdx"), userIdx);

        String selectNoticeListQuery =
                "select P.postIdx, P.title, P.content,\n" +
                "       IF(likeCount is null, 0, likeCount) as likeCount,\n" +
                "       If(commentCount is null, 0, commentCount) as commentCount,\n" +
                "       If(imageCount is null, 0, imageCount) as imageCount,\n" +
                "       case\n" +
                "        when timestampdiff(day , P.updatedAt, current_timestamp) < 365\n" +
                "        then date_format(P.updatedAt, '%m/%d %h:%i')\n" +
                "        else date_format(P.updatedAt, '%Y/%m/%d %h:%i')\n" +
                "        end as updatedAt\n" +
                "from Post as P\n" +
                "    left join (select userIdx, buildingIdx from User)\n" +
                "        U on U.buildingIdx=?\n" +
                "    left join(select postIdx, userIdx, count(postLikeIdx)as likeCount from PostLike group by postIdx)\n" +
                "        PL on P.postIdx = PL.postIdx\n" +
                "    left join(select postIdx, count(commentIdx)as commentCount from Comment group by postIdx)\n" +
                "        C on C.postIdx = P.postIdx\n" +
                "    left join(select postIdx, count(postImageIdx)as imageCount from PostImage group by postIdx)\n" +
                "        PI on PI.postIdx = P.postIdx\n" +
                "where P.userIdx=U.userIdx and P.type='general' and P.status='ACTIVE'\n" +
                "group by postIdx";
        return this.jdbcTemplate.query(selectNoticeListQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new GetPostInfo(
                        rs.getInt("postIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getInt("imageCount"),
                        rs.getString("updatedAt")
                ), buildingIdx);
    }

    /** 정보게시판 리스트 조회 **/
    public List<GetPostInfo> selectInformationList(Long userIdx){
        String selectBuildingIdxQuery =
                "select buildingIdx\n" +
                        "from User\n" +
                        "where userIdx=?";
        int buildingIdx = this.jdbcTemplate.queryForObject(selectBuildingIdxQuery,
                (rs,rowNum) -> rs.getInt("buildingIdx"), userIdx);

        String selectNoticeListQuery =
                "select P.postIdx, P.title, P.content,\n" +
                        "       IF(likeCount is null, 0, likeCount) as likeCount,\n" +
                        "       If(commentCount is null, 0, commentCount) as commentCount,\n" +
                        "       If(imageCount is null, 0, imageCount) as imageCount,\n" +
                        "       case\n" +
                        "        when timestampdiff(day , P.updatedAt, current_timestamp) < 365\n" +
                        "        then date_format(P.updatedAt, '%m/%d %h:%i')\n" +
                        "        else date_format(P.updatedAt, '%Y/%m/%d %h:%i')\n" +
                        "        end as updatedAt\n" +
                        "from Post as P\n" +
                        "    left join (select userIdx, buildingIdx from User)\n" +
                        "        U on U.buildingIdx=?\n" +
                        "    left join(select postIdx, userIdx, count(postLikeIdx)as likeCount from PostLike group by postIdx)\n" +
                        "        PL on P.postIdx = PL.postIdx\n" +
                        "    left join(select postIdx, count(commentIdx)as commentCount from Comment group by postIdx)\n" +
                        "        C on C.postIdx = P.postIdx\n" +
                        "    left join(select postIdx, count(postImageIdx)as imageCount from PostImage group by postIdx)\n" +
                        "        PI on PI.postIdx = P.postIdx\n" +
                        "where P.userIdx=U.userIdx and P.type='information' and P.status='ACTIVE'\n" +
                        "group by postIdx";
        return this.jdbcTemplate.query(selectNoticeListQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new GetPostInfo(
                        rs.getInt("postIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getInt("imageCount"),
                        rs.getString("updatedAt")
                ), buildingIdx);
    }

    /** 장터게시판-나눔 리스트 조회 **/
    public List<GetPostInfo> selectShareList(Long userIdx){
        String selectBuildingIdxQuery =
                "select buildingIdx\n" +
                        "from User\n" +
                        "where userIdx=?";
        int buildingIdx = this.jdbcTemplate.queryForObject(selectBuildingIdxQuery,
                (rs,rowNum) -> rs.getInt("buildingIdx"), userIdx);

        String selectNoticeListQuery =
                "select P.postIdx, P.title, P.content,\n" +
                        "       IF(likeCount is null, 0, likeCount) as likeCount,\n" +
                        "       If(commentCount is null, 0, commentCount) as commentCount,\n" +
                        "       If(imageCount is null, 0, imageCount) as imageCount,\n" +
                        "       case\n" +
                        "        when timestampdiff(day , P.updatedAt, current_timestamp) < 365\n" +
                        "        then date_format(P.updatedAt, '%m/%d %h:%i')\n" +
                        "        else date_format(P.updatedAt, '%Y/%m/%d %h:%i')\n" +
                        "        end as updatedAt\n" +
                        "from Post as P\n" +
                        "    left join (select userIdx, buildingIdx from User)\n" +
                        "        U on U.buildingIdx=?\n" +
                        "    left join(select postIdx, userIdx, count(postLikeIdx)as likeCount from PostLike group by postIdx)\n" +
                        "        PL on P.postIdx = PL.postIdx\n" +
                        "    left join(select postIdx, count(commentIdx)as commentCount from Comment group by postIdx)\n" +
                        "        C on C.postIdx = P.postIdx\n" +
                        "    left join(select postIdx, count(postImageIdx)as imageCount from PostImage group by postIdx)\n" +
                        "        PI on PI.postIdx = P.postIdx\n" +
                        "where P.userIdx=U.userIdx and P.type='share' and P.status='ACTIVE'\n" +
                        "group by postIdx";
        return this.jdbcTemplate.query(selectNoticeListQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new GetPostInfo(
                        rs.getInt("postIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getInt("imageCount"),
                        rs.getString("updatedAt")
                ), buildingIdx);
    }

    /** 장터게시판-공동구매 리스트 조회 **/
    public List<GetPostInfo> selectGroupList(Long userIdx){
        String selectBuildingIdxQuery =
                "select buildingIdx\n" +
                        "from User\n" +
                        "where userIdx=?";
        int buildingIdx = this.jdbcTemplate.queryForObject(selectBuildingIdxQuery,
                (rs,rowNum) -> rs.getInt("buildingIdx"), userIdx);

        String selectNoticeListQuery =
                "select P.postIdx, P.title, P.content,\n" +
                        "       IF(likeCount is null, 0, likeCount) as likeCount,\n" +
                        "       If(commentCount is null, 0, commentCount) as commentCount,\n" +
                        "       If(imageCount is null, 0, imageCount) as imageCount,\n" +
                        "       case\n" +
                        "        when timestampdiff(day , P.updatedAt, current_timestamp) < 365\n" +
                        "        then date_format(P.updatedAt, '%m/%d %h:%i')\n" +
                        "        else date_format(P.updatedAt, '%Y/%m/%d %h:%i')\n" +
                        "        end as updatedAt\n" +
                        "from Post as P\n" +
                        "    left join (select userIdx, buildingIdx from User)\n" +
                        "        U on U.buildingIdx=?\n" +
                        "    left join(select postIdx, userIdx, count(postLikeIdx)as likeCount from PostLike group by postIdx)\n" +
                        "        PL on P.postIdx = PL.postIdx\n" +
                        "    left join(select postIdx, count(commentIdx)as commentCount from Comment group by postIdx)\n" +
                        "        C on C.postIdx = P.postIdx\n" +
                        "    left join(select postIdx, count(postImageIdx)as imageCount from PostImage group by postIdx)\n" +
                        "        PI on PI.postIdx = P.postIdx\n" +
                        "where P.userIdx=U.userIdx and P.type='group' and P.status='ACTIVE'\n" +
                        "group by postIdx";
        return this.jdbcTemplate.query(selectNoticeListQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new GetPostInfo(
                        rs.getInt("postIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getInt("imageCount"),
                        rs.getString("updatedAt")
                ), buildingIdx);
    }

    /** 장터게시판-중고거래 리스트 조회 **/
    public List<GetPostInfo> selectSecondhandList(Long userIdx){
        String selectBuildingIdxQuery =
                "select buildingIdx\n" +
                        "from User\n" +
                        "where userIdx=?";
        int buildingIdx = this.jdbcTemplate.queryForObject(selectBuildingIdxQuery,
                (rs,rowNum) -> rs.getInt("buildingIdx"), userIdx);

        String selectSecondhandListQuery =
                "select P.postIdx, P.title, P.content,\n" +
                        "       IF(likeCount is null, 0, likeCount) as likeCount,\n" +
                        "       If(commentCount is null, 0, commentCount) as commentCount,\n" +
                        "       If(imageCount is null, 0, imageCount) as imageCount,\n" +
                        "       case\n" +
                        "        when timestampdiff(day , P.updatedAt, current_timestamp) < 365\n" +
                        "        then date_format(P.updatedAt, '%m/%d %h:%i')\n" +
                        "        else date_format(P.updatedAt, '%Y/%m/%d %h:%i')\n" +
                        "        end as updatedAt\n" +
                        "from Post as P\n" +
                        "    left join (select userIdx, buildingIdx from User)\n" +
                        "        U on U.buildingIdx=?\n" +
                        "    left join(select postIdx, userIdx, count(postLikeIdx)as likeCount from PostLike group by postIdx)\n" +
                        "        PL on P.postIdx = PL.postIdx\n" +
                        "    left join(select postIdx, count(commentIdx)as commentCount from Comment group by postIdx)\n" +
                        "        C on C.postIdx = P.postIdx\n" +
                        "    left join(select postIdx, count(postImageIdx)as imageCount from PostImage group by postIdx)\n" +
                        "        PI on PI.postIdx = P.postIdx\n" +
                        "where P.userIdx=U.userIdx and P.type='secondhand' and P.status='ACTIVE'\n" +
                        "group by postIdx";
        return this.jdbcTemplate.query(selectSecondhandListQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new GetPostInfo(
                        rs.getInt("postIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getInt("imageCount"),
                        rs.getString("updatedAt")
                ), buildingIdx);
    }

    /** 내가 작성한 글 조회 **/
    public List<GetPostInfo> selectMypostList(Long userIdx){
        String selectMypostListQuery =
                "select P.postIdx, P.title, P.content,\n" +
                "       IF(likeCount is null, 0, likeCount) as likeCount,\n" +
                "       If(commentCount is null, 0, commentCount) as commentCount,\n" +
                "       If(imageCount is null, 0, imageCount) as imageCount,\n" +
                "       case\n" +
                "        when timestampdiff(day , P.updatedAt, current_timestamp) < 365\n" +
                "        then date_format(P.updatedAt, '%m/%d %h:%i')\n" +
                "        else date_format(P.updatedAt, '%Y/%m/%d %h:%i')\n" +
                "        end as updatedAt\n" +
                "from Post as P\n" +
                "    left join (select userIdx, buildingIdx from User)\n" +
                "        U on U.userIdx=?\n" +
                "    left join(select postIdx, userIdx, count(postLikeIdx)as likeCount from PostLike group by postIdx)\n" +
                "        PL on P.postIdx = PL.postIdx\n" +
                "    left join(select postIdx, count(commentIdx)as commentCount from Comment group by postIdx)\n" +
                "        C on C.postIdx = P.postIdx\n" +
                "    left join(select postIdx, count(postImageIdx)as imageCount from PostImage group by postIdx)\n" +
                "        PI on PI.postIdx = P.postIdx\n" +
                "where P.userIdx=U.userIdx and P.status='ACTIVE'\n" +
                "group by postIdx";
        return this.jdbcTemplate.query(selectMypostListQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new GetPostInfo(
                        rs.getInt("postIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getInt("imageCount"),
                        rs.getString("updatedAt")
                ), userIdx);
    }

    /** 내가 스크랩한 글 조회 **/
    public List<GetPostInfo> selectMysaveList(Long userIdx){
        String selectMysaveListQuery =
                "select P.postIdx, P.title, P.content,\n" +
                "       IF(likeCount is null, 0, likeCount) as likeCount,\n" +
                "       If(commentCount is null, 0, commentCount) as commentCount,\n" +
                "       If(imageCount is null, 0, imageCount) as imageCount,\n" +
                "       case\n" +
                "        when timestampdiff(day , P.updatedAt, current_timestamp) < 365\n" +
                "        then date_format(P.updatedAt, '%m/%d %h:%i')\n" +
                "        else date_format(P.updatedAt, '%Y/%m/%d %h:%i')\n" +
                "        end as updatedAt\n" +
                "from Post as P\n" +
                "    left join User U on P.userIdx=U.userIdx\n" +
                "    left join PostSave PS on P.postIdx = PS.postIdx\n" +
                "    left join(select postIdx, userIdx, count(postLikeIdx)as likeCount from PostLike group by postIdx)\n" +
                "        PL on P.postIdx = PL.postIdx\n" +
                "    left join(select postIdx, count(commentIdx)as commentCount from Comment group by postIdx)\n" +
                "        C on C.postIdx = P.postIdx\n" +
                "    left join(select postIdx, count(postImageIdx)as imageCount from PostImage group by postIdx)\n" +
                "        PI on PI.postIdx = P.postIdx\n" +
                "where PS.userIdx=? and P.status='ACTIVE'\n" +
                "group by postIdx";
        return this.jdbcTemplate.query(selectMysaveListQuery, // 리스트면 query, 리스트가 아니면 queryForObject
                (rs,rowNum) -> new GetPostInfo(
                        rs.getInt("postIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getInt("imageCount"),
                        rs.getString("updatedAt")
                ), userIdx);
    }

}
