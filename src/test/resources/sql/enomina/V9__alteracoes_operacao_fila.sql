/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     31/01/2025 13:59:01                          */
/*==============================================================*/


alter table tb_arquivo_ope_nao_confirmadas drop foreign key FK_R_793;

alter table tb_operacao_nao_confirmada drop foreign key FK_R_791;

alter table tb_operacao_nao_confirmada drop foreign key FK_R_792;

drop table if exists tmp_tb_operacao_nao_confirmada;

rename table tb_operacao_nao_confirmada to tmp_tb_operacao_nao_confirmada;

/*==============================================================*/
/* Table: tb_operacao_nao_confirmada                            */
/*==============================================================*/
create table tb_operacao_nao_confirmada
(
   ONC_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   RSE_CODIGO           varchar(32),
   ACR_CODIGO           varchar(32) not null,
   ONC_IP_ACESSO        varchar(45) not null,
   ONC_DATA             datetime not null,
   ONC_DETALHE          longtext not null,
   ONC_PARAMETROS       text not null,
   primary key (ONC_CODIGO)
) ENGINE = InnoDB;

insert into tb_operacao_nao_confirmada (ONC_CODIGO, USU_CODIGO, ACR_CODIGO, ONC_IP_ACESSO, ONC_DATA, ONC_DETALHE, ONC_PARAMETROS)
select ONC_CODIGO, USU_CODIGO, ACR_CODIGO, ONC_IP_ACESSO, ONC_DATA, ONC_DETALHE, ONC_PARAMETROS
from tmp_tb_operacao_nao_confirmada;

drop table if exists tmp_tb_operacao_nao_confirmada;

alter table tb_arquivo_ope_nao_confirmadas add constraint FK_R_793 foreign key (ONC_CODIGO)
      references tb_operacao_nao_confirmada (ONC_CODIGO) on delete restrict on update restrict;

alter table tb_operacao_nao_confirmada add constraint FK_R_791 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_operacao_nao_confirmada add constraint FK_R_792 foreign key (ACR_CODIGO)
      references tb_acesso_recurso (ACR_CODIGO) on delete restrict on update restrict;

alter table tb_operacao_nao_confirmada add constraint FK_R_977 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;
