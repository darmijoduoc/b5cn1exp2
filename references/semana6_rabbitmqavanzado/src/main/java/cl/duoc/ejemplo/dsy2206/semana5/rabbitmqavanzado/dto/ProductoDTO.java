package cl.duoc.ejemplo.dsy2206.semana5.rabbitmqavanzado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductoDTO {

	private Long id;
	private String nombre;
	private String categoria;
}
