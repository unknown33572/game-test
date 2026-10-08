package com.study.textgame.controller;

import com.study.textgame.dto.ItemRequest;
import com.study.textgame.dto.ItemResponse;
import com.study.textgame.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("")
    public ItemResponse createItem(@Valid @RequestBody ItemRequest itemRequest) {
        return itemService.createItem(itemRequest);
    }

    @GetMapping("/{id}")
    public ItemResponse viewItem(@PathVariable Long id) {
        return itemService.getItem(id);
    }

    @GetMapping("")
    public List<ItemResponse> viewItems() {
        return itemService.getItems();
    }

    @PutMapping("/{id}")
    public ItemResponse updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest itemRequest) {
        return itemService.updateItem(id, itemRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
