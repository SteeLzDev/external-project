package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.query.definicaotaxajuros.ListaServicosDefinicaoTaxaJurosQuery;
import com.zetra.econsig.values.Columns;

public class DefinicaoTaxaJurosHome extends AbstractEntityHome {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DefinicaoTaxaJurosHome.class);

    public static DefinicaoTaxaJuros findByPrimaryKey(String dtjCodigo) throws FindException {
        final DefinicaoTaxaJuros definicaoTaxaJuros = new DefinicaoTaxaJuros();
        definicaoTaxaJuros.setDtjCodigo(dtjCodigo);
        return find(definicaoTaxaJuros, dtjCodigo);
    }

    public static DefinicaoTaxaJuros findDefinicaoTaxaJurosByCodigo(String dtjCodigo) throws FindException {
        final String query = "FROM DefinicaoTaxaJuros dtj left join fetch dtj.orgao org join fetch dtj.servico svc WHERE dtj.dtjCodigo = :dtjCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("dtjCodigo", dtjCodigo);

        final List<DefinicaoTaxaJuros> definicaoTaxaJuros = findByQuery(query, parameters);

        if (definicaoTaxaJuros == null || definicaoTaxaJuros.size() == 0) {
            return null;
        } else if (definicaoTaxaJuros.size() == 1) {
            return definicaoTaxaJuros.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado", (AcessoSistema) null);
        }
    }

    public static List<DefinicaoTaxaJuros> selectDefinicaoTaxaJurosTabelaAtiva(String csaCodigo, AcessoSistema responsavel) throws FindException {
        final String query = "FROM DefinicaoTaxaJuros dtj WHERE dtj.consignataria.csaCodigo = :csaCodigo AND dtj.dtjDataVigenciaIni is not null AND dtj.dtjDataVigenciaFim is null";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);

        final List<DefinicaoTaxaJuros> definicaoTaxaJuros = findByQuery(query, parameters);

        if (definicaoTaxaJuros == null || definicaoTaxaJuros.size() == 0) {
            return null;
        } else {
            return definicaoTaxaJuros;
        }
    }

    public static DefinicaoTaxaJuros create(String csaCodigo, String orgCodigo, String svcCodigo, String funCodigo, Short dtjFaixaEtariaIni, Short dtjFaixaEtariaFim, Short dtjFaixaTempServicoIni, Short dtjFaixaTempServicoFim, BigDecimal dtjFaixaSalarioIni, BigDecimal dtjFaixaSalarioFim, BigDecimal dtjFaixaMargemIni, BigDecimal dtjFaixaMargemFim, BigDecimal dtjFaixaValorTotalIni, BigDecimal dtjFaixaValorTotalFim, BigDecimal dtjFaixaValorContratoIni, BigDecimal dtjFaixaValorContratoFim, Short dtjFaixaPrazoIni, Short dtjFaixaPrazoFim, BigDecimal dtjTaxaJuros, BigDecimal dtjTaxaJurosMinima, Date dtjDataCadastro) throws CreateException {
        final Session session = SessionUtil.getSession();
        try {
            final DefinicaoTaxaJuros definicaoTaxaJuros = new DefinicaoTaxaJuros();
            definicaoTaxaJuros.setDtjCodigo(DBHelper.getNextId());

            definicaoTaxaJuros.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            definicaoTaxaJuros.setServico(session.getReference(Servico.class, svcCodigo));
            if (!TextHelper.isNull(orgCodigo)) {
                definicaoTaxaJuros.setOrgao(session.getReference(Orgao.class, orgCodigo));
            }
            if (!TextHelper.isNull(funCodigo)) {
                definicaoTaxaJuros.setFuncao(session.getReference(Funcao.class, funCodigo));
            }
            definicaoTaxaJuros.setDtjTaxaJuros(dtjTaxaJuros);
            definicaoTaxaJuros.setDtjTaxaJurosMinima(dtjTaxaJurosMinima);
            definicaoTaxaJuros.setDtjFaixaEtariaIni(dtjFaixaEtariaIni);
            definicaoTaxaJuros.setDtjFaixaEtariaFim(dtjFaixaEtariaFim);
            definicaoTaxaJuros.setDtjFaixaTempServicoIni(dtjFaixaTempServicoIni);
            definicaoTaxaJuros.setDtjFaixaTempServicoFim(dtjFaixaTempServicoFim);
            definicaoTaxaJuros.setDtjFaixaSalarioIni(dtjFaixaSalarioIni);
            definicaoTaxaJuros.setDtjFaixaSalarioFim(dtjFaixaSalarioFim);
            definicaoTaxaJuros.setDtjFaixaMargemIni(dtjFaixaMargemIni);
            definicaoTaxaJuros.setDtjFaixaMargemFim(dtjFaixaMargemFim);
            definicaoTaxaJuros.setDtjFaixaValorTotalIni(dtjFaixaValorTotalIni);
            definicaoTaxaJuros.setDtjFaixaValorTotalFim(dtjFaixaValorTotalFim);
            definicaoTaxaJuros.setDtjFaixaValorContratoIni(dtjFaixaValorContratoIni);
            definicaoTaxaJuros.setDtjFaixaValorContratoFim(dtjFaixaValorContratoFim);
            definicaoTaxaJuros.setDtjFaixaPrazoIni(dtjFaixaPrazoIni);
            definicaoTaxaJuros.setDtjFaixaPrazoFim(dtjFaixaPrazoFim);
            definicaoTaxaJuros.setDtjDataCadastro(dtjDataCadastro);

            return create(definicaoTaxaJuros, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void deletarTabelaIniciada(String csaCodigo) {
        final Session session = SessionUtil.getSession();
        final String queryDelete = "DELETE FROM DefinicaoTaxaJuros dtj WHERE dtj.dtjDataVigenciaIni IS NULL and dtj.dtjDataVigenciaFim IS NULL and dtj.consignataria.csaCodigo = :csaCodigo";
        final MutationQuery query = session.createMutationQuery(queryDelete);
        query.setParameter("csaCodigo", csaCodigo);

        query.executeUpdate();
        SessionUtil.closeSession(session);
    }

    public static void ativarTabela(String csaCodigo, AcessoSistema responsavel) throws UpdateException {
        final Session session = SessionUtil.getSession();
        final List<CustomTransferObject> lista = new ArrayList<>();

        try {
            // lista serviços
            final ListaServicosDefinicaoTaxaJurosQuery query = new ListaServicosDefinicaoTaxaJurosQuery();
            query.csaCodigo = csaCodigo;
            final List<String> servicos = query.executarLista();

            // Para cada servico busco a data possível para cadastrar a data vigência inicial
            for (int i = 0; i < servicos.size(); i++) {
                final String svcCodigo = servicos.get(i).toString();

                // Busca a data usando o svc_codigo
                final CustomTransferObject dataPossivel = SimulacaoHelper.calcularDataTaxaJuros(svcCodigo, responsavel);
                final Date data = DateHelper.parse(dataPossivel.getAttribute(Columns.CFT_DATA_INI_VIG).toString(), "yyyy-MM-dd");

                // -1 para subtrair um segundo da data acima para preenchimento da data FIM a ser usada no método ativarTabela()
                final Date dataFim = DateHelper.addSeconds(data, -1);

                CustomTransferObject obj = new CustomTransferObject();
                obj.setAttribute(Columns.DTJ_SVC_CODIGO, svcCodigo);
                obj.setAttribute(Columns.DTJ_DATA_VIGENCIA_INI, data);
                obj.setAttribute(Columns.DTJ_DATA_VIGENCIA_FIM, dataFim);

                lista.add(obj);
            }
        } catch (ViewHelperException | ParseException | HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UpdateException(ex);
        }

        final String hqlAtualizaDataFim = "UPDATE DefinicaoTaxaJuros dtj SET dtj.dtjDataVigenciaFim = :dataAtual WHERE dtj.dtjDataVigenciaIni IS NOT NULL and dtj.dtjDataVigenciaFim IS NULL and dtj.consignataria.csaCodigo = :csaCodigo and dtj.servico.svcCodigo = :svcCodigo";
        final MutationQuery queryAtualizaDataFim = session.createMutationQuery(hqlAtualizaDataFim);

        final String hqlAtualizaDataIni = "UPDATE DefinicaoTaxaJuros dtj SET dtj.dtjDataVigenciaIni = :dataAtual WHERE dtj.dtjDataVigenciaIni IS NULL and dtj.dtjDataVigenciaFim IS NULL and dtj.consignataria.csaCodigo = :csaCodigo and dtj.servico.svcCodigo = :svcCodigo";
        final MutationQuery queryAtualizaDataIni = session.createMutationQuery(hqlAtualizaDataIni);

        for (CustomTransferObject obj : lista) {
            queryAtualizaDataFim.setParameter("dataAtual", obj.getAttribute(Columns.DTJ_DATA_VIGENCIA_FIM));
            queryAtualizaDataFim.setParameter("svcCodigo", obj.getAttribute(Columns.DTJ_SVC_CODIGO));
            queryAtualizaDataFim.setParameter("csaCodigo", csaCodigo);
            queryAtualizaDataFim.executeUpdate();

            queryAtualizaDataIni.setParameter("dataAtual", obj.getAttribute(Columns.DTJ_DATA_VIGENCIA_INI));
            queryAtualizaDataIni.setParameter("svcCodigo", obj.getAttribute(Columns.DTJ_SVC_CODIGO));
            queryAtualizaDataIni.setParameter("csaCodigo", csaCodigo);
            queryAtualizaDataIni.executeUpdate();
        }

        SessionUtil.closeSession(session);
    }
}
