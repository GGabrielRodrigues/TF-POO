package bagagem.gui;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

/**
 * Um JPanel personalizado que desenha uma imagem de fundo.
 * A imagem é redimensionada para preencher todo o painel.
 */
public class PainelComImagem extends JPanel {

    private Image imagemDeFundo;

    /**
     * Construtor que carrega a imagem a partir de um caminho no projeto.
     * @param caminhoImagem O caminho para a imagem, a partir da pasta 'src'.
     * Exemplo: "/imagens/fundo.jpg"
     */
    public PainelComImagem(String caminhoImagem) {
        // Carrega a imagem de forma segura, usando a variável recebida.
        URL url = getClass().getResource(caminhoImagem); // <-- CORRIGIDO
        
        if (url != null) {
            this.imagemDeFundo = new ImageIcon(url).getImage();
        } else {
            // Usa a variável também na mensagem de erro.
            System.err.println("Imagem de fundo não encontrada em: " + caminhoImagem); // <-- CORRIGIDO
        }
    }

    /**
     * Este método é chamado automaticamente pelo Swing para desenhar o componente.
     * Nós o sobrescrevemos para primeiro desenhar nossa imagem.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Mantém o comportamento padrão de pintura
        
        // Desenha a imagem de fundo, cobrindo toda a área do painel.
        if (imagemDeFundo != null) {
            g.drawImage(imagemDeFundo, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}