package com.zetra.econsig.helper.seguranca;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OcorrenciaUsuarioTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ControleLogin</p>
 * <p>Description: Faz o controle de tentativas de login do usuário e o bloqueia, caso
 *                 o número de tentativas defindas no sistema tenha sido alcançado.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ControleLogin {
    private static final int VALOR_DEFAULT_TENTATIVAS = 5;
    //  Sinlgeton, instância única desta classe de controle de login
    private static ControleLogin singleton;
    //  Intervalo de tempo em horas, onde a contagem das consultas deve ser reiniciado
    private int intervalo = 1;
    // Faz um mapeamento onde a chave é o usuCodigo ou usuCodigo
    // e o valor é o tempo da primeira consulta, indicando o inicio do período
    private Map<String, Long> hora;
    // Faz um mapeamento onde a chave é o usuCodigo
    // e o valor é o número de consultas realizadas no período
    private Map<String, Integer> tentativas;
    // Quantidade de consultas máximas em um dado intervalo de tempo
    private int limite;
    // Tempo, em milisegundos, da realização da última limpeza
    private long ultimaLimpeza = Calendar.getInstance().getTimeInMillis();
    // Intervalo de tempo, em milisegundos, da realização de limpezas no cache
    private static final long intervaloLimpeza = 1000 * 60 * 60;

    static {
        singleton = new ControleLogin();
    }

    private ControleLogin() {
        reset();
    }

    public static ControleLogin getInstance() {
        return singleton;
    }

    public void reset() {
        try {
            Object param = ParamSist.getInstance().getParam(CodedValues.TPC_NUM_MAX_TENTATIVA_LOGIN, null);
            limite = param != null ? Integer.parseInt(param.toString()) : VALOR_DEFAULT_TENTATIVAS;

        } catch (Exception ex) {
            limite = 0;
            intervalo = 0;
        }

        hora = new HashMap<>();
        tentativas = new HashMap<>();
    }

    public synchronized void bloqueiaUsuario (TransferObject usuario, AcessoSistema responsavel) throws ZetraException {
        bloqueiaUsuario(usuario, responsavel, null);
    }

    public synchronized void bloqueiaUsuario (TransferObject usuario, AcessoSistema responsavel, String messageKey) throws ZetraException {
        try {
            Object param = ParamSist.getInstance().getParam(CodedValues.TPC_NUM_MAX_TENTATIVA_LOGIN, null);
            limite = param != null ? Integer.parseInt(param.toString()) : VALOR_DEFAULT_TENTATIVAS;

        } catch (Exception ex) {
            limite = 0;
            intervalo = 0;
        }

        int limiteTentativas = limite;

        if (limiteTentativas != 0) {
            long agora = Calendar.getInstance().getTimeInMillis();

            // Verifica a necessidade de uma limpeza nos hashs
            if (agora > (ultimaLimpeza + intervaloLimpeza)) {
                ultimaLimpeza = agora;
                limpa();
            }

            // Chave dos mapeamentos.
            String chave = usuario.getAttribute(Columns.USU_CODIGO).toString();

            Long tempo = hora.get(chave);

            if (tempo == null ) {

                if (limiteTentativas == 1) {
                    //Se o limite foi alcançado, então bloqueia o usuário
                    UsuarioDelegate usuDelegate = new UsuarioDelegate();
                    UsuarioTransferObject usuTransfer = usuDelegate.findUsuario(chave, responsavel);
                    usuTransfer.setStuCodigo(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE);
                    usuTransfer.setUsuTipoBloq(ApplicationResourcesHelper.getMessage("mensagem.usuario.bloqueado.automaticamente", responsavel));

                    // Grava ocorrência de bloqueio automático do usuário
                    OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                    ocorrencia.setUsuCodigo(chave);
                    ocorrencia.setTocCodigo(CodedValues.TOC_BLOQUEIO_AUTOMATICO_USUARIO);
                    ocorrencia.setOusUsuCodigo((responsavel.getUsuCodigo() != null) ? responsavel.getUsuCodigo():AcessoSistema.getAcessoUsuarioSistema().getUsuCodigo());
                    ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.bloqueio.automatico.usuario.n.maximo.tentativas.login", responsavel));
                    ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

                    usuDelegate.updateUsuario(usuTransfer, ocorrencia, responsavel);
                    
                    final boolean enviaEmailCseBloqueioUsuario = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIAR_EMAIL_ALERTA_USUARIO_BLOQUEADO_LOGIN_MALSUCEDIDO, responsavel);
                    if(enviaEmailCseBloqueioUsuario) {
                        EnviaEmailHelper.enviarEmailCseBloqueioUsuario(usuTransfer, responsavel);
                    }

                    ZetraException ze = null;

                    if (TextHelper.isNull(messageKey)){
                        ze = new ZetraException("mensagem.informacao.aviso.numero.maximo.tentativas.login.alcancado", responsavel);
                    } else {
                        ze = new ZetraException("mensagem.informacao.aviso.numero.maximo.tentativas.login.alcancado.arg0", responsavel, ApplicationResourcesHelper.getMessage(messageKey, responsavel));
                    }

                    throw ze;
                } else if (limiteTentativas == 2) {
                    hora.put(chave, Long.valueOf(agora));
                    tentativas.put(chave, Integer.valueOf(1));

                    ZetraException ze = null;

                    if (TextHelper.isNull(messageKey)){
                        ze = new ZetraException("mensagem.informacao.aviso.ultima.tentativa", responsavel);
                    } else {
                        ze = new ZetraException("mensagem.informacao.aviso.ultima.tentativa.arg0", responsavel, ApplicationResourcesHelper.getMessage(messageKey, responsavel));
                    }

                    throw ze;
                }

                //Se é a primeira consulta, então adiciona os valores aos mapeamentos
                hora.put(chave, Long.valueOf(agora));
                tentativas.put(chave, Integer.valueOf(1));

            } else {
                // Pega a data da última consulta
                Date dataUltConsulta = new Date(tempo.longValue());
                // Pega a data atual
                Date dataAtual = new Date(agora);

                // Se a diferença em dias é maior do que o intervalo,
                // então reinicializa o controle
                if (hourDiff(dataAtual, dataUltConsulta) >= intervalo) {
                    hora.put(chave, Long.valueOf(agora));
                    tentativas.put(chave, Integer.valueOf(1));
                } else {
                    // Se ainda está dentro do intervalo, verifica a qtd
                    // de tentativas já realizadas
                    Integer qtd = tentativas.get(chave);
                    int qtdtentativas = (qtd != null) ? qtd.intValue() : 0;
                    qtdtentativas++;

                    if (qtdtentativas >= limiteTentativas) {
                        // Se o limite foi alcançado, então bloqueia o usuário
                        UsuarioDelegate usuDelegate = new UsuarioDelegate();
                        UsuarioTransferObject usuTransfer = usuDelegate.findUsuario(chave, responsavel);
                        usuTransfer.setStuCodigo(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE);
                        usuTransfer.setUsuTipoBloq(ApplicationResourcesHelper.getMessage("mensagem.usuario.bloqueado.automaticamente", responsavel));


                        // Grava ocorrência de bloqueio automático do usuário
                        OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                        ocorrencia.setUsuCodigo(chave);
                        ocorrencia.setTocCodigo(CodedValues.TOC_BLOQUEIO_AUTOMATICO_USUARIO);
                        ocorrencia.setOusUsuCodigo((responsavel.getUsuCodigo() != null) ? responsavel.getUsuCodigo():AcessoSistema.getAcessoUsuarioSistema().getUsuCodigo());
                        ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.bloqueio.automatico.usuario.n.maximo.tentativas.login", responsavel));
                        ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

                        usuDelegate.updateUsuario(usuTransfer, ocorrencia, responsavel);

                        resetTetantivasLogin(chave);
                        ZetraException ze = null;

                        if (messageKey == null){
                            ze = new ZetraException("mensagem.informacao.aviso.numero.maximo.tentativas.login.alcancado", responsavel);
                        } else {
                            ze = new ZetraException("mensagem.informacao.aviso.numero.maximo.tentativas.login.alcancado.arg0", responsavel, ApplicationResourcesHelper.getMessage(messageKey, responsavel));
                        }

                        throw ze;
                    } else {
                        // Se o limite ainda não foi alcançado,
                        // atualiza o contador e permite a consulta
                        tentativas.put(chave, Integer.valueOf(qtdtentativas));

                        

                        if ((qtdtentativas) == (limiteTentativas - 1)) {
                            ZetraException ze = null;

                            if (messageKey == null){
                                ze = new ZetraException("mensagem.informacao.aviso.ultima.tentativa", responsavel);
                            } else {
                                ze = new ZetraException("mensagem.informacao.aviso.ultima.tentativa.arg0", responsavel, ApplicationResourcesHelper.getMessage(messageKey, responsavel));
                            }

                            throw ze;

                        } else if (qtdtentativas < (limiteTentativas - 1)){
                            ZetraException ze = null;
                            Integer restantes = limiteTentativas - qtdtentativas;

                            if (TextHelper.isNull(messageKey)){
                                ze = new ZetraException("mensagem.informacao.aviso.quantidade.tentativa", responsavel, restantes.toString());
                            } else {
                                ze = new ZetraException("mensagem.informacao.aviso.quantidade.tentativa.arg0", responsavel, ApplicationResourcesHelper.getMessage(messageKey, responsavel), restantes.toString());
                            }

                            throw ze;
                        }
                    }
                }
            }
        }
    }

    /**
     * Retorna a diferença em horas de duas datas.
     * @param a
     * @param b
     * @return
     */
    private int hourDiff(Date a, Date b) {
        int tempDifference = 0;
        int difference = 0;
        Calendar earlier = Calendar.getInstance();
        Calendar later = Calendar.getInstance();

        if (a.compareTo(b) < 0) {
            earlier.setTime(a);
            later.setTime(b);
        } else {
            earlier.setTime(b);
            later.setTime(a);
        }

        while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR)) {
            tempDifference = 24 * 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
            difference += tempDifference;
            earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
        }

        if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR)) {
            tempDifference = 24 * (later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR));
            difference += tempDifference;
        }

        if (earlier.get(Calendar.HOUR_OF_DAY) != later.get(Calendar.HOUR_OF_DAY)) {
            tempDifference = later.get(Calendar.HOUR_OF_DAY) - earlier.get(Calendar.HOUR_OF_DAY);
            difference += tempDifference;
        }

        return difference;
    }

    /**
     * Retira da memória a referência a tentativas de login que já estão fora do
     * intervalo de tempo.
     */
    private void limpa() {
        Map<String, Long> result = new HashMap<>();

        Date dataAtual = DateHelper.getSystemDatetime();

        String chave = null;
        Long tempo = null;
        Date dataUltConsulta = null;

        Iterator<String> it = hora.keySet().iterator();
        while (it.hasNext()) {
            chave = it.next();
            tempo = hora.get(chave);
            dataUltConsulta = new Date(tempo.longValue());

            if (hourDiff(dataAtual, dataUltConsulta) < intervalo) {
                // Se ainda está no intervalo, mantém a chave
                result.put(chave, tempo);
            } else {
                // Se já não está mais no intervalo, então retira a chave
                tentativas.remove(chave);
            }
        }

        // Limpa o map das horas
        hora.clear();
        hora.putAll(result);
        result.clear();
    }

    /**
     * limpa o cache para um determinado usuário
     * @param usuCodigo
     */
    public void resetTetantivasLogin(String usuCodigo) {
        hora.remove(usuCodigo);
        tentativas.remove(usuCodigo);
    }
}
