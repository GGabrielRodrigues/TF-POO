# Sistema de Gestão de Bagagens

## 1. Visão Geral do Projeto

Este projeto, desenvolvido para a disciplina de Programação Orientada a Objetos, é um sistema de desktop para a gestão de processos de ocorrências com bagagens em uma companhia aérea. A aplicação substitui um processo manual e baseado em papel por uma solução digital, centralizada e mais eficiente.

O sistema permite o registro, a consulta e a gestão de diferentes tipos de processos, como **danificação de bagagens**, **extravio** e **itens esquecidos em aeronaves**, além de permitir a associação dos respectivos recibos de resolução.

**Autores:**
* JÚLIA DE SOUZA NASCIMENTO
* ANA LUÍSA PEREIRA DOS SANTOS
* GABRIEL RODRIGUES DA SILVA

---

## 2. Problema Abordado

O setor de bagagens de companhias aéreas frequentemente lida com processos de forma manual. Documentos em papel são preenchidos, escaneados e armazenados em nuvem, um fluxo de trabalho que é:

* **Lento e moroso:** Aumenta o tempo de resolução para o cliente.
* **Suscetível a erros:** Erros de preenchimento e perda de documentos são comuns.
* **Ineficiente:** Dificulta a busca, o rastreamento e a geração de relatórios sobre as ocorrências.

---

## 3. Funcionalidades Principais

O sistema foi projetado para automatizar e organizar todo o ciclo de vida de uma ocorrência de bagagem.

### Gestão de Processos
* **Cadastro de Novos Processos:** Permite registrar processos de três tipos: `Danificação de Bagagem`, `Extravio de Bagagem` e `Item Esquecido em Avião`.
* **Anexo de Documentos:** Suporte para anexar documentos relevantes (imagens em JPG/PNG ou arquivos PDF) a cada processo.
* **Edição e Exclusão:** Flexibilidade para editar informações de um processo já cadastrado ou excluí-lo (o que também remove os recibos associados).
* **Listagem e Consulta:** Exibe todos os processos em uma tabela, com funcionalidades de filtro por base e tipo de processo.
* **Busca Específica:** Permite a busca de um processo único pela combinação de `Base` + `Número do Processo`.

### Gestão de Recibos
* **Associação a Processos:** Um recibo pode ser criado e associado a um processo existente, formalizando a resolução da ocorrência.
* **Tipos de Recibos Específicos:** O sistema valida a associação correta entre o tipo de processo e o tipo de recibo (ex: um `ReciboConsertoBagagem` só pode ser associado a um processo de `DanificacaoBagagem`).
* **Gerenciamento Completo:** Funcionalidades de listagem, edição, exclusão e visualização de detalhes para todos os recibos.

---

## 4. Requisitos do Sistema

### Requisitos Funcionais (RF)

* **RF1:** Permitir o cadastro de processos de bagagem (Danificação, Extravio, Itens Esquecidos).
* **RF2:** Armazenar informações básicas do processo (base, número do processo, data de abertura).
* **RF3:** Permitir a edição das informações do processo (exceto a Base e Número do Processo).
* **RF4:** Permitir a remoção de um processo iniciado.
* **RF5:** Exibir informações completas do processo, incluindo imagens.
* **RF6:** Permitir a captura de imagens de documentos utilizando a câmera do dispositivo.
* **RF7:** Aceitar upload de imagens já existentes (JPG, PNG, PDF).
* **RF8:** Exibir miniatura da imagem antes do armazenamento para pré-visualização.
* **RF9:** Renomear arquivos de imagem para padronização.
* **RF10:** Armazenar documentos em um repositório local com metadados associados.
* **RF11:** Identificar e classificar automaticamente os processos nas categorias adequadas.
* **RF12:** Permitir a associação de recibos a processos existentes.
* **RF13:** Armazenar informações específicas de cada tipo de recibo (ex.: quantidade de milhas).
* **RF14:** Permitir a edição dos dados do recibo.
* **RF15:** Listar todos os processos cadastrados, com possibilidade de filtros.
* **RF16:** Buscar um processo específico por base + número do processo.
* **RF17:** Garantir que cada processo possa estar associado a no máximo um recibo.
* **RF18:** Garantir que cada recibo esteja vinculado a exatamente um processo.

### Requisitos Não Funcionais (RNF)

