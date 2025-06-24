package bagagem.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * MainFrame é a janela principal da aplicação de gestão de bagagens.
 * Ela contém um menu para navegação e um painel de fundo com imagem.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Sistema de Gestão de Bagagens da Companhia Aérea");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(new PainelComImagem("/imagens/bag.jpg"));
        setLayout(new BorderLayout());

        createMenuBar();
    }

    /**
     * Cria e configura a barra de menu da janela principal.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu "Processo"
        JMenu processoMenu = new JMenu("Processo");
        JMenuItem cadastrarProcessoItem = new JMenuItem("Cadastrar Novo Processo");
        JMenuItem listarProcessosItem = new JMenuItem("Listar Processos");
        processoMenu.add(cadastrarProcessoItem);
        processoMenu.add(listarProcessosItem);
        menuBar.add(processoMenu);

        cadastrarProcessoItem.addActionListener(e -> showPanel(new CadastroProcessoPanel()));
        listarProcessosItem.addActionListener(e -> showPanel(new ListagemProcessosPanel(this)));

        // Menu "Recibo"
        JMenu reciboMenu = new JMenu("Recibo");
        JMenuItem associarReciboItem = new JMenuItem("Associar Recibo");
        JMenuItem listarRecibosItem = new JMenuItem("Listar Recibos");
        reciboMenu.add(associarReciboItem);
        reciboMenu.add(listarRecibosItem);
        menuBar.add(reciboMenu);
        
        associarReciboItem.addActionListener(e -> showPanel(new AssociarReciboPanel()));
        listarRecibosItem.addActionListener(e -> showPanel(new ListagemRecibosPanel(this)));
        // Menu "Ajuda"
        JMenu ajudaMenu = new JMenu("Ajuda");
        JMenuItem sobreItem = new JMenuItem("Sobre");
        ajudaMenu.add(sobreItem);
        menuBar.add(ajudaMenu);

        // --- AÇÃO PARA O MENU "SOBRE" ADICIONADA AQUI ---
        sobreItem.addActionListener(e -> mostrarJanelaSobre());

        setJMenuBar(menuBar);
    }
    
    /**
     * Cria e exibe a janela de diálogo "Sobre".
     */
    private void mostrarJanelaSobre() {
        // Monta a mensagem usando HTML para formatação (negrito, quebra de linha).
        String mensagem = "<html>" +
            "<body>" +
            "<h2>Sistema de Gestão de Bagagens</h2>" +
            "<p>Este sistema automatiza o registro e organização digital<br>" +
            "dos processos de bagagens de uma companhia aérea.</p>" +
            "<br>" + // Quebra de linha
            "<b>Desenvolvido por:</b>" +
            "<ul>" +
            "<li>JÚLIA DE SOUZA NASCCIMENTO/li>" +
            "<li>ANA LUÍSA PEREIRA DOS SANTOS</li>" +
            "<li>GABRIEL RODRIGUES DA SILVA</li>" +
            "</ul>" +
            "<p><b>Disciplina:</b> Programação Orientada a Objetos</p>" +
            "<p><b>Docente:</b> Prof. Elias Batista Ferreira</p>" +
            "<br>" +
            "<p>Universidade Federal de Goiás - UFG</p>" +
            "<p>Instituto de Informática - 2025/1</p>" +
            "</body>" +
            "</html>";

        // Tenta carregar o ícone personalizado
        ImageIcon icone = null;
        URL urlIcone = getClass().getResource("/imagens/logo_sobre.png");
        if (urlIcone != null) {
            icone = new ImageIcon(urlIcone);
        }

        // Exibe a caixa de diálogo
        JOptionPane.showMessageDialog(
            this,                               // Componente pai (a própria janela)
            mensagem,                           // A mensagem formatada em HTML
            "Sobre o Sistema",                  // O título da janela de diálogo
            JOptionPane.INFORMATION_MESSAGE,    // O tipo de mensagem (afeta o ícone padrão)
            icone                               // Nosso ícone personalizado (ou null se não for encontrado)
        );
    }

    /**
     * Exibe um painel específico na janela principal.
     * @param panel O JPanel a ser exibido.
     */
    void showPanel(JPanel panel) {
        panel.setOpaque(false);
        getContentPane().removeAll();
        getContentPane().add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}