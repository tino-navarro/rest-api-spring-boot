package br.edu.atitus.api_sample.dtos;

import br.edu.atitus.api_sample.entities.RelatoType;

public record RelatoDTO(String title, RelatoType type, String description, double latitude, double longitude) {

}
