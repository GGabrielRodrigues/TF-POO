package bagagem.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Classe abstrata Processo.
 * Representa de forma genérica qualquer processo de bagagem que será
 * escaneado, organizado e armazenado no sistema.
 */
public abstract class Processo implements Serializable{

    // Atributos privados
    private String base; // Sigla do aeroporto em que o processo foi iniciado (por exemplo, "GYN" ou "GRU"). 
    private String numeroProcesso; // Número de identificação do processo dentro daquela base. 
    private Date dataAbertura; // Data em que a imagem do documento foi capturada e cadastrado o processo no sistema. 
    private String caminhoDocumento; // NOVO ATRIBUTO: para armazenar o caminho/nome do arquivo da imagem.
    private String tipoArquivoDocumento; // NOVO ATRIBUTO: Tipo do arquivo (JPG, PNG, PDF).
    private long tamanhoArquivoDocumento; // NOVO ATRIBUTO: Tamanho do arquivo em bytes (simulado).

    /**
     * Construtor da classe Processo.
     * @param base A sigla do aeroporto onde o processo foi iniciado. 
     * @param numeroProcesso O número de identificação do processo. 
     * @param dataAbertura A data de abertura do processo. 
     */
    public Processo(String base, String numeroProcesso, Date dataAbertura) {
        this.base = base;
        this.numeroProcesso = numeroProcesso;
        this.dataAbertura = dataAbertura;
        this.caminhoDocumento = null; // Inicializa sem caminho de documento
        this.tipoArquivoDocumento = null; // Inicializa novos atributos
        this.tamanhoArquivoDocumento = 0;   // Inicializa novos atributos
    }

