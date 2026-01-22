package com.zetra.econsig.persistence.query.agendamento;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusAgendamentoEnum;
import com.zetra.econsig.values.TipoAgendamentoEnum;

/**
 * <p>Title: ListaAgendamentosParaExecucaoQuery</p>
 * <p>Description: Listagem de agendamentos para serem executados.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAgendamentosParaExecucaoQuery extends HNativeQuery {

    private final List<String> sagCodigos;
    private final List<String> tagCodigos;
    private final List<String> tocCodigos;

    public ListaAgendamentosParaExecucaoQuery() {
        sagCodigos = new ArrayList<String>();
        sagCodigos.add(StatusAgendamentoEnum.AGUARDANDO_EXECUCAO.getCodigo());
        sagCodigos.add(StatusAgendamentoEnum.EXECUCAO_DIARIA.getCodigo());
        sagCodigos.add(StatusAgendamentoEnum.EXECUCAO_SEMANAL.getCodigo());
        sagCodigos.add(StatusAgendamentoEnum.EXECUCAO_MENSAL.getCodigo());
        sagCodigos.add(StatusAgendamentoEnum.EXECUCAO_ANUAL.getCodigo());

        tagCodigos = new ArrayList<String>();
        tagCodigos.add(TipoAgendamentoEnum.PERIODICO_DIARIO.getCodigo());
        tagCodigos.add(TipoAgendamentoEnum.PERIODICO_SEMANAL.getCodigo());
        tagCodigos.add(TipoAgendamentoEnum.PERIODICO_MENSAL.getCodigo());
        tagCodigos.add(TipoAgendamentoEnum.PERIODICO_ANUAL.getCodigo());

        tocCodigos = new ArrayList<String>();
        tocCodigos.add(CodedValues.TOC_PROCESSAMENTO_AGENDAMENTO);
        tocCodigos.add(CodedValues.TOC_ERRO_PROCESSAMENTO_AGENDAMENTO);
        tocCodigos.add(CodedValues.TOC_CONCLUSAO_AGENDAMENTO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("select ");
        corpo.append("agd.agd_codigo, ");
        corpo.append("agd.agd_descricao, ");
        corpo.append("agd.agd_data_cadastro, ");
        corpo.append("agd.agd_data_prevista, ");
        corpo.append("agd.agd_java_class_name, ");
        corpo.append("sag.sag_codigo, ");
        corpo.append("sag.sag_descricao, ");
        corpo.append("tag.tag_codigo, ");
        corpo.append("tag.tag_descricao, ");
        corpo.append("usu.usu_codigo, ");
        corpo.append("usu.usu_login ");
        corpo.append("from tb_agendamento agd ");
        corpo.append("inner join tb_status_agendamento sag on (sag.sag_codigo = agd.sag_codigo) ");
        corpo.append("inner join tb_tipo_agendamento tag on (tag.tag_codigo = agd.tag_codigo) ");
        corpo.append("inner join tb_usuario usu on (usu.usu_codigo = agd.usu_codigo) ");

        corpo.append("left outer join tb_ocorrencia_agendamento oag on (oag.agd_codigo = agd.agd_codigo ");
        corpo.append("and oag.toc_codigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
        corpo.append(" and oag.oag_data_inicio between format_datetime(case ");
        corpo.append(" when sag.sag_codigo = '").append(StatusAgendamentoEnum.EXECUCAO_SEMANAL.getCodigo()).append("' then add_day(data_corrente(), -6) ");
        corpo.append(" when sag.sag_codigo = '").append(StatusAgendamentoEnum.EXECUCAO_MENSAL.getCodigo()).append("' then add_day(data_corrente(), -29) ");
        corpo.append(" when sag.sag_codigo = '").append(StatusAgendamentoEnum.EXECUCAO_ANUAL.getCodigo()).append("' then add_month(data_corrente(), -12) ");
        corpo.append(" else data_corrente() end, '00:00:00') ");

        corpo.append(" and format_datetime(data_corrente(), '23:59:59')) ");

        corpo.append("where oag.oag_codigo IS NULL ");
        corpo.append(" and sag.sag_codigo ").append(criaClausulaNomeada("sagCodigos", sagCodigos));
        corpo.append(" and tag.tag_codigo ").append(criaClausulaNomeada("tagCodigos", tagCodigos));

        corpo.append(" and (agd.agd_data_prevista <= data_corrente() ");
        corpo.append(" or agd.agd_data_prevista IS NULL) ");

        corpo.append(" order by sag.sag_codigo, agd.agd_data_cadastro ASC");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        defineValorClausulaNomeada("sagCodigos", sagCodigos, query);
        defineValorClausulaNomeada("tagCodigos", tagCodigos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.AGD_CODIGO,
                Columns.AGD_DESCRICAO,
                Columns.AGD_DATA_CADASTRO,
                Columns.AGD_DATA_PREVISTA,
                Columns.AGD_JAVA_CLASS_NAME,
                Columns.SAG_CODIGO,
                Columns.SAG_DESCRICAO,
                Columns.TAG_CODIGO,
                Columns.TAG_DESCRICAO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
