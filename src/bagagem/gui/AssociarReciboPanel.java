package bagagem.gui;

import bagagem.model.*;
import bagagem.model.exception.ValidacaoException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map; // Adicionar este import
import java.util.HashMap; // Adicionar este import
import javax.imageio.ImageIO; // Adicionar este import
import java.awt.image.BufferedImage; // Adicionar este import
import java.io.IOException; // Adicionar este import


public class AssociarReciboPanel extends JPanel {
    private JPanel panelBusca;
    private JTextField txtBuscaBase;
    private JTextField txtBuscaNumeroProcesso;
    private JButton btnBuscarProcesso;
    private JTextArea areaDetalhesProcesso;
    private JComboBox<String> cmbTipoRecibo;
    private JFormattedTextField txtDataAssinatura;
    private JLabel lblCampoEspecificoRecibo;
    private JTextField txtCampoEspecificoRecibo;
    private JButton btnSalvarRecibo;
    private JPanel panelCriacaoRecibo;
    private JTextField txtCaminhoDocumentoRecibo;
    private JButton btnAnexarDocumentoRecibo;
    
    private JLabel lblMiniaturaDocumentoRecibo; // Variável para a miniatura

    private String tempCaminhoDocRecibo;
    private String tempTipoDocRecibo;
    private long tempTamanhoDocRecibo;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Processo processoAlvo;
    private Recibo reciboEmEdicao;

