package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: Lista Solicitação de Saldo Devedor por Registro Servidor</p>
 * <p>Description: Listagem de Solicitações de Saldo devedor por  Registro Servidor para ser exibido no aplicativo CSE</p>
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: Salt</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSolicitacaoSaldoDevedorPorRegistroServidorQuery extends HNativeQuery {

    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<String> tocCodigoSolicitacao = CodedValues.TOC_CODIGOS_SOLICITACAO_SALDO_DEVEDOR;
        List<TipoSolicitacaoEnum> tisCodigoSaldoDevedor = TipoSolicitacaoEnum.getSolicitacoesSaldoDevedor();

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT DISTINCT ");
        corpoBuilder.append("tb_consignataria.csa_nome_abrev as CONSIGNATARIA_ABREV, ");
        corpoBuilder.append("tb_consignataria.csa_nome AS CONSIGNATARIA, ");
        corpoBuilder.append("cast(tb_aut_desconto.ade_numero as char) AS ADE, ");
        corpoBuilder.append("cast(tb_aut_desconto.ade_vlr as char) AS VALOR , ");
        corpoBuilder.append("tb_aut_desconto.ade_identificador AS IDENTIFICADOR , ");
        corpoBuilder.append("tb_aut_desconto.ade_indice AS INDICE , ");
        corpoBuilder.append("cast(tb_aut_desconto.ade_prazo as char) AS PRAZO, ");
        corpoBuilder.append("cast(tb_aut_desconto.ade_prd_pagas as char) AS PAGAS, ");
        corpoBuilder.append("tb_convenio.cnv_cod_verba AS VERBA, ");
        corpoBuilder.append("tb_servico.svc_descricao AS SERVICO, ");
        corpoBuilder.append("tb_registro_servidor.rse_matricula AS MATRICULA, ");
        corpoBuilder.append("tb_servidor.ser_nome AS NOME, ");
        corpoBuilder.append("tb_servidor.ser_cpf AS CPF, ");
        corpoBuilder.append("tb_ocorrencia_autorizacao.oca_data AS DATA_SOLICITACAO, ");
        corpoBuilder.append("tb_tipo_solicitacao.tis_descricao AS TIPO_SOLICITACAO, ");
        corpoBuilder.append("tb_status_solicitacao.sso_descricao AS STATUS_SOLICITACAO ");
        corpoBuilder.append("FROM tb_aut_desconto ");
        corpoBuilder.append("INNER JOIN tb_solicitacao_autorizacao ON (tb_aut_desconto.ade_codigo = tb_solicitacao_autorizacao.ade_codigo AND tb_solicitacao_autorizacao.tis_codigo ");
        corpoBuilder.append(criaClausulaNomeada("tisCodigo", tisCodigoSaldoDevedor)).append(") ");
        corpoBuilder.append("INNER JOIN tb_tipo_solicitacao  ON (tb_tipo_solicitacao.tis_codigo = tb_solicitacao_autorizacao.tis_codigo) ");
        corpoBuilder.append("INNER JOIN tb_status_solicitacao ON (tb_status_solicitacao.sso_codigo = tb_solicitacao_autorizacao.sso_codigo) ");
        corpoBuilder.append("INNER JOIN tb_registro_servidor ON (tb_registro_servidor.rse_codigo = tb_aut_desconto.rse_codigo) ");
        corpoBuilder.append("INNER JOIN tb_servidor ON (tb_servidor.ser_codigo = tb_registro_servidor.ser_codigo) ");
        corpoBuilder.append("INNER JOIN tb_verba_convenio ON (tb_verba_convenio.vco_codigo = tb_aut_desconto.vco_codigo) ");
        corpoBuilder.append("INNER JOIN tb_convenio ON (tb_convenio.cnv_codigo = tb_verba_convenio.cnv_codigo) ");
        corpoBuilder.append("INNER JOIN tb_consignataria ON (tb_consignataria.csa_codigo = tb_convenio.csa_codigo) ");
        corpoBuilder.append("INNER JOIN tb_servico ON (tb_servico.svc_codigo = tb_convenio.svc_codigo) ");
        corpoBuilder.append("INNER JOIN tb_ocorrencia_autorizacao ON (tb_aut_desconto.ade_codigo = tb_ocorrencia_autorizacao.ade_codigo AND tb_ocorrencia_autorizacao.toc_codigo ");
        corpoBuilder.append(criaClausulaNomeada("tocCodigoSolicitacao", tocCodigoSolicitacao)).append(" ) ");
        corpoBuilder.append("WHERE ");
        corpoBuilder.append("tb_registro_servidor.rse_codigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo)).append(" ");
        corpoBuilder.append("AND tb_aut_desconto.sad_codigo ").append(criaClausulaNomeada("sadCodigos", CodedValues.SAD_CODIGOS_ATIVOS)).append(" ");
        corpoBuilder.append("AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca WHERE oca.ADE_CODIGO = tb_aut_desconto.ade_codigo AND oca.OCA_DATA > tb_ocorrencia_autorizacao.oca_data AND oca.TOC_CODIGO ");
        corpoBuilder.append(criaClausulaNomeada("tocCodigoInformacao", CodedValues.TOC_INFORMACAO_SALDO_DEVEDOR)).append(") ");
        corpoBuilder.append("ORDER BY CONSIGNATARIA_ABREV, ADE");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tisCodigo", tisCodigoSaldoDevedor, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("sadCodigos", CodedValues.SAD_CODIGOS_ATIVOS, query);
        defineValorClausulaNomeada("tocCodigoSolicitacao", tocCodigoSolicitacao, query);
        defineValorClausulaNomeada("tocCodigoInformacao", CodedValues.TOC_INFORMACAO_SALDO_DEVEDOR, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            "CONSIGNATARIA_ABREV",
            "CONSIGNATARIA",
            "ADE",
            "VALOR",
            "IDENTIFICADOR",
            "INDICE",
            "PRAZO",
            "PAGAS",
            "VERBA",
            "SERVICO",
            "MATRICULA",
            "NOME",
            "CPF",
            "DATA_SOLICITACAO",
            "TIPO_SOLICITACAO",
            "STATUS_SOLICITACAO"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
