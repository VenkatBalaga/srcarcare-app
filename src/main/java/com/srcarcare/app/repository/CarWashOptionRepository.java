package com.srcarcare.app.repository;

import com.srcarcare.app.entity.CarWashOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarWashOptionRepository extends JpaRepository<CarWashOption, Long> {
    List<CarWashOption> findByActiveTrue();
    List<CarWashOption> findAllByOrderByIdDesc();
}