    // Construtor ÚNICO para Criação (reciboParaEditar == null) ou Edição (reciboParaEditar != null)
    public AssociarReciboPanel(Recibo reciboParaEditar) {
        this.reciboEmEdicao = reciboParaEditar; // Define o recibo para edição (pode ser null)

        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        // --- PAINEL DE BUSCA DE PROCESSO (Visível apenas para CRIAR novo recibo) ---
        panelBusca = new JPanel(new GridBagLayout());
        panelBusca.setBorder(BorderFactory.createTitledBorder("1. Buscar Processo para Associar"));
        GridBagConstraints gbcBusca = new GridBagConstraints();
        gbcBusca.insets = new Insets(5, 5, 5, 5);
        gbcBusca.fill = GridBagConstraints.HORIZONTAL;

        gbcBusca.gridx = 0; gbcBusca.gridy = 0;
        panelBusca.add(new JLabel("Base:"), gbcBusca);
        txtBuscaBase = new JTextField(5);
        gbcBusca.gridx = 1;
        panelBusca.add(txtBuscaBase, gbcBusca);

        gbcBusca.gridx = 2;
        panelBusca.add(new JLabel("Número Processo:"), gbcBusca);
        txtBuscaNumeroProcesso = new JTextField(8);
        gbcBusca.gridx = 3;
        panelBusca.add(txtBuscaNumeroProcesso, gbcBusca);

        btnBuscarProcesso = new JButton("Buscar Processo");
        gbcBusca.gridx = 4;
        panelBusca.add(btnBuscarProcesso, gbcBusca);

        add(panelBusca, BorderLayout.NORTH);

        // --- PAINEL CENTRAL (Detalhes do Processo + Criação/Edição do Recibo) ---
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        areaDetalhesProcesso = new JTextArea(5, 40);
        areaDetalhesProcesso.setEditable(false);
        areaDetalhesProcesso.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaDetalhesProcesso.setBorder(BorderFactory.createTitledBorder("Detalhes do Processo Encontrado"));
        panelCentral.add(new JScrollPane(areaDetalhesProcesso), BorderLayout.NORTH);

        // --- PAINEL DE CRIAÇÃO/EDIÇÃO DO RECIBO ---
        panelCriacaoRecibo = new JPanel(new GridBagLayout());
        panelCriacaoRecibo.setBorder(BorderFactory.createTitledBorder("2. Criar e Salvar Novo Recibo"));
        GridBagConstraints gbcRecibo = new GridBagConstraints();
        gbcRecibo.insets = new Insets(5, 5, 5, 5);
        gbcRecibo.fill = GridBagConstraints.HORIZONTAL;
        gbcRecibo.anchor = GridBagConstraints.WEST;

        gbcRecibo.gridx = 0; gbcRecibo.gridy = 0;
        panelCriacaoRecibo.add(new JLabel("Tipo de Recibo:"), gbcRecibo);
        cmbTipoRecibo = new JComboBox<>();
        gbcRecibo.gridx = 1; gbcRecibo.gridwidth = 2;
        panelCriacaoRecibo.add(cmbTipoRecibo, gbcRecibo);

        gbcRecibo.gridx = 0; gbcRecibo.gridy++; gbcRecibo.gridwidth = 1;
        panelCriacaoRecibo.add(new JLabel("Data Assinatura (dd/MM/yyyy):"), gbcRecibo);
        try {
            txtDataAssinatura = new JFormattedTextField(new MaskFormatter("##/##/####"));
        } catch (ParseException e) {
            txtDataAssinatura = new JFormattedTextField();
        }
        gbcRecibo.gridx = 1; gbcRecibo.gridwidth = 2;
        panelCriacaoRecibo.add(txtDataAssinatura, gbcRecibo);

        gbcRecibo.gridx = 0; gbcRecibo.gridy++; gbcRecibo.gridwidth = 1;
        lblCampoEspecificoRecibo = new JLabel("Dado Específico:");
        panelCriacaoRecibo.add(lblCampoEspecificoRecibo, gbcRecibo);
        txtCampoEspecificoRecibo = new JTextField(20);
        gbcRecibo.gridx = 1; gbcRecibo.gridwidth = 2;
        panelCriacaoRecibo.add(txtCampoEspecificoRecibo, gbcRecibo);

        // --- Seção de Anexo de Documento do Recibo com Miniatura ---
        gbcRecibo.gridx = 0; gbcRecibo.gridy++; gbcRecibo.gridwidth = 1;
        panelCriacaoRecibo.add(new JLabel("Anexo do Recibo:"), gbcRecibo);
        
        gbcRecibo.gridx = 1; gbcRecibo.gridwidth = 1; // Coluna para o campo de texto
        txtCaminhoDocumentoRecibo = new JTextField(20);
        txtCaminhoDocumentoRecibo.setEditable(false);
        panelCriacaoRecibo.add(txtCaminhoDocumentoRecibo, gbcRecibo);
        
        gbcRecibo.gridx = 2; gbcRecibo.gridwidth = 1; // Coluna para a miniatura
        lblMiniaturaDocumentoRecibo = new JLabel(); // NOVO JLabel
        lblMiniaturaDocumentoRecibo.setPreferredSize(new Dimension(80, 80));
        lblMiniaturaDocumentoRecibo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblMiniaturaDocumentoRecibo.setHorizontalAlignment(SwingConstants.CENTER);
        lblMiniaturaDocumentoRecibo.setVerticalAlignment(SwingConstants.CENTER);
        panelCriacaoRecibo.add(lblMiniaturaDocumentoRecibo, gbcRecibo);

        // Botão Anexar Documento ABAIXO do campo de texto e miniatura (ajuste de layout)
        gbcRecibo.gridx = 1; gbcRecibo.gridy++; gbcRecibo.gridwidth = 2; 
        gbcRecibo.anchor = GridBagConstraints.CENTER;
        btnAnexarDocumentoRecibo = new JButton("Anexar...");
        panelCriacaoRecibo.add(btnAnexarDocumentoRecibo, gbcRecibo);
        // --- Fim da Seção de Anexo ---
        
        gbcRecibo.gridx = 0; gbcRecibo.gridy++; gbcRecibo.gridwidth = 3; gbcRecibo.anchor = GridBagConstraints.CENTER;
        btnSalvarRecibo = new JButton("Salvar Recibo");
        panelCriacaoRecibo.add(btnSalvarRecibo, gbcRecibo);

        panelCentral.add(panelCriacaoRecibo, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // --- LISTENERS ---
        btnBuscarProcesso.addActionListener(e -> buscarProcessoParaAssociacao()); // RENOMEADO para evitar conflito
        btnSalvarRecibo.addActionListener(e -> salvarRecibo());
        cmbTipoRecibo.addActionListener(e -> atualizarCampoEspecificoRecibo());
        btnAnexarDocumentoRecibo.addActionListener(e -> selecionarDocumentoRecibo());

        // --- LÓGICA DE INICIALIZAÇÃO (Criação vs Edição) ---
        if (reciboEmEdicao != null) {
            // MODO DE EDIÇÃO:
            panelBusca.setVisible(false); // Esconde a busca de processo
            areaDetalhesProcesso.setBorder(BorderFactory.createTitledBorder("Processo Associado (Recibo ID: " + reciboEmEdicao.getId() + ")"));
            btnSalvarRecibo.setText("Atualizar Recibo");
            this.processoAlvo = reciboEmEdicao.getProcessoAssociado(); // Define o processo alvo
            
            // Popula e seleciona o tipo correto do recibo em edição
            popularTiposRecibo(); // Popula o JComboBox com base no processoAlvo (já definido)
            
            preencherCamposParaEdicao(); // Preenche os campos do recibo
            habilitarCamposRecibo(true); // Garante que a seção de recibo esteja visível
            
            // Desabilita campos que não devem ser alterados na edição do recibo
            cmbTipoRecibo.setEnabled(false); 

        } else {
            // MODO DE CRIAÇÃO (ASSOCIAÇÃO):
            // Este construtor sem parâmetro será chamado via showPanel(new AssociarReciboPanel())
            habilitarCamposRecibo(false); // Esconde a seção de recibo inicialmente
        }
    }

    // Método para controlar a visibilidade da seção de criação/edição de recibo
    // E, crucialmente, para limpar o estado quando desabilitado (para novo recibo)
    private void habilitarCamposRecibo(boolean habilitar) {
        panelCriacaoRecibo.setVisible(habilitar);
        if(!habilitar && reciboEmEdicao == null) { // Apenas limpa se for um NOVO recibo
            areaDetalhesProcesso.setText("");
            processoAlvo = null;
            // Limpa também os campos da seção de recibo
            txtDataAssinatura.setText("");
            txtCampoEspecificoRecibo.setText("");
            txtCaminhoDocumentoRecibo.setText("");
            lblMiniaturaDocumentoRecibo.setIcon(null);
            lblMiniaturaDocumentoRecibo.setText("Sem Anexo");
            cmbTipoRecibo.removeAllItems(); // Remove os itens do ComboBox para que sejam populados novamente.
            tempCaminhoDocRecibo = null;
            tempTipoDocRecibo = null;
            tempTamanhoDocRecibo = 0;
        }
    }

    // Lógica de busca de processo para associação de NOVO recibo
    // RENOMEADO para evitar conflito e ser mais descritivo
    private void buscarProcessoParaAssociacao() {
        String base = txtBuscaBase.getText().trim().toUpperCase();
        String numero = txtBuscaNumeroProcesso.getText().trim();
        if (base.isEmpty() || numero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Base e Número do Processo são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        processoAlvo = ProcessoRepository.buscarProcessoPorBaseNumero(base, numero);
        if (processoAlvo == null) {
            JOptionPane.showMessageDialog(this, "Processo não encontrado.", "Busca", JOptionPane.INFORMATION_MESSAGE);
            habilitarCamposRecibo(false);
            return;
        }
        // Validação adicional para novo recibo: impede associar se já tiver um.
        // Essa validação só faz sentido se o sistema permite apenas 1 recibo por processo.
        // Se puder ter múltiplos recibos, remova este if.
        List<Recibo> recibosExistentes = ProcessoRepository.listarRecibosPorProcesso(processoAlvo);
        if (!recibosExistentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este processo já possui um recibo associado.", "Validação", JOptionPane.WARNING_MESSAGE);
            habilitarCamposRecibo(false);
            return;
        }
        
        areaDetalhesProcesso.setText(
            "Processo Encontrado:\n" +
            "  - Tipo: " + processoAlvo.getClass().getSimpleName() + "\n" +
            "  - Base: " + processoAlvo.getBase() + "\n" +
            "  - Número: " + processoAlvo.getNumeroProcesso()
        );
        popularTiposRecibo(); // Popula o combobox de tipos de recibo
        habilitarCamposRecibo(true); // Habilita a seção de recibo
        txtDataAssinatura.setText(dateFormat.format(new Date())); // Preenche data atual
    }

    // Popula o JComboBox de tipos de recibo baseados no processo alvo
    private void popularTiposRecibo() {
        cmbTipoRecibo.removeAllItems();
        if (processoAlvo instanceof DanificacaoBagagem) {
            cmbTipoRecibo.addItem("Recibo de Conserto de Bagagem");
            cmbTipoRecibo.addItem("Recibo de Indenização em Milhas");
        } else if (processoAlvo instanceof ExtravioBagagem) {
            cmbTipoRecibo.addItem("Recibo de Entrega de Bagagem Extraviada");
        } else if (processoAlvo instanceof ItemEsquecidoAviao) {
            cmbTipoRecibo.addItem("Recibo de Item Esquecido em Avião");
        }
        // Se for modo de edição, tenta selecionar o item correto no combobox
        if (reciboEmEdicao != null) {
            String tipoSimples = reciboEmEdicao.getClass().getSimpleName();
            switch (tipoSimples) {
                case "ReciboConsertoBagagem": cmbTipoRecibo.setSelectedItem("Recibo de Conserto de Bagagem"); break;
                case "ReciboIndenizacaoMilhas": cmbTipoRecibo.setSelectedItem("Recibo de Indenização em Milhas"); break;
                case "ReciboEntregaBagagemExtraviada": cmbTipoRecibo.setSelectedItem("Recibo de Entrega de Bagagem Extraviada"); break;
                case "ReciboItemEsquecidoAviao": cmbTipoRecibo.setSelectedItem("Recibo de Item Esquecido em Avião"); break;
            }
        }
    }
    
    // Atualiza o JLabel do campo específico do recibo
    private void atualizarCampoEspecificoRecibo() {
        String tipoRecibo = (String) cmbTipoRecibo.getSelectedItem();
        if (tipoRecibo == null) return;
        switch(tipoRecibo) {
            case "Recibo de Indenização em Milhas": lblCampoEspecificoRecibo.setText("Quantidade de Milhas:"); break;
            case "Recibo de Item Esquecido em Avião": lblCampoEspecificoRecibo.setText("Doc. Identificação Cliente:"); break;
            default: lblCampoEspecificoRecibo.setText("Local de Entrega/Retirada:"); break;
        }
        // Só limpa o campo se estiver criando um novo recibo ou mudando o tipo de recibo em um novo.
        // Se estiver editando, o campo já terá o valor preenchido.
        // A comparação de tipo aqui pode ser simplificada já que o cmbTipoRecibo.setEnabled(false) em edição.
        if(reciboEmEdicao == null) { // Apenas limpa em modo de criação
            txtCampoEspecificoRecibo.setText("");
        }
    }

    // Lógica para selecionar arquivo de documento para o recibo
    private void selecionarDocumentoRecibo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imagens e PDFs", "jpg", "jpeg", "png", "pdf"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtCaminhoDocumentoRecibo.setText(selectedFile.getAbsolutePath());
            this.tempCaminhoDocRecibo = selectedFile.getAbsolutePath();
            String fileName = selectedFile.getName();
            int i = fileName.lastIndexOf('.');
            this.tempTipoDocRecibo = (i > 0) ? fileName.substring(i + 1).toUpperCase() : "";
            this.tempTamanhoDocRecibo = selectedFile.length();

            // Exibir miniatura do recibo após selecionar
            exibirMiniaturaRecibo(this.tempCaminhoDocRecibo, lblMiniaturaDocumentoRecibo);
        }
    }

