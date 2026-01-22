-- DESENV-19673
-- 1) ATUALIZAR O GRUPO DE PARAMETROS NOS NOVOS PARAMETROS: 

update tb_tipo_param_sist_consignante set  gps_codigo = '1' where tpc_codigo in ('888','914');
update tb_tipo_param_sist_consignante set  gps_codigo = '2' where tpc_codigo in ('910');
update tb_tipo_param_sist_consignante set  gps_codigo = '3' where tpc_codigo in ('909','911');


-- 2) CONCEDER A  PERMISSAO DE VISUALIZAR E ALTERAR PARA USUÁRIOS SUP: 

update tb_tipo_param_sist_consignante set tpc_sup_consulta = 'S', tpc_sup_altera ='S' where tpc_codigo in ('888','909','910','911','914');


-- 3) VINCULAR OS PARAMETROS AOS PERFIS: 


insert into tb_perfil_param_sist_cse 
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-IMP_AVANCADO')
where tpc.tpc_codigo in ('888','909','910','911','914')
and not exists (
  select 1 from tb_perfil_param_sist_cse pcc
  where tpc.tpc_codigo = pcc.tpc_codigo
    and per.per_codigo = pcc.per_codigo
);


insert into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-MANUTENCAO')
where tpc.tpc_codigo in ('909','910','911')
and not exists (
  select 1 from tb_perfil_param_sist_cse pcc
  where tpc.tpc_codigo = pcc.tpc_codigo
    and per.per_codigo = pcc.per_codigo
);

insert into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-PRODUCAO')
where tpc.tpc_codigo in ('910')
and not exists (
  select 1 from tb_perfil_param_sist_cse pcc
  where tpc.tpc_codigo = pcc.tpc_codigo
    and per.per_codigo = pcc.per_codigo
);

insert into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-SEGURANCA_BASICO')
where tpc.tpc_codigo in ('888','914')
and not exists (
 select 1 from tb_perfil_param_sist_cse pcc
 where tpc.tpc_codigo = pcc.tpc_codigo
 and per.per_codigo = pcc.per_codigo
);

-- 4) CONFIGURAÇÃO DA TB_FUNCAO PARA SEGUNDA SENHA E EXIGE MOTIVO OPERACAO:

update tb_funcao
set FUN_EXIGE_TMO = 'S',
FUN_EXIGE_SEGUNDA_SENHA_CSE = 'P',
FUN_EXIGE_SEGUNDA_SENHA_SUP = 'S',
FUN_EXIGE_SEGUNDA_SENHA_ORG = 'S',
FUN_EXIGE_SEGUNDA_SENHA_CSA = 'S',
FUN_EXIGE_SEGUNDA_SENHA_COR = 'S'
where FUN_CODIGO IN ('542','543');

