package com.budget_planner.budget_planner.expense.persist;

import com.budget_planner.budget_planner.expense.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByUserId (UUID userId);
}
