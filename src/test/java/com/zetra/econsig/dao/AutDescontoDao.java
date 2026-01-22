package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.AutDesconto;

public interface AutDescontoDao extends JpaRepository<AutDesconto, String> {

	List<AutDesconto> findByRseCodigo(@Param("rse_codigo") String rseCodigo);

	AutDesconto findByAdeNumero(@Param("ADE_NUMERO") Long adeNumero);

	AutDesconto findByAdeCodigo(@Param("ADE_CODIGO") String adeCodigo);

	@Query(value = "SELECT distinct ade.* FROM tb_aut_desconto ade" +
			" INNER JOIN tb_verba_convenio vco ON ade.vco_codigo = vco.vco_codigo" +
			" INNER JOIN tb_convenio cnv ON vco.cnv_codigo = cnv.cnv_codigo" +
			" INNER JOIN tb_consignataria csa ON cnv.csa_codigo = csa.csa_codigo" +
			" WHERE ade.rse_codigo = ?1" +
			" AND ade.sad_codigo in (?2)" +
			" AND csa.csa_ativo = 1",
				nativeQuery = true)
	List<AutDesconto> getAdesByRseCodigoAndSadCodigo(String rseCodigo, List<String> sadCodigos);

	@Query(value = "SELECT distinct ade.* FROM tb_aut_desconto ade" +
			" INNER JOIN tb_verba_convenio vco ON ade.vco_codigo = vco.vco_codigo" +
			" INNER JOIN tb_convenio cnv ON vco.cnv_codigo = cnv.cnv_codigo" +
			" INNER JOIN tb_consignataria csa ON cnv.csa_codigo = csa.csa_codigo" +
			" AND ade.sad_codigo in (?1)",
				nativeQuery = true)
	List<AutDesconto> getAdesBySadCodigo(List<String> sadCodigos);

	@Query(value = "SELECT distinct ade.* FROM tb_aut_desconto ade" +
			" INNER JOIN tb_verba_convenio vco ON ade.vco_codigo = vco.vco_codigo" +
			" INNER JOIN tb_convenio cnv ON vco.cnv_codigo = cnv.cnv_codigo" +
			" INNER JOIN tb_consignataria csa ON cnv.csa_codigo = csa.csa_codigo" +
			" WHERE ade.rse_codigo = ?1" +
			" AND ade.sad_codigo not in (?2)" +
			" AND csa.csa_ativo = 1",
				nativeQuery = true)
	List<AutDesconto> getAdesByRseCodigoAndNotSadCodigo(String rseCodigo, List<String> sadCodigos);

	@Query(value = "SELECT distinct ade.* FROM tb_aut_desconto ade " +
			" INNER JOIN tb_verba_convenio vco ON ade.vco_codigo = vco.vco_codigo" +
			" INNER JOIN tb_convenio cnv ON vco.cnv_codigo = cnv.cnv_codigo" +
			" INNER JOIN tb_servico svc ON cnv.svc_codigo = svc.svc_codigo" +
			" INNER JOIN tb_consignataria csa ON cnv.csa_codigo = csa.csa_codigo" +
			" WHERE ade.rse_codigo = ?1"+
			" AND ade.sad_codigo in (?2)" +
			" AND svc.svc_codigo = ?3" +
			" AND csa.csa_ativo = 1",
				nativeQuery = true)
	List<AutDesconto> getAdesByRseCodigoAndSadCodigoAndSvcCodigo(String rseCodigo, List<String> sadCodigos, String svcCodigo);

	@Query(value = "SELECT distinct ade.* FROM tb_aut_desconto ade" +
			" WHERE ade.sad_codigo in (?1)",
				nativeQuery = true)
	List<AutDesconto> findBySadCodigo(List<String> sadCodigosAtivos);

