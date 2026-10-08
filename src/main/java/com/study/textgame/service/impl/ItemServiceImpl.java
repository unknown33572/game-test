package com.study.textgame.service.impl;

import com.study.textgame.dto.ItemRequest;
import com.study.textgame.dto.ItemResponse;
import com.study.textgame.entity.Item;
import com.study.textgame.exception.ItemNotFoundException;
import com.study.textgame.repository.ItemRepository;
import com.study.textgame.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;

  @Transactional
  @Override
  public ItemResponse createItem(ItemRequest request) {
    Item item = Item.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .createdAt(LocalDateTime.now())
                    .grade(request.getGrade())
                    .build();

    Item savedItem = itemRepository.save(item);

    ItemResponse response = new ItemResponse();
    response.setId(savedItem.getId());
    response.setName(savedItem.getName());
    response.setDescription(savedItem.getDescription());
    response.setCreatedAt(savedItem.getCreatedAt());
    response.setUpdatedAt(savedItem.getUpdatedAt());
    response.setGrade(savedItem.getGrade());
    return response;
  }

  @Transactional(readOnly = true)
  @Override
  public ItemResponse getItem(Long id) {

    Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));

    return toResponse(item);
  }

  @Transactional(readOnly = true)
  @Override
  public List<ItemResponse> getItems() {
    List<Item> items = new ArrayList<>();
    items = itemRepository.findAllByOrderByCreatedAtDesc();

    List<ItemResponse> responses = new ArrayList<>();
    for (int i = 0; i < items.size(); i++) {
//      ItemResponse resp = new ItemResponse();
//      resp.setId(getItemList.get(i).getId());
//      resp.setName(getItemList.get(i).getName());
//      resp.setDescription(getItemList.get(i).getDescription());
      responses.add(toResponse(items.get(i))); // 위 네 줄 단축.
    }
    return responses;
  }

  @Transactional
  @Override
  public ItemResponse updateItem(Long id, ItemRequest request) {
    Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    item.updateItem(request.getName(), request.getDescription(), request.getGrade(), LocalDateTime.now());
    return toResponse(item);
  }

  @Transactional
  @Override
  public void deleteItem(Long id) {
    Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    itemRepository.delete(item);
  }

  private ItemResponse toResponse(Item item) {
    ItemResponse resp = new ItemResponse();
    resp.setId(item.getId());
    resp.setName(item.getName());
    resp.setDescription(item.getDescription());
    resp.setCreatedAt(item.getCreatedAt());
    resp.setUpdatedAt(item.getUpdatedAt());
    resp.setGrade(item.getGrade());

    return resp;
  }
}
