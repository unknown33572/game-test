package com.study.textgame.service;

import com.study.textgame.dto.ItemRequest;
import com.study.textgame.dto.ItemResponse;

import java.util.List;

public interface ItemService {
  ItemResponse createItem(ItemRequest request);
  ItemResponse getItem(Long id);
  List<ItemResponse> getItems();
  ItemResponse updateItem(Long id, ItemRequest request);
  void deleteItem(Long id);
}
