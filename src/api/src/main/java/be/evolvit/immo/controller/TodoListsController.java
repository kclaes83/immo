package be.evolvit.immo.controller;

import be.evolvit.immo.api.ListsApi;
import be.evolvit.immo.model.TodoList;
import be.evolvit.immo.repository.TodoListRepository;
import jakarta.validation.Valid;
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
public class TodoListsController implements ListsApi {

  private final TodoListRepository todoListRepository;

  public TodoListsController(TodoListRepository todoListRepository) {
    this.todoListRepository = todoListRepository;
  }

  public ResponseEntity<TodoList> createList(TodoList todoList) {
    final TodoList savedTodoList = todoListRepository.save(todoList);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(savedTodoList.getId())
      .toUri();
    return ResponseEntity.created(location).body(savedTodoList);
  }

  public ResponseEntity<Void> deleteListById(String listId) {
    Optional<TodoList> todoList = todoListRepository.findById(listId);
    if (todoList.isPresent()) {
      todoListRepository.deleteById(listId);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  public ResponseEntity<TodoList> getListById(String listId) {
    return todoListRepository
      .findById(listId)
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  public ResponseEntity<List<TodoList>> getLists(
    BigDecimal top,
    BigDecimal skip
  ) {
    // no need to check nullity of top and skip, because they have default values.
    return ResponseEntity.ok(
      todoListRepository
        .findAll(PageRequest.of(skip.intValue(), top.intValue()))
        .getContent()
    );
  }

  public ResponseEntity<TodoList> updateListById(
    String listId,
    @Valid TodoList todoList
  ) {
    // make sure listId is set into the todoItem, otherwise it will create a new todo
    // list.
    todoList.setId(listId);
    return todoListRepository
      .findById(listId)
      .map(t -> ResponseEntity.ok(todoListRepository.save(todoList)))
      .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
