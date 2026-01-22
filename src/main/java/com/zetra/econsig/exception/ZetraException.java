package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: ZetraException</p>
 * <p>Description: Classe pai para todas as checked exceptions</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ZetraException extends Exception {

    // Constantes para os tipos de mensagem
    public static final String MENSAGEM_PADRAO = "";
    public static final String MENSAGEM_LOTE = ".lote";
    public static final String MENSAGEM_LOTE_FEBRABAN = ".febraban";
    public static final String MENSAGEM_PROCESSAMENTO_XML = ".xml";
    public static final String MENSAGEM_ACESSO_CENTRAL = ".acesso";

    private String message;
    private String messageKey;
    private String[] messageArgs;
    private AcessoSistema responsavel;

    protected ZetraException(String message) {
        super(message);
        this.message = message;
    }

    public ZetraException(Throwable cause) {
        super(cause.getMessage(), cause);
        if (cause != null && cause instanceof ZetraException) {
            message     = ((ZetraException) cause).message;
            messageKey  = ((ZetraException) cause).messageKey;
            messageArgs = ((ZetraException) cause).messageArgs;
            responsavel = ((ZetraException) cause).responsavel;
        }
    }

    protected ZetraException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    @Deprecated
    public ZetraException(String message, String key) {
        super(message);
        setMessageKey(key);
        this.message = message;
    }

    public ZetraException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super();
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
        this.responsavel = responsavel;
    }

    public ZetraException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(cause);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
        this.responsavel = responsavel;
    }

    @Deprecated
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String[] getMessageArgs() {
        return messageArgs;
    }

    /**
     * Sobropõe o método padrão para pegar a mensagem do properties se esta
     * ZetraException tiver sido criada com uma properties key.
     */
    @Override
    public String getMessage() {
        return getResourcesMessage(MENSAGEM_PADRAO);
    }

    /**
     * Permite que uma classe de exceção defina se a mensagem pode ser sobreposta,
     * útil para casos onde a mensagem não vem do application-resources
     * @param message
     */
    protected void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retorna a mensagem configurada no arquivo de propriedades.
     * O tipo indica o sufixo que será aplicado à chave informada
     * para obter a mensagem específica para cada caso. As constantes
     * MENSAGEM_PADRAO, MENSAGEM_LOTE, MENSAGEM_XML e MENSAGEM_ACESSO_CENTRAL
     * são as opções disponíveis.
     * @param type String
     * @return String
     */
    public String getResourcesMessage(String type) {
        if (!TextHelper.isNull(messageKey) && (!TextHelper.isNull(type) || TextHelper.isNull(message))) {
            String resourceMessage = ApplicationResourcesHelper.getMessage(messageKey + type, responsavel, messageArgs);
            return (resourceMessage != null ? resourceMessage : message);
        } else {
            return message;
        }
    }

    public static ZetraException byMessage(String message) {
        return new ZetraException(message);
    }

    public static ZetraException byMessage(String message, Throwable cause) {
        return new ZetraException(message, cause);
    }
}
