package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zetra.econsig.persistence.entity.UsuarioOrg;
import com.zetra.econsig.persistence.entity.UsuarioOrgId;

public interface UsuarioOrgDao extends JpaRepository<UsuarioOrg, UsuarioOrgId> {

	@Query(value = "SELECT usuOrg.org_codigo from tb_usuario usu"
			+ " INNER JOIN tb_usuario_org usuOrg ON usu.usu_codigo = usuOrg.usu_codigo"
			+ " WHERE usu.usu_login = ?1", nativeQuery = true)
	String getOrgCodigoByUsuLogin(String usuLogin);

    Long removeByUsuCodigo(String usuCodigo);

}
