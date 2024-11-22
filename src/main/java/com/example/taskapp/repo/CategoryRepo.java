package com.example.taskapp.repo;

import com.example.taskapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    Category findById(long id);
    Category findByName(String name);
}
