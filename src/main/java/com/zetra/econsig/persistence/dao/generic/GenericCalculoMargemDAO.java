package com.zetra.econsig.persistence.dao.generic;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.persistence.dao.CalculoMargemDAO;

/**
 * <p>Title: GenericCalculoMargemDAO</p>
 * <p>Description: Implementacao Genérica do DAO de cálculo de margem. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericCalculoMargemDAO implements CalculoMargemDAO {

    /**
     * De acordo com o tipo da entidade, gera o complemento da query para
     * incluir ligação à tabela de período de exportação.
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @param rseJoin : apelido ao qual a tabela de registro servidor será ligada
     * @return
     */
    protected String getComplementoJoinPeriodoExp(String tipoEntidade, List<String> entCodigos, String rseJoin) {
        String complementoJoin = "";
        if ((entCodigos != null && entCodigos.size() > 0) && (tipoEntidade.equalsIgnoreCase("EST") || tipoEntidade.equalsIgnoreCase("ORG") || tipoEntidade.equalsIgnoreCase("RSE"))) {
            complementoJoin = " inner join tb_periodo_exportacao pex on (rse.org_codigo = pex.org_codigo) ";
        } else {
            complementoJoin = " inner join tb_registro_servidor rse on (rse.rse_codigo = " + rseJoin + ".rse_codigo) "
                            + " inner join tb_periodo_exportacao pex on (rse.org_codigo = pex.org_codigo) ";
        }
        return complementoJoin;
    }

    /**
     * De acordo com o tipo da entidade, gera o complemento da query para
     * incluir restrição que selecione apenas margens de servidores da entidade.
     * Deve ser utilizadm em conjunto com {@link #getComplementoWhere(String, List)}
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @param rseJoin : apelido ao qual a tabela de registro servidor será ligada
     * @return
     */
    protected String getComplementoJoin(String tipoEntidade, List<String> entCodigos, String rseJoin) {
        String complementoJoin = "";
        if (entCodigos != null && entCodigos.size() > 0) {
            if (tipoEntidade.equalsIgnoreCase("EST")) {
                complementoJoin = " inner join tb_registro_servidor rse on (rse.rse_codigo = " + rseJoin + ".rse_codigo) "
                                + " inner join tb_orgao org on (rse.org_codigo = org.org_codigo) ";
            } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                complementoJoin = " inner join tb_registro_servidor rse on (rse.rse_codigo = " + rseJoin + ".rse_codigo) ";
            } else if (tipoEntidade.equalsIgnoreCase("RSE")) {
                complementoJoin = " inner join tb_registro_servidor rse on (rse.rse_codigo = " + rseJoin + ".rse_codigo) ";
            }
        }
        return complementoJoin;
    }

    /**
     * De acordo com o tipo da entidade, gera o complemento da query para
     * incluir restrição que selecione apenas margens de servidores da entidade.
     * Deve ser utilizadm em conjunto com {@link #getComplementoJoin(String, List, String)}.
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @return
     */
    protected String getComplementoWhere(String tipoEntidade, List<String> entCodigos, MapSqlParameterSource queryParams) {
        String complementoWhere = "";
        if (entCodigos != null && entCodigos.size() > 0) {
            if (tipoEntidade.equalsIgnoreCase("EST")) {
                complementoWhere = " and org.est_codigo in (:entCodigos) ";
                queryParams.addValue("entCodigos", entCodigos);
            } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                complementoWhere = " and rse.org_codigo in (:entCodigos) ";
                queryParams.addValue("entCodigos", entCodigos);
            } else if (tipoEntidade.equalsIgnoreCase("RSE")) {
                complementoWhere = " and rse.rse_codigo in (:entCodigos) ";
                queryParams.addValue("entCodigos", entCodigos);
            }
        }
        return complementoWhere;
    }


    /**
     * De acordo com o tipo da entidade, gera o complemento da query para
     * incluir restrição que selecione apenas margens de servidores da entidade.
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @return
     */
    protected String getComplementoWhereExists(String tipoEntidade, List<String> entCodigos, MapSqlParameterSource queryParams) {
        String complementoWhere = "";
        if (entCodigos != null && entCodigos.size() > 0) {
            if (tipoEntidade.equalsIgnoreCase("EST")) {
                complementoWhere = " and exists ("
                                 + " select 1 from tb_registro_servidor rse"
                                 + " inner join tb_orgao org on (rse.org_codigo = org.org_codigo)"
                                 + " where rse.rse_codigo = mrs.rse_codigo"
                                 + " and org.est_codigo in (:entCodigos)"
                                 + ")"
                                 ;
                queryParams.addValue("entCodigos", entCodigos);
            } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                complementoWhere = " and exists ("
                                 + " select 1 from tb_registro_servidor rse"
                                 + " where rse.rse_codigo = mrs.rse_codigo"
                                 + " and rse.org_codigo in (:entCodigos)"
                                 + ")"
                                 ;
                queryParams.addValue("entCodigos", entCodigos);
            } else if (tipoEntidade.equalsIgnoreCase("RSE")) {
                complementoWhere = " and mrs.rse_codigo in (:entCodigos) ";
                queryParams.addValue("entCodigos", entCodigos);
            }
        }
        return complementoWhere;
    }
}