    @Query(value = "select " +
            "  ade.adeVlr, " +
            "  ade.adeCodigo, " +
            "  ade.adeData, " +
            "  ade.adeAnoMesIni, " +
            "  ade.adePrazo, " +
            "  ade.adePrdPagas, " +
            "  ade.adeCodReg, " +
            "  ade.adeIndice, " +
            "  ade.statusAutorizacaoDesconto.sadCodigo, " +
            "  prd.prdNumero " +
            "from AutDesconto ade " +
            "inner join ade.verbaConvenio vco " +
            "inner join vco.convenio cnv " +
            "inner join cnv.servico svc " +
            "inner join cnv.consignataria csa " +
            "left outer join ade.parcelaDescontoPeriodoSet prd WITH prd.statusParcelaDesconto.spdCodigo = '4' " +
            "where ade.registroServidor.rseCodigo = ?1 " +
            "  AND cnv.cnvCodigo = ?2 " +
            "  AND ((ade.statusAutorizacaoDesconto.sadCodigo IN ('0','4','5','6','10','11','12','13','14','15','16','17')) " +
            "    OR (ade.statusAutorizacaoDesconto.sadCodigo IN ('1','2') " +
            "    AND NOT EXISTS ( " +
            "      SELECT 1 FROM ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad " +
            "      INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem " +
            "      WHERE rad.tipoNatureza.tntCodigo IN ('6','7') " +
            "        AND adeOrigem.statusAutorizacaoDesconto.sadCodigo IN ('11','15') " +
            "        AND adeOrigem.verbaConvenio.vcoCodigo = vco.vcoCodigo " +
            "        AND adeOrigem.registroServidor.rseCodigo = ade.registroServidor.rseCodigo " +
            "    ) " +
            "  ) " +
            ") ")
    List<Object[]> getAdesLimiteByRseCodigoAndCnvCodigo(String rseCodigo, String cnvCodigo);

    @Query(value = "select concat( " +
            "  lpad(coalesce(rse.rse_matricula, ''), 10, '#'), ';', " +
            "  coalesce(ser.ser_cpf, ''), ';', " +
            "  rpad(coalesce(ser.ser_nome, ''), 50, '#'), ';',  " +
            "  lpad(coalesce(greatest(abs(ade.ade_vlr - 0.5), 1), ''), 9, '0'), ';', " +
            "  ade.ade_ano_mes_ini, ';', " +
            "  lpad(coalesce(ade.ade_prazo, ''), 3, '0'), ';',  " +
            "  lpad(coalesce(cnv.cnv_cod_verba, ''), 4, '#'), ';', " +
            "  lpad(coalesce(csa.csa_identificador, ''), 14, '#'), ';',  " +
            "  case when ade.sad_codigo in ('0', '1', '2') then 'C' when ade.sad_codigo in ('11', '15') then 'E' else 'A' end, ';',  " +
            "  lpad('05', 3, '#'), ';', " +
            "  lpad(coalesce(ade.ade_vlr_liquido, ''), 9, '0'), ';',  " +
            "  lpad(coalesce(org.org_identificador, ''), 9, '#'), ';', " +
            "  lpad(coalesce(ade.ade_vlr, ''), 9, '0'), ';', " +
            "  '1', ';', " +
            "  lpad(coalesce(ade.ade_indice, ''), 5, '#'), ';') as linha " +
            "from tb_aut_desconto ade " +
            "inner join tb_registro_servidor rse on (ade.RSE_CODIGO = rse.RSE_CODIGO) " +
            "inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) " +
            "inner join tb_orgao org on (rse.org_codigo = org.ORG_CODIGO) " +
            "inner join tb_estabelecimento est on (org.EST_CODIGO = est.est_codigo) " +
            "inner join tb_verba_convenio vco on (ade.VCO_CODIGO = vco.vco_codigo) " +
            "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) " +
            "inner join tb_consignataria csa on (csa.csa_codigo = cnv.csa_codigo) " +
            "where ade.sad_codigo in ('0', '1', '2', '4', '5', '6', '10', '11', '12', '13', '14', '15', '16', '17') ",
            nativeQuery = true)
    List<String> getLinhasLoteTodasAdesAtivas();
}
