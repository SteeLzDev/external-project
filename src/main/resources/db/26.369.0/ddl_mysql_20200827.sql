/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     27/08/2020 13:41:48                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_endereco_correspondente                            */
/*==============================================================*/
create table tb_endereco_correspondente
(
   ECR_CODIGO           varchar(32) not null,
   TIE_CODIGO           varchar(32) not null,
   COR_CODIGO           varchar(32) not null,
   ECR_LOGRADOURO       varchar(100) not null,
   ECR_NUMERO           varchar(15) not null,
   ECR_COMPLEMENTO      varchar(40) not null,
   ECR_BAIRRO           varchar(40) not null,
   ECR_MUNICIPIO        varchar(40) not null,
   ECR_UF               char(2) not null,
   ECR_CEP              varchar(10) not null,
   ECR_LATITUDE         decimal(18,15),
   ECR_LONGITUDE        decimal(18,15),
   primary key (ECR_CODIGO)
) ENGINE=InnoDB;

alter table tb_endereco_correspondente add constraint FK_R_810 foreign key (TIE_CODIGO)
      references tb_tipo_endereco (TIE_CODIGO) on delete restrict on update restrict;

alter table tb_endereco_correspondente add constraint FK_R_811 foreign key (COR_CODIGO)
      references tb_correspondente (COR_CODIGO) on delete restrict on update restrict;

