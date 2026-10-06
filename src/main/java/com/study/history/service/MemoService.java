package com.study.history.service;

import com.study.history.dto.MemoRequest;
import com.study.history.dto.MemoResponse;

import java.util.List;

public interface MemoService {
  MemoResponse createMemo(MemoRequest request);
  MemoResponse getMemo(Long id);
  List<MemoResponse> getMemos();
}
