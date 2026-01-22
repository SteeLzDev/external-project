package com.zetra.econsig.persistence.query.auditoria;

import java.text.ParseException;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaLogAuditoriaQuery</p>
 * <p>Description: Listagem de log de auditoria por entidade.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaLogAuditoriaQuery extends HNativeQuery {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaLogAuditoriaQuery.class);

    public boolean count = false;
    public String codigoEntidade;
    public String tipoEntidade;
    public boolean naoAuditado = false;
    public TransferObject criterios = null;

    public ListaLogAuditoriaQuery() {
        addFieldType("AUDITORIA_CODIGO", StandardBasicTypes.INTEGER);
        addFieldType("OBSERVACAO", StandardBasicTypes.STRING);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        if (TextHelper.isNull(codigoEntidade) || TextHelper.isNull(tipoEntidade)) {
            throw new HQueryException("mensagem.erro.informacoes.obrigatorios.nao.informadas", (AcessoSistema) null);
        }

        String auditado = naoAuditado ? CodedValues.TPC_NAO : CodedValues.TPC_SIM;

        String tabela = null;
        String campoAudCodigo = null;
        String campoCodigoEntidade = null;
        String campoNomeEntidade = null;
        String campoTloCodigo = null;
        String campoUsuCodigo = null;
        String campoFunCodigo = null;
        String campoTenCodigo = null;
        String campoAuditado = null;
        String campoData = null;
        String campoIp = null;
        String campoObs = null;
        String campoDataAuditoria = null;
        String campoUsuCodigoAuditor = null;
        StringBuilder join = new StringBuilder();

        if (tipoEntidade.equals(AcessoSistema.ENTIDADE_COR)) {
            tabela = Columns.TB_AUDITORIA_COR;
            campoAudCodigo = Columns.ACO_CODIGO;
            campoCodigoEntidade = Columns.ACO_COR_CODIGO;
            campoTloCodigo = Columns.ACO_TLO_CODIGO;
            campoUsuCodigo = Columns.ACO_USU_CODIGO;
            campoFunCodigo = Columns.ACO_FUN_CODIGO;
            campoTenCodigo = Columns.ACO_TEN_CODIGO;
            campoAuditado = Columns.ACO_AUDITADO;
            campoData = Columns.ACO_DATA;
            campoIp = Columns.ACO_IP;
            campoObs = Columns.ACO_OBS;
            campoNomeEntidade = Columns.COR_NOME;
            campoDataAuditoria = Columns.ACO_DATA_AUDITORIA;
            campoUsuCodigoAuditor = Columns.ACO_USU_CODIGO_AUDITOR;
            join.append(" inner join ").append(Columns.TB_CORRESPONDENTE).append(" on (").append(Columns.COR_CODIGO).append(" = ").append(campoCodigoEntidade).append(")");
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSA)) {
            tabela = Columns.TB_AUDITORIA_CSA;
            campoAudCodigo = Columns.ACS_CODIGO;
            campoCodigoEntidade = Columns.ACS_CSA_CODIGO;
            campoTloCodigo = Columns.ACS_TLO_CODIGO;
            campoUsuCodigo = Columns.ACS_USU_CODIGO;
            campoFunCodigo = Columns.ACS_FUN_CODIGO;
            campoTenCodigo = Columns.ACS_TEN_CODIGO;
            campoAuditado = Columns.ACS_AUDITADO;
            campoData = Columns.ACS_DATA;
            campoIp = Columns.ACS_IP;
            campoObs = Columns.ACS_OBS;
            campoNomeEntidade = Columns.CSA_NOME;
            campoDataAuditoria = Columns.ACS_DATA_AUDITORIA;
            campoUsuCodigoAuditor = Columns.ACS_USU_CODIGO_AUDITOR;
            join.append(" inner join ").append(Columns.TB_CONSIGNATARIA).append(" on (").append(Columns.CSA_CODIGO).append(" = ").append(campoCodigoEntidade).append(")");
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {
            tabela = Columns.TB_AUDITORIA_CSE;
            campoAudCodigo = Columns.ACE_CODIGO;
            campoCodigoEntidade = Columns.ACE_CSE_CODIGO;
            campoTloCodigo = Columns.ACE_TLO_CODIGO;
            campoUsuCodigo = Columns.ACE_USU_CODIGO;
            campoFunCodigo = Columns.ACE_FUN_CODIGO;
            campoTenCodigo = Columns.ACE_TEN_CODIGO;
            campoAuditado = Columns.ACE_AUDITADO;
            campoData = Columns.ACE_DATA;
            campoIp = Columns.ACE_IP;
            campoObs = Columns.ACE_OBS;
            campoNomeEntidade = Columns.CSE_NOME;
            campoDataAuditoria = Columns.ACE_DATA_AUDITORIA;
            campoUsuCodigoAuditor = Columns.ACE_USU_CODIGO_AUDITOR;
            join.append(" inner join ").append(Columns.TB_CONSIGNANTE).append(" on (").append(Columns.CSE_CODIGO).append(" = ").append(campoCodigoEntidade).append(")");
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
            tabela = Columns.TB_AUDITORIA_ORG;
            campoAudCodigo = Columns.AOR_CODIGO;
            campoCodigoEntidade = Columns.AOR_ORG_CODIGO;
            campoTloCodigo = Columns.AOR_TLO_CODIGO;
            campoUsuCodigo = Columns.AOR_USU_CODIGO;
            campoFunCodigo = Columns.AOR_FUN_CODIGO;
            campoTenCodigo = Columns.AOR_TEN_CODIGO;
            campoAuditado = Columns.AOR_AUDITADO;
            campoData = Columns.AOR_DATA;
            campoIp = Columns.AOR_IP;
            campoObs = Columns.AOR_OBS;
            campoNomeEntidade = Columns.ORG_NOME;
            campoDataAuditoria = Columns.AOR_DATA_AUDITORIA;
            campoUsuCodigoAuditor = Columns.AOR_USU_CODIGO_AUDITOR;
            join.append(" inner join ").append(Columns.TB_ORGAO).append(" on (").append(Columns.ORG_CODIGO).append(" = ").append(campoCodigoEntidade).append(")");
        } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_SUP)) {
            tabela = Columns.TB_AUDITORIA_SUP;
            campoAudCodigo = Columns.ASU_CODIGO;
            campoCodigoEntidade = Columns.ASU_CSE_CODIGO;
            campoTloCodigo = Columns.ASU_TLO_CODIGO;
            campoUsuCodigo = Columns.ASU_USU_CODIGO;
            campoFunCodigo = Columns.ASU_FUN_CODIGO;
            campoTenCodigo = Columns.ASU_TEN_CODIGO;
            campoAuditado = Columns.ASU_AUDITADO;
            campoData = Columns.ASU_DATA;
            campoIp = Columns.ASU_IP;
            campoObs = Columns.ASU_OBS;
            campoNomeEntidade = Columns.CSE_CODIGO;
            campoDataAuditoria = Columns.ASU_DATA_AUDITORIA;
            campoUsuCodigoAuditor = Columns.ASU_USU_CODIGO_AUDITOR;
            join.append(" inner join ").append(Columns.TB_CONSIGNANTE).append(" on (").append(Columns.CSE_CODIGO).append(" = ").append(campoCodigoEntidade).append(")");
        } else {
            throw new HQueryException("mensagem.erro.sistema.tipo.entidade.invalido", (AcessoSistema) null);
        }

        String periodoIni = null;
        String periodoFim = null;
        try {
            periodoIni = (criterios != null && criterios.getAttribute("PERIODO_INI") != null ? DateHelper.reformat(criterios.getAttribute("PERIODO_INI").toString(), LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00") : null);
        } catch (ParseException e) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.data.inicio.parse.invalido", (AcessoSistema) null), e);
        }
        try {
            periodoFim = (criterios != null && criterios.getAttribute("PERIODO_FIM") != null ? DateHelper.reformat(criterios.getAttribute("PERIODO_FIM").toString(), LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59") : null);
        } catch (ParseException e) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.data.fim.parse.invalido", (AcessoSistema) null), e);
        }
        String tenCodigo = (criterios != null && criterios.getAttribute("TEN_CODIGO") != null ? criterios.getAttribute("TEN_CODIGO").toString() : null);
        String funCodigo = (criterios != null && criterios.getAttribute("FUN_CODIGO") != null ? criterios.getAttribute("FUN_CODIGO").toString() : null);
        String usuLogin = (criterios != null && criterios.getAttribute("USU_LOGIN") != null ? criterios.getAttribute("USU_LOGIN").toString() : null);
        String logObs = (criterios != null && criterios.getAttribute("LOG_OBS") != null ? criterios.getAttribute("LOG_OBS").toString() : null);

        StringBuilder corpo = new StringBuilder();

        if (count) {
            corpo.append("select count(*) ");
        } else  {
            corpo.append("select ");
            corpo.append(campoAudCodigo).append(" as AUDITORIA_CODIGO, ");
            corpo.append(campoCodigoEntidade).append(" as CODIGO_ENTIDADE, ");
            corpo.append(campoNomeEntidade).append(" as NOME_ENTIDADE, ");
            corpo.append(campoTloCodigo).append(" as TLO_CODIGO, ");
            corpo.append(Columns.TLO_DESCRICAO).append(" as TLO_DESCRICAO, ");
            corpo.append(campoUsuCodigo).append(" as USU_CODIGO, ");
            corpo.append(Columns.USU_LOGIN).append(" as USU_LOGIN, ");
            corpo.append(campoFunCodigo).append(" as FUN_CODIGO, ");
            corpo.append(Columns.FUN_DESCRICAO).append(" as FUN_DESCRICAO, ");
            corpo.append(campoTenCodigo).append(" as TEN_CODIGO, ");
            corpo.append(Columns.TEN_DESCRICAO).append(" as TEN_DESCRICAO, ");
            corpo.append(campoAuditado).append(" as AUDITADO, ");
            corpo.append(campoData).append(" as DATA, ");
            corpo.append(campoDataAuditoria).append(" as DATA_AUDITORIA, ");
            corpo.append(campoIp).append(" as IP, ");
            corpo.append(campoObs).append(" as OBSERVACAO, ");
            corpo.append(campoUsuCodigoAuditor).append(" as USU_CODIGO_AUDITOR, ");
            corpo.append("usuAud.USU_LOGIN as USU_LOGIN_AUDITOR ");
        }

        corpo.append(" from ").append(tabela).append(" ");
        corpo.append(" inner join ").append(Columns.TB_TIPO_LOG).append(" on (").append(Columns.TLO_CODIGO).append(" = ").append(campoTloCodigo).append(")");
        corpo.append(" inner join ").append(Columns.TB_USUARIO).append(" on (").append(Columns.USU_CODIGO).append(" = ").append(campoUsuCodigo).append(")");
        corpo.append(" inner join ").append(Columns.TB_FUNCAO).append(" on (").append(Columns.FUN_CODIGO).append(" = ").append(campoFunCodigo).append(")");
        corpo.append(" inner join ").append(Columns.TB_TIPO_ENTIDADE).append(" on (").append(Columns.TEN_CODIGO).append(" = ").append(campoTenCodigo).append(")");
        corpo.append(" left outer join ").append(Columns.TB_USUARIO).append(" usuAud on (usuAud.USU_CODIGO = ").append(campoUsuCodigoAuditor).append(")");
        corpo.append(join);
        corpo.append(" where ").append(campoCodigoEntidade).append(criaClausulaNomeada("codigoEntidade", codigoEntidade));

        if (!TextHelper.isNull(auditado)) {
            corpo.append(" and ").append(campoAuditado).append(criaClausulaNomeada("auditado", auditado));
        }
        if (!TextHelper.isNull(periodoIni)) {
            corpo.append(" and ").append(campoData).append(" >= :periodoIni");
        }
        if (!TextHelper.isNull(periodoFim)) {
            corpo.append(" and ").append(campoData).append(" <= :periodoFim");
        }
        if (!TextHelper.isNull(tenCodigo)) {
            corpo.append(" and ").append(campoTenCodigo).append(criaClausulaNomeada("tenCodigo", tenCodigo));
        }
        if (!TextHelper.isNull(funCodigo)) {
            corpo.append(" and ").append(campoFunCodigo).append(criaClausulaNomeada("funCodigo", funCodigo));
        }
        if (!TextHelper.isNull(usuLogin)) {
            corpo.append(" and ").append(criaClausulaNomeada(Columns.USU_LOGIN, "usuLogin", usuLogin));
        }
        if (!TextHelper.isNull(logObs)) {
            corpo.append(" and ").append(criaClausulaNomeada(campoObs, "logObs", logObs));
        }

        if (!count) {
            corpo.append(" order by ").append(campoData).append(" desc ");
        }

        Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        if (!TextHelper.isNull(auditado)) {
            defineValorClausulaNomeada("auditado", auditado, query);
        }
        if (!TextHelper.isNull(periodoIni)) {
            defineValorClausulaNomeada("periodoIni", parseDateTimeString(periodoIni), query);
        }
        if (!TextHelper.isNull(periodoFim)) {
            defineValorClausulaNomeada("periodoFim", parseDateTimeString(periodoFim), query);
        }
        if (!TextHelper.isNull(tenCodigo)) {
            defineValorClausulaNomeada("tenCodigo", tenCodigo, query);
        }
        if (!TextHelper.isNull(funCodigo)) {
            defineValorClausulaNomeada("funCodigo", funCodigo, query);
        }
        if (!TextHelper.isNull(usuLogin)) {
            defineValorClausulaNomeada("usuLogin", usuLogin, query);
        }
        if (!TextHelper.isNull(logObs)) {
            defineValorClausulaNomeada("logObs", logObs, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {"AUDITORIA_CODIGO",
                "CODIGO_ENTIDADE",
                "NOME_ENTIDADE",
                "TLO_CODIGO",
                "TLO_DESCRICAO",
                "USU_CODIGO",
                "USU_LOGIN",
                "FUN_CODIGO",
                "FUN_DESCRICAO",
                "TEN_CODIGO",
                "TEN_DESCRICAO",
                "AUDITADO",
                "DATA",
                "DATA_AUDITORIA",
                "IP",
                "OBSERVACAO",
                "USU_CODIGO_AUDITOR",
                "USU_LOGIN_AUDITOR"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
