package be.evolvit.immo.repository;

import be.evolvit.immo.model.TodoList;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoListRepository
  extends
    PagingAndSortingRepository<TodoList, String>,
    ListCrudRepository<TodoList, String> {}
