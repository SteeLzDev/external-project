-- DESENV-19123
DELETE FROM tb_ajuda WHERE ACR_CODIGO IN (
  SELECT ACR_CODIGO 
  FROM tb_acesso_recurso 
  WHERE ACR_RECURSO IN (
    '/topo/topo_consignante.jsp',
    '/topo/topo_consignataria.jsp',
    '/js/menu/hierMenuDef.jsp',
    '/geral/msg404.jsp',
    '/geral/msg500.jsp',
    '/geral/mensagem.jsp',
    '/geral/ctrl_paginacao.jsp',
    '/margem/include_campo_matricula.jsp',
    '/usuario/ip_acesso_usuario.jsp',
    '/login/index.jsp',
    '/index.jsp'
  )
);
 
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (
  SELECT ACR_CODIGO 
  FROM tb_acesso_recurso 
  WHERE ACR_RECURSO IN (
    '/topo/topo_consignante.jsp',
    '/topo/topo_consignataria.jsp',
    '/js/menu/hierMenuDef.jsp',
    '/geral/msg404.jsp',
    '/geral/msg500.jsp',
    '/geral/mensagem.jsp',
    '/geral/ctrl_paginacao.jsp',
    '/margem/include_campo_matricula.jsp',
    '/usuario/ip_acesso_usuario.jsp',
    '/login/index.jsp',
    '/index.jsp'
  )
);
 
DELETE FROM tb_acesso_recurso 
WHERE ACR_RECURSO IN (
    '/topo/topo_consignante.jsp',
    '/topo/topo_consignataria.jsp',
    '/js/menu/hierMenuDef.jsp',
    '/geral/msg404.jsp',
    '/geral/msg500.jsp',
    '/geral/mensagem.jsp',
    '/geral/ctrl_paginacao.jsp',
    '/margem/include_campo_matricula.jsp',
    '/usuario/ip_acesso_usuario.jsp',
    '/login/index.jsp',
    '/index.jsp'
);
 
-- /v3/consultarServidor?acao=visualizarTermoUso
DELETE FROM tb_ajuda WHERE ACR_CODIGO IN ('14783', '14784', '14785');
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN ('14783', '14784', '14785');
DELETE FROM tb_acesso_recurso WHERE ACR_CODIGO IN ('14783', '14784', '14785');

DELETE FROM tb_texto_sistema WHERE TEX_CHAVE IN (
'rotulo.estabelecimento.imagem',
'rotulo.matricula.imagem',
'rotulo.menu.seta.direita.imagem',
'rotulo.menu.seta.esquerda.imagem',
'rotulo.menu.seta.sobre.direita.imagem',
'rotulo.menu.seta.sobre.esquerda.imagem',
'rotulo.menu.seta.cima.imagem',
'rotulo.menu.seta.baixo.imagem',
'rotulo.topo.imagem',
'rotulo.topoquebra.imagem'
);

