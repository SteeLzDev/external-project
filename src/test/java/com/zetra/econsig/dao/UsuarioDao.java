package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Usuario;

public interface UsuarioDao extends JpaRepository<Usuario, String> {
	
	Usuario findByUsuLogin(@Param("USU_LOGIN") String usuLogin);
	
	@Query(value = "SELECT usr.usu_login FROM tb_usuario usr"
				+ " LEFT JOIN tb_usuario_cse cse ON "
					+ "usr.usu_codigo = cse.usu_codigo"
			+ " WHERE usr.stu_codigo = 1 AND cse.usu_codigo IS NOT NULL"
			+ " ORDER BY RAND() ASC"
			+ " LIMIT 1",
		nativeQuery = true
	)
	String getCseAtivo();
	
	@Query(value = "SELECT usr.usu_login FROM tb_usuario usr"
				+ " LEFT JOIN tb_usuario_csa csa ON "
					+ "usr.usu_codigo = csa.usu_codigo"
			+ " WHERE usr.stu_codigo = 1 AND csa.usu_codigo IS NOT NULL"
			+ " ORDER BY RAND() ASC"
			+ " LIMIT 1",
		nativeQuery = true
	)
	String getCsaAtivo();
	
	@Query(value = "SELECT usr.usu_login FROM tb_usuario usr"
				+ " LEFT JOIN tb_usuario_org org ON "
					+ "usr.usu_codigo = org.usu_codigo"
			+ " WHERE usr.stu_codigo = 1 AND org.usu_codigo IS NOT NULL"
			+ " ORDER BY RAND() ASC"
			+ " LIMIT 1",
		nativeQuery = true
	)
	String getOrgAtivo();
	
	@Query(value = "SELECT usr.usu_login FROM tb_usuario usr"
				+ " LEFT JOIN tb_usuario_sup sup ON "
					+ "usr.usu_codigo = sup.usu_codigo"
			+ " WHERE usr.stu_codigo = 1 AND sup.usu_codigo IS NOT NULL"
			+ " ORDER BY RAND() ASC"
			+ " LIMIT 1",
		nativeQuery = true
	)
	String getSupAtivo();
	
	@Query(value = "SELECT usr.usu_login FROM tb_usuario usr"
				+ " LEFT JOIN tb_usuario_cor cor ON "
					+ "usr.usu_codigo = cor.usu_codigo"
			+ " WHERE usr.stu_codigo = 1 AND cor.usu_codigo IS NOT NULL"
			+ " ORDER BY RAND() ASC"
			+ " LIMIT 1",
		nativeQuery = true
	)
	String getCorAtivo();
}