/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package be.evolvit.immo.controller;

import be.evolvit.immo.api.ItemsApi;
import be.evolvit.immo.model.TodoItem;
import be.evolvit.immo.model.TodoList;
import be.evolvit.immo.model.TodoState;
import be.evolvit.immo.repository.TodoItemRepository;
import be.evolvit.immo.repository.TodoListRepository;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class TodoItemsController implements ItemsApi {

  private final TodoListRepository todoListRepository;

  private final TodoItemRepository todoItemRepository;

  public TodoItemsController(
    TodoListRepository todoListRepository,
    TodoItemRepository todoItemRepository
  ) {
    this.todoListRepository = todoListRepository;
    this.todoItemRepository = todoItemRepository;
  }

  public ResponseEntity<TodoItem> createItem(String listId, TodoItem todoItem) {
    final Optional<TodoList> optionalTodoList = todoListRepository.findById(
      listId
    );
    if (optionalTodoList.isPresent()) {
      todoItem.setListId(listId);
      final TodoItem savedTodoItem = todoItemRepository.save(todoItem);
      final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(savedTodoItem.getId())
        .toUri();
      return ResponseEntity.created(location).body(savedTodoItem);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  public ResponseEntity<Void> deleteItemById(String listId, String itemId) {
    Optional<TodoItem> todoItem = getTodoItem(listId, itemId);
    if (todoItem.isPresent()) {
      todoItemRepository.deleteById(itemId);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  public ResponseEntity<TodoItem> getItemById(String listId, String itemId) {
    return getTodoItem(listId, itemId)
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  public ResponseEntity<List<TodoItem>> getItemsByListId(
    String listId,
    BigDecimal top,
    BigDecimal skip
  ) {
    // no need to check nullity of top and skip, because they have default values.
    Optional<TodoList> todoList = todoListRepository.findById(listId);
    if (todoList.isPresent()) {
      return ResponseEntity.ok(
        todoItemRepository.findByListId(
          listId,
          PageRequest.of(skip.intValue(), top.intValue())
        )
      );
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  public ResponseEntity<TodoItem> updateItemById(
    String listId,
    String itemId,
    TodoItem todoItem
  ) {
    // make sure listId and itemId are set into the todoItem, otherwise it will create
    // a new todo item.
    return getTodoItem(listId, itemId)
      .map(t -> {
        todoItemRepository.save(todoItem);
        return ResponseEntity.ok(todoItem);
      })
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  public ResponseEntity<List<TodoItem>> getItemsByListIdAndState(
    String listId,
    TodoState state,
    BigDecimal top,
    BigDecimal skip
  ) {
    // no need to check nullity of top and skip, because they have default values.
    return ResponseEntity.ok(
      todoItemRepository.findByListIdAndState(
        listId,
        state.name(),
        PageRequest.of(skip.intValue(), top.intValue())
      )
    );
  }

  public ResponseEntity<Void> updateItemsStateByListId(
    String listId,
    TodoState state,
    List<String> itemIds
  ) {
    // update all items in list with the given state if `itemIds` is not specified.
    for (TodoItem todoItem : todoItemRepository.findByListId(listId)) {
      todoItem.setState(state);
      todoItemRepository.save(todoItem);
    }
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private Optional<TodoItem> getTodoItem(String listId, String itemId) {
    Optional<TodoList> optionalTodoList = todoListRepository.findById(listId);
    if (optionalTodoList.isEmpty()) {
      return Optional.empty();
    }
    Optional<TodoItem> optionalTodoItem = todoItemRepository.findById(itemId);
    if (optionalTodoItem.isPresent()) {
      TodoItem todoItem = optionalTodoItem.get();
      if (todoItem.getListId().equals(listId)) {
        return Optional.of(todoItem);
      } else {
        return Optional.empty();
      }
    } else {
      return Optional.empty();
    }
  }
}
