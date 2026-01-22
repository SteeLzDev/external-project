package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: UNESP</p>
 * <p>Description: Implementações específicas para UNESP.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UNESP extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UNESP.class);

    @Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {

        //Regra da Exportação.
        //Devemos colocar na tb_tmp_exportacao_ordenada a coluna autoriza_pgt_parcial como "N" quando existir para o servidor contratos que não sejam das consignatárias e serviços abaixo.
        //        Csa_identificador Csa_nome
        //        571 BANCO DO BRASIL
        //        574 SANTANDER
        //        649 COOPERFAC
        //        693 COOPUNESP
        //        655 SICOOB UNICENTRO
        //
        //        Svc_identificador Svc_descricao
        //        12G EMPRÉSTIMO35%/96
        //        16A EMPRESTIMO AUXILIO MUTUO 35%
        //        42A EMPRESTIMO COOPERATIVA DE CREDITO 35%
        //        12 EMPRESTIMO
        //        12F EMPRESTIMO/120
        //        12D EMPRESTIMO/84
        //        12C EMPRESTIMO/72
        //        12E EMPRESTIMO/96
        //
        //Se existir algum contrato com uma consignatária listada, porém o serviço não é o listado devemos marcar como "N" também o desconto parcial ou quando
        //existir algum contrato com um serviço listado e a consignatária não é da lista

        List<String> csaIdentificador = new ArrayList<>();
        csaIdentificador.add("571");
        csaIdentificador.add("574");
        csaIdentificador.add("649");
        csaIdentificador.add("693");
        csaIdentificador.add("655");

        List<String> svcIdentificador = new ArrayList<>();
        svcIdentificador.add("12G");
        svcIdentificador.add("16A");
        svcIdentificador.add("42A");
        svcIdentificador.add("12");
        svcIdentificador.add("12F");
        svcIdentificador.add("12D");
        svcIdentificador.add("12C");
        svcIdentificador.add("12E");

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        StringBuilder query = new StringBuilder();

        try {
            int rows = 0;

            // Considero inicialmente todos os contratos exportados como parciais e posteriormente vamos ajustando de acordo com a regra.
            query.append("UPDATE tb_tmp_exportacao_ordenada tmp ");
            query.append("SET tmp.autoriza_pgt_parcial= 'S' ");

            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);
            query.setLength(0);

            //Neste momento criamos uma tabela temporária com todos os servidores que possuem contratos que não são da lista de parciais.
            // Criamos a temporarária, pois a performance ficou melhor no update.
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_servidores_contratos_nao_parciais ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TEMPORARY TABLE tb_tmp_servidores_contratos_nao_parciais ");
            query.append("SELECT rse.rse_matricula, ser.ser_cpf, est.est_identificador, org.org_identificador ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (org.org_codigo = rse.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (est.est_codigo = org.est_codigo) ");
            query.append("INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo) ");
            query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
            query.append("WHERE ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','")).append("') ");
            query.append("AND (  ");
            //Quando existir consignatárias e serviços diferentes listados.
            query.append("(csa.csa_identificador NOT IN ('").append(TextHelper.join(csaIdentificador, "','")).append("') ");
            query.append("AND svc.svc_identificador NOT IN ('").append(TextHelper.join(svcIdentificador, "','")).append("')) ");

            //Quando existir consignatárias iguais e serviços diferentes listados.
            query.append(" OR (csa.csa_identificador IN ('").append(TextHelper.join(csaIdentificador, "','")).append("') ");
            query.append("AND svc.svc_identificador NOT IN ('").append(TextHelper.join(svcIdentificador, "','")).append("')) ");

            //Quando existir serviços iguais e consignátarias diferentes listados.
            query.append(" OR (csa.csa_identificador NOT IN ('").append(TextHelper.join(csaIdentificador, "','")).append("') ");
            query.append("AND svc.svc_identificador IN ('").append(TextHelper.join(svcIdentificador, "','")).append("')) ");
            query.append(") ");
            query.append("GROUP BY rse.rse_matricula, ser.ser_cpf, est.est_identificador, org.org_identificador ");

            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);
            query.setLength(0);

            // Quando existis linhas com as condições abaixo, a autorização deve ser modificada para não permitir parcial, pois os servidores tem contratos difernetes das listadas.
            query.append("UPDATE tb_tmp_exportacao_ordenada tmp ");
            query.append("SET tmp.autoriza_pgt_parcial= 'N' ");
            query.append("WHERE EXISTS (SELECT 1 FROM tb_tmp_servidores_contratos_nao_parciais scnp ");
            query.append("WHERE scnp.rse_matricula = tmp.rse_matricula AND scnp.ser_cpf = tmp.ser_cpf AND scnp.est_identificador = tmp.est_identificador AND scnp.org_identificador = tmp.org_identificador) ");

            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