    // Método auxiliar para exibir a miniatura de um recibo
    private void exibirMiniaturaRecibo(String caminhoArquivo, JLabel targetLabel) {
        if (caminhoArquivo == null || caminhoArquivo.isEmpty()) {
            targetLabel.setIcon(null);
            targetLabel.setText("Sem Anexo");
            return;
        }

        File file = new File(caminhoArquivo);
        if (!file.exists()) {
            targetLabel.setIcon(null);
            targetLabel.setText("Arquivo não encontrado");
            return;
        }

        String tipoDoc = "";
        int i = caminhoArquivo.lastIndexOf('.');
        if (i > 0) {
            tipoDoc = caminhoArquivo.substring(i + 1).toLowerCase();
        }
        
        if (tipoDoc.equals("jpg") || tipoDoc.equals("jpeg") || tipoDoc.equals("png")) {
            try {
                BufferedImage originalImage = ImageIO.read(file);
                if (originalImage != null) {
                    int desiredSize = 70; // Tamanho para a miniatura do recibo
                    Image scaledImage = originalImage.getScaledInstance(desiredSize, desiredSize, Image.SCALE_SMOOTH);
                    targetLabel.setIcon(new ImageIcon(scaledImage));
                    targetLabel.setText("");
                } else {
                    targetLabel.setIcon(null);
                    targetLabel.setText("Erro ao carregar");
                }
            } catch (IOException e) {
                targetLabel.setIcon(null);
                targetLabel.setText("Erro de leitura");
                System.err.println("Erro ao carregar miniatura do recibo: " + e.getMessage());
            }
        } else if (tipoDoc.equals("pdf")) {
            targetLabel.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
            targetLabel.setText("PDF");
        } else {
            targetLabel.setIcon(null);
            targetLabel.setText("Não suportado");
        }
    }


