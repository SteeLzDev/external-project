-- DESENV-22608
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR) 
VALUES ('561', '1', 'Enviar email na solicitação/simulacao de consignacao', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('17050', '6', '561', '/v3/simularConsignacao', 'acao', 'enviaEmailConsignacao', 1, 'N', 'S', NULL, 'N', '2');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarEmailSimulacaoConsignacao', '<@cse_nome>: Simulação de Consignação', 'Prezado (a) <@usu_nome>, <br>Segue em anexo o resultado da sua simulação de consignação realizado em <@data_hora_atual>. <br>Abaixo seguem os contatos das consignatárias participantes:<br><br><@infor_csas><@csa_contato><br><@/infor_csas><br>  Atenciosamente <@logoSistema>');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO)
VALUES ('45', 'E-mail de Notificação de Simulacao de consignacao', 'I');