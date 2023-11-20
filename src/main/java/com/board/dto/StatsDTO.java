package com.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatsDTO {
	private String userid;
	private String username;
	private int stats;
	private int reply;
	private int board;
	private int seq;
}
