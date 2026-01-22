/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/12/2022 16:13:09                          */
/*==============================================================*/

SET @orgCodigo := (select org_codigo from tb_orgao where org_ativo = 1 limit 1);

/*==============================================================*/
/* Table: tb_historico_exportacao                               */
/*==============================================================*/
create table tb_historico_exportacao
(
   HIE_CODIGO           varchar(32) not null,
   ORG_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   HIE_PERIODO          date not null,
   HIE_DATA_INI         datetime not null,
   HIE_DATA_FIM         datetime not null,
   HIE_DATA             datetime not null,
   primary key (HIE_CODIGO),
   key idx_periodo_org (HIE_PERIODO, ORG_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_historico_exportacao add constraint FK_R_899 foreign key (ORG_CODIGO)
      references tb_orgao (ORG_CODIGO) on delete restrict on update restrict;

alter table tb_historico_exportacao add constraint FK_R_900 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

insert into tb_historico_exportacao (HIE_CODIGO, ORG_CODIGO, USU_CODIGO, HIE_PERIODO, HIE_DATA_INI, HIE_DATA_FIM, HIE_DATA)
select HI_CODIGO, coalesce(ORG_CODIGO, @orgCodigo), coalesce(USU_CODIGO, '1'), coalesce(HI_PERIODO, date_format(HI_DATA_FIM, '%Y-%m-01')), HI_DATA_INI, HI_DATA_FIM, HI_DATA
from tb_historico_integracao
;

drop table if exists tb_historico_integracao;

create view tb_historico_integracao as 
select HIE_CODIGO as HI_CODIGO, ORG_CODIGO, USU_CODIGO, HIE_PERIODO as HI_PERIODO, cast(HIE_DATA_INI as date) as HI_DATA_INI, cast(HIE_DATA_FIM as date) as HI_DATA_FIM, cast(HIE_DATA as date) as HI_DATA, 'E' as HI_TIPO
from tb_historico_exportacao
;
