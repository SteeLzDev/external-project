package com.zetra.econsig.helper.consignacao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: DuplicaParcelaHelper</p>
 * <p>Description: Helper Class para Operação de Duplicação de Parcela</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DuplicaParcelaHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DuplicaParcelaHelper.class);

    /**
     * Duplica parcelas de um determinado código de verba de uma consignatária
     * @param csaCodigo     : código da consignatária
     * @param cnvCodVerva   : código de verba
     * @param adeIndice     : ADE Indíce
     * @param multiplicador : valor a ser aplicado sobre a parcela existente, se informado
     * @param valor         : valor da parcela a ser criada, se informado
     * @param responsavel   : usuário responsavel
     * @param ipUsuario     : IP do usuário responsável
     * @throws ViewHelperException
     */
    public static void duplicaParcela(String csaCodigo, String cnvCodVerba, String adeIndice,
            BigDecimal multiplicador, BigDecimal valor,
            boolean validar, AcessoSistema responsavel) throws ViewHelperException {
        AutorizacaoDelegate adeDelegate = null;
        ConsignacaoDelegate consigDelegate = null;
        List<TransferObject> lstDuplicaParcela = null;
        try {
            if (!validar) {
                LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setConsignataria(csaCodigo);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.log.duplicacao.parcela", responsavel));
                if (!TextHelper.isNull(cnvCodVerba)) {
                    log.add(" " + ApplicationResourcesHelper.getMessage("rotulo.log.codigo.verba", responsavel, cnvCodVerba));
                }
                if (!TextHelper.isNull(adeIndice)) {
                    log.add(" " + ApplicationResourcesHelper.getMessage("rotulo.log.indice", responsavel, adeIndice));
                }
                log.write();
            }
            adeDelegate = new AutorizacaoDelegate();
            consigDelegate = new ConsignacaoDelegate();
            lstDuplicaParcela = adeDelegate.lstDuplicaParcela(csaCodigo, cnvCodVerba, adeIndice, responsavel);
        } catch (Exception ex) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }

        String msgCritica;
        List<String> critica = new ArrayList<String>();
        String msgOk = (validar) ? ApplicationResourcesHelper.getMessage("mensagem.duplicar.parcela.validada.sucesso", responsavel)
                : ApplicationResourcesHelper.getMessage("mensagem.duplicar.parcela.concluido.sucesso", responsavel);

        int totalAde = lstDuplicaParcela.size();
        int totalInvalidos = 0;
        Iterator<TransferObject> it = lstDuplicaParcela.iterator();
        TransferObject cto = null;
        String rseCodigo;
        BigDecimal adeVlr;
        String corCodigo;
        Integer adePrazo;
        Integer adeCarencia;
        String adeIdentificador;
        String cnvCodigo;
        String sad = CodedValues.SAD_DEFERIDA;
        String adeTipoVlr;
        Short adeIntFolha;
        Short adeIncMargem;
        String adePeriodicidade;

        while (it.hasNext()) {
            try {
                msgCritica = "";
                cto = it.next();
                // Dados da ADE
                rseCodigo = (String) cto.getAttribute(Columns.RSE_CODIGO);
                adeVlr = (valor != null) ? valor : new BigDecimal(cto.getAttribute(Columns.ADE_VLR).toString()).multiply(multiplicador);
                corCodigo = (String) cto.getAttribute(Columns.COR_CODIGO);
                adePrazo = Integer.valueOf(1);
                adeCarencia = Integer.valueOf(0);
                adeIdentificador = ApplicationResourcesHelper.getMessage("rotulo.duplicar.parcela.identificador", responsavel) + " " + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMdd");
                cnvCodigo = (String) cto.getAttribute(Columns.CNV_CODIGO);
                adeTipoVlr = (String) cto.getAttribute(Columns.ADE_TIPO_VLR);
                adeIntFolha = (cto.getAttribute(Columns.ADE_INT_FOLHA) == null) ? null : Short.valueOf(cto.getAttribute(Columns.ADE_INT_FOLHA).toString());
                adeIncMargem = (cto.getAttribute(Columns.ADE_INC_MARGEM) == null) ? null : Short.valueOf(cto.getAttribute(Columns.ADE_INC_MARGEM).toString());
                adePeriodicidade = (String) cto.getAttribute(Columns.ADE_PERIODICIDADE);

                // Cria parãmetro de reserva de margem
                ReservarMargemParametros reservaParam = new ReservarMargemParametros();
                reservaParam.setRseCodigo(rseCodigo);
                reservaParam.setAdeVlr(adeVlr);
                reservaParam.setCorCodigo(corCodigo);
                reservaParam.setAdePrazo(adePrazo);
                reservaParam.setAdeCarencia(adeCarencia);
                reservaParam.setAdeIdentificador(adeIdentificador);
                reservaParam.setCnvCodigo(cnvCodigo);
                reservaParam.setSadCodigo(sad);
                reservaParam.setSerSenha(null);
                reservaParam.setComSerSenha(Boolean.FALSE);
                reservaParam.setAdeTipoVlr(adeTipoVlr);
                reservaParam.setAdeIntFolha(adeIntFolha);
                reservaParam.setAdeIncMargem(adeIncMargem);
                reservaParam.setAdePeriodicidade(adePeriodicidade);
                reservaParam.setAdeIndice("gerar_maior");
                reservaParam.setValidar(Boolean.valueOf(validar));
                reservaParam.setPermitirValidacaoTaxa(Boolean.TRUE);
                reservaParam.setSerAtivo(Boolean.TRUE);
                reservaParam.setCnvAtivo(Boolean.TRUE);
                reservaParam.setSerCnvAtivo(Boolean.TRUE);
                reservaParam.setSvcAtivo(Boolean.TRUE);
                reservaParam.setCsaAtivo(Boolean.TRUE);
                reservaParam.setOrgAtivo(Boolean.TRUE);
                reservaParam.setEstAtivo(Boolean.TRUE);
                reservaParam.setCseAtivo(Boolean.TRUE);
                reservaParam.setAcao("DUPLICAR");

                consigDelegate.reservarMargem(reservaParam, responsavel);
            } catch (NumberFormatException ex) {
                msgCritica = ex.getMessage();
            } catch (AutorizacaoControllerException ex) {
                msgCritica = ex.getMessage();
            }
            if (!msgCritica.equals("")) {
                totalInvalidos++;
            }
            String prazo = "1"; // (cto.getAttribute(Columns.ADE_PRAZO) != null) ? cto.getAttribute(Columns.ADE_PRAZO).toString() : "Indeterminado";
            critica.add(TextHelper.formataMensagem(cto.getAttribute(Columns.ADE_NUMERO).toString(), " ", 10, false) + " " +
                    TextHelper.formataMensagem(cto.getAttribute(Columns.RSE_MATRICULA).toString(), " ", 12, true) + " " +
                    TextHelper.formataMensagem(cto.getAttribute(Columns.SER_CPF).toString(), " ", 14, true) + " " +
                    TextHelper.formataMensagem(cto.getAttribute(Columns.SER_NOME).toString(), " ", 30, true) + " " +
                    TextHelper.formataMensagem(prazo, " ", 13, false) + " " +
                    TextHelper.formataMensagem(msgCritica.equals("") ? msgOk : msgCritica, " ", 50, true));
        }

        String nomeArqSaida = null;
        try {
            // Grava arquivo contendo as parcelas não encontradas no sistema

            String pathSaida = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "duplicacao" + File.separatorChar
                    + "csa" + File.separatorChar + csaCodigo + File.separatorChar;
            if (!validar) {
                nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel) + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".txt";
            } else {
                nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.validacao.prefixo", responsavel) + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".txt";
            }

            File dir = new File(pathSaida);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new ViewHelperException("mensagem.erro.criacao.diretorio", responsavel, dir.getAbsolutePath());
            }

            PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaida)));
            arqSaida.println(TextHelper.formataMensagem("", "#", CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.duplicacao.parcela", responsavel), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.data", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "dd/MM/yyyy-HHmmss")), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem("", "#", CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.regras.utilizadas", responsavel), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.parcelas.iguais.a", responsavel, cnvCodVerba), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.indice", responsavel, (adeIndice != null ? adeIndice : ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel))), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.multiplicador", responsavel, (multiplicador != null ? NumberHelper.format(multiplicador.doubleValue(), NumberHelper.getLang()) : "")), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.valor", responsavel, (valor != null ? NumberHelper.format(valor.doubleValue(), NumberHelper.getLang()) : "")), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem("", "#", CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.total.registros", responsavel, String.valueOf(totalAde)), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.total.registros.validos", responsavel, String.valueOf(totalAde - totalInvalidos)), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.total.registros.invalidos", responsavel, String.valueOf(totalInvalidos)), CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem("", "#", CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            arqSaida.println(TextHelper.formataMensagem("", CodedValues.COMPLEMENTO_DEFAULT, CodedValues.TAMANHO_MSG_ERRO_DEFAULT, true));
            //CABECALHO DO ARQUIVO
            arqSaida.println("# " +
                    TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.ade.numero", responsavel), " ", 8, false) + " " +
                    TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.matricula", responsavel), " ", 12, true) + " " +
                    TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.cpf", responsavel), " ", 14, true) + " " +
                    TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.nome", responsavel), " ", 30, true) + " " +
                    TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.prazo", responsavel), " ", 13, false) + " " +
                    TextHelper.formataMensagem(ApplicationResourcesHelper.getMessage("rotulo.arq.duplicar.parcela.resultado", responsavel), " ", 50, true));

            // Imprime as linhas de critica no arquivo
            if (critica.size() > 0) {
                arqSaida.println(TextHelper.join(critica, System.getProperty("line.separator")));
            }

            arqSaida.close();
            LOG.debug("FIM ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
            // Compacta os arquvivos gerados em apenas um
            LOG.debug("compacta os arquivos: " + DateHelper.getSystemDatetime());
            String nomeArqZip = nomeArqSaida.replaceAll(".txt", ".zip");
            FileHelper.zip(nomeArqSaida, nomeArqZip);
            LOG.debug("fim - compacta os arquivos: " + DateHelper.getSystemDatetime());
            FileHelper.delete(nomeArqSaida);
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}