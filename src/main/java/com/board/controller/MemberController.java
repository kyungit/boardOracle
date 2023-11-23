package com.board.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.board.dto.AddressDTO;
import com.board.dto.FileDTO;
import com.board.dto.MemberDTO;
import com.board.service.MemberService;
import com.board.util.Page;

@Controller
public class MemberController {

	@Autowired
	MemberService service;
	
	@Autowired
	private BCryptPasswordEncoder pwdEncoder;
	
	//로그인 화면 보기
	@GetMapping("/member/login")
	public void getLogin() { }
	
	//로그인 
	@ResponseBody
	@PostMapping("/member/login")
	public String postLogin(MemberDTO member,HttpSession session,@RequestParam("autologin") String autologin) {
		
		String authkey = "";
		
	    
		String pwStatus = "N";
		System.out.println("로그인 컨트롤러");
		//로그인 시 authkey 생성  
		if(autologin.equals("NEW")) {
			authkey = UUID.randomUUID().toString().replaceAll("-", "");
			member.setAuthkey(authkey);
			service.authkeyUpdate(member);			
		}
		
		//authkey가 클라이언트에 쿠키로 존재할 경우 로그인 과정 없이 세션 생성 후 게시판 목록 페이지로 이동
		if(autologin.equals("PASS")) {
			System.out.println("PASS 로그인");
			//MemberDTO memberInfo = service.memberInfo(member.getUserid());
			//memberInfo.setAuthkey(service.memberInfoByAuthkey(member).getAuthkey()); 
			MemberDTO memberInfo = service.memberInfoByAuthkey(member);
			//if(memberInfo != null) {
				//System.out.println("lastpwdate컨트롤러 : " + service.memberInfo(member.getUserid()).getLastpwdate());
				
			
				 LocalDate lastpwdate = memberInfo.getLastpwdate();
				 int pwCheck = memberInfo.getPwcheck();
				 LocalDate after30 = lastpwdate.plusDays(pwCheck * 30);
				 System.out.println("pwStatus1 = " + pwStatus);
				 if (LocalDate.now().isAfter(after30)) {
					 pwStatus = "Y"; 
					 System.out.println("pwStatus2 = " + pwStatus);
					 return "{\"userid\" : \"" + memberInfo.getUserid() + "\" , \"pwStatus\" : \"" + pwStatus + "\",\"username\" : \"" +memberInfo.getUsername()+ "\",\"pwcheck\" : \"" +memberInfo.getPwcheck()+ "\"}";
				 }
				 
				 
				
				//세션 생성
				session.setMaxInactiveInterval(3600*24*7);//세션 유지 기간 설정
				session.setAttribute("userid", memberInfo.getUserid());
				session.setAttribute("username", memberInfo.getUsername());
				session.setAttribute("role", memberInfo.getRole());
				//session.setAttribute("pwStatus", pwStatus);
				session.setAttribute("pwcheck", memberInfo.getPwcheck());
				session.setAttribute("lastpwdate", memberInfo.getLastpwdate());
				//System.out.println("lastpwdate 세션 생성(member컨트롤러) : " + service.memberInfo(member.getUserid()).getLastpwdate());
					
				
				return "{\"message\":\"GOOD\"}";
				//return "{\"message\":\"GOOD\",\"pwStatus\":\"" +pwStatus+ "\"}";
			//}
			
		}		
		
		//아이디 존재 여부 확인
		if(service.idCheck(member.getUserid()) == 0) {
			return "{\"message\":\"ID_NOT_FOUND\"}";
		}
		
		//패스워드가 올바르게 들어 왔는지 확인
		if(!pwdEncoder.matches(member.getPassword(), service.memberInfo(member.getUserid()).getPassword())) {
			//잘못된 패스워드 일때
			return "{\"message\":\"PASSWORD_NOT_FOUND\"}";
		}else {
			//제대로 된 아이디와 패스워드가 입력되었을 때
			
			
			
			//패스워드 확인 후 마지막 패스워드 변경일이 30일이 경과 되었을 경우 ...
			 
			 LocalDate lastpwdate = service.memberInfo(member.getUserid()).getLastpwdate();
			 int pwCheck = service.memberInfo(member.getUserid()).getPwcheck(); LocalDate after30 =
			 lastpwdate.plusDays(pwCheck * 30);
			 if (LocalDate.now().isAfter(after30)) {
				 pwStatus = "Y"; 
				 return "{\"userid\" : \"" + service.memberInfo(member.getUserid()).getUserid() + "\" , \"pwStatus\" : \"" + pwStatus + "\",\"username\" : \"" +service.memberInfo(member.getUserid()).getUsername()+ "\",\"pwcheck\" : \"" +service.memberInfo(member.getUserid()).getPwcheck()+ "\"}";
			 }
			 
			 
			//마지막 로그인 날짜 등록
			member.setLastlogindate(LocalDate.now());
			service.lastlogindateUpdate(member);	
			//세션 생성
			session.setMaxInactiveInterval(3600*24*7);//세션 유지 기간 설정
			session.setAttribute("userid", service.memberInfo(member.getUserid()).getUserid());
			session.setAttribute("username", service.memberInfo(member.getUserid()).getUsername());
			session.setAttribute("role", service.memberInfo(member.getUserid()).getRole());
			session.setAttribute("pwStatus", pwStatus);
			//session.setAttribute("pwcheck", service.memberInfo(member.getUserid()).getPwcheck());
			//session.setAttribute("lastpwdate", service.memberInfo(member.getUserid()).getLastpwdate());
			
			//System.out.println("pwstatus(controller) : " + pwStatus);
			
						
			return "{\"message\":\"GOOD\",\"authkey\":\"" + member.getAuthkey() + "\"}";
			
			//return "{\"message\":\"GOOD\"				,\"pwStatus\":\"" +pwStatus+ "\"}";
			//return "{\"message\":\"GOOD\",\"authkey\":\"" + member.getAuthkey() + "\",\"pwStatus\" : \"" + pwStatus + "\"}";
		}		
	}
	
