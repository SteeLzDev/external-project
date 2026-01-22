#language:pt

Funcionalidade: Manutenção Beneficiários
	Permite cadastrar, editar, excluir benefiários e anexar arquivo.

	Cenário: Cadastrar novo beneficiário com papel Suporte
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficiarios
   	E pesquisar o servidor "123456"
    E cadastrar novo beneficiario com CPF "796.731.430-13" com papel Suporte
    E clicar no botao salvar
    Então exibe a mensagem "Beneficiário criado com sucesso."
        
  Cenário: Cadastrar novo beneficiário com papel Servidor
   	Dado que o usuario Servidor esteja logado
    Quando acessar menu Manutencao > Beneficiarios
   	E cadastrar novo beneficiario com CPF "206.823.230-80" com papel Servidor
   	E clicar no botao salvar
    Então exibe a mensagem "Beneficiário criado com sucesso."
    
  Cenário: Cadastrar beneficiário com CPF já existente
   	Dado que o usuario Servidor esteja logado
    Quando acessar menu Manutencao > Beneficiarios
   	E cadastrar novo beneficiario com CPF "092.459.399-79" com papel Servidor
   	E clicar no botao salvar
    Então exibe a mensagem de erro "ESTE CPF PERTENCE A OUTRO BENEFICIÁRIO DESTE GRUPO FAMILIAR."
    
 	Cenário: Cadastrar novo endereço do servidor
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficiarios
    E pesquisar o servidor "123456"
    E cadastrar novo endereco do servidor
    E clicar no botao salvar
    Então exibe a mensagem "Endereço do servidor criado com sucesso"
    
 	Cenário: Editar endereço do servidor
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficiarios
    E pesquisar o servidor "145985"
    E editar endereco do servidor
    E clicar no botao salvar
    Então exibe a mensagem "Alterações salvas com sucesso."  
   
 	Cenário: Excluir endereço do servidor
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficiarios
    E pesquisar o servidor "145985"
    E excluir endereco do servidor
    Então exibe a mensagem "Endereço do servidor excluído com sucesso."  
        
	Cenário: Tentar cadastrar beneficiário sem informar os campos obrigatórios
   	Dado que o usuario Servidor esteja logado
    Quando acessar menu Manutencao > Beneficiarios
   	E clicar em Novo Beneficiario
    Então tentar cadastrar beneficiario sem informar os campos obrigatorios
    
  Esquema do Cenário: Tentar cadastrar endereço do servidor sem informar os campos obrigatórios
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficiarios
    E pesquisar o servidor "145985"
    E cadastrar endereco "<cep>", "<logradouro>", "<numero>", "<bairro>", "<uf>" e "<cidade>"
    E clicar no botao salvar
    Então exibe o alerta com a mensagem "<mensagem>"
    Exemplos: 
      | cep        | logradouro       | numero | bairro |      uf        |     cidade     | mensagem |
      |            | Avenida Portugal |   52   | Itapoã | Minas Gerais   | Belo Horizonte | O CEP deve ser informado.       |
      | 31.710-400 |                  |   52   | Itapoã | Minas Gerais   | Belo Horizonte | O logradouro deve ser informado.|
      | 31.710-400 | Avenida Portugal |        | Itapoã | Minas Gerais   | Belo Horizonte | O número deve ser informado.    |
      | 31.710-400 | Avenida Portugal |   52   |        | Minas Gerais   | Belo Horizonte | O bairro deve ser informado.    |
      | 31.710-400 | Avenida Portugal |   52   | Itapoã | -- Selecione --| Belo Horizonte | O estado deve ser informado.    |
      | 31.710-400 | Avenida Portugal |   52   | Itapoã | Minas Gerais   | -- Selecione --| A cidade deve ser informada.    |
   
  Cenário: Editar dados do beneficiário
   	Dado que o usuario Servidor esteja logado
    Quando acessar menu Manutencao > Beneficiarios
   	E editar os dados do beneficiarios
   	E clicar no botao salvar
    Então exibe a mensagem "Alterações salvas com sucesso."    
         
  Cenário: Excluir beneficiário
   	Dado que o usuario Servidor esteja logado
    Quando acessar menu Manutencao > Beneficiarios
   	E excluir beneficiario
    Então exibe a mensagem "Beneficiário deletado com sucesso." 
        
  Cenário: Incluir novo anexo beneficiário
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficiarios
   	E pesquisar o servidor "123456"
   	E anexar arquivo beneficiario
    Então exibe a mensagem "Anexo inserido com sucesso."
    E verificar link para download 
        
  Cenário: Tentar incluir novo anexo beneficiário sem informar campos obrigatórios
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficiarios
   	E pesquisar o servidor "123456"
    Então tentar incluir anexo sem informar campos obrigatorios
       
  Cenário: Excluir anexo beneficiário
    Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficiarios
   	E pesquisar o servidor "123456"
   	E excluir anexo beneficiario
    Então exibe a mensagem "Anexo deletado com sucesso." 
       
  Cenário: Editar dados do anexo beneficiário
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficiarios
   	E pesquisar o servidor "123456"
   	E editar dados do anexo beneficiario
   	E clicar no botao salvar
    Então exibe a mensagem "Anexo editado com sucesso."  
    
    
    