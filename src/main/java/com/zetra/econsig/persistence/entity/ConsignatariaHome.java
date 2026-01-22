package com.zetra.econsig.persistence.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ConsignatariaHome</p>
 * <p>Description: Classe Home para a entidade Consignataria</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsignatariaHome extends AbstractEntityHome {

    public static Consignataria findByPrimaryKey(String csaCodigo) throws FindException {
        Consignataria consignataria = new Consignataria();
        consignataria.setCsaCodigo(csaCodigo);
        return find(consignataria, csaCodigo);
    }

    public static Consignataria findByIdn(String csaIdentificador) throws FindException {
        String query = "FROM Consignataria csa WHERE csa.csaIdentificador = :csaIdentificador";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaIdentificador", csaIdentificador);

        List<Consignataria> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Consignataria findByCorrespondente(String corCodigo) throws FindException {
        String query = "SELECT csa FROM Consignataria csa INNER JOIN csa.correspondenteSet cor WHERE cor.corCodigo = :corCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("corCodigo", corCodigo);

        List<Consignataria> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Consignataria findByAdeCodigo(String adeCodigo) throws FindException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT csa FROM AutDesconto ade ");
        query.append("INNER JOIN ade.verbaConvenio vco ");
        query.append("INNER JOIN vco.convenio cnv ");
        query.append("INNER JOIN cnv.consignataria csa ");
        query.append("WHERE ade.adeCodigo = :adeCodigo ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        List<Consignataria> result = findByQuery(query.toString(), parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Consignataria create(String csaIdentificador, String csaNome, String csaEmail, String csaCnpj, String csaCnpjCta,
            String csaResponsavel, String csaLogradouro, Integer csaNro, String csaCompl, String csaBairro,
            String csaCidade, String csaUf, String csaCep, String csaTel, String csaFax, String csaNroBco,
            String csaNroCta, String csaNroAge, String csaDigCta, Short csaAtivo, String csaResponsavel2,
            String csaResponsavel3, String csaRespCargo, String csaRespCargo2, String csaRespCargo3,
            String csaRespTelefone, String csaRespTelefone2, String csaRespTelefone3, String csaTxtContato,
            String csaContato, String csaContatoTel, String csaEndereco2, String csaNomeAbreviado, String tgcCodigo,
            String csaIdentificadorInterno, Date csaDataExpiracao, String csaNroContrato, String csaIPAcesso,
            String csaDDNSAcesso, String csaExigeEnderecoAcesso, String csaUnidadeOrganizacional, String csaNroContratoZetra, String csaNcaNatureza,
            String csaProjetoInadimplencia, String csaEmailExpiracao, Date csaDataExpiracaoCadastral, String csaInstrucaoAnexo, String csaPermiteIncluirAde, String csaCodigoAns, String csaEmailProjInadimplencia, String csaNumeroProcesso, Date csaDataIniContrato, Date csaDataRenovacaoContrato, String csaObsContrato, String csaPermiteApi,
            String csaWhatsapp, String csaEmailContato, String csaConsultaMargemSemSenha, String csaEmailNotificacaoRco) throws CreateException {

        Session session = SessionUtil.getSession();

        Consignataria bean = new Consignataria();
        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setCsaCodigo(objectId);
            bean.setCsaIdentificador(csaIdentificador);
            bean.setCsaNome(csaNome);
            bean.setCsaEmail(csaEmail);
            bean.setCsaEmailExpiracao(csaEmailExpiracao);
            bean.setCsaCnpj(csaCnpj);
            bean.setCsaCnpjCta(csaCnpjCta);
            bean.setCsaResponsavel(csaResponsavel);
            bean.setCsaLogradouro(csaLogradouro);
            bean.setCsaNro(csaNro);
            bean.setCsaCompl(csaCompl);
            bean.setCsaBairro(csaBairro);
            bean.setCsaCidade(csaCidade);
            bean.setCsaUf(csaUf);
            bean.setCsaCep(csaCep);
            bean.setCsaTel(csaTel);
            bean.setCsaFax(csaFax);
            bean.setCsaNroBco(csaNroBco);
            bean.setCsaNroCta(csaNroCta);
            bean.setCsaNroAge(csaNroAge);
            bean.setCsaDigCta(csaDigCta);
            bean.setCsaAtivo(csaAtivo);
            bean.setCsaResponsavel2(csaResponsavel2);
            bean.setCsaResponsavel3(csaResponsavel3);
            bean.setCsaRespCargo(csaRespCargo);
            bean.setCsaRespCargo2(csaRespCargo2);
            bean.setCsaRespCargo3(csaRespCargo3);
            bean.setCsaRespTelefone(csaRespTelefone);
            bean.setCsaRespTelefone2(csaRespTelefone2);
            bean.setCsaRespTelefone3(csaRespTelefone3);
            bean.setCsaTxtContato(csaTxtContato);
            bean.setCsaContato(csaContato);
            bean.setCsaContatoTel(csaContatoTel);
            bean.setCsaEndereco2(csaEndereco2);
            bean.setCsaNomeAbrev(csaNomeAbreviado);
            bean.setCsaIdentificadorInterno(csaIdentificadorInterno);
            bean.setCsaDataExpiracao(csaDataExpiracao);
            bean.setCsaDataExpiracaoCadastral(csaDataExpiracaoCadastral);
            bean.setCsaNroContrato(csaNroContrato);
            bean.setCsaIpAcesso(csaIPAcesso);
            bean.setCsaDdnsAcesso(csaDDNSAcesso);
            bean.setCsaExigeEnderecoAcesso(csaExigeEnderecoAcesso);
            bean.setCsaUnidadeOrganizacional(csaUnidadeOrganizacional);
            bean.setCsaNroContratoZetra(csaNroContratoZetra);
            bean.setCsaProjetoInadimplencia(csaProjetoInadimplencia);
            bean.setCsaInstrucaoAnexo(csaInstrucaoAnexo);
            bean.setCsaPermiteIncluirAde(!TextHelper.isNull(csaPermiteIncluirAde) ? csaPermiteIncluirAde : "S");
            bean.setCsaCodigoAns(csaCodigoAns);
            bean.setCsaEmailProjInadimplencia(csaEmailProjInadimplencia);
            bean.setCsaObsContrato(csaObsContrato);
            bean.setCsaNumContrato(csaNumeroProcesso);
            bean.setCsaDataRenovacaoContrato(csaDataRenovacaoContrato);
            bean.setCsaDataIniContrato(csaDataIniContrato);
            if (tgcCodigo != null) {
                bean.setTipoGrupoConsignataria(session.getReference(TipoGrupoConsignataria.class, tgcCodigo));
            }
            if (csaNcaNatureza != null) {
                bean.setNaturezaConsignataria(session.getReference(NaturezaConsignataria.class, csaNcaNatureza));
            }
            bean.setCsaPermiteApi(csaPermiteApi == null ? "N" : csaPermiteApi);
            bean.setCsaWhatsapp(csaWhatsapp);
            bean.setCsaEmailContato(csaEmailContato);
            bean.setCsaConsultaMargemSemSenha(!TextHelper.isNull(csaConsultaMargemSemSenha) ? csaConsultaMargemSemSenha : "N");
            bean.setCsaEmailNotificacaoRco(csaEmailNotificacaoRco);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static List<Consignataria> findByProjetoInadimplencia(String csaProjetoInadimplencia) throws FindException {
        StringBuilder query = new StringBuilder();
        query.append("FROM Consignataria csa ");
        query.append("WHERE csa.csaProjetoInadimplencia = :csaProjetoInadimplencia ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaProjetoInadimplencia", csaProjetoInadimplencia);

        return findByQuery(query.toString(), parameters);
    }

    /**
     * Busca consignatarias com email cadastrado
     * @return
     * @throws FindException
     */
    public static List<Consignataria> findByEmailCadastrado() throws FindException {
        StringBuilder query = new StringBuilder();
        query.append("FROM Consignataria csa ");
        query.append("WHERE csa.csaEmail IS NOT NULL ");
        query.append("AND trim(csa.csaEmail) != '' ");

        return findByQuery(query.toString(), null);
    }

    public static Consignataria findConsignatariaByNumeroContratoBeneficio(String numeroContratoBenificio) throws FindException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT distinct csa ");
        query.append("FROM ContratoBeneficio cbe ");
        query.append("INNER JOIN cbe.beneficio ben ");
        query.append("INNER JOIN ben.consignataria csa ");
        query.append("WHERE cbe.cbeNumero = :numeroContratoBenificio");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("numeroContratoBenificio", numeroContratoBenificio);

        List<Consignataria> lista = findByQuery(query.toString(), parameters);

        if (lista == null || lista.size() == 0) {
            return null;
        } else if (lista.size() == 1) {
            return lista.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado", (AcessoSistema) null);
        }
    }

    public static List<Consignataria> lstConsignatariaByNcaCodigo(String ncaCodigo, AcessoSistema resposavel) throws FindException{
        StringBuilder query = new StringBuilder();
        query.append("SELECT distinct csa ");
        query.append("FROM Consignataria csa ");
        query.append("INNER JOIN csa.naturezaConsignataria nca ");
        query.append("WHERE nca.ncaCodigo = :ncaCodigo");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ncaCodigo", ncaCodigo);

        List<Consignataria> lista = findByQuery(query.toString(), parameters);

        if (lista == null || lista.size() == 0) {
            lista = new ArrayList<>();
            return lista;
        } else {
            return lista;
        }
    }

    /**
     * Atualiza o motivo de bloqueio da consignatária quando esta está bloqueada
     * @param csaCodigo
     * @param tmbCodigo
     * @throws UpdateException
     */
    public static void updateMotivoBloqueio(String csaCodigo, String tmbCodigo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            String hql = "UPDATE Consignataria set tipoMotivoBloqueio.tmbCodigo = :tmbCodigo WHERE csaCodigo = :csaCodigo AND csaAtivo = :csaInativo";
            MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("csaCodigo", csaCodigo);
            queryUpdate.setParameter("tmbCodigo", tmbCodigo);
            queryUpdate.setParameter("csaInativo", CodedValues.STS_INATIVO);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}