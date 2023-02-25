package com.sys.dosa.exam.service;

import org.springframework.stereotype.Service;

import com.sys.dosa.exam.repository.BoardRepository;
import com.sys.dosa.exam.vo.Board;

@Service
public class BoardService {
	private BoardRepository boardRepository;
	
	public BoardService(BoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}
	
	public Board getBoardById(int id) {		
		return boardRepository.getBoardById(id);
	}

}
