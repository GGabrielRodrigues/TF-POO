package bagagem.gui;

import bagagem.model.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap; // Importar HashMap
import java.util.Map; // Importar Map

/**
 * Painel para cadastro e edição de processos de bagagem, com um formulário
 * opaco sobre um fundo transparente.
 */
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
            // Desabilita campos chave para edição para manter a integridade do processo
            txtBase.setEnabled(false);
            txtNumeroProcesso.setEnabled(false);
            cmbTipoProcesso.setEnabled(false);
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
        String base = txtBase.getText().trim().toUpperCase();
        if (!base.matches("^[A-Z]{3}$")) {
            JOptionPane.showMessageDialog(this, "A Base deve conter 3 letras maiúsculas.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String numeroProcesso = txtNumeroProcesso.getText().trim();
        if (!numeroProcesso.matches("^\\d{5}$")) {
            JOptionPane.showMessageDialog(this, "O Número do Processo deve conter 5 dígitos.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Date dataAbertura;
        try {
            dataAbertura = dateFormat.parse(txtDataAbertura.getText());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String campoEspecifico = txtCampoEspecifico.getText().trim().toUpperCase();
        if (campoEspecifico.isEmpty()) {
             JOptionPane.showMessageDialog(this, "O campo específico é obrigatório.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (processoEmEdicao == null) {
            // Lógica para NOVO processo
            if (ProcessoRepository.buscarProcessoPorBaseNumero(base, numeroProcesso) != null) {
                JOptionPane.showMessageDialog(this, "Processo já existe.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Processo novoProcesso = criarProcesso(base, numeroProcesso, dataAbertura, campoEspecifico);
            ProcessoRepository.adicionarProcesso(novoProcesso);
            JOptionPane.showMessageDialog(this, "Processo cadastrado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
        } else {
            // Lógica para EDIÇÃO de processo
            // Atualiza os atributos comuns
            Map<String, Object> novosDados = new HashMap<>();
            novosDados.put("dataAbertura", dataAbertura); // Apenas dataAbertura pode ser editada para processos já existentes
            novosDados.put("caminhoDocumento", tempCaminhoDoc);
            novosDados.put("tipoArquivoDocumento", tempTipoDoc);
            novosDados.put("tamanhoArquivoDocumento", tempTamanhoDoc);
            
            // Atualiza o atributo específico dependendo do tipo de processo
            if (processoEmEdicao instanceof DanificacaoBagagem) {
                ((DanificacaoBagagem) processoEmEdicao).setEtiquetaBagagemDanificada(campoEspecifico);
            } else if (processoEmEdicao instanceof ExtravioBagagem) {
                ((ExtravioBagagem) processoEmEdicao).setEtiquetaBagagemExtraviada(campoEspecifico);
            } else if (processoEmEdicao instanceof ItemEsquecidoAviao) {
                ((ItemEsquecidoAviao) processoEmEdicao).setNumeroVoo(campoEspecifico);
            }

            // Chama o método editarInformacoes do processo para aplicar as mudanças
            processoEmEdicao.editarInformacoes(novosDados); // Usa o Map para os atributos comuns

            // Persiste as alterações no repositório
            boolean atualizado = ProcessoRepository.atualizarProcesso(processoEmEdicao);
            if (atualizado) {
                JOptionPane.showMessageDialog(this, "Processo atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                // Opcional: Voltar para a tela de listagem após a atualização
                // parentFrame.showPanel(new ListagemProcessosPanel(parentFrame));
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao atualizar processo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
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
    }
}