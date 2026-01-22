-- DESENV-18145
INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('210', 'Termo Aditivo preenchido');
INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('211', 'Termo Aditivo Assinado pela Consignatária');
INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) VALUES ('212', 'Credenciamento Finalizado');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailTermoPreenchidoCse', '<@nome_sistema> - <@nome_consignante>: Termo Preenchido Credenciamento ', 'Prezada <@csa_nome>,<br> o termo aditivo foi preenchido, gentileza assina-lo, acessar o sistema e fazer o upload dele.');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailTermoAssCsa', 'Termo Assinado Credenciamento: <@csa_nome> ', 'Prezados,<br> o termo aditivo foi assinado pela consignatária <@csa_nome>, gentileza acessar o sistema para verificar.');

INSERT INTO tb_modelo_email (MEM_CODIGO, MEM_TITULO, MEM_TEXTO) VALUES ('enviarEmailTermoAssCse', 'Termo Assinado Credenciamento CSE ', 'Prezado(a) <@usu_nome>,<br> o termo aditivo foi assinado pela consignante, gentileza acessar o sistema para verificar.');

INSERT INTO tb_tipo_arquivo (TAR_CODIGO, TAR_DESCRICAO, TAR_QTD_DIAS_LIMPEZA, TAR_UPLOAD_SUP, TAR_UPLOAD_CSE, TAR_UPLOAD_ORG, TAR_UPLOAD_CSA, TAR_UPLOAD_COR, TAR_NOTIFICACAO_UPLOAD)
VALUES('66', 'Arquivo Credenciamento Termo Aditivo Assinado CSE', 0, 'S', 'S', 'S', 'S', 'S', 'N');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16653', '1', '528', '/v3/visualizarDashboardCredenciamento', 'acao', 'preencherTermoCredenciamentoCsa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16654', '2', '528', '/v3/visualizarDashboardCredenciamento', 'acao', 'preencherTermoCredenciamentoCsa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16655', '7', '528', '/v3/visualizarDashboardCredenciamento', 'acao', 'preencherTermoCredenciamentoCsa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16656', '1', '200', '/v3/downloadArquivo', 'tipo', 'anexo_credenciamento_termo', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16657', '2', '200', '/v3/downloadArquivo', 'tipo', 'anexo_credenciamento_termo', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16658', '7', '200', '/v3/downloadArquivo', 'tipo', 'anexo_credenciamento_termo', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16659', '2', '528', '/v3/visualizarDashboardCredenciamento', 'acao', 'assinarTermoCredenciamentoCsa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16660', '1', '528', '/v3/visualizarDashboardCredenciamento', 'acao', 'assinarTermoCseCredenciamentoCsa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16661', '7', '528', '/v3/visualizarDashboardCredenciamento', 'acao', 'assinarTermoCseCredenciamentoCsa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('535', '1', 'Aprovar Termo Aditivo de Entidade Consignatária', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

