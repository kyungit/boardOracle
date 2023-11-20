package com.board.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.board.dto.BoardDTO;
import com.board.dto.FileDTO;
import com.board.dto.MemberDTO;
import com.board.dto.ReplyDTO;
import com.board.dto.StatsDTO;
@Mapper
public interface MasterMapper {

	//모든 회원 정보 조회
	public List<MemberDTO> allMember(Map<String,Object> data);
	
	//총 회원 수
	public int allMemberCount();
	
	public int allMemberSearchCount(String keyword);

	public List<BoardDTO> list(Map<String, Object> data);

	public int getTotalCountByMember(String keyword);

	public int getTotalCount();

	public List<ReplyDTO> replyList(Map<String, Object> data);

	public int getTotalReplyCountByMember(String keyword);

	public int getTotalReplyCount();

	public List<FileDTO> checkfileXSearch();

	public void checkfileDelete();

	public List<StatsDTO> statsBoard(String keyword);

	public List<StatsDTO> statsReply(String keyword);

	public List<StatsDTO> statsMember(String keyword);

	public void deleteBoardByMaster(int seqno);

	public void fileInfoUpdate(int seqno);

	public void deleteReplyByMaster(int replyseqno);

	

}
