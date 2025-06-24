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

public class CadastroProcessoPanel extends JPanel {

    private JTextField txtBase;
    private JTextField txtNumeroProcesso;
    private JFormattedTextField txtDataAbertura;
    private JComboBox<String> cmbTipoProcesso;
    private JLabel lblCampoEspecifico;
    private JTextField txtCampoEspecifico;
    private JTextField txtCaminhoDocumento;
    private JButton btnSalvar;
    private Processo processoEmEdicao;
    private String tempCaminhoDoc;
    private String tempTipoDoc;
    private long tempTamanhoDoc;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public CadastroProcessoPanel() {
        this(null);
    }

    public CadastroProcessoPanel(Processo processoParaEditar) {
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
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Documento Anexado:"), gbc);
        gbc.gridx = 1;
        txtCaminhoDocumento = new JTextField(30);
        txtCaminhoDocumento.setEditable(false);
        formPanel.add(txtCaminhoDocumento, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnAnexarDocumento = new JButton("Anexar Documento...");
        formPanel.add(btnAnexarDocumento, gbc);
        gbc.gridy++;
        btnSalvar = new JButton("Salvar Processo");
        formPanel.add(btnSalvar, gbc);
        cmbTipoProcesso.addActionListener(e -> atualizarCampoEspecifico());
        btnAnexarDocumento.addActionListener(e -> selecionarDocumento());
        btnSalvar.addActionListener(e -> salvarProcesso());
        if (processoEmEdicao != null) {
            titleLabel.setText("Edição de Processo");
            btnSalvar.setText("Atualizar Processo");
            preencherCamposParaEdicao(processoEmEdicao);
            txtBase.setEnabled(false);
            txtNumeroProcesso.setEnabled(false);
            cmbTipoProcesso.setEnabled(false);
        } else {
            txtDataAbertura.setText(dateFormat.format(new Date()));
            atualizarCampoEspecifico();
        }
        addPlaceholder(txtBase, "Ex: GYN");
        addPlaceholder(txtNumeroProcesso, "Ex: 12345");
        addPlaceholder(txtCaminhoDocumento, "Selecione um documento para anexar");


        // A linha do placeholder de data foi removida
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.insets = new Insets(20, 20, 20, 20);
        add(formPanel, gbcMain);
    }

    private void atualizarCampoEspecifico() {
        String tipoSelecionado = (String) cmbTipoProcesso.getSelectedItem();
        txtCampoEspecifico.setText("");
        switch (tipoSelecionado) {
            case "Danificação de Bagagem":
                lblCampoEspecifico.setText("Etiqueta da Bagagem Danificada:");
                addPlaceholder(txtCampoEspecifico, "Ex: BG-12345678");
                break;
            case "Extravio de Bagagem":
                lblCampoEspecifico.setText("Etiqueta da Bagagem Extraviada:");
                addPlaceholder(txtCampoEspecifico, "Ex: BG-12345678");
                break;
            case "Item Esquecido em Avião":
                lblCampoEspecifico.setText("Número do Voo:");
                addPlaceholder(txtCampoEspecifico, "Ex: G3 1234");
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
        }
    }

    private void salvarProcesso() {
        try {
            String base = txtBase.getText().trim().toUpperCase();
            String numeroProcesso = txtNumeroProcesso.getText().trim();
            Date dataAbertura;
            try {
                dataAbertura = dateFormat.parse(txtDataAbertura.getText());
            } catch (ParseException e) {
                throw new ValidacaoException("Formato de data inválido. Use dd/MM/yyyy.");
            }
            String campoEspecifico = txtCampoEspecifico.getText().trim().toUpperCase();
            if (campoEspecifico.isEmpty() || campoEspecifico.startsWith("EX:")) {
                throw new ValidacaoException("O campo específico é obrigatório.");
            }
            String tipoSelecionado = (String) cmbTipoProcesso.getSelectedItem();
            switch (tipoSelecionado) {
                case "Danificação de Bagagem":
                case "Extravio de Bagagem":
                    if (!campoEspecifico.matches("^[A-Z]{2}-\\d{8}$")) {
                        throw new ValidacaoException("Formato de etiqueta inválido. Use 2 letras, hífen e 8 números (Ex: BG-12345678).");
                    }
                    break;
                case "Item Esquecido em Avião":
                    if (!campoEspecifico.matches("^[A-Z0-9]{2}\\s\\d{4}$")) {
                        throw new ValidacaoException("Formato de voo inválido. Use 2 caracteres, espaço e 4 números (Ex: G3 1234).");
                    }
                    break;
            }
            if (processoEmEdicao == null) {
                if (tempCaminhoDoc == null || tempCaminhoDoc.trim().isEmpty()) {
                    throw new ValidacaoException("É obrigatório anexar um documento para criar um novo processo.");
                }
                if (ProcessoRepository.buscarProcessoPorBaseNumero(base, numeroProcesso) != null) {
                    throw new ValidacaoException("Processo com esta Base e Número já existe.");
                }
                Processo novoProcesso = criarProcesso(base, numeroProcesso, dataAbertura, campoEspecifico);
                ProcessoRepository.adicionarProcesso(novoProcesso);
                JOptionPane.showMessageDialog(this, "Processo cadastrado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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

    private Processo criarProcesso(String base, String numeroProcesso, Date data, String campo) {
        String tipo = (String) cmbTipoProcesso.getSelectedItem();
        Processo p = null;
        switch (tipo) {
            case "Danificação de Bagagem":
                p = new DanificacaoBagagem(base, numeroProcesso, data, campo);
                break;
            case "Extravio de Bagagem":
                p = new ExtravioBagagem(base, numeroProcesso, data, campo);
                break;
            case "Item Esquecido em Avião":
                p = new ItemEsquecidoAviao(base, numeroProcesso, data, campo);
                break;
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
        
        // Garante que os placeholders voltem após limpar
        addPlaceholder(txtBase, "Ex: GYN");
        addPlaceholder(txtNumeroProcesso, "Ex: 12345");
        atualizarCampoEspecifico();
    }

    private void addPlaceholder(JTextField textField, String placeholder) {
        // Remove listeners antigos para evitar duplicação
        for(java.awt.event.FocusListener listener : textField.getFocusListeners()) {
            textField.removeFocusListener(listener);
        }
        
        Color gainColor = Color.BLACK; // Cor do texto normal
        Color lostColor = Color.GRAY;  // Cor do placeholder

        if (textField.getText().isEmpty()) {
            textField.setText(placeholder);
            textField.setForeground(lostColor);
        } else {
            // Garante que se houver texto, a cor seja a correta
            textField.setForeground(gainColor);
        }

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getForeground() == lostColor && textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(gainColor);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(lostColor);
                }
            }
        });
    }
}