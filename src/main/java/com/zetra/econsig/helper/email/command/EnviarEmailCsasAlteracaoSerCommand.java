package com.zetra.econsig.helper.email.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jakarta.mail.MessagingException;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ModeloEmailEnum;

/**
 * <p>Title: EnviarEmailCsasAlteracaoSerCommand</p>
 * <p>Description: Command para envio de email de notificação para CSA/COR na alteração de servidor pelo CSE/ORG.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailCsasAlteracaoSerCommand extends AbstractEnviarEmailCommand {

    private TransferObject dadosServidor;

    private String cpf;

    private String matricula;

    private String novoStatus;

    private String obsAlteracao;

    public TransferObject getDadosServidor() {
        return dadosServidor;
    }

    public void setDadosServidor(TransferObject dadosServidor) {
        this.dadosServidor = dadosServidor;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNovoStatus() {
        return novoStatus;
    }

    public void setNovoStatus(String novoStatus) {
        this.novoStatus = novoStatus;
    }

    public String getObsAlteracao() {
        return obsAlteracao;
    }

    public void setObsAlteracao(String obsAlteracao) {
        this.obsAlteracao = obsAlteracao;
    }

    @Override
    public void execute() throws ViewHelperException {
        try {
            Set<String> lstEmailsDest = lstEmailsCsaCorComAde(dadosServidor, responsavel);
            if (lstEmailsDest != null && !lstEmailsDest.isEmpty()) {
                ServidorDelegate serDelegate = new ServidorDelegate();
                String serCodigo = dadosServidor.getAttribute(Columns.SER_CODIGO) != null ?
                                      (String) dadosServidor.getAttribute(Columns.SER_CODIGO) :
                                      (String) dadosServidor.getAttribute(Columns.RSE_SER_CODIGO);

                TransferObject rseTo = null;
                if (TextHelper.isNull(matricula) || dadosServidor.getAttribute(Columns.RSE_MATRICULA) == null) {
                    List<TransferObject> lstRse = serDelegate.lstRegistroServidor(serCodigo, (String) null, (String) null, responsavel);
                    if (lstRse != null && !lstRse.isEmpty()) {
                        rseTo = lstRse.get(0);
                    }
                }

                ServidorTransferObject serTO = null;
                if (TextHelper.isNull(cpf)) {
                    serTO = serDelegate.findServidor(serCodigo, responsavel);
                }

                String chaveNovoStatus = ApplicationResourcesHelper.getMessage("rotulo.situacao.edicao", responsavel);
                String textoSituacao = (rseTo != null) ? (String) rseTo.getAttribute(Columns.SRS_DESCRICAO) : null;
                String msgAlteracaoStatus = "";
                if (!TextHelper.isNull(novoStatus)) {
                    switch (novoStatus) {
                        case CodedValues.SRS_ATIVO:
                            chaveNovoStatus = ApplicationResourcesHelper.getMessage("rotulo.situacao.ativacao", responsavel);
                            textoSituacao = ApplicationResourcesHelper.getMessage("rotulo.situacao.ativo.singular", responsavel);
                            break;

                        case CodedValues.SRS_BLOQUEADO:
                        case CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA:
                            chaveNovoStatus = ApplicationResourcesHelper.getMessage("rotulo.situacao.bloqueio", responsavel);
                            textoSituacao = ApplicationResourcesHelper.getMessage("rotulo.situacao.bloqueado.singular", responsavel);
                            break;

                        case CodedValues.SRS_EXCLUIDO:
                        case CodedValues.SRS_FALECIDO:
                            chaveNovoStatus = ApplicationResourcesHelper.getMessage("rotulo.situacao.exclusao", responsavel);
                            textoSituacao = ApplicationResourcesHelper.getMessage("rotulo.situacao.excluido.singular", responsavel);
                            break;

                        case CodedValues.SRS_PENDENTE:
                        default:
                            // do nothing
                            break;
                    }

                    if (TextHelper.isNull(textoSituacao)) {
                        throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel);
                    }
                    msgAlteracaoStatus = ApplicationResourcesHelper.getMessage("mensagem.email.dados.servidor.editado.status", responsavel, textoSituacao);
                }

                // 1. Busca o template do e-mail
                ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_CSAS_ALTERACAO_SER, responsavel);

                // 2. Preenche dos dados disponíveis para uso no template
                CustomTransferObject dados = new CustomTransferObject();
                if (rseTo != null) {
                    for (Object key : rseTo.getAtributos().keySet()) {
                        String chave = key.toString();
                        dados.setAttribute(Columns.getColumnName(chave).toLowerCase(), rseTo.getAttribute(chave));
                    }
                }
                if (serTO != null) {
                    for (Object key : serTO.getAtributos().keySet()) {
                        String chave = key.toString();
                        dados.setAttribute(Columns.getColumnName(chave).toLowerCase(), serTO.getAttribute(chave));
                    }
                }
                for (Object key : dadosServidor.getAtributos().keySet()) {
                    String chave = key.toString();
                    dados.setAttribute(Columns.getColumnName(chave).toLowerCase(), dadosServidor.getAttribute(chave));
                }
                dados.setAttribute("alteracao_servidor_operacao", chaveNovoStatus);
                dados.setAttribute("alteracao_servidor_status", textoSituacao);
                dados.setAttribute("alteracao_servidor_mensagem_status", msgAlteracaoStatus);
                dados.setAttribute("alteracao_servidor_mensagem_status_noescape", msgAlteracaoStatus);
                dados.setAttribute("alteracao_servidor_observacao", obsAlteracao);
                dados.setAttribute("alteracao_servidor_observacao_noescape", obsAlteracao);
                interpolador.setDados(dados);

                // 3. Interpola o template gerando os textos finais prontos para uso.
                String titulo = interpolador.interpolateTitulo();
                String corpo = interpolador.interpolateTexto();

                // Envia o e-mail.
                for (String destinatario: lstEmailsDest) {
                    new MailHelper().send(destinatario, null, null, titulo, corpo, null);
                }
            }
        } catch (MessagingException | ServidorControllerException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }
    /**
     * Lista os e-mails de todas consignatárias e correspondentes que tenham contrato ativo com o servidor dado
     * @param dadosSerTO - dados do servidor alvo
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    private Set<String> lstEmailsCsaCorComAde(TransferObject dadosSerTO, AcessoSistema responsavel) throws ViewHelperException {
        try {
            ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();

            String serCodigo = (!TextHelper.isNull(dadosSerTO.getAttribute(Columns.SER_CODIGO))) ? (String) dadosSerTO.getAttribute(Columns.SER_CODIGO) :
                               (!TextHelper.isNull(dadosSerTO.getAttribute(Columns.RSE_SER_CODIGO))) ? (String) dadosSerTO.getAttribute(Columns.RSE_SER_CODIGO) : null;

           if (TextHelper.isNull(serCodigo)) {
               throw new ViewHelperException("mensagem.erroInternoSistema", responsavel);
           }
           String rseCodigo = (String) dadosSerTO.getAttribute(Columns.RSE_CODIGO);
           List<String> listaRse = new ArrayList<>();

           List<TransferObject> lstCsas = null;
           if (TextHelper.isNull(rseCodigo)) {
               try {
                   List<RegistroServidorTO> lstRses = new ServidorDelegate().findRegistroServidorBySerCodigo(serCodigo, responsavel);

                   lstCsas = new ArrayList<>();
                   for (RegistroServidorTO rse: lstRses) {
                       List<TransferObject> lstTmp = csaDelegate.lstConsignatariaSerTemAde(serCodigo, rse.getRseCodigo(), true, responsavel);
                       lstCsas.addAll(lstTmp);
                       listaRse.add(rse.getRseCodigo());
                   }
               } catch (ServidorControllerException e) {
                   throw new ViewHelperException(e.getMessageKey(), responsavel, e);
               }
           } else {
               lstCsas = csaDelegate.lstConsignatariaSerTemAde(serCodigo, rseCodigo, true, responsavel);
               listaRse.add(rseCodigo);
           }

           if (lstCsas != null && !lstCsas.isEmpty()) {
               Set<String> emails = new TreeSet<>();

               AutorizacaoDelegate autDelegate = new AutorizacaoDelegate();
               List<String> csaCodigos = new ArrayList<>();

               for (TransferObject csa: lstCsas) {
                   csaCodigos.add((String) csa.getAttribute(Columns.CSA_CODIGO));
                   if (!TextHelper.isNull(csa.getAttribute(Columns.CSA_EMAIL))) {
                       emails.add((String) csa.getAttribute(Columns.CSA_EMAIL));
                   }
               }
               CustomTransferObject criterio = new CustomTransferObject();
               criterio.setAttribute("csaCodigos", csaCodigos);

               List<TransferObject> lstTotalAdes = new ArrayList<>();

               for (String rse_codigo: listaRse) {
                   List<TransferObject> lstAdes = autDelegate.pesquisaAutorizacao(AcessoSistema.ENTIDADE_CSA, null, rse_codigo, null, null, CodedValues.SAD_CODIGOS_ATIVOS, null, criterio, responsavel);
                   lstTotalAdes.addAll(lstAdes);
               }

               if (lstTotalAdes != null && !lstTotalAdes.isEmpty()) {
                   for (TransferObject adeTO: lstTotalAdes) {
                       String corIdentificador = (String) adeTO.getAttribute(Columns.COR_IDENTIFICADOR);
                       if (!TextHelper.isNull(corIdentificador)) {
                           try {
                               CorrespondenteTransferObject cor = csaDelegate.findCorrespondenteByIdn(corIdentificador, (String) adeTO.getAttribute(Columns.CSA_CODIGO), responsavel);
                               if (!TextHelper.isNull(cor.getCorEmail())) {
                                   emails.add(cor.getCorEmail());
                               }
                           } catch (ConsignatariaControllerException e) {
                           }
                       }
                   }
               }
               return emails;
           }
           return null;

        } catch (ConsignatariaControllerException e) {
            throw new ViewHelperException(e.getMessageKey(), responsavel, e);
        } catch (AutorizacaoControllerException e) {
            throw new ViewHelperException(e.getMessageKey(), responsavel, e);
        }
    }
}
