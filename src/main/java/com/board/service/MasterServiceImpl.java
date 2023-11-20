package com.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.board.dto.BoardDTO;
import com.board.dto.FileDTO;
import com.board.dto.MemberDTO;
import com.board.dto.ReplyDTO;
import com.board.dto.StatsDTO;
import com.board.mapper.MasterMapper;

@Service
public class MasterServiceImpl implements MasterService {
	@Autowired
	private MasterMapper mapper;
	
	//모든 회원
	@Override
	public List<MemberDTO> allMember(int startPoint, int endPoint, String keyword){
		Map<String, Object> data = new HashMap<>();
		data.put("startPoint", startPoint);
		data.put("endPoint", endPoint);
		data.put("keyword", keyword);
		return mapper.allMember(data);
	}

	//총 회원 수
	@Override
	public int allMemberCount() {
		return mapper.allMemberCount();
	}
	
	//검색 결과 회원 수
	@Override
	public int allMemberSearchCount(String keyword) {
		// TODO Auto-generated method stub
		return mapper.allMemberSearchCount(keyword);

	}

	//게시글 전체 조회
	@Override
	public List<BoardDTO> list(int startPoint, int endPoint, String keyword) throws Exception {
		Map<String, Object> data = new HashMap<>();
		data.put("startPoint", startPoint);
		data.put("endPoint", endPoint);
		data.put("keyword", keyword);
		return mapper.list(data);
	}

	@Override
	public int getTotalCountByMember(String keyword) throws Exception {
		
		return mapper.getTotalCountByMember(keyword);
	}

	@Override
	public int getTotalCount() throws Exception {
		// TODO Auto-generated method stub
		return mapper.getTotalCount();
	}

	@Override
	public List<ReplyDTO> replyList(int startPoint, int endPoint, String keyword) {
		Map<String, Object> data = new HashMap<>();
		data.put("startPoint", startPoint);
		data.put("endPoint", endPoint);
		data.put("keyword", keyword);
		return mapper.replyList(data);
	}

	@Override
	public int getTotalReplyCountByMember(String keyword) {
		// TODO Auto-generated method stub
		return  mapper.getTotalReplyCountByMember(keyword);
	}

	@Override
	public int getTotalReplyCount() throws Exception {
		// TODO Auto-generated method stub
		return  mapper.getTotalReplyCount();
	}

	//첨부파일 삭제 체크파일x 조회
	@Override
	public List<FileDTO> checkfileXSearch() throws Exception {
		// TODO Auto-generated method stub
		return mapper.checkfileXSearch();
	}

	@Override
	public void checkfileDelete() throws Exception {
		mapper.checkfileDelete();
		
	}

	@Override
	public List<StatsDTO> statsBoard(String keyword) {
		
		return mapper.statsBoard(keyword);
	}

	@Override
	public List<StatsDTO> statsReply(String keyword) {
		
		return mapper.statsReply(keyword);
	}

	@Override
	public List<StatsDTO> statsMember(String keyword) {
		
		return mapper.statsMember(keyword);
	}

	@Override
	public void deleteBoardByMaster(int seqno) {
		mapper.deleteBoardByMaster(seqno);
		
	}

	@Override
	public void fileInfoUpdate(int seqno) {
		mapper.fileInfoUpdate(seqno);
		
	}

	@Override
	public void deleteReplyByMaster(int replyseqno) {
		mapper.deleteReplyByMaster(replyseqno);
		
	}

	
	
	
	
	
}
