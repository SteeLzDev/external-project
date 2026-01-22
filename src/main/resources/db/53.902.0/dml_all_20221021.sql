-- DESENV-18779
UPDATE tb_tipo_param_sist_consignante SET TPC_DESCRICAO = 'Envia email ao servidor no (des)bloqueio por serviço, natureza de serviço, verba ou consignatária' WHERE TPC_CODIGO = '542';

INSERT INTO tb_tipo_entidade (TEN_CODIGO, TEN_DESCRICAO, TEN_AUDITORIA, TEN_CAMPO_ENT_00, TEN_CAMPO_ENT_01, TEN_CAMPO_ENT_02)
VALUES ('128', 'Parâmetro Consignatária Registro Servidor', 0, 'TPA_CODIGO', 'RSE_CODIGO', 'CSA_CODIGO');

INSERT INTO tb_tipo_notificacao (TNO_CODIGO, TNO_DESCRICAO, TNO_ENVIO)
VALUES ('34', 'E-mail de bloqueio/desbloqueio de servidor pela consignatária', 'I');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO)
VALUES ('enviarEmailBloqDesbloqSerCsa', '<@nome_sistema> - <@nome_consignante>: Bloqueio/Desbloqueio de servidor por consignatária', 'Prezado <@ser_nome>,<br><@bloqueado><br><@desbloqueado><br><@alterado><br>');

INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('76', 'Quantidade de contratos por consignatária', 'INT', 'N', 'N', 'N');

INSERT INTO tb_tipo_ocorrencia VALUES ('216', 'Bloqueio de consignatário para servidor');

INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('538', '8', 'Bloquear consignatárias de servidor', 'S', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('1', '538');
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('3', '538');
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('7', '538');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16691', '1', '538', '/v3/listarConsignatariaServidor', 'acao', 'iniciar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16692', '3', '538', '/v3/listarConsignatariaServidor', 'acao', 'iniciar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16693', '7', '538', '/v3/listarConsignatariaServidor', 'acao', 'iniciar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16694', '1', '538', '/v3/listarConsignatariaServidor', 'acao', 'editar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16695', '3', '538', '/v3/listarConsignatariaServidor', 'acao', 'editar', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16696', '7', '538', '/v3/listarConsignatariaServidor', 'acao', 'editar', 1, 'S', 'S', NULL, 'N', '2');

