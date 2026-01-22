package com.zetra.econsig.service.solicitacaosuporte;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.SolicitacaoSuporteAPIException;
import com.zetra.econsig.exception.SolicitacaoSuporteControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.solicitacaosuporte.SolicitacaoSuportAppController;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.Consignante;
import com.zetra.econsig.persistence.entity.ConsignanteHome;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.persistence.entity.SolicitacaoSuporte;
import com.zetra.econsig.persistence.entity.SolicitacaoSuporteHome;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.query.usuario.ListaUsuarioSolicitacaoSuporteQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

@Service
@Transactional
public class SolicitacaoSuporteControllerBean implements SolicitacaoSuporteController {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitacaoSuporteControllerBean.class);

    @Override
    public Map<String, String> lstValoresCampoSolicitacaoSuporte(String campo, AcessoSistema responsavel) throws SolicitacaoSuporteControllerException {
        try {
            return SolicitacaoSuportAppController.getValoresCampo(campo);
        } catch (SolicitacaoSuporteAPIException | IOException e) {
            throw new SolicitacaoSuporteControllerException(e);
        }
    }

    @Override
    public String criarSolicitacaoSuporte(TransferObject solicitacaoSuporte, AcessoSistema responsavel) throws SolicitacaoSuporteControllerException {
        try {
            File file = null;
            if ((TextHelper.isNull(solicitacaoSuporte.getAttribute(Columns.SOS_SERVICO_TRANSIENTE)) && TextHelper.isNull(solicitacaoSuporte.getAttribute(Columns.SOS_TOTEM))) ||
                    TextHelper.isNull(solicitacaoSuporte.getAttribute(Columns.SOS_SUMARIO)) ||
                    TextHelper.isNull(solicitacaoSuporte.getAttribute(Columns.SOS_DESCRICAO_TRANSIENTE))) {
                LOG.error("Devem ser informado valores a todos os campos da tela.");
                throw new SolicitacaoSuporteControllerException("mensagem.informe.valores.todos.campos.tela", responsavel);
            }

            final Consignante cse = ConsignanteHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA);
            if (TextHelper.isNull(cse.getCseIdentificadorInterno())) {
                LOG.error("Identificador interno do Sistema deve ser informado.");
                throw new SolicitacaoSuporteControllerException("mensagem.informe.identificador.interno.sistema", responsavel);
            }

            final boolean totem = solicitacaoSuporte.getAttribute(Columns.SOS_TOTEM) != null;
            final String matricula = (String) solicitacaoSuporte.getAttribute(Columns.SOS_MATRICULA);
            final String usuarioSuporte = (String) solicitacaoSuporte.getAttribute(Columns.SOS_USUARIO_SUPORTE);

            solicitacaoSuporte.setAttribute(Columns.SOS_SISTEMA_TRANSIENTE, cse.getCseIdentificadorInterno());
            solicitacaoSuporte.setAttribute(Columns.SOS_CLIENTE_TRANSIENTE, responsavel.getUsuNome());
            if (responsavel.isCsaCor()) {
                solicitacaoSuporte.setAttribute(Columns.SOS_PAPEL_TRANSIENTE, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular",  responsavel));

                final Consignataria csa = ConsignatariaHome.findByPrimaryKey(responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.getCodigoEntidadePai());
                solicitacaoSuporte.setAttribute(Columns.SOS_DESCSA_TRANSIENTE, csa.getCsaIdentificador() + " - " + csa.getCsaNome());
            } else if (responsavel.isCse()) {
                solicitacaoSuporte.setAttribute(Columns.SOS_PAPEL_TRANSIENTE, ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel));
            } else if (responsavel.isOrg()) {
                solicitacaoSuporte.setAttribute(Columns.SOS_PAPEL_TRANSIENTE, ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel));
            } else if (responsavel.isSer()) {
                solicitacaoSuporte.setAttribute(Columns.SOS_PAPEL_TRANSIENTE, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel));
            } else if (responsavel.isSup()) {
                solicitacaoSuporte.setAttribute(Columns.SOS_PAPEL_TRANSIENTE, ApplicationResourcesHelper.getMessage("rotulo.zetrasoft", responsavel));
            }

            solicitacaoSuporte.setAttribute(Columns.SOS_LOGIN_ECONSIG_TRANSIENTE, responsavel.getUsuLogin());
            final Usuario usuario = UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo());
            solicitacaoSuporte.setAttribute(Columns.SOS_EMAIL_TRANSIENTE, usuario.getUsuEmail());
            solicitacaoSuporte.setAttribute(Columns.USU_CPF, usuario.getUsuCpf());
            if(responsavel.isSer()) {
                final Servidor servidor = totem && !TextHelper.isNull(matricula) ? ServidorHome.findByRseMatricula(matricula) : ServidorHome.findByUsuCodigo(usuario.getUsuCodigo());
                if(!TextHelper.isNull(servidor.getSerCelular())) {
                    solicitacaoSuporte.setAttribute(Columns.SOS_TELEFONE_TRANSIENTE, servidor.getSerCelular());
                }
                if (TextHelper.isNull(servidor.getSerCelular()) && !TextHelper.isNull(servidor.getSerTel())) {
                    solicitacaoSuporte.setAttribute(Columns.SOS_TELEFONE_TRANSIENTE, servidor.getSerCelular());
                }
            }

            if (TextHelper.isNull(solicitacaoSuporte.getAttribute(Columns.SOS_TELEFONE_TRANSIENTE)) && !TextHelper.isNull(usuario.getUsuTel())) {
                solicitacaoSuporte.setAttribute(Columns.SOS_TELEFONE_TRANSIENTE, usuario.getUsuTel());
            }

            if(totem) {
                if(!TextHelper.isNull(usuarioSuporte)) {
                    final Usuario usuSuporte = UsuarioHome.findByLogin(usuarioSuporte);
                    if(!TextHelper.isNull(usuSuporte.getUsuEmail())) {
                        solicitacaoSuporte.setAttribute(Columns.SOS_EMAIL_USUARIO_SUPORTE, usuSuporte.getUsuEmail());
                    }
                }

                if(solicitacaoSuporte.getAttribute(Columns.SOS_ARQUIVO) != null) {
                    final String diretorioRaiz = ParamSist.getDiretorioRaizArquivos();
                    final String diretorioTemporario = diretorioRaiz + File.separatorChar + "temp";
                    final String arquivo = diretorioTemporario + File.separatorChar + responsavel.hashCode() + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss") + ".jpg";

                    final byte[] arq = Base64.decodeBase64((String) solicitacaoSuporte.getAttribute(Columns.SOS_ARQUIVO));
                    FileUtils.writeByteArrayToFile(new File(arquivo), arq);

                    file = new File (arquivo);
                    solicitacaoSuporte.setAttribute(Columns.SOS_ARQUIVO, file);
                }
            }

            final String sosChave = SolicitacaoSuportAppController.criarSolicicataoSuporte(solicitacaoSuporte);

            if(!totem) {
                SolicitacaoSuporteHome.create(sosChave, responsavel.getUsuCodigo(),
                        (String) solicitacaoSuporte.getAttribute(Columns.SOS_SUMARIO),
                        (String) solicitacaoSuporte.getAttribute(Columns.SOS_PRIORIDADE));
            }

            if((file != null) && file.exists()) {
                file.delete();
            }

            return sosChave;
        } catch (SolicitacaoSuporteAPIException | CreateException | FindException | IOException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new SolicitacaoSuporteControllerException(e);
        }
    }

    @Override
    public TransferObject findSolicitacaoSuporte(String sosCodigo, AcessoSistema responsavel) throws SolicitacaoSuporteControllerException {
        try {
            final SolicitacaoSuporte solicitacao = SolicitacaoSuporteHome.findByPrimaryKey(sosCodigo);
            return SolicitacaoSuportAppController.findSolicitacaoSuporte(solicitacao.getSosChave());
        } catch (SolicitacaoSuporteAPIException | FindException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SolicitacaoSuporteControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstSolicitacaoSuporte(String usuCodigo, AcessoSistema responsavel) throws SolicitacaoSuporteControllerException {
        try {
            final List<SolicitacaoSuporte> lstSolicitacao = SolicitacaoSuporteHome.findByUsuario(usuCodigo);
            final List<TransferObject> retorno = new ArrayList<>();

            for (final SolicitacaoSuporte solicitacaoSuporte : lstSolicitacao) {
                final CustomTransferObject solicitacaoTO = new CustomTransferObject();
                solicitacaoTO.setAttribute(Columns.SOS_CHAVE, solicitacaoSuporte.getSosChave());
                solicitacaoTO.setAttribute(Columns.SOS_CODIGO, solicitacaoSuporte.getSosCodigo());
                solicitacaoTO.setAttribute(Columns.SOS_DATA_CADASTRO, solicitacaoSuporte.getSosDataCadastro());
                solicitacaoTO.setAttribute(Columns.SOS_PRIORIDADE_ID_TRANSIENTE, solicitacaoSuporte.getSosPrioridade());
                solicitacaoTO.setAttribute(Columns.SOS_PRIORIDADE, solicitacaoSuporte.getSosPrioridade());
                solicitacaoTO.setAttribute(Columns.SOS_SUMARIO, solicitacaoSuporte.getSosSumario());

                retorno.add(solicitacaoTO);
            }

            return retorno;
        } catch (final FindException e) {
            throw new SolicitacaoSuporteControllerException(e);
        }
    }

    @Override
    public String atualizaSolicitacaoSuporte(TransferObject solicitacaoSuporte, String sosCodigo, AcessoSistema responsavel) throws SolicitacaoSuporteControllerException {
        try {
            final SolicitacaoSuporte solicitacao = SolicitacaoSuporteHome.findByPrimaryKey(sosCodigo);
            final String sosPrioridade = (String) solicitacaoSuporte.getAttribute(Columns.SOS_PRIORIDADE_ID_TRANSIENTE);
            final String sosSla = (String) solicitacaoSuporte.getAttribute(Columns.SOS_SLA_INDICATOR);

            if(!TextHelper.isNull(sosPrioridade) && (TextHelper.isNull(solicitacao.getSosPrioridade()) || !solicitacao.getSosPrioridade().equals(sosPrioridade))){
                solicitacao.setSosPrioridade(sosPrioridade);
            }

            if(!TextHelper.isNull(sosSla) && (TextHelper.isNull(solicitacao.getSosSla()) || !solicitacao.getSosSla().equals(sosSla))){
                solicitacao.setSosSla(sosSla);
            }

            AbstractEntityHome.update(solicitacao);
        } catch (FindException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SolicitacaoSuporteControllerException(ex);
        }
        return null;
    }

    @Override
    public List<TransferObject> lstTodasSolicitacaoSuporte(AcessoSistema responsavel) throws SolicitacaoSuporteControllerException {
        try {
            final ListaUsuarioSolicitacaoSuporteQuery query = new ListaUsuarioSolicitacaoSuporteQuery();

            return query.executarDTO();
        } catch (final HQueryException e) {
            throw new SolicitacaoSuporteControllerException(e);
        }
    }

}
