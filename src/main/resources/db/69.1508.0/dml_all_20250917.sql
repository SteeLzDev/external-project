UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Reconheço'
WHERE TEX_CHAVE = 'rotulo.tabela.autorizar.consignacao.aceito';

UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Não Reconheço'
WHERE TEX_CHAVE = 'rotulo.tabela.autorizar.consignacao.nao.aceito';

UPDATE tb_texto_sistema
SET TEX_TEXTO = 'Gentileza confirmar a leitura do termo de responsabilidade.'
WHERE TEX_CHAVE = 'mensagem.info.autorizar.confirmar.autorizacao';

DELETE FROM tb_texto_sistema
WHERE TEX_CHAVE IN ('rotulo.tabela.autorizar.estou.ciente', 'mensagem.informe.um.contrato.autorizacao.ade.por.verba');
