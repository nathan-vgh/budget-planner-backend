package com.budget_planner.budget_planner.expense.persist;

import com.budget_planner.budget_planner.expense.domain.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    Page<Budget> findAllByUserId (UUID userId, Pageable pageable);
}
