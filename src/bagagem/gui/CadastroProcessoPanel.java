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
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CadastroProcessoPanel extends JPanel {
    private JTextField txtBase;
    private JTextField txtNumeroProcesso;
    private JFormattedTextField txtDataAbertura;
    private JComboBox<String> cmbTipoProcesso;
    private JLabel lblCampoEspecifico;
    private JTextField txtCampoEspecifico;
    private JTextField txtCaminhoDocumento;
    private JButton btnSalvar;
    private JLabel lblMiniaturaDocumento;
    private JPanel panelAnexo;
    
    private MainFrame parentFrame; // Referência para a janela principal
    private Processo processoEmEdicao;

    private String tempCaminhoDoc;
    private String tempTipoDoc;
    private long tempTamanhoDoc;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Construtor para criar um novo processo.
     * @param parent A referência do MainFrame.
     */
    public CadastroProcessoPanel(MainFrame parent) {
        this(parent, null); // Chama o construtor principal com processo nulo
    }

    /**
     * Construtor principal para criar ou editar um processo.
     * @param parent A referência do MainFrame.
     * @param processoParaEditar O processo a ser editado, ou null para um novo.
     */
    public CadastroProcessoPanel(MainFrame parent, Processo processoParaEditar) {
        this.parentFrame = parent; // Armazena a referência
        this.processoEmEdicao = processoParaEditar;

        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 235));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Cadastro de Processo", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        formPanel.add(new JLabel("Base (Ex: GYN):"), gbc);
        gbc.gridx = 1;
        txtBase = new JTextField(10);
        formPanel.add(txtBase, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Número do Processo:"), gbc);
        gbc.gridx = 1;
        txtNumeroProcesso = new JTextField(15);
        formPanel.add(txtNumeroProcesso, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Data de Abertura:"), gbc);
        gbc.gridx = 1;
        try {
            txtDataAbertura = new JFormattedTextField(new MaskFormatter("##/##/####"));
        } catch (ParseException e) {
            txtDataAbertura = new JFormattedTextField();
        }
        formPanel.add(txtDataAbertura, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Tipo de Processo:"), gbc);
        gbc.gridx = 1;
        String[] tiposProcesso = {"Danificação de Bagagem", "Extravio de Bagagem", "Item Esquecido em Avião"};
        cmbTipoProcesso = new JComboBox<>(tiposProcesso);
        formPanel.add(cmbTipoProcesso, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        lblCampoEspecifico = new JLabel("Campo Específico:");
        formPanel.add(lblCampoEspecifico, gbc);
        gbc.gridx = 1;
        txtCampoEspecifico = new JTextField(20);
        formPanel.add(txtCampoEspecifico, gbc);
        
        panelAnexo = new JPanel(new GridBagLayout());
        panelAnexo.setOpaque(false);
        panelAnexo.setBorder(BorderFactory.createTitledBorder("Anexo do Documento"));
        GridBagConstraints gbcAnexo = new GridBagConstraints();
        gbcAnexo.insets = new Insets(2, 5, 2, 5);
        gbcAnexo.fill = GridBagConstraints.HORIZONTAL;

        gbcAnexo.gridx = 0;
        gbcAnexo.gridy = 0;
        gbcAnexo.gridheight = 2;
        gbcAnexo.anchor = GridBagConstraints.CENTER;
        gbcAnexo.fill = GridBagConstraints.NONE;
        lblMiniaturaDocumento = new JLabel();
        lblMiniaturaDocumento.setPreferredSize(new Dimension(90, 90));
        lblMiniaturaDocumento.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblMiniaturaDocumento.setHorizontalAlignment(SwingConstants.CENTER);
        lblMiniaturaDocumento.setVerticalAlignment(SwingConstants.CENTER);
        panelAnexo.add(lblMiniaturaDocumento, gbcAnexo);

        gbcAnexo.gridx = 1;
        gbcAnexo.gridy = 0;
        gbcAnexo.gridheight = 1;
        gbcAnexo.weightx = 1.0;
        gbcAnexo.fill = GridBagConstraints.HORIZONTAL;
        txtCaminhoDocumento = new JTextField(25);
        txtCaminhoDocumento.setEditable(false);
        panelAnexo.add(txtCaminhoDocumento, gbcAnexo);

        gbcAnexo.gridx = 1;
        gbcAnexo.gridy = 1;
        gbcAnexo.weightx = 0;
        gbcAnexo.anchor = GridBagConstraints.WEST;
        JButton btnAnexarDocumento = new JButton("Anexar Documento...");
        panelAnexo.add(btnAnexarDocumento, gbcAnexo);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(panelAnexo, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        btnSalvar = new JButton("Salvar Processo");
        formPanel.add(btnSalvar, gbc);
        
        cmbTipoProcesso.addActionListener(e -> atualizarCampoEspecifico());
        btnAnexarDocumento.addActionListener(e -> selecionarDocumento());
        btnSalvar.addActionListener(e -> salvarProcesso());

        if (this.processoEmEdicao != null) {
            titleLabel.setText("Edição de Processo");
            btnSalvar.setText("Atualizar Processo");
            preencherCamposParaEdicao(this.processoEmEdicao);
            txtBase.setEnabled(false);
            txtNumeroProcesso.setEnabled(false);
            cmbTipoProcesso.setEnabled(false);
            if (this.processoEmEdicao.getCaminhoDocumento() != null && !this.processoEmEdicao.getCaminhoDocumento().isEmpty()) {
                exibirMiniatura(this.processoEmEdicao.getCaminhoDocumento());
            }
        } else {
            txtDataAbertura.setText(dateFormat.format(new Date()));
            atualizarCampoEspecifico();
        }
        
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.insets = new Insets(20, 20, 20, 20);
        add(formPanel, gbcMain);
    }
    
     private void salvarProcesso() {
        try {
            String base = txtBase.getText().trim().toUpperCase();
            if (!base.matches("^[A-Z]{3}$")) {
                throw new ValidacaoException("A Base deve conter 3 letras maiúsculas.");
            }
            String numeroProcesso = txtNumeroProcesso.getText().trim();
            if (!numeroProcesso.matches("^\\d{5}$")) {
                throw new ValidacaoException("O Número do Processo deve conter 5 dígitos.");
            }
            Date dataAbertura;
            try {
                dataAbertura = dateFormat.parse(txtDataAbertura.getText());
            } catch (ParseException e) {
                throw new ValidacaoException("Formato de data inválido. Use dd/MM/yyyy.");
            }
            String campoEspecifico = txtCampoEspecifico.getText().trim().toUpperCase();
            if (campoEspecifico.isEmpty()) {
                throw new ValidacaoException("O campo específico é obrigatório.");
            }
            
            String tipoSelecionado = (String) cmbTipoProcesso.getSelectedItem();
            
            if (tipoSelecionado.equals("Danificação de Bagagem") || tipoSelecionado.equals("Extravio de Bagagem")) {
                if (!campoEspecifico.matches("^[A-Z]{2}-\\d{8}$")) {
                    throw new ValidacaoException("Formato de etiqueta inválido. Use AA-12345678.");
                }
            }
            
            if (tipoSelecionado.equals("Item Esquecido em Avião")) {
                if (!campoEspecifico.matches("^[A-Z]{2} \\d{4}$")) {
                    throw new ValidacaoException("Formato de voo inválido. Use AA 1234.");
                }
            }

            if (processoEmEdicao == null) {
                if (ProcessoRepository.buscarProcessoPorBaseNumero(base, numeroProcesso) != null) {
                    throw new ValidacaoException("Processo com esta Base e Número já existe.");
                }
                
                if (tempCaminhoDoc == null || tempCaminhoDoc.trim().isEmpty()) {
                    throw new ValidacaoException("É obrigatório anexar um documento para criar um novo processo.");
                }
                
                Processo novoProcesso = criarProcesso(base, numeroProcesso, dataAbertura, campoEspecifico);
                ProcessoRepository.adicionarProcesso(novoProcesso);
                JOptionPane.showMessageDialog(this, "Processo cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
            } else {
                Map<String, Object> novosDados = new HashMap<>();
                novosDados.put("dataAbertura", dataAbertura);
                novosDados.put("caminhoDocumento", tempCaminhoDoc);
                novosDados.put("tipoArquivoDocumento", tempTipoDoc);
                novosDados.put("tamanhoArquivoDocumento", tempTamanhoDoc);
                
                if (processoEmEdicao instanceof DanificacaoBagagem) {
                    ((DanificacaoBagagem) processoEmEdicao).setEtiquetaBagagemDanificada(campoEspecifico);
                } else if (processoEmEdicao instanceof ExtravioBagagem) {
                    ((ExtravioBagagem) processoEmEdicao).setEtiquetaBagagemExtraviada(campoEspecifico);
                } else if (processoEmEdicao instanceof ItemEsquecidoAviao) {
                    ((ItemEsquecidoAviao) processoEmEdicao).setNumeroVoo(campoEspecifico);
                }

                processoEmEdicao.editarInformacoes(novosDados);

                boolean atualizado = ProcessoRepository.atualizarProcesso(processoEmEdicao);
                if (atualizado) {
                    JOptionPane.showMessageDialog(this, "Processo atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao atualizar processo.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (ValidacaoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void atualizarCampoEspecifico() {
        String tipoSelecionado = (String) cmbTipoProcesso.getSelectedItem();
        switch (tipoSelecionado) {
            case "Danificação de Bagagem":
                lblCampoEspecifico.setText("Etiqueta da Bagagem Danificada:");
                break;
            case "Extravio de Bagagem":
                lblCampoEspecifico.setText("Etiqueta da Bagagem Extraviada:");
                break;
            case "Item Esquecido em Avião":
                lblCampoEspecifico.setText("Número do Voo:");
                break;
        }
    }
    
    private void preencherCamposParaEdicao(Processo processo) {
        txtBase.setText(processo.getBase());
        txtNumeroProcesso.setText(processo.getNumeroProcesso());
        txtDataAbertura.setText(dateFormat.format(processo.getDataAbertura()));
        String tipoProcessoClasse = processo.getClass().getSimpleName();
        switch (tipoProcessoClasse) {
            case "DanificacaoBagagem":
                cmbTipoProcesso.setSelectedItem("Danificação de Bagagem");
                txtCampoEspecifico.setText(((DanificacaoBagagem) processo).getEtiquetaBagagemDanificada());
                break;
            case "ExtravioBagagem":
                cmbTipoProcesso.setSelectedItem("Extravio de Bagagem");
                txtCampoEspecifico.setText(((ExtravioBagagem) processo).getEtiquetaBagagemExtraviada());
                break;
            case "ItemEsquecidoAviao":
                cmbTipoProcesso.setSelectedItem("Item Esquecido em Avião");
                txtCampoEspecifico.setText(((ItemEsquecidoAviao) processo).getNumeroVoo());
                break;
        }
        if (processo.getCaminhoDocumento() != null) {
            txtCaminhoDocumento.setText(processo.getCaminhoDocumento());
            this.tempCaminhoDoc = processo.getCaminhoDocumento();
            this.tempTipoDoc = processo.getTipoArquivoDocumento();
            this.tempTamanhoDoc = processo.getTamanhoArquivoDocumento();
            exibirMiniatura(processo.getCaminhoDocumento());
        }
    }

    private void selecionarDocumento() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imagens e PDFs", "jpg", "jpeg", "png", "pdf"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtCaminhoDocumento.setText(selectedFile.getAbsolutePath());
            this.tempCaminhoDoc = selectedFile.getAbsolutePath();
            String fileName = selectedFile.getName();
            int i = fileName.lastIndexOf('.');
            this.tempTipoDoc = (i > 0) ? fileName.substring(i + 1).toUpperCase() : "";
            this.tempTamanhoDoc = selectedFile.length();
            
            exibirMiniatura(this.tempCaminhoDoc);
        }
    }

    private void exibirMiniatura(String caminhoArquivo) {
        if (caminhoArquivo == null || caminhoArquivo.isEmpty()) {
            lblMiniaturaDocumento.setIcon(null);
            lblMiniaturaDocumento.setText("Nenhum anexo");
            return;
        }

        File file = new File(caminhoArquivo);
        if (!file.exists()) {
            lblMiniaturaDocumento.setIcon(null);
            lblMiniaturaDocumento.setText("Arquivo não encontrado");
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
                    lblMiniaturaDocumento.setIcon(new ImageIcon(scaledImage));
                    lblMiniaturaDocumento.setText("");
                } else {
                    lblMiniaturaDocumento.setIcon(null);
                    lblMiniaturaDocumento.setText("Erro ao carregar imagem");
                }
            } catch (IOException e) {
                lblMiniaturaDocumento.setIcon(null);
                lblMiniaturaDocumento.setText("Erro de leitura");
                System.err.println("Erro ao carregar miniatura: " + e.getMessage());
            }
        } else if (tipoDoc.equals("pdf")) {
            lblMiniaturaDocumento.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
            lblMiniaturaDocumento.setText("PDF");
        } else {
            lblMiniaturaDocumento.setIcon(null);
            lblMiniaturaDocumento.setText("Arquivo não suportado");
        }
    }
    
    private Processo criarProcesso(String base, String numeroProcesso, Date data, String campo) {
        String tipo = (String) cmbTipoProcesso.getSelectedItem();
        Processo p = null;
        switch (tipo) {
            case "Danificação de Bagagem": p = new DanificacaoBagagem(base, numeroProcesso, data, campo); break;
            case "Extravio de Bagagem": p = new ExtravioBagagem(base, numeroProcesso, data, campo); break;
            case "Item Esquecido em Avião": p = new ItemEsquecidoAviao(base, numeroProcesso, data, campo); break;
        }
        if (p != null) {
            p.setCaminhoDocumento(this.tempCaminhoDoc);
            p.setTipoArquivoDocumento(this.tempTipoDoc);
            p.setTamanhoArquivoDocumento(this.tempTamanhoDoc);
        }
        return p;
    }

    private void limparCampos() {
        txtBase.setText("");
        txtNumeroProcesso.setText("");
        txtDataAbertura.setText(dateFormat.format(new Date()));
        txtCampoEspecifico.setText("");
        txtCaminhoDocumento.setText("");
        tempCaminhoDoc = null;
        tempTipoDoc = null;
        tempTamanhoDoc = 0;
        lblMiniaturaDocumento.setIcon(null);
        lblMiniaturaDocumento.setText("Nenhum anexo");
    }
}