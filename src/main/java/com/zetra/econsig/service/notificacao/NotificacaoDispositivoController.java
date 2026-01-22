package com.zetra.econsig.service.notificacao;


import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.NotificacaoDispositivoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.TipoNotificacaoEnum;

public interface NotificacaoDispositivoController {

    public List<TransferObject> lstNotificacoes(String funCodigo, String tnoCodigo, boolean ndiAtivo, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException;

    public String createNotificacaoDispositivo(String usuCodigoOperador, String usuCodigoDestinatario, String funCodigo, String ndiTexto, Date ndiData, Date ndiDataEnvio, Short ndiStatus, String tnoCodigo, AcessoSistema responsavel ) throws NotificacaoDispositivoControllerException;

    public void enviarNotificacao(String ndiCodigo, String deviceToken, String ndiTexto, String mensagemNdi, String tituloNdi, String usuCodigoDestinatario, String tnoCodigo, boolean ndiAtivo, String collapseId, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException;

    public boolean enviarNotificacao(String serCodigo, String titulo, String texto, TipoNotificacaoEnum tipoNotificacao, String funCodigo, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException;

    public void registrarEnvioNotificacao(String ndiCodigo, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException;

    public void registrarEnvioNotificacoesInativas(String ndiCodigo, String funCodigo, String usuCodigoDestinatario, String tnoCodigo, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException;

    public void enviarNotificacaoMultipla(String tituloPush, String textoPush, String strJsonBody, AcessoSistema responsavel) throws NotificacaoDispositivoControllerException;
}
