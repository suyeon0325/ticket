package com.moaplace.mapper;

import java.util.HashMap;
import java.util.List;

import com.moaplace.dto.BookingSelectViewDTO;
import com.moaplace.dto.BookingShowDTO;
import com.moaplace.dto.CalendarDateDTO;
import com.moaplace.dto.MainShowDTO;
import com.moaplace.dto.ShowDTO;
import com.moaplace.dto.ShowDetailDTO;
import com.moaplace.dto.admin.show.MapperDetailDTO;
import com.moaplace.dto.admin.show.ShowListDTO;
import com.moaplace.dto.admin.show.ShowPartInfoDTO;
import com.moaplace.vo.ShowVO;

public interface ShowMapper {
	
	int showInsert(ShowVO vo);
  
	List<ShowListDTO> showList(HashMap<String, Object> map);
	
	List<MapperDetailDTO> showDetail(int num);
	
	List<ShowPartInfoDTO> searchShow(String title);
	
	ShowPartInfoDTO searchShowNumber(int num);
	
	int showUpdate(ShowVO vo);

	int firstCntRow();

	int currentCount(HashMap<String, Object> map);
	
	List<ShowDTO> list(HashMap<String, Object> map);

	int cntRow();
  
	int count(HashMap<String, Object> map);

	List<ShowDetailDTO> detail(int show_num);
	
	int count();
	
	List<BookingSelectViewDTO> showBookingSelect(int num);
	
	ShowVO showSelect(int num);
	
	//공연 삭제
	int deleteShow(int num);

	String returnThumb(int num);

	// 달력에 출력할 정보 뽑아오기
	List<CalendarDateDTO> monthSelect(int month); 

	//메인페이지 진행중인 공연
	List<MainShowDTO> getRunningShow();
	
	//예매페이지 공연장, 공연명 조회
	BookingShowDTO getBookingShow(int show_num);

}