    // Métodos Getters para os atributos (necessários para acessar os valores)
    public String getBase() {
        return base;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public String getCaminhoDocumento() {
        return caminhoDocumento;
    }

    public String getTipoArquivoDocumento() {
        return tipoArquivoDocumento;
    }

    public long getTamanhoArquivoDocumento() {
        return tamanhoArquivoDocumento;
    }

    // Métodos Setters para permitir a edição (quando necessário, com validação futura)
    public void setBase(String base) {
        this.base = base;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public void setCaminhoDocumento(String caminhoDocumento) {
        this.caminhoDocumento = caminhoDocumento;
    }

    public void setTipoArquivoDocumento(String tipoArquivoDocumento) {
        this.tipoArquivoDocumento = tipoArquivoDocumento;
    }

    public void setTamanhoArquivoDocumento(long tamanhoArquivoDocumento) {
        this.tamanhoArquivoDocumento = tamanhoArquivoDocumento;
    }

    /**
     * Aciona a câmera do dispositivo e retorna o caminho/URL da foto capturada. 
     * Simula a captura de uma imagem através da câmera do dispositivo, a armazena
     * e atribui metadados simulados.
     * @return Uma String representando o caminho/URL da imagem.
     */
    public String capturarImagem() { 
        System.out.println("Câmera acionada. Imagem capturada.");
        String novoCaminho = "caminho/para/imagem_capturada_" + System.currentTimeMillis() + ".jpg";
        this.caminhoDocumento = novoCaminho; // Armazena o caminho gerado
        this.tipoArquivoDocumento = "JPG"; // Simula tipo de arquivo capturado
        this.tamanhoArquivoDocumento = 1024 * 500; // Simula 500 KB
        return novoCaminho;
    }

    /**
     * Renomeia o arquivo de imagem (por exemplo, para padronizar o nome no repositório). 
     * Atualiza o atributo caminhoDocumento com o novo nome, tentando preservar a extensão.
     * @param novoNome O novo nome para o arquivo de imagem. 
     */
    public void renomearDocumento(String novoNome) { 
        if (this.caminhoDocumento != null && !this.caminhoDocumento.isEmpty()) {
            String extensao = "";
            int indicePonto = this.caminhoDocumento.lastIndexOf('.');
            if (indicePonto > 0) {
                extensao = this.caminhoDocumento.substring(indicePonto);
            } else if (this.tipoArquivoDocumento != null && !this.tipoArquivoDocumento.isEmpty()) {
                // Se não tem ponto no nome original, tenta usar o tipo de arquivo como extensão
                extensao = "." + this.tipoArquivoDocumento.toLowerCase();
            }
            this.caminhoDocumento = novoNome + extensao; // Define o novo caminho/nome
            System.out.println("Documento renomeado para: " + this.caminhoDocumento);
        } else {
            System.out.println("Nenhum documento para renomear neste processo.");
        }
    }

    /**
     * Permite atualizar qualquer atributo do processo (até base e numeroProcesso). 
     * Permite atualizar qualquer atributo do processo, incluindo metadados do documento.
     * @param novosDados Um Map onde a chave é o nome do atributo e o valor é o novo dado.
     */
    public void editarInformacoes(Map<String, Object> novosDados) { 
        if (novosDados.containsKey("base")) {
            this.setBase((String) novosDados.get("base"));
            System.out.println("Base atualizada para: " + this.base);
        }
        if (novosDados.containsKey("numeroProcesso")) {
            this.setNumeroProcesso((String) novosDados.get("numeroProcesso"));
            System.out.println("Número do processo atualizado para: " + this.numeroProcesso);
        }
        if (novosDados.containsKey("dataAbertura")) {
            this.setDataAbertura((Date) novosDados.get("dataAbertura"));
            System.out.println("Data de abertura atualizada para: " + this.dataAbertura);
        }
        if (novosDados.containsKey("caminhoDocumento")) {
            this.setCaminhoDocumento((String) novosDados.get("caminhoDocumento"));
            System.out.println("Caminho do documento atualizado para: " + this.caminhoDocumento);
        }
        if (novosDados.containsKey("tipoArquivoDocumento")) {
            this.setTipoArquivoDocumento((String) novosDados.get("tipoArquivoDocumento"));
            System.out.println("Tipo de arquivo do documento atualizado para: " + this.tipoArquivoDocumento);
        }
        if (novosDados.containsKey("tamanhoArquivoDocumento")) {
            if (novosDados.get("tamanhoArquivoDocumento") instanceof Long) {
                this.setTamanhoArquivoDocumento((Long) novosDados.get("tamanhoArquivoDocumento"));
            } else if (novosDados.get("tamanhoArquivoDocumento") instanceof Integer) {
                this.setTamanhoArquivoDocumento(((Integer) novosDados.get("tamanhoArquivoDocumento")).longValue());
            }
            System.out.println("Tamanho do arquivo do documento atualizado para: " + this.tamanhoArquivoDocumento + " bytes.");
        }
        System.out.println("Informações do processo editadas.");
    }

    /**
     * Salva no repositório (nuvem ou local) o arquivo de imagem e os metadados mínimos (base, numeroProcesso e dataAbertura). 
     * Agora também inclui os metadados do documento (caminho, tipo, tamanho).
     */
    public void armazenarDocumento() { 
        System.out.println("Documento e metadados armazenados para o processo: " + this.base + " - " + this.numeroProcesso);
        System.out.println("Caminho do documento armazenado: " + (caminhoDocumento != null ? caminhoDocumento : "N/A"));
        System.out.println("Metadados do Documento: Tipo=" + (tipoArquivoDocumento != null ? tipoArquivoDocumento : "N/A") +
                           ", Tamanho=" + (tamanhoArquivoDocumento > 0 ? (tamanhoArquivoDocumento / 1024) + " KB" : "N/A"));
        // Em uma implementação real, aqui haveria lógica para persistir os dados e o arquivo.
    }

    /**
     * Retorna todos os processos armazenados, podendo aceitar filtros simples. 
     * (Simplificado para esta fase, em uma aplicação real, interagiria com um banco de dados ou repositório)
     * @return Uma lista de objetos Processo. 
     */
    public static List<Processo> listarDocumentos() { 
        System.out.println("Listando todos os processos (implementação simplificada).");
        // Em uma implementação real, buscaria dados de um repositório.
        return new ArrayList<>(); // Retorna uma lista vazia por enquanto
    }

    /**
     * Retorna o processo específico com seus atributos e caminho de imagem - identificado pela combinação de "base + numeroProcesso". 
     * (Simplificado para esta fase)
     * @param base A sigla do aeroporto do processo a ser buscado. 
     * @param numeroProcesso O número do processo a ser buscado. 
     * @return Um objeto Processo específico, ou null se não encontrado (nesta fase, apenas um exemplo). 
     */
    public static Processo buscarDocumento(String base, String numeroProcesso) { 
        System.out.println("Buscando processo: " + base + " - " + numeroProcesso + " (implementação simplificada).");
        // Em uma implementação real, buscaria no repositório.
        return null; // Retorna null por enquanto
    }
}