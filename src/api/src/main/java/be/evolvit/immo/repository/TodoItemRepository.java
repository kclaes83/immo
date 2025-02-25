package be.evolvit.immo.repository;

import be.evolvit.immo.model.TodoItem;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoItemRepository
  extends
    PagingAndSortingRepository<TodoItem, String>,
    ListCrudRepository<TodoItem, String> {
  List<TodoItem> findByListId(String listId);

  List<TodoItem> findByListId(String listId, Pageable pageable);

  List<TodoItem> findByListIdAndState(
    String listId,
    String state,
    Pageable pageable
  );
}
