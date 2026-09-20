package com.srcarcare.app.repository;

import com.srcarcare.app.entity.CarServiceOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarServiceOptionRepository extends JpaRepository<CarServiceOption, Long> {
    List<CarServiceOption> findByActiveTrue();
    List<CarServiceOption> findAllByOrderByIdDesc();
}
