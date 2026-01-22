package com.zetra.econsig.service.consignante;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.Banco;
import com.zetra.econsig.persistence.entity.BancoHome;
import com.zetra.econsig.persistence.entity.CalendarioFolhaCse;
import com.zetra.econsig.persistence.entity.CalendarioFolhaCseHome;
import com.zetra.econsig.persistence.entity.CalendarioFolhaEst;
import com.zetra.econsig.persistence.entity.CalendarioFolhaEstHome;
import com.zetra.econsig.persistence.entity.CalendarioFolhaOrg;
import com.zetra.econsig.persistence.entity.CalendarioFolhaOrgHome;
import com.zetra.econsig.persistence.entity.Consignante;
import com.zetra.econsig.persistence.entity.ConsignanteHome;
import com.zetra.econsig.persistence.entity.DadosConsignante;
import com.zetra.econsig.persistence.entity.DadosConsignanteHome;
import com.zetra.econsig.persistence.entity.DadosConsignanteId;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCse;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCseHome;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCseId;
import com.zetra.econsig.persistence.entity.Estabelecimento;
import com.zetra.econsig.persistence.entity.EstabelecimentoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaConsignanteHome;
import com.zetra.econsig.persistence.entity.OcorrenciaOrgaoHome;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.TipoConsignante;
import com.zetra.econsig.persistence.entity.TipoConsignanteHome;
import com.zetra.econsig.persistence.query.consignante.ListaOcorrenciaConsignanteQuery;
import com.zetra.econsig.persistence.query.estabelecimento.ListaEstabelecimentoQuery;
import com.zetra.econsig.persistence.query.funcao.FuncoesEnvioEmailCseQuery;
import com.zetra.econsig.persistence.query.orgao.ListaOcorrenciaOrgaoQuery;
import com.zetra.econsig.persistence.query.orgao.ListaOrgaoQuery;
import com.zetra.econsig.persistence.query.orgao.ObtemOrgaoDiaRepasseQuery;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: ConsignanteControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ConsignanteControllerBean implements ConsignanteController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsignanteControllerBean.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private HistoricoArquivoController historicoArquivoController;

    @Autowired
    private UsuarioController usuarioController;

    @Override
    public OrgaoTransferObject findOrgao(String orgCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        final OrgaoTransferObject criterio = new OrgaoTransferObject(orgCodigo);
        return findOrgao(criterio, responsavel);
    }

    @Override
    public OrgaoTransferObject findOrgaoByIdn(String orgIdentificador, String estCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        final OrgaoTransferObject criterio = new OrgaoTransferObject();
        criterio.setOrgIdentificador(orgIdentificador);
        criterio.setEstCodigo(estCodigo);
        return findOrgao(criterio, responsavel);
    }

    // Órgão
    @Override
    public OrgaoTransferObject findOrgao(OrgaoTransferObject orgao, AcessoSistema responsavel) throws ConsignanteControllerException {
        return setOrgaoValues(findOrgaoBean(orgao, responsavel));
    }

    private Orgao findOrgaoBean(OrgaoTransferObject orgao, AcessoSistema responsavel) throws ConsignanteControllerException {
        Orgao orgaoBean = null;
        if (orgao.getOrgCodigo() != null) {
            try {
                orgaoBean = OrgaoHome.findByPrimaryKey(orgao.getOrgCodigo());
            } catch (final FindException ex) {
                throw new ConsignanteControllerException("mensagem.erro.orgao.nao.encontrado", responsavel);
            }
        } else if ((orgao.getOrgIdentificador() != null) && (orgao.getEstCodigo() != null)) {
            try {
                orgaoBean = OrgaoHome.findByIdn(orgao.getOrgIdentificador(), orgao.getEstCodigo());
            } catch (final FindException ex) {
                throw new ConsignanteControllerException("mensagem.erro.orgao.nao.encontrado", responsavel);
            }
        } else {
            throw new ConsignanteControllerException("mensagem.erro.orgao.nao.encontrado", responsavel);
        }
        return orgaoBean;
    }

    /**
     * retorna uma lista de órgãos ligados a convênios com código de verba DIRF
     * @param responsavel
     * @return
     * @throws ConsignanteControllerException
     */
    @Override
    public List<OrgaoTransferObject> listarOrgaosDirf(AcessoSistema responsavel) throws ConsignanteControllerException {
        List<OrgaoTransferObject> lstRet = null;
        try {
            final List<Orgao> listOrgaoEnt = OrgaoHome.listOrgCnvDIRF();

            for (final Orgao orgao : listOrgaoEnt) {
                if (lstRet == null) {
                    lstRet = new ArrayList<>();
                }
                lstRet.add(setOrgaoValues(orgao));
            }
        } catch (final FindException e) {
            throw new ConsignanteControllerException("mensagem.erro.orgao.nao.encontrado", responsavel);
        }

        return lstRet;
    }

    private OrgaoTransferObject setOrgaoValues(Orgao orgaoBean) {
        final OrgaoTransferObject orgao = new OrgaoTransferObject(orgaoBean.getOrgCodigo());
        orgao.setEstCodigo(orgaoBean.getEstabelecimento().getEstCodigo());
        orgao.setOrgAtivo(orgaoBean.getOrgAtivo());
        orgao.setOrgBairro(orgaoBean.getOrgBairro());
        orgao.setOrgCep(orgaoBean.getOrgCep());
        orgao.setOrgCidade(orgaoBean.getOrgCidade());
        orgao.setOrgCompl(orgaoBean.getOrgCompl());
        orgao.setOrgEmail(orgaoBean.getOrgEmail());
        orgao.setOrgEmailFolha(orgaoBean.getOrgEmailFolha());
        orgao.setOrgFax(orgaoBean.getOrgFax());
        orgao.setOrgIdentificador(orgaoBean.getOrgIdentificador());
        orgao.setOrgLogradouro(orgaoBean.getOrgLogradouro());
        orgao.setOrgNome(orgaoBean.getOrgNome());
        orgao.setOrgNomeAbrev(orgaoBean.getOrgNomeAbrev());
        orgao.setOrgNro(orgaoBean.getOrgNro());
        orgao.setOrgResponsavel(orgaoBean.getOrgResponsavel());
        orgao.setOrgResponsavel2(orgaoBean.getOrgResponsavel2());
        orgao.setOrgResponsavel3(orgaoBean.getOrgResponsavel3());
        orgao.setOrgRespCargo(orgaoBean.getOrgRespCargo());
        orgao.setOrgRespCargo2(orgaoBean.getOrgRespCargo2());
        orgao.setOrgRespCargo3(orgaoBean.getOrgRespCargo3());
        orgao.setOrgRespTelefone(orgaoBean.getOrgRespTelefone());
        orgao.setOrgRespTelefone2(orgaoBean.getOrgRespTelefone2());
        orgao.setOrgRespTelefone3(orgaoBean.getOrgRespTelefone3());
        orgao.setOrgTel(orgaoBean.getOrgTel());
        orgao.setOrgUf(orgaoBean.getOrgUf());
        orgao.setOrgCnpj(orgaoBean.getOrgCnpj());
        orgao.setOrgDiaRepasse(orgaoBean.getOrgDiaRepasse());
        orgao.setOrgIPAcesso(orgaoBean.getOrgIpAcesso());
        orgao.setOrgDDNSAcesso(orgaoBean.getOrgDdnsAcesso());
        orgao.setOrgFolha(orgaoBean.getOrgFolha());
        orgao.setOrgEmailValidarServidor(orgaoBean.getOrgEmailValidarServidor());
        return orgao;
    }

    @Override
    public String createOrgao(OrgaoTransferObject orgao, AcessoSistema responsavel) throws ConsignanteControllerException {
        String orgCodigo = null;
        try {
            try {
                // Verifica se já existe órgão com mesmos identificadores de estabelecimento e de órgão
                OrgaoHome.findByIdn(orgao.getOrgIdentificador(), orgao.getEstCodigo());
                // Envia mensagem de erro ao usuário
                throw new ConsignanteControllerException("mensagem.erro.nao.possivel.criar.este.orgao.existe.outro.mesmo.codigo.neste.estabelecimento", responsavel);
            } catch (final FindException ex) {
                // Não imprime exceção já que o esperado é que não exista mesmo
            }

            final Orgao orgaoBean = OrgaoHome.create(orgao.getEstCodigo(), orgao.getOrgIdentificador(), orgao.getOrgNome(), orgao.getOrgNomeAbrev(), orgao.getOrgCnpj(), orgao.getOrgEmail(), orgao.getOrgResponsavel(), orgao.getOrgLogradouro(), orgao.getOrgNro(), orgao.getOrgCompl(), orgao.getOrgBairro(), orgao.getOrgCidade(), orgao.getOrgUf(), orgao.getOrgCep(), orgao.getOrgTel(), orgao.getOrgFax(), orgao.getOrgAtivo(), orgao.getOrgResponsavel2(), orgao.getOrgResponsavel3(), orgao.getOrgRespCargo(), orgao.getOrgRespCargo2(), orgao.getOrgRespCargo3(), orgao.getOrgRespTelefone(),
                    orgao.getOrgRespTelefone2(), orgao.getOrgRespTelefone3(), orgao.getOrgDiaRepasse(), orgao.getOrgIPAcesso(), orgao.getOrgDDNSAcesso(), orgao.getOrgEmailFolha(), orgao.getOrgFolha());
            // Pega o código do órgão criado
            orgCodigo = orgaoBean.getOrgCodigo();
            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.ORGAO, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setOrgao(orgCodigo);
            logDelegate.setEstabelecimento(orgao.getEstCodigo());
            logDelegate.getUpdatedFields(orgao.getAtributos(), null);
            logDelegate.write();

            if (ParamSist.paramEquals(CodedValues.TPC_CADASTRO_SIMPLIFICADO_ESTABELECIMENTO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                return orgCodigo;
            }
            if (ParamSist.paramEquals(CodedValues.TPC_CRIA_USUARIO_MASTER_ORGAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                createUsuarioMasterOrgao(orgaoBean, responsavel);
            }

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erro.nao.possivel.criar.este.orgao.erro.interno", responsavel, ex.getMessage());
        }
        return orgCodigo;
    }

    private String createUsuarioMasterOrgao(Orgao orgaoBean, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final List<String> funCodigo = new ArrayList<>();

            // Lista as permissões permitidas baseado em natureza, entre outras coisas
            final List<TransferObject> funcoesPermitidas = usuarioController.lstFuncoesPermitidasPerfil(AcessoSistema.ENTIDADE_ORG, orgaoBean.getOrgCodigo(), responsavel);
            final Iterator<TransferObject> it = funcoesPermitidas.iterator();
            CustomTransferObject custom;
            while (it.hasNext()) {
                custom = (CustomTransferObject) it.next();
                funCodigo.add(custom.getAttribute(Columns.FUN_CODIGO).toString());
            }

            final String perfil = usuarioController.createPerfil(AcessoSistema.ENTIDADE_ORG, orgaoBean.getOrgCodigo(), "MASTER", null, null, null, null, null, funCodigo, null, null, responsavel);

            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.YEAR, 2099);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            final java.sql.Date data = new java.sql.Date(cal.getTimeInMillis());

            final String usuSenha = null;

            final UsuarioTransferObject usuarioTransferObject = new UsuarioTransferObject();
            usuarioTransferObject.setStuCodigo(CodedValues.STU_BLOQUEADO);
            final String login = !TextHelper.isNull(orgaoBean.getOrgEmail()) ? orgaoBean.getOrgEmail() : "MASTER" + orgaoBean.getOrgIdentificador();
            usuarioTransferObject.setUsuLogin(login);
            usuarioTransferObject.setUsuSenha(usuSenha);
            usuarioTransferObject.setUsuSenha2(null);
            usuarioTransferObject.setUsuNome(orgaoBean.getOrgNome());
            usuarioTransferObject.setUsuEmail(orgaoBean.getOrgEmail());
            usuarioTransferObject.setUsuTel(null);
            usuarioTransferObject.setUsuDicaSenha(null);
            usuarioTransferObject.setUsuTipoBloq(null);
            usuarioTransferObject.setUsuDataExpSenha(data);
            usuarioTransferObject.setUsuDataExpSenha2(null);
            usuarioTransferObject.setUsuIpAcesso(null);
            usuarioTransferObject.setUsuDDNSAcesso(null);
            usuarioTransferObject.setUsuCPF(null);
            usuarioTransferObject.setUsuCentralizador("N");
            usuarioTransferObject.setUsuExigeCertificado(null);
            usuarioTransferObject.setUsuMatriculaInst(null);
            usuarioTransferObject.setUsuChaveRecuperarSenha(null);
            usuarioTransferObject.setUsuDataFimVig(null);
            usuarioTransferObject.setUsuDeficienteVisual(null);

            return usuarioController.createUsuario(usuarioTransferObject, perfil, orgaoBean.getOrgCodigo(), AcessoSistema.ENTIDADE_ORG, null, false, usuSenha, false,responsavel);

        } catch (final UsuarioControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException(ex);
        }
    }

    @Override
    public String createOrgao(OrgaoTransferObject orgao, boolean criarConvenio, String orgCodigoACopiar, AcessoSistema responsavel) throws ConsignanteControllerException {
        final String orgCodigo = createOrgao(orgao, responsavel);
        if (criarConvenio) {
            try {
                convenioController.criaConveniosParaNovoOrgao(orgCodigo, orgao.getEstCodigo(), orgCodigoACopiar, responsavel);
            } catch (final ConvenioControllerException e) {
                throw new ConsignanteControllerException(e);
            }
        }

        return orgCodigo;
    }

    @Override
    public void updateOrgao(OrgaoTransferObject orgao, AcessoSistema responsavel) throws ConsignanteControllerException {

        try {
            final Orgao orgaoBean = findOrgaoBean(orgao, responsavel);
            final LogDelegate log = new LogDelegate(responsavel, Log.ORGAO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setOrgao(orgaoBean.getOrgCodigo());

            /* Compara a versão do cache com a passada por parâmetro */
            final OrgaoTransferObject orgaoCache = setOrgaoValues(orgaoBean);
            final CustomTransferObject merge = log.getUpdatedFields(orgao.getAtributos(), orgaoCache.getAtributos());

            final boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_ORG);

            if (merge.getAtributos().containsKey(Columns.ORG_IDENTIFICADOR) || (merge.getAtributos().containsKey(Columns.ORG_EST_CODIGO))) {

                // Verifica se não existe outro órgão com o mesmo ID no estabelecimento
                final OrgaoTransferObject teste = new OrgaoTransferObject();

                if (merge.getAtributos().containsKey(Columns.ORG_IDENTIFICADOR)) {
                    teste.setOrgIdentificador((String) merge.getAttribute(Columns.ORG_IDENTIFICADOR));
                } else {
                    teste.setOrgIdentificador(orgaoBean.getOrgIdentificador());
                }

                if (merge.getAtributos().containsKey(Columns.ORG_EST_CODIGO)) {
                    teste.setEstCodigo((String) merge.getAttribute(Columns.ORG_EST_CODIGO));
                } else {
                    teste.setEstCodigo(orgaoBean.getEstabelecimento().getEstCodigo());
                }

                boolean existe = false;
                try {
                    findOrgaoBean(teste, responsavel);
                    existe = true;
                } catch (final ConsignanteControllerException ex) {
                }
                if (existe) {
                    throw new ConsignanteControllerException("mensagem.erro.nao.possivel.alterar.este.orgao.existe.outro.mesmo.codigo.neste.estabelecimento", responsavel);
                }

                if (merge.getAtributos().containsKey(Columns.ORG_IDENTIFICADOR)) {
                    orgaoBean.setOrgIdentificador((String) merge.getAttribute(Columns.ORG_IDENTIFICADOR));
                }

                if (merge.getAtributos().containsKey(Columns.ORG_EST_CODIGO)) {
                    try {
                        final Estabelecimento estabelecimento = EstabelecimentoHome.findByPrimaryKey((String) merge.getAttribute(Columns.ORG_EST_CODIGO));
                        orgaoBean.setEstabelecimento(estabelecimento);
                    } catch (final FindException e) {
                        LOG.error("Não foi possível localizar o estabelecimento.");
                    }
                }
            }

            if (merge.getAtributos().containsKey(Columns.ORG_ATIVO)) {
                final Short orgAtivoOld = orgaoBean.getOrgAtivo();
                final Short orgAtivoNew = (Short) merge.getAttribute(Columns.ORG_ATIVO);

                if (orgAtivoOld.equals(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA) && !responsavel.isSup()) {
                    // Se está desbloqueando, e foi bloqueado por segurança, não deixa desbloqueá-lo
                    throw new ConsignanteControllerException("mensagem.erro.nao.possivel.desbloquear.orgao.arg0.pois.foi.bloqueado.por.seguranca", responsavel, orgaoBean.getOrgNome());
                }

                orgaoBean.setOrgAtivo(orgAtivoNew);
            }
            if (merge.getAtributos().containsKey(Columns.ORG_BAIRRO)) {
                orgaoBean.setOrgBairro((String) merge.getAttribute(Columns.ORG_BAIRRO));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_CEP)) {
                orgaoBean.setOrgCep((String) merge.getAttribute(Columns.ORG_CEP));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_CIDADE)) {
                orgaoBean.setOrgCidade((String) merge.getAttribute(Columns.ORG_CIDADE));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_COMPL)) {
                orgaoBean.setOrgCompl((String) merge.getAttribute(Columns.ORG_COMPL));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_DIA_REPASSE)) {
                orgaoBean.setOrgDiaRepasse((Integer) merge.getAttribute(Columns.ORG_DIA_REPASSE));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_EMAIL)) {
                orgaoBean.setOrgEmail((String) merge.getAttribute(Columns.ORG_EMAIL));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_FAX)) {
                orgaoBean.setOrgFax((String) merge.getAttribute(Columns.ORG_FAX));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_LOGRADOURO)) {
                orgaoBean.setOrgLogradouro((String) merge.getAttribute(Columns.ORG_LOGRADOURO));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_NOME)) {
                orgaoBean.setOrgNome((String) merge.getAttribute(Columns.ORG_NOME));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_NOME_ABREV)) {
                orgaoBean.setOrgNomeAbrev((String) merge.getAttribute(Columns.ORG_NOME_ABREV));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_NRO)) {
                orgaoBean.setOrgNro((Integer) merge.getAttribute(Columns.ORG_NRO));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_RESPONSAVEL)) {
                orgaoBean.setOrgResponsavel((String) merge.getAttribute(Columns.ORG_RESPONSAVEL));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_RESPONSAVEL_2)) {
                orgaoBean.setOrgResponsavel2((String) merge.getAttribute(Columns.ORG_RESPONSAVEL_2));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_RESPONSAVEL_3)) {
                orgaoBean.setOrgResponsavel3((String) merge.getAttribute(Columns.ORG_RESPONSAVEL_3));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_RESP_CARGO)) {
                orgaoBean.setOrgRespCargo((String) merge.getAttribute(Columns.ORG_RESP_CARGO));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_RESP_CARGO_2)) {
                orgaoBean.setOrgRespCargo2((String) merge.getAttribute(Columns.ORG_RESP_CARGO_2));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_RESP_CARGO_3)) {
                orgaoBean.setOrgRespCargo3((String) merge.getAttribute(Columns.ORG_RESP_CARGO_3));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_RESP_TELEFONE)) {
                orgaoBean.setOrgRespTelefone((String) merge.getAttribute(Columns.ORG_RESP_TELEFONE));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_RESP_TELEFONE_2)) {
                orgaoBean.setOrgRespTelefone2((String) merge.getAttribute(Columns.ORG_RESP_TELEFONE_2));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_RESP_TELEFONE_3)) {
                orgaoBean.setOrgRespTelefone3((String) merge.getAttribute(Columns.ORG_RESP_TELEFONE_3));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_TEL)) {
                orgaoBean.setOrgTel((String) merge.getAttribute(Columns.ORG_TEL));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_UF)) {
                orgaoBean.setOrgUf((String) merge.getAttribute(Columns.ORG_UF));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_CNPJ)) {
                orgaoBean.setOrgCnpj((String) merge.getAttribute(Columns.ORG_CNPJ));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_IP_ACESSO) && podeEditarEnderecoAcesso) {
                orgaoBean.setOrgIpAcesso((String) merge.getAttribute(Columns.ORG_IP_ACESSO));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_DDNS_ACESSO) && podeEditarEnderecoAcesso) {
                orgaoBean.setOrgDdnsAcesso((String) merge.getAttribute(Columns.ORG_DDNS_ACESSO));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_EMAIL_FOLHA)) {
                orgaoBean.setOrgEmailFolha((String) merge.getAttribute(Columns.ORG_EMAIL_FOLHA));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_EMAIL_VALIDAR_SERVIDOR)) {
                orgaoBean.setOrgEmailValidarServidor((String) merge.getAttribute(Columns.ORG_EMAIL_VALIDAR_SERVIDOR));
            }
            if (merge.getAtributos().containsKey(Columns.ORG_FOLHA)) {
                orgaoBean.setOrgFolha((String) merge.getAttribute(Columns.ORG_FOLHA));
            }
            AbstractEntityHome.update(orgaoBean);

            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void removeOrgao(OrgaoTransferObject orgao, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final Orgao orgaoBean = findOrgaoBean(orgao, responsavel);
            final String orgCodigo = orgaoBean.getOrgCodigo();
            AbstractEntityHome.remove(orgaoBean);
            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.ORGAO, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setOrgao(orgCodigo);
            logDelegate.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.RemoveException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.nao.possivel.excluir.orgao.selecionado.pois.possui.dependentes", responsavel);
        }
    }

    @Override
    public List<TransferObject> lstOrgaos(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {
        return lstOrgaos(criterio, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstOrgaos(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaOrgaoQuery query = new ListaOrgaoQuery();
            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            if (criterio != null) {
                query.orgAtivo = criterio.getAttribute(Columns.ORG_ATIVO);
                query.estIdentificador = (String) criterio.getAttribute(Columns.EST_IDENTIFICADOR);
                query.estNome = (String) criterio.getAttribute(Columns.EST_NOME);
                query.orgIdentificador = (String) criterio.getAttribute(Columns.ORG_IDENTIFICADOR);
                query.orgNome = (String) criterio.getAttribute(Columns.ORG_NOME);
                query.orgCodigo = criterio.getAttribute(Columns.ORG_CODIGO);
                query.estCodigo = (String) criterio.getAttribute(Columns.EST_CODIGO);
                query.orgEstCodigo = (String) criterio.getAttribute(Columns.ORG_EST_CODIGO);
                query.csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
            }

            return query.executarDTO();

        } catch (final HQueryException ex) {
            throw new ConsignanteControllerException(ex);
        }
    }

    @Override
    public int countOrgaos(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaOrgaoQuery query = new ListaOrgaoQuery();
            query.count = true;

            if (criterio != null) {
                query.orgAtivo = criterio.getAttribute(Columns.ORG_ATIVO);
                query.estIdentificador = (String) criterio.getAttribute(Columns.EST_IDENTIFICADOR);
                query.estNome = (String) criterio.getAttribute(Columns.EST_NOME);
                query.orgIdentificador = (String) criterio.getAttribute(Columns.ORG_IDENTIFICADOR);
                query.orgNome = (String) criterio.getAttribute(Columns.ORG_NOME);
                query.csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
            }

            return query.executarContador();

        } catch (final HQueryException ex) {
            throw new ConsignanteControllerException(ex);
        }
    }

    /**
     * Retorna um Map onde a chave é o código do órgão e o valor é
     * o dia de repasse do órgao. Faz isso para todos os órgãos se o parâmetro
     * for nulo. Retorna apenas órgãos que tenham dia de repasse setado, ou seja não nulo.
     * @param orgCodigo : código do órgão a ser selecionado o dia de repasse, nulo para todos os órgãos
     * @param responsavel : usuário responsável pela operação
     * @return : um Map onde a chave é o código do órgão e o valor é o dia de repasse
     * @throws ConsignanteControllerException
     */
    @Override
    public Map<String, Integer> getOrgDiaRepasse(String orgCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ObtemOrgaoDiaRepasseQuery query = new ObtemOrgaoDiaRepasseQuery();
            query.orgCodigo = orgCodigo;
            return query.executarMapa();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Estabelecimento
    @Override
    public EstabelecimentoTransferObject findEstabelecimento(String estCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        final EstabelecimentoTransferObject criterio = new EstabelecimentoTransferObject(estCodigo);
        return findEstabelecimento(criterio, responsavel);
    }

    @Override
    public EstabelecimentoTransferObject findEstabelecimentoByIdn(String estIdentificador, AcessoSistema responsavel) throws ConsignanteControllerException {
        final EstabelecimentoTransferObject criterio = new EstabelecimentoTransferObject();
        criterio.setEstIdentificador(estIdentificador);
        return findEstabelecimento(criterio, responsavel);
    }

    @Override
    public EstabelecimentoTransferObject findEstabelecimentoByOrgao(String orgCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            return setEstabelecimentoValues(EstabelecimentoHome.findByOrgao(orgCodigo));
        } catch (final FindException ex) {
            throw new ConsignanteControllerException("mensagem.erro.estabelecimento.nao.encontrado", responsavel);
        }
    }

    @Override
    public EstabelecimentoTransferObject findEstabelecimento(EstabelecimentoTransferObject estabelecimento, AcessoSistema responsavel) throws ConsignanteControllerException {
        return setEstabelecimentoValues(findEstabelecimentoBean(estabelecimento, responsavel));
    }

    private Estabelecimento findEstabelecimentoBean(EstabelecimentoTransferObject estabelecimento, AcessoSistema responsavel) throws ConsignanteControllerException {
        Estabelecimento estabelecimentoBean = null;
        if (estabelecimento.getEstCodigo() != null) {
            try {
                estabelecimentoBean = EstabelecimentoHome.findByPrimaryKey(estabelecimento.getEstCodigo());
            } catch (final FindException ex) {
                throw new ConsignanteControllerException("mensagem.erro.estabelecimento.nao.encontrado", responsavel);
            }
        } else if (estabelecimento.getEstIdentificador() != null) {
            try {
                estabelecimentoBean = EstabelecimentoHome.findByIdn(estabelecimento.getEstIdentificador());
            } catch (final FindException ex) {
                throw new ConsignanteControllerException("mensagem.erro.estabelecimento.nao.encontrado", responsavel);
            }
        } else if (ParamSist.paramEquals(CodedValues.TPC_CADASTRO_SIMPLIFICADO_ESTABELECIMENTO,
                CodedValues.TPC_SIM, responsavel)) {
            try {
                estabelecimentoBean = EstabelecimentoHome.findByLast();
            } catch (final FindException ex) {
                throw new ConsignanteControllerException("mensagem.erro.estabelecimento.nao.encontrado", responsavel);
            }
        } else {
            throw new ConsignanteControllerException("mensagem.erro.estabelecimento.nao.encontrado", responsavel);
        }
        return estabelecimentoBean;
    }

    private EstabelecimentoTransferObject setEstabelecimentoValues(Estabelecimento estabelecimentoBean) {
        final EstabelecimentoTransferObject estabelecimento = new EstabelecimentoTransferObject(estabelecimentoBean.getEstCodigo());
        estabelecimento.setCseCodigo(estabelecimentoBean.getConsignante().getCseCodigo());
        estabelecimento.setEstIdentificador(estabelecimentoBean.getEstIdentificador());
        estabelecimento.setEstNome(estabelecimentoBean.getEstNome());
        estabelecimento.setEstNomeAbrev(estabelecimentoBean.getEstNomeAbrev());
        estabelecimento.setEstCnpj(estabelecimentoBean.getEstCnpj());
        estabelecimento.setEstEmail(estabelecimentoBean.getEstEmail());
        estabelecimento.setEstResponsavel(estabelecimentoBean.getEstResponsavel());
        estabelecimento.setEstResponsavel2(estabelecimentoBean.getEstResponsavel2());
        estabelecimento.setEstResponsavel3(estabelecimentoBean.getEstResponsavel3());
        estabelecimento.setEstRespCargo(estabelecimentoBean.getEstRespCargo());
        estabelecimento.setEstRespCargo2(estabelecimentoBean.getEstRespCargo2());
        estabelecimento.setEstRespCargo3(estabelecimentoBean.getEstRespCargo3());
        estabelecimento.setEstRespTelefone(estabelecimentoBean.getEstRespTelefone());
        estabelecimento.setEstRespTelefone2(estabelecimentoBean.getEstRespTelefone2());
        estabelecimento.setEstRespTelefone3(estabelecimentoBean.getEstRespTelefone3());
        estabelecimento.setEstLogradouro(estabelecimentoBean.getEstLogradouro());
        estabelecimento.setEstNro(estabelecimentoBean.getEstNro());
        estabelecimento.setEstCompl(estabelecimentoBean.getEstCompl());
        estabelecimento.setEstBairro(estabelecimentoBean.getEstBairro());
        estabelecimento.setEstCidade(estabelecimentoBean.getEstCidade());
        estabelecimento.setEstUf(estabelecimentoBean.getEstUf());
        estabelecimento.setEstCep(estabelecimentoBean.getEstCep());
        estabelecimento.setEstTel(estabelecimentoBean.getEstTel());
        estabelecimento.setEstFax(estabelecimentoBean.getEstFax());
        estabelecimento.setEstAtivo(estabelecimentoBean.getEstAtivo());
        estabelecimento.setEstFolha(estabelecimentoBean.getEstFolha());

        return estabelecimento;
    }

    @Override
    public String createEstabelecimento(EstabelecimentoTransferObject estabelecimento, AcessoSistema responsavel) throws ConsignanteControllerException {
        String estCodigo = null;
        try {
            try {
                // Verifica se já existe estabelecimento com mesmo identificador
                EstabelecimentoHome.findByIdn(estabelecimento.getEstIdentificador());
                // Se encontrou, então envia erro para o usuário
                throw new ConsignanteControllerException("mensagem.erro.nao.possivel.criar.este.estabelecimento.existe.outro.mesmo.codigo", responsavel);
            } catch (final FindException ex) {
                // Não imprime exceção já que o esperado é que não exista mesmo
            }

            try {
                // OK: Não existe com mesmo Identificador, então verifica o CNPJ
                EstabelecimentoHome.findByCnpj(estabelecimento.getEstCnpj());
                // Se encontrou, então envia erro para o usuário
                throw new ConsignanteControllerException("mensagem.erro.nao.possivel.criar.este.estabelecimento.existe.outro.mesmo.cnpj", responsavel);
            } catch (final FindException ex) {
                // Não imprime exceção já que o esperado é que não exista mesmo
            }

            final Estabelecimento estabelecimentoBean = EstabelecimentoHome.create(estabelecimento.getCseCodigo(), estabelecimento.getEstIdentificador(), estabelecimento.getEstNome(), estabelecimento.getEstCnpj(), estabelecimento.getEstEmail(), estabelecimento.getEstResponsavel(), estabelecimento.getEstLogradouro(), estabelecimento.getEstNro(), estabelecimento.getEstCompl(), estabelecimento.getEstBairro(), estabelecimento.getEstCidade(), estabelecimento.getEstUf(), estabelecimento.getEstCep(), estabelecimento.getEstTel(), estabelecimento.getEstFax(), estabelecimento.getEstAtivo(),
                    estabelecimento.getEstResponsavel2(), estabelecimento.getEstResponsavel3(), estabelecimento.getEstRespCargo(), estabelecimento.getEstRespCargo2(), estabelecimento.getEstRespCargo3(), estabelecimento.getEstRespTelefone(), estabelecimento.getEstRespTelefone2(), estabelecimento.getEstRespTelefone3(), estabelecimento.getEstFolha());
            estCodigo = estabelecimentoBean.getEstCodigo();
            final LogDelegate log = new LogDelegate(responsavel, Log.ESTABELECIMENTO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setEstabelecimento(estCodigo);
            log.setConsignante(estabelecimento.getCseCodigo());
            log.getUpdatedFields(estabelecimento.getAtributos(), null);
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erro.nao.possivel.criar.este.estabelecimento.erro.interno", responsavel, ex.getMessage());
        }
        return estCodigo;
    }

    @Override
    public void updateEstabelecimento(EstabelecimentoTransferObject estabelecimento, AcessoSistema responsavel) throws ConsignanteControllerException {

        try {
            final Estabelecimento estabelecimentoBean = findEstabelecimentoBean(estabelecimento, responsavel);
            final LogDelegate log = new LogDelegate(responsavel, Log.ESTABELECIMENTO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setEstabelecimento(estabelecimentoBean.getEstCodigo());

            /* Compara a versão do cache com a passada por parâmetro */
            final EstabelecimentoTransferObject estabelecimentoCache = setEstabelecimentoValues(estabelecimentoBean);
            final CustomTransferObject merge = log.getUpdatedFields(estabelecimento.getAtributos(), estabelecimentoCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.EST_IDENTIFICADOR)) {
                try {
                    // Verifica se já existe estabelecimento com mesmo identificador
                    EstabelecimentoHome.findByIdn((String) merge.getAttribute(Columns.EST_IDENTIFICADOR));
                    // Se encontrou, então envia erro para o usuário
                    throw new ConsignanteControllerException("mensagem.erro.nao.possivel.alterar.este.estabelecimento.existe.outro.mesmo.codigo", responsavel);
                } catch (final FindException ex) {
                    // Não imprime exceção já que o esperado é que não exista mesmo
                }

                estabelecimentoBean.setEstIdentificador((String) merge.getAttribute(Columns.EST_IDENTIFICADOR));
            }
            if (merge.getAtributos().containsKey(Columns.EST_CNPJ)) {
                try {
                    // OK: Não existe com mesmo Identificador, então verifica o CNPJ
                    EstabelecimentoHome.findByCnpj((String) merge.getAttribute(Columns.EST_CNPJ));
                    // Se encontrou, então envia erro para o usuário
                    throw new ConsignanteControllerException("mensagem.erro.nao.possivel.alterar.este.estabelecimento.existe.outro.mesmo.cnpj", responsavel);
                } catch (final FindException ex) {
                    // Não imprime exceção já que o esperado é que não exista mesmo
                }

                estabelecimentoBean.setEstCnpj((String) merge.getAttribute(Columns.EST_CNPJ));
            }

            if (merge.getAtributos().containsKey(Columns.EST_ATIVO)) {
                estabelecimentoBean.setEstAtivo((Short) merge.getAttribute(Columns.EST_ATIVO));
            }
            if (merge.getAtributos().containsKey(Columns.EST_BAIRRO)) {
                estabelecimentoBean.setEstBairro((String) merge.getAttribute(Columns.EST_BAIRRO));
            }
            if (merge.getAtributos().containsKey(Columns.EST_CEP)) {
                estabelecimentoBean.setEstCep((String) merge.getAttribute(Columns.EST_CEP));
            }
            if (merge.getAtributos().containsKey(Columns.EST_CIDADE)) {
                estabelecimentoBean.setEstCidade((String) merge.getAttribute(Columns.EST_CIDADE));
            }
            if (merge.getAtributos().containsKey(Columns.EST_COMPL)) {
                estabelecimentoBean.setEstCompl((String) merge.getAttribute(Columns.EST_COMPL));
            }
            if (merge.getAtributos().containsKey(Columns.EST_EMAIL)) {
                estabelecimentoBean.setEstEmail((String) merge.getAttribute(Columns.EST_EMAIL));
            }
            if (merge.getAtributos().containsKey(Columns.EST_FAX)) {
                estabelecimentoBean.setEstFax((String) merge.getAttribute(Columns.EST_FAX));
            }
            if (merge.getAtributos().containsKey(Columns.EST_LOGRADOURO)) {
                estabelecimentoBean.setEstLogradouro((String) merge.getAttribute(Columns.EST_LOGRADOURO));
            }
            if (merge.getAtributos().containsKey(Columns.EST_NOME)) {
                estabelecimentoBean.setEstNome((String) merge.getAttribute(Columns.EST_NOME));
            }
            if (merge.getAtributos().containsKey(Columns.EST_NRO)) {
                estabelecimentoBean.setEstNro((Integer) merge.getAttribute(Columns.EST_NRO));
            }
            if (merge.getAtributos().containsKey(Columns.EST_RESPONSAVEL)) {
                estabelecimentoBean.setEstResponsavel((String) merge.getAttribute(Columns.EST_RESPONSAVEL));
            }
            if (merge.getAtributos().containsKey(Columns.EST_RESPONSAVEL_2)) {
                estabelecimentoBean.setEstResponsavel2((String) merge.getAttribute(Columns.EST_RESPONSAVEL_2));
            }
            if (merge.getAtributos().containsKey(Columns.EST_RESPONSAVEL_3)) {
                estabelecimentoBean.setEstResponsavel3((String) merge.getAttribute(Columns.EST_RESPONSAVEL_3));
            }
            if (merge.getAtributos().containsKey(Columns.EST_RESP_CARGO)) {
                estabelecimentoBean.setEstRespCargo((String) merge.getAttribute(Columns.EST_RESP_CARGO));
            }
            if (merge.getAtributos().containsKey(Columns.EST_RESP_CARGO_2)) {
                estabelecimentoBean.setEstRespCargo2((String) merge.getAttribute(Columns.EST_RESP_CARGO_2));
            }
            if (merge.getAtributos().containsKey(Columns.EST_RESP_CARGO_3)) {
                estabelecimentoBean.setEstRespCargo3((String) merge.getAttribute(Columns.EST_RESP_CARGO_3));
            }
            if (merge.getAtributos().containsKey(Columns.EST_RESP_TELEFONE)) {
                estabelecimentoBean.setEstRespTelefone((String) merge.getAttribute(Columns.EST_RESP_TELEFONE));
            }
            if (merge.getAtributos().containsKey(Columns.EST_RESP_TELEFONE_2)) {
                estabelecimentoBean.setEstRespTelefone2((String) merge.getAttribute(Columns.EST_RESP_TELEFONE_2));
            }
            if (merge.getAtributos().containsKey(Columns.EST_RESP_TELEFONE_3)) {
                estabelecimentoBean.setEstRespTelefone3((String) merge.getAttribute(Columns.EST_RESP_TELEFONE_3));
            }
            if (merge.getAtributos().containsKey(Columns.EST_TEL)) {
                estabelecimentoBean.setEstTel((String) merge.getAttribute(Columns.EST_TEL));
            }
            if (merge.getAtributos().containsKey(Columns.EST_UF)) {
                estabelecimentoBean.setEstUf((String) merge.getAttribute(Columns.EST_UF));
            }
            if (merge.getAtributos().containsKey(Columns.EST_FOLHA)) {
                estabelecimentoBean.setEstFolha((String) merge.getAttribute(Columns.EST_FOLHA));
            }

            AbstractEntityHome.update(estabelecimentoBean);
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void removeEstabelecimento(EstabelecimentoTransferObject estabelecimento, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final Estabelecimento estabelecimentoBean = findEstabelecimentoBean(estabelecimento, responsavel);
            final String estCodigo = estabelecimentoBean.getEstCodigo();
            AbstractEntityHome.remove(estabelecimentoBean);
            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.ESTABELECIMENTO, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setEstabelecimento(estCodigo);
            logDelegate.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.RemoveException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.nao.possivel.excluir.estabelecimento.selecionado.pois.possui.dependentes", responsavel);
        }
    }

    @Override
    public List<TransferObject> lstEstabelecimentos(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {
        return lstEstabelecimentos(criterio, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstEstabelecimentos(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaEstabelecimentoQuery query = new ListaEstabelecimentoQuery();

            if (criterio != null) {
                query.estIdentificador = (String) criterio.getAttribute(Columns.EST_IDENTIFICADOR);
                query.estNome = (String) criterio.getAttribute(Columns.EST_NOME);
                query.estAtivo = (Short) criterio.getAttribute(Columns.EST_ATIVO);
                query.estCodigo = criterio.getAttribute(Columns.EST_CODIGO);
            }

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ConsignanteControllerException(ex);
        }
    }

    @Override
    public int countEstabelecimentos(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaEstabelecimentoQuery query = new ListaEstabelecimentoQuery();
            query.count = true;
            query.estIdentificador = (String) criterio.getAttribute(Columns.EST_IDENTIFICADOR);
            query.estNome = (String) criterio.getAttribute(Columns.EST_NOME);
            query.estAtivo = (Short) criterio.getAttribute(Columns.EST_ATIVO);
            query.estCodigo = criterio.getAttribute(Columns.EST_CODIGO);

            return query.executarContador();
        } catch (final HQueryException ex) {
            throw new ConsignanteControllerException(ex);
        }
    }

    /**
     * Retorna a data da última atualização de versao do sistema. Utiliza a primeira data disponível com a
     * observação da ocorrência que possua o release.tag atual.
     * @return
     * @throws ConsignanteControllerException
     */
    @Override
    public String dataUltimaAtualizacaoSistema() throws ConsignanteControllerException {
        try {
            final ListaOcorrenciaConsignanteQuery query = new ListaOcorrenciaConsignanteQuery();
            query.versao = true;
            final List<TransferObject> result = query.executarDTO();
            if (result.isEmpty()) {
                return ApplicationResourcesHelper.getMessage("release.date", null);
            } else {
                final CustomTransferObject cto = (CustomTransferObject) result.get(0);
                return DateHelper.toDateTimeString((java.util.Date) cto.getAttribute(Columns.OCE_DATA));
            }

        } catch (final HQueryException ex) {
            throw new ConsignanteControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstOcorrenciaConsignante(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaOcorrenciaConsignanteQuery query = new ListaOcorrenciaConsignanteQuery();

            if (criterio != null) {
                query.cseCodigo = (String) criterio.getAttribute(Columns.CSE_CODIGO);
                query.oceCodigo = (String) criterio.getAttribute(Columns.OCE_CODIGO);
                query.tocCodigo = (String) criterio.getAttribute(Columns.TOC_CODIGO);
            }

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ConsignanteControllerException(ex);
        }
    }

    @Override
    public int countOcorrenciaConsignante(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaOcorrenciaConsignanteQuery query = new ListaOcorrenciaConsignanteQuery();

            if (criterio != null) {
                query.count = true;
                query.cseCodigo = (String) criterio.getAttribute(Columns.CSE_CODIGO);
                query.oceCodigo = (String) criterio.getAttribute(Columns.OCE_CODIGO);
                query.tocCodigo = (String) criterio.getAttribute(Columns.TOC_CODIGO);

                if (!TextHelper.isNull(criterio.getAttribute(CodedValues.FILTRO_OCE_DATA_INI)) && !TextHelper.isNull(criterio.getAttribute(CodedValues.FILTRO_OCE_DATA_FIM))) {
                    query.oceDataIni = (java.util.Date) criterio.getAttribute(CodedValues.FILTRO_OCE_DATA_INI);
                    query.oceDataFim = (java.util.Date) criterio.getAttribute(CodedValues.FILTRO_OCE_DATA_FIM);
                }
            }

            return query.executarContador();
        } catch (final HQueryException ex) {
            throw new ConsignanteControllerException(ex);
        }
    }

    @Override
    public int countOcorrenciaOrgao(TransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaOcorrenciaOrgaoQuery query = new ListaOcorrenciaOrgaoQuery();
            query.count = true;

            if (criterio != null) {
                query.orgCodigo = (String) criterio.getAttribute(Columns.ORG_CODIGO);
                query.tocCodigo = (String) criterio.getAttribute(Columns.TOC_CODIGO);
            }

            return query.executarContador();
        } catch (final HQueryException ex) {
            throw new ConsignanteControllerException(ex);
        }
    }

    // Consignante
    @Override
    public ConsignanteTransferObject findConsignante(String cseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        final ConsignanteTransferObject criterio = new ConsignanteTransferObject(cseCodigo);
        return findConsignante(criterio, responsavel);
    }

    @Override
    public ConsignanteTransferObject findConsignanteByIdn(String cseIdentificador, AcessoSistema responsavel) throws ConsignanteControllerException {
        final ConsignanteTransferObject criterio = new ConsignanteTransferObject();
        criterio.setCseIdentificador(cseIdentificador);
        return findConsignante(criterio, responsavel);
    }

    @Override
    public ConsignanteTransferObject findConsignante(ConsignanteTransferObject consignante, AcessoSistema responsavel) throws ConsignanteControllerException {
        return setConsignanteValues(findConsignanteBean(consignante, responsavel));
    }

    private Consignante findConsignanteBean(ConsignanteTransferObject consignante, AcessoSistema responsavel) throws ConsignanteControllerException {
        Consignante consignanteBean = null;
        if (consignante.getCseCodigo() != null) {
            try {
                consignanteBean = ConsignanteHome.findByPrimaryKey(consignante.getCseCodigo());
            } catch (final FindException ex) {
                throw new ConsignanteControllerException("mensagem.erro.consignante.nao.encontrado", responsavel);
            }
        } else if (consignante.getCseIdentificador() != null) {
            try {
                consignanteBean = ConsignanteHome.findByIdn(consignante.getCseIdentificador());
            } catch (final FindException ex) {
                throw new ConsignanteControllerException("mensagem.erro.consignante.nao.encontrado", responsavel);
            }
        } else {
            throw new ConsignanteControllerException("mensagem.erro.consignante.nao.encontrado", responsavel);
        }
        return consignanteBean;
    }

    private ConsignanteTransferObject setConsignanteValues(Consignante consignanteBean) {
        final ConsignanteTransferObject consignante = new ConsignanteTransferObject(consignanteBean.getCseCodigo());
        consignante.setCseAtivo(consignanteBean.getCseAtivo());
        consignante.setCseBairro(consignanteBean.getCseBairro());
        consignante.setCseCep(consignanteBean.getCseCep());
        consignante.setCseCidade(consignanteBean.getCseCidade());
        consignante.setCseCnpj(consignanteBean.getCseCnpj());
        consignante.setCseCompl(consignanteBean.getCseCompl());
        consignante.setCseEmail(consignanteBean.getCseEmail());
        consignante.setCseFax(consignanteBean.getCseFax());
        consignante.setCseIdentificador(consignanteBean.getCseIdentificador());
        consignante.setCseLogradouro(consignanteBean.getCseLogradouro());
        consignante.setCseNome(consignanteBean.getCseNome());
        consignante.setCseNro(consignanteBean.getCseNro());
        consignante.setCseResponsavel(consignanteBean.getCseResponsavel());
        consignante.setCseResponsavel2(consignanteBean.getCseResponsavel2());
        consignante.setCseResponsavel3(consignanteBean.getCseResponsavel3());
        consignante.setCseRespCargo(consignanteBean.getCseRespCargo());
        consignante.setCseRespCargo2(consignanteBean.getCseRespCargo2());
        consignante.setCseRespCargo3(consignanteBean.getCseRespCargo3());
        consignante.setCseRespTelefone(consignanteBean.getCseRespTelefone());
        consignante.setCseRespTelefone2(consignanteBean.getCseRespTelefone2());
        consignante.setCseRespTelefone3(consignanteBean.getCseRespTelefone3());
        consignante.setCseTel(consignanteBean.getCseTel());
        consignante.setCseUf(consignanteBean.getCseUf());
        consignante.setCseLicenca(consignanteBean.getCseLicenca());
        consignante.setCseCertificadoCentralizador(consignanteBean.getCseCertificadoCentralizador());
        consignante.setCseCertificadoCentralMobile(consignanteBean.getCseCertificadoCentralMobile());
        consignante.setCseRsaPublicKeyCentralizador(consignanteBean.getCseRsaPublicKeyCentralizador());
        consignante.setCseRsaModulusCentralizador(consignanteBean.getCseRsaModulusCentralizador());
        consignante.setCseIPAcesso(consignanteBean.getCseIpAcesso());
        consignante.setCseDDNSAcesso(consignanteBean.getCseDdnsAcesso());
        consignante.setCseEmailFolha(consignanteBean.getCseEmailFolha());
        consignante.setIdentificadorInterno(consignanteBean.getCseIdentificadorInterno());
        consignante.setCseDataCobranca(consignanteBean.getCseDataCobranca() != null ? new Date(consignanteBean.getCseDataCobranca().getTime()) : null);
        consignante.setTipoConsignante(consignanteBean.getTipoConsignante() != null ? consignanteBean.getTipoConsignante().getTceCodigo() : null);
        consignante.setCseFolha(consignanteBean.getCseFolha());
        consignante.setCseEmailValidarServidor(consignanteBean.getCseEmailValidarServidor());
        consignante.setCseProjetoInadimplencia(consignanteBean.getCseProjetoInadimplencia());
        consignante.setCseDataAtualizacaoCadastral(consignanteBean.getCseDataAtualizacaoCadastral());
        consignante.setCseSistemaFolha(consignanteBean.getCseSistemaFolha());
        consignante.setBanco(consignanteBean.getBanco() != null ? consignanteBean.getBanco().getBcoCodigo() : null);
        return consignante;
    }

    @Override
    public void setBancosCse(List<String> cseBancos) {
        try {
            BancoHome.updateBcoCodigo(cseBancos);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void updateConsignante(ConsignanteTransferObject consignante, AcessoSistema responsavel) throws ConsignanteControllerException {
        this.updateConsignante(consignante, null, responsavel);
    }

    @Override
    public void updateConsignante(ConsignanteTransferObject consignante, String msg, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final Consignante consignanteBean = findConsignanteBean(consignante, responsavel);
            final LogDelegate log = new LogDelegate(responsavel, Log.CONSIGNANTE, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setConsignante(consignanteBean.getCseCodigo());

            if (!TextHelper.isNull(msg)) {
                log.add(ApplicationResourcesHelper.getMessage("rotulo.motivo.arg0", responsavel, msg));
            }

            /* Compara a versão do cache com a passada por parâmetro */
            final ConsignanteTransferObject consignanteCache = setConsignanteValues(consignanteBean);
            final CustomTransferObject merge = log.getUpdatedFields(consignante.getAtributos(), consignanteCache.getAtributos());

            final boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_CSE);

            if (merge.getAtributos().containsKey(Columns.CSE_IDENTIFICADOR)) {
                consignanteBean.setCseIdentificador((String) merge.getAttribute(Columns.CSE_IDENTIFICADOR));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_IDENTIFICADOR_INTERNO)) {
                consignanteBean.setCseIdentificadorInterno((String) merge.getAttribute(Columns.CSE_IDENTIFICADOR_INTERNO));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_ATIVO)) {
                consignanteBean.setCseAtivo((Short) merge.getAttribute(Columns.CSE_ATIVO));

                final Short status = (Short) consignante.getAttribute(Columns.CSE_ATIVO);

                if (status.equals(CodedValues.STS_ATIVO)) {
                    createOcorrenciaCse(CodedValues.TOC_SISTEMA_ATIVO, msg, responsavel);
                } else {
                    createOcorrenciaCse(CodedValues.TOC_SISTEMA_INDISPONIVEL, msg, responsavel);
                }
            }
            if (merge.getAtributos().containsKey(Columns.CSE_BAIRRO)) {
                consignanteBean.setCseBairro((String) merge.getAttribute(Columns.CSE_BAIRRO));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_CEP)) {
                consignanteBean.setCseCep((String) merge.getAttribute(Columns.CSE_CEP));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_CIDADE)) {
                consignanteBean.setCseCidade((String) merge.getAttribute(Columns.CSE_CIDADE));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_CNPJ)) {
                consignanteBean.setCseCnpj((String) merge.getAttribute(Columns.CSE_CNPJ));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_COMPL)) {
                consignanteBean.setCseCompl((String) merge.getAttribute(Columns.CSE_COMPL));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_EMAIL)) {
                consignanteBean.setCseEmail((String) merge.getAttribute(Columns.CSE_EMAIL));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_FAX)) {
                consignanteBean.setCseFax((String) merge.getAttribute(Columns.CSE_FAX));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_LOGRADOURO)) {
                consignanteBean.setCseLogradouro((String) merge.getAttribute(Columns.CSE_LOGRADOURO));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_NOME)) {
                consignanteBean.setCseNome((String) merge.getAttribute(Columns.CSE_NOME));
                // Atualização de Cache
                LoginHelper.setCseNome((String) merge.getAttribute(Columns.CSE_NOME));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_NRO)) {
                consignanteBean.setCseNro((Integer) merge.getAttribute(Columns.CSE_NRO));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_RESPONSAVEL)) {
                consignanteBean.setCseResponsavel((String) merge.getAttribute(Columns.CSE_RESPONSAVEL));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_RESPONSAVEL_2)) {
                consignanteBean.setCseResponsavel2((String) merge.getAttribute(Columns.CSE_RESPONSAVEL_2));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_RESPONSAVEL_3)) {
                consignanteBean.setCseResponsavel3((String) merge.getAttribute(Columns.CSE_RESPONSAVEL_3));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_RESP_CARGO)) {
                consignanteBean.setCseRespCargo((String) merge.getAttribute(Columns.CSE_RESP_CARGO));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_RESP_CARGO_2)) {
                consignanteBean.setCseRespCargo2((String) merge.getAttribute(Columns.CSE_RESP_CARGO_2));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_RESP_CARGO_3)) {
                consignanteBean.setCseRespCargo3((String) merge.getAttribute(Columns.CSE_RESP_CARGO_3));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_RESP_TELEFONE)) {
                consignanteBean.setCseRespTelefone((String) merge.getAttribute(Columns.CSE_RESP_TELEFONE));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_RESP_TELEFONE_2)) {
                consignanteBean.setCseRespTelefone2((String) merge.getAttribute(Columns.CSE_RESP_TELEFONE_2));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_RESP_TELEFONE_3)) {
                consignanteBean.setCseRespTelefone3((String) merge.getAttribute(Columns.CSE_RESP_TELEFONE_3));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_TEL)) {
                consignanteBean.setCseTel((String) merge.getAttribute(Columns.CSE_TEL));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_UF)) {
                consignanteBean.setCseUf((String) merge.getAttribute(Columns.CSE_UF));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_IP_ACESSO) && podeEditarEnderecoAcesso) {
                consignanteBean.setCseIpAcesso((String) merge.getAttribute(Columns.CSE_IP_ACESSO));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_DDNS_ACESSO) && podeEditarEnderecoAcesso) {
                consignanteBean.setCseDdnsAcesso((String) merge.getAttribute(Columns.CSE_DDNS_ACESSO));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_EMAIL_FOLHA)) {
                consignanteBean.setCseEmailFolha((String) merge.getAttribute(Columns.CSE_EMAIL_FOLHA));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_EMAIL_VALIDAR_SERVIDOR)) {
                consignanteBean.setCseEmailValidarServidor((String) merge.getAttribute(Columns.CSE_EMAIL_VALIDAR_SERVIDOR));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_CERTIFICADO_CENTRALIZADOR)) {
                consignanteBean.setCseCertificadoCentralizador((String) merge.getAttribute(Columns.CSE_CERTIFICADO_CENTRALIZADOR));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_CERTIFICADO_CENTRAL_MOBILE)) {
                consignanteBean.setCseCertificadoCentralMobile((String) merge.getAttribute(Columns.CSE_CERTIFICADO_CENTRAL_MOBILE));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_DATA_COBRANCA) && responsavel.isSup()) {
                consignanteBean.setCseDataCobranca((java.sql.Date) merge.getAttribute(Columns.CSE_DATA_COBRANCA));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_TCE_CODIGO) && responsavel.isSup()) {
                final String tceCodigo = (String) merge.getAttribute(Columns.CSE_TCE_CODIGO);
                if (!TextHelper.isNull(tceCodigo)) {
                    consignanteBean.setTipoConsignante(new TipoConsignante((String) merge.getAttribute(Columns.CSE_TCE_CODIGO)));
                } else {
                    consignanteBean.setTipoConsignante(null);
                }
            }
            if (merge.getAtributos().containsKey(Columns.CSE_FOLHA)) {
                consignanteBean.setCseFolha((String) merge.getAttribute(Columns.CSE_FOLHA));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_PROJETO_INADIMPLENCIA)) {
                consignanteBean.setCseProjetoInadimplencia((String) merge.getAttribute(Columns.CSE_PROJETO_INADIMPLENCIA));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_DATA_ATUALIZACAO_CADASTRAL)) {
                consignanteBean.setCseDataAtualizacaoCadastral((java.util.Date) merge.getAttribute(Columns.CSE_DATA_ATUALIZACAO_CADASTRAL));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_SISTEMA_FOLHA)) {
                consignanteBean.setCseSistemaFolha((String) merge.getAttribute(Columns.CSE_SISTEMA_FOLHA));
            }
            if (merge.getAtributos().containsKey(Columns.CSE_BCO_CODIGO) && responsavel.isSup()) {
                final Short bcoCodigo = (Short) merge.getAttribute(Columns.CSE_BCO_CODIGO);
                if (!TextHelper.isNull(bcoCodigo)) {
                    consignanteBean.setBanco(new Banco((Short) merge.getAttribute(Columns.CSE_BCO_CODIGO)));
                } else {
                    consignanteBean.setBanco(null);
                }
            }
            AbstractEntityHome.update(consignanteBean);

            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void createOcorrenciaCse(String tocCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        this.createOcorrenciaCse(tocCodigo, null, responsavel);
    }

    @Override
    public void createOcorrenciaCse(String tocCodigo, String msg, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            OcorrenciaConsignanteHome.create(tocCodigo, msg, responsavel.getUsuCodigo(), responsavel.getIpUsuario());
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void createOcorrenciaOrg(String orgCodigo, String tocCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            OcorrenciaOrgaoHome.create(orgCodigo, tocCodigo, responsavel.getUsuCodigo(), responsavel.getIpUsuario());
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TipoConsignante> lstTipoCse(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            return TipoConsignanteHome.findAll();
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<Banco> lstBanco(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            return BancoHome.findAllActive();
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<Banco> lstBancoFolha(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            return BancoHome.findAllFolha();
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Limpa o código de folha das tabelas de consignante, estabelecimento e orgão
     * @param responsavel
     * @throws ConsignanteControllerException
     */
    @Override
    public void limparCodigoFolha(AcessoSistema responsavel) throws ConsignanteControllerException {

        try {
            // Limpa cse_folha
            ConsignanteTransferObject consignante = new ConsignanteTransferObject(CodedValues.CSE_CODIGO_SISTEMA);
            consignante = findConsignante(consignante, responsavel);
            if (StringUtils.isNotBlank(consignante.getCseFolha())) {
                consignante.setCseFolha("");
                updateConsignante(consignante, responsavel);
            }
            // Limpa est_folha
            final List<TransferObject> estabelecimentos = lstEstabelecimentos(null, responsavel);
            for (final TransferObject estabelecimento : estabelecimentos) {
                if (StringUtils.isNotBlank((String) estabelecimento.getAttribute(Columns.EST_FOLHA))) {
                    final EstabelecimentoTransferObject estTo = findEstabelecimento(new EstabelecimentoTransferObject((String) estabelecimento.getAttribute(Columns.EST_CODIGO)), responsavel);
                    estTo.setEstFolha("");
                    updateEstabelecimento(estTo, responsavel);
                }
            }
            // Limpa org_folha
            final List<TransferObject> orgaos = lstOrgaos(null, responsavel);
            for (final TransferObject orgao : orgaos) {
                if (StringUtils.isNotBlank((String) orgao.getAttribute(Columns.ORG_FOLHA))) {
                    final OrgaoTransferObject orgaoTo = findOrgao(new OrgaoTransferObject((String) orgao.getAttribute(Columns.ORG_CODIGO)), responsavel);
                    orgaoTo.setOrgFolha("");
                    updateOrgao(orgaoTo, responsavel);
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

    }

    /**
     * Envia notificação de alerta de não envio de arquivos folha próximo à data esperada
     * @param responsavel
     * @throws ConsignanteControllerException
     */
    @Override
    public void enviaNotificacaoEnvioArquivosFolha(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {

            // Recupera os parâmetros e verifica se existem
            final Object paramAntes = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_ANTES_NOTIFICACAO_ENVIO_ARQ_FOLHA, responsavel);
            final Object paramDepois = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_DEPOIS_NOTIFICACAO_ENVIO_ARQ_FOLHA, responsavel);
            if (((paramAntes == null) || paramAntes.toString().isBlank()) && ((paramDepois == null) || paramDepois.toString().isBlank())) {
                return;
            }

            // Recupera os períodos atuais para cada entidade
            final java.util.Date now = DateHelper.getSystemDate();
            final List<TransferObject> periodos = periodoController.obtemPeriodoImpRetorno(null, null, false, responsavel);

            // Converte o parâmetro para uma lista de integers
            final List<Integer> diasNotificacaoAntes = new ArrayList<>();
            if (paramAntes != null) {
                final String[] diasNotificacaoAntesSt = StringUtils.split(paramAntes.toString(), ",");
                for (final String diaSt : diasNotificacaoAntesSt) {
                    Integer dia;
                    try {
                        dia = Integer.parseInt(diaSt);
                    } catch (final NumberFormatException e) {
                        throw new ConsignanteControllerException("mensagem.erro.interno.parametro.sistema.arg0.contem.valor.incorreto", responsavel, CodedValues.TPC_DIAS_ANTES_NOTIFICACAO_ENVIO_ARQ_FOLHA);
                    }
                    diasNotificacaoAntes.add(dia);
                }
            }

            final List<Integer> diasNotificacaoDepois = new ArrayList<>();
            if (paramDepois != null) {
                final String[] diasNotificacaoDepoisSt = StringUtils.split(paramDepois.toString(), ",");
                for (final String diaSt : diasNotificacaoDepoisSt) {
                    Integer dia;
                    try {
                        dia = Integer.parseInt(diaSt);
                    } catch (final NumberFormatException e) {
                        throw new ConsignanteControllerException("mensagem.erro.interno.parametro.sistema.arg0.contem.valor.incorreto", responsavel, CodedValues.TPC_DIAS_ANTES_NOTIFICACAO_ENVIO_ARQ_FOLHA);
                    }
                    diasNotificacaoDepois.add(dia);
                }
            }

            // Carrega os dados iniciais
            final List<String> tiposArquivo = Arrays.asList(TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO.getCodigo());
            final List<String> periodosJaVerificadosCse = new ArrayList<>();
            final Map<String, List<String>> periodosJaVerificadosEst = new HashMap<>();
            final List<String> extensoes = Arrays.asList("", ".prc", ".prc.ok", ".crypt", ".prc.ok.crypt", ".aguardando", ".erro");

            for (final TransferObject periodoEntidade : periodos) {
                final Date periodo = (Date) periodoEntidade.getAttribute(Columns.PEX_PERIODO);

                try {
                    final CalendarioFolhaOrg cfo = CalendarioFolhaOrgHome.findByPrimaryKey((String) periodoEntidade.getAttribute(Columns.ORG_CODIGO), periodo);
                    final java.util.Date dataPrevistaRetorno = cfo.getCfoDataPrevistaRetorno();
                    if (dataPrevistaRetorno != null) {
                        final List<TransferObject> arquivos = historicoArquivoController.lstHistoricoArquivo(tiposArquivo, periodo, AcessoSistema.ENTIDADE_ORG, responsavel);
                        if ((arquivos != null) && !arquivos.isEmpty()) {
                            for (final TransferObject arquivo : arquivos) {
                                boolean arqFisicoOrgExiste = false;

                                for (final String extensao : extensoes) {
                                    arqFisicoOrgExiste = existeArquivoFisico(AcessoSistema.ENTIDADE_ORG, (String) arquivo.getAttribute(Columns.HAR_NOME_ARQUIVO) + extensao, (String) periodoEntidade.getAttribute(Columns.EST_CODIGO), (String) periodoEntidade.getAttribute(Columns.ORG_CODIGO), responsavel);
                                    if (arqFisicoOrgExiste) {
                                        break;
                                    }
                                }
                                if (!arqFisicoOrgExiste) {
                                    // se arquivo não existe fisicamente apesar ter registro no histório, envia e-mail
                                    final OrgaoTransferObject org = findOrgao((String) periodoEntidade.getAttribute(Columns.ORG_CODIGO), responsavel);
                                    for (final Integer dias : diasNotificacaoAntes) {
                                        if (dataPrevistaRetorno.compareTo(DateHelper.clearHourTime(DateHelper.addDays(now, dias))) == 0) {
                                            EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(org.getOrgEmailFolha(), dataPrevistaRetorno, true, responsavel);
                                        }
                                    }
                                    for (final Integer dias : diasNotificacaoDepois) {
                                        if (now.compareTo(DateHelper.clearHourTime(DateHelper.addDays(dataPrevistaRetorno, dias))) == 0) {
                                            EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(org.getOrgEmailFolha(), dataPrevistaRetorno, false, responsavel);
                                        }
                                    }
                                }
                            }
                        } else {
                            // não existem arquivos para o período. Envia e-mail
                            final OrgaoTransferObject org = findOrgao((String) periodoEntidade.getAttribute(Columns.ORG_CODIGO), responsavel);
                            for (final Integer dias : diasNotificacaoAntes) {
                                if (dataPrevistaRetorno.compareTo(DateHelper.clearHourTime(DateHelper.addDays(now, dias))) == 0) {
                                    EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(org.getOrgEmailFolha(), dataPrevistaRetorno, true, responsavel);
                                }
                            }
                            for (final Integer dias : diasNotificacaoDepois) {
                                if (now.compareTo(DateHelper.clearHourTime(DateHelper.addDays(dataPrevistaRetorno, dias))) == 0) {
                                    EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(org.getOrgEmailFolha(), dataPrevistaRetorno, false, responsavel);
                                }
                            }
                        }
                    }
                } catch (final FindException fex) {
                    LOG.debug("Registro calendário não encontrado");
                }

                try {
                    List<String> lstPeriodosEst = periodosJaVerificadosEst.get(periodoEntidade.getAttribute(Columns.EST_CODIGO));

                    if ((lstPeriodosEst == null) || (lstPeriodosEst.isEmpty() && !lstPeriodosEst.contains(DateHelper.toDateString(periodo)))) {
                        // evita verificação e envio de e-mail repetido para estabelecimento
                        if (lstPeriodosEst == null) {
                            lstPeriodosEst = new ArrayList<>();
                        }
                        lstPeriodosEst.add(DateHelper.toDateString(periodo));
                        periodosJaVerificadosEst.put((String) periodoEntidade.getAttribute(Columns.EST_CODIGO), lstPeriodosEst);

                        final CalendarioFolhaEst cfe = CalendarioFolhaEstHome.findByPrimaryKey((String) periodoEntidade.getAttribute(Columns.EST_CODIGO), periodo);
                        final java.util.Date dataPrevistaRetorno = cfe.getCfeDataPrevistaRetorno();
                        if (dataPrevistaRetorno != null) {
                            final List<TransferObject> arquivos = historicoArquivoController.lstHistoricoArquivo(tiposArquivo, periodo, AcessoSistema.ENTIDADE_EST, responsavel);
                            if ((arquivos != null) && !arquivos.isEmpty()) {
                                for (final TransferObject arquivo : arquivos) {
                                    boolean arqFisicoEstExiste = false;

                                    for (final String extensao : extensoes) {
                                        arqFisicoEstExiste = existeArquivoFisico(AcessoSistema.ENTIDADE_EST, (String) arquivo.getAttribute(Columns.HAR_NOME_ARQUIVO) + extensao, (String) periodoEntidade.getAttribute(Columns.EST_CODIGO), null, responsavel);
                                        if (arqFisicoEstExiste) {
                                            break;
                                        }
                                    }
                                    if (!arqFisicoEstExiste) {
                                        // se arquivo não existe fisicamente apesar ter registro no histório, envia e-mail
                                        final EstabelecimentoTransferObject est = findEstabelecimento((String) periodoEntidade.getAttribute(Columns.EST_CODIGO), responsavel);
                                        for (final Integer dias : diasNotificacaoAntes) {
                                            if (dataPrevistaRetorno.compareTo(DateHelper.clearHourTime(DateHelper.addDays(now, dias))) == 0) {
                                                EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(est.getEstEmail(), dataPrevistaRetorno, true, responsavel);
                                            }
                                        }
                                        for (final Integer dias : diasNotificacaoDepois) {
                                            if (now.compareTo(DateHelper.clearHourTime(DateHelper.addDays(dataPrevistaRetorno, dias))) == 0) {
                                                EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(est.getEstEmail(), dataPrevistaRetorno, false, responsavel);
                                            }
                                        }
                                    }
                                }
                            } else {
                                // não existem arquivos para o período. Envia e-mail
                                final EstabelecimentoTransferObject est = findEstabelecimento((String) periodoEntidade.getAttribute(Columns.EST_CODIGO), responsavel);
                                for (final Integer dias : diasNotificacaoAntes) {
                                    if (dataPrevistaRetorno.compareTo(DateHelper.clearHourTime(DateHelper.addDays(now, dias))) == 0) {
                                        EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(est.getEstEmail(), dataPrevistaRetorno, true, responsavel);
                                    }
                                }
                                for (final Integer dias : diasNotificacaoDepois) {
                                    if (now.compareTo(DateHelper.clearHourTime(DateHelper.addDays(dataPrevistaRetorno, dias))) == 0) {
                                        EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(est.getEstEmail(), dataPrevistaRetorno, false, responsavel);
                                    }
                                }
                            }
                        }
                    }
                } catch (final FindException fex) {
                    LOG.debug("Registro calendário não encontrado");
                }

                try {
                    if (!periodosJaVerificadosCse.contains(DateHelper.toDateString(periodo))) {
                        // evita verificação e envio de e-mail repetido para consignante
                        periodosJaVerificadosCse.add(DateHelper.toDateString(periodo));

                        final CalendarioFolhaCse cfc = CalendarioFolhaCseHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA, periodo);
                        final java.util.Date dataPrevistaRetorno = cfc.getCfcDataPrevistaRetorno();
                        if (dataPrevistaRetorno != null) {
                            final List<TransferObject> arquivos = historicoArquivoController.lstHistoricoArquivo(tiposArquivo, periodo, AcessoSistema.ENTIDADE_CSE, responsavel);
                            if ((arquivos != null) && !arquivos.isEmpty()) {
                                for (final TransferObject arquivo : arquivos) {
                                    boolean arqFisicoCseExiste = false;

                                    for (final String extensao : extensoes) {
                                        arqFisicoCseExiste = existeArquivoFisico(AcessoSistema.ENTIDADE_CSE, (String) arquivo.getAttribute(Columns.HAR_NOME_ARQUIVO) + extensao, null, null, responsavel);
                                        if (arqFisicoCseExiste) {
                                            break;
                                        }
                                    }
                                    if (!arqFisicoCseExiste) {
                                        // se arquivo não existe fisicamente apesar ter registro no histório, envia e-mail
                                        final ConsignanteTransferObject cse = findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                                        for (final Integer dias : diasNotificacaoAntes) {
                                            if (dataPrevistaRetorno.compareTo(DateHelper.clearHourTime(DateHelper.addDays(now, dias))) == 0) {
                                                EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(cse.getCseEmailFolha(), dataPrevistaRetorno, true, responsavel);
                                            }
                                        }
                                        for (final Integer dias : diasNotificacaoDepois) {
                                            if (now.compareTo(DateHelper.clearHourTime(DateHelper.addDays(dataPrevistaRetorno, dias))) == 0) {
                                                EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(cse.getCseEmailFolha(), dataPrevistaRetorno, false, responsavel);
                                            }
                                        }
                                    }
                                }
                            } else {
                                // não existem arquivos para o período. Envia e-mail
                                final ConsignanteTransferObject cse = findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                                for (final Integer dias : diasNotificacaoAntes) {
                                    if (dataPrevistaRetorno.compareTo(DateHelper.clearHourTime(DateHelper.addDays(now, dias))) == 0) {
                                        EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(cse.getCseEmailFolha(), dataPrevistaRetorno, true, responsavel);
                                    }
                                }
                                for (final Integer dias : diasNotificacaoDepois) {
                                    if (now.compareTo(DateHelper.clearHourTime(DateHelper.addDays(dataPrevistaRetorno, dias))) == 0) {
                                        EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha(cse.getCseEmailFolha(), dataPrevistaRetorno, false, responsavel);
                                    }
                                }
                            }
                        }
                    }
                } catch (final FindException fex) {
                    LOG.debug("Registro calendário não encontrado");
                }
            }

        } catch (final Exception e) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * confere se arquivos registrados na tabela histórico de arquivo realmente existem fisicamente
     * @param tipoEntidade
     * @param nomeArquivo
     * @param estCodigo
     * @param orgCodigo
     * @param responsavel
     * @return
     */
    private boolean existeArquivoFisico(String tipoEntidade, String nomeArquivo, String estCodigo, String orgCodigo, AcessoSistema responsavel) {
        // Diretório Raiz eConsig e dos arquivos de configuração
        final String absolutePath = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "retorno";
        final List<String> lstPath = new ArrayList<>();

        switch (tipoEntidade) {
		case AcessoSistema.ENTIDADE_CSE:
			lstPath.add(absolutePath + File.separatorChar + "cse");
			break;
		case AcessoSistema.ENTIDADE_EST:
			lstPath.add(absolutePath + File.separatorChar + "est");
			lstPath.add(absolutePath + File.separatorChar + "est" + File.separatorChar + estCodigo);
			break;
		case AcessoSistema.ENTIDADE_ORG:
			lstPath.add(absolutePath + File.separatorChar + "cse");
			lstPath.add(absolutePath + File.separatorChar + "cse" + File.separatorChar + orgCodigo);
			//se responsável ORG que fez upload tem permissão de acessar consignações do estabelecimento, grava neste diretório abaixo
            lstPath.add(absolutePath + File.separatorChar + "est" + File.separatorChar + estCodigo);
			break;
		case null:
		default:
			break;
		}

        // Verifica se o arquivo de entrada existe e pode ser lido
        if (!TextHelper.isNull(nomeArquivo)) {
            for (final String path : lstPath) {
                final String fileName = path + File.separatorChar + nomeArquivo;
                final File arqEntrada = new File(fileName);
                if (arqEntrada.exists() && arqEntrada.canRead()) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public Orgao findByOrgCnpj(String orgCnpj, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            return OrgaoHome.findByOrgCnpj(orgCnpj);
        } catch (final FindException e) {
            throw new ConsignanteControllerException("mensagem.convenio.nenhum.orgao.encontrado", responsavel, e);
        }
    }

    @Override
    public Estabelecimento findByEstCnpj(String estCnpj, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            return EstabelecimentoHome.findByCnpj(estCnpj);
        } catch (final FindException e) {
            throw new ConsignanteControllerException("mensagem.convenio.nenhum.orgao.encontrado", responsavel, e);
        }
    }

    @Override
    public String findDadoAdicionalConsignante(String cseCodigo, String tdaCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final DadosConsignanteId id = new DadosConsignanteId(cseCodigo, tdaCodigo);
            final DadosConsignante dac = DadosConsignanteHome.findByPrimaryKey(id);
            return dac.getDacValor();
        } catch (final FindException ex) {
            return null;
        }
    }

    @Override
    public List<TransferObject> lstFuncoesEnvioEmailCse(String cseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            return new FuncoesEnvioEmailCseQuery(cseCodigo).executarDTO();
        } catch (final HQueryException  ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.consignante.nenhuma.funcao.encontrada", responsavel, ex);
        }
    }

    @Override
    public void salvarFuncoesEnvioEmailCse(List<DestinatarioEmailCse> listaInc, List<DestinatarioEmailCse> listaAlt, List<DestinatarioEmailCse> listaExc, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            if ((listaInc != null) && !listaInc.isEmpty()) {
                for (final DestinatarioEmailCse deeInc : listaInc) {
                    DestinatarioEmailCseHome.create(deeInc.getFunCodigo(), deeInc.getPapCodigo(), deeInc.getCseCodigo(), deeInc.getDeeReceber(), deeInc.getDeeEmail());

                    final LogDelegate log = new LogDelegate(responsavel, Log.CONFIGURACAO_ENVIO_EMAIL_CSE, Log.CREATE, Log.LOG_INFORMACAO);
                    log.setFuncao(deeInc.getFunCodigo());
                    log.setPapel(deeInc.getPapCodigo());
                    log.setConsignante(deeInc.getCseCodigo());
                    log.addChangedField(Columns.DEE_RECEBER, deeInc.getDeeReceber());
                    log.addChangedField(Columns.DEE_EMAIL, deeInc.getDeeEmail());
                    log.write();
                }
            }
            if ((listaAlt != null) && !listaAlt.isEmpty()) {
                for (final DestinatarioEmailCse deeAlt : listaAlt) {
                    final DestinatarioEmailCse dee = DestinatarioEmailCseHome.findByPrimaryKey(deeAlt.getFunCodigo(), deeAlt.getPapCodigo(), deeAlt.getCseCodigo());
                    final String deeReceberOld = dee.getDeeReceber();
                    final String deeEmailOld = dee.getDeeEmail();

                    dee.setDeeReceber(deeAlt.getDeeReceber());
                    dee.setDeeEmail(deeAlt.getDeeEmail());
                    AbstractEntityHome.update(dee);

                    final LogDelegate log = new LogDelegate(responsavel, Log.CONFIGURACAO_ENVIO_EMAIL_CSE, Log.UPDATE, Log.LOG_INFORMACAO);
                    log.setFuncao(deeAlt.getFunCodigo());
                    log.setPapel(deeAlt.getPapCodigo());
                    log.setConsignante(deeAlt.getCseCodigo());
                    if (!deeReceberOld.equals(dee.getDeeReceber())) {
                        log.addChangedField(Columns.DEE_RECEBER, dee.getDeeReceber(), deeReceberOld);
                    }
                    if (((deeEmailOld != null) && (dee.getDeeEmail() == null)) || ((deeEmailOld == null) && (dee.getDeeEmail() != null)) || ((deeEmailOld != null) && (dee.getDeeEmail() != null) && !deeEmailOld.equals(dee.getDeeEmail()))) {
                        log.addChangedField(Columns.DEE_EMAIL, dee.getDeeEmail(), deeEmailOld);
                    }
                    log.write();
                }
            }
            if ((listaExc != null) && !listaExc.isEmpty()) {
                for (final DestinatarioEmailCse deeExc : listaExc) {
                    final DestinatarioEmailCse dee = DestinatarioEmailCseHome.findByPrimaryKey(deeExc.getFunCodigo(), deeExc.getPapCodigo(), deeExc.getCseCodigo());
                    AbstractEntityHome.remove(dee);

                    final LogDelegate log = new LogDelegate(responsavel, Log.CONFIGURACAO_ENVIO_EMAIL_CSE, Log.DELETE, Log.LOG_INFORMACAO);
                    log.setFuncao(deeExc.getFunCodigo());
                    log.setPapel(deeExc.getPapCodigo());
                    log.setConsignante(deeExc.getCseCodigo());
                    log.write();
                }
            }
        } catch (CreateException | UpdateException | RemoveException | FindException | LogControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String getEmailCseNotificacaoOperacao(String funCodigo, String papCodigoOperador, String cseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        final ConsignanteTransferObject cse = findConsignante(cseCodigo, responsavel);
        try {
            final DestinatarioEmailCse bean = DestinatarioEmailCseHome.findByPrimaryKey(new DestinatarioEmailCseId(funCodigo, papCodigoOperador, cseCodigo));
            if ("N".equalsIgnoreCase(bean.getDeeReceber())) {
                // Se existe o registro e a consignante optou por não receber, então retorna nulo
                return null;
            } else {
                // Se existe o registro e a consignante optou por receber, verifica se tem e-mail específico para o envio
                return !TextHelper.isNull(bean.getDeeEmail()) ? bean.getDeeEmail() : cse.getCseEmail();
            }
        } catch (final FindException ex) {
            // Se a CSE não tem configuração específica sobre a função, então deve retornar
            // o e-mail geral da consignatária
            return cse.getCseEmail();
        }
    }
}
