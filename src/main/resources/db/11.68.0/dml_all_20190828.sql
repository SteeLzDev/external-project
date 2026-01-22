-- DESENV-10127

-- ALTERA IMAGEM DOS MENUS
UPDATE tb_menu SET MNU_IMAGEM = 'i-favorito' WHERE MNU_CODIGO = '0';
UPDATE tb_menu SET MNU_IMAGEM = 'i-operacional' WHERE MNU_CODIGO = '1';
UPDATE tb_menu SET MNU_IMAGEM = 'i-relatorio' WHERE MNU_CODIGO = '2';
UPDATE tb_menu SET MNU_IMAGEM = 'i-manutencao' WHERE MNU_CODIGO = '3';
UPDATE tb_menu SET MNU_IMAGEM = 'i-sistema' WHERE MNU_CODIGO = '4';
UPDATE tb_menu SET MNU_IMAGEM = 'i-operacional' WHERE MNU_CODIGO = '5';

INSERT INTO tb_menu (MNU_CODIGO, MNU_DESCRICAO, MNU_ATIVO, MNU_SEQUENCIA, MNU_IMAGEM) VALUES ('6', 'Ajuda', '1', '6', 'i-ajuda');

-- INSERE ITEM MENU PARA SEREM UTILIZADOS EM ACESSO RECURSO QUE PODE SER UTILIZADO NO DASHBOARD
INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_SEQUENCIA, ITM_SEPARADOR, ITM_IMAGEM, TEX_CHAVE) VALUES ('223', '6', null, 'Ajuda', 1, 'N', 'i-ajuda', NULL);
INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_SEQUENCIA, ITM_SEPARADOR, ITM_IMAGEM, TEX_CHAVE) VALUES ('224', '1', null, 'Visualizar ocorrências', 79, 'N', 'i-relatorio', NULL);
INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_SEQUENCIA, ITM_SEPARADOR, ITM_IMAGEM, TEX_CHAVE) VALUES ('225', '1', null, 'Acessar portal euConsigoMais', 80, 'N', 'i-simular', 'mensagem.tooltip.dashboard.612');

-- ATUALIZA ACESSO RECURSO COM ITEM MENU CRIADO
UPDATE tb_acesso_recurso SET ITM_CODIGO = '225' WHERE ACR_RECURSO = '/v3/autenticarEuConsigoMais' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'gerarToken';
UPDATE tb_acesso_recurso SET ITM_CODIGO = '223' WHERE ACR_RECURSO = '/v3/visualizarAjudaContexto';
UPDATE tb_acesso_recurso SET ITM_CODIGO = '224' WHERE ACR_RECURSO = '/v3/listarOcorrenciaServidor' AND PAP_CODIGO = '6';

-- ATUALIZA ITEM MENU DO ACESSO RECURSO COM IMAGEM E TEXTO ESPECIFICO
UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-relatorio' 
WHERE ACR_RECURSO = '/v3/listarContrachequeServidor' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-consignacao' 
WHERE ACR_RECURSO = '/servidor/extrato_divida.jsp' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-consultar' 
WHERE ACR_RECURSO = '/v3/consultarConsignacao' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'pesquisarConsignacao' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-integrar', TEX_CHAVE = 'mensagem.tooltip.dashboard.428' 
WHERE ACR_RECURSO = '/v3/integrarFolha' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-upload' 
WHERE ACR_RECURSO = '/v3/uploadArquivo' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-download', TEX_CHAVE = 'mensagem.tooltip.dashboard.36' 
WHERE ACR_RECURSO = '/v3/listarArquivosDownloadIntegracao' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-consultar'  
WHERE ACR_RECURSO = '/v3/consultarConsignacao' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-consultar'  
WHERE ACR_RECURSO = '/v3/consultarMargem' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-margem'  
WHERE ACR_RECURSO = '/v3/reservarMargem' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-margem'  
WHERE ACR_RECURSO = '/v3/incluirConsignacao' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-colaboradores', TEX_CHAVE = 'mensagem.tooltip.dashboard.119' 
WHERE ACR_RECURSO = '/v3/consultarServidor' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-colaboradores', TEX_CHAVE = 'mensagem.tooltip.dashboard.398' 
WHERE ACR_RECURSO = '/v3/pesquisarServidor' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-servidor' 
WHERE ACR_RECURSO = '/v3/validarServidor' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-servidor' 
WHERE ACR_RECURSO = '/v3/pesquisarServidor' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-margem' 
WHERE ACR_RECURSO = '/v3/confirmarConsignacao' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-relatorio-desconto', TEX_CHAVE = 'mensagem.tooltip.dashboard.87' 
WHERE ACR_RECURSO = '/v3/listarRelatorio' AND ACR_PARAMETRO = 'tipo' AND ACR_OPERACAO = 'integracao' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-relatorio' 
WHERE ACR_RECURSO = '/v3/listarRelatorio' AND ACR_PARAMETRO = 'tipo' AND ACR_OPERACAO = 'mov_mes_csa' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-consultar' 
WHERE ACR_RECURSO = '/v3/editarConsignante' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-servidor' 
WHERE ACR_RECURSO = '/v3/importarServidoresBloqueados' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-servidor' 
WHERE ACR_RECURSO = '/v3/importarServidoresBloqueadosDesligados' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-margem' 
WHERE ACR_RECURSO = '/v3/liquidarConsignacao' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;

UPDATE 
tb_item_menu itm 
INNER JOIN tb_acesso_recurso acr ON (itm.ITM_CODIGO = acr.ITM_CODIGO)
SET ITM_IMAGEM = 'i-upload' 
WHERE ACR_RECURSO = '/v3/uploadArquivoGenerico' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'iniciar' 
AND acr.ITM_CODIGO IS NOT NULL;