* **RNF1:** Interface intuitiva para facilitar o registro e consulta.
* **RNF2:** Interface responsiva.
* **RNF3:** Tempo de resposta $\le3$ segundos para consultas.
* **RNF4:** Armazenamento seguro das imagens e metadados.
* **RNF5:** Armazenar até 20.000 processos sem degradação de performance.
* **RNF6:** Desenvolvimento em Java para desktop.
* **RNF7:** Funcionar em sistemas operacionais Windows e Linux.

---

## 5. Arquitetura e Tecnologias

O projeto foi desenvolvido em **Java** com a interface gráfica construída utilizando a biblioteca **Swing**. A arquitetura segue os princípios da Programação Orientada a Objetos.

### Estrutura do Código
O código-fonte está organizado nos seguintes pacotes:
* `bagagem.main`: Contém a classe `Application`, ponto de entrada do sistema.
* `bagagem.model`: O coração do sistema, com as classes de domínio (`Processo`, `Recibo` e suas subclasses), a classe de exceção personalizada (`ValidacaoException`) e o `ProcessoRepository`, responsável pela persistência.
* `bagagem.gui`: Todas as classes relacionadas à interface gráfica (`MainFrame`, `CadastroProcessoPanel`, `ListagemProcessosPanel`, etc.).
* `test`: Contém os testes unitários desenvolvidos com **JUnit 5** para validar as regras de negócio e a lógica das classes do modelo.

### Persistência de Dados
A persistência é realizada de duas formas:
1.  **Serialização de Objetos Java:** As listas de processos e recibos são salvas em arquivos binários (`.dat`) na raiz do projeto. Isso garante que os dados sejam mantidos entre as execuções do programa.
2.  **Repositório de Documentos:** Os arquivos anexados (imagens e PDFs) são copiados para uma pasta dedicada no computador do usuário (`Documentos/BagagemSystemDocs`), organizados em subpastas por tipo. O sistema armazena apenas o caminho para esses arquivos, tornando o acesso mais eficiente.

---

## 6. Como Executar o Projeto

1.  **Pré-requisitos:**
    * JDK 23 ou superior instalado.
    * Um IDE Java, como o [Apache NetBeans](https://netbeans.apache.org/) ou [IntelliJ IDEA](https://www.jetbrains.com/idea/).

2.  **Passos para Execução:**
    * Clone ou baixe este repositório.
    * Abra o projeto no seu IDE.
    * Localize a classe `bagagem.main.Application.java`.
    * Execute o método `main` desta classe para iniciar o sistema.

3.  **Observações:**
    * Ao ser executado pela primeira vez, o sistema criará automaticamente a pasta `BagagemSystemDocs` no diretório "Documentos" do seu usuário.
    * Os arquivos de dados (`processos.dat`, `recibos.dat`, `counters.dat`) serão criados na pasta raiz do projeto.

---

## 7. Diagrama de Classes

O diagrama abaixo ilustra a relação entre as principais classes do domínio.

<details>
<summary>Código PlantUML do Diagrama</summary>

```plantuml
@startuml
skinparam classAttributeIconSize 0
hide empty members
skinparam defaultFontName Arial

abstract class Processo {
 # base: String
 # numeroProcesso: String
 # dataAbertura: Date
 + capturarImagem()
 + renomearDocumento(novoNome: String)
 + editarInformacoes(novosDados: Map<String, Object>)
 + armazenarDocumento()
 + listarDocumentos()
 + buscarDocumento(base: String, numeroProcesso: String)
}

abstract class Recibo {
 # base: String
 # numeroProcesso: String
 # dataAssinatura: Date
 # processoAssociado: Processo
 + associarAoProcesso(p: Processo)
 + editarDadosRecibo(novosDados: Map<String, Object>)
}

class DanificacaoBagagem extends Processo {
 + etiquetaBagagemDanificada: String
}

class ExtravioBagagem extends Processo {
 + etiquetaBagagemExtraviada: String
}

class ItemEsquecidoAviao extends Processo {
 + numeroVoo: String
}

class ReciboConsertoBagagem extends Recibo {
 + entregaOuRetiradaEmAeroporto: String
}

class ReciboIndenizacaoMilhas extends Recibo {
 + quantidadeMilhas: int
}

class ReciboEntregaBagagemExtraviada extends Recibo {
 + entregaOuRetiradaEmAeroporto: String
}

class ReciboItemEsquecidoAviao extends Recibo {
 + documentoIdentificacaoClienteRetirada: String
}

Recibo "1" --> "1" Processo : "refere-se a"

@enduml