	//회원 등록 화면 보기
	@GetMapping("/member/signup")
	public void getSignup() {	}
	
	//회원 등록 하기
	@ResponseBody
	@PostMapping("/member/signup")
	public Map<String,String> postSignup(MemberDTO member, @RequestParam("fileUpload") MultipartFile multipartFile) throws Exception {
		
		String path = "c:\\Repository\\profile\\";
		File targetFile;
		
		if(!multipartFile.isEmpty()) {
			
			String org_filename = multipartFile.getOriginalFilename();
			String org_fileExtension = org_filename.substring(org_filename.lastIndexOf("."));			
			String stored_filename = UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;
			
			try {
				targetFile = new File(path + stored_filename);				
				multipartFile.transferTo(targetFile);
				
				member.setOrg_filename(org_filename);
				member.setStored_filename(stored_filename);
				member.setFilesize(multipartFile.getSize());				
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			String inputPassword = member.getPassword();
			String pwd = pwdEncoder.encode(inputPassword); //단방향 암호화 
			member.setPassword(pwd);
			member.setLastpwdate(LocalDate.now());
			
		}
		
		service.memberInfoRegistry(member);
		
		Map<String,String> data = new HashMap<>();
		data.put("message", "GOOD");
		data.put("username", URLEncoder.encode(member.getUsername(),"UTF-8"));
		
		return data;
		
		//return "{\"message\":\"GOOD\",\"username\":\"" + URLEncoder.encode(member.getUsername(),"UTF-8") + "\"}";
	}
	
	//회원 가입 시 아이디 중복 확인
	@ResponseBody
	@PostMapping("/member/idCheck")
	public int postIdCheck(@RequestBody String userid) throws Exception{
		
		int result = service.idCheck(userid);
		return result;
	}
	
	//회원 정보 보기
	@GetMapping("/member/memberInfo")
	public void getMemberInfo(HttpSession session, Model model,@RequestParam(name="userid",defaultValue="",required=false) String userid) throws Exception {
		String session_userid = (String)session.getAttribute("userid");
		String userIdController = userid.equals("")?  session_userid:userid;
		model.addAttribute("memberInfo", service.memberInfo(userIdController));
		
	}
	
	//회원 패스워드 변경
	@GetMapping("/member/memberPasswordModify")
	public void getMemberPasswordModify() throws Exception { }
	
	//회원 기본 정보 변경
	@GetMapping("/member/memberInfoModify")
	public void getMemberInfoModify(Model model,HttpSession session) {
		String userid = (String) session.getAttribute("userid");
		MemberDTO member = service.memberInfo(userid);
		model.addAttribute("member",member);
		
	}
	
	//회원 정보 수정
	@ResponseBody
	@PostMapping("/member/memberInfoModify")
	public Map<String,String> postMemberInfoModify(MemberDTO member, @RequestParam("fileUpload") MultipartFile multipartFile, HttpSession session) throws Exception {
		String path = "c:\\Repository\\profile\\";
		File targetFile;
		
		String userid = (String)session.getAttribute("userid");
		
		MemberDTO memberDB = service.memberInfo(userid);
		
		
		
		memberDB.setGender(member.getGender());
		memberDB.setHobby(member.getHobby());
		memberDB.setJob(member.getJob());
		memberDB.setAddress(member.getAddress());
		memberDB.setTelno(member.getTelno());
		memberDB.setEmail(member.getEmail());
		memberDB.setDescription(member.getDescription());
		System.out.println("---------------------stored_filename : " + memberDB.getStored_filename());
		
		if(!multipartFile.isEmpty()) {
			System.out.println("*****************multipartFile********************");
			
			File file = new File("c:\\Repository\\profile\\");
			File[] fileList = file.listFiles();
			
					
			for(int i=0;i<fileList.length;i++) {
	    		if(fileList[i].getName().equals(memberDB.getStored_filename())) {
	    			fileList[i].delete();
	    		}
	    	}
			
			
			String org_filename = multipartFile.getOriginalFilename();
			String org_fileExtension = org_filename.substring(org_filename.lastIndexOf("."));			
			String stored_filename = UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;
			
			try {
				targetFile = new File(path + stored_filename);				
				multipartFile.transferTo(targetFile);
				
				memberDB.setOrg_filename(org_filename);
				memberDB.setStored_filename(stored_filename);
				memberDB.setFilesize(multipartFile.getSize());	
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
					
		}
		
		
		
		service.memberInfoModify(memberDB);
		
		Map<String,String> data = new HashMap<>();
		data.put("status", "GOOD");
		data.put("username", URLEncoder.encode(member.getUsername(),"UTF-8"));
		return data;
		//return "{\"message\":\"GOOD\",\"username\":\"" + URLEncoder.encode(member.getUsername(),"UTF-8") + "\"}";
	}
	
	//회원 패스워드 변경
	@ResponseBody
	@PostMapping("/member/memberPasswordModify")
	public String postMemberPasswordModify(@RequestParam("old_password") String old_password, 
				@RequestParam("new_password") String new_password, HttpSession session,
				@RequestParam(name="userid",defaultValue="",required=false) String userid) throws Exception { 
		
		
		
		System.out.println("패스워드 변경 userid : " + userid);
		//패스워드가 올바르게 들어 왔는지 확인
//		if(!pwdEncoder.matches(old_password, service.memberInfo(userid).getPassword())) {
//			return "{\"message\":\"PASSWORD_NOT_FOUND\"}";
//		}
		
		if(userid.equals("")) {
			userid=(String)session.getAttribute("userid");
		}
		if(!pwdEncoder.matches(old_password, service.memberInfo(userid).getPassword())) {
			return "{\"message\":\"PASSWORD_NOT_FOUND\"}";
		}
		
		//신규 패스워드로 업데이트
		MemberDTO member = new MemberDTO();
		member.setUserid(userid);		
		member.setPassword(pwdEncoder.encode(new_password));
		member.setLastpwdate(LocalDate.now());
		service.memberPasswordModify(member);
		
		return "{\"message\":\"GOOD\"}";
	}
	
	//로그아웃
	@GetMapping("/member/logout")
	public void getLogout(HttpSession session,Model model) {
		String userid = (String)session.getAttribute("userid");
		String username = (String)session.getAttribute("username");
		
		MemberDTO member = new MemberDTO();
		member.setUserid(userid);
		member.setLastlogoutdate(LocalDate.now());
		
		service.lastlogoutdateUpdate(member);
		
		model.addAttribute("userid", userid);
		model.addAttribute("username", username);
		session.invalidate(); //모든 세션 종료 --> 로그아웃...
	}
	
	//패스워드 변경 후 세션 종료
	@GetMapping("/member/memberSessionOut")
	public String getMemberSessionOut(HttpSession session) {
		
		MemberDTO member = new MemberDTO();
		member.setUserid((String)session.getAttribute("userid"));
		member.setLastlogoutdate(LocalDate.now());
		service.lastlogoutdateUpdate(member);
		session.invalidate();	
		
		return "redirect:/";
	}
	
	//아이디 찾기
	@GetMapping("/member/searchID")
	public void getSearchID() {}
	
	//아이디 찾기
	@ResponseBody
	@PostMapping("/member/searchID")
	public String postSearchID(MemberDTO member) {
		
		String userid = service.searchID(member) == null?"ID_NOT_FOUND":service.searchID(member);		
		return "{\"message\":\"" + userid + "\"}";
	}
	
	//임시 패스워드 생성
	@GetMapping("/member/searchPassword")
	public void getSearchPassword() {}
	
	//임시 패스워드 생성
	@ResponseBody
	@PostMapping("/member/searchPassword")
	public String postSearchPassword(MemberDTO member) throws Exception{
		//아이디 존재 여부 확인
		if(service.idCheck(member.getUserid()) == 0)
			return "{\"status\":\"ID_NOT_FOUND\"}";
		//TELNO 확인
		if(!service.memberInfo(member.getUserid()).getTelno().equals(member.getTelno()))
			return "{\"status\":\"TELNO_NOT_FOUND\"}";
		
		//임시 패스워드 생성	
		String rawTempPW = service.tempPasswordMaker();
		member.setPassword(pwdEncoder.encode(rawTempPW));
		member.setLastpwdate(LocalDate.now());
		service.memberPasswordModify(member);

		return "{\"status\":\"GOOD\",\"password\":\"" + rawTempPW + "\"}";
	}
	
	//로그인 시 패스워드 변경 여부 확인
	@GetMapping("/member/pwCheckNotice")
	public void getPWCheckNotice(@RequestParam("userid") String userid, Model model) throws Exception {
		model.addAttribute("userid",userid);
	}
	
	//회원 탈퇴
	@GetMapping("/member/memberDrop")
	public void getMemberDrop() throws Exception{
		
		
		
	}
	
	//탈퇴 하기
	@ResponseBody
	@PostMapping("member/memberDrop")
	public String postMemberDrop(HttpSession session, MemberDTO member) throws Exception{
		String userid = (String)session.getAttribute("userid");
		//넣는건 세션의 userid
		
		if(!member.getUserid().equals(userid)) {
			return "{\"status\" : \"ID_NOT_FOUND\"}";
		}
		//패스워드가 올바르게 들어 왔는지 확인
		else if(!pwdEncoder.matches(member.getPassword(), service.memberInfo(member.getUserid()).getPassword())) {
			//잘못된 패스워드 일때
			return "{\"message\":\"PASSWORD_NOT_FOUND\"}";
		}
		else {
			File file = new File("c:\\Repository\\profile\\");
			File[] fileList = file.listFiles();
			
					
			for(int i=0;i<fileList.length;i++) {
	    		if(fileList[i].getName().equals(member.getStored_filename())) {
	    			fileList[i].delete();
	    		}
	    	}
			service.checkfileXwhenDrop(userid);
			service.memberDrop(userid);
			return "{\"status\" : \"GOOD\"}";
			
		}
				
		
		
	}
	
	
	//주소 검색
	@GetMapping("/member/addrSearch")
	public void getAddrsearch(@RequestParam("addrSearch") String addrSearch, 
			@RequestParam("page") int pageNum, Model model) {
				
		int postNum = 5; //한 화면에 보여지는 게시물 행의 갯수
		int startPoint = (pageNum-1) * postNum + 1; //페이지 시작 게시물 번호
		int endPoint = pageNum * postNum;
		int pageListCount = 5; //화면 하단에 보여지는 페이지리스트의 페이지 갯수		
		int totalCount = service.addrTotalCount(addrSearch); //전체 게시물 갯수
		
		Page page = new Page();
		model.addAttribute("list", service.addrSearch(startPoint, endPoint, addrSearch));
		model.addAttribute("pageList", page.getPageAddress(pageNum, postNum, pageListCount, totalCount, addrSearch));
		
	}
	
	
	
	
	//패스워드 변경한지 30일 지난 경우 또 30일 이후에 알림
	@ResponseBody
	@PostMapping("/member/pwCheckNotice")
	public String postAfterThirty(HttpSession session,@RequestParam("userid") String userid) {
		
		service.pwCheckUpdate(userid);
		
		/*
		 * Map<String,Object> data = new HashMap<>(); data.put("status", "GOOD"); return
		 * data;
		 */
		return "{\"status\":\"GOOD\"}";
	}
	
	
}
