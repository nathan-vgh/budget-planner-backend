package com.budget_planner.budget_planner.currency.persist;

import com.budget_planner.budget_planner.common.persistence.ReadOnlyRepository;
import com.budget_planner.budget_planner.currency.domain.Currency;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CurrencyRepository extends ReadOnlyRepository<Currency, UUID> {}
