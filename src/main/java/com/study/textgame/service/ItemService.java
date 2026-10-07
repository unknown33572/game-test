package com.study.textgame.service;

import com.study.textgame.dto.ItemRequest;
import com.study.textgame.dto.ItemResponse;

import java.util.List;

public interface ItemService {
  ItemResponse createItem(ItemRequest request);
  ItemResponse getItem(Long id);
  List<ItemResponse> getItems();
}
