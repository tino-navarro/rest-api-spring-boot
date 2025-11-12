package br.edu.atitus.api_sample.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.edu.atitus.api_sample.entities.RelatoEntity;
import br.edu.atitus.api_sample.entities.UserEntity;
import br.edu.atitus.api_sample.repositories.RelatoRepository;

@Service
public class RelatoService {

	private final RelatoRepository repository;

	public RelatoService(RelatoRepository repository) {
		super();
		this.repository = repository;
	}
	
		public RelatoEntity save(RelatoEntity relato) throws Exception {
		if (relato == null)
			throw new Exception("Objeto Nulo");
		
		if (relato.getTitle() == null || relato.getTitle().isEmpty())
				throw new Exception("Título Inválido");
			relato.setTitle(relato.getTitle().trim());
			
			if (relato.getDescription() == null || relato.getDescription().isEmpty())
			throw new Exception("Descrição Inválida");
		relato.setDescription(relato.getDescription().trim());
		
		if (relato.getLatitude() < -90 || relato.getLatitude() > 90)
			throw new Exception("Latitude Inválida");
		
		if (relato.getLongitude() < -180 || relato.getLongitude() > 180)
			throw new Exception("Longitude Inválida");
		
		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		relato.setUser(userAuth);
		
		return repository.save(relato);
	}
	
		public RelatoEntity update(UUID id, RelatoEntity relato) throws Exception {
			// 1. Verificar se o ponto existe.
			var relatoExistente = repository.findById(id)
					.orElseThrow(() -> new Exception("Não existe relato cadastrado com este ID"));
			
			// 2. Verificar se o ponto pertence ao usuário logado.
			UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			if (!relatoExistente.getUser().getId().equals(userAuth.getId()))
				throw new Exception("Você não tem permissão para atualizar este registro");
			
			// Aplica as validações do save (título, descrição, lat/long)
			relato.setId(id);
			relato.setUser(userAuth);
			
			return this.save(relato);
		}
		
		public void deleteById(UUID id) throws Exception {
		var relato = repository.findById(id)
				.orElseThrow(() -> new Exception("Não existe relato cadastrado com este ID"));
		
		UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (!relato.getUser().getId().equals(userAuth.getId()))
			throw new Exception("Você não tem permissão para apagar este registro");
		
		repository.deleteById(id);
	}
	
public List<RelatoEntity> findAll() {
			UserEntity userAuth = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			return repository.findByUser(userAuth);
		}
	
}
