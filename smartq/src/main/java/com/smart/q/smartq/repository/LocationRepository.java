package com.smart.q.smartq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.q.smartq.model.Location;

public interface LocationRepository extends JpaRepository<Location, String> {
	
}
