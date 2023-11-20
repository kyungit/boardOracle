package com.board.service;

import java.util.List;

import com.board.dto.BoardDTO;
import com.board.dto.FileDTO;
import com.board.dto.MemberDTO;
import com.board.dto.ReplyDTO;
import com.board.dto.StatsDTO;

public interface MasterService {
	
	public List<MemberDTO> allMember(int startPoint,int endPoint,String keyword);
	
	public int allMemberCount();
	
	public int allMemberSearchCount(String keyword);

	public List<BoardDTO> list(int startPoint, int endPoint, String keyword) throws Exception;
	
	//멤버별 게시물 전체 갯수 계산
	public int getTotalCountByMember(String keyword) throws Exception;
	
	//게시물 전체 갯수
	public int getTotalCount() throws Exception;
	
	public List<ReplyDTO> replyList(int startPoint,int endPoint,String keyword);
	
	public int getTotalReplyCountByMember(String keyword);
	
	public int getTotalReplyCount() throws Exception;
	
	//첨부파일 삭제 x조회
	public List<FileDTO> checkfileXSearch() throws Exception;
	
	//첨부파일 삭제
	public void checkfileDelete() throws Exception;

	public List<StatsDTO> statsBoard(String keyword);

	public List<StatsDTO> statsReply(String keyword);

	public List<StatsDTO> statsMember(String keyword);

	public void deleteBoardByMaster(int seqno);

	public void fileInfoUpdate(int seqno);

	public void deleteReplyByMaster(int replyseqno);

}
