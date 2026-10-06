package com.study.history.service.impl;

import com.study.history.dto.MemoRequest;
import com.study.history.dto.MemoResponse;
import com.study.history.entity.Memo;
import com.study.history.repository.MemoRepository;
import com.study.history.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

  private final MemoRepository memoRepository;

  @Transactional
  @Override
  public MemoResponse createMemo(MemoRequest request) {
    Memo memo = Memo.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .createdAt(LocalDateTime.now())
                    .build();

    Memo savedMemo = memoRepository.save(memo);

    MemoResponse response = new MemoResponse();
    response.setId(savedMemo.getId());
    response.setTitle(savedMemo.getTitle());
    response.setContent(savedMemo.getContent());
    response.setCreatedAt(savedMemo.getCreatedAt());
    response.setUpdatedAt(savedMemo.getUpdatedAt());
    return response;
  }

  @Transactional(readOnly = true)
  @Override
  public MemoResponse getMemo(Long id) {

    Memo memo = memoRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User is not" + id));

    return toResponse(memo);
  }

  @Transactional(readOnly = true)
  @Override
  public List<MemoResponse> getMemos() {
    List<Memo> memos = new ArrayList<>();
    memos = memoRepository.findAllByOrderByCreatedAtDesc();

    List<MemoResponse> responses = new ArrayList<>();
    for (int i = 0; i < memos.size(); i++) {
//      MemoResponse resp = new MemoResponse();
//      resp.setId(getMemoList.get(i).getId());
//      resp.setTitle(getMemoList.get(i).getTitle());
//      resp.setContent(getMemoList.get(i).getContent());
      responses.add(toResponse(memos.get(i))); // 위 네 줄 단축.
    }
    return responses;
  }

  private MemoResponse toResponse(Memo memo) {
    MemoResponse resp = new MemoResponse();
    resp.setId(memo.getId());
    resp.setTitle(memo.getTitle());
    resp.setContent(memo.getContent());
    resp.setCreatedAt(memo.getCreatedAt());
    resp.setUpdatedAt(memo.getUpdatedAt());

    return resp;
  }
}
