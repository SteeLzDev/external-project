-- DESENV-11169
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/transferirConsignacaoGeral', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '204' AND PAP_CODIGO = '1' AND ACR_RECURSO = '/admin/transf_contratos.jsp'; 

UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/transferirConsignacaoGeral', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE FUN_CODIGO = '204' AND PAP_CODIGO = '7' AND ACR_RECURSO = '/admin/transf_contratos.jsp';

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES('15213', '1', '204', '/v3/transferirConsignacaoGeral', 'acao', 'transferir', 1, 'S', 'S', 'N', '2'); 
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO,  ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES('15214', '7', '204', '/v3/transferirConsignacaoGeral', 'acao', 'transferir', 1, 'S', 'S', 'N', '2'); 
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES('15215', '1', '204', '/v3/transferirConsignacaoGeral', 'acao', 'listar', 1, 'S', 'S', 'N', '2'); 
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES('15216', '7', '204', '/v3/transferirConsignacaoGeral', 'acao', 'listar', 1, 'S', 'S', 'N', '2');