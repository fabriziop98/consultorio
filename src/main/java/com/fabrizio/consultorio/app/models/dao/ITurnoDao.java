package com.fabrizio.consultorio.app.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fabrizio.consultorio.app.models.entity.Turno;

@Repository
public interface ITurnoDao extends JpaRepository<Turno, Long>{

}
