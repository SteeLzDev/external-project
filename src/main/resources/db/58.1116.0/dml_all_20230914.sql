-- DESENV-20326
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE IN ('mensagem.situacaoConsignacaoInvalida', 'mensagem.situacaoConsignacaoInvalida.febraban', 'mensagem.situacaoConsignacaoInvalida.lote', 'mensagem.situacaoConsignacaoInvalida.xml');

DELETE FROM tb_texto_sistema WHERE TEX_CHAVE IN ('mensagem.erro.liquidar.consignacao.status.invalido', 'mensagem.erro.liquidar.consignacao.status.invalido.febraban', 'mensagem.erro.liquidar.consignacao.status.invalido.lote');

