-- DESENV-19721
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('920', 'Exibir opção de envio de e-mail para SER comunicando as consignações que ficaram pendentes devido ao saldo insuficiente da verba rescisória', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('920', '1', 'N');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('225', 'Pagamento parcial ou não realizado devido ao saldo insuficiente da verba rescisória');

INSERT INTO tb_modelo_email (MEM_CODIGO,MEM_TITULO,MEM_TEXTO)
VALUES ('enviarEmailSerVerbaRescisoria', '<@nome_sistema> - <@nome_consignante>: Informação de retenção de verba rescisória para pagamento de saldo devedor ', 'Caro(a) <@nome_servidor>,<br><br>Em função da rescisão do seu contrato de trabalho e conforme Lei 10.820/2003, que dispõe sobre a autorização de descontos de prestações em folha de pagamento, informamos que, para o pagamento de parcelas relativas ao(s) contrato(s) de consignação em seu nome, houve a retenção de valores em seu TRCT \u2013 Termo de Rescisão de Contrato de Trabalho nos limites previstos em lei. Porém, restou saldo devedor, conforme descrição abaixo:<br><br><@detalhe_consignacao_label_html_noescape><br><br>Salientamos que a responsabilidade pela quitação do saldo devedor é exclusiva do colaborador. Desta forma, entre em contato com a(s) consignatária(s) para melhores informações e decisões quanto à forma para quitação/renegociação do saldo devedor. Contamos com sua compreensão e efetivação do contato com a(s) consignatária(s).');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16744', '1', '485', '/v3/listarColaboradoresVerbaRescisoria', 'acao', 'enviaEmailSer', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16745', '3', '485', '/v3/listarColaboradoresVerbaRescisoria', 'acao', 'enviaEmailSer', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16746', '7', '485', '/v3/listarColaboradoresVerbaRescisoria', 'acao', 'enviaEmailSer', 1, 'S', 'S', NULL, 'N', '2');

