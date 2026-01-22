/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     05/03/2021 08:11:37                          */
/*==============================================================*/


drop table if exists tmp_tb_anexo_beneficiario;

rename table tb_anexo_beneficiario to tmp_tb_anexo_beneficiario;

/*==============================================================*/
/* Table: tb_anexo_beneficiario                                 */
/*==============================================================*/
create table tb_anexo_beneficiario
(
   BFC_CODIGO           varchar(32) not null,
   ABF_NOME             varchar(255) not null,
   USU_CODIGO           varchar(32) not null,
   TAR_CODIGO           varchar(32) not null,
   ABF_DESCRICAO        varchar(255) not null,
   ABF_ATIVO            smallint not null,
   ABF_DATA             datetime not null,
   ABF_DATA_VALIDADE    date,
   ABF_IP_ACESSO        varchar(45),
   primary key (BFC_CODIGO, ABF_NOME)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert into tb_anexo_beneficiario (BFC_CODIGO, ABF_NOME, USU_CODIGO, TAR_CODIGO, ABF_DESCRICAO, ABF_ATIVO, ABF_DATA, ABF_DATA_VALIDADE, ABF_IP_ACESSO)
select BFC_CODIGO, ABF_NOME, USU_CODIGO, TAR_CODIGO, ABF_DESCRICAO, ABF_ATIVO, ABF_DATA, ABF_DATA_VALIDADE, ABF_IP_ACESSO
from tmp_tb_anexo_beneficiario;

drop table if exists tmp_tb_anexo_beneficiario;

alter table tb_anexo_beneficiario add constraint FK_R_706 foreign key (BFC_CODIGO)
      references tb_beneficiario (BFC_CODIGO) on delete restrict on update restrict;

alter table tb_anexo_beneficiario add constraint FK_R_707 foreign key (TAR_CODIGO)
      references tb_tipo_arquivo (TAR_CODIGO) on delete restrict on update restrict;

alter table tb_anexo_beneficiario add constraint FK_R_708 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

