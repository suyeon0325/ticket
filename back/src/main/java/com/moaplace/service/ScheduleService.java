package com.moaplace.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moaplace.dto.ResidualDTO;
import com.moaplace.dto.ScheduleDTO;
import com.moaplace.mapper.ScheduleMapper;

@Service
public class ScheduleService {

	@Autowired 
	private ScheduleMapper scheduleMapper;
	
	public List<ScheduleDTO> schedule(int show_num) {
		
		return scheduleMapper.schedule(show_num);
	}
	
	public int Rounds(HashMap<String, Object> map) {
		
		return scheduleMapper.Rounds(map);
	}
  
	public List<ResidualDTO> selectlist(int show_num) {
		
		return scheduleMapper.selectlist(show_num);
	}
	
	public List<Integer> rowinfo(int show_num) {
		
		return scheduleMapper.rowinfo(show_num);
	}

}