    private void salvarRecibo() {
        try {
            // Validação de processoAlvo apenas para criação, não edição
            if(reciboEmEdicao == null && processoAlvo == null) { 
                 throw new ValidacaoException("Busque um processo para associar o recibo.");
            }

            Date dataAssinatura;
            try {
                dataAssinatura = dateFormat.parse(txtDataAssinatura.getText());
            } catch (ParseException e) {
                throw new ValidacaoException("Formato de data inválido. Use dd/MM/yyyy.");
            }
            String tipoReciboSelecionado = (String) cmbTipoRecibo.getSelectedItem();
            if (tipoReciboSelecionado == null || tipoReciboSelecionado.isEmpty()) {
                 throw new ValidacaoException("Selecione o tipo de recibo.");
            }

            String dadoEspecifico = txtCampoEspecificoRecibo.getText().trim();
            if (dadoEspecifico.isEmpty()) {
                throw new ValidacaoException("O campo específico é obrigatório.");
            }
            
            if (reciboEmEdicao == null) { // Modo de Criação
                criarNovoRecibo(dataAssinatura, dadoEspecifico, tipoReciboSelecionado);
            } else { // Modo de Edição
                atualizarReciboExistente(dataAssinatura, dadoEspecifico);
            }

        } catch (ValidacaoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "A quantidade de milhas deve ser um número inteiro válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método auxiliar para criar um NOVO recibo
    private void criarNovoRecibo(Date dataAssinatura, String dadoEspecifico, String tipoReciboSelecionado) throws ValidacaoException, NumberFormatException {
        // Validação de anexo apenas para NOVO recibo
        if (tempCaminhoDocRecibo == null || tempCaminhoDocRecibo.trim().isEmpty()) {
            throw new ValidacaoException("É obrigatório anexar um documento para criar um novo recibo.");
        }

        Recibo novoRecibo = null;
        try {
            switch (tipoReciboSelecionado) {
                case "Recibo de Conserto de Bagagem":
                    novoRecibo = new ReciboConsertoBagagem(processoAlvo.getBase(), processoAlvo.getNumeroProcesso(), dataAssinatura, processoAlvo, dadoEspecifico);
                    break;
                case "Recibo de Indenização em Milhas":
                    int milhas = Integer.parseInt(dadoEspecifico);
                    novoRecibo = new ReciboIndenizacaoMilhas(processoAlvo.getBase(), processoAlvo.getNumeroProcesso(), dataAssinatura, processoAlvo, milhas);
                    break;
                case "Recibo de Entrega de Bagagem Extraviada":
                    novoRecibo = new ReciboEntregaBagagemExtraviada(processoAlvo.getBase(), processoAlvo.getNumeroProcesso(), dataAssinatura, processoAlvo, dadoEspecifico);
                    break;
                case "Recibo de Item Esquecido em Avião":
                    novoRecibo = new ReciboItemEsquecidoAviao(processoAlvo.getBase(), processoAlvo.getNumeroProcesso(), dataAssinatura, processoAlvo, dadoEspecifico);
                    break;
                default:
                    throw new ValidacaoException("Tipo de recibo selecionado inválido.");
            }
        } catch (IllegalArgumentException ex) {
            throw new ValidacaoException("Erro de associação: " + ex.getMessage());
        }

        if (novoRecibo != null) {
            novoRecibo.setCaminhoDocumento(this.tempCaminhoDocRecibo);
            novoRecibo.setTipoArquivoDocumento(this.tempTipoDocRecibo);
            novoRecibo.setTamanhoArquivoDocumento(this.tempTamanhoDocRecibo);
            ProcessoRepository.adicionarRecibo(novoRecibo);
            JOptionPane.showMessageDialog(this, "Recibo associado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
        } 
    }

    // Método auxiliar para ATUALIZAR um recibo existente
    private void atualizarReciboExistente(Date dataAssinatura, String dadoEspecifico) throws NumberFormatException {
        reciboEmEdicao.setDataAssinatura(dataAssinatura);

        if (reciboEmEdicao instanceof ReciboConsertoBagagem) {
            ((ReciboConsertoBagagem) reciboEmEdicao).setEntregaOuRetiradaEmAeroporto(dadoEspecifico);
        } else if (reciboEmEdicao instanceof ReciboIndenizacaoMilhas) {
            ((ReciboIndenizacaoMilhas) reciboEmEdicao).setQuantidadeMilhas(Integer.parseInt(dadoEspecifico));
        } else if (reciboEmEdicao instanceof ReciboEntregaBagagemExtraviada) {
            ((ReciboEntregaBagagemExtraviada) reciboEmEdicao).setEntregaOuRetiradaEmAeroporto(dadoEspecifico);
        } else if (reciboEmEdicao instanceof ReciboItemEsquecidoAviao) {
            ((ReciboItemEsquecidoAviao) reciboEmEdicao).setDocumentoIdentificacaoClienteRetirada(dadoEspecifico);
        }
        
        // Atualiza os metadados do documento SE um novo documento foi anexado DURANTE a edição
        // Ou se o documento original foi removido (tempCaminhoDocRecibo == null e original não é null)
        if (this.tempCaminhoDocRecibo != null && !this.tempCaminhoDocRecibo.isEmpty() && 
            (reciboEmEdicao.getCaminhoDocumento() == null || !this.tempCaminhoDocRecibo.equals(reciboEmEdicao.getCaminhoDocumento()))) {
            
            reciboEmEdicao.setCaminhoDocumento(this.tempCaminhoDocRecibo);
            reciboEmEdicao.setTipoArquivoDocumento(this.tempTipoDocRecibo);
            reciboEmEdicao.setTamanhoArquivoDocumento(this.tempTamanhoDocRecibo);
        } else if (this.tempCaminhoDocRecibo == null && reciboEmEdicao.getCaminhoDocumento() != null) {
            // Se o usuário limpou o campo de anexo durante a edição
            reciboEmEdicao.setCaminhoDocumento(null);
            reciboEmEdicao.setTipoArquivoDocumento(null);
            reciboEmEdicao.setTamanhoArquivoDocumento(0);
        }


        if (ProcessoRepository.atualizarRecibo(reciboEmEdicao)) {
            JOptionPane.showMessageDialog(this, "Recibo atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario(); // Limpa e prepara para nova operação
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao atualizar o recibo.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Preenche os campos do formulário com dados do recibo para EDIÇÃO
    private void preencherCamposParaEdicao() {
        if (reciboEmEdicao == null) return;
        
        txtDataAssinatura.setText(dateFormat.format(reciboEmEdicao.getDataAssinatura()));
        
        // Configura os dados do documento anexado para a miniatura
        if (reciboEmEdicao.getCaminhoDocumento() != null && !reciboEmEdicao.getCaminhoDocumento().isEmpty()) {
            txtCaminhoDocumentoRecibo.setText(reciboEmEdicao.getCaminhoDocumento());
            this.tempCaminhoDocRecibo = reciboEmEdicao.getCaminhoDocumento();
            this.tempTipoDocRecibo = reciboEmEdicao.getTipoArquivoDocumento();
            this.tempTamanhoDocRecibo = reciboEmEdicao.getTamanhoArquivoDocumento();
            exibirMiniaturaRecibo(reciboEmEdicao.getCaminhoDocumento(), lblMiniaturaDocumentoRecibo);
        } else {
            lblMiniaturaDocumentoRecibo.setIcon(null);
            lblMiniaturaDocumentoRecibo.setText("Sem Anexo");
            // Certifique-se de que tempCaminhoDocRecibo também seja null neste caso
            this.tempCaminhoDocRecibo = null;
            this.tempTipoDocRecibo = null;
            this.tempTamanhoDocRecibo = 0;
        }

        // Define o texto para o campo específico e seleciona o tipo no ComboBox
        // O cmbTipoRecibo.setEnabled(false) no construtor garante que isso não seja alterado em edição.
        String tipoReciboClasse = reciboEmEdicao.getClass().getSimpleName();
        switch (tipoReciboClasse) {
            case "ReciboConsertoBagagem":
                cmbTipoRecibo.setSelectedItem("Recibo de Conserto de Bagagem");
                txtCampoEspecificoRecibo.setText(((ReciboConsertoBagagem) reciboEmEdicao).getEntregaOuRetiradaEmAeroporto());
                break;
            case "ReciboIndenizacaoMilhas":
                cmbTipoRecibo.setSelectedItem("Recibo de Indenização em Milhas");
                txtCampoEspecificoRecibo.setText(String.valueOf(((ReciboIndenizacaoMilhas) reciboEmEdicao).getQuantidadeMilhas()));
                break;
            case "ReciboEntregaBagagemExtraviada":
                cmbTipoRecibo.setSelectedItem("Recibo de Entrega de Bagagem Extraviada");
                txtCampoEspecificoRecibo.setText(((ReciboEntregaBagagemExtraviada) reciboEmEdicao).getEntregaOuRetiradaEmAeroporto());
                break;
            case "ReciboItemEsquecidoAviao":
                cmbTipoRecibo.setSelectedItem("Recibo de Item Esquecido em Avião");
                txtCampoEspecificoRecibo.setText(((ReciboItemEsquecidoAviao) reciboEmEdicao).getDocumentoIdentificacaoClienteRetirada());
                break;
        }
    }
    
    // Limpa todos os campos do formulário e redefine o estado para criação
    private void limparFormulario() {
        txtBuscaBase.setText("");
        txtBuscaNumeroProcesso.setText("");
        txtCaminhoDocumentoRecibo.setText("");
        this.tempCaminhoDocRecibo = null;
        this.tempTipoDocRecibo = null;
        this.tempTamanhoDocRecibo = 0;
        lblMiniaturaDocumentoRecibo.setIcon(null);
        lblMiniaturaDocumentoRecibo.setText("Sem Anexo");
        txtDataAssinatura.setText(dateFormat.format(new Date()));
        txtCampoEspecificoRecibo.setText("");
        cmbTipoRecibo.removeAllItems(); // Remove todos os itens (serão populados novamente na busca)
        
        habilitarCamposRecibo(false); // Esconde a seção de recibo
        reciboEmEdicao = null; // Resetar recibo em edição
        processoAlvo = null; // Resetar processo alvo
        
        // Re-habilita campos de busca do processo
        panelBusca.setVisible(true);
        areaDetalhesProcesso.setText("");
        areaDetalhesProcesso.setBorder(BorderFactory.createTitledBorder("Detalhes do Processo Encontrado")); // Restaura título padrão
        txtBuscaBase.setEnabled(true);
        txtBuscaNumeroProcesso.setEnabled(true);
        btnBuscarProcesso.setEnabled(true);
        cmbTipoRecibo.setEnabled(true); // Garante que o ComboBox de tipo de recibo esteja habilitado para nova associação
    }
}