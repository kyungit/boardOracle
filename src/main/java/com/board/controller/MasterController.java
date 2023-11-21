package com.board.controller;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.board.dto.FileDTO;
import com.board.service.MasterService;
import com.board.dto.StatsDTO;
import com.board.util.Page;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MasterController {
	@Autowired
	private final MasterService service;
	@Autowired
    private DataSource dataSource;

	
	@GetMapping("/master/sysManager")
	public void getSysManager() {
		
	}
	
	//관리자용 회원 정보 보기
	@GetMapping("/master/sysMember")
	public void getSysMember(Model model,@RequestParam(name="page",defaultValue="1",required=false) int pageNum,
			@RequestParam(name="keyword",defaultValue="",required=false) String keyword) throws Exception{
		
	
		Page page = Page.getInstance();
		int postNum = page.getPostNum();
		//int postNum = 5; 									//한 화면에 보여지는 게시물 행의 갯수
		int startPoint = (pageNum-1) * postNum + 1; 		//페이지 시작 게시물 번호
		int endPoint = pageNum * postNum;
		int pageListCount = 5; 								//화면 하단에 보여지는 페이지리스트의 페이지 갯수		
		//int totalCount = service.allMemberCount(keyword); 	//전체 게시물 갯수	
		int totalCountSearch = service.allMemberSearchCount(keyword);
		int totalCount = service.allMemberCount();
		
		
		model.addAttribute("member", service.allMember(startPoint,endPoint,keyword));
		model.addAttribute("totalElement", totalCount);
		model.addAttribute("totalElementSearch", totalCountSearch);
		model.addAttribute("postNum", postNum);
		
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageList", page.getSysMemberList(pageNum, postNum, pageListCount,totalCountSearch,keyword));
		model.addAttribute("keyword",keyword);
		/*
		 * List<MemberDTO> member= service.allMember();
		 * model.addAttribute("member",member);
		 * model.addAttribute("allMember",service.allMemberCount());
		 */
		
	}
	
	//관리자 게시물 전체 조회
	@GetMapping("/master/sysBoard")
	public void getSysBoard(Model model,@RequestParam(name="page",defaultValue="1",required=false) int pageNum,
			@RequestParam(name="keyword",defaultValue="",required=false) String keyword) throws Exception  {
		//int postNum = 5; 									//한 화면에 보여지는 게시물 행의 갯수
	
		
		Page page = Page.getInstance();
		int postNum = page.getPostNum();
		int startPoint = (pageNum-1) * postNum + 1; 		//페이지 시작 게시물 번호
		int endPoint = pageNum * postNum;
		int pageListCount = 5; 								//화면 하단에 보여지는 페이지리스트의 페이지 갯수		
		int totalCountbyMember = service.getTotalCountByMember(keyword); 	//회원별 전체 게시물 갯수	
		
		int totalCount = service.getTotalCount();

		model.addAttribute("list", service.list(startPoint,endPoint,keyword));
		//model.addAttribute("writer", service.list(startPoint,endPoint,keyword).get(0).getWriter());
		model.addAttribute("totalElementByMember", totalCountbyMember);
		model.addAttribute("totalCount",totalCount);
		model.addAttribute("postNum", postNum);
		
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageList", page.getSysBoardList(pageNum, postNum, pageListCount,totalCountbyMember,keyword));
		
	}
	
	@Transactional
	@ResponseBody
	@PostMapping("/master/sysBoard")
	public String postSysBoard(@RequestParam("seqno") int seqno) {
		service.fileInfoUpdate(seqno);
		service.deleteBoardByMaster(seqno);
		return "{\"status\" : \"GOOD\" }";
		
	}
	
	
	//관리자용 댓글 수 조회
	@GetMapping("/master/sysReply")
	public void getSysReply(Model model,@RequestParam(name="page",defaultValue="1",required=false) int pageNum,
			@RequestParam(name="keyword",defaultValue="",required=false) String keyword) throws Exception {
		//int postNum = 5; 									//한 화면에 보여지는 게시물 행의 갯수
		
		Page page = Page.getInstance();
		int postNum = page.getPostNum();
		int startPoint = (pageNum-1) * postNum + 1; 		//페이지 시작 게시물 번호
		int endPoint = pageNum * postNum;
		int pageListCount = 5; 								//화면 하단에 보여지는 페이지리스트의 페이지 갯수		
		int totalCountbyMember = service.getTotalReplyCountByMember(keyword); 	//회원별 전체 게시물 갯수	
		
		int totalCount = service.getTotalReplyCount();
		
		

		model.addAttribute("reply", service.replyList(startPoint,endPoint,keyword));
		//model.addAttribute("writer", service.replyList(startPoint,endPoint,keyword).get(0).getReplywriter());
		model.addAttribute("totalElement", totalCountbyMember);
		model.addAttribute("postNum", postNum);
		model.addAttribute("totalCount",totalCount);
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageList", page.getSysReplyList(pageNum, postNum, pageListCount,totalCountbyMember,keyword));
	
	}
	
	@ResponseBody
	@PostMapping("/master/sysReply")
	public String postSysReply(@RequestParam("replyseqno") int replyseqno) {
		
		service.deleteReplyByMaster(replyseqno);

        return "{\"status\" : \"GOOD\" }";
		
	}
	
	
	@GetMapping("/master/fileDelete")
	public void getFileDelete(Model model) throws Exception {
		List<FileDTO> file = service.checkfileXSearch();
		int fileCount = file.size();
		
		model.addAttribute("file",file);
		model.addAttribute("fileCount",fileCount);
	}
	
	
	//실제 파일 삭제
	@Transactional
	@ResponseBody
	@PostMapping("/master/fileDelete")
	public String postFileDelete() throws Exception {
		
		//<!-- File file = new File(삭제할 파일 전체경로)  == ("c:\Repository\file\1234.jpg"); file.delete(); for문 돌리기 -->
		File file = new File("c:\\Repository\\file\\"); 
		File[] fileList = file.listFiles();
		System.out.println("fileList " + fileList[0].getName());
		
		List<FileDTO> fileListDB = service.checkfileXSearch();
       
		List<String> listddd = new ArrayList<>(); 
		
        //FileDTO fileDB = new FileDTO();
        
        for(int i = 0;i<fileListDB.size();i++) {
        	for(int j=0;j<fileList.length;j++) {

        		if(fileListDB.get(i).getStored_filename().equals(fileList[j].getName())) {
        			fileList[j].delete();
        		}
        	}
        }
        service.checkfileDelete();
        
        return "{\"status\" : \"GOOD\" }";
        
        
	}
	
	
	
	//관리자용 전체 회원 통계
	@GetMapping("/master/sysStats")
	public void getSysStats(Model model) throws Exception {
		
		int allMember = service.allMemberCount();
		int allBoard = service.getTotalCount();
		int allReply = service.getTotalReplyCount();
		
		model.addAttribute("allMember",allMember);
		model.addAttribute("allBoard",allBoard);
		model.addAttribute("allReply",allReply);
		
	} 
	
	//관리자용 전체 게시물 통계
	@GetMapping("/master/statsBoard")
	public void getStatsBoard(Model model,
			@RequestParam(name="keyword",defaultValue="",required=false) String keyword) throws Exception {
		List<StatsDTO> statsDTO = service.statsBoard(keyword);
		
		int totalCount = service.allMemberSearchCount(keyword);
		int totalBoardCount = service.getTotalCount();
		
		model.addAttribute("stats", statsDTO);
		model.addAttribute("totalElement", totalCount);
		model.addAttribute("totalBoardCount", totalBoardCount);
		model.addAttribute("keyword",keyword);
		
	} 
	
	
	@GetMapping("/master/statsReply")
	public void getStatsReply(Model model,
			@RequestParam(name="keyword",defaultValue="",required=false) String keyword) throws Exception {
		model.addAttribute("totalElement",service.getTotalReplyCount());
		model.addAttribute("stats",service.statsReply(keyword));
		model.addAttribute("keyword",keyword);
		model.addAttribute("totalElement",service.allMemberSearchCount(keyword));
		
	}
	
	@GetMapping("/master/statsMember")
	public void getStatsMember(Model model,
			@RequestParam(name="keyword",defaultValue="",required=false) String keyword) throws Exception {
		model.addAttribute("totalElement",service.allMemberCount());
		model.addAttribute("stats",service.statsMember(keyword));
		model.addAttribute("keyword",keyword);
		
		model.addAttribute("searchCount",service.statsMember(keyword).size());
	}
	  

	@GetMapping("/master/systemInfo")
	public void getSystemInfo(Model model) throws SQLException {
		model.addAttribute("osName", System.getProperty("os.name"));
        model.addAttribute("osVersion", System.getProperty("os.version"));
       
		model.addAttribute("javaVersion", System.getProperty("java.version"));
		model.addAttribute("JVMName", System.getProperty("java.vm.name"));
	    model.addAttribute("JVMVersion", System.getProperty("java.vm.version"));
	        
	    Runtime runtime = Runtime.getRuntime();
	    model.addAttribute("totalMemory", runtime.totalMemory());
	    model.addAttribute("freeMemory", runtime.freeMemory());
	    
	    
	    
	    DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        String driverName = metaData.getDriverName();
        String driverVersion = metaData.getDriverVersion();

        // 드라이버 정보 모델에 추가
        model.addAttribute("databaseDriverName", driverName);
        model.addAttribute("databaseDriverVersion", driverVersion);
        
        
        
        
	    // 현재 시간 정보 추가
	    model.addAttribute("currentTime", LocalDateTime.now());

	}
	
	
	
	@GetMapping("/master/sysBoardCss")
	public void getSysBoardCss(Model model) {
		int postNumNow = Page.getInstance().getPostNum();
		model.addAttribute("postNumNow",postNumNow);
		
		
	}
	@ResponseBody
	@PostMapping("/master/sysBoardCss")
	public String postSysBoardCss(int postNum) {
		Page.getInstance().setPostNum(postNum);
		
		return "{\"status\" : \"GOOD\" , \"page\" : \"" + postNum +"\"}";
	}
	
	
}
