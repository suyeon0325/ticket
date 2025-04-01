package com.moaplace.controller.admin;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moaplace.dto.AdminListDTO;
import com.moaplace.dto.AdminNoticeDetailDTO;
import com.moaplace.service.AdminNoticeService;
import com.moaplace.util.FileUtil;
import com.moaplace.util.PageUtil;
import com.moaplace.vo.AdminNoticeVO;

import lombok.extern.log4j.Log4j;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin/news")
@Log4j
public class MoaplaceController {
	@Autowired
	private AdminNoticeService service;
	@Autowired
	private FileUtil fileutil;


	//링크 접속 테스트 용
//	@GetMapping("/test")
//  public String test() {
//	  return "success";
//  }
  
	//공지사항 등록 
	@PostMapping(value = "/insert",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String newsinsert(@RequestPart("files") List<MultipartFile> multipartFile, 
			@RequestParam String title, @RequestParam String content, @RequestParam String sort_num) {
	  	
	  //받아온 값 확인
	  System.out.println("title="+title);
	  System.out.println("content="+content);
	  System.out.println("file="+multipartFile);
	  System.out.println("sort_num="+sort_num);
	  
	  log.info("========================컨트롤러==========================");
	  log.info("sort_num : " + sort_num);
	  log.info("========================================================");
	  
	  AdminNoticeVO vo = new AdminNoticeVO(0, 1, Integer.parseInt(sort_num) , title, content, null , 0);
	  int n = service.insert(multipartFile, vo);
	  if(n==0 || n==1) {
		  return "success";
	  }else {
		  return "fail";
	  }  
  }
	 
	//공지사항 리스트 - 리스트 및 검색
	@GetMapping(value= {
			"/list/{sort_num}",
			"/list/{sort_num}/{pageNum}",
			"/list/{sort_num}/{field}/{keyword}",
			"/list/{sort_num}/{field}/{keyword}/{pageNum}"}, produces = { MediaType.APPLICATION_JSON_VALUE })
	public HashMap<String, Object> list(
			@PathVariable(required = false) Integer sort_num,
			@PathVariable(required = false) Integer pageNum,
			@PathVariable(required = false) String field,
			@PathVariable(required = false) String keyword) {
		log.info(sort_num);
		log.info("pageNum:" + pageNum);
		log.info("field:" +field);
		log.info("keyword:" + keyword);
		
		if(sort_num == null) {
			sort_num = 0;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("sort_num", sort_num);
		map.put("field",field);
		map.put("keyword",keyword);
	
		if(pageNum == null) {
			pageNum = 1;
		}
		int totalRowCount = service.getCount(map); //전체 글 개수
		PageUtil pageutil = new PageUtil(pageNum,5,5,totalRowCount);
		
		int startRow = pageutil.getStartRow(); //시작행 번호
		int endRow = pageutil.getEndRow(); //끝행번호
		int startPageNum = pageutil.getStartPageNum(); //시작 페이지 번호
		int endPageNum = pageutil.getEndPageNum(); //끝 페이지 번호
		int totalPageCount = pageutil.getTotalPageCount(); //전체 글 개수
	    
		map.put("startPageNum" , startPageNum);
		map.put("endPageNum" , endPageNum);
		map.put("totalPageCount" , totalPageCount);
		map.put("totalRowCount" , totalRowCount);
		map.put("startRow" , startRow);
		map.put("endRow", endRow);
		map.put("pageNum",pageNum);
		List<AdminListDTO> list= service.listAll(map);
		
        map.put("list",list);
        map.put("pageutil",pageutil);
        return map;

	}
	
	//상세보기 
	@GetMapping(value= {"/detail/{member_num}/{notice_num}"}
	, produces = { MediaType.APPLICATION_JSON_VALUE })
	public HashMap<String, Object> detail(
			@PathVariable(required = false) String notice_num,
			@PathVariable(required = false) String member_num){
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		List<AdminNoticeDetailDTO> filelist = service.filelist(Integer.parseInt(notice_num));
		AdminListDTO detaillist =  service.selectdetail(Integer.parseInt(notice_num));
		
		map.put("sort_num",detaillist.getSort_num());
		map.put("sort_name",detaillist.getSort_name());
		map.put("notice_title",detaillist.getNotice_title());
		map.put("notice_content",detaillist.getNotice_content());

		//list는 size로 확인, 배열은 length
		if(filelist.size() >0) { 
		   map.put("filelist",filelist);
		}
		
		return map;

	}
	
    //파일 삭제하기(전체)
	 @GetMapping(value= {"/delete/{notice_num}"}
		, produces = { MediaType.APPLICATION_JSON_VALUE })
	 public int delete(@PathVariable(required = false) String notice_num) {
		
		 List<AdminNoticeDetailDTO> filelist = service.filelist(Integer.parseInt(notice_num));
		 

		try {
			if (filelist == null || filelist.size() == 0) {
				service.delete(Integer.parseInt(notice_num));
				return 1;
			} else {
				List<Integer> list = service.selectnum(Integer.parseInt(notice_num));
				int[] flist = list.stream().mapToInt(i -> i).toArray();
				for (int i = 0; i < flist.length; i++) {
					String fileName = service.selectfile(flist[i]).getNotice_savefile();
					fileutil.delete(fileName, "notice");
				}
				service.alldelete(Integer.parseInt(notice_num));
				service.delete(Integer.parseInt(notice_num));
				return 1;
			}

		} catch (Exception e) {
			e.getMessage();
			return 0;
		}
	}
	 
//	 //수정 , 파일 있으면 기존 파일 삭제 후 업뎃
//	 @PostMapping(value = "/update/{notice_num}",
//				consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//	    public String update(@PathVariable(required = false) String notice_num,
//	    		@RequestPart("files") List<MultipartFile> multipartFile, 
//				@RequestParam String title, @RequestParam String content, @RequestParam String sort_num
//				) {
//
//		//받아온 값 확인
//		  System.out.println("title="+title);
//		  System.out.println("content="+content);
//		  System.out.println("file="+multipartFile);
//		  System.out.println("sort_num="+sort_num);
//		  
//		  log.info("========================컨트롤러==========================");
//		  log.info("sort_num : " + sort_num);
//		  log.info("========================================================");
//		  
//		  AdminNoticeVO vo = new AdminNoticeVO(Integer.parseInt(notice_num), 1, Integer.parseInt(sort_num) , title, content, null , 0);
//		  int n = service.update(multipartFile, vo);
//		  if(n==0 || n==1) {
//			  return "success";
//		  }else {
//			  return "fail";
//		  }  
//	 
//	 }
	 
	 @PostMapping(value = "/update/{notice_num}",
				consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	 public String update(@PathVariable(required = false) String notice_num,
	    		@RequestPart(required = false,value="newfiles") List<MultipartFile> multipartFile, 
				@RequestParam String title, @RequestParam String content, @RequestParam String sort_num,
				@RequestParam(required = false,value="deletefiles") List<Integer> deletefiles) {    
		 
		log.info("============================ 수정확인  log=================================");
		log.info("deletefiles 정보 :" + deletefiles);
		log.info("========================================================================");
		if (deletefiles != null) {
			for (var i = 0; i < deletefiles.size(); i++) {
				String fileName = service.selectfile(deletefiles.get(i)).getNotice_savefile();
				fileutil.delete(fileName, "notice");
				
				service.deletefile(deletefiles.get(i));
				
			}
		}
		AdminNoticeVO vo = new AdminNoticeVO(Integer.parseInt(notice_num), 1, Integer.parseInt(sort_num), title,
				content, null, 0);
		int n = service.update(multipartFile, vo);
		if (n == 0 || n == 1) {
			return "success";
		} else {
			return "fail";
		}
	}
	 
	 
	//파일 개별 삭제(update 글 수정용)
	 @GetMapping(value= {"/filedelete/{notice_detail_num}"}
		, produces = { MediaType.APPLICATION_JSON_VALUE })
	 public int deletefile(@PathVariable(required = false) String notice_num,
			 @PathVariable(required = false) String notice_detail_num) { 

		try {
			String fileName = service.selectfile(Integer.parseInt(notice_detail_num)).getNotice_savefile();
			boolean fileResult = fileutil.delete(fileName, "notice");
			int dbResult = service.deletefile(Integer.parseInt(notice_detail_num));
			//Exception 던지기
			if(!fileResult || dbResult <= 0) throw new Exception();
			return 1;
		} catch (Exception e) {
			e.getMessage();
			return 0;
		}

	}
	
	//글 수정 리스트 보이는 용
	@GetMapping(value= {"/update/{notice_num}"}
	, produces = { MediaType.APPLICATION_JSON_VALUE })
	public HashMap<String, Object> detail(
			@PathVariable(required = false) String notice_num){
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		List<AdminNoticeDetailDTO> filelist = service.filelist(Integer.parseInt(notice_num));
		AdminListDTO updatelist =  service.selectdetail(Integer.parseInt(notice_num));
		
//		log.info("============================== 글 수정  log=================================");
//		log.info("notice_num: " + notice_num);
//		log.info("updatelist: " + updatelist);
//		log.info("=========================================================================");

		map.put("sort_num",updatelist.getSort_num());
		map.put("sort_name",updatelist.getSort_name());
		map.put("notice_title",updatelist.getNotice_title());
		map.put("notice_content",updatelist.getNotice_content());
		
		if(filelist.size() > 0) { 
		   map.put("filelist",filelist);
		}
		
		return map;

	}
	
	

}

