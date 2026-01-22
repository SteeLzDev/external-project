-- DESENV-16493
-- 1) ATUALIZAR O GRUPO DE PARAMETROS NOS NOVOS PARAMETROS: 

update tb_tipo_param_sist_consignante set  gps_codigo = '2' where tpc_codigo in ('853');

update tb_tipo_param_sist_consignante set  gps_codigo = '3' where tpc_codigo in ('844','845');


-- 2) CONCEDER A  PERMISSAO DE VISUALIZAR E ALTERAR PARA USUÁRIOS SUP: 

update tb_tipo_param_sist_consignante set tpc_sup_consulta = 'S', tpc_sup_altera ='S' where tpc_codigo in ('844','845','853');


-- 3) VINCULAR OS PARAMETROS AOS PERFIS: 


insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo) 
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-IMP_AVANCADO')
where tpc.tpc_codigo in ('844','845');

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo) 
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-MANUTENCAO')
where tpc.tpc_codigo in ('844','845'); 

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo) 
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-PRODUCAO')
where tpc.tpc_codigo in ('844','845','853'); 

-- 4) CONFIGURAÇÃO DA TB_FUNCAO PARA SEGUNDA SENHA E EXIGE MOTIVO OPERACAO:


update tb_funcao
set FUN_EXIGE_TMO = 'N',
FUN_EXIGE_SEGUNDA_SENHA_CSE = 'P',
FUN_EXIGE_SEGUNDA_SENHA_SUP = 'S',
FUN_EXIGE_SEGUNDA_SENHA_ORG = 'P',
FUN_EXIGE_SEGUNDA_SENHA_CSA = 'P',
FUN_EXIGE_SEGUNDA_SENHA_COR = 'P'
where FUN_CODIGO = '515';

update tb_funcao
set FUN_EXIGE_TMO = 'N',
FUN_EXIGE_SEGUNDA_SENHA_CSE = 'P',
FUN_EXIGE_SEGUNDA_SENHA_SUP = 'S',
FUN_EXIGE_SEGUNDA_SENHA_ORG = 'P',
FUN_EXIGE_SEGUNDA_SENHA_CSA = 'P',
FUN_EXIGE_SEGUNDA_SENHA_COR = 'P'
where FUN_CODIGO = '517';
