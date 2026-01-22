package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Agendamento;

public interface AgendamentoDao extends JpaRepository<Agendamento, String> {

	Agendamento findByAgdCodigo(String agdCodigo);

	List<Agendamento> findByUsuCodigoAndRelCodigoAndSagCodigoAndTagCodigo(@Param("usu_codigo") String usuCodigo,
			@Param("rel_codigo") String relCodigo, @Param("sag_codigo") String sagCodigo,
			@Param("tag_codigo") String tagCodigo);

	List<Agendamento> findByUsuCodigoAndRelCodigo(@Param("usu_codigo") String usuCodigo,
			@Param("rel_codigo") String relCodigo);

	@Modifying
	@Query(value = "update tb_agendamento u set u.sag_codigo = '3'", nativeQuery = true)
	public void desabilitarTodosAgendamentos();
}