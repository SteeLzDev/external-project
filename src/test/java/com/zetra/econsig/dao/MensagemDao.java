package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Mensagem;

public interface MensagemDao extends JpaRepository<Mensagem, String> {
	
	List<Mensagem> findByMenExibeCse(@Param("menExibeCse") String menExibeCse);
	
	List<Mensagem> findByMenExibeOrg(@Param("menExibeOrg") String menExibeOrg);
	
	List<Mensagem> findByMenExibeCsa(@Param("menExibeCsa") String menExibeCsa);
	
	List<Mensagem> findByMenExibeCor(@Param("menExibeCor") String menExibeCor);
	
	List<Mensagem> findByMenExibeSup(@Param("menExibeSup") String menExibeSup);
	
	List<Mensagem> findByMenExibeSer(@Param("menExibeSer") String menExibeSer);

	Mensagem findByMenCodigo(@Param("menCodigo") String menCodigo);
	
}