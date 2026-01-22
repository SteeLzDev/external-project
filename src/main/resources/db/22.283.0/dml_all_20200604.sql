-- DESENV-11180
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/downloadLayoutXml', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'downloadLayoutXml' WHERE ACR_CODIGO = '13697';

-- Para atualizar um texto no ApplicationResources.properties que estava errado:
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE = 'mensagem.erro.xmllayout.definicao.latout.arquivo';
