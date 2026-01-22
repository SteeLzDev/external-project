-- DESENV-21063
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO IN ('21','85') AND ACR_RECURSO = '/v3/manterConsignataria' AND ACR_OPERACAO = 'salvarServico';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '126' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '155' AND ACR_OPERACAO = 'cancelar_compra';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '164' AND ACR_OPERACAO = 'modificar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '165' AND ACR_OPERACAO = 'excluir';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '193' AND ACR_OPERACAO IN ('excluir','salvar');
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '204' AND ACR_OPERACAO = 'transferir';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '227' AND ACR_OPERACAO = 'salvarItemMenu';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '240' AND ACR_OPERACAO = 'ativar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '241' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '248' AND ACR_RECURSO = '/v3/auditarOperacoes' AND ACR_OPERACAO = 'auditar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '254' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '280' AND ACR_OPERACAO = 'atualizarContrato';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '287' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '299' AND ACR_OPERACAO IN ('excluir','salvar');
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '315' AND ACR_OPERACAO = 'enviar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '419' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '436' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '441' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '444' AND ACR_OPERACAO IN ('excluir','salvar');
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '451' AND ACR_OPERACAO = 'excluir';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'N' WHERE FUN_CODIGO = '452' AND ACR_FIM_FLUXO = 'S';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '452' AND ACR_OPERACAO IN ('salvar', 'excluir', 'ativarTabela', 'excluirTabelaIniciada');
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '468' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '474' AND ACR_OPERACAO = 'incluirReserva';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '475' AND ACR_OPERACAO IN ('excluir','salvar');
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '495' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '510' AND ACR_OPERACAO = 'salvarCnvVincServidor';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '515' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '521' AND ACR_OPERACAO = 'confirmarNotificacao';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '523' AND ACR_RECURSO = '/v3/manterComposicaoMargemServidor' AND ACR_OPERACAO IN ('salvar', 'excluir');
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '525' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '529' AND ACR_OPERACAO = 'salvar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '530' AND ACR_OPERACAO = 'salvarLimiteMargemCsaOrg';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '538' AND ACR_OPERACAO = 'editar';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE FUN_CODIGO = '540' AND ACR_OPERACAO = 'salvarValorFixoPosto';

UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE ACR_RECURSO = '/v3/executarRelatorio' AND ACR_FIM_FLUXO = 'N';
UPDATE tb_acesso_recurso SET ACR_FIM_FLUXO = 'S' WHERE ACR_RECURSO = '/v3/agendarRelatorio' AND ACR_FIM_FLUXO = 'N';

DELETE FROM tb_ajuda WHERE ACR_CODIGO IN ('14939','14940','14942');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN ('14939','14940','14942');
DELETE FROM tb_acesso_recurso WHERE ACR_CODIGO IN ('14939','14940','14942');

DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'rotulo.dashboard.credenciamento.nenhum.registro';

