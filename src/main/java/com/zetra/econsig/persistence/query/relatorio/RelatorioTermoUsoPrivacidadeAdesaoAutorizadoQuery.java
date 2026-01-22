package com.zetra.econsig.persistence.query.relatorio;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioTermoUsoPrivacidadeAdesaoAutorizadoQuery</p>
 * <p> Description: Relatório de termo de uso, privacidade e de adesão autorizado
 * pelo parâmetro de serviço 135.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 26263 $
 * $Date: 2019-02-19 18:35:15 -0300 (ter, 19 fev 2019) $
 */
public class RelatorioTermoUsoPrivacidadeAdesaoAutorizadoQuery extends ReportHQuery {

    public List<String> tocCodigo;
    public boolean aceiteWeb;
    public boolean aceiteMobile;
    public boolean aceiteTermo;
    public boolean aceitePrivacidade;
    public boolean aceiteTermoAdesaoAutorizado;
    public Date periodoIni;
    public Date periodoFim;
    public boolean cse;
    public boolean org;
    public boolean csa;
    public boolean cor;
    public boolean ser;
    public boolean sup;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {

        if (criterio != null) {
            periodoIni = (Date) criterio.getAttribute("periodoIni");
            periodoFim = (Date) criterio.getAttribute("periodoFim");
            aceiteWeb = (boolean) criterio.getAttribute("aceiteWeb");
            aceiteMobile = (boolean) criterio.getAttribute("aceiteMobile");
            aceiteTermo = (boolean) criterio.getAttribute("aceiteTermo");
            aceitePrivacidade = (boolean) criterio.getAttribute("aceitePrivacidade");
            aceiteTermoAdesaoAutorizado = (boolean) criterio.getAttribute("aceiteTermoAdesaoAutorizado");
            tocCodigo = (List<String>) criterio.getAttribute("tocCodigo");
            cse = (boolean) criterio.getAttribute("cse");
            org = (boolean) criterio.getAttribute("org");
            csa = (boolean) criterio.getAttribute("csa");
            cor = (boolean) criterio.getAttribute("cor");
            ser = (boolean) criterio.getAttribute("ser");
            sup = (boolean) criterio.getAttribute("sup");
        }

    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" select");
        corpoBuilder.append(" usu.usuNome as usu_nome,");
        corpoBuilder.append(" usu.usuLogin as usu_login,");
        corpoBuilder.append(" usu.usuEmail as usu_email,");
        corpoBuilder.append(" usu.usuTel as usu_tel,");
        corpoBuilder.append(" usu.usuCpf as usu_cpf,");
        corpoBuilder.append(" stu.stuDescricao as stu_descricao,");
        corpoBuilder.append(" case");
        corpoBuilder.append(" when usuarioCse.cseCodigo is not null then cse.cseNome");
        corpoBuilder.append(" when usuarioOrg.orgCodigo is not null then org.orgNome");
        corpoBuilder.append(" when usuarioCsa.csaCodigo is not null then csa.csaNome");
        corpoBuilder.append(" when usuarioCor.corCodigo is not null then cor.corNome");
        corpoBuilder.append(" when usuarioSer.serCodigo is not null then 'Servidor'");
        corpoBuilder.append(" when usuarioSup.cseCodigo is not null then 'Suporte'");
        corpoBuilder.append(" end AS ENTIDADE,");
        corpoBuilder.append(" to_locale_datetime(ous.ousData) as ous_data,");
        corpoBuilder.append(" ous.ousIpAcesso as ous_ip_acesso,");
        corpoBuilder.append(" case");
        if (aceiteTermo) {
            corpoBuilder.append(" when ous.tipoOcorrencia.tocCodigo in ('" + CodedValues.TOC_ACEITACAO_TERMO_DE_USO + "', '" + CodedValues.TOC_ACEITACAO_TERMO_DE_USO_MOBILE + "', '" + CodedValues.TOC_ACEITACAO_TERMO_DE_USO_SALARYPAY + "') then " + "'" + ApplicationResourcesHelper.getMessage("rotulo.termo.de.uso", null) + "'");
        }
        if (aceitePrivacidade) {
            corpoBuilder.append(" when ous.tipoOcorrencia.tocCodigo in ('"+ CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_SISTEMA + "', '" + CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_MOBILE + "') then " + "'" + ApplicationResourcesHelper.getMessage("rotulo.politica.privacidade.singular", null) + "'");
        }
        if(aceiteTermoAdesaoAutorizado) {
            corpoBuilder.append(" when tad.tadTitulo IS NOT NULL then tad.tadTitulo else '' ");
        }
        corpoBuilder.append(" end AS DOCUMENTO_ACEITO,");
        corpoBuilder.append(" case");
        if (aceiteMobile && aceiteWeb) {
            corpoBuilder.append(" when ous.tipoOcorrencia.tocCodigo in ('" + CodedValues.TOC_ACEITACAO_TERMO_DE_USO + "', '" + CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_SISTEMA + "') then 'WEB'");
            corpoBuilder.append(" when ous.tipoOcorrencia.tocCodigo in ('"+ CodedValues.TOC_ACEITACAO_TERMO_DE_USO_MOBILE +"', '" + CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_MOBILE + "', '" + CodedValues.TOC_ACEITACAO_TERMO_DE_USO_SALARYPAY + "') then 'MOBILE'");
            corpoBuilder.append(" when ltu.ltuCanal = '").append(CanalEnum.WEB.getCodigo()).append("' then 'WEB'");
            corpoBuilder.append(" when ltu.ltuCanal = '").append(CanalEnum.REST.getCodigo()).append("' then 'MOBILE'");
        } else if (!aceiteMobile && aceiteWeb) {
            corpoBuilder.append(" when ous.tipoOcorrencia.tocCodigo in ('" + CodedValues.TOC_ACEITACAO_TERMO_DE_USO + "', '" + CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_SISTEMA + "') then 'WEB'");
            corpoBuilder.append(" when ltu.ltuCanal = '").append(CanalEnum.WEB.getCodigo()).append("' then 'WEB'");
        } else {
            corpoBuilder.append(" when ous.tipoOcorrencia.tocCodigo in ('"+ CodedValues.TOC_ACEITACAO_TERMO_DE_USO_MOBILE +"', '" + CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_MOBILE + "', '" + CodedValues.TOC_ACEITACAO_TERMO_DE_USO_SALARYPAY + "') then 'MOBILE'");
            corpoBuilder.append(" when ltu.ltuCanal = '").append(CanalEnum.REST.getCodigo()).append("' then 'MOBILE'");
        }
        corpoBuilder.append(" end AS ACEITACAO_VIA");
        corpoBuilder.append(" from Usuario usu");
        corpoBuilder.append(" inner join usu.ocorrenciaUsuarioByUsuCodigoSet ous");
        corpoBuilder.append(" inner join usu.statusLogin stu");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCsaSet usuarioCsa");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCorSet usuarioCor");
        corpoBuilder.append(" LEFT JOIN usu.usuarioOrgSet usuarioOrg");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSerSet usuarioSer");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup");

        corpoBuilder.append(" LEFT JOIN usuarioCsa.consignataria csa");
        corpoBuilder.append(" LEFT JOIN usuarioCse.consignante cse");
        corpoBuilder.append(" LEFT JOIN usuarioCor.correspondente cor");
        corpoBuilder.append(" LEFT JOIN usuarioOrg.orgao org");
        
        if(aceiteTermoAdesaoAutorizado) {
            corpoBuilder.append(" LEFT JOIN usu.termoAdesaoSet tad");
            corpoBuilder.append(" LEFT JOIN tad.leituraTermoUsuarioSet ltu");
        }

        corpoBuilder.append(" where 1=1");
        corpoBuilder.append(" and ( ous.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigo));
        if(aceiteTermoAdesaoAutorizado) {
            corpoBuilder.append(" or ltu.ltuTermoAceito = '").append(CodedValues.CAS_SIM).append("' ");
        }
        corpoBuilder.append(") ");
        if(!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            corpoBuilder.append(" and ous.ousData between :periodoIni and :periodoFim");
        }        

        if (!cse || !org || !csa || !cor || !ser || !sup) {
            corpoBuilder.append(" and ( 1 = 2 ");
            if (csa) {
                corpoBuilder.append(" or usuarioCsa.csaCodigo is not null");
            }
            if (cor) {
                corpoBuilder.append(" or usuarioCor.corCodigo is not null");
            }
            if (ser) {
                corpoBuilder.append(" or usuarioSer.serCodigo is not null");
            }
            if (cse) {
                corpoBuilder.append(" or usuarioCse.cseCodigo is not null");
            }
            if (sup) {
                corpoBuilder.append(" or usuarioSup.cseCodigo is not null");
            }
            if (org) {
                corpoBuilder.append(" or usuarioOrg.orgCodigo is not null");
            }
            corpoBuilder.append(" )");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if(!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            defineValorClausulaNomeada("periodoIni", periodoIni, query);
            defineValorClausulaNomeada("periodoFim", periodoFim, query);
        }
        defineValorClausulaNomeada("tocCodigo", tocCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.USU_NOME,
                Columns.USU_LOGIN,
                Columns.USU_EMAIL,
                Columns.USU_TEL,
                Columns.USU_CPF,
                Columns.STU_DESCRICAO,
                "ENTIDADE",
                Columns.OUS_DATA,
                Columns.OUS_IP_ACESSO,
                "ACEITACAO_VIA",
                "DOCUMENTO_ACEITO"
        };
    }
}