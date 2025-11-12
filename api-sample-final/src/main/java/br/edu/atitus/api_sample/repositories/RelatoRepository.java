package br.edu.atitus.api_sample.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.atitus.api_sample.entities.RelatoEntity;
import br.edu.atitus.api_sample.entities.UserEntity;

@Repository
public interface RelatoRepository extends JpaRepository<RelatoEntity, UUID>{
	
	List<RelatoEntity> findByUser(UserEntity user);

}
