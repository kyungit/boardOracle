package com.board.dto;

import java.time.LocalDate;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDTO {

	private int seqno;
	private int seq;	
	private String writer;
	private String title;
	private String content;
	private String userid;
	private LocalDate regdate;
	private int hitno;
	private int likecnt;
	private int dislikecnt;

}
