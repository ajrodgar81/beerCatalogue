package com.haufeGroup.beerCatalogue.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "beers")
@SQLDelete(sql = "UPDATE beers SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Beer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String graduation;
	@Column(nullable = false)
	private String type;
	@Column(nullable = false)
	private String description;
	@Column(columnDefinition = "boolean default false")
	private Boolean deleted;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manufacturers_id", nullable = false)
	private Manufacturer manufacturer;
}
