package com.zetra.econsig.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.OrgaoDao;
import com.zetra.econsig.dao.PeriodoExportacaoDao;
import com.zetra.econsig.persistence.entity.Orgao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class PeriodoExportacaoService {

	@Autowired
	private PeriodoExportacaoDao periodoExportacaoDao;

	@PersistenceContext
    private EntityManager entityManager;

	@Autowired
	private OrgaoDao orgaoDao;

	@Transactional
	public void updateTodosPeriodosExportacao(String pexPeriodo, String pexDataIni, String pexDataFim) {
		periodoExportacaoDao.updateTodosPeriodosExportacaoDatas(pexPeriodo, pexDataIni, pexDataFim);
	}

	@Transactional
	public void insertRegistroPeriodo(String pexPeriodo, String orgCodigo, String pexDataIni, String pexDataFim) {
		insertRegistroPeriodo(pexPeriodo, orgCodigo, pexDataIni, pexDataFim, Short.valueOf("10"));
	}

	@Transactional
	public void insertRegistroPeriodo(String pexPeriodo, String orgCodigo, String pexDataIni, String pexDataFim, short diaCorde) {
	    entityManager.createNativeQuery("insert into tb_periodo_exportacao (pex_periodo, org_codigo, pex_data_ini, pex_data_fim, pex_dia_corte) VALUES (?,?,?,?,?)")
	      .setParameter(1, pexPeriodo)
	      .setParameter(2, orgCodigo)
	      .setParameter(3, pexDataIni)
	      .setParameter(4, pexDataFim)
	      .setParameter(5, diaCorde)
	      .executeUpdate();
	}

	@Transactional
	public void inserePeriodoExportacaoOrgaos(String pexPeriodo, String pexDataIni, String pexDataFim) {
		List<Orgao> lstOrgs = orgaoDao.findAll();

		lstOrgs.forEach(org -> insertRegistroPeriodo(pexPeriodo, org.getOrgCodigo(), pexDataIni, pexDataFim));
	}

	@Transactional
    public void deleteById(String orgCodigo, Date pexPeriodo) {
	    entityManager.createNativeQuery("delete from tb_periodo_exportacao where org_codigo = ? and pex_periodo = ?")
          .setParameter(1, orgCodigo)
          .setParameter(2, pexPeriodo)
          .executeUpdate();
    }

	@Transactional
	public void limpaTabela() {
		periodoExportacaoDao.deleteAll();
	}
}
