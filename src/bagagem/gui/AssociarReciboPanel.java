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
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;


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
    private JLabel lblMiniaturaDocumentoRecibo;
    private String tempCaminhoDocRecibo;
    private String tempTipoDocRecibo;
    private long tempTamanhoDocRecibo;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    private MainFrame parentFrame; 
    private Processo processoAlvo;
    private Recibo reciboEmEdicao;

    /**
     * Construtor principal para associar (criar) ou editar um recibo.
     * @param parent O MainFrame da aplicação.
     * @param reciboParaEditar O recibo a ser editado, ou null se for para criar um novo.
     */
    public AssociarReciboPanel(MainFrame parent, Recibo reciboParaEditar) {
        this.parentFrame = parent;
        this.reciboEmEdicao = reciboParaEditar;

        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

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

        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        areaDetalhesProcesso = new JTextArea(5, 40);
        areaDetalhesProcesso.setEditable(false);
        areaDetalhesProcesso.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaDetalhesProcesso.setBorder(BorderFactory.createTitledBorder("Detalhes do Processo Encontrado"));
        panelCentral.add(new JScrollPane(areaDetalhesProcesso), BorderLayout.NORTH);

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

        gbcRecibo.gridwidth = 1;
        gbcRecibo.gridx = 0; gbcRecibo.gridy++;
        panelCriacaoRecibo.add(new JLabel("Data Assinatura (dd/MM/yyyy):"), gbcRecibo);
        try {
            txtDataAssinatura = new JFormattedTextField(new MaskFormatter("##/##/####"));
        } catch (ParseException e) {
            txtDataAssinatura = new JFormattedTextField();
        }
        gbcRecibo.gridx = 1; gbcRecibo.gridwidth = 2;
        panelCriacaoRecibo.add(txtDataAssinatura, gbcRecibo);

        gbcRecibo.gridwidth = 1;
        gbcRecibo.gridx = 0; gbcRecibo.gridy++;
        lblCampoEspecificoRecibo = new JLabel("Dado Específico:");
        panelCriacaoRecibo.add(lblCampoEspecificoRecibo, gbcRecibo);
        txtCampoEspecificoRecibo = new JTextField(20);
        gbcRecibo.gridx = 1; gbcRecibo.gridwidth = 2;
        panelCriacaoRecibo.add(txtCampoEspecificoRecibo, gbcRecibo);

        JPanel panelAnexoRecibo = new JPanel(new GridBagLayout());
        panelAnexoRecibo.setOpaque(false);
        panelAnexoRecibo.setBorder(BorderFactory.createTitledBorder("Anexo do Recibo"));
        GridBagConstraints gbcAnexo = new GridBagConstraints();
        gbcAnexo.insets = new Insets(2, 5, 2, 5);
        gbcAnexo.fill = GridBagConstraints.HORIZONTAL;

        gbcAnexo.gridx = 0;
        gbcAnexo.gridy = 0;
        gbcAnexo.gridheight = 2;
        gbcAnexo.anchor = GridBagConstraints.CENTER;
        gbcAnexo.fill = GridBagConstraints.NONE;
        lblMiniaturaDocumentoRecibo = new JLabel();
        lblMiniaturaDocumentoRecibo.setPreferredSize(new Dimension(90, 90));
        lblMiniaturaDocumentoRecibo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblMiniaturaDocumentoRecibo.setHorizontalAlignment(SwingConstants.CENTER);
        lblMiniaturaDocumentoRecibo.setVerticalAlignment(SwingConstants.CENTER);
        panelAnexoRecibo.add(lblMiniaturaDocumentoRecibo, gbcAnexo);

        gbcAnexo.gridx = 1;
        gbcAnexo.gridy = 0;
        gbcAnexo.gridheight = 1;
        gbcAnexo.weightx = 1.0;
        gbcAnexo.fill = GridBagConstraints.HORIZONTAL;
        txtCaminhoDocumentoRecibo = new JTextField(25);
        txtCaminhoDocumentoRecibo.setEditable(false);
        panelAnexoRecibo.add(txtCaminhoDocumentoRecibo, gbcAnexo);

        gbcAnexo.gridx = 1;
        gbcAnexo.gridy = 1;
        gbcAnexo.weightx = 0;
        gbcAnexo.anchor = GridBagConstraints.WEST;
        JButton btnAnexarDocumentoRecibo = new JButton("Anexar...");
        panelAnexoRecibo.add(btnAnexarDocumentoRecibo, gbcAnexo);
        
        gbcRecibo.gridx = 0; gbcRecibo.gridy++; gbcRecibo.gridwidth = 3;
        panelCriacaoRecibo.add(panelAnexoRecibo, gbcRecibo);
        
        gbcRecibo.gridx = 0; gbcRecibo.gridy++; gbcRecibo.gridwidth = 3; gbcRecibo.anchor = GridBagConstraints.CENTER;
        btnSalvarRecibo = new JButton("Salvar Recibo");
        panelCriacaoRecibo.add(btnSalvarRecibo, gbcRecibo);

        panelCentral.add(panelCriacaoRecibo, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        btnBuscarProcesso.addActionListener(e -> buscarProcessoParaAssociacao());
        btnSalvarRecibo.addActionListener(e -> salvarRecibo());
        cmbTipoRecibo.addActionListener(e -> atualizarCampoEspecificoRecibo());
        btnAnexarDocumentoRecibo.addActionListener(e -> selecionarDocumentoRecibo());

        if (this.reciboEmEdicao != null) {
            panelBusca.setVisible(false);
            areaDetalhesProcesso.setBorder(BorderFactory.createTitledBorder("Processo Associado (Recibo ID: " + this.reciboEmEdicao.getId() + ")"));
            btnSalvarRecibo.setText("Atualizar Recibo");
            this.processoAlvo = this.reciboEmEdicao.getProcessoAssociado();
            
            popularTiposRecibo();
            
            preencherCamposParaEdicao();
            habilitarCamposRecibo(true);
            
            cmbTipoRecibo.setEnabled(false); 

        } else {
            habilitarCamposRecibo(false);
        }
    }
    
    private void habilitarCamposRecibo(boolean habilitar) {
        panelCriacaoRecibo.setVisible(habilitar);
        if(!habilitar && reciboEmEdicao == null) {
            areaDetalhesProcesso.setText("");
            processoAlvo = null;
            txtDataAssinatura.setText("");
            txtCampoEspecificoRecibo.setText("");
            txtCaminhoDocumentoRecibo.setText("");
            lblMiniaturaDocumentoRecibo.setIcon(null);
            lblMiniaturaDocumentoRecibo.setText("Sem Anexo");
            cmbTipoRecibo.removeAllItems();
            tempCaminhoDocRecibo = null;
            tempTipoDocRecibo = null;
            tempTamanhoDocRecibo = 0;
        }
    }

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
        popularTiposRecibo();
        habilitarCamposRecibo(true);
        txtDataAssinatura.setText(dateFormat.format(new Date()));
    }

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
    
    private void atualizarCampoEspecificoRecibo() {
        String tipoRecibo = (String) cmbTipoRecibo.getSelectedItem();
        if (tipoRecibo == null) return;
        switch(tipoRecibo) {
            case "Recibo de Indenização em Milhas": lblCampoEspecificoRecibo.setText("Quantidade de Milhas:"); break;
            case "Recibo de Item Esquecido em Avião": lblCampoEspecificoRecibo.setText("Doc. Identificação Cliente:"); break;
            default: lblCampoEspecificoRecibo.setText("Local de Entrega/Retirada:"); break;
        }

        if(reciboEmEdicao == null) {
            txtCampoEspecificoRecibo.setText("");
        }
    }

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

            exibirMiniaturaRecibo(this.tempCaminhoDocRecibo, lblMiniaturaDocumentoRecibo);
        }
    }

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

        String fileName = file.getName().toLowerCase();
        String tipoDoc = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            tipoDoc = fileName.substring(i + 1);
        }
        
        if (tipoDoc.equals("jpg") || tipoDoc.equals("jpeg") || tipoDoc.equals("png")) {
            try {
                BufferedImage originalImage = ImageIO.read(file);
                if (originalImage != null) {
                    Image scaledImage = originalImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
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
            
            if (reciboEmEdicao == null) {
                criarNovoRecibo(dataAssinatura, dadoEspecifico, tipoReciboSelecionado);
            } else {
                atualizarReciboExistente(dataAssinatura, dadoEspecifico);
            }

        } catch (ValidacaoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "A quantidade de milhas deve ser um número inteiro válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarNovoRecibo(Date dataAssinatura, String dadoEspecifico, String tipoReciboSelecionado) throws ValidacaoException, NumberFormatException {
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
        
        if (this.tempCaminhoDocRecibo != null && !this.tempCaminhoDocRecibo.isEmpty() && 
            (reciboEmEdicao.getCaminhoDocumento() == null || !this.tempCaminhoDocRecibo.equals(reciboEmEdicao.getCaminhoDocumento()))) {
            
            reciboEmEdicao.setCaminhoDocumento(this.tempCaminhoDocRecibo);
            reciboEmEdicao.setTipoArquivoDocumento(this.tempTipoDocRecibo);
            reciboEmEdicao.setTamanhoArquivoDocumento(this.tempTamanhoDocRecibo);
        } else if (this.tempCaminhoDocRecibo == null && reciboEmEdicao.getCaminhoDocumento() != null) {
            reciboEmEdicao.setCaminhoDocumento(null);
            reciboEmEdicao.setTipoArquivoDocumento(null);
            reciboEmEdicao.setTamanhoArquivoDocumento(0);
        }


        if (ProcessoRepository.atualizarRecibo(reciboEmEdicao)) {
            JOptionPane.showMessageDialog(this, "Recibo atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao atualizar o recibo.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherCamposParaEdicao() {
        if (reciboEmEdicao == null) return;
        
        txtDataAssinatura.setText(dateFormat.format(reciboEmEdicao.getDataAssinatura()));
        
        if (reciboEmEdicao.getCaminhoDocumento() != null && !reciboEmEdicao.getCaminhoDocumento().isEmpty()) {
            txtCaminhoDocumentoRecibo.setText(reciboEmEdicao.getCaminhoDocumento());
            this.tempCaminhoDocRecibo = reciboEmEdicao.getCaminhoDocumento();
            this.tempTipoDocRecibo = reciboEmEdicao.getTipoArquivoDocumento();
            this.tempTamanhoDocRecibo = reciboEmEdicao.getTamanhoArquivoDocumento();
            exibirMiniaturaRecibo(reciboEmEdicao.getCaminhoDocumento(), lblMiniaturaDocumentoRecibo);
        } else {
            lblMiniaturaDocumentoRecibo.setIcon(null);
            lblMiniaturaDocumentoRecibo.setText("Sem Anexo");
            this.tempCaminhoDocRecibo = null;
            this.tempTipoDocRecibo = null;
            this.tempTamanhoDocRecibo = 0;
        }

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
        cmbTipoRecibo.removeAllItems();
        
        habilitarCamposRecibo(false);
        reciboEmEdicao = null;
        processoAlvo = null;
        
        panelBusca.setVisible(true);
        areaDetalhesProcesso.setText("");
        areaDetalhesProcesso.setBorder(BorderFactory.createTitledBorder("Detalhes do Processo Encontrado"));
        txtBuscaBase.setEnabled(true);
        txtBuscaNumeroProcesso.setEnabled(true);
        btnBuscarProcesso.setEnabled(true);
        cmbTipoRecibo.setEnabled(true);
    }
}