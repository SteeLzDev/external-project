package com.zetra.econsig.persistence.query.servidor;

import java.text.ParseException;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServidorPendenteQuery</p>
 * <p>Description: Listagem de servidores pendentes de validação</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServidorPendenteQuery extends HQuery {

    public boolean count = false;
    public AcessoSistema responsavel;
    public TransferObject criterio;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String serCpf = (criterio != null ? (String) criterio.getAttribute(Columns.SER_CPF) : null);
        String serNome = (criterio != null ? (String) criterio.getAttribute(Columns.SER_NOME) : null);
        String serSobrenome = (criterio != null ? (String) criterio.getAttribute(Columns.SER_ULTIMO_NOME) : null);
        String serDataNascimento = (criterio != null ? (String) criterio.getAttribute(Columns.SER_DATA_NASC) : null);
        String estCodigo = (criterio != null ? (String) criterio.getAttribute(Columns.EST_CODIGO) : null);
        String orgCodigo = (criterio != null ? (String) criterio.getAttribute(Columns.ORG_CODIGO) : null);
        String rseMatricula = (criterio != null ? (String) criterio.getAttribute(Columns.RSE_MATRICULA) : null);

        String corpo = "";

        if (count) {
            corpo =
                "select count( distinct rse.rseCodigo) ";
        } else {
            corpo =
                "select " +
                "max(ors.orsData), " +
                "rse.rseCodigo, " +
                "rse.rseMatricula, " +
                "rse.rseTipo, " +
                "rse.rseSalario, " +
                "rse.rseProventos, " +
                "ser.serCodigo, " +
                "ser.serNome, " +
                "ser.serCpf, " +
                "ser.serDataNasc, " +
                "org.orgCodigo, " +
                "org.orgIdentificador, " +
                "org.orgNome, " +
                "est.estCodigo, " +
                "est.estIdentificador, " +
                "est.estNome, " +
                "(select max(orsIn.orsData) from OcorrenciaRegistroSer orsIn where orsIn.registroServidor.rseCodigo = rse.rseCodigo and orsIn.tipoOcorrencia.tocCodigo = '" + CodedValues.TOC_RSE_INCLUSAO_MANUAL + "') ";
        }


        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from OcorrenciaRegistroSer ors");
        corpoBuilder.append(" inner join ors.registroServidor rse");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" where rse.statusRegistroServidor.srsCodigo = '").append(CodedValues.SRS_PENDENTE).append("'");
        corpoBuilder.append(" and ors.tipoOcorrencia.tocCodigo in('").append(CodedValues.TOC_RSE_ALTERACAO_DADOS_CADASTRAIS).append("','").append(CodedValues.TOC_RSE_INCLUSAO_MANUAL).append("')");

        if ((responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) || !TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and est.estCodigo = :codigoEntidade");
        } else if ((responsavel.isOrg()) || !TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.orgCodigo = :codigoEntidade");
        }

        // Adiciona cláusula de matricula e cpf
        corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, serCpf, true));

        if (!TextHelper.isNull(serNome)) {
            corpoBuilder.append(" and (ser.serPrimeiroNome ").append(criaClausulaNomeada("serNome", CodedValues.LIKE_MULTIPLO + serNome));
            corpoBuilder.append(" or ser.serNome ").append(criaClausulaNomeada("serNome", CodedValues.LIKE_MULTIPLO + serNome));
            corpoBuilder.append(" )");
        }

        if (!TextHelper.isNull(serSobrenome)) {
            corpoBuilder.append(" and (ser.serUltimoNome ").append(criaClausulaNomeada("serSobrenome", CodedValues.LIKE_MULTIPLO + serSobrenome));
            corpoBuilder.append(" or ser.serNome ").append(criaClausulaNomeada("serSobrenome", CodedValues.LIKE_MULTIPLO + serSobrenome));
            corpoBuilder.append(" )");

        }

        if (!TextHelper.isNull(serDataNascimento)) {
            try {
                serDataNascimento = DateHelper.reformat(serDataNascimento, LocaleHelper.getDatePattern(), "yyyy-MM-dd");
                corpoBuilder.append(" and ser.serDataNasc ").append(criaClausulaNomeada("serDataNascimento", serDataNascimento));
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.nascimento.informada.invalida", responsavel, serDataNascimento);
            }
        }

        String tpcExibeSerPendenteSemAde = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EXIBE_SER_PENDENTE_COM_ADE, responsavel);
        boolean validaSeTemAde = !TextHelper.isNull(criterio.getAttribute("validaAdeSerPendente")) ? (Boolean) criterio.getAttribute("validaAdeSerPendente") : true;
        if (!TextHelper.isNull(tpcExibeSerPendenteSemAde) && tpcExibeSerPendenteSemAde.equals(CodedValues.TPC_SIM) && validaSeTemAde) {
            corpoBuilder.append(" and exists (");
            corpoBuilder.append(" select 1 from rse.autDescontoSet ade ");
            corpoBuilder.append(" where ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_AGUARD_CONF, "','")).append("')");
            corpoBuilder.append(")");
        }

        if (!count) {
        	String group = "rse.rseCodigo, " +
                    	   "rse.rseMatricula, " +
                           "rse.rseTipo, " +
		                   "rse.rseSalario, " +
		                   "rse.rseProventos, " +
		                   "ser.serCodigo, " +
		                   "ser.serNome, " +
		                   "ser.serCpf, " +
		                   "ser.serDataNasc, " +
		                   "org.orgCodigo, " +
		                   "org.orgIdentificador, " +
		                   "org.orgNome, " +
		                   "est.estCodigo, " +
		                   "est.estIdentificador, " +
		                   "est.estNome ";

        	corpoBuilder.append(" group by ").append(group);

            corpoBuilder.append(" order by rse.rseDataCarga, ser.serNome");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, true, query);

        if (!TextHelper.isNull(serNome)) {
            defineValorClausulaNomeada("serNome", serNome + CodedValues.LIKE_MULTIPLO, query);
        }

        if (!TextHelper.isNull(serSobrenome)) {
            defineValorClausulaNomeada("serSobrenome", CodedValues.LIKE_MULTIPLO + serSobrenome + CodedValues.LIKE_MULTIPLO, query);
        }

        if (!TextHelper.isNull(serDataNascimento)) {
            defineValorClausulaNomeada("serDataNascimento", serDataNascimento, query);
        }

        if ((responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) || !TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("codigoEntidade", (responsavel.getEstCodigo() != null ? responsavel.getEstCodigo() : estCodigo), query);
        } else if ((responsavel.isOrg()) || !TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("codigoEntidade", (responsavel.getOrgCodigo() != null ? responsavel.getOrgCodigo() : orgCodigo), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
        		Columns.ORS_DATA,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.RSE_TIPO,
                Columns.RSE_SALARIO,
                Columns.RSE_PROVENTOS,
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SER_DATA_NASC,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                "DATA_INCLUSAO"
        };
    }
}
